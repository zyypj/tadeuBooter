package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.List;
import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class SelectorComponentImpl extends AbstractComponent implements SelectorComponent {
  private final String pattern;
  
  @Nullable
  private final Component separator;
  
  static SelectorComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String pattern, @Nullable ComponentLike separator) {
    return new SelectorComponentImpl(
        ComponentLike.asComponents(children, IS_NOT_EMPTY), 
        Objects.<Style>requireNonNull(style, "style"), 
        Objects.<String>requireNonNull(pattern, "pattern"), 
        ComponentLike.unbox(separator));
  }
  
  SelectorComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String pattern, @Nullable Component separator) {
    super((List)children, style);
    this.pattern = pattern;
    this.separator = separator;
  }
  
  @NotNull
  public String pattern() {
    return this.pattern;
  }
  
  @NotNull
  public SelectorComponent pattern(@NotNull String pattern) {
    if (Objects.equals(this.pattern, pattern))
      return this; 
    return create((List)this.children, this.style, pattern, this.separator);
  }
  
  @Nullable
  public Component separator() {
    return this.separator;
  }
  
  @NotNull
  public SelectorComponent separator(@Nullable ComponentLike separator) {
    return create((List)this.children, this.style, this.pattern, separator);
  }
  
  @NotNull
  public SelectorComponent children(@NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.pattern, this.separator);
  }
  
  @NotNull
  public SelectorComponent style(@NotNull Style style) {
    return create((List)this.children, style, this.pattern, this.separator);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof SelectorComponent))
      return false; 
    if (!super.equals(other))
      return false; 
    SelectorComponent that = (SelectorComponent)other;
    return (Objects.equals(this.pattern, that.pattern()) && Objects.equals(this.separator, that.separator()));
  }
  
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + this.pattern.hashCode();
    result = 31 * result + Objects.hashCode(this.separator);
    return result;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  @NotNull
  public SelectorComponent.Builder toBuilder() {
    return new BuilderImpl(this);
  }
  
  static final class BuilderImpl extends AbstractComponentBuilder<SelectorComponent, SelectorComponent.Builder> implements SelectorComponent.Builder {
    @Nullable
    private String pattern;
    
    @Nullable
    private Component separator;
    
    BuilderImpl() {}
    
    BuilderImpl(@NotNull SelectorComponent component) {
      super(component);
      this.pattern = component.pattern();
      this.separator = component.separator();
    }
    
    @NotNull
    public SelectorComponent.Builder pattern(@NotNull String pattern) {
      this.pattern = Objects.<String>requireNonNull(pattern, "pattern");
      return this;
    }
    
    @NotNull
    public SelectorComponent.Builder separator(@Nullable ComponentLike separator) {
      this.separator = ComponentLike.unbox(separator);
      return this;
    }
    
    @NotNull
    public SelectorComponent build() {
      if (this.pattern == null)
        throw new IllegalStateException("pattern must be set"); 
      return SelectorComponentImpl.create((List)this.children, buildStyle(), this.pattern, this.separator);
    }
  }
}
