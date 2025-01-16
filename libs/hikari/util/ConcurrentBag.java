package me.syncwrld.booter.libs.hikari.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcurrentBag<T extends ConcurrentBag.IConcurrentBagEntry> implements AutoCloseable {
  private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentBag.class);
  
  private final CopyOnWriteArrayList<T> sharedList;
  
  private final boolean weakThreadLocals;
  
  private final ThreadLocal<List<Object>> threadList;
  
  private final IBagStateListener listener;
  
  private final AtomicInteger waiters;
  
  private volatile boolean closed;
  
  private final SynchronousQueue<T> handoffQueue;
  
  public ConcurrentBag(IBagStateListener listener) {
    this.listener = listener;
    this.weakThreadLocals = useWeakThreadLocals();
    this.handoffQueue = new SynchronousQueue<>(true);
    this.waiters = new AtomicInteger();
    this.sharedList = new CopyOnWriteArrayList<>();
    if (this.weakThreadLocals) {
      this.threadList = ThreadLocal.withInitial(() -> new ArrayList(16));
    } else {
      this.threadList = ThreadLocal.withInitial(() -> new FastList(IConcurrentBagEntry.class, 16));
    } 
  }
  
  public T borrow(long timeout, TimeUnit timeUnit) throws InterruptedException {
    List<Object> list = this.threadList.get();
    for (int i = list.size() - 1; i >= 0; i--) {
      Object entry = list.remove(i);
      IConcurrentBagEntry iConcurrentBagEntry = this.weakThreadLocals ? ((WeakReference<IConcurrentBagEntry>)entry).get() : (IConcurrentBagEntry)entry;
      if (iConcurrentBagEntry != null && iConcurrentBagEntry.compareAndSet(0, 1))
        return (T)iConcurrentBagEntry; 
    } 
    int waiting = this.waiters.incrementAndGet();
    try {
      for (IConcurrentBagEntry iConcurrentBagEntry : this.sharedList) {
        if (iConcurrentBagEntry.compareAndSet(0, 1)) {
          if (waiting > 1)
            this.listener.addBagItem(waiting - 1); 
          return (T)iConcurrentBagEntry;
        } 
      } 
      this.listener.addBagItem(waiting);
      timeout = timeUnit.toNanos(timeout);
      do {
        long start = ClockSource.currentTime();
        IConcurrentBagEntry iConcurrentBagEntry = (IConcurrentBagEntry)this.handoffQueue.poll(timeout, TimeUnit.NANOSECONDS);
        if (iConcurrentBagEntry == null || iConcurrentBagEntry.compareAndSet(0, 1))
          return (T)iConcurrentBagEntry; 
        timeout -= ClockSource.elapsedNanos(start);
      } while (timeout > 10000L);
      return null;
    } finally {
      this.waiters.decrementAndGet();
    } 
  }
  
  public void requite(T bagEntry) {
    bagEntry.setState(0);
    for (int i = 0; this.waiters.get() > 0; i++) {
      if (bagEntry.getState() != 0 || this.handoffQueue.offer(bagEntry))
        return; 
      if ((i & 0xFF) == 255) {
        LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(10L));
      } else {
        Thread.yield();
      } 
    } 
    List<Object> threadLocalList = this.threadList.get();
    if (threadLocalList.size() < 50)
      threadLocalList.add(this.weakThreadLocals ? new WeakReference<>(bagEntry) : bagEntry); 
  }
  
  public void add(T bagEntry) {
    if (this.closed) {
      LOGGER.info("ConcurrentBag has been closed, ignoring add()");
      throw new IllegalStateException("ConcurrentBag has been closed, ignoring add()");
    } 
    this.sharedList.add(bagEntry);
    while (this.waiters.get() > 0 && bagEntry.getState() == 0 && !this.handoffQueue.offer(bagEntry))
      Thread.yield(); 
  }
  
  public boolean remove(T bagEntry) {
    if (!bagEntry.compareAndSet(1, -1) && !bagEntry.compareAndSet(-2, -1) && !this.closed) {
      LOGGER.warn("Attempt to remove an object from the bag that was not borrowed or reserved: {}", bagEntry);
      return false;
    } 
    boolean removed = this.sharedList.remove(bagEntry);
    if (!removed && !this.closed)
      LOGGER.warn("Attempt to remove an object from the bag that does not exist: {}", bagEntry); 
    ((List)this.threadList.get()).remove(bagEntry);
    return removed;
  }
  
  public void close() {
    this.closed = true;
  }
  
  public List<T> values(int state) {
    List<T> list = (List<T>)this.sharedList.stream().filter(e -> (e.getState() == state)).collect(Collectors.toList());
    Collections.reverse(list);
    return list;
  }
  
  public List<T> values() {
    return (List<T>)this.sharedList.clone();
  }
  
  public boolean reserve(T bagEntry) {
    return bagEntry.compareAndSet(0, -2);
  }
  
  public void unreserve(T bagEntry) {
    if (bagEntry.compareAndSet(-2, 0)) {
      while (this.waiters.get() > 0 && !this.handoffQueue.offer(bagEntry))
        Thread.yield(); 
    } else {
      LOGGER.warn("Attempt to relinquish an object to the bag that was not reserved: {}", bagEntry);
    } 
  }
  
  public int getWaitingThreadCount() {
    return this.waiters.get();
  }
  
  public int getCount(int state) {
    int count = 0;
    for (IConcurrentBagEntry e : this.sharedList) {
      if (e.getState() == state)
        count++; 
    } 
    return count;
  }
  
  public int[] getStateCounts() {
    int[] states = new int[6];
    for (IConcurrentBagEntry e : this.sharedList)
      states[e.getState()] = states[e.getState()] + 1; 
    states[4] = this.sharedList.size();
    states[5] = this.waiters.get();
    return states;
  }
  
  public int size() {
    return this.sharedList.size();
  }
  
  public void dumpState() {
    this.sharedList.forEach(entry -> LOGGER.info(entry.toString()));
  }
  
  private boolean useWeakThreadLocals() {
    try {
      if (System.getProperty("me.syncwrld.booter.libs.hikari.useWeakReferences") != null)
        return Boolean.getBoolean("me.syncwrld.booter.libs.hikari.useWeakReferences"); 
      return (getClass().getClassLoader() != ClassLoader.getSystemClassLoader());
    } catch (SecurityException se) {
      return true;
    } 
  }
  
  public static interface IBagStateListener {
    void addBagItem(int param1Int);
  }
  
  public static interface IConcurrentBagEntry {
    public static final int STATE_NOT_IN_USE = 0;
    
    public static final int STATE_IN_USE = 1;
    
    public static final int STATE_REMOVED = -1;
    
    public static final int STATE_RESERVED = -2;
    
    boolean compareAndSet(int param1Int1, int param1Int2);
    
    void setState(int param1Int);
    
    int getState();
  }
}
