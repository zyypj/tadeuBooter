package me.syncwrld.booter.libs.google.guava.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
public class TreeMultimap<K, V> extends AbstractSortedKeySortedSetMultimap<K, V> {
  private transient Comparator<? super K> keyComparator;
  
  private transient Comparator<? super V> valueComparator;
  
  @GwtIncompatible
  @J2ktIncompatible
  private static final long serialVersionUID = 0L;
  
  public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create() {
    return new TreeMultimap<>(Ordering.natural(), Ordering.natural());
  }
  
  public static <K, V> TreeMultimap<K, V> create(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator) {
    return new TreeMultimap<>((Comparator<? super K>)Preconditions.checkNotNull(keyComparator), (Comparator<? super V>)Preconditions.checkNotNull(valueComparator));
  }
  
  public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
    return new TreeMultimap<>(Ordering.natural(), Ordering.natural(), multimap);
  }
  
  TreeMultimap(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator) {
    super(new TreeMap<>(keyComparator));
    this.keyComparator = keyComparator;
    this.valueComparator = valueComparator;
  }
  
  private TreeMultimap(Comparator<? super K> keyComparator, Comparator<? super V> valueComparator, Multimap<? extends K, ? extends V> multimap) {
    this(keyComparator, valueComparator);
    putAll(multimap);
  }
  
  Map<K, Collection<V>> createAsMap() {
    return createMaybeNavigableAsMap();
  }
  
  SortedSet<V> createCollection() {
    return new TreeSet<>(this.valueComparator);
  }
  
  Collection<V> createCollection(@ParametricNullness K key) {
    if (key == null)
      int i = keyComparator().compare(key, key); 
    return super.createCollection(key);
  }
  
  @Deprecated
  public Comparator<? super K> keyComparator() {
    return this.keyComparator;
  }
  
  public Comparator<? super V> valueComparator() {
    return this.valueComparator;
  }
  
  @GwtIncompatible
  public NavigableSet<V> get(@ParametricNullness K key) {
    return (NavigableSet<V>)super.get(key);
  }
  
  public NavigableSet<K> keySet() {
    return (NavigableSet<K>)super.keySet();
  }
  
  public NavigableMap<K, Collection<V>> asMap() {
    return (NavigableMap<K, Collection<V>>)super.asMap();
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    stream.writeObject(keyComparator());
    stream.writeObject(valueComparator());
    Serialization.writeMultimap(this, stream);
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    this.keyComparator = (Comparator<? super K>)Preconditions.checkNotNull(stream.readObject());
    this.valueComparator = (Comparator<? super V>)Preconditions.checkNotNull(stream.readObject());
    setMap(new TreeMap<>(this.keyComparator));
    Serialization.populateMultimap(this, stream);
  }
}
