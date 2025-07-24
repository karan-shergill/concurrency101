package learning.c_monitor_lock;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Producer-Consumer Assignment Implementation
 * 
 * ASSIGNMENT PROBLEM:
 * Two threads, a producer and a consumer, share a common, fixed-size buffer as a queue.
 * The producer's job is to generate data and put it into the buffer, while the consumer's 
 * job is to consume the data from the buffer.
 * 
 * CONSTRAINTS:
 * - Producer won't produce data if the buffer is full
 * - Consumer won't consume data if the buffer is empty
 * 
 * SOLUTION APPROACH:
 * - Use synchronized methods for thread safety
 * - Use wait() to block threads when conditions aren't met
 * - Use notify()/notifyAll() to wake up waiting threads
 * - Implement proper buffer management with size limits
 */
public class d_ProducerConsumerAssignment {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Producer-Consumer Assignment Solution ===\n");
        
        // Create shared buffer with capacity of 5 items
        final int BUFFER_CAPACITY = 5;
        SharedBuffer sharedBuffer = new SharedBuffer(BUFFER_CAPACITY);
        
        // Create producer thread
        Thread producerThread = new Thread(new Producer(sharedBuffer), "Producer-Thread");
        
        // Create consumer thread  
        Thread consumerThread = new Thread(new Consumer(sharedBuffer), "Consumer-Thread");
        
        System.out.println("Starting Producer-Consumer simulation...");
        System.out.println("Buffer capacity: " + BUFFER_CAPACITY + " items\n");
        
        // Start both threads
        producerThread.start();
        consumerThread.start();
        
        // Let them run for a while to see the interaction
        Thread.sleep(15000); // Run for 15 seconds
        
        // Interrupt both threads to stop them gracefully
        producerThread.interrupt();
        consumerThread.interrupt();
        
        // Wait for threads to finish
        producerThread.join();
        consumerThread.join();
        
        System.out.println("\n=== Simulation Completed ===");
        System.out.println("Final buffer state: " + sharedBuffer.getCurrentSize() + " items remaining");
    }
}

/**
 * SharedBuffer - The fixed-size buffer shared between Producer and Consumer
 * 
 * This class implements the core logic of the Producer-Consumer problem:
 * - Fixed capacity buffer using a queue
 * - Thread-safe operations using synchronized methods
 * - Proper coordination using wait() and notify()
 * 
 * KEY CONCEPTS:
 * - Monitor Lock: Each object has an intrinsic lock used by synchronized methods
 * - wait(): Causes current thread to wait until another thread calls notify()
 * - notify(): Wakes up a single thread that is waiting on this object's monitor
 */
class SharedBuffer {
    // Internal queue to store data items
    private Queue<Integer> buffer;
    
    // Maximum capacity of the buffer
    private final int capacity;
    
    // Counter for generating unique item IDs
    private int itemCounter = 1;
    
    /**
     * Constructor - Initialize the buffer with specified capacity
     * 
     * @param capacity Maximum number of items the buffer can hold
     */
    public SharedBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new LinkedList<>();
        System.out.println("📦 SharedBuffer created with capacity: " + capacity);
    }
    
    /**
     * PRODUCER METHOD - Add item to buffer
     * 
     * SYNCHRONIZATION LOGIC:
     * 1. Check if buffer is full
     * 2. If full, wait() until consumer removes items
     * 3. If not full, add item and notify() waiting consumer
     * 
     * WAIT/NOTIFY PATTERN:
     * - wait(): Releases monitor lock and waits
     * - notify(): Wakes up waiting thread
     * - synchronized: Ensures thread-safe access
     */
    public synchronized void produce() throws InterruptedException {
        // STEP 1: Check if buffer is full
        while (buffer.size() == capacity) {
            System.out.println("🚫 Buffer FULL! Producer " + Thread.currentThread().getName() + 
                             " waiting... [" + buffer.size() + "/" + capacity + "]");
            
            // STEP 2: Wait until consumer removes items
            // wait() releases the monitor lock and suspends this thread
            // Thread will resume when another thread calls notify() on this object
            wait();
            
            System.out.println("🔄 Producer " + Thread.currentThread().getName() + 
                             " resumed after waiting");
        }
        
        // STEP 3: Buffer has space, add new item
        int item = itemCounter++;
        buffer.offer(item);
        
        System.out.println("🏭 PRODUCED item #" + item + " by " + Thread.currentThread().getName() + 
                         " [Buffer: " + buffer.size() + "/" + capacity + "]");
        
        // STEP 4: Notify waiting consumer that new item is available
        // notify() wakes up ONE waiting thread (if any)
        notify();
    }
    
    /**
     * CONSUMER METHOD - Remove item from buffer
     * 
     * SYNCHRONIZATION LOGIC:
     * 1. Check if buffer is empty
     * 2. If empty, wait() until producer adds items
     * 3. If not empty, remove item and notify() waiting producer
     * 
     * COORDINATION WITH PRODUCER:
     * - Both methods are synchronized on same object (this)
     * - Only one can execute at a time
     * - wait() releases lock for other thread to proceed
     */
    public synchronized int consume() throws InterruptedException {
        // STEP 1: Check if buffer is empty
        while (buffer.isEmpty()) {
            System.out.println("🚫 Buffer EMPTY! Consumer " + Thread.currentThread().getName() + 
                             " waiting... [" + buffer.size() + "/" + capacity + "]");
            
            // STEP 2: Wait until producer adds items
            // wait() releases the monitor lock and suspends this thread
            wait();
            
            System.out.println("🔄 Consumer " + Thread.currentThread().getName() + 
                             " resumed after waiting");
        }
        
        // STEP 3: Buffer has items, remove one
        int item = buffer.poll();
        
        System.out.println("🍽️  CONSUMED item #" + item + " by " + Thread.currentThread().getName() + 
                         " [Buffer: " + buffer.size() + "/" + capacity + "]");
        
        // STEP 4: Notify waiting producer that space is available
        notify();
        
        return item;
    }
    
    /**
     * Utility method to get current buffer size
     * Synchronized to ensure consistent reads
     */
    public synchronized int getCurrentSize() {
        return buffer.size();
    }
    
    /**
     * Utility method to check if buffer is full
     * Synchronized to ensure consistent reads
     */
    public synchronized boolean isFull() {
        return buffer.size() == capacity;
    }
    
    /**
     * Utility method to check if buffer is empty
     * Synchronized to ensure consistent reads
     */
    public synchronized boolean isEmpty() {
        return buffer.isEmpty();
    }
}

/**
 * Producer - Thread that generates and adds items to the buffer
 * 
 * PRODUCER BEHAVIOR:
 * - Continuously generates items
 * - Waits when buffer is full
 * - Adds items at regular intervals
 * - Handles interruption gracefully
 */
class Producer implements Runnable {
    private SharedBuffer sharedBuffer;
    
    /**
     * Constructor - receives shared buffer reference
     */
    public Producer(SharedBuffer sharedBuffer) {
        this.sharedBuffer = sharedBuffer;
    }
    
    /**
     * run() method - Main producer logic
     * 
     * PRODUCTION CYCLE:
     * 1. Produce an item (add to buffer)
     * 2. Sleep for a while (simulate production time)
     * 3. Repeat until interrupted
     * 
     * ERROR HANDLING:
     * - Catches InterruptedException for graceful shutdown
     * - Uses Thread.interrupted() to check interruption status
     */
    @Override
    public void run() {
        System.out.println("🏭 Producer " + Thread.currentThread().getName() + " started");
        
        try {
            while (!Thread.interrupted()) {
                // Produce an item (may wait if buffer is full)
                sharedBuffer.produce();
                
                // Simulate production time (1-2 seconds)
                Thread.sleep(1000 + (int)(Math.random() * 1000));
            }
        } catch (InterruptedException e) {
            System.out.println("🛑 Producer " + Thread.currentThread().getName() + " interrupted");
        }
        
        System.out.println("🏁 Producer " + Thread.currentThread().getName() + " finished");
    }
}

/**
 * Consumer - Thread that removes and processes items from the buffer
 * 
 * CONSUMER BEHAVIOR:
 * - Continuously consumes items
 * - Waits when buffer is empty  
 * - Processes items at regular intervals
 * - Handles interruption gracefully
 */
class Consumer implements Runnable {
    private SharedBuffer sharedBuffer;
    
    /**
     * Constructor - receives shared buffer reference
     */
    public Consumer(SharedBuffer sharedBuffer) {
        this.sharedBuffer = sharedBuffer;
    }
    
    /**
     * run() method - Main consumer logic
     * 
     * CONSUMPTION CYCLE:
     * 1. Consume an item (remove from buffer)
     * 2. Process the item (simulate work)
     * 3. Sleep for a while (simulate processing time)
     * 4. Repeat until interrupted
     * 
     * PROCESSING SIMULATION:
     * - Consumer is slightly slower than producer
     * - This creates realistic buffer full/empty scenarios
     */
    @Override
    public void run() {
        System.out.println("🍽️  Consumer " + Thread.currentThread().getName() + " started");
        
        try {
            while (!Thread.interrupted()) {
                // Consume an item (may wait if buffer is empty)
                int item = sharedBuffer.consume();
                
                // Simulate processing time (slightly longer than production)
                System.out.println("   ⚙️  Processing item #" + item + "...");
                Thread.sleep(1200 + (int)(Math.random() * 800));
                System.out.println("   ✅ Finished processing item #" + item);
            }
        } catch (InterruptedException e) {
            System.out.println("🛑 Consumer " + Thread.currentThread().getName() + " interrupted");
        }
        
        System.out.println("🏁 Consumer " + Thread.currentThread().getName() + " finished");
    }
}

/*
COMPREHENSIVE LEARNING GUIDE:

1. ASSIGNMENT REQUIREMENTS FULFILLED:
   ✅ Two threads (producer and consumer)
   ✅ Shared fixed-size buffer (queue)
   ✅ Producer generates data and puts into buffer
   ✅ Consumer consumes data from buffer
   ✅ Producer waits when buffer is full
   ✅ Consumer waits when buffer is empty

2. KEY SYNCHRONIZATION CONCEPTS:

   MONITOR LOCK:
   - Each object has an intrinsic lock
   - synchronized methods acquire lock on 'this' object
   - Only one synchronized method can execute at a time per object

   WAIT/NOTIFY PATTERN:
   - wait(): Releases lock and waits for notification
   - notify(): Wakes up one waiting thread
   - notifyAll(): Wakes up all waiting threads
   - Must be called within synchronized context

   COORDINATION FLOW:
   Producer: Check full → wait() if full → produce → notify()
   Consumer: Check empty → wait() if empty → consume → notify()

3. THREAD COMMUNICATION:
   - Threads communicate through shared buffer state
   - wait() allows thread to yield control when condition not met
   - notify() signals condition change to waiting threads
   - Synchronized methods ensure atomic operations

4. REAL-WORLD APPLICATIONS:
   - Operating system process scheduling
   - Network packet buffering  
   - Database connection pooling
   - Message queue systems (Kafka, RabbitMQ)
   - Thread pool executors
   - Producer-consumer in web servers

5. COMMON PITFALLS AVOIDED:
   - Using while() instead of if() for wait conditions (handles spurious wakeups)
   - Proper exception handling for InterruptedException
   - Graceful thread shutdown using interrupt()
   - Consistent state checking with synchronized methods

6. PERFORMANCE CONSIDERATIONS:
   - Buffer size affects throughput vs memory usage
   - Producer/consumer speed ratio affects buffer utilization
   - Synchronization overhead vs thread safety trade-off

Run this program to see the Producer-Consumer pattern in action!
*/ 