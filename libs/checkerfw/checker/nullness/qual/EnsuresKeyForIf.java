package me.syncwrld.booter.libs.checkerfw.checker.nullness.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.ConditionalPostconditionAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InheritedAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.JavaExpression;
import me.syncwrld.booter.libs.checkerfw.framework.qual.QualifierArgument;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@ConditionalPostconditionAnnotation(qualifier = KeyFor.class)
@InheritedAnnotation
@Repeatable(EnsuresKeyForIf.List.class)
public @interface EnsuresKeyForIf {
  boolean result();
  
  String[] expression();
  
  @JavaExpression
  @QualifierArgument("value")
  String[] map();
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @ConditionalPostconditionAnnotation(qualifier = KeyFor.class)
  @InheritedAnnotation
  public static @interface List {
    EnsuresKeyForIf[] value();
  }
}
