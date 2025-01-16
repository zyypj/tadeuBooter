package me.syncwrld.booter.libs.checkerfw.common.aliasing.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultFor;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultQualifierInHierarchy;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@SubtypeOf({})
@DefaultQualifierInHierarchy
@DefaultFor(value = {TypeUseLocation.UPPER_BOUND, TypeUseLocation.LOWER_BOUND}, types = {Void.class})
public @interface MaybeAliased {}
