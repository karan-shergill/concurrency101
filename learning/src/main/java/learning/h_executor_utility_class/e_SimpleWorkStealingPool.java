package learning.h_executor_utility_class;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Simple Work Stealing Thread Pool Executor Example for Beginners
 * 
 * This example demonstrates:
 * 1. Creating work stealing pools using Executors.newWorkStealingPool()
 * 2. Understanding work stealing algorithm and load balancing
 * 3. Comparing work stealing with other executor types
 * 4. Handling uneven workloads efficiently
 * 5. CPU-bound vs I/O-bound task performance
 * 6. Best practices and real-world applications
 * 7. Integration with CompletableFuture and parallel streams
 * 
 * Work Stealing Thread Pool:
 * - Based on ForkJoinPool but with simpler API
 * - Each thread has its own work queue (deque)
 * - Idle threads "steal" work from busy threads' queues
 * - Excellent load balancing for uneven workloads
 * - Default parallelism = Runtime.getRuntime().availableProcessors()
 * - Best for CPU-intensive tasks with variable execution times
 * - Introduced in Java 8 as a simpler alternative to ForkJoinPool
 */
public class e_SimpleWorkStealingPool {
    
    private static final AtomicInteger taskCounter = new AtomicInteger(0);
    private static final List<String> completedTasks = Collections.synchronizedList(new ArrayList<>());
    
    public static void main(String[] args) {
        System.out.println("=== Work Stealing Thread Pool Demo ===\n");
        
        // Demo 1: Basic work stealing pool concepts
        demonstrateBasicWorkStealingConcepts();
        
        // Demo 2: Work stealing in action with uneven workloads
        demonstrateWorkStealingInAction();
        
        // Demo 3: Comparing with other executor types
        compareExecutorTypes();
        
        // Demo 4: CPU-bound vs I/O-bound tasks
        demonstrateCpuVsIoBoundTasks();
        
        // Demo 5: Load balancing demonstration
        demonstrateLoadBalancing();
        
        // Demo 6: Integration with CompletableFuture
        demonstrateCompletableFutureIntegration();
        
        // Demo 7: Real-world use cases
        demonstrateRealWorldUseCases();
        
        // Demo 8: Best practices and pitfalls
        demonstrateBestPractices();
        
        System.out.println("\n=== All Work Stealing demos completed ===");
    }
    
    /**
     * Demo 1: Basic Work Stealing Pool Concepts
     * Introduction to work stealing and how it differs from other pools
     */
    private static void demonstrateBasicWorkStealingConcepts() {
        System.out.println("--- Demo 1: Basic Work Stealing Concepts ---");
        
        // Create work stealing pool with default parallelism
        ExecutorService workStealingPool = Executors.newWorkStealingPool();
        
        // Get the underlying ForkJoinPool to inspect properties
        ForkJoinPool forkJoinPool = (ForkJoinPool) workStealingPool;
        
        System.out.println("Work Stealing Pool Properties:");
        System.out.println("- Parallelism level: " + forkJoinPool.getParallelism());
        System.out.println("- Pool size: " + forkJoinPool.getPoolSize());
        System.out.println("- Available processors: " + Runtime.getRuntime().availableProcessors());
        System.out.println("- Async mode: " + forkJoinPool.getAsyncMode());
        
        // Create custom work stealing pool with specific parallelism
        ExecutorService customPool = Executors.newWorkStealingPool(4);
        ForkJoinPool customForkJoinPool = (ForkJoinPool) customPool;
        
        System.out.println("\nCustom Work Stealing Pool (4 threads):");
        System.out.println("- Parallelism level: " + customForkJoinPool.getParallelism());
        
        // Submit simple tasks to see basic behavior
        System.out.println("\nSubmitting simple tasks...");
        
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            workStealingPool.execute(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.printf("Task %d executing on: %s%n", taskId, threadName);
                sleep(1000);
                System.out.printf("Task %d completed on: %s%n", taskId, threadName);
            });
        }
        
        sleep(3000);
        
        workStealingPool.shutdown();
        customPool.shutdown();
        System.out.println("Basic concepts demo completed!\n");
    }
    
    /**
     * Demo 2: Work Stealing in Action with Uneven Workloads
     * Shows how work stealing balances uneven task durations
     */
    private static void demonstrateWorkStealingInAction() {
        System.out.println("--- Demo 2: Work Stealing in Action ---");
        
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
        try {
            for (Future<String> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        System.out.println("\nNotice: Threads that finished early automatically picked up new work!");
        System.out.println("This is work stealing providing automatic load balancing.");
        
        workStealingPool.shutdown();
        System.out.println("Work stealing demo completed!\n");
    }
    
    /**
     * Demo 3: Comparing Different Executor Types
     * Shows performance differences with uneven workloads
     */
    private static void compareExecutorTypes() {
        System.out.println("--- Demo 3: Executor Types Comparison ---");
        
        // Create test workload with uneven execution times
        int[] taskTimes = {100, 2000, 300, 1500, 200, 1000, 400, 1800, 250, 1200};
        
        System.out.println("Testing with uneven workload: " + java.util.Arrays.toString(taskTimes));
        System.out.println("(Times in milliseconds)\n");
        
        // Test Fixed Thread Pool
        System.out.println("1. Fixed Thread Pool (4 threads):");
        long fixedPoolTime = testExecutor(Executors.newFixedThreadPool(4), taskTimes, "FIXED");
        
        // Test Cached Thread Pool
        System.out.println("\n2. Cached Thread Pool:");
        long cachedPoolTime = testExecutor(Executors.newCachedThreadPool(), taskTimes, "CACHED");
        
        // Test Work Stealing Pool
        System.out.println("\n3. Work Stealing Pool:");
        long workStealingTime = testExecutor(Executors.newWorkStealingPool(4), taskTimes, "WORK-STEAL");
        
        // Compare results
        System.out.println("\n📊 Performance Comparison:");
        System.out.printf("Fixed Pool:      %d ms%n", fixedPoolTime);
        System.out.printf("Cached Pool:     %d ms%n", cachedPoolTime);
        System.out.printf("Work Stealing:   %d ms%n", workStealingTime);
        
        System.out.printf("\nWork Stealing vs Fixed: %.2fx %s%n", 
                (double) fixedPoolTime / workStealingTime,
                workStealingTime < fixedPoolTime ? "faster" : "slower");
        
        System.out.println("Notice: Work stealing should perform better with uneven workloads!");
        System.out.println("Comparison completed!\n");
    }
    
    /**
     * Demo 4: CPU-bound vs I/O-bound Tasks
     * Shows when work stealing is most effective
     */
    private static void demonstrateCpuVsIoBoundTasks() {
        System.out.println("--- Demo 4: CPU-bound vs I/O-bound Tasks ---");
        
        ExecutorService workStealingPool = Executors.newWorkStealingPool();
        
        // CPU-bound tasks (work stealing excels here)
        System.out.println("Testing CPU-bound tasks (mathematical computations)...");
        
        long cpuStart = System.currentTimeMillis();
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
        try {
            for (Future<Long> future : cpuFutures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        long cpuTime = System.currentTimeMillis() - cpuStart;
        System.out.printf("CPU-bound tasks completed in: %d ms%n%n", cpuTime);
        
        // I/O-bound tasks (work stealing less beneficial)
        System.out.println("Testing I/O-bound tasks (simulated file operations)...");
        
        long ioStart = System.currentTimeMillis();
        List<Future<String>> ioFutures = new ArrayList<>();
        
        for (int i = 0; i < 8; i++) {
            final int taskId = i;
            Future<String> future = workStealingPool.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.printf("I/O Task %d started on %s%n", taskId, threadName);
                
                // Simulate I/O operation (sleeping)
                sleep(1000 + taskId * 100);
                
                String result = "File-" + taskId + ".txt processed";
                System.out.printf("I/O Task %d completed on %s%n", taskId, threadName);
                return result;
            });
            ioFutures.add(future);
        }
        
        // Wait for I/O tasks
        try {
            for (Future<String> future : ioFutures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        long ioTime = System.currentTimeMillis() - ioStart;
        System.out.printf("I/O-bound tasks completed in: %d ms%n", ioTime);
        
        System.out.println("\n💡 Key Insight:");
        System.out.println("Work stealing is most effective for CPU-bound tasks with variable execution times.");
        System.out.println("For I/O-bound tasks, the benefit is minimal since threads are mostly waiting.");
        
        workStealingPool.shutdown();
        System.out.println("CPU vs I/O demo completed!\n");
    }
    
    /**
     * Demo 5: Load Balancing Demonstration
     * Visualizes how work is distributed across threads
     */
    private static void demonstrateLoadBalancing() {
        System.out.println("--- Demo 5: Load Balancing Demonstration ---");
        
        ExecutorService workStealingPool = Executors.newWorkStealingPool(4);
        
        // Track tasks per thread
        ConcurrentHashMap<String, AtomicInteger> tasksPerThread = new ConcurrentHashMap<>();
        
        System.out.println("Submitting 20 tasks with random execution times...");
        System.out.println("Tracking which threads execute which tasks:\n");
        
        List<Future<Void>> futures = new ArrayList<>();
        Random random = new Random(42);
        
        for (int i = 1; i <= 20; i++) {
            final int taskId = i;
            Future<Void> future = workStealingPool.submit(() -> {
                String threadName = Thread.currentThread().getName();
                
                // Track task count per thread
                tasksPerThread.computeIfAbsent(threadName, k -> new AtomicInteger(0)).incrementAndGet();
                
                int executionTime = 200 + random.nextInt(800); // 200-1000ms
                
                System.out.printf("Task %02d -> %s (time: %d ms)%n", taskId, threadName, executionTime);
                
                // Simulate variable work
                simulateCpuWork(executionTime);
                
                return null;
            });
            futures.add(future);
        }
        
        // Wait for completion
        try {
            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        // Display load balancing results
        System.out.println("\n📈 Load Balancing Results:");
        System.out.println("Tasks executed per thread:");
        
        tasksPerThread.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().get() - e1.getValue().get())
                .forEach(entry -> {
                    String threadName = entry.getKey();
                    int taskCount = entry.getValue().get();
                    String bar = "█".repeat(taskCount);
                    System.out.printf("%-25s: %2d tasks %s%n", threadName, taskCount, bar);
                });
        
        // Calculate load distribution
        int totalTasks = tasksPerThread.values().stream().mapToInt(AtomicInteger::get).sum();
        double averageTasksPerThread = (double) totalTasks / tasksPerThread.size();
        
        System.out.printf("\nTotal tasks: %d%n", totalTasks);
        System.out.printf("Average per thread: %.1f%n", averageTasksPerThread);
        System.out.println("Notice: Work stealing automatically balanced the load!");
        
        workStealingPool.shutdown();
        System.out.println("Load balancing demo completed!\n");
    }
    
    /**
     * Demo 6: Integration with CompletableFuture
     * Shows how work stealing integrates with modern async APIs
     */
    private static void demonstrateCompletableFutureIntegration() {
        System.out.println("--- Demo 6: CompletableFuture Integration ---");
        
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
        
        try {
            String result = combinedResult.get(10, TimeUnit.SECONDS);
            System.out.println("\nFinal combined result:");
            System.out.println(result);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        System.out.println("\nNotice: Work stealing automatically managed the complex async workflow!");
        
        workStealingPool.shutdown();
        System.out.println("CompletableFuture integration demo completed!\n");
    }
    
    /**
     * Demo 7: Real-world Use Cases
     * Practical examples where work stealing excels
     */
    private static void demonstrateRealWorldUseCases() {
        System.out.println("--- Demo 7: Real-world Use Cases ---");
        
        // Use case 1: Parallel data processing
        demonstrateParallelDataProcessing();
        
        // Use case 2: Image processing simulation
        demonstrateImageProcessing();
        
        // Use case 3: Web scraping simulation
        demonstrateWebScraping();
    }
    
    /**
     * Real-world use case 1: Parallel Data Processing
     */
    private static void demonstrateParallelDataProcessing() {
        System.out.println("\nUse Case 1: Parallel Data Processing");
        
        ExecutorService workStealingPool = Executors.newWorkStealingPool();
        
        // Simulate processing different sized datasets
        int[] datasetSizes = {1000, 5000, 2000, 8000, 1500, 3000, 6000, 2500};
        
        System.out.println("Processing datasets of varying sizes in parallel...");
        
        List<Future<String>> results = new ArrayList<>();
        
        for (int i = 0; i < datasetSizes.length; i++) {
            final int datasetId = i + 1;
            final int size = datasetSizes[i];
            
            Future<String> future = workStealingPool.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.printf("Dataset %d (size: %d) started on %s%n", datasetId, size, threadName);
                
                // Simulate data processing time proportional to size
                int processingTime = size / 10; // milliseconds
                simulateCpuWork(processingTime);
                
                String result = String.format("Dataset %d processed (%d records)", datasetId, size);
                System.out.printf("Dataset %d completed on %s%n", datasetId, threadName);
                
                return result;
            });
            
            results.add(future);
        }
        
        // Collect results
        try {
            for (Future<String> future : results) {
                System.out.println("Result: " + future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        workStealingPool.shutdown();
        System.out.println("Parallel data processing completed!");
    }
    
    /**
     * Real-world use case 2: Image Processing Simulation
     */
    private static void demonstrateImageProcessing() {
        System.out.println("\nUse Case 2: Image Processing Simulation");
        
        ExecutorService workStealingPool = Executors.newWorkStealingPool();
        
        // Simulate processing images of different complexities
        String[] imageTypes = {"thumbnail", "standard", "hd", "4k", "thumbnail", "hd", "standard", "4k"};
        int[] processingTimes = {100, 500, 1000, 2000, 100, 1000, 500, 2000}; // milliseconds
        
        System.out.println("Processing images of different resolutions...");
        
        List<Future<String>> results = new ArrayList<>();
        
        for (int i = 0; i < imageTypes.length; i++) {
            final int imageId = i + 1;
            final String imageType = imageTypes[i];
            final int processingTime = processingTimes[i];
            
            Future<String> future = workStealingPool.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.printf("Image %d (%s) processing started on %s%n", 
                        imageId, imageType, threadName);
                
                // Simulate image processing
                simulateCpuWork(processingTime);
                
                String result = String.format("Image %d (%s) processed", imageId, imageType);
                System.out.printf("Image %d (%s) completed on %s%n", 
                        imageId, imageType, threadName);
                
                return result;
            });
            
            results.add(future);
        }
        
        // Wait for all images to be processed
        try {
            for (Future<String> future : results) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        workStealingPool.shutdown();
        System.out.println("Image processing completed!");
    }
    
    /**
     * Real-world use case 3: Web Scraping Simulation
     */
    private static void demonstrateWebScraping() {
        System.out.println("\nUse Case 3: Web Scraping Simulation");
        
        ExecutorService workStealingPool = Executors.newWorkStealingPool();
        
        // Simulate scraping different types of web pages
        String[] websites = {
            "news-site.com", "blog.com", "e-commerce.com", "social-media.com",
            "wiki.org", "forum.net", "documentation.io", "portfolio.dev"
        };
        
        System.out.println("Scraping multiple websites concurrently...");
        
        List<Future<String>> results = new ArrayList<>();
        Random random = new Random(42);
        
        for (String website : websites) {
            Future<String> future = workStealingPool.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.printf("Scraping %s on %s%n", website, threadName);
                
                // Simulate variable scraping time (network + processing)
                int scrapingTime = 300 + random.nextInt(1000); // 300-1300ms
                sleep(scrapingTime / 3); // Network time
                simulateCpuWork(scrapingTime * 2 / 3); // Processing time
                
                String result = String.format("Scraped %s (%d items found)", 
                        website, 10 + random.nextInt(90));
                System.out.printf("Completed scraping %s on %s%n", website, threadName);
                
                return result;
            });
            
            results.add(future);
        }
        
        // Collect scraping results
        try {
            System.out.println("\nScraping Results:");
            for (Future<String> future : results) {
                System.out.println("- " + future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        workStealingPool.shutdown();
        System.out.println("Web scraping simulation completed!");
    }
    
    /**
     * Demo 8: Best Practices and Common Pitfalls
     */
    private static void demonstrateBestPractices() {
        System.out.println("--- Demo 8: Best Practices ---");
        
        System.out.println("\n✅ WHEN TO USE Work Stealing Pool:");
        System.out.println("- CPU-intensive tasks with variable execution times");
        System.out.println("- Parallel data processing with uneven workloads");
        System.out.println("- Mathematical computations and algorithms");
        System.out.println("- Image/video processing with different complexities");
        System.out.println("- Batch processing of varied-size items");
        System.out.println("- Modern async workflows with CompletableFuture");
        
        System.out.println("\n❌ WHEN NOT TO USE Work Stealing Pool:");
        System.out.println("- Pure I/O-bound tasks (minimal CPU work)");
        System.out.println("- Tasks requiring strict ordering");
        System.out.println("- Very short-running tasks (overhead > benefit)");
        System.out.println("- Tasks that need dedicated thread resources");
        System.out.println("- Real-time applications with strict timing requirements");
        
        System.out.println("\n🎯 BEST PRACTICES:");
        System.out.println("- Use default parallelism level for most cases");
        System.out.println("- Prefer work stealing for heterogeneous workloads");
        System.out.println("- Combine with CompletableFuture for complex workflows");
        System.out.println("- Monitor thread utilization and task distribution");
        System.out.println("- Avoid blocking operations in work stealing tasks");
        System.out.println("- Consider ForkJoinPool for recursive divide-and-conquer");
        
        System.out.println("\n⚡ PERFORMANCE TIPS:");
        System.out.println("- Ensure tasks are CPU-bound for maximum benefit");
        System.out.println("- Avoid creating too many small tasks");
        System.out.println("- Use parallel streams for simple data processing");
        System.out.println("- Profile your application to verify performance gains");
        
        System.out.println("\nBest practices demo completed!\n");
    }
    
    // Helper methods
    
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private static void simulateCpuWork(long millis) {
        long endTime = System.currentTimeMillis() + millis;
        while (System.currentTimeMillis() < endTime) {
            // Simulate CPU work with some calculations
            Math.sqrt(Math.random() * 1000);
        }
    }
    
    private static long calculatePrimes(int limit) {
        return IntStream.range(2, limit)
                .filter(e_SimpleWorkStealingPool::isPrime)
                .count();
    }
    
    private static boolean isPrime(int number) {
        if (number < 2) return false;
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }
        return true;
    }
    
    private static long testExecutor(ExecutorService executor, int[] taskTimes, String executorType) {
        long startTime = System.currentTimeMillis();
        List<Future<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < taskTimes.length; i++) {
            final int taskId = i + 1;
            final int executionTime = taskTimes[i];
            
            Future<Void> future = executor.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.printf("  [%s] Task %d on %s (time: %d ms)%n", 
                        executorType, taskId, threadName, executionTime);
                
                simulateCpuWork(executionTime);
                return null;
            });
            
            futures.add(future);
        }
        
        // Wait for all tasks
        try {
            for (Future<Void> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        executor.shutdown();
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.printf("  Total time: %d ms%n", totalTime);
        
        return totalTime;
    }
} 