package hl.nio.codec.bean;

import hl.nio.codec.CodecException;
import hl.nio.codec.factory.BeanFactory;
import hl.nio.codec.factory.BeanFactoryAware;

import java.nio.ByteBuffer;


public abstract class BeanCodecSupport<T> implements BeanFactoryAware {

    protected BeanPropertyCodec[] beanPropertyCodecs = new BeanPropertyCodec[0];
    protected BeanFactory beanFactory = BeanFactory.Default;
    protected Class<T> type;

    public void encodeData(T object, ByteBuffer out) throws CodecException {
        for (BeanPropertyCodec metaFieldCodec : beanPropertyCodecs) {
            metaFieldCodec.encodeProperty(object, out);
        }
    }

    public T decodeData(ByteBuffer in) throws CodecException {
        T result = beanFactory.create(type);
        for (BeanPropertyCodec metaFieldCodec : beanPropertyCodecs) {
            metaFieldCodec.decodeProperty(result, in);
        }
        return result;
    }

    public BeanPropertyCodec[] getBeanPropertyCodecs() {
        return beanPropertyCodecs;
    }

    public void setBeanPropertyCodecs(BeanPropertyCodec[] beanPropertyCodecs) {
        this.beanPropertyCodecs = beanPropertyCodecs;
    }

    public BeanFactory getMessageFactory() {
        return beanFactory;
    }

    public void setMessageFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

}
