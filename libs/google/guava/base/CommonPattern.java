package me.syncwrld.booter.libs.google.guava.base;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class CommonPattern {
  public abstract CommonMatcher matcher(CharSequence paramCharSequence);
  
  public abstract String pattern();
  
  public abstract int flags();
  
  public abstract String toString();
  
  public static CommonPattern compile(String pattern) {
    return Platform.compilePattern(pattern);
  }
  
  public static boolean isPcreLike() {
    return Platform.patternCompilerIsPcreLike();
  }
}
