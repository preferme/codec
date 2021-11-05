package hl.nio.codec.field;

import hl.nio.codec.CodecException;

import java.nio.ByteBuffer;

public interface FieldCodec<T> {

    void encodeField(T value, ByteBuffer out) throws CodecException;

    T decodeField(ByteBuffer in) throws CodecException;

}
