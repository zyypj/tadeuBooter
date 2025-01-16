package me.syncwrld.booter.libs.checkerfw.common.subtyping.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TargetLocations;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;

@Documented
@SubtypeOf({})
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@TargetLocations({TypeUseLocation.EXPLICIT_LOWER_BOUND, TypeUseLocation.EXPLICIT_UPPER_BOUND})
public @interface Bottom {}
