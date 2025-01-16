package me.syncwrld.booter.libs.google.guava.base.internal;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

public class Finalizer implements Runnable {
  private static final Logger logger = Logger.getLogger(Finalizer.class.getName());
  
  private static final String FINALIZABLE_REFERENCE = "me.syncwrld.booter.libs.google.guava.base.FinalizableReference";
  
  private final WeakReference<Class<?>> finalizableReferenceClassReference;
  
  private final PhantomReference<Object> frqReference;
  
  private final ReferenceQueue<Object> queue;
  
  public static void startFinalizer(Class<?> finalizableReferenceClass, ReferenceQueue<Object> queue, PhantomReference<Object> frqReference) {
    if (!finalizableReferenceClass.getName().equals("me.syncwrld.booter.libs.google.guava.base.FinalizableReference"))
      throw new IllegalArgumentException("Expected com.google.common.base.FinalizableReference."); 
    Finalizer finalizer = new Finalizer(finalizableReferenceClass, queue, frqReference);
    String threadName = Finalizer.class.getName();
    Thread thread = null;
    if (bigThreadConstructor != null)
      try {
        boolean inheritThreadLocals = false;
        long defaultStackSize = 0L;
        thread = bigThreadConstructor.newInstance(new Object[] { null, finalizer, threadName, Long.valueOf(defaultStackSize), Boolean.valueOf(inheritThreadLocals) });
      } catch (Throwable t) {
        logger.log(Level.INFO, "Failed to create a thread without inherited thread-local values", t);
      }  
    if (thread == null)
      thread = new Thread((ThreadGroup)null, finalizer, threadName); 
    thread.setDaemon(true);
    try {
      if (inheritableThreadLocals != null)
        inheritableThreadLocals.set(thread, null); 
    } catch (Throwable t) {
      logger.log(Level.INFO, "Failed to clear thread local values inherited by reference finalizer thread.", t);
    } 
    thread.start();
  }
  
  @CheckForNull
  private static final Constructor<Thread> bigThreadConstructor = getBigThreadConstructor();
  
  @CheckForNull
  private static final Field inheritableThreadLocals = (bigThreadConstructor == null) ? getInheritableThreadLocalsField() : null;
  
  private Finalizer(Class<?> finalizableReferenceClass, ReferenceQueue<Object> queue, PhantomReference<Object> frqReference) {
    this.queue = queue;
    this.finalizableReferenceClassReference = new WeakReference<>(finalizableReferenceClass);
    this.frqReference = frqReference;
  }
  
  public void run() {
    while (true) {
      try {
        do {
        
        } while (cleanUp(this.queue.remove()));
        break;
      } catch (InterruptedException interruptedException) {}
    } 
  }
  
  private boolean cleanUp(Reference<?> firstReference) {
    Method finalizeReferentMethod = getFinalizeReferentMethod();
    if (finalizeReferentMethod == null)
      return false; 
    if (!finalizeReference(firstReference, finalizeReferentMethod))
      return false; 
    while (true) {
      Reference<?> furtherReference = this.queue.poll();
      if (furtherReference == null)
        return true; 
      if (!finalizeReference(furtherReference, finalizeReferentMethod))
        return false; 
    } 
  }
  
  private boolean finalizeReference(Reference<?> reference, Method finalizeReferentMethod) {
    reference.clear();
    if (reference == this.frqReference)
      return false; 
    try {
      finalizeReferentMethod.invoke(reference, new Object[0]);
    } catch (Throwable t) {
      logger.log(Level.SEVERE, "Error cleaning up after reference.", t);
    } 
    return true;
  }
  
  @CheckForNull
  private Method getFinalizeReferentMethod() {
    Class<?> finalizableReferenceClass = this.finalizableReferenceClassReference.get();
    if (finalizableReferenceClass == null)
      return null; 
    try {
      return finalizableReferenceClass.getMethod("finalizeReferent", new Class[0]);
    } catch (NoSuchMethodException e) {
      throw new AssertionError(e);
    } 
  }
  
  @CheckForNull
  private static Field getInheritableThreadLocalsField() {
    try {
      Field inheritableThreadLocals = Thread.class.getDeclaredField("inheritableThreadLocals");
      inheritableThreadLocals.setAccessible(true);
      return inheritableThreadLocals;
    } catch (Throwable t) {
      logger.log(Level.INFO, "Couldn't access Thread.inheritableThreadLocals. Reference finalizer threads will inherit thread local values.");
      return null;
    } 
  }
  
  @CheckForNull
  private static Constructor<Thread> getBigThreadConstructor() {
    try {
      return Thread.class.getConstructor(new Class[] { ThreadGroup.class, Runnable.class, String.class, long.class, boolean.class });
    } catch (Throwable t) {
      return null;
    } 
  }
}
