package me.syncwrld.booter.libs.google.kyori.adventure.bossbar;

import java.util.Set;
import me.syncwrld.booter.libs.google.kyori.adventure.audience.Audience;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentLike;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Index;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.ApiStatus.OverrideOnly;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;

@NonExtendable
public interface BossBar extends Examinable {
  public static final float MIN_PROGRESS = 0.0F;
  
  public static final float MAX_PROGRESS = 1.0F;
  
  @Deprecated
  @ScheduledForRemoval(inVersion = "5.0.0")
  public static final float MIN_PERCENT = 0.0F;
  
  @Deprecated
  @ScheduledForRemoval(inVersion = "5.0.0")
  public static final float MAX_PERCENT = 1.0F;
  
  @NotNull
  static BossBar bossBar(@NotNull ComponentLike name, float progress, @NotNull Color color, @NotNull Overlay overlay) {
    BossBarImpl.checkProgress(progress);
    return bossBar(name.asComponent(), progress, color, overlay);
  }
  
  @NotNull
  static BossBar bossBar(@NotNull Component name, float progress, @NotNull Color color, @NotNull Overlay overlay) {
    BossBarImpl.checkProgress(progress);
    return new BossBarImpl(name, progress, color, overlay);
  }
  
  @NotNull
  static BossBar bossBar(@NotNull ComponentLike name, float progress, @NotNull Color color, @NotNull Overlay overlay, @NotNull Set<Flag> flags) {
    BossBarImpl.checkProgress(progress);
    return bossBar(name.asComponent(), progress, color, overlay, flags);
  }
  
  @NotNull
  static BossBar bossBar(@NotNull Component name, float progress, @NotNull Color color, @NotNull Overlay overlay, @NotNull Set<Flag> flags) {
    BossBarImpl.checkProgress(progress);
    return new BossBarImpl(name, progress, color, overlay, flags);
  }
  
  @NotNull
  Component name();
  
  @Contract("_ -> this")
  @NotNull
  default BossBar name(@NotNull ComponentLike name) {
    return name(name.asComponent());
  }
  
  @Contract("_ -> this")
  @NotNull
  BossBar name(@NotNull Component paramComponent);
  
  float progress();
  
  @Contract("_ -> this")
  @NotNull
  BossBar progress(float paramFloat);
  
  @Deprecated
  @ScheduledForRemoval(inVersion = "5.0.0")
  default float percent() {
    return progress();
  }
  
  @Deprecated
  @ScheduledForRemoval(inVersion = "5.0.0")
  @Contract("_ -> this")
  @NotNull
  default BossBar percent(float progress) {
    return progress(progress);
  }
  
  @NotNull
  Color color();
  
  @Contract("_ -> this")
  @NotNull
  BossBar color(@NotNull Color paramColor);
  
  @NotNull
  Overlay overlay();
  
  @Contract("_ -> this")
  @NotNull
  BossBar overlay(@NotNull Overlay paramOverlay);
  
  @NotNull
  Set<Flag> flags();
  
  @Contract("_ -> this")
  @NotNull
  BossBar flags(@NotNull Set<Flag> paramSet);
  
  boolean hasFlag(@NotNull Flag paramFlag);
  
  @Contract("_ -> this")
  @NotNull
  BossBar addFlag(@NotNull Flag paramFlag);
  
  @Contract("_ -> this")
  @NotNull
  BossBar removeFlag(@NotNull Flag paramFlag);
  
  @Contract("_ -> this")
  @NotNull
  BossBar addFlags(@NotNull Flag... paramVarArgs);
  
  @Contract("_ -> this")
  @NotNull
  BossBar removeFlags(@NotNull Flag... paramVarArgs);
  
  @Contract("_ -> this")
  @NotNull
  BossBar addFlags(@NotNull Iterable<Flag> paramIterable);
  
  @Contract("_ -> this")
  @NotNull
  BossBar removeFlags(@NotNull Iterable<Flag> paramIterable);
  
  @Contract("_ -> this")
  @NotNull
  BossBar addListener(@NotNull Listener paramListener);
  
  @Contract("_ -> this")
  @NotNull
  BossBar removeListener(@NotNull Listener paramListener);
  
  @NotNull
  Iterable<? extends BossBarViewer> viewers();
  
  @NotNull
  default BossBar addViewer(@NotNull Audience viewer) {
    viewer.showBossBar(this);
    return this;
  }
  
  @NotNull
  default BossBar removeViewer(@NotNull Audience viewer) {
    viewer.hideBossBar(this);
    return this;
  }
  
  @OverrideOnly
  public static interface Listener {
    default void bossBarNameChanged(@NotNull BossBar bar, @NotNull Component oldName, @NotNull Component newName) {}
    
    default void bossBarProgressChanged(@NotNull BossBar bar, float oldProgress, float newProgress) {
      bossBarPercentChanged(bar, oldProgress, newProgress);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    default void bossBarPercentChanged(@NotNull BossBar bar, float oldProgress, float newProgress) {}
    
    default void bossBarColorChanged(@NotNull BossBar bar, @NotNull BossBar.Color oldColor, @NotNull BossBar.Color newColor) {}
    
    default void bossBarOverlayChanged(@NotNull BossBar bar, @NotNull BossBar.Overlay oldOverlay, @NotNull BossBar.Overlay newOverlay) {}
    
    default void bossBarFlagsChanged(@NotNull BossBar bar, @NotNull Set<BossBar.Flag> flagsAdded, @NotNull Set<BossBar.Flag> flagsRemoved) {}
  }
  
  public enum Color {
    PINK("pink"),
    BLUE("blue"),
    RED("red"),
    GREEN("green"),
    YELLOW("yellow"),
    PURPLE("purple"),
    WHITE("white");
    
    public static final Index<String, Color> NAMES;
    
    private final String name;
    
    static {
      NAMES = Index.create(Color.class, color -> color.name);
    }
    
    Color(String name) {
      this.name = name;
    }
  }
  
  public enum Flag {
    DARKEN_SCREEN("darken_screen"),
    PLAY_BOSS_MUSIC("play_boss_music"),
    CREATE_WORLD_FOG("create_world_fog");
    
    public static final Index<String, Flag> NAMES;
    
    private final String name;
    
    static {
      NAMES = Index.create(Flag.class, flag -> flag.name);
    }
    
    Flag(String name) {
      this.name = name;
    }
  }
  
  public enum Overlay {
    PROGRESS("progress"),
    NOTCHED_6("notched_6"),
    NOTCHED_10("notched_10"),
    NOTCHED_12("notched_12"),
    NOTCHED_20("notched_20");
    
    public static final Index<String, Overlay> NAMES;
    
    private final String name;
    
    static {
      NAMES = Index.create(Overlay.class, overlay -> overlay.name);
    }
    
    Overlay(String name) {
      this.name = name;
    }
  }
}
