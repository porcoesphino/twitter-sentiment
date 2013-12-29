package com.porcoesphino.twitterSentiment.gui;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

import com.porcoesphino.twitterSentiment.SandP500Lookup;
import com.porcoesphino.twitterSentiment.SentimentServer;

/**
 * A TableModel used to show the selected S&P companies and the amount of
 * tweets currently sent about them.
 *  
 * @author bodey.baker@gmail.com
 */
public class CompaniesSentimentTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = SentimentViewer.serialVersionUID;
	private final SentimentServer sentiment;
	private String[] tickerList;
	private String[] tweetCounts;
	private String[] frequentWordsList;
	
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
				return tweetCounts[rowIndex];
			default:
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
	
	public void updateCounters() {
		SwingWorker<String[], Void> counterUpdater = new SwingWorker<String[], Void> () {
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
			}
		};
		counterUpdater.execute();
	}
	
	public void updateTickers() {
		updateLists();
		fireTableDataChanged();
	}
}
