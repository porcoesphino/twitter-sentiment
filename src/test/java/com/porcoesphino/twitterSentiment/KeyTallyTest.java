package com.porcoesphino.twitterSentiment;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.porcoesphino.ts.KeyTally;
public class KeyTallyTest {

	public void testExpectedTallies(KeyTally<String> map, int[] expected) {
		int[] actual = new int[] {
				map.getTally("Calin"),
				map.getTally("Morgan"),
				map.getTally("Xavier")
		};
		assertArrayEquals("Incorrect State!", expected, actual);
	}
	
	public List<String> getSortedList(Set<String> set) {
		List<String> list = new ArrayList<String>(set);
		Collections.sort(list);
		return list;
	}
	
	public void testEqual(Set<String> expected, Set<String> actual) {
		assertTrue("Different number of items in set",
				expected.size() == actual.size());
		List<String> actualList = getSortedList(actual);
		List<String> expectedList = getSortedList(expected);
		for (int i=0; i<expected.size(); i++) {
			assertTrue("Items differ",
					actualList.get(i).equals(expectedList.get(i)));
		}
	}
	
	public void testExpectedLists(Map<Integer, Set<String>> actual,
			Map<Integer, Set<String>> expected) {
		assertTrue("Incorrect number of Tallies retrieved. Expected "
			+ expected.size() +" but there were " + actual.size(),
			expected.size() == actual.size());
		for (Entry<Integer, ? extends Set<String>> entry : expected.entrySet() ) {
			int tally = entry.getKey();
			Set<String> actualSet = actual.get(tally);
			assertTrue("Tally set missing", null != actualSet);
			testEqual(entry.getValue(), actualSet);
		}
	}
	
	@Test
	public void testIncrementKey() {
		KeyTally<String> keyTally = new KeyTally<String>();
		Map<Integer, Set<String>> actual;
		Map<Integer, Set<String>> expected;
		keyTally.incrementKey("Morgan", 1);
		testExpectedTallies(keyTally, new int[] {0, 1, 0});
		keyTally.incrementKey("Xavier", 1);
		testExpectedTallies(keyTally, new int[] {0, 1, 1});
		keyTally.incrementKey("Calin", 1);
		testExpectedTallies(keyTally, new int[] {1, 1, 1});
		keyTally.incrementKey("Morgan", 1);
		testExpectedTallies(keyTally, new int[] {1, 2, 1});
		actual = keyTally.getNLeastFrequentTallySets(2);
		expected = new HashMap<Integer, Set<String>>();
		expected.put(2, new HashSet<String>(Arrays.asList("Morgan")));
		expected.put(1, new HashSet<String>(Arrays.asList("Calin", "Xavier")));
		testExpectedLists(actual, expected);
		keyTally.incrementKey("Xavier", 1);
		testExpectedTallies(keyTally, new int[] {1, 2, 2});
		keyTally.incrementKey("Morgan", 1);
		testExpectedTallies(keyTally, new int[] {1, 3, 2});
		keyTally.incrementKey("Morgan", 10);
		testExpectedTallies(keyTally, new int[] {1, 13, 2});
		actual = keyTally.getNMostFrequentTallySets(1);
		expected = new HashMap<Integer, Set<String>>();
		expected.put(13, new HashSet<String>(Arrays.asList("Morgan")));
		testExpectedLists(actual, expected);
		actual = keyTally.getNLeastFrequentTallySets(1);
		expected = new HashMap<Integer, Set<String>>();
		expected.put(1, new HashSet<String>(Arrays.asList("Calin")));
		testExpectedLists(actual, expected);
		keyTally.incrementKey("Morgan", -10);
		testExpectedTallies(keyTally, new int[] {1, 3, 2});
		keyTally.incrementKey("Calin", -1);
		testExpectedTallies(keyTally, new int[] {0, 3, 2});
		keyTally.incrementKey("Morgan", -1);
		testExpectedTallies(keyTally, new int[] {0, 2, 2});
		actual = keyTally.getNMostFrequentTallySets(1);
		expected = new HashMap<Integer, Set<String>>();
		expected.put(2, new HashSet<String>(Arrays.asList("Morgan", "Xavier")));
		keyTally.incrementKey("Morgan", -1);
		testExpectedTallies(keyTally, new int[] {0, 1, 2});
		keyTally.incrementKey("Xavier", -1);
		testExpectedTallies(keyTally, new int[] {0, 1, 1});
		keyTally.incrementKey("Xavier", -1);
		testExpectedTallies(keyTally, new int[] {0, 1, 0});
	}

}
