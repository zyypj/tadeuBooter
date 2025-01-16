package me.syncwrld.booter.libs.checkerfw.checker.mustcall.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InheritedAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.JavaExpression;

@Target({ElementType.METHOD})
@InheritedAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CreatesMustCallFor.List.class)
public @interface CreatesMustCallFor {
  @JavaExpression
  String value() default "this";
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD})
  @InheritedAnnotation
  public static @interface List {
    CreatesMustCallFor[] value();
  }
}
