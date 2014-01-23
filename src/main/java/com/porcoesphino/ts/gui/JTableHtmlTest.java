package com.porcoesphino.ts.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class JTableHtmlTest extends JFrame {

	protected static final long serialVersionUID = 1L;

	public static class Item {
		public int id;
		public String msg;
	}
	
	static class TableModel extends AbstractTableModel {

		private static final long serialVersionUID = JTableHtmlTest.serialVersionUID;
		private Item[] items = new Item[] {};

		public int getRowCount() {
			return items.length;
		}

		public int getColumnCount() {
			return 1;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return items[rowIndex];
		}
		
		@Override
		public String getColumnName(int column) {
			return "";
		}

		public void updateItems() {
			SwingWorker<Item[], Void> worker = new SwingWorker<Item[], Void>() {

				@Override
				protected Item[] doInBackground() throws Exception {
					final Item[] tempList = new Item[3000];
					for (int i = 0; i < tempList.length; i++) {
						Item item = new Item();
						item.id = (int) (Math.random() * 10000);
						StringBuilder sb = new StringBuilder();
						sb.append("Here is our pretend Message: ");
						for (int wordNumber = 0; wordNumber < item.id / 100; wordNumber++) {
							sb.append('a');
							while (Math.random() > 0.3) {
								sb.append('a');
							}
							sb.append(' ');
						}
						System.out.println(sb.toString());
						item.msg = sb.toString();
						tempList[i] = item;
					}
					return tempList;
				}
				
				@Override
				protected void done() {
					try {
						items = get();
						fireTableDataChanged();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
					
				}
			};
			worker.execute();
		}

	}

	public static class TableRenderer implements TableCellRenderer {

		private static final String strColor = "#EDF5F4";
		private static final Color strideColor = Color.decode(strColor);

		JLabel htmlLabel = new JLabel();
		JPanel noHtmlPanel = new JPanel();
		JLabel noHtmlLabel = new JLabel();
		JTextArea noHTMLTextArea = new JTextArea();
		Item toRender = null;
		boolean useHtml = false;

		public TableRenderer() {
			noHTMLTextArea.setWrapStyleWord(false);
			noHTMLTextArea.setLineWrap(true);
			noHTMLTextArea.setOpaque(false);

			Font defaultFont = noHtmlLabel.getFont();
			Font boldFont = defaultFont.deriveFont(Font.BOLD);
			noHtmlLabel.setFont(boldFont);
			noHtmlLabel.setOpaque(false);

			noHtmlPanel.setLayout(new BorderLayout());
			noHtmlPanel.add(noHtmlLabel, BorderLayout.NORTH);
			noHtmlPanel.add(noHTMLTextArea, BorderLayout.SOUTH);
		}

		public void setUseHtml(boolean useHtml) {
			this.useHtml = useHtml;
		}

		public Component getJlabelRenderer(JTable table, Item value, int row) {

			String colorString = "";
			if (row % 2 == 0) {
				colorString = "background-color:" + strColor + ";";
			}
			if (toRender != value) {
				toRender = value;
				htmlLabel.setText("<html><div style='padding:2px;" + "width:"
						+ table.getWidth() + ";" + colorString
						+ "color:black;'>"
						+ "<div style='padding:2px;font-weight:500;'>"
						+ "Item " + value.id + "</div>" + value.msg
						+ "</div></html>");
			}

			return htmlLabel;
		}

		public Component getNoHtmlRenderer(JTable table, Item value, int row) {
			if (toRender != value) {
				toRender = value;
				noHtmlLabel.setText("Item " + value.id);
				noHTMLTextArea.setText(value.msg);

				if (row % 2 == 0) {
					noHtmlPanel.setBackground(strideColor);
					noHtmlPanel.setOpaque(true);
				} else {
					noHtmlPanel.setOpaque(false);
				}
			}

			return noHtmlPanel;
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (useHtml) {
				return getJlabelRenderer(table, (Item) value, row);
			} else {
				return getNoHtmlRenderer(table, (Item) value, row);
			}
		}

	}

	public JTableHtmlTest() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel controlPanel = new JPanel();
		JButton updaterControl = new JButton("Update 3000");
		final JCheckBox useHtmlControl = new JCheckBox("Use HTML");
		final TableModel model = new TableModel();
		final JTable table = new JTable(model);
		final TableRenderer renderer = new TableRenderer();
		JScrollPane scrollPane = new JScrollPane(table);
		final JLabel durationIndicator = new JLabel("0");

		controlPanel.add(useHtmlControl, BorderLayout.WEST);
		controlPanel.add(updaterControl, BorderLayout.EAST);

		getContentPane().add(controlPanel, BorderLayout.PAGE_START);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(durationIndicator, BorderLayout.PAGE_END);

		table.setDefaultRenderer(Object.class, renderer);
		
		// Only update the JTable row heights when they are in view
		final JViewport viewport = scrollPane.getViewport();
		viewport.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Rectangle viewRect = viewport.getViewRect();
				int first = table.rowAtPoint(new Point(0, viewRect.y));
				if (first == -1) {
					return;
				}
				int last = table.rowAtPoint(new Point(0, viewRect.y
						+ viewRect.height - 1));
				if (last == -1) {
					last = model.getRowCount() - 1;
				}

				int column = 0;
				for (int row = first; row <= last; row++) {
					Component comp = table.prepareRenderer(
								table.getCellRenderer(row, column),
								row, column);
					int rowHeight = comp.getPreferredSize().height;
					table.setRowHeight(row, rowHeight);
				}
			}
		});

		updaterControl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderer.setUseHtml(useHtmlControl.isSelected());
				model.updateItems();
			}
		});

		Timer counter = new Timer();
		counter.schedule(new TimerTask() {
			@Override
			public void run() {
				String previousCounter = durationIndicator.getText();
				final String newCounter = Integer.toString(Integer
						.parseInt(previousCounter) + 1);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						durationIndicator.setText(newCounter);
						setTitle(newCounter);
					}
				});
			}
		}, 0, 100);
	}

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JTableHtmlTest jlt = new JTableHtmlTest();
					jlt.pack();
					jlt.setSize(300, 300);
					jlt.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
}
