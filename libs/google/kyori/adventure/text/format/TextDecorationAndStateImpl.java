package me.syncwrld.booter.libs.google.kyori.adventure.text.format;

import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class TextDecorationAndStateImpl implements TextDecorationAndState {
  private final TextDecoration decoration;
  
  private final TextDecoration.State state;
  
  TextDecorationAndStateImpl(TextDecoration decoration, TextDecoration.State state) {
    this.decoration = decoration;
    this.state = Objects.<TextDecoration.State>requireNonNull(state, "state");
  }
  
  @NotNull
  public TextDecoration decoration() {
    return this.decoration;
  }
  
  public TextDecoration.State state() {
    return this.state;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (other == null || getClass() != other.getClass())
      return false; 
    TextDecorationAndStateImpl that = (TextDecorationAndStateImpl)other;
    return (this.decoration == that.decoration && this.state == that.state);
  }
  
  public int hashCode() {
    int result = this.decoration.hashCode();
    result = 31 * result + this.state.hashCode();
    return result;
  }
}
