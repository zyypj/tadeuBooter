package me.syncwrld.booter.libs.checkerfw.common.value.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.InvisibleQualifier;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TargetLocations;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@TargetLocations({TypeUseLocation.EXPLICIT_LOWER_BOUND, TypeUseLocation.EXPLICIT_UPPER_BOUND})
@SubtypeOf({ArrayLen.class, BoolVal.class, DoubleVal.class, IntVal.class, StringVal.class, MatchesRegex.class, DoesNotMatchRegex.class, ArrayLenRange.class, IntRange.class, IntRangeFromPositive.class, IntRangeFromGTENegativeOne.class, IntRangeFromNonNegative.class})
@InvisibleQualifier
public @interface BottomVal {}
