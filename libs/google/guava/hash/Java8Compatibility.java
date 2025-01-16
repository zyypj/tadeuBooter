package me.syncwrld.booter.libs.google.guava.hash;

import java.nio.Buffer;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class Java8Compatibility {
  static void clear(Buffer b) {
    b.clear();
  }
  
  static void flip(Buffer b) {
    b.flip();
  }
  
  static void limit(Buffer b, int limit) {
    b.limit(limit);
  }
  
  static void position(Buffer b, int position) {
    b.position(position);
  }
}
