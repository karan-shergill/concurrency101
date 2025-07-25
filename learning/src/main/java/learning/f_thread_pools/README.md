# ThreadPoolExecutor Examples and Flow Diagrams

This directory contains comprehensive examples and visualizations of ThreadPoolExecutor lifecycle and working mechanisms.

## Code Examples

### 1. `a_SimpleThreadPoolExecutor.java`
**Fundamental concepts and configuration**
- Basic ThreadPoolExecutor usage and configuration
- Different types of pre-configured thread pools (Fixed, Cached, Single)
- Custom ThreadPoolExecutor with custom thread factory
- Task submission methods (`execute()`, `submit()`, `invokeAll()`)
- Real-time monitoring and statistics
- Rejection policies demonstration

### 2. `b_PracticalThreadPoolUsage.java`
**Real-world scenarios and best practices**
- File processing system simulation
- Web server request handling with burst traffic
- Batch processing with CompletionService
- Producer-Consumer pattern using thread pools
- Error handling and graceful shutdown

## Flow Diagrams

### 1. Task Execution Flow
Shows the complete flow of how tasks are processed:
- Task submission → Core thread availability check
- Queue management when core threads are busy
- Non-core thread creation when queue is full
- Rejection policy application when capacity is exceeded
- Thread lifecycle management

### 2. ThreadPoolExecutor Lifecycle States
Demonstrates the state transitions:
- **RUNNING**: Accepts tasks and processes queued tasks
- **SHUTDOWN**: No new tasks, but processes existing tasks
- **STOP**: No new tasks, interrupts running tasks
- **TIDYING**: All tasks terminated, about to run terminated()
- **TERMINATED**: Fully shut down

### 3. Thread Management Flow
Illustrates internal thread management:
- Core thread creation and management
- Non-core thread lifecycle with keep-alive timeout
- Queue polling mechanism
- Thread termination conditions

### 4. Rejection Policies Flow
Shows different rejection handling strategies:
- **CallerRunsPolicy**: Execute task in calling thread
- **AbortPolicy**: Throw RejectedExecutionException
- **DiscardPolicy**: Silently discard the task
- **DiscardOldestPolicy**: Remove oldest task and add new one

### 5. Complete Sequence Diagram
Timeline showing the complete lifecycle from creation to termination:
- ThreadPoolExecutor initialization
- Normal operation phases
- Queue full scenarios
- Thread creation and termination
- Shutdown procedures

## Architecture Diagram (`ThreadPoolExecutor_Architecture.svg`)

A comprehensive visual showing:
- **Configuration Parameters**: Core/max pool size, keep-alive time, queue capacity
- **Core Components**: Work queue, thread factory, rejection policies
- **Thread States**: Running, idle, terminated threads with real-time status
- **Task Flow**: Step-by-step execution process
- **Lifecycle States**: All five states with transitions
- **Monitoring Data**: Real-time statistics and metrics

## Key Concepts Illustrated

### Thread Pool Configuration
```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    corePoolSize,      // Minimum threads to keep alive
    maximumPoolSize,   // Maximum threads allowed
    keepAliveTime,     // How long to keep idle threads
    TimeUnit.SECONDS,  // Time unit for keep-alive
    workQueue,         // Queue for pending tasks
    threadFactory,     // Custom thread creation
    rejectionPolicy    // Overflow handling strategy
);
```

### Task Processing Logic
1. **Core threads available** → Execute immediately
2. **Core threads busy** → Add to queue
3. **Queue full** → Create non-core thread (if under max)
4. **Max threads reached** → Apply rejection policy

### Thread Lifecycle
- **Core threads**: Always alive (unless `allowCoreThreadTimeOut(true)`)
- **Non-core threads**: Terminated after keep-alive timeout when idle
- **Worker threads**: Poll queue for tasks, execute, then return to polling

### Best Practices Demonstrated
- Proper thread pool sizing for different workload types
- Graceful shutdown procedures
- Error handling and task failure recovery
- Monitoring and performance tuning
- Custom thread factories for debugging
- Appropriate rejection policies for different scenarios

## Running the Examples

```bash
# Compile the Java files
javac learning/f_thread_pools/*.java

# Run the basic concepts demo
java learning.f_thread_pools.a_SimpleThreadPoolExecutor

# Run the practical examples
java learning.f_thread_pools.b_PracticalThreadPoolUsage
```

These examples provide a complete understanding of ThreadPoolExecutor from basic concepts to production-ready implementations with comprehensive error handling and monitoring. 