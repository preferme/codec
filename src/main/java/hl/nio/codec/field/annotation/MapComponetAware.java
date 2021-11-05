package hl.nio.codec.field.annotation;

import hl.nio.codec.field.GenericFieldCodec;

public interface MapComponetAware {

    void setKeyComponentCodec(GenericFieldCodec keyComponentCodec);

    void setValueComponentCodec(GenericFieldCodec valueComponentCodec);

}
