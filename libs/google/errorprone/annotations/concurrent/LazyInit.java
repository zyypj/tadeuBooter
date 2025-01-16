package me.syncwrld.booter.libs.google.errorprone.annotations.concurrent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.google.errorprone.annotations.IncompatibleModifiers;
import me.syncwrld.booter.libs.google.errorprone.annotations.Modifier;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@IncompatibleModifiers(modifier = {Modifier.FINAL})
public @interface LazyInit {}
