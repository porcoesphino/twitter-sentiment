package com.porcoesphino.twitterSentiment;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
	
	public void testExpectedLists(List<? extends Set<String>> actual,
			String[][] expected) {
		assertTrue("Incorrect number of Tallies retrieved. Expected "
			+ expected.length +" but there were " + actual.size(),
			expected.length == actual.size());
		for (int i=0; i<actual.size(); i++) {
			List<String> list = new ArrayList<String>(actual.get(i));
			Collections.sort(list);
			List<String> wanted = Arrays.asList(expected[i]);
			Collections.sort(wanted);
			assertTrue("Incorrect number of words retrieved. Expected "
					+ expected[i].length +" but there were " + list.size(),
					expected[i].length == list.size());
			for (int j=0; j<list.size(); j++) {
				assertTrue(list.get(j).equals(expected[i][j]));
			}
		}
	}
	
	@Test
	public void testIncrementKey() {
		KeyTally<String> map = new KeyTally<String>();
		List<? extends Set<String>> list;
		map.incrementKey("Morgan", 1);
		testExpectedTallies(map, new int[] {0, 1, 0});
		map.incrementKey("Xavier", 1);
		testExpectedTallies(map, new int[] {0, 1, 1});
		map.incrementKey("Calin", 1);
		testExpectedTallies(map, new int[] {1, 1, 1});
		map.incrementKey("Morgan", 1);
		testExpectedTallies(map, new int[] {1, 2, 1});
		list = map.getNLeastFrequentTallySets(2);
		testExpectedLists(list, new String[][] {
				{"Calin", "Xavier"},
				{"Morgan"}
		});
		map.incrementKey("Xavier", 1);
		testExpectedTallies(map, new int[] {1, 2, 2});
		map.incrementKey("Morgan", 1);
		testExpectedTallies(map, new int[] {1, 3, 2});
		map.incrementKey("Morgan", 10);
		testExpectedTallies(map, new int[] {1, 13, 2});
		list = map.getNMostFrequentTallySets(1);
		testExpectedLists(list, new String[][] {
				{"Morgan"}
		});
		list = map.getNLeastFrequentTallySets(1);
		testExpectedLists(list, new String[][] {
				{"Calin"}
		});
		map.incrementKey("Morgan", -10);
		testExpectedTallies(map, new int[] {1, 3, 2});
		map.incrementKey("Calin", -1);
		testExpectedTallies(map, new int[] {0, 3, 2});
		map.incrementKey("Morgan", -1);
		testExpectedTallies(map, new int[] {0, 2, 2});
		list = map.getNMostFrequentTallySets(1);
		testExpectedLists(list, new String[][] {
				{"Morgan", "Xavier"}
		});
		map.incrementKey("Morgan", -1);
		testExpectedTallies(map, new int[] {0, 1, 2});
		map.incrementKey("Xavier", -1);
		testExpectedTallies(map, new int[] {0, 1, 1});
		map.incrementKey("Xavier", -1);
		testExpectedTallies(map, new int[] {0, 1, 0});
	}

}
