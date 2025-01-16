package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.google.kyori.examination.string.StringExaminer;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.Debug.Renderer;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@Deprecated
@ScheduledForRemoval(inVersion = "5.0.0")
@Renderer(text = "this.debuggerString()", childrenArray = "this.children().toArray()", hasChildren = "!this.children().isEmpty()")
public abstract class AbstractComponent implements Component {
  protected final List<Component> children;
  
  protected final Style style;
  
  protected AbstractComponent(@NotNull List<? extends ComponentLike> children, @NotNull Style style) {
    this.children = ComponentLike.asComponents(children, IS_NOT_EMPTY);
    this.style = style;
  }
  
  @NotNull
  public final List<Component> children() {
    return this.children;
  }
  
  @NotNull
  public final Style style() {
    return this.style;
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof AbstractComponent))
      return false; 
    AbstractComponent that = (AbstractComponent)other;
    return (Objects.equals(this.children, that.children) && 
      Objects.equals(this.style, that.style));
  }
  
  public int hashCode() {
    int result = this.children.hashCode();
    result = 31 * result + this.style.hashCode();
    return result;
  }
  
  public abstract String toString();
  
  private String debuggerString() {
    Stream<? extends ExaminableProperty> examinablePropertiesWithoutChildren = examinableProperties().filter(property -> !property.name().equals("children"));
    return (String)StringExaminer.simpleEscaping().examine(examinableName(), examinablePropertiesWithoutChildren);
  }
}
