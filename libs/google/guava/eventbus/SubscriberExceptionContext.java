package me.syncwrld.booter.libs.google.guava.eventbus;

import java.lang.reflect.Method;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;

@ElementTypesAreNonnullByDefault
public class SubscriberExceptionContext {
  private final EventBus eventBus;
  
  private final Object event;
  
  private final Object subscriber;
  
  private final Method subscriberMethod;
  
  SubscriberExceptionContext(EventBus eventBus, Object event, Object subscriber, Method subscriberMethod) {
    this.eventBus = (EventBus)Preconditions.checkNotNull(eventBus);
    this.event = Preconditions.checkNotNull(event);
    this.subscriber = Preconditions.checkNotNull(subscriber);
    this.subscriberMethod = (Method)Preconditions.checkNotNull(subscriberMethod);
  }
  
  public EventBus getEventBus() {
    return this.eventBus;
  }
  
  public Object getEvent() {
    return this.event;
  }
  
  public Object getSubscriber() {
    return this.subscriber;
  }
  
  public Method getSubscriberMethod() {
    return this.subscriberMethod;
  }
}
