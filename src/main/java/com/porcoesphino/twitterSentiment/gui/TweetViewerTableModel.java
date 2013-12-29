package com.porcoesphino.twitterSentiment.gui;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;

import com.porcoesphino.twitterSentiment.SentimentServer;
import com.porcoesphino.twitterSentiment.TweetWindow.Tweet;

/**
 * This TableModel models the tweets sent about a company during the
 * current window. The TweetWindow object returns cloned data so the
 * display won't interfere with the operation of Twitter4J.
 * 
 * @author bodey.baker@gmail.com
 */
public class TweetViewerTableModel extends AbstractTableModel {

	private static final long serialVersionUID = SentimentViewer.serialVersionUID;
	private final SentimentServer sentiment;
	private String currentTicker;
	private Tweet[] tweets;
	SwingWorker<Tweet[], Void> jlistWorker;

	public TweetViewerTableModel(SentimentServer sentiment) {
		this.sentiment = sentiment;
		this.tweets = new Tweet[0];
	}

	public int getRowCount() {
		return tweets.length;
	}
	
	public int getColumnCount() {
		return 1;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		return tweets[rowIndex];
	}
	
	public void setCompany(final String ticker) {
		if (currentTicker == ticker
				|| (currentTicker != null && currentTicker.equals(ticker))) {
			return;
		}
		currentTicker = ticker;

		if (jlistWorker != null) {
			jlistWorker.cancel(true);
		}
		jlistWorker = new SwingWorker<Tweet[], Void>() {

			@Override
			protected Tweet[] doInBackground() throws Exception {
				final Tweet[] tempList;
				if (ticker == null) {
					tempList = sentiment.getUnmatchedTweets();
				} else {
					tempList = sentiment.getTweetsForCompany(ticker);
				}
				return tempList;
			}

			@Override
			protected void done() {
				try {
					TweetViewerTableModel.this.tweets = get();
					fireTableDataChanged();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		};
		jlistWorker.execute();
	}

	public void clearTweets() {
		tweets = new Tweet[0];
		fireTableDataChanged();
	}

}
