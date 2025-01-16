package me.syncwrld.booter.libs.checkerfw.checker.nullness.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultFor;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultQualifierInHierarchy;
import me.syncwrld.booter.libs.checkerfw.framework.qual.LiteralKind;
import me.syncwrld.booter.libs.checkerfw.framework.qual.QualifierForLiterals;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeKind;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;
import me.syncwrld.booter.libs.checkerfw.framework.qual.UpperBoundFor;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({MonotonicNonNull.class})
@DefaultQualifierInHierarchy
@QualifierForLiterals({LiteralKind.STRING})
@DefaultFor({TypeUseLocation.EXCEPTION_PARAMETER})
@UpperBoundFor(typeKinds = {TypeKind.PACKAGE, TypeKind.INT, TypeKind.BOOLEAN, TypeKind.CHAR, TypeKind.DOUBLE, TypeKind.FLOAT, TypeKind.LONG, TypeKind.SHORT, TypeKind.BYTE})
public @interface NonNull {}
