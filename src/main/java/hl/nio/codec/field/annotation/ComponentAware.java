package hl.nio.codec.field.annotation;

import hl.nio.codec.field.GenericFieldCodec;

public interface ComponentAware {

    void setComponentCodec(GenericFieldCodec componentCodec);

}
