package learning.g_future_callable_completableFuture;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Demonstrates CompletableFuture in Java (Java 8+)
 * 
 * CompletableFuture is a more powerful alternative to Future that provides:
 * - Asynchronous programming with callbacks
 * - Method chaining for pipeline operations
 * - Combining multiple futures
 * - Better exception handling
 * - Non-blocking operations
 * - Functional programming style
 */
public class c_SimpleCompletableFuture {
    
    private static final Random random = new Random();
    
    public static void main(String[] args) {
        System.out.println("=== CompletableFuture Examples ===\n");
        
        // Example 1: Basic CompletableFuture usage
        basicCompletableFutureExample();
        
        // Example 2: Asynchronous execution
        asyncExecutionExample();
        
        // Example 3: Chaining operations
        chainingOperationsExample();
        
        // Example 4: Combining multiple CompletableFutures
        combiningFuturesExample();
        
        // Example 5: Exception handling
        exceptionHandlingExample();
        
        // Example 6: Practical example - Web service calls
        practicalWebServiceExample();
        
        // Example 7: allOf and anyOf
        allOfAndAnyOfExample();
    }
    
    /**
     * Example 1: Basic CompletableFuture creation and usage
     */
    private static void basicCompletableFutureExample() {
        System.out.println("1. Basic CompletableFuture Example:");
        
        // Method 1: completedFuture - already completed
        CompletableFuture<String> completedFuture = CompletableFuture.completedFuture("Hello World");
        System.out.println("Completed future result: " + completedFuture.join());
        
        // Method 2: supplyAsync - asynchronous supplier
        CompletableFuture<Integer> supplyFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return 42;
        });
        
        // Method 3: runAsync - asynchronous runnable (no return value)
        CompletableFuture<Void> runFuture = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(500);
                System.out.println("Async task completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Wait for results
        System.out.println("Supply future result: " + supplyFuture.join());
        runFuture.join(); // Just wait for completion
        
        System.out.println();
    }
    
    /**
     * Example 2: Different async execution methods
     */
    private static void asyncExecutionExample() {
        System.out.println("2. Async Execution Example:");
        
        // Using default ForkJoinPool
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Task 1 running on: " + Thread.currentThread().getName());
            return "Result from default executor";
        });
        
        // Using custom executor
        ExecutorService customExecutor = Executors.newFixedThreadPool(2);
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Task 2 running on: " + Thread.currentThread().getName());
            return "Result from custom executor";
        }, customExecutor);
        
        // Get results
        System.out.println("Future 1: " + future1.join());
        System.out.println("Future 2: " + future2.join());
        
        customExecutor.shutdown();
        System.out.println();
    }
    
    /**
     * Example 3: Chaining operations with thenApply, thenCompose, thenAccept
     */
    private static void chainingOperationsExample() {
        System.out.println("3. Chaining Operations Example:");
        
        CompletableFuture<String> chainedFuture = CompletableFuture
            .supplyAsync(() -> {
                System.out.println("Step 1: Getting initial value");
                return 10;
            })
            .thenApply(value -> {
                System.out.println("Step 2: Transforming " + value);
                return value * 2;
            })
            .thenApply(value -> {
                System.out.println("Step 3: Converting to string " + value);
                return "Result: " + value;
            })
            .thenCompose(result -> {
                System.out.println("Step 4: Composing with another future");
                return CompletableFuture.supplyAsync(() -> result + " (composed)");
            });
        
        // Side effect without return value
        chainedFuture.thenAccept(result -> {
            System.out.println("Final result: " + result);
        }).join();
        
        System.out.println();
    }
    
    /**
     * Example 4: Combining multiple CompletableFutures
     */
    private static void combiningFuturesExample() {
        System.out.println("4. Combining Futures Example:");
        
        // Create independent futures
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return 10;
        });
        
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            sleep(1500);
            return 20;
        });
        
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            sleep(800);
            return "Hello";
        });
        
        // Combine two futures with same type
        CompletableFuture<Integer> combined = future1.thenCombine(future2, (a, b) -> {
            System.out.println("Combining " + a + " and " + b);
            return a + b;
        });
        
        // Combine futures with different types
        CompletableFuture<String> combinedDifferent = future1.thenCombine(future3, (num, str) -> {
            return str + " " + num;
        });
        
        System.out.println("Combined integers: " + combined.join());
        System.out.println("Combined different types: " + combinedDifferent.join());
        
        System.out.println();
    }
    
    /**
     * Example 5: Exception handling with handle, exceptionally, whenComplete
     */
    private static void exceptionHandlingExample() {
        System.out.println("5. Exception Handling Example:");
        
        // Future that might fail
        CompletableFuture<Integer> riskyFuture = CompletableFuture.supplyAsync(() -> {
            if (random.nextBoolean()) {
                throw new RuntimeException("Random failure!");
            }
            return 100;
        });
        
        // Method 1: exceptionally - handle exceptions
        CompletableFuture<Integer> handledWithExceptionally = riskyFuture
            .exceptionally(throwable -> {
                System.out.println("Handled exception: " + throwable.getMessage());
                return -1; // Default value
            });
        
        // Method 2: handle - handle both success and failure
        CompletableFuture<String> handledWithHandle = CompletableFuture.supplyAsync(() -> {
            if (random.nextBoolean()) {
                throw new RuntimeException("Another random failure!");
            }
            return 200;
        }).handle((result, throwable) -> {
            if (throwable != null) {
                return "Error: " + throwable.getMessage();
            } else {
                return "Success: " + result;
            }
        });
        
        // Method 3: whenComplete - perform side effects
        CompletableFuture<Integer> withSideEffects = CompletableFuture.supplyAsync(() -> {
            return 300;
        }).whenComplete((result, throwable) -> {
            if (throwable != null) {
                System.out.println("Task failed: " + throwable.getMessage());
            } else {
                System.out.println("Task succeeded with result: " + result);
            }
        });
        
        System.out.println("Exceptionally result: " + handledWithExceptionally.join());
        System.out.println("Handle result: " + handledWithHandle.join());
        System.out.println("WhenComplete result: " + withSideEffects.join());
        
        System.out.println();
    }
    
    /**
     * Example 6: Practical example simulating web service calls
     */
    private static void practicalWebServiceExample() {
        System.out.println("6. Practical Web Service Example:");
        
        // Simulate getting user data, orders, and preferences in parallel
        CompletableFuture<User> userFuture = getUserData(123);
        CompletableFuture<List<Order>> ordersFuture = getUserOrders(123);
        CompletableFuture<Preferences> preferencesFuture = getUserPreferences(123);
        
        // Combine all data to create a complete profile
        CompletableFuture<UserProfile> profileFuture = userFuture
            .thenCombine(ordersFuture, (user, orders) -> {
                user.setOrders(orders);
                return user;
            })
            .thenCombine(preferencesFuture, (user, preferences) -> {
                return new UserProfile(user, preferences);
            })
            .exceptionally(throwable -> {
                System.out.println("Failed to create profile: " + throwable.getMessage());
                return new UserProfile(new User("Unknown", "unknown@email.com"), new Preferences());
            });
        
        UserProfile profile = profileFuture.join();
        System.out.println("User profile created: " + profile);
        
        System.out.println();
    }
    
    /**
     * Example 7: allOf and anyOf operations
     */
    private static void allOfAndAnyOfExample() {
        System.out.println("7. AllOf and AnyOf Example:");
        
        // Create multiple futures with different completion times
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            final int taskNum = i;
            futures.add(CompletableFuture.supplyAsync(() -> {
                sleep(taskNum * 200);
                return "Task " + taskNum + " completed";
            }));
        }
        
        // allOf - wait for all to complete
        CompletableFuture<Void> allCompleted = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        CompletableFuture<List<String>> allResults = allCompleted.thenApply(v -> {
            return futures.stream()
                .map(CompletableFuture::join)
                .toList();
        });
        
        // anyOf - wait for any one to complete
        CompletableFuture<Object> anyCompleted = CompletableFuture.anyOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        System.out.println("First completed: " + anyCompleted.join());
        System.out.println("All results: " + allResults.join());
        
        System.out.println();
    }
    
    // Helper methods for web service example
    private static CompletableFuture<User> getUserData(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(800);
            return new User("John Doe", "john@example.com");
        });
    }
    
    private static CompletableFuture<List<Order>> getUserOrders(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(1200);
            List<Order> orders = new ArrayList<>();
            orders.add(new Order(1, "Laptop", 999.99));
            orders.add(new Order(2, "Mouse", 29.99));
            return orders;
        });
    }
    
    private static CompletableFuture<Preferences> getUserPreferences(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            sleep(600);
            return new Preferences();
        });
    }
    
    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Helper classes for demonstration
    static class User {
        private String name;
        private String email;
        private List<Order> orders;
        
        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }
        
        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }
        
        @Override
        public String toString() {
            return "User{name='" + name + "', email='" + email + "', orders=" + orders + "}";
        }
    }
    
    static class Order {
        private int id;
        private String product;
        private double price;
        
        public Order(int id, String product, double price) {
            this.id = id;
            this.product = product;
            this.price = price;
        }
        
        @Override
        public String toString() {
            return "Order{id=" + id + ", product='" + product + "', price=" + price + "}";
        }
    }
    
    static class Preferences {
        private String theme = "dark";
        private String language = "en";
        
        @Override
        public String toString() {
            return "Preferences{theme='" + theme + "', language='" + language + "'}";
        }
    }
    
    static class UserProfile {
        private User user;
        private Preferences preferences;
        
        public UserProfile(User user, Preferences preferences) {
            this.user = user;
            this.preferences = preferences;
        }
        
        @Override
        public String toString() {
            return "UserProfile{user=" + user + ", preferences=" + preferences + "}";
        }
    }
}

/**
 * Key Points about CompletableFuture:
 * 
 * 1. Creation Methods:
 *    - completedFuture(value): Already completed future
 *    - supplyAsync(supplier): Asynchronous supplier
 *    - runAsync(runnable): Asynchronous runnable (no return)
 * 
 * 2. Transformation Methods:
 *    - thenApply(function): Transform result
 *    - thenCompose(function): Flat-map (chain futures)
 *    - thenAccept(consumer): Side effect with result
 *    - thenRun(runnable): Side effect without result
 * 
 * 3. Combining Methods:
 *    - thenCombine(other, function): Combine two futures
 *    - allOf(futures...): Wait for all to complete
 *    - anyOf(futures...): Wait for any one to complete
 * 
 * 4. Exception Handling:
 *    - exceptionally(function): Handle exceptions
 *    - handle(bifunction): Handle both success and failure
 *    - whenComplete(biconsumer): Side effects for both cases
 * 
 * 5. Execution:
 *    - join(): Block and get result (throws unchecked exceptions)
 *    - get(): Block and get result (throws checked exceptions)
 *    - All async methods can accept custom executor
 * 
 * 6. Advantages over Future:
 *    - Non-blocking pipeline operations
 *    - Better composition and chaining
 *    - Functional programming style
 *    - Better exception handling
 *    - No need for ExecutorService management
 */ 