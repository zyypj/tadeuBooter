package me.syncwrld.booter.libs.google.yamlbeans;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import me.syncwrld.booter.libs.google.yamlbeans.parser.AliasEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.CollectionStartEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.Event;
import me.syncwrld.booter.libs.google.yamlbeans.parser.EventType;
import me.syncwrld.booter.libs.google.yamlbeans.parser.Parser;
import me.syncwrld.booter.libs.google.yamlbeans.parser.ScalarEvent;
import me.syncwrld.booter.libs.google.yamlbeans.scalar.ScalarSerializer;
import me.syncwrld.booter.libs.google.yamlbeans.tokenizer.Tokenizer;

public class YamlReader implements AutoCloseable {
  private final YamlConfig config;
  
  Parser parser;
  
  private final Map<String, Object> anchors = new HashMap<String, Object>();
  
  public YamlReader(Reader reader) {
    this(reader, new YamlConfig());
  }
  
  public YamlReader(Reader reader, YamlConfig config) {
    this.config = config;
    this.parser = new Parser(reader, config.readConfig.defaultVersion);
  }
  
  public YamlReader(String yaml) {
    this(new StringReader(yaml));
  }
  
  public YamlReader(String yaml, YamlConfig config) {
    this(new StringReader(yaml), config);
  }
  
  public YamlConfig getConfig() {
    return this.config;
  }
  
  public Object get(String alias) {
    return this.anchors.get(alias);
  }
  
  private void addAnchor(String key, Object value) {
    if (this.config.readConfig.anchors)
      this.anchors.put(key, value); 
  }
  
  public void close() throws IOException {
    this.parser.close();
    this.anchors.clear();
  }
  
  public Object read() throws YamlException {
    return read(null);
  }
  
  public <T> T read(Class<T> type) throws YamlException {
    return read(type, null);
  }
  
  public <T> T read(Class<T> type, Class elementType) throws YamlException {
    this.anchors.clear();
    try {
      Event event;
      do {
        event = this.parser.getNextEvent();
        if (event == null)
          return null; 
        if (event.type == EventType.STREAM_END)
          return null; 
      } while (event.type != EventType.DOCUMENT_START);
      Object object = readValue(type, elementType, null);
      this.parser.getNextEvent();
      return (T)object;
    } catch (me.syncwrld.booter.libs.google.yamlbeans.parser.Parser.ParserException ex) {
      throw new YamlException("Error parsing YAML.", ex);
    } catch (me.syncwrld.booter.libs.google.yamlbeans.tokenizer.Tokenizer.TokenizerException ex) {
      throw new YamlException("Error tokenizing YAML.", ex);
    } 
  }
  
  public <T> Iterator<T> readAll(final Class<T> type) {
    return new Iterator<T>() {
        public boolean hasNext() {
          Event event = YamlReader.this.parser.peekNextEvent();
          return (event != null && event.type != EventType.STREAM_END);
        }
        
        public T next() {
          try {
            return YamlReader.this.read(type);
          } catch (YamlException ex) {
            throw new RuntimeException("Error reading YAML document for iterator.", ex);
          } 
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  protected Object readValue(Class<?> type, Class elementType, Class<?> defaultType) throws YamlException, Parser.ParserException, Tokenizer.TokenizerException {
    Object value;
    String tag = null, anchor = null;
    Event event = this.parser.peekNextEvent();
    switch (event.type) {
      case ALIAS:
        this.parser.getNextEvent();
        anchor = ((AliasEvent)event).anchor;
        value = this.anchors.get(anchor);
        if (value == null && this.config.readConfig.anchors)
          throw new YamlReaderException("Unknown anchor: " + anchor); 
        return value;
      case MAPPING_START:
      case SEQUENCE_START:
        tag = ((CollectionStartEvent)event).tag;
        anchor = ((CollectionStartEvent)event).anchor;
        break;
      case SCALAR:
        tag = ((ScalarEvent)event).tag;
        anchor = ((ScalarEvent)event).anchor;
        break;
    } 
    return readValueInternal(chooseType(tag, defaultType, type), elementType, anchor);
  }
  
  private Class<?> chooseType(String tag, Class<?> defaultType, Class<?> providedType) throws YamlReaderException {
    if (tag != null && this.config.readConfig.classTags) {
      Class<?> userConfiguredByTag = this.config.tagToClass.get(tag);
      if (userConfiguredByTag != null)
        return userConfiguredByTag; 
      ClassLoader classLoader = (this.config.readConfig.classLoader == null) ? getClass().getClassLoader() : this.config.readConfig.classLoader;
      tag = tag.replace("!", "");
      try {
        Class<?> loadedFromTag = findTagClass(tag, classLoader);
        if (loadedFromTag != null) {
          if (providedType != null && !providedType.isAssignableFrom(loadedFromTag))
            throw new YamlReaderException("Class specified by tag is incompatible with expected type: " + loadedFromTag
                .getName() + " (expected " + providedType.getName() + ")"); 
          return loadedFromTag;
        } 
      } catch (ClassNotFoundException e) {
        throw new YamlReaderException("Unable to find class specified by tag: " + tag);
      } 
    } 
    if (defaultType != null)
      return defaultType; 
    return providedType;
  }
  
  protected Class<?> findTagClass(String tag, ClassLoader classLoader) throws ClassNotFoundException {
    return Class.forName(tag, true, classLoader);
  }
  
  private Object readValueInternal(Class<Object> type, Class<?> elementType, String anchor) throws YamlException, Parser.ParserException, Tokenizer.TokenizerException {
    Class<ArrayList> clazz;
    Object object;
    Collection<Object> collection;
    ArrayList<Object> keys;
    Object array;
    int i;
    if (type == null || type == Object.class) {
      Class<LinkedHashMap> clazz2;
      Class<String> clazz1;
      Event event1 = this.parser.peekNextEvent();
      switch (event1.type) {
        case MAPPING_START:
          clazz2 = LinkedHashMap.class;
          break;
        case SCALAR:
          if (this.config.readConfig.guessNumberTypes) {
            String value = ((ScalarEvent)event1).value;
            if (value != null) {
              Number number = valueConvertedNumber(value);
              if (number != null) {
                if (anchor != null)
                  addAnchor(anchor, number); 
                this.parser.getNextEvent();
                return number;
              } 
            } 
          } 
          clazz1 = String.class;
          break;
        case SEQUENCE_START:
          clazz = ArrayList.class;
          break;
        default:
          throw new YamlReaderException("Expected scalar, sequence, or mapping but found: " + event1.type);
      } 
    } 
    if (Beans.isScalar(clazz)) {
      Event event1 = this.parser.getNextEvent();
      if (event1.type != EventType.SCALAR)
        throw new YamlReaderException("Expected scalar for primitive type '" + clazz
            .getClass() + "' but found: " + event1.type); 
      String value = ((ScalarEvent)event1).value;
      try {
        Object convertedValue;
        if (value == null) {
          convertedValue = null;
        } else if (clazz == String.class) {
          convertedValue = value;
        } else if (clazz == int.class || clazz == Integer.class) {
          convertedValue = Integer.decode(value);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
          convertedValue = Boolean.valueOf(value);
        } else if (clazz == float.class || clazz == Float.class) {
          convertedValue = Float.valueOf(value);
        } else if (clazz == double.class || clazz == Double.class) {
          convertedValue = Double.valueOf(value);
        } else if (clazz == long.class || clazz == Long.class) {
          convertedValue = Long.decode(value);
        } else if (clazz == short.class || clazz == Short.class) {
          convertedValue = Short.decode(value);
        } else if (clazz == char.class || clazz == Character.class) {
          convertedValue = Character.valueOf(value.charAt(0));
        } else if (clazz == byte.class || clazz == Byte.class) {
          convertedValue = Byte.decode(value);
        } else {
          throw new YamlException("Unknown field type.");
        } 
        if (anchor != null)
          addAnchor(anchor, convertedValue); 
        return convertedValue;
      } catch (Exception ex) {
        Object convertedValue;
        throw new YamlReaderException("Unable to convert value to required type \"" + clazz + "\": " + value, convertedValue);
      } 
    } 
    for (Map.Entry<Class<?>, ScalarSerializer> entry : this.config.scalarSerializers.entrySet()) {
      if (((Class)entry.getKey()).isAssignableFrom(clazz)) {
        ScalarSerializer serializer = entry.getValue();
        Event event1 = this.parser.getNextEvent();
        if (event1.type != EventType.SCALAR)
          throw new YamlReaderException("Expected scalar for type '" + clazz + "' to be deserialized by scalar serializer '" + serializer
              .getClass().getName() + "' but found: " + event1.type); 
        Object value = serializer.read(((ScalarEvent)event1).value);
        if (anchor != null)
          addAnchor(anchor, value); 
        return value;
      } 
    } 
    if (Enum.class.isAssignableFrom(clazz)) {
      Event event1 = this.parser.getNextEvent();
      if (event1.type != EventType.SCALAR)
        throw new YamlReaderException("Expected scalar for enum type but found: " + event1.type); 
      String enumValueName = ((ScalarEvent)event1).value;
      if (enumValueName == null)
        return null; 
      try {
        return Enum.valueOf(clazz, enumValueName);
      } catch (Exception ex) {
        throw new YamlReaderException("Unable to find enum value '" + enumValueName + "' for enum class: " + clazz.getName());
      } 
    } 
    Event event = this.parser.peekNextEvent();
    switch (event.type) {
      case MAPPING_START:
        event = this.parser.getNextEvent();
        try {
          object = createObject(clazz);
        } catch (InvocationTargetException ex) {
          throw new YamlReaderException("Error creating object.", ex);
        } 
        if (anchor != null)
          addAnchor(anchor, object); 
        keys = new ArrayList();
        while (true) {
          if ((this.parser.peekNextEvent()).type == EventType.MAPPING_END) {
            this.parser.getNextEvent();
            break;
          } 
          Object key = readValue(null, null, null);
          boolean isExplicitKey = key instanceof Map;
          Object value = null;
          if (isExplicitKey) {
            Map.Entry nameValuePair = ((Map)key).entrySet().iterator().next();
            key = nameValuePair.getKey();
            value = nameValuePair.getValue();
          } 
          if (object instanceof Map) {
            if (this.config.tagSuffix != null) {
              Event nextEvent = this.parser.peekNextEvent();
              switch (nextEvent.type) {
                case MAPPING_START:
                case SEQUENCE_START:
                  ((Map<String, String>)object).put(key + this.config.tagSuffix, ((CollectionStartEvent)nextEvent).tag);
                  break;
                case SCALAR:
                  ((Map<String, String>)object).put(key + this.config.tagSuffix, ((ScalarEvent)nextEvent).tag);
                  break;
              } 
            } 
            if (!isExplicitKey)
              value = readValue(elementType, null, null); 
            if (!this.config.allowDuplicates && ((Map)object).containsKey(key))
              throw new YamlReaderException("Duplicate key found '" + key + "'"); 
            if (this.config.readConfig.autoMerge && "<<".equals(key) && value != null) {
              mergeMap((Map<String, Object>)object, value);
              continue;
            } 
            ((Map<Object, Object>)object).put(key, value);
            continue;
          } 
          try {
            if (!this.config.allowDuplicates && keys.contains(key))
              throw new YamlReaderException("Duplicate key found '" + key + "'"); 
            keys.add(key);
            Beans.Property property = Beans.getProperty(clazz, (String)key, this.config.beanProperties, this.config.privateFields, this.config);
            if (property == null) {
              if (this.config.readConfig.ignoreUnknownProperties) {
                Event nextEvent = this.parser.peekNextEvent();
                EventType nextType = nextEvent.type;
                if (nextType == EventType.SEQUENCE_START || nextType == EventType.MAPPING_START) {
                  skipRange();
                  continue;
                } 
                this.parser.getNextEvent();
                continue;
              } 
              throw new YamlReaderException("Unable to find property '" + key + "' on class: " + clazz.getName());
            } 
            Class propertyElementType = this.config.propertyToElementType.get(property);
            if (propertyElementType == null)
              propertyElementType = property.getElementType(); 
            Class propertyDefaultType = this.config.propertyToDefaultType.get(property);
            if (!isExplicitKey)
              value = readValue(property.getType(), propertyElementType, propertyDefaultType); 
            property.set(object, value);
          } catch (Exception ex) {
            if (ex instanceof YamlReaderException)
              throw (YamlReaderException)ex; 
            throw new YamlReaderException("Error setting property '" + key + "' on class: " + clazz.getName(), ex);
          } 
        } 
        if (object instanceof DeferredConstruction)
          try {
            object = ((DeferredConstruction)object).construct();
            if (anchor != null)
              addAnchor(anchor, object); 
          } catch (InvocationTargetException ex) {
            throw new YamlReaderException("Error creating object.", ex);
          }  
        return object;
      case SEQUENCE_START:
        event = this.parser.getNextEvent();
        if (Collection.class.isAssignableFrom(clazz)) {
          try {
            collection = (Collection)Beans.createObject(clazz, this.config.privateConstructors);
          } catch (InvocationTargetException ex) {
            throw new YamlReaderException("Error creating object.", ex);
          } 
        } else if (clazz.isArray()) {
          collection = new ArrayList();
          elementType = clazz.getComponentType();
        } else {
          throw new YamlReaderException("A sequence is not a valid value for the type: " + clazz.getName());
        } 
        if (!clazz.isArray() && anchor != null)
          addAnchor(anchor, collection); 
        while (true) {
          event = this.parser.peekNextEvent();
          if (event.type == EventType.SEQUENCE_END) {
            this.parser.getNextEvent();
            break;
          } 
          collection.add(readValue(elementType, null, null));
        } 
        if (!clazz.isArray())
          return collection; 
        array = Array.newInstance(elementType, collection.size());
        i = 0;
        for (Object object1 : collection)
          Array.set(array, i++, object1); 
        if (anchor != null)
          addAnchor(anchor, array); 
        return array;
    } 
    throw new YamlReaderException("Expected data for a " + clazz.getName() + " field but found: " + event.type);
  }
  
  private void mergeMap(Map<String, Object> dest, Object source) throws YamlReaderException {
    if (source instanceof Collection) {
      for (Object item : source)
        mergeMap(dest, item); 
    } else if (source instanceof Map) {
      Map<String, Object> map = (Map<String, Object>)source;
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        if (!dest.containsKey(entry.getKey()))
          dest.put(entry.getKey(), entry.getValue()); 
      } 
    } else {
      throw new YamlReaderException("Expected a mapping or a sequence of mappings for a '<<' merge field but found: " + source
          .getClass().getSimpleName());
    } 
  }
  
  protected Object createObject(Class type) throws InvocationTargetException {
    DeferredConstruction deferredConstruction = Beans.getDeferredConstruction(type, this.config);
    if (deferredConstruction != null)
      return deferredConstruction; 
    return Beans.createObject(type, this.config.privateConstructors);
  }
  
  public class YamlReaderException extends YamlException {
    public YamlReaderException(String message, Throwable cause) {
      super("Line " + YamlReader.this.parser.getLineNumber() + ", column " + YamlReader.this.parser.getColumn() + ": " + message, cause);
    }
    
    public YamlReaderException(String message) {
      this(message, null);
    }
  }
  
  private Number valueConvertedNumber(String value) {
    Number number = null;
    try {
      number = Long.decode(value);
    } catch (NumberFormatException numberFormatException) {}
    if (number == null)
      try {
        number = Double.valueOf(Double.parseDouble(value));
      } catch (NumberFormatException numberFormatException) {} 
    return number;
  }
  
  private void skipRange() {
    int depth = 0;
    do {
      Event nextEvent = this.parser.getNextEvent();
      switch (nextEvent.type) {
        case SEQUENCE_START:
          depth++;
          break;
        case MAPPING_START:
          depth++;
          break;
        case SEQUENCE_END:
          depth--;
          break;
        case MAPPING_END:
          depth--;
          break;
      } 
    } while (depth > 0);
  }
  
  public static void main(String[] args) throws Exception {
    YamlReader reader = new YamlReader(new FileReader("test/test.yml"));
    Object object = reader.read();
    System.out.println(object);
    StringWriter string = new StringWriter();
    YamlWriter writer = new YamlWriter(string);
    writer.write(object);
    writer.close();
    System.out.println(string);
  }
}
