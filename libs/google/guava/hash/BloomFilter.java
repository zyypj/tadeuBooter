package me.syncwrld.booter.libs.google.guava.hash;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.RoundingMode;
import java.util.stream.Collector;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.Beta;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Predicate;
import me.syncwrld.booter.libs.google.guava.math.DoubleMath;
import me.syncwrld.booter.libs.google.guava.math.LongMath;
import me.syncwrld.booter.libs.google.guava.primitives.SignedBytes;
import me.syncwrld.booter.libs.google.guava.primitives.UnsignedBytes;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@Beta
public final class BloomFilter<T> implements Predicate<T>, Serializable {
  private final BloomFilterStrategies.LockFreeBitArray bits;
  
  private final int numHashFunctions;
  
  private final Funnel<? super T> funnel;
  
  private final Strategy strategy;
  
  private static final long serialVersionUID = -889275714L;
  
  private BloomFilter(BloomFilterStrategies.LockFreeBitArray bits, int numHashFunctions, Funnel<? super T> funnel, Strategy strategy) {
    Preconditions.checkArgument((numHashFunctions > 0), "numHashFunctions (%s) must be > 0", numHashFunctions);
    Preconditions.checkArgument((numHashFunctions <= 255), "numHashFunctions (%s) must be <= 255", numHashFunctions);
    this.bits = (BloomFilterStrategies.LockFreeBitArray)Preconditions.checkNotNull(bits);
    this.numHashFunctions = numHashFunctions;
    this.funnel = (Funnel<? super T>)Preconditions.checkNotNull(funnel);
    this.strategy = (Strategy)Preconditions.checkNotNull(strategy);
  }
  
  public BloomFilter<T> copy() {
    return new BloomFilter(this.bits.copy(), this.numHashFunctions, this.funnel, this.strategy);
  }
  
  public boolean mightContain(@ParametricNullness T object) {
    return this.strategy.mightContain(object, this.funnel, this.numHashFunctions, this.bits);
  }
  
  @Deprecated
  public boolean apply(@ParametricNullness T input) {
    return mightContain(input);
  }
  
  @CanIgnoreReturnValue
  public boolean put(@ParametricNullness T object) {
    return this.strategy.put(object, this.funnel, this.numHashFunctions, this.bits);
  }
  
  public double expectedFpp() {
    return Math.pow(this.bits.bitCount() / bitSize(), this.numHashFunctions);
  }
  
  public long approximateElementCount() {
    long bitSize = this.bits.bitSize();
    long bitCount = this.bits.bitCount();
    double fractionOfBitsSet = bitCount / bitSize;
    return DoubleMath.roundToLong(
        -Math.log1p(-fractionOfBitsSet) * bitSize / this.numHashFunctions, RoundingMode.HALF_UP);
  }
  
  @VisibleForTesting
  long bitSize() {
    return this.bits.bitSize();
  }
  
  public boolean isCompatible(BloomFilter<T> that) {
    Preconditions.checkNotNull(that);
    return (this != that && this.numHashFunctions == that.numHashFunctions && 
      
      bitSize() == that.bitSize() && this.strategy
      .equals(that.strategy) && this.funnel
      .equals(that.funnel));
  }
  
  public void putAll(BloomFilter<T> that) {
    Preconditions.checkNotNull(that);
    Preconditions.checkArgument((this != that), "Cannot combine a BloomFilter with itself.");
    Preconditions.checkArgument((this.numHashFunctions == that.numHashFunctions), "BloomFilters must have the same number of hash functions (%s != %s)", this.numHashFunctions, that.numHashFunctions);
    Preconditions.checkArgument(
        (bitSize() == that.bitSize()), "BloomFilters must have the same size underlying bit arrays (%s != %s)", 
        
        bitSize(), that
        .bitSize());
    Preconditions.checkArgument(this.strategy
        .equals(that.strategy), "BloomFilters must have equal strategies (%s != %s)", this.strategy, that.strategy);
    Preconditions.checkArgument(this.funnel
        .equals(that.funnel), "BloomFilters must have equal funnels (%s != %s)", this.funnel, that.funnel);
    this.bits.putAll(that.bits);
  }
  
  public boolean equals(@CheckForNull Object object) {
    if (object == this)
      return true; 
    if (object instanceof BloomFilter) {
      BloomFilter<?> that = (BloomFilter)object;
      return (this.numHashFunctions == that.numHashFunctions && this.funnel
        .equals(that.funnel) && this.bits
        .equals(that.bits) && this.strategy
        .equals(that.strategy));
    } 
    return false;
  }
  
  public int hashCode() {
    return Objects.hashCode(new Object[] { Integer.valueOf(this.numHashFunctions), this.funnel, this.strategy, this.bits });
  }
  
  public static <T> Collector<T, ?, BloomFilter<T>> toBloomFilter(Funnel<? super T> funnel, long expectedInsertions) {
    return toBloomFilter(funnel, expectedInsertions, 0.03D);
  }
  
  public static <T> Collector<T, ?, BloomFilter<T>> toBloomFilter(Funnel<? super T> funnel, long expectedInsertions, double fpp) {
    Preconditions.checkNotNull(funnel);
    Preconditions.checkArgument((expectedInsertions >= 0L), "Expected insertions (%s) must be >= 0", expectedInsertions);
    Preconditions.checkArgument((fpp > 0.0D), "False positive probability (%s) must be > 0.0", Double.valueOf(fpp));
    Preconditions.checkArgument((fpp < 1.0D), "False positive probability (%s) must be < 1.0", Double.valueOf(fpp));
    return (Collector)Collector.of(() -> create(funnel, expectedInsertions, fpp), BloomFilter::put, (bf1, bf2) -> {
          bf1.putAll(bf2);
          return bf1;
        }new Collector.Characteristics[] { Collector.Characteristics.UNORDERED, Collector.Characteristics.CONCURRENT });
  }
  
  public static <T> BloomFilter<T> create(Funnel<? super T> funnel, int expectedInsertions, double fpp) {
    return create(funnel, expectedInsertions, fpp);
  }
  
  public static <T> BloomFilter<T> create(Funnel<? super T> funnel, long expectedInsertions, double fpp) {
    return create(funnel, expectedInsertions, fpp, BloomFilterStrategies.MURMUR128_MITZ_64);
  }
  
  @VisibleForTesting
  static <T> BloomFilter<T> create(Funnel<? super T> funnel, long expectedInsertions, double fpp, Strategy strategy) {
    Preconditions.checkNotNull(funnel);
    Preconditions.checkArgument((expectedInsertions >= 0L), "Expected insertions (%s) must be >= 0", expectedInsertions);
    Preconditions.checkArgument((fpp > 0.0D), "False positive probability (%s) must be > 0.0", Double.valueOf(fpp));
    Preconditions.checkArgument((fpp < 1.0D), "False positive probability (%s) must be < 1.0", Double.valueOf(fpp));
    Preconditions.checkNotNull(strategy);
    if (expectedInsertions == 0L)
      expectedInsertions = 1L; 
    long numBits = optimalNumOfBits(expectedInsertions, fpp);
    int numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, numBits);
    try {
      return new BloomFilter<>(new BloomFilterStrategies.LockFreeBitArray(numBits), numHashFunctions, funnel, strategy);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Could not create BloomFilter of " + numBits + " bits", e);
    } 
  }
  
  public static <T> BloomFilter<T> create(Funnel<? super T> funnel, int expectedInsertions) {
    return create(funnel, expectedInsertions);
  }
  
  public static <T> BloomFilter<T> create(Funnel<? super T> funnel, long expectedInsertions) {
    return create(funnel, expectedInsertions, 0.03D);
  }
  
  @VisibleForTesting
  static int optimalNumOfHashFunctions(long n, long m) {
    return Math.max(1, (int)Math.round(m / n * Math.log(2.0D)));
  }
  
  @VisibleForTesting
  static long optimalNumOfBits(long n, double p) {
    if (p == 0.0D)
      p = Double.MIN_VALUE; 
    return (long)(-n * Math.log(p) / Math.log(2.0D) * Math.log(2.0D));
  }
  
  private Object writeReplace() {
    return new SerialForm<>(this);
  }
  
  private void readObject(ObjectInputStream stream) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializedForm");
  }
  
  private static class SerialForm<T> implements Serializable {
    final long[] data;
    
    final int numHashFunctions;
    
    final Funnel<? super T> funnel;
    
    final BloomFilter.Strategy strategy;
    
    private static final long serialVersionUID = 1L;
    
    SerialForm(BloomFilter<T> bf) {
      this.data = BloomFilterStrategies.LockFreeBitArray.toPlainArray(bf.bits.data);
      this.numHashFunctions = bf.numHashFunctions;
      this.funnel = bf.funnel;
      this.strategy = bf.strategy;
    }
    
    Object readResolve() {
      return new BloomFilter(new BloomFilterStrategies.LockFreeBitArray(this.data), this.numHashFunctions, this.funnel, this.strategy);
    }
  }
  
  public void writeTo(OutputStream out) throws IOException {
    DataOutputStream dout = new DataOutputStream(out);
    dout.writeByte(SignedBytes.checkedCast(this.strategy.ordinal()));
    dout.writeByte(UnsignedBytes.checkedCast(this.numHashFunctions));
    dout.writeInt(this.bits.data.length());
    for (int i = 0; i < this.bits.data.length(); i++)
      dout.writeLong(this.bits.data.get(i)); 
  }
  
  public static <T> BloomFilter<T> readFrom(InputStream in, Funnel<? super T> funnel) throws IOException {
    Preconditions.checkNotNull(in, "InputStream");
    Preconditions.checkNotNull(funnel, "Funnel");
    int strategyOrdinal = -1;
    int numHashFunctions = -1;
    int dataLength = -1;
    try {
      DataInputStream din = new DataInputStream(in);
      strategyOrdinal = din.readByte();
      numHashFunctions = UnsignedBytes.toInt(din.readByte());
      dataLength = din.readInt();
      Strategy strategy = BloomFilterStrategies.values()[strategyOrdinal];
      BloomFilterStrategies.LockFreeBitArray dataArray = new BloomFilterStrategies.LockFreeBitArray(LongMath.checkedMultiply(dataLength, 64L));
      for (int i = 0; i < dataLength; i++)
        dataArray.putData(i, din.readLong()); 
      return new BloomFilter<>(dataArray, numHashFunctions, funnel, strategy);
    } catch (IOException e) {
      throw e;
    } catch (Exception e) {
      String message = "Unable to deserialize BloomFilter from InputStream. strategyOrdinal: " + strategyOrdinal + " numHashFunctions: " + numHashFunctions + " dataLength: " + dataLength;
      throw new IOException(message, e);
    } 
  }
  
  static interface Strategy extends Serializable {
    <T> boolean put(@ParametricNullness T param1T, Funnel<? super T> param1Funnel, int param1Int, BloomFilterStrategies.LockFreeBitArray param1LockFreeBitArray);
    
    <T> boolean mightContain(@ParametricNullness T param1T, Funnel<? super T> param1Funnel, int param1Int, BloomFilterStrategies.LockFreeBitArray param1LockFreeBitArray);
    
    int ordinal();
  }
}
