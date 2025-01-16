package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSet<E> extends ForwardingCollection<E> implements Set<E> {
  public boolean equals(@CheckForNull Object object) {
    return (object == this || delegate().equals(object));
  }
  
  public int hashCode() {
    return delegate().hashCode();
  }
  
  protected boolean standardRemoveAll(Collection<?> collection) {
    return Sets.removeAllImpl(this, (Collection)Preconditions.checkNotNull(collection));
  }
  
  protected boolean standardEquals(@CheckForNull Object object) {
    return Sets.equalsImpl(this, object);
  }
  
  protected int standardHashCode() {
    return Sets.hashCodeImpl(this);
  }
  
  protected abstract Set<E> delegate();
}
