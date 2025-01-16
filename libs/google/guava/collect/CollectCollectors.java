package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class CollectCollectors {
  private static final Collector<Object, ?, ImmutableList<Object>> TO_IMMUTABLE_LIST = Collector.of(ImmutableList::builder, ImmutableList.Builder::add, ImmutableList.Builder::combine, ImmutableList.Builder::build, new Collector.Characteristics[0]);
  
  private static final Collector<Object, ?, ImmutableSet<Object>> TO_IMMUTABLE_SET = Collector.of(ImmutableSet::builder, ImmutableSet.Builder::add, ImmutableSet.Builder::combine, ImmutableSet.Builder::build, new Collector.Characteristics[0]);
  
  @GwtIncompatible
  private static final Collector<Range<Comparable<?>>, ?, ImmutableRangeSet<Comparable<?>>> TO_IMMUTABLE_RANGE_SET = Collector.of(ImmutableRangeSet::builder, ImmutableRangeSet.Builder::add, ImmutableRangeSet.Builder::combine, ImmutableRangeSet.Builder::build, new Collector.Characteristics[0]);
  
  static <E> Collector<E, ?, ImmutableList<E>> toImmutableList() {
    return (Collector)TO_IMMUTABLE_LIST;
  }
  
  static <E> Collector<E, ?, ImmutableSet<E>> toImmutableSet() {
    return (Collector)TO_IMMUTABLE_SET;
  }
  
  static <E> Collector<E, ?, ImmutableSortedSet<E>> toImmutableSortedSet(Comparator<? super E> comparator) {
    Preconditions.checkNotNull(comparator);
    return Collector.of(() -> new ImmutableSortedSet.Builder(comparator), ImmutableSortedSet.Builder::add, ImmutableSortedSet.Builder::combine, ImmutableSortedSet.Builder::build, new Collector.Characteristics[0]);
  }
  
  static <E extends Enum<E>> Collector<E, ?, ImmutableSet<E>> toImmutableEnumSet() {
    return (Collector)EnumSetAccumulator.TO_IMMUTABLE_ENUM_SET;
  }
  
  private static <E extends Enum<E>> Collector<E, EnumSetAccumulator<E>, ImmutableSet<E>> toImmutableEnumSetGeneric() {
    return Collector.of(() -> new EnumSetAccumulator<>(), EnumSetAccumulator::add, EnumSetAccumulator::combine, EnumSetAccumulator::toImmutableSet, new Collector.Characteristics[] { Collector.Characteristics.UNORDERED });
  }
  
  private static final class EnumSetAccumulator<E extends Enum<E>> {
    private EnumSetAccumulator() {}
    
    static final Collector<Enum<?>, ?, ImmutableSet<? extends Enum<?>>> TO_IMMUTABLE_ENUM_SET = CollectCollectors.toImmutableEnumSetGeneric();
    
    @CheckForNull
    private EnumSet<E> set;
    
    void add(E e) {
      if (this.set == null) {
        this.set = EnumSet.of(e);
      } else {
        this.set.add(e);
      } 
    }
    
    EnumSetAccumulator<E> combine(EnumSetAccumulator<E> other) {
      if (this.set == null)
        return other; 
      if (other.set == null)
        return this; 
      this.set.addAll(other.set);
      return this;
    }
    
    ImmutableSet<E> toImmutableSet() {
      if (this.set == null)
        return ImmutableSet.of(); 
      ImmutableSet<E> ret = ImmutableEnumSet.asImmutable(this.set);
      this.set = null;
      return ret;
    }
  }
  
  @GwtIncompatible
  static <E extends Comparable<? super E>> Collector<Range<E>, ?, ImmutableRangeSet<E>> toImmutableRangeSet() {
    return (Collector)TO_IMMUTABLE_RANGE_SET;
  }
  
  static <T, E> Collector<T, ?, ImmutableMultiset<E>> toImmutableMultiset(Function<? super T, ? extends E> elementFunction, ToIntFunction<? super T> countFunction) {
    Preconditions.checkNotNull(elementFunction);
    Preconditions.checkNotNull(countFunction);
    return Collector.of(LinkedHashMultiset::create, (multiset, t) -> multiset.add(Preconditions.checkNotNull(elementFunction.apply(t)), countFunction.applyAsInt(t)), (multiset1, multiset2) -> {
          multiset1.addAll(multiset2);
          return multiset1;
        }multiset -> ImmutableMultiset.copyFromEntries(multiset.entrySet()), new Collector.Characteristics[0]);
  }
  
  static <T, E, M extends Multiset<E>> Collector<T, ?, M> toMultiset(Function<? super T, E> elementFunction, ToIntFunction<? super T> countFunction, Supplier<M> multisetSupplier) {
    Preconditions.checkNotNull(elementFunction);
    Preconditions.checkNotNull(countFunction);
    Preconditions.checkNotNull(multisetSupplier);
    return (Collector)Collector.of(multisetSupplier, (ms, t) -> ms.add(elementFunction.apply(t), countFunction.applyAsInt(t)), (ms1, ms2) -> {
          ms1.addAll(ms2);
          return ms1;
        }new Collector.Characteristics[0]);
  }
  
  static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    return Collector.of(Builder::new, (builder, input) -> builder.put(keyFunction.apply(input), valueFunction.apply(input)), ImmutableMap.Builder::combine, ImmutableMap.Builder::buildOrThrow, new Collector.Characteristics[0]);
  }
  
  static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    Preconditions.checkNotNull(mergeFunction);
    return Collectors.collectingAndThen(
        Collectors.toMap(keyFunction, valueFunction, mergeFunction, java.util.LinkedHashMap::new), ImmutableMap::copyOf);
  }
  
  static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(Comparator<? super K> comparator, Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    Preconditions.checkNotNull(comparator);
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    return Collector.of(() -> new ImmutableSortedMap.Builder<>(comparator), (builder, input) -> builder.put(keyFunction.apply(input), valueFunction.apply(input)), ImmutableSortedMap.Builder::combine, ImmutableSortedMap.Builder::buildOrThrow, new Collector.Characteristics[] { Collector.Characteristics.UNORDERED });
  }
  
  static <T, K, V> Collector<T, ?, ImmutableSortedMap<K, V>> toImmutableSortedMap(Comparator<? super K> comparator, Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
    Preconditions.checkNotNull(comparator);
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    Preconditions.checkNotNull(mergeFunction);
    return Collectors.collectingAndThen(
        Collectors.toMap(keyFunction, valueFunction, mergeFunction, () -> new TreeMap<>(comparator)), ImmutableSortedMap::copyOfSorted);
  }
  
  static <T, K, V> Collector<T, ?, ImmutableBiMap<K, V>> toImmutableBiMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    return Collector.of(Builder::new, (builder, input) -> builder.put(keyFunction.apply(input), valueFunction.apply(input)), ImmutableBiMap.Builder::combine, ImmutableBiMap.Builder::buildOrThrow, new Collector.Characteristics[0]);
  }
  
  @J2ktIncompatible
  static <T, K extends Enum<K>, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    return Collector.of(() -> new EnumMapAccumulator<>(()), (accum, t) -> {
          Enum enum_ = keyFunction.apply(t);
          V newValue = valueFunction.apply(t);
          accum.put((Enum)Preconditions.checkNotNull(enum_, "Null key for input %s", t), Preconditions.checkNotNull(newValue, "Null value for input %s", t));
        }EnumMapAccumulator::combine, EnumMapAccumulator::toImmutableMap, new Collector.Characteristics[] { Collector.Characteristics.UNORDERED });
  }
  
  @J2ktIncompatible
  static <T, K extends Enum<K>, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableEnumMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    Preconditions.checkNotNull(mergeFunction);
    return Collector.of(() -> new EnumMapAccumulator<>(mergeFunction), (accum, t) -> {
          Enum enum_ = keyFunction.apply(t);
          V newValue = valueFunction.apply(t);
          accum.put((Enum)Preconditions.checkNotNull(enum_, "Null key for input %s", t), Preconditions.checkNotNull(newValue, "Null value for input %s", t));
        }EnumMapAccumulator::combine, EnumMapAccumulator::toImmutableMap, new Collector.Characteristics[0]);
  }
  
  @J2ktIncompatible
  private static class EnumMapAccumulator<K extends Enum<K>, V> {
    private final BinaryOperator<V> mergeFunction;
    
    @CheckForNull
    private EnumMap<K, V> map = null;
    
    EnumMapAccumulator(BinaryOperator<V> mergeFunction) {
      this.mergeFunction = mergeFunction;
    }
    
    void put(K key, V value) {
      if (this.map == null) {
        this.map = new EnumMap<>(Collections.singletonMap(key, value));
      } else {
        this.map.merge(key, value, this.mergeFunction);
      } 
    }
    
    EnumMapAccumulator<K, V> combine(EnumMapAccumulator<K, V> other) {
      if (this.map == null)
        return other; 
      if (other.map == null)
        return this; 
      other.map.forEach(this::put);
      return this;
    }
    
    ImmutableMap<K, V> toImmutableMap() {
      return (this.map == null) ? ImmutableMap.<K, V>of() : ImmutableEnumMap.<K, V>asImmutable(this.map);
    }
  }
  
  @GwtIncompatible
  static <T, K extends Comparable<? super K>, V> Collector<T, ?, ImmutableRangeMap<K, V>> toImmutableRangeMap(Function<? super T, Range<K>> keyFunction, Function<? super T, ? extends V> valueFunction) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    return Collector.of(ImmutableRangeMap::builder, (builder, input) -> builder.put(keyFunction.apply(input), valueFunction.apply(input)), ImmutableRangeMap.Builder::combine, ImmutableRangeMap.Builder::build, new Collector.Characteristics[0]);
  }
  
  static <T, K, V> Collector<T, ?, ImmutableListMultimap<K, V>> toImmutableListMultimap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    Preconditions.checkNotNull(keyFunction, "keyFunction");
    Preconditions.checkNotNull(valueFunction, "valueFunction");
    return Collector.of(ImmutableListMultimap::builder, (builder, t) -> builder.put(keyFunction.apply(t), valueFunction.apply(t)), ImmutableListMultimap.Builder::combine, ImmutableListMultimap.Builder::build, new Collector.Characteristics[0]);
  }
  
  static <T, K, V> Collector<T, ?, ImmutableListMultimap<K, V>> flatteningToImmutableListMultimap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends Stream<? extends V>> valuesFunction) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valuesFunction);
    Objects.requireNonNull(MultimapBuilder.linkedHashKeys().arrayListValues());
    return Collectors.collectingAndThen(flatteningToMultimap(input -> Preconditions.checkNotNull(keyFunction.apply(input)), input -> ((Stream)valuesFunction.apply(input)).peek(Preconditions::checkNotNull), MultimapBuilder.linkedHashKeys().arrayListValues()::build), ImmutableListMultimap::copyOf);
  }
  
  static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> toImmutableSetMultimap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
    Preconditions.checkNotNull(keyFunction, "keyFunction");
    Preconditions.checkNotNull(valueFunction, "valueFunction");
    return Collector.of(ImmutableSetMultimap::builder, (builder, t) -> builder.put(keyFunction.apply(t), valueFunction.apply(t)), ImmutableSetMultimap.Builder::combine, ImmutableSetMultimap.Builder::build, new Collector.Characteristics[0]);
  }
  
  static <T, K, V> Collector<T, ?, ImmutableSetMultimap<K, V>> flatteningToImmutableSetMultimap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends Stream<? extends V>> valuesFunction) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valuesFunction);
    Objects.requireNonNull(MultimapBuilder.linkedHashKeys().linkedHashSetValues());
    return Collectors.collectingAndThen(flatteningToMultimap(input -> Preconditions.checkNotNull(keyFunction.apply(input)), input -> ((Stream)valuesFunction.apply(input)).peek(Preconditions::checkNotNull), MultimapBuilder.linkedHashKeys().linkedHashSetValues()::build), ImmutableSetMultimap::copyOf);
  }
  
  static <T, K, V, M extends Multimap<K, V>> Collector<T, ?, M> toMultimap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction, Supplier<M> multimapSupplier) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    Preconditions.checkNotNull(multimapSupplier);
    return (Collector)Collector.of(multimapSupplier, (multimap, input) -> multimap.put(keyFunction.apply(input), valueFunction.apply(input)), (multimap1, multimap2) -> {
          multimap1.putAll(multimap2);
          return multimap1;
        }new Collector.Characteristics[0]);
  }
  
  static <T, K, V, M extends Multimap<K, V>> Collector<T, ?, M> flatteningToMultimap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends Stream<? extends V>> valueFunction, Supplier<M> multimapSupplier) {
    Preconditions.checkNotNull(keyFunction);
    Preconditions.checkNotNull(valueFunction);
    Preconditions.checkNotNull(multimapSupplier);
    return (Collector)Collector.of(multimapSupplier, (multimap, input) -> {
          K key = keyFunction.apply(input);
          Collection<V> valuesForKey = multimap.get(key);
          Objects.requireNonNull(valuesForKey);
          ((Stream)valueFunction.apply(input)).forEachOrdered(valuesForKey::add);
        }(multimap1, multimap2) -> {
          multimap1.putAll(multimap2);
          return multimap1;
        }new Collector.Characteristics[0]);
  }
}
