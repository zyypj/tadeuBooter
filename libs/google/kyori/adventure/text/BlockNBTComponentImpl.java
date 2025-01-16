package me.syncwrld.booter.libs.google.kyori.adventure.text;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.adventure.text.format.Style;
import me.syncwrld.booter.libs.google.kyori.adventure.util.Buildable;
import me.syncwrld.booter.libs.google.kyori.adventure.util.ShadyPines;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class BlockNBTComponentImpl extends NBTComponentImpl<BlockNBTComponent, BlockNBTComponent.Builder> implements BlockNBTComponent {
  private final BlockNBTComponent.Pos pos;
  
  static BlockNBTComponent create(@NotNull List<? extends ComponentLike> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable ComponentLike separator, @NotNull BlockNBTComponent.Pos pos) {
    return new BlockNBTComponentImpl(
        ComponentLike.asComponents(children, IS_NOT_EMPTY), 
        Objects.<Style>requireNonNull(style, "style"), 
        Objects.<String>requireNonNull(nbtPath, "nbtPath"), interpret, 
        
        ComponentLike.unbox(separator), 
        Objects.<BlockNBTComponent.Pos>requireNonNull(pos, "pos"));
  }
  
  BlockNBTComponentImpl(@NotNull List<Component> children, @NotNull Style style, String nbtPath, boolean interpret, @Nullable Component separator, @NotNull BlockNBTComponent.Pos pos) {
    super(children, style, nbtPath, interpret, separator);
    this.pos = pos;
  }
  
  @NotNull
  public BlockNBTComponent nbtPath(@NotNull String nbtPath) {
    if (Objects.equals(this.nbtPath, nbtPath))
      return this; 
    return create((List)this.children, this.style, nbtPath, this.interpret, this.separator, this.pos);
  }
  
  @NotNull
  public BlockNBTComponent interpret(boolean interpret) {
    if (this.interpret == interpret)
      return this; 
    return create((List)this.children, this.style, this.nbtPath, interpret, this.separator, this.pos);
  }
  
  @Nullable
  public Component separator() {
    return this.separator;
  }
  
  @NotNull
  public BlockNBTComponent separator(@Nullable ComponentLike separator) {
    return create((List)this.children, this.style, this.nbtPath, this.interpret, separator, this.pos);
  }
  
  @NotNull
  public BlockNBTComponent.Pos pos() {
    return this.pos;
  }
  
  @NotNull
  public BlockNBTComponent pos(@NotNull BlockNBTComponent.Pos pos) {
    return create((List)this.children, this.style, this.nbtPath, this.interpret, this.separator, pos);
  }
  
  @NotNull
  public BlockNBTComponent children(@NotNull List<? extends ComponentLike> children) {
    return create(children, this.style, this.nbtPath, this.interpret, this.separator, this.pos);
  }
  
  @NotNull
  public BlockNBTComponent style(@NotNull Style style) {
    return create((List)this.children, style, this.nbtPath, this.interpret, this.separator, this.pos);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof BlockNBTComponent))
      return false; 
    if (!super.equals(other))
      return false; 
    BlockNBTComponent that = (BlockNBTComponent)other;
    return Objects.equals(this.pos, that.pos());
  }
  
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + this.pos.hashCode();
    return result;
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  public BlockNBTComponent.Builder toBuilder() {
    return new BuilderImpl(this);
  }
  
  static final class BuilderImpl extends AbstractNBTComponentBuilder<BlockNBTComponent, BlockNBTComponent.Builder> implements BlockNBTComponent.Builder {
    @Nullable
    private BlockNBTComponent.Pos pos;
    
    BuilderImpl() {}
    
    BuilderImpl(@NotNull BlockNBTComponent component) {
      super(component);
      this.pos = component.pos();
    }
    
    public BlockNBTComponent.Builder pos(@NotNull BlockNBTComponent.Pos pos) {
      this.pos = Objects.<BlockNBTComponent.Pos>requireNonNull(pos, "pos");
      return this;
    }
    
    @NotNull
    public BlockNBTComponent build() {
      if (this.nbtPath == null)
        throw new IllegalStateException("nbt path must be set"); 
      if (this.pos == null)
        throw new IllegalStateException("pos must be set"); 
      return BlockNBTComponentImpl.create((List)this.children, buildStyle(), this.nbtPath, this.interpret, this.separator, this.pos);
    }
  }
  
  static final class LocalPosImpl implements BlockNBTComponent.LocalPos {
    private final double left;
    
    private final double up;
    
    private final double forwards;
    
    LocalPosImpl(double left, double up, double forwards) {
      this.left = left;
      this.up = up;
      this.forwards = forwards;
    }
    
    public double left() {
      return this.left;
    }
    
    public double up() {
      return this.up;
    }
    
    public double forwards() {
      return this.forwards;
    }
    
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("left", this.left), 
            ExaminableProperty.of("up", this.up), 
            ExaminableProperty.of("forwards", this.forwards) });
    }
    
    public boolean equals(@Nullable Object other) {
      if (this == other)
        return true; 
      if (!(other instanceof BlockNBTComponent.LocalPos))
        return false; 
      BlockNBTComponent.LocalPos that = (BlockNBTComponent.LocalPos)other;
      return (ShadyPines.equals(that.left(), left()) && 
        ShadyPines.equals(that.up(), up()) && 
        ShadyPines.equals(that.forwards(), forwards()));
    }
    
    public int hashCode() {
      int result = Double.hashCode(this.left);
      result = 31 * result + Double.hashCode(this.up);
      result = 31 * result + Double.hashCode(this.forwards);
      return result;
    }
    
    public String toString() {
      return String.format("^%f ^%f ^%f", new Object[] { Double.valueOf(this.left), Double.valueOf(this.up), Double.valueOf(this.forwards) });
    }
    
    @NotNull
    public String asString() {
      return BlockNBTComponentImpl.Tokens.serializeLocal(this.left) + ' ' + BlockNBTComponentImpl.Tokens.serializeLocal(this.up) + ' ' + BlockNBTComponentImpl.Tokens.serializeLocal(this.forwards);
    }
  }
  
  static final class WorldPosImpl implements BlockNBTComponent.WorldPos {
    private final BlockNBTComponent.WorldPos.Coordinate x;
    
    private final BlockNBTComponent.WorldPos.Coordinate y;
    
    private final BlockNBTComponent.WorldPos.Coordinate z;
    
    WorldPosImpl(BlockNBTComponent.WorldPos.Coordinate x, BlockNBTComponent.WorldPos.Coordinate y, BlockNBTComponent.WorldPos.Coordinate z) {
      this.x = Objects.<BlockNBTComponent.WorldPos.Coordinate>requireNonNull(x, "x");
      this.y = Objects.<BlockNBTComponent.WorldPos.Coordinate>requireNonNull(y, "y");
      this.z = Objects.<BlockNBTComponent.WorldPos.Coordinate>requireNonNull(z, "z");
    }
    
    @NotNull
    public BlockNBTComponent.WorldPos.Coordinate x() {
      return this.x;
    }
    
    @NotNull
    public BlockNBTComponent.WorldPos.Coordinate y() {
      return this.y;
    }
    
    @NotNull
    public BlockNBTComponent.WorldPos.Coordinate z() {
      return this.z;
    }
    
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
      return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("x", this.x), 
            ExaminableProperty.of("y", this.y), 
            ExaminableProperty.of("z", this.z) });
    }
    
    public boolean equals(@Nullable Object other) {
      if (this == other)
        return true; 
      if (!(other instanceof BlockNBTComponent.WorldPos))
        return false; 
      BlockNBTComponent.WorldPos that = (BlockNBTComponent.WorldPos)other;
      return (this.x.equals(that.x()) && this.y
        .equals(that.y()) && this.z
        .equals(that.z()));
    }
    
    public int hashCode() {
      int result = this.x.hashCode();
      result = 31 * result + this.y.hashCode();
      result = 31 * result + this.z.hashCode();
      return result;
    }
    
    public String toString() {
      return this.x.toString() + ' ' + this.y.toString() + ' ' + this.z.toString();
    }
    
    @NotNull
    public String asString() {
      return BlockNBTComponentImpl.Tokens.serializeCoordinate(x()) + ' ' + BlockNBTComponentImpl.Tokens.serializeCoordinate(y()) + ' ' + BlockNBTComponentImpl.Tokens.serializeCoordinate(z());
    }
    
    static final class CoordinateImpl implements BlockNBTComponent.WorldPos.Coordinate {
      private final int value;
      
      private final BlockNBTComponent.WorldPos.Coordinate.Type type;
      
      CoordinateImpl(int value, @NotNull BlockNBTComponent.WorldPos.Coordinate.Type type) {
        this.value = value;
        this.type = Objects.<BlockNBTComponent.WorldPos.Coordinate.Type>requireNonNull(type, "type");
      }
      
      public int value() {
        return this.value;
      }
      
      @NotNull
      public BlockNBTComponent.WorldPos.Coordinate.Type type() {
        return this.type;
      }
      
      @NotNull
      public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("value", this.value), 
              ExaminableProperty.of("type", this.type) });
      }
      
      public boolean equals(@Nullable Object other) {
        if (this == other)
          return true; 
        if (!(other instanceof BlockNBTComponent.WorldPos.Coordinate))
          return false; 
        BlockNBTComponent.WorldPos.Coordinate that = (BlockNBTComponent.WorldPos.Coordinate)other;
        return (value() == that.value() && 
          type() == that.type());
      }
      
      public int hashCode() {
        int result = this.value;
        result = 31 * result + this.type.hashCode();
        return result;
      }
      
      public String toString() {
        return ((this.type == BlockNBTComponent.WorldPos.Coordinate.Type.RELATIVE) ? "~" : "") + this.value;
      }
    }
  }
  
  static final class Tokens {
    static final Pattern LOCAL_PATTERN = Pattern.compile("^\\^(-?\\d+(\\.\\d+)?) \\^(-?\\d+(\\.\\d+)?) \\^(-?\\d+(\\.\\d+)?)$");
    
    static final Pattern WORLD_PATTERN = Pattern.compile("^(~?)(-?\\d+) (~?)(-?\\d+) (~?)(-?\\d+)$");
    
    static final String LOCAL_SYMBOL = "^";
    
    static final String RELATIVE_SYMBOL = "~";
    
    static final String ABSOLUTE_SYMBOL = "";
    
    static BlockNBTComponent.WorldPos.Coordinate deserializeCoordinate(String prefix, String value) {
      int i = Integer.parseInt(value);
      if (prefix.equals(""))
        return BlockNBTComponent.WorldPos.Coordinate.absolute(i); 
      if (prefix.equals("~"))
        return BlockNBTComponent.WorldPos.Coordinate.relative(i); 
      throw new AssertionError();
    }
    
    static String serializeLocal(double value) {
      return "^" + value;
    }
    
    static String serializeCoordinate(BlockNBTComponent.WorldPos.Coordinate coordinate) {
      return ((coordinate.type() == BlockNBTComponent.WorldPos.Coordinate.Type.RELATIVE) ? "~" : "") + coordinate.value();
    }
  }
}
