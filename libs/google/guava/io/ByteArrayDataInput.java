package me.syncwrld.booter.libs.google.guava.io;

import java.io.DataInput;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public interface ByteArrayDataInput extends DataInput {
  void readFully(byte[] paramArrayOfbyte);
  
  void readFully(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  int skipBytes(int paramInt);
  
  @CanIgnoreReturnValue
  boolean readBoolean();
  
  @CanIgnoreReturnValue
  byte readByte();
  
  @CanIgnoreReturnValue
  int readUnsignedByte();
  
  @CanIgnoreReturnValue
  short readShort();
  
  @CanIgnoreReturnValue
  int readUnsignedShort();
  
  @CanIgnoreReturnValue
  char readChar();
  
  @CanIgnoreReturnValue
  int readInt();
  
  @CanIgnoreReturnValue
  long readLong();
  
  @CanIgnoreReturnValue
  float readFloat();
  
  @CanIgnoreReturnValue
  double readDouble();
  
  @CheckForNull
  @CanIgnoreReturnValue
  String readLine();
  
  @CanIgnoreReturnValue
  String readUTF();
}
