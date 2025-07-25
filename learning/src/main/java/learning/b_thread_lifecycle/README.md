# Thread Lifecycle in Java 🔄

## Overview

Understanding thread lifecycle is fundamental to concurrent programming in Java. Every thread goes through various states during its execution, and understanding these states helps in debugging, monitoring, and optimizing multithreaded applications.

## Thread States (6 States) 📊

Java threads have **6 distinct states** defined in the `Thread.State` enum:

### 1. NEW 🆕
- **Definition**: Thread is created but `start()` has not been called
- **Duration**: From object creation until `start()` is called
- **Transitions**: Can only move to RUNNABLE state

### 2. RUNNABLE 🏃‍♂️
- **Definition**: Thread is executing or ready to execute
- **Sub-states**: 
  - **Running**: Currently executing on CPU
  - **Ready**: Waiting for CPU time slice
- **Transitions**: Can move to BLOCKED, WAITING, TIMED_WAITING, or TERMINATED

### 3. BLOCKED 🚫
- **Definition**: Thread is waiting to acquire a monitor lock
- **Common scenario**: Waiting to enter a `synchronized` block/method
- **Transitions**: Moves back to RUNNABLE when lock is acquired

### 4. WAITING ⏳
- **Definition**: Thread is waiting indefinitely for another thread's action
- **Caused by**: `wait()`, `join()`, `LockSupport.park()`
- **Transitions**: Back to RUNNABLE when notified/interrupted

### 5. TIMED_WAITING ⏰
- **Definition**: Thread is waiting for a specified time period
- **Caused by**: `sleep()`, `wait(timeout)`, `join(timeout)`
- **Transitions**: Back to RUNNABLE when time expires or interrupted

### 6. TERMINATED ☠️
- **Definition**: Thread has completed execution
- **Causes**: Normal completion or uncaught exception
- **Final state**: Cannot transition to any other state

## Code Example Analysis 💻

**File:** `SimpleThreadLifecycle.java`

```java
public class SimpleThreadLifecycle {
    public static void main(String[] args) throws InterruptedException {
        
        // 1. NEW STATE - Thread created but not started
        Thread myThread = new Thread(() -> {
            try {
                System.out.println("Thread is running...");
                Thread.sleep(10000); // 10 seconds sleep
                System.out.println("Thread finished work");
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted");
            }
        });
        
        System.out.println("1. After creation: " + myThread.getState()); // NEW
        
        // 2. RUNNABLE STATE - Thread started and ready to run
        myThread.start();
        System.out.println("2. After start(): " + myThread.getState()); // RUNNABLE
        
        // 3. TIMED_WAITING STATE - Thread is sleeping
        Thread.sleep(100); // Give thread time to start sleeping
        System.out.println("3. While sleeping: " + myThread.getState()); // TIMED_WAITING
        
        // 4. Wait for thread to finish
        myThread.join(); // Current thread waits for myThread
        
        // 5. TERMINATED STATE - Thread has finished
        System.out.println("4. After completion: " + myThread.getState()); // TERMINATED
    }
}
```

### State Transitions Demonstrated:
```
NEW → RUNNABLE → TIMED_WAITING → RUNNABLE → TERMINATED
```

## Visual Thread Lifecycle

![Thread Lifecycle Transitions](thread_lifecycle_transitions.svg)

## Complete State Transition Examples 🔄

### BLOCKED State Example
```java
public class BlockedStateExample {
    private static final Object lock = new Object();
    
    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(5000); // Hold lock for 5 seconds
                } catch (InterruptedException e) { }
            }
        });
        
        Thread thread2 = new Thread(() -> {
            synchronized (lock) { // Will be BLOCKED waiting for lock
                System.out.println("Got the lock!");
            }
        });
        
        thread1.start();
        Thread.sleep(100); // Let thread1 acquire lock
        thread2.start();
        Thread.sleep(100); // Let thread2 try to acquire lock
        
        System.out.println("Thread2 state: " + thread2.getState()); // BLOCKED
    }
}
```

### WAITING State Example
```java
public class WaitingStateExample {
    private static final Object lock = new Object();
    
    public static void main(String[] args) throws InterruptedException {
        Thread waiterThread = new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait(); // Thread goes to WAITING state
                } catch (InterruptedException e) { }
            }
        });
        
        waiterThread.start();
        Thread.sleep(100); // Let thread start waiting
        
        System.out.println("Waiter state: " + waiterThread.getState()); // WAITING
        
        synchronized (lock) {
            lock.notify(); // Wake up the waiting thread
        }
    }
}
```

## Thread State Monitoring 📊

### Using getState() Method
```java
// Monitor thread state
Thread thread = new Thread(() -> {
    // Some work
});

Thread.State state = thread.getState();
System.out.println("Current state: " + state);
```

### State Checking Utility
```java
public class ThreadStateMonitor {
    public static void printThreadState(Thread thread, String description) {
        System.out.printf("%s: %s%n", description, thread.getState());
    }
    
    public static boolean isThreadAlive(Thread thread) {
        Thread.State state = thread.getState();
        return state != Thread.State.NEW && state != Thread.State.TERMINATED;
    }
}
```

## Best Practices 📋

1. **Monitor Critical Threads**
   ```java
   if (criticalThread.getState() == Thread.State.TERMINATED) {
       // Handle thread completion or failure
   }
   ```

2. **Avoid Infinite Waiting**
   ```java
   // Instead of wait()
   lock.wait(5000); // Timeout after 5 seconds
   ```

3. **Handle InterruptedException**
   ```java
   try {
       Thread.sleep(1000);
   } catch (InterruptedException e) {
       Thread.currentThread().interrupt(); // Restore interrupt status
       return; // Exit gracefully
   }
   ```

4. **Use Thread Names for Debugging**
   ```java
   Thread worker = new Thread(task, "WorkerThread-1");
   System.out.println(worker.getName() + " state: " + worker.getState());
   ```

## Common Patterns

### Thread State Machine
```java
public enum ThreadTask {
    INITIALIZING,
    PROCESSING, 
    WAITING_FOR_RESOURCE,
    COMPLETING,
    FINISHED
}
```

### Graceful Thread Shutdown
```java
public class GracefulWorker implements Runnable {
    private volatile boolean running = true;
    
    public void shutdown() {
        running = false;
    }
    
    @Override
    public void run() {
        while (running) {
            // Do work
            if (Thread.currentThread().isInterrupted()) {
                break; // Respond to interruption
            }
        }
    }
}
```

---

## Interview Questions & Answers 🎤

### Q1: What are the 6 thread states in Java?

**Answer:**
1. **NEW** - Thread created but not started
2. **RUNNABLE** - Thread executing or ready to execute  
3. **BLOCKED** - Thread waiting to acquire a monitor lock
4. **WAITING** - Thread waiting indefinitely for another thread's action
5. **TIMED_WAITING** - Thread waiting for a specified time period
6. **TERMINATED** - Thread has completed execution

### Q2: What's the difference between BLOCKED and WAITING states?

**Answer:**
- **BLOCKED**: Thread is waiting to acquire a **monitor lock** (synchronized block/method). It will automatically become RUNNABLE when the lock is available.
- **WAITING**: Thread is waiting for **explicit notification** from another thread (`notify()`, `notifyAll()`) or for another thread to complete (`join()`).

```java
// BLOCKED example
synchronized(obj) { } // Thread waits for lock

// WAITING example  
synchronized(obj) {
    obj.wait(); // Thread waits for notify()
}
```

### Q3: Can a thread go directly from NEW to TERMINATED?

**Answer:**
No! A thread must go through RUNNABLE state first. The minimum path is:
```
NEW → RUNNABLE → TERMINATED
```
Even if a thread's `run()` method is empty, it still transitions through RUNNABLE before reaching TERMINATED.

### Q4: What happens if you call start() on a TERMINATED thread?

**Answer:**
It throws `IllegalThreadStateException`. Once a thread reaches TERMINATED state, it cannot be restarted. You must create a new Thread object.

```java
Thread t = new Thread(() -> System.out.println("Hello"));
t.start();
t.join(); // Wait for termination
// t.getState() == TERMINATED
t.start(); // Throws IllegalThreadStateException
```

### Q5: How can you check if a thread is still alive?

**Answer:**
Use `isAlive()` method or check the state:

```java
// Method 1: isAlive()
if (thread.isAlive()) {
    // Thread is in RUNNABLE, BLOCKED, WAITING, or TIMED_WAITING
}

// Method 2: State checking
Thread.State state = thread.getState();
boolean alive = (state != Thread.State.NEW && state != Thread.State.TERMINATED);
```

### Q6: What causes a thread to enter TIMED_WAITING state?

**Answer:**
Several methods can cause TIMED_WAITING:
- `Thread.sleep(milliseconds)`
- `Object.wait(timeout)`
- `Thread.join(timeout)`
- `LockSupport.parkNanos()`
- `LockSupport.parkUntil()`

### Q7: Can you force a thread to change states?

**Answer:**
You **cannot directly force** state changes, but you can **influence** them:
- `start()` - NEW → RUNNABLE
- `interrupt()` - Can wake up WAITING/TIMED_WAITING threads
- `notify()/notifyAll()` - Wake up WAITING threads
- Release locks - Allow BLOCKED threads to proceed

You **cannot** force a thread to BLOCKED or TERMINATED states directly.

### Q8: What's the difference between sleep() and wait()?

**Answer:**
| `sleep()` | `wait()` |
|-----------|----------|
| Static method of Thread | Instance method of Object |
| Does NOT release locks | RELEASES the monitor lock |
| Thread enters TIMED_WAITING | Thread enters WAITING/TIMED_WAITING |
| Automatically wakes up after timeout | Must be woken by notify()/notifyAll() |
| Can be called anywhere | Must be called within synchronized block |

### Q9: How do you monitor thread states in production?

**Answer:**
```java
// 1. JMX (Java Management Extensions)
ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
ThreadInfo[] threads = threadBean.getThreadInfo(threadBean.getAllThreadIds());

// 2. Custom monitoring
public class ThreadMonitor {
    public static void logThreadState(Thread thread) {
        System.out.printf("Thread %s: %s%n", 
            thread.getName(), thread.getState());
    }
}

// 3. Thread dumps
jstack <pid> // Command line tool
```

### Q10: What happens during thread state transitions?

**Answer:**
State transitions involve:
1. **JVM scheduler decisions** - Which thread gets CPU time
2. **Lock acquisition/release** - For synchronized blocks
3. **System calls** - For sleep(), wait(), I/O operations
4. **Memory barriers** - Ensuring memory consistency
5. **Context switching** - Saving/restoring thread state

The JVM handles these transitions automatically, but understanding them helps in:
- Performance optimization
- Deadlock debugging  
- Resource management
- Application monitoring 