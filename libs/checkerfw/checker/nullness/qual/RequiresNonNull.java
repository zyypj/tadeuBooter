package me.syncwrld.booter.libs.checkerfw.checker.nullness.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.PreconditionAnnotation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Repeatable(RequiresNonNull.List.class)
@PreconditionAnnotation(qualifier = NonNull.class)
public @interface RequiresNonNull {
  String[] value();
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @PreconditionAnnotation(qualifier = NonNull.class)
  public static @interface List {
    RequiresNonNull[] value();
  }
}
