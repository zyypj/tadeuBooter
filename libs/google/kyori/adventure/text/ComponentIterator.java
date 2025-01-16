package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import me.syncwrld.booter.libs.jtann.NotNull;

final class ComponentIterator implements Iterator<Component> {
  private Component component;
  
  private final ComponentIteratorType type;
  
  private final Set<ComponentIteratorFlag> flags;
  
  private final Deque<Component> deque;
  
  ComponentIterator(@NotNull Component component, @NotNull ComponentIteratorType type, @NotNull Set<ComponentIteratorFlag> flags) {
    this.component = component;
    this.type = type;
    this.flags = flags;
    this.deque = new ArrayDeque<>();
  }
  
  public boolean hasNext() {
    return (this.component != null || !this.deque.isEmpty());
  }
  
  public Component next() {
    if (this.component != null) {
      Component next = this.component;
      this.component = null;
      this.type.populate(next, this.deque, this.flags);
      return next;
    } 
    if (this.deque.isEmpty())
      throw new NoSuchElementException(); 
    this.component = this.deque.poll();
    return next();
  }
}
