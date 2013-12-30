package com.porcoesphino.twitterSentiment.gui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class SwingHelper {
	/**
	 * Resizes the columns to match content while keeping the table the same
	 * size. This means that the last column may be larger than the content.
	 */
	public static void resizeTableColumns(JTable table) {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		int totalWidth = 0;
		for (int i = 0; i < table.getColumnCount(); i++) {
			DefaultTableColumnModel colModel = (DefaultTableColumnModel) table
					.getColumnModel();
			TableColumn col = colModel.getColumn(i);
			
			if (i == table.getColumnCount() -1) {
				col.setPreferredWidth(table.getWidth() - totalWidth);
				table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				return;
			}
			
			int width = 0;
			TableCellRenderer renderer = col.getHeaderRenderer();
			if (renderer == null) {
				renderer = table.getTableHeader().getDefaultRenderer();
			}
			Component comp = renderer.getTableCellRendererComponent(table,
					col.getHeaderValue(), false, false, -1, i);
			width = Math.max(width, comp.getPreferredSize().width);
			for (int r = 0; r < table.getRowCount(); r++) {
				renderer = table.getCellRenderer(r, i);
				comp = renderer.getTableCellRendererComponent(table,
						table.getValueAt(r, i), false, false, r, i);
				width = Math.max(width, comp.getPreferredSize().width);
			}
			totalWidth += width + 2;
			col.setPreferredWidth(width + 2);
		}
	}
}
