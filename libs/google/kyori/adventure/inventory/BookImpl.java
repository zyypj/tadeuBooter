package me.syncwrld.booter.libs.google.kyori.adventure.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;

final class BookImpl implements Book {
  private final Component title;
  
  private final Component author;
  
  private final List<Component> pages;
  
  BookImpl(@NotNull Component title, @NotNull Component author, @NotNull List<Component> pages) {
    this.title = Objects.<Component>requireNonNull(title, "title");
    this.author = Objects.<Component>requireNonNull(author, "author");
    this.pages = Collections.unmodifiableList(Objects.<List<? extends Component>>requireNonNull(pages, "pages"));
  }
  
  @NotNull
  public Component title() {
    return this.title;
  }
  
  @NotNull
  public Book title(@NotNull Component title) {
    return new BookImpl(Objects.<Component>requireNonNull(title, "title"), this.author, this.pages);
  }
  
  @NotNull
  public Component author() {
    return this.author;
  }
  
  @NotNull
  public Book author(@NotNull Component author) {
    return new BookImpl(this.title, Objects.<Component>requireNonNull(author, "author"), this.pages);
  }
  
  @NotNull
  public List<Component> pages() {
    return this.pages;
  }
  
  @NotNull
  public Book pages(@NotNull List<Component> pages) {
    return new BookImpl(this.title, this.author, new ArrayList<>(Objects.<Collection<? extends Component>>requireNonNull(pages, "pages")));
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("title", this.title), 
          ExaminableProperty.of("author", this.author), 
          ExaminableProperty.of("pages", this.pages) });
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof BookImpl))
      return false; 
    BookImpl that = (BookImpl)o;
    return (this.title.equals(that.title) && this.author
      .equals(that.author) && this.pages
      .equals(that.pages));
  }
  
  public int hashCode() {
    int result = this.title.hashCode();
    result = 31 * result + this.author.hashCode();
    result = 31 * result + this.pages.hashCode();
    return result;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  static final class BuilderImpl implements Book.Builder {
    private Component title = (Component)Component.empty();
    
    private Component author = (Component)Component.empty();
    
    private final List<Component> pages = new ArrayList<>();
    
    @NotNull
    public Book.Builder title(@NotNull Component title) {
      this.title = Objects.<Component>requireNonNull(title, "title");
      return this;
    }
    
    @NotNull
    public Book.Builder author(@NotNull Component author) {
      this.author = Objects.<Component>requireNonNull(author, "author");
      return this;
    }
    
    @NotNull
    public Book.Builder addPage(@NotNull Component page) {
      this.pages.add(Objects.<Component>requireNonNull(page, "page"));
      return this;
    }
    
    @NotNull
    public Book.Builder pages(@NotNull Collection<Component> pages) {
      this.pages.addAll(Objects.<Collection<? extends Component>>requireNonNull(pages, "pages"));
      return this;
    }
    
    @NotNull
    public Book.Builder pages(@NotNull Component... pages) {
      Collections.addAll(this.pages, pages);
      return this;
    }
    
    @NotNull
    public Book build() {
      return new BookImpl(this.title, this.author, new ArrayList<>(this.pages));
    }
  }
}
