package hl.nio.codec.pack.annotation;

import hl.nio.codec.Category;
import hl.nio.codec.Version;
import hl.nio.codec.pack.PackageCodec;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Package {

	Version version() default Version.V1_0;
	Category category() default Category.UserCustom;
	int typeId() default 0;
	int value()  default 0;

	@SuppressWarnings("rawtypes")
	Class<? extends PackageCodec> codec() default PackageCodec.class;
	
}
