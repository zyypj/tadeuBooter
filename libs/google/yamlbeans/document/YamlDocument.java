package me.syncwrld.booter.libs.google.yamlbeans.document;

import java.util.Iterator;
import me.syncwrld.booter.libs.google.yamlbeans.YamlException;

public interface YamlDocument {
  String getTag();
  
  int size();
  
  YamlEntry getEntry(String paramString) throws YamlException;
  
  YamlEntry getEntry(int paramInt) throws YamlException;
  
  boolean deleteEntry(String paramString) throws YamlException;
  
  void setEntry(String paramString, boolean paramBoolean) throws YamlException;
  
  void setEntry(String paramString, Number paramNumber) throws YamlException;
  
  void setEntry(String paramString1, String paramString2) throws YamlException;
  
  void setEntry(String paramString, YamlElement paramYamlElement) throws YamlException;
  
  YamlElement getElement(int paramInt) throws YamlException;
  
  void deleteElement(int paramInt) throws YamlException;
  
  void setElement(int paramInt, boolean paramBoolean) throws YamlException;
  
  void setElement(int paramInt, Number paramNumber) throws YamlException;
  
  void setElement(int paramInt, String paramString) throws YamlException;
  
  void setElement(int paramInt, YamlElement paramYamlElement) throws YamlException;
  
  void addElement(boolean paramBoolean) throws YamlException;
  
  void addElement(Number paramNumber) throws YamlException;
  
  void addElement(String paramString) throws YamlException;
  
  void addElement(YamlElement paramYamlElement) throws YamlException;
  
  Iterator iterator();
}
