package com.porcoesphino.twitterSentiment;

import java.util.List;
import java.util.regex.Pattern;

import com.porcoesphino.twitterSentiment.TweetWindow.StatusAndMeta;
import com.porcoesphino.twitterSentiment.TweetWindow.Tweet;

/**
 * This class is intended to parse a tweet to test the sentiment. Currently it
 * can only: - tests if the tweet is about the company it is configured for.
 * 
 * @author bodey.baker@gmail.com
 */
public class CompanyTweetParser {

	// This is probably a better way to do it:
	// http://www.regular-expressions.info/unicode.html#category
	// The symbols in the sections below can be found here:
	// // http://unicode-table.com/en/sections/miscellaneous-symbols/
	private static String pattern = "[" + " -&(-/:-@\\[-`{-~"
			+ "\\u2000-\\u2BFF"
			+ "\\u2E00-\\u2E7F"
			+ "\\u2FF0-\\u303F"
			+ "\\uE000-\\uDBFF\\uDFFF"
			+ "]+";
	public static Pattern startingWhitespaceAndSymbols = Pattern.compile("^"
			+ pattern);
	public static Pattern trailingWhitespaceAndSymbols = Pattern
			.compile(pattern + "$");
	public static Pattern anyUrl = Pattern
			.compile("(\\S+\\.(com|net|org|edu|gov|co)(\\/\\S+)?)");
	public static Pattern anyWhitespaceAndSymbols = Pattern.compile(pattern);

	public static String trim(String input) {
		String startClean = startingWhitespaceAndSymbols.matcher(input)
				.replaceFirst("");
		String endClean = trailingWhitespaceAndSymbols.matcher(startClean)
				.replaceAll("");
		;
		return endClean;
	}

	public static String removeWebsite(String input) {
		return anyUrl.matcher(input).replaceAll("");
	}

	public static String[] splitOnWhiteSpaceAndSymbols(String input) {
		return anyWhitespaceAndSymbols.split(input);
	}

	public static String[] splitCompanyNameIntoWords(String name) {
		return splitOnWhiteSpaceAndSymbols(trim(name));
	}

	/*
	 * Split a string of text into words and remove these common symbols: -
	 * Cashtag, Hashtag, Username - And all others because they cause parsing
	 * errors
	 */
	public static String[] splitTweetIntoWords(String message) {
		String[] words = splitOnWhiteSpaceAndSymbols(trim(removeWebsite(message)));
		for (int i=0; i<words.length; i++) {
			words[i] = words[i].toLowerCase();
		}
		return words;
	}

	public final String ticker;
	public final String name;
	private final String[] splitName;

	private final TweetWindow companiesTweets;

	public CompanyTweetParser(String ticker, String companyName) {
		super();

		this.ticker = ticker;
		name = companyName;
		splitName = CompanyTweetParser.splitCompanyNameIntoWords(companyName);
		companiesTweets = new TweetWindow();
	}

	private boolean containsCompanyName(String[] words) {
		int companyCounter = 0;
		for (int i = 0; i < words.length; i++) {
			String testWord = words[i];
			String companyWord = splitName[companyCounter];
			if (testWord.equalsIgnoreCase(companyWord)) {
				companyCounter++;
				if (companyCounter == splitName.length) {
					return true;
				}
			} else {
				companyCounter = 0;
			}
		}
		return false;
	}

	private boolean containsTicker(String[] words) {
		for (int i = 0; i < words.length; i++) {
			String testWord = words[i];
			if (testWord.equalsIgnoreCase(ticker)) {
				return true;
			}
		}
		return false;
	}

	public boolean addIfForThisCompany(StatusAndMeta statusAndMeta) {
		if (containsTicker(statusAndMeta.words)
				|| containsCompanyName(statusAndMeta.words)) {
			companiesTweets.addTweet(statusAndMeta);
			return true;
		} else {
			return false;
		}
	}

	public void setWindow(long windowInMilliseconds) {
		companiesTweets.setWindow(windowInMilliseconds);
	}

	public int getNumberOfTweets() {
		return companiesTweets.getNumberOfTweetsInWindow();
	}

	public Tweet[] getTweets() {
		return companiesTweets.getTweets();
	}
	
	public Integer getWordFrequency(String word) {
		return companiesTweets.getWordFrequency(word);
	}
	
	public List<? extends List<String>> getNMostFrequentTallies(int n) {
		return companiesTweets.getNMostFrequentTallies(n);
	}
}
