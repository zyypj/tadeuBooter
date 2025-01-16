package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.CompatibleWith;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Multiset<E> extends Collection<E> {
  default void forEachEntry(ObjIntConsumer<? super E> action) {
    Preconditions.checkNotNull(action);
    entrySet().forEach(entry -> action.accept(entry.getElement(), entry.getCount()));
  }
  
  default void forEach(Consumer<? super E> action) {
    Preconditions.checkNotNull(action);
    entrySet()
      .forEach(entry -> {
          E elem = entry.getElement();
          int count = entry.getCount();
          for (int i = 0; i < count; i++)
            action.accept(elem); 
        });
  }
  
  default Spliterator<E> spliterator() {
    return Multisets.spliteratorImpl(this);
  }
  
  int size();
  
  int count(@CheckForNull @CompatibleWith("E") Object paramObject);
  
  @CanIgnoreReturnValue
  int add(@ParametricNullness E paramE, int paramInt);
  
  @CanIgnoreReturnValue
  boolean add(@ParametricNullness E paramE);
  
  @CanIgnoreReturnValue
  int remove(@CheckForNull @CompatibleWith("E") Object paramObject, int paramInt);
  
  @CanIgnoreReturnValue
  boolean remove(@CheckForNull Object paramObject);
  
  @CanIgnoreReturnValue
  int setCount(@ParametricNullness E paramE, int paramInt);
  
  @CanIgnoreReturnValue
  boolean setCount(@ParametricNullness E paramE, int paramInt1, int paramInt2);
  
  Set<E> elementSet();
  
  Set<Entry<E>> entrySet();
  
  boolean equals(@CheckForNull Object paramObject);
  
  int hashCode();
  
  String toString();
  
  Iterator<E> iterator();
  
  boolean contains(@CheckForNull Object paramObject);
  
  boolean containsAll(Collection<?> paramCollection);
  
  @CanIgnoreReturnValue
  boolean removeAll(Collection<?> paramCollection);
  
  @CanIgnoreReturnValue
  boolean retainAll(Collection<?> paramCollection);
  
  public static interface Entry<E> {
    @ParametricNullness
    E getElement();
    
    int getCount();
    
    boolean equals(@CheckForNull Object param1Object);
    
    int hashCode();
    
    String toString();
  }
}
