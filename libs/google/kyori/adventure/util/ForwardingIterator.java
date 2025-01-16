package me.syncwrld.booter.libs.google.kyori.adventure.util;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Supplier;
import me.syncwrld.booter.libs.jtann.NotNull;

public final class ForwardingIterator<T> implements Iterable<T> {
  private final Supplier<Iterator<T>> iterator;
  
  private final Supplier<Spliterator<T>> spliterator;
  
  public ForwardingIterator(@NotNull Supplier<Iterator<T>> iterator, @NotNull Supplier<Spliterator<T>> spliterator) {
    this.iterator = Objects.<Supplier<Iterator<T>>>requireNonNull(iterator, "iterator");
    this.spliterator = Objects.<Supplier<Spliterator<T>>>requireNonNull(spliterator, "spliterator");
  }
  
  @NotNull
  public Iterator<T> iterator() {
    return this.iterator.get();
  }
  
  @NotNull
  public Spliterator<T> spliterator() {
    return this.spliterator.get();
  }
}
