package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.j2objc.annotations.ReflectionSupport;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
@J2ktIncompatible
@ReflectionSupport(ReflectionSupport.Level.FULL)
public class AtomicDouble extends Number implements Serializable {
  private static final long serialVersionUID = 0L;
  
  private volatile transient long value;
  
  private static final AtomicLongFieldUpdater<AtomicDouble> updater = AtomicLongFieldUpdater.newUpdater(AtomicDouble.class, "value");
  
  public AtomicDouble(double initialValue) {
    this.value = Double.doubleToRawLongBits(initialValue);
  }
  
  public AtomicDouble() {}
  
  public final double get() {
    return Double.longBitsToDouble(this.value);
  }
  
  public final void set(double newValue) {
    long next = Double.doubleToRawLongBits(newValue);
    this.value = next;
  }
  
  public final void lazySet(double newValue) {
    long next = Double.doubleToRawLongBits(newValue);
    updater.lazySet(this, next);
  }
  
  public final double getAndSet(double newValue) {
    long next = Double.doubleToRawLongBits(newValue);
    return Double.longBitsToDouble(updater.getAndSet(this, next));
  }
  
  public final boolean compareAndSet(double expect, double update) {
    return updater.compareAndSet(this, Double.doubleToRawLongBits(expect), Double.doubleToRawLongBits(update));
  }
  
  public final boolean weakCompareAndSet(double expect, double update) {
    return updater.weakCompareAndSet(this, 
        Double.doubleToRawLongBits(expect), Double.doubleToRawLongBits(update));
  }
  
  @CanIgnoreReturnValue
  public final double getAndAdd(double delta) {
    return getAndAccumulate(delta, Double::sum);
  }
  
  @CanIgnoreReturnValue
  public final double addAndGet(double delta) {
    return accumulateAndGet(delta, Double::sum);
  }
  
  @CanIgnoreReturnValue
  public final double getAndAccumulate(double x, DoubleBinaryOperator accumulatorFunction) {
    Preconditions.checkNotNull(accumulatorFunction);
    return getAndUpdate(oldValue -> accumulatorFunction.applyAsDouble(oldValue, x));
  }
  
  @CanIgnoreReturnValue
  public final double accumulateAndGet(double x, DoubleBinaryOperator accumulatorFunction) {
    Preconditions.checkNotNull(accumulatorFunction);
    return updateAndGet(oldValue -> accumulatorFunction.applyAsDouble(oldValue, x));
  }
  
  @CanIgnoreReturnValue
  public final double getAndUpdate(DoubleUnaryOperator updateFunction) {
    while (true) {
      long current = this.value;
      double currentVal = Double.longBitsToDouble(current);
      double nextVal = updateFunction.applyAsDouble(currentVal);
      long next = Double.doubleToRawLongBits(nextVal);
      if (updater.compareAndSet(this, current, next))
        return currentVal; 
    } 
  }
  
  @CanIgnoreReturnValue
  public final double updateAndGet(DoubleUnaryOperator updateFunction) {
    while (true) {
      long current = this.value;
      double currentVal = Double.longBitsToDouble(current);
      double nextVal = updateFunction.applyAsDouble(currentVal);
      long next = Double.doubleToRawLongBits(nextVal);
      if (updater.compareAndSet(this, current, next))
        return nextVal; 
    } 
  }
  
  public String toString() {
    return Double.toString(get());
  }
  
  public int intValue() {
    return (int)get();
  }
  
  public long longValue() {
    return (long)get();
  }
  
  public float floatValue() {
    return (float)get();
  }
  
  public double doubleValue() {
    return get();
  }
  
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeDouble(get());
  }
  
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    set(s.readDouble());
  }
}
