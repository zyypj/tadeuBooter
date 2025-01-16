package me.syncwrld.booter.libs.google.guava.reflect;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotCall;
import me.syncwrld.booter.libs.google.guava.base.Function;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ForwardingMap;
import me.syncwrld.booter.libs.google.guava.collect.ForwardingMapEntry;
import me.syncwrld.booter.libs.google.guava.collect.ForwardingSet;
import me.syncwrld.booter.libs.google.guava.collect.Iterators;
import me.syncwrld.booter.libs.google.guava.collect.Maps;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
public final class MutableTypeToInstanceMap<B> extends ForwardingMap<TypeToken<? extends B>, B> implements TypeToInstanceMap<B> {
  private final Map<TypeToken<? extends B>, B> backingMap = Maps.newHashMap();
  
  @CheckForNull
  public <T extends B> T getInstance(Class<T> type) {
    return trustedGet(TypeToken.of(type));
  }
  
  @CheckForNull
  public <T extends B> T getInstance(TypeToken<T> type) {
    return trustedGet(type.rejectTypeVariables());
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public <T extends B> T putInstance(Class<T> type, @ParametricNullness T value) {
    return trustedPut(TypeToken.of(type), value);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public <T extends B> T putInstance(TypeToken<T> type, @ParametricNullness T value) {
    return trustedPut(type.rejectTypeVariables(), value);
  }
  
  @Deprecated
  @CheckForNull
  @CanIgnoreReturnValue
  @DoNotCall("Always throws UnsupportedOperationException")
  public B put(TypeToken<? extends B> key, @ParametricNullness B value) {
    throw new UnsupportedOperationException("Please use putInstance() instead.");
  }
  
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public void putAll(Map<? extends TypeToken<? extends B>, ? extends B> map) {
    throw new UnsupportedOperationException("Please use putInstance() instead.");
  }
  
  public Set<Map.Entry<TypeToken<? extends B>, B>> entrySet() {
    return UnmodifiableEntry.transformEntries(super.entrySet());
  }
  
  protected Map<TypeToken<? extends B>, B> delegate() {
    return this.backingMap;
  }
  
  @CheckForNull
  private <T extends B> T trustedPut(TypeToken<T> type, @ParametricNullness T value) {
    return (T)this.backingMap.put(type, (B)value);
  }
  
  @CheckForNull
  private <T extends B> T trustedGet(TypeToken<T> type) {
    return (T)this.backingMap.get(type);
  }
  
  private static final class UnmodifiableEntry<K, V> extends ForwardingMapEntry<K, V> {
    private final Map.Entry<K, V> delegate;
    
    static <K, V> Set<Map.Entry<K, V>> transformEntries(final Set<Map.Entry<K, V>> entries) {
      return (Set<Map.Entry<K, V>>)new ForwardingSet<Map.Entry<K, V>>() {
          protected Set<Map.Entry<K, V>> delegate() {
            return entries;
          }
          
          public Iterator<Map.Entry<K, V>> iterator() {
            return MutableTypeToInstanceMap.UnmodifiableEntry.transformEntries(super.iterator());
          }
          
          public Object[] toArray() {
            Object[] result = standardToArray();
            return result;
          }
          
          public <T> T[] toArray(T[] array) {
            return (T[])standardToArray((Object[])array);
          }
        };
    }
    
    private static <K, V> Iterator<Map.Entry<K, V>> transformEntries(Iterator<Map.Entry<K, V>> entries) {
      return Iterators.transform(entries, UnmodifiableEntry::new);
    }
    
    private UnmodifiableEntry(Map.Entry<K, V> delegate) {
      this.delegate = (Map.Entry<K, V>)Preconditions.checkNotNull(delegate);
    }
    
    protected Map.Entry<K, V> delegate() {
      return this.delegate;
    }
    
    @ParametricNullness
    public V setValue(@ParametricNullness V value) {
      throw new UnsupportedOperationException();
    }
  }
}
