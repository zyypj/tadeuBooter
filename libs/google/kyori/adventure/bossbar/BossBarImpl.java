package me.syncwrld.booter.libs.google.kyori.adventure.bossbar;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Services;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.ApiStatus.Internal;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class BossBarImpl extends HackyBossBarPlatformBridge implements BossBar {
  private final List<BossBar.Listener> listeners = new CopyOnWriteArrayList<>();
  
  private Component name;
  
  private float progress;
  
  private BossBar.Color color;
  
  private BossBar.Overlay overlay;
  
  private final Set<BossBar.Flag> flags = EnumSet.noneOf(BossBar.Flag.class);
  
  @Nullable
  BossBarImplementation implementation;
  
  @Internal
  static final class ImplementationAccessor {
    private static final Optional<BossBarImplementation.Provider> SERVICE = Services.service(BossBarImplementation.Provider.class);
    
    @NotNull
    static <I extends BossBarImplementation> I get(@NotNull BossBar bar, @NotNull Class<I> type) {
      BossBarImplementation implementation = ((BossBarImpl)bar).implementation;
      if (implementation == null) {
        implementation = ((BossBarImplementation.Provider)SERVICE.get()).create(bar);
        ((BossBarImpl)bar).implementation = implementation;
      } 
      return type.cast(implementation);
    }
  }
  
  BossBarImpl(@NotNull Component name, float progress, @NotNull BossBar.Color color, @NotNull BossBar.Overlay overlay) {
    this.name = Objects.<Component>requireNonNull(name, "name");
    this.progress = progress;
    this.color = Objects.<BossBar.Color>requireNonNull(color, "color");
    this.overlay = Objects.<BossBar.Overlay>requireNonNull(overlay, "overlay");
  }
  
  BossBarImpl(@NotNull Component name, float progress, @NotNull BossBar.Color color, @NotNull BossBar.Overlay overlay, @NotNull Set<BossBar.Flag> flags) {
    this(name, progress, color, overlay);
    this.flags.addAll(flags);
  }
  
  @NotNull
  public Component name() {
    return this.name;
  }
  
  @NotNull
  public BossBar name(@NotNull Component newName) {
    Objects.requireNonNull(newName, "name");
    Component oldName = this.name;
    if (!Objects.equals(newName, oldName)) {
      this.name = newName;
      forEachListener(listener -> listener.bossBarNameChanged(this, oldName, newName));
    } 
    return this;
  }
  
  public float progress() {
    return this.progress;
  }
  
  @NotNull
  public BossBar progress(float newProgress) {
    checkProgress(newProgress);
    float oldProgress = this.progress;
    if (newProgress != oldProgress) {
      this.progress = newProgress;
      forEachListener(listener -> listener.bossBarProgressChanged(this, oldProgress, newProgress));
    } 
    return this;
  }
  
  static void checkProgress(float progress) {
    if (progress < 0.0F || progress > 1.0F)
      throw new IllegalArgumentException("progress must be between 0.0 and 1.0, was " + progress); 
  }
  
  @NotNull
  public BossBar.Color color() {
    return this.color;
  }
  
  @NotNull
  public BossBar color(@NotNull BossBar.Color newColor) {
    Objects.requireNonNull(newColor, "color");
    BossBar.Color oldColor = this.color;
    if (newColor != oldColor) {
      this.color = newColor;
      forEachListener(listener -> listener.bossBarColorChanged(this, oldColor, newColor));
    } 
    return this;
  }
  
  @NotNull
  public BossBar.Overlay overlay() {
    return this.overlay;
  }
  
  @NotNull
  public BossBar overlay(@NotNull BossBar.Overlay newOverlay) {
    Objects.requireNonNull(newOverlay, "overlay");
    BossBar.Overlay oldOverlay = this.overlay;
    if (newOverlay != oldOverlay) {
      this.overlay = newOverlay;
      forEachListener(listener -> listener.bossBarOverlayChanged(this, oldOverlay, newOverlay));
    } 
    return this;
  }
  
  @NotNull
  public Set<BossBar.Flag> flags() {
    return Collections.unmodifiableSet(this.flags);
  }
  
  @NotNull
  public BossBar flags(@NotNull Set<BossBar.Flag> newFlags) {
    if (newFlags.isEmpty()) {
      Set<BossBar.Flag> oldFlags = EnumSet.copyOf(this.flags);
      this.flags.clear();
      forEachListener(listener -> listener.bossBarFlagsChanged(this, Collections.emptySet(), oldFlags));
    } else if (!this.flags.equals(newFlags)) {
      Set<BossBar.Flag> oldFlags = EnumSet.copyOf(this.flags);
      this.flags.clear();
      this.flags.addAll(newFlags);
      Set<BossBar.Flag> added = EnumSet.copyOf(newFlags);
      Objects.requireNonNull(oldFlags);
      added.removeIf(oldFlags::contains);
      Set<BossBar.Flag> removed = EnumSet.copyOf(oldFlags);
      Objects.requireNonNull(this.flags);
      removed.removeIf(this.flags::contains);
      forEachListener(listener -> listener.bossBarFlagsChanged(this, added, removed));
    } 
    return this;
  }
  
  public boolean hasFlag(@NotNull BossBar.Flag flag) {
    return this.flags.contains(flag);
  }
  
  @NotNull
  public BossBar addFlag(@NotNull BossBar.Flag flag) {
    return editFlags(flag, Set::add, BossBarImpl::onFlagsAdded);
  }
  
  @NotNull
  public BossBar removeFlag(@NotNull BossBar.Flag flag) {
    return editFlags(flag, Set::remove, BossBarImpl::onFlagsRemoved);
  }
  
  @NotNull
  private BossBar editFlags(@NotNull BossBar.Flag flag, @NotNull BiPredicate<Set<BossBar.Flag>, BossBar.Flag> predicate, BiConsumer<BossBarImpl, Set<BossBar.Flag>> onChange) {
    if (predicate.test(this.flags, flag))
      onChange.accept(this, Collections.singleton(flag)); 
    return this;
  }
  
  @NotNull
  public BossBar addFlags(@NotNull BossBar.Flag... flags) {
    return editFlags(flags, Set::add, BossBarImpl::onFlagsAdded);
  }
  
  @NotNull
  public BossBar removeFlags(@NotNull BossBar.Flag... flags) {
    return editFlags(flags, Set::remove, BossBarImpl::onFlagsRemoved);
  }
  
  @NotNull
  private BossBar editFlags(BossBar.Flag[] flags, BiPredicate<Set<BossBar.Flag>, BossBar.Flag> predicate, BiConsumer<BossBarImpl, Set<BossBar.Flag>> onChange) {
    if (flags.length == 0)
      return this; 
    Set<BossBar.Flag> changes = null;
    for (int i = 0, length = flags.length; i < length; i++) {
      if (predicate.test(this.flags, flags[i])) {
        if (changes == null)
          changes = EnumSet.noneOf(BossBar.Flag.class); 
        changes.add(flags[i]);
      } 
    } 
    if (changes != null)
      onChange.accept(this, changes); 
    return this;
  }
  
  @NotNull
  public BossBar addFlags(@NotNull Iterable<BossBar.Flag> flags) {
    return editFlags(flags, Set::add, BossBarImpl::onFlagsAdded);
  }
  
  @NotNull
  public BossBar removeFlags(@NotNull Iterable<BossBar.Flag> flags) {
    return editFlags(flags, Set::remove, BossBarImpl::onFlagsRemoved);
  }
  
  @NotNull
  private BossBar editFlags(Iterable<BossBar.Flag> flags, BiPredicate<Set<BossBar.Flag>, BossBar.Flag> predicate, BiConsumer<BossBarImpl, Set<BossBar.Flag>> onChange) {
    Set<BossBar.Flag> changes = null;
    for (BossBar.Flag flag : flags) {
      if (predicate.test(this.flags, flag)) {
        if (changes == null)
          changes = EnumSet.noneOf(BossBar.Flag.class); 
        changes.add(flag);
      } 
    } 
    if (changes != null)
      onChange.accept(this, changes); 
    return this;
  }
  
  @NotNull
  public BossBar addListener(@NotNull BossBar.Listener listener) {
    this.listeners.add(listener);
    return this;
  }
  
  @NotNull
  public BossBar removeListener(@NotNull BossBar.Listener listener) {
    this.listeners.remove(listener);
    return this;
  }
  
  @NotNull
  public Iterable<? extends BossBarViewer> viewers() {
    if (this.implementation != null)
      return this.implementation.viewers(); 
    return Collections.emptyList();
  }
  
  private void forEachListener(@NotNull Consumer<BossBar.Listener> consumer) {
    for (BossBar.Listener listener : this.listeners)
      consumer.accept(listener); 
  }
  
  private static void onFlagsAdded(BossBarImpl bar, Set<BossBar.Flag> flagsAdded) {
    bar.forEachListener(listener -> listener.bossBarFlagsChanged(bar, flagsAdded, Collections.emptySet()));
  }
  
  private static void onFlagsRemoved(BossBarImpl bar, Set<BossBar.Flag> flagsRemoved) {
    bar.forEachListener(listener -> listener.bossBarFlagsChanged(bar, Collections.emptySet(), flagsRemoved));
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("name", this.name), 
          ExaminableProperty.of("progress", this.progress), 
          ExaminableProperty.of("color", this.color), 
          ExaminableProperty.of("overlay", this.overlay), 
          ExaminableProperty.of("flags", this.flags) });
  }
  
  public String toString() {
    return Internals.toString(this);
  }
}
