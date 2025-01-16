package me.syncwrld.booter.libs.checkerfw.common.value.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.ConditionalPostconditionAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InheritedAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.QualifierArgument;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@ConditionalPostconditionAnnotation(qualifier = MinLen.class)
@InheritedAnnotation
@Repeatable(EnsuresMinLenIf.List.class)
public @interface EnsuresMinLenIf {
  boolean result();
  
  String[] expression();
  
  @QualifierArgument("value")
  int targetValue() default 0;
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @ConditionalPostconditionAnnotation(qualifier = MinLen.class)
  @InheritedAnnotation
  public static @interface List {
    EnsuresMinLenIf[] value();
  }
}
