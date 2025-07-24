package learning.d_locks;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

/**
 * Simple ReentrantLock Example
 * Shows how to use ReentrantLock and its advantages over synchronized
 */
public class a_SimpleReentrantLock {
    
    // ReentrantLock instance - more flexible than synchronized
    private static final ReentrantLock lock = new ReentrantLock();
    private static int counter = 0;
    
    public static void main(String[] args) throws InterruptedException {
        
        System.out.println("=== ReentrantLock Demonstration ===\n");
        
        // 1. Basic ReentrantLock usage
        demonstrateBasicLock();
        
        // 2. Show fairness feature
        demonstrateFairness();
        
        // 3. Show tryLock feature
        demonstrateTryLock();
        
        // 4. Show lock interruption
        demonstrateLockInterruption();
        
        // 5. Compare with synchronized
        demonstrateComparison();
    }
    
    /**
     * BASIC USAGE: ReentrantLock with proper try-finally pattern
     */
    private static void demonstrateBasicLock() throws InterruptedException {
        System.out.println("1. BASIC REENTRANT LOCK USAGE:");
        counter = 0; // Reset counter
        
        ReentrantLockCounter lockCounter = new ReentrantLockCounter();
        
        // Create 2 threads that increment counter
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                lockCounter.increment();
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                lockCounter.increment();
            }
        });
        
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        
        System.out.println("Expected: 2000, Actual: " + lockCounter.getValue());
        System.out.println("Result: " + (lockCounter.getValue() == 2000 ? "SUCCESS!" : "FAILED!"));
        System.out.println();
    }
    
    /**
     * FAIRNESS: ReentrantLock can be fair (FIFO) or unfair
     */
    private static void demonstrateFairness() throws InterruptedException {
        System.out.println("2. FAIRNESS DEMONSTRATION:");
        
        // Fair lock - threads get lock in order they requested it
        ReentrantLock fairLock = new ReentrantLock(true); // true = fair
        
        System.out.println("Creating 3 threads with fair lock...");
        
        for (int i = 1; i <= 3; i++) {
            final int threadNum = i;
            new Thread(() -> {
                try {
                    fairLock.lock();
                    System.out.println("Thread " + threadNum + " acquired fair lock");
                    Thread.sleep(100); // Simulate work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    System.out.println("Thread " + threadNum + " releasing fair lock");
                    fairLock.unlock();
                }
            }).start();
        }
        
        Thread.sleep(500); // Wait for demonstration
        System.out.println();
    }
    
    /**
     * TRY LOCK: Attempt to acquire lock without blocking
     */
    private static void demonstrateTryLock() throws InterruptedException {
        System.out.println("3. TRY LOCK DEMONSTRATION:");
        
        ReentrantLock tryLock = new ReentrantLock();
        
        // Thread 1: Holds lock for a while
        Thread holder = new Thread(() -> {
            try {
                tryLock.lock();
                System.out.println("Thread 1: Got the lock, working for 2 seconds...");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                System.out.println("Thread 1: Releasing lock");
                tryLock.unlock();
            }
        });
        
        // Thread 2: Tries to get lock without waiting
        Thread tryer = new Thread(() -> {
            try {
                Thread.sleep(100); // Let thread 1 get lock first
                
                System.out.println("Thread 2: Trying to get lock without waiting...");
                if (tryLock.tryLock()) {
                    try {
                        System.out.println("Thread 2: Got lock immediately!");
                    } finally {
                        tryLock.unlock();
                    }
                } else {
                    System.out.println("Thread 2: Lock not available, doing other work");
                }
                
                // Try with timeout
                System.out.println("Thread 2: Trying to get lock with 1 second timeout...");
                if (tryLock.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        System.out.println("Thread 2: Got lock with timeout!");
                    } finally {
                        tryLock.unlock();
                    }
                } else {
                    System.out.println("Thread 2: Timeout! Still couldn't get lock");
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        holder.start();
        tryer.start();
        holder.join();
        tryer.join();
        System.out.println();
    }
    
    /**
     * INTERRUPTION: ReentrantLock can be interrupted while waiting
     */
    private static void demonstrateLockInterruption() throws InterruptedException {
        System.out.println("4. LOCK INTERRUPTION DEMONSTRATION:");
        
        ReentrantLock interruptLock = new ReentrantLock();
        
        // Thread 1: Holds lock
        Thread holder = new Thread(() -> {
            try {
                interruptLock.lock();
                System.out.println("Thread 1: Got lock, sleeping for 3 seconds...");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Thread 1: Interrupted while holding lock");
            } finally {
                System.out.println("Thread 1: Releasing lock");
                interruptLock.unlock();
            }
        });
        
        // Thread 2: Waits for lock but can be interrupted
        Thread waiter = new Thread(() -> {
            try {
                Thread.sleep(100); // Let thread 1 get lock first
                System.out.println("Thread 2: Waiting for lock (interruptibly)...");
                interruptLock.lockInterruptibly(); // Can be interrupted!
                try {
                    System.out.println("Thread 2: Finally got the lock!");
                } finally {
                    interruptLock.unlock();
                }
            } catch (InterruptedException e) {
                System.out.println("Thread 2: Interrupted while waiting for lock!");
            }
        });
        
        holder.start();
        waiter.start();
        
        Thread.sleep(500); // Let them start
        System.out.println("Main: Interrupting waiting thread...");
        waiter.interrupt(); // Interrupt the waiting thread
        
        holder.join();
        waiter.join();
        System.out.println();
    }
    
    /**
     * COMPARISON: ReentrantLock vs synchronized
     */
    private static void demonstrateComparison() {
        System.out.println("5. REENTRANT LOCK vs SYNCHRONIZED:");
        System.out.println("ReentrantLock advantages:");
        System.out.println("  ✓ Can try to acquire lock without blocking (tryLock)");
        System.out.println("  ✓ Can be interrupted while waiting (lockInterruptibly)");
        System.out.println("  ✓ Can set fairness policy");
        System.out.println("  ✓ More flexible - can acquire in one method, release in another");
        System.out.println("  ✓ Better performance under high contention");
        System.out.println();
        System.out.println("synchronized advantages:");
        System.out.println("  ✓ Simpler syntax");
        System.out.println("  ✓ Automatic lock release (can't forget to unlock)");
        System.out.println("  ✓ JVM optimizations");
        System.out.println("  ✓ Less error-prone");
        System.out.println();
        System.out.println("Rule: Use synchronized for simple cases, ReentrantLock for advanced features");
    }
    
    /**
     * Thread-safe counter using ReentrantLock
     */
    static class ReentrantLockCounter {
        private final ReentrantLock lock = new ReentrantLock();
        private int count = 0;
        
        public void increment() {
            lock.lock(); // Acquire the lock
            try {
                count++; // Critical section - only one thread at a time
            } finally {
                lock.unlock(); // ALWAYS unlock in finally block!
            }
        }
        
        public int getValue() {
            lock.lock(); // Even reading should be protected
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }
        
        // Alternative method showing lock status
        public void incrementWithInfo() {
            System.out.println("Thread " + Thread.currentThread().getName() + 
                             " trying to acquire lock...");
            
            lock.lock();
            try {
                System.out.println("Thread " + Thread.currentThread().getName() + 
                                 " acquired lock, hold count: " + lock.getHoldCount());
                count++;
                
                // ReentrantLock is "reentrant" - same thread can acquire it multiple times
                demonstrateReentrant();
                
            } finally {
                System.out.println("Thread " + Thread.currentThread().getName() + 
                                 " releasing lock");
                lock.unlock();
            }
        }
        
        private void demonstrateReentrant() {
            // Same thread can acquire the lock again!
            lock.lock();
            try {
                System.out.println("  Reentrant call - hold count now: " + lock.getHoldCount());
            } finally {
                lock.unlock();
            }
        }
    }
}

/*
REENTRANT LOCK KEY CONCEPTS:

1. EXPLICIT LOCKING:
   - Must call lock() and unlock() explicitly
   - ALWAYS use try-finally pattern
   - More control than synchronized

2. ADVANCED FEATURES:
   - tryLock(): Non-blocking attempt to acquire
   - tryLock(timeout): Try with timeout
   - lockInterruptibly(): Can be interrupted while waiting
   - Fairness: Can ensure FIFO order

3. REENTRANT NATURE:
   - Same thread can acquire the lock multiple times
   - Must call unlock() for each lock() call
   - getHoldCount() shows how many times current thread holds lock

4. WHEN TO USE:
   - Need advanced features (tryLock, interruption, fairness)
   - Complex locking scenarios
   - High contention situations
   - When synchronized isn't flexible enough

5. BEST PRACTICES:
   - ALWAYS use try-finally
   - Acquire and release in same method when possible
   - Consider using synchronized for simple cases
   - Be careful with lock ordering to avoid deadlocks

REMEMBER: With great power comes great responsibility!
ReentrantLock is more powerful but also more error-prone than synchronized.
*/ 