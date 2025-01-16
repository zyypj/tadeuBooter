package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Comparator;
import java.util.SortedSet;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class SortedIterables {
  public static boolean hasSameComparator(Comparator<?> comparator, Iterable<?> elements) {
    Comparator<?> comparator2;
    Preconditions.checkNotNull(comparator);
    Preconditions.checkNotNull(elements);
    if (elements instanceof SortedSet) {
      comparator2 = comparator((SortedSet)elements);
    } else if (elements instanceof SortedIterable) {
      comparator2 = ((SortedIterable)elements).comparator();
    } else {
      return false;
    } 
    return comparator.equals(comparator2);
  }
  
  public static <E> Comparator<? super E> comparator(SortedSet<E> sortedSet) {
    Comparator<? super E> result = sortedSet.comparator();
    if (result == null)
      result = Ordering.natural(); 
    return result;
  }
}
