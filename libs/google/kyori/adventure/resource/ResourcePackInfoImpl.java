package me.syncwrld.booter.libs.google.kyori.adventure.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import me.syncwrld.booter.libs.google.kyori.adventure.internal.Internals;
import me.syncwrld.booter.libs.google.kyori.examination.ExaminableProperty;
import me.syncwrld.booter.libs.jtann.NotNull;
import me.syncwrld.booter.libs.jtann.Nullable;

final class ResourcePackInfoImpl implements ResourcePackInfo {
  private final UUID id;
  
  private final URI uri;
  
  private final String hash;
  
  ResourcePackInfoImpl(@NotNull UUID id, @NotNull URI uri, @NotNull String hash) {
    this.id = Objects.<UUID>requireNonNull(id, "id");
    this.uri = Objects.<URI>requireNonNull(uri, "uri");
    this.hash = Objects.<String>requireNonNull(hash, "hash");
  }
  
  @NotNull
  public UUID id() {
    return this.id;
  }
  
  @NotNull
  public URI uri() {
    return this.uri;
  }
  
  @NotNull
  public String hash() {
    return this.hash;
  }
  
  @NotNull
  public Stream<? extends ExaminableProperty> examinableProperties() {
    return Stream.of(new ExaminableProperty[] { ExaminableProperty.of("id", this.id), 
          ExaminableProperty.of("uri", this.uri), 
          ExaminableProperty.of("hash", this.hash) });
  }
  
  public String toString() {
    return Internals.toString(this);
  }
  
  public boolean equals(@Nullable Object other) {
    if (this == other)
      return true; 
    if (!(other instanceof ResourcePackInfoImpl))
      return false; 
    ResourcePackInfoImpl that = (ResourcePackInfoImpl)other;
    return (this.id.equals(that.id) && this.uri
      .equals(that.uri) && this.hash
      .equals(that.hash));
  }
  
  public int hashCode() {
    int result = this.id.hashCode();
    result = 31 * result + this.uri.hashCode();
    result = 31 * result + this.hash.hashCode();
    return result;
  }
  
  static final class BuilderImpl implements ResourcePackInfo.Builder {
    private UUID id;
    
    private URI uri;
    
    private String hash;
    
    @NotNull
    public ResourcePackInfo.Builder id(@NotNull UUID id) {
      this.id = Objects.<UUID>requireNonNull(id, "id");
      return this;
    }
    
    @NotNull
    public ResourcePackInfo.Builder uri(@NotNull URI uri) {
      this.uri = Objects.<URI>requireNonNull(uri, "uri");
      if (this.id == null)
        this.id = UUID.nameUUIDFromBytes(uri.toString().getBytes(StandardCharsets.UTF_8)); 
      return this;
    }
    
    @NotNull
    public ResourcePackInfo.Builder hash(@NotNull String hash) {
      this.hash = Objects.<String>requireNonNull(hash, "hash");
      return this;
    }
    
    @NotNull
    public ResourcePackInfo build() {
      return new ResourcePackInfoImpl(this.id, this.uri, this.hash);
    }
    
    @NotNull
    public CompletableFuture<ResourcePackInfo> computeHashAndBuild(@NotNull Executor executor) {
      return ResourcePackInfoImpl.computeHash(Objects.<URI>requireNonNull(this.uri, "uri"), executor)
        .thenApply(hash -> {
            hash(hash);
            return build();
          });
    }
  }
  
  static CompletableFuture<String> computeHash(URI uri, Executor exec) {
    CompletableFuture<String> result = new CompletableFuture<>();
    exec.execute(() -> {
          try {
            URL url = uri.toURL();
            URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", "adventure/" + ResourcePackInfoImpl.class.getPackage().getSpecificationVersion() + " (pack-fetcher)");
            InputStream is = conn.getInputStream();
            try {
              MessageDigest digest = MessageDigest.getInstance("SHA-1");
              byte[] buf = new byte[8192];
              int read;
              while ((read = is.read(buf)) != -1)
                digest.update(buf, 0, read); 
              result.complete(bytesToString(digest.digest()));
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
          } catch (IOException|java.security.NoSuchAlgorithmException ex) {
            result.completeExceptionally(ex);
          } 
        });
    return result;
  }
  
  static String bytesToString(byte[] arr) {
    StringBuilder builder = new StringBuilder(arr.length * 2);
    Formatter fmt = new Formatter(builder, Locale.ROOT);
    for (int i = 0; i < arr.length; i++) {
      fmt.format("%02x", new Object[] { Integer.valueOf(arr[i] & 0xFF) });
    } 
    return builder.toString();
  }
}
