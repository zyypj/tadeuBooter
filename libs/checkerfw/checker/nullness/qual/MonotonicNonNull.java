package me.syncwrld.booter.libs.checkerfw.checker.nullness.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.MonotonicQualifier;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
@SubtypeOf({Nullable.class})
@MonotonicQualifier(NonNull.class)
public @interface MonotonicNonNull {}
