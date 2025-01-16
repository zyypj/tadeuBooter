package me.syncwrld.booter.libs.checkerfw.checker.interning.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultFor;
import me.syncwrld.booter.libs.checkerfw.framework.qual.LiteralKind;
import me.syncwrld.booter.libs.checkerfw.framework.qual.QualifierForLiterals;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeKind;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({UnknownInterned.class})
@QualifierForLiterals({LiteralKind.PRIMITIVE, LiteralKind.STRING})
@DefaultFor(typeKinds = {TypeKind.BOOLEAN, TypeKind.BYTE, TypeKind.CHAR, TypeKind.DOUBLE, TypeKind.FLOAT, TypeKind.INT, TypeKind.LONG, TypeKind.SHORT})
public @interface Interned {}
