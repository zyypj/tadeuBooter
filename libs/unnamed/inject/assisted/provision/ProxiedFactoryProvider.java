package me.syncwrld.booter.libs.unnamed.inject.assisted.provision;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.syncwrld.booter.libs.unnamed.inject.assisted.ValueFactory;
import me.syncwrld.booter.libs.unnamed.inject.impl.InjectorImpl;
import me.syncwrld.booter.libs.unnamed.inject.impl.ProvisionStack;
import me.syncwrld.booter.libs.unnamed.inject.key.InjectedKey;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;
import me.syncwrld.booter.libs.unnamed.inject.resolve.solution.InjectableConstructor;
import me.syncwrld.booter.libs.unnamed.inject.util.ElementFormatter;

public class ProxiedFactoryProvider<T> extends StdProvider<T> {
  private final Class<? extends ValueFactory> factory;
  
  private final Method method;
  
  private final List<InjectedKey<?>> keys;
  
  private final InjectableConstructor constructor;
  
  private final Key<?> key;
  
  private T factoryInstance;
  
  ProxiedFactoryProvider(Class<? extends ValueFactory> factory, Method method, List<InjectedKey<?>> keys, InjectableConstructor constructor, Key<?> key) {
    this.factory = factory;
    this.method = method;
    this.keys = keys;
    this.constructor = constructor;
    this.key = key;
  }
  
  public Class<? extends ValueFactory> getFactory() {
    return this.factory;
  }
  
  public Method getFactoryMethod() {
    return this.method;
  }
  
  public Key<?> getBuildType() {
    return this.key;
  }
  
  public Constructor<?> getTargetConstructor() {
    return this.constructor.getMember();
  }
  
  private Object createInstance(InjectorImpl injector, Object[] extras) {
    Map<Key<?>, Object> values = new HashMap<>();
    for (int i = 0; i < extras.length; i++) {
      Key<?> valueKey = ((InjectedKey)this.keys.get(i)).getKey();
      Object value = extras[i];
      values.put(valueKey, value);
    } 
    Object[] givenArgs = new Object[this.constructor.getKeys().size()];
    int j = 0;
    for (InjectedKey<?> injection : (Iterable<InjectedKey<?>>)this.constructor.getKeys()) {
      if (injection.isAssisted()) {
        givenArgs[j] = values.get(injection.getKey());
      } else {
        Object val = injector.getInstance(injector
            .stackForThisThread(), injection
            .getKey(), true);
        givenArgs[j] = val;
      } 
      j++;
    } 
    try {
      Object instance = this.constructor.getMember().newInstance(givenArgs);
      injector.injectMembers(this.key
          .getType(), instance);
      return instance;
    } catch (InstantiationException|java.lang.reflect.InvocationTargetException|IllegalAccessException e) {
      injector.stackForThisThread().attach("Errors while invoking assisted constructor " + 
          
          ElementFormatter.formatConstructor(this.constructor.getMember(), this.keys), e);
      return null;
    } 
  }
  
  public void inject(ProvisionStack stack, InjectorImpl injector) {
    this.factoryInstance = (T)Proxy.newProxyInstance(
        getClass().getClassLoader(), new Class[] { this.factory }, (proxy, method, args) -> {
          if (method.equals(this.method))
            return createInstance(injector, args); 
          switch (method.getName()) {
            case "equals":
              return Boolean.valueOf(false);
            case "hashCode":
              return Integer.valueOf(0);
            case "toString":
              return this.factory.getName() + " Trew-generated implementation";
          } 
          return null;
        });
    this.injected = true;
  }
  
  public T get() {
    return this.factoryInstance;
  }
}
