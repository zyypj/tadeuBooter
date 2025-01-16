package me.syncwrld.booter.libs.google.kyori.adventure.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import me.syncwrld.booter.libs.jtann.NotNull;

public final class MonkeyBars {
  @SafeVarargs
  @NotNull
  public static <E extends Enum<E>> Set<E> enumSet(Class<E> type, E... constants) {
    Set<E> set = EnumSet.noneOf(type);
    Collections.addAll(set, constants);
    return Collections.unmodifiableSet(set);
  }
  
  @NotNull
  public static <T> List<T> addOne(@NotNull List<T> oldList, T newElement) {
    if (oldList.isEmpty())
      return Collections.singletonList(newElement); 
    List<T> newList = new ArrayList<>(oldList.size() + 1);
    newList.addAll(oldList);
    newList.add(newElement);
    return Collections.unmodifiableList(newList);
  }
  
  @SafeVarargs
  @NotNull
  public static <I, O> List<O> nonEmptyArrayToList(@NotNull Function<I, O> mapper, @NotNull I first, @NotNull I... others) {
    List<O> ret = new ArrayList<>(others.length + 1);
    ret.add(mapper.apply(first));
    for (I other : others)
      ret.add(Objects.requireNonNull(mapper.apply(Objects.requireNonNull(other, "source[?]")), "mapper(source[?])")); 
    return Collections.unmodifiableList(ret);
  }
  
  @NotNull
  public static <I, O> List<O> toUnmodifiableList(@NotNull Function<I, O> mapper, @NotNull Iterable<? extends I> source) {
    ArrayList<O> ret = (source instanceof Collection) ? new ArrayList<>(((Collection)source).size()) : new ArrayList<>();
    for (I el : source)
      ret.add(Objects.requireNonNull(mapper.apply(Objects.requireNonNull(el, "source[?]")), "mapper(source[?])")); 
    return Collections.unmodifiableList(ret);
  }
}
