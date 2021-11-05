package hl.nio.codec.useage;

import hl.nio.codec.field.annotation.Field;
import hl.nio.codec.pack.annotation.Inherited;

@Inherited
public class AbstractResponse {

    @Field
    private int errorCode;
    @Field
    private String errorMessage;

}
