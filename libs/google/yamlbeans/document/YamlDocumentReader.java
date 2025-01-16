package me.syncwrld.booter.libs.google.yamlbeans.document;

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import me.syncwrld.booter.libs.google.yamlbeans.Version;
import me.syncwrld.booter.libs.google.yamlbeans.YamlException;
import me.syncwrld.booter.libs.google.yamlbeans.parser.AliasEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.Event;
import me.syncwrld.booter.libs.google.yamlbeans.parser.EventType;
import me.syncwrld.booter.libs.google.yamlbeans.parser.MappingStartEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.Parser;
import me.syncwrld.booter.libs.google.yamlbeans.parser.ScalarEvent;
import me.syncwrld.booter.libs.google.yamlbeans.parser.SequenceStartEvent;
import me.syncwrld.booter.libs.google.yamlbeans.tokenizer.Tokenizer;

public class YamlDocumentReader {
  Parser parser;
  
  public YamlDocumentReader(String yaml) {
    this(new StringReader(yaml));
  }
  
  public YamlDocumentReader(String yaml, Version version) {
    this(new StringReader(yaml), version);
  }
  
  public YamlDocumentReader(Reader reader) {
    this(reader, (Version)null);
  }
  
  public YamlDocumentReader(Reader reader, Version version) {
    if (version == null)
      version = Version.DEFAULT_VERSION; 
    this.parser = new Parser(reader, version);
  }
  
  public YamlDocument read() throws YamlException {
    return read(YamlDocument.class);
  }
  
  public <T> T read(Class<T> type) throws YamlException {
    try {
      while (true) {
        Event event = this.parser.peekNextEvent();
        if (event == null)
          return null; 
        switch (event.type) {
          case STREAM_START:
            this.parser.getNextEvent();
            continue;
          case STREAM_END:
            this.parser.getNextEvent();
            return null;
          case DOCUMENT_START:
            this.parser.getNextEvent();
            return (T)readDocument();
        } 
        break;
      } 
      throw new IllegalStateException();
    } catch (me.syncwrld.booter.libs.google.yamlbeans.parser.Parser.ParserException ex) {
      throw new YamlException("Error parsing YAML.", ex);
    } catch (me.syncwrld.booter.libs.google.yamlbeans.tokenizer.Tokenizer.TokenizerException ex) {
      throw new YamlException("Error tokenizing YAML.", ex);
    } 
  }
  
  public <T> Iterator<T> readAll(final Class<T> type) {
    Iterator<T> iterator = new Iterator<T>() {
        public boolean hasNext() {
          Event event = YamlDocumentReader.this.parser.peekNextEvent();
          return (event != null && event.type != EventType.STREAM_END);
        }
        
        public T next() {
          try {
            return YamlDocumentReader.this.read(type);
          } catch (YamlException e) {
            throw new RuntimeException("Iterative reading documents exception", e);
          } 
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
    return iterator;
  }
  
  private YamlElement readDocument() {
    YamlElement yamlElement = null;
    Event event = this.parser.peekNextEvent();
    switch (event.type) {
      case SCALAR:
        yamlElement = readScalar();
        this.parser.getNextEvent();
        return yamlElement;
      case ALIAS:
        yamlElement = readAlias();
        this.parser.getNextEvent();
        return yamlElement;
      case MAPPING_START:
        yamlElement = readMapping();
        this.parser.getNextEvent();
        return yamlElement;
      case SEQUENCE_START:
        yamlElement = readSequence();
        this.parser.getNextEvent();
        return yamlElement;
    } 
    throw new IllegalStateException();
  }
  
  private YamlMapping readMapping() {
    Event event = this.parser.getNextEvent();
    if (event.type != EventType.MAPPING_START)
      throw new IllegalStateException(); 
    YamlMapping element = new YamlMapping();
    MappingStartEvent mapping = (MappingStartEvent)event;
    element.setTag(mapping.tag);
    element.setAnchor(mapping.anchor);
    readMappingElements(element);
    return element;
  }
  
  private void readMappingElements(YamlMapping mapping) {
    while (true) {
      Event event = this.parser.peekNextEvent();
      if (event.type == EventType.MAPPING_END) {
        this.parser.getNextEvent();
        return;
      } 
      YamlEntry entry = readEntry();
      mapping.addEntry(entry);
    } 
  }
  
  private YamlEntry readEntry() {
    YamlScalar scalar = readScalar();
    YamlElement value = readValue();
    return new YamlEntry(scalar, value);
  }
  
  private YamlElement readValue() {
    Event event = this.parser.peekNextEvent();
    switch (event.type) {
      case SCALAR:
        return readScalar();
      case ALIAS:
        return readAlias();
      case MAPPING_START:
        return readMapping();
      case SEQUENCE_START:
        return readSequence();
    } 
    throw new IllegalStateException();
  }
  
  private YamlAlias readAlias() {
    Event event = this.parser.getNextEvent();
    if (event.type != EventType.ALIAS)
      throw new IllegalStateException(); 
    YamlAlias element = new YamlAlias();
    AliasEvent alias = (AliasEvent)event;
    element.setAnchor(alias.anchor);
    return element;
  }
  
  private YamlSequence readSequence() {
    Event event = this.parser.getNextEvent();
    if (event.type != EventType.SEQUENCE_START)
      throw new IllegalStateException(); 
    YamlSequence element = new YamlSequence();
    SequenceStartEvent sequence = (SequenceStartEvent)event;
    element.setTag(sequence.tag);
    element.setAnchor(sequence.anchor);
    readSequenceElements(element);
    return element;
  }
  
  private void readSequenceElements(YamlSequence sequence) {
    while (true) {
      Event event = this.parser.peekNextEvent();
      if (event.type == EventType.SEQUENCE_END) {
        this.parser.getNextEvent();
        return;
      } 
      YamlElement element = readValue();
      sequence.addElement(element);
    } 
  }
  
  private YamlScalar readScalar() {
    Event event = this.parser.getNextEvent();
    if (event.type != EventType.SCALAR)
      throw new IllegalStateException(); 
    ScalarEvent scalar = (ScalarEvent)event;
    YamlScalar element = new YamlScalar();
    element.setTag(scalar.tag);
    element.setAnchor(scalar.anchor);
    element.setValue(scalar.value);
    return element;
  }
}
