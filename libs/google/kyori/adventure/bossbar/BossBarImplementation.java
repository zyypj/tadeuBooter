package me.syncwrld.booter.libs.google.kyori.adventure.bossbar;

import java.util.Collections;
import me.syncwrld.booter.libs.jtann.ApiStatus.Internal;
import me.syncwrld.booter.libs.jtann.NotNull;

@Internal
public interface BossBarImplementation {
  @Internal
  @NotNull
  static <I extends BossBarImplementation> I get(@NotNull BossBar bar, @NotNull Class<I> type) {
    return BossBarImpl.ImplementationAccessor.get(bar, type);
  }
  
  @Internal
  @NotNull
  default Iterable<? extends BossBarViewer> viewers() {
    return Collections.emptyList();
  }
  
  @Internal
  public static interface Provider {
    @Internal
    @NotNull
    BossBarImplementation create(@NotNull BossBar param1BossBar);
  }
}
