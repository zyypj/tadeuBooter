package me.syncwrld.booter.libs.google.guava.graph;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.Nonnull;
import me.syncwrld.booter.libs.javax.annotation.meta.TypeQualifierDefault;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@TypeQualifierDefault({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Nonnull
@GwtCompatible
@interface ElementTypesAreNonnullByDefault {}
