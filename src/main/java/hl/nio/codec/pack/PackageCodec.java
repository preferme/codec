package hl.nio.codec.pack;

import hl.nio.codec.CodecException;

import java.nio.ByteBuffer;

public interface PackageCodec<T> {

    void encodePackage(T value, ByteBuffer out) throws CodecException;

    T decodePackage(ByteBuffer in) throws CodecException;

}
