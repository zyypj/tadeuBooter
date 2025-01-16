package me.syncwrld.booter.libs.google.kyori.adventure.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import me.syncwrld.booter.libs.google.kyori.adventure.builder.AbstractBuilder;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;

@NonExtendable
public interface Book extends Buildable<Book, Book.Builder>, Examinable {
  @NotNull
  static Book book(@NotNull Component title, @NotNull Component author, @NotNull Collection<Component> pages) {
    return new BookImpl(title, author, new ArrayList<>(pages));
  }
  
  @NotNull
  static Book book(@NotNull Component title, @NotNull Component author, @NotNull Component... pages) {
    return book(title, author, Arrays.asList(pages));
  }
  
  @NotNull
  static Builder builder() {
    return new BookImpl.BuilderImpl();
  }
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  Book pages(@NotNull Component... pages) {
    return pages(Arrays.asList(pages));
  }
  
  @NotNull
  default Builder toBuilder() {
    return builder()
      .title(title())
      .author(author())
      .pages(pages());
  }
  
  @NotNull
  Component title();
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  Book title(@NotNull Component paramComponent);
  
  @NotNull
  Component author();
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  Book author(@NotNull Component paramComponent);
  
  @NotNull
  List<Component> pages();
  
  @Contract(value = "_ -> new", pure = true)
  @NotNull
  Book pages(@NotNull List<Component> paramList);
  
  public static interface Builder extends AbstractBuilder<Book>, Buildable.Builder<Book> {
    @Contract("_ -> this")
    @NotNull
    Builder title(@NotNull Component param1Component);
    
    @Contract("_ -> this")
    @NotNull
    Builder author(@NotNull Component param1Component);
    
    @Contract("_ -> this")
    @NotNull
    Builder addPage(@NotNull Component param1Component);
    
    @Contract("_ -> this")
    @NotNull
    Builder pages(@NotNull Component... param1VarArgs);
    
    @Contract("_ -> this")
    @NotNull
    Builder pages(@NotNull Collection<Component> param1Collection);
    
    @NotNull
    Book build();
  }
}
