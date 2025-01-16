package me.syncwrld.booter.libs.google.kyori.adventure.util;

import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.jtann.Nullable;

final class HSVLikeImpl implements HSVLike {
  private final float h;
  
  private final float s;
  
  private final float v;
  
  HSVLikeImpl(float h, float s, float v) {
    requireInsideRange(h, "h");
    requireInsideRange(s, "s");
    requireInsideRange(v, "v");
    this.h = h;
    this.s = s;
    this.v = v;
  }
  
  public float h() {
    return this.h;
  }
  
  public float s() {
    return this.s;
  }
  
  public float v() {
    return this.v;
  }
  
  private static void requireInsideRange(float number, String name) throws IllegalArgumentException {
    if (number < 0.0F || 1.0F < number)
      throw new IllegalArgumentException(name + " (" + number + ") is not inside the required range: [0,1]"); 
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof HSVLikeImpl))
      return false; 
    HSVLikeImpl that = (HSVLikeImpl)other;
    return (ShadyPines.equals(that.h, this.h) && ShadyPines.equals(that.s, this.s) && ShadyPines.equals(that.v, this.v));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { Float.valueOf(this.h), Float.valueOf(this.s), Float.valueOf(this.v) });
  }
  
  public String toString() {
    return Internals.toString(this);
  }
}
