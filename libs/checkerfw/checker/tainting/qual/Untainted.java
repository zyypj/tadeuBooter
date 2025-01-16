package me.syncwrld.booter.libs.checkerfw.checker.tainting.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultFor;
import me.syncwrld.booter.libs.checkerfw.framework.qual.LiteralKind;
import me.syncwrld.booter.libs.checkerfw.framework.qual.QualifierForLiterals;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({Tainted.class})
@QualifierForLiterals({LiteralKind.STRING})
@DefaultFor({TypeUseLocation.LOWER_BOUND})
public @interface Untainted {}
