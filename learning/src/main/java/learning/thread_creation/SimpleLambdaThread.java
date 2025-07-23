package learning.thread_creation;

/**
 * Simple Lambda Thread Example
 * Just the basics - no fluff!
 */
public class SimpleLambdaThread {
    
    public static void main(String[] args) {
        
        // OLD WAY (Anonymous class) - verbose
        Thread oldWay = new Thread(new Runnable() {
            public void run() {
                System.out.println("Old way: " + Thread.currentThread().getName());
            }
        });
        
        // NEW WAY (Lambda) - simple!
        Thread newWay = new Thread(() -> {
            System.out.println("Lambda way: " + Thread.currentThread().getName());
        });
        
        // Even simpler (one line)
        Thread simplest = new Thread(() -> System.out.println("Simplest: " + Thread.currentThread().getName()));
        
        // Run them
        oldWay.start();
        newWay.start();
        simplest.start();
    }
} 