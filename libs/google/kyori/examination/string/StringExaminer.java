package me.syncwrld.booter.libs.google.kyori.examination.string;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.AbstractExaminer;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public class StringExaminer extends AbstractExaminer<String> {
  private static final Function<String, String> DEFAULT_ESCAPER;
  
  static {
    DEFAULT_ESCAPER = (string -> string.replace("\"", "\\\"").replace("\\", "\\\\").replace("\b", "\\b").replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t"));
  }
  
  private static final Collector<CharSequence, ?, String> COMMA_CURLY = Collectors.joining(", ", "{", "}");
  
  private static final Collector<CharSequence, ?, String> COMMA_SQUARE = Collectors.joining(", ", "[", "]");
  
  private final Function<String, String> escaper;
  
  @NotNull
  public static StringExaminer simpleEscaping() {
    return Instances.SIMPLE_ESCAPING;
  }
  
  public StringExaminer(@NotNull Function<String, String> escaper) {
    this.escaper = escaper;
  }
  
  @NotNull
  protected <E> String array(E[] array, @NotNull Stream<String> elements) {
    return elements.collect(COMMA_SQUARE);
  }
  
  @NotNull
  protected <E> String collection(@NotNull Collection<E> collection, @NotNull Stream<String> elements) {
    return elements.collect(COMMA_SQUARE);
  }
  
  @NotNull
  protected String examinable(@NotNull String name, @NotNull Stream<Map.Entry<String, String>> properties) {
    return name + (String)properties.<CharSequence>map(property -> (String)property.getKey() + '=' + (String)property.getValue()).collect(COMMA_CURLY);
  }
  
  @NotNull
  protected <K, V> String map(@NotNull Map<K, V> map, @NotNull Stream<Map.Entry<String, String>> entries) {
    return entries.<CharSequence>map(entry -> (String)entry.getKey() + '=' + (String)entry.getValue()).collect(COMMA_CURLY);
  }
  
  @NotNull
  protected String nil() {
    return "null";
  }
  
  @NotNull
  protected String scalar(@NotNull Object value) {
    return String.valueOf(value);
  }
  
  @NotNull
  public String examine(boolean value) {
    return String.valueOf(value);
  }
  
  @NotNull
  public String examine(byte value) {
    return String.valueOf(value);
  }
  
  @NotNull
  public String examine(char value) {
    return Strings.wrapIn(this.escaper.apply(String.valueOf(value)), '\'');
  }
  
  @NotNull
  public String examine(double value) {
    return Strings.withSuffix(String.valueOf(value), 'd');
  }
  
  @NotNull
  public String examine(float value) {
    return Strings.withSuffix(String.valueOf(value), 'f');
  }
  
  @NotNull
  public String examine(int value) {
    return String.valueOf(value);
  }
  
  @NotNull
  public String examine(long value) {
    return String.valueOf(value);
  }
  
  @NotNull
  public String examine(short value) {
    return String.valueOf(value);
  }
  
  @NotNull
  protected <T> String stream(@NotNull Stream<T> stream) {
    return stream.<CharSequence>map(this::examine).collect(COMMA_SQUARE);
  }
  
  @NotNull
  protected String stream(@NotNull DoubleStream stream) {
    return stream.<CharSequence>mapToObj(this::examine).collect(COMMA_SQUARE);
  }
  
  @NotNull
  protected String stream(@NotNull IntStream stream) {
    return stream.<CharSequence>mapToObj(this::examine).collect(COMMA_SQUARE);
  }
  
  @NotNull
  protected String stream(@NotNull LongStream stream) {
    return stream.<CharSequence>mapToObj(this::examine).collect(COMMA_SQUARE);
  }
  
  @NotNull
  public String examine(@Nullable String value) {
    if (value == null)
      return nil(); 
    return Strings.wrapIn(this.escaper.apply(value), '"');
  }
  
  @NotNull
  protected String array(int length, IntFunction<String> value) {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    for (int i = 0; i < length; i++) {
      sb.append(value.apply(i));
      if (i + 1 < length)
        sb.append(", "); 
    } 
    sb.append(']');
    return sb.toString();
  }
  
  private static final class Instances {
    static final StringExaminer SIMPLE_ESCAPING = new StringExaminer(StringExaminer.DEFAULT_ESCAPER);
  }
}
