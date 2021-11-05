package hl.nio.codec.bean;

import hl.nio.codec.CodecException;


@FunctionalInterface
public interface PropertyGetter<V, T> {

    V get(T obj) throws CodecException;

}
