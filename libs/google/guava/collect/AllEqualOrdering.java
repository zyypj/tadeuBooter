package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import java.util.List;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
final class AllEqualOrdering extends Ordering<Object> implements Serializable {
  static final AllEqualOrdering INSTANCE = new AllEqualOrdering();
  
  private static final long serialVersionUID = 0L;
  
  public int compare(@CheckForNull Object left, @CheckForNull Object right) {
    return 0;
  }
  
  public <E> List<E> sortedCopy(Iterable<E> iterable) {
    return Lists.newArrayList(iterable);
  }
  
  public <E> ImmutableList<E> immutableSortedCopy(Iterable<E> iterable) {
    return ImmutableList.copyOf(iterable);
  }
  
  public <S> Ordering<S> reverse() {
    return this;
  }
  
  private Object readResolve() {
    return INSTANCE;
  }
  
  public String toString() {
    return "Ordering.allEqual()";
  }
}
