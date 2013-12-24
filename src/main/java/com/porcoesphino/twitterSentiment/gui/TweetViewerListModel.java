package com.porcoesphino.twitterSentiment.gui;

import javax.swing.AbstractListModel;

import com.porcoesphino.twitterSentiment.SentimentServer;
import com.porcoesphino.twitterSentiment.TweetWindow;
import com.porcoesphino.twitterSentiment.TweetWindow.Tweet;

public class TweetViewerListModel extends AbstractListModel<String> {

	private static final long serialVersionUID = SentimentViewer.serialVersionUID;
	private final SentimentServer sentiment;
	private Tweet[] tweets;
	private int width;
	
	public TweetViewerListModel(SentimentServer sentiment) {
		this.sentiment = sentiment;
		this.width = width;
	}
	
	public int getSize() {
		if (tweets == null) {
			return 0;
		}
		return tweets.length;
	}

	public String getElementAt(int index) {
		TweetWindow.Tweet tweet = tweets[index];
		String html = "<html><div style='padding:2px;"
		    + "width:" + width + ";"
		    + "background-color:#EDF5F4;color:black'>"
		    + "<div style='padding:2px;font-weight:500;'>"
		    + "@" + tweet.user
		    + "</div><p style='text-wrap:break-word;'>"
		    + tweet.message
		    + "</p></div></html>";
		return html;
	}
	
	public void setSize(int width) {
		this.width = width;
	}
	
	public void setCompany(String ticker) {
		if (ticker == null) {
			tweets = sentiment.getUnmatchedTweets();
		} else {
			tweets = sentiment.getTweetsForCompany(ticker);
		}
		fireContentsChanged(this, 0, tweets.length);
	}
	
	public void clearTweets() { 
		tweets = new Tweet[0];
		fireContentsChanged(this, 0, tweets.length);
	}
}
