package me.syncwrld.booter.libs.google.kyori.adventure.text.event;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Objects;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;

final class ClickCallbackOptionsImpl implements ClickCallback.Options {
  static final ClickCallback.Options DEFAULT = (new BuilderImpl()).build();
  
  private final int uses;
  
  private final Duration lifetime;
  
  ClickCallbackOptionsImpl(int uses, Duration lifetime) {
    this.uses = uses;
    this.lifetime = lifetime;
  }
  
  public int uses() {
    return this.uses;
  }
  
  @NotNull
  public Duration lifetime() {
    return this.lifetime;
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("uses", this.uses), 
          ExaminableProperty.of("expiration", this.lifetime) });
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  static final class BuilderImpl implements ClickCallback.Options.Builder {
    private static final int DEFAULT_USES = 1;
    
    private int uses;
    
    private Duration lifetime;
    
    BuilderImpl() {
      this.uses = 1;
      this.lifetime = ClickCallback.DEFAULT_LIFETIME;
    }
    
    BuilderImpl(ClickCallback.Options existing) {
      this.uses = existing.uses();
      this.lifetime = existing.lifetime();
    }
    
    public ClickCallback.Options build() {
      return new ClickCallbackOptionsImpl(this.uses, this.lifetime);
    }
    
    @NotNull
    public ClickCallback.Options.Builder uses(int uses) {
      this.uses = uses;
      return this;
    }
    
    @NotNull
    public ClickCallback.Options.Builder lifetime(@NotNull TemporalAmount lifetime) {
      this.lifetime = (lifetime instanceof Duration) ? (Duration)lifetime : Duration.from(Objects.<TemporalAmount>requireNonNull(lifetime, "lifetime"));
      return this;
    }
  }
}
