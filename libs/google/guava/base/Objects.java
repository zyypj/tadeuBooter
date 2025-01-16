package me.syncwrld.booter.libs.google.guava.base;

import java.util.Arrays;
import me.syncwrld.booter.libs.google.guava.annotations.GwtCompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Objects extends ExtraObjectsMethodsForWeb {
  public static boolean equal(@CheckForNull Object a, @CheckForNull Object b) {
    return (a == b || (a != null && a.equals(b)));
  }
  
  public static int hashCode(@CheckForNull Object... objects) {
    return Arrays.hashCode(objects);
  }
}
