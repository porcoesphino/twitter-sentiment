package com.porcoesphino.twitterSentiment;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractListModel;
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

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

public class SentimentViewer {

	private static final long serialVersionUID = 1L;
	public final LinkedList<String> viewingTickers = new LinkedList<String>();
	private TableCompaniesViewing tableModelViewing;
	
	class Tweet {
		public final String user;
		public final String message;
		
		public Tweet(String user, String message) {
			this.user = user;
			this.message = message;
		}
	}
	
	public static final int addSorted(String string, LinkedList<String> list) {
		for (int listIndex = 0; listIndex < list.size(); listIndex++) {
			String rowTicker =list.get(listIndex);
			if (rowTicker.compareTo(string) > 0) {
				list.add(listIndex, string);
				return listIndex;
			}
		}
		list.add(string);
		return list.size();
	}
	
	public static final int removeSorted(String string, LinkedList<String> List) {
		for (int rowIndex = 0; rowIndex < List.size(); rowIndex++) {
			String rowTicker = List.get(rowIndex);
			if (rowTicker.compareTo(string) == 0) {
				List.remove(rowIndex);
				return rowIndex;
			}
		}
		return -1;
	}
	
	class TableCompaniesViewing extends AbstractTableModel {
		
		private static final long serialVersionUID = SentimentViewer.serialVersionUID;
		private final SentimentServer sentiment;
		
		public TableCompaniesViewing(SentimentServer sentiment) {
			this.sentiment = sentiment;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex == viewingTickers.size()) {
				switch (columnIndex) {
					case 0:
						return "";
					case 1:
						return "<html><div style=\"color:red;\">Unmatched Tweets</div></html>";
					default:
						return 0;
				}
			}
			String ticker = viewingTickers.get(rowIndex);
			switch (columnIndex) {
				case 0:
					return ticker;
				case 1:
					return SandP500Lookup.getCompanyName(ticker);
				default:
					return "0";
			}
		}
		
		public int getRowCount() {
			return viewingTickers.size() + 1;
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
	}
	
	class TableCompaniesToView extends AbstractTableModel {
		
		private static final long serialVersionUID = SentimentViewer.serialVersionUID;

		public void addCompany(String ticker) {
			if (viewingTickers.contains(ticker)) {
				return;
			}
			int insertRow = addSorted(ticker, viewingTickers);
			fireTableRowsInserted(insertRow, insertRow);
		}
		
		public void removeCompany(String ticker) {
			int removedRow = removeSorted(ticker, viewingTickers);
			fireTableRowsDeleted(removedRow, removedRow);
		}
		
		public Object getValueAt(int rowIndex, int columnIndex) {
			String ticker = viewingTickers.get(rowIndex);
			if (columnIndex == 0) {
				return ticker;
			}
			return SandP500Lookup.getCompanyName(ticker);
		}
		
		public int getRowCount() {
			return viewingTickers.size();
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
	}
	
	
	private JFrame frame;

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
		
		final TableCompaniesToView companiesToView = new TableCompaniesToView();
		
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
					companiesToView.addCompany(ticker);
				}
			}
		});
		JScrollPane scrollPaneAll = new JScrollPane(tableAll);
		panelStart.add(scrollPaneAll, new CC().grow().pushX().growX());
		
		final JTable tableSelected = new JTable();
		tableSelected.setModel(companiesToView);
		tableSelected.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int[] selected = tableSelected.getSelectedRows();
				tableSelected.clearSelection();
				for (int rowIndex : selected) {
					String ticker = (String) tableSelected.getValueAt(rowIndex, 0);
					companiesToView.removeCompany(ticker);
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
		tableModelViewing =
			    new TableCompaniesViewing(sentiment);
		tableAll.setModel(tableModelViewing);
		JScrollPane scrollPaneAll = new JScrollPane(tableAll);
		panelRunning.add(scrollPaneAll, new CC().grow().pushX().growX());
		
		final LinkedList<Tweet> displayedTweets = new LinkedList<Tweet>();
		for (int i = 0; i < 20; i++) {
			displayedTweets.add(new Tweet("porcoesphino",
			    "I really, really hope this works!!! 123455kdfjkasldfjas"
			    + "kldfakjflksadjfasldkfjaslkdfjalsdfjsald)"));
		}
		
		final JList<String> listTweets = new JList<String>(new AbstractListModel<String>() {

			private static final long serialVersionUID = SentimentViewer.serialVersionUID;
			
			public int getSize() {
				return displayedTweets.size();
			}

			public String getElementAt(int index) {
				Tweet tweet = displayedTweets.get(index);
				String html = "<html><div style=\"padding:2px;"
				    + "background-color:#EDF5F4;color:black\">"
				    + "<div style=\"padding:2px;font-weight:500;\">"
				    + "@" + tweet.user
				    + "</div><p style=\"text-wrap:break-word;\">"
				    + tweet.message
				    + "</p></div></html>";
				return html;
			}
		});
		
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
		}, 1000, 1000);
		
		panelRunning.add(lblPreMissed, new CC().span(2));
		
		tableAll.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				for (int rowIndex : tableAll.getSelectedRows() ) {
					String ticker = (String) tableAll.getValueAt(rowIndex, 0);
					String name = (String) tableAll.getValueAt(rowIndex, 1);
					String displayName;
					if (name.contains("<html>")) {
						displayName = "Unknown Tweets";
					} else {
						displayName = name + " (" + ticker + ")";
					}
					lblFocus.setText((new Date()).toString() + ": " + displayName);
				}
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
					tableModelViewing.fireTableDataChanged();
					Timer timer2 = new Timer();
					timer2.schedule(new TimerTask() {
						@Override
						public void run() {
							tableModelViewing.fireTableDataChanged();
						}
					}, 1000,5000);
					CompaniesFilter companies = new CompaniesFilter();
					companies.addCompanies(viewingTickers.toArray(new String[] {}));
					sentiment.startServer(companies);
				} else if (btnStart.getText().contentEquals(msgRunning)) {
					btnStart.setText(msgStart);
					spinnerInterval.setEnabled(true);
					labelInterval.setToolTipText("");
					labelInterval.setText("Interval (mins)");
					cardLayout.first(panelContent);
					sentiment.stopServer();
				} else {
					throw new RuntimeException("Button out of Sync. No idea how.");
				}
			}
		});
		
		cardLayout.first(panelContent);
		
		addSorted("GOOG", viewingTickers);
		addSorted("AMZN", viewingTickers);
		addSorted("AAPL", viewingTickers);
		addSorted("MSFT", viewingTickers);
	}

}