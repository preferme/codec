package hl.nio.codec.bean;

import hl.nio.codec.CodecException;
import hl.nio.codec.field.FieldCodec;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class BeanPropertyCodec<F, T> {

    protected Field field;
    protected int index;
    protected int ordinal;
    protected boolean ignore;
    protected FieldCodec<F> codec;
    protected PropertyGetter<F, T> getter;
    protected PropertySetter<T, F> setter;

    public BeanPropertyCodec() { }

    public void encodeProperty(T object, ByteBuffer out) throws CodecException {
        if (!ignore) {
            F value = getter.get(object);
            codec.encodeField(value, out);
        }
    }

    public void decodeProperty(T object, ByteBuffer in) throws CodecException {
        if (!ignore) {
            F value = codec.decodeField(in);
            setter.set(object, value);
        }
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public FieldCodec<F> getCodec() {
        return codec;
    }

    public void setCodec(FieldCodec<F> codec) {
        this.codec = codec;
    }

    public PropertyGetter<F, T> getGetter() {
        return getter;
    }

    public void setGetter(PropertyGetter<F, T> getter) {
        this.getter = getter;
    }

    public PropertySetter<T, F> getSetter() {
        return setter;
    }

    public void setSetter(PropertySetter<T, F> setter) {
        this.setter = setter;
    }

}
