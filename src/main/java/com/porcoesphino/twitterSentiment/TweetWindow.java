package com.porcoesphino.twitterSentiment;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import twitter4j.Status;

/**
 * TweetWindow keeps track of the tweets that have been seen over the
 * duration we are interested in and provides access to a cloned copy
 * of those tweets and the current tally. It's expected that access will
 * occur in different threads so methods are synchronized.
 * 
 * @author bodey.baker@gmail.com
 */
public class TweetWindow {
	
	public class Tweet {
		public final String user;
		public final String message;
		
		Tweet(String user, String message) {
			this.user = user;
			this.message = message;
		}
	}
	
	public static class StatusAndMeta {
		public final Status status;
		public final String[] words;
		public final long creationTimestamp;
		
		public StatusAndMeta(Status status, String[] words) {
			this.status = status;
			this.words = words;
			if (status == null) { // Needed for tests
				this.creationTimestamp = -1;
			} else {
				this.creationTimestamp = status.getCreatedAt().getTime();
			}
		}
	}
	
	private final Queue<StatusAndMeta> tweetsInWindow = new LinkedList<StatusAndMeta>();
	long windowInMilliseconds;
	
	private void updateQueue() {
		long currentTimestamp = (new Date()).getTime();
		while (tweetsInWindow.size() > 0) {
			Long firstTweetTimestamp = tweetsInWindow.peek().creationTimestamp;
			if (firstTweetTimestamp < currentTimestamp - windowInMilliseconds) {
				tweetsInWindow.remove();
			} else {
				break;
			}
		}
	}
	
	public synchronized void setWindow(long windowInMilliseconds) {
		TweetWindow.this.windowInMilliseconds = windowInMilliseconds;
	}
	
	public synchronized int getNumberOfTweetsInWindow() {
		updateQueue();
		return tweetsInWindow.size();
	}
	
	public synchronized void addTweet(StatusAndMeta statusAndMeta) {
		updateQueue();
		tweetsInWindow.add(statusAndMeta);
	}
	
	public synchronized Tweet[] getTweets() {
		updateQueue();
		Tweet[] tweets = new Tweet[tweetsInWindow.size()];
		int index = 0;
		for (StatusAndMeta statusAndMeta : tweetsInWindow) {
			tweets[index] = new Tweet(
					statusAndMeta.status.getUser().getScreenName(),
					statusAndMeta.status.getText());
			index++;
		}
		return tweets;
	}

}
