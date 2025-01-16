package me.syncwrld.booter.libs.unnamed.inject.scope;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import me.syncwrld.booter.libs.unnamed.inject.Provider;

public final class LazySingletonScope implements Scope {
  public <T> Provider<T> scope(Provider<T> unscoped) {
    if (unscoped instanceof LazySingletonProvider)
      return unscoped; 
    return new LazySingletonProvider<>(unscoped);
  }
  
  static class LazySingletonProvider<T> implements Provider<T> {
    private final Lock instanceLock = new ReentrantLock();
    
    private final Provider<T> delegate;
    
    private volatile T instance;
    
    LazySingletonProvider(Provider<T> unscoped) {
      this.delegate = unscoped;
    }
    
    public T get() {
      if (this.instance == null) {
        this.instanceLock.lock();
        try {
          if (this.instance == null)
            this.instance = (T)this.delegate.get(); 
        } finally {
          this.instanceLock.unlock();
        } 
      } 
      return this.instance;
    }
    
    public String toString() {
      return "LazySingleton(" + this.delegate.toString() + ")";
    }
  }
}
