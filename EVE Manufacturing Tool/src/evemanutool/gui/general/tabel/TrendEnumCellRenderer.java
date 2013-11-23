package evemanutool.gui.general.tabel;

import java.awt.Component;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import evemanutool.constants.DBConstants.Trend;
import evemanutool.constants.GUIConstants;

@SuppressWarnings("serial")
public class TrendEnumCellRenderer extends DefaultTableCellRenderer implements GUIConstants {
	
	private ArrayList<ImageIcon> scaledIcons = new ArrayList<>();
	
	public TrendEnumCellRenderer(int rowHeight) {
		setHorizontalAlignment(CENTER);
		ImageIcon tmp;
		for (String path : TREND_ICON_PATHS) {
			tmp = new ImageIcon(path);
			tmp.setImage(tmp.getImage().getScaledInstance(rowHeight, rowHeight, Image.SCALE_SMOOTH));
			scaledIcons.add(tmp);
		}
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		JLabel c = (JLabel) super.getTableCellRendererComponent(table,
				value, isSelected, hasFocus, row, column);
		c.setText("");
		
		//Icon.
		int index = Arrays.asList(TRENDS).indexOf((Trend) value);
		c.setToolTipText(TREND_ICON_TIPS[index]);
		c.setIcon(scaledIcons.get(index)); 
		return c;
	}
}
