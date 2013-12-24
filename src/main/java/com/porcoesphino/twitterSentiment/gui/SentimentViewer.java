package com.porcoesphino.twitterSentiment.gui;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import com.porcoesphino.twitterSentiment.SandP500Lookup;
import com.porcoesphino.twitterSentiment.SentimentServer;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

public class SentimentViewer {

	protected static final long serialVersionUID = 1L;
	
	private SortedCompanyTableModel tableModelCompaniesToView;
	private CompaniesSentimentTableModel tableModelCompaniesSentiment;
	private TweetViewerListModel listModelTweetViewer;
	
	private JFrame frame;
	private int guiPollingPeriodInMilliseconds = 500;
	private int guiPollingWaitInMilliseconds = 0;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SentimentViewer window = new SentimentViewer();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SentimentViewer() {
		try {
			SandP500Lookup.parsePrices();
		} catch (IOException e) {
			System.err.println("Does the companies.txt file exist?");
			e.printStackTrace();
			System.exit(1);
		} catch (ParseException e) {
			System.err.println("Is the companies.txt file corrupted?");
			e.printStackTrace();
			System.exit(1);
		}
		initialize();
	}

	private JPanel generateStartingPanel() {
		
		tableModelCompaniesToView = new SortedCompanyTableModel();
		
		JPanel panelStart = new JPanel();
		panelStart.setLayout(new MigLayout("fill"));
		
		JLabel lblAll = new JLabel("S&P Companies (Select to watch)");
		panelStart.add(lblAll, new CC());
		
		JLabel lblSelected = new JLabel("Companies To Watch (Select to remove)");
		panelStart.add(lblSelected, new CC().wrap());
		
		final JTable tableAll = new JTable();
		
		tableAll.setModel(new AbstractTableModel() {
			
			private static final long serialVersionUID = SentimentViewer.serialVersionUID;
			String[] tickers = SandP500Lookup.getTickers();
			Boolean sorted = false;
			
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (!sorted) {
					Arrays.sort(tickers);
				}
				String ticker = tickers[rowIndex];
				if (columnIndex == 0) {
					return ticker;
				}
				return SandP500Lookup.getCompanyName(ticker);
			}
			
			public int getRowCount() {
				return tickers.length;
			}
			
			public int getColumnCount() {
				return 2;
			}
			
			@Override
			public String getColumnName(int column) {
				if (column == 0) {
					return "Ticker";
				} else {
					return "Name";
				}
			}
		});
		tableAll.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				for (int rowIndex : tableAll.getSelectedRows() ) {
					String ticker = (String) tableAll.getValueAt(rowIndex, 0);
					tableModelCompaniesToView.addCompany(ticker);
				}
			}
		});
		JScrollPane scrollPaneAll = new JScrollPane(tableAll);
		panelStart.add(scrollPaneAll, new CC().grow().pushX().growX());
		
		final JTable tableSelected = new JTable();
		tableSelected.setModel(tableModelCompaniesToView);
		tableSelected.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int[] selected = tableSelected.getSelectedRows();
				tableSelected.clearSelection();
				for (int rowIndex : selected) {
					String ticker = (String) tableSelected.getValueAt(rowIndex, 0);
					tableModelCompaniesToView.removeCompany(ticker);
				}
				
			}
		});
		JScrollPane scrollPaneSelected = new JScrollPane(tableSelected);
		panelStart.add(scrollPaneSelected, new CC().grow().pushX().growX());
		
		return panelStart;
	}
	
	private JPanel generateRunningPanel(final SentimentServer sentiment) {
		
		JPanel panelRunning = new JPanel();
		panelRunning.setLayout(new MigLayout("fill"));
		
		JLabel lblSelected = new JLabel("Companies");
		panelRunning.add(lblSelected, new CC());
		
		final JLabel lblFocus = new JLabel("");
		panelRunning.add(lblFocus, new CC().wrap());
		
		final JTable tableAll = new JTable();
		tableModelCompaniesSentiment =
			    new CompaniesSentimentTableModel(sentiment);
		tableAll.setModel(tableModelCompaniesSentiment);
		JScrollPane scrollPaneAll = new JScrollPane(tableAll);
		panelRunning.add(scrollPaneAll, new CC().grow().pushX().growX());
		
		listModelTweetViewer = new TweetViewerListModel(sentiment);
		final JList<String> listTweets = new JList<String>(listModelTweetViewer);
		
		JScrollPane scrollPaneTweets = new JScrollPane(listTweets);
		panelRunning.add(scrollPaneTweets, new CC().grow().pushX().growX().wrap());
		
		final JLabel lblPreMissed = new JLabel("Unreceived tweets due to track limitation: "
		    + 0);
		
		Timer timerUnreceivedTweets = new Timer();
		timerUnreceivedTweets.schedule(new TimerTask() {
			@Override
			public void run() {
				lblPreMissed.setText("Unreceived tweets due to track limitation: " +
					     sentiment.getNumberOfLimitedStatuses());
			}
		}, guiPollingWaitInMilliseconds, guiPollingPeriodInMilliseconds);
		panelRunning.add(lblPreMissed, new CC().span(2));
		
		listModelTweetViewer.setSize(listTweets.getWidth());
		
		tableAll.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				int[] indexes = tableAll.getSelectedRows();
				if (indexes.length == 0) {
					lblFocus.setText("");
					return;
				}
				int rowIndex = indexes[0];
				String ticker = (String) tableAll.getValueAt(rowIndex, 0);
				String name = (String) tableAll.getValueAt(rowIndex, 1);
				String displayName;
				if (name.contains("<html>")) {
					displayName = "Unknown Tweets";
					listModelTweetViewer.setSize(listTweets.getWidth());
					listModelTweetViewer.setCompany(null);
				} else {
					displayName = name + " (" + ticker + ")";
					listModelTweetViewer.setSize(listTweets.getWidth());
					listModelTweetViewer.setCompany(ticker);
				}
				lblFocus.setText((new Date()).toString() + ": " + displayName);
			}
		});
		
		return panelRunning;
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		final String msgStart = "Start";
		final String msgRunning = "Stop";
		
		final SentimentServer sentiment = new SentimentServer();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(
		    new MigLayout("fill"));
		
		final JPanel panelContent = new JPanel();
		final CardLayout cardLayout = new CardLayout();
		panelContent.setLayout(cardLayout);
		final JPanel panelStarting = generateStartingPanel();
		final JPanel panelRunning = generateRunningPanel(sentiment);
		panelContent.add(panelStarting, msgStart);
		panelContent.add(panelRunning, msgRunning);
		frame.getContentPane().add(panelContent, new CC().dockNorth().grow().push().wrap());
		
		final JPanel panelCommon = new JPanel();
		panelCommon.setLayout(new MigLayout("fill"));
		frame.getContentPane().add(panelCommon, new CC().dockSouth().growX().pushX());
		
		final String strInterval = "Interval (mins)";
		final JLabel labelInterval = new JLabel(strInterval);
		panelCommon.add(labelInterval);
		
		final JSpinner spinnerInterval = new JSpinner(new SpinnerNumberModel(5, .5, 60, 1));
		panelCommon.add(spinnerInterval, new CC());
		
		final JButton btnStart = new JButton(msgStart);
		panelCommon.add(btnStart, new CC().grow());
		
		btnStart.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (btnStart.getText().contentEquals(msgStart)) {
					btnStart.setText(msgRunning);
					spinnerInterval.setEnabled(false);
					labelInterval.setToolTipText("Until there are tweets from "
					    + "the full interval, this indicator will be red");
					labelInterval.setText("<html><font style=\"color:red\">"
					    + strInterval
					    + "</font></html>");
					long waitInMilliSeconds =
					    ((Double) spinnerInterval.getValue()).longValue()
					    * 60 * 1000;
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							labelInterval.setText(
							    "<html><font style=\"color:green\">"
							    + strInterval
							    + "Interval (mins)</font></html>");
						}
					}, waitInMilliSeconds);
					cardLayout.last(panelContent);
					sentiment.setCompanies(
					    tableModelCompaniesToView.getCompaniesTickers());
					tableModelCompaniesSentiment.updateTickers();
					sentiment.startServer();
					Timer timer2 = new Timer();
					timer2.schedule(new TimerTask() {
						@Override
						public void run() {
							tableModelCompaniesSentiment.updateCounters();
						}
					}, guiPollingWaitInMilliseconds, guiPollingPeriodInMilliseconds);
				} else if (btnStart.getText().contentEquals(msgRunning)) {
					btnStart.setText(msgStart);
					spinnerInterval.setEnabled(true);
					labelInterval.setToolTipText("");
					labelInterval.setText("Interval (mins)");
					cardLayout.first(panelContent);
					sentiment.stopServer();
					listModelTweetViewer.clearTweets();
				} else {
					throw new RuntimeException("Button out of Sync. No idea how.");
				}
			}
		});
		
		cardLayout.first(panelContent);
		
		tableModelCompaniesToView.addCompany("GOOG");
		tableModelCompaniesToView.addCompany("AMZN");
		tableModelCompaniesToView.addCompany("AAPL");
		tableModelCompaniesToView.addCompany("MSFT");
	}

}