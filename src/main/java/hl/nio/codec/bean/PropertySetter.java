package hl.nio.codec.bean;

import hl.nio.codec.CodecException;


@FunctionalInterface
public interface PropertySetter<T, V> {

    void set(T object, V value) throws CodecException;

}
