package hl.nio.codec.field.buildin;

import hl.nio.codec.Category;
import hl.nio.codec.CodecException;
import hl.nio.codec.field.ObjectableFieldCodec;

import java.nio.ByteBuffer;
import java.time.ZoneOffset;

import static hl.nio.codec.field.buildin.Constants.*;


public enum DateTimeCodec implements ObjectableFieldCodec {

    Date(java.util.Date.class, DATE){
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            java.util.Date value = (java.util.Date)fieldValue;
            out.putLong(value.getTime());
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            long value = in.getLong();
            return new java.util.Date(value);
        }
    },
    Calendar(java.util.Calendar.class, CALENDAR) {
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            java.util.Calendar value = (java.util.Calendar)fieldValue;
            out.putLong(value.getTimeInMillis());
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            long value = in.getLong();
            java.util.Calendar target = java.util.Calendar.getInstance();
            target.setTimeInMillis(value);
            return target;
        }
    },
    LocalDate(java.time.LocalDate.class, LOCAL_DATE) {
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            java.time.LocalDate value = (java.time.LocalDate)fieldValue;
            out.putLong(value.toEpochDay());
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            long value = in.getLong();
            return java.time.LocalDate.ofEpochDay(value);
        }
    },
    LocalTime(java.time.LocalTime.class, LOCAL_TIME) {
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            java.time.LocalTime value = (java.time.LocalTime)fieldValue;
            out.putLong(value.toNanoOfDay());
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            long value = in.getLong();
            return java.time.LocalTime.ofNanoOfDay(value);
        }
    },
    LocalDateTime(java.time.LocalDateTime.class, LOCAL_DATE_TIME) {
        @Override
        public void encodeData(Object fieldValue, ByteBuffer out) throws CodecException {
            java.time.LocalDateTime value = (java.time.LocalDateTime)fieldValue;
            out.putLong(value.toEpochSecond(ZoneOffset.UTC));
        }
        @Override
        public Object decodeData(ByteBuffer in) throws CodecException {
            long value = in.getLong();
            return java.time.LocalDateTime.ofEpochSecond(value, 0,ZoneOffset.UTC);
        }
    };

    private final Category category = Category.BuildIn;
    private final Class<?> type;
    private final short typeId;

    DateTimeCodec(Class<?> type, short typeId) {
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
