/**
 * 
 */
package com.porcoesphino.twitterSentiment;

import twitter4j.StallWarning;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;

/**
 * A StatusListener purposed to remove the uglier junk out of
 * CompaniesFilter for readability.
 * 
 * @author bodeybaker@gmail.com
 */
public abstract class AbstractTweetListener implements StatusListener {

	/* (non-Javadoc)
	 * @see twitter4j.StreamListener#onException(java.lang.Exception)
	 */
	public void onException(Exception ex) {
		if (ex instanceof TwitterException) {
			TwitterException tEx = (TwitterException) ex;
			if (tEx.exceededRateLimitation()) {
				System.err.println("We're being rate limited and need to wait.");
				System.err.println(tEx.getMessage());
				System.exit(1);
			}
		}
		ex.printStackTrace();
	}

	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onDeletionNotice(twitter4j.StatusDeletionNotice)
	 */
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		System.err.println("Got a status deletion notice id:"
				+ statusDeletionNotice.getStatusId());
	}

	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onScrubGeo(long, long)
	 */
	public void onScrubGeo(long userId, long upToStatusId) {
		System.err.println("Got scrub_geo event userId:" + userId
				+ " upToStatusId:" + upToStatusId);
	}

	/* (non-Javadoc)
	 * @see twitter4j.StatusListener#onStallWarning(twitter4j.StallWarning)
	 */
	public void onStallWarning(StallWarning warning) {
		System.err.println("Got stall warning:" + warning);
	}
}
