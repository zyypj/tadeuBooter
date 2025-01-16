package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import java.math.RoundingMode;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Function;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.math.IntMath;
import me.syncwrld.booter.libs.google.guava.primitives.Ints;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public final class Lists {
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayList() {
    return new ArrayList<>();
  }
  
  @SafeVarargs
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayList(E... elements) {
    Preconditions.checkNotNull(elements);
    int capacity = computeArrayListCapacity(elements.length);
    ArrayList<E> list = new ArrayList<>(capacity);
    Collections.addAll(list, elements);
    return list;
  }
  
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
    Preconditions.checkNotNull(elements);
    return (elements instanceof Collection) ? 
      new ArrayList<>((Collection<? extends E>)elements) : 
      newArrayList(elements.iterator());
  }
  
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
    ArrayList<E> list = newArrayList();
    Iterators.addAll(list, elements);
    return list;
  }
  
  @VisibleForTesting
  static int computeArrayListCapacity(int arraySize) {
    CollectPreconditions.checkNonnegative(arraySize, "arraySize");
    return Ints.saturatedCast(5L + arraySize + (arraySize / 10));
  }
  
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayListWithCapacity(int initialArraySize) {
    CollectPreconditions.checkNonnegative(initialArraySize, "initialArraySize");
    return new ArrayList<>(initialArraySize);
  }
  
  @GwtCompatible(serializable = true)
  public static <E> ArrayList<E> newArrayListWithExpectedSize(int estimatedSize) {
    return new ArrayList<>(computeArrayListCapacity(estimatedSize));
  }
  
  @GwtCompatible(serializable = true)
  public static <E> LinkedList<E> newLinkedList() {
    return new LinkedList<>();
  }
  
  @GwtCompatible(serializable = true)
  public static <E> LinkedList<E> newLinkedList(Iterable<? extends E> elements) {
    LinkedList<E> list = newLinkedList();
    Iterables.addAll(list, elements);
    return list;
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
    return new CopyOnWriteArrayList<>();
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterable<? extends E> elements) {
    Collection<? extends E> elementsCollection = (elements instanceof Collection) ? (Collection<? extends E>)elements : newArrayList(elements);
    return new CopyOnWriteArrayList<>(elementsCollection);
  }
  
  public static <E> List<E> asList(@ParametricNullness E first, E[] rest) {
    return new OnePlusArrayList<>(first, rest);
  }
  
  public static <E> List<E> asList(@ParametricNullness E first, @ParametricNullness E second, E[] rest) {
    return new TwoPlusArrayList<>(first, second, rest);
  }
  
  private static class OnePlusArrayList<E> extends AbstractList<E> implements Serializable, RandomAccess {
    @ParametricNullness
    final E first;
    
    final E[] rest;
    
    @J2ktIncompatible
    private static final long serialVersionUID = 0L;
    
    OnePlusArrayList(@ParametricNullness E first, E[] rest) {
      this.first = first;
      this.rest = (E[])Preconditions.checkNotNull(rest);
    }
    
    public int size() {
      return IntMath.saturatedAdd(this.rest.length, 1);
    }
    
    @ParametricNullness
    public E get(int index) {
      Preconditions.checkElementIndex(index, size());
      return (index == 0) ? this.first : this.rest[index - 1];
    }
  }
  
  private static class TwoPlusArrayList<E> extends AbstractList<E> implements Serializable, RandomAccess {
    @ParametricNullness
    final E first;
    
    @ParametricNullness
    final E second;
    
    final E[] rest;
    
    @J2ktIncompatible
    private static final long serialVersionUID = 0L;
    
    TwoPlusArrayList(@ParametricNullness E first, @ParametricNullness E second, E[] rest) {
      this.first = first;
      this.second = second;
      this.rest = (E[])Preconditions.checkNotNull(rest);
    }
    
    public int size() {
      return IntMath.saturatedAdd(this.rest.length, 2);
    }
    
    @ParametricNullness
    public E get(int index) {
      switch (index) {
        case 0:
          return this.first;
        case 1:
          return this.second;
      } 
      Preconditions.checkElementIndex(index, size());
      return this.rest[index - 2];
    }
  }
  
  public static <B> List<List<B>> cartesianProduct(List<? extends List<? extends B>> lists) {
    return CartesianList.create(lists);
  }
  
  @SafeVarargs
  public static <B> List<List<B>> cartesianProduct(List<? extends B>... lists) {
    return cartesianProduct(Arrays.asList(lists));
  }
  
  public static <F, T> List<T> transform(List<F> fromList, Function<? super F, ? extends T> function) {
    return (fromList instanceof RandomAccess) ? 
      new TransformingRandomAccessList<>(fromList, function) : 
      new TransformingSequentialList<>(fromList, function);
  }
  
  private static class TransformingSequentialList<F, T> extends AbstractSequentialList<T> implements Serializable {
    final List<F> fromList;
    
    final Function<? super F, ? extends T> function;
    
    private static final long serialVersionUID = 0L;
    
    TransformingSequentialList(List<F> fromList, Function<? super F, ? extends T> function) {
      this.fromList = (List<F>)Preconditions.checkNotNull(fromList);
      this.function = (Function<? super F, ? extends T>)Preconditions.checkNotNull(function);
    }
    
    protected void removeRange(int fromIndex, int toIndex) {
      this.fromList.subList(fromIndex, toIndex).clear();
    }
    
    public int size() {
      return this.fromList.size();
    }
    
    public ListIterator<T> listIterator(int index) {
      return new TransformedListIterator<F, T>(this.fromList.listIterator(index)) {
          @ParametricNullness
          T transform(@ParametricNullness F from) {
            return (T)Lists.TransformingSequentialList.this.function.apply(from);
          }
        };
    }
    
    public boolean removeIf(Predicate<? super T> filter) {
      Preconditions.checkNotNull(filter);
      return this.fromList.removeIf(element -> filter.test(this.function.apply(element)));
    }
  }
  
  private static class TransformingRandomAccessList<F, T> extends AbstractList<T> implements RandomAccess, Serializable {
    final List<F> fromList;
    
    final Function<? super F, ? extends T> function;
    
    private static final long serialVersionUID = 0L;
    
    TransformingRandomAccessList(List<F> fromList, Function<? super F, ? extends T> function) {
      this.fromList = (List<F>)Preconditions.checkNotNull(fromList);
      this.function = (Function<? super F, ? extends T>)Preconditions.checkNotNull(function);
    }
    
    protected void removeRange(int fromIndex, int toIndex) {
      this.fromList.subList(fromIndex, toIndex).clear();
    }
    
    @ParametricNullness
    public T get(int index) {
      return (T)this.function.apply(this.fromList.get(index));
    }
    
    public Iterator<T> iterator() {
      return listIterator();
    }
    
    public ListIterator<T> listIterator(int index) {
      return new TransformedListIterator<F, T>(this.fromList.listIterator(index)) {
          T transform(F from) {
            return (T)Lists.TransformingRandomAccessList.this.function.apply(from);
          }
        };
    }
    
    public boolean isEmpty() {
      return this.fromList.isEmpty();
    }
    
    public boolean removeIf(Predicate<? super T> filter) {
      Preconditions.checkNotNull(filter);
      return this.fromList.removeIf(element -> filter.test(this.function.apply(element)));
    }
    
    @ParametricNullness
    public T remove(int index) {
      return (T)this.function.apply(this.fromList.remove(index));
    }
    
    public int size() {
      return this.fromList.size();
    }
  }
  
  public static <T> List<List<T>> partition(List<T> list, int size) {
    Preconditions.checkNotNull(list);
    Preconditions.checkArgument((size > 0));
    return (list instanceof RandomAccess) ? 
      new RandomAccessPartition<>(list, size) : 
      new Partition<>(list, size);
  }
  
  private static class Partition<T> extends AbstractList<List<T>> {
    final List<T> list;
    
    final int size;
    
    Partition(List<T> list, int size) {
      this.list = list;
      this.size = size;
    }
    
    public List<T> get(int index) {
      Preconditions.checkElementIndex(index, size());
      int start = index * this.size;
      int end = Math.min(start + this.size, this.list.size());
      return this.list.subList(start, end);
    }
    
    public int size() {
      return IntMath.divide(this.list.size(), this.size, RoundingMode.CEILING);
    }
    
    public boolean isEmpty() {
      return this.list.isEmpty();
    }
  }
  
  private static class RandomAccessPartition<T> extends Partition<T> implements RandomAccess {
    RandomAccessPartition(List<T> list, int size) {
      super(list, size);
    }
  }
  
  public static ImmutableList<Character> charactersOf(String string) {
    return new StringAsImmutableList((String)Preconditions.checkNotNull(string));
  }
  
  public static List<Character> charactersOf(CharSequence sequence) {
    return new CharSequenceAsList((CharSequence)Preconditions.checkNotNull(sequence));
  }
  
  private static final class StringAsImmutableList extends ImmutableList<Character> {
    private final String string;
    
    StringAsImmutableList(String string) {
      this.string = string;
    }
    
    public int indexOf(@CheckForNull Object object) {
      return (object instanceof Character) ? this.string.indexOf(((Character)object).charValue()) : -1;
    }
    
    public int lastIndexOf(@CheckForNull Object object) {
      return (object instanceof Character) ? this.string.lastIndexOf(((Character)object).charValue()) : -1;
    }
    
    public ImmutableList<Character> subList(int fromIndex, int toIndex) {
      Preconditions.checkPositionIndexes(fromIndex, toIndex, size());
      return Lists.charactersOf(this.string.substring(fromIndex, toIndex));
    }
    
    boolean isPartialView() {
      return false;
    }
    
    public Character get(int index) {
      Preconditions.checkElementIndex(index, size());
      return Character.valueOf(this.string.charAt(index));
    }
    
    public int size() {
      return this.string.length();
    }
    
    @J2ktIncompatible
    @GwtIncompatible
    Object writeReplace() {
      return super.writeReplace();
    }
  }
  
  private static final class CharSequenceAsList extends AbstractList<Character> {
    private final CharSequence sequence;
    
    CharSequenceAsList(CharSequence sequence) {
      this.sequence = sequence;
    }
    
    public Character get(int index) {
      Preconditions.checkElementIndex(index, size());
      return Character.valueOf(this.sequence.charAt(index));
    }
    
    public int size() {
      return this.sequence.length();
    }
  }
  
  public static <T> List<T> reverse(List<T> list) {
    if (list instanceof ImmutableList) {
      List<?> reversed = ((ImmutableList)list).reverse();
      List<T> result = (List)reversed;
      return result;
    } 
    if (list instanceof ReverseList)
      return ((ReverseList<T>)list).getForwardList(); 
    if (list instanceof RandomAccess)
      return new RandomAccessReverseList<>(list); 
    return new ReverseList<>(list);
  }
  
  private static class ReverseList<T> extends AbstractList<T> {
    private final List<T> forwardList;
    
    ReverseList(List<T> forwardList) {
      this.forwardList = (List<T>)Preconditions.checkNotNull(forwardList);
    }
    
    List<T> getForwardList() {
      return this.forwardList;
    }
    
    private int reverseIndex(int index) {
      int size = size();
      Preconditions.checkElementIndex(index, size);
      return size - 1 - index;
    }
    
    private int reversePosition(int index) {
      int size = size();
      Preconditions.checkPositionIndex(index, size);
      return size - index;
    }
    
    public void add(int index, @ParametricNullness T element) {
      this.forwardList.add(reversePosition(index), element);
    }
    
    public void clear() {
      this.forwardList.clear();
    }
    
    @ParametricNullness
    public T remove(int index) {
      return this.forwardList.remove(reverseIndex(index));
    }
    
    protected void removeRange(int fromIndex, int toIndex) {
      subList(fromIndex, toIndex).clear();
    }
    
    @ParametricNullness
    public T set(int index, @ParametricNullness T element) {
      return this.forwardList.set(reverseIndex(index), element);
    }
    
    @ParametricNullness
    public T get(int index) {
      return this.forwardList.get(reverseIndex(index));
    }
    
    public int size() {
      return this.forwardList.size();
    }
    
    public List<T> subList(int fromIndex, int toIndex) {
      Preconditions.checkPositionIndexes(fromIndex, toIndex, size());
      return Lists.reverse(this.forwardList.subList(reversePosition(toIndex), reversePosition(fromIndex)));
    }
    
    public Iterator<T> iterator() {
      return listIterator();
    }
    
    public ListIterator<T> listIterator(int index) {
      int start = reversePosition(index);
      final ListIterator<T> forwardIterator = this.forwardList.listIterator(start);
      return new ListIterator<T>() {
          boolean canRemoveOrSet;
          
          public void add(@ParametricNullness T e) {
            forwardIterator.add(e);
            forwardIterator.previous();
            this.canRemoveOrSet = false;
          }
          
          public boolean hasNext() {
            return forwardIterator.hasPrevious();
          }
          
          public boolean hasPrevious() {
            return forwardIterator.hasNext();
          }
          
          @ParametricNullness
          public T next() {
            if (!hasNext())
              throw new NoSuchElementException(); 
            this.canRemoveOrSet = true;
            return forwardIterator.previous();
          }
          
          public int nextIndex() {
            return Lists.ReverseList.this.reversePosition(forwardIterator.nextIndex());
          }
          
          @ParametricNullness
          public T previous() {
            if (!hasPrevious())
              throw new NoSuchElementException(); 
            this.canRemoveOrSet = true;
            return forwardIterator.next();
          }
          
          public int previousIndex() {
            return nextIndex() - 1;
          }
          
          public void remove() {
            CollectPreconditions.checkRemove(this.canRemoveOrSet);
            forwardIterator.remove();
            this.canRemoveOrSet = false;
          }
          
          public void set(@ParametricNullness T e) {
            Preconditions.checkState(this.canRemoveOrSet);
            forwardIterator.set(e);
          }
        };
    }
  }
  
  private static class RandomAccessReverseList<T> extends ReverseList<T> implements RandomAccess {
    RandomAccessReverseList(List<T> forwardList) {
      super(forwardList);
    }
  }
  
  static int hashCodeImpl(List<?> list) {
    int hashCode = 1;
    for (Object o : list) {
      hashCode = 31 * hashCode + ((o == null) ? 0 : o.hashCode());
      hashCode = hashCode ^ 0xFFFFFFFF ^ 0xFFFFFFFF;
    } 
    return hashCode;
  }
  
  static boolean equalsImpl(List<?> thisList, @CheckForNull Object other) {
    if (other == Preconditions.checkNotNull(thisList))
      return true; 
    if (!(other instanceof List))
      return false; 
    List<?> otherList = (List)other;
    int size = thisList.size();
    if (size != otherList.size())
      return false; 
    if (thisList instanceof RandomAccess && otherList instanceof RandomAccess) {
      for (int i = 0; i < size; i++) {
        if (!Objects.equal(thisList.get(i), otherList.get(i)))
          return false; 
      } 
      return true;
    } 
    return Iterators.elementsEqual(thisList.iterator(), otherList.iterator());
  }
  
  static <E> boolean addAllImpl(List<E> list, int index, Iterable<? extends E> elements) {
    boolean changed = false;
    ListIterator<E> listIterator = list.listIterator(index);
    for (E e : elements) {
      listIterator.add(e);
      changed = true;
    } 
    return changed;
  }
  
  static int indexOfImpl(List<?> list, @CheckForNull Object element) {
    if (list instanceof RandomAccess)
      return indexOfRandomAccess(list, element); 
    ListIterator<?> listIterator = list.listIterator();
    while (listIterator.hasNext()) {
      if (Objects.equal(element, listIterator.next()))
        return listIterator.previousIndex(); 
    } 
    return -1;
  }
  
  private static int indexOfRandomAccess(List<?> list, @CheckForNull Object element) {
    int size = list.size();
    if (element == null) {
      for (int i = 0; i < size; i++) {
        if (list.get(i) == null)
          return i; 
      } 
    } else {
      for (int i = 0; i < size; i++) {
        if (element.equals(list.get(i)))
          return i; 
      } 
    } 
    return -1;
  }
  
  static int lastIndexOfImpl(List<?> list, @CheckForNull Object element) {
    if (list instanceof RandomAccess)
      return lastIndexOfRandomAccess(list, element); 
    ListIterator<?> listIterator = list.listIterator(list.size());
    while (listIterator.hasPrevious()) {
      if (Objects.equal(element, listIterator.previous()))
        return listIterator.nextIndex(); 
    } 
    return -1;
  }
  
  private static int lastIndexOfRandomAccess(List<?> list, @CheckForNull Object element) {
    if (element == null) {
      for (int i = list.size() - 1; i >= 0; i--) {
        if (list.get(i) == null)
          return i; 
      } 
    } else {
      for (int i = list.size() - 1; i >= 0; i--) {
        if (element.equals(list.get(i)))
          return i; 
      } 
    } 
    return -1;
  }
  
  static <E> ListIterator<E> listIteratorImpl(List<E> list, int index) {
    return (new AbstractListWrapper<>(list)).listIterator(index);
  }
  
  static <E> List<E> subListImpl(List<E> list, int fromIndex, int toIndex) {
    List<E> wrapper;
    if (list instanceof RandomAccess) {
      wrapper = new RandomAccessListWrapper<E>(list) {
          @J2ktIncompatible
          private static final long serialVersionUID = 0L;
          
          public ListIterator<E> listIterator(int index) {
            return this.backingList.listIterator(index);
          }
        };
    } else {
      wrapper = new AbstractListWrapper<E>(list) {
          @J2ktIncompatible
          private static final long serialVersionUID = 0L;
          
          public ListIterator<E> listIterator(int index) {
            return this.backingList.listIterator(index);
          }
        };
    } 
    return wrapper.subList(fromIndex, toIndex);
  }
  
  private static class AbstractListWrapper<E> extends AbstractList<E> {
    final List<E> backingList;
    
    AbstractListWrapper(List<E> backingList) {
      this.backingList = (List<E>)Preconditions.checkNotNull(backingList);
    }
    
    public void add(int index, @ParametricNullness E element) {
      this.backingList.add(index, element);
    }
    
    public boolean addAll(int index, Collection<? extends E> c) {
      return this.backingList.addAll(index, c);
    }
    
    @ParametricNullness
    public E get(int index) {
      return this.backingList.get(index);
    }
    
    @ParametricNullness
    public E remove(int index) {
      return this.backingList.remove(index);
    }
    
    @ParametricNullness
    public E set(int index, @ParametricNullness E element) {
      return this.backingList.set(index, element);
    }
    
    public boolean contains(@CheckForNull Object o) {
      return this.backingList.contains(o);
    }
    
    public int size() {
      return this.backingList.size();
    }
  }
  
  private static class RandomAccessListWrapper<E> extends AbstractListWrapper<E> implements RandomAccess {
    RandomAccessListWrapper(List<E> backingList) {
      super(backingList);
    }
  }
  
  static <T> List<T> cast(Iterable<T> iterable) {
    return (List<T>)iterable;
  }
}
