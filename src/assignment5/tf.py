#!/usr/bin/env python3
import re, collections, glob, time
from concurrent.futures import ThreadPoolExecutor

stopwords = set(open('stop_words').read().split(','))

def count_words_in_file(filepath):
    # This function runs in a separate thread for each file
    with open(filepath, encoding='utf-8') as f:
        words = re.findall(r'\w{3,}', f.read().lower())
    return collections.Counter(w for w in words if w not in stopwords)

start = time.time()

# Find all text files in current directory
files = glob.glob('*.txt')
final_counter = collections.Counter()

# Create thread pool and map file processing
with ThreadPoolExecutor() as executor:
    # Map executes count_words_in_file for each file in parallel and returns the results as they complete
    results = executor.map(count_words_in_file, files)
    
    # Wait for all tasks to complete and merge their results
    for res in results:
        final_counter.update(res)

end = time.time()

print("---------- Word counts (top 40) -----------")
for w, c in final_counter.most_common(40):
    print(f"{w} - {c}")

print(f"Elapsed time: {(end - start) * 1000:.2f}ms")