package me.syncwrld.booter.libs.google.kyori.adventure.key;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.ijann.RegExp;
import me.syncwrld.booter.libs.jtann.NotNull;

final class KeyImpl implements Key {
  static final Comparator<? super Key> COMPARATOR = Comparator.comparing(Key::value).thenComparing(Key::namespace);
  
  @RegExp
  static final String NAMESPACE_PATTERN = "[a-z0-9_\\-.]+";
  
  @RegExp
  static final String VALUE_PATTERN = "[a-z0-9_\\-./]+";
  
  private final String namespace;
  
  private final String value;
  
  KeyImpl(@NotNull String namespace, @NotNull String value) {
    checkError("namespace", namespace, value, Key.checkNamespace(namespace));
    checkError("value", namespace, value, Key.checkValue(value));
    this.namespace = Objects.<String>requireNonNull(namespace, "namespace");
    this.value = Objects.<String>requireNonNull(value, "value");
  }
  
  private static void checkError(String name, String namespace, String value, OptionalInt index) {
    if (index.isPresent()) {
      int indexValue = index.getAsInt();
      char character = value.charAt(indexValue);
      throw new InvalidKeyException(namespace, value, String.format("Non [a-z0-9_.-] character in %s of Key[%s] at index %d ('%s', bytes: %s)", new Object[] { name, 
              
              asString(namespace, value), 
              Integer.valueOf(indexValue), 
              Character.valueOf(character), 
              Arrays.toString(String.valueOf(character).getBytes(StandardCharsets.UTF_8)) }));
    } 
  }
  
  static boolean allowedInNamespace(char character) {
    return (character == '_' || character == '-' || (character >= 'a' && character <= 'z') || (character >= '0' && character <= '9') || character == '.');
  }
  
  static boolean allowedInValue(char character) {
    return (character == '_' || character == '-' || (character >= 'a' && character <= 'z') || (character >= '0' && character <= '9') || character == '.' || character == '/');
  }
  
  @NotNull
  public String namespace() {
    return this.namespace;
  }
  
  @NotNull
  public String value() {
    return this.value;
  }
  
  @NotNull
  public String asString() {
    return asString(this.namespace, this.value);
  }
  
  @NotNull
  private static String asString(@NotNull String namespace, @NotNull String value) {
    return namespace + ':' + value;
  }
  
  @NotNull
  public String toString() {
    return asString();
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("namespace", this.namespace), 
          ExaminableProperty.of("value", this.value) });
  }
  
  public boolean equals(Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof Key))
      return false; 
    Key that = (Key)other;
    return (Objects.equals(this.namespace, that.namespace()) && Objects.equals(this.value, that.value()));
  }
  
  public int hashCode() {
    int result = this.namespace.hashCode();
    result = 31 * result + this.value.hashCode();
    return result;
  }
  
  public int compareTo(@NotNull Key that) {
    return super.compareTo(that);
  }
}
