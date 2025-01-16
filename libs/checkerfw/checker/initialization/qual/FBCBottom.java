package me.syncwrld.booter.libs.checkerfw.checker.initialization.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.LiteralKind;
import me.syncwrld.booter.libs.checkerfw.framework.qual.QualifierForLiterals;
import me.syncwrld.booter.libs.checkerfw.framework.qual.SubtypeOf;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TargetLocations;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@TargetLocations({TypeUseLocation.EXPLICIT_LOWER_BOUND, TypeUseLocation.EXPLICIT_UPPER_BOUND})
@SubtypeOf({UnderInitialization.class, Initialized.class})
@QualifierForLiterals({LiteralKind.NULL})
public @interface FBCBottom {}
