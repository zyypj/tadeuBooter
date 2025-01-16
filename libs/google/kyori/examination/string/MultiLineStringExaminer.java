package me.syncwrld.booter.libs.google.kyori.examination.string;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

public class MultiLineStringExaminer extends AbstractExaminer<Stream<String>> {
  private static final String INDENT_2 = "  ";
  
  private final StringExaminer examiner;
  
  @NotNull
  public static MultiLineStringExaminer simpleEscaping() {
    return Instances.SIMPLE_ESCAPING;
  }
  
  public MultiLineStringExaminer(@NotNull StringExaminer examiner) {
    this.examiner = examiner;
  }
  
  @NotNull
  protected <E> Stream<String> array(E[] array, @NotNull Stream<Stream<String>> elements) {
    return arrayLike(elements);
  }
  
  @NotNull
  protected <E> Stream<String> collection(@NotNull Collection<E> collection, @NotNull Stream<Stream<String>> elements) {
    return arrayLike(elements);
  }
  
  @NotNull
  protected Stream<String> examinable(@NotNull String name, @NotNull Stream<Map.Entry<String, Stream<String>>> properties) {
    Stream<String> flattened = flatten(",", properties.map(entry -> association(examine((String)entry.getKey()), " = ", (Stream<String>)entry.getValue())));
    Stream<String> indented = indent(flattened);
    return enclose(indented, name + "{", "}");
  }
  
  @NotNull
  protected <K, V> Stream<String> map(@NotNull Map<K, V> map, @NotNull Stream<Map.Entry<Stream<String>, Stream<String>>> entries) {
    Stream<String> flattened = flatten(",", entries.map(entry -> association((Stream<String>)entry.getKey(), " = ", (Stream<String>)entry.getValue())));
    Stream<String> indented = indent(flattened);
    return enclose(indented, "{", "}");
  }
  
  @NotNull
  protected Stream<String> nil() {
    return Stream.of(this.examiner.nil());
  }
  
  @NotNull
  protected Stream<String> scalar(@NotNull Object value) {
    return Stream.of(this.examiner.scalar(value));
  }
  
  @NotNull
  public Stream<String> examine(boolean value) {
    return Stream.of(this.examiner.examine(value));
  }
  
  @NotNull
  public Stream<String> examine(byte value) {
    return Stream.of(this.examiner.examine(value));
  }
  
  @NotNull
  public Stream<String> examine(char value) {
    return Stream.of(this.examiner.examine(value));
  }
  
  @NotNull
  public Stream<String> examine(double value) {
    return Stream.of(this.examiner.examine(value));
  }
  
  @NotNull
  public Stream<String> examine(float value) {
    return Stream.of(this.examiner.examine(value));
  }
  
  @NotNull
  public Stream<String> examine(int value) {
    return Stream.of(this.examiner.examine(value));
  }
  
  @NotNull
  public Stream<String> examine(long value) {
    return Stream.of(this.examiner.examine(value));
  }
  
  @NotNull
  public Stream<String> examine(short value) {
    return Stream.of(this.examiner.examine(value));
  }
  
  @NotNull
  protected Stream<String> array(int length, IntFunction<Stream<String>> value) {
    return arrayLike(
        (length == 0) ? 
        Stream.<Stream<String>>empty() : 
        IntStream.range(0, length).<Stream<String>>mapToObj(value));
  }
  
  @NotNull
  protected <T> Stream<String> stream(@NotNull Stream<T> stream) {
    return arrayLike(stream.map(this::examine));
  }
  
  @NotNull
  protected Stream<String> stream(@NotNull DoubleStream stream) {
    return arrayLike(stream.mapToObj(this::examine));
  }
  
  @NotNull
  protected Stream<String> stream(@NotNull IntStream stream) {
    return arrayLike(stream.mapToObj(this::examine));
  }
  
  @NotNull
  protected Stream<String> stream(@NotNull LongStream stream) {
    return arrayLike(stream.mapToObj(this::examine));
  }
  
  @NotNull
  public Stream<String> examine(@Nullable String value) {
    return Stream.of(this.examiner.examine(value));
  }
  
  private Stream<String> arrayLike(Stream<Stream<String>> streams) {
    Stream<String> flattened = flatten(",", streams);
    Stream<String> indented = indent(flattened);
    return enclose(indented, "[", "]");
  }
  
  private static Stream<String> enclose(Stream<String> lines, String open, String close) {
    return enclose(lines.collect((Collector)Collectors.toList()), open, close);
  }
  
  private static Stream<String> enclose(List<String> lines, String open, String close) {
    if (lines.isEmpty())
      return Stream.of(open + close); 
    return Stream.<Stream<String>>of((Stream<String>[])new Stream[] { Stream.of(open), 
          indent(lines.stream()), 
          Stream.of(close) }).reduce(Stream.empty(), Stream::concat);
  }
  
  private static Stream<String> flatten(String delimiter, Stream<Stream<String>> bumpy) {
    List<String> flat = new ArrayList<>();
    bumpy.forEachOrdered(lines -> {
          if (!flat.isEmpty()) {
            int last = flat.size() - 1;
            flat.set(last, (String)flat.get(last) + delimiter);
          } 
          Objects.requireNonNull(flat);
          lines.forEachOrdered(flat::add);
        });
    return flat.stream();
  }
  
  private static Stream<String> association(Stream<String> left, String middle, Stream<String> right) {
    return association(left
        .collect((Collector)Collectors.toList()), middle, right
        
        .collect((Collector)Collectors.toList()));
  }
  
  private static Stream<String> association(List<String> left, String middle, List<String> right) {
    int lefts = left.size();
    int rights = right.size();
    int height = Math.max(lefts, rights);
    int leftWidth = Strings.maxLength(left.stream());
    String leftPad = (lefts < 2) ? "" : Strings.repeat(" ", leftWidth);
    String middlePad = (lefts < 2) ? "" : Strings.repeat(" ", middle.length());
    List<String> result = new ArrayList<>(height);
    for (int i = 0; i < height; i++) {
      String l = (i < lefts) ? Strings.padEnd(left.get(i), leftWidth, ' ') : leftPad;
      String m = (i == 0) ? middle : middlePad;
      String r = (i < rights) ? right.get(i) : "";
      result.add(l + m + r);
    } 
    return result.stream();
  }
  
  private static Stream<String> indent(Stream<String> lines) {
    return lines.map(line -> "  " + line);
  }
  
  private static final class Instances {
    static final MultiLineStringExaminer SIMPLE_ESCAPING = new MultiLineStringExaminer(StringExaminer.simpleEscaping());
  }
}
