package com.porcoesphino.twitterSentiment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.porcoesphino.twitterSentiment.TweetWindow.StatusAndMeta;
import com.porcoesphino.twitterSentiment.TweetWindow.Tweet;

import twitter4j.FilterQuery;
import twitter4j.Status;

/**
 * A class to keep track of all the companies we are interested in.
 * Ensures that we don't exceed track limitations when adding companies.
 * Implements the StatusListener and tests to see which companies each
 * incoming tweets applies to.
 * 
 * Note:
 *  - There are still often more tweets then limits allow
 *      - https://dev.twitter.com/docs/streaming-apis/messages#Limit_notices_limit
 * 
 * Tracking Limit:
 *   - https://dev.twitter.com/docs/streaming-apis/parameters#track
 * 
 * @author bodey.baker@gmail.com
 */
public class CompaniesFilter extends AbstractTweetListener{
	private HashMap<String, CompanyTweetParser> companyParsers = new HashMap<String, CompanyTweetParser>();
	private LinkedList<String> trackingFilter = new LinkedList<String>();
	
	private int numberOfLimitedStatuses = 0;
	private TweetWindow unmatchedTweets = new TweetWindow();
	
	// Apparently we can open 200 companies or 400 tracking terms.
	// I didn't see this in the documentation, only a mention of
	// a 60 char limit:
	// 
	// https://dev.twitter.com/docs/streaming-apis/parameters#track
	public boolean addCompany(String ticker) {
		String name = SandP500Lookup.getCompanyName(ticker);
		if (companyParsers.size() >= 200) {
			return false;
		} else {
			CompanyTweetParser parser = new CompanyTweetParser(ticker, name);
			companyParsers.put(ticker, parser);
			trackingFilter.add(ticker);
			trackingFilter.add(name);
			return true;
		}
	}
	
	public void addCompanies(String[] tickers) {
		for (String ticker : tickers) {
			addCompany(ticker);
		}
	}
	
	public String toCompanyListString() {
		StringBuilder builder = new StringBuilder();
		for (CompanyTweetParser parser: companyParsers.values()) {
			builder.append(" - ");
			builder.append(parser.name);
		}
		return builder.toString();
	}
	
	public FilterQuery getFilterQuery() {
		FilterQuery result = new FilterQuery();
		String[] trackingStrings = new String[trackingFilter.size()];
		trackingStrings = trackingFilter.toArray(trackingStrings);
		return result.track(trackingStrings);
	}
	
	public void setWindow(long windowInMilliseconds) {
		for (CompanyTweetParser parser : companyParsers.values()) {
			parser.setWindow(windowInMilliseconds);
		}
		unmatchedTweets.setWindow(windowInMilliseconds);
	}
	
	public String[] getCompaniesTickers() {
		return companyParsers.keySet().toArray(new String[] {});
	}
	
	public int getNumberOfTweetsForCompany(String ticker) {
		return companyParsers.get(ticker).getNumberOfTweets();
	}
	
	public Tweet[] getTweetsForCompany(String ticker) {
		return companyParsers.get(ticker).getTweets();
	}
	
	public int getNumberOfUnmatchedTweets() {
		return unmatchedTweets.getNumberOfTweetsInWindow();
	}
	
	public Tweet[] getUnmatchedTweets() {
		return unmatchedTweets.getTweets();
	}
	
	public int getNumberOfLimitedStatuses() {
		return numberOfLimitedStatuses;
	}
	
	public Integer getWordFrequencyForCompany(String ticker, String word) {
		CompanyTweetParser parser = companyParsers.get(ticker);
		return parser.getWordFrequency(word);
	}
	
	public List<? extends List<String>> getNMostFrequentTalliesForCompany(String ticker, int n) {
		CompanyTweetParser parser = companyParsers.get(ticker);
		return parser.getNMostFrequentTallies(n);
	}
	
	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onTrackLimitationNotice(int)
	 */
	//https://dev.twitter.com/docs/streaming-apis/messages#Limit_notices_limit
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		this.numberOfLimitedStatuses = numberOfLimitedStatuses;
	}
	
	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onStatus(twitter4j.Status)
	 */
	public void onStatus(Status status) {
		String text = status.getText();
		String[] words = CompanyTweetParser.splitTweetIntoWords(text);
		if (words.length == 1 && words[0].length() == 0) {
			unmatchedTweets.addTweet(new StatusAndMeta(status, null));
			return;
		}
		boolean found = false;
		StatusAndMeta statusAndMeta = new StatusAndMeta(status, words);
		for (CompanyTweetParser parser : companyParsers.values()) {
			if (parser.addIfForThisCompany(statusAndMeta)) {
				found = true;
			}
		}
		if (!found) {
			unmatchedTweets.addTweet(new StatusAndMeta(status, null));
		}
	}
}
