package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible
class SingletonImmutableTable<R, C, V> extends ImmutableTable<R, C, V> {
  final R singleRowKey;
  
  final C singleColumnKey;
  
  final V singleValue;
  
  SingletonImmutableTable(R rowKey, C columnKey, V value) {
    this.singleRowKey = (R)Preconditions.checkNotNull(rowKey);
    this.singleColumnKey = (C)Preconditions.checkNotNull(columnKey);
    this.singleValue = (V)Preconditions.checkNotNull(value);
  }
  
  SingletonImmutableTable(Table.Cell<R, C, V> cell) {
    this(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
  }
  
  public ImmutableMap<R, V> column(C columnKey) {
    Preconditions.checkNotNull(columnKey);
    return containsColumn(columnKey) ? 
      ImmutableMap.<R, V>of(this.singleRowKey, this.singleValue) : 
      ImmutableMap.<R, V>of();
  }
  
  public ImmutableMap<C, Map<R, V>> columnMap() {
    return ImmutableMap.of(this.singleColumnKey, ImmutableMap.of(this.singleRowKey, this.singleValue));
  }
  
  public ImmutableMap<R, Map<C, V>> rowMap() {
    return ImmutableMap.of(this.singleRowKey, ImmutableMap.of(this.singleColumnKey, this.singleValue));
  }
  
  public int size() {
    return 1;
  }
  
  ImmutableSet<Table.Cell<R, C, V>> createCellSet() {
    return ImmutableSet.of(cellOf(this.singleRowKey, this.singleColumnKey, this.singleValue));
  }
  
  ImmutableCollection<V> createValues() {
    return ImmutableSet.of(this.singleValue);
  }
  
  @J2ktIncompatible
  @GwtIncompatible
  Object writeReplace() {
    return ImmutableTable.SerializedForm.create(this, new int[] { 0 }, new int[] { 0 });
  }
}
