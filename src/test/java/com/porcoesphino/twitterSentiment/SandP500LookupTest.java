package com.porcoesphino.twitterSentiment;

import java.io.IOException;
import java.text.ParseException;

import junit.framework.TestCase;

public class SandP500LookupTest extends TestCase {

	public void testParsePrices() {
		try {
			SandP500Lookup.parsePrices();
		} catch (IOException err) {
			fail("IOException parsing S&P500 file");
		} catch (ParseException err) {
			fail("ParseException parsing S&P500 file");
		}
	}

	public void testGetTickers() {
		// The file probably opened if there are 500 items
		assertEquals(SandP500Lookup.getTickers().length, 500);
	}

	public void testGetNTickers() {
		int number = (int) Math.round(Math.random() * 500);
		assertEquals(SandP500Lookup.getNTickers(number).length, number);
	}

}
