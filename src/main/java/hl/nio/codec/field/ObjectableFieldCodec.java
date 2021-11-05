package hl.nio.codec.field;

import hl.nio.codec.CodecException;

import java.nio.ByteBuffer;

public interface ObjectableFieldCodec<T> extends GenericFieldCodec<T> {

    int NULL_OBJECT_KEY = 0;

    @Override
    default void encodeField(T value, ByteBuffer out) throws CodecException {
        out.putInt( value == null ? NULL_OBJECT_KEY : fieldKey());
        if ( value != null ) {
            encodeData(value, out);
        }
    }

    @Override
    default T decodeField(ByteBuffer in) throws CodecException {
        int key = in.getInt();
        if ( key == NULL_OBJECT_KEY) {
            return null;
        }
        if ( fieldKey() != key ) {
            throw new CodecException("[ObjectableFieldCodec][decodeField] Illegal field codec (key=0x" + Integer.toHexString(fieldKey()) + ") for field (key=0x" + Integer.toHexString(key) + ").");
        }
        return decodeData(in);
    }

    void encodeData(T object, ByteBuffer out) throws CodecException;

    T decodeData(ByteBuffer in) throws CodecException;

}
