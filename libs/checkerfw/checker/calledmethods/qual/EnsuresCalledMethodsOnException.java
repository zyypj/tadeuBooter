package me.syncwrld.booter.libs.checkerfw.checker.calledmethods.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InheritedAnnotation;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Repeatable(EnsuresCalledMethodsOnException.List.class)
@Retention(RetentionPolicy.RUNTIME)
@InheritedAnnotation
public @interface EnsuresCalledMethodsOnException {
  String[] value();
  
  String[] methods();
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @InheritedAnnotation
  public static @interface List {
    EnsuresCalledMethodsOnException[] value();
  }
}
