package assignment3_3;

import java.util.concurrent.BlockingQueue;  // *** MODIFIED ***

public class Producer implements Runnable {
    private BlockingQueue<Message> queue;  // *** MODIFIED ***
    private volatile boolean running = true; // *** MODIFIED ***
    private int id;

    public Producer(BlockingQueue<Message> q, int n) {  // *** MODIFIED ***
	queue = q;
	id = n;
    }

    public void stop() {
	running = false;
    }

    public void run() {
	int count = 0;
	while (running) {
	    int n = RandomUtils.randomInteger();
	    try {
		Thread.sleep(n);
		Message msg = new Message("message-" + n);
        queue.put(msg); // Put the message in the queue  // *** MODIFIED ***
		count++;
		RandomUtils.print("Produced " + msg.get(), id);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
        // remove all logic for sending "stop" from here // *** MODIFIED ***
	RandomUtils.print("Messages sent: " + count, id);
        CountUtils.addSentMessageCount(count); // *** MODIFIED ***
    }
}
