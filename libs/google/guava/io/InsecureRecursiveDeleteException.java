package me.syncwrld.booter.libs.google.guava.io;

import java.nio.file.FileSystemException;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class InsecureRecursiveDeleteException extends FileSystemException {
  public InsecureRecursiveDeleteException(@CheckForNull String file) {
    super(file, null, "unable to guarantee security of recursive delete");
  }
}
