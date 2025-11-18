package assignment3_4;

import java.util.concurrent.Semaphore; // *** MODIFIED ***

public class Main3 {

    private static Semaphore mutex = new Semaphore(1); // *** MODIFIED ***

   private static void nap(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void addProc(HighLevelDisplay d) {
        for (int i = 0; i < 20; i++) {
            // *** MODIFIED ***
            try {
                mutex.acquire(); // P-operation: enter critical section
                d.addRow("FLIGHT " + i); // critical section
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            } finally {
                mutex.release(); // V-operation: leave critical section
            }
            nap(500);
            // nap(ThreadLocalRandom.current().nextInt(100, 501));
        }
    }

    private static void deleteProc(HighLevelDisplay d) {
        for (int i = 0; i < 20; i++) {
            // *** MODIFIED ***
            try {
                mutex.acquire(); // P-operation: enter critical section
                d.deleteRow(0); // critical section
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            } finally {
                mutex.release(); // V-operation: leave critical section
            }
            nap(500);
            // nap(ThreadLocalRandom.current().nextInt(100, 501));
        }
    }

    public static void main(String [] args) {
	final HighLevelDisplay d = new JDisplay2();

	new Thread () {
	    public void run() {
		addProc(d);
	    }
	}.start();


	new Thread () {
	    public void run() {
		deleteProc(d);
	    }
	}.start();

    }
}