package evemanutool.gui.corp.components;

import javax.swing.SwingConstants;

import evemanutool.data.display.BlueprintAsset;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class BlueprintAssetModel extends SimpleTableModel<BlueprintAsset> implements SwingConstants{

	public BlueprintAssetModel() {
		super(	new String[] {"Name", "ME Level", "PE Level", "Location", "Activity", "State"},
				new int[] {LEFT, RIGHT, RIGHT, LEFT, LEFT, LEFT},
				new int[] {1, 2});
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return String.class;
	
			case 1: case 2:
				return Integer.class;
				
			case 3: case 4: case 5:
				return String.class;
				
			default:
				return Object.class;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		BlueprintAsset b = dataList.get(row);
		
		switch (col) {
		case 0:
			return b.getItem().getName();

		case 1:
			return b.getAssetEntry().getMe();
			
		case 2:
			return b.getAssetEntry().getPe();
			
		case 3:
			return b.getLocation() != null ? b.getLocation().getName() : "";
			
		case 4:
			return b.getActivity() != null ? b.getActivity().name : "";
			
		case 5:
			return b.getState() != null ? b.getState().name : "";
			
		default:
			return null;
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		//Set the appropriate value for ME or PE depending on the column, only if valid.
		if (getColumnClass(columnIndex) == Integer.class && aValue != null) {
			if (columnIndex == 1) {
				dataList.get(rowIndex).getAssetEntry().setMe((int) aValue);
			}else if (columnIndex == 2) {
				dataList.get(rowIndex).getAssetEntry().setPe((int) aValue);
			}
			//Update the cell.
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}
}
