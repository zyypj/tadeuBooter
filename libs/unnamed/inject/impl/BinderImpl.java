package me.syncwrld.booter.libs.unnamed.inject.impl;

import java.util.HashMap;
import java.util.Map;
import me.syncwrld.booter.libs.unnamed.inject.Binder;
import me.syncwrld.booter.libs.unnamed.inject.Module;
import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.error.BindingException;
import me.syncwrld.booter.libs.unnamed.inject.error.ErrorAttachable;
import me.syncwrld.booter.libs.unnamed.inject.error.ErrorAttachableImpl;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.multibinding.MultiBindingBuilderImpl;
import me.syncwrld.booter.libs.unnamed.inject.provision.Providers;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.MethodAsProvider;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.generic.GenericProvider;
import me.syncwrld.booter.libs.unnamed.inject.provision.std.generic.impl.TypeReferenceGenericProvider;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public class BinderImpl extends ErrorAttachableImpl implements Binder {
  private final Map<Key<?>, Provider<?>> bindings = new HashMap<>();
  
  public BinderImpl() {
    ((Binder.Scoped)bind(TypeReference.class).toGenericProvider((GenericProvider)new TypeReferenceGenericProvider())).singleton();
  }
  
  public <T> StdProvider<T> getProvider(Key<T> key) {
    StdProvider<T> provider = (StdProvider<T>)this.bindings.get(key);
    return provider;
  }
  
  public void $unsafeBind(Key<?> key, Provider<?> provider) {
    Validate.notNull(key, "key", new Object[0]);
    Validate.notNull(provider, "provider", new Object[0]);
    if (!(provider instanceof StdProvider) || ((StdProvider)provider).onBind(this, key))
      this.bindings.put(key, Providers.normalize(provider)); 
  }
  
  public <T> Binder.QualifiedBindingBuilder<T> bind(TypeReference<T> keyType) {
    return new BindingBuilderImpl<>(this, keyType);
  }
  
  public <T> Binder.MultiBindingBuilder<T> multibind(TypeReference<T> keyType) {
    return (Binder.MultiBindingBuilder<T>)new MultiBindingBuilderImpl(this, keyType);
  }
  
  public void reportAttachedErrors() {
    if (hasErrors())
      throw new BindingException(formatMessages()); 
  }
  
  public void install(Iterable<? extends Module> modules) {
    for (Module module : modules) {
      module.configure(this);
      MethodAsProvider.resolveMethodProviders((ErrorAttachable)this, 
          
          TypeReference.of(module.getClass()), module)
        
        .forEach(this::$unsafeBind);
    } 
  }
}
