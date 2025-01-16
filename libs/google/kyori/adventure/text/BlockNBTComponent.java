package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.regex.Matcher;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.examination.Examinable;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.ApiStatus.ScheduledForRemoval;
import me.syncwrld.booter.libs.jtann.Contract;
import me.syncwrld.booter.libs.jtann.NotNull;

public interface BlockNBTComponent extends NBTComponent<BlockNBTComponent, BlockNBTComponent.Builder>, ScopedComponent<BlockNBTComponent> {
  @NotNull
  Pos pos();
  
  @Contract(pure = true)
  @NotNull
  BlockNBTComponent pos(@NotNull Pos paramPos);
  
  @Contract(pure = true)
  @NotNull
  default BlockNBTComponent localPos(double left, double up, double forwards) {
    return pos(LocalPos.localPos(left, up, forwards));
  }
  
  @Contract(pure = true)
  @NotNull
  default BlockNBTComponent worldPos(WorldPos.Coordinate x, WorldPos.Coordinate y, WorldPos.Coordinate z) {
    return pos(WorldPos.worldPos(x, y, z));
  }
  
  @Contract(pure = true)
  @NotNull
  default BlockNBTComponent absoluteWorldPos(int x, int y, int z) {
    return worldPos(WorldPos.Coordinate.absolute(x), WorldPos.Coordinate.absolute(y), WorldPos.Coordinate.absolute(z));
  }
  
  @Contract(pure = true)
  @NotNull
  default BlockNBTComponent relativeWorldPos(int x, int y, int z) {
    return worldPos(WorldPos.Coordinate.relative(x), WorldPos.Coordinate.relative(y), WorldPos.Coordinate.relative(z));
  }
  
  @NotNull
  default Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.concat(
        Stream.of(
          ExaminableProperty.of("pos", pos())), super
        
        .examinableProperties());
  }
  
  public static interface Builder extends NBTComponentBuilder<BlockNBTComponent, Builder> {
    @Contract("_ -> this")
    @NotNull
    Builder pos(@NotNull BlockNBTComponent.Pos param1Pos);
    
    @Contract("_, _, _ -> this")
    @NotNull
    default Builder localPos(double left, double up, double forwards) {
      return pos(BlockNBTComponent.LocalPos.localPos(left, up, forwards));
    }
    
    @Contract("_, _, _ -> this")
    @NotNull
    default Builder worldPos(BlockNBTComponent.WorldPos.Coordinate x, BlockNBTComponent.WorldPos.Coordinate y, BlockNBTComponent.WorldPos.Coordinate z) {
      return pos(BlockNBTComponent.WorldPos.worldPos(x, y, z));
    }
    
    @Contract("_, _, _ -> this")
    @NotNull
    default Builder absoluteWorldPos(int x, int y, int z) {
      return worldPos(BlockNBTComponent.WorldPos.Coordinate.absolute(x), BlockNBTComponent.WorldPos.Coordinate.absolute(y), BlockNBTComponent.WorldPos.Coordinate.absolute(z));
    }
    
    @Contract("_, _, _ -> this")
    @NotNull
    default Builder relativeWorldPos(int x, int y, int z) {
      return worldPos(BlockNBTComponent.WorldPos.Coordinate.relative(x), BlockNBTComponent.WorldPos.Coordinate.relative(y), BlockNBTComponent.WorldPos.Coordinate.relative(z));
    }
  }
  
  public static interface Pos extends Examinable {
    @NotNull
    static Pos fromString(@NotNull String input) throws IllegalArgumentException {
      Matcher localMatch = BlockNBTComponentImpl.Tokens.LOCAL_PATTERN.matcher(input);
      if (localMatch.matches())
        return BlockNBTComponent.LocalPos.localPos(
            Double.parseDouble(localMatch.group(1)), 
            Double.parseDouble(localMatch.group(3)), 
            Double.parseDouble(localMatch.group(5))); 
      Matcher worldMatch = BlockNBTComponentImpl.Tokens.WORLD_PATTERN.matcher(input);
      if (worldMatch.matches())
        return BlockNBTComponent.WorldPos.worldPos(
            BlockNBTComponentImpl.Tokens.deserializeCoordinate(worldMatch.group(1), worldMatch.group(2)), 
            BlockNBTComponentImpl.Tokens.deserializeCoordinate(worldMatch.group(3), worldMatch.group(4)), 
            BlockNBTComponentImpl.Tokens.deserializeCoordinate(worldMatch.group(5), worldMatch.group(6))); 
      throw new IllegalArgumentException("Cannot convert position specification '" + input + "' into a position");
    }
    
    @NotNull
    String asString();
  }
  
  public static interface LocalPos extends Pos {
    @NotNull
    static LocalPos localPos(double left, double up, double forwards) {
      return new BlockNBTComponentImpl.LocalPosImpl(left, up, forwards);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    static LocalPos of(double left, double up, double forwards) {
      return new BlockNBTComponentImpl.LocalPosImpl(left, up, forwards);
    }
    
    double left();
    
    double up();
    
    double forwards();
  }
  
  public static interface WorldPos extends Pos {
    @NotNull
    static WorldPos worldPos(@NotNull Coordinate x, @NotNull Coordinate y, @NotNull Coordinate z) {
      return new BlockNBTComponentImpl.WorldPosImpl(x, y, z);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    static WorldPos of(@NotNull Coordinate x, @NotNull Coordinate y, @NotNull Coordinate z) {
      return new BlockNBTComponentImpl.WorldPosImpl(x, y, z);
    }
    
    @NotNull
    Coordinate x();
    
    @NotNull
    Coordinate y();
    
    @NotNull
    Coordinate z();
    
    public static interface Coordinate extends Examinable {
      @NotNull
      static Coordinate absolute(int value) {
        return coordinate(value, Type.ABSOLUTE);
      }
      
      @NotNull
      static Coordinate relative(int value) {
        return coordinate(value, Type.RELATIVE);
      }
      
      @NotNull
      static Coordinate coordinate(int value, @NotNull Type type) {
        return new BlockNBTComponentImpl.WorldPosImpl.CoordinateImpl(value, type);
      }
      
      @Deprecated
      @ScheduledForRemoval(inVersion = "5.0.0")
      @NotNull
      static Coordinate of(int value, @NotNull Type type) {
        return new BlockNBTComponentImpl.WorldPosImpl.CoordinateImpl(value, type);
      }
      
      int value();
      
      @NotNull
      Type type();
      
      public enum Type {
        ABSOLUTE, RELATIVE;
      }
    }
  }
  
  public static interface Coordinate extends Examinable {
    @NotNull
    static Coordinate absolute(int value) {
      return coordinate(value, Type.ABSOLUTE);
    }
    
    @NotNull
    static Coordinate relative(int value) {
      return coordinate(value, Type.RELATIVE);
    }
    
    @NotNull
    static Coordinate coordinate(int value, @NotNull Type type) {
      return new BlockNBTComponentImpl.WorldPosImpl.CoordinateImpl(value, type);
    }
    
    @Deprecated
    @ScheduledForRemoval(inVersion = "5.0.0")
    @NotNull
    static Coordinate of(int value, @NotNull Type type) {
      return new BlockNBTComponentImpl.WorldPosImpl.CoordinateImpl(value, type);
    }
    
    int value();
    
    @NotNull
    Type type();
    
    public enum Type {
      ABSOLUTE, RELATIVE;
    }
  }
}
