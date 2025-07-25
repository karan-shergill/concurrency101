package learning.f_thread_pools;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;

/**
 * ThreadPoolExecutor Demo - Demonstrates thread pool management and task execution
 * 
 * ThreadPoolExecutor is the core implementation of ExecutorService that manages
 * a pool of worker threads to execute submitted tasks efficiently.
 * 
 * Key benefits:
 * - Reuses threads instead of creating new ones for each task
 * - Controls the number of concurrent threads
 * - Provides task queuing and scheduling
 * - Better resource management and performance
 */
public class a_SimpleThreadPoolExecutor {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ThreadPoolExecutor Demo ===\n");
        
        // Demo 1: Basic ThreadPoolExecutor usage
        demonstrateBasicThreadPool();
        
        // Demo 2: Different types of thread pools
        demonstrateDifferentPoolTypes();
        
        // Demo 3: Custom ThreadPoolExecutor configuration
        demonstrateCustomThreadPool();
        
        // Demo 4: Task submission methods
        demonstrateTaskSubmission();
        
        // Demo 5: Monitoring and statistics
        demonstrateMonitoring();
        
        // Demo 6: Rejection policies
        demonstrateRejectionPolicies();
    }
    
    /**
     * Demonstrates basic ThreadPoolExecutor usage
     */
    private static void demonstrateBasicThreadPool() throws InterruptedException {
        System.out.println("1. Basic ThreadPoolExecutor Usage:");
        
        // Create a thread pool with 3 core threads and 5 maximum threads
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            3,                      // corePoolSize
            5,                      // maximumPoolSize  
            60L,                    // keepAliveTime
            TimeUnit.SECONDS,       // time unit
            new LinkedBlockingQueue<Runnable>() // work queue
        );
        
        // Submit some tasks
        for (int i = 1; i <= 8; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Task " + taskId + " executing on thread: " + 
                                 Thread.currentThread().getName());
                try {
                    Thread.sleep(2000); // Simulate work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Task " + taskId + " completed");
            });
        }
        
        // Proper shutdown
        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
        
        System.out.println("Basic thread pool demo completed\n");
    }
    
    /**
     * Demonstrates different types of pre-configured thread pools
     */
    private static void demonstrateDifferentPoolTypes() throws InterruptedException {
        System.out.println("2. Different Thread Pool Types:");
        
        // Fixed Thread Pool
        System.out.println("Fixed Thread Pool (3 threads):");
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
        submitSampleTasks(fixedPool, "FixedPool");
        shutdownAndWait(fixedPool);
        
        // Cached Thread Pool
        System.out.println("\nCached Thread Pool (creates threads as needed):");
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        submitSampleTasks(cachedPool, "CachedPool");
        shutdownAndWait(cachedPool);
        
        // Single Thread Executor
        System.out.println("\nSingle Thread Executor:");
        ExecutorService singlePool = Executors.newSingleThreadExecutor();
        submitSampleTasks(singlePool, "SinglePool");
        shutdownAndWait(singlePool);
        
        System.out.println("Different pool types demo completed\n");
    }
    
    /**
     * Demonstrates custom ThreadPoolExecutor configuration
     */
    private static void demonstrateCustomThreadPool() throws InterruptedException {
        System.out.println("3. Custom ThreadPoolExecutor Configuration:");
        
        // Custom thread factory
        ThreadFactory customThreadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "CustomWorker-" + threadNumber.getAndIncrement());
                t.setDaemon(false);
                t.setPriority(Thread.NORM_PRIORITY);
                return t;
            }
        };
        
        // Create custom thread pool
        ThreadPoolExecutor customExecutor = new ThreadPoolExecutor(
            2,                          // corePoolSize
            4,                          // maximumPoolSize
            30L,                        // keepAliveTime
            TimeUnit.SECONDS,           // time unit
            new ArrayBlockingQueue<>(10), // bounded queue
            customThreadFactory,        // custom thread factory
            new ThreadPoolExecutor.CallerRunsPolicy() // rejection policy
        );
        
        // Enable core thread timeout
        customExecutor.allowCoreThreadTimeOut(true);
        
        System.out.println("Initial pool size: " + customExecutor.getPoolSize());
        
        // Submit tasks
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            customExecutor.submit(() -> {
                System.out.println("Custom task " + taskId + " on " + 
                                 Thread.currentThread().getName());
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        Thread.sleep(1000);
        System.out.println("Active pool size: " + customExecutor.getPoolSize());
        
        shutdownAndWait(customExecutor);
        System.out.println("Custom thread pool demo completed\n");
    }
    
    /**
     * Demonstrates different task submission methods
     */
    private static void demonstrateTaskSubmission() throws InterruptedException {
        System.out.println("4. Task Submission Methods:");
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // 1. execute() - fire and forget
        System.out.println("Using execute() method:");
        executor.execute(() -> {
            System.out.println("Execute task running on: " + Thread.currentThread().getName());
        });
        
        // 2. submit() with Runnable - returns Future<?>
        System.out.println("Using submit() with Runnable:");
        Future<?> runnableFuture = executor.submit(() -> {
            System.out.println("Submit Runnable task running on: " + Thread.currentThread().getName());
        });
        
        try {
            runnableFuture.get(); // Wait for completion
            System.out.println("Runnable task completed successfully");
        } catch (ExecutionException e) {
            System.out.println("Runnable task failed: " + e.getCause());
        }
        
        // 3. submit() with Callable - returns Future<T>
        System.out.println("Using submit() with Callable:");
        Future<String> callableFuture = executor.submit(() -> {
            Thread.sleep(1000);
            return "Result from " + Thread.currentThread().getName();
        });
        
        try {
            String result = callableFuture.get(2, TimeUnit.SECONDS);
            System.out.println("Callable result: " + result);
        } catch (ExecutionException | TimeoutException e) {
            System.out.println("Callable task failed or timed out: " + e);
        }
        
        // 4. invokeAll() - submit multiple tasks
        System.out.println("Using invokeAll():");
        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            final int taskId = i;
            tasks.add(() -> "Task " + taskId + " result");
        }
        
        List<Future<String>> futures = executor.invokeAll(tasks);
        for (Future<String> future : futures) {
            try {
                System.out.println("InvokeAll result: " + future.get());
            } catch (ExecutionException e) {
                System.out.println("Task failed: " + e.getCause());
            }
        }
        
        shutdownAndWait(executor);
        System.out.println("Task submission demo completed\n");
    }
    
    /**
     * Demonstrates monitoring and statistics
     */
    private static void demonstrateMonitoring() throws InterruptedException {
        System.out.println("5. Monitoring and Statistics:");
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, 4, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(5)
        );
        
        // Submit tasks to monitor
        for (int i = 1; i <= 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Monitor statistics
        for (int i = 0; i < 5; i++) {
            Thread.sleep(500);
            System.out.printf("Pool Size: %d, Active: %d, Queue Size: %d, Completed: %d%n",
                executor.getPoolSize(),
                executor.getActiveCount(), 
                executor.getQueue().size(),
                executor.getCompletedTaskCount());
        }
        
        shutdownAndWait(executor);
        
        // Final statistics
        System.out.printf("Final - Total tasks: %d, Completed: %d, Largest pool size: %d%n",
            executor.getTaskCount(),
            executor.getCompletedTaskCount(),
            executor.getLargestPoolSize());
        
        System.out.println("Monitoring demo completed\n");
    }
    
    /**
     * Demonstrates different rejection policies
     */
    private static void demonstrateRejectionPolicies() throws InterruptedException {
        System.out.println("6. Rejection Policies:");
        
        // Small pool that will quickly reach capacity
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1, 1, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2), // Small queue
            new ThreadPoolExecutor.CallerRunsPolicy() // Rejection policy
        );
        
        System.out.println("Submitting tasks to demonstrate CallerRunsPolicy...");
        
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            try {
                executor.submit(() -> {
                    System.out.println("Task " + taskId + " running on: " + 
                                     Thread.currentThread().getName());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                System.out.println("Task " + taskId + " submitted");
            } catch (RejectedExecutionException e) {
                System.out.println("Task " + taskId + " was rejected: " + e.getMessage());
            }
        }
        
        shutdownAndWait(executor);
        System.out.println("Rejection policies demo completed\n");
    }
    
    // Helper methods
    
    private static void submitSampleTasks(ExecutorService executor, String poolName) {
        for (int i = 1; i <= 4; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println(poolName + " - Task " + taskId + " on " + 
                                 Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
    
    private static void shutdownAndWait(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            System.out.println("Forcing shutdown...");
            executor.shutdownNow();
        }
    }
} 