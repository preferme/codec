package hl.nio.codec.field.annotation;

import hl.nio.codec.field.GenericFieldCodec;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 对于数组、集合类型的字段，可以设置内部 元素 的相关信息。
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, ANNOTATION_TYPE })
public @interface Component {

    /**
     * 元素类型。要求必须是实现类类型，并且，带有@Package注解，并且，PackageCodec 必须是FieldCodec的实现类，否则，可以不设置。
     */
    Class<?> type() default Object.class;

    /**
     * 如果元素是字符类型的，可以设置对应的字符集
     */
    Charset charset() default @Charset;

    /**
     * 可以自定义元素的编解码器
     */
    @SuppressWarnings("rawtypes")
    Class<? extends GenericFieldCodec> codec() default GenericFieldCodec.class;

}
