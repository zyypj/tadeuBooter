package me.syncwrld.booter.libs.google.guava.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
public abstract class AbstractInvocationHandler implements InvocationHandler {
  private static final Object[] NO_ARGS = new Object[0];
  
  @CheckForNull
  public final Object invoke(Object proxy, Method method, @CheckForNull Object[] args) throws Throwable {
    if (args == null)
      args = NO_ARGS; 
    if (args.length == 0 && method.getName().equals("hashCode"))
      return Integer.valueOf(hashCode()); 
    if (args.length == 1 && method
      .getName().equals("equals") && method
      .getParameterTypes()[0] == Object.class) {
      Object arg = args[0];
      if (arg == null)
        return Boolean.valueOf(false); 
      if (proxy == arg)
        return Boolean.valueOf(true); 
      return Boolean.valueOf((isProxyOfSameInterfaces(arg, proxy.getClass()) && 
          equals(Proxy.getInvocationHandler(arg))));
    } 
    if (args.length == 0 && method.getName().equals("toString"))
      return toString(); 
    return handleInvocation(proxy, method, args);
  }
  
  @CheckForNull
  protected abstract Object handleInvocation(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable;
  
  public boolean equals(@CheckForNull Object obj) {
    return super.equals(obj);
  }
  
  public int hashCode() {
    return super.hashCode();
  }
  
  public String toString() {
    return super.toString();
  }
  
  private static boolean isProxyOfSameInterfaces(Object arg, Class<?> proxyClass) {
    return (proxyClass.isInstance(arg) || (
      
      Proxy.isProxyClass(arg.getClass()) && 
      Arrays.equals((Object[])arg.getClass().getInterfaces(), (Object[])proxyClass.getInterfaces())));
  }
}
