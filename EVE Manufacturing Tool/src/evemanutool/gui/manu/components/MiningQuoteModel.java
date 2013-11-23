package evemanutool.gui.manu.components;

import javax.swing.SwingConstants;

import evemanutool.data.display.MiningQuote;
import evemanutool.gui.general.tabel.SimpleTableModel;

@SuppressWarnings("serial")
public class MiningQuoteModel extends SimpleTableModel<MiningQuote> implements SwingConstants {

	public MiningQuoteModel() {
		super(	new String[] {"Name","Volume (M3)", "Refine Size (Units)", "Income/M3 (ISK)", "Income/Hour (ISK)"},
				new int[] {LEFT, RIGHT, RIGHT, RIGHT, RIGHT});
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		switch (columnIndex) {

		case 0:
			return String.class;
			
		case 1: case 2: case 3: case 4:
			return Double.class;

		default:
			return Object.class;
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		MiningQuote b = dataList.get(row);

		switch (col) {
		case 0:
			return b.getOre().getName();

		case 1:
			return b.getOre().getVolume();

		case 2:
			return b.getOre().getPortionSize();

		case 3:
			return b.getIncomePerM3();

		case 4:
			return b.getIncomePerH();

		default:
			return null;
		}
	}
}
