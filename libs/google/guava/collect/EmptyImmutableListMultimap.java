package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Map;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
class EmptyImmutableListMultimap extends ImmutableListMultimap<Object, Object> {
  static final EmptyImmutableListMultimap INSTANCE = new EmptyImmutableListMultimap();
  
  private static final long serialVersionUID = 0L;
  
  private EmptyImmutableListMultimap() {
    super(ImmutableMap.of(), 0);
  }
  
  public ImmutableMap<Object, Collection<Object>> asMap() {
    return super.asMap();
  }
  
  private Object readResolve() {
    return INSTANCE;
  }
}
