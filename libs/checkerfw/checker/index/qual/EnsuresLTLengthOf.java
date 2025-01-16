package me.syncwrld.booter.libs.checkerfw.checker.index.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InheritedAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.JavaExpression;
import me.syncwrld.booter.libs.checkerfw.framework.qual.PostconditionAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.QualifierArgument;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@PostconditionAnnotation(qualifier = LTLengthOf.class)
@InheritedAnnotation
@Repeatable(EnsuresLTLengthOf.List.class)
public @interface EnsuresLTLengthOf {
  @JavaExpression
  String[] value();
  
  @JavaExpression
  @QualifierArgument("value")
  String[] targetValue();
  
  @JavaExpression
  @QualifierArgument("offset")
  String[] offset() default {};
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @PostconditionAnnotation(qualifier = LTLengthOf.class)
  @InheritedAnnotation
  public static @interface List {
    EnsuresLTLengthOf[] value();
  }
}
