package learning.h_executor_utility_class;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple Fixed Thread Pool Executor Example
 * 
 * This example demonstrates:
 * 1. Creating a fixed thread pool using Executors.newFixedThreadPool()
 * 2. Submitting tasks to the thread pool
 * 3. Using different types of task submission methods
 * 4. Proper shutdown procedures
 * 5. Exception handling in thread pools
 * 
 * A Fixed Thread Pool:
 * - Has a fixed number of threads (specified at creation)
 * - Reuses threads for multiple tasks
 * - Queues tasks when all threads are busy
 * - Ideal when you know the optimal number of threads for your workload
 */
public class a_SimpleFixedThreadPoolExecutor {
    
    // Shared counter to demonstrate thread safety
    private static final AtomicInteger taskCounter = new AtomicInteger(0);
    
    public static void main(String[] args) {
        System.out.println("=== Fixed Thread Pool Executor Demo ===\n");
        
        // Demo 1: Basic fixed thread pool usage
        demonstrateBasicFixedThreadPool();
        
        // Demo 2: Using submit() with Future
        demonstrateSubmitWithFuture();
        
        // Demo 3: Exception handling in thread pool
        demonstrateExceptionHandling();
        
        // Demo 4: Comparing with other executor types
        compareExecutorTypes();
        
        System.out.println("\n=== All demos completed ===");
    }
    
    /**
     * Demo 1: Basic Fixed Thread Pool Usage
     * Shows how to create, use, and properly shutdown a fixed thread pool
     */
    private static void demonstrateBasicFixedThreadPool() {
        System.out.println("--- Demo 1: Basic Fixed Thread Pool ---");
        
        // Create a fixed thread pool with 3 threads
        // This means maximum 3 tasks can run simultaneously
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        try {
            // Submit 8 tasks to demonstrate queuing behavior
            for (int i = 1; i <= 8; i++) {
                final int taskId = i;
                
                // Using execute() - fire and forget, no return value
                executor.execute(() -> {
                    String threadName = Thread.currentThread().getName();
                    System.out.printf("Task %d started on thread: %s%n", taskId, threadName);
                    
                    try {
                        // Simulate some work
                        Thread.sleep(2000);
                        System.out.printf("Task %d completed on thread: %s%n", taskId, threadName);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.printf("Task %d was interrupted%n", taskId);
                    }
                });
            }
            
            // Wait a bit to see the execution pattern
            Thread.sleep(1000);
            System.out.println("All tasks submitted. Notice only 3 run simultaneously.\n");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Proper shutdown procedure
            shutdownExecutorGracefully(executor, "Basic Fixed Thread Pool");
        }
    }
    
    /**
     * Demo 2: Using submit() with Future
     * Shows how to get return values from tasks
     */
    private static void demonstrateSubmitWithFuture() {
        System.out.println("--- Demo 2: Submit with Future ---");
        
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
            System.out.println("Waiting for results...");
            System.out.println("Future 1: " + future1.get());
            System.out.println("Future 2: " + future2.get());
            System.out.println("Future 3: " + future3.get());
            System.out.println();
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error getting future results: " + e.getMessage());
        } finally {
            shutdownExecutorGracefully(executor, "Submit with Future");
        }
    }
    
    /**
     * Demo 3: Exception Handling in Thread Pool
     * Shows how to handle exceptions in thread pool tasks
     */
    private static void demonstrateExceptionHandling() {
        System.out.println("--- Demo 3: Exception Handling ---");
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        try {
            // Task that throws an exception
            Future<String> futureWithException = executor.submit(() -> {
                Thread.sleep(500);
                if (true) { // Always throw for demo
                    throw new RuntimeException("Simulated task failure!");
                }
                return "This won't be reached";
            });
            
            // Task that completes successfully
            Future<String> successfulFuture = executor.submit(() -> {
                Thread.sleep(1000);
                return "Success despite other task failing!";
            });
            
            try {
                // This will throw ExecutionException containing the RuntimeException
                String result1 = futureWithException.get();
                System.out.println("Result 1: " + result1);
            } catch (ExecutionException e) {
                System.out.println("Caught exception from failed task: " + e.getCause().getMessage());
            }
            
            // This task should still complete successfully
            String result2 = successfulFuture.get();
            System.out.println("Result 2: " + result2);
            System.out.println();
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Unexpected error: " + e.getMessage());
        } finally {
            shutdownExecutorGracefully(executor, "Exception Handling");
        }
    }
    
    /**
     * Demo 4: Comparing Different Executor Types
     * Shows the difference between fixed, cached, and single thread executors
     */
    private static void compareExecutorTypes() {
        System.out.println("--- Demo 4: Comparing Executor Types ---");
        
        System.out.println("Creating different executor types...");
        
        // Reset counter for this demo
        taskCounter.set(0);
        
        // Fixed thread pool - exactly 2 threads
        ExecutorService fixedExecutor = Executors.newFixedThreadPool(2);
        System.out.println("Fixed Pool (2 threads): ");
        submitTasksToExecutor(fixedExecutor, "FIXED", 4);
        
        // Single thread executor - exactly 1 thread
        ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
        System.out.println("Single Thread Pool (1 thread): ");
        submitTasksToExecutor(singleExecutor, "SINGLE", 3);
        
        // Cached thread pool - creates threads as needed
        ExecutorService cachedExecutor = Executors.newCachedThreadPool();
        System.out.println("Cached Pool (creates as needed): ");
        submitTasksToExecutor(cachedExecutor, "CACHED", 4);
        
        // Shutdown all executors
        shutdownExecutorGracefully(fixedExecutor, "Fixed Pool");
        shutdownExecutorGracefully(singleExecutor, "Single Thread Pool");
        shutdownExecutorGracefully(cachedExecutor, "Cached Pool");
    }
    
    /**
     * Helper method to submit tasks to an executor
     */
    private static void submitTasksToExecutor(ExecutorService executor, String type, int taskCount) {
        for (int i = 1; i <= taskCount; i++) {
            final int taskId = taskCounter.incrementAndGet();
            executor.execute(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.printf("  [%s] Task %d on %s%n", type, taskId, threadName);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        try {
            Thread.sleep(1500); // Let tasks complete before moving to next demo
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Proper way to shutdown an ExecutorService
     * This is important to avoid resource leaks!
     */
    private static void shutdownExecutorGracefully(ExecutorService executor, String name) {
        System.out.println("Shutting down " + name + "...");
        
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
        
        System.out.println(name + " shutdown complete.\n");
    }
} 