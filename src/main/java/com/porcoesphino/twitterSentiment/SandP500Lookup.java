package com.porcoesphino.twitterSentiment;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Set;

/**
 * Singleton to provide the current S&P 500 companies.
 * 
 * Abstracts away the code for getting the company details.
 * Currently only checks at startup but could be implemented
 * as a poller.
 * 
 * @author bodey.baker@gmail.com
 */
public enum SandP500Lookup {
	
	// Singleton pattern defined here:
	// http://stackoverflow.com/questions/70689/what-is-an-efficient-way-to-implement-a-singleton-pattern-in-java
	INSTANCE;

	private static HashMap<String, String> tickerToCompanyName = new HashMap<String, String>();
	
	private static String stripSuffixes(String companyName) {
		String[] suffixes = new String[]{" Company A", " Company", " plc", " Inc", " Corp",
			    " Inc.", " Co", " Group Inc", ", Inc.", " Corporation",
			    " Co. Inc.", " Corp.", " Group", " Holdings Inc", " Financial",
			    " Systems", " Group Inc.", " Enterprises"};
		int smallestIndex = companyName.length();
		for (String suffix : suffixes) {
			// Ensure it's a suffix and not in the middle of the string
			if (companyName.endsWith(suffix)) {
				int thisIndex = companyName.lastIndexOf(suffix);
				if (thisIndex < smallestIndex) {
					smallestIndex = thisIndex;
				}
			}
		}
		return companyName.substring(0, smallestIndex);
	}
	
	private static String stripPrefixes(String companyName) {
		String[] prefixes = new String[]{"The "};
			int largestIndex = 0;
			for (String prefix : prefixes) {
				// Ensure it's a suffix and not in the middle of the string
				if (companyName.startsWith(prefix)) {
					int thisIndex = companyName.indexOf(prefix) + prefix.length();
					if (thisIndex > largestIndex) {
						largestIndex = thisIndex;
					}
				}
			}
			return companyName.substring(largestIndex, companyName.length());
	}
	
	public static String stripSuffixesAndPrefixes(String companyName) {
		return stripSuffixes(stripPrefixes(companyName));
	}
	
	/*
	 * Parses the S&P 500 company file. Copy and paste from Wikipedia data.
	 * TODO: URL, update file on startup?
	 * 
	 * Assume file contains one tab delimited line per company type:
	 *   - ticker
	 *   - company name
	 *   - other
	 */
	public static void parsePrices() throws IOException, ParseException {
		BufferedReader pricesFileReader = Files.newBufferedReader(
		    App.companiesFilePath,
		    App.defaultCharset);
		String ticker;
		String company;
		String line;
		int companyCount = 0;
		do {
			line = pricesFileReader.readLine();
			if (line == null) {
				break;
			}
			String[] splitLine = line.split("\t");
			if (line.length() == 0)  {
				break;
			}
			ticker = splitLine[0];
			company = stripSuffixesAndPrefixes(splitLine[1]);
			tickerToCompanyName.put(ticker, company);
			companyCount++;
		} while (line != null && line.length() != 0);
		if (companyCount != 500) {
			throw new ParseException("There aren't 500 companies in the S&P500 file!", companyCount);
		}
	}
	
	public static String[] getTickers() {
		Set<String> tickers = tickerToCompanyName.keySet();
		return tickers.toArray(new String[0]);
	}
	
	public static String getCompanyName(String ticker) {
		return tickerToCompanyName.get(ticker);
	}
}
