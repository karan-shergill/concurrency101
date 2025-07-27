# Executors Utility Class & Thread Pool Types in Java

- [Overview 🚀](#overview-)
  - [Key Concepts Covered:](#key-concepts-covered)
- [What are the Different Executor Types? 🤔](#what-are-the-different-executor-types-)
  - [Fixed Thread Pool](#fixed-thread-pool)
  - [Cached Thread Pool](#cached-thread-pool)
  - [Single Thread Executor](#single-thread-executor)
  - [ForkJoinPool](#forkjoinpool)
  - [Work Stealing Pool](#work-stealing-pool)
- [1. Fixed Thread Pool Executor 🔧](#1-fixed-thread-pool-executor-)
  - [Basic Fixed Thread Pool Usage](#basic-fixed-thread-pool-usage)
  - [Using submit() with Future 📋](#using-submit-with-future-)
  - [Exception Handling in Thread Pool 🛡️](#exception-handling-in-thread-pool-)
  - [Proper Executor Shutdown 🔚](#proper-executor-shutdown-)
- [2. Cached Thread Pool Executor ⚡](#2-cached-thread-pool-executor-)
  - [Basic Cached Thread Pool Behavior](#basic-cached-thread-pool-behavior)
  - [Thread Creation and Reuse Patterns 🔄](#thread-creation-and-reuse-patterns-)
  - [Burst Workload Handling 🌊](#burst-workload-handling-)
  - [Potential Problems with Cached Thread Pools ⚠️](#potential-problems-with-cached-thread-pools-)
- [3. Single Thread Executor 1️⃣](#3-single-thread-executor-1)
  - [Basic Single Thread Execution](#basic-single-thread-execution)
  - [Task Ordering Guarantees 📋](#task-ordering-guarantees-)
  - [Thread Safety Without Synchronization 🔒](#thread-safety-without-synchronization-)
  - [Real-world Use Cases 🌍](#real-world-use-cases-)
    - [Use Case 1: Sequential File Processing](#use-case-1-sequential-file-processing)
    - [Use Case 2: Asynchronous Logging System](#use-case-2-asynchronous-logging-system)
- [4. ForkJoinPool ⚡](#4-forkjoinpool-)
  - [Basic ForkJoinPool Concepts](#basic-forkjoinpool-concepts)
  - [RecursiveTask - Parallel Sum Calculation 🧮](#recursivetask---parallel-sum-calculation-)
  - [RecursiveAction - Parallel Array Processing 🔄](#recursiveaction---parallel-array-processing-)
  - [Work-Stealing Demonstration 🏃‍♂️](#work-stealing-demonstration-)
- [5. Work Stealing Pool 🔄](#5-work-stealing-pool-)
  - [Basic Work Stealing Pool Concepts](#basic-work-stealing-pool-concepts)
  - [Work Stealing in Action with Uneven Workloads ⚖️](#work-stealing-in-action-with-uneven-workloads-)
  - [CPU-bound vs I/O-bound Tasks 💻](#cpu-bound-vs-io-bound-tasks-)
  - [Integration with CompletableFuture 🌟](#integration-with-completablefuture-)
- [Executor Types Comparison 📊](#executor-types-comparison-)
  - [Performance Characteristics](#performance-characteristics)
  - [When to Use Each Type 🎯](#when-to-use-each-type-)
    - [Fixed Thread Pool ✅](#fixed-thread-pool-)
    - [Cached Thread Pool ✅](#cached-thread-pool-)
    - [Single Thread Executor ✅](#single-thread-executor-)
    - [ForkJoinPool ✅](#forkjoinpool-)
    - [Work Stealing Pool ✅](#work-stealing-pool-)
- [Best Practices 💡](#best-practices-)
  - [1. **Choose the Right Executor Type**](#1-choose-the-right-executor-type)
  - [2. **Always Shutdown Executors Properly**](#2-always-shutdown-executors-properly)
  - [3. **Handle Exceptions Appropriately**](#3-handle-exceptions-appropriately)
  - [4. **Monitor Thread Pool Health**](#4-monitor-thread-pool-health)
  - [5. **Size Thread Pools Appropriately**](#5-size-thread-pools-appropriately)
- [Common Pitfalls ⚠️](#common-pitfalls-)
  - [1. **Not Shutting Down Executors**](#1-not-shutting-down-executors)
  - [2. **Using Cached Thread Pool Inappropriately**](#2-using-cached-thread-pool-inappropriately)
  - [3. **Ignoring Exception Handling**](#3-ignoring-exception-handling)
  - [4. **Blocking in ForkJoinPool**](#4-blocking-in-forkjoinpool)
- [Interview Questions & Answers 🎤](#interview-questions--answers-)
  - [Q1: What are the main differences between Fixed, Cached, and Single Thread Executors?](#q1-what-are-the-main-differences-between-fixed-cached-and-single-thread-executors)
  - [Q2: When would you choose ForkJoinPool over a regular ThreadPoolExecutor?](#q2-when-would-you-choose-forkjoinpool-over-a-regular-threadpoolexecutor)
  - [Q3: What is work-stealing and how does it improve performance?](#q3-what-is-work-stealing-and-how-does-it-improve-performance)
  - [Q4: How do you properly shutdown an ExecutorService?](#q4-how-do-you-properly-shutdown-an-executorservice)
  - [Q5: What problems can occur with CachedThreadPool and how do you avoid them?](#q5-what-problems-can-occur-with-cachedthreadpool-and-how-do-you-avoid-them)
  - [Q6: How do you handle exceptions in different executor types?](#q6-how-do-you-handle-exceptions-in-different-executor-types)
  - [Q7: What's the difference between newWorkStealingPool() and ForkJoinPool?](#q7-whats-the-difference-between-newworkstealingpool-and-forkjoinpool)
  - [Q8: How do you choose the right thread pool size?](#q8-how-do-you-choose-the-right-thread-pool-size)
  - [Q9: When should you use single thread executor vs synchronized blocks?](#q9-when-should-you-use-single-thread-executor-vs-synchronized-blocks)
  - [Q10: What are the performance implications of different queue types in thread pools?](#q10-what-are-the-performance-implications-of-different-queue-types-in-thread-pools)

## Overview 🚀

This package demonstrates the powerful `Executors` utility class and the different types of thread pools available in Java. The Executors class provides convenient factory methods for creating various ExecutorService implementations, each optimized for different use cases and workload patterns.

### Key Concepts Covered:

1. **Fixed Thread Pool** - Reuses a fixed number of threads operating off a shared queue
2. **Cached Thread Pool** - Creates new threads as needed, reuses existing ones  
3. **Single Thread Executor** - Uses exactly one thread for sequential task execution
4. **ForkJoinPool** - Designed for divide-and-conquer algorithms with work-stealing
5. **Work Stealing Pool** - Simplified ForkJoinPool for uneven workloads
6. **Performance Characteristics** - When to use each type and trade-offs
7. **Best Practices** - Proper usage, shutdown procedures, and common pitfalls

---

## What are the Different Executor Types? 🤔

### Fixed Thread Pool
- **Fixed number of threads** (specified at creation)
- **Shared work queue** (unbounded LinkedBlockingQueue)
- **Reuses threads** for multiple tasks
- **Queues tasks** when all threads are busy
- **Predictable resource usage**

### Cached Thread Pool  
- **Creates threads as needed** (no upper limit!)
- **Reuses existing threads** when available
- **60-second timeout** for idle threads
- **SynchronousQueue** (no task queuing)
- **Handles bursts well** but can create too many threads

### Single Thread Executor
- **Exactly one thread** for all tasks
- **Sequential execution** with ordering guarantees
- **Unbounded queue** for pending tasks
- **Implicit thread safety** for shared state
- **Perfect for ordered processing**

### ForkJoinPool
- **Divide-and-conquer** algorithms
- **Work-stealing** between threads
- **RecursiveTask/RecursiveAction** for recursive algorithms
- **Optimized for CPU-intensive** parallel computations
- **Best for tree-like task structures**

### Work Stealing Pool
- **Simplified ForkJoinPool** with easier API
- **Excellent load balancing** for uneven workloads
- **Automatic work distribution** between threads
- **Great for heterogeneous tasks**
- **Integrates well with CompletableFuture**

---

## 1. Fixed Thread Pool Executor 🔧
**File:** `a_SimpleFixedThreadPoolExecutor.java`

### Basic Fixed Thread Pool Usage

```java
// Create a fixed thread pool with 3 threads
ExecutorService executor = Executors.newFixedThreadPool(3);

try {
    // Submit 8 tasks to demonstrate queuing behavior
    for (int i = 1; i <= 8; i++) {
        final int taskId = i;
        
        // Using execute() - fire and forget, no return value
        executor.execute(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.printf("Task %d started on thread: %s%n", taskId, threadName);
            
            Thread.sleep(2000); // Simulate work
            System.out.printf("Task %d completed on thread: %s%n", taskId, threadName);
        });
    }
    
    System.out.println("All tasks submitted. Notice only 3 run simultaneously.");
    
} finally {
    // Proper shutdown procedure
    shutdownExecutorGracefully(executor);
}
```

### Using submit() with Future 📋

```java
ExecutorService executor = Executors.newFixedThreadPool(2);

try {
    // Submit tasks that return values using Callable
    Future<String> future1 = executor.submit(() -> {
        Thread.sleep(1500);
        return "Result from task 1 (thread: " + Thread.currentThread().getName() + ")";
    });
    
    Future<String> future2 = executor.submit(() -> {
        Thread.sleep(1000);
        return "Result from task 2 (thread: " + Thread.currentThread().getName() + ")";
    });
    
    Future<Integer> future3 = executor.submit(() -> {
        Thread.sleep(500);
        return 42;
    });
    
    // Get results (this will block until tasks complete)
    System.out.println("Future 1: " + future1.get());
    System.out.println("Future 2: " + future2.get());
    System.out.println("Future 3: " + future3.get());
    
} catch (InterruptedException | ExecutionException e) {
    System.err.println("Error getting future results: " + e.getMessage());
} finally {
    shutdownExecutorGracefully(executor);
}
```

### Exception Handling in Thread Pool 🛡️

```java
ExecutorService executor = Executors.newFixedThreadPool(2);

try {
    // Task that throws an exception
    Future<String> futureWithException = executor.submit(() -> {
        Thread.sleep(500);
        throw new RuntimeException("Simulated task failure!");
    });
    
    // Task that completes successfully
    Future<String> successfulFuture = executor.submit(() -> {
        Thread.sleep(1000);
        return "Success despite other task failing!";
    });
    
    try {
        // This will throw ExecutionException containing the RuntimeException
        String result1 = futureWithException.get();
    } catch (ExecutionException e) {
        System.out.println("Caught exception from failed task: " + e.getCause().getMessage());
    }
    
    // This task should still complete successfully
    String result2 = successfulFuture.get();
    System.out.println("Result 2: " + result2);
    
} catch (InterruptedException | ExecutionException e) {
    System.err.println("Unexpected error: " + e.getMessage());
} finally {
    shutdownExecutorGracefully(executor);
}
```

### Proper Executor Shutdown 🔚

```java
private static void shutdownExecutorGracefully(ExecutorService executor) {
    System.out.println("Shutting down executor...");
    
    // Step 1: Stop accepting new tasks
    executor.shutdown();
    
    try {
        // Step 2: Wait for existing tasks to complete (with timeout)
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            System.out.println("Tasks didn't finish in time, forcing shutdown...");
            
            // Step 3: Force shutdown if tasks don't complete in time
            executor.shutdownNow();
            
            // Step 4: Wait a bit more for forced shutdown
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate cleanly!");
            }
        }
    } catch (InterruptedException e) {
        // Current thread was interrupted, force shutdown
        executor.shutdownNow();
        Thread.currentThread().interrupt();
    }
    
    System.out.println("Executor shutdown complete.");
}
```

---

## 2. Cached Thread Pool Executor ⚡
**File:** `b_SimpleCachedThreadPoolExecutor.java`

### Basic Cached Thread Pool Behavior

```java
ExecutorService executor = Executors.newCachedThreadPool();

try {
    System.out.println("Submitting 5 concurrent tasks...");
    
    // Submit 5 tasks simultaneously - should create 5 threads
    for (int i = 1; i <= 5; i++) {
        final int taskId = i;
        executor.execute(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.printf("Task %d started on thread: %s%n", taskId, threadName);
            
            Thread.sleep(2000); // Simulate work
            System.out.printf("Task %d completed on thread: %s%n", taskId, threadName);
        });
    }
    
    Thread.sleep(3000);
    System.out.println("Notice: All 5 tasks ran simultaneously on different threads!");
    
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    shutdownExecutorGracefully(executor);
}
```

### Thread Creation and Reuse Patterns 🔄

```java
ExecutorService executor = Executors.newCachedThreadPool();

try {
    System.out.println("First batch: 3 tasks...");
    // First batch - should create 3 threads
    for (int i = 1; i <= 3; i++) {
        final int taskId = i;
        executor.execute(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.printf("Batch 1 - Task %d on thread: %s%n", taskId, threadName);
            sleep(1000);
        });
    }
    
    Thread.sleep(2000); // Wait for first batch to complete
    
    System.out.println("Second batch: 3 more tasks (should reuse threads)...");
    // Second batch - should reuse the same 3 threads
    for (int i = 4; i <= 6; i++) {
        final int taskId = i;
        executor.execute(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.printf("Batch 2 - Task %d on thread: %s%n", taskId, threadName);
            sleep(1000);
        });
    }
    
    Thread.sleep(2000);
    System.out.println("Notice: Second batch likely reused threads from first batch!");
    
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    shutdownExecutorGracefully(executor);
}
```

### Burst Workload Handling 🌊

```java
ExecutorService executor = Executors.newCachedThreadPool();

try {
    System.out.println("Simulating burst workload: 10 tasks submitted rapidly...");
    
    // Submit 10 tasks very quickly
    List<Future<String>> futures = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
        final int taskId = i;
        Future<String> future = executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.printf("Burst task %d started on: %s%n", taskId, threadName);
            sleep(1500); // Simulate work
            return "Task " + taskId + " result";
        });
        futures.add(future);
    }
    
    // Wait for all tasks to complete
    for (Future<String> future : futures) {
        String result = future.get();
        System.out.printf("Got result: %s%n", result);
    }
    
    System.out.println("All burst tasks completed! Cached pool handled the spike well.");
    
} catch (InterruptedException | ExecutionException e) {
    System.err.println("Error in burst demo: " + e.getMessage());
} finally {
    shutdownExecutorGracefully(executor);
}
```

### Potential Problems with Cached Thread Pools ⚠️

```java
ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

try {
    System.out.println("Problem: Unbounded Thread Creation");
    System.out.println("If you submit too many long-running tasks simultaneously:");
    
    // Simulate submitting many tasks (but keep it safe for demo)
    for (int i = 1; i <= 8; i++) {
        final int taskId = i;
        executor.execute(() -> {
            System.out.printf("Long task %d started on: %s%n", taskId, Thread.currentThread().getName());
            sleep(2000); // Simulate long-running work
        });
    }
    
    Thread.sleep(500);
    System.out.printf("Current pool size: %d threads created!%n", executor.getPoolSize());
    
    Thread.sleep(3000);
    
    System.out.println("Problem scenarios to avoid:");
    System.out.println("- Submitting thousands of tasks simultaneously");
    System.out.println("- Long-running tasks that block threads for extended periods");
    System.out.println("- This can lead to OutOfMemoryError due to too many threads!");
    
} catch (Exception e) {
    System.err.println("Error: " + e.getMessage());
} finally {
    shutdownExecutorGracefully(executor);
}
```

---

## 3. Single Thread Executor 1️⃣
**File:** `c_SimpleSingleThreadPoolExecutor.java`

### Basic Single Thread Execution

```java
ExecutorService executor = Executors.newSingleThreadExecutor();

try {
    System.out.println("Submitting 5 tasks to single thread executor...");
    
    for (int i = 1; i <= 5; i++) {
        final int taskId = i;
        executor.execute(() -> {
            String threadName = Thread.currentThread().getName();
            long threadId = Thread.currentThread().getId();
            
            System.out.printf("Task %d started on thread: %s (ID: %d)%n", 
                    taskId, threadName, threadId);
            
            sleep(1000); // Simulate work
            
            System.out.printf("Task %d completed on thread: %s (ID: %d)%n", 
                    taskId, threadName, threadId);
        });
    }
    
    Thread.sleep(6000);
    System.out.println("Notice: All tasks ran on the SAME thread, one after another!");
    
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    shutdownExecutorGracefully(executor);
}
```

### Task Ordering Guarantees 📋

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
List<String> executionOrder = Collections.synchronizedList(new ArrayList<>());

try {
    System.out.println("Submitting tasks in specific order...");
    
    // Submit tasks with different execution times
    String[] taskNames = {"FIRST", "SECOND", "THIRD", "FOURTH", "FIFTH"};
    int[] sleepTimes = {500, 200, 800, 100, 300}; // Different durations
    
    for (int i = 0; i < taskNames.length; i++) {
        final String taskName = taskNames[i];
        final int sleepTime = sleepTimes[i];
        final int taskNumber = i + 1;
        
        executor.execute(() -> {
            System.out.printf("Executing %s (task %d) - will take %d ms%n", 
                    taskName, taskNumber, sleepTime);
            
            sleep(sleepTime);
            executionOrder.add(taskName);
            
            System.out.printf("Completed %s (task %d)%n", taskName, taskNumber);
        });
    }
    
    Thread.sleep(3000);
    
    System.out.println("Execution order: " + executionOrder);
    System.out.println("Notice: Tasks executed in submission order, regardless of duration!");
    
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    shutdownExecutorGracefully(executor);
}
```

### Thread Safety Without Synchronization 🔒

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
List<String> sharedList = new ArrayList<>(); // NOT thread-safe, but OK with single thread

try {
    System.out.println("Multiple tasks modifying shared state without synchronization...");
    
    // Submit tasks that modify shared state
    for (int i = 1; i <= 5; i++) {
        final int taskId = i;
        executor.execute(() -> {
            // No synchronization needed because of single thread!
            String item = "Item-" + taskId;
            
            System.out.printf("Task %d: Adding %s to shared list%n", taskId, item);
            sharedList.add(item);
            
            sleep(500); // Simulate processing
            
            System.out.printf("Task %d: Current list size = %d%n", taskId, sharedList.size());
            System.out.printf("Task %d: List contents = %s%n", taskId, sharedList);
        });
    }
    
    Thread.sleep(3500);
    
    System.out.println("Final shared list: " + sharedList);
    System.out.println("Notice: No race conditions or data corruption - single thread is inherently safe!");
    
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    shutdownExecutorGracefully(executor);
}
```

### Real-world Use Cases 🌍

#### Use Case 1: Sequential File Processing
```java
ExecutorService executor = Executors.newSingleThreadExecutor();

try {
    String[] files = {"config.txt", "data.csv", "log.txt", "report.pdf"};
    
    System.out.println("Processing files in order (important for dependencies)...");
    
    for (String filename : files) {
        executor.execute(() -> {
            System.out.printf("Processing file: %s%n", filename);
            sleep(800); // Simulate file processing
            System.out.printf("Completed processing: %s%n", filename);
        });
    }
    
    Thread.sleep(4000);
    System.out.println("All files processed in correct order!");
    
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    shutdownExecutorGracefully(executor);
}
```

#### Use Case 2: Asynchronous Logging System
```java
ExecutorService logExecutor = Executors.newSingleThreadExecutor();

try {
    String[] logLevels = {"INFO", "DEBUG", "WARN", "ERROR", "INFO"};
    String[] messages = {
        "Application started",
        "Loading configuration", 
        "Memory usage high",
        "Database connection failed",
        "Retrying connection"
    };
    
    for (int i = 0; i < logLevels.length; i++) {
        final String level = logLevels[i];
        final String message = messages[i];
        final long timestamp = System.currentTimeMillis();
        
        // Log asynchronously without blocking main application
        logExecutor.execute(() -> {
            System.out.printf("[%s] %d: %s%n", level, timestamp, message);
            sleep(200); // Simulate writing to file
        });
        
        sleep(100); // Simulate application work
    }
    
    Thread.sleep(2000);
    System.out.println("Application continues while logging happens in background!");
    
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    shutdownExecutorGracefully(logExecutor);
}
```

---

## 4. ForkJoinPool ⚡
**File:** `d_SimpleForkJoinPool.java`

### Basic ForkJoinPool Concepts

```java
// Get the common ForkJoinPool (shared across the JVM)
ForkJoinPool commonPool = ForkJoinPool.commonPool();

System.out.println("Common ForkJoinPool info:");
System.out.println("- Parallelism level: " + commonPool.getParallelism());
System.out.println("- Pool size: " + commonPool.getPoolSize());
System.out.println("- Active thread count: " + commonPool.getActiveThreadCount());

// Create a custom ForkJoinPool
ForkJoinPool customPool = new ForkJoinPool(4); // 4 threads

// Simple task submission
Future<String> future = customPool.submit(() -> {
    return "Hello from ForkJoinPool thread: " + Thread.currentThread().getName();
});

System.out.println("Result: " + future.get());
customPool.shutdown();
```

### RecursiveTask - Parallel Sum Calculation 🧮

```java
/**
 * RecursiveTask example: Parallel Sum Calculation
 * Divides array into smaller chunks and computes sum recursively
 */
class ParallelSumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 100; // Minimum size before splitting
    private final int[] array;
    private final int start;
    private final int end;
    
    public ParallelSumTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected Long compute() {
        int length = end - start;
        
        // Base case: if task is small enough, compute directly
        if (length <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
                Math.sqrt(array[i]); // Simulate CPU work
            }
            return sum;
        }
        
        // Recursive case: split task into two subtasks
        int mid = start + length / 2;
        
        // Fork left subtask
        ParallelSumTask leftTask = new ParallelSumTask(array, start, mid);
        leftTask.fork(); // Execute asynchronously
        
        // Compute right subtask directly (work-stealing optimization)
        ParallelSumTask rightTask = new ParallelSumTask(array, mid, end);
        Long rightSum = rightTask.compute();
        
        // Join left subtask result
        Long leftSum = leftTask.join();
        
        return leftSum + rightSum;
    }
}

// Usage
int[] numbers = new int[1000];
// ... fill array with data ...

ForkJoinPool pool = new ForkJoinPool();
ParallelSumTask task = new ParallelSumTask(numbers, 0, numbers.length);
Long parallelSum = pool.invoke(task);
pool.shutdown();
```

### RecursiveAction - Parallel Array Processing 🔄

```java
/**
 * RecursiveAction example: Parallel Array Processing
 * Squares each element in the array in-place
 */
class ParallelSquareAction extends RecursiveAction {
    private static final int THRESHOLD = 4;
    private final int[] array;
    private final int start;
    private final int end;
    
    public ParallelSquareAction(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected void compute() {
        int length = end - start;
        
        // Base case: process directly if small enough
        if (length <= THRESHOLD) {
            for (int i = start; i < end; i++) {
                array[i] = array[i] * array[i];
                System.out.printf("Squared array[%d] = %d on thread %s%n", 
                        i, array[i], Thread.currentThread().getName());
            }
            return;
        }
        
        // Recursive case: split into subtasks
        int mid = start + length / 2;
        
        ParallelSquareAction leftAction = new ParallelSquareAction(array, start, mid);
        ParallelSquareAction rightAction = new ParallelSquareAction(array, mid, end);
        
        // Execute both subtasks in parallel
        invokeAll(leftAction, rightAction);
    }
}

// Usage
int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
System.out.println("Original array: " + Arrays.toString(numbers));

ForkJoinPool pool = new ForkJoinPool();
ParallelSquareAction action = new ParallelSquareAction(numbers, 0, numbers.length);
pool.invoke(action);

System.out.println("After parallel squaring: " + Arrays.toString(numbers));
pool.shutdown();
```

### Work-Stealing Demonstration 🏃‍♂️

```java
ForkJoinPool pool = new ForkJoinPool(4); // 4 worker threads

System.out.println("Creating tasks with uneven workloads to trigger work-stealing...");

// Create tasks with different amounts of work
List<Future<String>> futures = new ArrayList<>();

for (int i = 1; i <= 8; i++) {
    final int taskId = i;
    final int workAmount = i * 200; // Increasing work for each task
    
    Future<String> future = pool.submit(() -> {
        String threadName = Thread.currentThread().getName();
        System.out.printf("Task %d started on %s (work: %d ms)%n", taskId, threadName, workAmount);
        
        sleep(workAmount); // Simulate varying amounts of work
        
        System.out.printf("Task %d completed on %s%n", taskId, threadName);
        return "Task " + taskId + " result";
    });
    
    futures.add(future);
}

// Wait for all tasks
for (Future<String> future : futures) {
    future.get();
}

System.out.println("Notice: Threads that finish early will steal work from busy threads");
System.out.println("This is the work-stealing algorithm in action!");

pool.shutdown();
```

---

## 5. Work Stealing Pool 🔄
**File:** `e_SimpleWorkStealingPool.java`

### Basic Work Stealing Pool Concepts

```java
// Create work stealing pool with default parallelism
ExecutorService workStealingPool = Executors.newWorkStealingPool();

// Get the underlying ForkJoinPool to inspect properties
ForkJoinPool forkJoinPool = (ForkJoinPool) workStealingPool;

System.out.println("Work Stealing Pool Properties:");
System.out.println("- Parallelism level: " + forkJoinPool.getParallelism());
System.out.println("- Available processors: " + Runtime.getRuntime().availableProcessors());

// Create custom work stealing pool with specific parallelism
ExecutorService customPool = Executors.newWorkStealingPool(4);

// Submit simple tasks to see basic behavior
for (int i = 1; i <= 6; i++) {
    final int taskId = i;
    workStealingPool.execute(() -> {
        String threadName = Thread.currentThread().getName();
        System.out.printf("Task %d executing on: %s%n", taskId, threadName);
        sleep(1000);
        System.out.printf("Task %d completed on: %s%n", taskId, threadName);
    });
}

workStealingPool.shutdown();
customPool.shutdown();
```

### Work Stealing in Action with Uneven Workloads ⚖️

```java
ExecutorService workStealingPool = Executors.newWorkStealingPool(4);

System.out.println("Creating tasks with highly variable execution times...");
System.out.println("This will trigger work stealing as threads finish at different rates.");

// Create tasks with random execution times
Random random = new Random(42);
List<Future<String>> futures = new ArrayList<>();

for (int i = 1; i <= 12; i++) {
    final int taskId = i;
    final int executionTime = 500 + random.nextInt(2000); // 500-2500ms
    
    Future<String> future = workStealingPool.submit(() -> {
        String threadName = Thread.currentThread().getName();
        long startTime = System.currentTimeMillis();
        
        System.out.printf("Task %d started on %s (estimated time: %d ms)%n", 
                taskId, threadName, executionTime);
        
        // Simulate CPU-intensive work
        simulateCpuWork(executionTime);
        
        long actualTime = System.currentTimeMillis() - startTime;
        String result = String.format("Task %d completed on %s (actual time: %d ms)", 
                taskId, threadName, actualTime);
        System.out.println(result);
        
        return result;
    });
    
    futures.add(future);
}

// Wait for all tasks to complete
for (Future<String> future : futures) {
    future.get();
}

System.out.println("Notice: Threads that finished early automatically picked up new work!");
System.out.println("This is work stealing providing automatic load balancing.");

workStealingPool.shutdown();
```

### CPU-bound vs I/O-bound Tasks 💻

```java
ExecutorService workStealingPool = Executors.newWorkStealingPool();

// CPU-bound tasks (work stealing excels here)
System.out.println("Testing CPU-bound tasks (mathematical computations)...");

List<Future<Long>> cpuFutures = new ArrayList<>();
for (int i = 0; i < 8; i++) {
    final int taskId = i;
    Future<Long> future = workStealingPool.submit(() -> {
        String threadName = Thread.currentThread().getName();
        System.out.printf("CPU Task %d started on %s%n", taskId, threadName);
        
        // CPU-intensive calculation
        long result = calculatePrimes(50000 + taskId * 10000);
        
        System.out.printf("CPU Task %d completed on %s (found %d primes)%n", 
                taskId, threadName, result);
        return result;
    });
    cpuFutures.add(future);
}

// Wait for CPU tasks
for (Future<Long> future : cpuFutures) {
    future.get();
}

System.out.println("Work stealing is most effective for CPU-bound tasks with variable execution times.");

workStealingPool.shutdown();
```

### Integration with CompletableFuture 🌟

```java
ExecutorService workStealingPool = Executors.newWorkStealingPool();

System.out.println("Creating complex async pipeline with CompletableFuture...");

// Create a complex async computation pipeline
CompletableFuture<String> pipeline = CompletableFuture
        .supplyAsync(() -> {
            System.out.println("Step 1: Initial data fetch on " + Thread.currentThread().getName());
            sleep(500);
            return "raw-data";
        }, workStealingPool)
        .thenApplyAsync(data -> {
            System.out.println("Step 2: Data processing on " + Thread.currentThread().getName());
            sleep(300);
            return data.toUpperCase() + "-PROCESSED";
        }, workStealingPool)
        .thenApplyAsync(processedData -> {
            System.out.println("Step 3: Data transformation on " + Thread.currentThread().getName());
            sleep(200);
            return processedData + "-TRANSFORMED";
        }, workStealingPool)
        .thenApplyAsync(transformedData -> {
            System.out.println("Step 4: Final formatting on " + Thread.currentThread().getName());
            sleep(100);
            return "RESULT: " + transformedData;
        }, workStealingPool);

// Create multiple parallel pipelines
List<CompletableFuture<String>> pipelines = new ArrayList<>();
pipelines.add(pipeline);

for (int i = 2; i <= 4; i++) {
    final int pipelineId = i;
    CompletableFuture<String> parallelPipeline = CompletableFuture
            .supplyAsync(() -> {
                System.out.printf("Pipeline %d: Starting on %s%n", pipelineId, Thread.currentThread().getName());
                sleep(200 * pipelineId);
                return "data-" + pipelineId;
            }, workStealingPool)
            .thenApplyAsync(data -> {
                System.out.printf("Pipeline %d: Processing on %s%n", pipelineId, Thread.currentThread().getName());
                sleep(150);
                return "PROCESSED-" + data;
            }, workStealingPool);
    
    pipelines.add(parallelPipeline);
}

// Combine all results
CompletableFuture<String> combinedResult = CompletableFuture.allOf(
        pipelines.toArray(new CompletableFuture[0])
).thenApplyAsync(v -> {
    System.out.println("Combining results on " + Thread.currentThread().getName());
    return pipelines.stream()
            .map(CompletableFuture::join)
            .reduce("", (a, b) -> a + " | " + b);
}, workStealingPool);

String result = combinedResult.get(10, TimeUnit.SECONDS);
System.out.println("Final combined result: " + result);

workStealingPool.shutdown();
```

---

## Executor Types Comparison 📊

### Performance Characteristics

| Executor Type | Best For | Thread Count | Queue Type | Overhead |
|---------------|----------|--------------|------------|----------|
| **Fixed** | Predictable workloads | Fixed | Unbounded | Low |
| **Cached** | Burst workloads, short tasks | 0 to ∞ | SynchronousQueue | Medium |
| **Single** | Sequential processing | 1 | Unbounded | Lowest |
| **ForkJoin** | Divide-and-conquer | CPU cores | Work-stealing deques | Medium |
| **WorkStealing** | Uneven CPU workloads | CPU cores | Work-stealing deques | Medium |

### When to Use Each Type 🎯

#### Fixed Thread Pool ✅
```java
// ✅ Use for:
- Predictable, steady workloads
- Limited resource environments  
- Long-running applications
- When you know optimal thread count

ExecutorService executor = Executors.newFixedThreadPool(4);

// Examples:
- Web server request processing
- Database connection pooling
- Batch job processing
```

#### Cached Thread Pool ✅
```java
// ✅ Use for:
- Burst workloads with idle periods
- Short-lived, asynchronous tasks
- Applications with varying load

ExecutorService executor = Executors.newCachedThreadPool();

// Examples:
- Event handling systems
- Short-lived computations
- Reactive applications

// ❌ Avoid for:
- Long-running tasks
- High-volume sustained loads
- Memory-constrained environments
```

#### Single Thread Executor ✅
```java
// ✅ Use for:
- Sequential processing requirements
- Shared state without synchronization
- Ordered task execution

ExecutorService executor = Executors.newSingleThreadExecutor();

// Examples:
- Logging systems
- File processing pipelines
- State machine updates
- Event sourcing
```

#### ForkJoinPool ✅
```java
// ✅ Use for:
- Divide-and-conquer algorithms
- Recursive computations
- Tree/graph traversals

ForkJoinPool pool = new ForkJoinPool();

// Examples:
- Merge sort, quicksort
- Mathematical computations
- Parallel tree processing
- Image/signal processing
```

#### Work Stealing Pool ✅
```java
// ✅ Use for:
- Heterogeneous workloads
- Variable execution times
- Modern async workflows

ExecutorService pool = Executors.newWorkStealingPool();

// Examples:
- Parallel data processing
- CompletableFuture workflows
- Mixed computational tasks
- Load balancing scenarios
```

---

## Best Practices 💡

### 1. **Choose the Right Executor Type**
```java
// CPU-intensive, parallel algorithms
ForkJoinPool forkJoinPool = new ForkJoinPool();

// Variable workloads, modern async
ExecutorService workStealingPool = Executors.newWorkStealingPool();

// Predictable, steady workloads
ExecutorService fixedPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

// Sequential processing
ExecutorService singlePool = Executors.newSingleThreadExecutor();

// Burst workloads (use carefully)
ExecutorService cachedPool = Executors.newCachedThreadPool();
```

### 2. **Always Shutdown Executors Properly**
```java
// ✅ Proper shutdown pattern
private static void shutdownExecutorGracefully(ExecutorService executor) {
    executor.shutdown(); // Stop accepting new tasks
    
    try {
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            executor.shutdownNow(); // Force shutdown
            
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate cleanly!");
            }
        }
    } catch (InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
    }
}
```

### 3. **Handle Exceptions Appropriately**
```java
// ✅ For fire-and-forget tasks
executor.execute(() -> {
    try {
        // Task logic
    } catch (Exception e) {
        log.error("Task failed", e);
        // Handle or report error
    }
});

// ✅ For tasks with return values
Future<String> future = executor.submit(() -> {
    // Task logic that might throw
    return "result";
});

try {
    String result = future.get();
} catch (ExecutionException e) {
    Throwable cause = e.getCause();
    // Handle the actual exception
}
```

### 4. **Monitor Thread Pool Health**
```java
// ✅ Monitor ThreadPoolExecutor metrics
if (executor instanceof ThreadPoolExecutor) {
    ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;
    
    System.out.println("Pool size: " + tpe.getPoolSize());
    System.out.println("Active threads: " + tpe.getActiveCount());
    System.out.println("Queue size: " + tpe.getQueue().size());
    System.out.println("Completed tasks: " + tpe.getCompletedTaskCount());
}
```

### 5. **Size Thread Pools Appropriately**
```java
// ✅ CPU-intensive tasks
int cpuThreads = Runtime.getRuntime().availableProcessors();
ExecutorService cpuPool = Executors.newFixedThreadPool(cpuThreads);

// ✅ I/O-intensive tasks  
int ioThreads = Runtime.getRuntime().availableProcessors() * 2;
ExecutorService ioPool = Executors.newFixedThreadPool(ioThreads);

// ✅ Mixed workloads
ExecutorService mixedPool = Executors.newWorkStealingPool();
```

---

## Common Pitfalls ⚠️

### 1. **Not Shutting Down Executors**
```java
// ❌ Memory leak - executor never shuts down
public void badMethod() {
    ExecutorService executor = Executors.newFixedThreadPool(4);
    executor.execute(() -> System.out.println("Task"));
    // Missing shutdown - threads keep running!
}

// ✅ Proper cleanup
public void goodMethod() {
    ExecutorService executor = Executors.newFixedThreadPool(4);
    try {
        executor.execute(() -> System.out.println("Task"));
    } finally {
        shutdownExecutorGracefully(executor);
    }
}
```

### 2. **Using Cached Thread Pool Inappropriately**
```java
// ❌ Dangerous - can create thousands of threads
ExecutorService executor = Executors.newCachedThreadPool();
for (int i = 0; i < 10000; i++) {
    executor.execute(() -> {
        sleep(60000); // Long-running task
    });
}
// This can cause OutOfMemoryError!

// ✅ Better for long-running tasks
ExecutorService executor = Executors.newFixedThreadPool(10);
```

### 3. **Ignoring Exception Handling**
```java
// ❌ Silent failures
executor.execute(() -> {
    throw new RuntimeException("This will be silently ignored!");
});

// ✅ Proper exception handling
executor.execute(() -> {
    try {
        // Task logic
    } catch (Exception e) {
        log.error("Task failed", e);
    }
});
```

### 4. **Blocking in ForkJoinPool**
```java
// ❌ Don't block in ForkJoinPool tasks
forkJoinPool.submit(() -> {
    Thread.sleep(5000); // Blocks worker thread!
    return "result";
});

// ✅ Use appropriate executor for blocking operations
ExecutorService blockingPool = Executors.newFixedThreadPool(10);
blockingPool.submit(() -> {
    Thread.sleep(5000); // OK in regular thread pool
    return "result";
});
```

---

## Interview Questions & Answers 🎤

### Q1: What are the main differences between Fixed, Cached, and Single Thread Executors?
**Answer:**
- **Fixed Thread Pool**: Uses a fixed number of threads with an unbounded queue. Predictable resource usage, good for steady workloads.
- **Cached Thread Pool**: Creates threads as needed, reuses existing ones. No queue, 60-second timeout for idle threads. Good for burst workloads but can create unlimited threads.
- **Single Thread Executor**: Uses exactly one thread with an unbounded queue. Guarantees sequential execution and task ordering. Perfect for scenarios requiring ordered processing.

### Q2: When would you choose ForkJoinPool over a regular ThreadPoolExecutor?
**Answer:**
Choose **ForkJoinPool** when:
- Implementing divide-and-conquer algorithms (merge sort, quick sort)
- Working with recursive task structures
- Need work-stealing for load balancing
- Processing tree or graph structures in parallel
- Implementing mathematical computations that can be subdivided

Choose **ThreadPoolExecutor** for:
- I/O-bound tasks
- Simple parallel execution without recursion
- Tasks requiring strict resource limits
- When you need specific queue types or rejection policies

### Q3: What is work-stealing and how does it improve performance?
**Answer:**
**Work-stealing** is an algorithm where idle threads automatically "steal" work from busy threads' queues:
- Each thread maintains its own work deque (double-ended queue)
- Threads add new tasks to the head of their own deque
- When a thread finishes its work, it tries to steal from the tail of other threads' deques
- This provides automatic load balancing without central coordination
- Improves performance by reducing idle time and distributing work evenly

### Q4: How do you properly shutdown an ExecutorService?
**Answer:**
```java
private void shutdownExecutorGracefully(ExecutorService executor) {
    // 1. Stop accepting new tasks
    executor.shutdown();
    
    try {
        // 2. Wait for existing tasks to complete
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            // 3. Force shutdown if timeout
            executor.shutdownNow();
            
            // 4. Wait briefly for forced shutdown
            if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate cleanly!");
            }
        }
    } catch (InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();
    }
}
```

### Q5: What problems can occur with CachedThreadPool and how do you avoid them?
**Answer:**
**Problems:**
- **Unbounded thread creation**: Can create thousands of threads leading to OutOfMemoryError
- **Resource exhaustion**: Each thread consumes memory (typically 1MB stack space)
- **Context switching overhead**: Too many threads can hurt performance

**Solutions:**
- Use Fixed or Work Stealing pools for long-running tasks
- Monitor thread count in production
- Implement circuit breakers for task submission
- Use bounded queues and rejection policies for control

### Q6: How do you handle exceptions in different executor types?
**Answer:**
```java
// For execute() - exceptions are sent to UncaughtExceptionHandler
executor.execute(() -> {
    try {
        // Task logic
    } catch (Exception e) {
        log.error("Task failed", e);
    }
});

// For submit() - exceptions are available via Future.get()
Future<String> future = executor.submit(() -> {
    if (someCondition) {
        throw new RuntimeException("Task failed");
    }
    return "success";
});

try {
    String result = future.get();
} catch (ExecutionException e) {
    Throwable cause = e.getCause(); // Original exception
    log.error("Task failed", cause);
}
```

### Q7: What's the difference between newWorkStealingPool() and ForkJoinPool?
**Answer:**
- **newWorkStealingPool()**: Simplified factory method that creates a ForkJoinPool in async mode
- **ForkJoinPool**: Direct instantiation with more control over configuration

```java
// These are equivalent:
ExecutorService workStealing = Executors.newWorkStealingPool();
ForkJoinPool forkJoin = new ForkJoinPool(
    Runtime.getRuntime().availableProcessors(),
    ForkJoinPool.defaultForkJoinWorkerThreadFactory,
    null,
    true  // asyncMode = true
);
```

**Work Stealing Pool** is easier to use with regular Runnable/Callable tasks, while **ForkJoinPool** is better for RecursiveTask/RecursiveAction.

### Q8: How do you choose the right thread pool size?
**Answer:**
**For CPU-intensive tasks:**
```java
int cpuThreads = Runtime.getRuntime().availableProcessors();
```

**For I/O-intensive tasks:**
```java
int ioThreads = Runtime.getRuntime().availableProcessors() * 2;
// Or use the formula: threads = cores / (1 - blocking_factor)
```

**For mixed workloads:**
- Start with work stealing pool for automatic load balancing
- Monitor and adjust based on actual performance
- Consider separate pools for different task types

### Q9: When should you use single thread executor vs synchronized blocks?
**Answer:**
**Use Single Thread Executor when:**
- Tasks are asynchronous and can be queued
- You want non-blocking submission of ordered tasks
- Background processing is acceptable
- Tasks are independent but need sequential execution

**Use Synchronized blocks when:**
- You need immediate, synchronous execution
- Multiple threads need to coordinate on shared state
- Fine-grained control over critical sections is required
- Response time is critical

### Q10: What are the performance implications of different queue types in thread pools?
**Answer:**
**LinkedBlockingQueue (default for Fixed Pool):**
- Unbounded by default, can cause memory issues
- FIFO ordering, good for fairness
- Higher memory overhead due to linked structure

**ArrayBlockingQueue:**
- Bounded, prevents memory issues
- Pre-allocated array, lower memory overhead
- Can block producers when full

**SynchronousQueue (used by Cached Pool):**
- No storage capacity, direct handoff
- Lower latency, but requires available threads
- Can trigger thread creation immediately

**Priority queues:**
- Allow task prioritization
- Higher overhead for maintaining order
- Useful for deadline-based scheduling 
