package me.syncwrld.booter.libs.checkerfw.checker.mustcall.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE_USE})
public @interface MustCallAlias {}
