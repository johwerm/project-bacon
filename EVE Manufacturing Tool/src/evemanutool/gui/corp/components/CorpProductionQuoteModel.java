package evemanutool.gui.corp.components;

import javax.swing.SwingConstants;

import evemanutool.data.display.CorpProductionQuote;
import evemanutool.gui.general.tabel.SimpleTableModel;
import evemanutool.utils.calc.ProductionCalculator;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.datahandling.GUIUpdater;

@SuppressWarnings("serial")
public class CorpProductionQuoteModel extends SimpleTableModel<CorpProductionQuote> implements SwingConstants {

	//Constants.
	private static final int PRODUCTION_AIM_INDEX = 3;
	private static final int ACTIVE_INDEX = 8;
	
	//DB:s.
	private CorpApiDB cdb;
	
	//GUI Components.
	private GUIUpdater supplyPanel;
	
	public CorpProductionQuoteModel(CorpApiDB cdb, GUIUpdater supplyPanel) {
		super(	new String[] {"Name", "Market Volume/week", "Corp Sold/week", "On Sale Aim", "Left To Manufacture", "In Production", "Left To Sell", "On Sale", "Activated"},
				new int[] {LEFT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, RIGHT, CENTER},
				new int[] {PRODUCTION_AIM_INDEX, ACTIVE_INDEX});
		this.cdb = cdb;
		this.supplyPanel = supplyPanel;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return String.class;
	
			case 1: case 2: case 3: case 4: case 5: case 6: case 7:
				return Integer.class;
				
			case 8:
				return Boolean.class;
				
			default:
				return Object.class;
		}
	}
	
	@Override
	public Object getValueAt(int row, int col) {
		CorpProductionQuote q = dataList.get(row);
		
		switch (col) {
		case 0:
			return q.getQuote().getBpo().getBlueprintItem().getName();

		case 1:
			return q.getAvgWeekTradedAmount();
			
		case 2:
			return q.getAvgWeekCorpSoldAmount();
			
		case 3:
			return q.getSellTarget();
			
		case 4:
			return q.getNeedToProduce();
			
		case 5:
			return q.getInProduction();
			
		case 6:
			return q.getStock();

		case 7:
			return q.getOnSale();
			
		case 8:
			return q.isActive();
			
		default:
			return null;
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		//Set the value of aim and active.
		CorpProductionQuote q = dataList.get(rowIndex);
		if (aValue != null) {
			if (columnIndex == PRODUCTION_AIM_INDEX) {
				q.setSellTarget((int) aValue);
				ProductionCalculator.updateProductionQuote(q);
			}else if (columnIndex == ACTIVE_INDEX) {
				q.setActive((boolean) aValue);
			}
			//Update the supply data.
			cdb.updateSupplyData();
			supplyPanel.updateGUI();
			
			//Update the cell.
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}
}

