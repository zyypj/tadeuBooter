package me.syncwrld.booter.libs.google.kyori.adventure.text.event;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.syncwrld.booter.libs.google.kyori.adventure.audience.Audience;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.permission.PermissionChecker;
import me.syncwrld.booter.libs.google.kyori.adventure.util.PlatformAPI;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.ApiStatus.Internal;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.CheckReturnValue;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@FunctionalInterface
public interface ClickCallback<T extends Audience> {
  public static final Duration DEFAULT_LIFETIME = Duration.ofHours(12L);
  
  public static final int UNLIMITED_USES = -1;
  
  @CheckReturnValue
  @Contract(pure = true)
  @NotNull
  static <W extends Audience, N extends W> ClickCallback<W> widen(@NotNull ClickCallback<N> original, @NotNull Class<N> type, @Nullable Consumer<? super Audience> otherwise) {
    return audience -> {
        if (type.isInstance(audience)) {
          original.accept(type.cast(audience));
        } else if (otherwise != null) {
          otherwise.accept(audience);
        } 
      };
  }
  
  @CheckReturnValue
  @Contract(pure = true)
  @NotNull
  static <W extends Audience, N extends W> ClickCallback<W> widen(@NotNull ClickCallback<N> original, @NotNull Class<N> type) {
    return widen(original, type, null);
  }
  
  @CheckReturnValue
  @Contract(pure = true)
  @NotNull
  default ClickCallback<T> filter(@NotNull Predicate<T> filter) {
    return filter(filter, null);
  }
  
  @CheckReturnValue
  @Contract(pure = true)
  @NotNull
  default ClickCallback<T> filter(@NotNull Predicate<T> filter, @Nullable Consumer<? super Audience> otherwise) {
    return audience -> {
        if (filter.test(audience)) {
          accept((T)audience);
        } else if (otherwise != null) {
          otherwise.accept(audience);
        } 
      };
  }
  
  @CheckReturnValue
  @Contract(pure = true)
  @NotNull
  default ClickCallback<T> requiringPermission(@NotNull String permission) {
    return requiringPermission(permission, null);
  }
  
  @CheckReturnValue
  @Contract(pure = true)
  @NotNull
  default ClickCallback<T> requiringPermission(@NotNull String permission, @Nullable Consumer<? super Audience> otherwise) {
    return filter(audience -> ((PermissionChecker)audience.getOrDefault(PermissionChecker.POINTER, ClickCallbackInternals.ALWAYS_FALSE)).test(permission), otherwise);
  }
  
  void accept(@NotNull T paramT);
  
  @PlatformAPI
  @Internal
  public static interface Provider {
    @NotNull
    ClickEvent create(@NotNull ClickCallback<Audience> param1ClickCallback, @NotNull ClickCallback.Options param1Options);
  }
  
  @NonExtendable
  public static interface Options extends Examinable {
    @NotNull
    static Builder builder() {
      return new ClickCallbackOptionsImpl.BuilderImpl();
    }
    
    @NotNull
    static Builder builder(@NotNull Options existing) {
      return new ClickCallbackOptionsImpl.BuilderImpl(existing);
    }
    
    int uses();
    
    @NotNull
    Duration lifetime();
    
    @NonExtendable
    public static interface Builder extends AbstractBuilder<Options> {
      @NotNull
      Builder uses(int param2Int);
      
      @NotNull
      Builder lifetime(@NotNull TemporalAmount param2TemporalAmount);
    }
  }
}
