package me.syncwrld.booter.libs.checkerfw.checker.fenum.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultFor;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultQualifierInHierarchy;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@SubtypeOf({FenumTop.class})
@DefaultQualifierInHierarchy
@DefaultFor({TypeUseLocation.EXCEPTION_PARAMETER})
public @interface FenumUnqualified {}
