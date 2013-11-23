package evemanutool.gui.manu.components;

import javax.swing.SwingConstants;

import evemanutool.data.database.Material;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class MineralModel extends SimpleTableModel<Material> implements SwingConstants {

	public MineralModel() {
		super(	new String[] {"Name", "Amount", "Price/unit (ISK)",	"Total Price (ISK)"}, 
				new int[] {LEFT, RIGHT, RIGHT, RIGHT});
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		switch (columnIndex) {

		case 0:
			return String.class;

		case 1: case 2: case 3:
			return Double.class;

		default:
			return Object.class;
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
			return m.getPrice();

		case 3:
			return m.getPrice() * m.getAmount();

		default:
			return null;
		}
	}
}