package com.porcoesphino.ts.gui;

import java.util.LinkedList;

import javax.swing.table.AbstractTableModel;

import com.porcoesphino.ts.SandP500Lookup;

/**
 * This TableModel models S&P companies sorted alphabetically.
 * It locally uses the companies tickers and looks up extra
 * information using the SandP500Lookup singleton.
 * 
 * @author bodey.baker@gmail.com
 */
public class SortedCompanyTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = SentimentViewer.serialVersionUID;
	public final LinkedList<String> companyTickers = new LinkedList<String>();

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
	
	public SortedCompanyTableModel() {}
	
	public SortedCompanyTableModel(String[] tickers) {
		for (String ticker : tickers) {
			if (!companyTickers.contains(ticker)) {
				addSorted(ticker, companyTickers);
			}
		}
	}
	
	public void addCompany(String ticker) {
		if (companyTickers.contains(ticker)) {
			return;
		}
		int insertRow = addSorted(ticker, companyTickers);
		fireTableRowsInserted(insertRow, insertRow);
	}
	
	public void removeCompany(String ticker) {
		int removedRow = removeSorted(ticker, companyTickers);
		fireTableRowsDeleted(removedRow, removedRow);
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		String ticker = companyTickers.get(rowIndex);
		if (columnIndex == 0) {
			return ticker;
		}
		return SandP500Lookup.getCompanyName(ticker);
	}
	
	public int getRowCount() {
		return companyTickers.size();
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
	
	public String[] getCompaniesTickers() {
		return companyTickers.toArray(new String[] {});
	}
}
