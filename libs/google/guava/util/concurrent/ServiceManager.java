package me.syncwrld.booter.libs.google.guava.util.concurrent;

import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import me.syncwrld.booter.libs.google.errorprone.annotations.CanIgnoreReturnValue;
import me.syncwrld.booter.libs.google.errorprone.annotations.concurrent.GuardedBy;
import me.syncwrld.booter.libs.google.guava.annotations.GwtIncompatible;
import me.syncwrld.booter.libs.google.guava.annotations.J2ktIncompatible;
import me.syncwrld.booter.libs.google.guava.base.Function;
import me.syncwrld.booter.libs.google.guava.base.MoreObjects;
import me.syncwrld.booter.libs.google.guava.base.Preconditions;
import me.syncwrld.booter.libs.google.guava.base.Predicates;
import me.syncwrld.booter.libs.google.guava.base.Stopwatch;
import me.syncwrld.booter.libs.google.guava.collect.Collections2;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableCollection;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableList;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMap;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableMultimap;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableSet;
import me.syncwrld.booter.libs.google.guava.collect.ImmutableSetMultimap;
import me.syncwrld.booter.libs.google.guava.collect.Lists;
import me.syncwrld.booter.libs.google.guava.collect.Maps;
import me.syncwrld.booter.libs.google.guava.collect.MultimapBuilder;
import me.syncwrld.booter.libs.google.guava.collect.Multimaps;
import me.syncwrld.booter.libs.google.guava.collect.Multiset;
import me.syncwrld.booter.libs.google.guava.collect.Ordering;
import me.syncwrld.booter.libs.google.guava.collect.SetMultimap;
import me.syncwrld.booter.libs.google.guava.collect.UnmodifiableIterator;

@ElementTypesAreNonnullByDefault
@J2ktIncompatible
@GwtIncompatible
public final class ServiceManager implements ServiceManagerBridge {
  private static final LazyLogger logger = new LazyLogger(ServiceManager.class);
  
  private static final ListenerCallQueue.Event<Listener> HEALTHY_EVENT = new ListenerCallQueue.Event<Listener>() {
      public void call(ServiceManager.Listener listener) {
        listener.healthy();
      }
      
      public String toString() {
        return "healthy()";
      }
    };
  
  private static final ListenerCallQueue.Event<Listener> STOPPED_EVENT = new ListenerCallQueue.Event<Listener>() {
      public void call(ServiceManager.Listener listener) {
        listener.stopped();
      }
      
      public String toString() {
        return "stopped()";
      }
    };
  
  private final ServiceManagerState state;
  
  private final ImmutableList<Service> services;
  
  public static abstract class Listener {
    public void healthy() {}
    
    public void stopped() {}
    
    public void failure(Service service) {}
  }
  
  public ServiceManager(Iterable<? extends Service> services) {
    ImmutableList<Service> copy = ImmutableList.copyOf(services);
    if (copy.isEmpty()) {
      logger
        .get()
        .log(Level.WARNING, "ServiceManager configured with no services.  Is your application configured properly?", new EmptyServiceManagerWarning());
      copy = ImmutableList.of(new NoOpService());
    } 
    this.state = new ServiceManagerState((ImmutableCollection<Service>)copy);
    this.services = copy;
    WeakReference<ServiceManagerState> stateReference = new WeakReference<>(this.state);
    for (UnmodifiableIterator<Service> unmodifiableIterator = copy.iterator(); unmodifiableIterator.hasNext(); ) {
      Service service = unmodifiableIterator.next();
      service.addListener(new ServiceListener(service, stateReference), MoreExecutors.directExecutor());
      Preconditions.checkArgument((service.state() == Service.State.NEW), "Can only manage NEW services, %s", service);
    } 
    this.state.markReady();
  }
  
  public void addListener(Listener listener, Executor executor) {
    this.state.addListener(listener, executor);
  }
  
  @CanIgnoreReturnValue
  public ServiceManager startAsync() {
    UnmodifiableIterator<Service> unmodifiableIterator;
    for (unmodifiableIterator = this.services.iterator(); unmodifiableIterator.hasNext(); ) {
      Service service = unmodifiableIterator.next();
      Preconditions.checkState((service.state() == Service.State.NEW), "Not all services are NEW, cannot start %s", this);
    } 
    for (unmodifiableIterator = this.services.iterator(); unmodifiableIterator.hasNext(); ) {
      Service service = unmodifiableIterator.next();
      try {
        this.state.tryStartTiming(service);
        service.startAsync();
      } catch (IllegalStateException e) {
        logger.get().log(Level.WARNING, "Unable to start Service " + service, e);
      } 
    } 
    return this;
  }
  
  public void awaitHealthy() {
    this.state.awaitHealthy();
  }
  
  public void awaitHealthy(Duration timeout) throws TimeoutException {
    awaitHealthy(Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  public void awaitHealthy(long timeout, TimeUnit unit) throws TimeoutException {
    this.state.awaitHealthy(timeout, unit);
  }
  
  @CanIgnoreReturnValue
  public ServiceManager stopAsync() {
    for (UnmodifiableIterator<Service> unmodifiableIterator = this.services.iterator(); unmodifiableIterator.hasNext(); ) {
      Service service = unmodifiableIterator.next();
      service.stopAsync();
    } 
    return this;
  }
  
  public void awaitStopped() {
    this.state.awaitStopped();
  }
  
  public void awaitStopped(Duration timeout) throws TimeoutException {
    awaitStopped(Internal.toNanosSaturated(timeout), TimeUnit.NANOSECONDS);
  }
  
  public void awaitStopped(long timeout, TimeUnit unit) throws TimeoutException {
    this.state.awaitStopped(timeout, unit);
  }
  
  public boolean isHealthy() {
    for (UnmodifiableIterator<Service> unmodifiableIterator = this.services.iterator(); unmodifiableIterator.hasNext(); ) {
      Service service = unmodifiableIterator.next();
      if (!service.isRunning())
        return false; 
    } 
    return true;
  }
  
  public ImmutableSetMultimap<Service.State, Service> servicesByState() {
    return this.state.servicesByState();
  }
  
  public ImmutableMap<Service, Long> startupTimes() {
    return this.state.startupTimes();
  }
  
  public ImmutableMap<Service, Duration> startupDurations() {
    return ImmutableMap.copyOf(
        Maps.transformValues((Map)startupTimes(), Duration::ofMillis));
  }
  
  public String toString() {
    return MoreObjects.toStringHelper(ServiceManager.class)
      .add("services", Collections2.filter((Collection)this.services, Predicates.not(Predicates.instanceOf(NoOpService.class))))
      .toString();
  }
  
  private static final class ServiceManagerState {
    final Monitor monitor = new Monitor();
    
    @GuardedBy("monitor")
    final SetMultimap<Service.State, Service> servicesByState = MultimapBuilder.enumKeys(Service.State.class).linkedHashSetValues().build();
    
    @GuardedBy("monitor")
    final Multiset<Service.State> states = this.servicesByState
      .keys();
    
    @GuardedBy("monitor")
    final Map<Service, Stopwatch> startupTimers = Maps.newIdentityHashMap();
    
    @GuardedBy("monitor")
    boolean ready;
    
    @GuardedBy("monitor")
    boolean transitioned;
    
    final int numberOfServices;
    
    final Monitor.Guard awaitHealthGuard = new AwaitHealthGuard();
    
    final class AwaitHealthGuard extends Monitor.Guard {
      @GuardedBy("ServiceManagerState.this.monitor")
      public boolean isSatisfied() {
        return (ServiceManager.ServiceManagerState.this.states.count(Service.State.RUNNING) == ServiceManager.ServiceManagerState.this.numberOfServices || ServiceManager.ServiceManagerState.this.states
          .contains(Service.State.STOPPING) || ServiceManager.ServiceManagerState.this.states
          .contains(Service.State.TERMINATED) || ServiceManager.ServiceManagerState.this.states
          .contains(Service.State.FAILED));
      }
    }
    
    final Monitor.Guard stoppedGuard = new StoppedGuard();
    
    final class StoppedGuard extends Monitor.Guard {
      @GuardedBy("ServiceManagerState.this.monitor")
      public boolean isSatisfied() {
        return (ServiceManager.ServiceManagerState.this.states.count(Service.State.TERMINATED) + ServiceManager.ServiceManagerState.this.states.count(Service.State.FAILED) == ServiceManager.ServiceManagerState.this.numberOfServices);
      }
    }
    
    final ListenerCallQueue<ServiceManager.Listener> listeners = new ListenerCallQueue<>();
    
    ServiceManagerState(ImmutableCollection<Service> services) {
      this.numberOfServices = services.size();
      this.servicesByState.putAll(Service.State.NEW, (Iterable)services);
    }
    
    void tryStartTiming(Service service) {
      this.monitor.enter();
      try {
        Stopwatch stopwatch = this.startupTimers.get(service);
        if (stopwatch == null)
          this.startupTimers.put(service, Stopwatch.createStarted()); 
      } finally {
        this.monitor.leave();
      } 
    }
    
    void markReady() {
      this.monitor.enter();
      try {
        if (!this.transitioned) {
          this.ready = true;
        } else {
          List<Service> servicesInBadStates = Lists.newArrayList();
          for (UnmodifiableIterator<Service> unmodifiableIterator = servicesByState().values().iterator(); unmodifiableIterator.hasNext(); ) {
            Service service = unmodifiableIterator.next();
            if (service.state() != Service.State.NEW)
              servicesInBadStates.add(service); 
          } 
          throw new IllegalArgumentException("Services started transitioning asynchronously before the ServiceManager was constructed: " + servicesInBadStates);
        } 
      } finally {
        this.monitor.leave();
      } 
    }
    
    void addListener(ServiceManager.Listener listener, Executor executor) {
      this.listeners.addListener(listener, executor);
    }
    
    void awaitHealthy() {
      this.monitor.enterWhenUninterruptibly(this.awaitHealthGuard);
      try {
        checkHealthy();
      } finally {
        this.monitor.leave();
      } 
    }
    
    void awaitHealthy(long timeout, TimeUnit unit) throws TimeoutException {
      this.monitor.enter();
      try {
        if (!this.monitor.waitForUninterruptibly(this.awaitHealthGuard, timeout, unit))
          throw new TimeoutException("Timeout waiting for the services to become healthy. The following services have not started: " + 
              
              Multimaps.filterKeys(this.servicesByState, Predicates.in(ImmutableSet.of(Service.State.NEW, Service.State.STARTING)))); 
        checkHealthy();
      } finally {
        this.monitor.leave();
      } 
    }
    
    void awaitStopped() {
      this.monitor.enterWhenUninterruptibly(this.stoppedGuard);
      this.monitor.leave();
    }
    
    void awaitStopped(long timeout, TimeUnit unit) throws TimeoutException {
      this.monitor.enter();
      try {
        if (!this.monitor.waitForUninterruptibly(this.stoppedGuard, timeout, unit))
          throw new TimeoutException("Timeout waiting for the services to stop. The following services have not stopped: " + 
              
              Multimaps.filterKeys(this.servicesByState, Predicates.not(Predicates.in(EnumSet.of(Service.State.TERMINATED, Service.State.FAILED))))); 
      } finally {
        this.monitor.leave();
      } 
    }
    
    ImmutableSetMultimap<Service.State, Service> servicesByState() {
      ImmutableSetMultimap.Builder<Service.State, Service> builder = ImmutableSetMultimap.builder();
      this.monitor.enter();
      try {
        for (Map.Entry<Service.State, Service> entry : (Iterable<Map.Entry<Service.State, Service>>)this.servicesByState.entries()) {
          if (!(entry.getValue() instanceof ServiceManager.NoOpService))
            builder.put(entry); 
        } 
      } finally {
        this.monitor.leave();
      } 
      return builder.build();
    }
    
    ImmutableMap<Service, Long> startupTimes() {
      List<Map.Entry<Service, Long>> loadTimes;
      this.monitor.enter();
      try {
        loadTimes = Lists.newArrayListWithCapacity(this.startupTimers.size());
        for (Map.Entry<Service, Stopwatch> entry : this.startupTimers.entrySet()) {
          Service service = entry.getKey();
          Stopwatch stopwatch = entry.getValue();
          if (!stopwatch.isRunning() && !(service instanceof ServiceManager.NoOpService))
            loadTimes.add(Maps.immutableEntry(service, Long.valueOf(stopwatch.elapsed(TimeUnit.MILLISECONDS)))); 
        } 
      } finally {
        this.monitor.leave();
      } 
      Collections.sort(loadTimes, 
          
          (Comparator<? super Map.Entry<Service, Long>>)Ordering.natural()
          .onResultOf(new Function<Map.Entry<Service, Long>, Long>(this) {
              public Long apply(Map.Entry<Service, Long> input) {
                return input.getValue();
              }
            }));
      return ImmutableMap.copyOf(loadTimes);
    }
    
    void transitionService(Service service, Service.State from, Service.State to) {
      Preconditions.checkNotNull(service);
      Preconditions.checkArgument((from != to));
      this.monitor.enter();
      try {
        this.transitioned = true;
        if (!this.ready)
          return; 
        Preconditions.checkState(this.servicesByState
            .remove(from, service), "Service %s not at the expected location in the state map %s", service, from);
        Preconditions.checkState(this.servicesByState
            .put(to, service), "Service %s in the state map unexpectedly at %s", service, to);
        Stopwatch stopwatch = this.startupTimers.get(service);
        if (stopwatch == null) {
          stopwatch = Stopwatch.createStarted();
          this.startupTimers.put(service, stopwatch);
        } 
        if (to.compareTo(Service.State.RUNNING) >= 0 && stopwatch.isRunning()) {
          stopwatch.stop();
          if (!(service instanceof ServiceManager.NoOpService))
            ServiceManager.logger.get().log(Level.FINE, "Started {0} in {1}.", new Object[] { service, stopwatch }); 
        } 
        if (to == Service.State.FAILED)
          enqueueFailedEvent(service); 
        if (this.states.count(Service.State.RUNNING) == this.numberOfServices) {
          enqueueHealthyEvent();
        } else if (this.states.count(Service.State.TERMINATED) + this.states.count(Service.State.FAILED) == this.numberOfServices) {
          enqueueStoppedEvent();
        } 
      } finally {
        this.monitor.leave();
        dispatchListenerEvents();
      } 
    }
    
    void enqueueStoppedEvent() {
      this.listeners.enqueue(ServiceManager.STOPPED_EVENT);
    }
    
    void enqueueHealthyEvent() {
      this.listeners.enqueue(ServiceManager.HEALTHY_EVENT);
    }
    
    void enqueueFailedEvent(final Service service) {
      this.listeners.enqueue(new ListenerCallQueue.Event<ServiceManager.Listener>(this) {
            public void call(ServiceManager.Listener listener) {
              listener.failure(service);
            }
            
            public String toString() {
              return "failed({service=" + service + "})";
            }
          });
    }
    
    void dispatchListenerEvents() {
      Preconditions.checkState(
          !this.monitor.isOccupiedByCurrentThread(), "It is incorrect to execute listeners with the monitor held.");
      this.listeners.dispatch();
    }
    
    @GuardedBy("monitor")
    void checkHealthy() {
      if (this.states.count(Service.State.RUNNING) != this.numberOfServices) {
        IllegalStateException exception = new IllegalStateException("Expected to be healthy after starting. The following services are not running: " + Multimaps.filterKeys(this.servicesByState, Predicates.not(Predicates.equalTo(Service.State.RUNNING))));
        for (Service service : this.servicesByState.get(Service.State.FAILED))
          exception.addSuppressed(new ServiceManager.FailedService(service)); 
        throw exception;
      } 
    }
  }
  
  private static final class ServiceListener extends Service.Listener {
    final Service service;
    
    final WeakReference<ServiceManager.ServiceManagerState> state;
    
    ServiceListener(Service service, WeakReference<ServiceManager.ServiceManagerState> state) {
      this.service = service;
      this.state = state;
    }
    
    public void starting() {
      ServiceManager.ServiceManagerState state = this.state.get();
      if (state != null) {
        state.transitionService(this.service, Service.State.NEW, Service.State.STARTING);
        if (!(this.service instanceof ServiceManager.NoOpService))
          ServiceManager.logger.get().log(Level.FINE, "Starting {0}.", this.service); 
      } 
    }
    
    public void running() {
      ServiceManager.ServiceManagerState state = this.state.get();
      if (state != null)
        state.transitionService(this.service, Service.State.STARTING, Service.State.RUNNING); 
    }
    
    public void stopping(Service.State from) {
      ServiceManager.ServiceManagerState state = this.state.get();
      if (state != null)
        state.transitionService(this.service, from, Service.State.STOPPING); 
    }
    
    public void terminated(Service.State from) {
      ServiceManager.ServiceManagerState state = this.state.get();
      if (state != null) {
        if (!(this.service instanceof ServiceManager.NoOpService))
          ServiceManager.logger
            .get()
            .log(Level.FINE, "Service {0} has terminated. Previous state was: {1}", new Object[] { this.service, from }); 
        state.transitionService(this.service, from, Service.State.TERMINATED);
      } 
    }
    
    public void failed(Service.State from, Throwable failure) {
      ServiceManager.ServiceManagerState state = this.state.get();
      if (state != null) {
        boolean log = !(this.service instanceof ServiceManager.NoOpService);
        int i = log & ((from != Service.State.STARTING) ? 1 : 0);
        if (i != 0)
          ServiceManager.logger
            .get()
            .log(Level.SEVERE, "Service " + this.service + " has failed in the " + from + " state.", failure); 
        state.transitionService(this.service, from, Service.State.FAILED);
      } 
    }
  }
  
  private static final class NoOpService extends AbstractService {
    private NoOpService() {}
    
    protected void doStart() {
      notifyStarted();
    }
    
    protected void doStop() {
      notifyStopped();
    }
  }
  
  private static final class EmptyServiceManagerWarning extends Throwable {
    private EmptyServiceManagerWarning() {}
  }
  
  private static final class FailedService extends Throwable {
    FailedService(Service service) {
      super(service
          .toString(), service
          .failureCause(), false, false);
    }
  }
}
