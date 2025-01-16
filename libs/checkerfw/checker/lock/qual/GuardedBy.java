package me.syncwrld.booter.libs.checkerfw.checker.lock.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultFor;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultQualifierInHierarchy;
import me.syncwrld.booter.libs.checkerfw.framework.qual.JavaExpression;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeKind;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.UpperBoundFor;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({GuardedByUnknown.class})
@DefaultQualifierInHierarchy
@DefaultFor(value = {TypeUseLocation.EXCEPTION_PARAMETER, TypeUseLocation.UPPER_BOUND}, typeKinds = {TypeKind.BOOLEAN, TypeKind.BYTE, TypeKind.CHAR, TypeKind.DOUBLE, TypeKind.FLOAT, TypeKind.INT, TypeKind.LONG, TypeKind.SHORT}, types = {String.class, Void.class})
@UpperBoundFor(typeKinds = {TypeKind.BOOLEAN, TypeKind.BYTE, TypeKind.CHAR, TypeKind.DOUBLE, TypeKind.FLOAT, TypeKind.INT, TypeKind.LONG, TypeKind.SHORT}, types = {String.class})
public @interface GuardedBy {
  @JavaExpression
  String[] value() default {};
}
