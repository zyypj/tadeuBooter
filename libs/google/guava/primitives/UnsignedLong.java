package me.syncwrld.booter.libs.google.guava.primitives;

import java.io.Serializable;
import java.math.BigInteger;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
public final class UnsignedLong extends Number implements Comparable<UnsignedLong>, Serializable {
  private static final long UNSIGNED_MASK = 9223372036854775807L;
  
  public static final UnsignedLong ZERO = new UnsignedLong(0L);
  
  public static final UnsignedLong ONE = new UnsignedLong(1L);
  
  public static final UnsignedLong MAX_VALUE = new UnsignedLong(-1L);
  
  private final long value;
  
  private UnsignedLong(long value) {
    this.value = value;
  }
  
  public static UnsignedLong fromLongBits(long bits) {
    return new UnsignedLong(bits);
  }
  
  @CanIgnoreReturnValue
  public static UnsignedLong valueOf(long value) {
    Preconditions.checkArgument((value >= 0L), "value (%s) is outside the range for an unsigned long value", value);
    return fromLongBits(value);
  }
  
  @CanIgnoreReturnValue
  public static UnsignedLong valueOf(BigInteger value) {
    Preconditions.checkNotNull(value);
    Preconditions.checkArgument((value
        .signum() >= 0 && value.bitLength() <= 64), "value (%s) is outside the range for an unsigned long value", value);
    return fromLongBits(value.longValue());
  }
  
  @CanIgnoreReturnValue
  public static UnsignedLong valueOf(String string) {
    return valueOf(string, 10);
  }
  
  @CanIgnoreReturnValue
  public static UnsignedLong valueOf(String string, int radix) {
    return fromLongBits(UnsignedLongs.parseUnsignedLong(string, radix));
  }
  
  public UnsignedLong plus(UnsignedLong val) {
    return fromLongBits(this.value + ((UnsignedLong)Preconditions.checkNotNull(val)).value);
  }
  
  public UnsignedLong minus(UnsignedLong val) {
    return fromLongBits(this.value - ((UnsignedLong)Preconditions.checkNotNull(val)).value);
  }
  
  public UnsignedLong times(UnsignedLong val) {
    return fromLongBits(this.value * ((UnsignedLong)Preconditions.checkNotNull(val)).value);
  }
  
  public UnsignedLong dividedBy(UnsignedLong val) {
    return fromLongBits(UnsignedLongs.divide(this.value, ((UnsignedLong)Preconditions.checkNotNull(val)).value));
  }
  
  public UnsignedLong mod(UnsignedLong val) {
    return fromLongBits(UnsignedLongs.remainder(this.value, ((UnsignedLong)Preconditions.checkNotNull(val)).value));
  }
  
  public int intValue() {
    return (int)this.value;
  }
  
  public long longValue() {
    return this.value;
  }
  
  public float floatValue() {
    if (this.value >= 0L)
      return (float)this.value; 
    return (float)(this.value >>> 1L | this.value & 0x1L) * 2.0F;
  }
  
  public double doubleValue() {
    if (this.value >= 0L)
      return this.value; 
    return (this.value >>> 1L | this.value & 0x1L) * 2.0D;
  }
  
  public BigInteger bigIntegerValue() {
    BigInteger bigInt = BigInteger.valueOf(this.value & Long.MAX_VALUE);
    if (this.value < 0L)
      bigInt = bigInt.setBit(63); 
    return bigInt;
  }
  
  public int compareTo(UnsignedLong o) {
    Preconditions.checkNotNull(o);
    return UnsignedLongs.compare(this.value, o.value);
  }
  
  public int hashCode() {
    return Longs.hashCode(this.value);
  }
  
  public boolean equals(@CheckForNull Object obj) {
    if (obj instanceof UnsignedLong) {
      UnsignedLong other = (UnsignedLong)obj;
      return (this.value == other.value);
    } 
    return false;
  }
  
  public String toString() {
    return UnsignedLongs.toString(this.value);
  }
  
  public String toString(int radix) {
    return UnsignedLongs.toString(this.value, radix);
  }
}
