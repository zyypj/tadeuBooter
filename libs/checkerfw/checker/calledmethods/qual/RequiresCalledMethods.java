package me.syncwrld.booter.libs.checkerfw.checker.calledmethods.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.PreconditionAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.QualifierArgument;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@PreconditionAnnotation(qualifier = CalledMethods.class)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Repeatable(RequiresCalledMethods.List.class)
public @interface RequiresCalledMethods {
  String[] value();
  
  @QualifierArgument("value")
  String[] methods();
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @PreconditionAnnotation(qualifier = CalledMethods.class)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  public static @interface List {
    RequiresCalledMethods[] value();
  }
}
