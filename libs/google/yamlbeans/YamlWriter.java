package me.syncwrld.booter.libs.google.yamlbeans;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.syncwrld.booter.libs.google.yamlbeans.document.YamlElement;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.Emitter;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.EmitterException;
import me.syncwrld.booter.libs.google.yamlbeans.parser.AliasEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.DocumentEndEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.DocumentStartEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.Event;
import me.syncwrld.booter.libs.google.yamlbeans.parser.MappingStartEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.ScalarEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.SequenceStartEvent;
import me.syncwrld.booter.libs.google.yamlbeans.scalar.ScalarSerializer;

public class YamlWriter implements AutoCloseable {
  private final YamlConfig config;
  
  private final Emitter emitter;
  
  private boolean started;
  
  private Map<Class, Object> defaultValuePrototypes = (Map)new IdentityHashMap<Class<?>, Object>();
  
  private final List queuedObjects = new ArrayList();
  
  private final Map<Object, Integer> referenceCount = new IdentityHashMap<Object, Integer>();
  
  private final Map<Object, String> anchoredObjects = new HashMap<Object, String>();
  
  private int nextAnchor = 1;
  
  private boolean isRoot;
  
  public YamlWriter(Writer writer) {
    this(writer, new YamlConfig());
  }
  
  public YamlWriter(Writer writer, YamlConfig config) {
    this.config = config;
    this.emitter = new Emitter(writer, config.writeConfig.emitterConfig);
  }
  
  public void setAlias(Object object, String alias) {
    this.anchoredObjects.put(object, alias);
  }
  
  public void write(Object object) throws YamlException {
    if (this.config.writeConfig.autoAnchor) {
      countObjectReferences(object);
      this.queuedObjects.add(object);
      return;
    } 
    writeInternal(object);
  }
  
  public YamlConfig getConfig() {
    return this.config;
  }
  
  private void writeInternal(Object object) throws YamlException {
    try {
      if (!this.started) {
        this.emitter.emit(Event.STREAM_START);
        this.started = true;
      } 
      this.emitter.emit((Event)new DocumentStartEvent(this.config.writeConfig.explicitFirstDocument, this.config.writeConfig.version, this.config.writeConfig.tags));
      this.isRoot = true;
      writeValue(object, this.config.writeConfig.writeRootTags ? null : object.getClass(), null, null);
      this.emitter.emit((Event)new DocumentEndEvent(this.config.writeConfig.explicitEndDocument));
    } catch (EmitterException ex) {
      throw new YamlException("Error writing YAML.", ex);
    } catch (IOException ex) {
      throw new YamlException("Error writing YAML.", ex);
    } 
  }
  
  public void clearAnchors() throws YamlException {
    for (Object object : this.queuedObjects)
      writeInternal(object); 
    this.queuedObjects.clear();
    this.referenceCount.clear();
    this.nextAnchor = 1;
  }
  
  public void close() throws YamlException {
    clearAnchors();
    this.defaultValuePrototypes.clear();
    try {
      this.emitter.emit(Event.STREAM_END);
      this.emitter.close();
    } catch (EmitterException ex) {
      throw new YamlException(ex);
    } catch (IOException ex) {
      throw new YamlException(ex);
    } 
  }
  
  private void writeValue(Object object, Class<?> fieldClass, Class<?> elementType, Class<?> defaultType) throws EmitterException, IOException, YamlException {
    boolean isRoot = this.isRoot;
    this.isRoot = false;
    if (object instanceof YamlElement) {
      ((YamlElement)object).emitEvent(this.emitter, this.config.writeConfig);
      return;
    } 
    if (object == null) {
      this.emitter.emit((Event)new ScalarEvent(null, null, new boolean[] { true, true }, null, this.config.writeConfig.quote.c));
      return;
    } 
    Class<?> valueClass = object.getClass();
    boolean unknownType = (fieldClass == null);
    if (unknownType)
      fieldClass = valueClass; 
    String anchor = null;
    if (!Beans.isScalar(valueClass) && !(object instanceof Enum)) {
      anchor = this.anchoredObjects.get(object);
      if (this.config.writeConfig.autoAnchor) {
        Integer count = this.referenceCount.get(object);
        if (count == null) {
          this.emitter.emit((Event)new AliasEvent(anchor));
          return;
        } 
        if (count.intValue() > 1) {
          this.referenceCount.remove(object);
          if (anchor == null) {
            anchor = String.valueOf(this.nextAnchor++);
            this.anchoredObjects.put(object, anchor);
          } 
        } 
      } 
    } 
    String tag = null;
    boolean showTag = false;
    if ((unknownType || valueClass != fieldClass || this.config.writeConfig.writeClassName == YamlConfig.WriteClassName.ALWAYS) && this.config.writeConfig.writeClassName != YamlConfig.WriteClassName.NEVER) {
      showTag = true;
      if ((unknownType || fieldClass == List.class) && valueClass == ArrayList.class)
        showTag = false; 
      if ((unknownType || fieldClass == Map.class) && valueClass == HashMap.class)
        showTag = false; 
      if (fieldClass == Set.class && valueClass == HashSet.class)
        showTag = false; 
      if (valueClass == defaultType)
        showTag = false; 
      if (showTag) {
        tag = this.config.classNameToTag.get(valueClass.getName());
        if (tag == null)
          tag = valueClass.getName(); 
      } 
    } 
    for (Map.Entry<Class<?>, ScalarSerializer> entry : this.config.scalarSerializers.entrySet()) {
      if (((Class)entry.getKey()).isAssignableFrom(valueClass)) {
        ScalarSerializer serializer = entry.getValue();
        this.emitter.emit((Event)new ScalarEvent(null, tag, new boolean[] { (tag == null), (tag == null) }, serializer
              .write(object), this.config.writeConfig.quote.c));
        return;
      } 
    } 
    if (Beans.isScalar(valueClass)) {
      this.emitter.emit((Event)new ScalarEvent(null, tag, new boolean[] { true, true }, String.valueOf(object), this.config.writeConfig.quote.c));
      return;
    } 
    if (object instanceof Enum) {
      this.emitter.emit((Event)new ScalarEvent(null, object.getClass().getName(), new boolean[] { object
              .getClass().equals(fieldClass), object.getClass().equals(fieldClass) }, ((Enum)object)
            .name(), this.config.writeConfig.quote.c));
      return;
    } 
    if (object instanceof java.util.Collection) {
      this.emitter.emit((Event)new SequenceStartEvent(anchor, tag, !showTag, this.config.writeConfig.isFlowStyle()));
      for (Object item : object) {
        if (isRoot && !this.config.writeConfig.writeRootElementTags)
          elementType = item.getClass(); 
        writeValue(item, elementType, null, null);
      } 
      this.emitter.emit(Event.SEQUENCE_END);
      return;
    } 
    if (object instanceof Map) {
      this.emitter.emit((Event)new MappingStartEvent(anchor, tag, !showTag, this.config.writeConfig.isFlowStyle()));
      Map map = (Map)object;
      for (Object item : map.entrySet()) {
        Map.Entry entry = (Map.Entry)item;
        Object key = entry.getKey(), value = entry.getValue();
        if (isRoot && !this.config.writeConfig.writeRootElementTags)
          elementType = value.getClass(); 
        if (this.config.tagSuffix != null && key instanceof String) {
          if (((String)key).endsWith(this.config.tagSuffix))
            continue; 
          if (value instanceof String) {
            Object valueTag = map.get(key + this.config.tagSuffix);
            if (valueTag instanceof String) {
              writeValue(key, null, null, null);
              this.emitter.emit((Event)new ScalarEvent(null, (String)valueTag, new boolean[] { false, false }, (String)value, this.config.writeConfig.quote.c));
              continue;
            } 
          } 
        } 
        writeValue(key, null, null, null);
        writeValue(value, elementType, null, null);
      } 
      this.emitter.emit(Event.MAPPING_END);
      return;
    } 
    if (fieldClass.isArray()) {
      elementType = fieldClass.getComponentType();
      this.emitter.emit((Event)new SequenceStartEvent(anchor, null, true, this.config.writeConfig.isFlowStyle()));
      for (int i = 0, n = Array.getLength(object); i < n; i++)
        writeValue(Array.get(object, i), elementType, null, null); 
      this.emitter.emit(Event.SEQUENCE_END);
      return;
    } 
    Object prototype = null;
    if (!this.config.writeConfig.writeDefaultValues && valueClass != Class.class) {
      prototype = this.defaultValuePrototypes.get(valueClass);
      if (prototype == null && Beans.getDeferredConstruction(valueClass, this.config) == null) {
        try {
          prototype = Beans.createObject(valueClass, this.config.privateConstructors);
        } catch (InvocationTargetException ex) {
          throw new YamlException("Error creating object prototype to determine default values.", ex);
        } 
        this.defaultValuePrototypes.put(valueClass, prototype);
      } 
    } 
    Set<Beans.Property> properties = Beans.getProperties(valueClass, this.config.beanProperties, this.config.privateFields, this.config);
    this.emitter.emit((Event)new MappingStartEvent(anchor, tag, !showTag, this.config.writeConfig.isFlowStyle()));
    for (Beans.Property property : properties) {
      try {
        Object propertyValue = property.get(object);
        if (prototype != null) {
          Object prototypeValue = property.get(prototype);
          if ((propertyValue == null && prototypeValue == null) || (
            propertyValue != null && prototypeValue != null && prototypeValue.equals(propertyValue)))
            continue; 
        } 
        this.emitter.emit((Event)new ScalarEvent(null, null, new boolean[] { true, true }, property
              .getName(), this.config.writeConfig.quote.c));
        Class propertyElementType = this.config.propertyToElementType.get(property);
        Class propertyDefaultType = this.config.propertyToDefaultType.get(property);
        writeValue(propertyValue, property.getType(), propertyElementType, propertyDefaultType);
      } catch (Exception ex) {
        throw new YamlException("Error getting property '" + property + "' on class: " + valueClass.getName(), ex);
      } 
    } 
    this.emitter.emit(Event.MAPPING_END);
  }
  
  private void countObjectReferences(Object object) throws YamlException {
    if (object == null || Beans.isScalar(object.getClass()))
      return; 
    Integer count = this.referenceCount.get(object);
    if (count != null) {
      this.referenceCount.put(object, Integer.valueOf(count.intValue() + 1));
      return;
    } 
    this.referenceCount.put(object, Integer.valueOf(1));
    if (object instanceof java.util.Collection) {
      for (Object item : object)
        countObjectReferences(item); 
      return;
    } 
    if (object instanceof Map) {
      for (Object value : ((Map)object).values())
        countObjectReferences(value); 
      return;
    } 
    if (object.getClass().isArray()) {
      for (int i = 0, n = Array.getLength(object); i < n; i++)
        countObjectReferences(Array.get(object, i)); 
      return;
    } 
    Set<Beans.Property> properties = Beans.getProperties(object.getClass(), this.config.beanProperties, this.config.privateFields, this.config);
    for (Beans.Property property : properties) {
      Object propertyValue;
      if (Beans.isScalar(property.getType()))
        continue; 
      try {
        propertyValue = property.get(object);
      } catch (Exception ex) {
        throw new YamlException("Error getting property '" + property + "' on class: " + object.getClass().getName(), ex);
      } 
      countObjectReferences(propertyValue);
    } 
  }
}
