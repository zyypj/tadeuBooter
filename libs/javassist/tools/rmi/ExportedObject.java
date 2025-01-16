package me.syncwrld.booter.libs.javassist.tools.rmi;

import java.lang.reflect.Method;

class ExportedObject {
  public int identifier;
  
  public Object object;
  
  public Method[] methods;
}
