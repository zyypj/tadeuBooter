package me.syncwrld.booter.libs.unnamed.inject.scope;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import me.syncwrld.booter.libs.unnamed.inject.Singleton;
import me.syncwrld.booter.libs.unnamed.inject.util.Validate;

public final class ScopeScanner {
  private final Map<Class<? extends Annotation>, Scope> scopes = new HashMap<>();
  
  ScopeScanner() {
    this.scopes.put(Singleton.class, Scopes.SINGLETON);
  }
  
  public void bind(Class<? extends Annotation> annotationType, Scope scope) {
    Validate.notNull(annotationType, "annotationType", new Object[0]);
    Validate.notNull(scope, "scope", new Object[0]);
    this.scopes.put(annotationType, scope);
  }
  
  public Scope scan(AnnotatedElement element) {
    Annotation[] annotations = element.getDeclaredAnnotations();
    for (Annotation annotation : annotations) {
      Class<? extends Annotation> annotationType = annotation.annotationType();
      Scope scope = this.scopes.get(annotationType);
      if (scope != null)
        return scope; 
    } 
    return Scopes.NONE;
  }
}
