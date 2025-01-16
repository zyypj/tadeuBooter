package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
class EmptyImmutableSetMultimap extends ImmutableSetMultimap<Object, Object> {
  static final EmptyImmutableSetMultimap INSTANCE = new EmptyImmutableSetMultimap();
  
  private static final long serialVersionUID = 0L;
  
  private EmptyImmutableSetMultimap() {
    super(ImmutableMap.of(), 0, (Comparator<? super Object>)null);
  }
  
  public ImmutableMap<Object, Collection<Object>> asMap() {
    return super.asMap();
  }
  
  private Object readResolve() {
    return INSTANCE;
  }
}
