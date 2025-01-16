package me.syncwrld.booter.libs.checkerfw.checker.optional.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InheritedAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.PostconditionAnnotation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@PostconditionAnnotation(qualifier = Present.class)
@InheritedAnnotation
public @interface EnsuresPresent {
  String[] value();
}
