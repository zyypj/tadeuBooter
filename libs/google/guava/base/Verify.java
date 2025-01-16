package me.syncwrld.booter.libs.google.guava.base;

import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Verify {
  public static void verify(boolean expression) {
    if (!expression)
      throw new VerifyException(); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, @CheckForNull Object... errorMessageArgs) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, errorMessageArgs)); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, char p1) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, int p1) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, long p1) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, @CheckForNull Object p1) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1 })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, char p1, char p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Character.valueOf(p2) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, int p1, char p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Character.valueOf(p2) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, long p1, char p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Character.valueOf(p2) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, char p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Character.valueOf(p2) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, char p1, int p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Integer.valueOf(p2) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, int p1, int p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Integer.valueOf(p2) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, long p1, int p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Integer.valueOf(p2) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, int p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Integer.valueOf(p2) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, char p1, long p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), Long.valueOf(p2) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, int p1, long p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), Long.valueOf(p2) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, long p1, long p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), Long.valueOf(p2) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, long p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, Long.valueOf(p2) })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, char p1, @CheckForNull Object p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Character.valueOf(p1), p2 })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, int p1, @CheckForNull Object p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Integer.valueOf(p1), p2 })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, long p1, @CheckForNull Object p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { Long.valueOf(p1), p2 })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2 })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2, @CheckForNull Object p3) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2, p3 })); 
  }
  
  public static void verify(boolean expression, String errorMessageTemplate, @CheckForNull Object p1, @CheckForNull Object p2, @CheckForNull Object p3, @CheckForNull Object p4) {
    if (!expression)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, new Object[] { p1, p2, p3, p4 })); 
  }
  
  @CanIgnoreReturnValue
  public static <T> T verifyNotNull(@CheckForNull T reference) {
    return verifyNotNull(reference, "expected a non-null reference", new Object[0]);
  }
  
  @CanIgnoreReturnValue
  public static <T> T verifyNotNull(@CheckForNull T reference, String errorMessageTemplate, @CheckForNull Object... errorMessageArgs) {
    if (reference == null)
      throw new VerifyException(Strings.lenientFormat(errorMessageTemplate, errorMessageArgs)); 
    return reference;
  }
}
