package me.syncwrld.booter.libs.google.guava.io;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.StandardSystemProperty;
import me.syncwrld.booter.libs.google.guava.base.Throwables;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableList;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
abstract class TempFileCreator {
  static final TempFileCreator INSTANCE = pickSecureCreator();
  
  private static TempFileCreator pickSecureCreator() {
    try {
      Class.forName("java.nio.file.Path");
      return new JavaNioCreator();
    } catch (ClassNotFoundException classNotFoundException) {
      try {
        int version = ((Integer)Class.forName("android.os.Build$VERSION").getField("SDK_INT").get(null)).intValue();
        int jellyBean = ((Integer)Class.forName("android.os.Build$VERSION_CODES").getField("JELLY_BEAN").get(null)).intValue();
        if (version < jellyBean)
          return new ThrowingCreator(); 
      } catch (NoSuchFieldException e) {
        return new ThrowingCreator();
      } catch (ClassNotFoundException e) {
        return new ThrowingCreator();
      } catch (IllegalAccessException e) {
        return new ThrowingCreator();
      } 
      return new JavaIoCreator();
    } 
  }
  
  @IgnoreJRERequirement
  @VisibleForTesting
  static void testMakingUserPermissionsFromScratch() throws IOException {
    FileAttribute<?> unused = JavaNioCreator.userPermissions().get();
  }
  
  @IgnoreJRERequirement
  private static interface PermissionSupplier {
    FileAttribute<?> get() throws IOException;
  }
  
  @IgnoreJRERequirement
  private static final class JavaNioCreator extends TempFileCreator {
    private static final PermissionSupplier filePermissions;
    
    private static final PermissionSupplier directoryPermissions;
    
    private JavaNioCreator() {}
    
    File createTempDir() {
      try {
        return Files.createTempDirectory(
            Paths.get(StandardSystemProperty.JAVA_IO_TMPDIR.value(), new String[0]), null, (FileAttribute<?>[])new FileAttribute[] { directoryPermissions.get() }).toFile();
      } catch (IOException e) {
        throw new IllegalStateException("Failed to create directory", e);
      } 
    }
    
    File createTempFile(String prefix) throws IOException {
      return Files.createTempFile(
          Paths.get(StandardSystemProperty.JAVA_IO_TMPDIR.value(), new String[0]), prefix, null, (FileAttribute<?>[])new FileAttribute[] { filePermissions
            
            .get() }).toFile();
    }
    
    static {
      Set<String> views = FileSystems.getDefault().supportedFileAttributeViews();
      if (views.contains("posix")) {
        filePermissions = (() -> PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------")));
        directoryPermissions = (() -> PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------")));
      } else if (views.contains("acl")) {
        filePermissions = directoryPermissions = userPermissions();
      } else {
        filePermissions = directoryPermissions = (() -> {
            throw new IOException("unrecognized FileSystem type " + FileSystems.getDefault());
          });
      } 
    }
    
    private static PermissionSupplier userPermissions() {
      try {
        UserPrincipal user = FileSystems.getDefault().getUserPrincipalLookupService().lookupPrincipalByName(getUsername());
        final ImmutableList<AclEntry> acl = ImmutableList.of(
            AclEntry.newBuilder()
            .setType(AclEntryType.ALLOW)
            .setPrincipal(user)
            .setPermissions(EnumSet.allOf(AclEntryPermission.class))
            .setFlags(new AclEntryFlag[] { AclEntryFlag.DIRECTORY_INHERIT, AclEntryFlag.FILE_INHERIT }).build());
        FileAttribute<ImmutableList<AclEntry>> attribute = new FileAttribute<ImmutableList<AclEntry>>() {
            public String name() {
              return "acl:acl";
            }
            
            public ImmutableList<AclEntry> value() {
              return acl;
            }
          };
        return () -> attribute;
      } catch (IOException e) {
        return () -> {
            throw new IOException("Could not find user", e);
          };
      } 
    }
    
    @IgnoreJRERequirement
    private static interface PermissionSupplier {
      FileAttribute<?> get() throws IOException;
    }
    
    private static String getUsername() {
      String fromSystemProperty = Objects.<String>requireNonNull(StandardSystemProperty.USER_NAME.value());
      try {
        Class<?> processHandleClass = Class.forName("java.lang.ProcessHandle");
        Class<?> processHandleInfoClass = Class.forName("java.lang.ProcessHandle$Info");
        Class<?> optionalClass = Class.forName("java.util.Optional");
        Method currentMethod = processHandleClass.getMethod("current", new Class[0]);
        Method infoMethod = processHandleClass.getMethod("info", new Class[0]);
        Method userMethod = processHandleInfoClass.getMethod("user", new Class[0]);
        Method orElseMethod = optionalClass.getMethod("orElse", new Class[] { Object.class });
        Object current = currentMethod.invoke(null, new Object[0]);
        Object info = infoMethod.invoke(current, new Object[0]);
        Object user = userMethod.invoke(info, new Object[0]);
        return (String)Objects.<Object>requireNonNull(orElseMethod.invoke(user, new Object[] { fromSystemProperty }));
      } catch (ClassNotFoundException runningUnderAndroidOrJava8) {
        return fromSystemProperty;
      } catch (InvocationTargetException e) {
        Throwables.throwIfUnchecked(e.getCause());
        return fromSystemProperty;
      } catch (NoSuchMethodException shouldBeImpossible) {
        return fromSystemProperty;
      } catch (IllegalAccessException shouldBeImpossible) {
        return fromSystemProperty;
      } 
    }
  }
  
  private static final class JavaIoCreator extends TempFileCreator {
    private static final int TEMP_DIR_ATTEMPTS = 10000;
    
    private JavaIoCreator() {}
    
    File createTempDir() {
      File baseDir = new File(StandardSystemProperty.JAVA_IO_TMPDIR.value());
      String baseName = System.currentTimeMillis() + "-";
      for (int counter = 0; counter < 10000; counter++) {
        File tempDir = new File(baseDir, baseName + counter);
        if (tempDir.mkdir())
          return tempDir; 
      } 
      throw new IllegalStateException("Failed to create directory within 10000 attempts (tried " + baseName + "0 to " + baseName + '✏' + ')');
    }
    
    File createTempFile(String prefix) throws IOException {
      return File.createTempFile(prefix, null, null);
    }
  }
  
  private static final class ThrowingCreator extends TempFileCreator {
    private static final String MESSAGE = "Guava cannot securely create temporary files or directories under SDK versions before Jelly Bean. You can create one yourself, either in the insecure default directory or in a more secure directory, such as context.getCacheDir(). For more information, see the Javadoc for Files.createTempDir().";
    
    private ThrowingCreator() {}
    
    File createTempDir() {
      throw new IllegalStateException("Guava cannot securely create temporary files or directories under SDK versions before Jelly Bean. You can create one yourself, either in the insecure default directory or in a more secure directory, such as context.getCacheDir(). For more information, see the Javadoc for Files.createTempDir().");
    }
    
    File createTempFile(String prefix) throws IOException {
      throw new IOException("Guava cannot securely create temporary files or directories under SDK versions before Jelly Bean. You can create one yourself, either in the insecure default directory or in a more secure directory, such as context.getCacheDir(). For more information, see the Javadoc for Files.createTempDir().");
    }
  }
  
  private TempFileCreator() {}
  
  abstract File createTempDir();
  
  abstract File createTempFile(String paramString) throws IOException;
}
