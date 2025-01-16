package me.syncwrld.booter.libs.google.kyori.adventure.sound;

import java.util.Objects;
import java.util.OptionalLong;
import java.util.function.Supplier;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.util.ShadyPines;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

abstract class SoundImpl implements Sound {
  static final Sound.Emitter EMITTER_SELF = new Sound.Emitter() {
      public String toString() {
        return "SelfSoundEmitter";
      }
    };
  
  private final Sound.Source source;
  
  private final float volume;
  
  private final float pitch;
  
  private final OptionalLong seed;
  
  private SoundStop stop;
  
  SoundImpl(@NotNull Sound.Source source, float volume, float pitch, OptionalLong seed) {
    this.source = source;
    this.volume = volume;
    this.pitch = pitch;
    this.seed = seed;
  }
  
  @NotNull
  public Sound.Source source() {
    return this.source;
  }
  
  public float volume() {
    return this.volume;
  }
  
  public float pitch() {
    return this.pitch;
  }
  
  public OptionalLong seed() {
    return this.seed;
  }
  
  @NotNull
  public SoundStop asStop() {
    if (this.stop == null)
      this.stop = SoundStop.namedOnSource(name(), source()); 
    return this.stop;
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof SoundImpl))
      return false; 
    SoundImpl that = (SoundImpl)other;
    return (name().equals(that.name()) && this.source == that.source && 
      
      ShadyPines.equals(this.volume, that.volume) && 
      ShadyPines.equals(this.pitch, that.pitch) && this.seed
      .equals(that.seed));
  }
  
  public int hashCode() {
    int result = name().hashCode();
    result = 31 * result + this.source.hashCode();
    result = 31 * result + Float.hashCode(this.volume);
    result = 31 * result + Float.hashCode(this.pitch);
    result = 31 * result + this.seed.hashCode();
    return result;
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("name", name()), 
          ExaminableProperty.of("source", this.source), 
          ExaminableProperty.of("volume", this.volume), 
          ExaminableProperty.of("pitch", this.pitch), 
          ExaminableProperty.of("seed", this.seed) });
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  static final class BuilderImpl implements Sound.Builder {
    private static final float DEFAULT_VOLUME = 1.0F;
    
    private static final float DEFAULT_PITCH = 1.0F;
    
    private Key eagerType;
    
    private Supplier<? extends Sound.Type> lazyType;
    
    private Sound.Source source = Sound.Source.MASTER;
    
    private float volume = 1.0F;
    
    private float pitch = 1.0F;
    
    private OptionalLong seed = OptionalLong.empty();
    
    BuilderImpl(@NotNull Sound existing) {
      if (existing instanceof SoundImpl.Eager) {
        type(((SoundImpl.Eager)existing).name);
      } else if (existing instanceof SoundImpl.Lazy) {
        type(((SoundImpl.Lazy)existing).supplier);
      } else {
        throw new IllegalArgumentException("Unknown sound type " + existing + ", must be Eager or Lazy");
      } 
      source(existing.source())
        .volume(existing.volume())
        .pitch(existing.pitch())
        .seed(existing.seed());
    }
    
    @NotNull
    public Sound.Builder type(@NotNull Key type) {
      this.eagerType = Objects.<Key>requireNonNull(type, "type");
      this.lazyType = null;
      return this;
    }
    
    @NotNull
    public Sound.Builder type(@NotNull Sound.Type type) {
      this.eagerType = Objects.<Key>requireNonNull(((Sound.Type)Objects.<Sound.Type>requireNonNull(type, "type")).key(), "type.key()");
      this.lazyType = null;
      return this;
    }
    
    @NotNull
    public Sound.Builder type(@NotNull Supplier<? extends Sound.Type> typeSupplier) {
      this.lazyType = Objects.<Supplier<? extends Sound.Type>>requireNonNull(typeSupplier, "typeSupplier");
      this.eagerType = null;
      return this;
    }
    
    @NotNull
    public Sound.Builder source(@NotNull Sound.Source source) {
      this.source = Objects.<Sound.Source>requireNonNull(source, "source");
      return this;
    }
    
    @NotNull
    public Sound.Builder source(Sound.Source.Provider source) {
      return source(source.soundSource());
    }
    
    @NotNull
    public Sound.Builder volume(float volume) {
      this.volume = volume;
      return this;
    }
    
    @NotNull
    public Sound.Builder pitch(float pitch) {
      this.pitch = pitch;
      return this;
    }
    
    @NotNull
    public Sound.Builder seed(long seed) {
      this.seed = OptionalLong.of(seed);
      return this;
    }
    
    @NotNull
    public Sound.Builder seed(@NotNull OptionalLong seed) {
      this.seed = Objects.<OptionalLong>requireNonNull(seed, "seed");
      return this;
    }
    
    @NotNull
    public Sound build() {
      if (this.eagerType != null)
        return new SoundImpl.Eager(this.eagerType, this.source, this.volume, this.pitch, this.seed); 
      if (this.lazyType != null)
        return new SoundImpl.Lazy(this.lazyType, this.source, this.volume, this.pitch, this.seed); 
      throw new IllegalStateException("A sound type must be provided to build a sound");
    }
    
    BuilderImpl() {}
  }
  
  static final class Eager extends SoundImpl {
    final Key name;
    
    Eager(@NotNull Key name, @NotNull Sound.Source source, float volume, float pitch, OptionalLong seed) {
      super(source, volume, pitch, seed);
      this.name = name;
    }
    
    @NotNull
    public Key name() {
      return this.name;
    }
  }
  
  static final class Lazy extends SoundImpl {
    final Supplier<? extends Sound.Type> supplier;
    
    Lazy(@NotNull Supplier<? extends Sound.Type> supplier, @NotNull Sound.Source source, float volume, float pitch, OptionalLong seed) {
      super(source, volume, pitch, seed);
      this.supplier = supplier;
    }
    
    @NotNull
    public Key name() {
      return ((Sound.Type)this.supplier.get()).key();
    }
  }
}
