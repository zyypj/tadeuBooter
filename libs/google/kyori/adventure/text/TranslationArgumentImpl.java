package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.Objects;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class TranslationArgumentImpl implements TranslationArgument {
  private static final Component TRUE = Component.text("true");
  
  private static final Component FALSE = Component.text("false");
  
  private final Object value;
  
  TranslationArgumentImpl(Object value) {
    this.value = value;
  }
  
  @NotNull
  public Object value() {
    return this.value;
  }
  
  @NotNull
  public Component asComponent() {
    if (this.value instanceof Component)
      return (Component)this.value; 
    if (this.value instanceof Boolean)
      return ((Boolean)this.value).booleanValue() ? TRUE : FALSE; 
    return Component.text(String.valueOf(this.value));
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (other == null || getClass() != other.getClass())
      return false; 
    TranslationArgumentImpl that = (TranslationArgumentImpl)other;
    return Objects.equals(this.value, that.value);
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.value });
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(
        ExaminableProperty.of("value", this.value));
  }
}
