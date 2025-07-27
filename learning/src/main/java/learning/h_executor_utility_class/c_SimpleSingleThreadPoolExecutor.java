package learning.h_executor_utility_class;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Simple Single Thread Pool Executor Example for Beginners
 * 
 * This example demonstrates:
 * 1. Creating a single thread executor using Executors.newSingleThreadExecutor()
 * 2. Sequential task execution and ordering guarantees
 * 3. Task queuing behavior when multiple tasks are submitted
 * 4. Thread safety without explicit synchronization
 * 5. Comparing with multi-threaded executors
 * 6. Real-world use cases and best practices
 * 
 * A Single Thread Executor:
 * - Uses exactly ONE thread for all tasks
 * - Executes tasks sequentially (one after another)
 * - Maintains task submission order
 * - Queues tasks in an unbounded LinkedBlockingQueue
 * - Provides implicit thread safety for shared state
 * - Ideal for tasks that must be executed in order
 * - Great for logging, sequential file processing, state management
 */
public class c_SimpleSingleThreadPoolExecutor {
    
    // Shared state that will be safely accessed by single thread
    private static final List<String> sharedList = new ArrayList<>();
    private static final AtomicInteger taskCounter = new AtomicInteger(0);
    
    public static void main(String[] args) {
        System.out.println("=== Single Thread Pool Executor Demo ===\n");
        
        // Demo 1: Basic single thread execution
        demonstrateBasicSingleThreadExecution();
        
        // Demo 2: Task ordering guarantees
        demonstrateTaskOrdering();
        
        // Demo 3: Queue behavior with multiple tasks
        demonstrateQueueBehavior();
        
        // Demo 4: Thread safety without synchronization
        demonstrateThreadSafety();
        
        // Demo 5: Comparing single vs multi-threaded execution
        compareSingleVsMultiThreaded();
        
        // Demo 6: Real-world use cases
        demonstrateRealWorldUseCases();
        
        // Demo 7: Exception handling in single thread context
        demonstrateExceptionHandling();
        
        System.out.println("\n=== All demos completed ===");
    }
    
    /**
     * Demo 1: Basic Single Thread Execution
     * Shows how all tasks run on the same single thread sequentially
     */
    private static void demonstrateBasicSingleThreadExecution() {
        System.out.println("--- Demo 1: Basic Single Thread Execution ---");
        
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
                    
                    // Simulate some work
                    sleep(1000);
                    
                    System.out.printf("Task %d completed on thread: %s (ID: %d)%n", 
                            taskId, threadName, threadId);
                });
            }
            
            Thread.sleep(6000); // Wait for all tasks to complete
            System.out.println("Notice: All tasks ran on the SAME thread, one after another!\n");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownExecutorGracefully(executor, "Basic Single Thread");
        }
    }
    
    /**
     * Demo 2: Task Ordering Guarantees
     * Shows that tasks execute in the exact order they were submitted
     */
    private static void demonstrateTaskOrdering() {
        System.out.println("--- Demo 2: Task Ordering Guarantees ---");
        
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
            
            Thread.sleep(3000); // Wait for completion
            
            System.out.println("\nExecution order: " + executionOrder);
            System.out.println("Notice: Tasks executed in submission order, regardless of duration!\n");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownExecutorGracefully(executor, "Task Ordering");
        }
    }
    
    /**
     * Demo 3: Queue Behavior with Multiple Tasks
     * Shows how tasks are queued when submitted rapidly
     */
    private static void demonstrateQueueBehavior() {
        System.out.println("--- Demo 3: Queue Behavior ---");
        
        // Use ThreadPoolExecutor to inspect queue
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newSingleThreadExecutor();
        
        try {
            System.out.println("Rapidly submitting 8 tasks...");
            
            // Submit tasks quickly
            for (int i = 1; i <= 8; i++) {
                final int taskId = i;
                executor.execute(() -> {
                    System.out.printf("Task %d: Starting execution%n", taskId);
                    sleep(800); // Each task takes 800ms
                    System.out.printf("Task %d: Finished execution%n", taskId);
                });
                
                // Check queue size after each submission
                System.out.printf("After submitting task %d: Queue size = %d, Active = %d%n", 
                        i, executor.getQueue().size(), executor.getActiveCount());
            }
            
            System.out.println("\nWaiting for all tasks to complete...");
            Thread.sleep(7000);
            
            System.out.printf("Final state: Queue size = %d, Active = %d%n", 
                    executor.getQueue().size(), executor.getActiveCount());
            System.out.println("Notice: Tasks were queued and executed one by one!\n");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownExecutorGracefully(executor, "Queue Behavior");
        }
    }
    
    /**
     * Demo 4: Thread Safety Without Synchronization
     * Shows how single thread executor provides implicit thread safety
     */
    private static void demonstrateThreadSafety() {
        System.out.println("--- Demo 4: Thread Safety Without Synchronization ---");
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        // Clear the shared list
        sharedList.clear();
        
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
                    
                    // Simulate some processing
                    sleep(500);
                    
                    System.out.printf("Task %d: Current list size = %d%n", taskId, sharedList.size());
                    System.out.printf("Task %d: List contents = %s%n", taskId, sharedList);
                });
            }
            
            Thread.sleep(3500);
            
            System.out.println("\nFinal shared list: " + sharedList);
            System.out.println("Notice: No race conditions or data corruption - single thread is inherently safe!\n");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownExecutorGracefully(executor, "Thread Safety");
        }
    }
    
    /**
     * Demo 5: Comparing Single vs Multi-threaded Execution
     * Side-by-side comparison to show the differences
     */
    private static void compareSingleVsMultiThreaded() {
        System.out.println("--- Demo 5: Single vs Multi-threaded Comparison ---");
        
        System.out.println("First: Single thread executor...");
        ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
        long singleThreadTime = measureExecutionTime(singleExecutor, "SINGLE", 4);
        
        try {
            Thread.sleep(1000); // Brief pause between tests
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("\nThen: Fixed thread pool (4 threads)...");
        ExecutorService multiExecutor = Executors.newFixedThreadPool(4);
        long multiThreadTime = measureExecutionTime(multiExecutor, "MULTI", 4);
        
        System.out.printf("\nComparison Results:%n");
        System.out.printf("Single thread time: ~%d seconds%n", singleThreadTime / 1000);
        System.out.printf("Multi thread time: ~%d seconds%n", multiThreadTime / 1000);
        System.out.printf("Single thread executed tasks sequentially%n");
        System.out.printf("Multi thread executed tasks concurrently%n%n");
        
        shutdownExecutorGracefully(singleExecutor, "Single Thread Comparison");
        shutdownExecutorGracefully(multiExecutor, "Multi Thread Comparison");
    }
    
    /**
     * Demo 6: Real-world Use Cases
     * Shows practical applications of single thread executors
     */
    private static void demonstrateRealWorldUseCases() {
        System.out.println("--- Demo 6: Real-world Use Cases ---");
        
        // Use case 1: Sequential file processing
        demonstrateFileProcessing();
        
        // Use case 2: Logging system
        demonstrateLoggingSystem();
        
        // Use case 3: State machine updates
        demonstrateStateMachine();
    }
    
    /**
     * Use case 1: Sequential File Processing
     */
    private static void demonstrateFileProcessing() {
        System.out.println("\nUse Case 1: Sequential File Processing");
        
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
            shutdownExecutorGracefully(executor, "File Processing");
        }
    }
    
    /**
     * Use case 2: Logging System
     */
    private static void demonstrateLoggingSystem() {
        System.out.println("\nUse Case 2: Asynchronous Logging System");
        
        ExecutorService logExecutor = Executors.newSingleThreadExecutor();
        
        try {
            System.out.println("Simulating application with background logging...");
            
            // Simulate application generating log messages
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
            shutdownExecutorGracefully(logExecutor, "Logging System");
        }
    }
    
    /**
     * Use case 3: State Machine Updates
     */
    private static void demonstrateStateMachine() {
        System.out.println("\nUse Case 3: State Machine Updates");
        
        ExecutorService stateExecutor = Executors.newSingleThreadExecutor();
        
        try {
            System.out.println("Managing application state transitions...");
            
            String[] states = {"INITIALIZING", "LOADING", "READY", "PROCESSING", "COMPLETE"};
            
            for (String state : states) {
                stateExecutor.execute(() -> {
                    System.out.printf("State transition to: %s%n", state);
                    sleep(600); // Simulate state transition work
                    System.out.printf("State %s fully activated%n", state);
                });
            }
            
            Thread.sleep(4000);
            System.out.println("All state transitions completed in correct order!");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownExecutorGracefully(stateExecutor, "State Machine");
        }
    }
    
    /**
     * Demo 7: Exception Handling in Single Thread Context
     * Shows how exceptions are handled and don't affect other tasks
     */
    private static void demonstrateExceptionHandling() {
        System.out.println("--- Demo 7: Exception Handling ---");
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        try {
            System.out.println("Submitting tasks with some that will fail...");
            
            for (int i = 1; i <= 5; i++) {
                final int taskId = i;
                executor.execute(() -> {
                    try {
                        System.out.printf("Task %d: Starting%n", taskId);
                        
                        if (taskId == 3) {
                            throw new RuntimeException("Simulated failure in task " + taskId);
                        }
                        
                        sleep(800);
                        System.out.printf("Task %d: Completed successfully%n", taskId);
                        
                    } catch (Exception e) {
                        System.err.printf("Task %d: Failed with error: %s%n", taskId, e.getMessage());
                        // Exception is caught, subsequent tasks continue
                    }
                });
            }
            
            Thread.sleep(5000);
            System.out.println("Notice: Task 3 failed, but tasks 4 and 5 still executed!\n");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownExecutorGracefully(executor, "Exception Handling");
        }
    }
    
    /**
     * Helper method to measure execution time
     */
    private static long measureExecutionTime(ExecutorService executor, String type, int taskCount) {
        long startTime = System.currentTimeMillis();
        
        for (int i = 1; i <= taskCount; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.printf("  [%s] Task %d on thread: %s%n", 
                        type, taskId, Thread.currentThread().getName());
                sleep(1000); // Each task takes 1 second
            });
        }
        
        // Wait for completion
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
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