package me.syncwrld.booter.libs.google.guava.base;

import java.io.Serializable;
import java.util.Iterator;
import me.syncwrld.booter.libs.google.errorprone.annotations.CheckReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.ForOverride;
import me.syncwrld.booter.libs.google.errorprone.annotations.InlineMe;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.LazyInit;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.google.j2objc.annotations.RetainedWith;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class Converter<A, B> implements Function<A, B> {
  private final boolean handleNullAutomatically;
  
  @LazyInit
  @CheckForNull
  @RetainedWith
  private transient Converter<B, A> reverse;
  
  protected Converter() {
    this(true);
  }
  
  Converter(boolean handleNullAutomatically) {
    this.handleNullAutomatically = handleNullAutomatically;
  }
  
  @ForOverride
  protected abstract B doForward(A paramA);
  
  @ForOverride
  protected abstract A doBackward(B paramB);
  
  @CheckForNull
  public final B convert(@CheckForNull A a) {
    return correctedDoForward(a);
  }
  
  @CheckForNull
  B correctedDoForward(@CheckForNull A a) {
    if (this.handleNullAutomatically)
      return (a == null) ? null : Preconditions.<B>checkNotNull(doForward(a)); 
    return unsafeDoForward(a);
  }
  
  @CheckForNull
  A correctedDoBackward(@CheckForNull B b) {
    if (this.handleNullAutomatically)
      return (b == null) ? null : Preconditions.<A>checkNotNull(doBackward(b)); 
    return unsafeDoBackward(b);
  }
  
  @CheckForNull
  private B unsafeDoForward(@CheckForNull A a) {
    return doForward(NullnessCasts.uncheckedCastNullableTToT(a));
  }
  
  @CheckForNull
  private A unsafeDoBackward(@CheckForNull B b) {
    return doBackward(NullnessCasts.uncheckedCastNullableTToT(b));
  }
  
  public Iterable<B> convertAll(final Iterable<? extends A> fromIterable) {
    Preconditions.checkNotNull(fromIterable, "fromIterable");
    return new Iterable<B>() {
        public Iterator<B> iterator() {
          return new Iterator<B>() {
              private final Iterator<? extends A> fromIterator = fromIterable.iterator();
              
              public boolean hasNext() {
                return this.fromIterator.hasNext();
              }
              
              public B next() {
                return (B)Converter.this.convert(this.fromIterator.next());
              }
              
              public void remove() {
                this.fromIterator.remove();
              }
            };
        }
      };
  }
  
  @CheckReturnValue
  public Converter<B, A> reverse() {
    Converter<B, A> result = this.reverse;
    return (result == null) ? (this.reverse = new ReverseConverter<>(this)) : result;
  }
  
  private static final class ReverseConverter<A, B> extends Converter<B, A> implements Serializable {
    final Converter<A, B> original;
    
    private static final long serialVersionUID = 0L;
    
    ReverseConverter(Converter<A, B> original) {
      this.original = original;
    }
    
    protected A doForward(B b) {
      throw new AssertionError();
    }
    
    protected B doBackward(A a) {
      throw new AssertionError();
    }
    
    @CheckForNull
    A correctedDoForward(@CheckForNull B b) {
      return this.original.correctedDoBackward(b);
    }
    
    @CheckForNull
    B correctedDoBackward(@CheckForNull A a) {
      return this.original.correctedDoForward(a);
    }
    
    public Converter<A, B> reverse() {
      return this.original;
    }
    
    public boolean equals(@CheckForNull Object object) {
      if (object instanceof ReverseConverter) {
        ReverseConverter<?, ?> that = (ReverseConverter<?, ?>)object;
        return this.original.equals(that.original);
      } 
      return false;
    }
    
    public int hashCode() {
      return this.original.hashCode() ^ 0xFFFFFFFF;
    }
    
    public String toString() {
      return this.original + ".reverse()";
    }
  }
  
  public final <C> Converter<A, C> andThen(Converter<B, C> secondConverter) {
    return doAndThen(secondConverter);
  }
  
  <C> Converter<A, C> doAndThen(Converter<B, C> secondConverter) {
    return new ConverterComposition<>(this, Preconditions.<Converter<B, C>>checkNotNull(secondConverter));
  }
  
  private static final class ConverterComposition<A, B, C> extends Converter<A, C> implements Serializable {
    final Converter<A, B> first;
    
    final Converter<B, C> second;
    
    private static final long serialVersionUID = 0L;
    
    ConverterComposition(Converter<A, B> first, Converter<B, C> second) {
      this.first = first;
      this.second = second;
    }
    
    protected C doForward(A a) {
      throw new AssertionError();
    }
    
    protected A doBackward(C c) {
      throw new AssertionError();
    }
    
    @CheckForNull
    C correctedDoForward(@CheckForNull A a) {
      return this.second.correctedDoForward(this.first.correctedDoForward(a));
    }
    
    @CheckForNull
    A correctedDoBackward(@CheckForNull C c) {
      return this.first.correctedDoBackward(this.second.correctedDoBackward(c));
    }
    
    public boolean equals(@CheckForNull Object object) {
      if (object instanceof ConverterComposition) {
        ConverterComposition<?, ?, ?> that = (ConverterComposition<?, ?, ?>)object;
        return (this.first.equals(that.first) && this.second.equals(that.second));
      } 
      return false;
    }
    
    public int hashCode() {
      return 31 * this.first.hashCode() + this.second.hashCode();
    }
    
    public String toString() {
      return this.first + ".andThen(" + this.second + ")";
    }
  }
  
  @Deprecated
  @InlineMe(replacement = "this.convert(a)")
  public final B apply(A a) {
    return convert(a);
  }
  
  public boolean equals(@CheckForNull Object object) {
    return super.equals(object);
  }
  
  public static <A, B> Converter<A, B> from(Function<? super A, ? extends B> forwardFunction, Function<? super B, ? extends A> backwardFunction) {
    return new FunctionBasedConverter<>(forwardFunction, backwardFunction);
  }
  
  private static final class FunctionBasedConverter<A, B> extends Converter<A, B> implements Serializable {
    private final Function<? super A, ? extends B> forwardFunction;
    
    private final Function<? super B, ? extends A> backwardFunction;
    
    private FunctionBasedConverter(Function<? super A, ? extends B> forwardFunction, Function<? super B, ? extends A> backwardFunction) {
      this.forwardFunction = Preconditions.<Function<? super A, ? extends B>>checkNotNull(forwardFunction);
      this.backwardFunction = Preconditions.<Function<? super B, ? extends A>>checkNotNull(backwardFunction);
    }
    
    protected B doForward(A a) {
      return this.forwardFunction.apply(a);
    }
    
    protected A doBackward(B b) {
      return this.backwardFunction.apply(b);
    }
    
    public boolean equals(@CheckForNull Object object) {
      if (object instanceof FunctionBasedConverter) {
        FunctionBasedConverter<?, ?> that = (FunctionBasedConverter<?, ?>)object;
        return (this.forwardFunction.equals(that.forwardFunction) && this.backwardFunction
          .equals(that.backwardFunction));
      } 
      return false;
    }
    
    public int hashCode() {
      return this.forwardFunction.hashCode() * 31 + this.backwardFunction.hashCode();
    }
    
    public String toString() {
      return "Converter.from(" + this.forwardFunction + ", " + this.backwardFunction + ")";
    }
  }
  
  public static <T> Converter<T, T> identity() {
    return (IdentityConverter)IdentityConverter.INSTANCE;
  }
  
  private static final class IdentityConverter<T> extends Converter<T, T> implements Serializable {
    static final Converter<?, ?> INSTANCE = new IdentityConverter();
    
    private static final long serialVersionUID = 0L;
    
    protected T doForward(T t) {
      return t;
    }
    
    protected T doBackward(T t) {
      return t;
    }
    
    public IdentityConverter<T> reverse() {
      return this;
    }
    
    <S> Converter<T, S> doAndThen(Converter<T, S> otherConverter) {
      return Preconditions.<Converter<T, S>>checkNotNull(otherConverter, "otherConverter");
    }
    
    public String toString() {
      return "Converter.identity()";
    }
    
    private Object readResolve() {
      return INSTANCE;
    }
  }
}
