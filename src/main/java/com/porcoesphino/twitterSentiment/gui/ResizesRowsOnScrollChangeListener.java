package com.porcoesphino.twitterSentiment.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;

/**
 * A ChangeListener for a JTable's JViewport.
 * When using a JTable to show custom content sometimes rows must be resized.
 * This class resizes rows the first time they are visible in the JViewport.
 * 
 * @author bodey.baker@gmail.com
 */
public class ResizesRowsOnScrollChangeListener implements ChangeListener {
	
	private JViewport viewport;
	private JTable table;
	private TableModel model;
	private int maxSeenRow;
	
	public ResizesRowsOnScrollChangeListener(JViewport viewport, JTable table, TableModel model) {
		this.table = table;
		this.viewport = viewport;
		this.model = model;
		maxSeenRow = 0;
	}
	
	public void resetSeen() {
		maxSeenRow = 0;
	}
	
	public void stateChanged(ChangeEvent e) {
		
		int column = 0;
		Rectangle viewRect = viewport.getViewRect();
		int first = table.rowAtPoint(new Point(0, viewRect.y));
		if (first == -1) {
			return;
		}
		
		// Occasionally this ChangeListener gets called before the rows
		// can be resized successfully. This is a sledge hammer fix.
		Component comp = table.prepareRenderer(
					table.getCellRenderer(0, column),
						0, column);
		if (comp.getSize().height != comp.getPreferredSize().height) {
			maxSeenRow = 0;
		}
		
		int last = table.rowAtPoint(new Point(0, viewRect.y
				+ viewRect.height - 1));
		if (last == -1) {
			last = model.getRowCount() - 1;
		}
		if (last == 0 || last > maxSeenRow) {
			for (int row = maxSeenRow; row <= last; row++) {
				comp = table.prepareRenderer(
						table.getCellRenderer(row, column),
							row, column);
				int rowHeight = comp.getPreferredSize().height;
				if (comp.getSize().height != rowHeight) {
					table.setRowHeight(row, rowHeight);
				}
			}
			maxSeenRow = last;
		}
	}
}
