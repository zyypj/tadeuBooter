package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotCall;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Predicate;
import me.syncwrld.booter.libs.google.guava.base.Predicates;
import me.syncwrld.booter.libs.google.guava.math.IntMath;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public final class Sets {
  static abstract class ImprovedAbstractSet<E> extends AbstractSet<E> {
    public boolean removeAll(Collection<?> c) {
      return Sets.removeAllImpl(this, c);
    }
    
    public boolean retainAll(Collection<?> c) {
      return super.retainAll((Collection)Preconditions.checkNotNull(c));
    }
  }
  
  @GwtCompatible(serializable = true)
  public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(E anElement, E... otherElements) {
    return ImmutableEnumSet.asImmutable(EnumSet.of(anElement, otherElements));
  }
  
  @GwtCompatible(serializable = true)
  public static <E extends Enum<E>> ImmutableSet<E> immutableEnumSet(Iterable<E> elements) {
    if (elements instanceof ImmutableEnumSet)
      return (ImmutableEnumSet)elements; 
    if (elements instanceof Collection) {
      Collection<E> collection = (Collection<E>)elements;
      if (collection.isEmpty())
        return ImmutableSet.of(); 
      return ImmutableEnumSet.asImmutable(EnumSet.copyOf(collection));
    } 
    Iterator<E> itr = elements.iterator();
    if (itr.hasNext()) {
      EnumSet<E> enumSet = EnumSet.of(itr.next());
      Iterators.addAll(enumSet, itr);
      return ImmutableEnumSet.asImmutable(enumSet);
    } 
    return ImmutableSet.of();
  }
  
  public static <E extends Enum<E>> Collector<E, ?, ImmutableSet<E>> toImmutableEnumSet() {
    return CollectCollectors.toImmutableEnumSet();
  }
  
  public static <E extends Enum<E>> EnumSet<E> newEnumSet(Iterable<E> iterable, Class<E> elementType) {
    EnumSet<E> set = EnumSet.noneOf(elementType);
    Iterables.addAll(set, iterable);
    return set;
  }
  
  public static <E> HashSet<E> newHashSet() {
    return new HashSet<>();
  }
  
  public static <E> HashSet<E> newHashSet(E... elements) {
    HashSet<E> set = newHashSetWithExpectedSize(elements.length);
    Collections.addAll(set, elements);
    return set;
  }
  
  public static <E> HashSet<E> newHashSet(Iterable<? extends E> elements) {
    return (elements instanceof Collection) ? 
      new HashSet<>((Collection<? extends E>)elements) : 
      newHashSet(elements.iterator());
  }
  
  public static <E> HashSet<E> newHashSet(Iterator<? extends E> elements) {
    HashSet<E> set = newHashSet();
    Iterators.addAll(set, elements);
    return set;
  }
  
  public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize) {
    return new HashSet<>(Maps.capacity(expectedSize));
  }
  
  public static <E> Set<E> newConcurrentHashSet() {
    return Platform.newConcurrentHashSet();
  }
  
  public static <E> Set<E> newConcurrentHashSet(Iterable<? extends E> elements) {
    Set<E> set = newConcurrentHashSet();
    Iterables.addAll(set, elements);
    return set;
  }
  
  public static <E> LinkedHashSet<E> newLinkedHashSet() {
    return new LinkedHashSet<>();
  }
  
  public static <E> LinkedHashSet<E> newLinkedHashSet(Iterable<? extends E> elements) {
    if (elements instanceof Collection)
      return new LinkedHashSet<>((Collection<? extends E>)elements); 
    LinkedHashSet<E> set = newLinkedHashSet();
    Iterables.addAll(set, elements);
    return set;
  }
  
  public static <E> LinkedHashSet<E> newLinkedHashSetWithExpectedSize(int expectedSize) {
    return new LinkedHashSet<>(Maps.capacity(expectedSize));
  }
  
  public static <E extends Comparable> TreeSet<E> newTreeSet() {
    return new TreeSet<>();
  }
  
  public static <E extends Comparable> TreeSet<E> newTreeSet(Iterable<? extends E> elements) {
    TreeSet<E> set = newTreeSet();
    Iterables.addAll(set, elements);
    return set;
  }
  
  public static <E> TreeSet<E> newTreeSet(Comparator<? super E> comparator) {
    return new TreeSet<>((Comparator<? super E>)Preconditions.checkNotNull(comparator));
  }
  
  public static <E> Set<E> newIdentityHashSet() {
    return Collections.newSetFromMap(Maps.newIdentityHashMap());
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet() {
    return new CopyOnWriteArraySet<>();
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static <E> CopyOnWriteArraySet<E> newCopyOnWriteArraySet(Iterable<? extends E> elements) {
    Collection<? extends E> elementsCollection = (elements instanceof Collection) ? (Collection<? extends E>)elements : Lists.<E>newArrayList(elements);
    return new CopyOnWriteArraySet<>(elementsCollection);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  public static <E extends Enum<E>> EnumSet<E> complementOf(Collection<E> collection) {
    if (collection instanceof EnumSet)
      return EnumSet.complementOf((EnumSet<E>)collection); 
    Preconditions.checkArgument(
        !collection.isEmpty(), "collection is empty; use the other version of this method");
    Class<E> type = ((Enum<E>)collection.iterator().next()).getDeclaringClass();
    return makeComplementByHand(collection, type);
  }
  
  @GwtIncompatible
  public static <E extends Enum<E>> EnumSet<E> complementOf(Collection<E> collection, Class<E> type) {
    Preconditions.checkNotNull(collection);
    return (collection instanceof EnumSet) ? 
      EnumSet.<E>complementOf((EnumSet<E>)collection) : 
      makeComplementByHand(collection, type);
  }
  
  @GwtIncompatible
  private static <E extends Enum<E>> EnumSet<E> makeComplementByHand(Collection<E> collection, Class<E> type) {
    EnumSet<E> result = EnumSet.allOf(type);
    result.removeAll(collection);
    return result;
  }
  
  @Deprecated
  public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
    return Collections.newSetFromMap(map);
  }
  
  public static abstract class SetView<E> extends AbstractSet<E> {
    private SetView() {}
    
    public ImmutableSet<E> immutableCopy() {
      return ImmutableSet.copyOf(this);
    }
    
    @CanIgnoreReturnValue
    public <S extends Set<E>> S copyInto(S set) {
      set.addAll(this);
      return set;
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @DoNotCall("Always throws UnsupportedOperationException")
    public final boolean add(@ParametricNullness E e) {
      throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @DoNotCall("Always throws UnsupportedOperationException")
    public final boolean remove(@CheckForNull Object object) {
      throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @DoNotCall("Always throws UnsupportedOperationException")
    public final boolean addAll(Collection<? extends E> newElements) {
      throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @DoNotCall("Always throws UnsupportedOperationException")
    public final boolean removeAll(Collection<?> oldElements) {
      throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @DoNotCall("Always throws UnsupportedOperationException")
    public final boolean removeIf(Predicate<? super E> filter) {
      throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @CanIgnoreReturnValue
    @DoNotCall("Always throws UnsupportedOperationException")
    public final boolean retainAll(Collection<?> elementsToKeep) {
      throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @DoNotCall("Always throws UnsupportedOperationException")
    public final void clear() {
      throw new UnsupportedOperationException();
    }
    
    public abstract UnmodifiableIterator<E> iterator();
  }
  
  public static <E> SetView<E> union(final Set<? extends E> set1, final Set<? extends E> set2) {
    Preconditions.checkNotNull(set1, "set1");
    Preconditions.checkNotNull(set2, "set2");
    return new SetView<E>() {
        public int size() {
          int size = set1.size();
          for (E e : set2) {
            if (!set1.contains(e))
              size++; 
          } 
          return size;
        }
        
        public boolean isEmpty() {
          return (set1.isEmpty() && set2.isEmpty());
        }
        
        public UnmodifiableIterator<E> iterator() {
          return new AbstractIterator() {
              final Iterator<? extends E> itr1 = set1.iterator();
              
              final Iterator<? extends E> itr2 = set2.iterator();
              
              @CheckForNull
              protected E computeNext() {
                if (this.itr1.hasNext())
                  return this.itr1.next(); 
                while (this.itr2.hasNext()) {
                  E e = this.itr2.next();
                  if (!set1.contains(e))
                    return e; 
                } 
                return endOfData();
              }
            };
        }
        
        public Stream<E> stream() {
          return Stream.concat(set1.stream(), set2.stream().filter(e -> !set1.contains(e)));
        }
        
        public Stream<E> parallelStream() {
          return stream().parallel();
        }
        
        public boolean contains(@CheckForNull Object object) {
          return (set1.contains(object) || set2.contains(object));
        }
        
        public <S extends Set<E>> S copyInto(S set) {
          set.addAll(set1);
          set.addAll(set2);
          return set;
        }
        
        public ImmutableSet<E> immutableCopy() {
          ImmutableSet.Builder<E> builder = (new ImmutableSet.Builder<>()).addAll(set1).addAll(set2);
          return builder.build();
        }
      };
  }
  
  public static <E> SetView<E> intersection(final Set<E> set1, final Set<?> set2) {
    Preconditions.checkNotNull(set1, "set1");
    Preconditions.checkNotNull(set2, "set2");
    return new SetView<E>() {
        public UnmodifiableIterator<E> iterator() {
          return new AbstractIterator() {
              final Iterator<E> itr = set1.iterator();
              
              @CheckForNull
              protected E computeNext() {
                while (this.itr.hasNext()) {
                  E e = this.itr.next();
                  if (set2.contains(e))
                    return e; 
                } 
                return endOfData();
              }
            };
        }
        
        public Stream<E> stream() {
          Objects.requireNonNull(set2);
          return set1.stream().filter(set2::contains);
        }
        
        public Stream<E> parallelStream() {
          Objects.requireNonNull(set2);
          return set1.parallelStream().filter(set2::contains);
        }
        
        public int size() {
          int size = 0;
          for (E e : set1) {
            if (set2.contains(e))
              size++; 
          } 
          return size;
        }
        
        public boolean isEmpty() {
          return Collections.disjoint(set2, set1);
        }
        
        public boolean contains(@CheckForNull Object object) {
          return (set1.contains(object) && set2.contains(object));
        }
        
        public boolean containsAll(Collection<?> collection) {
          return (set1.containsAll(collection) && set2.containsAll(collection));
        }
      };
  }
  
  public static <E> SetView<E> difference(final Set<E> set1, final Set<?> set2) {
    Preconditions.checkNotNull(set1, "set1");
    Preconditions.checkNotNull(set2, "set2");
    return new SetView<E>() {
        public UnmodifiableIterator<E> iterator() {
          return new AbstractIterator() {
              final Iterator<E> itr = set1.iterator();
              
              @CheckForNull
              protected E computeNext() {
                while (this.itr.hasNext()) {
                  E e = this.itr.next();
                  if (!set2.contains(e))
                    return e; 
                } 
                return endOfData();
              }
            };
        }
        
        public Stream<E> stream() {
          return set1.stream().filter(e -> !set2.contains(e));
        }
        
        public Stream<E> parallelStream() {
          return set1.parallelStream().filter(e -> !set2.contains(e));
        }
        
        public int size() {
          int size = 0;
          for (E e : set1) {
            if (!set2.contains(e))
              size++; 
          } 
          return size;
        }
        
        public boolean isEmpty() {
          return set2.containsAll(set1);
        }
        
        public boolean contains(@CheckForNull Object element) {
          return (set1.contains(element) && !set2.contains(element));
        }
      };
  }
  
  public static <E> SetView<E> symmetricDifference(final Set<? extends E> set1, final Set<? extends E> set2) {
    Preconditions.checkNotNull(set1, "set1");
    Preconditions.checkNotNull(set2, "set2");
    return new SetView<E>() {
        public UnmodifiableIterator<E> iterator() {
          final Iterator<? extends E> itr1 = set1.iterator();
          final Iterator<? extends E> itr2 = set2.iterator();
          return new AbstractIterator() {
              @CheckForNull
              public E computeNext() {
                while (itr1.hasNext()) {
                  E elem1 = itr1.next();
                  if (!set2.contains(elem1))
                    return elem1; 
                } 
                while (itr2.hasNext()) {
                  E elem2 = itr2.next();
                  if (!set1.contains(elem2))
                    return elem2; 
                } 
                return endOfData();
              }
            };
        }
        
        public int size() {
          int size = 0;
          for (E e : set1) {
            if (!set2.contains(e))
              size++; 
          } 
          for (E e : set2) {
            if (!set1.contains(e))
              size++; 
          } 
          return size;
        }
        
        public boolean isEmpty() {
          return set1.equals(set2);
        }
        
        public boolean contains(@CheckForNull Object element) {
          return set1.contains(element) ^ set2.contains(element);
        }
      };
  }
  
  public static <E> Set<E> filter(Set<E> unfiltered, Predicate<? super E> predicate) {
    if (unfiltered instanceof SortedSet)
      return filter((SortedSet<E>)unfiltered, predicate); 
    if (unfiltered instanceof FilteredSet) {
      FilteredSet<E> filtered = (FilteredSet<E>)unfiltered;
      Predicate<E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
      return new FilteredSet<>((Set<E>)filtered.unfiltered, combinedPredicate);
    } 
    return new FilteredSet<>((Set<E>)Preconditions.checkNotNull(unfiltered), (Predicate<? super E>)Preconditions.checkNotNull(predicate));
  }
  
  public static <E> SortedSet<E> filter(SortedSet<E> unfiltered, Predicate<? super E> predicate) {
    if (unfiltered instanceof FilteredSet) {
      FilteredSet<E> filtered = (FilteredSet<E>)unfiltered;
      Predicate<E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
      return new FilteredSortedSet<>((SortedSet<E>)filtered.unfiltered, combinedPredicate);
    } 
    return new FilteredSortedSet<>((SortedSet<E>)Preconditions.checkNotNull(unfiltered), (Predicate<? super E>)Preconditions.checkNotNull(predicate));
  }
  
  @GwtIncompatible
  public static <E> NavigableSet<E> filter(NavigableSet<E> unfiltered, Predicate<? super E> predicate) {
    if (unfiltered instanceof FilteredSet) {
      FilteredSet<E> filtered = (FilteredSet<E>)unfiltered;
      Predicate<E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
      return new FilteredNavigableSet<>((NavigableSet<E>)filtered.unfiltered, combinedPredicate);
    } 
    return new FilteredNavigableSet<>((NavigableSet<E>)Preconditions.checkNotNull(unfiltered), (Predicate<? super E>)Preconditions.checkNotNull(predicate));
  }
  
  private static class FilteredSet<E> extends Collections2.FilteredCollection<E> implements Set<E> {
    FilteredSet(Set<E> unfiltered, Predicate<? super E> predicate) {
      super(unfiltered, predicate);
    }
    
    public boolean equals(@CheckForNull Object object) {
      return Sets.equalsImpl(this, object);
    }
    
    public int hashCode() {
      return Sets.hashCodeImpl(this);
    }
  }
  
  private static class FilteredSortedSet<E> extends FilteredSet<E> implements SortedSet<E> {
    FilteredSortedSet(SortedSet<E> unfiltered, Predicate<? super E> predicate) {
      super(unfiltered, predicate);
    }
    
    @CheckForNull
    public Comparator<? super E> comparator() {
      return ((SortedSet<E>)this.unfiltered).comparator();
    }
    
    public SortedSet<E> subSet(@ParametricNullness E fromElement, @ParametricNullness E toElement) {
      return new FilteredSortedSet(((SortedSet<E>)this.unfiltered)
          .subSet(fromElement, toElement), this.predicate);
    }
    
    public SortedSet<E> headSet(@ParametricNullness E toElement) {
      return new FilteredSortedSet(((SortedSet<E>)this.unfiltered).headSet(toElement), this.predicate);
    }
    
    public SortedSet<E> tailSet(@ParametricNullness E fromElement) {
      return new FilteredSortedSet(((SortedSet<E>)this.unfiltered).tailSet(fromElement), this.predicate);
    }
    
    @ParametricNullness
    public E first() {
      return Iterators.find(this.unfiltered.iterator(), this.predicate);
    }
    
    @ParametricNullness
    public E last() {
      SortedSet<E> sortedUnfiltered = (SortedSet<E>)this.unfiltered;
      while (true) {
        E element = sortedUnfiltered.last();
        if (this.predicate.apply(element))
          return element; 
        sortedUnfiltered = sortedUnfiltered.headSet(element);
      } 
    }
  }
  
  @GwtIncompatible
  private static class FilteredNavigableSet<E> extends FilteredSortedSet<E> implements NavigableSet<E> {
    FilteredNavigableSet(NavigableSet<E> unfiltered, Predicate<? super E> predicate) {
      super(unfiltered, predicate);
    }
    
    NavigableSet<E> unfiltered() {
      return (NavigableSet<E>)this.unfiltered;
    }
    
    @CheckForNull
    public E lower(@ParametricNullness E e) {
      return Iterators.find(unfiltered().headSet(e, false).descendingIterator(), this.predicate, null);
    }
    
    @CheckForNull
    public E floor(@ParametricNullness E e) {
      return Iterators.find(unfiltered().headSet(e, true).descendingIterator(), this.predicate, null);
    }
    
    @CheckForNull
    public E ceiling(@ParametricNullness E e) {
      return Iterables.find(unfiltered().tailSet(e, true), this.predicate, null);
    }
    
    @CheckForNull
    public E higher(@ParametricNullness E e) {
      return Iterables.find(unfiltered().tailSet(e, false), this.predicate, null);
    }
    
    @CheckForNull
    public E pollFirst() {
      return Iterables.removeFirstMatching(unfiltered(), this.predicate);
    }
    
    @CheckForNull
    public E pollLast() {
      return Iterables.removeFirstMatching(unfiltered().descendingSet(), this.predicate);
    }
    
    public NavigableSet<E> descendingSet() {
      return Sets.filter(unfiltered().descendingSet(), this.predicate);
    }
    
    public Iterator<E> descendingIterator() {
      return Iterators.filter(unfiltered().descendingIterator(), this.predicate);
    }
    
    @ParametricNullness
    public E last() {
      return Iterators.find(unfiltered().descendingIterator(), this.predicate);
    }
    
    public NavigableSet<E> subSet(@ParametricNullness E fromElement, boolean fromInclusive, @ParametricNullness E toElement, boolean toInclusive) {
      return Sets.filter(
          unfiltered().subSet(fromElement, fromInclusive, toElement, toInclusive), this.predicate);
    }
    
    public NavigableSet<E> headSet(@ParametricNullness E toElement, boolean inclusive) {
      return Sets.filter(unfiltered().headSet(toElement, inclusive), this.predicate);
    }
    
    public NavigableSet<E> tailSet(@ParametricNullness E fromElement, boolean inclusive) {
      return Sets.filter(unfiltered().tailSet(fromElement, inclusive), this.predicate);
    }
  }
  
  public static <B> Set<List<B>> cartesianProduct(List<? extends Set<? extends B>> sets) {
    return CartesianSet.create(sets);
  }
  
  @SafeVarargs
  public static <B> Set<List<B>> cartesianProduct(Set<? extends B>... sets) {
    return cartesianProduct(Arrays.asList(sets));
  }
  
  private static final class CartesianSet<E> extends ForwardingCollection<List<E>> implements Set<List<E>> {
    private final transient ImmutableList<ImmutableSet<E>> axes;
    
    private final transient CartesianList<E> delegate;
    
    static <E> Set<List<E>> create(List<? extends Set<? extends E>> sets) {
      ImmutableList.Builder<ImmutableSet<E>> axesBuilder = new ImmutableList.Builder<>(sets.size());
      for (Set<? extends E> set : sets) {
        ImmutableSet<E> copy = ImmutableSet.copyOf(set);
        if (copy.isEmpty())
          return ImmutableSet.of(); 
        axesBuilder.add(copy);
      } 
      final ImmutableList<ImmutableSet<E>> axes = axesBuilder.build();
      ImmutableList<List<E>> listAxes = (ImmutableList)new ImmutableList<List<List<E>>>() {
          public int size() {
            return axes.size();
          }
          
          public List<E> get(int index) {
            return ((ImmutableSet<E>)axes.get(index)).asList();
          }
          
          boolean isPartialView() {
            return true;
          }
          
          @J2ktIncompatible
          @GwtIncompatible
          Object writeReplace() {
            return super.writeReplace();
          }
        };
      return new CartesianSet<>(axes, new CartesianList<>(listAxes));
    }
    
    private CartesianSet(ImmutableList<ImmutableSet<E>> axes, CartesianList<E> delegate) {
      this.axes = axes;
      this.delegate = delegate;
    }
    
    protected Collection<List<E>> delegate() {
      return this.delegate;
    }
    
    public boolean contains(@CheckForNull Object object) {
      if (!(object instanceof List))
        return false; 
      List<?> list = (List)object;
      if (list.size() != this.axes.size())
        return false; 
      int i = 0;
      for (Object o : list) {
        if (!((ImmutableSet)this.axes.get(i)).contains(o))
          return false; 
        i++;
      } 
      return true;
    }
    
    public boolean equals(@CheckForNull Object object) {
      if (object instanceof CartesianSet) {
        CartesianSet<?> that = (CartesianSet)object;
        return this.axes.equals(that.axes);
      } 
      if (object instanceof Set) {
        Set<?> that = (Set)object;
        return (size() == that.size() && containsAll(that));
      } 
      return false;
    }
    
    public int hashCode() {
      int adjust = size() - 1;
      for (int i = 0; i < this.axes.size(); i++) {
        adjust *= 31;
        adjust = adjust ^ 0xFFFFFFFF ^ 0xFFFFFFFF;
      } 
      int hash = 1;
      for (UnmodifiableIterator<ImmutableSet<E>> unmodifiableIterator = this.axes.iterator(); unmodifiableIterator.hasNext(); ) {
        Set<E> axis = unmodifiableIterator.next();
        hash = 31 * hash + size() / axis.size() * axis.hashCode();
        hash = hash ^ 0xFFFFFFFF ^ 0xFFFFFFFF;
      } 
      hash += adjust;
      return hash ^ 0xFFFFFFFF ^ 0xFFFFFFFF;
    }
  }
  
  @GwtCompatible(serializable = false)
  public static <E> Set<Set<E>> powerSet(Set<E> set) {
    return new PowerSet<>(set);
  }
  
  private static final class SubSet<E> extends AbstractSet<E> {
    private final ImmutableMap<E, Integer> inputSet;
    
    private final int mask;
    
    SubSet(ImmutableMap<E, Integer> inputSet, int mask) {
      this.inputSet = inputSet;
      this.mask = mask;
    }
    
    public Iterator<E> iterator() {
      return new UnmodifiableIterator<E>() {
          final ImmutableList<E> elements = Sets.SubSet.this.inputSet.keySet().asList();
          
          int remainingSetBits = Sets.SubSet.this.mask;
          
          public boolean hasNext() {
            return (this.remainingSetBits != 0);
          }
          
          public E next() {
            int index = Integer.numberOfTrailingZeros(this.remainingSetBits);
            if (index == 32)
              throw new NoSuchElementException(); 
            this.remainingSetBits &= 1 << index ^ 0xFFFFFFFF;
            return this.elements.get(index);
          }
        };
    }
    
    public int size() {
      return Integer.bitCount(this.mask);
    }
    
    public boolean contains(@CheckForNull Object o) {
      Integer index = this.inputSet.get(o);
      return (index != null && (this.mask & 1 << index.intValue()) != 0);
    }
  }
  
  private static final class PowerSet<E> extends AbstractSet<Set<E>> {
    final ImmutableMap<E, Integer> inputSet;
    
    PowerSet(Set<E> input) {
      Preconditions.checkArgument(
          (input.size() <= 30), "Too many elements to create power set: %s > 30", input.size());
      this.inputSet = Maps.indexMap(input);
    }
    
    public int size() {
      return 1 << this.inputSet.size();
    }
    
    public boolean isEmpty() {
      return false;
    }
    
    public Iterator<Set<E>> iterator() {
      return (Iterator)new AbstractIndexedListIterator<Set<Set<E>>>(size()) {
          protected Set<E> get(int setBits) {
            return new Sets.SubSet<>(Sets.PowerSet.this.inputSet, setBits);
          }
        };
    }
    
    public boolean contains(@CheckForNull Object obj) {
      if (obj instanceof Set) {
        Set<?> set = (Set)obj;
        return this.inputSet.keySet().containsAll(set);
      } 
      return false;
    }
    
    public boolean equals(@CheckForNull Object obj) {
      if (obj instanceof PowerSet) {
        PowerSet<?> that = (PowerSet)obj;
        return this.inputSet.keySet().equals(that.inputSet.keySet());
      } 
      return super.equals(obj);
    }
    
    public int hashCode() {
      return this.inputSet.keySet().hashCode() << this.inputSet.size() - 1;
    }
    
    public String toString() {
      return "powerSet(" + this.inputSet + ")";
    }
  }
  
  public static <E> Set<Set<E>> combinations(Set<E> set, final int size) {
    final ImmutableMap<E, Integer> index = Maps.indexMap(set);
    CollectPreconditions.checkNonnegative(size, "size");
    Preconditions.checkArgument((size <= index.size()), "size (%s) must be <= set.size() (%s)", size, index.size());
    if (size == 0)
      return ImmutableSet.of(ImmutableSet.of()); 
    if (size == index.size())
      return ImmutableSet.of(index.keySet()); 
    return (Set)new AbstractSet<Set<Set<E>>>() {
        public boolean contains(@CheckForNull Object o) {
          if (o instanceof Set) {
            Set<?> s = (Set)o;
            return (s.size() == size && index.keySet().containsAll(s));
          } 
          return false;
        }
        
        public Iterator<Set<E>> iterator() {
          return new AbstractIterator() {
              final BitSet bits = new BitSet(index.size());
              
              @CheckForNull
              protected Set<E> computeNext() {
                if (this.bits.isEmpty()) {
                  this.bits.set(0, size);
                } else {
                  int firstSetBit = this.bits.nextSetBit(0);
                  int bitToFlip = this.bits.nextClearBit(firstSetBit);
                  if (bitToFlip == index.size())
                    return endOfData(); 
                  this.bits.set(0, bitToFlip - firstSetBit - 1);
                  this.bits.clear(bitToFlip - firstSetBit - 1, bitToFlip);
                  this.bits.set(bitToFlip);
                } 
                final BitSet copy = (BitSet)this.bits.clone();
                return new AbstractSet<E>() {
                    public boolean contains(@CheckForNull Object o) {
                      Integer i = (Integer)index.get(o);
                      return (i != null && copy.get(i.intValue()));
                    }
                    
                    public Iterator<E> iterator() {
                      return new AbstractIterator() {
                          int i = -1;
                          
                          @CheckForNull
                          protected E computeNext() {
                            this.i = copy.nextSetBit(this.i + 1);
                            if (this.i == -1)
                              return endOfData(); 
                            return index.keySet().asList().get(this.i);
                          }
                        };
                    }
                    
                    public int size() {
                      return size;
                    }
                  };
              }
            };
        }
        
        public int size() {
          return IntMath.binomial(index.size(), size);
        }
        
        public String toString() {
          return "Sets.combinations(" + index.keySet() + ", " + size + ")";
        }
      };
  }
  
  static int hashCodeImpl(Set<?> s) {
    int hashCode = 0;
    for (Object o : s) {
      hashCode += (o != null) ? o.hashCode() : 0;
      hashCode = hashCode ^ 0xFFFFFFFF ^ 0xFFFFFFFF;
    } 
    return hashCode;
  }
  
  static boolean equalsImpl(Set<?> s, @CheckForNull Object object) {
    if (s == object)
      return true; 
    if (object instanceof Set) {
      Set<?> o = (Set)object;
      try {
        return (s.size() == o.size() && s.containsAll(o));
      } catch (NullPointerException|ClassCastException ignored) {
        return false;
      } 
    } 
    return false;
  }
  
  public static <E> NavigableSet<E> unmodifiableNavigableSet(NavigableSet<E> set) {
    if (set instanceof ImmutableCollection || set instanceof UnmodifiableNavigableSet)
      return set; 
    return new UnmodifiableNavigableSet<>(set);
  }
  
  static final class UnmodifiableNavigableSet<E> extends ForwardingSortedSet<E> implements NavigableSet<E>, Serializable {
    private final NavigableSet<E> delegate;
    
    private final SortedSet<E> unmodifiableDelegate;
    
    @LazyInit
    @CheckForNull
    private transient UnmodifiableNavigableSet<E> descendingSet;
    
    private static final long serialVersionUID = 0L;
    
    UnmodifiableNavigableSet(NavigableSet<E> delegate) {
      this.delegate = (NavigableSet<E>)Preconditions.checkNotNull(delegate);
      this.unmodifiableDelegate = Collections.unmodifiableSortedSet(delegate);
    }
    
    protected SortedSet<E> delegate() {
      return this.unmodifiableDelegate;
    }
    
    public boolean removeIf(Predicate<? super E> filter) {
      throw new UnsupportedOperationException();
    }
    
    public Stream<E> stream() {
      return this.delegate.stream();
    }
    
    public Stream<E> parallelStream() {
      return this.delegate.parallelStream();
    }
    
    public void forEach(Consumer<? super E> action) {
      this.delegate.forEach(action);
    }
    
    @CheckForNull
    public E lower(@ParametricNullness E e) {
      return this.delegate.lower(e);
    }
    
    @CheckForNull
    public E floor(@ParametricNullness E e) {
      return this.delegate.floor(e);
    }
    
    @CheckForNull
    public E ceiling(@ParametricNullness E e) {
      return this.delegate.ceiling(e);
    }
    
    @CheckForNull
    public E higher(@ParametricNullness E e) {
      return this.delegate.higher(e);
    }
    
    @CheckForNull
    public E pollFirst() {
      throw new UnsupportedOperationException();
    }
    
    @CheckForNull
    public E pollLast() {
      throw new UnsupportedOperationException();
    }
    
    public NavigableSet<E> descendingSet() {
      UnmodifiableNavigableSet<E> result = this.descendingSet;
      if (result == null) {
        result = this.descendingSet = new UnmodifiableNavigableSet(this.delegate.descendingSet());
        result.descendingSet = this;
      } 
      return result;
    }
    
    public Iterator<E> descendingIterator() {
      return Iterators.unmodifiableIterator(this.delegate.descendingIterator());
    }
    
    public NavigableSet<E> subSet(@ParametricNullness E fromElement, boolean fromInclusive, @ParametricNullness E toElement, boolean toInclusive) {
      return Sets.unmodifiableNavigableSet(this.delegate
          .subSet(fromElement, fromInclusive, toElement, toInclusive));
    }
    
    public NavigableSet<E> headSet(@ParametricNullness E toElement, boolean inclusive) {
      return Sets.unmodifiableNavigableSet(this.delegate.headSet(toElement, inclusive));
    }
    
    public NavigableSet<E> tailSet(@ParametricNullness E fromElement, boolean inclusive) {
      return Sets.unmodifiableNavigableSet(this.delegate.tailSet(fromElement, inclusive));
    }
  }
  
  @GwtIncompatible
  public static <E> NavigableSet<E> synchronizedNavigableSet(NavigableSet<E> navigableSet) {
    return Synchronized.navigableSet(navigableSet);
  }
  
  static boolean removeAllImpl(Set<?> set, Iterator<?> iterator) {
    boolean changed = false;
    while (iterator.hasNext())
      changed |= set.remove(iterator.next()); 
    return changed;
  }
  
  static boolean removeAllImpl(Set<?> set, Collection<?> collection) {
    Preconditions.checkNotNull(collection);
    if (collection instanceof Multiset)
      collection = ((Multiset)collection).elementSet(); 
    if (collection instanceof Set && collection.size() > set.size())
      return Iterators.removeAll(set.iterator(), collection); 
    return removeAllImpl(set, collection.iterator());
  }
  
  @GwtIncompatible
  static class DescendingSet<E> extends ForwardingNavigableSet<E> {
    private final NavigableSet<E> forward;
    
    DescendingSet(NavigableSet<E> forward) {
      this.forward = forward;
    }
    
    protected NavigableSet<E> delegate() {
      return this.forward;
    }
    
    @CheckForNull
    public E lower(@ParametricNullness E e) {
      return this.forward.higher(e);
    }
    
    @CheckForNull
    public E floor(@ParametricNullness E e) {
      return this.forward.ceiling(e);
    }
    
    @CheckForNull
    public E ceiling(@ParametricNullness E e) {
      return this.forward.floor(e);
    }
    
    @CheckForNull
    public E higher(@ParametricNullness E e) {
      return this.forward.lower(e);
    }
    
    @CheckForNull
    public E pollFirst() {
      return this.forward.pollLast();
    }
    
    @CheckForNull
    public E pollLast() {
      return this.forward.pollFirst();
    }
    
    public NavigableSet<E> descendingSet() {
      return this.forward;
    }
    
    public Iterator<E> descendingIterator() {
      return this.forward.iterator();
    }
    
    public NavigableSet<E> subSet(@ParametricNullness E fromElement, boolean fromInclusive, @ParametricNullness E toElement, boolean toInclusive) {
      return this.forward.subSet(toElement, toInclusive, fromElement, fromInclusive).descendingSet();
    }
    
    public SortedSet<E> subSet(@ParametricNullness E fromElement, @ParametricNullness E toElement) {
      return standardSubSet(fromElement, toElement);
    }
    
    public NavigableSet<E> headSet(@ParametricNullness E toElement, boolean inclusive) {
      return this.forward.tailSet(toElement, inclusive).descendingSet();
    }
    
    public SortedSet<E> headSet(@ParametricNullness E toElement) {
      return standardHeadSet(toElement);
    }
    
    public NavigableSet<E> tailSet(@ParametricNullness E fromElement, boolean inclusive) {
      return this.forward.headSet(fromElement, inclusive).descendingSet();
    }
    
    public SortedSet<E> tailSet(@ParametricNullness E fromElement) {
      return standardTailSet(fromElement);
    }
    
    public Comparator<? super E> comparator() {
      Comparator<? super E> forwardComparator = this.forward.comparator();
      if (forwardComparator == null)
        return Ordering.<Comparable>natural().reverse(); 
      return reverse(forwardComparator);
    }
    
    private static <T> Ordering<T> reverse(Comparator<T> forward) {
      return Ordering.<T>from(forward).reverse();
    }
    
    @ParametricNullness
    public E first() {
      return this.forward.last();
    }
    
    @ParametricNullness
    public E last() {
      return this.forward.first();
    }
    
    public Iterator<E> iterator() {
      return this.forward.descendingIterator();
    }
    
    public Object[] toArray() {
      return standardToArray();
    }
    
    public <T> T[] toArray(T[] array) {
      return (T[])standardToArray((Object[])array);
    }
    
    public String toString() {
      return standardToString();
    }
  }
  
  @GwtIncompatible
  public static <K extends Comparable<? super K>> NavigableSet<K> subSet(NavigableSet<K> set, Range<K> range) {
    if (set.comparator() != null && set
      .comparator() != Ordering.natural() && range
      .hasLowerBound() && range
      .hasUpperBound())
      Preconditions.checkArgument(
          (set.comparator().compare(range.lowerEndpoint(), range.upperEndpoint()) <= 0), "set is using a custom comparator which is inconsistent with the natural ordering."); 
    if (range.hasLowerBound() && range.hasUpperBound())
      return set.subSet(range
          .lowerEndpoint(), 
          (range.lowerBoundType() == BoundType.CLOSED), range
          .upperEndpoint(), 
          (range.upperBoundType() == BoundType.CLOSED)); 
    if (range.hasLowerBound())
      return set.tailSet(range.lowerEndpoint(), (range.lowerBoundType() == BoundType.CLOSED)); 
    if (range.hasUpperBound())
      return set.headSet(range.upperEndpoint(), (range.upperBoundType() == BoundType.CLOSED)); 
    return (NavigableSet<K>)Preconditions.checkNotNull(set);
  }
}
