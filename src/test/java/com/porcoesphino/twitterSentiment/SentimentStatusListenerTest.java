package com.porcoesphino.twitterSentiment;

import java.util.Arrays;

import junit.framework.TestCase;

public class SentimentStatusListenerTest extends TestCase {

	public void testGetFilterFromTickers() {
		String tickers[] = new String[]{"microsoft", "apple"};
		String[] expected = new String[]{"$microsoft", "#microsoft", "@microsoft", "$apple", "#apple", "@apple"};
		String result[] = SentimentStatusListener.getFilterFromTickers(tickers);
		assertEquals(Arrays.asList(expected), Arrays.asList(result));
	}

}
