package me.syncwrld.booter.libs.google.guava.base;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Suppliers {
  public static <F, T> Supplier<T> compose(Function<? super F, T> function, Supplier<F> supplier) {
    return new SupplierComposition<>(function, supplier);
  }
  
  private static class SupplierComposition<F, T> implements Supplier<T>, Serializable {
    final Function<? super F, T> function;
    
    final Supplier<F> supplier;
    
    private static final long serialVersionUID = 0L;
    
    SupplierComposition(Function<? super F, T> function, Supplier<F> supplier) {
      this.function = Preconditions.<Function<? super F, T>>checkNotNull(function);
      this.supplier = Preconditions.<Supplier<F>>checkNotNull(supplier);
    }
    
    @ParametricNullness
    public T get() {
      return this.function.apply(this.supplier.get());
    }
    
    public boolean equals(@CheckForNull Object obj) {
      if (obj instanceof SupplierComposition) {
        SupplierComposition<?, ?> that = (SupplierComposition<?, ?>)obj;
        return (this.function.equals(that.function) && this.supplier.equals(that.supplier));
      } 
      return false;
    }
    
    public int hashCode() {
      return Objects.hashCode(new Object[] { this.function, this.supplier });
    }
    
    public String toString() {
      return "Suppliers.compose(" + this.function + ", " + this.supplier + ")";
    }
  }
  
  public static <T> Supplier<T> memoize(Supplier<T> delegate) {
    if (delegate instanceof NonSerializableMemoizingSupplier || delegate instanceof MemoizingSupplier)
      return delegate; 
    return (delegate instanceof Serializable) ? 
      new MemoizingSupplier<>(delegate) : 
      new NonSerializableMemoizingSupplier<>(delegate);
  }
  
  @VisibleForTesting
  static class MemoizingSupplier<T> implements Supplier<T>, Serializable {
    final Supplier<T> delegate;
    
    volatile transient boolean initialized;
    
    @CheckForNull
    transient T value;
    
    private static final long serialVersionUID = 0L;
    
    MemoizingSupplier(Supplier<T> delegate) {
      this.delegate = Preconditions.<Supplier<T>>checkNotNull(delegate);
    }
    
    @ParametricNullness
    public T get() {
      if (!this.initialized)
        synchronized (this) {
          if (!this.initialized) {
            T t = this.delegate.get();
            this.value = t;
            this.initialized = true;
            return t;
          } 
        }  
      return NullnessCasts.uncheckedCastNullableTToT(this.value);
    }
    
    public String toString() {
      return "Suppliers.memoize(" + (
        this.initialized ? ("<supplier that returned " + this.value + ">") : (String)this.delegate) + ")";
    }
  }
  
  @VisibleForTesting
  static class NonSerializableMemoizingSupplier<T> implements Supplier<T> {
    private static final Supplier<Void> SUCCESSFULLY_COMPUTED = () -> {
        throw new IllegalStateException();
      };
    
    private volatile Supplier<T> delegate;
    
    @CheckForNull
    private T value;
    
    NonSerializableMemoizingSupplier(Supplier<T> delegate) {
      this.delegate = Preconditions.<Supplier<T>>checkNotNull(delegate);
    }
    
    @ParametricNullness
    public T get() {
      if (this.delegate != SUCCESSFULLY_COMPUTED)
        synchronized (this) {
          if (this.delegate != SUCCESSFULLY_COMPUTED) {
            T t = this.delegate.get();
            this.value = t;
            this.delegate = (Supplier)SUCCESSFULLY_COMPUTED;
            return t;
          } 
        }  
      return NullnessCasts.uncheckedCastNullableTToT(this.value);
    }
    
    public String toString() {
      Supplier<T> delegate = this.delegate;
      return "Suppliers.memoize(" + (
        (delegate == SUCCESSFULLY_COMPUTED) ? (
        "<supplier that returned " + this.value + ">") : 
        (String)delegate) + ")";
    }
  }
  
  public static <T> Supplier<T> memoizeWithExpiration(Supplier<T> delegate, long duration, TimeUnit unit) {
    return new ExpiringMemoizingSupplier<>(delegate, duration, unit);
  }
  
  @VisibleForTesting
  static class ExpiringMemoizingSupplier<T> implements Supplier<T>, Serializable {
    final Supplier<T> delegate;
    
    final long durationNanos;
    
    @CheckForNull
    volatile transient T value;
    
    volatile transient long expirationNanos;
    
    private static final long serialVersionUID = 0L;
    
    ExpiringMemoizingSupplier(Supplier<T> delegate, long duration, TimeUnit unit) {
      this.delegate = Preconditions.<Supplier<T>>checkNotNull(delegate);
      this.durationNanos = unit.toNanos(duration);
      Preconditions.checkArgument((duration > 0L), "duration (%s %s) must be > 0", duration, unit);
    }
    
    @ParametricNullness
    public T get() {
      long nanos = this.expirationNanos;
      long now = System.nanoTime();
      if (nanos == 0L || now - nanos >= 0L)
        synchronized (this) {
          if (nanos == this.expirationNanos) {
            T t = this.delegate.get();
            this.value = t;
            nanos = now + this.durationNanos;
            this.expirationNanos = (nanos == 0L) ? 1L : nanos;
            return t;
          } 
        }  
      return NullnessCasts.uncheckedCastNullableTToT(this.value);
    }
    
    public String toString() {
      return "Suppliers.memoizeWithExpiration(" + this.delegate + ", " + this.durationNanos + ", NANOS)";
    }
  }
  
  public static <T> Supplier<T> ofInstance(@ParametricNullness T instance) {
    return new SupplierOfInstance<>(instance);
  }
  
  private static class SupplierOfInstance<T> implements Supplier<T>, Serializable {
    @ParametricNullness
    final T instance;
    
    private static final long serialVersionUID = 0L;
    
    SupplierOfInstance(@ParametricNullness T instance) {
      this.instance = instance;
    }
    
    @ParametricNullness
    public T get() {
      return this.instance;
    }
    
    public boolean equals(@CheckForNull Object obj) {
      if (obj instanceof SupplierOfInstance) {
        SupplierOfInstance<?> that = (SupplierOfInstance)obj;
        return Objects.equal(this.instance, that.instance);
      } 
      return false;
    }
    
    public int hashCode() {
      return Objects.hashCode(new Object[] { this.instance });
    }
    
    public String toString() {
      return "Suppliers.ofInstance(" + this.instance + ")";
    }
  }
  
  public static <T> Supplier<T> synchronizedSupplier(Supplier<T> delegate) {
    return new ThreadSafeSupplier<>(delegate);
  }
  
  private static class ThreadSafeSupplier<T> implements Supplier<T>, Serializable {
    final Supplier<T> delegate;
    
    private static final long serialVersionUID = 0L;
    
    ThreadSafeSupplier(Supplier<T> delegate) {
      this.delegate = Preconditions.<Supplier<T>>checkNotNull(delegate);
    }
    
    @ParametricNullness
    public T get() {
      synchronized (this.delegate) {
        return this.delegate.get();
      } 
    }
    
    public String toString() {
      return "Suppliers.synchronizedSupplier(" + this.delegate + ")";
    }
  }
  
  public static <T> Function<Supplier<T>, T> supplierFunction() {
    SupplierFunction<T> sf = SupplierFunctionImpl.INSTANCE;
    return sf;
  }
  
  private static interface SupplierFunction<T> extends Function<Supplier<T>, T> {}
  
  private enum SupplierFunctionImpl implements SupplierFunction<Object> {
    INSTANCE;
    
    @CheckForNull
    public Object apply(Supplier<Object> input) {
      return input.get();
    }
    
    public String toString() {
      return "Suppliers.supplierFunction()";
    }
  }
}
