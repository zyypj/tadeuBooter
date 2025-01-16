package me.syncwrld.booter.libs.google.guava.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.util.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.collect.FluentIterable;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableList;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
public final class Parameter implements AnnotatedElement {
  private final Invokable<?, ?> declaration;
  
  private final int position;
  
  private final TypeToken<?> type;
  
  private final ImmutableList<Annotation> annotations;
  
  private final Object annotatedType;
  
  Parameter(Invokable<?, ?> declaration, int position, TypeToken<?> type, Annotation[] annotations, Object annotatedType) {
    this.declaration = declaration;
    this.position = position;
    this.type = type;
    this.annotations = ImmutableList.copyOf((Object[])annotations);
    this.annotatedType = annotatedType;
  }
  
  public TypeToken<?> getType() {
    return this.type;
  }
  
  public Invokable<?, ?> getDeclaringInvokable() {
    return this.declaration;
  }
  
  public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
    return (getAnnotation(annotationType) != null);
  }
  
  @CheckForNull
  public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
    Preconditions.checkNotNull(annotationType);
    for (UnmodifiableIterator<Annotation> unmodifiableIterator = this.annotations.iterator(); unmodifiableIterator.hasNext(); ) {
      Annotation annotation = unmodifiableIterator.next();
      if (annotationType.isInstance(annotation))
        return annotationType.cast(annotation); 
    } 
    return null;
  }
  
  public Annotation[] getAnnotations() {
    return getDeclaredAnnotations();
  }
  
  public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
    return getDeclaredAnnotationsByType(annotationType);
  }
  
  public Annotation[] getDeclaredAnnotations() {
    return (Annotation[])this.annotations.toArray((Object[])new Annotation[0]);
  }
  
  @CheckForNull
  public <A extends Annotation> A getDeclaredAnnotation(Class<A> annotationType) {
    Preconditions.checkNotNull(annotationType);
    return (A)FluentIterable.from((Iterable)this.annotations).filter(annotationType).first().orNull();
  }
  
  public <A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> annotationType) {
    Annotation[] arrayOfAnnotation = (Annotation[])FluentIterable.from((Iterable)this.annotations).filter(annotationType).toArray(annotationType);
    return (A[])arrayOfAnnotation;
  }
  
  public AnnotatedType getAnnotatedType() {
    return Objects.<AnnotatedType>requireNonNull((AnnotatedType)this.annotatedType);
  }
  
  public boolean equals(@CheckForNull Object obj) {
    if (obj instanceof Parameter) {
      Parameter that = (Parameter)obj;
      return (this.position == that.position && this.declaration.equals(that.declaration));
    } 
    return false;
  }
  
  public int hashCode() {
    return this.position;
  }
  
  public String toString() {
    return this.type + " arg" + this.position;
  }
}
