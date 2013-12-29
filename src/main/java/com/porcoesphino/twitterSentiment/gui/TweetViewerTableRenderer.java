package com.porcoesphino.twitterSentiment.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

import com.porcoesphino.twitterSentiment.TweetWindow.Tweet;

/**
 * This TableRenderer renders Tweet objects.
 * 
 * @author bodey.baker@gmail.com
 */
public class TweetViewerTableRenderer implements TableCellRenderer {

	private static final String strColor = "#EDF5F4";
	private static final Color strideColor = Color.decode(strColor);

	JPanel tweetPanel = new JPanel();
	JLabel tweetName = new JLabel();
	JTextArea tweetMessage = new JTextArea();
	Tweet toRender = null;

	public TweetViewerTableRenderer() {
		tweetMessage.setWrapStyleWord(false);
		tweetMessage.setLineWrap(true);
		tweetMessage.setOpaque(false);

		Font defaultFont = tweetName.getFont();
		Font boldFont = defaultFont.deriveFont(Font.BOLD);
		tweetName.setFont(boldFont);
		tweetName.setOpaque(false);

		tweetPanel.setLayout(new BorderLayout());
		tweetPanel.add(tweetName, BorderLayout.NORTH);
		tweetPanel.add(tweetMessage, BorderLayout.SOUTH);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if (toRender != value) {
			toRender = (Tweet) value;
			tweetName.setText("@" + toRender.user);
			tweetMessage.setText(toRender.message);

			if (row % 2 == 0) {
				tweetPanel.setBackground(strideColor);
				tweetPanel.setOpaque(true);
			} else {
				tweetPanel.setOpaque(false);
			}
		}
		return tweetPanel;
	}

}
