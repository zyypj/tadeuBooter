package me.syncwrld.booter.libs.google.guava.base;

import me.syncwrld.booter.libs.google.errorprone.annotations.RestrictedApi;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
interface PatternCompiler {
  @RestrictedApi(explanation = "PatternCompiler is an implementation detail of com.google.common.base", allowedOnPath = ".*/com/google/common/base/.*")
  CommonPattern compile(String paramString);
  
  @RestrictedApi(explanation = "PatternCompiler is an implementation detail of com.google.common.base", allowedOnPath = ".*/com/google/common/base/.*")
  boolean isPcreLike();
}
