package com.porcoesphino.twitterSentiment.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

import com.porcoesphino.twitterSentiment.TweetWindow.Tweet;

/**
 * This TableRenderer renders Tweet objects.
 * 
 * @author bodey.baker@gmail.com
 */
public class TweetViewerTableRenderer implements TableCellRenderer {

	private static final String strColor = "#EDF5F4";
	private static final Color strideColor = Color.decode(strColor);

	JPanel tweetPanel;
	JLabel tweetName;
	JLabel tweetDate;
	JTextArea tweetMessage;
	Tweet toRender;
	DateFormat dateFormat;

	public TweetViewerTableRenderer() {
		
		tweetPanel = new JPanel(new MigLayout(new LC().fillX()));
		tweetName = new JLabel();
		tweetDate = new JLabel();
		tweetMessage = new JTextArea();
		toRender = null;
		dateFormat = new SimpleDateFormat("HH:mm:ss");
		
		tweetMessage.setWrapStyleWord(false);
		tweetMessage.setLineWrap(true);
		tweetMessage.setOpaque(false);

		Font defaultFont = tweetName.getFont();
		Font boldFont = defaultFont.deriveFont(Font.BOLD);
		tweetName.setFont(boldFont);
		tweetName.setOpaque(false);

		tweetPanel.add(tweetName, new CC().growX().pushX());
		tweetPanel.add(tweetDate, new CC().wrap());
		tweetPanel.add(tweetMessage, new CC().growX().span(2));
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if (toRender != value) {
			toRender = (Tweet) value;
			tweetName.setText("@" + toRender.user);
			tweetDate.setText(dateFormat.format(toRender.created));
			tweetMessage.setText(toRender.message);
			if (toRender.words == null) {
				tweetPanel.setToolTipText(null);
			} else {
				StringBuilder sb = new StringBuilder(toRender.message.length());
				for (String word : toRender.words) {
					sb.append(word);
					sb.append(' ');
				}
				sb.deleteCharAt(sb.length()-1);
				tweetPanel.setToolTipText(sb.toString());
			}

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
