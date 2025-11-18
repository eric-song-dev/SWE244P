package assignment3_3;

import java.util.concurrent.BlockingQueue; // *** MODIFIED ***

public class Consumer implements Runnable {
    private BlockingQueue<Message> queue; // *** MODIFIED ***
    private int id;

    public Consumer(BlockingQueue<Message> q, int n) { // *** MODIFIED ***
	queue = q;
	id = n;
    }

    public void run() {
	Message msg = null;
	int count = 0;
        try { // *** MODIFIED ***
            while (true) { // Changed from do-while // *** MODIFIED ***
                msg = queue.take(); // Get a message from the queue // *** MODIFIED ***

                if (msg.get().equals("stop")) { // *** MODIFIED ***
                    queue.put(msg); // Put the "stop" message back for other consumers // *** MODIFIED ***
                    break; // *** MODIFIED ***
                }
                count++;
                RandomUtils.print("Consumed " + msg.get(), id);
                Thread.sleep(RandomUtils.randomInteger());
            }
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }

	RandomUtils.print("Messages received: " + count, id);
        CountUtils.addReceivedMessageCount(count); // *** MODIFIED ***
    }
}
