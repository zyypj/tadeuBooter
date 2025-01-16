package me.syncwrld.booter.libs.google.guava.collect;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.base.MoreObjects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Predicate;
import me.syncwrld.booter.libs.google.guava.base.Predicates;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class TreeRangeMap<K extends Comparable, V> implements RangeMap<K, V> {
  private final NavigableMap<Cut<K>, RangeMapEntry<K, V>> entriesByLowerBound;
  
  public static <K extends Comparable, V> TreeRangeMap<K, V> create() {
    return new TreeRangeMap<>();
  }
  
  private TreeRangeMap() {
    this.entriesByLowerBound = Maps.newTreeMap();
  }
  
  private static final class RangeMapEntry<K extends Comparable, V> extends AbstractMapEntry<Range<K>, V> {
    private final Range<K> range;
    
    private final V value;
    
    RangeMapEntry(Cut<K> lowerBound, Cut<K> upperBound, V value) {
      this(Range.create(lowerBound, upperBound), value);
    }
    
    RangeMapEntry(Range<K> range, V value) {
      this.range = range;
      this.value = value;
    }
    
    public Range<K> getKey() {
      return this.range;
    }
    
    public V getValue() {
      return this.value;
    }
    
    public boolean contains(K value) {
      return this.range.contains(value);
    }
    
    Cut<K> getLowerBound() {
      return this.range.lowerBound;
    }
    
    Cut<K> getUpperBound() {
      return this.range.upperBound;
    }
  }
  
  @CheckForNull
  public V get(K key) {
    Map.Entry<Range<K>, V> entry = getEntry(key);
    return (entry == null) ? null : entry.getValue();
  }
  
  @CheckForNull
  public Map.Entry<Range<K>, V> getEntry(K key) {
    Map.Entry<Cut<K>, RangeMapEntry<K, V>> mapEntry = this.entriesByLowerBound.floorEntry(Cut.belowValue(key));
    if (mapEntry != null && ((RangeMapEntry)mapEntry.getValue()).contains(key))
      return mapEntry.getValue(); 
    return null;
  }
  
  public void put(Range<K> range, V value) {
    if (!range.isEmpty()) {
      Preconditions.checkNotNull(value);
      remove(range);
      this.entriesByLowerBound.put(range.lowerBound, new RangeMapEntry<>(range, value));
    } 
  }
  
  public void putCoalescing(Range<K> range, V value) {
    if (this.entriesByLowerBound.isEmpty()) {
      put(range, value);
      return;
    } 
    Range<K> coalescedRange = coalescedRange(range, (V)Preconditions.checkNotNull(value));
    put(coalescedRange, value);
  }
  
  private Range<K> coalescedRange(Range<K> range, V value) {
    Range<K> coalescedRange = range;
    Map.Entry<Cut<K>, RangeMapEntry<K, V>> lowerEntry = this.entriesByLowerBound.lowerEntry(range.lowerBound);
    coalescedRange = coalesce(coalescedRange, value, lowerEntry);
    Map.Entry<Cut<K>, RangeMapEntry<K, V>> higherEntry = this.entriesByLowerBound.floorEntry(range.upperBound);
    coalescedRange = coalesce(coalescedRange, value, higherEntry);
    return coalescedRange;
  }
  
  private static <K extends Comparable, V> Range<K> coalesce(Range<K> range, V value, @CheckForNull Map.Entry<Cut<K>, RangeMapEntry<K, V>> entry) {
    if (entry != null && ((RangeMapEntry)entry
      .getValue()).getKey().isConnected(range) && ((RangeMapEntry)entry
      .getValue()).getValue().equals(value))
      return range.span(((RangeMapEntry)entry.getValue()).getKey()); 
    return range;
  }
  
  public void putAll(RangeMap<K, ? extends V> rangeMap) {
    for (Map.Entry<Range<K>, ? extends V> entry : (Iterable<Map.Entry<Range<K>, ? extends V>>)rangeMap.asMapOfRanges().entrySet())
      put(entry.getKey(), entry.getValue()); 
  }
  
  public void clear() {
    this.entriesByLowerBound.clear();
  }
  
  public Range<K> span() {
    Map.Entry<Cut<K>, RangeMapEntry<K, V>> firstEntry = this.entriesByLowerBound.firstEntry();
    Map.Entry<Cut<K>, RangeMapEntry<K, V>> lastEntry = this.entriesByLowerBound.lastEntry();
    if (firstEntry == null || lastEntry == null)
      throw new NoSuchElementException(); 
    return Range.create(
        (((RangeMapEntry)firstEntry.getValue()).getKey()).lowerBound, (((RangeMapEntry)lastEntry.getValue()).getKey()).upperBound);
  }
  
  private void putRangeMapEntry(Cut<K> lowerBound, Cut<K> upperBound, V value) {
    this.entriesByLowerBound.put(lowerBound, new RangeMapEntry<>(lowerBound, upperBound, value));
  }
  
  public void remove(Range<K> rangeToRemove) {
    if (rangeToRemove.isEmpty())
      return; 
    Map.Entry<Cut<K>, RangeMapEntry<K, V>> mapEntryBelowToTruncate = this.entriesByLowerBound.lowerEntry(rangeToRemove.lowerBound);
    if (mapEntryBelowToTruncate != null) {
      RangeMapEntry<K, V> rangeMapEntry = mapEntryBelowToTruncate.getValue();
      if (rangeMapEntry.getUpperBound().compareTo(rangeToRemove.lowerBound) > 0) {
        if (rangeMapEntry.getUpperBound().compareTo(rangeToRemove.upperBound) > 0)
          putRangeMapEntry(rangeToRemove.upperBound, rangeMapEntry
              
              .getUpperBound(), (V)((RangeMapEntry)mapEntryBelowToTruncate
              .getValue()).getValue()); 
        putRangeMapEntry(rangeMapEntry
            .getLowerBound(), rangeToRemove.lowerBound, (V)((RangeMapEntry)mapEntryBelowToTruncate
            
            .getValue()).getValue());
      } 
    } 
    Map.Entry<Cut<K>, RangeMapEntry<K, V>> mapEntryAboveToTruncate = this.entriesByLowerBound.lowerEntry(rangeToRemove.upperBound);
    if (mapEntryAboveToTruncate != null) {
      RangeMapEntry<K, V> rangeMapEntry = mapEntryAboveToTruncate.getValue();
      if (rangeMapEntry.getUpperBound().compareTo(rangeToRemove.upperBound) > 0)
        putRangeMapEntry(rangeToRemove.upperBound, rangeMapEntry
            
            .getUpperBound(), (V)((RangeMapEntry)mapEntryAboveToTruncate
            .getValue()).getValue()); 
    } 
    this.entriesByLowerBound.subMap(rangeToRemove.lowerBound, rangeToRemove.upperBound).clear();
  }
  
  private void split(Cut<K> cut) {
    Map.Entry<Cut<K>, RangeMapEntry<K, V>> mapEntryToSplit = this.entriesByLowerBound.lowerEntry(cut);
    if (mapEntryToSplit == null)
      return; 
    RangeMapEntry<K, V> rangeMapEntry = mapEntryToSplit.getValue();
    if (rangeMapEntry.getUpperBound().compareTo(cut) <= 0)
      return; 
    putRangeMapEntry(rangeMapEntry.getLowerBound(), cut, rangeMapEntry.getValue());
    putRangeMapEntry(cut, rangeMapEntry.getUpperBound(), rangeMapEntry.getValue());
  }
  
  public void merge(Range<K> range, @CheckForNull V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    Preconditions.checkNotNull(range);
    Preconditions.checkNotNull(remappingFunction);
    if (range.isEmpty())
      return; 
    split(range.lowerBound);
    split(range.upperBound);
    Set<Map.Entry<Cut<K>, RangeMapEntry<K, V>>> entriesInMergeRange = this.entriesByLowerBound.subMap(range.lowerBound, range.upperBound).entrySet();
    ImmutableMap.Builder<Cut<K>, RangeMapEntry<K, V>> gaps = ImmutableMap.builder();
    if (value != null) {
      Iterator<Map.Entry<Cut<K>, RangeMapEntry<K, V>>> iterator = entriesInMergeRange.iterator();
      Cut<K> lowerBound = range.lowerBound;
      while (iterator.hasNext()) {
        RangeMapEntry<K, V> entry = (RangeMapEntry<K, V>)((Map.Entry)iterator.next()).getValue();
        Cut<K> upperBound = entry.getLowerBound();
        if (!lowerBound.equals(upperBound))
          gaps.put(lowerBound, new RangeMapEntry<>(lowerBound, upperBound, value)); 
        lowerBound = entry.getUpperBound();
      } 
      if (!lowerBound.equals(range.upperBound))
        gaps.put(lowerBound, new RangeMapEntry<>(lowerBound, range.upperBound, value)); 
    } 
    Iterator<Map.Entry<Cut<K>, RangeMapEntry<K, V>>> backingItr = entriesInMergeRange.iterator();
    while (backingItr.hasNext()) {
      Map.Entry<Cut<K>, RangeMapEntry<K, V>> entry = backingItr.next();
      V newValue = remappingFunction.apply((V)((RangeMapEntry)entry.getValue()).getValue(), value);
      if (newValue == null) {
        backingItr.remove();
        continue;
      } 
      entry.setValue(new RangeMapEntry<>(((RangeMapEntry)entry
            
            .getValue()).getLowerBound(), ((RangeMapEntry)entry.getValue()).getUpperBound(), newValue));
    } 
    this.entriesByLowerBound.putAll(gaps.build());
  }
  
  public Map<Range<K>, V> asMapOfRanges() {
    return new AsMapOfRanges(this.entriesByLowerBound.values());
  }
  
  public Map<Range<K>, V> asDescendingMapOfRanges() {
    return new AsMapOfRanges(this.entriesByLowerBound.descendingMap().values());
  }
  
  private final class AsMapOfRanges extends Maps.IteratorBasedAbstractMap<Range<K>, V> {
    final Iterable<Map.Entry<Range<K>, V>> entryIterable;
    
    AsMapOfRanges(Iterable<TreeRangeMap.RangeMapEntry<K, V>> entryIterable) {
      this.entryIterable = (Iterable)entryIterable;
    }
    
    public boolean containsKey(@CheckForNull Object key) {
      return (get(key) != null);
    }
    
    @CheckForNull
    public V get(@CheckForNull Object key) {
      if (key instanceof Range) {
        Range<?> range = (Range)key;
        TreeRangeMap.RangeMapEntry<K, V> rangeMapEntry = (TreeRangeMap.RangeMapEntry<K, V>)TreeRangeMap.this.entriesByLowerBound.get(range.lowerBound);
        if (rangeMapEntry != null && rangeMapEntry.getKey().equals(range))
          return rangeMapEntry.getValue(); 
      } 
      return null;
    }
    
    public int size() {
      return TreeRangeMap.this.entriesByLowerBound.size();
    }
    
    Iterator<Map.Entry<Range<K>, V>> entryIterator() {
      return this.entryIterable.iterator();
    }
  }
  
  public RangeMap<K, V> subRangeMap(Range<K> subRange) {
    if (subRange.equals(Range.all()))
      return this; 
    return new SubRangeMap(subRange);
  }
  
  private RangeMap<K, V> emptySubRangeMap() {
    return (RangeMap)EMPTY_SUB_RANGE_MAP;
  }
  
  private static final RangeMap<Comparable<?>, Object> EMPTY_SUB_RANGE_MAP = new RangeMap<Comparable<?>, Object>() {
      @CheckForNull
      public Object get(Comparable<?> key) {
        return null;
      }
      
      @CheckForNull
      public Map.Entry<Range<Comparable<?>>, Object> getEntry(Comparable<?> key) {
        return null;
      }
      
      public Range<Comparable<?>> span() {
        throw new NoSuchElementException();
      }
      
      public void put(Range<Comparable<?>> range, Object value) {
        Preconditions.checkNotNull(range);
        throw new IllegalArgumentException("Cannot insert range " + range + " into an empty subRangeMap");
      }
      
      public void putCoalescing(Range<Comparable<?>> range, Object value) {
        Preconditions.checkNotNull(range);
        throw new IllegalArgumentException("Cannot insert range " + range + " into an empty subRangeMap");
      }
      
      public void putAll(RangeMap<Comparable<?>, ? extends Object> rangeMap) {
        if (!rangeMap.asMapOfRanges().isEmpty())
          throw new IllegalArgumentException("Cannot putAll(nonEmptyRangeMap) into an empty subRangeMap"); 
      }
      
      public void clear() {}
      
      public void remove(Range<Comparable<?>> range) {
        Preconditions.checkNotNull(range);
      }
      
      public void merge(Range<Comparable<?>> range, @CheckForNull Object value, BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
        Preconditions.checkNotNull(range);
        throw new IllegalArgumentException("Cannot merge range " + range + " into an empty subRangeMap");
      }
      
      public Map<Range<Comparable<?>>, Object> asMapOfRanges() {
        return Collections.emptyMap();
      }
      
      public Map<Range<Comparable<?>>, Object> asDescendingMapOfRanges() {
        return Collections.emptyMap();
      }
      
      public RangeMap<Comparable<?>, Object> subRangeMap(Range<Comparable<?>> range) {
        Preconditions.checkNotNull(range);
        return this;
      }
    };
  
  private class SubRangeMap implements RangeMap<K, V> {
    private final Range<K> subRange;
    
    SubRangeMap(Range<K> subRange) {
      this.subRange = subRange;
    }
    
    @CheckForNull
    public V get(K key) {
      return this.subRange.contains(key) ? TreeRangeMap.this.get(key) : null;
    }
    
    @CheckForNull
    public Map.Entry<Range<K>, V> getEntry(K key) {
      if (this.subRange.contains(key)) {
        Map.Entry<Range<K>, V> entry = TreeRangeMap.this.getEntry(key);
        if (entry != null)
          return Maps.immutableEntry(((Range<K>)entry.getKey()).intersection(this.subRange), entry.getValue()); 
      } 
      return null;
    }
    
    public Range<K> span() {
      Cut<K> lowerBound, upperBound;
      Map.Entry<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> lowerEntry = TreeRangeMap.this.entriesByLowerBound.floorEntry(this.subRange.lowerBound);
      if (lowerEntry != null && ((TreeRangeMap.RangeMapEntry)lowerEntry
        .getValue()).getUpperBound().compareTo(this.subRange.lowerBound) > 0) {
        lowerBound = this.subRange.lowerBound;
      } else {
        lowerBound = (Cut<K>)TreeRangeMap.this.entriesByLowerBound.ceilingKey(this.subRange.lowerBound);
        if (lowerBound == null || lowerBound.compareTo(this.subRange.upperBound) >= 0)
          throw new NoSuchElementException(); 
      } 
      Map.Entry<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> upperEntry = TreeRangeMap.this.entriesByLowerBound.lowerEntry(this.subRange.upperBound);
      if (upperEntry == null)
        throw new NoSuchElementException(); 
      if (((TreeRangeMap.RangeMapEntry)upperEntry.getValue()).getUpperBound().compareTo(this.subRange.upperBound) >= 0) {
        upperBound = this.subRange.upperBound;
      } else {
        upperBound = ((TreeRangeMap.RangeMapEntry)upperEntry.getValue()).getUpperBound();
      } 
      return Range.create(lowerBound, upperBound);
    }
    
    public void put(Range<K> range, V value) {
      Preconditions.checkArgument(this.subRange
          .encloses(range), "Cannot put range %s into a subRangeMap(%s)", range, this.subRange);
      TreeRangeMap.this.put(range, value);
    }
    
    public void putCoalescing(Range<K> range, V value) {
      if (TreeRangeMap.this.entriesByLowerBound.isEmpty() || !this.subRange.encloses(range)) {
        put(range, value);
        return;
      } 
      Range<K> coalescedRange = TreeRangeMap.this.coalescedRange(range, (V)Preconditions.checkNotNull(value));
      put(coalescedRange.intersection(this.subRange), value);
    }
    
    public void putAll(RangeMap<K, ? extends V> rangeMap) {
      if (rangeMap.asMapOfRanges().isEmpty())
        return; 
      Range<K> span = rangeMap.span();
      Preconditions.checkArgument(this.subRange
          .encloses(span), "Cannot putAll rangeMap with span %s into a subRangeMap(%s)", span, this.subRange);
      TreeRangeMap.this.putAll(rangeMap);
    }
    
    public void clear() {
      TreeRangeMap.this.remove(this.subRange);
    }
    
    public void remove(Range<K> range) {
      if (range.isConnected(this.subRange))
        TreeRangeMap.this.remove(range.intersection(this.subRange)); 
    }
    
    public void merge(Range<K> range, @CheckForNull V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      Preconditions.checkArgument(this.subRange
          .encloses(range), "Cannot merge range %s into a subRangeMap(%s)", range, this.subRange);
      TreeRangeMap.this.merge(range, value, remappingFunction);
    }
    
    public RangeMap<K, V> subRangeMap(Range<K> range) {
      if (!range.isConnected(this.subRange))
        return TreeRangeMap.this.emptySubRangeMap(); 
      return TreeRangeMap.this.subRangeMap(range.intersection(this.subRange));
    }
    
    public Map<Range<K>, V> asMapOfRanges() {
      return new SubRangeMapAsMap();
    }
    
    public Map<Range<K>, V> asDescendingMapOfRanges() {
      return new SubRangeMapAsMap() {
          Iterator<Map.Entry<Range<K>, V>> entryIterator() {
            if (TreeRangeMap.SubRangeMap.this.subRange.isEmpty())
              return Iterators.emptyIterator(); 
            final Iterator<TreeRangeMap.RangeMapEntry<K, V>> backingItr = TreeRangeMap.this.entriesByLowerBound.headMap(TreeRangeMap.SubRangeMap.this.subRange.upperBound, false).descendingMap().values().iterator();
            return new AbstractIterator<Map.Entry<Range<K>, V>>() {
                @CheckForNull
                protected Map.Entry<Range<K>, V> computeNext() {
                  if (backingItr.hasNext()) {
                    TreeRangeMap.RangeMapEntry<K, V> entry = backingItr.next();
                    if (entry.getUpperBound().compareTo(TreeRangeMap.SubRangeMap.this.subRange.lowerBound) <= 0)
                      return endOfData(); 
                    return Maps.immutableEntry(entry.getKey().intersection(TreeRangeMap.SubRangeMap.this.subRange), entry.getValue());
                  } 
                  return endOfData();
                }
              };
          }
        };
    }
    
    public boolean equals(@CheckForNull Object o) {
      if (o instanceof RangeMap) {
        RangeMap<?, ?> rangeMap = (RangeMap<?, ?>)o;
        return asMapOfRanges().equals(rangeMap.asMapOfRanges());
      } 
      return false;
    }
    
    public int hashCode() {
      return asMapOfRanges().hashCode();
    }
    
    public String toString() {
      return asMapOfRanges().toString();
    }
    
    class SubRangeMapAsMap extends AbstractMap<Range<K>, V> {
      public boolean containsKey(@CheckForNull Object key) {
        return (get(key) != null);
      }
      
      @CheckForNull
      public V get(@CheckForNull Object key) {
        try {
          if (key instanceof Range) {
            Range<K> r = (Range<K>)key;
            if (!TreeRangeMap.SubRangeMap.this.subRange.encloses(r) || r.isEmpty())
              return null; 
            TreeRangeMap.RangeMapEntry<K, V> candidate = null;
            if (r.lowerBound.compareTo(TreeRangeMap.SubRangeMap.this.subRange.lowerBound) == 0) {
              Map.Entry<Cut<K>, TreeRangeMap.RangeMapEntry<K, V>> entry = TreeRangeMap.this.entriesByLowerBound.floorEntry(r.lowerBound);
              if (entry != null)
                candidate = entry.getValue(); 
            } else {
              candidate = (TreeRangeMap.RangeMapEntry<K, V>)TreeRangeMap.this.entriesByLowerBound.get(r.lowerBound);
            } 
            if (candidate != null && candidate
              .getKey().isConnected(TreeRangeMap.SubRangeMap.this.subRange) && candidate
              .getKey().intersection(TreeRangeMap.SubRangeMap.this.subRange).equals(r))
              return candidate.getValue(); 
          } 
        } catch (ClassCastException e) {
          return null;
        } 
        return null;
      }
      
      @CheckForNull
      public V remove(@CheckForNull Object key) {
        V value = get(key);
        if (value != null) {
          Range<K> range = (Range<K>)Objects.<Object>requireNonNull(key);
          TreeRangeMap.this.remove(range);
          return value;
        } 
        return null;
      }
      
      public void clear() {
        TreeRangeMap.SubRangeMap.this.clear();
      }
      
      private boolean removeEntryIf(Predicate<? super Map.Entry<Range<K>, V>> predicate) {
        List<Range<K>> toRemove = Lists.newArrayList();
        for (Map.Entry<Range<K>, V> entry : entrySet()) {
          if (predicate.apply(entry))
            toRemove.add(entry.getKey()); 
        } 
        for (Range<K> range : toRemove)
          TreeRangeMap.this.remove(range); 
        return !toRemove.isEmpty();
      }
      
      public Set<Range<K>> keySet() {
        return (Set)new Maps.KeySet<Range<Range<K>>, V>(this) {
            public boolean remove(@CheckForNull Object o) {
              return (TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.remove(o) != null);
            }
            
            public boolean retainAll(Collection<?> c) {
              return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(c)), Maps.keyFunction()));
            }
          };
      }
      
      public Set<Map.Entry<Range<K>, V>> entrySet() {
        return (Set)new Maps.EntrySet<Range<Range<K>>, V>() {
            Map<Range<K>, V> map() {
              return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this;
            }
            
            public Iterator<Map.Entry<Range<K>, V>> iterator() {
              return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.entryIterator();
            }
            
            public boolean retainAll(Collection<?> c) {
              return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.removeEntryIf(Predicates.not(Predicates.in(c)));
            }
            
            public int size() {
              return Iterators.size(iterator());
            }
            
            public boolean isEmpty() {
              return !iterator().hasNext();
            }
          };
      }
      
      Iterator<Map.Entry<Range<K>, V>> entryIterator() {
        if (TreeRangeMap.SubRangeMap.this.subRange.isEmpty())
          return Iterators.emptyIterator(); 
        Cut<K> cutToStart = (Cut<K>)MoreObjects.firstNonNull(TreeRangeMap.this
            .entriesByLowerBound.floorKey(TreeRangeMap.SubRangeMap.this.subRange.lowerBound), TreeRangeMap.SubRangeMap.this.subRange.lowerBound);
        final Iterator<TreeRangeMap.RangeMapEntry<K, V>> backingItr = TreeRangeMap.this.entriesByLowerBound.tailMap(cutToStart, true).values().iterator();
        return new AbstractIterator<Map.Entry<Range<K>, V>>() {
            @CheckForNull
            protected Map.Entry<Range<K>, V> computeNext() {
              while (backingItr.hasNext()) {
                TreeRangeMap.RangeMapEntry<K, V> entry = backingItr.next();
                if (entry.getLowerBound().compareTo(TreeRangeMap.SubRangeMap.this.subRange.upperBound) >= 0)
                  return endOfData(); 
                if (entry.getUpperBound().compareTo(TreeRangeMap.SubRangeMap.this.subRange.lowerBound) > 0)
                  return Maps.immutableEntry(entry.getKey().intersection(TreeRangeMap.SubRangeMap.this.subRange), entry.getValue()); 
              } 
              return endOfData();
            }
          };
      }
      
      public Collection<V> values() {
        return new Maps.Values<Range<Range<K>>, V>(this) {
            public boolean removeAll(Collection<?> c) {
              return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.in(c), Maps.valueFunction()));
            }
            
            public boolean retainAll(Collection<?> c) {
              return TreeRangeMap.SubRangeMap.SubRangeMapAsMap.this.removeEntryIf(Predicates.compose(Predicates.not(Predicates.in(c)), Maps.valueFunction()));
            }
          };
      }
    }
  }
  
  public boolean equals(@CheckForNull Object o) {
    if (o instanceof RangeMap) {
      RangeMap<?, ?> rangeMap = (RangeMap<?, ?>)o;
      return asMapOfRanges().equals(rangeMap.asMapOfRanges());
    } 
    return false;
  }
  
  public int hashCode() {
    return asMapOfRanges().hashCode();
  }
  
  public String toString() {
    return this.entriesByLowerBound.values().toString();
  }
}
