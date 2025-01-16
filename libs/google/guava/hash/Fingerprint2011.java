package me.syncwrld.booter.libs.google.guava.hash;

import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
final class Fingerprint2011 extends AbstractNonStreamingHashFunction {
  static final HashFunction FINGERPRINT_2011 = new Fingerprint2011();
  
  private static final long K0 = -6505348102511208375L;
  
  private static final long K1 = -8261664234251669945L;
  
  private static final long K2 = -4288712594273399085L;
  
  private static final long K3 = -4132994306676758123L;
  
  public HashCode hashBytes(byte[] input, int off, int len) {
    Preconditions.checkPositionIndexes(off, off + len, input.length);
    return HashCode.fromLong(fingerprint(input, off, len));
  }
  
  public int bits() {
    return 64;
  }
  
  public String toString() {
    return "Hashing.fingerprint2011()";
  }
  
  @VisibleForTesting
  static long fingerprint(byte[] bytes, int offset, int length) {
    if (length <= 32) {
      result = murmurHash64WithSeed(bytes, offset, length, -1397348546323613475L);
    } else if (length <= 64) {
      result = hashLength33To64(bytes, offset, length);
    } else {
      result = fullFingerprint(bytes, offset, length);
    } 
    long u = (length >= 8) ? LittleEndianByteArray.load64(bytes, offset) : -6505348102511208375L;
    long v = (length >= 9) ? LittleEndianByteArray.load64(bytes, offset + length - 8) : -6505348102511208375L;
    long result = hash128to64(result + v, u);
    return (result == 0L || result == 1L) ? (result + -2L) : result;
  }
  
  private static long shiftMix(long val) {
    return val ^ val >>> 47L;
  }
  
  @VisibleForTesting
  static long hash128to64(long high, long low) {
    long a = (low ^ high) * -4132994306676758123L;
    a ^= a >>> 47L;
    long b = (high ^ a) * -4132994306676758123L;
    b ^= b >>> 47L;
    b *= -4132994306676758123L;
    return b;
  }
  
  private static void weakHashLength32WithSeeds(byte[] bytes, int offset, long seedA, long seedB, long[] output) {
    long part1 = LittleEndianByteArray.load64(bytes, offset);
    long part2 = LittleEndianByteArray.load64(bytes, offset + 8);
    long part3 = LittleEndianByteArray.load64(bytes, offset + 16);
    long part4 = LittleEndianByteArray.load64(bytes, offset + 24);
    seedA += part1;
    seedB = Long.rotateRight(seedB + seedA + part4, 51);
    long c = seedA;
    seedA += part2;
    seedA += part3;
    seedB += Long.rotateRight(seedA, 23);
    output[0] = seedA + part4;
    output[1] = seedB + c;
  }
  
  private static long fullFingerprint(byte[] bytes, int offset, int length) {
    long x = LittleEndianByteArray.load64(bytes, offset);
    long y = LittleEndianByteArray.load64(bytes, offset + length - 16) ^ 0x8D58AC26AFE12E47L;
    long z = LittleEndianByteArray.load64(bytes, offset + length - 56) ^ 0xA5B85C5E198ED849L;
    long[] v = new long[2];
    long[] w = new long[2];
    weakHashLength32WithSeeds(bytes, offset + length - 64, length, y, v);
    weakHashLength32WithSeeds(bytes, offset + length - 32, length * -8261664234251669945L, -6505348102511208375L, w);
    z += shiftMix(v[1]) * -8261664234251669945L;
    x = Long.rotateRight(z + x, 39) * -8261664234251669945L;
    y = Long.rotateRight(y, 33) * -8261664234251669945L;
    length = length - 1 & 0xFFFFFFC0;
    while (true) {
      x = Long.rotateRight(x + y + v[0] + LittleEndianByteArray.load64(bytes, offset + 16), 37) * -8261664234251669945L;
      y = Long.rotateRight(y + v[1] + LittleEndianByteArray.load64(bytes, offset + 48), 42) * -8261664234251669945L;
      x ^= w[1];
      y ^= v[0];
      z = Long.rotateRight(z ^ w[0], 33);
      weakHashLength32WithSeeds(bytes, offset, v[1] * -8261664234251669945L, x + w[0], v);
      weakHashLength32WithSeeds(bytes, offset + 32, z + w[1], y, w);
      long tmp = z;
      z = x;
      x = tmp;
      offset += 64;
      length -= 64;
      if (length == 0)
        return hash128to64(hash128to64(v[0], w[0]) + shiftMix(y) * -8261664234251669945L + z, hash128to64(v[1], w[1]) + x); 
    } 
  }
  
  private static long hashLength33To64(byte[] bytes, int offset, int length) {
    long z = LittleEndianByteArray.load64(bytes, offset + 24);
    long a = LittleEndianByteArray.load64(bytes, offset) + (length + LittleEndianByteArray.load64(bytes, offset + length - 16)) * -6505348102511208375L;
    long b = Long.rotateRight(a + z, 52);
    long c = Long.rotateRight(a, 37);
    a += LittleEndianByteArray.load64(bytes, offset + 8);
    c += Long.rotateRight(a, 7);
    a += LittleEndianByteArray.load64(bytes, offset + 16);
    long vf = a + z;
    long vs = b + Long.rotateRight(a, 31) + c;
    a = LittleEndianByteArray.load64(bytes, offset + 16) + LittleEndianByteArray.load64(bytes, offset + length - 32);
    z = LittleEndianByteArray.load64(bytes, offset + length - 8);
    b = Long.rotateRight(a + z, 52);
    c = Long.rotateRight(a, 37);
    a += LittleEndianByteArray.load64(bytes, offset + length - 24);
    c += Long.rotateRight(a, 7);
    a += LittleEndianByteArray.load64(bytes, offset + length - 16);
    long wf = a + z;
    long ws = b + Long.rotateRight(a, 31) + c;
    long r = shiftMix((vf + ws) * -4288712594273399085L + (wf + vs) * -6505348102511208375L);
    return shiftMix(r * -6505348102511208375L + vs) * -4288712594273399085L;
  }
  
  @VisibleForTesting
  static long murmurHash64WithSeed(byte[] bytes, int offset, int length, long seed) {
    long mul = -4132994306676758123L;
    int topBit = 7;
    int lengthAligned = length & (topBit ^ 0xFFFFFFFF);
    int lengthRemainder = length & topBit;
    long hash = seed ^ length * mul;
    for (int i = 0; i < lengthAligned; i += 8) {
      long loaded = LittleEndianByteArray.load64(bytes, offset + i);
      long data = shiftMix(loaded * mul) * mul;
      hash ^= data;
      hash *= mul;
    } 
    if (lengthRemainder != 0) {
      long data = LittleEndianByteArray.load64Safely(bytes, offset + lengthAligned, lengthRemainder);
      hash ^= data;
      hash *= mul;
    } 
    hash = shiftMix(hash) * mul;
    hash = shiftMix(hash);
    return hash;
  }
}
