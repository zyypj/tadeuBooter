package me.syncwrld.booter.libs.javassist.util.proxy;

import java.lang.reflect.Method;

public interface MethodFilter {
  boolean isHandled(Method paramMethod);
}
