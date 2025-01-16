package me.syncwrld.booter.libs.google.guava.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Executor;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Function;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Supplier;
import me.syncwrld.booter.libs.google.guava.util.concurrent.Futures;
import me.syncwrld.booter.libs.google.guava.util.concurrent.ListenableFuture;
import me.syncwrld.booter.libs.google.guava.util.concurrent.ListenableFutureTask;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
public abstract class CacheLoader<K, V> {
  public abstract V load(K paramK) throws Exception;
  
  @GwtIncompatible
  public ListenableFuture<V> reload(K key, V oldValue) throws Exception {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(oldValue);
    return Futures.immediateFuture(load(key));
  }
  
  public Map<K, V> loadAll(Iterable<? extends K> keys) throws Exception {
    throw new UnsupportedLoadingOperationException();
  }
  
  public static <K, V> CacheLoader<K, V> from(Function<K, V> function) {
    return new FunctionToCacheLoader<>(function);
  }
  
  public static <V> CacheLoader<Object, V> from(Supplier<V> supplier) {
    return new SupplierToCacheLoader<>(supplier);
  }
  
  private static final class FunctionToCacheLoader<K, V> extends CacheLoader<K, V> implements Serializable {
    private final Function<K, V> computingFunction;
    
    private static final long serialVersionUID = 0L;
    
    public FunctionToCacheLoader(Function<K, V> computingFunction) {
      this.computingFunction = (Function<K, V>)Preconditions.checkNotNull(computingFunction);
    }
    
    public V load(K key) {
      return (V)this.computingFunction.apply(Preconditions.checkNotNull(key));
    }
  }
  
  @GwtIncompatible
  public static <K, V> CacheLoader<K, V> asyncReloading(final CacheLoader<K, V> loader, final Executor executor) {
    Preconditions.checkNotNull(loader);
    Preconditions.checkNotNull(executor);
    return new CacheLoader<K, V>() {
        public V load(K key) throws Exception {
          return loader.load(key);
        }
        
        public ListenableFuture<V> reload(K key, V oldValue) {
          ListenableFutureTask<V> task = ListenableFutureTask.create(() -> loader.reload(key, (V)oldValue).get());
          executor.execute((Runnable)task);
          return (ListenableFuture<V>)task;
        }
        
        public Map<K, V> loadAll(Iterable<? extends K> keys) throws Exception {
          return loader.loadAll(keys);
        }
      };
  }
  
  private static final class SupplierToCacheLoader<V> extends CacheLoader<Object, V> implements Serializable {
    private final Supplier<V> computingSupplier;
    
    private static final long serialVersionUID = 0L;
    
    public SupplierToCacheLoader(Supplier<V> computingSupplier) {
      this.computingSupplier = (Supplier<V>)Preconditions.checkNotNull(computingSupplier);
    }
    
    public V load(Object key) {
      Preconditions.checkNotNull(key);
      return (V)this.computingSupplier.get();
    }
  }
  
  public static final class UnsupportedLoadingOperationException extends UnsupportedOperationException {}
  
  public static final class InvalidCacheLoadException extends RuntimeException {
    public InvalidCacheLoadException(String message) {
      super(message);
    }
  }
}
