package evemanutool.gui.corp.components;

import java.util.Date;

import javax.swing.SwingConstants;

import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;

import evemanutool.data.display.MarketOrder;
import evemanutool.data.general.Time;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class MarketOrderModel extends SimpleTableModel<MarketOrder> implements SwingConstants {
	
	public MarketOrderModel() {
		super(	new String[] {"Type", "Quantity", "Price (ISK)", "Station", "Region", "Expires in", "Issued by", "Wallet Division"},
				new int[] {LEFT, RIGHT, RIGHT, LEFT, LEFT, LEFT, LEFT, LEFT});
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0: case 1:
				return String.class;
	
			case 2:
				return Double.class;
				
			case 3: case 4:
				return String.class;
				
			case 5:
				return Time.class;
				
			case 6: case 7:
				return String.class;
				
			default:
				return Object.class;
		}
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		MarketOrder m = dataList.get(row);
		ApiMarketOrder mm = m.getMarketOrder();
		
		switch (col) {
		case 0:
			return m.getItem().getName();

		case 1:
			return mm.getVolRemaining() + "/" + mm.getVolEntered();
			
		case 2:
			return mm.getPrice();
			
		case 3:
			return m.getStation().getName();
			
		case 4:
			return m.getRegion().getName();
			
		case 5:
			return new Time((long) (mm.getIssued().getTime() 
					+ (((long) mm.getDuration()) * 24 * 3600 * 1000) - new Date().getTime())); //Duration is in days.
			
		case 6:
			return m.getIssuer().getName();
			
		case 7:
			return m.getWalletDivision();
			
		default:
			return null;
		}
	}
}
