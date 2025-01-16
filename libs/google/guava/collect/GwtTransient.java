package me.syncwrld.booter.libs.google.guava.collect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@ElementTypesAreNonnullByDefault
@GwtCompatible
@interface GwtTransient {}
