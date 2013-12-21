package com.porcoesphino.twitterSentiment;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.ParseException;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

//TODO: Add a GUI. Mig Layout? Combo box, only 200 companies.
// This opens a service in another process which is queried

/**
 * The main class to query the twitter server and display popular words in
 * real time.
 * 
 * @author bodey.baker@gmail.com
 *
 */
public class SentimentServer {
	
	public static final Path companiesFilePath = FileSystems.getDefault().getPath(
			"", "companies.txt");
	// Don't rely on system having a reasonable charset
	public static final Charset defaultCharset = Charset.forName("UTF-8");
	private static CompaniesFilter companiesFilter = new CompaniesFilter();
	
	public static void main(String[] args) {
		
		try {
			SandP500Lookup.parsePrices();
		} catch (IOException e) {
			System.err.println("Can't read S&P500 file 'companies.txt'!");
			e.printStackTrace();
			System.exit(1);
		} catch (ParseException e) {
			System.err.println("Can't parse S&P500 file 'companies.txt'!");
			e.printStackTrace();
			System.exit(1);
		}
		
		
		companiesFilter.addCompanies(new String[]{"AAPL", "GOOG", "MSFT"});
		companiesFilter.addCompanies(SandP500Lookup.getTickers());
		
		System.out.println("Starting!");
		
		
		// TODO: The above
		TwitterStream twitterStream = new TwitterStreamFactory()
				.getInstance();
		
		twitterStream.addListener(companiesFilter);
		
		twitterStream.filter(companiesFilter.getFilterQuery());
	}
}
