package me.syncwrld.booter.libs.google.guava.collect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class TableCollectors {
  static <T, R, C, V> Collector<T, ?, ImmutableTable<R, C, V>> toImmutableTable(Function<? super T, ? extends R> rowFunction, Function<? super T, ? extends C> columnFunction, Function<? super T, ? extends V> valueFunction) {
    Preconditions.checkNotNull(rowFunction, "rowFunction");
    Preconditions.checkNotNull(columnFunction, "columnFunction");
    Preconditions.checkNotNull(valueFunction, "valueFunction");
    return Collector.of(Builder::new, (builder, t) -> builder.put(rowFunction.apply(t), columnFunction.apply(t), valueFunction.apply(t)), ImmutableTable.Builder::combine, ImmutableTable.Builder::build, new Collector.Characteristics[0]);
  }
  
  static <T, R, C, V> Collector<T, ?, ImmutableTable<R, C, V>> toImmutableTable(Function<? super T, ? extends R> rowFunction, Function<? super T, ? extends C> columnFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction) {
    Preconditions.checkNotNull(rowFunction, "rowFunction");
    Preconditions.checkNotNull(columnFunction, "columnFunction");
    Preconditions.checkNotNull(valueFunction, "valueFunction");
    Preconditions.checkNotNull(mergeFunction, "mergeFunction");
    return Collector.of(() -> new ImmutableTableCollectorState<>(), (state, input) -> state.put(rowFunction.apply(input), columnFunction.apply(input), valueFunction.apply(input), mergeFunction), (s1, s2) -> s1.combine(s2, mergeFunction), state -> state.toTable(), new Collector.Characteristics[0]);
  }
  
  static <T, R, C, V, I extends Table<R, C, V>> Collector<T, ?, I> toTable(Function<? super T, ? extends R> rowFunction, Function<? super T, ? extends C> columnFunction, Function<? super T, ? extends V> valueFunction, Supplier<I> tableSupplier) {
    return toTable(rowFunction, columnFunction, valueFunction, (v1, v2) -> {
          throw new IllegalStateException("Conflicting values " + v1 + " and " + v2);
        }tableSupplier);
  }
  
  static <T, R, C, V, I extends Table<R, C, V>> Collector<T, ?, I> toTable(Function<? super T, ? extends R> rowFunction, Function<? super T, ? extends C> columnFunction, Function<? super T, ? extends V> valueFunction, BinaryOperator<V> mergeFunction, Supplier<I> tableSupplier) {
    Preconditions.checkNotNull(rowFunction);
    Preconditions.checkNotNull(columnFunction);
    Preconditions.checkNotNull(valueFunction);
    Preconditions.checkNotNull(mergeFunction);
    Preconditions.checkNotNull(tableSupplier);
    return (Collector)Collector.of(tableSupplier, (table, input) -> mergeTables(table, rowFunction.apply(input), columnFunction.apply(input), valueFunction.apply(input), mergeFunction), (table1, table2) -> {
          for (Table.Cell<R, C, V> cell2 : (Iterable<Table.Cell<R, C, V>>)table2.cellSet())
            mergeTables(table1, cell2.getRowKey(), cell2.getColumnKey(), cell2.getValue(), mergeFunction); 
          return table1;
        }new Collector.Characteristics[0]);
  }
  
  private static final class ImmutableTableCollectorState<R, C, V> {
    final List<TableCollectors.MutableCell<R, C, V>> insertionOrder = new ArrayList<>();
    
    final Table<R, C, TableCollectors.MutableCell<R, C, V>> table = HashBasedTable.create();
    
    void put(R row, C column, V value, BinaryOperator<V> merger) {
      TableCollectors.MutableCell<R, C, V> oldCell = this.table.get(row, column);
      if (oldCell == null) {
        TableCollectors.MutableCell<R, C, V> cell = new TableCollectors.MutableCell<>(row, column, value);
        this.insertionOrder.add(cell);
        this.table.put(row, column, cell);
      } else {
        oldCell.merge(value, merger);
      } 
    }
    
    ImmutableTableCollectorState<R, C, V> combine(ImmutableTableCollectorState<R, C, V> other, BinaryOperator<V> merger) {
      for (TableCollectors.MutableCell<R, C, V> cell : other.insertionOrder)
        put(cell.getRowKey(), cell.getColumnKey(), cell.getValue(), merger); 
      return this;
    }
    
    ImmutableTable<R, C, V> toTable() {
      return ImmutableTable.copyOf((Iterable)this.insertionOrder);
    }
    
    private ImmutableTableCollectorState() {}
  }
  
  private static final class MutableCell<R, C, V> extends Tables.AbstractCell<R, C, V> {
    private final R row;
    
    private final C column;
    
    private V value;
    
    MutableCell(R row, C column, V value) {
      this.row = (R)Preconditions.checkNotNull(row, "row");
      this.column = (C)Preconditions.checkNotNull(column, "column");
      this.value = (V)Preconditions.checkNotNull(value, "value");
    }
    
    public R getRowKey() {
      return this.row;
    }
    
    public C getColumnKey() {
      return this.column;
    }
    
    public V getValue() {
      return this.value;
    }
    
    void merge(V value, BinaryOperator<V> mergeFunction) {
      Preconditions.checkNotNull(value, "value");
      this.value = (V)Preconditions.checkNotNull(mergeFunction.apply(this.value, value), "mergeFunction.apply");
    }
  }
  
  private static <R, C, V> void mergeTables(Table<R, C, V> table, @ParametricNullness R row, @ParametricNullness C column, @ParametricNullness V value, BinaryOperator<V> mergeFunction) {
    Preconditions.checkNotNull(value);
    V oldValue = table.get(row, column);
    if (oldValue == null) {
      table.put(row, column, value);
    } else {
      V newValue = mergeFunction.apply(oldValue, value);
      if (newValue == null) {
        table.remove(row, column);
      } else {
        table.put(row, column, newValue);
      } 
    } 
  }
}
