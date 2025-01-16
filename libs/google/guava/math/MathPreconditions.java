package me.syncwrld.booter.libs.google.guava.math;

import java.math.BigInteger;
import java.math.RoundingMode;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class MathPreconditions {
  @CanIgnoreReturnValue
  static int checkPositive(String role, int x) {
    if (x <= 0)
      throw new IllegalArgumentException(role + " (" + x + ") must be > 0"); 
    return x;
  }
  
  @CanIgnoreReturnValue
  static long checkPositive(String role, long x) {
    if (x <= 0L)
      throw new IllegalArgumentException(role + " (" + x + ") must be > 0"); 
    return x;
  }
  
  @CanIgnoreReturnValue
  static BigInteger checkPositive(String role, BigInteger x) {
    if (x.signum() <= 0)
      throw new IllegalArgumentException(role + " (" + x + ") must be > 0"); 
    return x;
  }
  
  @CanIgnoreReturnValue
  static int checkNonNegative(String role, int x) {
    if (x < 0)
      throw new IllegalArgumentException(role + " (" + x + ") must be >= 0"); 
    return x;
  }
  
  @CanIgnoreReturnValue
  static long checkNonNegative(String role, long x) {
    if (x < 0L)
      throw new IllegalArgumentException(role + " (" + x + ") must be >= 0"); 
    return x;
  }
  
  @CanIgnoreReturnValue
  static BigInteger checkNonNegative(String role, BigInteger x) {
    if (x.signum() < 0)
      throw new IllegalArgumentException(role + " (" + x + ") must be >= 0"); 
    return x;
  }
  
  @CanIgnoreReturnValue
  static double checkNonNegative(String role, double x) {
    if (x < 0.0D)
      throw new IllegalArgumentException(role + " (" + x + ") must be >= 0"); 
    return x;
  }
  
  static void checkRoundingUnnecessary(boolean condition) {
    if (!condition)
      throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary"); 
  }
  
  static void checkInRangeForRoundingInputs(boolean condition, double input, RoundingMode mode) {
    if (!condition)
      throw new ArithmeticException("rounded value is out of range for input " + input + " and rounding mode " + mode); 
  }
  
  static void checkNoOverflow(boolean condition, String methodName, int a, int b) {
    if (!condition)
      throw new ArithmeticException("overflow: " + methodName + "(" + a + ", " + b + ")"); 
  }
  
  static void checkNoOverflow(boolean condition, String methodName, long a, long b) {
    if (!condition)
      throw new ArithmeticException("overflow: " + methodName + "(" + a + ", " + b + ")"); 
  }
}
