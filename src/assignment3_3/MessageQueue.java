package assignment3_3;

// *** MODIFIED ***
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.List;
import java.util.ArrayList;

public class MessageQueue {
    private static int n_ids;

    public static void main(String[] args) {
        // *** MODIFIED ***
        if (args.length != 2) {
            System.err.println("usage: java MessageQueue <M_Producers> <N_Consumers>");
            System.exit(1);
        }

        int mProducers = 1;
        int nConsumers = 1;

        try {
            mProducers = Integer.parseInt(args[0]);
            nConsumers = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("arguments must be integers");
            System.exit(1);
        }

        if (mProducers <= 0 || nConsumers <= 0) {
            System.err.println("must have at least 1 producer and 1 consumer");
            System.exit(1);
        }

        System.out.println("producers: " + mProducers + ", consumers: " + nConsumers);

        BlockingQueue<Message> queue = new ArrayBlockingQueue<>(10);

        List<Producer> producers = new ArrayList<>();
        List<Thread> producerThreads = new ArrayList<>();
        List<Consumer> consumers = new ArrayList<>();
        List<Thread> consumerThreads = new ArrayList<>();

        // Start M Producers
        for (int i = 0; i < mProducers; i++) {
            Producer p = new Producer(queue, n_ids++);
            producers.add(p);
            Thread pThread = new Thread(p);
            producerThreads.add(pThread);
            pThread.start();
        }

        // Start N Consumers
        for (int i = 0; i < nConsumers; i++) {
            Consumer c = new Consumer(queue, n_ids++);
            consumers.add(c);
            Thread pThread = new Thread(c);
            consumerThreads.add(pThread);
            pThread.start();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stop all M producers
        for (Producer p : producers) {
            p.stop();
        }

        System.out.println("waiting for producers to finish");
        for (Thread pThread : producerThreads) {
            try {
                pThread.join(); // Guarantee all data is sent
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Put stop like poison pill
        try {
            queue.put(new Message("stop"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("waiting for consumers to finish");
        for (Thread pThread : consumerThreads) {
            try {
                pThread.join(); // Guarantee all data is received
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("totalSentMessageCount: " + CountUtils.getTotalSentMessageCount() + ", totalReceivedMessageCount: " + CountUtils.getTotalReceivedMessageCount());
    }
}
