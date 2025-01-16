package me.syncwrld.booter.libs.google.guava.base;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import me.syncwrld.booter.libs.google.errorprone.annotations.DoNotMock;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@DoNotMock("Use Optional.of(value) or Optional.absent()")
@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
public abstract class Optional<T> implements Serializable {
  private static final long serialVersionUID = 0L;
  
  public static <T> Optional<T> absent() {
    return Absent.withType();
  }
  
  public static <T> Optional<T> of(T reference) {
    return new Present<>(Preconditions.checkNotNull(reference));
  }
  
  public static <T> Optional<T> fromNullable(@CheckForNull T nullableReference) {
    return (nullableReference == null) ? absent() : new Present<>(nullableReference);
  }
  
  @CheckForNull
  public static <T> Optional<T> fromJavaUtil(@CheckForNull java.util.Optional<T> javaUtilOptional) {
    return (javaUtilOptional == null) ? null : fromNullable(javaUtilOptional.orElse(null));
  }
  
  @CheckForNull
  public static <T> java.util.Optional<T> toJavaUtil(@CheckForNull Optional<T> googleOptional) {
    return (googleOptional == null) ? null : googleOptional.toJavaUtil();
  }
  
  public java.util.Optional<T> toJavaUtil() {
    return java.util.Optional.ofNullable(orNull());
  }
  
  public abstract boolean isPresent();
  
  public abstract T get();
  
  public abstract T or(T paramT);
  
  public abstract Optional<T> or(Optional<? extends T> paramOptional);
  
  public abstract T or(Supplier<? extends T> paramSupplier);
  
  @CheckForNull
  public abstract T orNull();
  
  public abstract Set<T> asSet();
  
  public abstract <V> Optional<V> transform(Function<? super T, V> paramFunction);
  
  public abstract boolean equals(@CheckForNull Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
  
  public static <T> Iterable<T> presentInstances(final Iterable<? extends Optional<? extends T>> optionals) {
    Preconditions.checkNotNull(optionals);
    return new Iterable<T>() {
        public Iterator<T> iterator() {
          return new AbstractIterator<T>() {
              private final Iterator<? extends Optional<? extends T>> iterator = Preconditions.<Iterator<? extends Optional<? extends T>>>checkNotNull(optionals.iterator());
              
              @CheckForNull
              protected T computeNext() {
                while (this.iterator.hasNext()) {
                  Optional<? extends T> optional = this.iterator.next();
                  if (optional.isPresent())
                    return optional.get(); 
                } 
                return endOfData();
              }
            };
        }
      };
  }
}
