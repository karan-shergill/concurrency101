package learning.e_compare_and_swap;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AtomicInteger Demo - Demonstrates thread-safe atomic operations
 * 
 * AtomicInteger provides lock-free thread-safe operations on integers
 * using compare-and-swap (CAS) operations at the hardware level.
 * 
 * Key benefits:
 * - No explicit synchronization needed
 * - Better performance than synchronized blocks for simple operations
 * - Lock-free and wait-free for most operations
 */
public class a_SimpleAtomicInteger {
    
    private static final int THREAD_COUNT = 10;
    private static final int OPERATIONS_PER_THREAD = 1000;
    
    // AtomicInteger for thread-safe counter
    private static final AtomicInteger atomicCounter = new AtomicInteger(0);
    
    // Regular int for comparison (NOT thread-safe)
    private static int regularCounter = 0;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== AtomicInteger Demo ===\n");
        
        // Demo 1: Basic atomic operations
        demonstrateBasicOperations();
        
        // Demo 2: Thread-safe increment operations
        demonstrateThreadSafetyWithAtomicInteger();
        
        // Demo 3: Compare with non-atomic operations
        demonstrateNonAtomicProblems();
        
        // Demo 4: Advanced AtomicInteger methods
        demonstrateAdvancedMethods();
    }
    
    /**
     * Demonstrates basic AtomicInteger operations
     */
    private static void demonstrateBasicOperations() {
        System.out.println("1. Basic AtomicInteger Operations:");
        
        AtomicInteger atomic = new AtomicInteger(10);
        
        System.out.println("Initial value: " + atomic.get());
        
        // Basic operations
        System.out.println("getAndIncrement(): " + atomic.getAndIncrement()); // returns 10, then increments
        System.out.println("Current value: " + atomic.get()); // now 11
        
        System.out.println("incrementAndGet(): " + atomic.incrementAndGet()); // increments first, then returns 12
        
        System.out.println("getAndDecrement(): " + atomic.getAndDecrement()); // returns 12, then decrements
        System.out.println("Current value: " + atomic.get()); // now 11
        
        System.out.println("addAndGet(5): " + atomic.addAndGet(5)); // adds 5, then returns 16
        
        System.out.println("getAndSet(100): " + atomic.getAndSet(100)); // returns 16, sets to 100
        System.out.println("Final value: " + atomic.get()); // now 100
        
        System.out.println();
    }
    
    /**
     * Demonstrates thread-safe increment operations using AtomicInteger
     */
    private static void demonstrateThreadSafetyWithAtomicInteger() throws InterruptedException {
        System.out.println("2. Thread-Safe Increment with AtomicInteger:");
        
        atomicCounter.set(0); // Reset counter
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        
        // Start multiple threads that increment the atomic counter
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        atomicCounter.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(); // Wait for all threads to complete
        executor.shutdown();
        
        int expectedValue = THREAD_COUNT * OPERATIONS_PER_THREAD;
        System.out.println("Expected value: " + expectedValue);
        System.out.println("Actual atomic value: " + atomicCounter.get());
        System.out.println("Atomic operations are thread-safe: " + (atomicCounter.get() == expectedValue));
        System.out.println();
    }
    
    /**
     * Demonstrates problems with non-atomic operations in multithreaded environment
     */
    private static void demonstrateNonAtomicProblems() throws InterruptedException {
        System.out.println("3. Non-Atomic Operations (Race Condition Demo):");
        
        regularCounter = 0; // Reset counter
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        
        // Start multiple threads that increment the regular counter (NOT thread-safe)
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        regularCounter++; // This is NOT thread-safe!
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(); // Wait for all threads to complete
        executor.shutdown();
        
        int expectedValue = THREAD_COUNT * OPERATIONS_PER_THREAD;
        System.out.println("Expected value: " + expectedValue);
        System.out.println("Actual regular counter value: " + regularCounter);
        System.out.println("Lost increments due to race conditions: " + (expectedValue - regularCounter));
        System.out.println("Regular operations have race conditions: " + (regularCounter != expectedValue));
        System.out.println();
    }
    
    /**
     * Demonstrates advanced AtomicInteger methods
     */
    private static void demonstrateAdvancedMethods() {
        System.out.println("4. Advanced AtomicInteger Methods:");
        
        AtomicInteger atomic = new AtomicInteger(50);
        
        // compareAndSet - only updates if current value matches expected
        System.out.println("Initial value: " + atomic.get());
        System.out.println("compareAndSet(50, 100): " + atomic.compareAndSet(50, 100)); // true, updates to 100
        System.out.println("Current value: " + atomic.get());
        
        System.out.println("compareAndSet(50, 200): " + atomic.compareAndSet(50, 200)); // false, no update
        System.out.println("Current value: " + atomic.get()); // still 100
        
        // updateAndGet with lambda (Java 8+)
        System.out.println("updateAndGet(x -> x * 2): " + atomic.updateAndGet(x -> x * 2)); // 200
        
        // accumulateAndGet with lambda
        System.out.println("accumulateAndGet(10, Integer::sum): " + 
                          atomic.accumulateAndGet(10, Integer::sum)); // 210 (200 + 10)
        
        // Demonstrate retry pattern with compareAndSet
        demonstrateCompareAndSetRetry(atomic);
        
        System.out.println();
    }
    
    /**
     * Demonstrates the retry pattern often used with compareAndSet
     */
    private static void demonstrateCompareAndSetRetry(AtomicInteger atomic) {
        System.out.println("\nCompareAndSet Retry Pattern:");
        System.out.println("Doubling the value using CAS retry pattern...");
        
        int currentValue;
        int newValue;
        
        do {
            currentValue = atomic.get();
            newValue = currentValue * 2;
            System.out.println("Attempting to change " + currentValue + " to " + newValue);
        } while (!atomic.compareAndSet(currentValue, newValue));
        
        System.out.println("Successfully updated to: " + atomic.get());
    }
} 