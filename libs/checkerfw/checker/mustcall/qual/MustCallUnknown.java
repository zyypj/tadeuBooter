package me.syncwrld.booter.libs.checkerfw.checker.mustcall.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({})
public @interface MustCallUnknown {}
