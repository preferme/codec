package hl.nio.codec.field;


public interface GenericFieldCodec<T> extends FieldCodec<T> {

    int fieldKey();

    Class<T> getFieldType();

}
