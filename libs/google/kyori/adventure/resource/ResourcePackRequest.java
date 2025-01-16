package me.syncwrld.booter.libs.google.kyori.adventure.resource;

import java.util.List;
import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public interface ResourcePackRequest extends Examinable, ResourcePackRequestLike {
  @NotNull
  static ResourcePackRequest addingRequest(@NotNull ResourcePackInfoLike first, @NotNull ResourcePackInfoLike... others) {
    return (ResourcePackRequest)resourcePackRequest().packs(first, others).replace(false).build();
  }
  
  @NotNull
  static Builder resourcePackRequest() {
    return new ResourcePackRequestImpl.BuilderImpl();
  }
  
  @NotNull
  static Builder resourcePackRequest(@NotNull ResourcePackRequest existing) {
    return new ResourcePackRequestImpl.BuilderImpl(Objects.<ResourcePackRequest>requireNonNull(existing, "existing"));
  }
  
  @NotNull
  List<ResourcePackInfo> packs();
  
  @NotNull
  ResourcePackRequest packs(@NotNull Iterable<? extends ResourcePackInfoLike> paramIterable);
  
  @NotNull
  ResourcePackCallback callback();
  
  @NotNull
  ResourcePackRequest callback(@NotNull ResourcePackCallback paramResourcePackCallback);
  
  boolean replace();
  
  @NotNull
  ResourcePackRequest replace(boolean paramBoolean);
  
  boolean required();
  
  @Nullable
  Component prompt();
  
  @NotNull
  default ResourcePackRequest asResourcePackRequest() {
    return this;
  }
  
  public static interface Builder extends AbstractBuilder<ResourcePackRequest>, ResourcePackRequestLike {
    @Contract("_, _ -> this")
    @NotNull
    Builder packs(@NotNull ResourcePackInfoLike param1ResourcePackInfoLike, @NotNull ResourcePackInfoLike... param1VarArgs);
    
    @Contract("_ -> this")
    @NotNull
    Builder packs(@NotNull Iterable<? extends ResourcePackInfoLike> param1Iterable);
    
    @Contract("_ -> this")
    @NotNull
    Builder callback(@NotNull ResourcePackCallback param1ResourcePackCallback);
    
    @Contract("_ -> this")
    @NotNull
    Builder replace(boolean param1Boolean);
    
    @Contract("_ -> this")
    @NotNull
    Builder required(boolean param1Boolean);
    
    @Contract("_ -> this")
    @NotNull
    Builder prompt(@Nullable Component param1Component);
    
    @NotNull
    default ResourcePackRequest asResourcePackRequest() {
      return (ResourcePackRequest)build();
    }
  }
}
