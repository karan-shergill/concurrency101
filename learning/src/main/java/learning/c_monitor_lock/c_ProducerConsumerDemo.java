package learning.c_monitor_lock;

/**
 * Producer-Consumer Pattern Demonstration
 * 
 * This example shows how multiple threads can safely coordinate using monitor locks.
 * The Producer-Consumer pattern is a classic multithreading scenario where:
 * - Producer threads generate data/items
 * - Consumer threads process data/items
 * - They share a common resource (like a buffer or queue)
 * 
 * WITHOUT synchronization: Race conditions, data corruption, unpredictable behavior
 * WITH synchronization: Safe, predictable, coordinated execution
 */
public class c_ProducerConsumerDemo {

    public static void main(String[] args) {
        System.out.println("=== Producer-Consumer Pattern with Monitor Locks ===\n");

        // Create shared resource that both producer and consumer will use
        SharedResource sharedResource = new SharedResource();

        // Create producer thread using Runnable interface
        Thread producerThread = new Thread(new ProduceTask(sharedResource));
        
        // Create consumer thread using Runnable interface
        Thread consumerThread = new Thread(new ConsumeTask(sharedResource));

        // Alternative: Create consumer thread using lambda expression
        Thread consumerThread2 = new Thread(() -> {
            System.out.println("Consumer Thread (Lambda): " + Thread.currentThread().getName() + " starting");
            sharedResource.consumeItem();
        });

        System.out.println("Main thread: " + Thread.currentThread().getName() + " starting all threads");

        // Start all threads
        producerThread.start();
        consumerThread.start();
        consumerThread2.start();

        System.out.println("Main thread: " + Thread.currentThread().getName() + " finished starting threads");
        
        // Note: Main thread continues and may finish before other threads
        // Other threads will continue running independently
    }
}

/**
 * SharedResource - The resource that multiple threads will access
 * 
 * CRITICAL: This class uses synchronized methods to ensure thread safety
 * Without synchronization, multiple threads could access these methods simultaneously,
 * causing race conditions and unpredictable behavior.
 */
class SharedResource {
    
    /**
     * SYNCHRONIZED METHOD - Only one thread can execute this at a time
     * 
     * How Monitor Lock works here:
     * 1. When a thread calls this method, it acquires the monitor lock on this object
     * 2. If another thread tries to call ANY synchronized method on the same object,
     *    it must WAIT until the first thread releases the lock
     * 3. The lock is automatically released when the method completes
     * 
     * @param item The item number being produced
     */
    public synchronized void produceItem(int item) {
        System.out.println("🏭 PRODUCING item " + item + " by thread: " + Thread.currentThread().getName());
        
        try {
            // Simulate time-consuming production work
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Producer interrupted: " + e.getMessage());
        }
        
        System.out.println("✅ PRODUCED item " + item + " by thread: " + Thread.currentThread().getName());
    }
    
    /**
     * SYNCHRONIZED METHOD - Only one thread can execute this at a time
     * 
     * Important: Since both produceItem() and consumeItem() are synchronized
     * on the same object, they CANNOT run simultaneously. This ensures:
     * - No race conditions
     * - Consistent state
     * - Predictable execution order
     */
    public synchronized void consumeItem() {
        System.out.println("🍽️  CONSUMING item by thread: " + Thread.currentThread().getName());
        
        try {
            // Simulate time-consuming consumption work  
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            System.out.println("Consumer interrupted: " + e.getMessage());
        }
        
        System.out.println("✅ CONSUMED item by thread: " + Thread.currentThread().getName());
    }
}

/**
 * ProduceTask - Runnable implementation for producer thread
 * 
 * This class implements Runnable interface to define what the producer thread should do.
 * It holds a reference to the shared resource and produces multiple items.
 */
class ProduceTask implements Runnable {
    private SharedResource sharedResource;
    
    /**
     * Constructor - receives the shared resource to work with
     * 
     * @param resource The shared resource that this producer will use
     */
    public ProduceTask(SharedResource resource) {
        this.sharedResource = resource;
    }
    
    /**
     * run() method - executed when thread starts
     * 
     * This method defines the task that the producer thread will perform.
     * It produces multiple items using the shared resource.
     */
    @Override
    public void run() {
        System.out.println("Producer Thread: " + Thread.currentThread().getName() + " starting production");
        
        // Produce 3 items
        for (int i = 1; i <= 3; i++) {
            sharedResource.produceItem(i);
        }
        
        System.out.println("Producer Thread: " + Thread.currentThread().getName() + " finished production");
    }
}

/**
 * ConsumeTask - Runnable implementation for consumer thread
 * 
 * This class implements Runnable interface to define what the consumer thread should do.
 * It holds a reference to the shared resource and consumes items.
 */
class ConsumeTask implements Runnable {
    private SharedResource sharedResource;
    
    /**
     * Constructor - receives the shared resource to work with
     * 
     * @param resource The shared resource that this consumer will use  
     */
    public ConsumeTask(SharedResource resource) {
        this.sharedResource = resource;
    }
    
    /**
     * run() method - executed when thread starts
     * 
     * This method defines the task that the consumer thread will perform.
     * It consumes items using the shared resource.
     */
    @Override
    public void run() {
        System.out.println("Consumer Thread: " + Thread.currentThread().getName() + " starting consumption");
        
        // Consume 2 items
        for (int i = 1; i <= 2; i++) {
            sharedResource.consumeItem();
        }
        
        System.out.println("Consumer Thread: " + Thread.currentThread().getName() + " finished consumption");
    }
}

/*
KEY LEARNING POINTS:

1. MONITOR LOCK BASICS:
   - 'synchronized' keyword creates a monitor lock on the object
   - Only ONE thread can hold the lock at a time
   - Other threads wait in queue for their turn

2. THREAD COORDINATION:
   - Producer and Consumer threads work with same shared resource
   - Synchronization prevents race conditions
   - Execution order becomes predictable

3. IMPLEMENTATION PATTERNS:
   - Implement Runnable interface for thread tasks
   - Pass shared resources through constructors
   - Use lambda expressions as alternative to Runnable classes

4. REAL-WORLD APPLICATIONS:
   - Web servers: Multiple requests accessing database
   - File processing: Multiple threads reading/writing files
   - Logging systems: Multiple threads writing to same log file
   - Shopping carts: Multiple users updating inventory

5. WITHOUT SYNCHRONIZATION PROBLEMS:
   - Race conditions (threads interfering with each other)
   - Data corruption (partial updates)
   - Inconsistent state (some operations succeed, others fail)
   - Unpredictable results (different outcomes each time)

Remember: When multiple threads access shared data, always use synchronization!
*/ 