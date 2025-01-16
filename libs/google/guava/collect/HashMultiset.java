package me.syncwrld.booter.libs.google.guava.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.function.ObjIntConsumer;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true, emulated = true)
public final class HashMultiset<E> extends AbstractMapBasedMultiset<E> {
  @GwtIncompatible
  @J2ktIncompatible
  private static final long serialVersionUID = 0L;
  
  public static <E> HashMultiset<E> create() {
    return new HashMultiset<>();
  }
  
  public static <E> HashMultiset<E> create(int distinctElements) {
    return new HashMultiset<>(distinctElements);
  }
  
  public static <E> HashMultiset<E> create(Iterable<? extends E> elements) {
    HashMultiset<E> multiset = create(Multisets.inferDistinctElements(elements));
    Iterables.addAll(multiset, elements);
    return multiset;
  }
  
  private HashMultiset() {
    super(new HashMap<>());
  }
  
  private HashMultiset(int distinctElements) {
    super(Maps.newHashMapWithExpectedSize(distinctElements));
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void writeObject(ObjectOutputStream stream) throws IOException {
    stream.defaultWriteObject();
    Serialization.writeMultiset(this, stream);
  }
  
  @GwtIncompatible
  @J2ktIncompatible
  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    int distinctElements = Serialization.readCount(stream);
    setBackingMap(Maps.newHashMap());
    Serialization.populateMultiset(this, stream, distinctElements);
  }
}
