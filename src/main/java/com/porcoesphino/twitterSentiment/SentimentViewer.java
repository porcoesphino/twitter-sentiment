package com.porcoesphino.twitterSentiment;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

public class SentimentViewer {

	public final LinkedList<String> viewingTickers = new LinkedList<String>();
	
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
	
	@SuppressWarnings("serial")
	class TableCompaniesToView extends AbstractTableModel {
		
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

	@SuppressWarnings("serial")
	private void setStartingPanel(final JPanel panelContent, final JButton btnStart, final JSpinner spinnerInterval) {
		
		panelContent.removeAll();
		
		final TableCompaniesToView companiesToView = new TableCompaniesToView();
		
		JPanel startContent = new JPanel();
		
		startContent.setLayout(new MigLayout("fill"));
		panelContent.add(startContent, new CC().grow().push());
		
		JLabel lblAll = new JLabel("S&P Companies (Select to watch)");
		startContent.add(lblAll, new CC());
		
		JLabel lblSelected = new JLabel("Companies To Watch (Select to remove)");
		startContent.add(lblSelected, new CC().wrap());
		
		final JTable tableAll = new JTable();
		tableAll.setModel(new AbstractTableModel() {
			
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
		startContent.add(scrollPaneAll, new CC().grow().pushX().growX());
		
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
		startContent.add(scrollPaneSelected, new CC().grow().pushX().growX());
	}
	
	@SuppressWarnings("serial")
	private void setRunningPanel(final JPanel panelContent, final JButton btnStart, final JSpinner spinnerInterval) {
		
		panelContent.removeAll();
		
		JPanel runningContent = new JPanel();
		
		runningContent.setLayout(new MigLayout("fill"));
		panelContent.add(runningContent, new CC().grow().push());
		
		JLabel lblSelected = new JLabel("Companies");
		runningContent.add(lblSelected, new CC().wrap());
		
		final JTable tableAll = new JTable();
		tableAll.setModel(new AbstractTableModel() {
			
			public Object getValueAt(int rowIndex, int columnIndex) {
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
				return viewingTickers.size();
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
		});
		tableAll.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent e) {
				for (int rowIndex : tableAll.getSelectedRows() ) {
					String ticker = (String) tableAll.getValueAt(rowIndex, 0);
					String name = (String) tableAll.getValueAt(rowIndex, 1);
				}
			}
		});
		JScrollPane scrollPaneAll = new JScrollPane(tableAll);
		runningContent.add(scrollPaneAll, new CC().grow().pushX().growX());
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(
		    new MigLayout("fill"));
		
		final JPanel panelContent = new JPanel();
		panelContent.setLayout(new MigLayout("fill"));
		frame.getContentPane().add(panelContent, new CC().dockNorth().grow().push().wrap());
		
		JPanel panelCommon = new JPanel();
		//TODO: CardLayout?
		panelContent.setLayout(new MigLayout("fill"));
		frame.getContentPane().add(panelCommon, new CC().dockSouth().growX().pushX());
		
		JLabel labelInterval = new JLabel("Interval (mins)");
		panelCommon.add(labelInterval);
		
		final JSpinner spinnerInterval = new JSpinner(new SpinnerNumberModel(5, .5, 60, 1));
		panelCommon.add(spinnerInterval, new CC());
		
		final String msgStart = "Start";
		final String msgRunning = "Stop";
		final JButton btnStart = new JButton(msgStart);
		panelCommon.add(btnStart, new CC().grow());
		
		btnStart.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if (btnStart.getText().contentEquals(msgStart)) {
					btnStart.setText(msgRunning);
					spinnerInterval.setEnabled(false);
					setRunningPanel(panelContent, btnStart, spinnerInterval);
				} else if (btnStart.getText().contentEquals(msgRunning)) {
					btnStart.setText(msgStart);
					spinnerInterval.setEnabled(true);
					setStartingPanel(panelContent, btnStart, spinnerInterval);
				} else {
					throw new RuntimeException("Button out of Sync. No idea how.");
				}
			}
		});
		
		addSorted("GOOG", viewingTickers);
		addSorted("AMZN", viewingTickers);
		addSorted("AAPL", viewingTickers);
		addSorted("MSFT", viewingTickers);
		
		setStartingPanel(panelContent, btnStart, spinnerInterval);
	}

}
