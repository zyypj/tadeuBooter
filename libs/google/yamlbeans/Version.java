package me.syncwrld.booter.libs.google.yamlbeans;

public class Version {
  public static final Version V1_0 = new Version(1, 0);
  
  public static final Version V1_1 = new Version(1, 1);
  
  public static final Version DEFAULT_VERSION = V1_1;
  
  private final int major;
  
  private final int minor;
  
  private Version(int major, int minor) {
    this.major = major;
    this.minor = minor;
  }
  
  public static Version getVersion(String value) {
    Version version = null;
    if (value != null) {
      int dotIndex = value.indexOf('.');
      int major = 0;
      int minor = 0;
      if (dotIndex > 0)
        try {
          major = Integer.parseInt(value.substring(0, dotIndex));
          minor = Integer.parseInt(value.substring(dotIndex + 1));
        } catch (NumberFormatException e) {
          return null;
        }  
      if (major == V1_0.major && minor == V1_0.minor) {
        version = V1_0;
      } else if (major == V1_1.major && minor == V1_1.minor) {
        version = V1_1;
      } 
    } 
    return version;
  }
  
  public int getMajor() {
    return this.major;
  }
  
  public int getMinor() {
    return this.minor;
  }
  
  public String toString() {
    return this.major + "." + this.minor;
  }
}
