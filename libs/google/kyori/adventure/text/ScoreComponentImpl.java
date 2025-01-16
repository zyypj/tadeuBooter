package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.List;
import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class ScoreComponentImpl extends AbstractComponent implements ScoreComponent {
  private final String name;
  
  private final String objective;
  
  @Deprecated
  @Nullable
  private final String value;
  
  static ScoreComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String name, @NotNull String objective, @Nullable String value) {
    return new ScoreComponentImpl(
        ComponentLike.asComponents(children, IS_NOT_EMPTY), 
        Objects.<Style>requireNonNull(style, "style"), 
        Objects.<String>requireNonNull(name, "name"), 
        Objects.<String>requireNonNull(objective, "objective"), value);
  }
  
  ScoreComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String name, @NotNull String objective, @Nullable String value) {
    super((List)children, style);
    this.name = name;
    this.objective = objective;
    this.value = value;
  }
  
  @NotNull
  public String name() {
    return this.name;
  }
  
  @NotNull
  public ScoreComponent name(@NotNull String name) {
    if (Objects.equals(this.name, name))
      return this; 
    return create((List)this.children, this.style, name, this.objective, this.value);
  }
  
  @NotNull
  public String objective() {
    return this.objective;
  }
  
  @NotNull
  public ScoreComponent objective(@NotNull String objective) {
    if (Objects.equals(this.objective, objective))
      return this; 
    return create((List)this.children, this.style, this.name, objective, this.value);
  }
  
  @Deprecated
  @Nullable
  public String value() {
    return this.value;
  }
  
  @Deprecated
  @NotNull
  public ScoreComponent value(@Nullable String value) {
    if (Objects.equals(this.value, value))
      return this; 
    return create((List)this.children, this.style, this.name, this.objective, value);
  }
  
  @NotNull
  public ScoreComponent children(@NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.name, this.objective, this.value);
  }
  
  @NotNull
  public ScoreComponent style(@NotNull Style style) {
    return create((List)this.children, style, this.name, this.objective, this.value);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof ScoreComponent))
      return false; 
    if (!super.equals(other))
      return false; 
    ScoreComponent that = (ScoreComponent)other;
    return (Objects.equals(this.name, that.name()) && 
      Objects.equals(this.objective, that.objective()) && 
      Objects.equals(this.value, that.value()));
  }
  
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + this.name.hashCode();
    result = 31 * result + this.objective.hashCode();
    result = 31 * result + Objects.hashCode(this.value);
    return result;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  @NotNull
  public ScoreComponent.Builder toBuilder() {
    return new BuilderImpl(this);
  }
  
  static final class BuilderImpl extends AbstractComponentBuilder<ScoreComponent, ScoreComponent.Builder> implements ScoreComponent.Builder {
    @Nullable
    private String name;
    
    @Nullable
    private String objective;
    
    @Nullable
    private String value;
    
    BuilderImpl() {}
    
    BuilderImpl(@NotNull ScoreComponent component) {
      super(component);
      this.name = component.name();
      this.objective = component.objective();
      this.value = component.value();
    }
    
    @NotNull
    public ScoreComponent.Builder name(@NotNull String name) {
      this.name = Objects.<String>requireNonNull(name, "name");
      return this;
    }
    
    @NotNull
    public ScoreComponent.Builder objective(@NotNull String objective) {
      this.objective = Objects.<String>requireNonNull(objective, "objective");
      return this;
    }
    
    @Deprecated
    @NotNull
    public ScoreComponent.Builder value(@Nullable String value) {
      this.value = value;
      return this;
    }
    
    @NotNull
    public ScoreComponent build() {
      if (this.name == null)
        throw new IllegalStateException("name must be set"); 
      if (this.objective == null)
        throw new IllegalStateException("objective must be set"); 
      return ScoreComponentImpl.create((List)this.children, buildStyle(), this.name, this.objective, this.value);
    }
  }
}
