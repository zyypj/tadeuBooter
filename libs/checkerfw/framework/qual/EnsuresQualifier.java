package me.syncwrld.booter.libs.checkerfw.framework.qual;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@InheritedAnnotation
@Repeatable(EnsuresQualifier.List.class)
public @interface EnsuresQualifier {
  String[] expression();
  
  Class<? extends Annotation> qualifier();
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @InheritedAnnotation
  public static @interface List {
    EnsuresQualifier[] value();
  }
}
