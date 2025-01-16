package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.GuardedBy;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.Queues;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
final class ListenerCallQueue<L> {
  private static final LazyLogger logger = new LazyLogger(ListenerCallQueue.class);
  
  private final List<PerListenerQueue<L>> listeners = Collections.synchronizedList(new ArrayList<>());
  
  public void addListener(L listener, Executor executor) {
    Preconditions.checkNotNull(listener, "listener");
    Preconditions.checkNotNull(executor, "executor");
    this.listeners.add(new PerListenerQueue<>(listener, executor));
  }
  
  public void enqueue(Event<L> event) {
    enqueueHelper(event, event);
  }
  
  public void enqueue(Event<L> event, String label) {
    enqueueHelper(event, label);
  }
  
  private void enqueueHelper(Event<L> event, Object label) {
    Preconditions.checkNotNull(event, "event");
    Preconditions.checkNotNull(label, "label");
    synchronized (this.listeners) {
      for (PerListenerQueue<L> queue : this.listeners)
        queue.add(event, label); 
    } 
  }
  
  public void dispatch() {
    for (int i = 0; i < this.listeners.size(); i++)
      ((PerListenerQueue)this.listeners.get(i)).dispatch(); 
  }
  
  private static final class PerListenerQueue<L> implements Runnable {
    final L listener;
    
    final Executor executor;
    
    @GuardedBy("this")
    final Queue<ListenerCallQueue.Event<L>> waitQueue = Queues.newArrayDeque();
    
    @GuardedBy("this")
    final Queue<Object> labelQueue = Queues.newArrayDeque();
    
    @GuardedBy("this")
    boolean isThreadScheduled;
    
    PerListenerQueue(L listener, Executor executor) {
      this.listener = (L)Preconditions.checkNotNull(listener);
      this.executor = (Executor)Preconditions.checkNotNull(executor);
    }
    
    synchronized void add(ListenerCallQueue.Event<L> event, Object label) {
      this.waitQueue.add(event);
      this.labelQueue.add(label);
    }
    
    void dispatch() {
      boolean scheduleEventRunner = false;
      synchronized (this) {
        if (!this.isThreadScheduled) {
          this.isThreadScheduled = true;
          scheduleEventRunner = true;
        } 
      } 
      if (scheduleEventRunner)
        try {
          this.executor.execute(this);
        } catch (Exception e) {
          synchronized (this) {
            this.isThreadScheduled = false;
          } 
          ListenerCallQueue.logger
            .get()
            .log(Level.SEVERE, "Exception while running callbacks for " + this.listener + " on " + this.executor, e);
          throw e;
        }  
    }
    
    public void run() {
      boolean stillRunning = true;
      try {
        while (true) {
          ListenerCallQueue.Event<L> nextToRun;
          Object nextLabel;
          synchronized (this) {
            Preconditions.checkState(this.isThreadScheduled);
            nextToRun = this.waitQueue.poll();
            nextLabel = this.labelQueue.poll();
            if (nextToRun == null) {
              this.isThreadScheduled = false;
              stillRunning = false;
              break;
            } 
          } 
          try {
            nextToRun.call(this.listener);
          } catch (Exception e) {
            ListenerCallQueue.logger
              .get()
              .log(Level.SEVERE, "Exception while executing callback: " + this.listener + " " + nextLabel, e);
          } 
        } 
      } finally {
        if (stillRunning)
          synchronized (this) {
            this.isThreadScheduled = false;
          }  
      } 
    }
  }
  
  static interface Event<L> {
    void call(L param1L);
  }
}
