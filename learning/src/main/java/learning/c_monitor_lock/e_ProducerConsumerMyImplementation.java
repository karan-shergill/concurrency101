package learning.c_monitor_lock;

import java.util.LinkedList;
import java.util.Queue;

class SharedResource1 {
    private Queue<Integer> queue;
    private int queueSize;

    public SharedResource1(int queueSize) {
        this.queueSize = queueSize;
        queue = new LinkedList<>();
    }

    public synchronized void produce(int item) throws InterruptedException {
        if (queue.size() == queueSize) {
            System.out.println("Queue is full, can't add new item, PRODUCER needs to wait.");
            wait();
        }
        queue.add(item);
        System.out.println("Added item "+ item +" to Queue, current Queue size: "+queue.size());
        notify();
    }

    public synchronized int consume() throws InterruptedException {
        if (queue.isEmpty()) {
            System.out.println("Queue is empty, can't consume anything, CONSUMER needs to wait.");
            wait();
        }
        int val = queue.poll();
        System.out.println("Remove item "+val+" from the Queue, current Queue size: "+ queue.size());
        notify();
        return val;
    }
}

public class e_ProducerConsumerMyImplementation {
    public static void main(String[] args) {
        SharedResource1 sharedResource = new SharedResource1(3);

        Thread producerThread = new Thread(() -> {
            try {
                for (int i=1; i<=6; i++) {
                    sharedResource.produce(i);
                }
            } catch (Exception e) {
            }
        });

        Thread consumerThread = new Thread(() -> {
            try {
                for (int i=1; i<=6; i++) {
                    sharedResource.consume();
                }
            } catch (Exception e) {
            }
        });

        producerThread.start();
        consumerThread.start();
    }
}
