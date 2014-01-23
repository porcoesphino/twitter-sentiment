package com.porcoesphino.ts;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class KeyTally<K> {
	
	Map<K, Integer> keysToTallies;
	// We want a sparse sorted array here
	// This is probably a better option:
	// https://code.google.com/p/android-source-browsing/source/browse/core/java/android/util/SparseArray.java?repo=platform--frameworks--base
	TreeMap<Integer, Set<K>> talliesToKeys;
	
	public KeyTally() {
		keysToTallies = new HashMap<K, Integer>();
		talliesToKeys = new TreeMap<Integer, Set<K>>();
	}
	
	public void incrementKey(K key, int amount) {
		Integer tally = keysToTallies.get(key);
		if (tally == null) {
			if (amount < 0) {
				throw new IndexOutOfBoundsException(
						"The tallies are out of bounds");
			}
			tally = 0;
		} else {
		
			// Remove previous reference, if it exists
			Set<K> wordsWithPreviousCount = talliesToKeys.get(tally);
			if (wordsWithPreviousCount != null) {
				wordsWithPreviousCount.remove(key);
				if (wordsWithPreviousCount.size() == 0) {
					talliesToKeys.remove(tally);
				}
			}
		}
		
		// Update tally
		tally = tally + amount;
		
		if (tally == 0) {
			
			keysToTallies.remove(key);
			
		} else {
			
			keysToTallies.put(key, tally);
			
			// Insert new reference
			Set<K> wordsWithNewCount = talliesToKeys.get(tally);
			if (wordsWithNewCount == null) {
				// Use a HashSet for speed and since
				// we don't really care about order or iteration
				wordsWithNewCount = new LinkedHashSet<K>();
				talliesToKeys.put(tally, wordsWithNewCount);
			}
			wordsWithNewCount.add(key);
		}
	}
	
	public Integer getTally(K key) {
		Integer tally = keysToTallies.get(key);
		if (tally == null) {
			return 0;
		}
		return tally;
	}
	
	private Map<Integer, Set<K>> getNTallySets(int n, Set<Entry<Integer, Set<K>>> entrySet) {
		LinkedHashMap<Integer, Set<K>> result = new LinkedHashMap<Integer, Set<K>>();
		for (Entry<Integer, Set<K>> entry : entrySet) {
			// Clone this so there is no reference to our internal data.
			// Use a LinkedHashSet since the penalty for iterating is lower.
			LinkedHashSet<K> clone = new LinkedHashSet<K>(entry.getValue());
			result.put(entry.getKey(), clone);
			if (result.size() == n) {
				break;
			}
		}
		return result;
	}
	
	public int roughSpaceUsage() {
		int total = 0;
		for (Entry<Integer, Set<K>> entry : talliesToKeys.entrySet()) {
			total += entry.getValue().size() + 1;
		}
		return total + keysToTallies.size();
	}
	
	public Map<Integer, Set<K>> getNMostFrequentTallySets(int n) {
		return getNTallySets(n, talliesToKeys.descendingMap().entrySet());
	}
	
	public Map<Integer, Set<K>> getNLeastFrequentTallySets(int n) {
		return getNTallySets(n, talliesToKeys.entrySet());
	}
}
