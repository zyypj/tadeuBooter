package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.MoreObjects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Supplier;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableList;
import me.syncwrld.booter.libs.google.guava.collect.Lists;
import me.syncwrld.booter.libs.google.guava.collect.MapMaker;
import me.syncwrld.booter.libs.google.guava.math.IntMath;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public abstract class Striped<L> {
  private static final int LARGE_LAZY_CUTOFF = 1024;
  
  private static final int ALL_SET = -1;
  
  private Striped() {}
  
  public Iterable<L> bulkGet(Iterable<? extends Object> keys) {
    List<Object> result = Lists.newArrayList(keys);
    if (result.isEmpty())
      return (Iterable<L>)ImmutableList.of(); 
    int[] stripes = new int[result.size()];
    for (int i = 0; i < result.size(); i++)
      stripes[i] = indexFor(result.get(i)); 
    Arrays.sort(stripes);
    int previousStripe = stripes[0];
    result.set(0, getAt(previousStripe));
    for (int j = 1; j < result.size(); j++) {
      int currentStripe = stripes[j];
      if (currentStripe == previousStripe) {
        result.set(j, result.get(j - 1));
      } else {
        result.set(j, getAt(currentStripe));
        previousStripe = currentStripe;
      } 
    } 
    List<Object> list1 = result;
    return Collections.unmodifiableList((List)list1);
  }
  
  static <L> Striped<L> custom(int stripes, Supplier<L> supplier) {
    return new CompactStriped<>(stripes, supplier);
  }
  
  public static Striped<Lock> lock(int stripes) {
    return custom(stripes, PaddedLock::new);
  }
  
  public static Striped<Lock> lazyWeakLock(int stripes) {
    return lazy(stripes, () -> new ReentrantLock(false));
  }
  
  private static <L> Striped<L> lazy(int stripes, Supplier<L> supplier) {
    return (stripes < 1024) ? 
      new SmallLazyStriped<>(stripes, supplier) : 
      new LargeLazyStriped<>(stripes, supplier);
  }
  
  public static Striped<Semaphore> semaphore(int stripes, int permits) {
    return custom(stripes, () -> new PaddedSemaphore(permits));
  }
  
  public static Striped<Semaphore> lazyWeakSemaphore(int stripes, int permits) {
    return lazy(stripes, () -> new Semaphore(permits, false));
  }
  
  public static Striped<ReadWriteLock> readWriteLock(int stripes) {
    return custom(stripes, ReentrantReadWriteLock::new);
  }
  
  public static Striped<ReadWriteLock> lazyWeakReadWriteLock(int stripes) {
    return lazy(stripes, WeakSafeReadWriteLock::new);
  }
  
  private static final class WeakSafeReadWriteLock implements ReadWriteLock {
    private final ReadWriteLock delegate = new ReentrantReadWriteLock();
    
    public Lock readLock() {
      return new Striped.WeakSafeLock(this.delegate.readLock(), this);
    }
    
    public Lock writeLock() {
      return new Striped.WeakSafeLock(this.delegate.writeLock(), this);
    }
  }
  
  private static final class WeakSafeLock extends ForwardingLock {
    private final Lock delegate;
    
    private final Striped.WeakSafeReadWriteLock strongReference;
    
    WeakSafeLock(Lock delegate, Striped.WeakSafeReadWriteLock strongReference) {
      this.delegate = delegate;
      this.strongReference = strongReference;
    }
    
    Lock delegate() {
      return this.delegate;
    }
    
    public Condition newCondition() {
      return new Striped.WeakSafeCondition(this.delegate.newCondition(), this.strongReference);
    }
  }
  
  private static final class WeakSafeCondition extends ForwardingCondition {
    private final Condition delegate;
    
    private final Striped.WeakSafeReadWriteLock strongReference;
    
    WeakSafeCondition(Condition delegate, Striped.WeakSafeReadWriteLock strongReference) {
      this.delegate = delegate;
      this.strongReference = strongReference;
    }
    
    Condition delegate() {
      return this.delegate;
    }
  }
  
  private static abstract class PowerOfTwoStriped<L> extends Striped<L> {
    final int mask;
    
    PowerOfTwoStriped(int stripes) {
      Preconditions.checkArgument((stripes > 0), "Stripes must be positive");
      this.mask = (stripes > 1073741824) ? -1 : (Striped.ceilToPowerOfTwo(stripes) - 1);
    }
    
    final int indexFor(Object key) {
      int hash = Striped.smear(key.hashCode());
      return hash & this.mask;
    }
    
    public final L get(Object key) {
      return getAt(indexFor(key));
    }
  }
  
  private static class CompactStriped<L> extends PowerOfTwoStriped<L> {
    private final Object[] array;
    
    private CompactStriped(int stripes, Supplier<L> supplier) {
      super(stripes);
      Preconditions.checkArgument((stripes <= 1073741824), "Stripes must be <= 2^30)");
      this.array = new Object[this.mask + 1];
      for (int i = 0; i < this.array.length; i++)
        this.array[i] = supplier.get(); 
    }
    
    public L getAt(int index) {
      return (L)this.array[index];
    }
    
    public int size() {
      return this.array.length;
    }
  }
  
  @VisibleForTesting
  static class SmallLazyStriped<L> extends PowerOfTwoStriped<L> {
    final AtomicReferenceArray<ArrayReference<? extends L>> locks;
    
    final Supplier<L> supplier;
    
    final int size;
    
    final ReferenceQueue<L> queue = new ReferenceQueue<>();
    
    SmallLazyStriped(int stripes, Supplier<L> supplier) {
      super(stripes);
      this.size = (this.mask == -1) ? Integer.MAX_VALUE : (this.mask + 1);
      this.locks = new AtomicReferenceArray<>(this.size);
      this.supplier = supplier;
    }
    
    public L getAt(int index) {
      if (this.size != Integer.MAX_VALUE)
        Preconditions.checkElementIndex(index, size()); 
      ArrayReference<? extends L> existingRef = this.locks.get(index);
      L existing = (existingRef == null) ? null : existingRef.get();
      if (existing != null)
        return existing; 
      L created = (L)this.supplier.get();
      ArrayReference<L> newRef = new ArrayReference<>(created, index, this.queue);
      while (!this.locks.compareAndSet(index, existingRef, newRef)) {
        existingRef = this.locks.get(index);
        existing = (existingRef == null) ? null : existingRef.get();
        if (existing != null)
          return existing; 
      } 
      drainQueue();
      return created;
    }
    
    private void drainQueue() {
      Reference<? extends L> ref;
      while ((ref = this.queue.poll()) != null) {
        ArrayReference<? extends L> arrayRef = (ArrayReference<? extends L>)ref;
        this.locks.compareAndSet(arrayRef.index, arrayRef, null);
      } 
    }
    
    public int size() {
      return this.size;
    }
    
    private static final class ArrayReference<L> extends WeakReference<L> {
      final int index;
      
      ArrayReference(L referent, int index, ReferenceQueue<L> queue) {
        super(referent, queue);
        this.index = index;
      }
    }
  }
  
  @VisibleForTesting
  static class LargeLazyStriped<L> extends PowerOfTwoStriped<L> {
    final ConcurrentMap<Integer, L> locks;
    
    final Supplier<L> supplier;
    
    final int size;
    
    LargeLazyStriped(int stripes, Supplier<L> supplier) {
      super(stripes);
      this.size = (this.mask == -1) ? Integer.MAX_VALUE : (this.mask + 1);
      this.supplier = supplier;
      this.locks = (new MapMaker()).weakValues().makeMap();
    }
    
    public L getAt(int index) {
      if (this.size != Integer.MAX_VALUE)
        Preconditions.checkElementIndex(index, size()); 
      L existing = this.locks.get(Integer.valueOf(index));
      if (existing != null)
        return existing; 
      L created = (L)this.supplier.get();
      existing = this.locks.putIfAbsent(Integer.valueOf(index), created);
      return (L)MoreObjects.firstNonNull(existing, created);
    }
    
    public int size() {
      return this.size;
    }
  }
  
  private static int ceilToPowerOfTwo(int x) {
    return 1 << IntMath.log2(x, RoundingMode.CEILING);
  }
  
  private static int smear(int hashCode) {
    hashCode ^= hashCode >>> 20 ^ hashCode >>> 12;
    return hashCode ^ hashCode >>> 7 ^ hashCode >>> 4;
  }
  
  public abstract L get(Object paramObject);
  
  public abstract L getAt(int paramInt);
  
  abstract int indexFor(Object paramObject);
  
  public abstract int size();
  
  private static class PaddedLock extends ReentrantLock {
    long unused1;
    
    long unused2;
    
    long unused3;
    
    PaddedLock() {
      super(false);
    }
  }
  
  private static class PaddedSemaphore extends Semaphore {
    long unused1;
    
    long unused2;
    
    long unused3;
    
    PaddedSemaphore(int permits) {
      super(permits, false);
    }
  }
}
