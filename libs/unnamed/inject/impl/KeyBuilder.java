package me.syncwrld.booter.libs.unnamed.inject.impl;

import java.lang.annotation.Annotation;
import me.syncwrld.booter.libs.unnamed.inject.Binder;
import me.syncwrld.booter.libs.unnamed.inject.key.Key;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public interface KeyBuilder<R, T> extends Binder.Qualified<R> {
  Key<T> key();
  
  void setKey(Key<T> paramKey);
  
  default R markedWith(Class<? extends Annotation> qualifierType) {
    Validate.notNull(qualifierType, "qualifierType", new Object[0]);
    setKey(key().withQualifier(qualifierType));
    return getReturnValue();
  }
  
  default R qualified(Annotation annotation) {
    Validate.notNull(annotation, "annotation", new Object[0]);
    setKey(key().withQualifier(annotation));
    return getReturnValue();
  }
  
  default R named(String name) {
    Validate.notNull(name, "name", new Object[0]);
    return qualified((Annotation)Annotations.createNamed(name));
  }
  
  R getReturnValue();
}
