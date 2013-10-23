package com.porcoesphino.twitterSentiment;

import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

/**
 * The main class to query the twitter server and display popular words in
 * real time.
 * 
 * @author bodey.baker@gmail.com
 *
 */
public class App {
	public static void main(String[] args) {
		
		TwitterStream twitterStream = new TwitterStreamFactory()
				.getInstance();

		StatusListener listener = new SentimentStatusListener();
		twitterStream.addListener(listener);
		twitterStream.sample();
	}
}
