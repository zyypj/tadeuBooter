package me.syncwrld.booter.libs.google.yamlbeans;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.EmitterConfig;
import me.syncwrld.booter.libs.google.yamlbeans.scalar.DateSerializer;
import me.syncwrld.booter.libs.google.yamlbeans.scalar.ScalarSerializer;

public class YamlConfig {
  public final WriteConfig writeConfig = new WriteConfig();
  
  public ReadConfig readConfig = new ReadConfig();
  
  final Map<String, String> classNameToTag = new HashMap<String, String>();
  
  final Map<String, Class> tagToClass = (Map)new HashMap<String, Class<?>>();
  
  final Map<Class, ScalarSerializer> scalarSerializers = (Map)new IdentityHashMap<Class<?>, ScalarSerializer>();
  
  final Map<Beans.Property, Class> propertyToElementType = (Map)new HashMap<Beans.Property, Class<?>>();
  
  final Map<Beans.Property, Class> propertyToDefaultType = (Map)new HashMap<Beans.Property, Class<?>>();
  
  boolean beanProperties = true;
  
  boolean privateFields;
  
  boolean privateConstructors = true;
  
  boolean allowDuplicates = true;
  
  String tagSuffix;
  
  public YamlConfig() {
    this.scalarSerializers.put(Date.class, new DateSerializer());
    this.tagToClass.put("tag:yaml.org,2002:str", String.class);
    this.tagToClass.put("tag:yaml.org,2002:int", Integer.class);
    this.tagToClass.put("tag:yaml.org,2002:seq", ArrayList.class);
    this.tagToClass.put("tag:yaml.org,2002:map", HashMap.class);
    this.tagToClass.put("tag:yaml.org,2002:float", Float.class);
  }
  
  public void setAllowDuplicates(boolean allowDuplicates) {
    this.allowDuplicates = allowDuplicates;
  }
  
  public void setClassTag(String tag, Class type) {
    if (tag == null)
      throw new IllegalArgumentException("tag cannot be null."); 
    if (type == null)
      throw new IllegalArgumentException("type cannot be null."); 
    if (!tag.startsWith("!"))
      tag = "!" + tag; 
    this.classNameToTag.put(type.getName(), tag);
    this.tagToClass.put(tag, type);
  }
  
  public void setScalarSerializer(Class type, ScalarSerializer serializer) {
    if (type == null)
      throw new IllegalArgumentException("type cannot be null."); 
    if (serializer == null)
      throw new IllegalArgumentException("serializer cannot be null."); 
    this.scalarSerializers.put(type, serializer);
  }
  
  public void setPropertyElementType(Class type, String propertyName, Class elementType) {
    if (type == null)
      throw new IllegalArgumentException("type cannot be null."); 
    if (propertyName == null)
      throw new IllegalArgumentException("propertyName cannot be null."); 
    if (elementType == null)
      throw new IllegalArgumentException("propertyType cannot be null."); 
    Beans.Property property = Beans.getProperty(type, propertyName, this.beanProperties, this.privateFields, this);
    if (property == null)
      throw new IllegalArgumentException("The class " + type.getName() + " does not have a property named: " + propertyName); 
    if (!Collection.class.isAssignableFrom(property.getType()) && !Map.class.isAssignableFrom(property.getType()))
      throw new IllegalArgumentException("The '" + propertyName + "' property on the " + type.getName() + " class must be a Collection or Map: " + property
          .getType()); 
    this.propertyToElementType.put(property, elementType);
  }
  
  public void setPropertyDefaultType(Class type, String propertyName, Class defaultType) {
    if (type == null)
      throw new IllegalArgumentException("type cannot be null."); 
    if (propertyName == null)
      throw new IllegalArgumentException("propertyName cannot be null."); 
    if (defaultType == null)
      throw new IllegalArgumentException("defaultType cannot be null."); 
    Beans.Property property = Beans.getProperty(type, propertyName, this.beanProperties, this.privateFields, this);
    if (property == null)
      throw new IllegalArgumentException("The class " + type.getName() + " does not have a property named: " + propertyName); 
    this.propertyToDefaultType.put(property, defaultType);
  }
  
  public void setBeanProperties(boolean beanProperties) {
    this.beanProperties = beanProperties;
  }
  
  public void setPrivateFields(boolean privateFields) {
    this.privateFields = privateFields;
  }
  
  public void setPrivateConstructors(boolean privateConstructors) {
    this.privateConstructors = privateConstructors;
  }
  
  public void setTagSuffix(String tagSuffix) {
    this.tagSuffix = tagSuffix;
  }
  
  public static class WriteConfig {
    boolean explicitFirstDocument = false;
    
    boolean explicitEndDocument = false;
    
    boolean writeDefaultValues = false;
    
    boolean writeRootTags = true;
    
    boolean writeRootElementTags = true;
    
    boolean autoAnchor = true;
    
    boolean keepBeanPropertyOrder = false;
    
    YamlConfig.WriteClassName writeClassName = YamlConfig.WriteClassName.AUTO;
    
    YamlConfig.Quote quote = YamlConfig.Quote.NONE;
    
    Version version;
    
    Map<String, String> tags;
    
    boolean flowStyle;
    
    EmitterConfig emitterConfig = new EmitterConfig();
    
    WriteConfig() {
      this.emitterConfig.setUseVerbatimTags(false);
    }
    
    public void setExplicitFirstDocument(boolean explicitFirstDocument) {
      this.explicitFirstDocument = explicitFirstDocument;
    }
    
    public void setExplicitEndDocument(boolean explicitEndDocument) {
      this.explicitEndDocument = explicitEndDocument;
    }
    
    public void setWriteRootTags(boolean writeRootTags) {
      this.writeRootTags = writeRootTags;
    }
    
    public void setWriteRootElementTags(boolean writeRootElementTags) {
      this.writeRootElementTags = writeRootElementTags;
    }
    
    public void setWriteDefaultValues(boolean writeDefaultValues) {
      this.writeDefaultValues = writeDefaultValues;
    }
    
    public void setAutoAnchor(boolean autoAnchor) {
      this.autoAnchor = autoAnchor;
    }
    
    public void setKeepBeanPropertyOrder(boolean keepBeanPropertyOrder) {
      this.keepBeanPropertyOrder = keepBeanPropertyOrder;
    }
    
    public void setVersion(Version version) {
      this.version = version;
    }
    
    public void setTags(Map<String, String> tags) {
      this.tags = tags;
    }
    
    public void setCanonical(boolean canonical) {
      this.emitterConfig.setCanonical(canonical);
    }
    
    public void setIndentSize(int indentSize) {
      this.emitterConfig.setIndentSize(indentSize);
    }
    
    public void setWrapColumn(int wrapColumn) {
      this.emitterConfig.setWrapColumn(wrapColumn);
    }
    
    public void setUseVerbatimTags(boolean useVerbatimTags) {
      this.emitterConfig.setUseVerbatimTags(useVerbatimTags);
    }
    
    public void setEscapeUnicode(boolean escapeUnicode) {
      this.emitterConfig.setEscapeUnicode(escapeUnicode);
    }
    
    public void setWriteClassname(YamlConfig.WriteClassName write) {
      this.writeClassName = write;
    }
    
    public void setQuoteChar(YamlConfig.Quote quote) {
      this.quote = quote;
    }
    
    public YamlConfig.Quote getQuote() {
      return this.quote;
    }
    
    public void setFlowStyle(boolean flowStyle) {
      this.flowStyle = flowStyle;
    }
    
    public boolean isFlowStyle() {
      return this.flowStyle;
    }
    
    public void setPrettyFlow(boolean prettyFlow) {
      this.emitterConfig.setPrettyFlow(prettyFlow);
    }
  }
  
  public static class ReadConfig {
    Version defaultVersion = Version.DEFAULT_VERSION;
    
    ClassLoader classLoader;
    
    final Map<Class, YamlConfig.ConstructorParameters> constructorParameters = (Map)new IdentityHashMap<Class<?>, YamlConfig.ConstructorParameters>();
    
    boolean ignoreUnknownProperties;
    
    boolean autoMerge = true;
    
    boolean classTags = true;
    
    boolean guessNumberTypes;
    
    boolean anchors = true;
    
    public void setDefaultVersion(Version defaultVersion) {
      if (defaultVersion == null)
        throw new IllegalArgumentException("defaultVersion cannot be null."); 
      this.defaultVersion = defaultVersion;
    }
    
    public void setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
    }
    
    public void setConstructorParameters(Class type, Class[] parameterTypes, String[] parameterNames) {
      if (type == null)
        throw new IllegalArgumentException("type cannot be null."); 
      if (parameterTypes == null)
        throw new IllegalArgumentException("parameterTypes cannot be null."); 
      if (parameterNames == null)
        throw new IllegalArgumentException("parameterNames cannot be null."); 
      YamlConfig.ConstructorParameters parameters = new YamlConfig.ConstructorParameters();
      try {
        parameters.constructor = type.getConstructor(parameterTypes);
      } catch (Exception ex) {
        throw new IllegalArgumentException("Unable to find constructor: " + type
            .getName() + "(" + Arrays.toString(parameterTypes) + ")", ex);
      } 
      parameters.parameterNames = parameterNames;
      this.constructorParameters.put(type, parameters);
    }
    
    public void setIgnoreUnknownProperties(boolean allowUnknownProperties) {
      this.ignoreUnknownProperties = allowUnknownProperties;
    }
    
    public void setClassTags(boolean classTags) {
      this.classTags = classTags;
    }
    
    public void setAutoMerge(boolean autoMerge) {
      this.autoMerge = autoMerge;
    }
    
    public void setGuessNumberTypes(boolean guessNumberTypes) {
      this.guessNumberTypes = guessNumberTypes;
    }
    
    public void setAnchors(boolean anchors) {
      this.anchors = anchors;
    }
  }
  
  static class ConstructorParameters {
    public Constructor constructor;
    
    public String[] parameterNames;
  }
  
  public enum WriteClassName {
    ALWAYS, NEVER, AUTO;
  }
  
  public enum Quote {
    NONE(false),
    SINGLE('\''),
    DOUBLE('"'),
    LITERAL('|'),
    FOLDED('>');
    
    char c;
    
    Quote(char c) {
      this.c = c;
    }
    
    public char getStyle() {
      return this.c;
    }
  }
}
