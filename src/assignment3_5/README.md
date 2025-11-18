# Analysis

## The Four Conditions for Deadlock

A deadlock requires four conditions to occur simultaneously:

1.  **Mutual Exclusion**: Only one thread can use a resource at a time.
2.  **Hold and Wait**: A thread holds one resource while waiting for another.
3.  **No Preemption**: Resources cannot be forcibly taken from a thread.
4.  **Circular Wait**: A chain of threads exists where T1 waits for T2, T2 waits for T3, and the last thread waits for T1.

Breaking any one of these conditions prevents deadlock.

## Problems in the Original Code

1.  **Race Condition (No Locks)**: The original code lacks `synchronized`. Two threads can check the balance (e.g., 100), then both withdraw (e.g., 80), resulting in an incorrect final balance (-60) and data corruption.
2.  **Deadlock (Nested Locks)**: The commented code uses simple nested `synchronized` blocks. This creates a **Circular Wait** when transfers happen in opposite directions:
    * `Teller 1 (acc1 -> acc2)` locks `acc1`.
    * `Teller 2 (acc2 -> acc1)` locks `acc2`.
    * `Teller 1` now waits for `acc2` (held by T2).
    * `Teller 2` now waits for `acc1` (held by T1).
    * Both threads are blocked forever.

## Solution Comparison

Both solutions below fix the race condition and prevent deadlock:

| Solution              | Principle                                                                                                                                    | Advantages                                                    | Disadvantages                                                                       |
|:----------------------|:---------------------------------------------------------------------------------------------------------------------------------------------|:--------------------------------------------------------------|:------------------------------------------------------------------------------------|
| Lock Ordering         | Always acquire locks in a consistent order (e.g., by account ID). This breaks the **Circular Wait** condition                                | **High Concurrency**: Unrelated transfers can run in parallel |                                                                                     |
| Global `synchronized` | Explicit lock control for mutual exclusion. This breaks the **Hold and Wait** and **Circular Wait** condition since there is only one thread | **Very Simple**: Easy to implement                            | **Poor Performance**: Destroys all concurrency; only one transfer can run at a time |