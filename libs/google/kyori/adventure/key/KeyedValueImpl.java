package me.syncwrld.booter.libs.google.kyori.adventure.key;

import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.google.kyori.examination.Examiner;
import me.syncwrld.booter.libs.google.kyori.examination.string.StringExaminer;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class KeyedValueImpl<T> implements Examinable, KeyedValue<T> {
  private final Key key;
  
  private final T value;
  
  KeyedValueImpl(Key key, T value) {
    this.key = key;
    this.value = value;
  }
  
  @NotNull
  public Key key() {
    return this.key;
  }
  
  @NotNull
  public T value() {
    return this.value;
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (other == null || getClass() != other.getClass())
      return false; 
    KeyedValueImpl<?> that = (KeyedValueImpl)other;
    return (this.key.equals(that.key) && this.value.equals(that.value));
  }
  
  public int hashCode() {
    int result = this.key.hashCode();
    result = 31 * result + this.value.hashCode();
    return result;
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("key", this.key), 
          ExaminableProperty.of("value", this.value) });
  }
  
  public String toString() {
    return (String)examine((Examiner)StringExaminer.simpleEscaping());
  }
}
