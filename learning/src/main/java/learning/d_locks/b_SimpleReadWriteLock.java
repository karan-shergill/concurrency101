package learning.d_locks;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Simple ReadWriteLock Example
 * Shows how ReadWriteLock allows multiple readers but exclusive writers
 */
public class b_SimpleReadWriteLock {
    
    public static void main(String[] args) throws InterruptedException {
        
        System.out.println("=== ReadWriteLock Demonstration ===\n");
        
        // 1. Basic ReadWriteLock concept
        demonstrateBasicConcept();
        
        // 2. Multiple readers scenario
        demonstrateMultipleReaders();
        
        // 3. Writer exclusivity
        demonstrateWriterExclusivity();
        
        // 4. Performance comparison
        demonstratePerformanceBenefit();
        
        // 5. Real-world cache example
        demonstrateCacheExample();
        
        // 6. Lock downgrading (advanced)
        demonstrateLockDowngrading();
    }
    
    /**
     * BASIC CONCEPT: ReadWriteLock has separate read and write locks
     */
    private static void demonstrateBasicConcept() {
        System.out.println("1. BASIC READWRITELOCK CONCEPT:");
        System.out.println("ReadWriteLock provides TWO locks:");
        System.out.println("  • Read Lock  - Multiple threads can hold simultaneously");
        System.out.println("  • Write Lock - Only ONE thread can hold (exclusive)");
        System.out.println("  • Writers block all readers and other writers");
        System.out.println("  • Readers only block writers, not other readers");
        System.out.println();
        
        ReadWriteLock rwLock = new ReentrantReadWriteLock();
        
        System.out.println("Creating ReadWriteLock:");
        System.out.println("  readLock() = " + rwLock.readLock().getClass().getSimpleName());
        System.out.println("  writeLock() = " + rwLock.writeLock().getClass().getSimpleName());
        System.out.println();
    }
    
    /**
     * MULTIPLE READERS: Show that multiple threads can read simultaneously
     */
    private static void demonstrateMultipleReaders() throws InterruptedException {
        System.out.println("2. MULTIPLE READERS DEMONSTRATION:");
        
        SharedData data = new SharedData();
        data.setValue("Initial Value");
        
        System.out.println("Starting 3 reader threads simultaneously...");
        
        // Create 3 reader threads
        Thread[] readers = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int readerNum = i + 1;
            readers[i] = new Thread(() -> {
                for (int j = 0; j < 3; j++) {
                    String value = data.getValue();
                    System.out.println("Reader " + readerNum + " read: " + value + 
                                     " (attempt " + (j + 1) + ")");
                    try {
                        Thread.sleep(300); // Simulate reading work
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
        
        // Start all readers at the same time
        for (Thread reader : readers) {
            reader.start();
        }
        
        // Wait for all readers to finish
        for (Thread reader : readers) {
            reader.join();
        }
        
        System.out.println("Notice: All readers could access data simultaneously!");
        System.out.println();
    }
    
    /**
     * WRITER EXCLUSIVITY: Show that writers get exclusive access
     */
    private static void demonstrateWriterExclusivity() throws InterruptedException {
        System.out.println("3. WRITER EXCLUSIVITY DEMONSTRATION:");
        
        SharedData data = new SharedData();
        
        // Writer thread
        Thread writer = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                data.setValue("Value " + i);
                System.out.println("Writer updated data to: Value " + i);
                try {
                    Thread.sleep(500); // Simulate writing work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        
        // Reader thread (will be blocked while writer works)
        Thread reader = new Thread(() -> {
            try {
                Thread.sleep(100); // Let writer start first
                for (int i = 1; i <= 4; i++) {
                    String value = data.getValue();
                    System.out.println("  Reader read: " + value + " (attempt " + i + ")");
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        System.out.println("Starting writer and reader...");
        writer.start();
        reader.start();
        
        writer.join();
        reader.join();
        
        System.out.println("Notice: Reader had to wait for writer to finish!");
        System.out.println();
    }
    
    /**
     * PERFORMANCE: Compare ReadWriteLock with regular lock
     */
    private static void demonstratePerformanceBenefit() throws InterruptedException {
        System.out.println("4. PERFORMANCE BENEFIT DEMONSTRATION:");
        
        // Test with regular ReentrantLock
        long regularLockTime = testPerformance(new RegularLockData(), "ReentrantLock");
        
        // Test with ReadWriteLock
        long readWriteLockTime = testPerformance(new ReadWriteLockData(), "ReadWriteLock");
        
        System.out.println("Performance Results:");
        System.out.println("  Regular Lock:    " + regularLockTime + " ms");
        System.out.println("  ReadWriteLock:   " + readWriteLockTime + " ms");
        
        if (readWriteLockTime < regularLockTime) {
            double improvement = ((double)(regularLockTime - readWriteLockTime) / regularLockTime) * 100;
            System.out.println("  Improvement:     " + String.format("%.1f", improvement) + "% faster!");
        }
        System.out.println("  (Results may vary based on system and load)");
        System.out.println();
    }
    
    private static long testPerformance(DataAccess dataAccess, String lockType) throws InterruptedException {
        System.out.println("Testing " + lockType + " with 5 readers, 1 writer...");
        
        long startTime = System.currentTimeMillis();
        
        // Create 5 reader threads
        Thread[] readers = new Thread[5];
        for (int i = 0; i < 5; i++) {
            readers[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    dataAccess.read();
                    try {
                        Thread.sleep(1); // Simulate small amount of work
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
        
        // Create 1 writer thread
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                dataAccess.write("Data " + i);
                try {
                    Thread.sleep(10); // Writers do more work
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        
        // Start all threads
        for (Thread reader : readers) {
            reader.start();
        }
        writer.start();
        
        // Wait for completion
        for (Thread reader : readers) {
            reader.join();
        }
        writer.join();
        
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
    
    /**
     * REAL-WORLD EXAMPLE: Cache implementation with ReadWriteLock
     */
    private static void demonstrateCacheExample() throws InterruptedException {
        System.out.println("5. REAL-WORLD CACHE EXAMPLE:");
        
        ThreadSafeCache cache = new ThreadSafeCache();
        
        // Cache some initial data
        cache.put("user:1", "Alice");
        cache.put("user:2", "Bob");
        
        System.out.println("Cache initialized with 2 users");
        System.out.println("Starting 4 readers and 1 writer...");
        
        // Multiple reader threads
        Thread[] readers = new Thread[4];
        for (int i = 0; i < 4; i++) {
            final int readerNum = i + 1;
            readers[i] = new Thread(() -> {
                for (int j = 1; j <= 3; j++) {
                    String value = cache.get("user:" + j);
                    System.out.println("Reader " + readerNum + " got: " + value);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
        
        // Writer thread adding new cache entries
        Thread writer = new Thread(() -> {
            try {
                Thread.sleep(100); // Let readers start
                cache.put("user:3", "Charlie");
                System.out.println("Writer added user:3 = Charlie");
                Thread.sleep(300);
                cache.put("user:4", "Diana");
                System.out.println("Writer added user:4 = Diana");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Start all threads
        for (Thread reader : readers) {
            reader.start();
        }
        writer.start();
        
        // Wait for completion
        for (Thread reader : readers) {
            reader.join();
        }
        writer.join();
        
        System.out.println("Cache demo completed!");
        System.out.println();
    }
    
    /**
     * ADVANCED: Lock downgrading (from write to read)
     */
    private static void demonstrateLockDowngrading() throws InterruptedException {
        System.out.println("6. LOCK DOWNGRADING (ADVANCED):");
        System.out.println("You can downgrade from write lock to read lock (but not upgrade!)");
        
        DowngradableData data = new DowngradableData();
        
        Thread downgradingThread = new Thread(() -> {
            data.updateAndRead("New Value");
        });
        
        downgradingThread.start();
        downgradingThread.join();
        
        System.out.println("Lock downgrading completed!");
        System.out.println();
        
        System.out.println("=== ReadWriteLock Summary ===");
        System.out.println("Best for: Read-heavy workloads (caches, configurations, etc.)");
        System.out.println("Key benefits:");
        System.out.println("  ✓ Multiple readers can read simultaneously");
        System.out.println("  ✓ Writers get exclusive access");
        System.out.println("  ✓ Better performance than regular locks for read-heavy scenarios");
        System.out.println("  ✓ Supports lock downgrading (write → read)");
        System.out.println("Remember: Use for scenarios with many reads, few writes!");
    }
    
    // ==================== Helper Classes ====================
    
    /**
     * Shared data protected by ReadWriteLock
     */
    static class SharedData {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private String value = "";
        
        public String getValue() {
            lock.readLock().lock(); // Multiple threads can hold read lock
            try {
                System.out.println("    [READ LOCK] Thread " + 
                                 Thread.currentThread().getName() + " reading...");
                Thread.sleep(200); // Simulate reading work
                return value;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return value;
            } finally {
                System.out.println("    [READ LOCK] Thread " + 
                                 Thread.currentThread().getName() + " released read lock");
                lock.readLock().unlock();
            }
        }
        
        public void setValue(String newValue) {
            lock.writeLock().lock(); // Only one thread can hold write lock
            try {
                System.out.println("  [WRITE LOCK] Thread " + 
                                 Thread.currentThread().getName() + " writing...");
                Thread.sleep(300); // Simulate writing work
                this.value = newValue;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                System.out.println("  [WRITE LOCK] Thread " + 
                                 Thread.currentThread().getName() + " released write lock");
                lock.writeLock().unlock();
            }
        }
    }
    
    /**
     * Interface for performance testing
     */
    interface DataAccess {
        String read();
        void write(String value);
    }
    
    /**
     * Data access using regular ReentrantLock
     */
    static class RegularLockData implements DataAccess {
        private final ReentrantLock lock = new ReentrantLock();
        private String data = "initial";
        
        @Override
        public String read() {
            lock.lock();
            try {
                return data; // Even reads need to wait for lock
            } finally {
                lock.unlock();
            }
        }
        
        @Override
        public void write(String value) {
            lock.lock();
            try {
                this.data = value;
            } finally {
                lock.unlock();
            }
        }
    }
    
    /**
     * Data access using ReadWriteLock
     */
    static class ReadWriteLockData implements DataAccess {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private String data = "initial";
        
        @Override
        public String read() {
            lock.readLock().lock(); // Multiple readers can proceed
            try {
                return data;
            } finally {
                lock.readLock().unlock();
            }
        }
        
        @Override
        public void write(String value) {
            lock.writeLock().lock(); // Exclusive access for writer
            try {
                this.data = value;
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
    
    /**
     * Thread-safe cache using ReadWriteLock
     */
    static class ThreadSafeCache {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final Map<String, String> cache = new HashMap<>();
        
        public String get(String key) {
            lock.readLock().lock(); // Allow multiple readers
            try {
                String value = cache.get(key);
                System.out.println("    Cache read: " + key + " = " + value);
                return value;
            } finally {
                lock.readLock().unlock();
            }
        }
        
        public void put(String key, String value) {
            lock.writeLock().lock(); // Exclusive access for writing
            try {
                cache.put(key, value);
                System.out.println("  Cache write: " + key + " = " + value);
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        public int size() {
            lock.readLock().lock(); // Reading operation
            try {
                return cache.size();
            } finally {
                lock.readLock().unlock();
            }
        }
    }
    
    /**
     * Example of lock downgrading
     */
    static class DowngradableData {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private String data = "original";
        
        public void updateAndRead(String newValue) {
            lock.writeLock().lock(); // Start with write lock
            try {
                System.out.println("  Acquired write lock for update");
                data = newValue;
                System.out.println("  Updated data to: " + newValue);
                
                // Downgrade to read lock
                lock.readLock().lock(); // Acquire read lock while holding write lock
                System.out.println("  Acquired read lock for verification");
            } finally {
                lock.writeLock().unlock(); // Release write lock (downgrade!)
                System.out.println("  Released write lock (downgraded to read lock)");
            }
            
            try {
                // Now we only hold read lock
                System.out.println("  Verified data: " + data);
                System.out.println("  Now other readers can access simultaneously!");
            } finally {
                lock.readLock().unlock(); // Release read lock
                System.out.println("  Released read lock");
            }
        }
    }
}

/*
READWRITELOCK KEY CONCEPTS:

1. TWO SEPARATE LOCKS:
   - Read Lock: Multiple threads can hold simultaneously
   - Write Lock: Only one thread can hold (exclusive)
   - Writers block everyone, readers only block writers

2. PERFORMANCE BENEFITS:
   - Perfect for read-heavy workloads
   - Multiple readers don't block each other
   - Much faster than regular locks when reads >> writes
   - Common in caches, configurations, lookup tables

3. LOCK RULES:
   - Multiple readers: ✓ Allowed
   - Reader + Writer: ✗ Writer blocks reader
   - Multiple writers: ✗ Writers are exclusive
   - Writer + Reader: ✗ Writer blocks reader

4. LOCK DOWNGRADING:
   - Can downgrade: Write Lock → Read Lock
   - Cannot upgrade: Read Lock → Write Lock (would cause deadlock)
   - Useful for update-then-read patterns

5. WHEN TO USE:
   - Read-heavy scenarios (90%+ reads)
   - Caches and lookup tables
   - Configuration data
   - Shared collections with rare updates

6. BEST PRACTICES:
   - Always use try-finally blocks
   - Don't hold locks longer than necessary
   - Consider fairness for writer starvation
   - Monitor performance vs regular locks

REAL-WORLD EXAMPLES:
- Web caches (many reads, few cache invalidations)
- Configuration managers (read config frequently, update rarely)
- User session stores (many lookups, few updates)
- DNS caches, routing tables, etc.
*/ 