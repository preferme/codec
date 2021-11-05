package hl.nio.codec.factory;

import hl.nio.codec.pack.PackageCodec;

import java.lang.reflect.InvocationTargetException;

public interface PackageCodecFactory<T> {

    PackageCodec<T> create(Class<? extends PackageCodec> packageCodecType, Class<T> packageType) throws ObjectCreationException;

    PackageCodecFactory Default = ((packageCodecType, packageType) -> {
        try {
            return (PackageCodec) packageCodecType.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ObjectCreationException("Create PackageCodec Object ["+packageCodecType.getName()+"] failed.", e);
        } catch (InvocationTargetException | NoSuchMethodException e) {
            throw new ObjectCreationException("Create PackageCodec Object ["+packageCodecType.getName()+"] failed.", e);
        }
    });
}
