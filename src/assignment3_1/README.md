# Analysis

## Problems in Original Code
The original code uses a non-thread-safe `HashMap` with concurrent operations from multiple threads (adding, deleting, and printing entries), which causes two main issues:

1. **`ConcurrentModificationException`**: `HashMap`'s iterators are fail-fast. If one thread modifies the map (add/remove) while another thread is iterating over it (e.g., `printPeople` traversing `entrySet`, `deletePeople` traversing `keySet`), the iterator detects the concurrent modification and throws this exception.

2. **Data Inconsistency**: Even without exceptions, concurrent writes (add/delete) may corrupt `HashMap`'s internal structure (e.g., broken linked lists, lost entries) due to uncoordinated modifications, leading to hidden bugs.


## Solution Comparison

| Solution                  | Principle                          | Advantages                                                                   | Disadvantages                                                                  |
|---------------------------|------------------------------------|------------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| `ConcurrentHashMap`       | lock-free mechanism | Good concurrency performance, multiple threads can read/write simultaneously | Weakly consistent in `printPeople`                                             |
| `Collections.synchronizedMap` | Global synchronized lock | Easy to use                                                                  | Weakly consistent in `printPeople`, poor concurrency, all operations compete for the same lock |
| Manual `synchronized`     | Explicit lock control for mutual exclusion | Highly consistent in `printPeople`, flexible, lock scope can be controlled                    | Requires manual locking for all operations, it's easy to miss                  |