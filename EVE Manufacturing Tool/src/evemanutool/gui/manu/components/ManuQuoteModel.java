package evemanutool.gui.manu.components;

import javax.swing.SwingConstants;

import evemanutool.constants.DBConstants;
import evemanutool.data.database.ManuQuote;
import evemanutool.data.general.Time;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class ManuQuoteModel extends SimpleTableModel<ManuQuote> implements DBConstants, SwingConstants {
	
	public ManuQuoteModel() {
		super(	new String[] {"Name", "Production Time", "Sales/volume", "Sustainable Profit Value", "Trend", "Profit/hour (ISK)"},
				new int[] {LEFT, LEFT, RIGHT, RIGHT, CENTER, RIGHT});
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return String.class;
	
			case 1:
				return Time.class;
				
			case 2: case 3: case 5:
				return Double.class;
			
			case 4:
				return Trend.class;
				
			default:
				return Object.class;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		ManuQuote q = dataList.get(row);
		
		switch (col) {
		case 0:
			return q.getBpo().getBlueprintItem().getName();

		case 1:
			return q.getManuTime();
			
		case 2:
			return q.getSalesVolumeRatio();
			
		case 3:
			return q.getSustainableProfitValue();
			
		case 4:
			return q.getProductTrend();
			
		case 5:
			return q.getProfitPerHour();
			
		default:
			return null;
		}
	}
}
