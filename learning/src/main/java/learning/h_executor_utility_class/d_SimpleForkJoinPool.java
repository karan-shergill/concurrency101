package learning.h_executor_utility_class;

import java.util.concurrent.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Simple ForkJoinPool Example for Beginners
 * 
 * This example demonstrates:
 * 1. Creating and using ForkJoinPool for divide-and-conquer algorithms
 * 2. RecursiveTask for tasks that return results
 * 3. RecursiveAction for tasks that don't return results
 * 4. Work-stealing algorithm concepts
 * 5. Parallel processing vs sequential processing
 * 6. Real-world examples: parallel sum, merge sort, file processing
 * 7. Best practices and when to use ForkJoinPool
 * 
 * ForkJoinPool:
 * - Designed for divide-and-conquer algorithms
 * - Uses work-stealing algorithm (idle threads steal work from busy threads)
 * - Best for recursive tasks that can be broken into smaller subtasks
 * - Each thread has its own deque (double-ended queue)
 * - Optimized for CPU-intensive tasks that can be parallelized
 * - Default pool size = number of CPU cores
 * - Introduced in Java 7 as part of Fork/Join framework
 */
public class d_SimpleForkJoinPool {
    
    public static void main(String[] args) {
        System.out.println("=== ForkJoinPool Demo ===\n");
        
        // Demo 1: Basic ForkJoinPool concepts
        demonstrateBasicForkJoinConcepts();
        
        // Demo 2: RecursiveTask - tasks that return results
        demonstrateRecursiveTask();
        
        // Demo 3: RecursiveAction - tasks that don't return results
        demonstrateRecursiveAction();
        
        // Demo 4: Parallel vs Sequential performance
        compareParallelVsSequential();
        
        // Demo 5: Work-stealing demonstration
        demonstrateWorkStealing();
        
        // Demo 6: Real-world examples
        demonstrateRealWorldExamples();
        
        // Demo 7: Common pitfalls and best practices
        demonstrateBestPractices();
        
        System.out.println("\n=== All ForkJoinPool demos completed ===");
    }
    
    /**
     * Demo 1: Basic ForkJoinPool Concepts
     * Introduction to ForkJoinPool and how it differs from regular thread pools
     */
    private static void demonstrateBasicForkJoinConcepts() {
        System.out.println("--- Demo 1: Basic ForkJoinPool Concepts ---");
        
        // Get the common ForkJoinPool (shared across the JVM)
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        
        System.out.println("Common ForkJoinPool info:");
        System.out.println("- Parallelism level: " + commonPool.getParallelism());
        System.out.println("- Pool size: " + commonPool.getPoolSize());
        System.out.println("- Active thread count: " + commonPool.getActiveThreadCount());
        System.out.println("- Running thread count: " + commonPool.getRunningThreadCount());
        
        // Create a custom ForkJoinPool
        ForkJoinPool customPool = new ForkJoinPool(4); // 4 threads
        
        System.out.println("\nCustom ForkJoinPool info:");
        System.out.println("- Parallelism level: " + customPool.getParallelism());
        System.out.println("- Pool size: " + customPool.getPoolSize());
        
        // Simple task submission
        Future<String> future = customPool.submit(() -> {
            return "Hello from ForkJoinPool thread: " + Thread.currentThread().getName();
        });
        
        try {
            System.out.println("Result: " + future.get());
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        customPool.shutdown();
        System.out.println("Basic concepts demo completed!\n");
    }
    
    /**
     * Demo 2: RecursiveTask - Tasks that Return Results
     * Classic example: Computing sum of an array using divide-and-conquer
     */
    private static void demonstrateRecursiveTask() {
        System.out.println("--- Demo 2: RecursiveTask (Parallel Sum) ---");
        
        // Create a large array to sum
        int[] numbers = new int[1000];
        Random random = new Random(42); // Fixed seed for reproducible results
        
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = random.nextInt(100) + 1; // Numbers 1-100
        }
        
        System.out.println("Calculating sum of " + numbers.length + " numbers...");
        
        // Sequential sum for comparison
        long sequentialStart = System.nanoTime();
        long sequentialSum = 0;
        for (int num : numbers) {
            sequentialSum += num;
        }
        long sequentialTime = System.nanoTime() - sequentialStart;
        
        // Parallel sum using ForkJoinPool
        ForkJoinPool pool = new ForkJoinPool();
        
        long parallelStart = System.nanoTime();
        ParallelSumTask task = new ParallelSumTask(numbers, 0, numbers.length);
        Long parallelSum = pool.invoke(task);
        long parallelTime = System.nanoTime() - parallelStart;
        
        System.out.println("Sequential sum: " + sequentialSum + " (took " + sequentialTime / 1000 + " microseconds)");
        System.out.println("Parallel sum: " + parallelSum + " (took " + parallelTime / 1000 + " microseconds)");
        System.out.println("Results match: " + (sequentialSum == parallelSum));
        System.out.println("Speedup: " + String.format("%.2fx", (double) sequentialTime / parallelTime));
        
        pool.shutdown();
        System.out.println("RecursiveTask demo completed!\n");
    }
    
    /**
     * Demo 3: RecursiveAction - Tasks that Don't Return Results
     * Example: Parallel array processing (squaring numbers in place)
     */
    private static void demonstrateRecursiveAction() {
        System.out.println("--- Demo 3: RecursiveAction (Parallel Array Processing) ---");
        
        // Create array to process
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        int[] backup = Arrays.copyOf(numbers, numbers.length);
        
        System.out.println("Original array: " + Arrays.toString(numbers));
        
        // Process array in parallel (square each number)
        ForkJoinPool pool = new ForkJoinPool();
        
        ParallelSquareAction action = new ParallelSquareAction(numbers, 0, numbers.length);
        pool.invoke(action);
        
        System.out.println("After parallel squaring: " + Arrays.toString(numbers));
        
        // Verify with sequential processing
        for (int i = 0; i < backup.length; i++) {
            backup[i] = backup[i] * backup[i];
        }
        System.out.println("Sequential result: " + Arrays.toString(backup));
        System.out.println("Results match: " + Arrays.equals(numbers, backup));
        
        pool.shutdown();
        System.out.println("RecursiveAction demo completed!\n");
    }
    
    /**
     * Demo 4: Parallel vs Sequential Performance Comparison
     * Shows when ForkJoinPool provides benefits
     */
    private static void compareParallelVsSequential() {
        System.out.println("--- Demo 4: Performance Comparison ---");
        
        int[] sizes = {1000, 10000, 100000, 1000000};
        
        for (int size : sizes) {
            System.out.println("\nArray size: " + size);
            
            // Create random array
            int[] array = new int[size];
            Random random = new Random(42);
            for (int i = 0; i < size; i++) {
                array[i] = random.nextInt(1000);
            }
            
            // Sequential timing
            long sequentialStart = System.nanoTime();
            long sequentialSum = 0;
            for (int num : array) {
                sequentialSum += num;
                // Simulate some CPU work
                Math.sqrt(num);
            }
            long sequentialTime = System.nanoTime() - sequentialStart;
            
            // Parallel timing
            ForkJoinPool pool = new ForkJoinPool();
            long parallelStart = System.nanoTime();
            ParallelSumTask task = new ParallelSumTask(array, 0, array.length);
            Long parallelSum = pool.invoke(task);
            long parallelTime = System.nanoTime() - parallelStart;
            pool.shutdown();
            
            double speedup = (double) sequentialTime / parallelTime;
            System.out.printf("Sequential: %d ms, Parallel: %d ms, Speedup: %.2fx%n",
                    sequentialTime / 1_000_000, parallelTime / 1_000_000, speedup);
        }
        
        System.out.println("\nNotice: Speedup improves with larger arrays (more work to parallelize)");
        System.out.println("Performance comparison completed!\n");
    }
    
    /**
     * Demo 5: Work-Stealing Demonstration
     * Shows how ForkJoinPool's work-stealing algorithm works
     */
    private static void demonstrateWorkStealing() {
        System.out.println("--- Demo 5: Work-Stealing Demonstration ---");
        
        ForkJoinPool pool = new ForkJoinPool(4); // 4 worker threads
        
        System.out.println("Creating tasks with uneven workloads to trigger work-stealing...");
        
        // Create tasks with different amounts of work
        List<Future<String>> futures = new ArrayList<>();
        
        for (int i = 1; i <= 8; i++) {
            final int taskId = i;
            final int workAmount = i * 200; // Increasing work for each task
            
            Future<String> future = pool.submit(() -> {
                String threadName = Thread.currentThread().getName();
                System.out.printf("Task %d started on %s (work: %d ms)%n", taskId, threadName, workAmount);
                
                // Simulate varying amounts of work
                sleep(workAmount);
                
                System.out.printf("Task %d completed on %s%n", taskId, threadName);
                return "Task " + taskId + " result";
            });
            
            futures.add(future);
        }
        
        // Wait for all tasks
        try {
            for (Future<String> future : futures) {
                future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error: " + e.getMessage());
        }
        
        System.out.println("Notice: Threads that finish early will steal work from busy threads");
        System.out.println("This is the work-stealing algorithm in action!");
        
        pool.shutdown();
        System.out.println("Work-stealing demo completed!\n");
    }
    
    /**
     * Demo 6: Real-world Examples
     * Practical applications of ForkJoinPool
     */
    private static void demonstrateRealWorldExamples() {
        System.out.println("--- Demo 6: Real-world Examples ---");
        
        // Example 1: Parallel Merge Sort
        demonstrateParallelMergeSort();
        
        // Example 2: Parallel File Processing Simulation
        demonstrateParallelFileProcessing();
        
        // Example 3: Parallel Matrix Operations
        demonstrateParallelMatrixOperations();
    }
    
    /**
     * Real-world Example 1: Parallel Merge Sort
     */
    private static void demonstrateParallelMergeSort() {
        System.out.println("\nReal-world Example 1: Parallel Merge Sort");
        
        // Create unsorted array
        int[] array = {64, 34, 25, 12, 22, 11, 90, 88, 76, 50, 42, 30, 5, 77, 55};
        System.out.println("Original array: " + Arrays.toString(array));
        
        ForkJoinPool pool = new ForkJoinPool();
        
        // Parallel merge sort
        ParallelMergeSortTask sortTask = new ParallelMergeSortTask(array, 0, array.length - 1);
        pool.invoke(sortTask);
        
        System.out.println("Sorted array: " + Arrays.toString(array));
        
        pool.shutdown();
    }
    
    /**
     * Real-world Example 2: Parallel File Processing Simulation
     */
    private static void demonstrateParallelFileProcessing() {
        System.out.println("\nReal-world Example 2: Parallel File Processing");
        
        // Simulate a directory with many files
        String[] files = {
            "document1.txt", "document2.txt", "document3.txt", "document4.txt",
            "image1.jpg", "image2.jpg", "data1.csv", "data2.csv",
            "config1.xml", "config2.xml", "log1.log", "log2.log"
        };
        
        System.out.println("Processing " + files.length + " files in parallel...");
        
        ForkJoinPool pool = new ForkJoinPool();
        
        ParallelFileProcessingAction action = new ParallelFileProcessingAction(files, 0, files.length);
        pool.invoke(action);
        
        System.out.println("All files processed!");
        
        pool.shutdown();
    }
    
    /**
     * Real-world Example 3: Parallel Matrix Operations
     */
    private static void demonstrateParallelMatrixOperations() {
        System.out.println("\nReal-world Example 3: Parallel Matrix Operations");
        
        // Create small matrix for demonstration
        int[][] matrix = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12},
            {13, 14, 15, 16}
        };
        
        System.out.println("Original matrix:");
        printMatrix(matrix);
        
        ForkJoinPool pool = new ForkJoinPool();
        
        // Multiply each element by 2 in parallel
        ParallelMatrixMultiplyAction action = new ParallelMatrixMultiplyAction(matrix, 0, matrix.length, 2);
        pool.invoke(action);
        
        System.out.println("Matrix after multiplying by 2:");
        printMatrix(matrix);
        
        pool.shutdown();
    }
    
    /**
     * Demo 7: Best Practices and Common Pitfalls
     */
    private static void demonstrateBestPractices() {
        System.out.println("--- Demo 7: Best Practices ---");
        
        System.out.println("\n✅ WHEN TO USE ForkJoinPool:");
        System.out.println("- CPU-intensive tasks that can be divided recursively");
        System.out.println("- Divide-and-conquer algorithms (merge sort, quick sort)");
        System.out.println("- Parallel processing of large datasets");
        System.out.println("- Tree traversal operations");
        System.out.println("- Mathematical computations (matrix operations, numerical analysis)");
        
        System.out.println("\n❌ WHEN NOT TO USE ForkJoinPool:");
        System.out.println("- I/O-bound tasks (use regular thread pools instead)");
        System.out.println("- Tasks that cannot be easily divided");
        System.out.println("- Very small datasets (overhead > benefit)");
        System.out.println("- Tasks requiring sequential processing");
        
        System.out.println("\n🎯 BEST PRACTICES:");
        System.out.println("- Choose appropriate threshold for task splitting");
        System.out.println("- Avoid creating too many small tasks (overhead)");
        System.out.println("- Prefer RecursiveTask for compute-intensive operations");
        System.out.println("- Use common pool for simple cases: ForkJoinPool.commonPool()");
        System.out.println("- Be careful with shared mutable state");
        System.out.println("- Consider using parallel streams for simpler cases");
        
        System.out.println("\nBest practices demo completed!\n");
    }
    
    // Helper method for sleeping
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // Helper method to print matrix
    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}

/**
 * RecursiveTask example: Parallel Sum Calculation
 * Divides array into smaller chunks and computes sum recursively
 */
class ParallelSumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 100; // Minimum size before splitting
    private final int[] array;
    private final int start;
    private final int end;
    
    public ParallelSumTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected Long compute() {
        int length = end - start;
        
        // Base case: if task is small enough, compute directly
        if (length <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
                // Simulate some CPU work
                Math.sqrt(array[i]);
            }
            return sum;
        }
        
        // Recursive case: split task into two subtasks
        int mid = start + length / 2;
        
        // Fork left subtask
        ParallelSumTask leftTask = new ParallelSumTask(array, start, mid);
        leftTask.fork(); // Execute asynchronously
        
        // Compute right subtask directly (work-stealing optimization)
        ParallelSumTask rightTask = new ParallelSumTask(array, mid, end);
        Long rightSum = rightTask.compute();
        
        // Join left subtask result
        Long leftSum = leftTask.join();
        
        return leftSum + rightSum;
    }
}

/**
 * RecursiveAction example: Parallel Array Processing
 * Squares each element in the array in-place
 */
class ParallelSquareAction extends RecursiveAction {
    private static final int THRESHOLD = 4;
    private final int[] array;
    private final int start;
    private final int end;
    
    public ParallelSquareAction(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected void compute() {
        int length = end - start;
        
        // Base case: process directly if small enough
        if (length <= THRESHOLD) {
            for (int i = start; i < end; i++) {
                array[i] = array[i] * array[i];
                System.out.printf("Squared array[%d] = %d on thread %s%n", 
                        i, array[i], Thread.currentThread().getName());
            }
            return;
        }
        
        // Recursive case: split into subtasks
        int mid = start + length / 2;
        
        ParallelSquareAction leftAction = new ParallelSquareAction(array, start, mid);
        ParallelSquareAction rightAction = new ParallelSquareAction(array, mid, end);
        
        // Execute both subtasks in parallel
        invokeAll(leftAction, rightAction);
    }
}

/**
 * Real-world example: Parallel Merge Sort
 */
class ParallelMergeSortTask extends RecursiveAction {
    private static final int THRESHOLD = 10;
    private final int[] array;
    private final int start;
    private final int end;
    
    public ParallelMergeSortTask(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected void compute() {
        if (start < end) {
            if (end - start <= THRESHOLD) {
                // Use simple sort for small arrays
                Arrays.sort(array, start, end + 1);
            } else {
                int mid = (start + end) / 2;
                
                ParallelMergeSortTask leftTask = new ParallelMergeSortTask(array, start, mid);
                ParallelMergeSortTask rightTask = new ParallelMergeSortTask(array, mid + 1, end);
                
                invokeAll(leftTask, rightTask);
                
                merge(array, start, mid, end);
            }
        }
    }
    
    private void merge(int[] array, int start, int mid, int end) {
        // Simple merge implementation
        int[] temp = new int[end - start + 1];
        int i = start, j = mid + 1, k = 0;
        
        while (i <= mid && j <= end) {
            if (array[i] <= array[j]) {
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }
        
        while (i <= mid) temp[k++] = array[i++];
        while (j <= end) temp[k++] = array[j++];
        
        System.arraycopy(temp, 0, array, start, temp.length);
    }
}

/**
 * Real-world example: Parallel File Processing
 */
class ParallelFileProcessingAction extends RecursiveAction {
    private static final int THRESHOLD = 2;
    private final String[] files;
    private final int start;
    private final int end;
    
    public ParallelFileProcessingAction(String[] files, int start, int end) {
        this.files = files;
        this.start = start;
        this.end = end;
    }
    
    @Override
    protected void compute() {
        int length = end - start;
        
        if (length <= THRESHOLD) {
            // Process files directly
            for (int i = start; i < end; i++) {
                processFile(files[i]);
            }
        } else {
            // Split into subtasks
            int mid = start + length / 2;
            
            ParallelFileProcessingAction leftAction = new ParallelFileProcessingAction(files, start, mid);
            ParallelFileProcessingAction rightAction = new ParallelFileProcessingAction(files, mid, end);
            
            invokeAll(leftAction, rightAction);
        }
    }
    
    private void processFile(String filename) {
        String threadName = Thread.currentThread().getName();
        System.out.printf("Processing %s on thread %s%n", filename, threadName);
        
        // Simulate file processing
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.printf("Completed %s on thread %s%n", filename, threadName);
    }
}

/**
 * Real-world example: Parallel Matrix Operations
 */
class ParallelMatrixMultiplyAction extends RecursiveAction {
    private static final int THRESHOLD = 2;
    private final int[][] matrix;
    private final int startRow;
    private final int endRow;
    private final int multiplier;
    
    public ParallelMatrixMultiplyAction(int[][] matrix, int startRow, int endRow, int multiplier) {
        this.matrix = matrix;
        this.startRow = startRow;
        this.endRow = endRow;
        this.multiplier = multiplier;
    }
    
    @Override
    protected void compute() {
        int rowCount = endRow - startRow;
        
        if (rowCount <= THRESHOLD) {
            // Process rows directly
            for (int i = startRow; i < endRow; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    matrix[i][j] *= multiplier;
                }
                System.out.printf("Processed row %d on thread %s%n", i, Thread.currentThread().getName());
            }
        } else {
            // Split into subtasks
            int mid = startRow + rowCount / 2;
            
            ParallelMatrixMultiplyAction upperAction = new ParallelMatrixMultiplyAction(matrix, startRow, mid, multiplier);
            ParallelMatrixMultiplyAction lowerAction = new ParallelMatrixMultiplyAction(matrix, mid, endRow, multiplier);
            
            invokeAll(upperAction, lowerAction);
        }
    }
} 