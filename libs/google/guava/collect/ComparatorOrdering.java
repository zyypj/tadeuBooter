package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import java.util.Comparator;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
final class ComparatorOrdering<T> extends Ordering<T> implements Serializable {
  final Comparator<T> comparator;
  
  private static final long serialVersionUID = 0L;
  
  ComparatorOrdering(Comparator<T> comparator) {
    this.comparator = (Comparator<T>)Preconditions.checkNotNull(comparator);
  }
  
  public int compare(@ParametricNullness T a, @ParametricNullness T b) {
    return this.comparator.compare(a, b);
  }
  
  public boolean equals(@CheckForNull Object object) {
    if (object == this)
      return true; 
    if (object instanceof ComparatorOrdering) {
      ComparatorOrdering<?> that = (ComparatorOrdering)object;
      return this.comparator.equals(that.comparator);
    } 
    return false;
  }
  
  public int hashCode() {
    return this.comparator.hashCode();
  }
  
  public String toString() {
    return this.comparator.toString();
  }
}
