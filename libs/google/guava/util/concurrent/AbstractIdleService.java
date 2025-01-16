package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Supplier;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
@J2ktIncompatible
public abstract class AbstractIdleService implements Service {
  private final Supplier<String> threadNameSupplier = new ThreadNameSupplier();
  
  private final class ThreadNameSupplier implements Supplier<String> {
    private ThreadNameSupplier() {}
    
    public String get() {
      return AbstractIdleService.this.serviceName() + " " + AbstractIdleService.this.state();
    }
  }
  
  private final Service delegate = new DelegateService();
  
  private final class DelegateService extends AbstractService {
    private DelegateService() {}
    
    protected final void doStart() {
      MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier)
        .execute(() -> {
            try {
              AbstractIdleService.this.startUp();
              notifyStarted();
            } catch (Throwable t) {
              Platform.restoreInterruptIfIsInterruptedException(t);
              notifyFailed(t);
            } 
          });
    }
    
    protected final void doStop() {
      MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier)
        .execute(() -> {
            try {
              AbstractIdleService.this.shutDown();
              notifyStopped();
            } catch (Throwable t) {
              Platform.restoreInterruptIfIsInterruptedException(t);
              notifyFailed(t);
            } 
          });
    }
    
    public String toString() {
      return AbstractIdleService.this.toString();
    }
  }
  
  protected Executor executor() {
    return command -> MoreExecutors.newThread((String)this.threadNameSupplier.get(), command).start();
  }
  
  public String toString() {
    return serviceName() + " [" + state() + "]";
  }
  
  public final boolean isRunning() {
    return this.delegate.isRunning();
  }
  
  public final Service.State state() {
    return this.delegate.state();
  }
  
  public final void addListener(Service.Listener listener, Executor executor) {
    this.delegate.addListener(listener, executor);
  }
  
  public final Throwable failureCause() {
    return this.delegate.failureCause();
  }
  
  @CanIgnoreReturnValue
  public final Service startAsync() {
    this.delegate.startAsync();
    return this;
  }
  
  @CanIgnoreReturnValue
  public final Service stopAsync() {
    this.delegate.stopAsync();
    return this;
  }
  
  public final void awaitRunning() {
    this.delegate.awaitRunning();
  }
  
  public final void awaitRunning(Duration timeout) throws TimeoutException {
    super.awaitRunning(timeout);
  }
  
  public final void awaitRunning(long timeout, TimeUnit unit) throws TimeoutException {
    this.delegate.awaitRunning(timeout, unit);
  }
  
  public final void awaitTerminated() {
    this.delegate.awaitTerminated();
  }
  
  public final void awaitTerminated(Duration timeout) throws TimeoutException {
    super.awaitTerminated(timeout);
  }
  
  public final void awaitTerminated(long timeout, TimeUnit unit) throws TimeoutException {
    this.delegate.awaitTerminated(timeout, unit);
  }
  
  protected String serviceName() {
    return getClass().getSimpleName();
  }
  
  protected abstract void startUp() throws Exception;
  
  protected abstract void shutDown() throws Exception;
}
