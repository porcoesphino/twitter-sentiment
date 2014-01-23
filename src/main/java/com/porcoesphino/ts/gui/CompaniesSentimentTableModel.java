package com.porcoesphino.ts.gui;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

import com.porcoesphino.ts.SandP500Lookup;
import com.porcoesphino.ts.SentimentServer;

/**
 * A TableModel used to show the selected S&P companies and the amount of
 * tweets currently sent about them.
 *  
 * @author bodey.baker@gmail.com
 */
public class CompaniesSentimentTableModel extends AbstractTableModel {
	
	private class CounterUpdaterSwingWorker extends SwingWorker<String[], Void> {
		@Override
		protected String[] doInBackground() throws Exception {
			String[] newCounters = new String[tickerList.length+1]; 
			for (int rowIndex = 0; rowIndex < tickerList.length; rowIndex++) {
				String ticker = tickerList[rowIndex];
				newCounters[rowIndex] = Integer.toString(
						sentiment.getNumberOfTweetsForCompany(ticker));
			}
			newCounters[tickerList.length] = Integer.toString(
					sentiment.getNumberOfUnmatchedTweets());
			return newCounters;
		}
		@Override
		protected void done() {
			try {
				tweetCounts = get();
				for (int rowIndex = 0; rowIndex <= tickerList.length; rowIndex++) {
					fireTableCellUpdated(rowIndex, 2);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			counterUpdater = null;
		}
	}
	
	private class WordUpdaterSwingWorker extends SwingWorker<String[], Void> {
		@Override
		protected String[] doInBackground() throws Exception {
			String[] newWords = new String[tickerList.length+1]; 
			for (int rowIndex = 0; rowIndex < tickerList.length; rowIndex++) {
				String ticker = tickerList[rowIndex];
				List<? extends Set<String>> frequentWords
						= sentiment.getNMostFrequentTalliesForCompany(ticker, 10);
				StringBuilder sb = new StringBuilder();
				if (frequentWords != null) {
					for (int level = 0; level<frequentWords.size(); level++) {
						boolean needFirst = true;
						for (String word : frequentWords.get(level)) {
							if (needFirst) {
								sb.append(sentiment.getWordFrequencyForCompany(ticker, word));
								sb.append(": ");
								needFirst = false;
							}
							sb.append(word);
							sb.append(", ");
						}
					}
					if (sb.length() > 2) { 
						sb.delete(sb.length()-2, sb.length()-1);
					}
				}
				newWords[rowIndex] = sb.toString();
			}
			return newWords;
		}
		@Override
		protected void done() {
			try {
				frequentWordsList = get();
				for (int rowIndex = 0; rowIndex <= tickerList.length; rowIndex++) {
					fireTableCellUpdated(rowIndex, 3);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			wordUpdater = null;
		}
	}
	
	private static final long serialVersionUID = SentimentViewer.serialVersionUID;
	private final SentimentServer sentiment;
	private String[] tickerList;
	private String[] tweetCounts;
	private String[] frequentWordsList;
	
	private SwingWorker<String[], Void> counterUpdater;
	private SwingWorker<String[], Void> wordUpdater;
	
	private void updateLists() {
		tickerList = sentiment.getCompaniesTickers();
		tweetCounts = new String[tickerList.length+1];
		frequentWordsList = new String[tickerList.length];
	}
	
	public CompaniesSentimentTableModel(SentimentServer sentiment) {
		this.sentiment = sentiment;
		updateLists();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex == tickerList.length) {
			switch (columnIndex) {
				case 0:
				case 3:
					return "";
				case 1:
					return "<html><div style='color:red;'>Unmatched Tweets</div></html>";
			}
		}
		switch (columnIndex) {
			case 0:
				return tickerList[rowIndex];
			case 1:
				return SandP500Lookup.getCompanyName(tickerList[rowIndex]);
			case 2:
				if (tweetCounts.length == 1) {
					return "<html><span style='color:red'>0</span></html>";
				}
				return tweetCounts[rowIndex];
			default:
				if (frequentWordsList.length == 1) {
					return "<html><span style='color:red'>0</span></html>";
				}
				return frequentWordsList[rowIndex];
		}
	}
	
	public int getRowCount() {
		return tickerList.length + 1;
	}
	
	public int getColumnCount() {
		return 4;
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Ticker";
		case 1:
			return "Name";
		case 2:
			return "Count";
		default:
			return "Frequent Words";
		}
	}
	
	long nextMessageInMillis = System.currentTimeMillis();
	int counter = 0;
	
	public void updateCounters() {
		
		if (counterUpdater != null || wordUpdater != null) {
			counter++;
			long currentMillis = System.currentTimeMillis();
			if (nextMessageInMillis < currentMillis) {
				final double diffInMin = 1;
				nextMessageInMillis = currentMillis 
						+ (long) (diffInMin * 60 * 1000);
				System.err.println("Polled the UI " + counter
						+ " extra times in the last " + diffInMin + " minutes. The system isn't keeping up.");
				counter = 0;
			}
			return;
		}
		
		counterUpdater = new CounterUpdaterSwingWorker();
		counterUpdater.execute();
		wordUpdater = new WordUpdaterSwingWorker();
		wordUpdater.execute();
	}
	
	public void updateTickers() {
		updateLists();
		fireTableDataChanged();
	}
}
