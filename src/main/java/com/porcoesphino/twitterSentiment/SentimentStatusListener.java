/**
 * 
 */
package com.porcoesphino.twitterSentiment;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * The StatusListener that processes the sentiment.
 * 
 * @author bodeybaker@gmail.com
 */
public class SentimentStatusListener implements StatusListener {
	
	private final FilterQuery filterQuery;
	
	static String[] getFilterFromTickers(String[] tickers){
		String[] filterList = new String[tickers.length*3];
		String[] prefixes = new String[]{"$", "#", "@"};
		for (int tickerI=0; tickerI<tickers.length; tickerI++) {
			for (int prefixI=0; prefixI<prefixes.length; prefixI++) {
				filterList[tickerI*prefixes.length+prefixI] = prefixes[prefixI] + tickers[tickerI];
			}
		}
		return filterList;
	}
	
	public SentimentStatusListener(String[] tickers) {
		super();
		filterQuery = new FilterQuery();
		filterQuery.track(getFilterFromTickers(tickers));
		System.out.println(filterQuery);
	}
	
	public FilterQuery getFilterQuery() {
		return filterQuery;
	}

	/* (non-Javadoc)
	 * @see twitter4j.StreamListener#onException(java.lang.Exception)
	 */
	public void onException(Exception ex) {
		ex.printStackTrace();
	}

	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onDeletionNotice(twitter4j.StatusDeletionNotice)
	 */
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		System.out.println("Got a status deletion notice id:"
				+ statusDeletionNotice.getStatusId());
	}

	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onScrubGeo(long, long)
	 */
	public void onScrubGeo(long userId, long upToStatusId) {
		System.out.println("Got scrub_geo event userId:" + userId
				+ " upToStatusId:" + upToStatusId);
	}

	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onStallWarning(twitter4j.StallWarning)
	 */
	public void onStallWarning(StallWarning warning) {
		System.out.println("Got stall warning:" + warning);
	}

	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onStatus(twitter4j.Status)
	 */
	public void onStatus(Status status) {
		System.out.println("@" + status.getUser().getScreenName()
				+ " - " + status.getText());
	}

	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onTrackLimitationNotice(int)
	 */
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		System.out.println("Got track limitation notice:"
				+ numberOfLimitedStatuses);
	}

}
