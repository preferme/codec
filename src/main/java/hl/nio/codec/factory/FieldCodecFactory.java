package hl.nio.codec.factory;

import hl.nio.codec.field.FieldCodec;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public interface FieldCodecFactory {

    FieldCodec create(Class<? extends FieldCodec> fieldCodecType, Field field) throws ObjectCreationException;

    FieldCodecFactory Default = (fieldCodecType, field) -> {
        try {
            return (FieldCodec) fieldCodecType.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ObjectCreationException("Create FieldCodec Object [" + fieldCodecType.getName() + "] failed.", e);
        } catch (InvocationTargetException | NoSuchMethodException e) {
            throw new ObjectCreationException("Create FieldCodec Object [" + fieldCodecType.getName() + "] failed.", e);
        }
    };

}
