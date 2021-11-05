package hl.nio.codec.field.buildin;

import hl.nio.codec.Category;
import hl.nio.codec.CodecException;
import hl.nio.codec.field.annotation.CharsetAware;
import hl.nio.codec.field.ObjectableFieldCodec;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static hl.nio.codec.field.buildin.Constants.*;


public abstract class CharSequenceCodec<T> implements ObjectableFieldCodec<T>, CharsetAware {

    public static class CharBuffer extends CharSequenceCodec<java.nio.CharBuffer> {
        public CharBuffer() {
            super(java.nio.CharBuffer.class, CHAR_BUFFER);
        }
        @Override
        java.nio.CharBuffer wrap(java.lang.String value) {
            return java.nio.CharBuffer.wrap(value);
        }
    }

    public static class Segment extends CharSequenceCodec<javax.swing.text.Segment> {
        public Segment() {
            super(javax.swing.text.Segment.class, SEGMENT);
        }
        @Override
        javax.swing.text.Segment wrap(java.lang.String  value) {
            char[] chars = value.toCharArray();
            return new javax.swing.text.Segment(chars, 0, chars.length);
        }
    }

    public static class String extends CharSequenceCodec<java.lang.String> {
        public String() {
            super(java.lang.String.class, STRING);
        }
        @Override
        java.lang.String wrap(java.lang.String value) {
            return value;
        }
    }

    public static class StringBuffer extends CharSequenceCodec<java.lang.StringBuffer> {
        public StringBuffer() {
            super(java.lang.StringBuffer.class, STRING_BUFFER);
        }
        @Override
        java.lang.StringBuffer wrap(java.lang.String value) {
            return new java.lang.StringBuffer(value);
        }
    }

    public static class StringBuilder extends CharSequenceCodec<java.lang.StringBuilder> {
        public StringBuilder() {
            super(java.lang.StringBuilder.class, STRING_BUILDER);
        }
        @Override
        java.lang.StringBuilder wrap(java.lang.String  value) {
            return new java.lang.StringBuilder(value);
        }
    }

    public void encodeData(T fieldValue, ByteBuffer out) throws CodecException {
        int index = out.position();
        out.putInt(0);
        out.put(fieldValue.toString().getBytes(charset));
        out.putInt(index, out.position() - index - 4);
    }

    public T decodeData(ByteBuffer in) throws CodecException {
        int length = in.getInt();
        byte[] data = new byte[length];
        in.get(data);
        java.lang.String value = new java.lang.String(data, charset);
        return wrap(value);
    }

    protected final Category category = Category.BuildIn;
    protected final  Class<T> fieldType;
    protected final short typeId;
    protected Charset charset = Charset.defaultCharset();

    protected CharSequenceCodec(Class<T> fieldType, short typeId) {
        this.fieldType = fieldType;
        this.typeId = typeId;
    }

    abstract T wrap(java.lang.String value);

    @Override
    public Class<T> getFieldType() {
        return fieldType;
    }

    @Override
    public int fieldKey() {
        return ((category.value()&0xFFFF) << 16) | (typeId & 0xFFFF);
    }

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Category getCategory() {
        return category;
    }

    public short getTypeId() {
        return typeId;
    }

    public Charset getCharset() {
        return charset;
    }

}
