package assignment3_5;

import java.util.concurrent.atomic.*;

/**
 * DynamicOrderDeadlock
 * <p/>
 * Dynamic lock-ordering deadlock
 *
 * @author Brian Goetz and Tim Peierls
 * @author Crista Lopes
 */
public class BankAccounts {

    // *** MODIFIED ***
    // method1: add lock for the whole function, but it's not a good solution with bad performance
    /*
    public static synchronized void transferMoney(Account fromAccount,
                                     Account toAccount,
                                     DollarAmount amount)
            throws InsufficientFundsException {
        // synchronized (fromAccount) {
        //    synchronized (toAccount) {
                if (fromAccount.getBalance().compareTo(amount) < 0)
                    throw new InsufficientFundsException();
                else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
	//    }
	// }
    }
     */

    public static void transferMoney(Account fromAccount,
                                     Account toAccount,
                                     DollarAmount amount)
            throws InsufficientFundsException {
        // *** MODIFIED ***
        // method2: must acquire locks in a consistent order to prevent deadlock
        // use the account number as the unique identifier to determine this order
        // always lock the account with the smaller account number first

        Account firstLock;
        Account secondLock;

        if (fromAccount.getAcctNo() < toAccount.getAcctNo()) {
            firstLock = fromAccount;
            secondLock = toAccount;
        } else {
            firstLock = toAccount;
            secondLock = fromAccount;
        }

        synchronized (firstLock) {
            synchronized (secondLock) {
                if (fromAccount.getBalance().compareTo(amount) < 0)
                    throw new InsufficientFundsException();
                else {
                    fromAccount.debit(amount);
                    toAccount.credit(amount);
                }
            }
        }
    }

    static class DollarAmount implements Comparable<DollarAmount> {
        // Needs implementation
	private int value;

        public DollarAmount(int amount) {
	    value = amount;
        }

	public int get() { return value; }

        public DollarAmount add(DollarAmount d) {
            return new DollarAmount(value + d.get());
        }

        public DollarAmount subtract(DollarAmount d) {
            return new DollarAmount(value - d.get());
        }

        public int compareTo(DollarAmount d) {
	    if (value < d.get())
		return -1;
	    else if (value > d.get())
		return 1;
            return 0;
        }

	public String toString() {
	    return String.valueOf(value);
	}
    }

    static class Account {
        private DollarAmount balance;
        private final int acctNo;
        private static final AtomicInteger sequence = new AtomicInteger();

        public Account() {
            acctNo = sequence.incrementAndGet();
	    balance = new DollarAmount(100);
        }

        void debit(DollarAmount d) throws InsufficientFundsException {
            DollarAmount wouldHave = balance.subtract(d);
	    if (wouldHave.get() < 0)
		throw new InsufficientFundsException();
	    balance = wouldHave;
        }

        void credit(DollarAmount d) {
            balance = balance.add(d);
        }

        DollarAmount getBalance() {
            return balance;
        }

        int getAcctNo() {
            return acctNo;
        }
    }

    static class InsufficientFundsException extends Exception {
    }

    static class Teller implements Runnable {
	private Account from;
	private Account to;
	private int id;
	private boolean running = true;

	public Teller(int i, Account f, Account t) {
	    id = i;
	    from = f;
	    to = t;
	}

	public void run() {
	    while (running) {
		try {
		    Thread.sleep(RandomUtils.randomInteger());
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		DollarAmount amount = new DollarAmount(RandomUtils.randomInteger());
		try {
		    transferMoney(from, to, amount);
		} catch (InsufficientFundsException e) {
		    RandomUtils.print("Insufficient funds", id);
		    continue;
		}
		RandomUtils.print("Transferred " + amount + ", total: " + to.getBalance(), id);
	    }
	}

	public void stop() {
	    running = false;
	}
    }

    private static void nap(int ms) {
	try {
	    Thread.sleep(ms);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	Account acc1 = new Account();
	Account acc2 = new Account();

	System.out.println("Acc1:"+ acc1.getBalance() +
			   " Acc2:"+ acc2.getBalance() +
			   " Total:" + acc1.getBalance().add(acc2.getBalance()));
	System.out.println("--------------------------------");
	nap(1000);

	// One teller one way
	Teller t1 = new Teller(0, acc1, acc2);
	// Two tellers the other way
	Teller t2 = new Teller(1, acc2, acc1);
	Teller t3 = new Teller(2, acc2, acc1);
	
	new Thread(t1).start();
	new Thread(t2).start();
	new Thread(t3).start();

	nap(10000);
	t1.stop();
	t2.stop();
	t3.stop();

	nap(1000);
	System.out.println("--------------------------------");
	System.out.println("Acc1:"+ acc1.getBalance() +
			   " Acc2:"+ acc2.getBalance() +
			   " Total:" + acc1.getBalance().add(acc2.getBalance()));
    }
}
