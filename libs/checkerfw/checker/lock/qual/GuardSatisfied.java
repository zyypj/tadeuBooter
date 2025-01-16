package me.syncwrld.booter.libs.checkerfw.checker.lock.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TargetLocations;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
@TargetLocations({TypeUseLocation.RECEIVER, TypeUseLocation.PARAMETER, TypeUseLocation.RETURN})
@SubtypeOf({GuardedByUnknown.class})
public @interface GuardSatisfied {
  int value() default -1;
}
