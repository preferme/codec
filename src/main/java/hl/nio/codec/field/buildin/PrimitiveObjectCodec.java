package hl.nio.codec.field.buildin;

import hl.nio.codec.Category;
import hl.nio.codec.CodecException;
import hl.nio.codec.field.ObjectableFieldCodec;

import java.nio.ByteBuffer;

import static hl.nio.codec.field.buildin.Constants.*;


public enum PrimitiveObjectCodec implements ObjectableFieldCodec {

    Boolean(Boolean.class, PRIMITIVE_OBJECT_BOOLEAN) {
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            boolean value = (Boolean)fieldValue;
            out.put((byte) (value?1:0));
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            boolean value = in.get() == 1;
            return value;
        }
    },
    Character(Character.class, PRIMITIVE_OBJECT_CHARACTER){
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            char value = (Character)fieldValue;
            out.putChar(value);
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            char value = in.getChar();
            return value;
        }
    },
    Byte(Byte.class, PRIMITIVE_OBJECT_BYTE){
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            byte value = (Byte)fieldValue;
            out.put(value);
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            byte value = in.get();
            return value;
        }
    },
    Short(Short.class, PRIMITIVE_OBJECT_SHORT){
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            short value = (Short)fieldValue;
            out.putShort(value);
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            short value = in.getShort();
            return value;
        }
    },
    Integer(Integer.class, PRIMITIVE_OBJECT_INTEGER){
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            int value = (Integer)fieldValue;
            out.putInt(value);
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            int value = in.getInt();
            return value;
        }
    },
    Long(Long.class, PRIMITIVE_OBJECT_LONG){
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            long value = (Long)fieldValue;
            out.putLong(value);
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            long value = in.getLong();
            return value;
        }
    },
    Float(Float.class, PRIMITIVE_OBJECT_FLOAT){
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            float value = (Float)fieldValue;
            out.putFloat(value);
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            float value = in.getFloat();
            return value;
        }
    },
    Double(Double.class, PRIMITIVE_OBJECT_DOUBLE){
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            double value = (Double)fieldValue;
            out.putDouble(value);
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            double value = in.getDouble();
            return value;
        }
    };

    private final Category category = Category.BuildIn;
    private final Class<?> type;
    private final short typeId;

    PrimitiveObjectCodec(Class<?> type, short typeId) {
        this.type = type;
        this.typeId = typeId;
    }

    public Category getCategory() {
        return category;
    }

    public short getTypeId() {
        return typeId;
    }

    @Override
    public Class<?> getFieldType() {
        return type;
    }

    @Override
    public int fieldKey() {
        return ((category.value()&0xFFFF) << 16) | (typeId & 0xFFFF);
    }

}
