package me.syncwrld.booter.libs.google.guava.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
public final class ArrayListMultimap<K, V> extends ArrayListMultimapGwtSerializationDependencies<K, V> {
  private static final int DEFAULT_VALUES_PER_KEY = 3;
  
  @VisibleForTesting
  transient int expectedValuesPerKey;
  
  @GwtIncompatible
  @J2ktIncompatible
  private static final long serialVersionUID = 0L;
  
  public static <K, V> ArrayListMultimap<K, V> create() {
    return new ArrayListMultimap<>();
  }
  
  public static <K, V> ArrayListMultimap<K, V> create(int expectedKeys, int expectedValuesPerKey) {
    return new ArrayListMultimap<>(expectedKeys, expectedValuesPerKey);
  }
  
  public static <K, V> ArrayListMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
    return new ArrayListMultimap<>(multimap);
  }
  
  private ArrayListMultimap() {
    this(12, 3);
  }
  
  private ArrayListMultimap(int expectedKeys, int expectedValuesPerKey) {
    super(Platform.newHashMapWithExpectedSize(expectedKeys));
    CollectPreconditions.checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
    this.expectedValuesPerKey = expectedValuesPerKey;
  }
  
  private ArrayListMultimap(Multimap<? extends K, ? extends V> multimap) {
    this(multimap
        .keySet().size(), 
        (multimap instanceof ArrayListMultimap) ? 
        ((ArrayListMultimap)multimap).expectedValuesPerKey : 
        3);
    putAll(multimap);
  }
  
  List<V> createCollection() {
    return new ArrayList<>(this.expectedValuesPerKey);
  }
  
  @Deprecated
  public void trimToSize() {
    for (Collection<V> collection : backingMap().values()) {
      ArrayList<V> arrayList = (ArrayList<V>)collection;
      arrayList.trimToSize();
    } 
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    Serialization.writeMultimap(this, stream);
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    this.expectedValuesPerKey = 3;
    int distinctKeys = Serialization.readCount(stream);
    Map<K, Collection<V>> map = Maps.newHashMap();
    setMap(map);
    Serialization.populateMultimap(this, stream, distinctKeys);
  }
}
