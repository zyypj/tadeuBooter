package me.syncwrld.booter.libs.checkerfw.common.returnsreceiver.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.PolymorphicQualifier;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TargetLocations;
import me.syncwrld.booter.libs.checkerfw.framework.qual.TypeUseLocation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@PolymorphicQualifier
@TargetLocations({TypeUseLocation.RECEIVER, TypeUseLocation.RETURN})
public @interface This {}
