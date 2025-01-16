package me.syncwrld.booter.libs.google.guava.base;

import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Preconditions {
  public static void checkArgument(boolean expression) {
    if (!expression)
      throw new IllegalArgumentException(); 
  }
  
  public static void checkArgument(boolean expression, @CheckForNull Object errorMessage) {
    if (!expression)
      throw new IllegalArgumentException(String.valueOf(errorMessage)); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object... errorMessageArgs) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, errorMessageArgs)); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, char p1) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, int p1) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, long p1) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1 })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, char p1, char p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Character.valueOf(p2) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, char p1, int p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Integer.valueOf(p2) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, char p1, long p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Long.valueOf(p2) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, char p1, @CheckForNull Object p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), p2 })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, int p1, char p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Character.valueOf(p2) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, int p1, int p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Integer.valueOf(p2) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, int p1, long p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Long.valueOf(p2) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, int p1, @CheckForNull Object p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), p2 })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, long p1, char p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Character.valueOf(p2) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, long p1, int p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Integer.valueOf(p2) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, long p1, long p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Long.valueOf(p2) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, long p1, @CheckForNull Object p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), p2 })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, char p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Character.valueOf(p2) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, int p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Integer.valueOf(p2) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, long p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Long.valueOf(p2) })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2 })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2, @CheckForNull Object p3) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2, p3 })); 
  }
  
  public static void checkArgument(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2, @CheckForNull Object p3, @CheckForNull Object p4) {
    if (!expression)
      throw new IllegalArgumentException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2, p3, p4 })); 
  }
  
  public static void checkState(boolean expression) {
    if (!expression)
      throw new IllegalStateException(); 
  }
  
  public static void checkState(boolean expression, @CheckForNull Object errorMessage) {
    if (!expression)
      throw new IllegalStateException(String.valueOf(errorMessage)); 
  }
  
  public static void checkState(boolean expression, @CheckForNull String errorMessageTemplate, @CheckForNull Object... errorMessageArgs) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, errorMessageArgs)); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, char p1) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, int p1) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, long p1) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, @CheckForNull Object p1) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1 })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, char p1, char p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Character.valueOf(p2) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, char p1, int p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Integer.valueOf(p2) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, char p1, long p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Long.valueOf(p2) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, char p1, @CheckForNull Object p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), p2 })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, int p1, char p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Character.valueOf(p2) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, int p1, int p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Integer.valueOf(p2) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, int p1, long p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Long.valueOf(p2) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, int p1, @CheckForNull Object p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), p2 })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, long p1, char p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Character.valueOf(p2) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, long p1, int p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Integer.valueOf(p2) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, long p1, long p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Long.valueOf(p2) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, long p1, @CheckForNull Object p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), p2 })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, char p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Character.valueOf(p2) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, int p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Integer.valueOf(p2) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, long p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Long.valueOf(p2) })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2 })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2, @CheckForNull Object p3) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2, p3 })); 
  }
  
  public static void checkState(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2, @CheckForNull Object p3, @CheckForNull Object p4) {
    if (!expression)
      throw new IllegalStateException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2, p3, p4 })); 
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference) {
    if (reference == null)
      throw new NullPointerException(); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, @CheckForNull Object errorMessage) {
    if (reference == null)
      throw new NullPointerException(String.valueOf(errorMessage)); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, @CheckForNull Object... errorMessageArgs) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, errorMessageArgs)); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, char p1) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, int p1) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, long p1) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, @CheckForNull Object p1) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1 })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, char p1, char p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Character.valueOf(p2) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, char p1, int p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Integer.valueOf(p2) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, char p1, long p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Long.valueOf(p2) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, char p1, @CheckForNull Object p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), p2 })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, int p1, char p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Character.valueOf(p2) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, int p1, int p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Integer.valueOf(p2) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, int p1, long p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Long.valueOf(p2) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, int p1, @CheckForNull Object p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), p2 })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, long p1, char p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Character.valueOf(p2) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, long p1, int p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Integer.valueOf(p2) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, long p1, long p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Long.valueOf(p2) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, long p1, @CheckForNull Object p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), p2 })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, @CheckForNull Object p1, char p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Character.valueOf(p2) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, @CheckForNull Object p1, int p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Integer.valueOf(p2) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, @CheckForNull Object p1, long p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Long.valueOf(p2) })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2 })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2, @CheckForNull Object p3) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2, p3 })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static <T> T checkNotNull(@CheckForNull T reference, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2, @CheckForNull Object p3, @CheckForNull Object p4) {
    if (reference == null)
      throw new NullPointerException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2, p3, p4 })); 
    return reference;
  }
  
  @CanIgnoreReturnValue
  public static int checkElementIndex(int index, int size) {
    return checkElementIndex(index, size, "index");
  }
  
  @CanIgnoreReturnValue
  public static int checkElementIndex(int index, int size, String desc) {
    if (index < 0 || index >= size)
      throw new IndexOutOfBoundsException(badElementIndex(index, size, desc)); 
    return index;
  }
  
  private static String badElementIndex(int index, int size, String desc) {
    if (index < 0)
      return Strings.lenientFormat("%s (%s) must not be negative", new Object[] { desc, Integer.valueOf(index) }); 
    if (size < 0)
      throw new IllegalArgumentException("negative size: " + size); 
    return Strings.lenientFormat("%s (%s) must be less than size (%s)", new Object[] { desc, Integer.valueOf(index), Integer.valueOf(size) });
  }
  
  @CanIgnoreReturnValue
  public static int checkPositionIndex(int index, int size) {
    return checkPositionIndex(index, size, "index");
  }
  
  @CanIgnoreReturnValue
  public static int checkPositionIndex(int index, int size, String desc) {
    if (index < 0 || index > size)
      throw new IndexOutOfBoundsException(badPositionIndex(index, size, desc)); 
    return index;
  }
  
  private static String badPositionIndex(int index, int size, String desc) {
    if (index < 0)
      return Strings.lenientFormat("%s (%s) must not be negative", new Object[] { desc, Integer.valueOf(index) }); 
    if (size < 0)
      throw new IllegalArgumentException("negative size: " + size); 
    return Strings.lenientFormat("%s (%s) must not be greater than size (%s)", new Object[] { desc, Integer.valueOf(index), Integer.valueOf(size) });
  }
  
  public static void checkPositionIndexes(int start, int end, int size) {
    if (start < 0 || end < start || end > size)
      throw new IndexOutOfBoundsException(badPositionIndexes(start, end, size)); 
  }
  
  private static String badPositionIndexes(int start, int end, int size) {
    if (start < 0 || start > size)
      return badPositionIndex(start, size, "start index"); 
    if (end < 0 || end > size)
      return badPositionIndex(end, size, "end index"); 
    return Strings.lenientFormat("end index (%s) must not be less than start index (%s)", new Object[] { Integer.valueOf(end), Integer.valueOf(start) });
  }
}
