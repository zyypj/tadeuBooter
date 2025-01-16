package me.syncwrld.booter.libs.google.guava.base;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class MoreObjects {
  public static <T> T firstNonNull(@CheckForNull T first, @CheckForNull T second) {
    if (first != null)
      return first; 
    if (second != null)
      return second; 
    throw new NullPointerException("Both parameters are null");
  }
  
  public static ToStringHelper toStringHelper(Object self) {
    return new ToStringHelper(self.getClass().getSimpleName());
  }
  
  public static ToStringHelper toStringHelper(Class<?> clazz) {
    return new ToStringHelper(clazz.getSimpleName());
  }
  
  public static ToStringHelper toStringHelper(String className) {
    return new ToStringHelper(className);
  }
  
  public static final class ToStringHelper {
    private final String className;
    
    private final ValueHolder holderHead = new ValueHolder();
    
    private ValueHolder holderTail = this.holderHead;
    
    private boolean omitNullValues = false;
    
    private boolean omitEmptyValues = false;
    
    private ToStringHelper(String className) {
      this.className = Preconditions.<String>checkNotNull(className);
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper omitNullValues() {
      this.omitNullValues = true;
      return this;
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, @CheckForNull Object value) {
      return addHolder(name, value);
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, boolean value) {
      return addUnconditionalHolder(name, String.valueOf(value));
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, char value) {
      return addUnconditionalHolder(name, String.valueOf(value));
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, double value) {
      return addUnconditionalHolder(name, String.valueOf(value));
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, float value) {
      return addUnconditionalHolder(name, String.valueOf(value));
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, int value) {
      return addUnconditionalHolder(name, String.valueOf(value));
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper add(String name, long value) {
      return addUnconditionalHolder(name, String.valueOf(value));
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(@CheckForNull Object value) {
      return addHolder(value);
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(boolean value) {
      return addUnconditionalHolder(String.valueOf(value));
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(char value) {
      return addUnconditionalHolder(String.valueOf(value));
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(double value) {
      return addUnconditionalHolder(String.valueOf(value));
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(float value) {
      return addUnconditionalHolder(String.valueOf(value));
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(int value) {
      return addUnconditionalHolder(String.valueOf(value));
    }
    
    @CanIgnoreReturnValue
    public ToStringHelper addValue(long value) {
      return addUnconditionalHolder(String.valueOf(value));
    }
    
    private static boolean isEmpty(Object value) {
      if (value instanceof CharSequence)
        return (((CharSequence)value).length() == 0); 
      if (value instanceof Collection)
        return ((Collection)value).isEmpty(); 
      if (value instanceof Map)
        return ((Map)value).isEmpty(); 
      if (value instanceof Optional)
        return !((Optional)value).isPresent(); 
      if (value instanceof OptionalInt)
        return !((OptionalInt)value).isPresent(); 
      if (value instanceof OptionalLong)
        return !((OptionalLong)value).isPresent(); 
      if (value instanceof OptionalDouble)
        return !((OptionalDouble)value).isPresent(); 
      if (value instanceof Optional)
        return !((Optional)value).isPresent(); 
      if (value.getClass().isArray())
        return (Array.getLength(value) == 0); 
      return false;
    }
    
    public String toString() {
      boolean omitNullValuesSnapshot = this.omitNullValues;
      boolean omitEmptyValuesSnapshot = this.omitEmptyValues;
      String nextSeparator = "";
      StringBuilder builder = (new StringBuilder(32)).append(this.className).append('{');
      ValueHolder valueHolder = this.holderHead.next;
      for (; valueHolder != null; 
        valueHolder = valueHolder.next) {
        Object value = valueHolder.value;
        if (valueHolder instanceof UnconditionalValueHolder || ((value == null) ? !omitNullValuesSnapshot : (!omitEmptyValuesSnapshot || 
          
          !isEmpty(value)))) {
          builder.append(nextSeparator);
          nextSeparator = ", ";
          if (valueHolder.name != null)
            builder.append(valueHolder.name).append('='); 
          if (value != null && value.getClass().isArray()) {
            Object[] objectArray = { value };
            String arrayString = Arrays.deepToString(objectArray);
            builder.append(arrayString, 1, arrayString.length() - 1);
          } else {
            builder.append(value);
          } 
        } 
      } 
      return builder.append('}').toString();
    }
    
    private ValueHolder addHolder() {
      ValueHolder valueHolder = new ValueHolder();
      this.holderTail = this.holderTail.next = valueHolder;
      return valueHolder;
    }
    
    @CanIgnoreReturnValue
    private ToStringHelper addHolder(@CheckForNull Object value) {
      ValueHolder valueHolder = addHolder();
      valueHolder.value = value;
      return this;
    }
    
    @CanIgnoreReturnValue
    private ToStringHelper addHolder(String name, @CheckForNull Object value) {
      ValueHolder valueHolder = addHolder();
      valueHolder.value = value;
      valueHolder.name = Preconditions.<String>checkNotNull(name);
      return this;
    }
    
    private UnconditionalValueHolder addUnconditionalHolder() {
      UnconditionalValueHolder valueHolder = new UnconditionalValueHolder();
      this.holderTail = this.holderTail.next = valueHolder;
      return valueHolder;
    }
    
    @CanIgnoreReturnValue
    private ToStringHelper addUnconditionalHolder(Object value) {
      UnconditionalValueHolder valueHolder = addUnconditionalHolder();
      valueHolder.value = value;
      return this;
    }
    
    @CanIgnoreReturnValue
    private ToStringHelper addUnconditionalHolder(String name, Object value) {
      UnconditionalValueHolder valueHolder = addUnconditionalHolder();
      valueHolder.value = value;
      valueHolder.name = Preconditions.<String>checkNotNull(name);
      return this;
    }
    
    static class ValueHolder {
      @CheckForNull
      String name;
      
      @CheckForNull
      Object value;
      
      @CheckForNull
      ValueHolder next;
    }
    
    private static final class UnconditionalValueHolder extends ValueHolder {
      private UnconditionalValueHolder() {}
    }
  }
}
