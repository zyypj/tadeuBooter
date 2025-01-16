package me.syncwrld.booter.libs.checkerfw.checker.units.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
@SubtypeOf({UnknownUnits.class})
public @interface MixedUnits {}
