package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
abstract class ForwardingLock implements Lock {
  abstract Lock delegate();
  
  public void lock() {
    delegate().lock();
  }
  
  public void lockInterruptibly() throws InterruptedException {
    delegate().lockInterruptibly();
  }
  
  public boolean tryLock() {
    return delegate().tryLock();
  }
  
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    return delegate().tryLock(time, unit);
  }
  
  public void unlock() {
    delegate().unlock();
  }
  
  public Condition newCondition() {
    return delegate().newCondition();
  }
}
