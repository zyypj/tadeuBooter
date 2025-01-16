package me.syncwrld.booter.libs.google.guava.cache;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.GuardedBy;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Equivalence;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Stopwatch;
import me.syncwrld.booter.libs.google.guava.base.Ticker;
import me.syncwrld.booter.libs.google.guava.collect.AbstractSequentialIterator;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableSet;
import me.syncwrld.booter.libs.google.guava.collect.Iterators;
import me.syncwrld.booter.libs.google.guava.collect.Maps;
import me.syncwrld.booter.libs.google.guava.collect.Sets;
import me.syncwrld.booter.libs.google.guava.primitives.Ints;
import me.syncwrld.booter.libs.google.guava.util.concurrent.ExecutionError;
import me.syncwrld.booter.libs.google.guava.util.concurrent.Futures;
import me.syncwrld.booter.libs.google.guava.util.concurrent.ListenableFuture;
import me.syncwrld.booter.libs.google.guava.util.concurrent.MoreExecutors;
import me.syncwrld.booter.libs.google.guava.util.concurrent.SettableFuture;
import me.syncwrld.booter.libs.google.guava.util.concurrent.UncheckedExecutionException;
import me.syncwrld.booter.libs.google.guava.util.concurrent.Uninterruptibles;
import me.syncwrld.booter.libs.google.j2objc.annotations.RetainedWith;
import me.syncwrld.booter.libs.google.j2objc.annotations.Weak;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@GwtCompatible(emulated = true)
class LocalCache<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
  static final int MAXIMUM_CAPACITY = 1073741824;
  
  static final int MAX_SEGMENTS = 65536;
  
  static final int CONTAINS_VALUE_RETRIES = 3;
  
  static final int DRAIN_THRESHOLD = 63;
  
  static final int DRAIN_MAX = 16;
  
  static final Logger logger = Logger.getLogger(LocalCache.class.getName());
  
  final int segmentMask;
  
  final int segmentShift;
  
  final Segment<K, V>[] segments;
  
  final int concurrencyLevel;
  
  final Equivalence<Object> keyEquivalence;
  
  final Equivalence<Object> valueEquivalence;
  
  final Strength keyStrength;
  
  final Strength valueStrength;
  
  final long maxWeight;
  
  final Weigher<K, V> weigher;
  
  final long expireAfterAccessNanos;
  
  final long expireAfterWriteNanos;
  
  final long refreshNanos;
  
  final Queue<RemovalNotification<K, V>> removalNotificationQueue;
  
  final RemovalListener<K, V> removalListener;
  
  final Ticker ticker;
  
  final EntryFactory entryFactory;
  
  final AbstractCache.StatsCounter globalStatsCounter;
  
  @CheckForNull
  final CacheLoader<? super K, V> defaultLoader;
  
  LocalCache(CacheBuilder<? super K, ? super V> builder, @CheckForNull CacheLoader<? super K, V> loader) {
    this.concurrencyLevel = Math.min(builder.getConcurrencyLevel(), 65536);
    this.keyStrength = builder.getKeyStrength();
    this.valueStrength = builder.getValueStrength();
    this.keyEquivalence = builder.getKeyEquivalence();
    this.valueEquivalence = builder.getValueEquivalence();
    this.maxWeight = builder.getMaximumWeight();
    this.weigher = builder.getWeigher();
    this.expireAfterAccessNanos = builder.getExpireAfterAccessNanos();
    this.expireAfterWriteNanos = builder.getExpireAfterWriteNanos();
    this.refreshNanos = builder.getRefreshNanos();
    this.removalListener = builder.getRemovalListener();
    this
      
      .removalNotificationQueue = (this.removalListener == CacheBuilder.NullListener.INSTANCE) ? discardingQueue() : new ConcurrentLinkedQueue<>();
    this.ticker = builder.getTicker(recordsTime());
    this.entryFactory = EntryFactory.getFactory(this.keyStrength, usesAccessEntries(), usesWriteEntries());
    this.globalStatsCounter = (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get();
    this.defaultLoader = loader;
    int initialCapacity = Math.min(builder.getInitialCapacity(), 1073741824);
    if (evictsBySize() && !customWeigher())
      initialCapacity = (int)Math.min(initialCapacity, this.maxWeight); 
    int segmentShift = 0;
    int segmentCount = 1;
    while (segmentCount < this.concurrencyLevel && (
      !evictsBySize() || segmentCount * 20L <= this.maxWeight)) {
      segmentShift++;
      segmentCount <<= 1;
    } 
    this.segmentShift = 32 - segmentShift;
    this.segmentMask = segmentCount - 1;
    this.segments = newSegmentArray(segmentCount);
    int segmentCapacity = initialCapacity / segmentCount;
    if (segmentCapacity * segmentCount < initialCapacity)
      segmentCapacity++; 
    int segmentSize = 1;
    while (segmentSize < segmentCapacity)
      segmentSize <<= 1; 
    if (evictsBySize()) {
      long maxSegmentWeight = this.maxWeight / segmentCount + 1L;
      long remainder = this.maxWeight % segmentCount;
      for (int i = 0; i < this.segments.length; i++) {
        if (i == remainder)
          maxSegmentWeight--; 
        this.segments[i] = 
          createSegment(segmentSize, maxSegmentWeight, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
      } 
    } else {
      for (int i = 0; i < this.segments.length; i++)
        this.segments[i] = 
          createSegment(segmentSize, -1L, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get()); 
    } 
  }
  
  boolean evictsBySize() {
    return (this.maxWeight >= 0L);
  }
  
  boolean customWeigher() {
    return (this.weigher != CacheBuilder.OneWeigher.INSTANCE);
  }
  
  boolean expires() {
    return (expiresAfterWrite() || expiresAfterAccess());
  }
  
  boolean expiresAfterWrite() {
    return (this.expireAfterWriteNanos > 0L);
  }
  
  boolean expiresAfterAccess() {
    return (this.expireAfterAccessNanos > 0L);
  }
  
  boolean refreshes() {
    return (this.refreshNanos > 0L);
  }
  
  boolean usesAccessQueue() {
    return (expiresAfterAccess() || evictsBySize());
  }
  
  boolean usesWriteQueue() {
    return expiresAfterWrite();
  }
  
  boolean recordsWrite() {
    return (expiresAfterWrite() || refreshes());
  }
  
  boolean recordsAccess() {
    return expiresAfterAccess();
  }
  
  boolean recordsTime() {
    return (recordsWrite() || recordsAccess());
  }
  
  boolean usesWriteEntries() {
    return (usesWriteQueue() || recordsWrite());
  }
  
  boolean usesAccessEntries() {
    return (usesAccessQueue() || recordsAccess());
  }
  
  boolean usesKeyReferences() {
    return (this.keyStrength != Strength.STRONG);
  }
  
  boolean usesValueReferences() {
    return (this.valueStrength != Strength.STRONG);
  }
  
  enum Strength {
    STRONG {
      <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> segment, ReferenceEntry<K, V> entry, V value, int weight) {
        return (weight == 1) ? 
          new LocalCache.StrongValueReference<>(value) : 
          new LocalCache.WeightedStrongValueReference<>(value, weight);
      }
      
      Equivalence<Object> defaultEquivalence() {
        return Equivalence.equals();
      }
    },
    SOFT {
      <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> segment, ReferenceEntry<K, V> entry, V value, int weight) {
        return (weight == 1) ? 
          new LocalCache.SoftValueReference<>(segment.valueReferenceQueue, value, entry) : 
          new LocalCache.WeightedSoftValueReference<>(segment.valueReferenceQueue, value, entry, weight);
      }
      
      Equivalence<Object> defaultEquivalence() {
        return Equivalence.identity();
      }
    },
    WEAK {
      <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> segment, ReferenceEntry<K, V> entry, V value, int weight) {
        return (weight == 1) ? 
          new LocalCache.WeakValueReference<>(segment.valueReferenceQueue, value, entry) : 
          new LocalCache.WeightedWeakValueReference<>(segment.valueReferenceQueue, value, entry, weight);
      }
      
      Equivalence<Object> defaultEquivalence() {
        return Equivalence.identity();
      }
    };
    
    abstract <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> param1Segment, ReferenceEntry<K, V> param1ReferenceEntry, V param1V, int param1Int);
    
    abstract Equivalence<Object> defaultEquivalence();
  }
  
  enum EntryFactory {
    STRONG {
      <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> segment, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
        return new LocalCache.StrongEntry<>(key, hash, next);
      }
    },
    STRONG_ACCESS {
      <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> segment, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
        return new LocalCache.StrongAccessEntry<>(key, hash, next);
      }
      
      <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> segment, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext, K key) {
        ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext, key);
        copyAccessEntry(original, newEntry);
        return newEntry;
      }
    },
    STRONG_WRITE {
      <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> segment, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
        return new LocalCache.StrongWriteEntry<>(key, hash, next);
      }
      
      <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> segment, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext, K key) {
        ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext, key);
        copyWriteEntry(original, newEntry);
        return newEntry;
      }
    },
    STRONG_ACCESS_WRITE {
      <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> segment, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
        return new LocalCache.StrongAccessWriteEntry<>(key, hash, next);
      }
      
      <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> segment, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext, K key) {
        ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext, key);
        copyAccessEntry(original, newEntry);
        copyWriteEntry(original, newEntry);
        return newEntry;
      }
    },
    WEAK {
      <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> segment, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
        return new LocalCache.WeakEntry<>(segment.keyReferenceQueue, key, hash, next);
      }
    },
    WEAK_ACCESS {
      <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> segment, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
        return new LocalCache.WeakAccessEntry<>(segment.keyReferenceQueue, key, hash, next);
      }
      
      <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> segment, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext, K key) {
        ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext, key);
        copyAccessEntry(original, newEntry);
        return newEntry;
      }
    },
    WEAK_WRITE {
      <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> segment, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
        return new LocalCache.WeakWriteEntry<>(segment.keyReferenceQueue, key, hash, next);
      }
      
      <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> segment, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext, K key) {
        ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext, key);
        copyWriteEntry(original, newEntry);
        return newEntry;
      }
    },
    WEAK_ACCESS_WRITE {
      <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> segment, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
        return new LocalCache.WeakAccessWriteEntry<>(segment.keyReferenceQueue, key, hash, next);
      }
      
      <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> segment, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext, K key) {
        ReferenceEntry<K, V> newEntry = super.copyEntry(segment, original, newNext, key);
        copyAccessEntry(original, newEntry);
        copyWriteEntry(original, newEntry);
        return newEntry;
      }
    };
    
    static final int ACCESS_MASK = 1;
    
    static final int WRITE_MASK = 2;
    
    static final int WEAK_MASK = 4;
    
    static final EntryFactory[] factories = new EntryFactory[] { STRONG, STRONG_ACCESS, STRONG_WRITE, STRONG_ACCESS_WRITE, WEAK, WEAK_ACCESS, WEAK_WRITE, WEAK_ACCESS_WRITE };
    
    static {
    
    }
    
    static EntryFactory getFactory(LocalCache.Strength keyStrength, boolean usesAccessQueue, boolean usesWriteQueue) {
      int flags = ((keyStrength == LocalCache.Strength.WEAK) ? 4 : 0) | (usesAccessQueue ? 1 : 0) | (usesWriteQueue ? 2 : 0);
      return factories[flags];
    }
    
    <K, V> ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> segment, ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext, K key) {
      return newEntry(segment, key, original.getHash(), newNext);
    }
    
    <K, V> void copyAccessEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newEntry) {
      newEntry.setAccessTime(original.getAccessTime());
      LocalCache.connectAccessOrder(original.getPreviousInAccessQueue(), newEntry);
      LocalCache.connectAccessOrder(newEntry, original.getNextInAccessQueue());
      LocalCache.nullifyAccessOrder(original);
    }
    
    <K, V> void copyWriteEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newEntry) {
      newEntry.setWriteTime(original.getWriteTime());
      LocalCache.connectWriteOrder(original.getPreviousInWriteQueue(), newEntry);
      LocalCache.connectWriteOrder(newEntry, original.getNextInWriteQueue());
      LocalCache.nullifyWriteOrder(original);
    }
    
    abstract <K, V> ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> param1Segment, K param1K, int param1Int, @CheckForNull ReferenceEntry<K, V> param1ReferenceEntry);
  }
  
  static final ValueReference<Object, Object> UNSET = new ValueReference<Object, Object>() {
      @CheckForNull
      public Object get() {
        return null;
      }
      
      public int getWeight() {
        return 0;
      }
      
      @CheckForNull
      public ReferenceEntry<Object, Object> getEntry() {
        return null;
      }
      
      public LocalCache.ValueReference<Object, Object> copyFor(ReferenceQueue<Object> queue, @CheckForNull Object value, ReferenceEntry<Object, Object> entry) {
        return this;
      }
      
      public boolean isLoading() {
        return false;
      }
      
      public boolean isActive() {
        return false;
      }
      
      @CheckForNull
      public Object waitForValue() {
        return null;
      }
      
      public void notifyNewValue(Object newValue) {}
    };
  
  static <K, V> ValueReference<K, V> unset() {
    return (ValueReference)UNSET;
  }
  
  private enum NullEntry implements ReferenceEntry<Object, Object> {
    INSTANCE;
    
    @CheckForNull
    public LocalCache.ValueReference<Object, Object> getValueReference() {
      return null;
    }
    
    public void setValueReference(LocalCache.ValueReference<Object, Object> valueReference) {}
    
    @CheckForNull
    public ReferenceEntry<Object, Object> getNext() {
      return null;
    }
    
    public int getHash() {
      return 0;
    }
    
    @CheckForNull
    public Object getKey() {
      return null;
    }
    
    public long getAccessTime() {
      return 0L;
    }
    
    public void setAccessTime(long time) {}
    
    public ReferenceEntry<Object, Object> getNextInAccessQueue() {
      return this;
    }
    
    public void setNextInAccessQueue(ReferenceEntry<Object, Object> next) {}
    
    public ReferenceEntry<Object, Object> getPreviousInAccessQueue() {
      return this;
    }
    
    public void setPreviousInAccessQueue(ReferenceEntry<Object, Object> previous) {}
    
    public long getWriteTime() {
      return 0L;
    }
    
    public void setWriteTime(long time) {}
    
    public ReferenceEntry<Object, Object> getNextInWriteQueue() {
      return this;
    }
    
    public void setNextInWriteQueue(ReferenceEntry<Object, Object> next) {}
    
    public ReferenceEntry<Object, Object> getPreviousInWriteQueue() {
      return this;
    }
    
    public void setPreviousInWriteQueue(ReferenceEntry<Object, Object> previous) {}
  }
  
  static abstract class AbstractReferenceEntry<K, V> implements ReferenceEntry<K, V> {
    public LocalCache.ValueReference<K, V> getValueReference() {
      throw new UnsupportedOperationException();
    }
    
    public void setValueReference(LocalCache.ValueReference<K, V> valueReference) {
      throw new UnsupportedOperationException();
    }
    
    public ReferenceEntry<K, V> getNext() {
      throw new UnsupportedOperationException();
    }
    
    public int getHash() {
      throw new UnsupportedOperationException();
    }
    
    public K getKey() {
      throw new UnsupportedOperationException();
    }
    
    public long getAccessTime() {
      throw new UnsupportedOperationException();
    }
    
    public void setAccessTime(long time) {
      throw new UnsupportedOperationException();
    }
    
    public ReferenceEntry<K, V> getNextInAccessQueue() {
      throw new UnsupportedOperationException();
    }
    
    public void setNextInAccessQueue(ReferenceEntry<K, V> next) {
      throw new UnsupportedOperationException();
    }
    
    public ReferenceEntry<K, V> getPreviousInAccessQueue() {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous) {
      throw new UnsupportedOperationException();
    }
    
    public long getWriteTime() {
      throw new UnsupportedOperationException();
    }
    
    public void setWriteTime(long time) {
      throw new UnsupportedOperationException();
    }
    
    public ReferenceEntry<K, V> getNextInWriteQueue() {
      throw new UnsupportedOperationException();
    }
    
    public void setNextInWriteQueue(ReferenceEntry<K, V> next) {
      throw new UnsupportedOperationException();
    }
    
    public ReferenceEntry<K, V> getPreviousInWriteQueue() {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous) {
      throw new UnsupportedOperationException();
    }
  }
  
  static <K, V> ReferenceEntry<K, V> nullEntry() {
    return NullEntry.INSTANCE;
  }
  
  static final Queue<?> DISCARDING_QUEUE = new AbstractQueue() {
      public boolean offer(Object o) {
        return true;
      }
      
      @CheckForNull
      public Object peek() {
        return null;
      }
      
      @CheckForNull
      public Object poll() {
        return null;
      }
      
      public int size() {
        return 0;
      }
      
      public Iterator<Object> iterator() {
        return (Iterator<Object>)ImmutableSet.of().iterator();
      }
    };
  
  @LazyInit
  @CheckForNull
  @RetainedWith
  Set<K> keySet;
  
  @LazyInit
  @CheckForNull
  @RetainedWith
  Collection<V> values;
  
  @LazyInit
  @CheckForNull
  @RetainedWith
  Set<Map.Entry<K, V>> entrySet;
  
  static <E> Queue<E> discardingQueue() {
    return (Queue)DISCARDING_QUEUE;
  }
  
  static class StrongEntry<K, V> extends AbstractReferenceEntry<K, V> {
    final K key;
    
    final int hash;
    
    @CheckForNull
    final ReferenceEntry<K, V> next;
    
    volatile LocalCache.ValueReference<K, V> valueReference;
    
    StrongEntry(K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
      this.valueReference = LocalCache.unset();
      this.key = key;
      this.hash = hash;
      this.next = next;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public LocalCache.ValueReference<K, V> getValueReference() {
      return this.valueReference;
    }
    
    public void setValueReference(LocalCache.ValueReference<K, V> valueReference) {
      this.valueReference = valueReference;
    }
    
    public int getHash() {
      return this.hash;
    }
    
    public ReferenceEntry<K, V> getNext() {
      return this.next;
    }
  }
  
  static final class StrongAccessEntry<K, V> extends StrongEntry<K, V> {
    volatile long accessTime;
    
    @Weak
    ReferenceEntry<K, V> nextAccess;
    
    @Weak
    ReferenceEntry<K, V> previousAccess;
    
    StrongAccessEntry(K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
      super(key, hash, next);
      this.accessTime = Long.MAX_VALUE;
      this.nextAccess = LocalCache.nullEntry();
      this.previousAccess = LocalCache.nullEntry();
    }
    
    public long getAccessTime() {
      return this.accessTime;
    }
    
    public void setAccessTime(long time) {
      this.accessTime = time;
    }
    
    public ReferenceEntry<K, V> getNextInAccessQueue() {
      return this.nextAccess;
    }
    
    public void setNextInAccessQueue(ReferenceEntry<K, V> next) {
      this.nextAccess = next;
    }
    
    public ReferenceEntry<K, V> getPreviousInAccessQueue() {
      return this.previousAccess;
    }
    
    public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous) {
      this.previousAccess = previous;
    }
  }
  
  static final class StrongWriteEntry<K, V> extends StrongEntry<K, V> {
    volatile long writeTime;
    
    @Weak
    ReferenceEntry<K, V> nextWrite;
    
    @Weak
    ReferenceEntry<K, V> previousWrite;
    
    StrongWriteEntry(K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
      super(key, hash, next);
      this.writeTime = Long.MAX_VALUE;
      this.nextWrite = LocalCache.nullEntry();
      this.previousWrite = LocalCache.nullEntry();
    }
    
    public long getWriteTime() {
      return this.writeTime;
    }
    
    public void setWriteTime(long time) {
      this.writeTime = time;
    }
    
    public ReferenceEntry<K, V> getNextInWriteQueue() {
      return this.nextWrite;
    }
    
    public void setNextInWriteQueue(ReferenceEntry<K, V> next) {
      this.nextWrite = next;
    }
    
    public ReferenceEntry<K, V> getPreviousInWriteQueue() {
      return this.previousWrite;
    }
    
    public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous) {
      this.previousWrite = previous;
    }
  }
  
  static final class StrongAccessWriteEntry<K, V> extends StrongEntry<K, V> {
    volatile long accessTime;
    
    @Weak
    ReferenceEntry<K, V> nextAccess;
    
    @Weak
    ReferenceEntry<K, V> previousAccess;
    
    volatile long writeTime;
    
    @Weak
    ReferenceEntry<K, V> nextWrite;
    
    @Weak
    ReferenceEntry<K, V> previousWrite;
    
    StrongAccessWriteEntry(K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
      super(key, hash, next);
      this.accessTime = Long.MAX_VALUE;
      this.nextAccess = LocalCache.nullEntry();
      this.previousAccess = LocalCache.nullEntry();
      this.writeTime = Long.MAX_VALUE;
      this.nextWrite = LocalCache.nullEntry();
      this.previousWrite = LocalCache.nullEntry();
    }
    
    public long getAccessTime() {
      return this.accessTime;
    }
    
    public void setAccessTime(long time) {
      this.accessTime = time;
    }
    
    public ReferenceEntry<K, V> getNextInAccessQueue() {
      return this.nextAccess;
    }
    
    public void setNextInAccessQueue(ReferenceEntry<K, V> next) {
      this.nextAccess = next;
    }
    
    public ReferenceEntry<K, V> getPreviousInAccessQueue() {
      return this.previousAccess;
    }
    
    public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous) {
      this.previousAccess = previous;
    }
    
    public long getWriteTime() {
      return this.writeTime;
    }
    
    public void setWriteTime(long time) {
      this.writeTime = time;
    }
    
    public ReferenceEntry<K, V> getNextInWriteQueue() {
      return this.nextWrite;
    }
    
    public void setNextInWriteQueue(ReferenceEntry<K, V> next) {
      this.nextWrite = next;
    }
    
    public ReferenceEntry<K, V> getPreviousInWriteQueue() {
      return this.previousWrite;
    }
    
    public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous) {
      this.previousWrite = previous;
    }
  }
  
  static class WeakEntry<K, V> extends WeakReference<K> implements ReferenceEntry<K, V> {
    final int hash;
    
    @CheckForNull
    final ReferenceEntry<K, V> next;
    
    volatile LocalCache.ValueReference<K, V> valueReference;
    
    WeakEntry(ReferenceQueue<K> queue, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
      super(key, queue);
      this.valueReference = LocalCache.unset();
      this.hash = hash;
      this.next = next;
    }
    
    public K getKey() {
      return get();
    }
    
    public long getAccessTime() {
      throw new UnsupportedOperationException();
    }
    
    public void setAccessTime(long time) {
      throw new UnsupportedOperationException();
    }
    
    public ReferenceEntry<K, V> getNextInAccessQueue() {
      throw new UnsupportedOperationException();
    }
    
    public void setNextInAccessQueue(ReferenceEntry<K, V> next) {
      throw new UnsupportedOperationException();
    }
    
    public ReferenceEntry<K, V> getPreviousInAccessQueue() {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous) {
      throw new UnsupportedOperationException();
    }
    
    public long getWriteTime() {
      throw new UnsupportedOperationException();
    }
    
    public void setWriteTime(long time) {
      throw new UnsupportedOperationException();
    }
    
    public ReferenceEntry<K, V> getNextInWriteQueue() {
      throw new UnsupportedOperationException();
    }
    
    public void setNextInWriteQueue(ReferenceEntry<K, V> next) {
      throw new UnsupportedOperationException();
    }
    
    public ReferenceEntry<K, V> getPreviousInWriteQueue() {
      throw new UnsupportedOperationException();
    }
    
    public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous) {
      throw new UnsupportedOperationException();
    }
    
    public LocalCache.ValueReference<K, V> getValueReference() {
      return this.valueReference;
    }
    
    public void setValueReference(LocalCache.ValueReference<K, V> valueReference) {
      this.valueReference = valueReference;
    }
    
    public int getHash() {
      return this.hash;
    }
    
    public ReferenceEntry<K, V> getNext() {
      return this.next;
    }
  }
  
  static final class WeakAccessEntry<K, V> extends WeakEntry<K, V> {
    volatile long accessTime;
    
    @Weak
    ReferenceEntry<K, V> nextAccess;
    
    @Weak
    ReferenceEntry<K, V> previousAccess;
    
    WeakAccessEntry(ReferenceQueue<K> queue, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
      super(queue, key, hash, next);
      this.accessTime = Long.MAX_VALUE;
      this.nextAccess = LocalCache.nullEntry();
      this.previousAccess = LocalCache.nullEntry();
    }
    
    public long getAccessTime() {
      return this.accessTime;
    }
    
    public void setAccessTime(long time) {
      this.accessTime = time;
    }
    
    public ReferenceEntry<K, V> getNextInAccessQueue() {
      return this.nextAccess;
    }
    
    public void setNextInAccessQueue(ReferenceEntry<K, V> next) {
      this.nextAccess = next;
    }
    
    public ReferenceEntry<K, V> getPreviousInAccessQueue() {
      return this.previousAccess;
    }
    
    public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous) {
      this.previousAccess = previous;
    }
  }
  
  static final class WeakWriteEntry<K, V> extends WeakEntry<K, V> {
    volatile long writeTime;
    
    @Weak
    ReferenceEntry<K, V> nextWrite;
    
    @Weak
    ReferenceEntry<K, V> previousWrite;
    
    WeakWriteEntry(ReferenceQueue<K> queue, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
      super(queue, key, hash, next);
      this.writeTime = Long.MAX_VALUE;
      this.nextWrite = LocalCache.nullEntry();
      this.previousWrite = LocalCache.nullEntry();
    }
    
    public long getWriteTime() {
      return this.writeTime;
    }
    
    public void setWriteTime(long time) {
      this.writeTime = time;
    }
    
    public ReferenceEntry<K, V> getNextInWriteQueue() {
      return this.nextWrite;
    }
    
    public void setNextInWriteQueue(ReferenceEntry<K, V> next) {
      this.nextWrite = next;
    }
    
    public ReferenceEntry<K, V> getPreviousInWriteQueue() {
      return this.previousWrite;
    }
    
    public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous) {
      this.previousWrite = previous;
    }
  }
  
  static final class WeakAccessWriteEntry<K, V> extends WeakEntry<K, V> {
    volatile long accessTime;
    
    @Weak
    ReferenceEntry<K, V> nextAccess;
    
    @Weak
    ReferenceEntry<K, V> previousAccess;
    
    volatile long writeTime;
    
    @Weak
    ReferenceEntry<K, V> nextWrite;
    
    @Weak
    ReferenceEntry<K, V> previousWrite;
    
    WeakAccessWriteEntry(ReferenceQueue<K> queue, K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
      super(queue, key, hash, next);
      this.accessTime = Long.MAX_VALUE;
      this.nextAccess = LocalCache.nullEntry();
      this.previousAccess = LocalCache.nullEntry();
      this.writeTime = Long.MAX_VALUE;
      this.nextWrite = LocalCache.nullEntry();
      this.previousWrite = LocalCache.nullEntry();
    }
    
    public long getAccessTime() {
      return this.accessTime;
    }
    
    public void setAccessTime(long time) {
      this.accessTime = time;
    }
    
    public ReferenceEntry<K, V> getNextInAccessQueue() {
      return this.nextAccess;
    }
    
    public void setNextInAccessQueue(ReferenceEntry<K, V> next) {
      this.nextAccess = next;
    }
    
    public ReferenceEntry<K, V> getPreviousInAccessQueue() {
      return this.previousAccess;
    }
    
    public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous) {
      this.previousAccess = previous;
    }
    
    public long getWriteTime() {
      return this.writeTime;
    }
    
    public void setWriteTime(long time) {
      this.writeTime = time;
    }
    
    public ReferenceEntry<K, V> getNextInWriteQueue() {
      return this.nextWrite;
    }
    
    public void setNextInWriteQueue(ReferenceEntry<K, V> next) {
      this.nextWrite = next;
    }
    
    public ReferenceEntry<K, V> getPreviousInWriteQueue() {
      return this.previousWrite;
    }
    
    public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous) {
      this.previousWrite = previous;
    }
  }
  
  static class WeakValueReference<K, V> extends WeakReference<V> implements ValueReference<K, V> {
    final ReferenceEntry<K, V> entry;
    
    WeakValueReference(ReferenceQueue<V> queue, V referent, ReferenceEntry<K, V> entry) {
      super(referent, queue);
      this.entry = entry;
    }
    
    public int getWeight() {
      return 1;
    }
    
    public ReferenceEntry<K, V> getEntry() {
      return this.entry;
    }
    
    public void notifyNewValue(V newValue) {}
    
    public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry) {
      return new WeakValueReference(queue, value, entry);
    }
    
    public boolean isLoading() {
      return false;
    }
    
    public boolean isActive() {
      return true;
    }
    
    public V waitForValue() {
      return get();
    }
  }
  
  static class SoftValueReference<K, V> extends SoftReference<V> implements ValueReference<K, V> {
    final ReferenceEntry<K, V> entry;
    
    SoftValueReference(ReferenceQueue<V> queue, V referent, ReferenceEntry<K, V> entry) {
      super(referent, queue);
      this.entry = entry;
    }
    
    public int getWeight() {
      return 1;
    }
    
    public ReferenceEntry<K, V> getEntry() {
      return this.entry;
    }
    
    public void notifyNewValue(V newValue) {}
    
    public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry) {
      return new SoftValueReference(queue, value, entry);
    }
    
    public boolean isLoading() {
      return false;
    }
    
    public boolean isActive() {
      return true;
    }
    
    public V waitForValue() {
      return get();
    }
  }
  
  static class StrongValueReference<K, V> implements ValueReference<K, V> {
    final V referent;
    
    StrongValueReference(V referent) {
      this.referent = referent;
    }
    
    public V get() {
      return this.referent;
    }
    
    public int getWeight() {
      return 1;
    }
    
    public ReferenceEntry<K, V> getEntry() {
      return null;
    }
    
    public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry) {
      return this;
    }
    
    public boolean isLoading() {
      return false;
    }
    
    public boolean isActive() {
      return true;
    }
    
    public V waitForValue() {
      return get();
    }
    
    public void notifyNewValue(V newValue) {}
  }
  
  static final class WeightedWeakValueReference<K, V> extends WeakValueReference<K, V> {
    final int weight;
    
    WeightedWeakValueReference(ReferenceQueue<V> queue, V referent, ReferenceEntry<K, V> entry, int weight) {
      super(queue, referent, entry);
      this.weight = weight;
    }
    
    public int getWeight() {
      return this.weight;
    }
    
    public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry) {
      return new WeightedWeakValueReference(queue, value, entry, this.weight);
    }
  }
  
  static final class WeightedSoftValueReference<K, V> extends SoftValueReference<K, V> {
    final int weight;
    
    WeightedSoftValueReference(ReferenceQueue<V> queue, V referent, ReferenceEntry<K, V> entry, int weight) {
      super(queue, referent, entry);
      this.weight = weight;
    }
    
    public int getWeight() {
      return this.weight;
    }
    
    public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, ReferenceEntry<K, V> entry) {
      return new WeightedSoftValueReference(queue, value, entry, this.weight);
    }
  }
  
  static final class WeightedStrongValueReference<K, V> extends StrongValueReference<K, V> {
    final int weight;
    
    WeightedStrongValueReference(V referent, int weight) {
      super(referent);
      this.weight = weight;
    }
    
    public int getWeight() {
      return this.weight;
    }
  }
  
  static int rehash(int h) {
    h += h << 15 ^ 0xFFFFCD7D;
    h ^= h >>> 10;
    h += h << 3;
    h ^= h >>> 6;
    h += (h << 2) + (h << 14);
    return h ^ h >>> 16;
  }
  
  @VisibleForTesting
  ReferenceEntry<K, V> newEntry(K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
    Segment<K, V> segment = segmentFor(hash);
    segment.lock();
    try {
      return segment.newEntry(key, hash, next);
    } finally {
      segment.unlock();
    } 
  }
  
  @VisibleForTesting
  ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext) {
    int hash = original.getHash();
    return segmentFor(hash).copyEntry(original, newNext);
  }
  
  @VisibleForTesting
  ValueReference<K, V> newValueReference(ReferenceEntry<K, V> entry, V value, int weight) {
    int hash = entry.getHash();
    return this.valueStrength.referenceValue(segmentFor(hash), entry, (V)Preconditions.checkNotNull(value), weight);
  }
  
  int hash(@CheckForNull Object key) {
    int h = this.keyEquivalence.hash(key);
    return rehash(h);
  }
  
  void reclaimValue(ValueReference<K, V> valueReference) {
    ReferenceEntry<K, V> entry = valueReference.getEntry();
    int hash = entry.getHash();
    segmentFor(hash).reclaimValue(entry.getKey(), hash, valueReference);
  }
  
  void reclaimKey(ReferenceEntry<K, V> entry) {
    int hash = entry.getHash();
    segmentFor(hash).reclaimKey(entry, hash);
  }
  
  @VisibleForTesting
  boolean isLive(ReferenceEntry<K, V> entry, long now) {
    return (segmentFor(entry.getHash()).getLiveValue(entry, now) != null);
  }
  
  Segment<K, V> segmentFor(int hash) {
    return this.segments[hash >>> this.segmentShift & this.segmentMask];
  }
  
  Segment<K, V> createSegment(int initialCapacity, long maxSegmentWeight, AbstractCache.StatsCounter statsCounter) {
    return new Segment<>(this, initialCapacity, maxSegmentWeight, statsCounter);
  }
  
  @CheckForNull
  V getLiveValue(ReferenceEntry<K, V> entry, long now) {
    if (entry.getKey() == null)
      return null; 
    V value = (V)entry.getValueReference().get();
    if (value == null)
      return null; 
    if (isExpired(entry, now))
      return null; 
    return value;
  }
  
  boolean isExpired(ReferenceEntry<K, V> entry, long now) {
    Preconditions.checkNotNull(entry);
    if (expiresAfterAccess() && now - entry.getAccessTime() >= this.expireAfterAccessNanos)
      return true; 
    if (expiresAfterWrite() && now - entry.getWriteTime() >= this.expireAfterWriteNanos)
      return true; 
    return false;
  }
  
  static <K, V> void connectAccessOrder(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next) {
    previous.setNextInAccessQueue(next);
    next.setPreviousInAccessQueue(previous);
  }
  
  static <K, V> void nullifyAccessOrder(ReferenceEntry<K, V> nulled) {
    ReferenceEntry<K, V> nullEntry = nullEntry();
    nulled.setNextInAccessQueue(nullEntry);
    nulled.setPreviousInAccessQueue(nullEntry);
  }
  
  static <K, V> void connectWriteOrder(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next) {
    previous.setNextInWriteQueue(next);
    next.setPreviousInWriteQueue(previous);
  }
  
  static <K, V> void nullifyWriteOrder(ReferenceEntry<K, V> nulled) {
    ReferenceEntry<K, V> nullEntry = nullEntry();
    nulled.setNextInWriteQueue(nullEntry);
    nulled.setPreviousInWriteQueue(nullEntry);
  }
  
  void processPendingNotifications() {
    RemovalNotification<K, V> notification;
    while ((notification = this.removalNotificationQueue.poll()) != null) {
      try {
        this.removalListener.onRemoval(notification);
      } catch (Throwable e) {
        logger.log(Level.WARNING, "Exception thrown by removal listener", e);
      } 
    } 
  }
  
  final Segment<K, V>[] newSegmentArray(int ssize) {
    return (Segment<K, V>[])new Segment[ssize];
  }
  
  static class Segment<K, V> extends ReentrantLock {
    @Weak
    final LocalCache<K, V> map;
    
    volatile int count;
    
    @GuardedBy("this")
    long totalWeight;
    
    int modCount;
    
    int threshold;
    
    @CheckForNull
    volatile AtomicReferenceArray<ReferenceEntry<K, V>> table;
    
    final long maxSegmentWeight;
    
    @CheckForNull
    final ReferenceQueue<K> keyReferenceQueue;
    
    @CheckForNull
    final ReferenceQueue<V> valueReferenceQueue;
    
    final Queue<ReferenceEntry<K, V>> recencyQueue;
    
    final AtomicInteger readCount = new AtomicInteger();
    
    @GuardedBy("this")
    final Queue<ReferenceEntry<K, V>> writeQueue;
    
    @GuardedBy("this")
    final Queue<ReferenceEntry<K, V>> accessQueue;
    
    final AbstractCache.StatsCounter statsCounter;
    
    Segment(LocalCache<K, V> map, int initialCapacity, long maxSegmentWeight, AbstractCache.StatsCounter statsCounter) {
      this.map = map;
      this.maxSegmentWeight = maxSegmentWeight;
      this.statsCounter = (AbstractCache.StatsCounter)Preconditions.checkNotNull(statsCounter);
      initTable(newEntryArray(initialCapacity));
      this.keyReferenceQueue = map.usesKeyReferences() ? new ReferenceQueue<>() : null;
      this.valueReferenceQueue = map.usesValueReferences() ? new ReferenceQueue<>() : null;
      this
        .recencyQueue = map.usesAccessQueue() ? new ConcurrentLinkedQueue<>() : LocalCache.<ReferenceEntry<K, V>>discardingQueue();
      this.writeQueue = map.usesWriteQueue() ? new LocalCache.WriteQueue<>() : LocalCache.<ReferenceEntry<K, V>>discardingQueue();
      this.accessQueue = map.usesAccessQueue() ? new LocalCache.AccessQueue<>() : LocalCache.<ReferenceEntry<K, V>>discardingQueue();
    }
    
    AtomicReferenceArray<ReferenceEntry<K, V>> newEntryArray(int size) {
      return new AtomicReferenceArray<>(size);
    }
    
    void initTable(AtomicReferenceArray<ReferenceEntry<K, V>> newTable) {
      this.threshold = newTable.length() * 3 / 4;
      if (!this.map.customWeigher() && this.threshold == this.maxSegmentWeight)
        this.threshold++; 
      this.table = newTable;
    }
    
    @GuardedBy("this")
    ReferenceEntry<K, V> newEntry(K key, int hash, @CheckForNull ReferenceEntry<K, V> next) {
      return this.map.entryFactory.newEntry(this, (K)Preconditions.checkNotNull(key), hash, next);
    }
    
    @CheckForNull
    @GuardedBy("this")
    ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext) {
      K key = original.getKey();
      if (key == null)
        return null; 
      LocalCache.ValueReference<K, V> valueReference = original.getValueReference();
      V value = valueReference.get();
      if (value == null && valueReference.isActive())
        return null; 
      ReferenceEntry<K, V> newEntry = this.map.entryFactory.copyEntry(this, original, newNext, key);
      newEntry.setValueReference(valueReference.copyFor(this.valueReferenceQueue, value, newEntry));
      return newEntry;
    }
    
    @GuardedBy("this")
    void setValue(ReferenceEntry<K, V> entry, K key, V value, long now) {
      LocalCache.ValueReference<K, V> previous = entry.getValueReference();
      int weight = this.map.weigher.weigh(key, value);
      Preconditions.checkState((weight >= 0), "Weights must be non-negative");
      LocalCache.ValueReference<K, V> valueReference = this.map.valueStrength.referenceValue(this, entry, value, weight);
      entry.setValueReference(valueReference);
      recordWrite(entry, weight, now);
      previous.notifyNewValue(value);
    }
    
    @CanIgnoreReturnValue
    V get(K key, int hash, CacheLoader<? super K, V> loader) throws ExecutionException {
      Preconditions.checkNotNull(key);
      Preconditions.checkNotNull(loader);
      try {
        if (this.count != 0) {
          ReferenceEntry<K, V> e = getEntry(key, hash);
          if (e != null) {
            long now = this.map.ticker.read();
            V value = getLiveValue(e, now);
            if (value != null) {
              recordRead(e, now);
              this.statsCounter.recordHits(1);
              return scheduleRefresh(e, key, hash, value, now, loader);
            } 
            LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
            if (valueReference.isLoading())
              return waitForLoadingValue(e, key, valueReference); 
          } 
        } 
        return lockedGetOrLoad(key, hash, loader);
      } catch (ExecutionException ee) {
        Throwable cause = ee.getCause();
        if (cause instanceof Error)
          throw new ExecutionError((Error)cause); 
        if (cause instanceof RuntimeException)
          throw new UncheckedExecutionException(cause); 
        throw ee;
      } finally {
        postReadCleanup();
      } 
    }
    
    @CheckForNull
    V get(Object key, int hash) {
      try {
        if (this.count != 0) {
          long now = this.map.ticker.read();
          ReferenceEntry<K, V> e = getLiveEntry(key, hash, now);
          if (e == null)
            return null; 
          V value = (V)e.getValueReference().get();
          if (value != null) {
            recordRead(e, now);
            return scheduleRefresh(e, e.getKey(), hash, value, now, this.map.defaultLoader);
          } 
          tryDrainReferenceQueues();
        } 
        return null;
      } finally {
        postReadCleanup();
      } 
    }
    
    V lockedGetOrLoad(K key, int hash, CacheLoader<? super K, V> loader) throws ExecutionException {
      ReferenceEntry<K, V> e;
      LocalCache.ValueReference<K, V> valueReference = null;
      LocalCache.LoadingValueReference<K, V> loadingValueReference = null;
      boolean createNewEntry = true;
      lock();
      try {
        long now = this.map.ticker.read();
        preWriteCleanup(now);
        int newCount = this.count - 1;
        AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
        int index = hash & table.length() - 1;
        ReferenceEntry<K, V> first = table.get(index);
        for (e = first; e != null; e = e.getNext()) {
          K entryKey = e.getKey();
          if (e.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            valueReference = e.getValueReference();
            if (valueReference.isLoading()) {
              createNewEntry = false;
              break;
            } 
            V value = valueReference.get();
            if (value == null) {
              enqueueNotification(entryKey, hash, value, valueReference
                  .getWeight(), RemovalCause.COLLECTED);
            } else if (this.map.isExpired(e, now)) {
              enqueueNotification(entryKey, hash, value, valueReference
                  .getWeight(), RemovalCause.EXPIRED);
            } else {
              recordLockedRead(e, now);
              this.statsCounter.recordHits(1);
              return value;
            } 
            this.writeQueue.remove(e);
            this.accessQueue.remove(e);
            this.count = newCount;
            break;
          } 
        } 
        if (createNewEntry) {
          loadingValueReference = new LocalCache.LoadingValueReference<>();
          if (e == null) {
            e = newEntry(key, hash, first);
            e.setValueReference(loadingValueReference);
            table.set(index, e);
          } else {
            e.setValueReference(loadingValueReference);
          } 
        } 
      } finally {
        unlock();
        postWriteCleanup();
      } 
      if (createNewEntry)
        try {
          return loadSync(key, hash, loadingValueReference, loader);
        } finally {
          this.statsCounter.recordMisses(1);
        }  
      return waitForLoadingValue(e, key, valueReference);
    }
    
    V waitForLoadingValue(ReferenceEntry<K, V> e, K key, LocalCache.ValueReference<K, V> valueReference) throws ExecutionException {
      if (!valueReference.isLoading())
        throw new AssertionError(); 
      if (valueReference instanceof LocalCache.LoadingValueReference)
        Preconditions.checkState(
            
            (((LocalCache.LoadingValueReference)valueReference).getLoadingThread() != Thread.currentThread()), "Recursive load of: %s", key); 
      try {
        V value = valueReference.waitForValue();
        if (value == null)
          throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + key + "."); 
        long now = this.map.ticker.read();
        recordRead(e, now);
        return value;
      } finally {
        this.statsCounter.recordMisses(1);
      } 
    }
    
    @CheckForNull
    V compute(K key, int hash, BiFunction<? super K, ? super V, ? extends V> function) {
      LocalCache.ValueReference<K, V> valueReference = null;
      LocalCache.ComputingValueReference<K, V> computingValueReference = null;
      boolean createNewEntry = true;
      lock();
      try {
        long now = this.map.ticker.read();
        preWriteCleanup(now);
        AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
        int index = hash & table.length() - 1;
        ReferenceEntry<K, V> first = table.get(index);
        ReferenceEntry<K, V> e;
        for (e = first; e != null; e = e.getNext()) {
          K entryKey = e.getKey();
          if (e.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            valueReference = e.getValueReference();
            if (this.map.isExpired(e, now))
              enqueueNotification(entryKey, hash, valueReference
                  
                  .get(), valueReference
                  .getWeight(), RemovalCause.EXPIRED); 
            this.writeQueue.remove(e);
            this.accessQueue.remove(e);
            createNewEntry = false;
            break;
          } 
        } 
        computingValueReference = new LocalCache.ComputingValueReference<>(valueReference);
        if (e == null) {
          createNewEntry = true;
          e = newEntry(key, hash, first);
          e.setValueReference(computingValueReference);
          table.set(index, e);
        } else {
          e.setValueReference(computingValueReference);
        } 
        V newValue = computingValueReference.compute(key, function);
        if (newValue != null) {
          if (valueReference != null && newValue == valueReference.get()) {
            computingValueReference.set(newValue);
            e.setValueReference(valueReference);
            recordWrite(e, 0, now);
            return newValue;
          } 
          try {
            return getAndRecordStats(key, hash, computingValueReference, 
                Futures.immediateFuture(newValue));
          } catch (ExecutionException exception) {
            throw new AssertionError("impossible; Futures.immediateFuture can't throw");
          } 
        } 
        if (createNewEntry || valueReference.isLoading()) {
          removeLoadingValue(key, hash, computingValueReference);
          return null;
        } 
        removeEntry(e, hash, RemovalCause.EXPLICIT);
        return null;
      } finally {
        unlock();
        postWriteCleanup();
      } 
    }
    
    V loadSync(K key, int hash, LocalCache.LoadingValueReference<K, V> loadingValueReference, CacheLoader<? super K, V> loader) throws ExecutionException {
      ListenableFuture<V> loadingFuture = loadingValueReference.loadFuture(key, loader);
      return getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
    }
    
    ListenableFuture<V> loadAsync(K key, int hash, LocalCache.LoadingValueReference<K, V> loadingValueReference, CacheLoader<? super K, V> loader) {
      ListenableFuture<V> loadingFuture = loadingValueReference.loadFuture(key, loader);
      loadingFuture.addListener(() -> {
            try {
              getAndRecordStats((K)key, hash, loadingValueReference, loadingFuture);
            } catch (Throwable t) {
              LocalCache.logger.log(Level.WARNING, "Exception thrown during refresh", t);
              loadingValueReference.setException(t);
            } 
          }MoreExecutors.directExecutor());
      return loadingFuture;
    }
    
    @CanIgnoreReturnValue
    V getAndRecordStats(K key, int hash, LocalCache.LoadingValueReference<K, V> loadingValueReference, ListenableFuture<V> newValue) throws ExecutionException {
      V value = null;
      try {
        value = (V)Uninterruptibles.getUninterruptibly((Future)newValue);
        if (value == null)
          throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + key + "."); 
        this.statsCounter.recordLoadSuccess(loadingValueReference.elapsedNanos());
        storeLoadedValue(key, hash, loadingValueReference, value);
        return value;
      } finally {
        if (value == null) {
          this.statsCounter.recordLoadException(loadingValueReference.elapsedNanos());
          removeLoadingValue(key, hash, loadingValueReference);
        } 
      } 
    }
    
    V scheduleRefresh(ReferenceEntry<K, V> entry, K key, int hash, V oldValue, long now, CacheLoader<? super K, V> loader) {
      if (this.map.refreshes() && now - entry
        .getWriteTime() > this.map.refreshNanos && 
        !entry.getValueReference().isLoading()) {
        V newValue = refresh(key, hash, loader, true);
        if (newValue != null)
          return newValue; 
      } 
      return oldValue;
    }
    
    @CheckForNull
    @CanIgnoreReturnValue
    V refresh(K key, int hash, CacheLoader<? super K, V> loader, boolean checkTime) {
      LocalCache.LoadingValueReference<K, V> loadingValueReference = insertLoadingValueReference(key, hash, checkTime);
      if (loadingValueReference == null)
        return null; 
      ListenableFuture<V> result = loadAsync(key, hash, loadingValueReference, loader);
      if (result.isDone())
        try {
          return (V)Uninterruptibles.getUninterruptibly((Future)result);
        } catch (Throwable throwable) {} 
      return null;
    }
    
    @CheckForNull
    LocalCache.LoadingValueReference<K, V> insertLoadingValueReference(K key, int hash, boolean checkTime) {
      ReferenceEntry<K, V> e = null;
      lock();
      try {
        long now = this.map.ticker.read();
        preWriteCleanup(now);
        AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
        int index = hash & table.length() - 1;
        ReferenceEntry<K, V> first = table.get(index);
        for (e = first; e != null; e = e.getNext()) {
          K entryKey = e.getKey();
          if (e.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
            if (valueReference.isLoading() || (checkTime && now - e
              .getWriteTime() < this.map.refreshNanos))
              return null; 
            this.modCount++;
            LocalCache.LoadingValueReference<K, V> loadingValueReference1 = new LocalCache.LoadingValueReference<>(valueReference);
            e.setValueReference(loadingValueReference1);
            return loadingValueReference1;
          } 
        } 
        this.modCount++;
        LocalCache.LoadingValueReference<K, V> loadingValueReference = new LocalCache.LoadingValueReference<>();
        e = newEntry(key, hash, first);
        e.setValueReference(loadingValueReference);
        table.set(index, e);
        return loadingValueReference;
      } finally {
        unlock();
        postWriteCleanup();
      } 
    }
    
    void tryDrainReferenceQueues() {
      if (tryLock())
        try {
          drainReferenceQueues();
        } finally {
          unlock();
        }  
    }
    
    @GuardedBy("this")
    void drainReferenceQueues() {
      if (this.map.usesKeyReferences())
        drainKeyReferenceQueue(); 
      if (this.map.usesValueReferences())
        drainValueReferenceQueue(); 
    }
    
    @GuardedBy("this")
    void drainKeyReferenceQueue() {
      int i = 0;
      Reference<? extends K> ref;
      while ((ref = this.keyReferenceQueue.poll()) != null) {
        ReferenceEntry<K, V> entry = (ReferenceEntry)ref;
        this.map.reclaimKey(entry);
        if (++i == 16)
          break; 
      } 
    }
    
    @GuardedBy("this")
    void drainValueReferenceQueue() {
      int i = 0;
      Reference<? extends V> ref;
      while ((ref = this.valueReferenceQueue.poll()) != null) {
        LocalCache.ValueReference<K, V> valueReference = (LocalCache.ValueReference)ref;
        this.map.reclaimValue(valueReference);
        if (++i == 16)
          break; 
      } 
    }
    
    void clearReferenceQueues() {
      if (this.map.usesKeyReferences())
        clearKeyReferenceQueue(); 
      if (this.map.usesValueReferences())
        clearValueReferenceQueue(); 
    }
    
    void clearKeyReferenceQueue() {
      while (this.keyReferenceQueue.poll() != null);
    }
    
    void clearValueReferenceQueue() {
      while (this.valueReferenceQueue.poll() != null);
    }
    
    void recordRead(ReferenceEntry<K, V> entry, long now) {
      if (this.map.recordsAccess())
        entry.setAccessTime(now); 
      this.recencyQueue.add(entry);
    }
    
    @GuardedBy("this")
    void recordLockedRead(ReferenceEntry<K, V> entry, long now) {
      if (this.map.recordsAccess())
        entry.setAccessTime(now); 
      this.accessQueue.add(entry);
    }
    
    @GuardedBy("this")
    void recordWrite(ReferenceEntry<K, V> entry, int weight, long now) {
      drainRecencyQueue();
      this.totalWeight += weight;
      if (this.map.recordsAccess())
        entry.setAccessTime(now); 
      if (this.map.recordsWrite())
        entry.setWriteTime(now); 
      this.accessQueue.add(entry);
      this.writeQueue.add(entry);
    }
    
    @GuardedBy("this")
    void drainRecencyQueue() {
      ReferenceEntry<K, V> e;
      while ((e = this.recencyQueue.poll()) != null) {
        if (this.accessQueue.contains(e))
          this.accessQueue.add(e); 
      } 
    }
    
    void tryExpireEntries(long now) {
      if (tryLock())
        try {
          expireEntries(now);
        } finally {
          unlock();
        }  
    }
    
    @GuardedBy("this")
    void expireEntries(long now) {
      drainRecencyQueue();
      ReferenceEntry<K, V> e;
      while ((e = this.writeQueue.peek()) != null && this.map.isExpired(e, now)) {
        if (!removeEntry(e, e.getHash(), RemovalCause.EXPIRED))
          throw new AssertionError(); 
      } 
      while ((e = this.accessQueue.peek()) != null && this.map.isExpired(e, now)) {
        if (!removeEntry(e, e.getHash(), RemovalCause.EXPIRED))
          throw new AssertionError(); 
      } 
    }
    
    @GuardedBy("this")
    void enqueueNotification(@CheckForNull K key, int hash, @CheckForNull V value, int weight, RemovalCause cause) {
      this.totalWeight -= weight;
      if (cause.wasEvicted())
        this.statsCounter.recordEviction(); 
      if (this.map.removalNotificationQueue != LocalCache.DISCARDING_QUEUE) {
        RemovalNotification<K, V> notification = RemovalNotification.create(key, value, cause);
        this.map.removalNotificationQueue.offer(notification);
      } 
    }
    
    @GuardedBy("this")
    void evictEntries(ReferenceEntry<K, V> newest) {
      if (!this.map.evictsBySize())
        return; 
      drainRecencyQueue();
      if (newest.getValueReference().getWeight() > this.maxSegmentWeight && 
        !removeEntry(newest, newest.getHash(), RemovalCause.SIZE))
        throw new AssertionError(); 
      while (this.totalWeight > this.maxSegmentWeight) {
        ReferenceEntry<K, V> e = getNextEvictable();
        if (!removeEntry(e, e.getHash(), RemovalCause.SIZE))
          throw new AssertionError(); 
      } 
    }
    
    @GuardedBy("this")
    ReferenceEntry<K, V> getNextEvictable() {
      for (ReferenceEntry<K, V> e : this.accessQueue) {
        int weight = e.getValueReference().getWeight();
        if (weight > 0)
          return e; 
      } 
      throw new AssertionError();
    }
    
    ReferenceEntry<K, V> getFirst(int hash) {
      AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
      return table.get(hash & table.length() - 1);
    }
    
    @CheckForNull
    ReferenceEntry<K, V> getEntry(Object key, int hash) {
      for (ReferenceEntry<K, V> e = getFirst(hash); e != null; e = e.getNext()) {
        if (e.getHash() == hash) {
          K entryKey = e.getKey();
          if (entryKey == null) {
            tryDrainReferenceQueues();
          } else if (this.map.keyEquivalence.equivalent(key, entryKey)) {
            return e;
          } 
        } 
      } 
      return null;
    }
    
    @CheckForNull
    ReferenceEntry<K, V> getLiveEntry(Object key, int hash, long now) {
      ReferenceEntry<K, V> e = getEntry(key, hash);
      if (e == null)
        return null; 
      if (this.map.isExpired(e, now)) {
        tryExpireEntries(now);
        return null;
      } 
      return e;
    }
    
    V getLiveValue(ReferenceEntry<K, V> entry, long now) {
      if (entry.getKey() == null) {
        tryDrainReferenceQueues();
        return null;
      } 
      V value = (V)entry.getValueReference().get();
      if (value == null) {
        tryDrainReferenceQueues();
        return null;
      } 
      if (this.map.isExpired(entry, now)) {
        tryExpireEntries(now);
        return null;
      } 
      return value;
    }
    
    boolean containsKey(Object key, int hash) {
      try {
        if (this.count != 0) {
          long now = this.map.ticker.read();
          ReferenceEntry<K, V> e = getLiveEntry(key, hash, now);
          if (e == null)
            return false; 
          return (e.getValueReference().get() != null);
        } 
        return false;
      } finally {
        postReadCleanup();
      } 
    }
    
    @VisibleForTesting
    boolean containsValue(Object value) {
      try {
        if (this.count != 0) {
          long now = this.map.ticker.read();
          AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
          int length = table.length();
          for (int i = 0; i < length; i++) {
            for (ReferenceEntry<K, V> e = table.get(i); e != null; e = e.getNext()) {
              V entryValue = getLiveValue(e, now);
              if (entryValue != null)
                if (this.map.valueEquivalence.equivalent(value, entryValue))
                  return true;  
            } 
          } 
        } 
        return false;
      } finally {
        postReadCleanup();
      } 
    }
    
    @CheckForNull
    @CanIgnoreReturnValue
    V put(K key, int hash, V value, boolean onlyIfAbsent) {
      lock();
      try {
        long now = this.map.ticker.read();
        preWriteCleanup(now);
        int newCount = this.count + 1;
        if (newCount > this.threshold) {
          expand();
          newCount = this.count + 1;
        } 
        AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
        int index = hash & table.length() - 1;
        ReferenceEntry<K, V> first = table.get(index);
        for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
          K entryKey = e.getKey();
          if (e.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
            V entryValue = valueReference.get();
            if (entryValue == null) {
              this.modCount++;
              if (valueReference.isActive()) {
                enqueueNotification(key, hash, entryValue, valueReference
                    .getWeight(), RemovalCause.COLLECTED);
                setValue(e, key, value, now);
                newCount = this.count;
              } else {
                setValue(e, key, value, now);
                newCount = this.count + 1;
              } 
              this.count = newCount;
              evictEntries(e);
              return null;
            } 
            if (onlyIfAbsent) {
              recordLockedRead(e, now);
              return entryValue;
            } 
            this.modCount++;
            enqueueNotification(key, hash, entryValue, valueReference
                .getWeight(), RemovalCause.REPLACED);
            setValue(e, key, value, now);
            evictEntries(e);
            return entryValue;
          } 
        } 
        this.modCount++;
        ReferenceEntry<K, V> newEntry = newEntry(key, hash, first);
        setValue(newEntry, key, value, now);
        table.set(index, newEntry);
        newCount = this.count + 1;
        this.count = newCount;
        evictEntries(newEntry);
        return null;
      } finally {
        unlock();
        postWriteCleanup();
      } 
    }
    
    @GuardedBy("this")
    void expand() {
      AtomicReferenceArray<ReferenceEntry<K, V>> oldTable = this.table;
      int oldCapacity = oldTable.length();
      if (oldCapacity >= 1073741824)
        return; 
      int newCount = this.count;
      AtomicReferenceArray<ReferenceEntry<K, V>> newTable = newEntryArray(oldCapacity << 1);
      this.threshold = newTable.length() * 3 / 4;
      int newMask = newTable.length() - 1;
      for (int oldIndex = 0; oldIndex < oldCapacity; oldIndex++) {
        ReferenceEntry<K, V> head = oldTable.get(oldIndex);
        if (head != null) {
          ReferenceEntry<K, V> next = head.getNext();
          int headIndex = head.getHash() & newMask;
          if (next == null) {
            newTable.set(headIndex, head);
          } else {
            ReferenceEntry<K, V> tail = head;
            int tailIndex = headIndex;
            ReferenceEntry<K, V> e;
            for (e = next; e != null; e = e.getNext()) {
              int newIndex = e.getHash() & newMask;
              if (newIndex != tailIndex) {
                tailIndex = newIndex;
                tail = e;
              } 
            } 
            newTable.set(tailIndex, tail);
            for (e = head; e != tail; e = e.getNext()) {
              int newIndex = e.getHash() & newMask;
              ReferenceEntry<K, V> newNext = newTable.get(newIndex);
              ReferenceEntry<K, V> newFirst = copyEntry(e, newNext);
              if (newFirst != null) {
                newTable.set(newIndex, newFirst);
              } else {
                removeCollectedEntry(e);
                newCount--;
              } 
            } 
          } 
        } 
      } 
      this.table = newTable;
      this.count = newCount;
    }
    
    boolean replace(K key, int hash, V oldValue, V newValue) {
      lock();
      try {
        long now = this.map.ticker.read();
        preWriteCleanup(now);
        AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
        int index = hash & table.length() - 1;
        ReferenceEntry<K, V> first = table.get(index);
        for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
          K entryKey = e.getKey();
          if (e.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
            V entryValue = valueReference.get();
            if (entryValue == null) {
              if (valueReference.isActive()) {
                int newCount = this.count - 1;
                this.modCount++;
                ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, entryKey, hash, entryValue, valueReference, RemovalCause.COLLECTED);
                newCount = this.count - 1;
                table.set(index, newFirst);
                this.count = newCount;
              } 
              return false;
            } 
            if (this.map.valueEquivalence.equivalent(oldValue, entryValue)) {
              this.modCount++;
              enqueueNotification(key, hash, entryValue, valueReference
                  .getWeight(), RemovalCause.REPLACED);
              setValue(e, key, newValue, now);
              evictEntries(e);
              return true;
            } 
            recordLockedRead(e, now);
            return false;
          } 
        } 
        return false;
      } finally {
        unlock();
        postWriteCleanup();
      } 
    }
    
    @CheckForNull
    V replace(K key, int hash, V newValue) {
      lock();
      try {
        long now = this.map.ticker.read();
        preWriteCleanup(now);
        AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
        int index = hash & table.length() - 1;
        ReferenceEntry<K, V> first = table.get(index);
        ReferenceEntry<K, V> e;
        for (e = first; e != null; e = e.getNext()) {
          K entryKey = e.getKey();
          if (e.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
            V entryValue = valueReference.get();
            if (entryValue == null) {
              if (valueReference.isActive()) {
                int newCount = this.count - 1;
                this.modCount++;
                ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, entryKey, hash, entryValue, valueReference, RemovalCause.COLLECTED);
                newCount = this.count - 1;
                table.set(index, newFirst);
                this.count = newCount;
              } 
              return null;
            } 
            this.modCount++;
            enqueueNotification(key, hash, entryValue, valueReference
                .getWeight(), RemovalCause.REPLACED);
            setValue(e, key, newValue, now);
            evictEntries(e);
            return entryValue;
          } 
        } 
        e = null;
        return (V)e;
      } finally {
        unlock();
        postWriteCleanup();
      } 
    }
    
    @CheckForNull
    V remove(Object key, int hash) {
      lock();
      try {
        long now = this.map.ticker.read();
        preWriteCleanup(now);
        int newCount = this.count - 1;
        AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
        int index = hash & table.length() - 1;
        ReferenceEntry<K, V> first = table.get(index);
        ReferenceEntry<K, V> e;
        for (e = first; e != null; e = e.getNext()) {
          K entryKey = e.getKey();
          if (e.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            RemovalCause cause;
            LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
            V entryValue = valueReference.get();
            if (entryValue != null) {
              cause = RemovalCause.EXPLICIT;
            } else if (valueReference.isActive()) {
              cause = RemovalCause.COLLECTED;
            } else {
              return null;
            } 
            this.modCount++;
            ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, entryKey, hash, entryValue, valueReference, cause);
            newCount = this.count - 1;
            table.set(index, newFirst);
            this.count = newCount;
            return entryValue;
          } 
        } 
        e = null;
        return (V)e;
      } finally {
        unlock();
        postWriteCleanup();
      } 
    }
    
    boolean remove(Object key, int hash, Object value) {
      lock();
      try {
        long now = this.map.ticker.read();
        preWriteCleanup(now);
        int newCount = this.count - 1;
        AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
        int index = hash & table.length() - 1;
        ReferenceEntry<K, V> first = table.get(index);
        for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
          K entryKey = e.getKey();
          if (e.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            RemovalCause cause;
            LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
            V entryValue = valueReference.get();
            if (this.map.valueEquivalence.equivalent(value, entryValue)) {
              cause = RemovalCause.EXPLICIT;
            } else if (entryValue == null && valueReference.isActive()) {
              cause = RemovalCause.COLLECTED;
            } else {
              return false;
            } 
            this.modCount++;
            ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, entryKey, hash, entryValue, valueReference, cause);
            newCount = this.count - 1;
            table.set(index, newFirst);
            this.count = newCount;
            return (cause == RemovalCause.EXPLICIT);
          } 
        } 
        return false;
      } finally {
        unlock();
        postWriteCleanup();
      } 
    }
    
    @CanIgnoreReturnValue
    boolean storeLoadedValue(K key, int hash, LocalCache.LoadingValueReference<K, V> oldValueReference, V newValue) {
      lock();
      try {
        long now = this.map.ticker.read();
        preWriteCleanup(now);
        int newCount = this.count + 1;
        if (newCount > this.threshold) {
          expand();
          newCount = this.count + 1;
        } 
        AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
        int index = hash & table.length() - 1;
        ReferenceEntry<K, V> first = table.get(index);
        for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
          K entryKey = e.getKey();
          if (e.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            LocalCache.ValueReference<K, V> valueReference = e.getValueReference();
            V entryValue = valueReference.get();
            if (oldValueReference == valueReference || (entryValue == null && valueReference != LocalCache.UNSET)) {
              this.modCount++;
              if (oldValueReference.isActive()) {
                RemovalCause cause = (entryValue == null) ? RemovalCause.COLLECTED : RemovalCause.REPLACED;
                enqueueNotification(key, hash, entryValue, oldValueReference.getWeight(), cause);
                newCount--;
              } 
              setValue(e, key, newValue, now);
              this.count = newCount;
              evictEntries(e);
              return true;
            } 
            enqueueNotification(key, hash, newValue, 0, RemovalCause.REPLACED);
            return false;
          } 
        } 
        this.modCount++;
        ReferenceEntry<K, V> newEntry = newEntry(key, hash, first);
        setValue(newEntry, key, newValue, now);
        table.set(index, newEntry);
        this.count = newCount;
        evictEntries(newEntry);
        return true;
      } finally {
        unlock();
        postWriteCleanup();
      } 
    }
    
    void clear() {
      if (this.count != 0) {
        lock();
        try {
          long now = this.map.ticker.read();
          preWriteCleanup(now);
          AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
          int i;
          for (i = 0; i < table.length(); i++) {
            for (ReferenceEntry<K, V> e = table.get(i); e != null; e = e.getNext()) {
              if (e.getValueReference().isActive()) {
                K key = e.getKey();
                V value = (V)e.getValueReference().get();
                RemovalCause cause = (key == null || value == null) ? RemovalCause.COLLECTED : RemovalCause.EXPLICIT;
                enqueueNotification(key, e
                    .getHash(), value, e.getValueReference().getWeight(), cause);
              } 
            } 
          } 
          for (i = 0; i < table.length(); i++)
            table.set(i, null); 
          clearReferenceQueues();
          this.writeQueue.clear();
          this.accessQueue.clear();
          this.readCount.set(0);
          this.modCount++;
          this.count = 0;
        } finally {
          unlock();
          postWriteCleanup();
        } 
      } 
    }
    
    @CheckForNull
    @GuardedBy("this")
    ReferenceEntry<K, V> removeValueFromChain(ReferenceEntry<K, V> first, ReferenceEntry<K, V> entry, @CheckForNull K key, int hash, V value, LocalCache.ValueReference<K, V> valueReference, RemovalCause cause) {
      enqueueNotification(key, hash, value, valueReference.getWeight(), cause);
      this.writeQueue.remove(entry);
      this.accessQueue.remove(entry);
      if (valueReference.isLoading()) {
        valueReference.notifyNewValue(null);
        return first;
      } 
      return removeEntryFromChain(first, entry);
    }
    
    @CheckForNull
    @GuardedBy("this")
    ReferenceEntry<K, V> removeEntryFromChain(ReferenceEntry<K, V> first, ReferenceEntry<K, V> entry) {
      int newCount = this.count;
      ReferenceEntry<K, V> newFirst = entry.getNext();
      for (ReferenceEntry<K, V> e = first; e != entry; e = e.getNext()) {
        ReferenceEntry<K, V> next = copyEntry(e, newFirst);
        if (next != null) {
          newFirst = next;
        } else {
          removeCollectedEntry(e);
          newCount--;
        } 
      } 
      this.count = newCount;
      return newFirst;
    }
    
    @GuardedBy("this")
    void removeCollectedEntry(ReferenceEntry<K, V> entry) {
      enqueueNotification(entry
          .getKey(), entry
          .getHash(), (V)entry
          .getValueReference().get(), entry
          .getValueReference().getWeight(), RemovalCause.COLLECTED);
      this.writeQueue.remove(entry);
      this.accessQueue.remove(entry);
    }
    
    @CanIgnoreReturnValue
    boolean reclaimKey(ReferenceEntry<K, V> entry, int hash) {
      lock();
      try {
        int newCount = this.count - 1;
        AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
        int index = hash & table.length() - 1;
        ReferenceEntry<K, V> first = table.get(index);
        for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
          if (e == entry) {
            this.modCount++;
            ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, e
                
                .getKey(), hash, (V)e
                
                .getValueReference().get(), e
                .getValueReference(), RemovalCause.COLLECTED);
            newCount = this.count - 1;
            table.set(index, newFirst);
            this.count = newCount;
            return true;
          } 
        } 
        return false;
      } finally {
        unlock();
        postWriteCleanup();
      } 
    }
    
    @CanIgnoreReturnValue
    boolean reclaimValue(K key, int hash, LocalCache.ValueReference<K, V> valueReference) {
      lock();
      try {
        int newCount = this.count - 1;
        AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
        int index = hash & table.length() - 1;
        ReferenceEntry<K, V> first = table.get(index);
        for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
          K entryKey = e.getKey();
          if (e.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            LocalCache.ValueReference<K, V> v = e.getValueReference();
            if (v == valueReference) {
              this.modCount++;
              ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference
                  
                  .get(), valueReference, RemovalCause.COLLECTED);
              newCount = this.count - 1;
              table.set(index, newFirst);
              this.count = newCount;
              return true;
            } 
            return false;
          } 
        } 
        return false;
      } finally {
        unlock();
        if (!isHeldByCurrentThread())
          postWriteCleanup(); 
      } 
    }
    
    @CanIgnoreReturnValue
    boolean removeLoadingValue(K key, int hash, LocalCache.LoadingValueReference<K, V> valueReference) {
      lock();
      try {
        AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
        int index = hash & table.length() - 1;
        ReferenceEntry<K, V> first = table.get(index);
        for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
          K entryKey = e.getKey();
          if (e.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            LocalCache.ValueReference<K, V> v = e.getValueReference();
            if (v == valueReference) {
              if (valueReference.isActive()) {
                e.setValueReference(valueReference.getOldValue());
              } else {
                ReferenceEntry<K, V> newFirst = removeEntryFromChain(first, e);
                table.set(index, newFirst);
              } 
              return true;
            } 
            return false;
          } 
        } 
        return false;
      } finally {
        unlock();
        postWriteCleanup();
      } 
    }
    
    @VisibleForTesting
    @GuardedBy("this")
    @CanIgnoreReturnValue
    boolean removeEntry(ReferenceEntry<K, V> entry, int hash, RemovalCause cause) {
      int newCount = this.count - 1;
      AtomicReferenceArray<ReferenceEntry<K, V>> table = this.table;
      int index = hash & table.length() - 1;
      ReferenceEntry<K, V> first = table.get(index);
      for (ReferenceEntry<K, V> e = first; e != null; e = e.getNext()) {
        if (e == entry) {
          this.modCount++;
          ReferenceEntry<K, V> newFirst = removeValueFromChain(first, e, e
              
              .getKey(), hash, (V)e
              
              .getValueReference().get(), e
              .getValueReference(), cause);
          newCount = this.count - 1;
          table.set(index, newFirst);
          this.count = newCount;
          return true;
        } 
      } 
      return false;
    }
    
    void postReadCleanup() {
      if ((this.readCount.incrementAndGet() & 0x3F) == 0)
        cleanUp(); 
    }
    
    @GuardedBy("this")
    void preWriteCleanup(long now) {
      runLockedCleanup(now);
    }
    
    void postWriteCleanup() {
      runUnlockedCleanup();
    }
    
    void cleanUp() {
      long now = this.map.ticker.read();
      runLockedCleanup(now);
      runUnlockedCleanup();
    }
    
    void runLockedCleanup(long now) {
      if (tryLock())
        try {
          drainReferenceQueues();
          expireEntries(now);
          this.readCount.set(0);
        } finally {
          unlock();
        }  
    }
    
    void runUnlockedCleanup() {
      if (!isHeldByCurrentThread())
        this.map.processPendingNotifications(); 
    }
  }
  
  static class LoadingValueReference<K, V> implements ValueReference<K, V> {
    volatile LocalCache.ValueReference<K, V> oldValue;
    
    final SettableFuture<V> futureValue = SettableFuture.create();
    
    final Stopwatch stopwatch = Stopwatch.createUnstarted();
    
    final Thread loadingThread;
    
    public LoadingValueReference() {
      this(null);
    }
    
    public LoadingValueReference(@CheckForNull LocalCache.ValueReference<K, V> oldValue) {
      this.oldValue = (oldValue == null) ? LocalCache.<K, V>unset() : oldValue;
      this.loadingThread = Thread.currentThread();
    }
    
    public boolean isLoading() {
      return true;
    }
    
    public boolean isActive() {
      return this.oldValue.isActive();
    }
    
    public int getWeight() {
      return this.oldValue.getWeight();
    }
    
    @CanIgnoreReturnValue
    public boolean set(@CheckForNull V newValue) {
      return this.futureValue.set(newValue);
    }
    
    @CanIgnoreReturnValue
    public boolean setException(Throwable t) {
      return this.futureValue.setException(t);
    }
    
    private ListenableFuture<V> fullyFailedFuture(Throwable t) {
      return Futures.immediateFailedFuture(t);
    }
    
    public void notifyNewValue(@CheckForNull V newValue) {
      if (newValue != null) {
        set(newValue);
      } else {
        this.oldValue = LocalCache.unset();
      } 
    }
    
    public ListenableFuture<V> loadFuture(K key, CacheLoader<? super K, V> loader) {
      try {
        this.stopwatch.start();
        V previousValue = this.oldValue.get();
        if (previousValue == null) {
          V v = loader.load(key);
          return set(v) ? (ListenableFuture<V>)this.futureValue : Futures.immediateFuture(v);
        } 
        ListenableFuture<V> newValue = loader.reload(key, previousValue);
        if (newValue == null)
          return Futures.immediateFuture(null); 
        return Futures.transform(newValue, newResult -> {
              set((V)newResult);
              return newResult;
            }MoreExecutors.directExecutor());
      } catch (Throwable t) {
        ListenableFuture<V> result = setException(t) ? (ListenableFuture<V>)this.futureValue : fullyFailedFuture(t);
        if (t instanceof InterruptedException)
          Thread.currentThread().interrupt(); 
        return result;
      } 
    }
    
    @CheckForNull
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> function) {
      V previousValue, newValue;
      this.stopwatch.start();
      try {
        previousValue = this.oldValue.waitForValue();
      } catch (ExecutionException e) {
        previousValue = null;
      } 
      try {
        newValue = function.apply(key, previousValue);
      } catch (Throwable th) {
        setException(th);
        throw th;
      } 
      set(newValue);
      return newValue;
    }
    
    public long elapsedNanos() {
      return this.stopwatch.elapsed(TimeUnit.NANOSECONDS);
    }
    
    public V waitForValue() throws ExecutionException {
      return (V)Uninterruptibles.getUninterruptibly((Future)this.futureValue);
    }
    
    public V get() {
      return this.oldValue.get();
    }
    
    public LocalCache.ValueReference<K, V> getOldValue() {
      return this.oldValue;
    }
    
    public ReferenceEntry<K, V> getEntry() {
      return null;
    }
    
    public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, @CheckForNull V value, ReferenceEntry<K, V> entry) {
      return this;
    }
    
    Thread getLoadingThread() {
      return this.loadingThread;
    }
  }
  
  static class ComputingValueReference<K, V> extends LoadingValueReference<K, V> {
    ComputingValueReference(LocalCache.ValueReference<K, V> oldValue) {
      super(oldValue);
    }
    
    public boolean isLoading() {
      return false;
    }
  }
  
  static final class WriteQueue<K, V> extends AbstractQueue<ReferenceEntry<K, V>> {
    final ReferenceEntry<K, V> head = new LocalCache.AbstractReferenceEntry<K, V>(this) {
        public long getWriteTime() {
          return Long.MAX_VALUE;
        }
        
        public void setWriteTime(long time) {}
        
        @Weak
        ReferenceEntry<K, V> nextWrite = this;
        
        public ReferenceEntry<K, V> getNextInWriteQueue() {
          return this.nextWrite;
        }
        
        public void setNextInWriteQueue(ReferenceEntry<K, V> next) {
          this.nextWrite = next;
        }
        
        @Weak
        ReferenceEntry<K, V> previousWrite = this;
        
        public ReferenceEntry<K, V> getPreviousInWriteQueue() {
          return this.previousWrite;
        }
        
        public void setPreviousInWriteQueue(ReferenceEntry<K, V> previous) {
          this.previousWrite = previous;
        }
      };
    
    public boolean offer(ReferenceEntry<K, V> entry) {
      LocalCache.connectWriteOrder(entry.getPreviousInWriteQueue(), entry.getNextInWriteQueue());
      LocalCache.connectWriteOrder(this.head.getPreviousInWriteQueue(), entry);
      LocalCache.connectWriteOrder(entry, this.head);
      return true;
    }
    
    @CheckForNull
    public ReferenceEntry<K, V> peek() {
      ReferenceEntry<K, V> next = this.head.getNextInWriteQueue();
      return (next == this.head) ? null : next;
    }
    
    @CheckForNull
    public ReferenceEntry<K, V> poll() {
      ReferenceEntry<K, V> next = this.head.getNextInWriteQueue();
      if (next == this.head)
        return null; 
      remove(next);
      return next;
    }
    
    @CanIgnoreReturnValue
    public boolean remove(Object o) {
      ReferenceEntry<K, V> e = (ReferenceEntry<K, V>)o;
      ReferenceEntry<K, V> previous = e.getPreviousInWriteQueue();
      ReferenceEntry<K, V> next = e.getNextInWriteQueue();
      LocalCache.connectWriteOrder(previous, next);
      LocalCache.nullifyWriteOrder(e);
      return (next != LocalCache.NullEntry.INSTANCE);
    }
    
    public boolean contains(Object o) {
      ReferenceEntry<K, V> e = (ReferenceEntry<K, V>)o;
      return (e.getNextInWriteQueue() != LocalCache.NullEntry.INSTANCE);
    }
    
    public boolean isEmpty() {
      return (this.head.getNextInWriteQueue() == this.head);
    }
    
    public int size() {
      int size = 0;
      ReferenceEntry<K, V> e = this.head.getNextInWriteQueue();
      for (; e != this.head; 
        e = e.getNextInWriteQueue())
        size++; 
      return size;
    }
    
    public void clear() {
      ReferenceEntry<K, V> e = this.head.getNextInWriteQueue();
      while (e != this.head) {
        ReferenceEntry<K, V> next = e.getNextInWriteQueue();
        LocalCache.nullifyWriteOrder(e);
        e = next;
      } 
      this.head.setNextInWriteQueue(this.head);
      this.head.setPreviousInWriteQueue(this.head);
    }
    
    public Iterator<ReferenceEntry<K, V>> iterator() {
      return (Iterator<ReferenceEntry<K, V>>)new AbstractSequentialIterator<ReferenceEntry<K, V>>(peek()) {
          @CheckForNull
          protected ReferenceEntry<K, V> computeNext(ReferenceEntry<K, V> previous) {
            ReferenceEntry<K, V> next = previous.getNextInWriteQueue();
            return (next == LocalCache.WriteQueue.this.head) ? null : next;
          }
        };
    }
  }
  
  static final class AccessQueue<K, V> extends AbstractQueue<ReferenceEntry<K, V>> {
    final ReferenceEntry<K, V> head = new LocalCache.AbstractReferenceEntry<K, V>(this) {
        public long getAccessTime() {
          return Long.MAX_VALUE;
        }
        
        public void setAccessTime(long time) {}
        
        @Weak
        ReferenceEntry<K, V> nextAccess = this;
        
        public ReferenceEntry<K, V> getNextInAccessQueue() {
          return this.nextAccess;
        }
        
        public void setNextInAccessQueue(ReferenceEntry<K, V> next) {
          this.nextAccess = next;
        }
        
        @Weak
        ReferenceEntry<K, V> previousAccess = this;
        
        public ReferenceEntry<K, V> getPreviousInAccessQueue() {
          return this.previousAccess;
        }
        
        public void setPreviousInAccessQueue(ReferenceEntry<K, V> previous) {
          this.previousAccess = previous;
        }
      };
    
    public boolean offer(ReferenceEntry<K, V> entry) {
      LocalCache.connectAccessOrder(entry.getPreviousInAccessQueue(), entry.getNextInAccessQueue());
      LocalCache.connectAccessOrder(this.head.getPreviousInAccessQueue(), entry);
      LocalCache.connectAccessOrder(entry, this.head);
      return true;
    }
    
    @CheckForNull
    public ReferenceEntry<K, V> peek() {
      ReferenceEntry<K, V> next = this.head.getNextInAccessQueue();
      return (next == this.head) ? null : next;
    }
    
    @CheckForNull
    public ReferenceEntry<K, V> poll() {
      ReferenceEntry<K, V> next = this.head.getNextInAccessQueue();
      if (next == this.head)
        return null; 
      remove(next);
      return next;
    }
    
    @CanIgnoreReturnValue
    public boolean remove(Object o) {
      ReferenceEntry<K, V> e = (ReferenceEntry<K, V>)o;
      ReferenceEntry<K, V> previous = e.getPreviousInAccessQueue();
      ReferenceEntry<K, V> next = e.getNextInAccessQueue();
      LocalCache.connectAccessOrder(previous, next);
      LocalCache.nullifyAccessOrder(e);
      return (next != LocalCache.NullEntry.INSTANCE);
    }
    
    public boolean contains(Object o) {
      ReferenceEntry<K, V> e = (ReferenceEntry<K, V>)o;
      return (e.getNextInAccessQueue() != LocalCache.NullEntry.INSTANCE);
    }
    
    public boolean isEmpty() {
      return (this.head.getNextInAccessQueue() == this.head);
    }
    
    public int size() {
      int size = 0;
      ReferenceEntry<K, V> e = this.head.getNextInAccessQueue();
      for (; e != this.head; 
        e = e.getNextInAccessQueue())
        size++; 
      return size;
    }
    
    public void clear() {
      ReferenceEntry<K, V> e = this.head.getNextInAccessQueue();
      while (e != this.head) {
        ReferenceEntry<K, V> next = e.getNextInAccessQueue();
        LocalCache.nullifyAccessOrder(e);
        e = next;
      } 
      this.head.setNextInAccessQueue(this.head);
      this.head.setPreviousInAccessQueue(this.head);
    }
    
    public Iterator<ReferenceEntry<K, V>> iterator() {
      return (Iterator<ReferenceEntry<K, V>>)new AbstractSequentialIterator<ReferenceEntry<K, V>>(peek()) {
          @CheckForNull
          protected ReferenceEntry<K, V> computeNext(ReferenceEntry<K, V> previous) {
            ReferenceEntry<K, V> next = previous.getNextInAccessQueue();
            return (next == LocalCache.AccessQueue.this.head) ? null : next;
          }
        };
    }
  }
  
  public void cleanUp() {
    for (Segment<?, ?> segment : this.segments)
      segment.cleanUp(); 
  }
  
  public boolean isEmpty() {
    long sum = 0L;
    Segment<K, V>[] segments = this.segments;
    for (Segment<K, V> segment : segments) {
      if (segment.count != 0)
        return false; 
      sum += segment.modCount;
    } 
    if (sum != 0L) {
      for (Segment<K, V> segment : segments) {
        if (segment.count != 0)
          return false; 
        sum -= segment.modCount;
      } 
      return (sum == 0L);
    } 
    return true;
  }
  
  long longSize() {
    Segment<K, V>[] segments = this.segments;
    long sum = 0L;
    for (Segment<K, V> segment : segments)
      sum += segment.count; 
    return sum;
  }
  
  public int size() {
    return Ints.saturatedCast(longSize());
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V get(@CheckForNull Object key) {
    if (key == null)
      return null; 
    int hash = hash(key);
    return segmentFor(hash).get(key, hash);
  }
  
  @CanIgnoreReturnValue
  V get(K key, CacheLoader<? super K, V> loader) throws ExecutionException {
    int hash = hash(Preconditions.checkNotNull(key));
    return segmentFor(hash).get(key, hash, loader);
  }
  
  @CheckForNull
  public V getIfPresent(Object key) {
    int hash = hash(Preconditions.checkNotNull(key));
    V value = segmentFor(hash).get(key, hash);
    if (value == null) {
      this.globalStatsCounter.recordMisses(1);
    } else {
      this.globalStatsCounter.recordHits(1);
    } 
    return value;
  }
  
  @CheckForNull
  public V getOrDefault(@CheckForNull Object key, @CheckForNull V defaultValue) {
    V result = get(key);
    return (result != null) ? result : defaultValue;
  }
  
  V getOrLoad(K key) throws ExecutionException {
    return get(key, this.defaultLoader);
  }
  
  ImmutableMap<K, V> getAllPresent(Iterable<?> keys) {
    int hits = 0;
    int misses = 0;
    ImmutableMap.Builder<K, V> result = ImmutableMap.builder();
    for (Object key : keys) {
      V value = get(key);
      if (value == null) {
        misses++;
        continue;
      } 
      K castKey = (K)key;
      result.put(castKey, value);
      hits++;
    } 
    this.globalStatsCounter.recordHits(hits);
    this.globalStatsCounter.recordMisses(misses);
    return result.buildKeepingLast();
  }
  
  ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
    int hits = 0;
    int misses = 0;
    Map<K, V> result = Maps.newLinkedHashMap();
    Set<K> keysToLoad = Sets.newLinkedHashSet();
    for (K key : keys) {
      V value = get(key);
      if (!result.containsKey(key)) {
        result.put(key, value);
        if (value == null) {
          misses++;
          keysToLoad.add(key);
          continue;
        } 
        hits++;
      } 
    } 
    try {
      if (!keysToLoad.isEmpty())
        try {
          Map<K, V> newEntries = loadAll(Collections.unmodifiableSet(keysToLoad), this.defaultLoader);
          for (K key : keysToLoad) {
            V value = newEntries.get(key);
            if (value == null)
              throw new CacheLoader.InvalidCacheLoadException("loadAll failed to return a value for " + key); 
            result.put(key, value);
          } 
        } catch (UnsupportedLoadingOperationException e) {
          for (K key : keysToLoad) {
            misses--;
            result.put(key, get(key, this.defaultLoader));
          } 
        }  
      return ImmutableMap.copyOf(result);
    } finally {
      this.globalStatsCounter.recordHits(hits);
      this.globalStatsCounter.recordMisses(misses);
    } 
  }
  
  @CheckForNull
  Map<K, V> loadAll(Set<? extends K> keys, CacheLoader<? super K, V> loader) throws ExecutionException {
    Map<K, V> result;
    Preconditions.checkNotNull(loader);
    Preconditions.checkNotNull(keys);
    Stopwatch stopwatch = Stopwatch.createStarted();
    boolean success = false;
    try {
      Map<K, V> map = (Map)loader.loadAll(keys);
      result = map;
      success = true;
    } catch (UnsupportedLoadingOperationException e) {
      success = true;
      throw e;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ExecutionException(e);
    } catch (RuntimeException e) {
      throw new UncheckedExecutionException(e);
    } catch (Exception e) {
      throw new ExecutionException(e);
    } catch (Error e) {
      throw new ExecutionError(e);
    } finally {
      if (!success)
        this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS)); 
    } 
    if (result == null) {
      this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
      throw new CacheLoader.InvalidCacheLoadException(loader + " returned null map from loadAll");
    } 
    stopwatch.stop();
    boolean nullsPresent = false;
    for (Map.Entry<K, V> entry : result.entrySet()) {
      K key = entry.getKey();
      V value = entry.getValue();
      if (key == null || value == null) {
        nullsPresent = true;
        continue;
      } 
      put(key, value);
    } 
    if (nullsPresent) {
      this.globalStatsCounter.recordLoadException(stopwatch.elapsed(TimeUnit.NANOSECONDS));
      throw new CacheLoader.InvalidCacheLoadException(loader + " returned null keys or values from loadAll");
    } 
    this.globalStatsCounter.recordLoadSuccess(stopwatch.elapsed(TimeUnit.NANOSECONDS));
    return result;
  }
  
  @CheckForNull
  ReferenceEntry<K, V> getEntry(@CheckForNull Object key) {
    if (key == null)
      return null; 
    int hash = hash(key);
    return segmentFor(hash).getEntry(key, hash);
  }
  
  void refresh(K key) {
    int hash = hash(Preconditions.checkNotNull(key));
    segmentFor(hash).refresh(key, hash, this.defaultLoader, false);
  }
  
  public boolean containsKey(@CheckForNull Object key) {
    if (key == null)
      return false; 
    int hash = hash(key);
    return segmentFor(hash).containsKey(key, hash);
  }
  
  public boolean containsValue(@CheckForNull Object value) {
    if (value == null)
      return false; 
    long now = this.ticker.read();
    Segment<K, V>[] segments = this.segments;
    long last = -1L;
    for (int i = 0; i < 3; i++) {
      long sum = 0L;
      for (Segment<K, V> segment : segments) {
        int unused = segment.count;
        AtomicReferenceArray<ReferenceEntry<K, V>> table = segment.table;
        for (int j = 0; j < table.length(); j++) {
          for (ReferenceEntry<K, V> e = table.get(j); e != null; e = e.getNext()) {
            V v = segment.getLiveValue(e, now);
            if (v != null && this.valueEquivalence.equivalent(value, v))
              return true; 
          } 
        } 
        sum += segment.modCount;
      } 
      if (sum == last)
        break; 
      last = sum;
    } 
    return false;
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V put(K key, V value) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(value);
    int hash = hash(key);
    return segmentFor(hash).put(key, hash, value, false);
  }
  
  @CheckForNull
  public V putIfAbsent(K key, V value) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(value);
    int hash = hash(key);
    return segmentFor(hash).put(key, hash, value, true);
  }
  
  @CheckForNull
  public V compute(K key, BiFunction<? super K, ? super V, ? extends V> function) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(function);
    int hash = hash(key);
    return segmentFor(hash).compute(key, hash, function);
  }
  
  public V computeIfAbsent(K key, Function<? super K, ? extends V> function) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(function);
    return compute(key, (k, oldValue) -> (oldValue == null) ? function.apply(key) : oldValue);
  }
  
  @CheckForNull
  public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> function) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(function);
    return compute(key, (k, oldValue) -> (oldValue == null) ? null : function.apply(k, oldValue));
  }
  
  @CheckForNull
  public V merge(K key, V newValue, BiFunction<? super V, ? super V, ? extends V> function) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(newValue);
    Preconditions.checkNotNull(function);
    return compute(key, (k, oldValue) -> (oldValue == null) ? newValue : function.apply(oldValue, newValue));
  }
  
  public void putAll(Map<? extends K, ? extends V> m) {
    for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
      put(e.getKey(), e.getValue()); 
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V remove(@CheckForNull Object key) {
    if (key == null)
      return null; 
    int hash = hash(key);
    return segmentFor(hash).remove(key, hash);
  }
  
  @CanIgnoreReturnValue
  public boolean remove(@CheckForNull Object key, @CheckForNull Object value) {
    if (key == null || value == null)
      return false; 
    int hash = hash(key);
    return segmentFor(hash).remove(key, hash, value);
  }
  
  @CanIgnoreReturnValue
  public boolean replace(K key, @CheckForNull V oldValue, V newValue) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(newValue);
    if (oldValue == null)
      return false; 
    int hash = hash(key);
    return segmentFor(hash).replace(key, hash, oldValue, newValue);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V replace(K key, V value) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(value);
    int hash = hash(key);
    return segmentFor(hash).replace(key, hash, value);
  }
  
  public void clear() {
    for (Segment<K, V> segment : this.segments)
      segment.clear(); 
  }
  
  void invalidateAll(Iterable<?> keys) {
    for (Object key : keys)
      remove(key); 
  }
  
  public Set<K> keySet() {
    Set<K> ks = this.keySet;
    return (ks != null) ? ks : (this.keySet = new KeySet());
  }
  
  public Collection<V> values() {
    Collection<V> vs = this.values;
    return (vs != null) ? vs : (this.values = new Values());
  }
  
  @GwtIncompatible
  public Set<Map.Entry<K, V>> entrySet() {
    Set<Map.Entry<K, V>> es = this.entrySet;
    return (es != null) ? es : (this.entrySet = new EntrySet());
  }
  
  abstract class HashIterator<T> implements Iterator<T> {
    int nextSegmentIndex;
    
    int nextTableIndex;
    
    @CheckForNull
    LocalCache.Segment<K, V> currentSegment;
    
    @CheckForNull
    AtomicReferenceArray<ReferenceEntry<K, V>> currentTable;
    
    @CheckForNull
    ReferenceEntry<K, V> nextEntry;
    
    @CheckForNull
    LocalCache<K, V>.WriteThroughEntry nextExternal;
    
    @CheckForNull
    LocalCache<K, V>.WriteThroughEntry lastReturned;
    
    HashIterator() {
      this.nextSegmentIndex = LocalCache.this.segments.length - 1;
      this.nextTableIndex = -1;
      advance();
    }
    
    public abstract T next();
    
    final void advance() {
      this.nextExternal = null;
      if (nextInChain())
        return; 
      if (nextInTable())
        return; 
      while (this.nextSegmentIndex >= 0) {
        this.currentSegment = LocalCache.this.segments[this.nextSegmentIndex--];
        if (this.currentSegment.count != 0) {
          this.currentTable = this.currentSegment.table;
          this.nextTableIndex = this.currentTable.length() - 1;
          if (nextInTable())
            return; 
        } 
      } 
    }
    
    boolean nextInChain() {
      if (this.nextEntry != null)
        for (this.nextEntry = this.nextEntry.getNext(); this.nextEntry != null; this.nextEntry = this.nextEntry.getNext()) {
          if (advanceTo(this.nextEntry))
            return true; 
        }  
      return false;
    }
    
    boolean nextInTable() {
      while (this.nextTableIndex >= 0) {
        if ((this.nextEntry = this.currentTable.get(this.nextTableIndex--)) != null && (
          advanceTo(this.nextEntry) || nextInChain()))
          return true; 
      } 
      return false;
    }
    
    boolean advanceTo(ReferenceEntry<K, V> entry) {
      try {
        long now = LocalCache.this.ticker.read();
        K key = entry.getKey();
        V value = LocalCache.this.getLiveValue(entry, now);
        if (value != null) {
          this.nextExternal = new LocalCache.WriteThroughEntry(key, value);
          return true;
        } 
        return false;
      } finally {
        this.currentSegment.postReadCleanup();
      } 
    }
    
    public boolean hasNext() {
      return (this.nextExternal != null);
    }
    
    LocalCache<K, V>.WriteThroughEntry nextEntry() {
      if (this.nextExternal == null)
        throw new NoSuchElementException(); 
      this.lastReturned = this.nextExternal;
      advance();
      return this.lastReturned;
    }
    
    public void remove() {
      Preconditions.checkState((this.lastReturned != null));
      LocalCache.this.remove(this.lastReturned.getKey());
      this.lastReturned = null;
    }
  }
  
  final class KeyIterator extends HashIterator<K> {
    KeyIterator(LocalCache this$0) {
      super(this$0);
    }
    
    public K next() {
      return nextEntry().getKey();
    }
  }
  
  final class ValueIterator extends HashIterator<V> {
    ValueIterator(LocalCache this$0) {
      super(this$0);
    }
    
    public V next() {
      return nextEntry().getValue();
    }
  }
  
  final class WriteThroughEntry implements Map.Entry<K, V> {
    final K key;
    
    V value;
    
    WriteThroughEntry(K key, V value) {
      this.key = key;
      this.value = value;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public V getValue() {
      return this.value;
    }
    
    public boolean equals(@CheckForNull Object object) {
      if (object instanceof Map.Entry) {
        Map.Entry<?, ?> that = (Map.Entry<?, ?>)object;
        return (this.key.equals(that.getKey()) && this.value.equals(that.getValue()));
      } 
      return false;
    }
    
    public int hashCode() {
      return this.key.hashCode() ^ this.value.hashCode();
    }
    
    public V setValue(V newValue) {
      V oldValue = LocalCache.this.put(this.key, newValue);
      this.value = newValue;
      return oldValue;
    }
    
    public String toString() {
      return (new StringBuilder()).append(getKey()).append("=").append(getValue()).toString();
    }
  }
  
  final class EntryIterator extends HashIterator<Map.Entry<K, V>> {
    EntryIterator(LocalCache this$0) {
      super(this$0);
    }
    
    public Map.Entry<K, V> next() {
      return nextEntry();
    }
  }
  
  abstract class AbstractCacheSet<T> extends AbstractSet<T> {
    public int size() {
      return LocalCache.this.size();
    }
    
    public boolean isEmpty() {
      return LocalCache.this.isEmpty();
    }
    
    public void clear() {
      LocalCache.this.clear();
    }
    
    public Object[] toArray() {
      return LocalCache.toArrayList(this).toArray();
    }
    
    public <E> E[] toArray(E[] a) {
      return (E[])LocalCache.toArrayList(this).toArray((Object[])a);
    }
  }
  
  private static <E> ArrayList<E> toArrayList(Collection<E> c) {
    ArrayList<E> result = new ArrayList<>(c.size());
    Iterators.addAll(result, c.iterator());
    return result;
  }
  
  boolean removeIf(BiPredicate<? super K, ? super V> filter) {
    Preconditions.checkNotNull(filter);
    boolean changed = false;
    label17: for (K key : keySet()) {
      while (true) {
        V value = get(key);
        if (value != null) {
          if (!filter.test(key, value))
            continue label17; 
          if (remove(key, value))
            changed = true; 
          continue;
        } 
        continue label17;
      } 
    } 
    return changed;
  }
  
  final class KeySet extends AbstractCacheSet<K> {
    public Iterator<K> iterator() {
      return new LocalCache.KeyIterator(LocalCache.this);
    }
    
    public boolean contains(Object o) {
      return LocalCache.this.containsKey(o);
    }
    
    public boolean remove(Object o) {
      return (LocalCache.this.remove(o) != null);
    }
  }
  
  final class Values extends AbstractCollection<V> {
    public int size() {
      return LocalCache.this.size();
    }
    
    public boolean isEmpty() {
      return LocalCache.this.isEmpty();
    }
    
    public void clear() {
      LocalCache.this.clear();
    }
    
    public Iterator<V> iterator() {
      return new LocalCache.ValueIterator(LocalCache.this);
    }
    
    public boolean removeIf(Predicate<? super V> filter) {
      Preconditions.checkNotNull(filter);
      return LocalCache.this.removeIf((k, v) -> filter.test(v));
    }
    
    public boolean contains(Object o) {
      return LocalCache.this.containsValue(o);
    }
    
    public Object[] toArray() {
      return LocalCache.toArrayList(this).toArray();
    }
    
    public <E> E[] toArray(E[] a) {
      return (E[])LocalCache.toArrayList(this).toArray((Object[])a);
    }
  }
  
  final class EntrySet extends AbstractCacheSet<Map.Entry<K, V>> {
    public Iterator<Map.Entry<K, V>> iterator() {
      return new LocalCache.EntryIterator(LocalCache.this);
    }
    
    public boolean removeIf(Predicate<? super Map.Entry<K, V>> filter) {
      Preconditions.checkNotNull(filter);
      return LocalCache.this.removeIf((k, v) -> filter.test(Maps.immutableEntry(k, v)));
    }
    
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object key = e.getKey();
      if (key == null)
        return false; 
      V v = (V)LocalCache.this.get(key);
      return (v != null && LocalCache.this.valueEquivalence.equivalent(e.getValue(), v));
    }
    
    public boolean remove(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object key = e.getKey();
      return (key != null && LocalCache.this.remove(key, e.getValue()));
    }
  }
  
  static class ManualSerializationProxy<K, V> extends ForwardingCache<K, V> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    final LocalCache.Strength keyStrength;
    
    final LocalCache.Strength valueStrength;
    
    final Equivalence<Object> keyEquivalence;
    
    final Equivalence<Object> valueEquivalence;
    
    final long expireAfterWriteNanos;
    
    final long expireAfterAccessNanos;
    
    final long maxWeight;
    
    final Weigher<K, V> weigher;
    
    final int concurrencyLevel;
    
    final RemovalListener<? super K, ? super V> removalListener;
    
    @CheckForNull
    final Ticker ticker;
    
    final CacheLoader<? super K, V> loader;
    
    @CheckForNull
    transient Cache<K, V> delegate;
    
    ManualSerializationProxy(LocalCache<K, V> cache) {
      this(cache.keyStrength, cache.valueStrength, cache.keyEquivalence, cache.valueEquivalence, cache.expireAfterWriteNanos, cache.expireAfterAccessNanos, cache.maxWeight, cache.weigher, cache.concurrencyLevel, cache.removalListener, cache.ticker, cache.defaultLoader);
    }
    
    private ManualSerializationProxy(LocalCache.Strength keyStrength, LocalCache.Strength valueStrength, Equivalence<Object> keyEquivalence, Equivalence<Object> valueEquivalence, long expireAfterWriteNanos, long expireAfterAccessNanos, long maxWeight, Weigher<K, V> weigher, int concurrencyLevel, RemovalListener<? super K, ? super V> removalListener, Ticker ticker, CacheLoader<? super K, V> loader) {
      this.keyStrength = keyStrength;
      this.valueStrength = valueStrength;
      this.keyEquivalence = keyEquivalence;
      this.valueEquivalence = valueEquivalence;
      this.expireAfterWriteNanos = expireAfterWriteNanos;
      this.expireAfterAccessNanos = expireAfterAccessNanos;
      this.maxWeight = maxWeight;
      this.weigher = weigher;
      this.concurrencyLevel = concurrencyLevel;
      this.removalListener = removalListener;
      this.ticker = (ticker == Ticker.systemTicker() || ticker == CacheBuilder.NULL_TICKER) ? null : ticker;
      this.loader = loader;
    }
    
    CacheBuilder<K, V> recreateCacheBuilder() {
      CacheBuilder<K, V> builder = CacheBuilder.newBuilder().setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).valueEquivalence(this.valueEquivalence).concurrencyLevel(this.concurrencyLevel).removalListener(this.removalListener);
      builder.strictParsing = false;
      if (this.expireAfterWriteNanos > 0L)
        builder.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS); 
      if (this.expireAfterAccessNanos > 0L)
        builder.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS); 
      if (this.weigher != CacheBuilder.OneWeigher.INSTANCE) {
        Object<K, V> unused = (Object<K, V>)builder.weigher(this.weigher);
        if (this.maxWeight != -1L)
          builder.maximumWeight(this.maxWeight); 
      } else if (this.maxWeight != -1L) {
        builder.maximumSize(this.maxWeight);
      } 
      if (this.ticker != null)
        builder.ticker(this.ticker); 
      return builder;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      CacheBuilder<K, V> builder = recreateCacheBuilder();
      this.delegate = builder.build();
    }
    
    private Object readResolve() {
      return this.delegate;
    }
    
    protected Cache<K, V> delegate() {
      return this.delegate;
    }
  }
  
  static final class LoadingSerializationProxy<K, V> extends ManualSerializationProxy<K, V> implements LoadingCache<K, V>, Serializable {
    private static final long serialVersionUID = 1L;
    
    @CheckForNull
    transient LoadingCache<K, V> autoDelegate;
    
    LoadingSerializationProxy(LocalCache<K, V> cache) {
      super(cache);
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      CacheBuilder<K, V> builder = recreateCacheBuilder();
      this.autoDelegate = builder.build(this.loader);
    }
    
    public V get(K key) throws ExecutionException {
      return this.autoDelegate.get(key);
    }
    
    public V getUnchecked(K key) {
      return this.autoDelegate.getUnchecked(key);
    }
    
    public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
      return this.autoDelegate.getAll(keys);
    }
    
    public V apply(K key) {
      return this.autoDelegate.apply(key);
    }
    
    public void refresh(K key) {
      this.autoDelegate.refresh(key);
    }
    
    private Object readResolve() {
      return this.autoDelegate;
    }
  }
  
  static class LocalManualCache<K, V> implements Cache<K, V>, Serializable {
    final LocalCache<K, V> localCache;
    
    private static final long serialVersionUID = 1L;
    
    LocalManualCache(CacheBuilder<? super K, ? super V> builder) {
      this(new LocalCache<>(builder, null));
    }
    
    private LocalManualCache(LocalCache<K, V> localCache) {
      this.localCache = localCache;
    }
    
    @CheckForNull
    public V getIfPresent(Object key) {
      return this.localCache.getIfPresent(key);
    }
    
    public V get(K key, final Callable<? extends V> valueLoader) throws ExecutionException {
      Preconditions.checkNotNull(valueLoader);
      return this.localCache.get(key, (CacheLoader)new CacheLoader<Object, V>(this) {
            public V load(Object key) throws Exception {
              return valueLoader.call();
            }
          });
    }
    
    public ImmutableMap<K, V> getAllPresent(Iterable<?> keys) {
      return this.localCache.getAllPresent(keys);
    }
    
    public void put(K key, V value) {
      this.localCache.put(key, value);
    }
    
    public void putAll(Map<? extends K, ? extends V> m) {
      this.localCache.putAll(m);
    }
    
    public void invalidate(Object key) {
      Preconditions.checkNotNull(key);
      this.localCache.remove(key);
    }
    
    public void invalidateAll(Iterable<?> keys) {
      this.localCache.invalidateAll(keys);
    }
    
    public void invalidateAll() {
      this.localCache.clear();
    }
    
    public long size() {
      return this.localCache.longSize();
    }
    
    public ConcurrentMap<K, V> asMap() {
      return this.localCache;
    }
    
    public CacheStats stats() {
      AbstractCache.SimpleStatsCounter aggregator = new AbstractCache.SimpleStatsCounter();
      aggregator.incrementBy(this.localCache.globalStatsCounter);
      for (LocalCache.Segment<K, V> segment : this.localCache.segments)
        aggregator.incrementBy(segment.statsCounter); 
      return aggregator.snapshot();
    }
    
    public void cleanUp() {
      this.localCache.cleanUp();
    }
    
    Object writeReplace() {
      return new LocalCache.ManualSerializationProxy<>(this.localCache);
    }
    
    private void readObject(ObjectInputStream in) throws InvalidObjectException {
      throw new InvalidObjectException("Use ManualSerializationProxy");
    }
  }
  
  static class LocalLoadingCache<K, V> extends LocalManualCache<K, V> implements LoadingCache<K, V> {
    private static final long serialVersionUID = 1L;
    
    LocalLoadingCache(CacheBuilder<? super K, ? super V> builder, CacheLoader<? super K, V> loader) {
      super(new LocalCache<>(builder, (CacheLoader<? super K, V>)Preconditions.checkNotNull(loader)));
    }
    
    public V get(K key) throws ExecutionException {
      return this.localCache.getOrLoad(key);
    }
    
    @CanIgnoreReturnValue
    public V getUnchecked(K key) {
      try {
        return get(key);
      } catch (ExecutionException e) {
        throw new UncheckedExecutionException(e.getCause());
      } 
    }
    
    public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
      return this.localCache.getAll(keys);
    }
    
    public void refresh(K key) {
      this.localCache.refresh(key);
    }
    
    public final V apply(K key) {
      return getUnchecked(key);
    }
    
    Object writeReplace() {
      return new LocalCache.LoadingSerializationProxy<>(this.localCache);
    }
    
    private void readObject(ObjectInputStream in) throws InvalidObjectException {
      throw new InvalidObjectException("Use LoadingSerializationProxy");
    }
  }
  
  static interface ValueReference<K, V> {
    @CheckForNull
    V get();
    
    V waitForValue() throws ExecutionException;
    
    int getWeight();
    
    @CheckForNull
    ReferenceEntry<K, V> getEntry();
    
    ValueReference<K, V> copyFor(ReferenceQueue<V> param1ReferenceQueue, @CheckForNull V param1V, ReferenceEntry<K, V> param1ReferenceEntry);
    
    void notifyNewValue(@CheckForNull V param1V);
    
    boolean isLoading();
    
    boolean isActive();
  }
}
