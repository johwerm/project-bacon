package evemanutool.gui.general.tabel;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public abstract class SimpleTableModel<T> extends AbstractTableModel {

	//Constants.
	private final int[] columnAlign;
	private final int[] editableColumns;
	
	protected final ArrayList<T> dataList = new ArrayList<>();
	private final String[] columns;
	
	public SimpleTableModel(String[] columns, int[] columnAlign) {
		this.columns = columns;
		this.columnAlign = columnAlign;
		this.editableColumns = new int[0];
	}
	
	public SimpleTableModel(String[] columns, int[] columnAlign, int[] editableColumns) {
		this.columns = columns;
		this.columnAlign = columnAlign;
		this.editableColumns = editableColumns;
	}
	
	public void setData(Collection<T> l) {
		dataList.clear();
		dataList.addAll(l);
		fireTableDataChanged();
	}
	
	public T getDataAt(int row) {
		return dataList.get(row);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		for (int i : editableColumns) {
			if (i == columnIndex) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String getColumnName(int col) {
		return columns[col];
	}
	
	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public int getRowCount() {
		return dataList.size();
	}
	
	public int size() {
		return dataList.size();
	}

	public int[] getColumnAlign() {
		return columnAlign;
	}

	public int[] getEditableColumns() {
		return editableColumns;
	}
}
