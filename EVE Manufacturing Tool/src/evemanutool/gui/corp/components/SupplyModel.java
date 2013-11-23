package evemanutool.gui.corp.components;

import javax.swing.SwingConstants;

import evemanutool.data.display.Supply;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class SupplyModel extends SimpleTableModel<Supply> implements SwingConstants{
	
	public SupplyModel() {
		super(	new String[] {"Name", "Stock", "Additional Required", "In Production", "In Buy Orders"},
				new int[] {LEFT, LEFT, RIGHT, RIGHT, RIGHT, RIGHT});
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return String.class;
	
			case 1: case 2:
				return Long.class;
				
			 case 3: case 4:
				return Integer.class;
				
			default:
				return Object.class;
		}
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		Supply s = dataList.get(row);
		
		switch (col) {
		case 0:
			return s.getItem().getName();

		case 1:
			return s.getStock();
			
		case 2:
			return (long) s.getNeeded();
			
		case 3:
			return s.getInProduction();
			
		case 4:
			return s.getOnBuyOrder();
			
		default:
			return null;
		}
	}
}
