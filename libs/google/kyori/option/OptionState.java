package me.syncwrld.booter.libs.google.kyori.option;

import java.util.Map;
import java.util.function.Consumer;
import me.syncwrld.booter.libs.jtann.ApiStatus.NonExtendable;
import me.syncwrld.booter.libs.jtann.NotNull;

@NonExtendable
public interface OptionState {
  static OptionState emptyOptionState() {
    return OptionStateImpl.EMPTY;
  }
  
  @NotNull
  static Builder optionState() {
    return new OptionStateImpl.BuilderImpl();
  }
  
  @NotNull
  static VersionedBuilder versionedOptionState() {
    return new OptionStateImpl.VersionedBuilderImpl();
  }
  
  boolean has(@NotNull Option<?> paramOption);
  
  <V> V value(@NotNull Option<V> paramOption);
  
  @NonExtendable
  public static interface VersionedBuilder {
    @NotNull
    VersionedBuilder version(int param1Int, @NotNull Consumer<OptionState.Builder> param1Consumer);
    
    @NotNull
    OptionState.Versioned build();
  }
  
  @NonExtendable
  public static interface Builder {
    @NotNull
    <V> Builder value(@NotNull Option<V> param1Option, @NotNull V param1V);
    
    @NotNull
    Builder values(@NotNull OptionState param1OptionState);
    
    @NotNull
    OptionState build();
  }
  
  @NonExtendable
  public static interface Versioned extends OptionState {
    @NotNull
    Map<Integer, OptionState> childStates();
    
    @NotNull
    Versioned at(int param1Int);
  }
}
