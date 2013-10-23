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
	
	/*
	 * Parses the S&P 500 company file.
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
			company = splitLine[1];
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
	
	public static String[] getNTickers(int N) {
		Set<String> tickers = tickerToCompanyName.keySet();
		String[] result = new String[N];
		int i = 0;
		for (String t : tickers) {
			result[i] = t;
			i++;
			if (i >= N) {
				break;
			}
		}
		return result;
	}
}
