package me.syncwrld.booter.libs.google.kyori.adventure.bossbar;

import me.syncwrld.booter.libs.jtann.NotNull;

public interface BossBarViewer {
  @NotNull
  Iterable<? extends BossBar> activeBossBars();
}
