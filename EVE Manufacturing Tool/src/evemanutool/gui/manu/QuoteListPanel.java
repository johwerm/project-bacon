package evemanutool.gui.manu;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import evemanutool.constants.DBConstants.QuoteType;
import evemanutool.constants.DBConstants.Trend;
import evemanutool.data.database.ManuQuote;
import evemanutool.gui.general.tabel.ScrollableTablePanel;
import evemanutool.gui.general.tabel.TrendEnumCellRenderer;
import evemanutool.gui.manu.components.ManuQuoteModel;
import evemanutool.gui.manu.filter.QuoteFilterPanel;
import evemanutool.gui.manu.frameworks.InspectPanel;
import evemanutool.utils.databases.MarketGroupDB;
import evemanutool.utils.databases.QuoteDB;
import evemanutool.utils.datahandling.GUIUpdater;

@SuppressWarnings("serial")
public class QuoteListPanel extends JPanel implements GUIUpdater, SwingConstants{
	
	//Constants.
	private final QuoteType qT;
	
	//DB:s.
	private QuoteDB qdb;
	
	//Complete list.
	private ArrayList<ManuQuote> quoteList;
	
	//Additional Quote panels.
	private InspectPanel bI;
	private QuoteFilterPanel fP;
	
	//Table components.
	private ScrollableTablePanel<ManuQuote> quotePanel;

	public QuoteListPanel(QuoteType qT, QuoteDB qdb, MarketGroupDB gdb, InspectPanel bI) {
		
		this.qdb = qdb;
		this.qT = qT;
		
		setLayout(new GridLayout(1, 2));
		
		JPanel listPanel = new JPanel(new BorderLayout());
		
		quotePanel = new ScrollableTablePanel<>(new ManuQuoteModel());
		quotePanel.getTable().setDefaultRenderer(Trend.class, new TrendEnumCellRenderer(quotePanel.getTable().getRowHeight()));
		quotePanel.getTable().getSelectionModel().addListSelectionListener(new SelectionListener());
		
		//Setup internal panels.
		this.bI = bI;
		fP = new QuoteFilterPanel(gdb);
		fP.addPropertyChangeListener(QuoteFilterPanel.FILTER_UPDATE, new FilterListener());
		
		//Add to containers.
		listPanel.add(fP, BorderLayout.NORTH);
		listPanel.add(quotePanel, BorderLayout.CENTER);
		
		add(listPanel);
		add(bI);
	}
	
	/*
	 * Tries to select the given quote.
	 */
	public boolean selectQuote(ManuQuote quote) {
		
		//If in the datalist, find the index of the quote.
		int index;
		index = quoteList.indexOf(quote);
		
		if (index > 0) {
			//Reset the filter and show all quotes.
			fP.reset();
			setQuotes(quoteList);
			//Convert index from view to model.
			index = quotePanel.getSorter().convertRowIndexToView(index);
			//Select the quote.
			quotePanel.getTable().setRowSelectionInterval(index, index);
			//Scroll to row.
			quotePanel.getTable().scrollRectToVisible(
					new Rectangle(quotePanel.getTable().getCellRect(index, 0, true)));
			
			//Operation successful, return true.
			return true;
		}
		//Operation failed, return false.
		return false;
	}
	
	@Override
	public void updateGUI(){
		fP.updateGUI();
		setQuotes(qdb.getManuQuotes(qT));
	}
	
	private void setQuotes(Collection<ManuQuote> l) {
		quoteList = new ArrayList<>(l);
		quotePanel.setData(fP.filter(l));
	}
	
	private class FilterListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			quotePanel.setData(fP.filter(quoteList));
		}
	}
	
	private class SelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			//Sets the selected quote from the showed list (Not the complete).
			if (!e.getValueIsAdjusting() && 
					quotePanel.getTable().getSelectedRow() >= 0 && 
					quotePanel.getTable().getSelectedRow() < quotePanel.getModel().size()) {
				bI.setBpo(quotePanel.getModel().getDataAt(quotePanel.getSorter().convertRowIndexToModel(quotePanel.getTable().getSelectedRow())).getBpo());
			}
		}
	}
}
