package com.porcoesphino.ts;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.porcoesphino.ts.TweetWindow.Tweet;

import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

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
	
	private CompaniesFilter companiesFilter;
	private TwitterStream twitterStream;
	
	public SentimentServer() {
		companiesFilter = new CompaniesFilter();
	}
	
	public void addCompanies(String[] companies) {
		companiesFilter.addCompanies(companies);
	}
	
	public void setCompanies(String[] companies) {
		companiesFilter = new CompaniesFilter();
		addCompanies(companies);
	}
	
	public void startServer() {
		twitterStream = new TwitterStreamFactory()
		.getInstance();

		twitterStream.addListener(companiesFilter);

		twitterStream.filter(companiesFilter.getFilterQuery());
	}
	
	public void stopServer() {
		twitterStream.cleanUp();
		twitterStream.shutdown();
		companiesFilter = null;
	}
	
	public void setWindow(long windowInMilliseconds) {
		companiesFilter.setWindow(windowInMilliseconds);
	}
	
	public String[] getCompaniesTickers() {
		if (companiesFilter == null) {
			return new String[] {};
		}
		return companiesFilter.getCompaniesTickers();
	}
	
	public int getNumberOfLimitedStatuses() {
		if (companiesFilter == null) {
			return 0;
		}
		return companiesFilter.getNumberOfLimitedStatuses();
	}
	
	public int getNumberOfTweetsForCompany(String ticker) {
		if (companiesFilter == null) {
			return 0;
		}
		return companiesFilter.getNumberOfTweetsForCompany(ticker);
	}
	
	public Tweet[] getTweetsForCompany(String ticker) {
		if (companiesFilter == null) {
			return new Tweet[0];
		}
		return companiesFilter.getTweetsForCompany(ticker);
	}
	
	public int getNumberOfUnmatchedTweets() {
		if (companiesFilter == null) {
			return 0;
		}
		return companiesFilter.getNumberOfUnmatchedTweets();
	}
	
	public Tweet[] getUnmatchedTweets() {
		if (companiesFilter == null) {
			return new Tweet[0];
		}
		return companiesFilter.getUnmatchedTweets();
	}
	
	public Integer getWordFrequencyForCompany(String ticker, String word) {
		if (companiesFilter == null) {
			return 0;
		}
		return companiesFilter.getWordFrequencyForCompany(ticker, word);
	}
	
	public Map<Integer, ? extends Set<String>> getNMostFrequentTalliesForCompany(String ticker, int n) {
		if (companiesFilter == null) {
			return null;
		}
		return companiesFilter.getNMostFrequentTalliesForCompany(ticker, n);
	}
	
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
	
		final SentimentServer server = new SentimentServer();
		final String[] tickers = new String[]{"AAPL", "GOOG", "MSFT"};
		server.addCompanies(tickers);
//		server.addCompanies(SandP500Lookup.getTickers());
		int windowInMilliseconds = 60 * 1000;
		server.setWindow(windowInMilliseconds);
		
		System.out.println("Starting!");
		
		long durationInMilliSeconds = 1000;
		Timer intervalFinishedUpdateder = new Timer();
		intervalFinishedUpdateder.schedule(new TimerTask() {
			@Override
			public void run() {
				StringBuilder sb = new StringBuilder();
				for (String ticker : tickers) {
					sb.append(ticker);
					sb.append(" (");
					sb.append(server.getNumberOfTweetsForCompany(ticker));
					sb.append("): ");
					Map<Integer, ? extends Set<String>> frequentWords =
							server.getNMostFrequentTalliesForCompany(ticker, 10);
					String wordList = frequentWords.toString();
					wordList = wordList.substring(1, wordList.length() - 1);
					sb.append(wordList);
					sb.append("\n");
				}
				System.out.println(sb.toString());
			}
		}, 0, durationInMilliSeconds);
		
		server.startServer();
		
		
	}
}
