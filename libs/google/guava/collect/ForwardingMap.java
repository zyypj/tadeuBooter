package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingMap<K, V> extends ForwardingObject implements Map<K, V> {
  public int size() {
    return delegate().size();
  }
  
  public boolean isEmpty() {
    return delegate().isEmpty();
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V remove(@CheckForNull Object key) {
    return delegate().remove(key);
  }
  
  public void clear() {
    delegate().clear();
  }
  
  public boolean containsKey(@CheckForNull Object key) {
    return delegate().containsKey(key);
  }
  
  public boolean containsValue(@CheckForNull Object value) {
    return delegate().containsValue(value);
  }
  
  @CheckForNull
  public V get(@CheckForNull Object key) {
    return delegate().get(key);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public V put(@ParametricNullness K key, @ParametricNullness V value) {
    return delegate().put(key, value);
  }
  
  public void putAll(Map<? extends K, ? extends V> map) {
    delegate().putAll(map);
  }
  
  public Set<K> keySet() {
    return delegate().keySet();
  }
  
  public Collection<V> values() {
    return delegate().values();
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    return delegate().entrySet();
  }
  
  public boolean equals(@CheckForNull Object object) {
    return (object == this || delegate().equals(object));
  }
  
  public int hashCode() {
    return delegate().hashCode();
  }
  
  protected void standardPutAll(Map<? extends K, ? extends V> map) {
    Maps.putAllImpl(this, map);
  }
  
  @CheckForNull
  protected V standardRemove(@CheckForNull Object key) {
    Iterator<Map.Entry<K, V>> entryIterator = entrySet().iterator();
    while (entryIterator.hasNext()) {
      Map.Entry<K, V> entry = entryIterator.next();
      if (Objects.equal(entry.getKey(), key)) {
        V value = entry.getValue();
        entryIterator.remove();
        return value;
      } 
    } 
    return null;
  }
  
  protected void standardClear() {
    Iterators.clear(entrySet().iterator());
  }
  
  protected class StandardKeySet extends Maps.KeySet<K, V> {
    public StandardKeySet(ForwardingMap<K, V> this$0) {
      super(this$0);
    }
  }
  
  protected boolean standardContainsKey(@CheckForNull Object key) {
    return Maps.containsKeyImpl(this, key);
  }
  
  protected class StandardValues extends Maps.Values<K, V> {
    public StandardValues(ForwardingMap<K, V> this$0) {
      super(this$0);
    }
  }
  
  protected boolean standardContainsValue(@CheckForNull Object value) {
    return Maps.containsValueImpl(this, value);
  }
  
  protected abstract class StandardEntrySet extends Maps.EntrySet<K, V> {
    Map<K, V> map() {
      return ForwardingMap.this;
    }
  }
  
  protected boolean standardIsEmpty() {
    return !entrySet().iterator().hasNext();
  }
  
  protected boolean standardEquals(@CheckForNull Object object) {
    return Maps.equalsImpl(this, object);
  }
  
  protected int standardHashCode() {
    return Sets.hashCodeImpl(entrySet());
  }
  
  protected String standardToString() {
    return Maps.toStringImpl(this);
  }
  
  protected abstract Map<K, V> delegate();
}
