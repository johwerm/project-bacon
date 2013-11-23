package evemanutool.gui.corp.components;

import javax.swing.SwingConstants;

import evemanutool.data.display.CorpMember;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class CorpMemberModel extends SimpleTableModel<CorpMember> implements SwingConstants {
	
	public CorpMemberModel() {
		super(	new String[] {"Name", "Location", "Ship", "Total Tax (ISK)", "Avg. Week Tax (ISK)", "Isk Access (ISK)"},
				new int[] {LEFT, LEFT, LEFT, RIGHT, RIGHT, RIGHT});
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0: case 1: case 2:
				return String.class;
	
			case 3: case 4: case 5:
				return Double.class;
				
			default:
				return Object.class;
		}
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		CorpMember m = dataList.get(row);
		
		switch (col) {
		case 0:
			return m.getTracking().getName();

		case 1:
			return m.getTracking().getLocation();
			
		case 2:
			return m.getTracking().getShipType();
			
		case 3:
			return m.getTotalTax();
			
		case 4:
			return m.getAvgWeekTax();
			
		case 5:
			return m.getIskAccess();
			
		default:
			return null;
		}
	}
}
