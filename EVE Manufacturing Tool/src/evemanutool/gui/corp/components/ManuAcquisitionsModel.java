package evemanutool.gui.corp.components;

import javax.swing.SwingConstants;

import evemanutool.data.display.ManuAcquisition;
import evemanutool.data.general.Time;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class ManuAcquisitionsModel extends SimpleTableModel<ManuAcquisition> implements SwingConstants {
	
	public ManuAcquisitionsModel() {
		super(	new String[] {"Name", "Quantity", "In Production", "Material Coverage (%)", "Total Time"},
				new int[] {LEFT, RIGHT, RIGHT, RIGHT, RIGHT});
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return String.class;

			case 1:
				return Long.class;
	
			case 2: 
				return Double.class;

			case 3:
				return Integer.class;
				
			case 4:
				return Time.class;
				
			default:
				return Object.class;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		ManuAcquisition m = dataList.get(row);
		
		switch (col) {
		case 0:
			return m.getQuote().getBpo().getProduct().getName();

		case 1:
			return m.getAmount();
			
		case 2:
			return m.getInProduction();
			
		case 3:
			return (int) (m.getMaterialCoverage() * 100); //Round of and left shift to format to percentage.
			
		case 4:
			return m.getTotalSlotTime();
			
		default:
			return null;
		}
	}
}
