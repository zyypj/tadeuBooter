package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.List;
import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class StorageNBTComponentImpl extends NBTComponentImpl<StorageNBTComponent, StorageNBTComponent.Builder> implements StorageNBTComponent {
  private final Key storage;
  
  @NotNull
  static StorageNBTComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable ComponentLike separator, @NotNull Key storage) {
    return new StorageNBTComponentImpl(
        ComponentLike.asComponents(children, IS_NOT_EMPTY), 
        Objects.<Style>requireNonNull(style, "style"), 
        Objects.<String>requireNonNull(nbtPath, "nbtPath"), interpret, 
        
        ComponentLike.unbox(separator), 
        Objects.<Key>requireNonNull(storage, "storage"));
  }
  
  StorageNBTComponentImpl(@NotNull List<Component> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable Component separator, Key storage) {
    super(children, style, nbtPath, interpret, separator);
    this.storage = storage;
  }
  
  @NotNull
  public StorageNBTComponent nbtPath(@NotNull String nbtPath) {
    if (Objects.equals(this.nbtPath, nbtPath))
      return this; 
    return create((List)this.children, this.style, nbtPath, this.interpret, this.separator, this.storage);
  }
  
  @NotNull
  public StorageNBTComponent interpret(boolean interpret) {
    if (this.interpret == interpret)
      return this; 
    return create((List)this.children, this.style, this.nbtPath, interpret, this.separator, this.storage);
  }
  
  @Nullable
  public Component separator() {
    return this.separator;
  }
  
  @NotNull
  public StorageNBTComponent separator(@Nullable ComponentLike separator) {
    return create((List)this.children, this.style, this.nbtPath, this.interpret, separator, this.storage);
  }
  
  @NotNull
  public Key storage() {
    return this.storage;
  }
  
  @NotNull
  public StorageNBTComponent storage(@NotNull Key storage) {
    if (Objects.equals(this.storage, storage))
      return this; 
    return create((List)this.children, this.style, this.nbtPath, this.interpret, this.separator, storage);
  }
  
  @NotNull
  public StorageNBTComponent children(@NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.nbtPath, this.interpret, this.separator, this.storage);
  }
  
  @NotNull
  public StorageNBTComponent style(@NotNull Style style) {
    return create((List)this.children, style, this.nbtPath, this.interpret, this.separator, this.storage);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof StorageNBTComponent))
      return false; 
    if (!super.equals(other))
      return false; 
    StorageNBTComponentImpl that = (StorageNBTComponentImpl)other;
    return Objects.equals(this.storage, that.storage());
  }
  
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + this.storage.hashCode();
    return result;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  public StorageNBTComponent.Builder toBuilder() {
    return new BuilderImpl(this);
  }
  
  static class BuilderImpl extends AbstractNBTComponentBuilder<StorageNBTComponent, StorageNBTComponent.Builder> implements StorageNBTComponent.Builder {
    @Nullable
    private Key storage;
    
    BuilderImpl() {}
    
    BuilderImpl(@NotNull StorageNBTComponent component) {
      super(component);
      this.storage = component.storage();
    }
    
    public StorageNBTComponent.Builder storage(@NotNull Key storage) {
      this.storage = Objects.<Key>requireNonNull(storage, "storage");
      return this;
    }
    
    @NotNull
    public StorageNBTComponent build() {
      if (this.nbtPath == null)
        throw new IllegalStateException("nbt path must be set"); 
      if (this.storage == null)
        throw new IllegalStateException("storage must be set"); 
      return StorageNBTComponentImpl.create((List)this.children, buildStyle(), this.nbtPath, this.interpret, this.separator, this.storage);
    }
  }
}
