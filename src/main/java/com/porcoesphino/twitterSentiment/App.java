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
		
		TwitterStream twitterStream = new TwitterStreamFactory()
				.getInstance();
		SentimentStatusListener listener = new SentimentStatusListener(tickers);
		twitterStream.addListener(listener);
		twitterStream.filter(listener.getFilterQuery());
	}
}
