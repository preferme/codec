package hl.nio.codec.field.buildin;

import hl.nio.codec.field.FieldCodec;
import hl.nio.codec.field.GenericFieldCodec;
import hl.nio.codec.reflect.ReflectUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Set;

public class GenericCharSequenceCodecTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericCharSequenceCodecTest.class);
    private static final String BUILD_IN_FIELD_CODEC_PACKAGE = "hl.net.codec.field.buildin";
    private static ClassLoader loader = ReflectUtils.defaultClassLoader();

    @Test
    public void load() throws Exception {
        Set<Class<?>> klasses = ReflectUtils.loadClasses(
                BUILD_IN_FIELD_CODEC_PACKAGE, loader, true,
                klass-> Modifier.isPublic(klass.getModifiers()) && FieldCodec.class.isAssignableFrom(klass)
        );
        for (Class<?> klass : klasses) {
            if (klass.isEnum()) {
                GenericFieldCodec[] codecs = (GenericFieldCodec[]) klass.getEnumConstants();
                for (GenericFieldCodec codec : codecs) {
                    LOGGER.debug("[CharSequenceCodecTest][load] Codec Object : {}", codec);
//                    System.out.println(codec);
                }
            } else if (!Modifier.isAbstract(klass.getModifiers())){
                if (CharSequenceCodec.class.isAssignableFrom(klass)) {
                    System.out.println(klass);
                    newInstance(klass);
                }
                LOGGER.debug("[CharSequenceCodecTest][load] Codec Class : {}", klass);
            }
        }

    }

    private void newInstance(Class<?> klass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        CharSequenceCodec codec = (CharSequenceCodec) klass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        System.out.println(codec);
    }
}