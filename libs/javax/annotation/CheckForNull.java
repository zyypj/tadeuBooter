package me.syncwrld.booter.libs.javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifierNickname;
import me.syncwrld.booter.libs.javax.annotation.meta.When;

@Documented
@Nonnull(when = When.MAYBE)
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
public @interface CheckForNull {}
