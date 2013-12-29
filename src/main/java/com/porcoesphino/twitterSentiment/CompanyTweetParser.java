package com.porcoesphino.twitterSentiment;

import java.util.regex.Pattern;

import com.porcoesphino.twitterSentiment.TweetWindow.Tweet;

import twitter4j.Status;

/**
 * This class is intended to parse a tweet to test the sentiment.
 * Currently it can only:
 *  - tests if the tweet is about the company it is configured for.
 * 
 * @author bodey.baker@gmail.com
 */
public class CompanyTweetParser {
	
	public static Pattern startingWhitespaceAndSymbols = Pattern.compile("^\\W+");
	public static Pattern anyWhitespaceAndSymbols = Pattern.compile("\\W+");
	
	/*
	 * Split a string of text into words and remove these common symbols:
	 *  - Cashtag, Hashtag, Username
	 *  - And all others because they cause parsing errors
	 */
	//String.split(regex) actually simply invokes Pattern.compile(regex).split(this, limit), and compile() returns a new Pattern each time. If the split is a frequent operation, it is thus beneficial to create and reuse a single Pattern instance for performing the splits.
	public static String[] splitIntoWords(String statusText) {
		// TODO: Test this is actually faster
		return anyWhitespaceAndSymbols.split(
		    startingWhitespaceAndSymbols.matcher(statusText).replaceFirst(""));
		//return statusText.replaceFirst("^\\W+", "").split("\\W+");
	}
	
	public final String ticker;
	public final String name;
	private final String[] splitName;
	
	private final TweetWindow companiesTweets;
	
	public CompanyTweetParser(String ticker, String companyName) {
		super();
		
		
		
		this.ticker = ticker;
		name = companyName;
		splitName = CompanyTweetParser.splitIntoWords(companyName);
		companiesTweets = new TweetWindow();
	}
	
	private boolean containsCompanyName(String[] words) {
		int companyCounter = 0;
		for (int i = 0; i<words.length; i++) {
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
		for (int i = 0; i<words.length; i++) {
			String testWord = words[i];
			if (testWord.equalsIgnoreCase(ticker)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isForThisCompany(String[] words) {
		return containsTicker(words) || containsCompanyName(words);
	}
	
	public void addStatus(Status status) {
		companiesTweets.addTweet(status);
	}
	
	public int getNumberOfTweets() {
		return companiesTweets.getNumberOfTweetsInWindow();
	}
	
	public Tweet[] getTweets() {
		return companiesTweets.getTweets();
	}
}
