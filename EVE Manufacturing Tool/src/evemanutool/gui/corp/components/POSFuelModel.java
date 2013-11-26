package evemanutool.gui.corp.components;

import javax.swing.SwingConstants;

import evemanutool.data.display.Fuel;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class POSFuelModel extends SimpleTableModel<Fuel> implements SwingConstants {
	
	public POSFuelModel() {
		super(	new String[] {"Type", "Amount"},
				new int[] {LEFT, RIGHT});
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return String.class;
				
			case 1:
				return Integer.class;
				
			default:
				return Object.class;
		}
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		Fuel f = dataList.get(row);
		
		switch (col) {
		case 0:
			return f.getItem().getName();

		case 1:
			return f.getAmount();
			
		default:
			return null;
		}
	}
}
