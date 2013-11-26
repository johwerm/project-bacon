package evemanutool.gui.corp.components;

import javax.swing.SwingConstants;

import evemanutool.data.display.Fuel;
import evemanutool.data.general.Time;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class POSFuelModel extends SimpleTableModel<Fuel> implements SwingConstants {
	
	public POSFuelModel() {
		super(	new String[] {"Type", "Time Left", "Amount"},
				new int[] {LEFT, LEFT, RIGHT});
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return String.class;
				
			case 1:
				return Time.class;
				
			case 2:
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
			return new Time((long) (f.getAmount() / f.getReqAmount()) * 3600 * 1000);

		case 2:
			return f.getAmount();
			
		default:
			return null;
		}
	}
}
