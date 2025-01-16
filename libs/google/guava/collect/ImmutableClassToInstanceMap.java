package me.syncwrld.booter.libs.google.guava.collect;

import java.io.Serializable;
import java.util.Map;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotCall;
import me.syncwrld.booter.libs.google.errorprone.annotations.Immutable;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.primitives.Primitives;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@Immutable(containerOf = {"B"})
@ElementTypesAreNonnullByDefault
@GwtIncompatible
public final class ImmutableClassToInstanceMap<B> extends ForwardingMap<Class<? extends B>, B> implements ClassToInstanceMap<B>, Serializable {
  private static final ImmutableClassToInstanceMap<Object> EMPTY = new ImmutableClassToInstanceMap(
      ImmutableMap.of());
  
  private final ImmutableMap<Class<? extends B>, B> delegate;
  
  public static <B> ImmutableClassToInstanceMap<B> of() {
    return (ImmutableClassToInstanceMap)EMPTY;
  }
  
  public static <B, T extends B> ImmutableClassToInstanceMap<B> of(Class<T> type, T value) {
    ImmutableMap<Class<? extends B>, B> map = ImmutableMap.of(type, (B)value);
    return new ImmutableClassToInstanceMap<>(map);
  }
  
  public static <B> Builder<B> builder() {
    return new Builder<>();
  }
  
  public static final class Builder<B> {
    private final ImmutableMap.Builder<Class<? extends B>, B> mapBuilder = ImmutableMap.builder();
    
    @CanIgnoreReturnValue
    public <T extends B> Builder<B> put(Class<T> key, T value) {
      this.mapBuilder.put(key, (B)value);
      return this;
    }
    
    @CanIgnoreReturnValue
    public <T extends B> Builder<B> putAll(Map<? extends Class<? extends T>, ? extends T> map) {
      for (Map.Entry<? extends Class<? extends T>, ? extends T> entry : map.entrySet()) {
        Class<? extends T> type = entry.getKey();
        T value = entry.getValue();
        this.mapBuilder.put(type, cast((Class)type, value));
      } 
      return this;
    }
    
    private static <T> T cast(Class<T> type, Object value) {
      return Primitives.wrap(type).cast(value);
    }
    
    public ImmutableClassToInstanceMap<B> build() {
      ImmutableMap<Class<? extends B>, B> map = this.mapBuilder.buildOrThrow();
      if (map.isEmpty())
        return ImmutableClassToInstanceMap.of(); 
      return new ImmutableClassToInstanceMap<>(map);
    }
  }
  
  public static <B, S extends B> ImmutableClassToInstanceMap<B> copyOf(Map<? extends Class<? extends S>, ? extends S> map) {
    if (map instanceof ImmutableClassToInstanceMap) {
      Map<? extends Class<? extends S>, ? extends S> rawMap = map;
      ImmutableClassToInstanceMap<B> cast = (ImmutableClassToInstanceMap)rawMap;
      return cast;
    } 
    return (new Builder<>()).<S>putAll(map).build();
  }
  
  private ImmutableClassToInstanceMap(ImmutableMap<Class<? extends B>, B> delegate) {
    this.delegate = delegate;
  }
  
  protected Map<Class<? extends B>, B> delegate() {
    return this.delegate;
  }
  
  @CheckForNull
  public <T extends B> T getInstance(Class<T> type) {
    return (T)this.delegate.get(Preconditions.checkNotNull(type));
  }
  
  @Deprecated
  @CheckForNull
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public <T extends B> T putInstance(Class<T> type, T value) {
    throw new UnsupportedOperationException();
  }
  
  Object readResolve() {
    return isEmpty() ? of() : this;
  }
}
