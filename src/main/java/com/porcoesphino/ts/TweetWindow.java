package com.porcoesphino.ts;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
	
	public static class Tweet {
		public final String user;
		public final String message;
		public final String[] words;
		public final Date created;
		
		private Tweet(StatusAndMeta statusAndMeta) {
			this.user = statusAndMeta.status.getUser().getScreenName();
			this.message = statusAndMeta.status.getText();
			this.created = statusAndMeta.status.getCreatedAt();
			this.words = statusAndMeta.words;
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
	private final KeyTally<String> wordFrequencies = new KeyTally<String>();
	long windowInMilliseconds;
	
	private void updateQueue() {
		long currentTimestamp = (new Date()).getTime();
		while (tweetsInWindow.size() > 0) {
			StatusAndMeta oldestTweet = tweetsInWindow.peek();
			Long firstTweetTimestamp = oldestTweet.creationTimestamp;
			if (firstTweetTimestamp < currentTimestamp - windowInMilliseconds) {
				if (oldestTweet.words != null) {
					for (String word : oldestTweet.words) {
						wordFrequencies.incrementKey(word, -1);
					}
				}
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
	
	public synchronized Integer getWordFrequency(String word) {
		return wordFrequencies.getTally(word);
	}
	
	public synchronized List<? extends Set<String>> getNMostFrequentTallies(int n) {
		return wordFrequencies.getNMostFrequentTallySets(n);
	}
	
	public synchronized void addTweet(StatusAndMeta statusAndMeta) {
		updateQueue();
		tweetsInWindow.add(statusAndMeta);
		if (statusAndMeta.words != null) {
			for (String word : statusAndMeta.words) {
				wordFrequencies.incrementKey(word, 1);
			}
		}
	}
	
	public synchronized Tweet[] getTweets() {
		updateQueue();
		Tweet[] tweets = new Tweet[tweetsInWindow.size()];
		int index = 0;
		for (StatusAndMeta statusAndMeta : tweetsInWindow) {
			tweets[index] = new Tweet(statusAndMeta);
			index++;
		}
		return tweets;
	}

}
