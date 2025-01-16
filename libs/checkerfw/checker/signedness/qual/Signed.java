package me.syncwrld.booter.libs.checkerfw.checker.signedness.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultFor;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultQualifierInHierarchy;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeKind;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.UpperBoundFor;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({UnknownSignedness.class})
@DefaultQualifierInHierarchy
@DefaultFor(typeKinds = {TypeKind.BYTE, TypeKind.INT, TypeKind.LONG, TypeKind.SHORT, TypeKind.FLOAT, TypeKind.DOUBLE}, types = {Byte.class, Integer.class, Long.class, Short.class, Float.class, Double.class}, value = {TypeUseLocation.EXCEPTION_PARAMETER})
@UpperBoundFor(typeKinds = {TypeKind.FLOAT, TypeKind.DOUBLE}, types = {Float.class, Double.class})
public @interface Signed {}
