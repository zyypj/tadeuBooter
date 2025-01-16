package me.syncwrld.booter.libs.google.kyori.adventure.text.format;

import java.util.Objects;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Index;
import me.syncwrld.booter.libs.google.kyori.adventure.util.TriState;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

public enum TextDecoration implements StyleBuilderApplicable, TextFormat {
  OBFUSCATED("obfuscated"),
  BOLD("bold"),
  STRIKETHROUGH("strikethrough"),
  UNDERLINED("underlined"),
  ITALIC("italic");
  
  public static final Index<String, TextDecoration> NAMES;
  
  private final String name;
  
  static {
    NAMES = Index.create(TextDecoration.class, constant -> constant.name);
  }
  
  TextDecoration(String name) {
    this.name = name;
  }
  
  @Deprecated
  @NotNull
  public final TextDecorationAndState as(boolean state) {
    return withState(state);
  }
  
  @Deprecated
  @NotNull
  public final TextDecorationAndState as(@NotNull State state) {
    return withState(state);
  }
  
  @NotNull
  public final TextDecorationAndState withState(boolean state) {
    return new TextDecorationAndStateImpl(this, State.byBoolean(state));
  }
  
  @NotNull
  public final TextDecorationAndState withState(@NotNull State state) {
    return new TextDecorationAndStateImpl(this, state);
  }
  
  @NotNull
  public final TextDecorationAndState withState(@NotNull TriState state) {
    return new TextDecorationAndStateImpl(this, State.byTriState(state));
  }
  
  public void styleApply(Style.Builder style) {
    style.decorate(this);
  }
  
  @NotNull
  public String toString() {
    return this.name;
  }
  
  public enum State {
    NOT_SET("not_set"),
    FALSE("false"),
    TRUE("true");
    
    private final String name;
    
    State(String name) {
      this.name = name;
    }
    
    public String toString() {
      return this.name;
    }
    
    @NotNull
    public static State byBoolean(boolean flag) {
      return flag ? TRUE : FALSE;
    }
    
    @NotNull
    public static State byBoolean(@Nullable Boolean flag) {
      return (flag == null) ? NOT_SET : byBoolean(flag.booleanValue());
    }
    
    @NotNull
    public static State byTriState(@NotNull TriState flag) {
      Objects.requireNonNull(flag);
      switch (flag) {
        case TRUE:
          return TRUE;
        case FALSE:
          return FALSE;
        case NOT_SET:
          return NOT_SET;
      } 
      throw new IllegalArgumentException("Unable to turn TriState: " + flag + " into a TextDecoration.State");
    }
  }
}
