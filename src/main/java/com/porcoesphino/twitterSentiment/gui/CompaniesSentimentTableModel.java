package com.porcoesphino.twitterSentiment.gui;

import javax.swing.table.AbstractTableModel;

import com.porcoesphino.twitterSentiment.SandP500Lookup;
import com.porcoesphino.twitterSentiment.SentimentServer;

public class CompaniesSentimentTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = SentimentViewer.serialVersionUID;
	private final SentimentServer sentiment;
	private String[] viewingTickers;
	
	public CompaniesSentimentTableModel(SentimentServer sentiment) {
		this.sentiment = sentiment;
		viewingTickers = sentiment.getCompaniesTickers();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex == viewingTickers.length) {
			switch (columnIndex) {
				case 0:
					return "";
				case 1:
					return "<html><div style=\"color:red;\">Unmatched Tweets</div></html>";
				default:
					return sentiment.getNumberOfUnmatchedTweets();
			}
		}
		String ticker = viewingTickers[rowIndex];
		switch (columnIndex) {
			case 0:
				return ticker;
			case 1:
				return SandP500Lookup.getCompanyName(ticker);
			default:
				return sentiment.getNumberOfTweetsForCompany(ticker);
		}
	}
	
	public int getRowCount() {
		return viewingTickers.length + 1;
	}
	
	public int getColumnCount() {
		return 3;
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Ticker";
		case 1:
			return "Name";
		default:
			return "Count";
		}
	}
	
	public void updateCounters() {
		for (int rowIndex = 0; rowIndex <= viewingTickers.length; rowIndex++) {
			fireTableCellUpdated(rowIndex, 2);
		}
	}
	
	public void updateTickers() {
		viewingTickers = sentiment.getCompaniesTickers();
		fireTableDataChanged();
	}
}
