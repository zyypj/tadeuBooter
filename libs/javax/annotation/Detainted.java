package me.syncwrld.booter.libs.javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifierNickname;
import me.syncwrld.booter.libs.javax.annotation.meta.When;

@Documented
@Untainted(when = When.ALWAYS)
@Retention(RetentionPolicy.RUNTIME)
@TypeQualifierNickname
public @interface Detainted {}
