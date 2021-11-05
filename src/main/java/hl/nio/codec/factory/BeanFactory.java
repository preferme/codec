package hl.nio.codec.factory;

import java.lang.reflect.InvocationTargetException;

public interface BeanFactory {

    <T> T create(Class<T> type);

    BeanFactory Default = new BeanFactory() {
        @Override
        public <T> T create(Class<T> type) throws ObjectCreationException {
            try {
                return type.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ObjectCreationException("Create Package Object ["+type.getName()+"] failed.", e);
            } catch (InvocationTargetException | NoSuchMethodException e) {
                throw new ObjectCreationException("Create Package Object ["+type.getName()+"] failed.", e);
            }
        }
    };
}
