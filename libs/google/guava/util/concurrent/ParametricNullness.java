package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.Nonnull;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifierNickname;
import me.syncwrld.booter.libs.javax.annotation.meta.When;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Nonnull(when = When.UNKNOWN)
@GwtCompatible
@TypeQualifierNickname
@interface ParametricNullness {}
