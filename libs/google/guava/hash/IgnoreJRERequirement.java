package me.syncwrld.booter.libs.google.guava.hash;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
@ElementTypesAreNonnullByDefault
@interface IgnoreJRERequirement {}
