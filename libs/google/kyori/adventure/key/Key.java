package me.syncwrld.booter.libs.google.kyori.adventure.key;

import java.util.Comparator;
import java.util.OptionalInt;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface Key extends Comparable<Key>, Examinable, Namespaced, Keyed {
  public static final String MINECRAFT_NAMESPACE = "minecraft";
  
  public static final char DEFAULT_SEPARATOR = ':';
  
  @NotNull
  static Key key(@NotNull @KeyPattern String string) {
    return key(string, ':');
  }
  
  @NotNull
  static Key key(@NotNull String string, char character) {
    int index = string.indexOf(character);
    String namespace = (index >= 1) ? string.substring(0, index) : "minecraft";
    String value = (index >= 0) ? string.substring(index + 1) : string;
    return key(namespace, value);
  }
  
  @NotNull
  static Key key(@NotNull Namespaced namespaced, @NotNull @Value String value) {
    return key(namespaced.namespace(), value);
  }
  
  @NotNull
  static Key key(@NotNull @Namespace String namespace, @NotNull @Value String value) {
    return new KeyImpl(namespace, value);
  }
  
  @NotNull
  static Comparator<? super Key> comparator() {
    return KeyImpl.COMPARATOR;
  }
  
  static boolean parseable(@Nullable String string) {
    if (string == null)
      return false; 
    int index = string.indexOf(':');
    String namespace = (index >= 1) ? string.substring(0, index) : "minecraft";
    String value = (index >= 0) ? string.substring(index + 1) : string;
    return (parseableNamespace(namespace) && parseableValue(value));
  }
  
  static boolean parseableNamespace(@NotNull String namespace) {
    return !checkNamespace(namespace).isPresent();
  }
  
  @NotNull
  static OptionalInt checkNamespace(@NotNull String namespace) {
    for (int i = 0, length = namespace.length(); i < length; i++) {
      if (!allowedInNamespace(namespace.charAt(i)))
        return OptionalInt.of(i); 
    } 
    return OptionalInt.empty();
  }
  
  static boolean parseableValue(@NotNull String value) {
    return !checkValue(value).isPresent();
  }
  
  @NotNull
  static OptionalInt checkValue(@NotNull String value) {
    for (int i = 0, length = value.length(); i < length; i++) {
      if (!allowedInValue(value.charAt(i)))
        return OptionalInt.of(i); 
    } 
    return OptionalInt.empty();
  }
  
  static boolean allowedInNamespace(char character) {
    return KeyImpl.allowedInNamespace(character);
  }
  
  static boolean allowedInValue(char character) {
    return KeyImpl.allowedInValue(character);
  }
  
  @NotNull
  default String asMinimalString() {
    if (namespace().equals("minecraft"))
      return value(); 
    return asString();
  }
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("namespace", namespace()), 
          ExaminableProperty.of("value", value()) });
  }
  
  default int compareTo(@NotNull Key that) {
    return comparator().compare(this, that);
  }
  
  @NotNull
  default Key key() {
    return this;
  }
  
  @NotNull
  @Namespace
  String namespace();
  
  @NotNull
  @Value
  String value();
  
  @NotNull
  String asString();
}
