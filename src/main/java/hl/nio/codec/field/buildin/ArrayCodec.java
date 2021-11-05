package hl.nio.codec.field.buildin;

import hl.nio.codec.Category;
import hl.nio.codec.CodecException;
import hl.nio.codec.field.GenericFieldCodec;
import hl.nio.codec.field.ObjectableFieldCodec;
import hl.nio.codec.field.annotation.ComponentAware;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;


public class ArrayCodec implements ObjectableFieldCodec, ComponentAware {

    private final Category category = Category.BuildIn;
    private final Class<?> fieldType = Array.class;
    private final short typeId = Constants.ARRAY;;
    private GenericFieldCodec componentCodec = null;

    @Override
    public Class<?> getFieldType() {
        return fieldType;
    }

    @Override
    public int fieldKey() {
        return ((category.value()&0xFFFF) << 16) | (typeId & 0xFFFF);
    }

    @Override
    public void encodeData(Object value, ByteBuffer out) throws CodecException {
        // write component type
        out.putInt(componentCodec.fieldKey());
        // write length of array
        int length = Array.getLength(value);
        out.putShort((short)length);
        // write elements
        for (int i = 0; i < length; i++) {
            componentCodec.encodeField(Array.get(value, i), out);
        }
    }

    @Override
    public Object decodeData(ByteBuffer in) throws CodecException {
        // read component type
        int componentCodecKey = in.getInt();
        if (componentCodecKey != componentCodec.fieldKey()) {
            throw new CodecException("[GenericArrayCodec][decodeMessage] componentCodecKey(ox"+Integer.toHexString(componentCodecKey)+") != componentCodec.fieldKey(0x"+Integer.toHexString(componentCodec.fieldKey())+").");
        }
        Class<?> type = componentCodec.getFieldType();
        // read length of array
        int length = in.getShort();
        Object target = Array.newInstance(type, length);
        // read elements
        for(int i=0; i<length; i++) {
            Array.set(target, i, componentCodec.decodeField(in));
        }
        return target;
    }

    @Override
    public void setComponentCodec(GenericFieldCodec componentCodec) {
        this.componentCodec = componentCodec;
    }


    public Category getCategory() {
        return category;
    }

    public short getTypeId() {
        return typeId;
    }

    public GenericFieldCodec getComponentCodec() {
        return componentCodec;
    }

}
