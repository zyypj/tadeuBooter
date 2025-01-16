package me.syncwrld.booter.libs.google.kyori.adventure.sound;

import java.util.Objects;
import java.util.OptionalLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Keyed;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Index;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.NotNull;

@NonExtendable
public interface Sound extends Examinable {
  @NotNull
  static Builder sound() {
    return new SoundImpl.BuilderImpl();
  }
  
  @NotNull
  static Builder sound(@NotNull Sound existing) {
    return new SoundImpl.BuilderImpl(existing);
  }
  
  @NotNull
  static Sound sound(@NotNull Consumer<Builder> configurer) {
    return (Sound)AbstractBuilder.configureAndBuild(sound(), configurer);
  }
  
  @NotNull
  static Sound sound(@NotNull Key name, @NotNull Source source, float volume, float pitch) {
    return (Sound)sound().type(name).source(source).volume(volume).pitch(pitch).build();
  }
  
  @NotNull
  static Sound sound(@NotNull Type type, @NotNull Source source, float volume, float pitch) {
    Objects.requireNonNull(type, "type");
    return sound(type.key(), source, volume, pitch);
  }
  
  @NotNull
  static Sound sound(@NotNull Supplier<? extends Type> type, @NotNull Source source, float volume, float pitch) {
    return (Sound)sound().type(type).source(source).volume(volume).pitch(pitch).build();
  }
  
  @NotNull
  static Sound sound(@NotNull Key name, Source.Provider source, float volume, float pitch) {
    return sound(name, source.soundSource(), volume, pitch);
  }
  
  @NotNull
  static Sound sound(@NotNull Type type, Source.Provider source, float volume, float pitch) {
    return sound(type, source.soundSource(), volume, pitch);
  }
  
  @NotNull
  static Sound sound(@NotNull Supplier<? extends Type> type, Source.Provider source, float volume, float pitch) {
    return sound(type, source.soundSource(), volume, pitch);
  }
  
  @NotNull
  Key name();
  
  @NotNull
  Source source();
  
  float volume();
  
  float pitch();
  
  @NotNull
  OptionalLong seed();
  
  @NotNull
  SoundStop asStop();
  
  public static interface Provider {
    @NotNull
    Sound.Source soundSource();
  }
  
  public enum Source {
    MASTER("master"),
    MUSIC("music"),
    RECORD("record"),
    WEATHER("weather"),
    BLOCK("block"),
    HOSTILE("hostile"),
    NEUTRAL("neutral"),
    PLAYER("player"),
    AMBIENT("ambient"),
    VOICE("voice");
    
    public static final Index<String, Source> NAMES;
    
    private final String name;
    
    static {
      NAMES = Index.create(Source.class, source -> source.name);
    }
    
    Source(String name) {
      this.name = name;
    }
    
    public static interface Provider {
      @NotNull
      Sound.Source soundSource();
    }
  }
  
  public static interface Type extends Keyed {
    @NotNull
    Key key();
  }
  
  public static interface Emitter {
    @NotNull
    static Emitter self() {
      return SoundImpl.EMITTER_SELF;
    }
  }
  
  public static interface Builder extends AbstractBuilder<Sound> {
    @NotNull
    Builder type(@NotNull Key param1Key);
    
    @NotNull
    Builder type(@NotNull Sound.Type param1Type);
    
    @NotNull
    Builder type(@NotNull Supplier<? extends Sound.Type> param1Supplier);
    
    @NotNull
    Builder source(@NotNull Sound.Source param1Source);
    
    @NotNull
    Builder source(Sound.Source.Provider param1Provider);
    
    @NotNull
    Builder volume(float param1Float);
    
    @NotNull
    Builder pitch(float param1Float);
    
    @NotNull
    Builder seed(long param1Long);
    
    @NotNull
    Builder seed(@NotNull OptionalLong param1OptionalLong);
  }
}
