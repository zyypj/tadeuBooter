package me.syncwrld.booter.libs.checkerfw.common.subtyping.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InvisibleQualifier;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;

@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({})
@InvisibleQualifier
@SubtypeOf({})
public @interface Unqualified {}
