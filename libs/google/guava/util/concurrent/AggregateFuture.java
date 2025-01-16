package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import me.syncwrld.booter.libs.google.errorprone.annotations.ForOverride;
import me.syncwrld.booter.libs.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableCollection;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AggregateFuture<InputT, OutputT> extends AggregateFutureState<OutputT> {
  private static final LazyLogger logger = new LazyLogger(AggregateFuture.class);
  
  @CheckForNull
  private ImmutableCollection<? extends ListenableFuture<? extends InputT>> futures;
  
  private final boolean allMustSucceed;
  
  private final boolean collectsValues;
  
  AggregateFuture(ImmutableCollection<? extends ListenableFuture<? extends InputT>> futures, boolean allMustSucceed, boolean collectsValues) {
    super(futures.size());
    this.futures = (ImmutableCollection<? extends ListenableFuture<? extends InputT>>)Preconditions.checkNotNull(futures);
    this.allMustSucceed = allMustSucceed;
    this.collectsValues = collectsValues;
  }
  
  protected final void afterDone() {
    super.afterDone();
    ImmutableCollection<? extends Future<?>> localFutures = (ImmutableCollection)this.futures;
    releaseResources(ReleaseResourcesReason.OUTPUT_FUTURE_DONE);
    if ((isCancelled() & ((localFutures != null) ? 1 : 0)) != 0) {
      boolean wasInterrupted = wasInterrupted();
      for (UnmodifiableIterator<Future> unmodifiableIterator = localFutures.iterator(); unmodifiableIterator.hasNext(); ) {
        Future<?> future = unmodifiableIterator.next();
        future.cancel(wasInterrupted);
      } 
    } 
  }
  
  @CheckForNull
  protected final String pendingToString() {
    ImmutableCollection<? extends Future<?>> localFutures = (ImmutableCollection)this.futures;
    if (localFutures != null)
      return "futures=" + localFutures; 
    return super.pendingToString();
  }
  
  final void init() {
    Objects.requireNonNull(this.futures);
    if (this.futures.isEmpty()) {
      handleAllCompleted();
      return;
    } 
    if (this.allMustSucceed) {
      int i = 0;
      for (UnmodifiableIterator<ListenableFuture<? extends InputT>> unmodifiableIterator = this.futures.iterator(); unmodifiableIterator.hasNext(); ) {
        ListenableFuture<? extends InputT> future = unmodifiableIterator.next();
        int index = i++;
        future.addListener(() -> {
              try {
                if (future.isCancelled()) {
                  this.futures = null;
                  cancel(false);
                } else {
                  collectValueFromNonCancelledFuture(index, future);
                } 
              } finally {
                decrementCountAndMaybeComplete((ImmutableCollection<? extends Future<? extends InputT>>)null);
              } 
            }MoreExecutors.directExecutor());
      } 
    } else {
      ImmutableCollection<? extends ListenableFuture<? extends InputT>> immutableCollection = this.collectsValues ? this.futures : null;
      Runnable listener = () -> decrementCountAndMaybeComplete(localFutures);
      for (UnmodifiableIterator<ListenableFuture<? extends InputT>> unmodifiableIterator = this.futures.iterator(); unmodifiableIterator.hasNext(); ) {
        ListenableFuture<? extends InputT> future = unmodifiableIterator.next();
        future.addListener(listener, MoreExecutors.directExecutor());
      } 
    } 
  }
  
  private void handleException(Throwable throwable) {
    Preconditions.checkNotNull(throwable);
    if (this.allMustSucceed) {
      boolean completedWithFailure = setException(throwable);
      if (!completedWithFailure) {
        boolean firstTimeSeeingThisException = addCausalChain(getOrInitSeenExceptions(), throwable);
        if (firstTimeSeeingThisException) {
          log(throwable);
          return;
        } 
      } 
    } 
    if (throwable instanceof Error)
      log(throwable); 
  }
  
  private static void log(Throwable throwable) {
    String message = (throwable instanceof Error) ? "Input Future failed with Error" : "Got more than one input Future failure. Logging failures after the first";
    logger.get().log(Level.SEVERE, message, throwable);
  }
  
  final void addInitialException(Set<Throwable> seen) {
    Preconditions.checkNotNull(seen);
    if (!isCancelled())
      boolean bool = addCausalChain(seen, Objects.<Throwable>requireNonNull(tryInternalFastPathGetFailure())); 
  }
  
  private void collectValueFromNonCancelledFuture(int index, Future<? extends InputT> future) {
    try {
      collectOneValue(index, Futures.getDone((Future)future));
    } catch (ExecutionException e) {
      handleException(e.getCause());
    } catch (Throwable t) {
      handleException(t);
    } 
  }
  
  private void decrementCountAndMaybeComplete(@CheckForNull ImmutableCollection<? extends Future<? extends InputT>> futuresIfNeedToCollectAtCompletion) {
    int newRemaining = decrementRemainingAndGet();
    Preconditions.checkState((newRemaining >= 0), "Less than 0 remaining futures");
    if (newRemaining == 0)
      processCompleted(futuresIfNeedToCollectAtCompletion); 
  }
  
  private void processCompleted(@CheckForNull ImmutableCollection<? extends Future<? extends InputT>> futuresIfNeedToCollectAtCompletion) {
    if (futuresIfNeedToCollectAtCompletion != null) {
      int i = 0;
      for (UnmodifiableIterator<Future<? extends InputT>> unmodifiableIterator = futuresIfNeedToCollectAtCompletion.iterator(); unmodifiableIterator.hasNext(); ) {
        Future<? extends InputT> future = unmodifiableIterator.next();
        if (!future.isCancelled())
          collectValueFromNonCancelledFuture(i, future); 
        i++;
      } 
    } 
    clearSeenExceptions();
    handleAllCompleted();
    releaseResources(ReleaseResourcesReason.ALL_INPUT_FUTURES_PROCESSED);
  }
  
  @ForOverride
  @OverridingMethodsMustInvokeSuper
  void releaseResources(ReleaseResourcesReason reason) {
    Preconditions.checkNotNull(reason);
    this.futures = null;
  }
  
  enum ReleaseResourcesReason {
    OUTPUT_FUTURE_DONE, ALL_INPUT_FUTURES_PROCESSED;
  }
  
  private static boolean addCausalChain(Set<Throwable> seen, Throwable param) {
    Throwable t = param;
    for (; t != null; t = t.getCause()) {
      boolean firstTimeSeen = seen.add(t);
      if (!firstTimeSeen)
        return false; 
    } 
    return true;
  }
  
  abstract void collectOneValue(int paramInt, @ParametricNullness InputT paramInputT);
  
  abstract void handleAllCompleted();
}
