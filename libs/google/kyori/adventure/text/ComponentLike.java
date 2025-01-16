package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

@FunctionalInterface
public interface ComponentLike {
  @NotNull
  static List<Component> asComponents(@NotNull List<? extends ComponentLike> likes) {
    return asComponents(likes, null);
  }
  
  @NotNull
  static List<Component> asComponents(@NotNull List<? extends ComponentLike> likes, @Nullable Predicate<? super Component> filter) {
    Objects.requireNonNull(likes, "likes");
    int size = likes.size();
    if (size == 0)
      return Collections.emptyList(); 
    ArrayList<Component> components = null;
    for (int i = 0; i < size; i++) {
      ComponentLike like = likes.get(i);
      if (like == null)
        throw new NullPointerException("likes[" + i + "]"); 
      Component component = like.asComponent();
      if (filter == null || filter.test(component)) {
        if (components == null)
          components = new ArrayList<>(size); 
        components.add(component);
      } 
    } 
    if (components == null)
      return Collections.emptyList(); 
    components.trimToSize();
    return Collections.unmodifiableList(components);
  }
  
  @Nullable
  static Component unbox(@Nullable ComponentLike like) {
    return (like != null) ? like.asComponent() : null;
  }
  
  @Contract(pure = true)
  @NotNull
  Component asComponent();
}
