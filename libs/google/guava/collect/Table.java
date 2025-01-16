package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.CompatibleWith;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Use ImmutableTable, HashBasedTable, or another implementation")
@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Table<R, C, V> {
  boolean contains(@CheckForNull @CompatibleWith("R") Object paramObject1, @CheckForNull @CompatibleWith("C") Object paramObject2);
  
  boolean containsRow(@CheckForNull @CompatibleWith("R") Object paramObject);
  
  boolean containsColumn(@CheckForNull @CompatibleWith("C") Object paramObject);
  
  boolean containsValue(@CheckForNull @CompatibleWith("V") Object paramObject);
  
  @CheckForNull
  V get(@CheckForNull @CompatibleWith("R") Object paramObject1, @CheckForNull @CompatibleWith("C") Object paramObject2);
  
  boolean isEmpty();
  
  int size();
  
  boolean equals(@CheckForNull Object paramObject);
  
  int hashCode();
  
  void clear();
  
  @CheckForNull
  @CanIgnoreReturnValue
  V put(@ParametricNullness R paramR, @ParametricNullness C paramC, @ParametricNullness V paramV);
  
  void putAll(Table<? extends R, ? extends C, ? extends V> paramTable);
  
  @CheckForNull
  @CanIgnoreReturnValue
  V remove(@CheckForNull @CompatibleWith("R") Object paramObject1, @CheckForNull @CompatibleWith("C") Object paramObject2);
  
  Map<C, V> row(@ParametricNullness R paramR);
  
  Map<R, V> column(@ParametricNullness C paramC);
  
  Set<Cell<R, C, V>> cellSet();
  
  Set<R> rowKeySet();
  
  Set<C> columnKeySet();
  
  Collection<V> values();
  
  Map<R, Map<C, V>> rowMap();
  
  Map<C, Map<R, V>> columnMap();
  
  public static interface Cell<R, C, V> {
    @ParametricNullness
    R getRowKey();
    
    @ParametricNullness
    C getColumnKey();
    
    @ParametricNullness
    V getValue();
    
    boolean equals(@CheckForNull Object param1Object);
    
    int hashCode();
  }
}
