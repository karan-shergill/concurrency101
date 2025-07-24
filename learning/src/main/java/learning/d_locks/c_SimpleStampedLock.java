package learning.d_locks;

import java.util.concurrent.locks.StampedLock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simple StampedLock Example
 * Shows Java 8's most advanced lock with optimistic reading capabilities
 */
public class c_SimpleStampedLock {
    
    public static void main(String[] args) throws InterruptedException {
        
        System.out.println("=== StampedLock Demonstration ===\n");
        
        // 1. Basic StampedLock concept
        demonstrateBasicConcept();
        
        // 2. Optimistic reading - the key feature
        demonstrateOptimisticReading();
        
        // 3. Lock conversion between modes
        demonstrateLockConversion();
        
        // 4. Performance comparison
        demonstratePerformanceBenefit();
        
        // 5. Real-world coordinate example
        demonstrateCoordinateSystem();
        
        // 6. Important limitations
        demonstrateLimitations();
        
        // 7. When to use StampedLock
        demonstrateUsageGuidelines();
    }
    
    /**
     * BASIC CONCEPT: StampedLock has THREE modes
     */
    private static void demonstrateBasicConcept() {
        System.out.println("1. BASIC STAMPEDLOCK CONCEPT:");
        System.out.println("StampedLock provides THREE lock modes:");
        System.out.println("  • Write Lock     - Exclusive access (like write lock)");
        System.out.println("  • Read Lock      - Shared access (like read lock)");
        System.out.println("  • Optimistic Read - NO LOCK! Just a validation stamp");
        System.out.println();
        System.out.println("Key innovation: Optimistic reading for maximum performance!");
        System.out.println("Instead of acquiring read lock, just get a 'stamp' and validate later");
        System.out.println();
        
        StampedLock lock = new StampedLock();
        
        // Show stamp values
        long writeStamp = lock.writeLock();
        System.out.println("Write lock acquired with stamp: " + writeStamp);
        lock.unlockWrite(writeStamp);
        
        long readStamp = lock.readLock();
        System.out.println("Read lock acquired with stamp: " + readStamp);
        lock.unlockRead(readStamp);
        
        long optimisticStamp = lock.tryOptimisticRead();
        System.out.println("Optimistic read stamp: " + optimisticStamp);
        System.out.println("Optimistic stamp valid? " + lock.validate(optimisticStamp));
        System.out.println();
    }
    
    /**
     * OPTIMISTIC READING: The revolutionary feature of StampedLock
     */
    private static void demonstrateOptimisticReading() throws InterruptedException {
        System.out.println("2. OPTIMISTIC READING DEMONSTRATION:");
        System.out.println("Optimistic reading = 'Hope for the best, validate at the end'");
        System.out.println();
        
        OptimisticCounter counter = new OptimisticCounter();
        counter.setValue(100);
        
        // Reader using optimistic approach
        Thread optimisticReader = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                int value = counter.getValueOptimistic();
                System.out.println("Optimistic reader got: " + value + " (attempt " + (i + 1) + ")");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        
        // Writer that occasionally updates
        Thread writer = new Thread(() -> {
            try {
                Thread.sleep(300);
                counter.setValue(200);
                System.out.println("Writer updated value to 200");
                Thread.sleep(400);
                counter.setValue(300);
                System.out.println("Writer updated value to 300");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        System.out.println("Starting optimistic reader and writer...");
        optimisticReader.start();
        writer.start();
        
        optimisticReader.join();
        writer.join();
        
        System.out.println("Notice: Optimistic reading continued even during writes!");
        System.out.println("If validation failed, it would fall back to regular read lock");
        System.out.println();
    }
    
    /**
     * LOCK CONVERSION: Convert between different lock modes
     */
    private static void demonstrateLockConversion() throws InterruptedException {
        System.out.println("3. LOCK CONVERSION DEMONSTRATION:");
        System.out.println("StampedLock allows converting between lock modes efficiently");
        System.out.println();
        
        ConvertibleData data = new ConvertibleData();
        
        Thread converterThread = new Thread(() -> {
            data.conditionalUpdate("NewValue");
        });
        
        converterThread.start();
        converterThread.join();
        
        System.out.println("Lock conversion completed!");
        System.out.println();
    }
    
    /**
     * PERFORMANCE: Compare StampedLock with ReadWriteLock
     */
    private static void demonstratePerformanceBenefit() throws InterruptedException {
        System.out.println("4. PERFORMANCE COMPARISON:");
        System.out.println("Comparing StampedLock vs ReadWriteLock in read-heavy scenario...");
        System.out.println();
        
        // Test ReadWriteLock
        long rwLockTime = testPerformance(new ReadWriteLockData(), "ReadWriteLock");
        
        // Test StampedLock with optimistic reading
        long stampedLockTime = testPerformance(new StampedLockData(), "StampedLock");
        
        System.out.println("Performance Results:");
        System.out.println("  ReadWriteLock:   " + rwLockTime + " ms");
        System.out.println("  StampedLock:     " + stampedLockTime + " ms");
        
        if (stampedLockTime < rwLockTime) {
            double improvement = ((double)(rwLockTime - stampedLockTime) / rwLockTime) * 100;
            System.out.println("  Improvement:     " + String.format("%.1f", improvement) + "% faster!");
        } else {
            System.out.println("  Note: Performance can vary based on contention and read/write ratio");
        }
        System.out.println("  StampedLock excels with very read-heavy workloads (95%+ reads)");
        System.out.println();
    }
    
    private static long testPerformance(PerformanceTestData testData, String lockType) throws InterruptedException {
        System.out.println("Testing " + lockType + " with 8 readers, 1 writer...");
        
        long startTime = System.currentTimeMillis();
        
        // Create 8 reader threads (very read-heavy)
        Thread[] readers = new Thread[8];
        for (int i = 0; i < 8; i++) {
            readers[i] = new Thread(() -> {
                for (int j = 0; j < 200; j++) {
                    testData.read();
                    // Very minimal work to highlight lock overhead
                }
            });
        }
        
        // Create 1 writer thread
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                testData.write("Data " + i);
                try {
                    Thread.sleep(20); // Occasional writes
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
     * REAL-WORLD EXAMPLE: 2D coordinate system with frequent reads
     */
    private static void demonstrateCoordinateSystem() throws InterruptedException {
        System.out.println("5. REAL-WORLD COORDINATE SYSTEM EXAMPLE:");
        System.out.println("Multiple threads reading coordinates, occasional position updates");
        System.out.println();
        
        CoordinateSystem coords = new CoordinateSystem(0, 0);
        
        // Multiple reader threads checking position
        Thread[] readers = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int readerId = i + 1;
            readers[i] = new Thread(() -> {
                for (int j = 0; j < 4; j++) {
                    Point p = coords.getPosition();
                    System.out.println("Reader " + readerId + " sees position: " + p);
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
        
        // Mover thread updating position
        Thread mover = new Thread(() -> {
            try {
                Thread.sleep(200);
                coords.moveTo(10, 20);
                System.out.println("  Moved to (10, 20)");
                
                Thread.sleep(300);
                coords.moveBy(5, -10);
                System.out.println("  Moved by (5, -10) to current position");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // Start all threads
        for (Thread reader : readers) {
            reader.start();
        }
        mover.start();
        
        // Wait for completion
        for (Thread reader : readers) {
            reader.join();
        }
        mover.join();
        
        System.out.println("Coordinate system demo completed!");
        System.out.println();
    }
    
    /**
     * LIMITATIONS: Important things to know about StampedLock
     */
    private static void demonstrateLimitations() {
        System.out.println("6. IMPORTANT LIMITATIONS:");
        System.out.println("StampedLock is powerful but has important limitations:");
        System.out.println();
        System.out.println("❌ NOT REENTRANT:");
        System.out.println("   Same thread cannot acquire the same lock multiple times");
        System.out.println("   (Unlike ReentrantLock and ReentrantReadWriteLock)");
        System.out.println();
        System.out.println("❌ NO CONDITION SUPPORT:");
        System.out.println("   Cannot use await/signal like with ReentrantLock");
        System.out.println("   No Condition.await() or Condition.signal()");
        System.out.println();
        System.out.println("❌ COMPLEXITY:");
        System.out.println("   More complex to use correctly");
        System.out.println("   Easy to make mistakes with stamp validation");
        System.out.println();
        System.out.println("❌ STAMP OVERFLOW:");
        System.out.println("   Stamps can theoretically overflow (very rare)");
        System.out.println("   Should check for stamp == 0 in critical code");
        System.out.println();
        
        // Demonstrate non-reentrant nature
        StampedLock lock = new StampedLock();
        System.out.println("Demonstrating non-reentrant behavior:");
        long stamp1 = lock.readLock();
        System.out.println("  Acquired first read lock: " + stamp1);
        
        // This would deadlock if attempted!
        System.out.println("  ⚠️  Cannot acquire same lock again (would deadlock)");
        System.out.println("  Unlike ReentrantReadWriteLock, same thread cannot re-enter");
        
        lock.unlockRead(stamp1);
        System.out.println("  Released read lock");
        System.out.println();
    }
    
    /**
     * USAGE GUIDELINES: When to use StampedLock
     */
    private static void demonstrateUsageGuidelines() {
        System.out.println("7. WHEN TO USE STAMPEDLOCK:");
        System.out.println();
        System.out.println("✅ PERFECT FOR:");
        System.out.println("   • Very read-heavy workloads (95%+ reads)");
        System.out.println("   • High-performance scenarios where every nanosecond counts");
        System.out.println("   • Simple data structures (coordinates, counters, flags)");
        System.out.println("   • When you don't need reentrancy or conditions");
        System.out.println();
        System.out.println("❌ AVOID FOR:");
        System.out.println("   • Complex locking scenarios");
        System.out.println("   • When you need reentrancy");
        System.out.println("   • When you need condition variables");
        System.out.println("   • Write-heavy or balanced read/write workloads");
        System.out.println();
        System.out.println("📊 LOCK SELECTION GUIDE:");
        System.out.println("   synchronized       → Simple cases, minimal contention");
        System.out.println("   ReentrantLock      → Need advanced features (tryLock, fairness)");
        System.out.println("   ReadWriteLock      → Read-heavy (80-90% reads)");
        System.out.println("   StampedLock        → Very read-heavy (95%+ reads), maximum performance");
        System.out.println();
        System.out.println("Remember: Profile your application! The 'best' lock depends on your specific use case.");
    }
    
    // ==================== Helper Classes ====================
    
    /**
     * Counter demonstrating optimistic reading
     */
    static class OptimisticCounter {
        private final StampedLock lock = new StampedLock();
        private int value = 0;
        
        public void setValue(int newValue) {
            long stamp = lock.writeLock(); // Exclusive write
            try {
                System.out.println("    [WRITE] Setting value to " + newValue);
                this.value = newValue;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
        
        public int getValueOptimistic() {
            long stamp = lock.tryOptimisticRead(); // No lock! Just get stamp
            int currentValue = value; // Read the value
            
            if (!lock.validate(stamp)) {
                // Validation failed! Someone wrote while we were reading
                System.out.println("    [OPTIMISTIC] Validation failed, falling back to read lock");
                stamp = lock.readLock(); // Fall back to read lock
                try {
                    currentValue = value; // Re-read with lock
                } finally {
                    lock.unlockRead(stamp);
                }
            } else {
                System.out.println("    [OPTIMISTIC] Read succeeded without lock!");
            }
            
            return currentValue;
        }
        
        // Traditional read with lock for comparison
        public int getValueWithLock() {
            long stamp = lock.readLock();
            try {
                return value;
            } finally {
                lock.unlockRead(stamp);
            }
        }
    }
    
    /**
     * Example of lock conversion
     */
    static class ConvertibleData {
        private final StampedLock lock = new StampedLock();
        private String data = "initial";
        
        public void conditionalUpdate(String newValue) {
            // Start with optimistic read
            long stamp = lock.tryOptimisticRead();
            String currentData = data;
            
            if (!lock.validate(stamp)) {
                // Optimistic read failed, upgrade to read lock
                stamp = lock.readLock();
                try {
                    currentData = data;
                } finally {
                    lock.unlockRead(stamp);
                }
            }
            
            System.out.println("  Current data: " + currentData);
            
            // Decide if we need to update
            if (!currentData.equals(newValue)) {
                // Convert to write lock for update
                long writeStamp = lock.writeLock();
                try {
                    System.out.println("  Acquired write lock, updating to: " + newValue);
                    data = newValue;
                } finally {
                    lock.unlockWrite(writeStamp);
                }
            } else {
                System.out.println("  No update needed");
            }
        }
    }
    
    /**
     * Interface for performance testing
     */
    interface PerformanceTestData {
        String read();
        void write(String value);
    }
    
    /**
     * ReadWriteLock implementation for comparison
     */
    static class ReadWriteLockData implements PerformanceTestData {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private volatile String data = "initial";
        
        @Override
        public String read() {
            lock.readLock().lock();
            try {
                return data;
            } finally {
                lock.readLock().unlock();
            }
        }
        
        @Override
        public void write(String value) {
            lock.writeLock().lock();
            try {
                this.data = value;
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
    
    /**
     * StampedLock implementation with optimistic reading
     */
    static class StampedLockData implements PerformanceTestData {
        private final StampedLock lock = new StampedLock();
        private volatile String data = "initial";
        
        @Override
        public String read() {
            // Try optimistic read first
            long stamp = lock.tryOptimisticRead();
            String result = data;
            
            if (!lock.validate(stamp)) {
                // Fall back to pessimistic read
                stamp = lock.readLock();
                try {
                    result = data;
                } finally {
                    lock.unlockRead(stamp);
                }
            }
            return result;
        }
        
        @Override
        public void write(String value) {
            long stamp = lock.writeLock();
            try {
                this.data = value;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }
    
    /**
     * Point class for coordinate system
     */
    static class Point {
        final int x, y;
        
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }
    
    /**
     * Coordinate system using StampedLock
     */
    static class CoordinateSystem {
        private final StampedLock lock = new StampedLock();
        private int x, y;
        
        public CoordinateSystem(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public Point getPosition() {
            long stamp = lock.tryOptimisticRead();
            int currentX = x; // Read coordinates
            int currentY = y;
            
            if (!lock.validate(stamp)) {
                // Validation failed, fall back to read lock
                stamp = lock.readLock();
                try {
                    currentX = x;
                    currentY = y;
                } finally {
                    lock.unlockRead(stamp);
                }
            }
            
            return new Point(currentX, currentY);
        }
        
        public void moveTo(int newX, int newY) {
            long stamp = lock.writeLock();
            try {
                this.x = newX;
                this.y = newY;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
        
        public void moveBy(int deltaX, int deltaY) {
            long stamp = lock.writeLock();
            try {
                this.x += deltaX;
                this.y += deltaY;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }
}

/*
STAMPEDLOCK KEY CONCEPTS:

1. THREE LOCK MODES:
   - Write Lock: Exclusive access (stamp required to unlock)
   - Read Lock: Shared access (stamp required to unlock)  
   - Optimistic Read: NO LOCK! Just validation stamp

2. OPTIMISTIC READING:
   - Revolutionary approach: "Read first, validate later"
   - tryOptimisticRead() returns stamp (no actual lock)
   - validate(stamp) checks if data changed during read
   - Fall back to regular read lock if validation fails

3. PERFORMANCE BENEFITS:
   - Optimistic reads are VERY fast (no lock acquisition)
   - Perfect for read-heavy workloads (95%+ reads)
   - Can significantly outperform ReadWriteLock
   - Minimal writer starvation due to optimistic approach

4. LOCK CONVERSION:
   - Can convert between lock modes efficiently
   - tryConvertToWriteLock(), tryConvertToReadLock()
   - More flexible than traditional lock upgrading

5. IMPORTANT LIMITATIONS:
   - NOT reentrant (same thread cannot acquire twice)
   - No Condition support (no await/signal)
   - More complex to use correctly
   - Stamps can overflow (check for 0)

6. WHEN TO USE:
   - Very read-heavy workloads (95%+ reads)
   - Maximum performance requirements
   - Simple data structures
   - When you don't need reentrancy/conditions

7. TYPICAL PATTERN:
   ```java
   long stamp = lock.tryOptimisticRead();
   // read data
   if (!lock.validate(stamp)) {
       // fall back to pessimistic read
       stamp = lock.readLock();
       try {
           // re-read data
       } finally {
           lock.unlockRead(stamp);
       }
   }
   ```

BEST PRACTICES:
- Always validate optimistic reads
- Have fallback to pessimistic reads
- Use for simple data structures
- Profile performance gains
- Be careful with reentrancy needs

StampedLock is the most advanced lock in Java - use it when you need
maximum performance and have very read-heavy workloads!
*/ 