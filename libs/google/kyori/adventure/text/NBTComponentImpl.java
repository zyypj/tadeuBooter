package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.List;
import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

abstract class NBTComponentImpl<C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> extends AbstractComponent implements NBTComponent<C, B> {
  static final boolean INTERPRET_DEFAULT = false;
  
  final String nbtPath;
  
  final boolean interpret;
  
  @Nullable
  final Component separator;
  
  NBTComponentImpl(@NotNull List<Component> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable Component separator) {
    super((List)children, style);
    this.nbtPath = nbtPath;
    this.interpret = interpret;
    this.separator = separator;
  }
  
  @NotNull
  public String nbtPath() {
    return this.nbtPath;
  }
  
  public boolean interpret() {
    return this.interpret;
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof NBTComponent))
      return false; 
    if (!super.equals(other))
      return false; 
    NBTComponent<?, ?> that = (NBTComponent<?, ?>)other;
    return (Objects.equals(this.nbtPath, that.nbtPath()) && this.interpret == that.interpret() && Objects.equals(this.separator, that.separator()));
  }
  
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + this.nbtPath.hashCode();
    result = 31 * result + Boolean.hashCode(this.interpret);
    result = 31 * result + Objects.hashCode(this.separator);
    return result;
  }
}
