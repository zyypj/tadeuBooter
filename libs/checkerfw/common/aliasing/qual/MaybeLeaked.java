package me.syncwrld.booter.libs.checkerfw.common.aliasing.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.DefaultQualifierInHierarchy;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InvisibleQualifier;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@DefaultQualifierInHierarchy
@SubtypeOf({LeakedToResult.class})
@InvisibleQualifier
public @interface MaybeLeaked {}
