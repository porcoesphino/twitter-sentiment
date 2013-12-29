package com.porcoesphino.twitterSentiment.gui;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.porcoesphino.twitterSentiment.SandP500Lookup;
import com.porcoesphino.twitterSentiment.SentimentServer;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

/**
 * A GUI to view the numbers of tweets about selected S&P companies.
 * 
 * @author bodey.baker@gmail.com
 */
public class SentimentViewer {

	protected static final long serialVersionUID = 1L;
	
	private SortedCompanyTableModel selectedCompaniesModel;
	private CompaniesSentimentTableModel sentimentModel;
	private TweetViewerTableModel tweetsModel;
	
	private JFrame frame;
	private final int sentimentTableInitialWidth = 400;
	private final int guiPollingPeriodInMilliseconds = 100;
	private final int guiPollingWaitInMilliseconds = 0;
	private final int initialWindowInMinutes = 1;
	
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

	private JPanel generateInitialPanel() {
		
		// Construct and add Components
		JPanel initPanel = new JPanel();
		initPanel.setLayout(new MigLayout("fill"));
		
		JLabel allCompaniesLabel = 
				new JLabel("S&P Companies (Select to watch)");
		initPanel.add(allCompaniesLabel, new CC());
		
		JLabel selectedCompaniesLabel =
				new JLabel("Companies To Watch (Select to remove)");
		initPanel.add(selectedCompaniesLabel, new CC().wrap());
		
		final JTable allCompaniesIndicator = new JTable();
		
		JScrollPane allCompaniesScroller = 
				new JScrollPane(allCompaniesIndicator);
		initPanel.add(allCompaniesScroller, new CC().grow().pushX().growX());
		
		final JTable selectedCompaniesIndicator = new JTable();
		
		JScrollPane selectedCompaniesScroller = 
				new JScrollPane(selectedCompaniesIndicator);
		initPanel.add(selectedCompaniesScroller, 
				new CC().grow().pushX().growX());
		
		// Construct Models
		selectedCompaniesModel = new SortedCompanyTableModel();
		SortedCompanyTableModel allCompaniesModel =
				new SortedCompanyTableModel(SandP500Lookup.getTickers());
		
		// Configure Components and Models
		allCompaniesIndicator.setModel(allCompaniesModel);
		allCompaniesIndicator.getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				for (int rowIndex : allCompaniesIndicator.getSelectedRows() ) {
					String ticker = (String)
							allCompaniesIndicator.getValueAt(rowIndex, 0);
					selectedCompaniesModel.addCompany(ticker);
				}
			}
		});
		
		selectedCompaniesIndicator.setModel(selectedCompaniesModel);
		selectedCompaniesIndicator.getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int[] selected = selectedCompaniesIndicator.getSelectedRows();
				selectedCompaniesIndicator.clearSelection();
				for (int rowIndex : selected) {
					String ticker = (String) selectedCompaniesIndicator
							.getValueAt(rowIndex, 0);
					selectedCompaniesModel.removeCompany(ticker);
				}
			}
		});
		
		return initPanel;
	}
	
	private JPanel generateRunningPanel(final SentimentServer sentiment) {
		
		// Construct and Add Components
		JPanel execPanel = new JPanel();
		execPanel.setLayout(new MigLayout("fill"));
		
		final JTable sentimentIndicator = new JTable();
		JScrollPane sentimentScroller = new JScrollPane(sentimentIndicator);
		
		final JTable tweetsIndicator = new JTable();
		JScrollPane tweetsScroller = new JScrollPane(tweetsIndicator);
		
		JSplitPane splitPane = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT, sentimentScroller, tweetsScroller);
		execPanel.add(splitPane, 
				new CC().span(2).wrap().grow().pushX().growX());
		
		final JLabel unreceivedTweetsIndicator = new JLabel(
				"Unreceived tweets due to track limitation: " + 0);
		execPanel.add(unreceivedTweetsIndicator, new CC().span(2));
		
		// Construct Models
		sentimentModel = new CompaniesSentimentTableModel(sentiment);
		sentimentIndicator.setModel(sentimentModel);
		
		tweetsModel = new TweetViewerTableModel(sentiment);
		tweetsIndicator.setModel(tweetsModel);
		
		// Configure Components
		splitPane.setDividerLocation(sentimentTableInitialWidth);
		
		sentimentIndicator.getSelectionModel().setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION);
		
		tweetsIndicator.setDefaultRenderer(Object.class,
				new TweetViewerTableRenderer());
		tweetsIndicator.setAutoCreateColumnsFromModel(false);
		tweetsIndicator.getColumnModel().getColumn(0).setHeaderValue(" ");
		
		// Add Listeners
		JViewport tweetsViewport = tweetsScroller.getViewport();
		final ResizesRowsOnScrollChangeListener tweetsScrollListener =
				new ResizesRowsOnScrollChangeListener(
						tweetsViewport, tweetsIndicator, tweetsModel);
		tweetsViewport.addChangeListener(tweetsScrollListener);
		
		sentimentIndicator.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int[] indexes = sentimentIndicator.getSelectedRows();
				if (indexes.length == 0) {
					return;
				}
				int rowIndex = indexes[0];
				String ticker = (String) sentimentIndicator.getValueAt(rowIndex, 0);
				if (ticker.length() == 0) {
					tweetsModel.setCompany(null);
				} else {
					tweetsModel.setCompany(ticker);
				}
				
				tweetsIndicator.getColumnModel().getColumn(0).setHeaderValue(
						new Date().toString());
				tweetsIndicator.getTableHeader().resizeAndRepaint();
				
				tweetsIndicator.scrollRectToVisible(tweetsIndicator.getCellRect(0, 0, true));
				tweetsScrollListener.resetSeen();
			}
		});
		
		// Set Timers
		Timer unreceivedTweetsUpdated = new Timer();
		unreceivedTweetsUpdated.schedule(new TimerTask() {
			@Override
			public void run() {
				unreceivedTweetsIndicator.setText("Unreceived tweets due to track limitation: " +
					     sentiment.getNumberOfLimitedStatuses());
			}
		}, guiPollingWaitInMilliseconds, guiPollingPeriodInMilliseconds);
		
		return execPanel;
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		final String startMessage = "Start";
		final String stopMessage = "Stop";
		
		final SentimentServer sentiment = new SentimentServer();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("fill"));
		
		final JPanel mainContentPanel = new JPanel();
		final CardLayout cardLayout = new CardLayout();
		mainContentPanel.setLayout(cardLayout);
		final JPanel panelStarting = generateInitialPanel();
		final JPanel panelRunning = generateRunningPanel(sentiment);
		mainContentPanel.add(panelStarting, startMessage);
		mainContentPanel.add(panelRunning, stopMessage);
		frame.getContentPane().add(mainContentPanel,
				new CC().dockNorth().grow().push().wrap());
		
		final JPanel commonContentPanel = new JPanel();
		commonContentPanel.setLayout(new MigLayout("fill"));
		frame.getContentPane().add(commonContentPanel,
				new CC().dockSouth().growX().pushX());
		
		final String intervalMessage = "Interval (mins)";
		final JLabel intervalIndicator = new JLabel(intervalMessage);
		commonContentPanel.add(intervalIndicator);
		
		final SpinnerNumberModel intervalController =
				new SpinnerNumberModel(initialWindowInMinutes, 1, 60, 1);
		final JSpinner intervalViewer = new JSpinner(intervalController);
		commonContentPanel.add(intervalViewer, new CC());
		
		final JButton startController = new JButton(startMessage);
		commonContentPanel.add(startController, new CC().grow());
		
		startController.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (startController.getText().contentEquals(startMessage)) {
					startController.setText(stopMessage);
					intervalViewer.setEnabled(false);
					intervalIndicator.setToolTipText("Until there are tweets" +
					    " from the full interval, this indicator will be red");
					intervalIndicator.setText("<html><font style='color:red'>"
					    + intervalMessage
					    + "</font></html>");
					long windowInMilliSeconds =
					    intervalController.getNumber().longValue()
					    * 60 * 1000;
					Timer intervalFinishedUpdateder = new Timer();
					intervalFinishedUpdateder.schedule(new TimerTask() {
						@Override
						public void run() {
							intervalIndicator.setText(
							    "<html><font style='color:green'>"
							    + intervalMessage
							    + "</font></html>");
						}
					}, windowInMilliSeconds);
					cardLayout.last(mainContentPanel);
					sentiment.setCompanies(
					    selectedCompaniesModel.getCompaniesTickers());
					sentimentModel.updateTickers();
					sentiment.setWindow(windowInMilliSeconds);
					sentiment.startServer();
					Timer sentimentIndicatorsUpdater = new Timer();
					sentimentIndicatorsUpdater.schedule(new TimerTask() {
						@Override
						public void run() {
							sentimentModel.updateCounters();
						}
					}, guiPollingWaitInMilliseconds, guiPollingPeriodInMilliseconds);
				} else if (startController.getText().contentEquals(stopMessage)) {
					startController.setText(startMessage);
					intervalViewer.setEnabled(true);
					intervalIndicator.setToolTipText("");
					intervalIndicator.setText("Interval (mins)");
					cardLayout.first(mainContentPanel);
					sentiment.stopServer();
					tweetsModel.clearTweets();
				}
			}
		});
		
		cardLayout.first(mainContentPanel);
		
		selectedCompaniesModel.addCompany("GOOG");
		selectedCompaniesModel.addCompany("AMZN");
		selectedCompaniesModel.addCompany("AAPL");
		selectedCompaniesModel.addCompany("MSFT");
	}

}