package me.syncwrld.booter.libs.checkerfw.dataflow.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AssertMethod {
  Class<?> value() default AssertionError.class;
  
  int parameter() default 1;
  
  boolean isAssertFalse() default false;
}
