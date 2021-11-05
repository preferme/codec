package hl.nio.codec.field.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 需要字符编码的字段，可以设置对应的字符集。
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, ANNOTATION_TYPE })
public @interface Charset {

    String value() default "UTF-8";

}
