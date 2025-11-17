import java.util.concurrent.*;

public class Main3 {

   private static void nap(int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void addProc(HighLevelDisplay d) {
        for (int i = 0; i < 20; i++) {
            d.addRow("FLIGHT " + i);
            nap(500);
            // nap(ThreadLocalRandom.current().nextInt(100, 501));
        }
    }

    private static void deleteProc(HighLevelDisplay d) {
        for (int i = 0; i < 20; i++) {
            d.deleteRow(0);
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