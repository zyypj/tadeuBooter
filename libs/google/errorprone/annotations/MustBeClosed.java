package me.syncwrld.booter.libs.google.errorprone.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface MustBeClosed {}
