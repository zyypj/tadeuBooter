package me.syncwrld.booter.libs.google.guava.collect;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.GuardedBy;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Equivalence;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.primitives.Ints;
import me.syncwrld.booter.libs.google.j2objc.annotations.Weak;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@J2ktIncompatible
@GwtIncompatible
class MapMakerInternalMap<K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>, S extends MapMakerInternalMap.Segment<K, V, E, S>> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
  static final int MAXIMUM_CAPACITY = 1073741824;
  
  static final int MAX_SEGMENTS = 65536;
  
  static final int CONTAINS_VALUE_RETRIES = 3;
  
  static final int DRAIN_THRESHOLD = 63;
  
  static final int DRAIN_MAX = 16;
  
  final transient int segmentMask;
  
  final transient int segmentShift;
  
  final transient Segment<K, V, E, S>[] segments;
  
  final int concurrencyLevel;
  
  final Equivalence<Object> keyEquivalence;
  
  final transient InternalEntryHelper<K, V, E, S> entryHelper;
  
  private MapMakerInternalMap(MapMaker builder, InternalEntryHelper<K, V, E, S> entryHelper) {
    this.concurrencyLevel = Math.min(builder.getConcurrencyLevel(), 65536);
    this.keyEquivalence = builder.getKeyEquivalence();
    this.entryHelper = entryHelper;
    int initialCapacity = Math.min(builder.getInitialCapacity(), 1073741824);
    int segmentShift = 0;
    int segmentCount = 1;
    while (segmentCount < this.concurrencyLevel) {
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
    for (int i = 0; i < this.segments.length; i++)
      this.segments[i] = createSegment(segmentSize); 
  }
  
  static <K, V> MapMakerInternalMap<K, V, ? extends InternalEntry<K, V, ?>, ?> create(MapMaker builder) {
    if (builder.getKeyStrength() == Strength.STRONG && builder
      .getValueStrength() == Strength.STRONG)
      return new MapMakerInternalMap<>(builder, (InternalEntryHelper)StrongKeyStrongValueEntry.Helper.instance()); 
    if (builder.getKeyStrength() == Strength.STRONG && builder
      .getValueStrength() == Strength.WEAK)
      return new MapMakerInternalMap<>(builder, (InternalEntryHelper)StrongKeyWeakValueEntry.Helper.instance()); 
    if (builder.getKeyStrength() == Strength.WEAK && builder
      .getValueStrength() == Strength.STRONG)
      return new MapMakerInternalMap<>(builder, (InternalEntryHelper)WeakKeyStrongValueEntry.Helper.instance()); 
    if (builder.getKeyStrength() == Strength.WEAK && builder.getValueStrength() == Strength.WEAK)
      return new MapMakerInternalMap<>(builder, (InternalEntryHelper)WeakKeyWeakValueEntry.Helper.instance()); 
    throw new AssertionError();
  }
  
  static <K> MapMakerInternalMap<K, MapMaker.Dummy, ? extends InternalEntry<K, MapMaker.Dummy, ?>, ?> createWithDummyValues(MapMaker builder) {
    if (builder.getKeyStrength() == Strength.STRONG && builder
      .getValueStrength() == Strength.STRONG)
      return new MapMakerInternalMap<>(builder, (InternalEntryHelper)StrongKeyDummyValueEntry.Helper.instance()); 
    if (builder.getKeyStrength() == Strength.WEAK && builder
      .getValueStrength() == Strength.STRONG)
      return new MapMakerInternalMap<>(builder, (InternalEntryHelper)WeakKeyDummyValueEntry.Helper.instance()); 
    if (builder.getValueStrength() == Strength.WEAK)
      throw new IllegalArgumentException("Map cannot have both weak and dummy values"); 
    throw new AssertionError();
  }
  
  enum Strength {
    STRONG {
      Equivalence<Object> defaultEquivalence() {
        return Equivalence.equals();
      }
    },
    WEAK {
      Equivalence<Object> defaultEquivalence() {
        return Equivalence.identity();
      }
    };
    
    abstract Equivalence<Object> defaultEquivalence();
  }
  
  static abstract class AbstractStrongKeyEntry<K, V, E extends InternalEntry<K, V, E>> implements InternalEntry<K, V, E> {
    final K key;
    
    final int hash;
    
    AbstractStrongKeyEntry(K key, int hash) {
      this.key = key;
      this.hash = hash;
    }
    
    public final K getKey() {
      return this.key;
    }
    
    public final int getHash() {
      return this.hash;
    }
    
    @CheckForNull
    public E getNext() {
      return null;
    }
  }
  
  static <K, V, E extends InternalEntry<K, V, E>> WeakValueReference<K, V, E> unsetWeakValueReference() {
    return (WeakValueReference)UNSET_WEAK_VALUE_REFERENCE;
  }
  
  static class StrongKeyStrongValueEntry<K, V> extends AbstractStrongKeyEntry<K, V, StrongKeyStrongValueEntry<K, V>> implements StrongValueEntry<K, V, StrongKeyStrongValueEntry<K, V>> {
    @CheckForNull
    private volatile V value = null;
    
    private StrongKeyStrongValueEntry(K key, int hash) {
      super(key, hash);
    }
    
    @CheckForNull
    public final V getValue() {
      return this.value;
    }
    
    private static final class LinkedStrongKeyStrongValueEntry<K, V> extends StrongKeyStrongValueEntry<K, V> {
      private final MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> next;
      
      LinkedStrongKeyStrongValueEntry(K key, int hash, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> next) {
        super(key, hash);
        this.next = next;
      }
      
      public MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> getNext() {
        return this.next;
      }
    }
    
    static final class Helper<K, V> implements MapMakerInternalMap.InternalEntryHelper<K, V, StrongKeyStrongValueEntry<K, V>, MapMakerInternalMap.StrongKeyStrongValueSegment<K, V>> {
      private static final Helper<?, ?> INSTANCE = new Helper();
      
      static <K, V> Helper<K, V> instance() {
        return (Helper)INSTANCE;
      }
      
      public MapMakerInternalMap.Strength keyStrength() {
        return MapMakerInternalMap.Strength.STRONG;
      }
      
      public MapMakerInternalMap.Strength valueStrength() {
        return MapMakerInternalMap.Strength.STRONG;
      }
      
      public MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> newSegment(MapMakerInternalMap<K, V, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V>, MapMakerInternalMap.StrongKeyStrongValueSegment<K, V>> map, int initialCapacity) {
        return new MapMakerInternalMap.StrongKeyStrongValueSegment<>(map, initialCapacity);
      }
      
      public MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> copy(MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> segment, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> entry, @CheckForNull MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> newNext) {
        MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> newEntry = newEntry(segment, entry.key, entry.hash, newNext);
        newEntry.value = entry.value;
        return newEntry;
      }
      
      public void setValue(MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> segment, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> entry, V value) {
        entry.value = value;
      }
      
      public MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> newEntry(MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> segment, K key, int hash, @CheckForNull MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> next) {
        return (next == null) ? 
          new MapMakerInternalMap.StrongKeyStrongValueEntry<>(key, hash) : 
          new MapMakerInternalMap.StrongKeyStrongValueEntry.LinkedStrongKeyStrongValueEntry<>(key, hash, next);
      }
    }
  }
  
  static final class Helper<K, V> implements InternalEntryHelper<K, V, StrongKeyStrongValueEntry<K, V>, StrongKeyStrongValueSegment<K, V>> {
    private static final Helper<?, ?> INSTANCE = new Helper();
    
    static <K, V> Helper<K, V> instance() {
      return (Helper)INSTANCE;
    }
    
    public MapMakerInternalMap.Strength keyStrength() {
      return MapMakerInternalMap.Strength.STRONG;
    }
    
    public MapMakerInternalMap.Strength valueStrength() {
      return MapMakerInternalMap.Strength.STRONG;
    }
    
    public MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> newSegment(MapMakerInternalMap<K, V, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V>, MapMakerInternalMap.StrongKeyStrongValueSegment<K, V>> map, int initialCapacity) {
      return new MapMakerInternalMap.StrongKeyStrongValueSegment<>(map, initialCapacity);
    }
    
    public MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> copy(MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> segment, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> entry, @CheckForNull MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> newNext) {
      MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> newEntry = newEntry(segment, entry.key, entry.hash, newNext);
      newEntry.value = entry.value;
      return newEntry;
    }
    
    public void setValue(MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> segment, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> entry, V value) {
      entry.value = value;
    }
    
    public MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> newEntry(MapMakerInternalMap.StrongKeyStrongValueSegment<K, V> segment, K key, int hash, @CheckForNull MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> next) {
      return (next == null) ? new MapMakerInternalMap.StrongKeyStrongValueEntry<>(key, hash) : new MapMakerInternalMap.StrongKeyStrongValueEntry.LinkedStrongKeyStrongValueEntry<>(key, hash, next);
    }
  }
  
  static class StrongKeyWeakValueEntry<K, V> extends AbstractStrongKeyEntry<K, V, StrongKeyWeakValueEntry<K, V>> implements WeakValueEntry<K, V, StrongKeyWeakValueEntry<K, V>> {
    private volatile MapMakerInternalMap.WeakValueReference<K, V, StrongKeyWeakValueEntry<K, V>> valueReference = MapMakerInternalMap.unsetWeakValueReference();
    
    private StrongKeyWeakValueEntry(K key, int hash) {
      super(key, hash);
    }
    
    @CheckForNull
    public final V getValue() {
      return this.valueReference.get();
    }
    
    public final MapMakerInternalMap.WeakValueReference<K, V, StrongKeyWeakValueEntry<K, V>> getValueReference() {
      return this.valueReference;
    }
    
    private static final class LinkedStrongKeyWeakValueEntry<K, V> extends StrongKeyWeakValueEntry<K, V> {
      private final MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> next;
      
      LinkedStrongKeyWeakValueEntry(K key, int hash, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> next) {
        super(key, hash);
        this.next = next;
      }
      
      public MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> getNext() {
        return this.next;
      }
    }
    
    static final class Helper<K, V> implements MapMakerInternalMap.InternalEntryHelper<K, V, StrongKeyWeakValueEntry<K, V>, MapMakerInternalMap.StrongKeyWeakValueSegment<K, V>> {
      private static final Helper<?, ?> INSTANCE = new Helper();
      
      static <K, V> Helper<K, V> instance() {
        return (Helper)INSTANCE;
      }
      
      public MapMakerInternalMap.Strength keyStrength() {
        return MapMakerInternalMap.Strength.STRONG;
      }
      
      public MapMakerInternalMap.Strength valueStrength() {
        return MapMakerInternalMap.Strength.WEAK;
      }
      
      public MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> newSegment(MapMakerInternalMap<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>, MapMakerInternalMap.StrongKeyWeakValueSegment<K, V>> map, int initialCapacity) {
        return new MapMakerInternalMap.StrongKeyWeakValueSegment<>(map, initialCapacity);
      }
      
      @CheckForNull
      public MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> copy(MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> segment, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> entry, @CheckForNull MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> newNext) {
        if (MapMakerInternalMap.Segment.isCollected(entry))
          return null; 
        MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> newEntry = newEntry(segment, entry.key, entry.hash, newNext);
        newEntry.valueReference = entry.valueReference.copyFor(segment.queueForValues, newEntry);
        return newEntry;
      }
      
      public void setValue(MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> segment, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> entry, V value) {
        MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>> previous = entry.valueReference;
        entry.valueReference = new MapMakerInternalMap.WeakValueReferenceImpl<>(segment.queueForValues, value, entry);
        previous.clear();
      }
      
      public MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> newEntry(MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> segment, K key, int hash, @CheckForNull MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> next) {
        return (next == null) ? 
          new MapMakerInternalMap.StrongKeyWeakValueEntry<>(key, hash) : 
          new MapMakerInternalMap.StrongKeyWeakValueEntry.LinkedStrongKeyWeakValueEntry<>(key, hash, next);
      }
    }
  }
  
  static final class Helper<K, V> implements InternalEntryHelper<K, V, StrongKeyWeakValueEntry<K, V>, StrongKeyWeakValueSegment<K, V>> {
    private static final Helper<?, ?> INSTANCE = new Helper();
    
    static <K, V> Helper<K, V> instance() {
      return (Helper)INSTANCE;
    }
    
    public MapMakerInternalMap.Strength keyStrength() {
      return MapMakerInternalMap.Strength.STRONG;
    }
    
    public MapMakerInternalMap.Strength valueStrength() {
      return MapMakerInternalMap.Strength.WEAK;
    }
    
    public MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> newSegment(MapMakerInternalMap<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>, MapMakerInternalMap.StrongKeyWeakValueSegment<K, V>> map, int initialCapacity) {
      return new MapMakerInternalMap.StrongKeyWeakValueSegment<>(map, initialCapacity);
    }
    
    @CheckForNull
    public MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> copy(MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> segment, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> entry, @CheckForNull MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> newNext) {
      if (MapMakerInternalMap.Segment.isCollected(entry))
        return null; 
      MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> newEntry = newEntry(segment, entry.key, entry.hash, newNext);
      newEntry.valueReference = entry.valueReference.copyFor(segment.queueForValues, newEntry);
      return newEntry;
    }
    
    public void setValue(MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> segment, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> entry, V value) {
      MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>> previous = entry.valueReference;
      entry.valueReference = new MapMakerInternalMap.WeakValueReferenceImpl<>(segment.queueForValues, value, entry);
      previous.clear();
    }
    
    public MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> newEntry(MapMakerInternalMap.StrongKeyWeakValueSegment<K, V> segment, K key, int hash, @CheckForNull MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> next) {
      return (next == null) ? new MapMakerInternalMap.StrongKeyWeakValueEntry<>(key, hash) : new MapMakerInternalMap.StrongKeyWeakValueEntry.LinkedStrongKeyWeakValueEntry<>(key, hash, next);
    }
  }
  
  static class StrongKeyDummyValueEntry<K> extends AbstractStrongKeyEntry<K, MapMaker.Dummy, StrongKeyDummyValueEntry<K>> implements StrongValueEntry<K, MapMaker.Dummy, StrongKeyDummyValueEntry<K>> {
    private StrongKeyDummyValueEntry(K key, int hash) {
      super(key, hash);
    }
    
    public final MapMaker.Dummy getValue() {
      return MapMaker.Dummy.VALUE;
    }
    
    private static final class LinkedStrongKeyDummyValueEntry<K> extends StrongKeyDummyValueEntry<K> {
      private final MapMakerInternalMap.StrongKeyDummyValueEntry<K> next;
      
      LinkedStrongKeyDummyValueEntry(K key, int hash, MapMakerInternalMap.StrongKeyDummyValueEntry<K> next) {
        super(key, hash);
        this.next = next;
      }
      
      public MapMakerInternalMap.StrongKeyDummyValueEntry<K> getNext() {
        return this.next;
      }
    }
    
    static final class Helper<K> implements MapMakerInternalMap.InternalEntryHelper<K, MapMaker.Dummy, StrongKeyDummyValueEntry<K>, MapMakerInternalMap.StrongKeyDummyValueSegment<K>> {
      private static final Helper<?> INSTANCE = new Helper();
      
      static <K> Helper<K> instance() {
        return (Helper)INSTANCE;
      }
      
      public MapMakerInternalMap.Strength keyStrength() {
        return MapMakerInternalMap.Strength.STRONG;
      }
      
      public MapMakerInternalMap.Strength valueStrength() {
        return MapMakerInternalMap.Strength.STRONG;
      }
      
      public MapMakerInternalMap.StrongKeyDummyValueSegment<K> newSegment(MapMakerInternalMap<K, MapMaker.Dummy, MapMakerInternalMap.StrongKeyDummyValueEntry<K>, MapMakerInternalMap.StrongKeyDummyValueSegment<K>> map, int initialCapacity) {
        return new MapMakerInternalMap.StrongKeyDummyValueSegment<>(map, initialCapacity);
      }
      
      public MapMakerInternalMap.StrongKeyDummyValueEntry<K> copy(MapMakerInternalMap.StrongKeyDummyValueSegment<K> segment, MapMakerInternalMap.StrongKeyDummyValueEntry<K> entry, @CheckForNull MapMakerInternalMap.StrongKeyDummyValueEntry<K> newNext) {
        return newEntry(segment, entry.key, entry.hash, newNext);
      }
      
      public void setValue(MapMakerInternalMap.StrongKeyDummyValueSegment<K> segment, MapMakerInternalMap.StrongKeyDummyValueEntry<K> entry, MapMaker.Dummy value) {}
      
      public MapMakerInternalMap.StrongKeyDummyValueEntry<K> newEntry(MapMakerInternalMap.StrongKeyDummyValueSegment<K> segment, K key, int hash, @CheckForNull MapMakerInternalMap.StrongKeyDummyValueEntry<K> next) {
        return (next == null) ? 
          new MapMakerInternalMap.StrongKeyDummyValueEntry<>(key, hash) : 
          new MapMakerInternalMap.StrongKeyDummyValueEntry.LinkedStrongKeyDummyValueEntry<>(key, hash, next);
      }
    }
  }
  
  static final class Helper<K> implements InternalEntryHelper<K, MapMaker.Dummy, StrongKeyDummyValueEntry<K>, StrongKeyDummyValueSegment<K>> {
    private static final Helper<?> INSTANCE = new Helper();
    
    static <K> Helper<K> instance() {
      return (Helper)INSTANCE;
    }
    
    public MapMakerInternalMap.Strength keyStrength() {
      return MapMakerInternalMap.Strength.STRONG;
    }
    
    public MapMakerInternalMap.Strength valueStrength() {
      return MapMakerInternalMap.Strength.STRONG;
    }
    
    public MapMakerInternalMap.StrongKeyDummyValueSegment<K> newSegment(MapMakerInternalMap<K, MapMaker.Dummy, MapMakerInternalMap.StrongKeyDummyValueEntry<K>, MapMakerInternalMap.StrongKeyDummyValueSegment<K>> map, int initialCapacity) {
      return new MapMakerInternalMap.StrongKeyDummyValueSegment<>(map, initialCapacity);
    }
    
    public MapMakerInternalMap.StrongKeyDummyValueEntry<K> copy(MapMakerInternalMap.StrongKeyDummyValueSegment<K> segment, MapMakerInternalMap.StrongKeyDummyValueEntry<K> entry, @CheckForNull MapMakerInternalMap.StrongKeyDummyValueEntry<K> newNext) {
      return newEntry(segment, entry.key, entry.hash, newNext);
    }
    
    public void setValue(MapMakerInternalMap.StrongKeyDummyValueSegment<K> segment, MapMakerInternalMap.StrongKeyDummyValueEntry<K> entry, MapMaker.Dummy value) {}
    
    public MapMakerInternalMap.StrongKeyDummyValueEntry<K> newEntry(MapMakerInternalMap.StrongKeyDummyValueSegment<K> segment, K key, int hash, @CheckForNull MapMakerInternalMap.StrongKeyDummyValueEntry<K> next) {
      return (next == null) ? new MapMakerInternalMap.StrongKeyDummyValueEntry<>(key, hash) : new MapMakerInternalMap.StrongKeyDummyValueEntry.LinkedStrongKeyDummyValueEntry<>(key, hash, next);
    }
  }
  
  static abstract class AbstractWeakKeyEntry<K, V, E extends InternalEntry<K, V, E>> extends WeakReference<K> implements InternalEntry<K, V, E> {
    final int hash;
    
    AbstractWeakKeyEntry(ReferenceQueue<K> queue, K key, int hash) {
      super(key, queue);
      this.hash = hash;
    }
    
    public final K getKey() {
      return get();
    }
    
    public final int getHash() {
      return this.hash;
    }
    
    @CheckForNull
    public E getNext() {
      return null;
    }
  }
  
  static class WeakKeyDummyValueEntry<K> extends AbstractWeakKeyEntry<K, MapMaker.Dummy, WeakKeyDummyValueEntry<K>> implements StrongValueEntry<K, MapMaker.Dummy, WeakKeyDummyValueEntry<K>> {
    private WeakKeyDummyValueEntry(ReferenceQueue<K> queue, K key, int hash) {
      super(queue, key, hash);
    }
    
    public final MapMaker.Dummy getValue() {
      return MapMaker.Dummy.VALUE;
    }
    
    private static final class LinkedWeakKeyDummyValueEntry<K> extends WeakKeyDummyValueEntry<K> {
      private final MapMakerInternalMap.WeakKeyDummyValueEntry<K> next;
      
      private LinkedWeakKeyDummyValueEntry(ReferenceQueue<K> queue, K key, int hash, MapMakerInternalMap.WeakKeyDummyValueEntry<K> next) {
        super(queue, key, hash);
        this.next = next;
      }
      
      public MapMakerInternalMap.WeakKeyDummyValueEntry<K> getNext() {
        return this.next;
      }
    }
    
    static final class Helper<K> implements MapMakerInternalMap.InternalEntryHelper<K, MapMaker.Dummy, WeakKeyDummyValueEntry<K>, MapMakerInternalMap.WeakKeyDummyValueSegment<K>> {
      private static final Helper<?> INSTANCE = new Helper();
      
      static <K> Helper<K> instance() {
        return (Helper)INSTANCE;
      }
      
      public MapMakerInternalMap.Strength keyStrength() {
        return MapMakerInternalMap.Strength.WEAK;
      }
      
      public MapMakerInternalMap.Strength valueStrength() {
        return MapMakerInternalMap.Strength.STRONG;
      }
      
      public MapMakerInternalMap.WeakKeyDummyValueSegment<K> newSegment(MapMakerInternalMap<K, MapMaker.Dummy, MapMakerInternalMap.WeakKeyDummyValueEntry<K>, MapMakerInternalMap.WeakKeyDummyValueSegment<K>> map, int initialCapacity) {
        return new MapMakerInternalMap.WeakKeyDummyValueSegment<>(map, initialCapacity);
      }
      
      @CheckForNull
      public MapMakerInternalMap.WeakKeyDummyValueEntry<K> copy(MapMakerInternalMap.WeakKeyDummyValueSegment<K> segment, MapMakerInternalMap.WeakKeyDummyValueEntry<K> entry, @CheckForNull MapMakerInternalMap.WeakKeyDummyValueEntry<K> newNext) {
        K key = entry.getKey();
        if (key == null)
          return null; 
        return newEntry(segment, key, entry.hash, newNext);
      }
      
      public void setValue(MapMakerInternalMap.WeakKeyDummyValueSegment<K> segment, MapMakerInternalMap.WeakKeyDummyValueEntry<K> entry, MapMaker.Dummy value) {}
      
      public MapMakerInternalMap.WeakKeyDummyValueEntry<K> newEntry(MapMakerInternalMap.WeakKeyDummyValueSegment<K> segment, K key, int hash, @CheckForNull MapMakerInternalMap.WeakKeyDummyValueEntry<K> next) {
        return (next == null) ? 
          new MapMakerInternalMap.WeakKeyDummyValueEntry<>(segment.queueForKeys, key, hash) : 
          new MapMakerInternalMap.WeakKeyDummyValueEntry.LinkedWeakKeyDummyValueEntry<>(segment.queueForKeys, key, hash, next);
      }
    }
  }
  
  static final class Helper<K> implements InternalEntryHelper<K, MapMaker.Dummy, WeakKeyDummyValueEntry<K>, WeakKeyDummyValueSegment<K>> {
    private static final Helper<?> INSTANCE = new Helper();
    
    static <K> Helper<K> instance() {
      return (Helper)INSTANCE;
    }
    
    public MapMakerInternalMap.Strength keyStrength() {
      return MapMakerInternalMap.Strength.WEAK;
    }
    
    public MapMakerInternalMap.Strength valueStrength() {
      return MapMakerInternalMap.Strength.STRONG;
    }
    
    public MapMakerInternalMap.WeakKeyDummyValueSegment<K> newSegment(MapMakerInternalMap<K, MapMaker.Dummy, MapMakerInternalMap.WeakKeyDummyValueEntry<K>, MapMakerInternalMap.WeakKeyDummyValueSegment<K>> map, int initialCapacity) {
      return new MapMakerInternalMap.WeakKeyDummyValueSegment<>(map, initialCapacity);
    }
    
    @CheckForNull
    public MapMakerInternalMap.WeakKeyDummyValueEntry<K> copy(MapMakerInternalMap.WeakKeyDummyValueSegment<K> segment, MapMakerInternalMap.WeakKeyDummyValueEntry<K> entry, @CheckForNull MapMakerInternalMap.WeakKeyDummyValueEntry<K> newNext) {
      K key = entry.getKey();
      if (key == null)
        return null; 
      return newEntry(segment, key, entry.hash, newNext);
    }
    
    public void setValue(MapMakerInternalMap.WeakKeyDummyValueSegment<K> segment, MapMakerInternalMap.WeakKeyDummyValueEntry<K> entry, MapMaker.Dummy value) {}
    
    public MapMakerInternalMap.WeakKeyDummyValueEntry<K> newEntry(MapMakerInternalMap.WeakKeyDummyValueSegment<K> segment, K key, int hash, @CheckForNull MapMakerInternalMap.WeakKeyDummyValueEntry<K> next) {
      return (next == null) ? new MapMakerInternalMap.WeakKeyDummyValueEntry<>(segment.queueForKeys, key, hash) : new MapMakerInternalMap.WeakKeyDummyValueEntry.LinkedWeakKeyDummyValueEntry<>(segment.queueForKeys, key, hash, next, null);
    }
  }
  
  static class WeakKeyStrongValueEntry<K, V> extends AbstractWeakKeyEntry<K, V, WeakKeyStrongValueEntry<K, V>> implements StrongValueEntry<K, V, WeakKeyStrongValueEntry<K, V>> {
    @CheckForNull
    private volatile V value = null;
    
    private WeakKeyStrongValueEntry(ReferenceQueue<K> queue, K key, int hash) {
      super(queue, key, hash);
    }
    
    @CheckForNull
    public final V getValue() {
      return this.value;
    }
    
    private static final class LinkedWeakKeyStrongValueEntry<K, V> extends WeakKeyStrongValueEntry<K, V> {
      private final MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> next;
      
      private LinkedWeakKeyStrongValueEntry(ReferenceQueue<K> queue, K key, int hash, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> next) {
        super(queue, key, hash);
        this.next = next;
      }
      
      public MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> getNext() {
        return this.next;
      }
    }
    
    static final class Helper<K, V> implements MapMakerInternalMap.InternalEntryHelper<K, V, WeakKeyStrongValueEntry<K, V>, MapMakerInternalMap.WeakKeyStrongValueSegment<K, V>> {
      private static final Helper<?, ?> INSTANCE = new Helper();
      
      static <K, V> Helper<K, V> instance() {
        return (Helper)INSTANCE;
      }
      
      public MapMakerInternalMap.Strength keyStrength() {
        return MapMakerInternalMap.Strength.WEAK;
      }
      
      public MapMakerInternalMap.Strength valueStrength() {
        return MapMakerInternalMap.Strength.STRONG;
      }
      
      public MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> newSegment(MapMakerInternalMap<K, V, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V>, MapMakerInternalMap.WeakKeyStrongValueSegment<K, V>> map, int initialCapacity) {
        return new MapMakerInternalMap.WeakKeyStrongValueSegment<>(map, initialCapacity);
      }
      
      @CheckForNull
      public MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> copy(MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> segment, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> entry, @CheckForNull MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> newNext) {
        K key = entry.getKey();
        if (key == null)
          return null; 
        MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> newEntry = newEntry(segment, key, entry.hash, newNext);
        newEntry.value = entry.value;
        return newEntry;
      }
      
      public void setValue(MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> segment, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> entry, V value) {
        entry.value = value;
      }
      
      public MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> newEntry(MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> segment, K key, int hash, @CheckForNull MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> next) {
        return (next == null) ? 
          new MapMakerInternalMap.WeakKeyStrongValueEntry<>(segment.queueForKeys, key, hash) : 
          new MapMakerInternalMap.WeakKeyStrongValueEntry.LinkedWeakKeyStrongValueEntry<>(segment.queueForKeys, key, hash, next);
      }
    }
  }
  
  static final class Helper<K, V> implements InternalEntryHelper<K, V, WeakKeyStrongValueEntry<K, V>, WeakKeyStrongValueSegment<K, V>> {
    private static final Helper<?, ?> INSTANCE = new Helper();
    
    static <K, V> Helper<K, V> instance() {
      return (Helper)INSTANCE;
    }
    
    public MapMakerInternalMap.Strength keyStrength() {
      return MapMakerInternalMap.Strength.WEAK;
    }
    
    public MapMakerInternalMap.Strength valueStrength() {
      return MapMakerInternalMap.Strength.STRONG;
    }
    
    public MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> newSegment(MapMakerInternalMap<K, V, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V>, MapMakerInternalMap.WeakKeyStrongValueSegment<K, V>> map, int initialCapacity) {
      return new MapMakerInternalMap.WeakKeyStrongValueSegment<>(map, initialCapacity);
    }
    
    @CheckForNull
    public MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> copy(MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> segment, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> entry, @CheckForNull MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> newNext) {
      K key = entry.getKey();
      if (key == null)
        return null; 
      MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> newEntry = newEntry(segment, key, entry.hash, newNext);
      newEntry.value = entry.value;
      return newEntry;
    }
    
    public void setValue(MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> segment, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> entry, V value) {
      entry.value = value;
    }
    
    public MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> newEntry(MapMakerInternalMap.WeakKeyStrongValueSegment<K, V> segment, K key, int hash, @CheckForNull MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> next) {
      return (next == null) ? new MapMakerInternalMap.WeakKeyStrongValueEntry<>(segment.queueForKeys, key, hash) : new MapMakerInternalMap.WeakKeyStrongValueEntry.LinkedWeakKeyStrongValueEntry<>(segment.queueForKeys, key, hash, next, null);
    }
  }
  
  static class WeakKeyWeakValueEntry<K, V> extends AbstractWeakKeyEntry<K, V, WeakKeyWeakValueEntry<K, V>> implements WeakValueEntry<K, V, WeakKeyWeakValueEntry<K, V>> {
    private volatile MapMakerInternalMap.WeakValueReference<K, V, WeakKeyWeakValueEntry<K, V>> valueReference = MapMakerInternalMap.unsetWeakValueReference();
    
    WeakKeyWeakValueEntry(ReferenceQueue<K> queue, K key, int hash) {
      super(queue, key, hash);
    }
    
    public final V getValue() {
      return this.valueReference.get();
    }
    
    public final MapMakerInternalMap.WeakValueReference<K, V, WeakKeyWeakValueEntry<K, V>> getValueReference() {
      return this.valueReference;
    }
    
    private static final class LinkedWeakKeyWeakValueEntry<K, V> extends WeakKeyWeakValueEntry<K, V> {
      private final MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> next;
      
      LinkedWeakKeyWeakValueEntry(ReferenceQueue<K> queue, K key, int hash, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> next) {
        super(queue, key, hash);
        this.next = next;
      }
      
      public MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> getNext() {
        return this.next;
      }
    }
    
    static final class Helper<K, V> implements MapMakerInternalMap.InternalEntryHelper<K, V, WeakKeyWeakValueEntry<K, V>, MapMakerInternalMap.WeakKeyWeakValueSegment<K, V>> {
      private static final Helper<?, ?> INSTANCE = new Helper();
      
      static <K, V> Helper<K, V> instance() {
        return (Helper)INSTANCE;
      }
      
      public MapMakerInternalMap.Strength keyStrength() {
        return MapMakerInternalMap.Strength.WEAK;
      }
      
      public MapMakerInternalMap.Strength valueStrength() {
        return MapMakerInternalMap.Strength.WEAK;
      }
      
      public MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> newSegment(MapMakerInternalMap<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>, MapMakerInternalMap.WeakKeyWeakValueSegment<K, V>> map, int initialCapacity) {
        return new MapMakerInternalMap.WeakKeyWeakValueSegment<>(map, initialCapacity);
      }
      
      @CheckForNull
      public MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> copy(MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> segment, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> entry, @CheckForNull MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> newNext) {
        K key = entry.getKey();
        if (key == null)
          return null; 
        if (MapMakerInternalMap.Segment.isCollected(entry))
          return null; 
        MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> newEntry = newEntry(segment, key, entry.hash, newNext);
        newEntry.valueReference = entry.valueReference.copyFor(segment.queueForValues, newEntry);
        return newEntry;
      }
      
      public void setValue(MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> segment, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> entry, V value) {
        MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>> previous = entry.valueReference;
        entry.valueReference = new MapMakerInternalMap.WeakValueReferenceImpl<>(segment.queueForValues, value, entry);
        previous.clear();
      }
      
      public MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> newEntry(MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> segment, K key, int hash, @CheckForNull MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> next) {
        return (next == null) ? 
          new MapMakerInternalMap.WeakKeyWeakValueEntry<>(segment.queueForKeys, key, hash) : 
          new MapMakerInternalMap.WeakKeyWeakValueEntry.LinkedWeakKeyWeakValueEntry<>(segment.queueForKeys, key, hash, next);
      }
    }
  }
  
  static final class Helper<K, V> implements InternalEntryHelper<K, V, WeakKeyWeakValueEntry<K, V>, WeakKeyWeakValueSegment<K, V>> {
    private static final Helper<?, ?> INSTANCE = new Helper();
    
    static <K, V> Helper<K, V> instance() {
      return (Helper)INSTANCE;
    }
    
    public MapMakerInternalMap.Strength keyStrength() {
      return MapMakerInternalMap.Strength.WEAK;
    }
    
    public MapMakerInternalMap.Strength valueStrength() {
      return MapMakerInternalMap.Strength.WEAK;
    }
    
    public MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> newSegment(MapMakerInternalMap<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>, MapMakerInternalMap.WeakKeyWeakValueSegment<K, V>> map, int initialCapacity) {
      return new MapMakerInternalMap.WeakKeyWeakValueSegment<>(map, initialCapacity);
    }
    
    @CheckForNull
    public MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> copy(MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> segment, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> entry, @CheckForNull MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> newNext) {
      K key = entry.getKey();
      if (key == null)
        return null; 
      if (MapMakerInternalMap.Segment.isCollected(entry))
        return null; 
      MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> newEntry = newEntry(segment, key, entry.hash, newNext);
      newEntry.valueReference = entry.valueReference.copyFor(segment.queueForValues, newEntry);
      return newEntry;
    }
    
    public void setValue(MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> segment, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> entry, V value) {
      MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>> previous = entry.valueReference;
      entry.valueReference = new MapMakerInternalMap.WeakValueReferenceImpl<>(segment.queueForValues, value, entry);
      previous.clear();
    }
    
    public MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> newEntry(MapMakerInternalMap.WeakKeyWeakValueSegment<K, V> segment, K key, int hash, @CheckForNull MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> next) {
      return (next == null) ? new MapMakerInternalMap.WeakKeyWeakValueEntry<>(segment.queueForKeys, key, hash) : new MapMakerInternalMap.WeakKeyWeakValueEntry.LinkedWeakKeyWeakValueEntry<>(segment.queueForKeys, key, hash, next);
    }
  }
  
  static final class DummyInternalEntry implements InternalEntry<Object, Object, DummyInternalEntry> {
    private DummyInternalEntry() {
      throw new AssertionError();
    }
    
    public DummyInternalEntry getNext() {
      throw new AssertionError();
    }
    
    public int getHash() {
      throw new AssertionError();
    }
    
    public Object getKey() {
      throw new AssertionError();
    }
    
    public Object getValue() {
      throw new AssertionError();
    }
  }
  
  static final WeakValueReference<Object, Object, DummyInternalEntry> UNSET_WEAK_VALUE_REFERENCE = new WeakValueReference<Object, Object, DummyInternalEntry>() {
      @CheckForNull
      public MapMakerInternalMap.DummyInternalEntry getEntry() {
        return null;
      }
      
      public void clear() {}
      
      @CheckForNull
      public Object get() {
        return null;
      }
      
      public MapMakerInternalMap.WeakValueReference<Object, Object, MapMakerInternalMap.DummyInternalEntry> copyFor(ReferenceQueue<Object> queue, MapMakerInternalMap.DummyInternalEntry entry) {
        return this;
      }
    };
  
  @LazyInit
  @CheckForNull
  transient Set<K> keySet;
  
  @LazyInit
  @CheckForNull
  transient Collection<V> values;
  
  @LazyInit
  @CheckForNull
  transient Set<Map.Entry<K, V>> entrySet;
  
  private static final long serialVersionUID = 5L;
  
  static final class WeakValueReferenceImpl<K, V, E extends InternalEntry<K, V, E>> extends WeakReference<V> implements WeakValueReference<K, V, E> {
    @Weak
    final E entry;
    
    WeakValueReferenceImpl(ReferenceQueue<V> queue, V referent, E entry) {
      super(referent, queue);
      this.entry = entry;
    }
    
    public E getEntry() {
      return this.entry;
    }
    
    public MapMakerInternalMap.WeakValueReference<K, V, E> copyFor(ReferenceQueue<V> queue, E entry) {
      return new WeakValueReferenceImpl(queue, get(), entry);
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
  E copyEntry(E original, E newNext) {
    int hash = original.getHash();
    return segmentFor(hash).copyEntry(original, newNext);
  }
  
  int hash(Object key) {
    int h = this.keyEquivalence.hash(key);
    return rehash(h);
  }
  
  void reclaimValue(WeakValueReference<K, V, E> valueReference) {
    E entry = valueReference.getEntry();
    int hash = entry.getHash();
    segmentFor(hash).reclaimValue((K)entry.getKey(), hash, valueReference);
  }
  
  void reclaimKey(E entry) {
    int hash = entry.getHash();
    segmentFor(hash).reclaimKey(entry, hash);
  }
  
  @VisibleForTesting
  boolean isLiveForTesting(InternalEntry<K, V, ?> entry) {
    return (segmentFor(entry.getHash()).getLiveValueForTesting(entry) != null);
  }
  
  Segment<K, V, E, S> segmentFor(int hash) {
    return this.segments[hash >>> this.segmentShift & this.segmentMask];
  }
  
  Segment<K, V, E, S> createSegment(int initialCapacity) {
    return (Segment<K, V, E, S>)this.entryHelper.newSegment(this, initialCapacity);
  }
  
  @CheckForNull
  V getLiveValue(E entry) {
    if (entry.getKey() == null)
      return null; 
    return (V)entry.getValue();
  }
  
  final Segment<K, V, E, S>[] newSegmentArray(int ssize) {
    return (Segment<K, V, E, S>[])new Segment[ssize];
  }
  
  static abstract class Segment<K, V, E extends InternalEntry<K, V, E>, S extends Segment<K, V, E, S>> extends ReentrantLock {
    @Weak
    final MapMakerInternalMap<K, V, E, S> map;
    
    volatile int count;
    
    int modCount;
    
    int threshold;
    
    @CheckForNull
    volatile AtomicReferenceArray<E> table;
    
    final AtomicInteger readCount = new AtomicInteger();
    
    Segment(MapMakerInternalMap<K, V, E, S> map, int initialCapacity) {
      this.map = map;
      initTable(newEntryArray(initialCapacity));
    }
    
    abstract S self();
    
    @GuardedBy("this")
    void maybeDrainReferenceQueues() {}
    
    void maybeClearReferenceQueues() {}
    
    void setValue(E entry, V value) {
      this.map.entryHelper.setValue(self(), entry, value);
    }
    
    @CheckForNull
    E copyEntry(E original, E newNext) {
      return this.map.entryHelper.copy(self(), original, newNext);
    }
    
    AtomicReferenceArray<E> newEntryArray(int size) {
      return new AtomicReferenceArray<>(size);
    }
    
    void initTable(AtomicReferenceArray<E> newTable) {
      this.threshold = newTable.length() * 3 / 4;
      this.table = newTable;
    }
    
    abstract E castForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> param1InternalEntry);
    
    ReferenceQueue<K> getKeyReferenceQueueForTesting() {
      throw new AssertionError();
    }
    
    ReferenceQueue<V> getValueReferenceQueueForTesting() {
      throw new AssertionError();
    }
    
    MapMakerInternalMap.WeakValueReference<K, V, E> getWeakValueReferenceForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> entry) {
      throw new AssertionError();
    }
    
    MapMakerInternalMap.WeakValueReference<K, V, E> newWeakValueReferenceForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> entry, V value) {
      throw new AssertionError();
    }
    
    void setWeakValueReferenceForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> entry, MapMakerInternalMap.WeakValueReference<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>> valueReference) {
      throw new AssertionError();
    }
    
    void setTableEntryForTesting(int i, MapMakerInternalMap.InternalEntry<K, V, ?> entry) {
      this.table.set(i, castForTesting(entry));
    }
    
    E copyForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> entry, @CheckForNull MapMakerInternalMap.InternalEntry<K, V, ?> newNext) {
      return this.map.entryHelper.copy(self(), castForTesting(entry), castForTesting(newNext));
    }
    
    void setValueForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> entry, V value) {
      this.map.entryHelper.setValue(self(), castForTesting(entry), value);
    }
    
    E newEntryForTesting(K key, int hash, @CheckForNull MapMakerInternalMap.InternalEntry<K, V, ?> next) {
      return this.map.entryHelper.newEntry(self(), key, hash, castForTesting(next));
    }
    
    @CanIgnoreReturnValue
    boolean removeTableEntryForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> entry) {
      return removeEntryForTesting(castForTesting(entry));
    }
    
    @CheckForNull
    E removeFromChainForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> first, MapMakerInternalMap.InternalEntry<K, V, ?> entry) {
      return removeFromChain(castForTesting(first), castForTesting(entry));
    }
    
    @CheckForNull
    V getLiveValueForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> entry) {
      return getLiveValue(castForTesting(entry));
    }
    
    void tryDrainReferenceQueues() {
      if (tryLock())
        try {
          maybeDrainReferenceQueues();
        } finally {
          unlock();
        }  
    }
    
    @GuardedBy("this")
    void drainKeyReferenceQueue(ReferenceQueue<K> keyReferenceQueue) {
      int i = 0;
      Reference<? extends K> ref;
      while ((ref = keyReferenceQueue.poll()) != null) {
        MapMakerInternalMap.InternalEntry internalEntry = (MapMakerInternalMap.InternalEntry)ref;
        this.map.reclaimKey((E)internalEntry);
        if (++i == 16)
          break; 
      } 
    }
    
    @GuardedBy("this")
    void drainValueReferenceQueue(ReferenceQueue<V> valueReferenceQueue) {
      int i = 0;
      Reference<? extends V> ref;
      while ((ref = valueReferenceQueue.poll()) != null) {
        MapMakerInternalMap.WeakValueReference<K, V, E> valueReference = (MapMakerInternalMap.WeakValueReference)ref;
        this.map.reclaimValue(valueReference);
        if (++i == 16)
          break; 
      } 
    }
    
    <T> void clearReferenceQueue(ReferenceQueue<T> referenceQueue) {
      while (referenceQueue.poll() != null);
    }
    
    @CheckForNull
    E getFirst(int hash) {
      AtomicReferenceArray<E> table = this.table;
      return table.get(hash & table.length() - 1);
    }
    
    @CheckForNull
    E getEntry(Object key, int hash) {
      if (this.count != 0)
        for (E e = getFirst(hash); e != null; e = (E)e.getNext()) {
          if (e.getHash() == hash) {
            K entryKey = (K)e.getKey();
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
    E getLiveEntry(Object key, int hash) {
      return getEntry(key, hash);
    }
    
    @CheckForNull
    V get(Object key, int hash) {
      try {
        E e = getLiveEntry(key, hash);
        if (e == null)
          return null; 
        V value = (V)e.getValue();
        if (value == null)
          tryDrainReferenceQueues(); 
        return value;
      } finally {
        postReadCleanup();
      } 
    }
    
    boolean containsKey(Object key, int hash) {
      try {
        if (this.count != 0) {
          E e = getLiveEntry(key, hash);
          return (e != null && e.getValue() != null);
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
          AtomicReferenceArray<E> table = this.table;
          int length = table.length();
          for (int i = 0; i < length; i++) {
            for (MapMakerInternalMap.InternalEntry internalEntry = (MapMakerInternalMap.InternalEntry)table.get(i); internalEntry != null; internalEntry = (MapMakerInternalMap.InternalEntry)internalEntry.getNext()) {
              V entryValue = getLiveValue((E)internalEntry);
              if (entryValue != null)
                if (this.map.valueEquivalence().equivalent(value, entryValue))
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
    V put(K key, int hash, V value, boolean onlyIfAbsent) {
      lock();
      try {
        preWriteCleanup();
        int newCount = this.count + 1;
        if (newCount > this.threshold) {
          expand();
          newCount = this.count + 1;
        } 
        AtomicReferenceArray<E> table = this.table;
        int index = hash & table.length() - 1;
        MapMakerInternalMap.InternalEntry internalEntry1 = (MapMakerInternalMap.InternalEntry)table.get(index);
        for (MapMakerInternalMap.InternalEntry internalEntry2 = internalEntry1; internalEntry2 != null; internalEntry2 = (MapMakerInternalMap.InternalEntry)internalEntry2.getNext()) {
          K entryKey = (K)internalEntry2.getKey();
          if (internalEntry2.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            V entryValue = (V)internalEntry2.getValue();
            if (entryValue == null) {
              this.modCount++;
              setValue((E)internalEntry2, value);
              newCount = this.count;
              this.count = newCount;
              return null;
            } 
            if (onlyIfAbsent)
              return entryValue; 
            this.modCount++;
            setValue((E)internalEntry2, value);
            return entryValue;
          } 
        } 
        this.modCount++;
        E newEntry = this.map.entryHelper.newEntry(self(), key, hash, (E)internalEntry1);
        setValue(newEntry, value);
        table.set(index, newEntry);
        this.count = newCount;
        return null;
      } finally {
        unlock();
      } 
    }
    
    @GuardedBy("this")
    void expand() {
      AtomicReferenceArray<E> oldTable = this.table;
      int oldCapacity = oldTable.length();
      if (oldCapacity >= 1073741824)
        return; 
      int newCount = this.count;
      AtomicReferenceArray<E> newTable = newEntryArray(oldCapacity << 1);
      this.threshold = newTable.length() * 3 / 4;
      int newMask = newTable.length() - 1;
      for (int oldIndex = 0; oldIndex < oldCapacity; oldIndex++) {
        MapMakerInternalMap.InternalEntry internalEntry = (MapMakerInternalMap.InternalEntry)oldTable.get(oldIndex);
        if (internalEntry != null) {
          E next = (E)internalEntry.getNext();
          int headIndex = internalEntry.getHash() & newMask;
          if (next == null) {
            newTable.set(headIndex, (E)internalEntry);
          } else {
            E e1;
            MapMakerInternalMap.InternalEntry internalEntry1 = internalEntry;
            int tailIndex = headIndex;
            for (E e = next; e != null; e = (E)e.getNext()) {
              int newIndex = e.getHash() & newMask;
              if (newIndex != tailIndex) {
                tailIndex = newIndex;
                e1 = e;
              } 
            } 
            newTable.set(tailIndex, e1);
            for (MapMakerInternalMap.InternalEntry internalEntry2 = internalEntry; internalEntry2 != e1; internalEntry2 = (MapMakerInternalMap.InternalEntry)internalEntry2.getNext()) {
              int newIndex = internalEntry2.getHash() & newMask;
              MapMakerInternalMap.InternalEntry internalEntry3 = (MapMakerInternalMap.InternalEntry)newTable.get(newIndex);
              E newFirst = copyEntry((E)internalEntry2, (E)internalEntry3);
              if (newFirst != null) {
                newTable.set(newIndex, newFirst);
              } else {
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
        preWriteCleanup();
        AtomicReferenceArray<E> table = this.table;
        int index = hash & table.length() - 1;
        MapMakerInternalMap.InternalEntry internalEntry1 = (MapMakerInternalMap.InternalEntry)table.get(index);
        for (MapMakerInternalMap.InternalEntry internalEntry2 = internalEntry1; internalEntry2 != null; internalEntry2 = (MapMakerInternalMap.InternalEntry)internalEntry2.getNext()) {
          K entryKey = (K)internalEntry2.getKey();
          if (internalEntry2.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            V entryValue = (V)internalEntry2.getValue();
            if (entryValue == null) {
              if (isCollected(internalEntry2)) {
                int newCount = this.count - 1;
                this.modCount++;
                E newFirst = removeFromChain((E)internalEntry1, (E)internalEntry2);
                newCount = this.count - 1;
                table.set(index, newFirst);
                this.count = newCount;
              } 
              return false;
            } 
            if (this.map.valueEquivalence().equivalent(oldValue, entryValue)) {
              this.modCount++;
              setValue((E)internalEntry2, newValue);
              return true;
            } 
            return false;
          } 
        } 
        return false;
      } finally {
        unlock();
      } 
    }
    
    @CheckForNull
    V replace(K key, int hash, V newValue) {
      lock();
      try {
        preWriteCleanup();
        AtomicReferenceArray<E> table = this.table;
        int index = hash & table.length() - 1;
        MapMakerInternalMap.InternalEntry internalEntry1 = (MapMakerInternalMap.InternalEntry)table.get(index);
        MapMakerInternalMap.InternalEntry internalEntry2;
        for (internalEntry2 = internalEntry1; internalEntry2 != null; internalEntry2 = (MapMakerInternalMap.InternalEntry)internalEntry2.getNext()) {
          K entryKey = (K)internalEntry2.getKey();
          if (internalEntry2.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            V entryValue = (V)internalEntry2.getValue();
            if (entryValue == null) {
              if (isCollected(internalEntry2)) {
                int newCount = this.count - 1;
                this.modCount++;
                E newFirst = removeFromChain((E)internalEntry1, (E)internalEntry2);
                newCount = this.count - 1;
                table.set(index, newFirst);
                this.count = newCount;
              } 
              return null;
            } 
            this.modCount++;
            setValue((E)internalEntry2, newValue);
            return entryValue;
          } 
        } 
        internalEntry2 = null;
        return (V)internalEntry2;
      } finally {
        unlock();
      } 
    }
    
    @CheckForNull
    @CanIgnoreReturnValue
    V remove(Object key, int hash) {
      lock();
      try {
        preWriteCleanup();
        int newCount = this.count - 1;
        AtomicReferenceArray<E> table = this.table;
        int index = hash & table.length() - 1;
        MapMakerInternalMap.InternalEntry internalEntry1 = (MapMakerInternalMap.InternalEntry)table.get(index);
        MapMakerInternalMap.InternalEntry internalEntry2;
        for (internalEntry2 = internalEntry1; internalEntry2 != null; internalEntry2 = (MapMakerInternalMap.InternalEntry)internalEntry2.getNext()) {
          K entryKey = (K)internalEntry2.getKey();
          if (internalEntry2.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            V entryValue = (V)internalEntry2.getValue();
            if (entryValue == null)
              if (!isCollected(internalEntry2))
                return null;  
            this.modCount++;
            E newFirst = removeFromChain((E)internalEntry1, (E)internalEntry2);
            newCount = this.count - 1;
            table.set(index, newFirst);
            this.count = newCount;
            return entryValue;
          } 
        } 
        internalEntry2 = null;
        return (V)internalEntry2;
      } finally {
        unlock();
      } 
    }
    
    boolean remove(Object key, int hash, Object value) {
      lock();
      try {
        preWriteCleanup();
        int newCount = this.count - 1;
        AtomicReferenceArray<E> table = this.table;
        int index = hash & table.length() - 1;
        MapMakerInternalMap.InternalEntry internalEntry1 = (MapMakerInternalMap.InternalEntry)table.get(index);
        for (MapMakerInternalMap.InternalEntry internalEntry2 = internalEntry1; internalEntry2 != null; internalEntry2 = (MapMakerInternalMap.InternalEntry)internalEntry2.getNext()) {
          K entryKey = (K)internalEntry2.getKey();
          if (internalEntry2.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            V entryValue = (V)internalEntry2.getValue();
            boolean explicitRemoval = false;
            if (this.map.valueEquivalence().equivalent(value, entryValue)) {
              explicitRemoval = true;
            } else if (!isCollected(internalEntry2)) {
              return false;
            } 
            this.modCount++;
            E newFirst = removeFromChain((E)internalEntry1, (E)internalEntry2);
            newCount = this.count - 1;
            table.set(index, newFirst);
            this.count = newCount;
            return explicitRemoval;
          } 
        } 
        return false;
      } finally {
        unlock();
      } 
    }
    
    void clear() {
      if (this.count != 0) {
        lock();
        try {
          AtomicReferenceArray<E> table = this.table;
          for (int i = 0; i < table.length(); i++)
            table.set(i, null); 
          maybeClearReferenceQueues();
          this.readCount.set(0);
          this.modCount++;
          this.count = 0;
        } finally {
          unlock();
        } 
      } 
    }
    
    @CheckForNull
    @GuardedBy("this")
    E removeFromChain(E first, E entry) {
      int newCount = this.count;
      E newFirst = (E)entry.getNext();
      for (E e = first; e != entry; e = (E)e.getNext()) {
        E next = copyEntry(e, newFirst);
        if (next != null) {
          newFirst = next;
        } else {
          newCount--;
        } 
      } 
      this.count = newCount;
      return newFirst;
    }
    
    @CanIgnoreReturnValue
    boolean reclaimKey(E entry, int hash) {
      lock();
      try {
        int newCount = this.count - 1;
        AtomicReferenceArray<E> table = this.table;
        int index = hash & table.length() - 1;
        MapMakerInternalMap.InternalEntry internalEntry1 = (MapMakerInternalMap.InternalEntry)table.get(index);
        for (MapMakerInternalMap.InternalEntry internalEntry2 = internalEntry1; internalEntry2 != null; internalEntry2 = (MapMakerInternalMap.InternalEntry)internalEntry2.getNext()) {
          if (internalEntry2 == entry) {
            this.modCount++;
            E newFirst = removeFromChain((E)internalEntry1, (E)internalEntry2);
            newCount = this.count - 1;
            table.set(index, newFirst);
            this.count = newCount;
            return true;
          } 
        } 
        return false;
      } finally {
        unlock();
      } 
    }
    
    @CanIgnoreReturnValue
    boolean reclaimValue(K key, int hash, MapMakerInternalMap.WeakValueReference<K, V, E> valueReference) {
      lock();
      try {
        int newCount = this.count - 1;
        AtomicReferenceArray<E> table = this.table;
        int index = hash & table.length() - 1;
        MapMakerInternalMap.InternalEntry internalEntry1 = (MapMakerInternalMap.InternalEntry)table.get(index);
        for (MapMakerInternalMap.InternalEntry internalEntry2 = internalEntry1; internalEntry2 != null; internalEntry2 = (MapMakerInternalMap.InternalEntry)internalEntry2.getNext()) {
          K entryKey = (K)internalEntry2.getKey();
          if (internalEntry2.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            MapMakerInternalMap.WeakValueReference<K, V, E> v = ((MapMakerInternalMap.WeakValueEntry<K, V, E>)internalEntry2).getValueReference();
            if (v == valueReference) {
              this.modCount++;
              E newFirst = removeFromChain((E)internalEntry1, (E)internalEntry2);
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
      } 
    }
    
    @CanIgnoreReturnValue
    boolean clearValueForTesting(K key, int hash, MapMakerInternalMap.WeakValueReference<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>> valueReference) {
      lock();
      try {
        AtomicReferenceArray<E> table = this.table;
        int index = hash & table.length() - 1;
        MapMakerInternalMap.InternalEntry internalEntry1 = (MapMakerInternalMap.InternalEntry)table.get(index);
        for (MapMakerInternalMap.InternalEntry internalEntry2 = internalEntry1; internalEntry2 != null; internalEntry2 = (MapMakerInternalMap.InternalEntry)internalEntry2.getNext()) {
          K entryKey = (K)internalEntry2.getKey();
          if (internalEntry2.getHash() == hash && entryKey != null && this.map.keyEquivalence
            
            .equivalent(key, entryKey)) {
            MapMakerInternalMap.WeakValueReference<K, V, E> v = ((MapMakerInternalMap.WeakValueEntry<K, V, E>)internalEntry2).getValueReference();
            if (v == valueReference) {
              E newFirst = removeFromChain((E)internalEntry1, (E)internalEntry2);
              table.set(index, newFirst);
              return true;
            } 
            return false;
          } 
        } 
        return false;
      } finally {
        unlock();
      } 
    }
    
    @GuardedBy("this")
    boolean removeEntryForTesting(E entry) {
      int hash = entry.getHash();
      int newCount = this.count - 1;
      AtomicReferenceArray<E> table = this.table;
      int index = hash & table.length() - 1;
      MapMakerInternalMap.InternalEntry internalEntry1 = (MapMakerInternalMap.InternalEntry)table.get(index);
      for (MapMakerInternalMap.InternalEntry internalEntry2 = internalEntry1; internalEntry2 != null; internalEntry2 = (MapMakerInternalMap.InternalEntry)internalEntry2.getNext()) {
        if (internalEntry2 == entry) {
          this.modCount++;
          E newFirst = removeFromChain((E)internalEntry1, (E)internalEntry2);
          newCount = this.count - 1;
          table.set(index, newFirst);
          this.count = newCount;
          return true;
        } 
      } 
      return false;
    }
    
    static <K, V, E extends MapMakerInternalMap.InternalEntry<K, V, E>> boolean isCollected(E entry) {
      return (entry.getValue() == null);
    }
    
    @CheckForNull
    V getLiveValue(E entry) {
      if (entry.getKey() == null) {
        tryDrainReferenceQueues();
        return null;
      } 
      V value = (V)entry.getValue();
      if (value == null) {
        tryDrainReferenceQueues();
        return null;
      } 
      return value;
    }
    
    void postReadCleanup() {
      if ((this.readCount.incrementAndGet() & 0x3F) == 0)
        runCleanup(); 
    }
    
    @GuardedBy("this")
    void preWriteCleanup() {
      runLockedCleanup();
    }
    
    void runCleanup() {
      runLockedCleanup();
    }
    
    void runLockedCleanup() {
      if (tryLock())
        try {
          maybeDrainReferenceQueues();
          this.readCount.set(0);
        } finally {
          unlock();
        }  
    }
  }
  
  static final class StrongKeyStrongValueSegment<K, V> extends Segment<K, V, StrongKeyStrongValueEntry<K, V>, StrongKeyStrongValueSegment<K, V>> {
    StrongKeyStrongValueSegment(MapMakerInternalMap<K, V, MapMakerInternalMap.StrongKeyStrongValueEntry<K, V>, StrongKeyStrongValueSegment<K, V>> map, int initialCapacity) {
      super(map, initialCapacity);
    }
    
    StrongKeyStrongValueSegment<K, V> self() {
      return this;
    }
    
    @CheckForNull
    public MapMakerInternalMap.StrongKeyStrongValueEntry<K, V> castForTesting(@CheckForNull MapMakerInternalMap.InternalEntry<K, V, ?> entry) {
      return (MapMakerInternalMap.StrongKeyStrongValueEntry)entry;
    }
  }
  
  static final class StrongKeyWeakValueSegment<K, V> extends Segment<K, V, StrongKeyWeakValueEntry<K, V>, StrongKeyWeakValueSegment<K, V>> {
    private final ReferenceQueue<V> queueForValues = new ReferenceQueue<>();
    
    StrongKeyWeakValueSegment(MapMakerInternalMap<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>, StrongKeyWeakValueSegment<K, V>> map, int initialCapacity) {
      super(map, initialCapacity);
    }
    
    StrongKeyWeakValueSegment<K, V> self() {
      return this;
    }
    
    ReferenceQueue<V> getValueReferenceQueueForTesting() {
      return this.queueForValues;
    }
    
    @CheckForNull
    public MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> castForTesting(@CheckForNull MapMakerInternalMap.InternalEntry<K, V, ?> entry) {
      return (MapMakerInternalMap.StrongKeyWeakValueEntry)entry;
    }
    
    public MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>> getWeakValueReferenceForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> e) {
      return castForTesting(e).getValueReference();
    }
    
    public MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>> newWeakValueReferenceForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> e, V value) {
      return new MapMakerInternalMap.WeakValueReferenceImpl<>(this.queueForValues, value, castForTesting(e));
    }
    
    public void setWeakValueReferenceForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> e, MapMakerInternalMap.WeakValueReference<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>> valueReference) {
      MapMakerInternalMap.StrongKeyWeakValueEntry<K, V> entry = castForTesting(e);
      MapMakerInternalMap.WeakValueReference<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>> weakValueReference = valueReference;
      MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.StrongKeyWeakValueEntry<K, V>> previous = entry.valueReference;
      entry.valueReference = weakValueReference;
      previous.clear();
    }
    
    void maybeDrainReferenceQueues() {
      drainValueReferenceQueue(this.queueForValues);
    }
    
    void maybeClearReferenceQueues() {
      clearReferenceQueue(this.queueForValues);
    }
  }
  
  static final class StrongKeyDummyValueSegment<K> extends Segment<K, MapMaker.Dummy, StrongKeyDummyValueEntry<K>, StrongKeyDummyValueSegment<K>> {
    StrongKeyDummyValueSegment(MapMakerInternalMap<K, MapMaker.Dummy, MapMakerInternalMap.StrongKeyDummyValueEntry<K>, StrongKeyDummyValueSegment<K>> map, int initialCapacity) {
      super(map, initialCapacity);
    }
    
    StrongKeyDummyValueSegment<K> self() {
      return this;
    }
    
    public MapMakerInternalMap.StrongKeyDummyValueEntry<K> castForTesting(MapMakerInternalMap.InternalEntry<K, MapMaker.Dummy, ?> entry) {
      return (MapMakerInternalMap.StrongKeyDummyValueEntry)entry;
    }
  }
  
  static final class WeakKeyStrongValueSegment<K, V> extends Segment<K, V, WeakKeyStrongValueEntry<K, V>, WeakKeyStrongValueSegment<K, V>> {
    private final ReferenceQueue<K> queueForKeys = new ReferenceQueue<>();
    
    WeakKeyStrongValueSegment(MapMakerInternalMap<K, V, MapMakerInternalMap.WeakKeyStrongValueEntry<K, V>, WeakKeyStrongValueSegment<K, V>> map, int initialCapacity) {
      super(map, initialCapacity);
    }
    
    WeakKeyStrongValueSegment<K, V> self() {
      return this;
    }
    
    ReferenceQueue<K> getKeyReferenceQueueForTesting() {
      return this.queueForKeys;
    }
    
    public MapMakerInternalMap.WeakKeyStrongValueEntry<K, V> castForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> entry) {
      return (MapMakerInternalMap.WeakKeyStrongValueEntry)entry;
    }
    
    void maybeDrainReferenceQueues() {
      drainKeyReferenceQueue(this.queueForKeys);
    }
    
    void maybeClearReferenceQueues() {
      clearReferenceQueue(this.queueForKeys);
    }
  }
  
  static final class WeakKeyWeakValueSegment<K, V> extends Segment<K, V, WeakKeyWeakValueEntry<K, V>, WeakKeyWeakValueSegment<K, V>> {
    private final ReferenceQueue<K> queueForKeys = new ReferenceQueue<>();
    
    private final ReferenceQueue<V> queueForValues = new ReferenceQueue<>();
    
    WeakKeyWeakValueSegment(MapMakerInternalMap<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>, WeakKeyWeakValueSegment<K, V>> map, int initialCapacity) {
      super(map, initialCapacity);
    }
    
    WeakKeyWeakValueSegment<K, V> self() {
      return this;
    }
    
    ReferenceQueue<K> getKeyReferenceQueueForTesting() {
      return this.queueForKeys;
    }
    
    ReferenceQueue<V> getValueReferenceQueueForTesting() {
      return this.queueForValues;
    }
    
    @CheckForNull
    public MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> castForTesting(@CheckForNull MapMakerInternalMap.InternalEntry<K, V, ?> entry) {
      return (MapMakerInternalMap.WeakKeyWeakValueEntry)entry;
    }
    
    public MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>> getWeakValueReferenceForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> e) {
      return castForTesting(e).getValueReference();
    }
    
    public MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>> newWeakValueReferenceForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> e, V value) {
      return new MapMakerInternalMap.WeakValueReferenceImpl<>(this.queueForValues, value, castForTesting(e));
    }
    
    public void setWeakValueReferenceForTesting(MapMakerInternalMap.InternalEntry<K, V, ?> e, MapMakerInternalMap.WeakValueReference<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>> valueReference) {
      MapMakerInternalMap.WeakKeyWeakValueEntry<K, V> entry = castForTesting(e);
      MapMakerInternalMap.WeakValueReference<K, V, ? extends MapMakerInternalMap.InternalEntry<K, V, ?>> weakValueReference = valueReference;
      MapMakerInternalMap.WeakValueReference<K, V, MapMakerInternalMap.WeakKeyWeakValueEntry<K, V>> previous = entry.valueReference;
      entry.valueReference = weakValueReference;
      previous.clear();
    }
    
    void maybeDrainReferenceQueues() {
      drainKeyReferenceQueue(this.queueForKeys);
      drainValueReferenceQueue(this.queueForValues);
    }
    
    void maybeClearReferenceQueues() {
      clearReferenceQueue(this.queueForKeys);
    }
  }
  
  static final class WeakKeyDummyValueSegment<K> extends Segment<K, MapMaker.Dummy, WeakKeyDummyValueEntry<K>, WeakKeyDummyValueSegment<K>> {
    private final ReferenceQueue<K> queueForKeys = new ReferenceQueue<>();
    
    WeakKeyDummyValueSegment(MapMakerInternalMap<K, MapMaker.Dummy, MapMakerInternalMap.WeakKeyDummyValueEntry<K>, WeakKeyDummyValueSegment<K>> map, int initialCapacity) {
      super(map, initialCapacity);
    }
    
    WeakKeyDummyValueSegment<K> self() {
      return this;
    }
    
    ReferenceQueue<K> getKeyReferenceQueueForTesting() {
      return this.queueForKeys;
    }
    
    public MapMakerInternalMap.WeakKeyDummyValueEntry<K> castForTesting(MapMakerInternalMap.InternalEntry<K, MapMaker.Dummy, ?> entry) {
      return (MapMakerInternalMap.WeakKeyDummyValueEntry)entry;
    }
    
    void maybeDrainReferenceQueues() {
      drainKeyReferenceQueue(this.queueForKeys);
    }
    
    void maybeClearReferenceQueues() {
      clearReferenceQueue(this.queueForKeys);
    }
  }
  
  static final class CleanupMapTask implements Runnable {
    final WeakReference<MapMakerInternalMap<?, ?, ?, ?>> mapReference;
    
    public CleanupMapTask(MapMakerInternalMap<?, ?, ?, ?> map) {
      this.mapReference = new WeakReference<>(map);
    }
    
    public void run() {
      MapMakerInternalMap<?, ?, ?, ?> map = this.mapReference.get();
      if (map == null)
        throw new CancellationException(); 
      for (MapMakerInternalMap.Segment<?, ?, ?, ?> segment : map.segments)
        segment.runCleanup(); 
    }
  }
  
  @VisibleForTesting
  Strength keyStrength() {
    return this.entryHelper.keyStrength();
  }
  
  @VisibleForTesting
  Strength valueStrength() {
    return this.entryHelper.valueStrength();
  }
  
  @VisibleForTesting
  Equivalence<Object> valueEquivalence() {
    return this.entryHelper.valueStrength().defaultEquivalence();
  }
  
  public boolean isEmpty() {
    long sum = 0L;
    Segment<K, V, E, S>[] segments = this.segments;
    int i;
    for (i = 0; i < segments.length; i++) {
      if ((segments[i]).count != 0)
        return false; 
      sum += (segments[i]).modCount;
    } 
    if (sum != 0L) {
      for (i = 0; i < segments.length; i++) {
        if ((segments[i]).count != 0)
          return false; 
        sum -= (segments[i]).modCount;
      } 
      return (sum == 0L);
    } 
    return true;
  }
  
  public int size() {
    Segment<K, V, E, S>[] segments = this.segments;
    long sum = 0L;
    for (int i = 0; i < segments.length; i++)
      sum += (segments[i]).count; 
    return Ints.saturatedCast(sum);
  }
  
  @CheckForNull
  public V get(@CheckForNull Object key) {
    if (key == null)
      return null; 
    int hash = hash(key);
    return segmentFor(hash).get(key, hash);
  }
  
  @CheckForNull
  E getEntry(@CheckForNull Object key) {
    if (key == null)
      return null; 
    int hash = hash(key);
    return segmentFor(hash).getEntry(key, hash);
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
    Segment<K, V, E, S>[] segments = this.segments;
    long last = -1L;
    for (int i = 0; i < 3; i++) {
      long sum = 0L;
      for (Segment<K, V, E, S> segment : segments) {
        int unused = segment.count;
        AtomicReferenceArray<E> table = segment.table;
        for (int j = 0; j < table.length(); j++) {
          for (InternalEntry internalEntry = (InternalEntry)table.get(j); internalEntry != null; internalEntry = (InternalEntry)internalEntry.getNext()) {
            V v = segment.getLiveValue((E)internalEntry);
            if (v != null && valueEquivalence().equivalent(value, v))
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
  @CanIgnoreReturnValue
  public V putIfAbsent(K key, V value) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(value);
    int hash = hash(key);
    return segmentFor(hash).put(key, hash, value, true);
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
    for (Segment<K, V, E, S> segment : this.segments)
      segment.clear(); 
  }
  
  public Set<K> keySet() {
    Set<K> ks = this.keySet;
    return (ks != null) ? ks : (this.keySet = new KeySet());
  }
  
  public Collection<V> values() {
    Collection<V> vs = this.values;
    return (vs != null) ? vs : (this.values = new Values());
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    Set<Map.Entry<K, V>> es = this.entrySet;
    return (es != null) ? es : (this.entrySet = new EntrySet());
  }
  
  abstract class HashIterator<T> implements Iterator<T> {
    int nextSegmentIndex;
    
    int nextTableIndex;
    
    @CheckForNull
    MapMakerInternalMap.Segment<K, V, E, S> currentSegment;
    
    @CheckForNull
    AtomicReferenceArray<E> currentTable;
    
    @CheckForNull
    E nextEntry;
    
    @CheckForNull
    MapMakerInternalMap<K, V, E, S>.WriteThroughEntry nextExternal;
    
    @CheckForNull
    MapMakerInternalMap<K, V, E, S>.WriteThroughEntry lastReturned;
    
    HashIterator() {
      this.nextSegmentIndex = MapMakerInternalMap.this.segments.length - 1;
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
        this.currentSegment = MapMakerInternalMap.this.segments[this.nextSegmentIndex--];
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
        for (this.nextEntry = (E)this.nextEntry.getNext(); this.nextEntry != null; this.nextEntry = (E)this.nextEntry.getNext()) {
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
    
    boolean advanceTo(E entry) {
      try {
        K key = (K)entry.getKey();
        V value = (V)MapMakerInternalMap.this.getLiveValue(entry);
        if (value != null) {
          this.nextExternal = new MapMakerInternalMap.WriteThroughEntry(key, value);
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
    
    MapMakerInternalMap<K, V, E, S>.WriteThroughEntry nextEntry() {
      if (this.nextExternal == null)
        throw new NoSuchElementException(); 
      this.lastReturned = this.nextExternal;
      advance();
      return this.lastReturned;
    }
    
    public void remove() {
      CollectPreconditions.checkRemove((this.lastReturned != null));
      MapMakerInternalMap.this.remove(this.lastReturned.getKey());
      this.lastReturned = null;
    }
  }
  
  final class KeyIterator extends HashIterator<K> {
    KeyIterator(MapMakerInternalMap this$0) {
      super(this$0);
    }
    
    public K next() {
      return nextEntry().getKey();
    }
  }
  
  final class ValueIterator extends HashIterator<V> {
    ValueIterator(MapMakerInternalMap this$0) {
      super(this$0);
    }
    
    public V next() {
      return nextEntry().getValue();
    }
  }
  
  final class WriteThroughEntry extends AbstractMapEntry<K, V> {
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
      V oldValue = (V)MapMakerInternalMap.this.put(this.key, newValue);
      this.value = newValue;
      return oldValue;
    }
  }
  
  final class EntryIterator extends HashIterator<Map.Entry<K, V>> {
    EntryIterator(MapMakerInternalMap this$0) {
      super(this$0);
    }
    
    public Map.Entry<K, V> next() {
      return nextEntry();
    }
  }
  
  final class KeySet extends SafeToArraySet<K> {
    public Iterator<K> iterator() {
      return new MapMakerInternalMap.KeyIterator(MapMakerInternalMap.this);
    }
    
    public int size() {
      return MapMakerInternalMap.this.size();
    }
    
    public boolean isEmpty() {
      return MapMakerInternalMap.this.isEmpty();
    }
    
    public boolean contains(Object o) {
      return MapMakerInternalMap.this.containsKey(o);
    }
    
    public boolean remove(Object o) {
      return (MapMakerInternalMap.this.remove(o) != null);
    }
    
    public void clear() {
      MapMakerInternalMap.this.clear();
    }
  }
  
  final class Values extends AbstractCollection<V> {
    public Iterator<V> iterator() {
      return new MapMakerInternalMap.ValueIterator(MapMakerInternalMap.this);
    }
    
    public int size() {
      return MapMakerInternalMap.this.size();
    }
    
    public boolean isEmpty() {
      return MapMakerInternalMap.this.isEmpty();
    }
    
    public boolean contains(Object o) {
      return MapMakerInternalMap.this.containsValue(o);
    }
    
    public void clear() {
      MapMakerInternalMap.this.clear();
    }
    
    public Object[] toArray() {
      return MapMakerInternalMap.toArrayList(this).toArray();
    }
    
    public <T> T[] toArray(T[] a) {
      return (T[])MapMakerInternalMap.toArrayList(this).toArray((Object[])a);
    }
  }
  
  final class EntrySet extends SafeToArraySet<Map.Entry<K, V>> {
    public Iterator<Map.Entry<K, V>> iterator() {
      return new MapMakerInternalMap.EntryIterator(MapMakerInternalMap.this);
    }
    
    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object key = e.getKey();
      if (key == null)
        return false; 
      V v = (V)MapMakerInternalMap.this.get(key);
      return (v != null && MapMakerInternalMap.this.valueEquivalence().equivalent(e.getValue(), v));
    }
    
    public boolean remove(Object o) {
      if (!(o instanceof Map.Entry))
        return false; 
      Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
      Object key = e.getKey();
      return (key != null && MapMakerInternalMap.this.remove(key, e.getValue()));
    }
    
    public int size() {
      return MapMakerInternalMap.this.size();
    }
    
    public boolean isEmpty() {
      return MapMakerInternalMap.this.isEmpty();
    }
    
    public void clear() {
      MapMakerInternalMap.this.clear();
    }
  }
  
  private static abstract class SafeToArraySet<E> extends AbstractSet<E> {
    private SafeToArraySet() {}
    
    public Object[] toArray() {
      return MapMakerInternalMap.toArrayList(this).toArray();
    }
    
    public <T> T[] toArray(T[] a) {
      return (T[])MapMakerInternalMap.toArrayList(this).toArray((Object[])a);
    }
  }
  
  private static <E> ArrayList<E> toArrayList(Collection<E> c) {
    ArrayList<E> result = new ArrayList<>(c.size());
    Iterators.addAll(result, c.iterator());
    return result;
  }
  
  Object writeReplace() {
    return new SerializationProxy<>(this.entryHelper
        .keyStrength(), this.entryHelper
        .valueStrength(), this.keyEquivalence, this.entryHelper
        
        .valueStrength().defaultEquivalence(), this.concurrencyLevel, this);
  }
  
  @J2ktIncompatible
  private void readObject(ObjectInputStream in) throws InvalidObjectException {
    throw new InvalidObjectException("Use SerializationProxy");
  }
  
  static abstract class AbstractSerializationProxy<K, V> extends ForwardingConcurrentMap<K, V> implements Serializable {
    private static final long serialVersionUID = 3L;
    
    final MapMakerInternalMap.Strength keyStrength;
    
    final MapMakerInternalMap.Strength valueStrength;
    
    final Equivalence<Object> keyEquivalence;
    
    final Equivalence<Object> valueEquivalence;
    
    final int concurrencyLevel;
    
    transient ConcurrentMap<K, V> delegate;
    
    AbstractSerializationProxy(MapMakerInternalMap.Strength keyStrength, MapMakerInternalMap.Strength valueStrength, Equivalence<Object> keyEquivalence, Equivalence<Object> valueEquivalence, int concurrencyLevel, ConcurrentMap<K, V> delegate) {
      this.keyStrength = keyStrength;
      this.valueStrength = valueStrength;
      this.keyEquivalence = keyEquivalence;
      this.valueEquivalence = valueEquivalence;
      this.concurrencyLevel = concurrencyLevel;
      this.delegate = delegate;
    }
    
    protected ConcurrentMap<K, V> delegate() {
      return this.delegate;
    }
    
    void writeMapTo(ObjectOutputStream out) throws IOException {
      out.writeInt(this.delegate.size());
      for (Map.Entry<K, V> entry : this.delegate.entrySet()) {
        out.writeObject(entry.getKey());
        out.writeObject(entry.getValue());
      } 
      out.writeObject(null);
    }
    
    @J2ktIncompatible
    MapMaker readMapMaker(ObjectInputStream in) throws IOException {
      int size = in.readInt();
      return (new MapMaker())
        .initialCapacity(size)
        .setKeyStrength(this.keyStrength)
        .setValueStrength(this.valueStrength)
        .keyEquivalence(this.keyEquivalence)
        .concurrencyLevel(this.concurrencyLevel);
    }
    
    @J2ktIncompatible
    void readEntries(ObjectInputStream in) throws IOException, ClassNotFoundException {
      while (true) {
        K key = (K)in.readObject();
        if (key == null)
          break; 
        V value = (V)in.readObject();
        this.delegate.put(key, value);
      } 
    }
  }
  
  private static final class SerializationProxy<K, V> extends AbstractSerializationProxy<K, V> {
    private static final long serialVersionUID = 3L;
    
    SerializationProxy(MapMakerInternalMap.Strength keyStrength, MapMakerInternalMap.Strength valueStrength, Equivalence<Object> keyEquivalence, Equivalence<Object> valueEquivalence, int concurrencyLevel, ConcurrentMap<K, V> delegate) {
      super(keyStrength, valueStrength, keyEquivalence, valueEquivalence, concurrencyLevel, delegate);
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      writeMapTo(out);
    }
    
    @J2ktIncompatible
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      MapMaker mapMaker = readMapMaker(in);
      this.delegate = mapMaker.makeMap();
      readEntries(in);
    }
    
    private Object readResolve() {
      return this.delegate;
    }
  }
  
  static interface WeakValueReference<K, V, E extends InternalEntry<K, V, E>> {
    @CheckForNull
    V get();
    
    E getEntry();
    
    void clear();
    
    WeakValueReference<K, V, E> copyFor(ReferenceQueue<V> param1ReferenceQueue, E param1E);
  }
  
  static interface WeakValueEntry<K, V, E extends InternalEntry<K, V, E>> extends InternalEntry<K, V, E> {
    MapMakerInternalMap.WeakValueReference<K, V, E> getValueReference();
  }
  
  static interface StrongValueEntry<K, V, E extends InternalEntry<K, V, E>> extends InternalEntry<K, V, E> {}
  
  static interface InternalEntry<K, V, E extends InternalEntry<K, V, E>> {
    E getNext();
    
    int getHash();
    
    K getKey();
    
    V getValue();
  }
  
  static interface InternalEntryHelper<K, V, E extends InternalEntry<K, V, E>, S extends Segment<K, V, E, S>> {
    MapMakerInternalMap.Strength keyStrength();
    
    MapMakerInternalMap.Strength valueStrength();
    
    S newSegment(MapMakerInternalMap<K, V, E, S> param1MapMakerInternalMap, int param1Int);
    
    E newEntry(S param1S, K param1K, int param1Int, @CheckForNull E param1E);
    
    E copy(S param1S, E param1E1, @CheckForNull E param1E2);
    
    void setValue(S param1S, E param1E, V param1V);
  }
}
