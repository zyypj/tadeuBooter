package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.logging.Level;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.collect.Sets;
import me.syncwrld.booter.libs.google.j2objc.annotations.ReflectionSupport;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
@ReflectionSupport(ReflectionSupport.Level.FULL)
abstract class AggregateFutureState<OutputT> extends AbstractFuture.TrustedFuture<OutputT> {
  static {
    AtomicHelper helper;
  }
  
  @CheckForNull
  private volatile Set<Throwable> seenExceptions = null;
  
  private volatile int remaining;
  
  private static final AtomicHelper ATOMIC_HELPER;
  
  private static final LazyLogger log = new LazyLogger(AggregateFutureState.class);
  
  static {
    Throwable thrownReflectionFailure = null;
    try {
      helper = new SafeAtomicHelper(AtomicReferenceFieldUpdater.newUpdater(AggregateFutureState.class, Set.class, "seenExceptions"), AtomicIntegerFieldUpdater.newUpdater(AggregateFutureState.class, "remaining"));
    } catch (Throwable reflectionFailure) {
      thrownReflectionFailure = reflectionFailure;
      helper = new SynchronizedAtomicHelper();
    } 
    ATOMIC_HELPER = helper;
    if (thrownReflectionFailure != null)
      log.get().log(Level.SEVERE, "SafeAtomicHelper is broken!", thrownReflectionFailure); 
  }
  
  AggregateFutureState(int remainingFutures) {
    this.remaining = remainingFutures;
  }
  
  final Set<Throwable> getOrInitSeenExceptions() {
    Set<Throwable> seenExceptionsLocal = this.seenExceptions;
    if (seenExceptionsLocal == null) {
      seenExceptionsLocal = Sets.newConcurrentHashSet();
      addInitialException(seenExceptionsLocal);
      ATOMIC_HELPER.compareAndSetSeenExceptions(this, null, seenExceptionsLocal);
      seenExceptionsLocal = Objects.<Set<Throwable>>requireNonNull(this.seenExceptions);
    } 
    return seenExceptionsLocal;
  }
  
  final int decrementRemainingAndGet() {
    return ATOMIC_HELPER.decrementAndGetRemainingCount(this);
  }
  
  final void clearSeenExceptions() {
    this.seenExceptions = null;
  }
  
  abstract void addInitialException(Set<Throwable> paramSet);
  
  private static abstract class AtomicHelper {
    private AtomicHelper() {}
    
    abstract void compareAndSetSeenExceptions(AggregateFutureState<?> param1AggregateFutureState, @CheckForNull Set<Throwable> param1Set1, Set<Throwable> param1Set2);
    
    abstract int decrementAndGetRemainingCount(AggregateFutureState<?> param1AggregateFutureState);
  }
  
  private static final class SafeAtomicHelper extends AtomicHelper {
    final AtomicReferenceFieldUpdater<AggregateFutureState<?>, Set<Throwable>> seenExceptionsUpdater;
    
    final AtomicIntegerFieldUpdater<AggregateFutureState<?>> remainingCountUpdater;
    
    SafeAtomicHelper(AtomicReferenceFieldUpdater<AggregateFutureState<?>, Set<Throwable>> seenExceptionsUpdater, AtomicIntegerFieldUpdater<AggregateFutureState<?>> remainingCountUpdater) {
      this.seenExceptionsUpdater = seenExceptionsUpdater;
      this.remainingCountUpdater = remainingCountUpdater;
    }
    
    void compareAndSetSeenExceptions(AggregateFutureState<?> state, @CheckForNull Set<Throwable> expect, Set<Throwable> update) {
      this.seenExceptionsUpdater.compareAndSet(state, expect, update);
    }
    
    int decrementAndGetRemainingCount(AggregateFutureState<?> state) {
      return this.remainingCountUpdater.decrementAndGet(state);
    }
  }
  
  private static final class SynchronizedAtomicHelper extends AtomicHelper {
    private SynchronizedAtomicHelper() {}
    
    void compareAndSetSeenExceptions(AggregateFutureState<?> state, @CheckForNull Set<Throwable> expect, Set<Throwable> update) {
      synchronized (state) {
        if (state.seenExceptions == expect)
          state.seenExceptions = update; 
      } 
    }
    
    int decrementAndGetRemainingCount(AggregateFutureState<?> state) {
      synchronized (state) {
        return --state.remaining;
      } 
    }
  }
}
