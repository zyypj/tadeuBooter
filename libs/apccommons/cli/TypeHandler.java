package me.syncwrld.booter.libs.apccommons.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class TypeHandler {
  public static Class<?> createClass(String className) throws ParseException {
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new ParseException("Unable to find the class: " + className);
    } 
  }
  
  public static Date createDate(String str) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
  
  public static File createFile(String str) {
    return new File(str);
  }
  
  public static File[] createFiles(String str) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
  
  public static Number createNumber(String str) throws ParseException {
    try {
      if (str.indexOf('.') != -1)
        return Double.valueOf(str); 
      return Long.valueOf(str);
    } catch (NumberFormatException e) {
      throw new ParseException(e.getMessage());
    } 
  }
  
  public static Object createObject(String className) throws ParseException {
    Class<?> cl;
    try {
      cl = Class.forName(className);
    } catch (ClassNotFoundException cnfe) {
      throw new ParseException("Unable to find the class: " + className);
    } 
    try {
      return cl.getConstructor(new Class[0]).newInstance(new Object[0]);
    } catch (Exception e) {
      throw new ParseException(e.getClass().getName() + "; Unable to create an instance of: " + className);
    } 
  }
  
  public static URL createURL(String str) throws ParseException {
    try {
      return new URL(str);
    } catch (MalformedURLException e) {
      throw new ParseException("Unable to parse the URL: " + str);
    } 
  }
  
  public static <T> T createValue(String str, Class<T> clazz) throws ParseException {
    if (PatternOptionBuilder.STRING_VALUE == clazz)
      return (T)str; 
    if (PatternOptionBuilder.OBJECT_VALUE == clazz)
      return (T)createObject(str); 
    if (PatternOptionBuilder.NUMBER_VALUE == clazz)
      return (T)createNumber(str); 
    if (PatternOptionBuilder.DATE_VALUE == clazz)
      return (T)createDate(str); 
    if (PatternOptionBuilder.CLASS_VALUE == clazz)
      return (T)createClass(str); 
    if (PatternOptionBuilder.FILE_VALUE == clazz)
      return (T)createFile(str); 
    if (PatternOptionBuilder.EXISTING_FILE_VALUE == clazz)
      return (T)openFile(str); 
    if (PatternOptionBuilder.FILES_VALUE == clazz)
      return (T)createFiles(str); 
    if (PatternOptionBuilder.URL_VALUE == clazz)
      return (T)createURL(str); 
    throw new ParseException("Unable to handle the class: " + clazz);
  }
  
  public static Object createValue(String str, Object obj) throws ParseException {
    return createValue(str, (Class)obj);
  }
  
  public static FileInputStream openFile(String str) throws ParseException {
    try {
      return new FileInputStream(str);
    } catch (FileNotFoundException e) {
      throw new ParseException("Unable to find file: " + str);
    } 
  }
}
