# Analysis

## BlockingQueue

`BlockingQueue` is an interface that defines a special type of queue with two key properties:

* **Thread-Safe:** Multiple threads can concurrently access a `BlockingQueue` without needing external synchronization.
* **Blocking Behavior:** This is its core feature:
    * **Insertion (e.g., `put`):** When the queue is **full**, any thread attempting to add an element will be **blocked** (suspended) until space becomes available.
    * **Removal (e.g., `take`):** When the queue is **empty**, any thread attempting to retrieve an element will be **blocked** until an element becomes available.

## Problem 1: Consumer Deadlock

**The Issue:**
In a multi-consumer setup (N > 1), if only one "stop" message is sent, consumers deadlock.

**Example (M=1 Producer, N=2 Consumers):**

1. The single Producer sends one `stop` message.

2. Consumer C1 takes the `stop` message and terminates.

3. Consumer C2 remains blocked forever on `queue.take()`, as no more messages will arrive. The program hangs.

**The Solution: Poison Pill Propagation**
The `Consumer` is modified to re-queue the "stop" message before it terminates. This ensures the "pill" is passed to all N consumers.

## Problem 2: Race Condition and Data Loss

**The Issue:**
A race condition at shutdown caused the total "Sent" count to be 1 higher than "Received". This happens if a "stop" message is enqueued *before* a final data message.

**Example (M=2 Producers, N=3 Consumers):**

1. `main` signals P1 and P2 to stop.

2. Producer P1 (fast) sends `STOP_1`.

3. Producer P2 (delayed) sends its final data message, `DATA_FINAL`.

4. Producer P2 then sends `STOP_2`.

5. The queue state becomes: `[..., STOP_1, DATA_FINAL, STOP_2, ...]`.

6. `STOP_1` is propagated (see Problem 1). All 3 Consumers take a "stop" message and terminate.

7. **Result:** `DATA_FINAL` is left in the queue, un-consumed.

**The Solution: Coordinated Shutdown**
The `main` thread must coordinate the shutdown, not the producers.

1. **Producers:** `volatile boolean running` flag is used. Producers **never** send "stop" messages.

2. **assignment2_2.Main Thread:**
   1. Calls `producer.stop()` on all producers.
   2. Calls `producerThread.join()` on all producer threads, waiting for them to die. This guarantees all data is flushed to the queue.
   3. *After* all joins complete, `main` sends **one** `stop` message to the queue (which is then propagated by consumers).