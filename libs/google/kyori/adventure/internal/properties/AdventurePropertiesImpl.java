package me.syncwrld.booter.libs.google.kyori.adventure.internal.properties;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;
import me.syncwrld.booter.libs.jtann.VisibleForTesting;

final class AdventurePropertiesImpl {
  private static final String FILESYSTEM_DIRECTORY_NAME = "config";
  
  private static final String FILESYSTEM_FILE_NAME = "adventure.properties";
  
  private static final Properties PROPERTIES = new Properties();
  
  static {
    Path path = Optional.<String>ofNullable(System.getProperty(systemPropertyName("config"))).map(x$0 -> Paths.get(x$0, new String[0])).orElseGet(() -> Paths.get("config", new String[] { "adventure.properties" }));
    if (Files.isRegularFile(path, new java.nio.file.LinkOption[0]))
      try {
        InputStream is = Files.newInputStream(path, new java.nio.file.OpenOption[0]);
        try {
          PROPERTIES.load(is);
          if (is != null)
            is.close(); 
        } catch (Throwable throwable) {
          if (is != null)
            try {
              is.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } catch (IOException e) {
        print(e);
      }  
  }
  
  private static void print(Throwable ex) {
    ex.printStackTrace();
  }
  
  @VisibleForTesting
  @NotNull
  static String systemPropertyName(String name) {
    return String.join(".", new CharSequence[] { "net", "kyori", "adventure", name });
  }
  
  static <T> AdventureProperties.Property<T> property(@NotNull String name, @NotNull Function<String, T> parser, @Nullable T defaultValue) {
    return new PropertyImpl<>(name, parser, defaultValue);
  }
  
  private static final class PropertyImpl<T> implements AdventureProperties.Property<T> {
    private final String name;
    
    private final Function<String, T> parser;
    
    @Nullable
    private final T defaultValue;
    
    private boolean valueCalculated;
    
    @Nullable
    private T value;
    
    PropertyImpl(@NotNull String name, @NotNull Function<String, T> parser, @Nullable T defaultValue) {
      this.name = name;
      this.parser = parser;
      this.defaultValue = defaultValue;
    }
    
    @Nullable
    public T value() {
      if (!this.valueCalculated) {
        String property = AdventurePropertiesImpl.systemPropertyName(this.name);
        String value = System.getProperty(property, AdventurePropertiesImpl.PROPERTIES.getProperty(this.name));
        if (value != null)
          this.value = this.parser.apply(value); 
        if (this.value == null)
          this.value = this.defaultValue; 
        this.valueCalculated = true;
      } 
      return this.value;
    }
    
    public boolean equals(@Nullable Object that) {
      return (this == that);
    }
    
    public int hashCode() {
      return this.name.hashCode();
    }
  }
}
