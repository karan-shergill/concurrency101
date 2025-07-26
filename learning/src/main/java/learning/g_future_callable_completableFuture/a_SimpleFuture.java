package learning.g_future_callable_completableFuture;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates Future interface in Java Concurrency
 * 
 * Future represents the result of an asynchronous computation.
 * It provides methods to check if the computation is complete,
 * wait for its completion, and retrieve the result.
 */
public class a_SimpleFuture {
    
    public static void main(String[] args) {
        System.out.println("=== Future Examples ===\n");
        
        // Example 1: Basic Future usage
        basicFutureExample();
        
        // Example 2: Future with timeout
        futureWithTimeoutExample();
        
        // Example 3: Future cancellation
        futureCancellationExample();
        
        // Example 4: Multiple Futures
        multipleFuturesExample();
        
        // Example 5: Exception handling
        futureExceptionHandlingExample();
    }
    
    /**
     * Example 1: Basic Future usage with Callable
     */
    private static void basicFutureExample() {
        System.out.println("1. Basic Future Example:");
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        // Submit a Callable task that returns a result
        Future<String> future = executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(2000); // Simulate some work
                return "Hello from Future!";
            }
        });
        
        try {
            System.out.println("Task submitted, doing other work...");
            
            // Check if task is done (non-blocking)
            System.out.println("Is done? " + future.isDone());
            
            // Get the result (blocking call)
            String result = future.get();
            System.out.println("Result: " + result);
            System.out.println("Is done now? " + future.isDone());
            
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
    }
    
    /**
     * Example 2: Future with timeout to avoid indefinite blocking
     */
    private static void futureWithTimeoutExample() {
        System.out.println("2. Future with Timeout Example:");
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        // Submit a long-running task
        Future<Integer> future = executor.submit(() -> {
            try {
                Thread.sleep(5000); // Long operation
                return 42;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
        
        try {
            // Wait for result with timeout
            Integer result = future.get(3, TimeUnit.SECONDS);
            System.out.println("Result: " + result);
            
        } catch (TimeoutException e) {
            System.out.println("Task timed out!");
            future.cancel(true); // Cancel the task
            
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
    }
    
    /**
     * Example 3: Future cancellation
     */
    private static void futureCancellationExample() {
        System.out.println("3. Future Cancellation Example:");
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        Future<String> future = executor.submit(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Task was interrupted!");
                        return "Cancelled";
                    }
                    Thread.sleep(1000);
                    System.out.println("Working... " + (i + 1));
                }
                return "Completed normally";
            } catch (InterruptedException e) {
                System.out.println("Task interrupted during sleep");
                Thread.currentThread().interrupt();
                return "Interrupted";
            }
        });
        
        try {
            // Let it run for a bit
            Thread.sleep(3000);
            
            // Cancel the task
            boolean cancelled = future.cancel(true); // true = interrupt if running
            System.out.println("Cancellation successful: " + cancelled);
            System.out.println("Is cancelled: " + future.isCancelled());
            System.out.println("Is done: " + future.isDone());
            
            // Trying to get result of cancelled task
            try {
                String result = future.get();
                System.out.println("Result: " + result);
            } catch (CancellationException e) {
                System.out.println("Cannot get result - task was cancelled");
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Error getting result: " + e.getMessage());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
    }
    
    /**
     * Example 4: Working with multiple Futures
     */
    private static void multipleFuturesExample() {
        System.out.println("4. Multiple Futures Example:");
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // Submit multiple tasks
        Future<Integer> future1 = executor.submit(() -> {
            try {
                Thread.sleep(1000);
                return 10;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
        
        Future<Integer> future2 = executor.submit(() -> {
            try {
                Thread.sleep(1500);
                return 20;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
        
        Future<Integer> future3 = executor.submit(() -> {
            try {
                Thread.sleep(500);
                return 30;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
        
        try {
            // Collect results as they complete
            System.out.println("Waiting for results...");
            
            Integer result1 = future1.get();
            System.out.println("Task 1 completed: " + result1);
            
            Integer result2 = future2.get();
            System.out.println("Task 2 completed: " + result2);
            
            Integer result3 = future3.get();
            System.out.println("Task 3 completed: " + result3);
            
            Integer sum = result1 + result2 + result3;
            System.out.println("Total sum: " + sum);
            
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
    }
    
    /**
     * Example 5: Exception handling in Future
     */
    private static void futureExceptionHandlingExample() {
        System.out.println("5. Future Exception Handling Example:");
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        // Submit a task that throws an exception
        Future<String> future = executor.submit(() -> {
            Thread.sleep(1000);
            if (Math.random() > 0.5) {
                throw new RuntimeException("Something went wrong!");
            }
            return "Success!";
        });
        
        try {
            String result = future.get();
            System.out.println("Result: " + result);
            
        } catch (ExecutionException e) {
            // The actual exception is wrapped in ExecutionException
            Throwable cause = e.getCause();
            System.out.println("Task threw exception: " + cause.getMessage());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupted while waiting");
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
    }
}

/**
 * Key Points about Future:
 * 
 * 1. Future represents a pending result of an asynchronous operation
 * 2. Main methods:
 *    - get(): Blocks until result is available (can throw ExecutionException)
 *    - get(timeout, unit): Blocks with timeout (can throw TimeoutException)
 *    - isDone(): Non-blocking check if computation is complete
 *    - cancel(mayInterruptIfRunning): Attempts to cancel the computation
 *    - isCancelled(): Checks if the task was cancelled
 * 
 * 3. Exception Handling:
 *    - ExecutionException: Wraps exceptions thrown by the task
 *    - InterruptedException: If current thread is interrupted while waiting
 *    - CancellationException: If trying to get result of cancelled task
 *    - TimeoutException: If get() with timeout expires
 * 
 * 4. Limitations:
 *    - Cannot combine multiple Futures easily
 *    - No way to attach callbacks
 *    - Limited error handling capabilities
 *    - These limitations led to CompletableFuture in Java 8+
 */ 