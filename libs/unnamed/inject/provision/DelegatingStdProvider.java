package me.syncwrld.booter.libs.unnamed.inject.provision;

import java.util.Objects;
import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.impl.BinderImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scope;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public class DelegatingStdProvider<T> extends StdProvider<T> implements Provider<T> {
  private final Provider<T> delegate;
  
  public DelegatingStdProvider(Provider<T> delegate) {
    this.delegate = (Provider<T>)Validate.notNull(delegate, "delegate", new Object[0]);
  }
  
  public DelegatingStdProvider(boolean injected, Provider<T> delegate) {
    this(delegate);
    setInjected(injected);
  }
  
  public Provider<T> getDelegate() {
    return this.delegate;
  }
  
  public void inject(ProvisionStack stack, InjectorImpl injector) {
    Providers.inject(stack, injector, this.delegate);
    this.injected = true;
  }
  
  public boolean onBind(BinderImpl binder, Key<?> key) {
    if (this.delegate instanceof StdProvider)
      return ((StdProvider)this.delegate).onBind(binder, key); 
    return true;
  }
  
  public Provider<T> withScope(Key<?> match, Scope scope) {
    if (this.delegate instanceof StdProvider)
      return ((StdProvider<T>)this.delegate).withScope(match, scope); 
    return super.withScope(match, scope);
  }
  
  public T get() {
    return (T)this.delegate.get();
  }
  
  public String toString() {
    return this.delegate.toString();
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof DelegatingStdProvider))
      return false; 
    DelegatingStdProvider<?> that = (DelegatingStdProvider)o;
    return (that.isInjected() == isInjected() && 
      Objects.equals(this.delegate, that.delegate));
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { Boolean.valueOf(isInjected()), this.delegate });
  }
}
