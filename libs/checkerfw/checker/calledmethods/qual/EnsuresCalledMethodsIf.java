package me.syncwrld.booter.libs.checkerfw.checker.calledmethods.qual;

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
@ConditionalPostconditionAnnotation(qualifier = CalledMethods.class)
@InheritedAnnotation
@Repeatable(EnsuresCalledMethodsIf.List.class)
public @interface EnsuresCalledMethodsIf {
  boolean result();
  
  String[] expression();
  
  @QualifierArgument("value")
  String[] methods();
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @ConditionalPostconditionAnnotation(qualifier = CalledMethods.class)
  @InheritedAnnotation
  public static @interface List {
    EnsuresCalledMethodsIf[] value();
  }
}
