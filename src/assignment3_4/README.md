# Semaphore

A `Semaphore` is a synchronization tool that controls access to a shared resource by maintaining a count of "permits."

## Core Operations

* `acquire()`:

    * Attempts to take a permit.
    * If permits \> 0, the count is decremented, and the thread proceeds.
    * If permits = 0, the thread **blocks** (waits) until one is available.

* `release()`:

    * Releases a permit.
    * The count is incremented.
    * If other threads are waiting, one is unblocked.

## Common Use: The Mutex Pattern

When a `Semaphore` is initialized with 1 permit, it acts as a **Mutex** (Mutual Exclusion lock). This ensures **only one thread** can execute a critical section of code at a time.

This prevents race conditions, where multiple threads access shared data concurrently, leading to unpredictable results.