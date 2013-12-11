package com.porcoesphino.twitterSentiment;

import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;

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
	
	public static final Path companiesFilePath = FileSystems.getDefault().getPath(
			"", "companies.txt");
	// Don't rely on system having a reasonable charset
	public static final Charset defaultCharset = Charset.forName("UTF-8");
	
	public static void main(String[] args) {
		
		String tickers[] = new String[]{"microsoft", "apple"};
		
		StringBuilder builder = new StringBuilder();
		for (String t: tickers) {
			if (builder.length() != 0) {
				builder.append(", ");
			}
			builder.append(t);
		}
		builder.insert(0, "Matching: ");
		System.out.println(builder.toString());
		System.out.println("Starting!");
		
		// We can't open more than one stream using Twitter4J so only open
		// until the 60 char filter limit is reached.
		// 
		// https://dev.twitter.com/docs/streaming-apis/parameters#track
		// TODO: The above
		// One company has one Listener
		// Each company puts out it's filter query
		// These are appended with a comma
		// Due to rate limits only that many companies can be tracked
		// There are options to change them
		TwitterStream twitterStream = new TwitterStreamFactory()
				.getInstance();
		TweetListener listener = new TweetListener(tickers);
		twitterStream.addListener(listener);
		twitterStream.filter(listener.getFilterQuery());
	}
}
