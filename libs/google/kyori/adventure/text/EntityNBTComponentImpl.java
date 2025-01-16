package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.List;
import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class EntityNBTComponentImpl extends NBTComponentImpl<EntityNBTComponent, EntityNBTComponent.Builder> implements EntityNBTComponent {
  private final String selector;
  
  static EntityNBTComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable ComponentLike separator, String selector) {
    return new EntityNBTComponentImpl(
        ComponentLike.asComponents(children, IS_NOT_EMPTY), 
        Objects.<Style>requireNonNull(style, "style"), 
        Objects.<String>requireNonNull(nbtPath, "nbtPath"), interpret, 
        
        ComponentLike.unbox(separator), 
        Objects.<String>requireNonNull(selector, "selector"));
  }
  
  EntityNBTComponentImpl(@NotNull List<Component> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable Component separator, String selector) {
    super(children, style, nbtPath, interpret, separator);
    this.selector = selector;
  }
  
  @NotNull
  public EntityNBTComponent nbtPath(@NotNull String nbtPath) {
    if (Objects.equals(this.nbtPath, nbtPath))
      return this; 
    return create((List)this.children, this.style, nbtPath, this.interpret, this.separator, this.selector);
  }
  
  @NotNull
  public EntityNBTComponent interpret(boolean interpret) {
    if (this.interpret == interpret)
      return this; 
    return create((List)this.children, this.style, this.nbtPath, interpret, this.separator, this.selector);
  }
  
  @Nullable
  public Component separator() {
    return this.separator;
  }
  
  @NotNull
  public EntityNBTComponent separator(@Nullable ComponentLike separator) {
    return create((List)this.children, this.style, this.nbtPath, this.interpret, separator, this.selector);
  }
  
  @NotNull
  public String selector() {
    return this.selector;
  }
  
  @NotNull
  public EntityNBTComponent selector(@NotNull String selector) {
    if (Objects.equals(this.selector, selector))
      return this; 
    return create((List)this.children, this.style, this.nbtPath, this.interpret, this.separator, selector);
  }
  
  @NotNull
  public EntityNBTComponent children(@NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.nbtPath, this.interpret, this.separator, this.selector);
  }
  
  @NotNull
  public EntityNBTComponent style(@NotNull Style style) {
    return create((List)this.children, style, this.nbtPath, this.interpret, this.separator, this.selector);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof EntityNBTComponent))
      return false; 
    if (!super.equals(other))
      return false; 
    EntityNBTComponentImpl that = (EntityNBTComponentImpl)other;
    return Objects.equals(this.selector, that.selector());
  }
  
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + this.selector.hashCode();
    return result;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  public EntityNBTComponent.Builder toBuilder() {
    return new BuilderImpl(this);
  }
  
  static final class BuilderImpl extends AbstractNBTComponentBuilder<EntityNBTComponent, EntityNBTComponent.Builder> implements EntityNBTComponent.Builder {
    @Nullable
    private String selector;
    
    BuilderImpl() {}
    
    BuilderImpl(@NotNull EntityNBTComponent component) {
      super(component);
      this.selector = component.selector();
    }
    
    public EntityNBTComponent.Builder selector(@NotNull String selector) {
      this.selector = Objects.<String>requireNonNull(selector, "selector");
      return this;
    }
    
    @NotNull
    public EntityNBTComponent build() {
      if (this.nbtPath == null)
        throw new IllegalStateException("nbt path must be set"); 
      if (this.selector == null)
        throw new IllegalStateException("selector must be set"); 
      return EntityNBTComponentImpl.create((List)this.children, buildStyle(), this.nbtPath, this.interpret, this.separator, this.selector);
    }
  }
}
