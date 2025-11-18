# Analysis

## Problems in Original Code

The code suffers from a **race condition** despite `Vector`'s methods being individually thread-safe. The problem is in the compound "check-then-act" operations within `getLast()` and `deleteLast()`.

An `ArrayIndexOutOfBoundsException` occurs when:

1.  **Getter** thread calls `getLast()`, checks the `size()`, and calculates `lastIndex = 0`.
2.  **Context Switch:** The `Getter` thread is paused *before* it can call `people.get(0)`.
3.  **Deleter** thread calls `deleteLast()`, also calculates `lastIndex = 0`, and successfully calls `people.remove(0)`. The `Vector` is now empty.
4.  **Context Switch:** The `Getter` thread resumes.
5.  **Exception:** `Getter` tries to execute `people.get(0)` on an empty `Vector`, causing the crash.

## Solution Comparison

| Solution | Principle | Advantages | Disadvantages |
| :--- | :--- | :--- | :--- |
| Manual `synchronized` | Explicit lock control for mutual exclusion | Flexible, lock scope can be controlled | Requires manual locking for all operations, it's easy to miss |
| `ConcurrentLinkedDeque` | lock-free mechanism | Good concurrency performance, multiple threads can read/write simultaneously |  |