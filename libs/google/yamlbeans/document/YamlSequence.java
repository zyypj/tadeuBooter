package me.syncwrld.booter.libs.google.yamlbeans.document;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import me.syncwrld.booter.libs.google.yamlbeans.YamlConfig;
import me.syncwrld.booter.libs.google.yamlbeans.YamlException;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.Emitter;
import me.syncwrld.booter.libs.google.yamlbeans.emitter.EmitterException;
import me.syncwrld.booter.libs.google.yamlbeans.parser.Event;
import me.syncwrld.booter.libs.google.yamlbeans.parser.SequenceStartEvent;

public class YamlSequence extends YamlElement implements YamlDocument {
  List<YamlElement> elements = new LinkedList<YamlElement>();
  
  public int size() {
    return this.elements.size();
  }
  
  public void addElement(YamlElement element) {
    this.elements.add(element);
  }
  
  public void deleteElement(int item) throws YamlException {
    this.elements.remove(item);
  }
  
  public YamlElement getElement(int item) throws YamlException {
    return this.elements.get(item);
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    if (this.anchor != null) {
      sb.append('&');
      sb.append(this.anchor);
      sb.append(' ');
    } 
    if (this.tag != null) {
      sb.append(" !");
      sb.append(this.tag);
    } 
    if (!this.elements.isEmpty()) {
      sb.append('[');
      for (YamlElement element : this.elements) {
        sb.append(element.toString());
        sb.append(',');
      } 
      sb.setLength(sb.length() - 1);
      sb.append(']');
    } 
    return sb.toString();
  }
  
  public void emitEvent(Emitter emitter, YamlConfig.WriteConfig config) throws EmitterException, IOException {
    emitter.emit((Event)new SequenceStartEvent(this.anchor, this.tag, (this.tag == null), config.isFlowStyle()));
    for (YamlElement element : this.elements)
      element.emitEvent(emitter, config); 
    emitter.emit(Event.SEQUENCE_END);
  }
  
  public YamlEntry getEntry(String key) throws YamlException {
    throw new YamlException("Can only get entry on mapping!");
  }
  
  public YamlEntry getEntry(int index) throws YamlException {
    throw new YamlException("Can only get entry on mapping!");
  }
  
  public boolean deleteEntry(String key) throws YamlException {
    throw new YamlException("Can only delete entry on mapping!");
  }
  
  public void setEntry(String key, boolean value) throws YamlException {
    throw new YamlException("Can only set entry on mapping!");
  }
  
  public void setEntry(String key, Number value) throws YamlException {
    throw new YamlException("Can only set entry on mapping!");
  }
  
  public void setEntry(String key, String value) throws YamlException {
    throw new YamlException("Can only set entry on mapping!");
  }
  
  public void setEntry(String key, YamlElement value) throws YamlException {
    throw new YamlException("Can only set entry on mapping!");
  }
  
  public void setElement(int item, boolean value) throws YamlException {
    this.elements.set(item, new YamlScalar(Boolean.valueOf(value)));
  }
  
  public void setElement(int item, Number value) throws YamlException {
    this.elements.set(item, new YamlScalar(value));
  }
  
  public void setElement(int item, String value) throws YamlException {
    this.elements.set(item, new YamlScalar(value));
  }
  
  public void setElement(int item, YamlElement element) throws YamlException {
    this.elements.set(item, element);
  }
  
  public void addElement(boolean value) throws YamlException {
    this.elements.add(new YamlScalar(Boolean.valueOf(value)));
  }
  
  public void addElement(Number value) throws YamlException {
    this.elements.add(new YamlScalar(value));
  }
  
  public void addElement(String value) throws YamlException {
    this.elements.add(new YamlScalar(value));
  }
  
  public Iterator<YamlElement> iterator() {
    return this.elements.iterator();
  }
}
