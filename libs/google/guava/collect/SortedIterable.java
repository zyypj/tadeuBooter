package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Comparator;
import java.util.Iterator;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
interface SortedIterable<T> extends Iterable<T> {
  Comparator<? super T> comparator();
  
  Iterator<T> iterator();
}
