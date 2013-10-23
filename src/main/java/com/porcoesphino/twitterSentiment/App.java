package com.porcoesphino.twitterSentiment;

import twitter4j.FilterQuery;
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
		
		String keywords[] = new String[]{"microsoft, apple"};
		
		StringBuilder builder = new StringBuilder();
		for (String ticker: keywords) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(ticker);
		}
		builder.insert(0, "Matching: ");
		System.out.println(builder.toString());
		System.out.println("Starting!");
		
		TwitterStream twitterStream = new TwitterStreamFactory()
				.getInstance();
		SentimentStatusListener listener = new SentimentStatusListener(keywords);
		twitterStream.addListener(listener);
		twitterStream.filter(listener.getFilterQuery());
	}
}
