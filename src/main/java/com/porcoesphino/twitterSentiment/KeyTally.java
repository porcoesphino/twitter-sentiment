package com.porcoesphino.twitterSentiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
		
		// Update tally and update map
		tally = tally + amount;
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
	
	public Integer getTally(K key) {
		Integer tally = keysToTallies.get(key);
		if (tally == null) {
			return 0;
		}
		return tally;
	}
	
	private List<? extends Set<K>> getNTallySets(int n, Set<Entry<Integer, Set<K>>> entrySet) {
		ArrayList<Set<K>> result = new ArrayList<Set<K>>();
		int i = 0;
		for (Entry<Integer, Set<K>> entry : entrySet) {
			// Clone this so there is no reference to our internal data.
			// Use a LinkedHashSet since the penalty for iterating is lower.
			Set<K> clone = new LinkedHashSet<K>(entry.getValue());
			result.add(i, clone);
			i++;
			if (i == n) {
				break;
			}
		}
		return result;
	}
	
	public List<? extends Set<K>> getNMostFrequentTallySets(int n) {
		return getNTallySets(n, talliesToKeys.descendingMap().entrySet());
	}
	
	public List<? extends Set<K>> getNLeastFrequentTallySets(int n) {
		return getNTallySets(n, talliesToKeys.entrySet());
	}
	
	public void printFrequencyList() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, Set<K>> entry : talliesToKeys.entrySet()) {
			int frequency = entry.getKey();
			Set<K> wordList = entry.getValue();
			sb.append(frequency);
			sb.append(": ");
			for (K word : wordList) {
				sb.append(word);
				sb.append(", ");
			}
			sb.delete(sb.length()-2, sb.length()-1);
			sb.append("\n");
		}
		sb.deleteCharAt(sb.length()-1);
		System.out.println(sb.toString());
	}
}
