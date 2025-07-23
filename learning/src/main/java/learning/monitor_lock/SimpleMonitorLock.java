package learning.monitor_lock;

/**
 * Simple Monitor Lock Example
 * Shows WHY we need synchronization
 */
public class SimpleMonitorLock {
    
    // Shared variable that multiple threads will access
    private static int counter = 0;
    
    public static void main(String[] args) throws InterruptedException {
        
        System.out.println("=== Why Monitor Locks Are Needed ===\n");
        
        // First, show the PROBLEM without synchronization
        demonstrateProblem();
        
        // Then, show the SOLUTION with synchronization
        demonstrateSolution();
    }
    
    /**
     * PROBLEM: Multiple threads accessing shared data without protection
     * This causes "race conditions" - unpredictable results!
     */
    private static void demonstrateProblem() throws InterruptedException {
        System.out.println("1. WITHOUT SYNCHRONIZATION (PROBLEM):");
        counter = 0; // Reset counter
        
        // Create 2 threads that both increment counter 1000 times
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter++; // DANGER! Not thread-safe
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter++; // DANGER! Not thread-safe
            }
        });
        
        // Start both threads
        thread1.start();
        thread2.start();
        
        // Wait for both to finish
        thread1.join();
        thread2.join();
        
        System.out.println("Expected result: 2000");
        System.out.println("Actual result: " + counter);
        System.out.println("Problem: " + (counter != 2000 ? "RACE CONDITION OCCURRED!" : "Got lucky this time"));
        System.out.println();
    }
    
    /**
     * SOLUTION: Using synchronized method to protect shared data
     * Monitor lock ensures only ONE thread can access at a time
     */
    private static void demonstrateSolution() throws InterruptedException {
        System.out.println("2. WITH SYNCHRONIZATION (SOLUTION):");
        
        SafeCounter safeCounter = new SafeCounter();
        
        // Create 2 threads that both increment counter 1000 times
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                safeCounter.increment(); // SAFE! Protected by monitor lock
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                safeCounter.increment(); // SAFE! Protected by monitor lock
            }
        });
        
        // Start both threads
        thread1.start();
        thread2.start();
        
        // Wait for both to finish
        thread1.join();
        thread2.join();
        
        System.out.println("Expected result: 2000");
        System.out.println("Actual result: " + safeCounter.getValue());
        System.out.println("Result: " + (safeCounter.getValue() == 2000 ? "PERFECT! Always correct" : "Something went wrong"));
    }
    
    /**
     * Thread-safe counter using synchronized methods
     * The 'synchronized' keyword creates a monitor lock
     */
    static class SafeCounter {
        private int count = 0;
        
        // SYNCHRONIZED METHOD - only one thread can enter at a time
        public synchronized void increment() {
            count++; // Now this is safe!
            
            // What synchronized does:
            // 1. Thread acquires monitor lock on this object
            // 2. Thread executes the method
            // 3. Thread releases monitor lock when done
            // 4. Other threads wait their turn
        }
        
        // SYNCHRONIZED METHOD for reading too
        public synchronized int getValue() {
            return count; // Safe to read
        }
    }
}

/*
WHY MONITOR LOCKS ARE NEEDED:

PROBLEM: Race Conditions
- Multiple threads access shared data simultaneously
- Operations like counter++ are NOT atomic (they have multiple steps)
- Threads can interrupt each other mid-operation
- Results become unpredictable and wrong

SOLUTION: Monitor Locks (synchronized)
- Only ONE thread can hold the lock at a time
- Other threads must WAIT their turn
- Guarantees operations complete without interruption
- Results are always correct and predictable

SIMPLE RULE: If multiple threads access shared data, use synchronized!
*/ 