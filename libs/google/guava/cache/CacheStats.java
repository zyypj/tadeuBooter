package me.syncwrld.booter.libs.google.guava.cache;

import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.MoreObjects;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.math.LongMath;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class CacheStats {
  private final long hitCount;
  
  private final long missCount;
  
  private final long loadSuccessCount;
  
  private final long loadExceptionCount;
  
  private final long totalLoadTime;
  
  private final long evictionCount;
  
  public CacheStats(long hitCount, long missCount, long loadSuccessCount, long loadExceptionCount, long totalLoadTime, long evictionCount) {
    Preconditions.checkArgument((hitCount >= 0L));
    Preconditions.checkArgument((missCount >= 0L));
    Preconditions.checkArgument((loadSuccessCount >= 0L));
    Preconditions.checkArgument((loadExceptionCount >= 0L));
    Preconditions.checkArgument((totalLoadTime >= 0L));
    Preconditions.checkArgument((evictionCount >= 0L));
    this.hitCount = hitCount;
    this.missCount = missCount;
    this.loadSuccessCount = loadSuccessCount;
    this.loadExceptionCount = loadExceptionCount;
    this.totalLoadTime = totalLoadTime;
    this.evictionCount = evictionCount;
  }
  
  public long requestCount() {
    return LongMath.saturatedAdd(this.hitCount, this.missCount);
  }
  
  public long hitCount() {
    return this.hitCount;
  }
  
  public double hitRate() {
    long requestCount = requestCount();
    return (requestCount == 0L) ? 1.0D : (this.hitCount / requestCount);
  }
  
  public long missCount() {
    return this.missCount;
  }
  
  public double missRate() {
    long requestCount = requestCount();
    return (requestCount == 0L) ? 0.0D : (this.missCount / requestCount);
  }
  
  public long loadCount() {
    return LongMath.saturatedAdd(this.loadSuccessCount, this.loadExceptionCount);
  }
  
  public long loadSuccessCount() {
    return this.loadSuccessCount;
  }
  
  public long loadExceptionCount() {
    return this.loadExceptionCount;
  }
  
  public double loadExceptionRate() {
    long totalLoadCount = LongMath.saturatedAdd(this.loadSuccessCount, this.loadExceptionCount);
    return (totalLoadCount == 0L) ? 0.0D : (this.loadExceptionCount / totalLoadCount);
  }
  
  public long totalLoadTime() {
    return this.totalLoadTime;
  }
  
  public double averageLoadPenalty() {
    long totalLoadCount = LongMath.saturatedAdd(this.loadSuccessCount, this.loadExceptionCount);
    return (totalLoadCount == 0L) ? 0.0D : (this.totalLoadTime / totalLoadCount);
  }
  
  public long evictionCount() {
    return this.evictionCount;
  }
  
  public CacheStats minus(CacheStats other) {
    return new CacheStats(
        Math.max(0L, LongMath.saturatedSubtract(this.hitCount, other.hitCount)), 
        Math.max(0L, LongMath.saturatedSubtract(this.missCount, other.missCount)), 
        Math.max(0L, LongMath.saturatedSubtract(this.loadSuccessCount, other.loadSuccessCount)), 
        Math.max(0L, LongMath.saturatedSubtract(this.loadExceptionCount, other.loadExceptionCount)), 
        Math.max(0L, LongMath.saturatedSubtract(this.totalLoadTime, other.totalLoadTime)), 
        Math.max(0L, LongMath.saturatedSubtract(this.evictionCount, other.evictionCount)));
  }
  
  public CacheStats plus(CacheStats other) {
    return new CacheStats(
        LongMath.saturatedAdd(this.hitCount, other.hitCount), 
        LongMath.saturatedAdd(this.missCount, other.missCount), 
        LongMath.saturatedAdd(this.loadSuccessCount, other.loadSuccessCount), 
        LongMath.saturatedAdd(this.loadExceptionCount, other.loadExceptionCount), 
        LongMath.saturatedAdd(this.totalLoadTime, other.totalLoadTime), 
        LongMath.saturatedAdd(this.evictionCount, other.evictionCount));
  }
  
  public int hashCode() {
    return Objects.hashCode(new Object[] { Long.valueOf(this.hitCount), Long.valueOf(this.missCount), Long.valueOf(this.loadSuccessCount), Long.valueOf(this.loadExceptionCount), Long.valueOf(this.totalLoadTime), Long.valueOf(this.evictionCount) });
  }
  
  public boolean equals(@CheckForNull Object object) {
    if (object instanceof CacheStats) {
      CacheStats other = (CacheStats)object;
      return (this.hitCount == other.hitCount && this.missCount == other.missCount && this.loadSuccessCount == other.loadSuccessCount && this.loadExceptionCount == other.loadExceptionCount && this.totalLoadTime == other.totalLoadTime && this.evictionCount == other.evictionCount);
    } 
    return false;
  }
  
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("hitCount", this.hitCount)
      .add("missCount", this.missCount)
      .add("loadSuccessCount", this.loadSuccessCount)
      .add("loadExceptionCount", this.loadExceptionCount)
      .add("totalLoadTime", this.totalLoadTime)
      .add("evictionCount", this.evictionCount)
      .toString();
  }
}
