package me.syncwrld.booter.libs.unnamed.inject;

import me.syncwrld.booter.libs.unnamed.inject.key.TypeReference;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public abstract class AbstractModule implements Module {
  private Binder binder;
  
  public final void configure(Binder binder) {
    Validate.state((this.binder == null), "The binder is already being configured by this module!", new Object[0]);
    this.binder = binder;
    configure();
    this.binder = null;
  }
  
  protected final Binder binder() {
    Validate.state((this.binder != null), "The binder isn't specified yet!", new Object[0]);
    return this.binder;
  }
  
  protected final <T> Binder.QualifiedBindingBuilder<T> bind(Class<T> keyType) {
    return binder().bind(keyType);
  }
  
  protected final <T> Binder.QualifiedBindingBuilder<T> bind(TypeReference<T> keyType) {
    return binder().bind(keyType);
  }
  
  protected final <T> Binder.MultiBindingBuilder<T> multibind(Class<T> keyType) {
    return binder().multibind(keyType);
  }
  
  protected final <T> Binder.MultiBindingBuilder<T> multibind(TypeReference<T> keyType) {
    return binder().multibind(keyType);
  }
  
  protected final void install(Module... modules) {
    binder().install(modules);
  }
  
  protected final void install(Iterable<? extends Module> modules) {
    binder().install(modules);
  }
  
  protected void configure() {}
}
