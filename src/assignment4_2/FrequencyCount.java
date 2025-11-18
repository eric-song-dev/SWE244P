package assignment4_2;

/*
 * @author Crista Lopes
 * Simple word frequency program
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
// *** MODIFIED ***
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FrequencyCount {
    private static final List<String> stop_words = new ArrayList<String>();

    static final class Counter {
	private HashMap<String, Integer> frequencies = new HashMap<String, Integer>();

	private void process(Path filepath) {
	    try {
		try (Stream<String> lines = Files.lines(filepath /*Paths.get(filename)*/ )) {
			lines.forEach(line -> { process(line); });
		    }
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	// Keep only the non stop words with 3 or more characters
	private void process(String line) {
	    String[] words = line.split("\\W+");
	    for (String word : words) {
		String w = word.toLowerCase();
		if (!stop_words.contains(w) && w.length() > 2) {
		    if (frequencies.containsKey(w))
			frequencies.put(w, frequencies.get(w)+1);
		    else
			frequencies.put(w, 1);
		}
	    }
	}

	private List<Map.Entry<String, Integer>> sort() {
	    Set<Map.Entry<String, Integer>> set = frequencies.entrySet();
	    List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
										      set);
	    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
		    public int compare(Map.Entry<String, Integer> o1,
				       Map.Entry<String, Integer> o2) {
			return o2.getValue().compareTo(o1.getValue());
		    }
		});
	    return list;
	}

	private HashMap<String, Integer> getFrequencies() { 
	    return frequencies; 
	}

	public void merge(Counter other) {
	    other.getFrequencies().forEach((k, v) -> frequencies.merge(k, v, Integer::sum));
	}

	// Only the top 40 words that are 3 or more characters
	public String toString() {
	    List<Map.Entry<String, Integer>> sortedMap = sort();
	    StringBuilder sb = new StringBuilder("---------- Word counts (top 40) -----------\n");
	    int i = 0;
	    for (Map.Entry<String, Integer> e : sortedMap) {
		String k = e.getKey();
		sb.append(k + ":" + e.getValue() +"\n"); 
		if (i++ > 40)
		    break;
	    }
	    return sb.toString();
	}

    }

    private static void loadStopWords() {
	String str = "";
	try {
	    byte[] encoded = Files.readAllBytes(Paths.get("stop_words"));
	    str = new String(encoded);    
	} catch (IOException e) {
	    System.out.println("Error reading stop_words");
	}
	String[] words = str.split(",");
	stop_words.addAll(Arrays.asList(words));
    }

    private static void countWords(Path p, Counter c) {
	System.out.println("Started " + p);
	c.process(p);
	System.out.println("Ended " + p);
    }

    public static void main(String[] args) {
        loadStopWords();

        // *** MODIFIED ***
        // Create a fixed thread pool to manage client connections
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        // Create a Future list to hold the results from each thread
        List<Future<Counter>> futures = new ArrayList<>();

        long start = System.nanoTime();
        try {
            try (Stream<Path> paths = Files.walk(Paths.get("."))) {
                // Collect all file paths into a list first
                List<Path> filePaths = paths.filter(p -> p.toString().endsWith(".txt"))
                        .collect(Collectors.toList());

                // Iterate and submit one task per file
                for (Path p : filePaths) {
                    // Define the task as a Callable since it can get a Future
                    Callable<Counter> task = () -> {
                        // Each thread gets its own Counter
                        Counter localCounter = new Counter();
                        countWords(p, localCounter);
                        return localCounter;
                    };
                    // Submit the task and store its Future
                    futures.add(pool.submit(task));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Counter finalCounter = new Counter();
        pool.shutdown();

        // Wait for all tasks to complete and merge their results
        for (Future<Counter> f : futures) {
            try {
                finalCounter.merge(f.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        long end = System.nanoTime();

        long elapsed = end - start;

        // Print the final merged counter
        System.out.println(finalCounter);
        System.out.println("Elapsed time: " + elapsed/1000000 + "ms");

    }
}
