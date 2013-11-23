package evemanutool.gui.general.tabel;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class BooleanCellRenderer extends DefaultTableCellRenderer {
	
	public BooleanCellRenderer() {
		setHorizontalAlignment(CENTER);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (table.getValueAt(row, column) == null) {

			JLabel comp = (JLabel) super.getTableCellRendererComponent(table,
					value, isSelected, hasFocus, row, column);
			comp.setText(null);
			return comp;
		}
		return table.getDefaultRenderer(table.getColumnClass(column))
				.getTableCellRendererComponent(table, value, isSelected,
						hasFocus, row, column);
	}
}
