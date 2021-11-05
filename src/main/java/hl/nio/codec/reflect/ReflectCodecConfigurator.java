package hl.nio.codec.reflect;

import hl.nio.codec.CodecException;
import hl.nio.codec.GenericComboCodecV10;
import hl.nio.codec.Version;
import hl.nio.codec.factory.FieldCodecFactory;
import hl.nio.codec.factory.BeanFactory;
import hl.nio.codec.factory.BeanFactoryAware;
import hl.nio.codec.factory.PackageCodecFactory;
import hl.nio.codec.field.FieldCodec;
import hl.nio.codec.field.GenericFieldCodec;
import hl.nio.codec.bean.BeanCodecSupport;
import hl.nio.codec.bean.BeanPropertyCodec;
import hl.nio.codec.bean.PropertyGetter;
import hl.nio.codec.bean.PropertySetter;
import hl.nio.codec.pack.GenericPackageCodec;
import hl.nio.codec.pack.PackageCodec;
import hl.nio.codec.pack.PackageCodecManager;
import hl.nio.codec.pack.annotation.Inherited;
import hl.nio.codec.pack.annotation.Package;
import hl.nio.codec.pack.annotation.PackageAware;
import hl.nio.codec.field.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectCodecConfigurator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReflectCodecConfigurator.class);
    private static final String BUILD_IN_FIELD_CODEC_PACKAGE = "hl.nio.codec.field.buildin";

    private static String[] mergePackages(String[] packages) {
        ArrayList<String> packageList = new ArrayList<>(packages.length);
        for (String pack : packages) {
            String[] sub = pack.split(";");
            for (String p : sub) {
                if (p.trim().length()>0) {
                    packageList.add(p.trim());
                }
            }
        }
        return packageList.toArray(new String[packageList.size()]);
    }

    private final Map<Class, GenericFieldCodec> fieldCodecs = new HashMap<>();
    private final Map<Class, Class<GenericFieldCodec>> fieldCodecTypes = new HashMap<>();
    private final Map<Class, GenericPackageCodec> packageCodecs = new HashMap<>();
    private final Map<Version, Class<? extends GenericPackageCodec>> packageCodecTypes = new HashMap<>();

    private String[] scanPackages = new String[0];
    private ClassLoader loader = ReflectUtils.defaultClassLoader();
    private FieldCodecFactory fieldCodecFactory = FieldCodecFactory.Default;
    private PackageCodecFactory packageCodecFactory = PackageCodecFactory.Default;
    private BeanFactory beanFactory = BeanFactory.Default;
    private PackageCodecManager packageCodecManager = new PackageCodecManager();
    private Comparator<BeanPropertyCodec> messageFieldComparator = (o1, o2)-> {
        Class<?> t1 = o1.getField().getDeclaringClass();
        Class<?> t2 = o2.getField().getDeclaringClass();
        if (t1 == t2) {
            int i1 = o1.getOrdinal();
            int i2 = o2.getOrdinal();
            if (i1 == i2) return 0;
            return i1 > i2 ? 1 : -1;
        }
        return t2.isAssignableFrom(t1) ? 1 : -1;
    };

    public ReflectCodecConfigurator() {
        this.packageCodecTypes.put(Version.V1_0, GenericComboCodecV10.class);
    }

    public void doCofiguration() throws ReflectiveOperationException {
        configureBuildInFieldCodecs();
        String[] scanPackages = mergePackages(this.scanPackages);
        Set<Class<?>> packageTypes = new HashSet<>();
        for (String scanPackage : scanPackages) {
            try {
                Set<Class<?>> klasses = ReflectUtils.loadClasses(
                        scanPackage, loader, true,
                        klass-> klass.isAnnotationPresent(Package.class) && !Modifier.isAbstract(klass.getModifiers())
                );
                packageTypes.addAll(klasses);
            } catch (IOException e) {
                throw new ReflectiveOperationException("[ReflectCodecConfigurator][doCofiguration] Load Package classes failed.", e);
            }
        }
        for (Class<?> packageType : packageTypes) {
            circularReferenceDetection(new LinkedHashSet<>(), packageType);
        }
        List<Class<?>> sortedPakcageTypes = packageTypes.stream().sorted((p1, p2)->{
            if (p1.isAssignableFrom(p2)) {
                return -1;
            }
            if (p2.isAssignableFrom(p1)) {
                return 1;
            }
            if (isRefrenceFrom(p1, p2)) {
                return -1;
            }
            if (isRefrenceFrom(p2, p1)) {
                return 1;
            }
            return 0;
        }).collect(Collectors.toList());
        for (Class<?> packageType : sortedPakcageTypes) {
            PackageCodec packageCodec = makePackageCodec(packageType);

            if (packageCodec instanceof GenericPackageCodec) {
                if (!packageCodecs.containsKey(packageType)) {
                    packageCodecs.put(packageType, (GenericPackageCodec) packageCodec);
                }
            }
            if (packageCodec instanceof GenericFieldCodec) {
                if (!fieldCodecs.containsKey(packageType)) {
                    fieldCodecs.put(packageType, (GenericFieldCodec) packageCodec);
                }
            }

            packageCodecManager.regist(packageCodec, packageType);
            LOGGER.info("[ReflectCodecConfigurator][doCofiguration] regist PackageCodec[{}] for Package[{}].", packageCodec, packageType.getName());
        }

    }

    private boolean isRefrenceFrom(Class<?> c1, Class<?> c2) {
        return Arrays.stream(c2.getDeclaredFields()).anyMatch(field -> {
            if (field.isAnnotationPresent(Component.class)) {
                return c1.isAssignableFrom(field.getAnnotation(Component.class).type());
            }
            if (field.isAnnotationPresent(MapComponet.class)) {
                return c1.isAssignableFrom(field.getAnnotation(MapComponet.class).key().type()) ||
                        c1.isAssignableFrom(field.getAnnotation(MapComponet.class).value().type());
            }
            if (field.getType().isArray()) {
                return c1.isAssignableFrom(field.getType().getComponentType());
            }
            return c1.isAssignableFrom(field.getType());
        });
    }

    private void configureBuildInFieldCodecs() throws ReflectiveOperationException {
        try {
            Set<Class<?>> klasses = ReflectUtils.loadClasses(
                    BUILD_IN_FIELD_CODEC_PACKAGE, loader, true,
                    klass-> Modifier.isPublic(klass.getModifiers()) && FieldCodec.class.isAssignableFrom(klass)
            );
            for (Class<?> klass : klasses) {
                if (klass.isEnum()) {
                    GenericFieldCodec[] codecs = (GenericFieldCodec[]) klass.getEnumConstants();
                    for (GenericFieldCodec codec : codecs) {
                        fieldCodecs.put(codec.getFieldType(), codec);
                        LOGGER.debug("[ReflectCodecConfigurator][configureBuildInFieldCodecs] regist Codec Object : {}", codec);
                    }
                } else if (!Modifier.isAbstract(klass.getModifiers()) &&
                        GenericFieldCodec.class.isAssignableFrom(klass)) {
                    GenericFieldCodec fieldCodec = (GenericFieldCodec) fieldCodecFactory.create((Class<? extends FieldCodec>) klass, null);
                    fieldCodecTypes.put(fieldCodec.getFieldType(), (Class<GenericFieldCodec>) klass);
                    LOGGER.debug("[ReflectCodecConfigurator][configureBuildInFieldCodecs] regist Codec Class : {}", klass.getName());
                }
            }
        } catch (IOException e) {
            throw new ReflectiveOperationException("[ReflectCodecConfigurator][configureBuildInFieldCodecs] load BuildIn Codec classes failed.", e);
        }
    }

    private void circularReferenceDetection(LinkedHashSet<Class<?>> stack, Class<?> target) {
        if (fieldCodecs.containsKey(target) || fieldCodecTypes.containsKey(target)) {
            return;
        }
        if (target.isArray()) {
            target = target.getComponentType();
        }
        Package packageAnno = target.getAnnotation(Package.class);
        if (packageAnno == null) {
            throw new IllegalStateException("[ReflectCodecConfigurator][circularReferenceDetection] Class ["+target.getName()+"] need @Package annotation.");
        }
        if (stack.contains(target)) {
            throw new IllegalStateException("[ReflectCodecConfigurator][circularReferenceDetection] Circular Reference : " + stack.stream().map(c->c.getName()).collect(Collectors.joining(" -> ", "[ ", " ]")));
        }
        stack.add(target);

        Class<?> superClass = target.getSuperclass();
        if (superClass.isAnnotationPresent(Inherited.class)) {
            if (!(fieldCodecs.containsKey(superClass) || fieldCodecTypes.containsKey(superClass))){
                for(java.lang.reflect.Field field : superClass.getDeclaredFields()){
                    if (field.isAnnotationPresent(Field.class)) {
                        circularReferenceDetection(stack, field.getType());
                    }
                }
            }
        }
        for(java.lang.reflect.Field field : target.getDeclaredFields()){
            if (field.isAnnotationPresent(Field.class)) {
                circularReferenceDetection(stack, field.getType());
            }
        }
    }

    private PackageCodec makePackageCodec(Class<?> packageType) throws ReflectiveOperationException {
        Package packageAnno = packageType.getAnnotation(Package.class);
        final boolean customCodec = packageAnno.codec() != PackageCodec.class;

        PackageCodec packageCodec = null;
        if (customCodec) {
            // Custom Package Codec validate
            Class<? extends PackageCodec> packageCodecType = packageAnno.codec();
            if (Modifier.isAbstract(packageCodecType.getModifiers())) {
                throw new IllegalArgumentException("[ReflectCodecConfigurator][makePackageCodec] codec() in @Package can't be an abstract class. " +
                        "package type ["+ packageType.getName() + "], codec() class ["+packageCodecType.getName()+"]");
            }
            packageCodec = packageCodecFactory.create(packageCodecType, packageType);
        } else {
            Class<? extends GenericPackageCodec> packageCodecType = packageCodecTypes.get(packageAnno.version());
            if (packageCodecType == null) {
                throw new IllegalStateException("[ReflectCodecConfigurator][makePackageCodec] No PackageCodec for version : " + packageAnno.version());
            }
            packageCodec = packageCodecFactory.create(packageCodecType, packageType);
        }

        if (packageCodec instanceof BeanCodecSupport) {
            configureMessageCodecSupport((BeanCodecSupport) packageCodec, packageType);
        }

        preparePackageCodec(packageCodec, packageType);

        return packageCodec;
    }

    private void configureMessageCodecSupport(BeanCodecSupport codec, Class<?> messageType) throws ReflectiveOperationException {
        List<java.lang.reflect.Field> fields = new ArrayList<>();
        boolean inheritCodec = messageType.getSuperclass().isAnnotationPresent(Inherited.class);
        parseClass(messageType, fields, inheritCodec);
        BeanPropertyCodec[] messageFields = makeMessageFields(fields);
        codec.setType(messageType);
        codec.setBeanPropertyCodecs(messageFields);
        codec.setMessageFactory(beanFactory);
    }

    private void parseClass(Class<?> target, List<java.lang.reflect.Field> list, boolean inheritCodec) {
        if (inheritCodec && target.getSuperclass() != Object.class && !target.getSuperclass().isInterface()) {
            parseClass(target.getSuperclass(), list, true);
        }
        List<java.lang.reflect.Field> fields = Arrays.stream(target.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Field.class)).collect(Collectors.toList());
        list.addAll(fields);
    }

    private BeanPropertyCodec[] makeMessageFields(List<java.lang.reflect.Field> fields) throws ReflectiveOperationException {
        List<BeanPropertyCodec> entities = new ArrayList<>(fields.size());
        for (java.lang.reflect.Field field : fields) {
            BeanPropertyCodec entity = new BeanPropertyCodec();
            entity.setField(field);
            Field fieldAnno = field.getAnnotation(Field.class);
            entity.setOrdinal(fieldAnno.ordinal());
            entity.setIgnore(fieldAnno.ignore());
            entity.setCodec(makeFieldCodec(field, fieldAnno));
            entity.setGetter(makeMessageFieldGetter(field));
            entity.setSetter(makeMessageFieldSetter(field));
            entities.add(entity);
        }

        entities.sort(messageFieldComparator);

        for (int index = 1; index<entities.size();index++) {
            entities.get(index).setIndex(index);
        }

        return entities.toArray(new BeanPropertyCodec[entities.size()]);
    }

    private FieldCodec makeFieldCodec(java.lang.reflect.Field field, Field fieldAnno) throws ReflectiveOperationException {
        Class<?> fieldType = field.getType();
        final boolean customCodec = fieldAnno.codec() != FieldCodec.class;

        FieldCodec fieldCodec = null;
        if (customCodec) {
            Class<? extends FieldCodec> fieldCodecType = fieldAnno.codec();
            if (Modifier.isAbstract(fieldCodecType.getModifiers())) {
                throw new ReflectiveOperationException("[ReflectCodecConfigurator][makeFieldCodec] codec() in @Field can't be an abstract class. " +
                        "field type ["+ fieldType.getName() + "], codec() class ["+fieldCodecType.getName()+"]");
            }
            fieldCodec = fieldCodecFactory.create(fieldCodecType, field);
        } else {
            // Cache Hit
            if (fieldCodecs.containsKey(fieldType)) {
                return fieldCodecs.get(fieldType);
            }
            if (fieldCodecTypes.containsKey(fieldType)) {
                Class<? extends GenericFieldCodec> fieldCodecType = fieldCodecTypes.get(fieldType);
                fieldCodec = fieldCodecFactory.create(fieldCodecType, field);
            } else if (packageCodecs.containsKey(fieldType)) {
                PackageCodec packageCodec = packageCodecs.get(fieldType);
                if (packageCodec instanceof FieldCodec) {
                    fieldCodec = (FieldCodec) makePackageCodec(fieldType);
                }
            }
            if (fieldCodec == null) {
                throw new IllegalStateException("[ReflectCodecConfigurator][makeFieldCodec] No FieldCodec for Field("+field.getName()+")["+ fieldType.getName() + "] in class ["+ field.getDeclaringClass().getName()+"]");
            }
        }

        prepareFieldCodec(fieldCodec, field);

        return fieldCodec;
    }

    private void prepareFieldCodec(FieldCodec fieldCodec, java.lang.reflect.Field field) throws ReflectiveOperationException {
        if (fieldCodec instanceof FieldAware) {
            Field fieldAnno = field.getAnnotation(Field.class);
            ((FieldAware)fieldCodec).setOrdinal(fieldAnno.ordinal());
            ((FieldAware)fieldCodec).setIgnore(fieldAnno.ignore());
        }
        if (fieldCodec instanceof CharsetAware && field.isAnnotationPresent(Charset.class)) {
            String charset = field.getAnnotation(Charset.class).value();
            ((CharsetAware)fieldCodec).setCharset(java.nio.charset.Charset.forName(charset));
        }
        if (fieldCodec instanceof ComponentAware) {
            if (field.isAnnotationPresent(Component.class)) {
                Component component = field.getAnnotation(Component.class);
                GenericFieldCodec componentCodec = makeComponetCodec(field, component.codec(), component.type(), component.charset().value());
                ((ComponentAware) fieldCodec).setComponentCodec(componentCodec);
            } else {
                if (field.getType().isArray()) {
                    GenericFieldCodec componentCodec = makeComponetCodec(field, GenericFieldCodec.class, field.getType().getComponentType(), null);
                    ((ComponentAware) fieldCodec).setComponentCodec(componentCodec);
                } else {
                    // 查看是否具备泛型，如果没有泛型或者说，无法得到具体的实现类，则抛出异常，必须添加注解。

                    throw new ReflectiveOperationException("[ReflectCodecConfigurator][prepareFieldCodec] FieldCodec[" +
                            fieldCodec.getClass().getName() +
                            "] implements ComponentAware and Field(" +
                            field.getName() + ")[" + field.getType().getName() +
                            "] in Class[" +
                            field.getDeclaringClass().getName() +
                            "] need @Component annotation.");
                }
            }
        }
        if (fieldCodec instanceof MapComponetAware) {
            if (field.isAnnotationPresent(MapComponet.class)) {
                MapComponet mapComponet = field.getAnnotation(MapComponet.class);
                Component keyComponet = mapComponet.key();
                Component valueComponent = mapComponet.value();
                GenericFieldCodec keyComponentCodec = makeComponetCodec(field, keyComponet.codec(), keyComponet.type(), keyComponet.charset().value());
                GenericFieldCodec valueComponentCodec = makeComponetCodec(field, valueComponent.codec(), valueComponent.type(), valueComponent.charset().value());
                ((MapComponetAware)fieldCodec).setKeyComponentCodec(keyComponentCodec);
                ((MapComponetAware)fieldCodec).setValueComponentCodec(valueComponentCodec);
            } else {
                // 查看是否具备泛型，如果没有泛型或者说，无法得到具体的实现类，则抛出异常，必须添加注解。

                throw new ReflectiveOperationException("[ReflectCodecConfigurator][prepareFieldCodec] FieldCodec[" +
                        fieldCodec.getClass().getName() +
                        "] implements MapComponetAware and Field(" +
                        field.getName() + ")[" + field.getType().getName() +
                        "] in Class[" +
                        field.getDeclaringClass().getName() +
                        "] need @MapComponet annotation.");
            }
        }
        if (fieldCodec instanceof BeanFactoryAware) {
            ((BeanFactoryAware)fieldCodec).setMessageFactory(beanFactory);
        }
    }

    private GenericFieldCodec makeComponetCodec(java.lang.reflect.Field field, Class<? extends GenericFieldCodec> codecType, Class<?> type, String charset) throws ReflectiveOperationException {
        GenericFieldCodec codec = null;
        if (codecType != GenericFieldCodec.class) {
            if (Modifier.isAbstract(codecType.getModifiers())) {
                throw new ReflectiveOperationException("[ReflectCodecConfigurator][makeComponetCodec] codec() in @Component can't be an abstract class. " +
                        "field type ["+ field.getClass().getName() + "], codec() class ["+codecType.getName()+"]");
            }
            codec = (GenericFieldCodec) fieldCodecFactory.create(codecType, field);
        } else {
            // Cache Hit
            if (fieldCodecs.containsKey(type)) {
                return fieldCodecs.get(type);
            }
            if (fieldCodecTypes.containsKey(type)) {
                Class<? extends GenericFieldCodec> fieldCodecType = fieldCodecTypes.get(type);
                codec = (GenericFieldCodec) fieldCodecFactory.create(fieldCodecType, field);
            }
            if (packageCodecs.containsKey(type)) {
                PackageCodec packageCodec = packageCodecs.get(type);
                if (packageCodec instanceof GenericFieldCodec) {
                    codec = (GenericFieldCodec) makePackageCodec(type);
                }
            }
            if (codec == null) {
                throw new IllegalStateException("[ReflectCodecConfigurator][makeComponetCodec] No FieldCodec for Field("+field.getName()+")["+ field.getType().getName() + "] in class ["+ field.getDeclaringClass().getName()+"]");
            }
        }
        if (codec instanceof CharsetAware && charset != null) {
            ((CharsetAware)codec).setCharset(java.nio.charset.Charset.forName(charset));
        }
        if (codec instanceof BeanFactoryAware) {
            ((BeanFactoryAware)codec).setMessageFactory(beanFactory);
        }
        return codec;
    }

    private PropertySetter makeMessageFieldSetter(java.lang.reflect.Field field) {
        String fieldName = field.getName();
        Class<?> ownerType = field.getDeclaringClass();
        try {
            String methodName = new StringBuilder("set").append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1)).toString();
            Method method = ownerType.getMethod(methodName, new Class[]{field.getType()});
            return (object, value) -> {
                try {
                    method.invoke(object, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new CodecException("[ReflectComboCodec][makeMessageFieldSetter] invoke setter method["+methodName+"] failed.", e);
                }
            };
        } catch (NoSuchMethodException e) {
            field.setAccessible(true);
            return (object, value) -> {
                try {
                    field.set(object, value);
                } catch (IllegalAccessException ex) {
                    throw new CodecException("[ReflectComboCodec][makeMessageFieldSetter] set field["+fieldName+"] value failed.", e);
                }
            };
        }
    }

    private PropertyGetter makeMessageFieldGetter(java.lang.reflect.Field field) {
        String fieldName = field.getName();
        Class<?> type = field.getDeclaringClass();
        try {
            String methodName = new StringBuilder("get").append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1)).toString();
            Method method = type.getMethod(methodName, new Class[0]);
            if (!field.getType().isAssignableFrom(method.getReturnType())) {
                throw new NoSuchMethodException("[ReflectComboCodec][makeMessageFieldSetter] Wrong return Type.");
            }
            return (object) -> {
                try {
                    return method.invoke(object, new Object[0]);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new CodecException("[ReflectComboCodec][makeMessageFieldSetter] invoke setter method["+methodName+"] failed.", e);
                }
            };
        } catch (NoSuchMethodException e) {
            field.setAccessible(true);
            return (object) -> {
                try {
                    return field.get(object);
                } catch (IllegalAccessException ex) {
                    throw new CodecException("[ReflectComboCodec][makeMessageFieldSetter] set field["+fieldName+"] value failed.", e);
                }
            };
        }
    }

    private void preparePackageCodec(PackageCodec packageCodec, Class<?> packageType) {
        if (packageCodec instanceof PackageAware) {
            Package packageAnno = packageType.getAnnotation(Package.class);
            ((PackageAware)packageCodec).setVersion(packageAnno.version());
            ((PackageAware)packageCodec).setCategory(packageAnno.category());
            ((PackageAware)packageCodec).setTypeId(packageAnno.typeId() == 0 ? packageAnno.value() : packageAnno.typeId());
        }
        if (packageCodec instanceof BeanFactoryAware) {
            ((BeanFactoryAware)packageCodec).setMessageFactory(beanFactory);
        }
    }

    public void setScanPackages(String... scanPackages) {
        this.scanPackages = scanPackages;
    }

    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public void setPackageCodecType(Version version, Class<? extends GenericPackageCodec> packageCodecType) {
        packageCodecTypes.put(version, packageCodecType);
    }

    public void setFieldCodecFactory(FieldCodecFactory fieldCodecFactory) {
        this.fieldCodecFactory = fieldCodecFactory;
    }

    public void setPackageCodecFactory(PackageCodecFactory packageCodecFactory) {
        this.packageCodecFactory = packageCodecFactory;
    }

    public void setMessageFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setMessageFieldComparator(Comparator<BeanPropertyCodec> messageFieldComparator) {
        this.messageFieldComparator = messageFieldComparator;
    }

    public void setPackageCodecManager(PackageCodecManager packageCodecManager) {
        this.packageCodecManager = packageCodecManager;
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public FieldCodecFactory getFieldCodecFactory() {
        return fieldCodecFactory;
    }

    public PackageCodecFactory getPackageCodecFactory() {
        return packageCodecFactory;
    }

    public BeanFactory getMessageFactory() {
        return beanFactory;
    }

    public PackageCodecManager getPackageCodecManager() {
        return packageCodecManager;
    }

    public Comparator<BeanPropertyCodec> getMessageFieldComparator() {
        return messageFieldComparator;
    }

}
