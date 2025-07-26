package learning.g_future_callable_completableFuture;

import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates Callable interface in Java Concurrency
 * 
 * Callable is similar to Runnable but:
 * - Can return a result (generic type T)
 * - Can throw checked exceptions
 * - Must implement call() method instead of run()
 * - Works with ExecutorService to return Future objects
 */
public class b_SimpleCallable {
    
    public static void main(String[] args) {
        System.out.println("=== Callable Examples ===\n");
        
        // Example 1: Basic Callable usage
        basicCallableExample();
        
        // Example 2: Callable vs Runnable comparison
        callableVsRunnableExample();
        
        // Example 3: Callable with different return types
        differentReturnTypesExample();
        
        // Example 4: Callable with checked exceptions
        callableWithExceptionsExample();
        
        // Example 5: Multiple Callables
        multipleCallablesExample();
        
        // Example 6: Practical Callable example
        practicalCallableExample();
    }
    
    /**
     * Example 1: Basic Callable implementation and usage
     */
    private static void basicCallableExample() {
        System.out.println("1. Basic Callable Example:");
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        // Method 1: Anonymous Callable implementation
        Callable<String> callable1 = new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(1000);
                return "Hello from Callable!";
            }
        };
        
        // Method 2: Lambda expression (Java 8+)
        Callable<Integer> callable2 = () -> {
            Thread.sleep(500);
            return 42;
        };
        
        try {
            // Submit and get results
            Future<String> future1 = executor.submit(callable1);
            Future<Integer> future2 = executor.submit(callable2);
            
            System.out.println("String result: " + future1.get());
            System.out.println("Integer result: " + future2.get());
            
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
    }
    
    /**
     * Example 2: Comparison between Callable and Runnable
     */
    private static void callableVsRunnableExample() {
        System.out.println("2. Callable vs Runnable Comparison:");
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // Runnable example - no return value
        Runnable runnable = () -> {
            try {
                Thread.sleep(1000);
                System.out.println("Runnable completed (no return value)");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        
        // Callable example - returns a value
        Callable<String> callable = () -> {
            Thread.sleep(1000);
            return "Callable completed with return value";
        };
        
        try {
            // Submit Runnable - returns Future<?> with null result
            Future<?> runnableFuture = executor.submit(runnable);
            runnableFuture.get(); // Just waits for completion
            System.out.println("Runnable future result: " + runnableFuture.get());
            
            // Submit Callable - returns Future<String>
            Future<String> callableFuture = executor.submit(callable);
            String result = callableFuture.get();
            System.out.println("Callable result: " + result);
            
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
    }
    
    /**
     * Example 3: Callable with different return types
     */
    private static void differentReturnTypesExample() {
        System.out.println("3. Callable with Different Return Types:");
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // String Callable
        Callable<String> stringCallable = () -> "Hello World";
        
        // Boolean Callable
        Callable<Boolean> booleanCallable = () -> {
            // Simulate some computation
            Thread.sleep(500);
            return Math.random() > 0.5;
        };
        
        // Custom Object Callable
        Callable<Person> personCallable = () -> {
            Thread.sleep(300);
            return new Person("John Doe", 30);
        };
        
        // List Callable
        Callable<List<Integer>> listCallable = () -> {
            List<Integer> numbers = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                numbers.add(i * i); // Square numbers
            }
            return numbers;
        };
        
        try {
            Future<String> stringFuture = executor.submit(stringCallable);
            Future<Boolean> booleanFuture = executor.submit(booleanCallable);
            Future<Person> personFuture = executor.submit(personCallable);
            Future<List<Integer>> listFuture = executor.submit(listCallable);
            
            System.out.println("String result: " + stringFuture.get());
            System.out.println("Boolean result: " + booleanFuture.get());
            System.out.println("Person result: " + personFuture.get());
            System.out.println("List result: " + listFuture.get());
            
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
    }
    
    /**
     * Example 4: Callable that throws checked exceptions
     */
    private static void callableWithExceptionsExample() {
        System.out.println("4. Callable with Exceptions Example:");
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        // Callable that might throw a checked exception
        Callable<String> riskyCallable = () -> {
            Thread.sleep(500);
            
            // Simulate random failure
            if (Math.random() > 0.5) {
                throw new Exception("Simulated business logic exception");
            }
            
            return "Success!";
        };
        
        try {
            Future<String> future = executor.submit(riskyCallable);
            String result = future.get();
            System.out.println("Result: " + result);
            
        } catch (ExecutionException e) {
            System.out.println("Callable threw exception: " + e.getCause().getMessage());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupted while waiting");
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
    }
    
    /**
     * Example 5: Working with multiple Callables using invokeAll
     */
    private static void multipleCallablesExample() {
        System.out.println("5. Multiple Callables Example:");
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        // Create a list of Callables
        List<Callable<Integer>> callables = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            final int taskNumber = i;
            callables.add(() -> {
                Thread.sleep(taskNumber * 200); // Different sleep times
                return taskNumber * taskNumber; // Return square
            });
        }
        
        try {
            // invokeAll() executes all Callables and returns when all complete
            System.out.println("Executing all callables...");
            List<Future<Integer>> futures = executor.invokeAll(callables);
            
            // Collect results
            System.out.println("Results:");
            for (int i = 0; i < futures.size(); i++) {
                Integer result = futures.get(i).get();
                System.out.println("Task " + (i + 1) + " result: " + result);
            }
            
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
    }
    
    /**
     * Example 6: Practical Callable example - Parallel computation
     */
    private static void practicalCallableExample() {
        System.out.println("6. Practical Callable Example - Parallel Sum:");
        
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // Large array to sum
        int[] numbers = new int[1000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i + 1; // 1 to 1000
        }
        
        // Create Callables to sum different parts of the array
        List<Callable<Long>> sumTasks = new ArrayList<>();
        int chunkSize = numbers.length / 4;
        
        for (int i = 0; i < 4; i++) {
            final int start = i * chunkSize;
            final int end = (i == 3) ? numbers.length : (i + 1) * chunkSize;
            
            sumTasks.add(() -> {
                long sum = 0;
                for (int j = start; j < end; j++) {
                    sum += numbers[j];
                }
                System.out.println("Chunk " + (start/chunkSize + 1) + " sum: " + sum);
                return sum;
            });
        }
        
        try {
            // Execute all sum tasks
            List<Future<Long>> futures = executor.invokeAll(sumTasks);
            
            // Combine results
            long totalSum = 0;
            for (Future<Long> future : futures) {
                totalSum += future.get();
            }
            
            System.out.println("Total sum: " + totalSum);
            System.out.println("Expected sum (1+2+...+1000): " + (1000 * 1001 / 2));
            
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
    }
    
    /**
     * Helper class for demonstration
     */
    static class Person {
        private String name;
        private int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
    }
}

/**
 * Key Points about Callable:
 * 
 * 1. Callable vs Runnable:
 *    - Callable can return a result, Runnable cannot
 *    - Callable can throw checked exceptions, Runnable cannot
 *    - Callable has call() method, Runnable has run() method
 * 
 * 2. Generic Type Support:
 *    - Callable<T> where T is the return type
 *    - Future<T> returned by ExecutorService.submit(Callable<T>)
 * 
 * 3. Exception Handling:
 *    - Checked exceptions thrown by call() are wrapped in ExecutionException
 *    - Access original exception using ExecutionException.getCause()
 * 
 * 4. ExecutorService Methods:
 *    - submit(Callable<T>): Returns Future<T>
 *    - invokeAll(Collection<Callable<T>>): Execute all and return List<Future<T>>
 *    - invokeAny(Collection<Callable<T>>): Return result of first completed
 * 
 * 5. Use Cases:
 *    - When you need a return value from asynchronous task
 *    - Parallel computations that combine results
 *    - Tasks that might throw checked exceptions
 *    - Mathematical computations, data processing, etc.
 */ 