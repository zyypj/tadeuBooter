package me.syncwrld.booter.libs.google.kyori.option;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class OptionStateImpl implements OptionState {
  static final OptionState EMPTY = new OptionStateImpl(new IdentityHashMap<>());
  
  private final IdentityHashMap<Option<?>, Object> values;
  
  OptionStateImpl(IdentityHashMap<Option<?>, Object> values) {
    this.values = new IdentityHashMap<>(values);
  }
  
  public boolean has(@NotNull Option<?> option) {
    return this.values.containsKey(Objects.requireNonNull(option, "flag"));
  }
  
  public <V> V value(@NotNull Option<V> option) {
    V value = option.type().cast(this.values.get(Objects.requireNonNull(option, "flag")));
    return (value == null) ? option.defaultValue() : value;
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (other == null || getClass() != other.getClass())
      return false; 
    OptionStateImpl that = (OptionStateImpl)other;
    return Objects.equals(this.values, that.values);
  }
  
  public int hashCode() {
    return Objects.hash(new Object[] { this.values });
  }
  
  public String toString() {
    return getClass().getSimpleName() + "{values=" + this.values + '}';
  }
  
  static final class VersionedImpl implements OptionState.Versioned {
    private final SortedMap<Integer, OptionState> sets;
    
    private final int targetVersion;
    
    private final OptionState filtered;
    
    VersionedImpl(SortedMap<Integer, OptionState> sets, int targetVersion, OptionState filtered) {
      this.sets = sets;
      this.targetVersion = targetVersion;
      this.filtered = filtered;
    }
    
    public boolean has(@NotNull Option<?> option) {
      return this.filtered.has(option);
    }
    
    public <V> V value(@NotNull Option<V> option) {
      return this.filtered.value(option);
    }
    
    @NotNull
    public Map<Integer, OptionState> childStates() {
      return Collections.unmodifiableSortedMap(this.sets.headMap(Integer.valueOf(this.targetVersion + 1)));
    }
    
    @NotNull
    public OptionState.Versioned at(int version) {
      return new VersionedImpl(this.sets, version, flattened(this.sets, version));
    }
    
    public static OptionState flattened(SortedMap<Integer, OptionState> versions, int targetVersion) {
      Map<Integer, OptionState> applicable = versions.headMap(Integer.valueOf(targetVersion + 1));
      OptionState.Builder builder = OptionState.optionState();
      for (OptionState child : applicable.values())
        builder.values(child); 
      return builder.build();
    }
    
    public boolean equals(@Nullable Object other) {
      if (this == other)
        return true; 
      if (other == null || getClass() != other.getClass())
        return false; 
      VersionedImpl that = (VersionedImpl)other;
      return (this.targetVersion == that.targetVersion && 
        Objects.equals(this.sets, that.sets) && 
        Objects.equals(this.filtered, that.filtered));
    }
    
    public int hashCode() {
      return Objects.hash(new Object[] { this.sets, 
            
            Integer.valueOf(this.targetVersion), this.filtered });
    }
    
    public String toString() {
      return getClass().getSimpleName() + "{sets=" + this.sets + ", targetVersion=" + this.targetVersion + ", filtered=" + this.filtered + '}';
    }
  }
  
  static final class BuilderImpl implements OptionState.Builder {
    private final IdentityHashMap<Option<?>, Object> values = new IdentityHashMap<>();
    
    @NotNull
    public OptionState build() {
      if (this.values.isEmpty())
        return OptionStateImpl.EMPTY; 
      return new OptionStateImpl(this.values);
    }
    
    @NotNull
    public <V> OptionState.Builder value(@NotNull Option<V> option, @NotNull V value) {
      this.values.put(
          Objects.<Option>requireNonNull(option, "flag"), 
          Objects.requireNonNull(value, "value"));
      return this;
    }
    
    @NotNull
    public OptionState.Builder values(@NotNull OptionState existing) {
      if (existing instanceof OptionStateImpl) {
        this.values.putAll(((OptionStateImpl)existing).values);
      } else if (existing instanceof OptionStateImpl.VersionedImpl) {
        this.values.putAll(((OptionStateImpl)((OptionStateImpl.VersionedImpl)existing).filtered).values);
      } else {
        throw new IllegalArgumentException("existing set " + existing + " is of an unknown implementation type");
      } 
      return this;
    }
  }
  
  static final class VersionedBuilderImpl implements OptionState.VersionedBuilder {
    private final Map<Integer, OptionStateImpl.BuilderImpl> builders = new TreeMap<>();
    
    public OptionState.Versioned build() {
      if (this.builders.isEmpty())
        return new OptionStateImpl.VersionedImpl(Collections.emptySortedMap(), 0, OptionState.emptyOptionState()); 
      SortedMap<Integer, OptionState> built = new TreeMap<>();
      for (Map.Entry<Integer, OptionStateImpl.BuilderImpl> entry : this.builders.entrySet())
        built.put(entry.getKey(), ((OptionStateImpl.BuilderImpl)entry.getValue()).build()); 
      return new OptionStateImpl.VersionedImpl(built, ((Integer)built.lastKey()).intValue(), OptionStateImpl.VersionedImpl.flattened(built, ((Integer)built.lastKey()).intValue()));
    }
    
    @NotNull
    public OptionState.VersionedBuilder version(int version, @NotNull Consumer<OptionState.Builder> versionBuilder) {
      ((Consumer<OptionState.Builder>)Objects.<Consumer<OptionState.Builder>>requireNonNull(versionBuilder, "versionBuilder"))
        .accept(this.builders.computeIfAbsent(Integer.valueOf(version), $ -> new OptionStateImpl.BuilderImpl()));
      return this;
    }
  }
}
