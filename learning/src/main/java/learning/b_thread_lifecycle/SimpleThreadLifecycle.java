package learning.b_thread_lifecycle;

/**
 * Simple Thread Lifecycle Example
 * Shows the 6 states a thread goes through
 */
public class SimpleThreadLifecycle {
    
    public static void main(String[] args) throws InterruptedException {
        
        // 1. NEW STATE - Thread is created but not started yet
        Thread myThread = new Thread(() -> {
            try {
                System.out.println("Thread is running...");
                Thread.sleep(10000); // Sleep for 1 second
                System.out.println("Thread finished work");
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted");
            }
        });
        
        // Check state - should be NEW
        System.out.println("1. After creation: " + myThread.getState()); // NEW
        
        // 2. RUNNABLE STATE - Thread is started and ready to run
        myThread.start();
        System.out.println("2. After start(): " + myThread.getState()); // RUNNABLE
        
        // 3. TIMED_WAITING STATE - Thread is sleeping
        Thread.sleep(100); // Wait a bit
        System.out.println("3. While sleeping: " + myThread.getState()); // TIMED_WAITING
        
        // 4. Wait for thread to finish
        myThread.join(); // Wait until myThread completes
        
        // 5. TERMINATED STATE - Thread has finished
        System.out.println("4. After completion: " + myThread.getState()); // TERMINATED
        
        System.out.println("\nThread lifecycle complete!");
    }
}

/*
THREAD STATES EXPLAINED:

1. NEW - Thread created but start() not called yet
2. RUNNABLE - Thread is executing or ready to execute  
3. BLOCKED - Thread waiting to acquire a lock
4. WAITING - Thread waiting indefinitely (wait(), join())
5. TIMED_WAITING - Thread waiting for specific time (sleep())
6. TERMINATED - Thread finished execution

FLOW: NEW → RUNNABLE → TIMED_WAITING → RUNNABLE → TERMINATED
*/ 