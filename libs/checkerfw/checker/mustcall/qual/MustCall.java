package me.syncwrld.booter.libs.checkerfw.checker.mustcall.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultFor;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultQualifierInHierarchy;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({MustCallUnknown.class})
@DefaultQualifierInHierarchy
@DefaultFor({TypeUseLocation.EXCEPTION_PARAMETER, TypeUseLocation.UPPER_BOUND})
public @interface MustCall {
  String[] value() default {};
}
