package me.syncwrld.booter.libs.unnamed.inject.assisted.provision;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.syncwrld.booter.libs.unnamed.inject.Provider;
import me.syncwrld.booter.libs.unnamed.inject.assisted.Assisted;
import me.syncwrld.booter.libs.unnamed.inject.assisted.ValueFactory;
import me.syncwrld.booter.libs.unnamed.inject.error.ErrorAttachable;
import me.syncwrld.booter.libs.unnamed.inject.error.FactoryException;
import me.syncwrld.booter.libs.unnamed.inject.impl.BinderImpl;
import me.syncwrld.booter.libs.unnamed.inject.key.InjectedKey;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.provision.StdProvider;
import me.syncwrld.booter.libs.unnamed.inject.resolve.ComponentResolver;
import me.syncwrld.booter.libs.unnamed.inject.resolve.solution.InjectableConstructor;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public class ToFactoryProvider<T> extends StdProvider<T> {
  private final TypeReference<? extends ValueFactory> factory;
  
  public ToFactoryProvider(TypeReference<? extends ValueFactory> factory) {
    this.factory = (TypeReference<? extends ValueFactory>)Validate.notNull(factory, "factory", new Object[0]);
  }
  
  public boolean onBind(BinderImpl binder, Key<?> key) {
    Class<? extends ValueFactory> factoryRawType = this.factory.getRawType();
    TypeReference<?> required = key.getType();
    InjectableConstructor constructor = ComponentResolver.constructor().resolve((ErrorAttachable)binder, required, Assisted.class);
    if (constructor == null) {
      binder.attach("Bad assisted object", (Throwable)new FactoryException("Cannot resolve constructor annotated with @Assisted in type " + required));
      return false;
    } 
    if (!factoryRawType.isInterface()) {
      binder.attach(new String[] { "Factory " + this.factory + " must be an interface with one single method!" });
      return false;
    } 
    int methodCount = (factoryRawType.getMethods()).length;
    if (methodCount != 1) {
      binder.attach("Bad factory method", (Throwable)new FactoryException("Factory " + this.factory + " has invalid method count (expected: 1, found: " + methodCount + ")"));
      return false;
    } 
    Method method = factoryRawType.getMethods()[0];
    TypeReference typeReference = this.factory.resolve(method.getGenericReturnType());
    if (!required.equals(typeReference)) {
      binder.attach("Bad factory method", (Throwable)new FactoryException("Method " + method
            
            .getName() + " of factory " + this.factory + " must return " + required));
      return false;
    } 
    List<InjectedKey<?>> keys = ComponentResolver.keys().keysOf(this.factory, method
        
        .getParameters());
    Set<Key<?>> assists = new HashSet<>();
    for (InjectedKey<?> parameterKey : keys) {
      if (!assists.add(parameterKey.getKey())) {
        binder.attach("Duplicated factory assisted keys", (Throwable)new FactoryException("Creator method has two equal assisted values! Consider using qualifiers to difference them (key " + parameterKey
              
              .getKey() + ")"));
        return false;
      } 
    } 
    Set<Key<?>> constructorAssists = new HashSet<>();
    for (InjectedKey<?> parameterKey : (Iterable<InjectedKey<?>>)constructor.getKeys()) {
      if (parameterKey.isAssisted()) {
        if (!assists.contains(parameterKey.getKey())) {
          binder.attach("Unsatisfied Assisted Constructor", (Throwable)new FactoryException("Constructor requires assist for " + parameterKey
                
                .getKey() + " and method doesn't give it!"));
          return false;
        } 
        if (!constructorAssists.add(parameterKey.getKey())) {
          binder.attach("Duplicated constructor assisted keys", (Throwable)new FactoryException("Constructor has two equal assisted keys! Consider using qualifiers to difference them (key " + parameterKey
                
                .getKey() + ")"));
          return false;
        } 
      } 
    } 
    if (assists.size() != constructorAssists.size()) {
      binder.attach("Assists mismatch, different assisted injections count", (Throwable)new FactoryException("Assists mismatch! Constructor has " + constructorAssists
            
            .size() + " values and method " + assists.size() + " values."));
      return false;
    } 
    Key<T> castedKey = (Key)key;
    binder.$unsafeBind(Key.of(this.factory), (Provider)new ProxiedFactoryProvider(factoryRawType, method, keys, constructor, castedKey));
    return false;
  }
  
  public T get() {
    throw new IllegalStateException("The instance is bound to a Factory, you must get an instance of that factory!");
  }
}
