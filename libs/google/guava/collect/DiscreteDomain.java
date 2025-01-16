package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.primitives.Ints;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class DiscreteDomain<C extends Comparable> {
  final boolean supportsFastOffset;
  
  public static DiscreteDomain<Integer> integers() {
    return IntegerDomain.INSTANCE;
  }
  
  private static final class IntegerDomain extends DiscreteDomain<Integer> implements Serializable {
    private static final IntegerDomain INSTANCE = new IntegerDomain();
    
    private static final long serialVersionUID = 0L;
    
    IntegerDomain() {
      super(true);
    }
    
    @CheckForNull
    public Integer next(Integer value) {
      int i = value.intValue();
      return (i == Integer.MAX_VALUE) ? null : Integer.valueOf(i + 1);
    }
    
    @CheckForNull
    public Integer previous(Integer value) {
      int i = value.intValue();
      return (i == Integer.MIN_VALUE) ? null : Integer.valueOf(i - 1);
    }
    
    Integer offset(Integer origin, long distance) {
      CollectPreconditions.checkNonnegative(distance, "distance");
      return Integer.valueOf(Ints.checkedCast(origin.longValue() + distance));
    }
    
    public long distance(Integer start, Integer end) {
      return end.intValue() - start.intValue();
    }
    
    public Integer minValue() {
      return Integer.valueOf(-2147483648);
    }
    
    public Integer maxValue() {
      return Integer.valueOf(2147483647);
    }
    
    private Object readResolve() {
      return INSTANCE;
    }
    
    public String toString() {
      return "DiscreteDomain.integers()";
    }
  }
  
  public static DiscreteDomain<Long> longs() {
    return LongDomain.INSTANCE;
  }
  
  private static final class LongDomain extends DiscreteDomain<Long> implements Serializable {
    private static final LongDomain INSTANCE = new LongDomain();
    
    private static final long serialVersionUID = 0L;
    
    LongDomain() {
      super(true);
    }
    
    @CheckForNull
    public Long next(Long value) {
      long l = value.longValue();
      return (l == Long.MAX_VALUE) ? null : Long.valueOf(l + 1L);
    }
    
    @CheckForNull
    public Long previous(Long value) {
      long l = value.longValue();
      return (l == Long.MIN_VALUE) ? null : Long.valueOf(l - 1L);
    }
    
    Long offset(Long origin, long distance) {
      CollectPreconditions.checkNonnegative(distance, "distance");
      long result = origin.longValue() + distance;
      if (result < 0L)
        Preconditions.checkArgument((origin.longValue() < 0L), "overflow"); 
      return Long.valueOf(result);
    }
    
    public long distance(Long start, Long end) {
      long result = end.longValue() - start.longValue();
      if (end.longValue() > start.longValue() && result < 0L)
        return Long.MAX_VALUE; 
      if (end.longValue() < start.longValue() && result > 0L)
        return Long.MIN_VALUE; 
      return result;
    }
    
    public Long minValue() {
      return Long.valueOf(Long.MIN_VALUE);
    }
    
    public Long maxValue() {
      return Long.valueOf(Long.MAX_VALUE);
    }
    
    private Object readResolve() {
      return INSTANCE;
    }
    
    public String toString() {
      return "DiscreteDomain.longs()";
    }
  }
  
  public static DiscreteDomain<BigInteger> bigIntegers() {
    return BigIntegerDomain.INSTANCE;
  }
  
  private static final class BigIntegerDomain extends DiscreteDomain<BigInteger> implements Serializable {
    private static final BigIntegerDomain INSTANCE = new BigIntegerDomain();
    
    BigIntegerDomain() {
      super(true);
    }
    
    private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
    
    private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    
    private static final long serialVersionUID = 0L;
    
    public BigInteger next(BigInteger value) {
      return value.add(BigInteger.ONE);
    }
    
    public BigInteger previous(BigInteger value) {
      return value.subtract(BigInteger.ONE);
    }
    
    BigInteger offset(BigInteger origin, long distance) {
      CollectPreconditions.checkNonnegative(distance, "distance");
      return origin.add(BigInteger.valueOf(distance));
    }
    
    public long distance(BigInteger start, BigInteger end) {
      return end.subtract(start).max(MIN_LONG).min(MAX_LONG).longValue();
    }
    
    private Object readResolve() {
      return INSTANCE;
    }
    
    public String toString() {
      return "DiscreteDomain.bigIntegers()";
    }
  }
  
  protected DiscreteDomain() {
    this(false);
  }
  
  private DiscreteDomain(boolean supportsFastOffset) {
    this.supportsFastOffset = supportsFastOffset;
  }
  
  C offset(C origin, long distance) {
    C current = origin;
    CollectPreconditions.checkNonnegative(distance, "distance");
    long i;
    for (i = 0L; i < distance; i++) {
      current = next(current);
      if (current == null)
        throw new IllegalArgumentException("overflowed computing offset(" + origin + ", " + distance + ")"); 
    } 
    return current;
  }
  
  @CanIgnoreReturnValue
  public C minValue() {
    throw new NoSuchElementException();
  }
  
  @CanIgnoreReturnValue
  public C maxValue() {
    throw new NoSuchElementException();
  }
  
  @CheckForNull
  public abstract C next(C paramC);
  
  @CheckForNull
  public abstract C previous(C paramC);
  
  public abstract long distance(C paramC1, C paramC2);
}
