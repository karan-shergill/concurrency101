# Concurrency & Multithreading
**A Comprehensive Guide to Java Concurrency and Multithreading Fundamentals**

## 📚 Learnings

### 1. [Thread Creation](https://github.com/karan-shergill/concurrency101/blob/main/learning/src/main/java/learning/a_thread_creation/README.md)
**Foundation of Multithreading**

Learn the fundamental approaches to creating and starting threads in Java:
- **Runnable Interface**: Best practice approach for task definition
- **Thread Class Extension**: Direct thread subclassing (when appropriate)
- **Lambda Expressions**: Modern, concise thread creation
- **Thread Naming & Priority**: Thread identification and scheduling hints

**Key Files:**
- `a_LearnRunnableInterface.java` - Runnable implementation patterns
- `b_LearnExtendThread.java` - Thread class extension examples
- `c_SimpleLambdaThread.java` - Lambda-based thread creation


---

### 2. [Thread Lifecycle](https://github.com/karan-shergill/concurrency101/blob/main/learning/src/main/java/learning/b_thread_lifecycle/README.md)
**Understanding Thread States and Transitions**

Deep dive into thread states and how threads transition between them:
- **NEW**: Thread created but not started
- **RUNNABLE**: Thread executing in the JVM
- **BLOCKED**: Thread blocked waiting for monitor lock
- **WAITING**: Thread waiting indefinitely for another thread
- **TIMED_WAITING**: Thread waiting for specified period
- **TERMINATED**: Thread has completed execution

**Key Files:**
- `SimpleThreadLifecycle.java` - Demonstrates all thread states


---

### 3. [Monitor Lock (Synchronized)](https://github.com/karan-shergill/concurrency101/blob/main/learning/src/main/java/learning/c_monitor_lock/README.md)
**Java's Built-in Synchronization Mechanism**

Master Java's fundamental synchronization using synchronized keyword:
- **Synchronized Methods**: Method-level synchronization
- **Synchronized Blocks**: Fine-grained synchronization control
- **Wait/Notify/NotifyAll**: Inter-thread communication
- **Producer-Consumer Pattern**: Classic concurrency problem solution
- **Deadlock Prevention**: Best practices and pitfalls

**Key Files:**
- `a_SimpleMonitorLock.java` - Basic synchronized usage
- `b_MonitorLockTasksDemo.java` - Practical synchronization scenarios
- `c_ProducerConsumerDemo.java` - Producer-consumer implementation
- `d_ProducerConsumerAssignment.java` - Hands-on exercise
- `e_ProducerConsumerMyImplementation.java` - Solution implementation


---

### 4. [Locks](https://github.com/karan-shergill/concurrency101/blob/main/learning/src/main/java/learning/d_locks/README.md)
**Advanced Locking Mechanisms for Fine-grained Control**

Explore explicit locks for more sophisticated synchronization:
- **ReentrantLock**: Flexible alternative to synchronized with timeout and interruption
- **ReadWriteLock**: Separate read and write locks for improved performance
- **StampedLock**: Optimistic reading with lock upgrading (Java 8+)
- **Semaphore**: Controlling access to resources with permits
- **Lock Fairness**: FIFO vs non-fair locking policies
- **Try-lock Patterns**: Non-blocking lock acquisition

**Key Files:**
- `a_SimpleReentrantLock.java` - ReentrantLock usage patterns
- `b_SimpleReadWriteLock.java` - Read-write lock implementation
- `c_SimpleStampedLock.java` - Optimistic locking with StampedLock
- `d_SimpleSemaphore.java` - Resource access control with Semaphore


---

### 5. [Compare and Swap (Atomic Operations)](https://github.com/karan-shergill/concurrency101/blob/main/learning/src/main/java/learning/e_compare_and_swap/README.md)
**Lock-free Programming with Atomic Operations**

Learn high-performance, lock-free programming techniques:
- **AtomicInteger/Long/Boolean**: Thread-safe primitive operations
- **Compare-and-Swap (CAS)**: Hardware-level atomic operations
- **ABA Problem**: Understanding and preventing CAS pitfalls
- **Memory Ordering**: Happens-before relationships
- **Performance Benefits**: When to choose atomic over locks
- **Atomic Reference**: CAS operations on object references

**Key Files:**
- `a_SimpleAtomicInteger.java` - Comprehensive atomic operations demo


---

### 6. [Thread Pools](https://github.com/karan-shergill/concurrency101/blob/main/learning/src/main/java/learning/f_thread_pools/README.md)
**Efficient Thread Management and Resource Control**

Master thread pool concepts for scalable concurrent applications:
- **ThreadPoolExecutor**: Core thread pool implementation
- **Core vs Maximum Pool Size**: Thread pool sizing strategies
- **Queue Types**: LinkedBlockingQueue, ArrayBlockingQueue, SynchronousQueue
- **Rejection Policies**: Handling overload scenarios
- **Thread Pool Lifecycle**: Proper shutdown procedures
- **Task Scheduling**: Delayed and periodic task execution
- **Monitoring**: Thread pool health and performance metrics

**Key Files:**
- `a_SimpleThreadPoolExecutor.java` - Basic thread pool setup and usage
- `b_PracticalThreadPoolUsage.java` - Real-world scenarios and best practices


---

### 7. [Future, Callable & CompletableFuture](https://github.com/karan-shergill/concurrency101/blob/main/learning/src/main/java/learning/g_future_callable_completable_future/README.md)
**Asynchronous Programming and Result Handling**

Handle asynchronous computations and compose complex async workflows:
- **Future Interface**: Representing pending computation results
- **Callable vs Runnable**: Tasks that return values vs void tasks
- **CompletableFuture**: Modern async programming (Java 8+)
- **Completion Stages**: Chaining async operations
- **Exception Handling**: Dealing with failures in async code
- **Combining Futures**: AllOf, AnyOf operations
- **Custom Executors**: Using specific thread pools for async tasks

**Key Files:**
- `a_SimpleFuture.java` - Basic Future usage patterns
- `b_SimpleCallable.java` - Callable task implementation
- `c_SimpleCompletableFuture.java` - Advanced async programming


---

### 8. [Executor Utility Class](https://github.com/karan-shergill/concurrency101/blob/main/learning/src/main/java/learning/h_executor_utility_class/README.md)
**Built-in Executor Services for Common Scenarios**

Leverage Java's built-in executor implementations for different use cases:
- **FixedThreadPool**: Fixed number of threads for predictable workloads
- **CachedThreadPool**: Dynamic thread creation for variable workloads
- **SingleThreadExecutor**: Sequential task execution with thread safety
- **ScheduledThreadPool**: Time-based task scheduling
- **ForkJoinPool**: Work-stealing for divide-and-conquer algorithms
- **WorkStealingPool**: Parallel processing with work stealing (Java 8+)
- **Virtual Threads**: Project Loom integration (Java 19+)

**Key Files:**
- `a_SimpleFixedThreadPoolExecutor.java` - Fixed thread pool scenarios
- `b_SimpleCachedThreadPoolExecutor.java` - Dynamic thread pool usage
- `c_SimpleSingleThreadPoolExecutor.java` - Single-threaded execution
- `d_SimpleForkJoinPool.java` - Fork-join framework implementation
- `e_SimpleWorkStealingPool.java` - Work-stealing pool usage

**Performance Considerations:** Choosing the right executor for your use case

---

## 🛠️ Project Structure

```
learning/
├── src/main/java/learning/
│   ├── a_thread_creation/          # Thread creation fundamentals
│   ├── b_thread_lifecycle/         # Thread state management
│   ├── c_monitor_lock/             # Synchronized programming
│   ├── d_locks/                    # Explicit locking mechanisms
│   ├── e_compare_and_swap/         # Atomic operations
│   ├── f_thread_pools/             # Thread pool management
│   ├── g_future_callable_completable_future/  # Async programming
│   └── h_executor_utility_class/   # Built-in executors
├── diagrams/                       # Visual learning resources
├── pom.xml                         # Maven configuration
└── README.md                       # This comprehensive guide
```