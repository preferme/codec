package hl.nio.codec.field.annotation;

import hl.nio.codec.field.FieldCodec;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({ FIELD })
public @interface Field {

	int ordinal() default Integer.MAX_VALUE;
	boolean ignore() default false;
	
	@SuppressWarnings("rawtypes")
	Class<? extends FieldCodec> codec() default FieldCodec.class;

}
