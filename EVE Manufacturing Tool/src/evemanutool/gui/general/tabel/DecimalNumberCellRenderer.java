package evemanutool.gui.general.tabel;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class DecimalNumberCellRenderer extends DefaultTableCellRenderer {
	private DecimalFormat formatter;
	private String suffix;
	public DecimalNumberCellRenderer(String suffix) {
		this.suffix = suffix;
		
		setHorizontalAlignment(RIGHT);
	}

	@Override
	public void setValue(Object value) {
		if (formatter == null) {
			formatter = new DecimalFormat("#,###,###,##0.00");
		}			
		setText((value == null) ? "" : formatter.format(value) + suffix);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
				row, column);
		
		//Set font color.
		if (value != null && ((value instanceof Double && (Double) value < 0))) {
			c.setForeground(Color.RED);
		}else {
			c.setForeground(Color.BLACK);
		}
		return c;
	}
}
