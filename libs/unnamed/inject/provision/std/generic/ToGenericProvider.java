package me.syncwrld.booter.libs.unnamed.inject.provision.std.generic;

import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.impl.BinderImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.ScopedProvider;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scope;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public class ToGenericProvider<T> extends ScopedProvider<T> implements Provider<T> {
  private final GenericProvider<T> provider;
  
  private Scope scope;
  
  public ToGenericProvider(GenericProvider<T> provider) {
    this.provider = (GenericProvider<T>)Validate.notNull(provider, "provider", new Object[0]);
  }
  
  public void inject(ProvisionStack stack, InjectorImpl injector) {
    this.injected = true;
  }
  
  public boolean onBind(BinderImpl binder, Key<?> key) {
    boolean isRawType = key.isPureRawType();
    if (!isRawType)
      binder.attach(new String[] { "You must bound the raw-type to a GenericProvider, not a parameterized type! (key: " + key + ", genericProvider: " + this.provider + ")" }); 
    return isRawType;
  }
  
  public T get() {
    throw new IllegalStateException("Key was bound to a generic provider, it cannot complete a raw-type!\n\tProvider: " + this.provider);
  }
  
  public T get(Key<?> bound) {
    return this.provider.get(bound);
  }
  
  public Provider<T> withScope(Key<?> match, Scope scope) {
    if (scope != null)
      this.scope = scope; 
    if (match.isPureRawType())
      return this; 
    return new SyntheticGenericProvider(match, (scope == null) ? this.scope : scope);
  }
  
  public boolean requiresJitScoping() {
    return true;
  }
  
  public class SyntheticGenericProvider extends StdProvider<T> implements Provider<T> {
    private final Scope scope;
    
    private final Provider<T> scoped;
    
    public SyntheticGenericProvider(Key<?> match, Scope scope) {
      this.scope = scope;
      Provider<T> unscoped = ToGenericProvider.this.provider.asConstantProvider(match);
      this.scoped = (scope == null) ? unscoped : scope.scope(unscoped);
      setInjected(true);
    }
    
    public T get() {
      return (T)this.scoped.get();
    }
    
    public Provider<T> withScope(Key<?> match, Scope scope) {
      Validate.argument((this.scope == scope), "Not the same scope on GenericProvider!", new Object[0]);
      return new SyntheticGenericProvider(match, scope);
    }
  }
}
