package me.syncwrld.booter.libs.google.kyori.adventure.util;

import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface HSVLike extends Examinable {
  @NotNull
  static HSVLike hsvLike(float h, float s, float v) {
    return new HSVLikeImpl(h, s, v);
  }
  
  @Deprecated
  @ScheduledForRemoval(inVersion = "5.0.0")
  @NotNull
  static HSVLike of(float h, float s, float v) {
    return new HSVLikeImpl(h, s, v);
  }
  
  @NotNull
  static HSVLike fromRGB(int red, int green, int blue) {
    float s, h, r = red / 255.0F;
    float g = green / 255.0F;
    float b = blue / 255.0F;
    float min = Math.min(r, Math.min(g, b));
    float max = Math.max(r, Math.max(g, b));
    float delta = max - min;
    if (max != 0.0F) {
      s = delta / max;
    } else {
      s = 0.0F;
    } 
    if (s == 0.0F)
      return new HSVLikeImpl(0.0F, s, max); 
    if (r == max) {
      h = (g - b) / delta;
    } else if (g == max) {
      h = 2.0F + (b - r) / delta;
    } else {
      h = 4.0F + (r - g) / delta;
    } 
    h *= 60.0F;
    if (h < 0.0F)
      h += 360.0F; 
    return new HSVLikeImpl(h / 360.0F, s, max);
  }
  
  float h();
  
  float s();
  
  float v();
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("h", h()), 
          ExaminableProperty.of("s", s()), 
          ExaminableProperty.of("v", v()) });
  }
}
