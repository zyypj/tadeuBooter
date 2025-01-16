package me.syncwrld.booter.libs.checkerfw.common.returnsreceiver.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultFor;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultQualifierInHierarchy;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InvisibleQualifier;
import me.syncwrld.booter.libs.checkerfw.framework.qual.LiteralKind;
import me.syncwrld.booter.libs.checkerfw.framework.qual.QualifierForLiterals;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({})
@DefaultQualifierInHierarchy
@QualifierForLiterals({LiteralKind.NULL})
@DefaultFor({TypeUseLocation.LOWER_BOUND})
@InvisibleQualifier
public @interface UnknownThis {}
