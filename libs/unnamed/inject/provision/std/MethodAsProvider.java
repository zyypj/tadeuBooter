package me.syncwrld.booter.libs.unnamed.inject.provision.std;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.Provides;
import me.syncwrld.booter.libs.unnamed.inject.error.BindingException;
import me.syncwrld.booter.libs.unnamed.inject.error.ErrorAttachable;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;
import me.syncwrld.booter.libs.unnamed.inject.resolve.ComponentResolver;
import me.syncwrld.booter.libs.unnamed.inject.resolve.solution.InjectableMethod;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scope;
import me.syncwrld.booter.libs.unnamed.inject.scope.Scopes;

public class MethodAsProvider<T> extends StdProvider<T> {
  private final Object moduleInstance;
  
  private final InjectableMethod method;
  
  private InjectorImpl injector;
  
  public MethodAsProvider(Object moduleInstance, InjectableMethod method) {
    this.moduleInstance = moduleInstance;
    this.method = method;
  }
  
  public static <T> Map<Key<?>, Provider<?>> resolveMethodProviders(ErrorAttachable errors, TypeReference<T> type, T instance) {
    Map<Key<?>, Provider<?>> providers = new HashMap<>();
    for (InjectableMethod injectableMethod : ComponentResolver.methods().resolve(type, Provides.class)) {
      Method method = injectableMethod.getMember();
      Key<?> key = ComponentResolver.keys().keyOf(injectableMethod.getDeclaringType().resolve(method.getGenericReturnType()), method.getAnnotations()).getKey();
      Scope scope = Scopes.getScanner().scan(method);
      Provider<?> provider = (new MethodAsProvider(instance, injectableMethod)).withScope(key, scope);
      if (providers.putIfAbsent(key, provider) != null)
        errors.attach("Method provider duplicate", (Throwable)new BindingException("Type " + type + " has two or more method providers with the same return key!")); 
    } 
    return providers;
  }
  
  public void inject(ProvisionStack stack, InjectorImpl injector) {
    this.injector = injector;
    this.injected = true;
  }
  
  public T get() {
    T value = (T)this.method.inject(this.injector, this.injector.stackForThisThread(), this.moduleInstance);
    return value;
  }
}
