import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class HelloWorldTask implements Runnable {
    final int id;

    private volatile boolean running = true;

    public HelloWorldTask(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (this.running) {
            try {
                System.out.println("Hello World! I'm thread " + this.id + ". The time is " + new Date());
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                if (!this.running) {
                    System.out.println("thread " + this.id + " received interrupt");
                    break;
                }
            }
        }
        System.out.println("thread " + this.id + " stopped");
    }

    public void stopRunning() {
        this.running = false;
    }
}

public class ThreadManager {
    private static final String OPTION_CREATE = "a";
    private static final String OPTION_STOP_ONE = "b";
    private static final String OPTION_STOP_ALL = "c";

    private static final AtomicInteger threadIdCounter = new AtomicInteger(0);

    private static final Map<Integer, ManagedThread> activeThreads = new ConcurrentHashMap<>();

    // bind thread and its task so that we can call task.stopRunning() and thread.interrupt() together
    private static class ManagedThread {
        private final Thread thread;
        private final HelloWorldTask task;

        public ManagedThread(Thread thread, HelloWorldTask task) {
            this.thread = thread;
            this.task = task;
        }

        public void start() {
            this.thread.start();
        }

        public void stop() {
            System.out.println("signal thread " + task.id + " to stop");
            this.task.stopRunning();
            this.thread.interrupt();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            printMenu();
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+");
            String command = parts[0].toLowerCase();

            switch (command) {
                case OPTION_CREATE:
                    createNewThread();
                    break;
                case OPTION_STOP_ONE:
                    if (parts.length > 1) {
                        try {
                            int id = Integer.parseInt(parts[1]);
                            stopThread(id);
                        } catch (NumberFormatException e) {
                            System.out.println("invalid thread ID, please enter a number");
                        }
                    } else {
                        System.out.println("please specify a thread ID (e.g. \"b 2\" kills thread 2)");
                    }
                    break;
                case OPTION_STOP_ALL:
                    stopAllThreads();
                    exit = true;
                    break;
                default:
                    System.out.println("invalid option, please try again");
            }
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n---------------------------------");
        System.out.println("Here are your options:");
        System.out.println("a - Create a new thread");
        System.out.println("b - Stop a given thread (e.g. \"b 2\" kills thread 2)");
        System.out.println("c - Stop all threads and exit this program.");
        System.out.print("Enter your choice: ");
    }

    private static void createNewThread() {
        int id = threadIdCounter.incrementAndGet();

        HelloWorldTask task = new HelloWorldTask(id);
        Thread thread = new Thread(task);
        ManagedThread managedThread = new ManagedThread(thread, task);

        activeThreads.put(id, managedThread);

        managedThread.start();

        System.out.println("started new thread " + id);
    }

    private static void stopThread(int id) {
        var managedThread = activeThreads.remove(id);

        if (managedThread != null) {
            managedThread.stop();
        } else {
            System.out.println("thread " + id + " not found or already stopped");
        }
    }

    private static void stopAllThreads() {
        System.out.println("stop all threads");
        for (var id : new java.util.ArrayList<>(activeThreads.keySet())) {
            stopThread(id);
        }
    }
}