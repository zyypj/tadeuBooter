package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Comparator;
import java.util.List;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Function;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class SortedLists {
  enum KeyPresentBehavior {
    ANY_PRESENT {
      <E> int resultIndex(Comparator<? super E> comparator, @ParametricNullness E key, List<? extends E> list, int foundIndex) {
        return foundIndex;
      }
    },
    LAST_PRESENT {
      <E> int resultIndex(Comparator<? super E> comparator, @ParametricNullness E key, List<? extends E> list, int foundIndex) {
        int lower = foundIndex;
        int upper = list.size() - 1;
        while (lower < upper) {
          int middle = lower + upper + 1 >>> 1;
          int c = comparator.compare(list.get(middle), key);
          if (c > 0) {
            upper = middle - 1;
            continue;
          } 
          lower = middle;
        } 
        return lower;
      }
    },
    FIRST_PRESENT {
      <E> int resultIndex(Comparator<? super E> comparator, @ParametricNullness E key, List<? extends E> list, int foundIndex) {
        int lower = 0;
        int upper = foundIndex;
        while (lower < upper) {
          int middle = lower + upper >>> 1;
          int c = comparator.compare(list.get(middle), key);
          if (c < 0) {
            lower = middle + 1;
            continue;
          } 
          upper = middle;
        } 
        return lower;
      }
    },
    FIRST_AFTER {
      public <E> int resultIndex(Comparator<? super E> comparator, @ParametricNullness E key, List<? extends E> list, int foundIndex) {
        return LAST_PRESENT.<E>resultIndex(comparator, key, list, foundIndex) + 1;
      }
    },
    LAST_BEFORE {
      public <E> int resultIndex(Comparator<? super E> comparator, @ParametricNullness E key, List<? extends E> list, int foundIndex) {
        return FIRST_PRESENT.<E>resultIndex(comparator, key, list, foundIndex) - 1;
      }
    };
    
    abstract <E> int resultIndex(Comparator<? super E> param1Comparator, @ParametricNullness E param1E, List<? extends E> param1List, int param1Int);
  }
  
  enum KeyAbsentBehavior {
    NEXT_LOWER {
      int resultIndex(int higherIndex) {
        return higherIndex - 1;
      }
    },
    NEXT_HIGHER {
      public int resultIndex(int higherIndex) {
        return higherIndex;
      }
    },
    INVERTED_INSERTION_INDEX {
      public int resultIndex(int higherIndex) {
        return higherIndex ^ 0xFFFFFFFF;
      }
    };
    
    abstract int resultIndex(int param1Int);
  }
  
  public static <E extends Comparable> int binarySearch(List<? extends E> list, E e, KeyPresentBehavior presentBehavior, KeyAbsentBehavior absentBehavior) {
    Preconditions.checkNotNull(e);
    return binarySearch(list, e, Ordering.natural(), presentBehavior, absentBehavior);
  }
  
  public static <E, K extends Comparable> int binarySearch(List<E> list, Function<? super E, K> keyFunction, K key, KeyPresentBehavior presentBehavior, KeyAbsentBehavior absentBehavior) {
    Preconditions.checkNotNull(key);
    return binarySearch(list, keyFunction, key, 
        Ordering.natural(), presentBehavior, absentBehavior);
  }
  
  public static <E, K> int binarySearch(List<E> list, Function<? super E, K> keyFunction, @ParametricNullness K key, Comparator<? super K> keyComparator, KeyPresentBehavior presentBehavior, KeyAbsentBehavior absentBehavior) {
    return binarySearch(
        Lists.transform(list, keyFunction), key, keyComparator, presentBehavior, absentBehavior);
  }
  
  public static <E> int binarySearch(List<? extends E> list, @ParametricNullness E key, Comparator<? super E> comparator, KeyPresentBehavior presentBehavior, KeyAbsentBehavior absentBehavior) {
    Preconditions.checkNotNull(comparator);
    Preconditions.checkNotNull(list);
    Preconditions.checkNotNull(presentBehavior);
    Preconditions.checkNotNull(absentBehavior);
    if (!(list instanceof java.util.RandomAccess))
      list = Lists.newArrayList(list); 
    int lower = 0;
    int upper = list.size() - 1;
    while (lower <= upper) {
      int middle = lower + upper >>> 1;
      int c = comparator.compare(key, list.get(middle));
      if (c < 0) {
        upper = middle - 1;
        continue;
      } 
      if (c > 0) {
        lower = middle + 1;
        continue;
      } 
      return lower + presentBehavior
        .<E>resultIndex(comparator, key, list
          .subList(lower, upper + 1), middle - lower);
    } 
    return absentBehavior.resultIndex(lower);
  }
}
