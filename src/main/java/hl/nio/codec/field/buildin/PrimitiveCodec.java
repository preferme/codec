package hl.nio.codec.field.buildin;

import hl.nio.codec.Category;
import hl.nio.codec.CodecException;
import hl.nio.codec.field.GenericFieldCodec;

import java.nio.ByteBuffer;


public enum PrimitiveCodec implements GenericFieldCodec {

    PrimitiveBoolean(boolean.class, Constants.PRIMITIVE_BOOLEAN) {
        @Override
        public void encodeField(Object fieldValue, ByteBuffer out) throws CodecException {
            boolean value = (boolean)fieldValue;
            out.put((byte) (value?1:0));
        }
        @Override
        public Object decodeField(ByteBuffer in) throws CodecException {
            boolean value = in.get() == 1;
            return value;
        }
    },
    PrimitiveCharacter(char.class, Constants.PRIMITIVE_CHARACTER){
        @Override
        public void encodeField(Object fieldValue, ByteBuffer out) throws CodecException {
            char value = (char)fieldValue;
            out.putChar(value);
        }
        @Override
        public Object decodeField(ByteBuffer in) throws CodecException {
            char value = in.getChar();
            return value;
        }
    },
    PrimitiveByte(byte.class, Constants.PRIMITIVE_BYTE){
        @Override
        public void encodeField(Object fieldValue, ByteBuffer out) throws CodecException {
            byte value = (byte)fieldValue;
            out.put(value);
        }
        @Override
        public Object decodeField(ByteBuffer in) throws CodecException {
            byte value = in.get();
            return value;
        }
    },
    PrimitiveShort(short.class, Constants.PRIMITIVE_SHORT){
        @Override
        public void encodeField(Object fieldValue, ByteBuffer out) throws CodecException {
            short value = (short)fieldValue;
            out.putShort(value);
        }
        @Override
        public Object decodeField(ByteBuffer in) throws CodecException {
            short value = in.getShort();
            return value;
        }
    },
    PrimitiveInteger(int.class, Constants.PRIMITIVE_INTEGER){
        @Override
        public void encodeField(Object fieldValue, ByteBuffer out) throws CodecException {
            int value = (int)fieldValue;
            out.putInt(value);
        }
        @Override
        public Object decodeField(ByteBuffer in) throws CodecException {
            int value = in.getInt();
            return value;
        }
    },
    PrimitiveLong(long.class, Constants.PRIMITIVE_LONG){
        @Override
        public void encodeField(Object fieldValue, ByteBuffer out) throws CodecException {
            long value = (long)fieldValue;
            out.putLong(value);
        }
        @Override
        public Object decodeField(ByteBuffer in) throws CodecException {
            long value = in.getLong();
            return value;
        }
    },
    PrimitiveFloat(float.class, Constants.PRIMITIVE_FLOAT){
        @Override
        public void encodeField(Object fieldValue, ByteBuffer out) throws CodecException {
            float value = (float)fieldValue;
            out.putFloat(value);
        }
        @Override
        public Object decodeField(ByteBuffer in) throws CodecException {
            float value = in.getFloat();
            return value;
        }
    },
    PrimitiveDouble(double.class, Constants.PRIMITIVE_DOUBLE){
        @Override
        public void encodeField(Object fieldValue, ByteBuffer out) throws CodecException {
            double value = (double)fieldValue;
            out.putDouble(value);
        }
        @Override
        public Object decodeField(ByteBuffer in) throws CodecException {
            double value = in.getDouble();
            return value;
        }
    };

    private final Category category = Category.BuildIn;
    private final Class<?> type;
    private final short typeId;

    PrimitiveCodec(Class<?> type, short typeId) {
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
