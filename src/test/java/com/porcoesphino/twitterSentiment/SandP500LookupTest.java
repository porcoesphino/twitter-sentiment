package com.porcoesphino.twitterSentiment;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import static org.junit.Assert.*;

public class SandP500LookupTest {

	@Test
	public void testParsePrices() {
		try {
			SandP500Lookup.parsePrices();
		} catch (IOException err) {
			fail("IOException parsing S&P500 file");
		} catch (ParseException err) {
			fail("ParseException parsing S&P500 file");
		}
	}

	@Test
	public void testGetTickers() {
		// The file probably opened if there are 500 items
		assertEquals(SandP500Lookup.getTickers().length, 500);
	}
	
	public void testRemoveSuffixes(String original, String expected) {
		String result = SandP500Lookup.stripSuffixesAndPrefixes(original);
		assertEquals("Didn't remove suffix from [" + original + "] to [" +
		    expected + "] instead [" + result + "]",
		    result, expected);
	}
	
	@Test
	public void removingSuffixDotCom() {
		testRemoveSuffixes("Amazon.com Inc", "Amazon");
	}
	
	@Test
	public void removingSuffixCompanyA() {
		testRemoveSuffixes("Abercrombie & Fitch Company A", "Abercrombie & Fitch");
	}
	
	@Test
	public void removingSuffixCompany() {
		testRemoveSuffixes("Boeing Company", "Boeing");
	}
	
	@Test
	public void removingSuffixPlc() {
		testRemoveSuffixes("Accenture plc", "Accenture");
	}
	
	@Test
	public void removingSuffixInc() {
		testRemoveSuffixes("Adobe Systems Inc", "Adobe Systems");
	}
	
	@Test
	public void removingSuffixCorp() {
		testRemoveSuffixes("ADT Corp", "ADT");
	}
	
	@Test
	public void removingSuffixResourcesInc() {
		testRemoveSuffixes("AGL Resources Inc.", "AGL Resources");
	}
	
	@Test
	public void removingSuffixCo() {
		testRemoveSuffixes("American Express Co", "American Express");
	}
	
	@Test
	public void removingSuffixGroupInc() {
		testRemoveSuffixes("American Intl Group Inc", "American Intl");
	}
	
	@Test
	public void removingSuffixCommaIncDot() {
		testRemoveSuffixes("Analog Devices, Inc.", "Analog Devices");
	}
	
	@Test
	public void removingSuffixCorporation() {
		testRemoveSuffixes("BB&T Corporation", "BB&T");
	}
	
	@Test
	public void removingSuffixCoDotIncDot() {
		testRemoveSuffixes("Best Buy Co. Inc.", "Best Buy");
	}
	
	@Test
	public void removingSuffixCorpDot() {
		testRemoveSuffixes("CBS Corp.", "CBS");
	}
	
	@Test
	public void removingSuffixGroup() {
		testRemoveSuffixes("CBRE Group", "CBRE");
	}
	
	@Test
	public void removingSuffixHoldingsInc() {
		testRemoveSuffixes("CF Industries Holdings Inc", "CF Industries");
	}
	
	@Test
	public void removingSuffixFinancial() {
		testRemoveSuffixes("Cincinnati Financial", "Cincinnati");
	}
	
	@Test
	public void removingSuffixSystems() {
		testRemoveSuffixes("Cisco Systems", "Cisco");
	}
	
	@Test
	public void removingSuffixGroupIncDot() {
		testRemoveSuffixes("CME Group Inc.", "CME");
	}
	
	@Test
	public void removingSuffixEnterprises() {
		testRemoveSuffixes("Coca-Cola Enterprises", "Coca-Cola");
	}
	
	@Test
	public void removingSuffixLtdDot() {
		testRemoveSuffixes("Schlumberger Ltd.", "Schlumberger");
	}
	
	@Test
	public void removingSuffixIntApostropheL() {
		testRemoveSuffixes("Honeywell Int'l", "Honeywell");
	}
	
	@Test
	public void keepsSuffixAndCoDot() {
		testRemoveSuffixes("Lilly (Eli) & Co.", "Lilly (Eli) & Co.");
	}
	
	@Test
	public void removingSuffixCoDot() {
		testRemoveSuffixes("Petroleum Co.", "Petroleum");
	}
	
	@Test
	public void removingPrefix() {
		testRemoveSuffixes("The Coca Cola Company", "Coca Cola");
	}
}
