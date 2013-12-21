package com.porcoesphino.twitterSentiment;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;

public class SentimentViewer {

	@SuppressWarnings("serial")
	class TableCompaniesToView extends AbstractTableModel {
		
		LinkedList<String> tickers = new LinkedList<String>();
		HashMap<String, String> tickersToCompanyNames = new HashMap<String, String>();
		
		public void addCompany(String ticker, String name) {
			if (tickersToCompanyNames.containsKey(ticker)) {
				return;
			}
			tickers.push(ticker);
			tickersToCompanyNames.put(ticker, name);
			fireTableRowsInserted(tickers.size(), tickers.size());
		}
		
		public void removeCompany(String ticker) {
			tickersToCompanyNames.remove(ticker);
			for (int rowIndex = 0; rowIndex < tickers.size(); rowIndex++) {
				String rowTicker = tickers.get(rowIndex);
				if (rowTicker.compareTo(ticker) == 0) {
					tickers.remove(rowIndex);
					fireTableRowsDeleted(rowIndex, rowIndex);
				}
			}
		}
		
		public Object getValueAt(int rowIndex, int columnIndex) {
			String ticker = tickers.get(rowIndex);
			if (columnIndex == 0) {
				return ticker;
			}
			return tickersToCompanyNames.get(ticker);
		}
		
		public int getRowCount() {
			return tickers.size();
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
	private void setStartingPanel(JPanel panelContent, JButton btnStart) {
		
		final TableCompaniesToView companiesToView = new TableCompaniesToView();
		companiesToView.addCompany("GOOG", "Google");
		companiesToView.addCompany("AMZN", "Amazon.com");
		companiesToView.addCompany("AAPL", "Apple");
		companiesToView.addCompany("MSFT", "Microsoft");
		
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
					String name = (String) tableAll.getValueAt(rowIndex, 1);
					companiesToView.addCompany(ticker, name);
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
		
		btnStart.setText("Start");
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
		
		JPanel panelContent = new JPanel();
		panelContent.setLayout(new MigLayout("fill"));
		frame.getContentPane().add(panelContent, new CC().dockNorth().grow().push());
		
		JButton btnStart = new JButton("Start");
		frame.getContentPane().add(btnStart, new CC().dockSouth());
		
		setStartingPanel(panelContent, btnStart);
	}

}
