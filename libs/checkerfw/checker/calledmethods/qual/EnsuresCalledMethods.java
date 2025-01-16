package me.syncwrld.booter.libs.checkerfw.checker.calledmethods.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InheritedAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.PostconditionAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.QualifierArgument;

@PostconditionAnnotation(qualifier = CalledMethods.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Repeatable(EnsuresCalledMethods.List.class)
@InheritedAnnotation
public @interface EnsuresCalledMethods {
  String[] value();
  
  @QualifierArgument("value")
  String[] methods();
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @InheritedAnnotation
  @PostconditionAnnotation(qualifier = CalledMethods.class)
  public static @interface List {
    EnsuresCalledMethods[] value();
  }
}
