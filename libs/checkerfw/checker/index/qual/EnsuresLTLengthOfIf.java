package me.syncwrld.booter.libs.checkerfw.checker.index.qual;

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
@ConditionalPostconditionAnnotation(qualifier = LTLengthOf.class)
@InheritedAnnotation
@Repeatable(EnsuresLTLengthOfIf.List.class)
public @interface EnsuresLTLengthOfIf {
  boolean result();
  
  String[] expression();
  
  @JavaExpression
  @QualifierArgument("value")
  String[] targetValue();
  
  @JavaExpression
  @QualifierArgument("offset")
  String[] offset() default {};
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @ConditionalPostconditionAnnotation(qualifier = LTLengthOf.class)
  @InheritedAnnotation
  public static @interface List {
    EnsuresLTLengthOfIf[] value();
  }
}
