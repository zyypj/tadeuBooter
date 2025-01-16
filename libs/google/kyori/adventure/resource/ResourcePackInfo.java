package me.syncwrld.booter.libs.google.kyori.adventure.resource;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface ResourcePackInfo extends Examinable, ResourcePackInfoLike {
  @NotNull
  static ResourcePackInfo resourcePackInfo(@NotNull UUID id, @NotNull URI uri, @NotNull String hash) {
    return new ResourcePackInfoImpl(id, uri, hash);
  }
  
  @NotNull
  static Builder resourcePackInfo() {
    return new ResourcePackInfoImpl.BuilderImpl();
  }
  
  @NotNull
  UUID id();
  
  @NotNull
  URI uri();
  
  @NotNull
  String hash();
  
  @NotNull
  default ResourcePackInfo asResourcePackInfo() {
    return this;
  }
  
  public static interface Builder extends AbstractBuilder<ResourcePackInfo>, ResourcePackInfoLike {
    @NotNull
    default CompletableFuture<ResourcePackInfo> computeHashAndBuild() {
      return computeHashAndBuild(ForkJoinPool.commonPool());
    }
    
    @NotNull
    default ResourcePackInfo asResourcePackInfo() {
      return build();
    }
    
    @Contract("_ -> this")
    @NotNull
    Builder id(@NotNull UUID param1UUID);
    
    @Contract("_ -> this")
    @NotNull
    Builder uri(@NotNull URI param1URI);
    
    @Contract("_ -> this")
    @NotNull
    Builder hash(@NotNull String param1String);
    
    @NotNull
    ResourcePackInfo build();
    
    @NotNull
    CompletableFuture<ResourcePackInfo> computeHashAndBuild(@NotNull Executor param1Executor);
  }
}
