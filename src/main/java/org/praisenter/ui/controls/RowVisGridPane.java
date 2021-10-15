package org.praisenter.ui.controls;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * As simple extension to the GridPane class to assist with hide/show logic by row.
 * @author William Bittle
 *
 */
public class RowVisGridPane extends GridPane {
	
	private final Map<Integer, RowDefinition> rows = new HashMap<>();
	
	@Override
	public void add(Node child, int columnIndex, int rowIndex) {
		super.add(child, columnIndex, rowIndex);
		
		RowDefinition row = this.rows.get(rowIndex);
		if (row == null) {
			row = new RowDefinition();
			this.rows.put(rowIndex, row);
		}
		
		ColumnDefinition col = new ColumnDefinition(columnIndex, child);
		row.columns.put(columnIndex, col);
	}
	
	public void add(Node child, int columnIndex, int rowIndex, int colspan) {
		super.add(child, columnIndex, rowIndex, colspan, 1);
		
		RowDefinition row = this.rows.get(rowIndex);
		if (row == null) {
			row = new RowDefinition();
			this.rows.put(rowIndex, row);
		}
		
		ColumnDefinition col = new ColumnDefinition(columnIndex, child, colspan);
		row.columns.put(columnIndex, col);
	}
	
	@Override
	public void add(Node child, int columnIndex, int rowIndex, int colspan, int rowspan) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addColumn(int columnIndex, Node... children) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addRow(int rowIndex, Node... children) {
		int column = 0;
		for (Node child : children) {
			this.add(child, column++, rowIndex);
		}
	}
	
	public void hideRow(int rowIndex) {
		RowDefinition row = this.rows.get(rowIndex);
		if (row != null && row.visible) {
			row.visible = false;
			reLayout();
		}
	}
	
	public void hideRows(int... rowIndices) {
		boolean layout = false;
		for (int rowIndex : rowIndices) {
			RowDefinition row = this.rows.get(rowIndex);
			if (row != null && row.visible) {
				row.visible = false;
				layout = true;
			}
			
		}
		if (layout) reLayout();
	}
	
	public void showRow(int rowIndex) {
		RowDefinition row = this.rows.get(rowIndex);
		if (row != null && !row.visible) {
			row.visible = true;
			reLayout();
		}
	}
	
	public void showRows(int... rowIndices) {
		boolean layout = false;
		for (int rowIndex : rowIndices) {
			RowDefinition row = this.rows.get(rowIndex);
			if (row != null && !row.visible) {
				row.visible = true;
				layout = true;
			}
			
		}
		if (layout) reLayout();
	}
	
	public void showRowsOnly(int... rowIndices) {
		for (Integer index : this.rows.keySet()) {
			RowDefinition def = this.rows.get(index);
			def.visible = false;
			for (Integer test : rowIndices) {
				if (test == index) {
					def.visible = true;
					break;
				}
			}
		}
		this.reLayout();
	}
	
	public void showAllRows() {
		boolean layout = false;
		for (RowDefinition row : this.rows.values()) {
			if (row != null && !row.visible) {
				row.visible = true;
				layout = true;
			}
			
		}
		if (layout) reLayout();
	}
	
	protected int reLayout() {
		int i = 0;
		
		// add or remove nodes
		for (RowDefinition row : this.rows.values()) {
			if (!row.visible) {
				// remove all the column nodes
				for (ColumnDefinition col : row.columns.values()) {
					this.getChildren().remove(col.node);
				}
			}
			if (row.visible) {
				// add any column nodes that aren't already on the scene
				for (ColumnDefinition col : row.columns.values()) {
					if (!this.getChildren().contains(col.node)) {
						this.getChildren().add(col.node);
					}
				}
			}
		}
		
		// reset the column constraints
		for (RowDefinition row : this.rows.values()) {
			if (row.visible) {
				for (ColumnDefinition col : row.columns.values()) {
					GridPane.setConstraints(col.node, col.column, i, col.colspan, 1);
				}
				i++;
			}
		}
		
		return i;
	}
	
	private class ColumnDefinition {
		final int column;
		final int colspan;
		final Node node;
		public ColumnDefinition(int column, Node node) {
			this(column, node, 1);
		}
		public ColumnDefinition(int column, Node node, int colspan) {
			this.column = column;
			this.node = node;
			this.colspan = colspan;
		}
	}
	
	private class RowDefinition {
		final Map<Integer, ColumnDefinition> columns;
		boolean visible;
		public RowDefinition() {
			this.visible = true;
			this.columns = new HashMap<>();
		}
	}
}
