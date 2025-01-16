package me.syncwrld.booter.libs.google.guava.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public class BigDecimalMath {
  public static double roundToDouble(BigDecimal x, RoundingMode mode) {
    return BigDecimalToDoubleRounder.INSTANCE.roundToDouble(x, mode);
  }
  
  private static class BigDecimalToDoubleRounder extends ToDoubleRounder<BigDecimal> {
    static final BigDecimalToDoubleRounder INSTANCE = new BigDecimalToDoubleRounder();
    
    double roundToDoubleArbitrarily(BigDecimal bigDecimal) {
      return bigDecimal.doubleValue();
    }
    
    int sign(BigDecimal bigDecimal) {
      return bigDecimal.signum();
    }
    
    BigDecimal toX(double d, RoundingMode mode) {
      return new BigDecimal(d);
    }
    
    BigDecimal minus(BigDecimal a, BigDecimal b) {
      return a.subtract(b);
    }
  }
}
