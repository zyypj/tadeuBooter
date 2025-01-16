package me.syncwrld.booter.libs.unnamed.inject.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import me.syncwrld.booter.libs.unnamed.inject.error.ErrorAttachableImpl;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;

public class ProvisionStack extends ErrorAttachableImpl {
  private final Map<Key<?>, Object> values = new HashMap<>();
  
  private final LinkedList<KeyInstanceEntry<?>> stack = new LinkedList<>();
  
  public boolean has(Key<?> key) {
    return this.values.containsKey(key);
  }
  
  public <T> T get(Key<T> key) {
    T value = (T)this.values.get(key);
    return value;
  }
  
  public void pop() {
    Map.Entry<Key<?>, Object> entry = this.stack.removeFirst();
    if (entry != null)
      this.values.remove(entry.getKey()); 
  }
  
  public <T> void push(Key<T> key, T value) {
    this.values.put(key, value);
    this.stack.addFirst(new KeyInstanceEntry(key, value));
  }
  
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append('(');
    builder.append(errorCount());
    builder.append(" errors");
    builder.append(") ");
    Iterator<KeyInstanceEntry<?>> entries = this.stack.iterator();
    while (entries.hasNext()) {
      KeyInstanceEntry<?> entry = entries.next();
      builder.append(entry.getKey());
      if (entries.hasNext())
        builder.append(" -> "); 
    } 
    return builder.toString();
  }
  
  private static class KeyInstanceEntry<T> implements Map.Entry<Key<?>, Object> {
    private final Key<T> key;
    
    private final T value;
    
    public KeyInstanceEntry(Key<T> key, T value) {
      this.key = key;
      this.value = value;
    }
    
    public Key<?> getKey() {
      return this.key;
    }
    
    public Object getValue() {
      return this.value;
    }
    
    public T setValue(Object value) {
      throw new UnsupportedOperationException("This entry is immutable!");
    }
    
    public boolean equals(Object o) {
      if (this == o)
        return true; 
      if (o == null || getClass() != o.getClass())
        return false; 
      KeyInstanceEntry<?> that = (KeyInstanceEntry)o;
      return (this.key.equals(that.key) && this.value
        .equals(that.value));
    }
    
    public int hashCode() {
      int result = 1;
      result = 31 * result + this.key.hashCode();
      result = 31 * result + this.value.hashCode();
      return result;
    }
  }
}
