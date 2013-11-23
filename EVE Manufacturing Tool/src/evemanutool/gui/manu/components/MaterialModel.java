package evemanutool.gui.manu.components;

import javax.swing.SwingConstants;

import evemanutool.data.database.Material;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class MaterialModel extends SimpleTableModel<Material> implements SwingConstants {
	
	public MaterialModel() {
		super(	new String[] {"Name", "Amount", "Manufacture", "Manufacture Cost/unit (ISK)", "Price/unit (ISK)", "Total Cost (ISK)"}, 
				new int[] {LEFT, RIGHT, CENTER, RIGHT, RIGHT, RIGHT},
				new int[] {2});
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return String.class;
				
			case 2:
				return Boolean.class;
	
			case 1: case 3: case 4: case 5:
				return Double.class;
				
			default:
				return Object.class;
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return getColumnClass(columnIndex) == Boolean.class && 
				dataList.get(rowIndex).canBeManufactured();
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		//Set value to the underlying data.
		if (getColumnClass(columnIndex) == Boolean.class) {
			dataList.get(rowIndex).setProduced((Boolean) aValue);
			//Update the cell.
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		Material m = dataList.get(row);
		
		switch (col) {
		case 0:
			return m.getItem().getName();

		case 1:
			return m.getAmount();
			
		case 2:
			return m.canBeManufactured() ? m.isProduced() : null; //Checkbox!
			
		case 3:
			return m.getManufactureQuote() == null ? null : m.getManufactureQuote().getManuCost();
			
		case 4:
			return m.getPrice();
			
		case 5:
			return m.isProduced() ? m.getManufactureQuote().getManuCost() * m.getAmount() : 
									m.getPrice() * m.getAmount();
			
		default:
			return null;
		}
	}
}
