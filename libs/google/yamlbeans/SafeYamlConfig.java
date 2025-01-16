package me.syncwrld.booter.libs.google.yamlbeans;

public class SafeYamlConfig extends YamlConfig {
  public SafeYamlConfig() {
    this.readConfig = new SafeReadConfig();
  }
  
  public static class SafeReadConfig extends YamlConfig.ReadConfig {
    public SafeReadConfig() {
      this.anchors = false;
      this.classTags = false;
    }
    
    public void setClassTags(boolean classTags) {
      if (classTags)
        throw new IllegalArgumentException("Class Tags cannot be enabled in SafeYamlConfig."); 
    }
    
    public void setAnchors(boolean anchors) {
      if (anchors)
        throw new IllegalArgumentException("Anchors cannot be enabled in SafeYamlConfig."); 
    }
  }
}
