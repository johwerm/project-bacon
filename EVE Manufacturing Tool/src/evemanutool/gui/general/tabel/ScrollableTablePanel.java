package evemanutool.gui.general.tabel;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import evemanutool.constants.DBConstants;

@SuppressWarnings("serial")
public class ScrollableTablePanel<T> extends JPanel implements DBConstants, SwingConstants{
		
	//Table components.
	private final AdjustingTable table;
	private final SimpleTableModel<T> model;
	private final RowSorter<TableModel> sorter;
	
	public ScrollableTablePanel(SimpleTableModel<T> model) {
	
		//Set layout.
		setLayout(new BorderLayout());
		
		//Setup table.
		this.model = model;
		table = new AdjustingTable(model);
		sorter = new TableRowSorter<TableModel>(model);
		table.setRowSorter(sorter);
		table.getTableHeader().setReorderingAllowed(false);
		
		//Renderer.
		table.setDefaultRenderer(Long.class, new LongNumberCellRenderer(""));
		table.setDefaultRenderer(Double.class, new DecimalNumberCellRenderer(""));
		table.setDefaultRenderer(Integer.class, new IntegerNumberCellRenderer(""));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getTableHeader().setDefaultRenderer(new HeaderRenderer(table, model.getColumnAlign(), model.getEditableColumns()));
		
		//Fixes background color of boolean cells.
		((JComponent) table.getDefaultRenderer(Boolean.class)).setOpaque(true);
		
		//ScrollPane.
		JScrollPane pane = new JScrollPane(table);
		add(pane, BorderLayout.CENTER);
	}
	
	public void setData(Collection<T> l) {
		model.setData(l);
	}
	
	public JTable getTable() {
		return table;
	}
	
	public SimpleTableModel<T> getModel() {
		return model;
	}
	
	public RowSorter<TableModel> getSorter() {
		return sorter;
	}
}
