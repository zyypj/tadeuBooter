package me.syncwrld.booter.libs.google.guava.collect;

import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
class ImmutableMapEntry<K, V> extends ImmutableEntry<K, V> {
  static <K, V> ImmutableMapEntry<K, V>[] createEntryArray(int size) {
    return (ImmutableMapEntry<K, V>[])new ImmutableMapEntry[size];
  }
  
  ImmutableMapEntry(K key, V value) {
    super(key, value);
    CollectPreconditions.checkEntryNotNull(key, value);
  }
  
  ImmutableMapEntry(ImmutableMapEntry<K, V> contents) {
    super(contents.getKey(), contents.getValue());
  }
  
  @CheckForNull
  ImmutableMapEntry<K, V> getNextInKeyBucket() {
    return null;
  }
  
  @CheckForNull
  ImmutableMapEntry<K, V> getNextInValueBucket() {
    return null;
  }
  
  boolean isReusable() {
    return true;
  }
  
  static class NonTerminalImmutableMapEntry<K, V> extends ImmutableMapEntry<K, V> {
    @CheckForNull
    private final transient ImmutableMapEntry<K, V> nextInKeyBucket;
    
    NonTerminalImmutableMapEntry(K key, V value, @CheckForNull ImmutableMapEntry<K, V> nextInKeyBucket) {
      super(key, value);
      this.nextInKeyBucket = nextInKeyBucket;
    }
    
    @CheckForNull
    final ImmutableMapEntry<K, V> getNextInKeyBucket() {
      return this.nextInKeyBucket;
    }
    
    final boolean isReusable() {
      return false;
    }
  }
  
  static final class NonTerminalImmutableBiMapEntry<K, V> extends NonTerminalImmutableMapEntry<K, V> {
    @CheckForNull
    private final transient ImmutableMapEntry<K, V> nextInValueBucket;
    
    NonTerminalImmutableBiMapEntry(K key, V value, @CheckForNull ImmutableMapEntry<K, V> nextInKeyBucket, @CheckForNull ImmutableMapEntry<K, V> nextInValueBucket) {
      super(key, value, nextInKeyBucket);
      this.nextInValueBucket = nextInValueBucket;
    }
    
    @CheckForNull
    ImmutableMapEntry<K, V> getNextInValueBucket() {
      return this.nextInValueBucket;
    }
  }
}
