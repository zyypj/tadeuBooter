package me.syncwrld.booter.libs.javassist.util.proxy;

import java.lang.reflect.Method;

public interface MethodHandler {
  Object invoke(Object paramObject, Method paramMethod1, Method paramMethod2, Object[] paramArrayOfObject) throws Throwable;
}
