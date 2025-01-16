package me.syncwrld.booter.libs.checkerfw.checker.lock.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InheritedAnnotation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.PostconditionAnnotation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@PostconditionAnnotation(qualifier = LockHeld.class)
@InheritedAnnotation
@Repeatable(EnsuresLockHeld.List.class)
public @interface EnsuresLockHeld {
  String[] value();
  
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
  @PostconditionAnnotation(qualifier = LockHeld.class)
  @InheritedAnnotation
  public static @interface List {
    EnsuresLockHeld[] value();
  }
}
