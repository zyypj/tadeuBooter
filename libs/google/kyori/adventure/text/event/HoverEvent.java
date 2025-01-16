package me.syncwrld.booter.libs.google.kyori.adventure.text.event;

import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Key;
import me.syncwrld.booter.libs.google.kyori.adventure.key.Keyed;
import me.syncwrld.booter.libs.google.kyori.adventure.nbt.api.BinaryTagHolder;
import me.syncwrld.booter.libs.google.kyori.adventure.text.Component;
import me.syncwrld.booter.libs.google.kyori.adventure.text.ComponentLike;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.StyleBuilderApplicable;
import me.syncwrld.booter.libs.google.kyori.adventure.text.renderer.ComponentRenderer;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Index;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public final class HoverEvent<V> implements Examinable, HoverEventSource<V>, StyleBuilderApplicable {
  private final Action<V> action;
  
  private final V value;
  
  @NotNull
  public static HoverEvent<Component> showText(@NotNull ComponentLike text) {
    return showText(text.asComponent());
  }
  
  @NotNull
  public static HoverEvent<Component> showText(@NotNull Component text) {
    return new HoverEvent<>(Action.SHOW_TEXT, text);
  }
  
  @NotNull
  public static HoverEvent<ShowItem> showItem(@NotNull Key item, int count) {
    return showItem(item, count, (BinaryTagHolder)null);
  }
  
  @NotNull
  public static HoverEvent<ShowItem> showItem(@NotNull Keyed item, int count) {
    return showItem(item, count, (BinaryTagHolder)null);
  }
  
  @NotNull
  public static HoverEvent<ShowItem> showItem(@NotNull Key item, int count, @Nullable BinaryTagHolder nbt) {
    return showItem(ShowItem.of(item, count, nbt));
  }
  
  @NotNull
  public static HoverEvent<ShowItem> showItem(@NotNull Keyed item, int count, @Nullable BinaryTagHolder nbt) {
    return showItem(ShowItem.of(item, count, nbt));
  }
  
  @NotNull
  public static HoverEvent<ShowItem> showItem(@NotNull ShowItem item) {
    return new HoverEvent<>(Action.SHOW_ITEM, item);
  }
  
  @NotNull
  public static HoverEvent<ShowEntity> showEntity(@NotNull Key type, @NotNull UUID id) {
    return showEntity(type, id, (Component)null);
  }
  
  @NotNull
  public static HoverEvent<ShowEntity> showEntity(@NotNull Keyed type, @NotNull UUID id) {
    return showEntity(type, id, (Component)null);
  }
  
  @NotNull
  public static HoverEvent<ShowEntity> showEntity(@NotNull Key type, @NotNull UUID id, @Nullable Component name) {
    return showEntity(ShowEntity.of(type, id, name));
  }
  
  @NotNull
  public static HoverEvent<ShowEntity> showEntity(@NotNull Keyed type, @NotNull UUID id, @Nullable Component name) {
    return showEntity(ShowEntity.of(type, id, name));
  }
  
  @NotNull
  public static HoverEvent<ShowEntity> showEntity(@NotNull ShowEntity entity) {
    return new HoverEvent<>(Action.SHOW_ENTITY, entity);
  }
  
  @Deprecated
  @NotNull
  public static HoverEvent<String> showAchievement(@NotNull String value) {
    return new HoverEvent<>(Action.SHOW_ACHIEVEMENT, value);
  }
  
  @NotNull
  public static <V> HoverEvent<V> hoverEvent(@NotNull Action<V> action, @NotNull V value) {
    return new HoverEvent<>(action, value);
  }
  
  private HoverEvent(@NotNull Action<V> action, @NotNull V value) {
    this.action = Objects.<Action<V>>requireNonNull(action, "action");
    this.value = Objects.requireNonNull(value, "value");
  }
  
  @NotNull
  public Action<V> action() {
    return this.action;
  }
  
  @NotNull
  public V value() {
    return this.value;
  }
  
  @NotNull
  public HoverEvent<V> value(@NotNull V value) {
    return new HoverEvent(this.action, value);
  }
  
  @NotNull
  public <C> HoverEvent<V> withRenderedValue(@NotNull ComponentRenderer<C> renderer, @NotNull C context) {
    V oldValue = this.value;
    V newValue = this.action.renderer.render(renderer, context, oldValue);
    if (newValue != oldValue)
      return new HoverEvent(this.action, newValue); 
    return this;
  }
  
  @NotNull
  public HoverEvent<V> asHoverEvent() {
    return this;
  }
  
  @NotNull
  public HoverEvent<V> asHoverEvent(@NotNull UnaryOperator<V> op) {
    if (op == UnaryOperator.identity())
      return this; 
    return new HoverEvent(this.action, op.apply(this.value));
  }
  
  public void styleApply(Style.Builder style) {
    style.hoverEvent(this);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (other == null || getClass() != other.getClass())
      return false; 
    HoverEvent<?> that = (HoverEvent)other;
    return (this.action == that.action && this.value.equals(that.value));
  }
  
  public int hashCode() {
    int result = this.action.hashCode();
    result = 31 * result + this.value.hashCode();
    return result;
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("action", this.action), 
          ExaminableProperty.of("value", this.value) });
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  @FunctionalInterface
  static interface Renderer<V> {
    @NotNull
    <C> V render(@NotNull ComponentRenderer<C> param1ComponentRenderer, @NotNull C param1C, @NotNull V param1V);
  }
  
  public static final class ShowItem implements Examinable {
    private final Key item;
    
    private final int count;
    
    @Nullable
    private final BinaryTagHolder nbt;
    
    @NotNull
    public static ShowItem showItem(@NotNull Key item, int count) {
      return showItem(item, count, (BinaryTagHolder)null);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    public static ShowItem of(@NotNull Key item, int count) {
      return of(item, count, (BinaryTagHolder)null);
    }
    
    @NotNull
    public static ShowItem showItem(@NotNull Keyed item, int count) {
      return showItem(item, count, (BinaryTagHolder)null);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    public static ShowItem of(@NotNull Keyed item, int count) {
      return of(item, count, (BinaryTagHolder)null);
    }
    
    @NotNull
    public static ShowItem showItem(@NotNull Key item, int count, @Nullable BinaryTagHolder nbt) {
      return new ShowItem(Objects.<Key>requireNonNull(item, "item"), count, nbt);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    public static ShowItem of(@NotNull Key item, int count, @Nullable BinaryTagHolder nbt) {
      return new ShowItem(Objects.<Key>requireNonNull(item, "item"), count, nbt);
    }
    
    @NotNull
    public static ShowItem showItem(@NotNull Keyed item, int count, @Nullable BinaryTagHolder nbt) {
      return new ShowItem(((Keyed)Objects.<Keyed>requireNonNull(item, "item")).key(), count, nbt);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    public static ShowItem of(@NotNull Keyed item, int count, @Nullable BinaryTagHolder nbt) {
      return new ShowItem(((Keyed)Objects.<Keyed>requireNonNull(item, "item")).key(), count, nbt);
    }
    
    private ShowItem(@NotNull Key item, int count, @Nullable BinaryTagHolder nbt) {
      this.item = item;
      this.count = count;
      this.nbt = nbt;
    }
    
    @NotNull
    public Key item() {
      return this.item;
    }
    
    @NotNull
    public ShowItem item(@NotNull Key item) {
      if (((Key)Objects.<Key>requireNonNull(item, "item")).equals(this.item))
        return this; 
      return new ShowItem(item, this.count, this.nbt);
    }
    
    public int count() {
      return this.count;
    }
    
    @NotNull
    public ShowItem count(int count) {
      if (count == this.count)
        return this; 
      return new ShowItem(this.item, count, this.nbt);
    }
    
    @Nullable
    public BinaryTagHolder nbt() {
      return this.nbt;
    }
    
    @NotNull
    public ShowItem nbt(@Nullable BinaryTagHolder nbt) {
      if (Objects.equals(nbt, this.nbt))
        return this; 
      return new ShowItem(this.item, this.count, nbt);
    }
    
    public boolean equals(@Nullable Object other) {
      if (this == other)
        return true; 
      if (other == null || getClass() != other.getClass())
        return false; 
      ShowItem that = (ShowItem)other;
      return (this.item.equals(that.item) && this.count == that.count && Objects.equals(this.nbt, that.nbt));
    }
    
    public int hashCode() {
      int result = this.item.hashCode();
      result = 31 * result + Integer.hashCode(this.count);
      result = 31 * result + Objects.hashCode(this.nbt);
      return result;
    }
    
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("item", this.item), 
            ExaminableProperty.of("count", this.count), 
            ExaminableProperty.of("nbt", this.nbt) });
    }
    
    public String toString() {
      return Internals.toString(this);
    }
  }
  
  public static final class ShowEntity implements Examinable {
    private final Key type;
    
    private final UUID id;
    
    private final Component name;
    
    @NotNull
    public static ShowEntity showEntity(@NotNull Key type, @NotNull UUID id) {
      return showEntity(type, id, (Component)null);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    public static ShowEntity of(@NotNull Key type, @NotNull UUID id) {
      return of(type, id, (Component)null);
    }
    
    @NotNull
    public static ShowEntity showEntity(@NotNull Keyed type, @NotNull UUID id) {
      return showEntity(type, id, (Component)null);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    public static ShowEntity of(@NotNull Keyed type, @NotNull UUID id) {
      return of(type, id, (Component)null);
    }
    
    @NotNull
    public static ShowEntity showEntity(@NotNull Key type, @NotNull UUID id, @Nullable Component name) {
      return new ShowEntity(Objects.<Key>requireNonNull(type, "type"), Objects.<UUID>requireNonNull(id, "id"), name);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    public static ShowEntity of(@NotNull Key type, @NotNull UUID id, @Nullable Component name) {
      return new ShowEntity(Objects.<Key>requireNonNull(type, "type"), Objects.<UUID>requireNonNull(id, "id"), name);
    }
    
    @NotNull
    public static ShowEntity showEntity(@NotNull Keyed type, @NotNull UUID id, @Nullable Component name) {
      return new ShowEntity(((Keyed)Objects.<Keyed>requireNonNull(type, "type")).key(), Objects.<UUID>requireNonNull(id, "id"), name);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    public static ShowEntity of(@NotNull Keyed type, @NotNull UUID id, @Nullable Component name) {
      return new ShowEntity(((Keyed)Objects.<Keyed>requireNonNull(type, "type")).key(), Objects.<UUID>requireNonNull(id, "id"), name);
    }
    
    private ShowEntity(@NotNull Key type, @NotNull UUID id, @Nullable Component name) {
      this.type = type;
      this.id = id;
      this.name = name;
    }
    
    @NotNull
    public Key type() {
      return this.type;
    }
    
    @NotNull
    public ShowEntity type(@NotNull Key type) {
      if (((Key)Objects.<Key>requireNonNull(type, "type")).equals(this.type))
        return this; 
      return new ShowEntity(type, this.id, this.name);
    }
    
    @NotNull
    public ShowEntity type(@NotNull Keyed type) {
      return type(((Keyed)Objects.<Keyed>requireNonNull(type, "type")).key());
    }
    
    @NotNull
    public UUID id() {
      return this.id;
    }
    
    @NotNull
    public ShowEntity id(@NotNull UUID id) {
      if (((UUID)Objects.<UUID>requireNonNull(id)).equals(this.id))
        return this; 
      return new ShowEntity(this.type, id, this.name);
    }
    
    @Nullable
    public Component name() {
      return this.name;
    }
    
    @NotNull
    public ShowEntity name(@Nullable Component name) {
      if (Objects.equals(name, this.name))
        return this; 
      return new ShowEntity(this.type, this.id, name);
    }
    
    public boolean equals(@Nullable Object other) {
      if (this == other)
        return true; 
      if (other == null || getClass() != other.getClass())
        return false; 
      ShowEntity that = (ShowEntity)other;
      return (this.type.equals(that.type) && this.id.equals(that.id) && Objects.equals(this.name, that.name));
    }
    
    public int hashCode() {
      int result = this.type.hashCode();
      result = 31 * result + this.id.hashCode();
      result = 31 * result + Objects.hashCode(this.name);
      return result;
    }
    
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("type", this.type), 
            ExaminableProperty.of("id", this.id), 
            ExaminableProperty.of("name", this.name) });
    }
    
    public String toString() {
      return Internals.toString(this);
    }
  }
  
  public static final class Action<V> {
    public static final Action<Component> SHOW_TEXT = new Action("show_text", (Class)Component.class, true, (Renderer)new Renderer<Component>() {
          @NotNull
          public <C> Component render(@NotNull ComponentRenderer<C> renderer, @NotNull C context, @NotNull Component value) {
            return renderer.render(value, context);
          }
        });
    
    public static final Action<HoverEvent.ShowItem> SHOW_ITEM = new Action("show_item", (Class)HoverEvent.ShowItem.class, true, (Renderer)new Renderer<HoverEvent.ShowItem>() {
          @NotNull
          public <C> HoverEvent.ShowItem render(@NotNull ComponentRenderer<C> renderer, @NotNull C context, @NotNull HoverEvent.ShowItem value) {
            return value;
          }
        });
    
    public static final Action<HoverEvent.ShowEntity> SHOW_ENTITY = new Action("show_entity", (Class)HoverEvent.ShowEntity.class, true, (Renderer)new Renderer<HoverEvent.ShowEntity>() {
          @NotNull
          public <C> HoverEvent.ShowEntity render(@NotNull ComponentRenderer<C> renderer, @NotNull C context, @NotNull HoverEvent.ShowEntity value) {
            if (value.name == null)
              return value; 
            return value.name(renderer.render(value.name, context));
          }
        });
    
    @Deprecated
    public static final Action<String> SHOW_ACHIEVEMENT = new Action("show_achievement", (Class)String.class, true, (Renderer)new Renderer<String>() {
          @NotNull
          public <C> String render(@NotNull ComponentRenderer<C> renderer, @NotNull C context, @NotNull String value) {
            return value;
          }
        });
    
    public static final Index<String, Action<?>> NAMES;
    
    private final String name;
    
    private final Class<V> type;
    
    private final boolean readable;
    
    private final Renderer<V> renderer;
    
    static {
      NAMES = Index.create(constant -> constant.name, (Object[])new Action[] { SHOW_TEXT, SHOW_ITEM, SHOW_ENTITY, SHOW_ACHIEVEMENT });
    }
    
    Action(String name, Class<V> type, boolean readable, Renderer<V> renderer) {
      this.name = name;
      this.type = type;
      this.readable = readable;
      this.renderer = renderer;
    }
    
    @NotNull
    public Class<V> type() {
      return this.type;
    }
    
    public boolean readable() {
      return this.readable;
    }
    
    @NotNull
    public String toString() {
      return this.name;
    }
    
    @FunctionalInterface
    static interface Renderer<V> {
      @NotNull
      <C> V render(@NotNull ComponentRenderer<C> param2ComponentRenderer, @NotNull C param2C, @NotNull V param2V);
    }
  }
}
