package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public interface SortedMultiset<E> extends SortedMultisetBridge<E>, SortedIterable<E> {
  Comparator<? super E> comparator();
  
  @CheckForNull
  Multiset.Entry<E> firstEntry();
  
  @CheckForNull
  Multiset.Entry<E> lastEntry();
  
  @CheckForNull
  Multiset.Entry<E> pollFirstEntry();
  
  @CheckForNull
  Multiset.Entry<E> pollLastEntry();
  
  NavigableSet<E> elementSet();
  
  Set<Multiset.Entry<E>> entrySet();
  
  Iterator<E> iterator();
  
  SortedMultiset<E> descendingMultiset();
  
  SortedMultiset<E> headMultiset(@ParametricNullness E paramE, BoundType paramBoundType);
  
  SortedMultiset<E> subMultiset(@ParametricNullness E paramE1, BoundType paramBoundType1, @ParametricNullness E paramE2, BoundType paramBoundType2);
  
  SortedMultiset<E> tailMultiset(@ParametricNullness E paramE, BoundType paramBoundType);
}
