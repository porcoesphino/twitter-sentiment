package com.porcoesphino.twitterSentiment;

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
	/*
	 * Split a string of text into words and remove these common symbols:
	 *  - Cashtag, Hashtag, Username
	 *  - And all others because they cause parsing errors
	 */
	public static String[] splitIntoWords(String statusText) {
		return statusText.replaceFirst("^\\W+", "").split("\\W+");
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
