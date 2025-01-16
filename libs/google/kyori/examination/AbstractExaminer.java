package me.syncwrld.booter.libs.google.kyori.examination;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public abstract class AbstractExaminer<R> implements Examiner<R> {
  @NotNull
  public R examine(@Nullable Object value) {
    if (value == null)
      return nil(); 
    if (value instanceof String)
      return examine((String)value); 
    if (value instanceof Examinable)
      return examine((Examinable)value); 
    if (value instanceof Collection)
      return collection((Collection)value); 
    if (value instanceof Map)
      return map((Map<?, ?>)value); 
    if (value.getClass().isArray()) {
      Class<?> type = value.getClass().getComponentType();
      if (type.isPrimitive()) {
        if (type == boolean.class)
          return examine((boolean[])value); 
        if (type == byte.class)
          return examine((byte[])value); 
        if (type == char.class)
          return examine((char[])value); 
        if (type == double.class)
          return examine((double[])value); 
        if (type == float.class)
          return examine((float[])value); 
        if (type == int.class)
          return examine((int[])value); 
        if (type == long.class)
          return examine((long[])value); 
        if (type == short.class)
          return examine((short[])value); 
      } 
      return array((Object[])value);
    } 
    if (value instanceof Boolean)
      return examine(((Boolean)value).booleanValue()); 
    if (value instanceof Character)
      return examine(((Character)value).charValue()); 
    if (value instanceof Number) {
      if (value instanceof Byte)
        return examine(((Byte)value).byteValue()); 
      if (value instanceof Double)
        return examine(((Double)value).doubleValue()); 
      if (value instanceof Float)
        return examine(((Float)value).floatValue()); 
      if (value instanceof Integer)
        return examine(((Integer)value).intValue()); 
      if (value instanceof Long)
        return examine(((Long)value).longValue()); 
      if (value instanceof Short)
        return examine(((Short)value).shortValue()); 
    } else if (value instanceof java.util.stream.BaseStream) {
      if (value instanceof Stream)
        return stream((Stream)value); 
      if (value instanceof DoubleStream)
        return stream((DoubleStream)value); 
      if (value instanceof IntStream)
        return stream((IntStream)value); 
      if (value instanceof LongStream)
        return stream((LongStream)value); 
    } 
    return scalar(value);
  }
  
  @NotNull
  private <E> R array(E[] array) {
    return array(array, Arrays.<E>stream(array).map(this::examine));
  }
  
  @NotNull
  private <E> R collection(@NotNull Collection<E> collection) {
    return collection(collection, collection.stream().map(this::examine));
  }
  
  @NotNull
  public R examine(@NotNull String name, @NotNull Stream<? extends ExaminableProperty> properties) {
    return examinable(name, properties.map(property -> new AbstractMap.SimpleImmutableEntry<>(property.name(), property.examine(this))));
  }
  
  @NotNull
  private <K, V> R map(@NotNull Map<K, V> map) {
    return map(map, map.entrySet().stream().map(entry -> new AbstractMap.SimpleImmutableEntry<>(examine(entry.getKey()), examine(entry.getValue()))));
  }
  
  @NotNull
  public R examine(boolean[] values) {
    if (values == null)
      return nil(); 
    return array(values.length, index -> examine(values[index]));
  }
  
  @NotNull
  public R examine(byte[] values) {
    if (values == null)
      return nil(); 
    return array(values.length, index -> examine(values[index]));
  }
  
  @NotNull
  public R examine(char[] values) {
    if (values == null)
      return nil(); 
    return array(values.length, index -> examine(values[index]));
  }
  
  @NotNull
  public R examine(double[] values) {
    if (values == null)
      return nil(); 
    return array(values.length, index -> examine(values[index]));
  }
  
  @NotNull
  public R examine(float[] values) {
    if (values == null)
      return nil(); 
    return array(values.length, index -> examine(values[index]));
  }
  
  @NotNull
  public R examine(int[] values) {
    if (values == null)
      return nil(); 
    return array(values.length, index -> examine(values[index]));
  }
  
  @NotNull
  public R examine(long[] values) {
    if (values == null)
      return nil(); 
    return array(values.length, index -> examine(values[index]));
  }
  
  @NotNull
  public R examine(short[] values) {
    if (values == null)
      return nil(); 
    return array(values.length, index -> examine(values[index]));
  }
  
  @NotNull
  protected abstract <E> R array(E[] paramArrayOfE, @NotNull Stream<R> paramStream);
  
  @NotNull
  protected abstract <E> R collection(@NotNull Collection<E> paramCollection, @NotNull Stream<R> paramStream);
  
  @NotNull
  protected abstract R examinable(@NotNull String paramString, @NotNull Stream<Map.Entry<String, R>> paramStream);
  
  @NotNull
  protected abstract <K, V> R map(@NotNull Map<K, V> paramMap, @NotNull Stream<Map.Entry<R, R>> paramStream);
  
  @NotNull
  protected abstract R nil();
  
  @NotNull
  protected abstract R scalar(@NotNull Object paramObject);
  
  @NotNull
  protected abstract <T> R stream(@NotNull Stream<T> paramStream);
  
  @NotNull
  protected abstract R stream(@NotNull DoubleStream paramDoubleStream);
  
  @NotNull
  protected abstract R stream(@NotNull IntStream paramIntStream);
  
  @NotNull
  protected abstract R stream(@NotNull LongStream paramLongStream);
  
  @NotNull
  protected abstract R array(int paramInt, IntFunction<R> paramIntFunction);
}
