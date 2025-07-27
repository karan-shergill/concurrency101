package learning.h_executor_utility_class;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

/**
 * Simple Cached Thread Pool Executor Example for Beginners
 * 
 * This example demonstrates:
 * 1. Creating a cached thread pool using Executors.newCachedThreadPool()
 * 2. Understanding thread creation and reuse behavior
 * 3. Thread timeout and automatic cleanup (60 seconds idle timeout)
 * 4. Comparing with fixed thread pool behavior
 * 5. Best use cases and potential pitfalls
 * 
 * A Cached Thread Pool:
 * - Creates new threads as needed
 * - Reuses existing threads when available
 * - Terminates idle threads after 60 seconds
 * - Has no limit on the number of threads (can be dangerous!)
 * - Ideal for short-lived, asynchronous tasks
 * - Uses SynchronousQueue (no task queuing - immediate thread creation)
 */
public class b_SimpleCachedThreadPoolExecutor {
    
    private static final AtomicInteger taskCounter = new AtomicInteger(0);
    private static final AtomicInteger activeThreadCount = new AtomicInteger(0);
    
    public static void main(String[] args) {
        System.out.println("=== Cached Thread Pool Executor Demo ===\n");
        
        // Demo 1: Basic cached thread pool behavior
        demonstrateCachedThreadPoolBasics();
        
        // Demo 2: Thread creation and reuse patterns
        demonstrateThreadCreationAndReuse();
        
        // Demo 3: Burst workload handling
        demonstrateBurstWorkloadHandling();
        
        // Demo 4: Thread timeout and cleanup behavior
        demonstrateThreadTimeoutBehavior();
        
        // Demo 5: Comparing fixed vs cached thread pools
        compareFixedVsCachedThreadPools();
        
        // Demo 6: Potential problems with cached thread pools
        demonstratePotentialProblems();
        
        System.out.println("\n=== All demos completed ===");
    }
    
    /**
     * Demo 1: Basic Cached Thread Pool Behavior
     * Shows how cached thread pool creates threads on demand
     */
    private static void demonstrateCachedThreadPoolBasics() {
        System.out.println("--- Demo 1: Basic Cached Thread Pool Behavior ---");
        
        ExecutorService executor = Executors.newCachedThreadPool();
        
        try {
            System.out.println("Submitting 5 concurrent tasks...");
            
            // Submit 5 tasks simultaneously - should create 5 threads
            for (int i = 1; i <= 5; i++) {
                final int taskId = i;
                executor.execute(() -> {
                    String threadName = Thread.currentThread().getName();
                    System.out.printf("Task %d started on thread: %s%n", taskId, threadName);
                    
                    try {
                        Thread.sleep(2000); // Simulate work
                        System.out.printf("Task %d completed on thread: %s%n", taskId, threadName);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            
            Thread.sleep(3000); // Wait for tasks to complete
            System.out.println("Notice: All 5 tasks ran simultaneously on different threads!\n");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownExecutorGracefully(executor, "Cached Thread Pool Basic");
        }
    }
    
    /**
     * Demo 2: Thread Creation and Reuse Patterns
     * Shows how cached thread pool reuses threads when possible
     */
    private static void demonstrateThreadCreationAndReuse() {
        System.out.println("--- Demo 2: Thread Creation and Reuse ---");
        
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
            
            System.out.println("\nSecond batch: 3 more tasks (should reuse threads)...");
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
            System.out.println("Notice: Second batch likely reused threads from first batch!\n");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownExecutorGracefully(executor, "Thread Reuse Demo");
        }
    }
    
    /**
     * Demo 3: Burst Workload Handling
     * Shows how cached thread pool handles sudden spikes in workload
     */
    private static void demonstrateBurstWorkloadHandling() {
        System.out.println("--- Demo 3: Burst Workload Handling ---");
        
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
            System.out.println("Waiting for all burst tasks to complete...");
            for (int i = 0; i < futures.size(); i++) {
                String result = futures.get(i).get();
                System.out.printf("Got result: %s%n", result);
            }
            
            System.out.println("All burst tasks completed! Cached pool handled the spike well.\n");
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error in burst demo: " + e.getMessage());
        } finally {
            shutdownExecutorGracefully(executor, "Burst Workload Demo");
        }
    }
    
    /**
     * Demo 4: Thread Timeout and Cleanup Behavior
     * Shows how cached thread pool cleans up idle threads after 60 seconds
     */
    private static void demonstrateThreadTimeoutBehavior() {
        System.out.println("--- Demo 4: Thread Timeout Behavior ---");
        System.out.println("(Note: This demo shows the concept but doesn't wait 60 seconds)");
        
        // Create a cached thread pool
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        
        try {
            System.out.println("Initial pool size: " + executor.getPoolSize());
            
            // Submit some tasks to create threads
            System.out.println("Submitting 3 tasks to create threads...");
            for (int i = 1; i <= 3; i++) {
                final int taskId = i;
                executor.execute(() -> {
                    System.out.printf("Task %d on thread: %s%n", taskId, Thread.currentThread().getName());
                    sleep(500);
                });
            }
            
            Thread.sleep(1000);
            System.out.println("Pool size after tasks: " + executor.getPoolSize());
            System.out.println("Active threads: " + executor.getActiveCount());
            
            // Explain what would happen after 60 seconds
            System.out.println("\nIn a real scenario:");
            System.out.println("- Threads will stay alive for 60 seconds");
            System.out.println("- After 60 seconds of inactivity, they will be terminated");
            System.out.println("- Pool size will shrink back to 0");
            System.out.println("- New threads will be created when new tasks arrive\n");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownExecutorGracefully(executor, "Thread Timeout Demo");
        }
    }
    
    /**
     * Demo 5: Comparing Fixed vs Cached Thread Pools
     * Side-by-side comparison to highlight differences
     */
    private static void compareFixedVsCachedThreadPools() {
        System.out.println("--- Demo 5: Fixed vs Cached Comparison ---");
        
        // Fixed thread pool with 2 threads
        ExecutorService fixedExecutor = Executors.newFixedThreadPool(2);
        
        // Cached thread pool
        ExecutorService cachedExecutor = Executors.newCachedThreadPool();
        
        try {
            System.out.println("Submitting 4 tasks to FIXED thread pool (2 threads):");
            submitTasksWithLabel(fixedExecutor, "FIXED", 4);
            
            Thread.sleep(3000); // Wait for fixed pool tasks
            
            System.out.println("\nSubmitting 4 tasks to CACHED thread pool:");
            submitTasksWithLabel(cachedExecutor, "CACHED", 4);
            
            Thread.sleep(2000); // Wait for cached pool tasks
            
            System.out.println("\nComparison Summary:");
            System.out.println("- Fixed pool: Tasks 3&4 waited for threads 1&2 to be free");
            System.out.println("- Cached pool: All 4 tasks ran immediately on separate threads\n");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownExecutorGracefully(fixedExecutor, "Fixed Pool Comparison");
            shutdownExecutorGracefully(cachedExecutor, "Cached Pool Comparison");
        }
    }
    
    /**
     * Demo 6: Potential Problems with Cached Thread Pools
     * Shows scenarios where cached thread pools can be problematic
     */
    private static void demonstratePotentialProblems() {
        System.out.println("--- Demo 6: Potential Problems ---");
        
        System.out.println("WARNING: This demo shows potential issues (simulated safely)");
        
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        
        try {
            System.out.println("\nProblem 1: Unbounded Thread Creation");
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
            
            Thread.sleep(3000); // Wait for tasks to complete
            
            System.out.println("\nProblem scenarios to avoid:");
            System.out.println("- Submitting thousands of tasks simultaneously");
            System.out.println("- Long-running tasks that block threads for extended periods");
            System.out.println("- Recursive task submission without limits");
            System.out.println("- This can lead to OutOfMemoryError due to too many threads!");
            
            System.out.println("\nBetter alternatives:");
            System.out.println("- Use fixed thread pools for predictable workloads");
            System.out.println("- Use work queues to limit concurrent execution");
            System.out.println("- Monitor and set thread limits in production systems\n");
            
        } catch (Exception e) {
            System.err.println("Error in problems demo: " + e.getMessage());
        } finally {
            shutdownExecutorGracefully(executor, "Problems Demo");
        }
    }
    
    /**
     * Helper method to submit tasks with labels
     */
    private static void submitTasksWithLabel(ExecutorService executor, String label, int count) {
        for (int i = 1; i <= count; i++) {
            final int taskId = i;
            executor.execute(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.printf("  [%s] Task %d on %s%n", label, taskId, threadName);
                sleep(1500);
            });
        }
    }
    
    /**
     * Helper method for sleep without checked exception
     */
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Proper way to shutdown an ExecutorService
     */
    private static void shutdownExecutorGracefully(ExecutorService executor, String name) {
        System.out.println("Shutting down " + name + "...");
        
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("Tasks didn't finish in time, forcing shutdown...");
                executor.shutdownNow();
                
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate cleanly!");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        System.out.println(name + " shutdown complete.\n");
    }
} 