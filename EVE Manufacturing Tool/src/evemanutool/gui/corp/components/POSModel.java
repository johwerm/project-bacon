package evemanutool.gui.corp.components;

import javax.swing.SwingConstants;

import evemanutool.data.display.POS;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class POSModel extends SimpleTableModel<POS> implements SwingConstants {
	
	public POSModel() {
		super(	new String[] {"Name", "Type", "Location", "State", "Fuel Left"},
				new int[] {LEFT, LEFT, LEFT, LEFT, LEFT});
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0: case 1: case 2: case 3: case 4:
				return String.class;
				
			default:
				return Object.class;
		}
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		POS p = dataList.get(row);
		
		switch (col) {
		case 0:
			return p.getApiLocation().getItemName();

		case 1:
			return p.getControlTower().getName();

		case 2:
			return p.getSystem().getName();
			
		case 3:
			return p.getState().name();
			
		case 4:
			return p.getFuelLeft().toString();
			
		default:
			return null;
		}
	}
}
