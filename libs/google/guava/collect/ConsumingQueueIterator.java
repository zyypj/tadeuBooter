package me.syncwrld.booter.libs.google.guava.collect;

import java.util.Queue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class ConsumingQueueIterator<T> extends AbstractIterator<T> {
  private final Queue<T> queue;
  
  ConsumingQueueIterator(Queue<T> queue) {
    this.queue = (Queue<T>)Preconditions.checkNotNull(queue);
  }
  
  @CheckForNull
  protected T computeNext() {
    if (this.queue.isEmpty())
      return endOfData(); 
    return this.queue.remove();
  }
}
