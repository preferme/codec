package hl.nio.codec.field.buildin;

import hl.nio.codec.Category;
import hl.nio.codec.CodecException;
import hl.nio.codec.factory.BeanFactory;
import hl.nio.codec.factory.BeanFactoryAware;
import hl.nio.codec.field.GenericFieldCodec;
import hl.nio.codec.field.ObjectableFieldCodec;
import hl.nio.codec.field.annotation.MapComponetAware;

import java.nio.ByteBuffer;
import java.util.*;

import static hl.nio.codec.field.buildin.Constants.*;


public abstract class MapCodec<T extends Map> implements ObjectableFieldCodec<T>, MapComponetAware, BeanFactoryAware {

    public static class HashMap extends MapCodec<java.util.HashMap> {
        public HashMap() {
            super(java.util.HashMap.class, HASH_MAP);
        }
    }

    public static class Hashtable extends MapCodec<java.util.Hashtable> {
        public Hashtable() {
            super(java.util.Hashtable.class, HASH_TABLE);
        }
    }

    public static class IdentityHashMap extends MapCodec<java.util.IdentityHashMap> {
        public IdentityHashMap() {
            super(java.util.IdentityHashMap.class, IDENTITY_HASH_MAP);
        }
    }

    public static class LinkedHashMap extends MapCodec<java.util.LinkedHashMap> {
        public LinkedHashMap() {
            super(java.util.LinkedHashMap.class, LINKED_HASH_MAP);
        }
    }

    public static class Properties extends MapCodec<java.util.Properties> {
        public Properties() {
            super(java.util.Properties.class, PROPERTIES);
        }
    }

    public static class TreeMap extends MapCodec<java.util.TreeMap> {
        public TreeMap() {
            super(java.util.TreeMap.class, TREE_MAP);
        }
    }

    public static class WeakHashMap extends MapCodec<java.util.WeakHashMap> {
        public WeakHashMap() {
            super(java.util.WeakHashMap.class, WEAK_HASH_MAP);
        }
    }

    public static class ConcurrentHashMap extends MapCodec<java.util.concurrent.ConcurrentHashMap> {
        public ConcurrentHashMap() {
            super(java.util.concurrent.ConcurrentHashMap.class, CONCURRENT_HASH_MAP);
        }
    }

    public static class ConcurrentSkipListMap extends MapCodec<java.util.concurrent.ConcurrentSkipListMap> {
        public ConcurrentSkipListMap() {
            super(java.util.concurrent.ConcurrentSkipListMap.class, CONCURRENT_SKIP_LIST_MAP);
        }
    }

    public void encodeData(T fieldValue, ByteBuffer out) throws CodecException {
        encodeEntries(keyComponentCodec, valueComponentCodec, out, fieldValue);
    }

    public T decodeData(ByteBuffer in) throws CodecException {
        T value = beanFactory.create(fieldType);
        decodeEntries(keyComponentCodec, valueComponentCodec, in, value);
        return value;
    }

    private static void encodeEntries(GenericFieldCodec keyComponentCodec, GenericFieldCodec valueComponentCodec, ByteBuffer out, Map value) throws CodecException {
        out.putInt(value.size());
        for (Iterator<Map.Entry> iter = value.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            encodeElement(keyComponentCodec, out, key);
            encodeElement(valueComponentCodec, out, val);
        }
    }

    private static void encodeElement(GenericFieldCodec genericFieldCodec, ByteBuffer out, Object element) throws CodecException {
        if (element == null) {
            out.putInt(0);
        } else {
            genericFieldCodec.encodeField(element, out);
        }
    }

    private static void decodeEntries(GenericFieldCodec keyComponentCodec, GenericFieldCodec valueComponentCodec, ByteBuffer in, Map value) throws CodecException {
        int length = in.getInt();
        for (int i=0; i<length; i++) {
            Object key = decodeElement(keyComponentCodec, in);
            Object val = decodeElement(valueComponentCodec, in);
            value.put(key, val);
        }
    }

    private static Object decodeElement(GenericFieldCodec genericFieldCodec, ByteBuffer in) throws CodecException {
        int messageKey = in.getInt();
        if (messageKey == 0) {
            return null;
        }
        in.position(in.position()-4);
        Object element = genericFieldCodec.decodeField(in);
        return element;
    }

    protected final Category category = Category.BuildIn;
    protected final Class<T> fieldType;
    protected final short typeId;
    protected GenericFieldCodec keyComponentCodec;
    protected GenericFieldCodec valueComponentCodec;
    protected BeanFactory beanFactory;

    protected MapCodec(Class<T> fieldType, short typeId) {
        this.fieldType = fieldType;
        this.typeId = typeId;
    }

    @Override
    public Class<T> getFieldType() {
        return fieldType;
    }

    @Override
    public int fieldKey() {
        return ((category.value()&0xFFFF) << 16) | (typeId & 0xFFFF);
    }

    @Override
    public void setKeyComponentCodec(GenericFieldCodec keyComponentCodec) {
        this.keyComponentCodec = keyComponentCodec;
    }

    @Override
    public void setValueComponentCodec(GenericFieldCodec valueComponentCodec) {
        this.valueComponentCodec = valueComponentCodec;
    }

    @Override
    public void setMessageFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Category getCategory() {
        return category;
    }

    public short getTypeId() {
        return typeId;
    }

    public GenericFieldCodec getKeyComponentCodec() {
        return keyComponentCodec;
    }

    public GenericFieldCodec getValueComponentCodec() {
        return valueComponentCodec;
    }

    public BeanFactory getMessageFactory() {
        return beanFactory;
    }

}
