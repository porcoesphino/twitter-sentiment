package com.porcoesphino.twitterSentiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class KeyTally<K> {
	
	Map<K, Integer> keysToTallies;
	TreeMap<Integer, LinkedList<K>> talliesToKeys;
	
	public KeyTally() {
		keysToTallies = new HashMap<K, Integer>();
		talliesToKeys = new TreeMap<Integer, LinkedList<K>>();
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
			LinkedList<K> wordsWithPreviousCount = talliesToKeys.get(tally);
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
		LinkedList<K> wordsWithNewCount = talliesToKeys.get(tally);
		if (wordsWithNewCount == null) {
			wordsWithNewCount = new LinkedList<K>();
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
	
	private List<? extends List<K>> getNTallies(int n, Set<Entry<Integer, LinkedList<K>>> entrySet) {
		ArrayList<ArrayList<K>> result = new ArrayList<ArrayList<K>>();
		int i = 0;
		for (Entry<Integer, LinkedList<K>> entry : entrySet) {
			ArrayList<K> clone = new ArrayList<K>(entry.getValue());
			result.add(i, clone);
			i++;
			if (i == n) {
				break;
			}
		}
		return result;
	}
	
	public List<? extends List<K>> getNMostFrequentTallies(int n) {
		return getNTallies(n, talliesToKeys.descendingMap().entrySet());
	}
	
	public List<? extends List<K>> getNLeastFrequentTallies(int n) {
		return getNTallies(n, talliesToKeys.entrySet());
	}
	
	public void printFrequencyList() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, LinkedList<K>> entry : talliesToKeys.entrySet()) {
			int frequency = entry.getKey();
			LinkedList<K> wordList = entry.getValue();
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
		System.out.println("Done!");
	}
}
