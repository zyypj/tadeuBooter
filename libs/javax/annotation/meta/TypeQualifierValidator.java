package me.syncwrld.booter.libs.javax.annotation.meta;

import me.syncwrld.booter.libs.javax.annotation.Nonnull;

public interface TypeQualifierValidator<A extends java.lang.annotation.Annotation> {
  @Nonnull
  When forConstantValue(@Nonnull A paramA, Object paramObject);
}
