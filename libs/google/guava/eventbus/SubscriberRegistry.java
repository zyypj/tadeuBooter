package me.syncwrld.booter.libs.google.guava.eventbus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import me.syncwrld.booter.libs.google.guava.annotations.VisibleForTesting;
import me.syncwrld.booter.libs.google.guava.base.MoreObjects;
import me.syncwrld.booter.libs.google.guava.base.Objects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Throwables;
import me.syncwrld.booter.libs.google.guava.cache.CacheBuilder;
import me.syncwrld.booter.libs.google.guava.cache.CacheLoader;
import me.syncwrld.booter.libs.google.guava.cache.LoadingCache;
import me.syncwrld.booter.libs.google.guava.collect.HashMultimap;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableList;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableSet;
import me.syncwrld.booter.libs.google.guava.collect.Iterators;
import me.syncwrld.booter.libs.google.guava.collect.Lists;
import me.syncwrld.booter.libs.google.guava.collect.Maps;
import me.syncwrld.booter.libs.google.guava.collect.Multimap;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;
import me.syncwrld.booter.libs.google.guava.primitives.Primitives;
import me.syncwrld.booter.libs.google.guava.reflect.TypeToken;
import me.syncwrld.booter.libs.google.guava.util.concurrent.UncheckedExecutionException;
import me.syncwrld.booter.libs.google.j2objc.annotations.Weak;
import me.syncwrld.booter.libs.javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
final class SubscriberRegistry {
  private final ConcurrentMap<Class<?>, CopyOnWriteArraySet<Subscriber>> subscribers = Maps.newConcurrentMap();
  
  @Weak
  private final EventBus bus;
  
  SubscriberRegistry(EventBus bus) {
    this.bus = (EventBus)Preconditions.checkNotNull(bus);
  }
  
  void register(Object listener) {
    Multimap<Class<?>, Subscriber> listenerMethods = findAllSubscribers(listener);
    for (Map.Entry<Class<?>, Collection<Subscriber>> entry : (Iterable<Map.Entry<Class<?>, Collection<Subscriber>>>)listenerMethods.asMap().entrySet()) {
      Class<?> eventType = entry.getKey();
      Collection<Subscriber> eventMethodsInListener = entry.getValue();
      CopyOnWriteArraySet<Subscriber> eventSubscribers = this.subscribers.get(eventType);
      if (eventSubscribers == null) {
        CopyOnWriteArraySet<Subscriber> newSet = new CopyOnWriteArraySet<>();
        eventSubscribers = (CopyOnWriteArraySet<Subscriber>)MoreObjects.firstNonNull(this.subscribers.putIfAbsent(eventType, newSet), newSet);
      } 
      eventSubscribers.addAll(eventMethodsInListener);
    } 
  }
  
  void unregister(Object listener) {
    Multimap<Class<?>, Subscriber> listenerMethods = findAllSubscribers(listener);
    for (Map.Entry<Class<?>, Collection<Subscriber>> entry : (Iterable<Map.Entry<Class<?>, Collection<Subscriber>>>)listenerMethods.asMap().entrySet()) {
      Class<?> eventType = entry.getKey();
      Collection<Subscriber> listenerMethodsForType = entry.getValue();
      CopyOnWriteArraySet<Subscriber> currentSubscribers = this.subscribers.get(eventType);
      if (currentSubscribers == null || !currentSubscribers.removeAll(listenerMethodsForType))
        throw new IllegalArgumentException("missing event subscriber for an annotated method. Is " + listener + " registered?"); 
    } 
  }
  
  @VisibleForTesting
  Set<Subscriber> getSubscribersForTesting(Class<?> eventType) {
    return (Set<Subscriber>)MoreObjects.firstNonNull(this.subscribers.get(eventType), ImmutableSet.of());
  }
  
  Iterator<Subscriber> getSubscribers(Object event) {
    ImmutableSet<Class<?>> eventTypes = flattenHierarchy(event.getClass());
    List<Iterator<Subscriber>> subscriberIterators = Lists.newArrayListWithCapacity(eventTypes.size());
    for (UnmodifiableIterator<Class<?>> unmodifiableIterator = eventTypes.iterator(); unmodifiableIterator.hasNext(); ) {
      Class<?> eventType = unmodifiableIterator.next();
      CopyOnWriteArraySet<Subscriber> eventSubscribers = this.subscribers.get(eventType);
      if (eventSubscribers != null)
        subscriberIterators.add(eventSubscribers.iterator()); 
    } 
    return Iterators.concat(subscriberIterators.iterator());
  }
  
  private static final LoadingCache<Class<?>, ImmutableList<Method>> subscriberMethodsCache = CacheBuilder.newBuilder()
    .weakKeys()
    .build(new CacheLoader<Class<?>, ImmutableList<Method>>() {
        public ImmutableList<Method> load(Class<?> concreteClass) throws Exception {
          return SubscriberRegistry.getAnnotatedMethodsNotCached(concreteClass);
        }
      });
  
  private Multimap<Class<?>, Subscriber> findAllSubscribers(Object listener) {
    HashMultimap hashMultimap = HashMultimap.create();
    Class<?> clazz = listener.getClass();
    for (UnmodifiableIterator<Method> unmodifiableIterator = getAnnotatedMethods(clazz).iterator(); unmodifiableIterator.hasNext(); ) {
      Method method = unmodifiableIterator.next();
      Class<?>[] parameterTypes = method.getParameterTypes();
      Class<?> eventType = parameterTypes[0];
      hashMultimap.put(eventType, Subscriber.create(this.bus, listener, method));
    } 
    return (Multimap<Class<?>, Subscriber>)hashMultimap;
  }
  
  private static ImmutableList<Method> getAnnotatedMethods(Class<?> clazz) {
    try {
      return (ImmutableList<Method>)subscriberMethodsCache.getUnchecked(clazz);
    } catch (UncheckedExecutionException e) {
      Throwables.throwIfUnchecked(e.getCause());
      throw e;
    } 
  }
  
  private static ImmutableList<Method> getAnnotatedMethodsNotCached(Class<?> clazz) {
    Set<? extends Class<?>> supertypes = TypeToken.of(clazz).getTypes().rawTypes();
    Map<MethodIdentifier, Method> identifiers = Maps.newHashMap();
    for (Class<?> supertype : supertypes) {
      for (Method method : supertype.getDeclaredMethods()) {
        if (method.isAnnotationPresent((Class)Subscribe.class) && !method.isSynthetic()) {
          Class<?>[] parameterTypes = method.getParameterTypes();
          Preconditions.checkArgument((parameterTypes.length == 1), "Method %s has @Subscribe annotation but has %s parameters. Subscriber methods must have exactly 1 parameter.", method, parameterTypes.length);
          Preconditions.checkArgument(
              !parameterTypes[0].isPrimitive(), "@Subscribe method %s's parameter is %s. Subscriber methods cannot accept primitives. Consider changing the parameter to %s.", method, parameterTypes[0]
              
              .getName(), 
              Primitives.wrap(parameterTypes[0]).getSimpleName());
          MethodIdentifier ident = new MethodIdentifier(method);
          if (!identifiers.containsKey(ident))
            identifiers.put(ident, method); 
        } 
      } 
    } 
    return ImmutableList.copyOf(identifiers.values());
  }
  
  private static final LoadingCache<Class<?>, ImmutableSet<Class<?>>> flattenHierarchyCache = CacheBuilder.newBuilder()
    .weakKeys()
    .build(new CacheLoader<Class<?>, ImmutableSet<Class<?>>>() {
        public ImmutableSet<Class<?>> load(Class<?> concreteClass) {
          return ImmutableSet.copyOf(
              TypeToken.of(concreteClass).getTypes().rawTypes());
        }
      });
  
  @VisibleForTesting
  static ImmutableSet<Class<?>> flattenHierarchy(Class<?> concreteClass) {
    try {
      return (ImmutableSet<Class<?>>)flattenHierarchyCache.getUnchecked(concreteClass);
    } catch (UncheckedExecutionException e) {
      throw Throwables.propagate(e.getCause());
    } 
  }
  
  private static final class MethodIdentifier {
    private final String name;
    
    private final List<Class<?>> parameterTypes;
    
    MethodIdentifier(Method method) {
      this.name = method.getName();
      this.parameterTypes = Arrays.asList(method.getParameterTypes());
    }
    
    public int hashCode() {
      return Objects.hashCode(new Object[] { this.name, this.parameterTypes });
    }
    
    public boolean equals(@CheckForNull Object o) {
      if (o instanceof MethodIdentifier) {
        MethodIdentifier ident = (MethodIdentifier)o;
        return (this.name.equals(ident.name) && this.parameterTypes.equals(ident.parameterTypes));
      } 
      return false;
    }
  }
}
