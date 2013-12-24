package com.porcoesphino.twitterSentiment;

import java.util.LinkedList;
import java.util.Queue;

import twitter4j.Status;

public class TweetWindow {
	
	public class Tweet {
		public final String user;
		public final String message;
		
		Tweet(String user, String message) {
			this.user = user;
			this.message = message;
		}
	}
	
	private final Queue<Status> tweetsInWindow = new LinkedList<Status>();
	
	public int getNumberOfTweetsInWindow() {
		return tweetsInWindow.size();
	}
	
	public void addTweet(Status status) {
		tweetsInWindow.add(status);
	}
	
	public Tweet[] getTweets() {
		Tweet[] tweets = new Tweet[tweetsInWindow.size()];
		int index = 0;
		for (Status status : tweetsInWindow) {
			tweets[index] = new Tweet(status.getUser().getName(), status.getText());
			index++;
		}
		return tweets;
	}

}
