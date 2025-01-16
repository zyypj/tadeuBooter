package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Map;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Predicate;

@ElementTypesAreNonnullByDefault
@GwtCompatible
interface FilteredMultimap<K, V> extends Multimap<K, V> {
  Multimap<K, V> unfiltered();
  
  Predicate<? super Map.Entry<K, V>> entryPredicate();
}
