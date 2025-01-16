package me.syncwrld.booter.libs.google.guava.collect;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Predicate;
import me.syncwrld.booter.libs.google.guava.base.Predicates;
import me.syncwrld.booter.libs.google.j2objc.annotations.Weak;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class FilteredMultimapValues<K, V> extends AbstractCollection<V> {
  @Weak
  private final FilteredMultimap<K, V> multimap;
  
  FilteredMultimapValues(FilteredMultimap<K, V> multimap) {
    this.multimap = (FilteredMultimap<K, V>)Preconditions.checkNotNull(multimap);
  }
  
  public Iterator<V> iterator() {
    return Maps.valueIterator(this.multimap.entries().iterator());
  }
  
  public boolean contains(@CheckForNull Object o) {
    return this.multimap.containsValue(o);
  }
  
  public int size() {
    return this.multimap.size();
  }
  
  public boolean remove(@CheckForNull Object o) {
    Predicate<? super Map.Entry<K, V>> entryPredicate = this.multimap.entryPredicate();
    Iterator<Map.Entry<K, V>> unfilteredItr = this.multimap.unfiltered().entries().iterator();
    while (unfilteredItr.hasNext()) {
      Map.Entry<K, V> entry = unfilteredItr.next();
      if (entryPredicate.apply(entry) && Objects.equal(entry.getValue(), o)) {
        unfilteredItr.remove();
        return true;
      } 
    } 
    return false;
  }
  
  public boolean removeAll(Collection<?> c) {
    return Iterables.removeIf(this.multimap
        .unfiltered().entries(), 
        
        Predicates.and(this.multimap
          .entryPredicate(), Maps.valuePredicateOnEntries(Predicates.in(c))));
  }
  
  public boolean retainAll(Collection<?> c) {
    return Iterables.removeIf(this.multimap
        .unfiltered().entries(), 
        
        Predicates.and(this.multimap
          .entryPredicate(), 
          Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(c)))));
  }
  
  public void clear() {
    this.multimap.clear();
  }
}
