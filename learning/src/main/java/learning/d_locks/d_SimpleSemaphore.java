package learning.d_locks;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simple Semaphore Example
 * Shows how Semaphore controls access to shared resources using permits
 */
public class d_SimpleSemaphore {
    
    public static void main(String[] args) throws InterruptedException {
        
        System.out.println("=== Semaphore Demonstration ===\n");
        
        // 1. Basic Semaphore concept
        demonstrateBasicConcept();
        
        // 2. Binary semaphore (like a mutex)
        demonstrateBinarySemaphore();
        
        // 3. Counting semaphore (multiple permits)
        demonstrateCountingSemaphore();
        
        // 4. Resource pool example
        demonstrateResourcePool();
        
        // 5. Rate limiting example
        demonstrateRateLimiting();
        
        // 6. Producer-consumer with bounded buffer
        demonstrateProducerConsumer();
        
        // 7. Fairness and tryAcquire
        demonstrateFairnessAndTryAcquire();
        
        // 8. Comparison with other mechanisms
        demonstrateComparison();
    }
    
    /**
     * BASIC CONCEPT: Semaphore manages permits for resource access
     */
    private static void demonstrateBasicConcept() {
        System.out.println("1. BASIC SEMAPHORE CONCEPT:");
        System.out.println("Semaphore = 'Traffic Signal' for threads");
        System.out.println("  • Maintains a count of available 'permits'");
        System.out.println("  • acquire() - takes a permit (blocks if none available)");
        System.out.println("  • release() - returns a permit");
        System.out.println("  • Multiple threads can hold permits simultaneously");
        System.out.println();
        
        // Create semaphore with 3 permits
        Semaphore semaphore = new Semaphore(3);
        
        System.out.println("Created semaphore with 3 permits");
        System.out.println("Available permits: " + semaphore.availablePermits());
        
        try {
            // Acquire permits one by one
            semaphore.acquire();
            System.out.println("Acquired 1 permit. Available: " + semaphore.availablePermits());
            
            semaphore.acquire(2);
            System.out.println("Acquired 2 more permits. Available: " + semaphore.availablePermits());
            
            // Release permits
            semaphore.release();
            System.out.println("Released 1 permit. Available: " + semaphore.availablePermits());
            
            semaphore.release(2);
            System.out.println("Released 2 permits. Available: " + semaphore.availablePermits());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println();
    }
    
    /**
     * BINARY SEMAPHORE: Acts like a mutex (0 or 1 permits)
     */
    private static void demonstrateBinarySemaphore() throws InterruptedException {
        System.out.println("2. BINARY SEMAPHORE (MUTEX-LIKE):");
        System.out.println("Binary semaphore = semaphore with 1 permit (acts like mutex)");
        System.out.println();
        
        BinarySemaphoreCounter counter = new BinarySemaphoreCounter();
        
        // Create multiple threads trying to increment
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int threadNum = i + 1;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    counter.increment(threadNum);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
        
        System.out.println("Starting 3 threads with binary semaphore...");
        for (Thread thread : threads) {
            thread.start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("Final counter value: " + counter.getValue());
        System.out.println("Notice: Only one thread could access counter at a time");
        System.out.println();
    }
    
    /**
     * COUNTING SEMAPHORE: Multiple permits allow multiple threads
     */
    private static void demonstrateCountingSemaphore() throws InterruptedException {
        System.out.println("3. COUNTING SEMAPHORE (MULTIPLE PERMITS):");
        System.out.println("Counting semaphore = allows N threads to access resource simultaneously");
        System.out.println();
        
        // Semaphore allowing 2 threads to work simultaneously
        Semaphore parkingLot = new Semaphore(2);
        
        // Create 5 cars trying to park
        Thread[] cars = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int carNum = i + 1;
            cars[i] = new Thread(() -> {
                try {
                    System.out.println("Car " + carNum + " looking for parking spot...");
                    parkingLot.acquire(); // Get parking permit
                    
                    System.out.println("  Car " + carNum + " PARKED! (spots used: " + 
                                     (2 - parkingLot.availablePermits()) + "/2)");
                    Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000)); // Park for a while
                    
                    System.out.println("  Car " + carNum + " leaving parking spot");
                    parkingLot.release(); // Return parking permit
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        System.out.println("5 cars trying to park in 2-spot parking lot...");
        for (Thread car : cars) {
            car.start();
        }
        
        for (Thread car : cars) {
            car.join();
        }
        
        System.out.println("All cars finished parking!");
        System.out.println();
    }
    
    /**
     * RESOURCE POOL: Classic semaphore use case
     */
    private static void demonstrateResourcePool() throws InterruptedException {
        System.out.println("4. RESOURCE POOL EXAMPLE:");
        System.out.println("Database connection pool with limited connections");
        System.out.println();
        
        DatabaseConnectionPool pool = new DatabaseConnectionPool(3);
        
        // Create multiple clients trying to use database
        Thread[] clients = new Thread[6];
        for (int i = 0; i < 6; i++) {
            final int clientNum = i + 1;
            clients[i] = new Thread(() -> {
                pool.executeQuery("SELECT * FROM users WHERE id = " + clientNum, clientNum);
            });
        }
        
        System.out.println("6 clients trying to use 3-connection database pool...");
        for (Thread client : clients) {
            client.start();
        }
        
        for (Thread client : clients) {
            client.join();
        }
        
        System.out.println("All database queries completed!");
        System.out.println();
    }
    
    /**
     * RATE LIMITING: Control the rate of operations
     */
    private static void demonstrateRateLimiting() throws InterruptedException {
        System.out.println("5. RATE LIMITING EXAMPLE:");
        System.out.println("API rate limiter - max 2 requests per second");
        System.out.println();
        
        RateLimiter rateLimiter = new RateLimiter(2); // 2 permits per second
        
        // Create multiple API requests
        Thread[] requests = new Thread[6];
        for (int i = 0; i < 6; i++) {
            final int requestNum = i + 1;
            requests[i] = new Thread(() -> {
                rateLimiter.makeApiCall("Request " + requestNum);
            });
        }
        
        System.out.println("Making 6 API requests with rate limiting...");
        long startTime = System.currentTimeMillis();
        
        for (Thread request : requests) {
            request.start();
        }
        
        for (Thread request : requests) {
            request.join();
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("All requests completed in " + (endTime - startTime) + " ms");
        System.out.println("Notice: Requests were throttled to respect rate limit");
        System.out.println();
    }
    
    /**
     * PRODUCER-CONSUMER: Bounded buffer using semaphores
     */
    private static void demonstrateProducerConsumer() throws InterruptedException {
        System.out.println("6. PRODUCER-CONSUMER WITH BOUNDED BUFFER:");
        System.out.println("Using semaphores to control buffer size and coordination");
        System.out.println();
        
        BoundedBuffer<String> buffer = new BoundedBuffer<>(3);
        
        // Producer thread
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 6; i++) {
                buffer.put("Item " + i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        
        // Consumer thread
        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(500); // Let producer get ahead
                for (int i = 1; i <= 6; i++) {
                    String item = buffer.take();
                    System.out.println("  Consumer got: " + item);
                    Thread.sleep(400);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        System.out.println("Producer-consumer with buffer size 3...");
        producer.start();
        consumer.start();
        
        producer.join();
        consumer.join();
        
        System.out.println("Producer-consumer demo completed!");
        System.out.println();
    }
    
    /**
     * FAIRNESS AND TRY ACQUIRE: Advanced semaphore features
     */
    private static void demonstrateFairnessAndTryAcquire() throws InterruptedException {
        System.out.println("7. FAIRNESS AND TRY ACQUIRE:");
        System.out.println("Semaphore can be fair (FIFO) and supports non-blocking acquire");
        System.out.println();
        
        // Fair semaphore - grants permits in FIFO order
        Semaphore fairSemaphore = new Semaphore(1, true); // true = fair
        
        // Demonstrate tryAcquire
        System.out.println("Demonstrating tryAcquire...");
        boolean acquired = fairSemaphore.tryAcquire();
        System.out.println("tryAcquire() immediate: " + acquired);
        
        if (acquired) {
            // Try to acquire again (should fail)
            boolean acquired2 = fairSemaphore.tryAcquire();
            System.out.println("tryAcquire() when unavailable: " + acquired2);
            
            // Try with timeout
            boolean acquired3 = fairSemaphore.tryAcquire(100, TimeUnit.MILLISECONDS);
            System.out.println("tryAcquire() with 100ms timeout: " + acquired3);
            
            fairSemaphore.release();
        }
        
        // Demonstrate fairness with multiple threads
        System.out.println("\nDemonstrating fairness with 3 threads...");
        for (int i = 1; i <= 3; i++) {
            final int threadNum = i;
            new Thread(() -> {
                try {
                    System.out.println("Thread " + threadNum + " waiting for fair semaphore...");
                    fairSemaphore.acquire();
                    System.out.println("  Thread " + threadNum + " acquired fair semaphore");
                    Thread.sleep(200);
                    System.out.println("  Thread " + threadNum + " releasing fair semaphore");
                    fairSemaphore.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        
        Thread.sleep(1000); // Let demonstration complete
        System.out.println();
    }
    
    /**
     * COMPARISON: Semaphore vs other synchronization mechanisms
     */
    private static void demonstrateComparison() {
        System.out.println("8. SEMAPHORE vs OTHER SYNCHRONIZATION:");
        System.out.println();
        System.out.println("🔒 TRADITIONAL LOCKS:");
        System.out.println("   synchronized, ReentrantLock → Binary access (0 or 1 thread)");
        System.out.println("   ReadWriteLock → Multiple readers OR one writer");
        System.out.println("   StampedLock → Optimistic reading + traditional locking");
        System.out.println();
        System.out.println("🎫 SEMAPHORE:");
        System.out.println("   → Counting permits (0 to N threads)");
        System.out.println("   → Perfect for resource pooling");
        System.out.println("   → Rate limiting and throttling");
        System.out.println("   → Producer-consumer coordination");
        System.out.println();
        System.out.println("✅ USE SEMAPHORE FOR:");
        System.out.println("   • Database connection pools");
        System.out.println("   • Thread pool size limits");
        System.out.println("   • Rate limiting APIs");
        System.out.println("   • Bounded buffer implementations");
        System.out.println("   • Parking lot / resource allocation");
        System.out.println("   • Download/upload slot management");
        System.out.println();
        System.out.println("❌ AVOID SEMAPHORE FOR:");
        System.out.println("   • Simple mutual exclusion (use synchronized/ReentrantLock)");
        System.out.println("   • Complex condition-based waiting (use Condition)");
        System.out.println("   • When you need lock ownership tracking");
        System.out.println();
        System.out.println("📊 QUICK REFERENCE:");
        System.out.println("   Binary Semaphore(1)    → Like mutex");
        System.out.println("   Counting Semaphore(N)  → N threads allowed");
        System.out.println("   Fair Semaphore         → FIFO permit granting");
        System.out.println("   tryAcquire()           → Non-blocking attempt");
    }
    
    // ==================== Helper Classes ====================
    
    /**
     * Counter using binary semaphore (like mutex)
     */
    static class BinarySemaphoreCounter {
        private final Semaphore semaphore = new Semaphore(1); // Binary semaphore
        private int count = 0;
        
        public void increment(int threadNum) {
            try {
                System.out.println("Thread " + threadNum + " waiting for binary semaphore...");
                semaphore.acquire(); // Get exclusive access
                
                System.out.println("  Thread " + threadNum + " acquired semaphore, incrementing...");
                int oldValue = count;
                Thread.sleep(200); // Simulate work
                count = oldValue + 1;
                System.out.println("  Thread " + threadNum + " incremented from " + oldValue + " to " + count);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                System.out.println("  Thread " + threadNum + " releasing semaphore");
                semaphore.release(); // Release exclusive access
            }
        }
        
        public int getValue() {
            return count;
        }
    }
    
    /**
     * Database connection pool using semaphore
     */
    static class DatabaseConnectionPool {
        private final Semaphore connectionSemaphore;
        private final List<String> connections;
        
        public DatabaseConnectionPool(int poolSize) {
            this.connectionSemaphore = new Semaphore(poolSize);
            this.connections = new ArrayList<>();
            for (int i = 1; i <= poolSize; i++) {
                connections.add("Connection-" + i);
            }
        }
        
        public void executeQuery(String query, int clientNum) {
            try {
                System.out.println("Client " + clientNum + " requesting database connection...");
                connectionSemaphore.acquire(); // Get connection permit
                
                System.out.println("  Client " + clientNum + " got connection! Executing: " + query);
                System.out.println("  Active connections: " + 
                                 (connections.size() - connectionSemaphore.availablePermits()) + 
                                 "/" + connections.size());
                
                Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1500)); // Simulate query time
                
                System.out.println("  Client " + clientNum + " finished query, releasing connection");
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                connectionSemaphore.release(); // Return connection permit
            }
        }
    }
    
    /**
     * Rate limiter using semaphore with automatic permit refill
     */
    static class RateLimiter {
        private final Semaphore semaphore;
        private final int maxRequests;
        
        public RateLimiter(int requestsPerSecond) {
            this.maxRequests = requestsPerSecond;
            this.semaphore = new Semaphore(requestsPerSecond);
            
            // Refill permits every second
            Thread refillThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(1000); // Wait 1 second
                        int permitsToAdd = maxRequests - semaphore.availablePermits();
                        if (permitsToAdd > 0) {
                            semaphore.release(permitsToAdd);
                            System.out.println("    [RATE LIMITER] Refilled " + permitsToAdd + " permits");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
            refillThread.setDaemon(true);
            refillThread.start();
        }
        
        public void makeApiCall(String request) {
            try {
                System.out.println(request + " waiting for rate limit permit...");
                semaphore.acquire(); // Wait for rate limit permit
                
                System.out.println("  " + request + " executing (permits left: " + 
                                 semaphore.availablePermits() + ")");
                Thread.sleep(100); // Simulate API call
                
                // Note: We don't release the permit - it gets refilled by timer
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Bounded buffer using semaphores for producer-consumer
     */
    static class BoundedBuffer<T> {
        private final Queue<T> buffer = new LinkedList<>();
        private final Semaphore emptySlots;  // Permits for adding items
        private final Semaphore fullSlots;   // Permits for removing items
        private final Semaphore mutex;       // Mutual exclusion for buffer access
        
        public BoundedBuffer(int capacity) {
            this.emptySlots = new Semaphore(capacity); // Initially all slots empty
            this.fullSlots = new Semaphore(0);         // Initially no items
            this.mutex = new Semaphore(1);             // Binary semaphore for mutual exclusion
        }
        
        public void put(T item) {
            try {
                System.out.println("Producer putting: " + item);
                emptySlots.acquire(); // Wait for empty slot
                mutex.acquire();      // Get exclusive access to buffer
                
                buffer.offer(item);
                System.out.println("  Buffer now has " + buffer.size() + " items: " + buffer);
                
                mutex.release();      // Release buffer access
                fullSlots.release();  // Signal that item is available
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        public T take() {
            try {
                fullSlots.acquire(); // Wait for available item
                mutex.acquire();     // Get exclusive access to buffer
                
                T item = buffer.poll();
                System.out.println("  Buffer after take has " + buffer.size() + " items: " + buffer);
                
                mutex.release();     // Release buffer access
                emptySlots.release(); // Signal that slot is available
                
                return item;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }
}

/*
SEMAPHORE KEY CONCEPTS:

1. PERMIT-BASED ACCESS CONTROL:
   - Maintains count of available "permits"
   - acquire() takes permit (blocks if none available)
   - release() returns permit
   - Multiple threads can hold permits simultaneously

2. TYPES OF SEMAPHORES:
   - Binary Semaphore (1 permit) → Acts like mutex
   - Counting Semaphore (N permits) → Allows N threads
   - Fair Semaphore → FIFO permit granting

3. COMMON USE CASES:
   - Resource pooling (database connections, threads)
   - Rate limiting and throttling
   - Producer-consumer with bounded buffers
   - Parking lot / slot allocation
   - Download/upload concurrency limits

4. ADVANCED FEATURES:
   - tryAcquire() → Non-blocking attempt
   - tryAcquire(timeout) → Attempt with timeout
   - acquire(n) / release(n) → Multiple permits
   - Fairness parameter in constructor

5. PRODUCER-CONSUMER PATTERN:
   - emptySlots semaphore → Controls buffer capacity
   - fullSlots semaphore → Signals available items
   - mutex semaphore → Protects buffer access

6. SEMAPHORE vs LOCKS:
   - Locks → Binary (0 or 1 thread access)
   - Semaphores → Counting (0 to N thread access)
   - Locks → Ownership-based
   - Semaphores → Permit-based (no ownership)

7. BEST PRACTICES:
   - Use try-finally for acquire/release
   - Match every acquire() with release()
   - Consider fairness for long-running scenarios
   - Use appropriate permit count for your use case

TYPICAL PATTERNS:

Resource Pool:
```java
semaphore.acquire();  // Get resource
try {
    // use resource
} finally {
    semaphore.release(); // Return resource
}
```

Rate Limiting:
```java
rateLimitSemaphore.acquire(); // Wait for rate limit
// make API call
// Don't release - permits refilled by timer
```

Producer-Consumer:
```java
// Producer
emptySlots.acquire();
mutex.acquire();
try {
    buffer.add(item);
} finally {
    mutex.release();
    fullSlots.release();
}
```

Remember: Semaphores are perfect for controlling access to limited
resources, not for general mutual exclusion!
*/ 