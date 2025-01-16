package me.syncwrld.booter.libs.google.guava.reflect;

import java.util.Map;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotCall;
import me.syncwrld.booter.libs.google.guava.collect.ForwardingMap;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
public final class ImmutableTypeToInstanceMap<B> extends ForwardingMap<TypeToken<? extends B>, B> implements TypeToInstanceMap<B> {
  private final ImmutableMap<TypeToken<? extends B>, B> delegate;
  
  public static <B> ImmutableTypeToInstanceMap<B> of() {
    return new ImmutableTypeToInstanceMap<>(ImmutableMap.of());
  }
  
  public static <B> Builder<B> builder() {
    return new Builder<>();
  }
  
  public static final class Builder<B> {
    private final ImmutableMap.Builder<TypeToken<? extends B>, B> mapBuilder = ImmutableMap.builder();
    
    @CanIgnoreReturnValue
    public <T extends B> Builder<B> put(Class<T> key, T value) {
      this.mapBuilder.put(TypeToken.of(key), value);
      return this;
    }
    
    @CanIgnoreReturnValue
    public <T extends B> Builder<B> put(TypeToken<T> key, T value) {
      this.mapBuilder.put(key.rejectTypeVariables(), value);
      return this;
    }
    
    public ImmutableTypeToInstanceMap<B> build() {
      return new ImmutableTypeToInstanceMap<>(this.mapBuilder.buildOrThrow());
    }
    
    private Builder() {}
  }
  
  private ImmutableTypeToInstanceMap(ImmutableMap<TypeToken<? extends B>, B> delegate) {
    this.delegate = delegate;
  }
  
  @CheckForNull
  public <T extends B> T getInstance(TypeToken<T> type) {
    return trustedGet(type.rejectTypeVariables());
  }
  
  @CheckForNull
  public <T extends B> T getInstance(Class<T> type) {
    return trustedGet(TypeToken.of(type));
  }
  
  @Deprecated
  @CheckForNull
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public <T extends B> T putInstance(TypeToken<T> type, T value) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CheckForNull
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public <T extends B> T putInstance(Class<T> type, T value) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @CheckForNull
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public B put(TypeToken<? extends B> key, B value) {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public void putAll(Map<? extends TypeToken<? extends B>, ? extends B> map) {
    throw new UnsupportedOperationException();
  }
  
  protected Map<TypeToken<? extends B>, B> delegate() {
    return (Map<TypeToken<? extends B>, B>)this.delegate;
  }
  
  @CheckForNull
  private <T extends B> T trustedGet(TypeToken<T> type) {
    return (T)this.delegate.get(type);
  }
}
