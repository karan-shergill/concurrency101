package learning.f_thread_pools;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Practical ThreadPoolExecutor Examples
 * 
 * Real-world scenarios demonstrating effective ThreadPoolExecutor usage:
 * - File processing system
 * - Web server request handling simulation
 * - Batch processing with results collection
 */
public class b_PracticalThreadPoolUsage {
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Practical ThreadPoolExecutor Usage ===\n");
        
        // Demo 1: File processing system
        demonstrateFileProcessingSystem();
        
        // Demo 2: Web server simulation
        demonstrateWebServerSimulation();
        
        // Demo 3: Batch processing with result collection
        demonstrateBatchProcessing();
        
        // Demo 4: Producer-Consumer with ThreadPool
        demonstrateProducerConsumerThreadPool();
    }
    
    /**
     * Simulates a file processing system using ThreadPoolExecutor
     */
    private static void demonstrateFileProcessingSystem() throws InterruptedException {
        System.out.println("1. File Processing System Simulation:");
        
        // Configure thread pool for I/O intensive tasks
        ThreadPoolExecutor fileProcessor = new ThreadPoolExecutor(
            4,                          // Core threads for I/O operations
            8,                          // Max threads
            60L, TimeUnit.SECONDS,      // Keep alive time
            new LinkedBlockingQueue<>(100), // Large queue for pending files
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "FileProcessor-" + counter.getAndIncrement());
                    t.setDaemon(false);
                    return t;
                }
            }
        );
        
        // Simulate file processing tasks
        String[] fileTypes = {"document.pdf", "image.jpg", "video.mp4", "data.csv", "archive.zip"};
        List<Future<ProcessingResult>> futures = new ArrayList<>();
        
        System.out.println("Submitting file processing tasks...");
        
        for (int i = 1; i <= 12; i++) {
            final String fileName = fileTypes[i % fileTypes.length] + "_" + i;
            
            Future<ProcessingResult> future = fileProcessor.submit(new FileProcessingTask(fileName));
            futures.add(future);
        }
        
        // Collect results
        int successCount = 0;
        int failureCount = 0;
        
        for (Future<ProcessingResult> future : futures) {
            try {
                ProcessingResult result = future.get();
                if (result.isSuccess()) {
                    successCount++;
                    System.out.println("✓ " + result.getFileName() + " processed in " + 
                                     result.getProcessingTime() + "ms");
                } else {
                    failureCount++;
                    System.out.println("✗ " + result.getFileName() + " failed: " + result.getError());
                }
            } catch (ExecutionException e) {
                failureCount++;
                System.out.println("✗ Task execution failed: " + e.getCause());
            }
        }
        
        System.out.printf("File processing completed: %d successful, %d failed%n", 
                         successCount, failureCount);
        
        fileProcessor.shutdown();
        fileProcessor.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("File processing system demo completed\n");
    }
    
    /**
     * Simulates web server request handling
     */
    private static void demonstrateWebServerSimulation() throws InterruptedException {
        System.out.println("2. Web Server Request Handling Simulation:");
        
        // Web server thread pool configuration
        ThreadPoolExecutor webServer = new ThreadPoolExecutor(
            10,                         // Core threads for baseline load
            50,                         // Max threads for peak load
            30L, TimeUnit.SECONDS,      // Thread timeout
            new LinkedBlockingQueue<>(200), // Request queue
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "WebServer-" + counter.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy() // Backpressure handling
        );
        
        // Simulate incoming requests
        AtomicInteger requestCounter = new AtomicInteger(1);
        AtomicInteger successfulRequests = new AtomicInteger(0);
        
        System.out.println("Simulating web requests...");
        
        // Submit requests at different rates
        for (int burst = 1; burst <= 3; burst++) {
            System.out.println("Request burst " + burst + ":");
            
            for (int i = 1; i <= 20; i++) {
                final int requestId = requestCounter.getAndIncrement();
                
                webServer.submit(() -> {
                    try {
                        // Simulate request processing
                        String requestType = (requestId % 3 == 0) ? "Database" : 
                                           (requestId % 2 == 0) ? "API" : "Static";
                        
                        long startTime = System.currentTimeMillis();
                        int processingTime = handleRequest(requestType);
                        long endTime = System.currentTimeMillis();
                        
                        successfulRequests.incrementAndGet();
                        
                        System.out.printf("Request %d (%s) handled by %s in %dms%n",
                                        requestId, requestType, 
                                        Thread.currentThread().getName(),
                                        (endTime - startTime));
                        
                    } catch (Exception e) {
                        System.out.println("Request " + requestId + " failed: " + e.getMessage());
                    }
                });
                
                Thread.sleep(50); // Simulate request arrival rate
            }
            
            Thread.sleep(2000); // Pause between bursts
            
            System.out.printf("Burst %d stats - Pool: %d, Active: %d, Queue: %d%n",
                            burst, webServer.getPoolSize(), 
                            webServer.getActiveCount(), 
                            webServer.getQueue().size());
        }
        
        webServer.shutdown();
        webServer.awaitTermination(10, TimeUnit.SECONDS);
        
        System.out.printf("Web server simulation completed: %d requests processed%n", 
                         successfulRequests.get());
        System.out.println("Web server demo completed\n");
    }
    
    /**
     * Demonstrates batch processing with result collection
     */
    private static void demonstrateBatchProcessing() throws InterruptedException {
        System.out.println("3. Batch Processing with Result Collection:");
        
        // Batch processing configuration
        ThreadPoolExecutor batchProcessor = new ThreadPoolExecutor(
            3, 6, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>()
        );
        
        // Create batch jobs
        List<BatchJob> jobs = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            jobs.add(new BatchJob("Batch-" + i, 50 + (i * 25))); // Different batch sizes
        }
        
        System.out.println("Processing " + jobs.size() + " batch jobs...");
        
        // Submit all jobs and collect futures
        List<Future<BatchResult>> futures = new ArrayList<>();
        for (BatchJob job : jobs) {
            futures.add(batchProcessor.submit(job));
        }
        
        // Process results as they complete
        CompletionService<BatchResult> completionService = 
            new ExecutorCompletionService<>(batchProcessor);
        
        // Re-submit with completion service for demonstration
        for (BatchJob job : jobs) {
            completionService.submit(job);
        }
        
        int processedJobs = 0;
        int totalItems = 0;
        
        try {
            for (int i = 0; i < jobs.size(); i++) {
                Future<BatchResult> completedFuture = completionService.take(); // Blocks until one completes
                BatchResult result = completedFuture.get();
                
                processedJobs++;
                totalItems += result.getItemsProcessed();
                
                System.out.printf("Completed: %s - %d items in %dms (%.2f items/sec)%n",
                                result.getJobName(),
                                result.getItemsProcessed(),
                                result.getProcessingTime(),
                                result.getItemsPerSecond());
            }
        } catch (ExecutionException e) {
            System.out.println("Batch job failed: " + e.getCause());
        }
        
        batchProcessor.shutdown();
        batchProcessor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.printf("Batch processing completed: %d jobs, %d total items processed%n",
                         processedJobs, totalItems);
        System.out.println("Batch processing demo completed\n");
    }
    
    /**
     * Demonstrates Producer-Consumer pattern with ThreadPoolExecutor
     */
    private static void demonstrateProducerConsumerThreadPool() throws InterruptedException {
        System.out.println("4. Producer-Consumer with ThreadPool:");
        
        BlockingQueue<WorkItem> workQueue = new LinkedBlockingQueue<>(50);
        
        // Producer thread pool
        ThreadPoolExecutor producers = new ThreadPoolExecutor(
            2, 2, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>()
        );
        
        // Consumer thread pool  
        ThreadPoolExecutor consumers = new ThreadPoolExecutor(
            3, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>()
        );
        
        AtomicInteger producedCount = new AtomicInteger(0);
        AtomicInteger consumedCount = new AtomicInteger(0);
        
        // Start producers
        for (int i = 1; i <= 2; i++) {
            final int producerId = i;
            producers.submit(() -> {
                try {
                    for (int j = 1; j <= 15; j++) {
                        WorkItem item = new WorkItem("Producer-" + producerId + "-Item-" + j);
                        workQueue.put(item);
                        producedCount.incrementAndGet();
                        System.out.println("Produced: " + item.getId());
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Start consumers
        for (int i = 1; i <= 3; i++) {
            final int consumerId = i;
            consumers.submit(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        WorkItem item = workQueue.poll(2, TimeUnit.SECONDS);
                        if (item != null) {
                            // Simulate processing
                            Thread.sleep(300);
                            consumedCount.incrementAndGet();
                            System.out.println("Consumer-" + consumerId + " processed: " + item.getId());
                        } else {
                            break; // No more items
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Monitor progress
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            System.out.printf("Progress - Produced: %d, Consumed: %d, Queue: %d%n",
                            producedCount.get(), consumedCount.get(), workQueue.size());
            
            if (producedCount.get() > 0 && consumedCount.get() >= producedCount.get()) {
                break;
            }
        }
        
        producers.shutdown();
        consumers.shutdown();
        
        producers.awaitTermination(5, TimeUnit.SECONDS);
        consumers.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.printf("Producer-Consumer completed - Produced: %d, Consumed: %d%n",
                         producedCount.get(), consumedCount.get());
        System.out.println("Producer-Consumer demo completed\n");
    }
    
    // Helper classes and methods
    
    private static int handleRequest(String requestType) throws InterruptedException {
        Random random = new Random();
        int baseTime = 100;
        
        switch (requestType) {
            case "Database":
                Thread.sleep(baseTime + random.nextInt(200)); // 100-300ms
                return baseTime + 200;
            case "API":
                Thread.sleep(baseTime + random.nextInt(100)); // 100-200ms  
                return baseTime + 100;
            case "Static":
                Thread.sleep(baseTime + random.nextInt(50));  // 100-150ms
                return baseTime + 50;
            default:
                Thread.sleep(baseTime);
                return baseTime;
        }
    }
    
    // Supporting classes
    
    static class FileProcessingTask implements Callable<ProcessingResult> {
        private final String fileName;
        
        public FileProcessingTask(String fileName) {
            this.fileName = fileName;
        }
        
        @Override
        public ProcessingResult call() {
            long startTime = System.currentTimeMillis();
            
            try {
                // Simulate file processing based on file type
                if (fileName.contains("video")) {
                    Thread.sleep(2000 + new Random().nextInt(1000)); // 2-3 seconds
                } else if (fileName.contains("image")) {
                    Thread.sleep(800 + new Random().nextInt(400));   // 0.8-1.2 seconds
                } else {
                    Thread.sleep(300 + new Random().nextInt(200));   // 0.3-0.5 seconds
                }
                
                // Simulate occasional failures
                if (new Random().nextInt(10) == 0) {
                    throw new RuntimeException("Processing error");
                }
                
                long processingTime = System.currentTimeMillis() - startTime;
                return new ProcessingResult(fileName, true, processingTime, null);
                
            } catch (Exception e) {
                long processingTime = System.currentTimeMillis() - startTime;
                return new ProcessingResult(fileName, false, processingTime, e.getMessage());
            }
        }
    }
    
    static class ProcessingResult {
        private final String fileName;
        private final boolean success;
        private final long processingTime;
        private final String error;
        
        public ProcessingResult(String fileName, boolean success, long processingTime, String error) {
            this.fileName = fileName;
            this.success = success;
            this.processingTime = processingTime;
            this.error = error;
        }
        
        public String getFileName() { return fileName; }
        public boolean isSuccess() { return success; }
        public long getProcessingTime() { return processingTime; }
        public String getError() { return error; }
    }
    
    static class BatchJob implements Callable<BatchResult> {
        private final String jobName;
        private final int itemCount;
        
        public BatchJob(String jobName, int itemCount) {
            this.jobName = jobName;
            this.itemCount = itemCount;
        }
        
        @Override
        public BatchResult call() throws Exception {
            long startTime = System.currentTimeMillis();
            
            // Simulate batch processing
            for (int i = 0; i < itemCount; i++) {
                Thread.sleep(20); // Process each item
            }
            
            long processingTime = System.currentTimeMillis() - startTime;
            return new BatchResult(jobName, itemCount, processingTime);
        }
    }
    
    static class BatchResult {
        private final String jobName;
        private final int itemsProcessed;
        private final long processingTime;
        
        public BatchResult(String jobName, int itemsProcessed, long processingTime) {
            this.jobName = jobName;
            this.itemsProcessed = itemsProcessed;
            this.processingTime = processingTime;
        }
        
        public String getJobName() { return jobName; }
        public int getItemsProcessed() { return itemsProcessed; }
        public long getProcessingTime() { return processingTime; }
        public double getItemsPerSecond() {
            return (double) itemsProcessed / (processingTime / 1000.0);
        }
    }
    
    static class WorkItem {
        private final String id;
        
        public WorkItem(String id) {
            this.id = id;
        }
        
        public String getId() { return id; }
    }
} 