package me.syncwrld.booter.libs.checkerfw.checker.nullness.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.ConditionalPostconditionAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InheritedAnnotation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@ConditionalPostconditionAnnotation(qualifier = NonNull.class)
@InheritedAnnotation
@Repeatable(EnsuresNonNullIf.List.class)
public @interface EnsuresNonNullIf {
  boolean result();
  
  String[] expression();
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @ConditionalPostconditionAnnotation(qualifier = NonNull.class)
  @InheritedAnnotation
  public static @interface List {
    EnsuresNonNullIf[] value();
  }
}
