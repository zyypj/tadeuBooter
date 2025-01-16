package me.syncwrld.booter.libs.unnamed.inject.impl;

import java.util.List;
import me.syncwrld.booter.libs.unnamed.inject.Injector;
import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.error.ErrorAttachable;
import me.syncwrld.booter.libs.unnamed.inject.error.InjectionException;
import me.syncwrld.booter.libs.unnamed.inject.key.InjectedKey;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;
import me.syncwrld.booter.libs.unnamed.inject.resolve.ComponentResolver;
import me.syncwrld.booter.libs.unnamed.inject.resolve.solution.InjectableConstructor;
import me.syncwrld.booter.libs.unnamed.inject.resolve.solution.InjectableMember;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public class InjectorImpl implements Injector {
  public static final Object ABSENT_INSTANCE = new Object();
  
  protected final ThreadLocal<ProvisionStack> provisionStackThreadLocal = new ThreadLocal<>();
  
  private final ProvisionHandle provisionHandle;
  
  private final BinderImpl binder;
  
  public InjectorImpl(BinderImpl binder) {
    this.binder = (BinderImpl)Validate.notNull(binder);
    this.provisionHandle = new ProvisionHandle(this, binder);
  }
  
  public Object getValue(InjectedKey<?> key, ProvisionStack stack) {
    List<String> snapshot = stack.getErrorMessages();
    Object value = getInstance(stack, key.getKey(), true);
    if (value == null && !key.isOptional())
      return ABSENT_INSTANCE; 
    if (key.isOptional())
      stack.applySnapshot(snapshot); 
    return value;
  }
  
  public <T> T getInstance(TypeReference<T> type) {
    boolean stackWasNotPresent = (this.provisionStackThreadLocal.get() == null);
    T value = getInstance(stackForThisThread(), Key.of(type), true);
    if (stackWasNotPresent)
      removeStackFromThisThread(); 
    return value;
  }
  
  public <T> void injectMembers(TypeReference<T> type, T instance) {
    boolean stackWasNotPresent = (this.provisionStackThreadLocal.get() == null);
    injectMembers(stackForThisThread(), Key.of(type), instance);
    if (stackWasNotPresent)
      removeStackFromThisThread(); 
  }
  
  public ProvisionStack stackForThisThread() {
    ProvisionStack stack = this.provisionStackThreadLocal.get();
    if (stack == null) {
      stack = new ProvisionStack();
      this.provisionStackThreadLocal.set(stack);
    } 
    return stack;
  }
  
  protected void removeStackFromThisThread() {
    ProvisionStack stack = this.provisionStackThreadLocal.get();
    this.provisionStackThreadLocal.set(null);
    if (stack != null && stack.hasErrors())
      throw new InjectionException(stack.formatMessages()); 
  }
  
  public <T> Provider<? extends T> getProvider(TypeReference<T> key) {
    return (Provider)this.binder.getProvider(Key.of(key));
  }
  
  public void injectStaticMembers(Class<?> clazz) {
    boolean stackWasNotPresent = (this.provisionStackThreadLocal.get() == null);
    injectMembers(stackForThisThread(), Key.of(TypeReference.of(clazz)), null);
    if (stackWasNotPresent)
      removeStackFromThisThread(); 
  }
  
  public <T> void injectMembers(ProvisionStack stack, Key<T> type, T instance) {
    if (instance != null)
      stack.push(type, instance); 
    for (InjectableMember member : ComponentResolver.fields().get(type.getType()))
      member.inject(this, stack, instance); 
    for (InjectableMember member : ComponentResolver.methods().get(type.getType()))
      member.inject(this, stack, instance); 
    if (instance != null)
      stack.pop(); 
  }
  
  public <T> T getInstance(Key<T> type, boolean useExplicitBindings) {
    return getInstance(stackForThisThread(), type, useExplicitBindings);
  }
  
  public <T> T getInstance(ProvisionStack stack, Key<T> type, boolean useExplicitBindings) {
    Class<? super T> rawType = type.getType().getRawType();
    if (rawType == Injector.class || rawType == InjectorImpl.class)
      return (T)this; 
    if (stack.has(type))
      return stack.get(type); 
    AnnotationScanner.bind(type.getType(), this.binder);
    AnnotationScanner.scope(type.getType(), this.binder);
    if (useExplicitBindings) {
      StdProvider<T> provider = this.provisionHandle.getProviderAndInject(stack, type);
      if (provider != null)
        return (T)provider.get(type); 
    } 
    InjectableConstructor constructor = ComponentResolver.constructor().get((ErrorAttachable)stack, type.getType());
    if (constructor == null)
      return null; 
    Object instance = constructor.inject(this, stack, null);
    T value = (T)instance;
    if (value != null)
      injectMembers(stack, type, value); 
    return value;
  }
}
