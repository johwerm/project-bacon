package evemanutool.gui.manu;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import evemanutool.constants.DBConstants.QuoteType;
import evemanutool.data.database.Material;
import evemanutool.data.display.MiningQuote;
import evemanutool.gui.general.tabel.ScrollableTablePanel;
import evemanutool.gui.manu.components.MineralModel;
import evemanutool.gui.manu.components.MiningQuoteModel;
import evemanutool.utils.databases.QuoteDB;
import evemanutool.utils.datahandling.GUIUpdater;

@SuppressWarnings("serial")
public class MiningPanel extends JPanel implements GUIUpdater, SwingConstants {

	//Constants.
	private QuoteType qT;
	
	//DBs.
	private QuoteDB qdb;

	//GUI components.
	private ScrollableTablePanel<MiningQuote> miningPanel;
	private ScrollableTablePanel<Material> mineralPanel;


	public MiningPanel(QuoteType qT, QuoteDB qdb, String label) {

		this.qdb = qdb;
		this.qT = qT;
		
		// Set layout.
		setLayout(new GridLayout(2, 1));
		
		miningPanel = new ScrollableTablePanel<>(new MiningQuoteModel());
		miningPanel.getTable().getSelectionModel().addListSelectionListener(new SelectionListener());
		
		mineralPanel = new ScrollableTablePanel<>(new MineralModel());
		
		// Add components to panel.
		add(miningPanel);
		add(mineralPanel);
	}
	
	@Override
	public void updateGUI(){
		miningPanel.getModel().setData(qdb.getMiningQuotes(qT));
	}
	
	private class SelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			//Sets the selected quote from the showed list (Not the complete).
			if (!e.getValueIsAdjusting() && 
					miningPanel.getTable().getSelectedRow() >= 0 && 
					miningPanel.getTable().getSelectedRow() < miningPanel.getModel().size()) {
				mineralPanel.setData(miningPanel.getModel().getDataAt(
						miningPanel.getSorter().convertRowIndexToModel(
								miningPanel.getTable().getSelectedRow())).getOre().getBaseMaterials());
			}
		}
	}
}
