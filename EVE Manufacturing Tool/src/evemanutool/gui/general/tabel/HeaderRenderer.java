package evemanutool.gui.general.tabel;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import evemanutool.constants.GUIConstants;

public class HeaderRenderer implements TableCellRenderer, GUIConstants {

    DefaultTableCellRenderer renderer;
    int[] aligns;
    int[] editable;

    public HeaderRenderer(JTable table, int[] aligns) {
    	this.aligns = aligns;
    	this.editable = new int[0];
        renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
    }

    public HeaderRenderer(JTable table, int[] aligns, int[] editable) {
    	this.aligns = aligns;
    	this.editable = editable;
    	renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
    }

    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
    	renderer.setHorizontalAlignment(aligns[col]);
    	
    	Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
    	//Color if editable.
    	for (int i : editable) {
			if (i == col) {
				c.setForeground(EDITABLE_TABLE_HEAD);
			}
		}
        return c;
    }
}
