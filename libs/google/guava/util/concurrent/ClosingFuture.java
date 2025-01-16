package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.io.Closeable;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Functions;
import me.syncwrld.booter.libs.google.guava.base.MoreObjects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.FluentIterable;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableList;
import me.syncwrld.booter.libs.google.guava.collect.Lists;
import me.syncwrld.booter.libs.google.j2objc.annotations.RetainedWith;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Use ClosingFuture.from(Futures.immediate*Future)")
@ElementTypesAreNonnullByDefault
@J2ktIncompatible
public final class ClosingFuture<V> {
  private static final LazyLogger logger = new LazyLogger(ClosingFuture.class);
  
  public static final class DeferredCloser {
    @RetainedWith
    private final ClosingFuture.CloseableList list;
    
    DeferredCloser(ClosingFuture.CloseableList list) {
      this.list = list;
    }
    
    @ParametricNullness
    @CanIgnoreReturnValue
    public <C extends AutoCloseable> C eventuallyClose(@ParametricNullness C closeable, Executor closingExecutor) {
      Preconditions.checkNotNull(closingExecutor);
      if (closeable != null)
        this.list.add((AutoCloseable)closeable, closingExecutor); 
      return closeable;
    }
  }
  
  public static final class ValueAndCloser<V> {
    private final ClosingFuture<? extends V> closingFuture;
    
    ValueAndCloser(ClosingFuture<? extends V> closingFuture) {
      this.closingFuture = (ClosingFuture<? extends V>)Preconditions.checkNotNull(closingFuture);
    }
    
    @ParametricNullness
    public V get() throws ExecutionException {
      return Futures.getDone(this.closingFuture.future);
    }
    
    public void closeAsync() {
      this.closingFuture.close();
    }
  }
  
  public static <V> ClosingFuture<V> submit(ClosingCallable<V> callable, Executor executor) {
    return new ClosingFuture<>(callable, executor);
  }
  
  public static <V> ClosingFuture<V> submitAsync(AsyncClosingCallable<V> callable, Executor executor) {
    return new ClosingFuture<>(callable, executor);
  }
  
  public static <V> ClosingFuture<V> from(ListenableFuture<V> future) {
    return new ClosingFuture<>(future);
  }
  
  @Deprecated
  public static <C extends AutoCloseable> ClosingFuture<C> eventuallyClosing(ListenableFuture<C> future, final Executor closingExecutor) {
    Preconditions.checkNotNull(closingExecutor);
    final ClosingFuture<C> closingFuture = new ClosingFuture<>(Futures.nonCancellationPropagating(future));
    Futures.addCallback(future, (FutureCallback)new FutureCallback<AutoCloseable>() {
          public void onSuccess(@CheckForNull AutoCloseable result) {
            closingFuture.closeables.closer.eventuallyClose(result, closingExecutor);
          }
          
          public void onFailure(Throwable t) {}
        }MoreExecutors.directExecutor());
    return closingFuture;
  }
  
  public static Combiner whenAllComplete(Iterable<? extends ClosingFuture<?>> futures) {
    return new Combiner(false, futures);
  }
  
  public static Combiner whenAllComplete(ClosingFuture<?> future1, ClosingFuture<?>... moreFutures) {
    return whenAllComplete(Lists.asList(future1, (Object[])moreFutures));
  }
  
  public static Combiner whenAllSucceed(Iterable<? extends ClosingFuture<?>> futures) {
    return new Combiner(true, futures);
  }
  
  public static <V1, V2> Combiner2<V1, V2> whenAllSucceed(ClosingFuture<V1> future1, ClosingFuture<V2> future2) {
    return new Combiner2<>(future1, future2);
  }
  
  public static <V1, V2, V3> Combiner3<V1, V2, V3> whenAllSucceed(ClosingFuture<V1> future1, ClosingFuture<V2> future2, ClosingFuture<V3> future3) {
    return new Combiner3<>(future1, future2, future3);
  }
  
  public static <V1, V2, V3, V4> Combiner4<V1, V2, V3, V4> whenAllSucceed(ClosingFuture<V1> future1, ClosingFuture<V2> future2, ClosingFuture<V3> future3, ClosingFuture<V4> future4) {
    return new Combiner4<>(future1, future2, future3, future4);
  }
  
  public static <V1, V2, V3, V4, V5> Combiner5<V1, V2, V3, V4, V5> whenAllSucceed(ClosingFuture<V1> future1, ClosingFuture<V2> future2, ClosingFuture<V3> future3, ClosingFuture<V4> future4, ClosingFuture<V5> future5) {
    return new Combiner5<>(future1, future2, future3, future4, future5);
  }
  
  public static Combiner whenAllSucceed(ClosingFuture<?> future1, ClosingFuture<?> future2, ClosingFuture<?> future3, ClosingFuture<?> future4, ClosingFuture<?> future5, ClosingFuture<?> future6, ClosingFuture<?>... moreFutures) {
    return whenAllSucceed(
        (Iterable<? extends ClosingFuture<?>>)FluentIterable.of(future1, (Object[])new ClosingFuture[] { future2, future3, future4, future5, future6 }).append((Object[])moreFutures));
  }
  
  private final AtomicReference<State> state = new AtomicReference<>(State.OPEN);
  
  private final CloseableList closeables = new CloseableList();
  
  private final FluentFuture<V> future;
  
  private ClosingFuture(ListenableFuture<V> future) {
    this.future = FluentFuture.from(future);
  }
  
  private ClosingFuture(final ClosingCallable<V> callable, Executor executor) {
    Preconditions.checkNotNull(callable);
    TrustedListenableFutureTask<V> task = TrustedListenableFutureTask.create(new Callable<V>() {
          @ParametricNullness
          public V call() throws Exception {
            return callable.call(ClosingFuture.this.closeables.closer);
          }
          
          public String toString() {
            return callable.toString();
          }
        });
    executor.execute(task);
    this.future = task;
  }
  
  private ClosingFuture(final AsyncClosingCallable<V> callable, Executor executor) {
    Preconditions.checkNotNull(callable);
    TrustedListenableFutureTask<V> task = TrustedListenableFutureTask.create(new AsyncCallable<V>() {
          public ListenableFuture<V> call() throws Exception {
            ClosingFuture.CloseableList newCloseables = new ClosingFuture.CloseableList();
            try {
              ClosingFuture<V> closingFuture = callable.call(newCloseables.closer);
              closingFuture.becomeSubsumedInto(ClosingFuture.this.closeables);
              return closingFuture.future;
            } finally {
              ClosingFuture.this.closeables.add(newCloseables, MoreExecutors.directExecutor());
            } 
          }
          
          public String toString() {
            return callable.toString();
          }
        });
    executor.execute(task);
    this.future = task;
  }
  
  public ListenableFuture<?> statusFuture() {
    return Futures.nonCancellationPropagating(this.future.transform(Functions.constant(null), MoreExecutors.directExecutor()));
  }
  
  public <U> ClosingFuture<U> transform(final ClosingFunction<? super V, U> function, Executor executor) {
    Preconditions.checkNotNull(function);
    AsyncFunction<V, U> applyFunction = new AsyncFunction<V, U>() {
        public ListenableFuture<U> apply(V input) throws Exception {
          return ClosingFuture.this.closeables.applyClosingFunction(function, input);
        }
        
        public String toString() {
          return function.toString();
        }
      };
    return derive(this.future.transformAsync(applyFunction, executor));
  }
  
  public <U> ClosingFuture<U> transformAsync(final AsyncClosingFunction<? super V, U> function, Executor executor) {
    Preconditions.checkNotNull(function);
    AsyncFunction<V, U> applyFunction = new AsyncFunction<V, U>() {
        public ListenableFuture<U> apply(V input) throws Exception {
          return ClosingFuture.this.closeables.applyAsyncClosingFunction(function, input);
        }
        
        public String toString() {
          return function.toString();
        }
      };
    return derive(this.future.transformAsync(applyFunction, executor));
  }
  
  public static <V, U> AsyncClosingFunction<V, U> withoutCloser(final AsyncFunction<V, U> function) {
    Preconditions.checkNotNull(function);
    return new AsyncClosingFunction<V, U>() {
        public ClosingFuture<U> apply(ClosingFuture.DeferredCloser closer, V input) throws Exception {
          return ClosingFuture.from(function.apply(input));
        }
      };
  }
  
  public <X extends Throwable> ClosingFuture<V> catching(Class<X> exceptionType, ClosingFunction<? super X, ? extends V> fallback, Executor executor) {
    return catchingMoreGeneric(exceptionType, fallback, executor);
  }
  
  private <X extends Throwable, W extends V> ClosingFuture<V> catchingMoreGeneric(Class<X> exceptionType, final ClosingFunction<? super X, W> fallback, Executor executor) {
    Preconditions.checkNotNull(fallback);
    AsyncFunction<X, W> applyFallback = new AsyncFunction<X, W>() {
        public ListenableFuture<W> apply(X exception) throws Exception {
          return ClosingFuture.this.closeables.applyClosingFunction(fallback, exception);
        }
        
        public String toString() {
          return fallback.toString();
        }
      };
    return derive(this.future.catchingAsync(exceptionType, applyFallback, executor));
  }
  
  public <X extends Throwable> ClosingFuture<V> catchingAsync(Class<X> exceptionType, AsyncClosingFunction<? super X, ? extends V> fallback, Executor executor) {
    return catchingAsyncMoreGeneric(exceptionType, fallback, executor);
  }
  
  private <X extends Throwable, W extends V> ClosingFuture<V> catchingAsyncMoreGeneric(Class<X> exceptionType, final AsyncClosingFunction<? super X, W> fallback, Executor executor) {
    Preconditions.checkNotNull(fallback);
    AsyncFunction<X, W> asyncFunction = new AsyncFunction<X, W>() {
        public ListenableFuture<W> apply(X exception) throws Exception {
          return ClosingFuture.this.closeables.applyAsyncClosingFunction(fallback, exception);
        }
        
        public String toString() {
          return fallback.toString();
        }
      };
    return derive(this.future.catchingAsync(exceptionType, asyncFunction, executor));
  }
  
  public FluentFuture<V> finishToFuture() {
    if (compareAndUpdateState(State.OPEN, State.WILL_CLOSE)) {
      logger.get().log(Level.FINER, "will close {0}", this);
      this.future.addListener(new Runnable() {
            public void run() {
              ClosingFuture.this.checkAndUpdateState(ClosingFuture.State.WILL_CLOSE, ClosingFuture.State.CLOSING);
              ClosingFuture.this.close();
              ClosingFuture.this.checkAndUpdateState(ClosingFuture.State.CLOSING, ClosingFuture.State.CLOSED);
            }
          }MoreExecutors.directExecutor());
    } else {
      switch ((State)this.state.get()) {
        case SUBSUMED:
          throw new IllegalStateException("Cannot call finishToFuture() after deriving another step");
        case WILL_CREATE_VALUE_AND_CLOSER:
          throw new IllegalStateException("Cannot call finishToFuture() after calling finishToValueAndCloser()");
        case WILL_CLOSE:
        case CLOSING:
        case CLOSED:
          throw new IllegalStateException("Cannot call finishToFuture() twice");
        case OPEN:
          throw new AssertionError();
      } 
    } 
    return this.future;
  }
  
  public void finishToValueAndCloser(final ValueAndCloserConsumer<? super V> consumer, Executor executor) {
    Preconditions.checkNotNull(consumer);
    if (!compareAndUpdateState(State.OPEN, State.WILL_CREATE_VALUE_AND_CLOSER)) {
      switch ((State)this.state.get()) {
        case SUBSUMED:
          throw new IllegalStateException("Cannot call finishToValueAndCloser() after deriving another step");
        case WILL_CLOSE:
        case CLOSING:
        case CLOSED:
          throw new IllegalStateException("Cannot call finishToValueAndCloser() after calling finishToFuture()");
        case WILL_CREATE_VALUE_AND_CLOSER:
          throw new IllegalStateException("Cannot call finishToValueAndCloser() twice");
      } 
      throw new AssertionError(this.state);
    } 
    this.future.addListener(new Runnable() {
          public void run() {
            ClosingFuture.provideValueAndCloser(consumer, ClosingFuture.this);
          }
        }executor);
  }
  
  private static <C, V extends C> void provideValueAndCloser(ValueAndCloserConsumer<C> consumer, ClosingFuture<V> closingFuture) {
    consumer.accept(new ValueAndCloser<>(closingFuture));
  }
  
  @CanIgnoreReturnValue
  public boolean cancel(boolean mayInterruptIfRunning) {
    logger.get().log(Level.FINER, "cancelling {0}", this);
    boolean cancelled = this.future.cancel(mayInterruptIfRunning);
    if (cancelled)
      close(); 
    return cancelled;
  }
  
  private void close() {
    logger.get().log(Level.FINER, "closing {0}", this);
    this.closeables.close();
  }
  
  private <U> ClosingFuture<U> derive(FluentFuture<U> future) {
    ClosingFuture<U> derived = new ClosingFuture(future);
    becomeSubsumedInto(derived.closeables);
    return derived;
  }
  
  private void becomeSubsumedInto(CloseableList otherCloseables) {
    checkAndUpdateState(State.OPEN, State.SUBSUMED);
    otherCloseables.add(this.closeables, MoreExecutors.directExecutor());
  }
  
  public static final class Peeker {
    private final ImmutableList<ClosingFuture<?>> futures;
    
    private volatile boolean beingCalled;
    
    private Peeker(ImmutableList<ClosingFuture<?>> futures) {
      this.futures = (ImmutableList<ClosingFuture<?>>)Preconditions.checkNotNull(futures);
    }
    
    @ParametricNullness
    public final <D> D getDone(ClosingFuture<D> closingFuture) throws ExecutionException {
      Preconditions.checkState(this.beingCalled);
      Preconditions.checkArgument(this.futures.contains(closingFuture));
      return Futures.getDone(closingFuture.future);
    }
    
    @ParametricNullness
    private <V> V call(ClosingFuture.Combiner.CombiningCallable<V> combiner, ClosingFuture.CloseableList closeables) throws Exception {
      this.beingCalled = true;
      ClosingFuture.CloseableList newCloseables = new ClosingFuture.CloseableList();
      try {
        return combiner.call(newCloseables.closer, this);
      } finally {
        closeables.add(newCloseables, MoreExecutors.directExecutor());
        this.beingCalled = false;
      } 
    }
    
    private <V> FluentFuture<V> callAsync(ClosingFuture.Combiner.AsyncCombiningCallable<V> combiner, ClosingFuture.CloseableList closeables) throws Exception {
      this.beingCalled = true;
      ClosingFuture.CloseableList newCloseables = new ClosingFuture.CloseableList();
      try {
        ClosingFuture<V> closingFuture = combiner.call(newCloseables.closer, this);
        closingFuture.becomeSubsumedInto(closeables);
        return closingFuture.future;
      } finally {
        closeables.add(newCloseables, MoreExecutors.directExecutor());
        this.beingCalled = false;
      } 
    }
  }
  
  @DoNotMock("Use ClosingFuture.whenAllSucceed() or .whenAllComplete() instead.")
  public static class Combiner {
    private final ClosingFuture.CloseableList closeables = new ClosingFuture.CloseableList();
    
    private final boolean allMustSucceed;
    
    protected final ImmutableList<ClosingFuture<?>> inputs;
    
    private Combiner(boolean allMustSucceed, Iterable<? extends ClosingFuture<?>> inputs) {
      this.allMustSucceed = allMustSucceed;
      this.inputs = ImmutableList.copyOf(inputs);
      for (ClosingFuture<?> input : inputs)
        input.becomeSubsumedInto(this.closeables); 
    }
    
    public <V> ClosingFuture<V> call(final CombiningCallable<V> combiningCallable, Executor executor) {
      Callable<V> callable = new Callable<V>() {
          @ParametricNullness
          public V call() throws Exception {
            return (new ClosingFuture.Peeker(ClosingFuture.Combiner.this.inputs)).call(combiningCallable, ClosingFuture.Combiner.this.closeables);
          }
          
          public String toString() {
            return combiningCallable.toString();
          }
        };
      ClosingFuture<V> derived = new ClosingFuture<>(futureCombiner().call(callable, executor));
      derived.closeables.add(this.closeables, MoreExecutors.directExecutor());
      return derived;
    }
    
    public <V> ClosingFuture<V> callAsync(final AsyncCombiningCallable<V> combiningCallable, Executor executor) {
      AsyncCallable<V> asyncCallable = new AsyncCallable<V>() {
          public ListenableFuture<V> call() throws Exception {
            return (new ClosingFuture.Peeker(ClosingFuture.Combiner.this.inputs)).callAsync(combiningCallable, ClosingFuture.Combiner.this.closeables);
          }
          
          public String toString() {
            return combiningCallable.toString();
          }
        };
      ClosingFuture<V> derived = new ClosingFuture<>(futureCombiner().callAsync(asyncCallable, executor));
      derived.closeables.add(this.closeables, MoreExecutors.directExecutor());
      return derived;
    }
    
    private Futures.FutureCombiner<Object> futureCombiner() {
      return this.allMustSucceed ? 
        Futures.<Object>whenAllSucceed((Iterable)inputFutures()) : 
        Futures.<Object>whenAllComplete((Iterable)inputFutures());
    }
    
    private ImmutableList<FluentFuture<?>> inputFutures() {
      return FluentIterable.from((Iterable)this.inputs)
        .transform(future -> future.future)
        .toList();
    }
    
    @FunctionalInterface
    public static interface CombiningCallable<V> {
      @ParametricNullness
      V call(ClosingFuture.DeferredCloser param2DeferredCloser, ClosingFuture.Peeker param2Peeker) throws Exception;
    }
    
    @FunctionalInterface
    public static interface AsyncCombiningCallable<V> {
      ClosingFuture<V> call(ClosingFuture.DeferredCloser param2DeferredCloser, ClosingFuture.Peeker param2Peeker) throws Exception;
    }
  }
  
  public static final class Combiner2<V1, V2> extends Combiner {
    private final ClosingFuture<V1> future1;
    
    private final ClosingFuture<V2> future2;
    
    private Combiner2(ClosingFuture<V1> future1, ClosingFuture<V2> future2) {
      super(true, (Iterable)ImmutableList.of(future1, future2));
      this.future1 = future1;
      this.future2 = future2;
    }
    
    public <U> ClosingFuture<U> call(final ClosingFunction2<V1, V2, U> function, Executor executor) {
      return call(new ClosingFuture.Combiner.CombiningCallable<U>() {
            @ParametricNullness
            public U call(ClosingFuture.DeferredCloser closer, ClosingFuture.Peeker peeker) throws Exception {
              return (U)function.apply(closer, peeker.getDone(ClosingFuture.Combiner2.this.future1), peeker.getDone(ClosingFuture.Combiner2.this.future2));
            }
            
            public String toString() {
              return function.toString();
            }
          }executor);
    }
    
    public <U> ClosingFuture<U> callAsync(final AsyncClosingFunction2<V1, V2, U> function, Executor executor) {
      return callAsync(new ClosingFuture.Combiner.AsyncCombiningCallable<U>() {
            public ClosingFuture<U> call(ClosingFuture.DeferredCloser closer, ClosingFuture.Peeker peeker) throws Exception {
              return function.apply(closer, peeker.getDone(ClosingFuture.Combiner2.this.future1), peeker.getDone(ClosingFuture.Combiner2.this.future2));
            }
            
            public String toString() {
              return function.toString();
            }
          }executor);
    }
    
    @FunctionalInterface
    public static interface ClosingFunction2<V1, V2, U> {
      @ParametricNullness
      U apply(ClosingFuture.DeferredCloser param2DeferredCloser, @ParametricNullness V1 param2V1, @ParametricNullness V2 param2V2) throws Exception;
    }
    
    @FunctionalInterface
    public static interface AsyncClosingFunction2<V1, V2, U> {
      ClosingFuture<U> apply(ClosingFuture.DeferredCloser param2DeferredCloser, @ParametricNullness V1 param2V1, @ParametricNullness V2 param2V2) throws Exception;
    }
  }
  
  public static final class Combiner3<V1, V2, V3> extends Combiner {
    private final ClosingFuture<V1> future1;
    
    private final ClosingFuture<V2> future2;
    
    private final ClosingFuture<V3> future3;
    
    private Combiner3(ClosingFuture<V1> future1, ClosingFuture<V2> future2, ClosingFuture<V3> future3) {
      super(true, (Iterable)ImmutableList.of(future1, future2, future3));
      this.future1 = future1;
      this.future2 = future2;
      this.future3 = future3;
    }
    
    public <U> ClosingFuture<U> call(final ClosingFunction3<V1, V2, V3, U> function, Executor executor) {
      return call(new ClosingFuture.Combiner.CombiningCallable<U>() {
            @ParametricNullness
            public U call(ClosingFuture.DeferredCloser closer, ClosingFuture.Peeker peeker) throws Exception {
              return (U)function.apply(closer, peeker
                  
                  .getDone(ClosingFuture.Combiner3.this.future1), peeker
                  .getDone(ClosingFuture.Combiner3.this.future2), peeker
                  .getDone(ClosingFuture.Combiner3.this.future3));
            }
            
            public String toString() {
              return function.toString();
            }
          }executor);
    }
    
    public <U> ClosingFuture<U> callAsync(final AsyncClosingFunction3<V1, V2, V3, U> function, Executor executor) {
      return callAsync(new ClosingFuture.Combiner.AsyncCombiningCallable<U>() {
            public ClosingFuture<U> call(ClosingFuture.DeferredCloser closer, ClosingFuture.Peeker peeker) throws Exception {
              return function.apply(closer, peeker
                  
                  .getDone(ClosingFuture.Combiner3.this.future1), peeker
                  .getDone(ClosingFuture.Combiner3.this.future2), peeker
                  .getDone(ClosingFuture.Combiner3.this.future3));
            }
            
            public String toString() {
              return function.toString();
            }
          }executor);
    }
    
    @FunctionalInterface
    public static interface ClosingFunction3<V1, V2, V3, U> {
      @ParametricNullness
      U apply(ClosingFuture.DeferredCloser param2DeferredCloser, @ParametricNullness V1 param2V1, @ParametricNullness V2 param2V2, @ParametricNullness V3 param2V3) throws Exception;
    }
    
    @FunctionalInterface
    public static interface AsyncClosingFunction3<V1, V2, V3, U> {
      ClosingFuture<U> apply(ClosingFuture.DeferredCloser param2DeferredCloser, @ParametricNullness V1 param2V1, @ParametricNullness V2 param2V2, @ParametricNullness V3 param2V3) throws Exception;
    }
  }
  
  public static final class Combiner4<V1, V2, V3, V4> extends Combiner {
    private final ClosingFuture<V1> future1;
    
    private final ClosingFuture<V2> future2;
    
    private final ClosingFuture<V3> future3;
    
    private final ClosingFuture<V4> future4;
    
    private Combiner4(ClosingFuture<V1> future1, ClosingFuture<V2> future2, ClosingFuture<V3> future3, ClosingFuture<V4> future4) {
      super(true, (Iterable)ImmutableList.of(future1, future2, future3, future4));
      this.future1 = future1;
      this.future2 = future2;
      this.future3 = future3;
      this.future4 = future4;
    }
    
    public <U> ClosingFuture<U> call(final ClosingFunction4<V1, V2, V3, V4, U> function, Executor executor) {
      return call(new ClosingFuture.Combiner.CombiningCallable<U>() {
            @ParametricNullness
            public U call(ClosingFuture.DeferredCloser closer, ClosingFuture.Peeker peeker) throws Exception {
              return (U)function.apply(closer, peeker
                  
                  .getDone(ClosingFuture.Combiner4.this.future1), peeker
                  .getDone(ClosingFuture.Combiner4.this.future2), peeker
                  .getDone(ClosingFuture.Combiner4.this.future3), peeker
                  .getDone(ClosingFuture.Combiner4.this.future4));
            }
            
            public String toString() {
              return function.toString();
            }
          }executor);
    }
    
    public <U> ClosingFuture<U> callAsync(final AsyncClosingFunction4<V1, V2, V3, V4, U> function, Executor executor) {
      return callAsync(new ClosingFuture.Combiner.AsyncCombiningCallable<U>() {
            public ClosingFuture<U> call(ClosingFuture.DeferredCloser closer, ClosingFuture.Peeker peeker) throws Exception {
              return function.apply(closer, peeker
                  
                  .getDone(ClosingFuture.Combiner4.this.future1), peeker
                  .getDone(ClosingFuture.Combiner4.this.future2), peeker
                  .getDone(ClosingFuture.Combiner4.this.future3), peeker
                  .getDone(ClosingFuture.Combiner4.this.future4));
            }
            
            public String toString() {
              return function.toString();
            }
          }executor);
    }
    
    @FunctionalInterface
    public static interface ClosingFunction4<V1, V2, V3, V4, U> {
      @ParametricNullness
      U apply(ClosingFuture.DeferredCloser param2DeferredCloser, @ParametricNullness V1 param2V1, @ParametricNullness V2 param2V2, @ParametricNullness V3 param2V3, @ParametricNullness V4 param2V4) throws Exception;
    }
    
    @FunctionalInterface
    public static interface AsyncClosingFunction4<V1, V2, V3, V4, U> {
      ClosingFuture<U> apply(ClosingFuture.DeferredCloser param2DeferredCloser, @ParametricNullness V1 param2V1, @ParametricNullness V2 param2V2, @ParametricNullness V3 param2V3, @ParametricNullness V4 param2V4) throws Exception;
    }
  }
  
  public static final class Combiner5<V1, V2, V3, V4, V5> extends Combiner {
    private final ClosingFuture<V1> future1;
    
    private final ClosingFuture<V2> future2;
    
    private final ClosingFuture<V3> future3;
    
    private final ClosingFuture<V4> future4;
    
    private final ClosingFuture<V5> future5;
    
    private Combiner5(ClosingFuture<V1> future1, ClosingFuture<V2> future2, ClosingFuture<V3> future3, ClosingFuture<V4> future4, ClosingFuture<V5> future5) {
      super(true, (Iterable)ImmutableList.of(future1, future2, future3, future4, future5));
      this.future1 = future1;
      this.future2 = future2;
      this.future3 = future3;
      this.future4 = future4;
      this.future5 = future5;
    }
    
    public <U> ClosingFuture<U> call(final ClosingFunction5<V1, V2, V3, V4, V5, U> function, Executor executor) {
      return call(new ClosingFuture.Combiner.CombiningCallable<U>() {
            @ParametricNullness
            public U call(ClosingFuture.DeferredCloser closer, ClosingFuture.Peeker peeker) throws Exception {
              return (U)function.apply(closer, peeker
                  
                  .getDone(ClosingFuture.Combiner5.this.future1), peeker
                  .getDone(ClosingFuture.Combiner5.this.future2), peeker
                  .getDone(ClosingFuture.Combiner5.this.future3), peeker
                  .getDone(ClosingFuture.Combiner5.this.future4), peeker
                  .getDone(ClosingFuture.Combiner5.this.future5));
            }
            
            public String toString() {
              return function.toString();
            }
          }executor);
    }
    
    public <U> ClosingFuture<U> callAsync(final AsyncClosingFunction5<V1, V2, V3, V4, V5, U> function, Executor executor) {
      return callAsync(new ClosingFuture.Combiner.AsyncCombiningCallable<U>() {
            public ClosingFuture<U> call(ClosingFuture.DeferredCloser closer, ClosingFuture.Peeker peeker) throws Exception {
              return function.apply(closer, peeker
                  
                  .getDone(ClosingFuture.Combiner5.this.future1), peeker
                  .getDone(ClosingFuture.Combiner5.this.future2), peeker
                  .getDone(ClosingFuture.Combiner5.this.future3), peeker
                  .getDone(ClosingFuture.Combiner5.this.future4), peeker
                  .getDone(ClosingFuture.Combiner5.this.future5));
            }
            
            public String toString() {
              return function.toString();
            }
          }executor);
    }
    
    @FunctionalInterface
    public static interface ClosingFunction5<V1, V2, V3, V4, V5, U> {
      @ParametricNullness
      U apply(ClosingFuture.DeferredCloser param2DeferredCloser, @ParametricNullness V1 param2V1, @ParametricNullness V2 param2V2, @ParametricNullness V3 param2V3, @ParametricNullness V4 param2V4, @ParametricNullness V5 param2V5) throws Exception;
    }
    
    @FunctionalInterface
    public static interface AsyncClosingFunction5<V1, V2, V3, V4, V5, U> {
      ClosingFuture<U> apply(ClosingFuture.DeferredCloser param2DeferredCloser, @ParametricNullness V1 param2V1, @ParametricNullness V2 param2V2, @ParametricNullness V3 param2V3, @ParametricNullness V4 param2V4, @ParametricNullness V5 param2V5) throws Exception;
    }
  }
  
  public String toString() {
    return MoreObjects.toStringHelper(this).add("state", this.state.get()).addValue(this.future).toString();
  }
  
  protected void finalize() {
    if (((State)this.state.get()).equals(State.OPEN)) {
      logger.get().log(Level.SEVERE, "Uh oh! An open ClosingFuture has leaked and will close: {0}", this);
      FluentFuture<V> fluentFuture = finishToFuture();
    } 
  }
  
  private static void closeQuietly(@CheckForNull AutoCloseable closeable, Executor executor) {
    if (closeable == null)
      return; 
    try {
      executor.execute(() -> {
            try {
              closeable.close();
            } catch (Exception e) {
              Platform.restoreInterruptIfIsInterruptedException(e);
              logger.get().log(Level.WARNING, "thrown by close()", e);
            } 
          });
    } catch (RejectedExecutionException e) {
      if (logger.get().isLoggable(Level.WARNING))
        logger
          .get()
          .log(Level.WARNING, 
            
            String.format("while submitting close to %s; will close inline", new Object[] { executor }), e); 
      closeQuietly(closeable, MoreExecutors.directExecutor());
    } 
  }
  
  private void checkAndUpdateState(State oldState, State newState) {
    Preconditions.checkState(
        compareAndUpdateState(oldState, newState), "Expected state to be %s, but it was %s", oldState, newState);
  }
  
  private boolean compareAndUpdateState(State oldState, State newState) {
    return this.state.compareAndSet(oldState, newState);
  }
  
  private static final class CloseableList extends IdentityHashMap<AutoCloseable, Executor> implements Closeable {
    private final ClosingFuture.DeferredCloser closer = new ClosingFuture.DeferredCloser(this);
    
    private volatile boolean closed;
    
    @CheckForNull
    private volatile CountDownLatch whenClosed;
    
    <V, U> ListenableFuture<U> applyClosingFunction(ClosingFuture.ClosingFunction<? super V, U> transformation, @ParametricNullness V input) throws Exception {
      CloseableList newCloseables = new CloseableList();
      try {
        return (ListenableFuture)Futures.immediateFuture(transformation.apply(newCloseables.closer, input));
      } finally {
        add(newCloseables, MoreExecutors.directExecutor());
      } 
    }
    
    <V, U> FluentFuture<U> applyAsyncClosingFunction(ClosingFuture.AsyncClosingFunction<V, U> transformation, @ParametricNullness V input) throws Exception {
      CloseableList newCloseables = new CloseableList();
      try {
        ClosingFuture<U> closingFuture = transformation.apply(newCloseables.closer, input);
        closingFuture.becomeSubsumedInto(newCloseables);
        return closingFuture.future;
      } finally {
        add(newCloseables, MoreExecutors.directExecutor());
      } 
    }
    
    public void close() {
      if (this.closed)
        return; 
      synchronized (this) {
        if (this.closed)
          return; 
        this.closed = true;
      } 
      for (Map.Entry<AutoCloseable, Executor> entry : entrySet())
        ClosingFuture.closeQuietly(entry.getKey(), entry.getValue()); 
      clear();
      if (this.whenClosed != null)
        this.whenClosed.countDown(); 
    }
    
    void add(@CheckForNull AutoCloseable closeable, Executor executor) {
      Preconditions.checkNotNull(executor);
      if (closeable == null)
        return; 
      synchronized (this) {
        if (!this.closed) {
          put(closeable, executor);
          return;
        } 
      } 
      ClosingFuture.closeQuietly(closeable, executor);
    }
    
    CountDownLatch whenClosedCountDown() {
      if (this.closed)
        return new CountDownLatch(0); 
      synchronized (this) {
        if (this.closed)
          return new CountDownLatch(0); 
        Preconditions.checkState((this.whenClosed == null));
        return this.whenClosed = new CountDownLatch(1);
      } 
    }
    
    private CloseableList() {}
  }
  
  @VisibleForTesting
  CountDownLatch whenClosedCountDown() {
    return this.closeables.whenClosedCountDown();
  }
  
  enum State {
    OPEN, SUBSUMED, WILL_CLOSE, CLOSING, CLOSED, WILL_CREATE_VALUE_AND_CLOSER;
  }
  
  @FunctionalInterface
  public static interface ValueAndCloserConsumer<V> {
    void accept(ClosingFuture.ValueAndCloser<V> param1ValueAndCloser);
  }
  
  @FunctionalInterface
  public static interface AsyncClosingFunction<T, U> {
    ClosingFuture<U> apply(ClosingFuture.DeferredCloser param1DeferredCloser, @ParametricNullness T param1T) throws Exception;
  }
  
  @FunctionalInterface
  public static interface ClosingFunction<T, U> {
    @ParametricNullness
    U apply(ClosingFuture.DeferredCloser param1DeferredCloser, @ParametricNullness T param1T) throws Exception;
  }
  
  @FunctionalInterface
  public static interface AsyncClosingCallable<V> {
    ClosingFuture<V> call(ClosingFuture.DeferredCloser param1DeferredCloser) throws Exception;
  }
  
  @FunctionalInterface
  public static interface ClosingCallable<V> {
    @ParametricNullness
    V call(ClosingFuture.DeferredCloser param1DeferredCloser) throws Exception;
  }
}
