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
import me.syncwrld.booter.libs.google.yamlbeans.parser.MappingStartEvent;

public class YamlMapping extends YamlElement implements YamlDocument {
  List<YamlEntry> entries = new LinkedList<YamlEntry>();
  
  public int size() {
    return this.entries.size();
  }
  
  public void addEntry(YamlEntry entry) {
    this.entries.add(entry);
  }
  
  public boolean deleteEntry(String key) {
    for (int index = 0; index < this.entries.size(); index++) {
      if (key.equals(((YamlEntry)this.entries.get(index)).getKey().getValue())) {
        this.entries.remove(index);
        return true;
      } 
    } 
    return false;
  }
  
  public YamlEntry getEntry(String key) throws YamlException {
    for (YamlEntry entry : this.entries) {
      if (key.equals(entry.getKey().getValue()))
        return entry; 
    } 
    return null;
  }
  
  public YamlEntry getEntry(int index) throws YamlException {
    return this.entries.get(index);
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
    if (!this.entries.isEmpty()) {
      sb.append('{');
      for (YamlEntry entry : this.entries) {
        sb.append(entry.toString());
        sb.append(',');
      } 
      sb.setLength(sb.length() - 1);
      sb.append('}');
    } 
    return sb.toString();
  }
  
  public void emitEvent(Emitter emitter, YamlConfig.WriteConfig config) throws EmitterException, IOException {
    emitter.emit((Event)new MappingStartEvent(this.anchor, this.tag, (this.tag == null), config.isFlowStyle()));
    for (YamlEntry entry : this.entries)
      entry.emitEvent(emitter, config); 
    emitter.emit(Event.MAPPING_END);
  }
  
  public void setEntry(String key, boolean value) throws YamlException {
    setEntry(key, new YamlScalar(Boolean.valueOf(value)));
  }
  
  public void setEntry(String key, Number value) throws YamlException {
    setEntry(key, new YamlScalar(value));
  }
  
  public void setEntry(String key, String value) throws YamlException {
    setEntry(key, new YamlScalar(value));
  }
  
  public void setEntry(String key, YamlElement value) throws YamlException {
    YamlEntry entry = getEntry(key);
    if (entry != null) {
      entry.setValue(value);
    } else {
      entry = new YamlEntry(new YamlScalar(key), value);
      addEntry(entry);
    } 
  }
  
  public YamlElement getElement(int item) throws YamlException {
    throw new YamlException("Can only get element on sequence!");
  }
  
  public void deleteElement(int element) throws YamlException {
    throw new YamlException("Can only delete element on sequence!");
  }
  
  public void setElement(int item, boolean element) throws YamlException {
    throw new YamlException("Can only set element on sequence!");
  }
  
  public void setElement(int item, Number element) throws YamlException {
    throw new YamlException("Can only set element on sequence!");
  }
  
  public void setElement(int item, String element) throws YamlException {
    throw new YamlException("Can only set element on sequence!");
  }
  
  public void setElement(int item, YamlElement element) throws YamlException {
    throw new YamlException("Can only set element on sequence!");
  }
  
  public void addElement(boolean element) throws YamlException {
    throw new YamlException("Can only add element on sequence!");
  }
  
  public void addElement(Number element) throws YamlException {
    throw new YamlException("Can only add element on sequence!");
  }
  
  public void addElement(String element) throws YamlException {
    throw new YamlException("Can only add element on sequence!");
  }
  
  public void addElement(YamlElement element) throws YamlException {
    throw new YamlException("Can only add element on sequence!");
  }
  
  public Iterator<YamlEntry> iterator() {
    return this.entries.iterator();
  }
}
