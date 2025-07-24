package learning.c_monitor_lock;

/**
 * Monitor Lock Tasks Demonstration
 * 
 * This example shows different synchronization scenarios:
 * 1. Synchronized methods
 * 2. Synchronized blocks
 * 3. Non-synchronized methods
 * 4. How threads interact with monitor locks
 * 
 * Key Learning: Understanding when threads block vs when they can run concurrently
 */
public class b_MonitorLockTasksDemo {

    public static void main(String[] args) {
        System.out.println("=== Monitor Lock Tasks Demonstration ===\n");

        // Create a single object that multiple threads will access
        MonitorLockExample obj = new MonitorLockExample();

        // Create multiple threads that will call different methods
        Thread t1 = new Thread(() -> obj.task1(), "Thread-1");
        Thread t2 = new Thread(() -> obj.task2(), "Thread-2");  
        Thread t3 = new Thread(() -> obj.task3(), "Thread-3");

        System.out.println("Main thread starting all threads...\n");

        // Start all threads at roughly the same time
        t1.start();
        t2.start();
        t3.start();

        System.out.println("Main thread finished starting threads");
        System.out.println("Watch the execution order to understand monitor locks!\n");
    }
}

/**
 * MonitorLockExample - Demonstrates different synchronization scenarios
 * 
 * This class shows how monitor locks work with:
 * - Synchronized methods
 * - Synchronized blocks
 * - Non-synchronized methods
 */
class MonitorLockExample {
    
    /**
     * SYNCHRONIZED METHOD - Entire method is protected by monitor lock
     * 
     * Monitor Lock Behavior:
     * - Thread must acquire lock on 'this' object before entering
     * - No other synchronized method can run on same object until this completes
     * - Lock is automatically released when method finishes (even if exception occurs)
     */
    public synchronized void task1() {
        System.out.println("🔒 TASK1 STARTED by " + Thread.currentThread().getName() + 
                         " (synchronized method - has monitor lock)");
        
        try {
            // Simulate some work that takes time
            System.out.println("   TASK1: Doing some work...");
            Thread.sleep(3000); // 3 seconds of work
            System.out.println("   TASK1: Work completed!");
            
        } catch (InterruptedException e) {
            System.out.println("   TASK1: Interrupted - " + e.getMessage());
        }
        
        System.out.println("🔓 TASK1 FINISHED by " + Thread.currentThread().getName() + 
                         " (releasing monitor lock)");
    }
    
    /**
     * MIXED METHOD - Part synchronized, part not
     * 
     * Monitor Lock Behavior:
     * - Code before synchronized block can run concurrently
     * - Only the synchronized block requires monitor lock
     * - Code after synchronized block can run concurrently again
     */
    public void task2() {
        // This part CAN run concurrently with other non-synchronized code
        System.out.println("📢 TASK2 STARTED by " + Thread.currentThread().getName() + 
                         " (before synchronized block - no lock needed)");
        
        System.out.println("   TASK2: Doing work outside synchronized block...");
        
        try {
            Thread.sleep(1000); // 1 second of non-synchronized work
        } catch (InterruptedException e) {
            System.out.println("   TASK2: Interrupted - " + e.getMessage());
        }
        
        // This part REQUIRES monitor lock - will wait if another thread has it
        synchronized (this) {
            System.out.println("🔒 TASK2 ENTERED synchronized block by " + 
                             Thread.currentThread().getName() + " (acquired monitor lock)");
            
            try {
                System.out.println("   TASK2: Doing work inside synchronized block...");
                Thread.sleep(2000); // 2 seconds of synchronized work
                System.out.println("   TASK2: Synchronized work completed!");
                
            } catch (InterruptedException e) {
                System.out.println("   TASK2: Interrupted in synchronized block - " + e.getMessage());
            }
            
            System.out.println("🔓 TASK2 EXITING synchronized block by " + 
                             Thread.currentThread().getName() + " (releasing monitor lock)");
        }
        
        // This part CAN run concurrently again
        System.out.println("📢 TASK2 FINISHED by " + Thread.currentThread().getName() + 
                         " (after synchronized block - no lock needed)");
    }
    
    /**
     * NON-SYNCHRONIZED METHOD - No monitor lock required
     * 
     * Monitor Lock Behavior:
     * - Can run concurrently with other non-synchronized methods
     * - Can run concurrently with synchronized methods (but won't interfere)
     * - Multiple threads can execute this simultaneously
     */
    public void task3() {
        System.out.println("🟢 TASK3 STARTED by " + Thread.currentThread().getName() + 
                         " (non-synchronized - no lock needed)");
        
        try {
            System.out.println("   TASK3: Doing concurrent work...");
            Thread.sleep(1500); // 1.5 seconds of work
            System.out.println("   TASK3: Concurrent work completed!");
            
        } catch (InterruptedException e) {
            System.out.println("   TASK3: Interrupted - " + e.getMessage());
        }
        
        System.out.println("🟢 TASK3 FINISHED by " + Thread.currentThread().getName() + 
                         " (no lock was needed)");
    }
}

/*
UNDERSTANDING THE EXECUTION PATTERNS:

When you run this demo, you'll observe these patterns:

1. SYNCHRONIZED METHOD (task1):
   - Only ONE thread can execute task1() at a time
   - If Thread-1 is running task1(), Thread-2 and Thread-3 cannot call task1()
   - They must wait until Thread-1 finishes and releases the monitor lock

2. SYNCHRONIZED BLOCK (task2):
   - Before synchronized block: Can run concurrently
   - Inside synchronized block: Only one thread at a time
   - After synchronized block: Can run concurrently again

3. NON-SYNCHRONIZED METHOD (task3):
   - Multiple threads can run task3() simultaneously
   - No waiting required
   - Completely independent execution

KEY MONITOR LOCK RULES:

1. SAME OBJECT RULE:
   - Monitor lock is per-object (per instance)
   - If Thread-A has lock on obj1, Thread-B can still get lock on obj2
   - Different objects = different locks

2. LOCK GRANULARITY:
   - synchronized method = entire method protected
   - synchronized block = only that block protected

3. AUTOMATIC RELEASE:
   - Lock is automatically released when:
     * Method completes normally
     * Method throws an exception
     * synchronized block ends

4. DEADLOCK PREVENTION:
   - Always acquire locks in same order
   - Keep synchronized sections short
   - Avoid nested synchronization when possible

REAL-WORLD ANALOGY:
Think of monitor lock like a bathroom key:
- Only one person can use the bathroom at a time (synchronized method)
- They must wait in line if someone else has the key (blocked threads)
- Once done, they return the key for next person (lock released)
- Multiple bathrooms = multiple objects = multiple locks

PERFORMANCE IMPLICATIONS:
- Synchronization adds overhead
- Too much synchronization = bottleneck
- Too little synchronization = race conditions
- Balance is key for good performance
*/ 