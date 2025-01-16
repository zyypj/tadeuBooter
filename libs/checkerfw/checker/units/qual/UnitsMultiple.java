package me.syncwrld.booter.libs.checkerfw.checker.units.qual;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface UnitsMultiple {
  Class<? extends Annotation> quantity();
  
  Prefix prefix() default Prefix.one;
}
