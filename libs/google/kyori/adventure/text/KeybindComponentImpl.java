package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.List;
import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class KeybindComponentImpl extends AbstractComponent implements KeybindComponent {
  private final String keybind;
  
  static KeybindComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, @NotNull String keybind) {
    return new KeybindComponentImpl(
        ComponentLike.asComponents(children, IS_NOT_EMPTY), 
        Objects.<Style>requireNonNull(style, "style"), 
        Objects.<String>requireNonNull(keybind, "keybind"));
  }
  
  KeybindComponentImpl(@NotNull List<Component> children, @NotNull Style style, @NotNull String keybind) {
    super((List)children, style);
    this.keybind = keybind;
  }
  
  @NotNull
  public String keybind() {
    return this.keybind;
  }
  
  @NotNull
  public KeybindComponent keybind(@NotNull String keybind) {
    if (Objects.equals(this.keybind, keybind))
      return this; 
    return create((List)this.children, this.style, keybind);
  }
  
  @NotNull
  public KeybindComponent children(@NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.keybind);
  }
  
  @NotNull
  public KeybindComponent style(@NotNull Style style) {
    return create((List)this.children, style, this.keybind);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof KeybindComponent))
      return false; 
    if (!super.equals(other))
      return false; 
    KeybindComponent that = (KeybindComponent)other;
    return Objects.equals(this.keybind, that.keybind());
  }
  
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + this.keybind.hashCode();
    return result;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  @NotNull
  public KeybindComponent.Builder toBuilder() {
    return new BuilderImpl(this);
  }
  
  static final class BuilderImpl extends AbstractComponentBuilder<KeybindComponent, KeybindComponent.Builder> implements KeybindComponent.Builder {
    @Nullable
    private String keybind;
    
    BuilderImpl() {}
    
    BuilderImpl(@NotNull KeybindComponent component) {
      super(component);
      this.keybind = component.keybind();
    }
    
    @NotNull
    public KeybindComponent.Builder keybind(@NotNull String keybind) {
      this.keybind = Objects.<String>requireNonNull(keybind, "keybind");
      return this;
    }
    
    @NotNull
    public KeybindComponent build() {
      if (this.keybind == null)
        throw new IllegalStateException("keybind must be set"); 
      return KeybindComponentImpl.create((List)this.children, buildStyle(), this.keybind);
    }
  }
}
