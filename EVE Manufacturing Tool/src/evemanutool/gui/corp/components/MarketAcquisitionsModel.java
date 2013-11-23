package evemanutool.gui.corp.components;

import javax.swing.SwingConstants;

import evemanutool.data.display.MarketAcquisition;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class MarketAcquisitionsModel extends SimpleTableModel<MarketAcquisition> implements SwingConstants {
	
	public MarketAcquisitionsModel() {
		super(	new String[] {"Name", "Quantity", "Price (ISK)", "Total Price (ISK)", "Total Volume (M3)"},
				new int[] {LEFT, RIGHT, RIGHT, RIGHT, RIGHT});
	}
		
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return String.class;

			case 1:
				return Long.class;
	
			case 2: case 3: case 4:
				return Double.class;
				
			default:
				return Object.class;
		}
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		MarketAcquisition m = dataList.get(row);
		
		switch (col) {
		case 0:
			return m.getItem().getName();

		case 1:
			return m.getAmount();
			
		case 2:
			return m.getPrice();
			
		case 3:
			return m.getTotalCost();
			
		case 4:
			return m.getTotalVolume();
			
		default:
			return null;
		}
	}
}
