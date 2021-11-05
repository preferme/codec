package hl.nio.codec.field.buildin;

import hl.nio.codec.Category;
import hl.nio.codec.CodecException;
import hl.nio.codec.field.ObjectableFieldCodec;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static hl.nio.codec.field.buildin.Constants.*;


public enum NumberCodec implements ObjectableFieldCodec {

    AtomicInteger(java.util.concurrent.atomic.AtomicInteger.class, ATOMIC_INTEGER) {
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            AtomicInteger value = (AtomicInteger)fieldValue;
            out.putInt(value.get());
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            int value = in.getInt();
            return new AtomicInteger(value);
        }
    },
    AtomicLong(java.util.concurrent.atomic.AtomicLong.class, ATOMIC_LONG) {
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            AtomicLong value = (AtomicLong)fieldValue;
            out.putLong(value.get());
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            long value = in.getLong();
            return new AtomicLong(value);
        }
    },
    BigInteger(java.math.BigInteger.class, BIG_INTEGER) {
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            BigInteger value = (BigInteger)fieldValue;
            byte[] data = value.toByteArray();
            out.putInt(data.length);
            out.put(data);
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            int length = in.getInt();
            byte[] data = new byte[length];
            in.get(data);
            return new BigInteger(data);
        }
    },
    BigDecimal(java.math.BigDecimal.class, BIG_DECIMAL) {
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            BigDecimal value = (BigDecimal)fieldValue;
            String number = value.toString();
            byte[] data = number.getBytes();
            out.putInt(data.length);
            out.put(data);
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            int length = in.getInt();
            byte[] data = new byte[length];
            in.get(data);
            String number = new String(data);
            return new BigDecimal(number);
        }
    };

    private final Category category = Category.BuildIn;
    private final Class<?> type;
    private final short typeId;

    NumberCodec(Class<?> type, short typeId) {
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
