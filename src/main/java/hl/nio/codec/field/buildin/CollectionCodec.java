package hl.nio.codec.field.buildin;

import hl.nio.codec.Category;
import hl.nio.codec.CodecException;
import hl.nio.codec.factory.BeanFactory;
import hl.nio.codec.factory.BeanFactoryAware;
import hl.nio.codec.field.GenericFieldCodec;
import hl.nio.codec.field.ObjectableFieldCodec;
import hl.nio.codec.field.annotation.ComponentAware;

import java.nio.ByteBuffer;
import java.util.*;

import static hl.nio.codec.field.buildin.Constants.*;


public abstract class CollectionCodec<T extends java.util.Collection> implements ObjectableFieldCodec<T>, ComponentAware, BeanFactoryAware {

    public static class ArrayList extends CollectionCodec<java.util.ArrayList> {
        public ArrayList() {
            super(java.util.ArrayList.class, ARRAY_LIST);
        }
    }
    public static class LinkedList extends CollectionCodec<java.util.LinkedList> {
        public LinkedList() {
            super(java.util.LinkedList.class, LINKED_LIST);
        }
    }

    public static class Stack extends CollectionCodec<java.util.Stack> {
        public Stack() {
            super(java.util.Stack.class, STACK);
        }
    }

    public static class Vector extends CollectionCodec<java.util.Vector> {
        public Vector() {
            super(java.util.Vector.class, VECTOR);
        }
    }

    public static class CopyOnWriteArrayList extends CollectionCodec<java.util.concurrent.CopyOnWriteArrayList> {
        public CopyOnWriteArrayList() {
            super(java.util.concurrent.CopyOnWriteArrayList.class, COPY_ON_WRITE_ARRAY_LIST);
        }
    }

    public static class HashSet extends CollectionCodec<java.util.HashSet> {
        public HashSet() {
            super(java.util.HashSet.class, HASH_SET);
        }
    }

    public static class LinkedHashSet extends CollectionCodec<java.util.LinkedHashSet> {
        public LinkedHashSet() {
            super(java.util.LinkedHashSet.class, LINKED_HASH_SET);
        }
    }

    public static class TreeSet extends CollectionCodec<java.util.TreeSet> {
        public TreeSet() {
            super(java.util.TreeSet.class, TREE_SET);
        }
    }

    @Override
    public void encodeData(T fieldValue, ByteBuffer out) throws CodecException {
        out.putInt(fieldValue.size());
        encodeElements(componentCodec, out, fieldValue);
    }
    @Override
    public T decodeData(ByteBuffer in) throws CodecException {
        int length = in.getInt();
        T value = beanFactory.create(fieldType);
        decodeElements(componentCodec, in, length, value);
        return value;
    }

    private static void encodeElements(GenericFieldCodec componentCodec, ByteBuffer out, Collection value) throws CodecException {
        for (Object element : value) {
            if (element == null) {
                out.putInt(NULL_OBJECT_KEY);
                continue;
            }
            componentCodec.encodeField(element, out);
        }
    }

    private static void decodeElements(GenericFieldCodec componentCodec, ByteBuffer in, int length, Collection value) throws CodecException {
        for (int i=0; i<length; i++) {
            int codecKey = in.getInt();
            if (codecKey == NULL_OBJECT_KEY) {
                value.add(null);
            } else {
                in.position(in.position()-4);
                Object element = componentCodec.decodeField(in);
                value.add(element);
            }
        }
    }

    protected final Category category = Category.BuildIn;
    protected final Class<T> fieldType;
    protected final short typeId;
    protected GenericFieldCodec componentCodec;
    protected BeanFactory beanFactory;

    protected CollectionCodec(Class<T> fieldType, short typeId) {
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
    public void setComponentCodec(GenericFieldCodec componentCodec) {
        this.componentCodec = componentCodec;
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

    public GenericFieldCodec getComponentCodec() {
        return componentCodec;
    }

}
