package me.syncwrld.booter.libs.javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifierNickname;
import me.syncwrld.booter.libs.javax.annotation.meta.When;

@Documented
@Nonnegative(when = When.UNKNOWN)
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
public @interface Signed {}
