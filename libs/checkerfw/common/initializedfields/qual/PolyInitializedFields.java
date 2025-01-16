package me.syncwrld.booter.libs.checkerfw.common.initializedfields.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import me.syncwrld.booter.libs.checkerfw.framework.qual.PolymorphicQualifier;

@PolymorphicQualifier(InitializedFields.class)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface PolyInitializedFields {}
