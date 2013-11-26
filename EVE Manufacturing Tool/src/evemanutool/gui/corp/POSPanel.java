package evemanutool.gui.corp;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import evemanutool.data.display.POS;
import evemanutool.gui.corp.components.POSDetailsPanel;
import evemanutool.gui.corp.components.POSModel;
import evemanutool.gui.general.tabel.ScrollableTablePanel;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.datahandling.GUIUpdater;

@SuppressWarnings("serial")
public class POSPanel extends JPanel implements GUIUpdater {
	
	//DB:s
	private CorpApiDB cdb;

	//Internal panels.
	private ScrollableTablePanel<POS> posPanel;
	private POSDetailsPanel posDetailsPanel;

	public POSPanel(CorpApiDB cdb) {
		
		//Set DB refs.
		this.cdb = cdb;

		//Main layout.
		setLayout(new GridLayout(1, 2));
		
		posPanel = new ScrollableTablePanel<>(new POSModel());
		posPanel.setBorder(BorderFactory.createTitledBorder("Starbases"));
		posPanel.getTable().getSelectionModel().addListSelectionListener(new SelectionListener());
		
		//Create details panel.
		posDetailsPanel = new POSDetailsPanel();
		posDetailsPanel.setBorder(BorderFactory.createTitledBorder("Details"));
		
		//Add top level components.
		add(posPanel);
		add(posDetailsPanel);
	}
	
	@Override
	public void updateGUI(){
		//POS list.
		posPanel.setData(cdb.getPosList());
	}
	
	private class SelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			//Sets the selected quote from the showed list (Not the complete).
			if (!e.getValueIsAdjusting() && 
					posPanel.getTable().getSelectedRow() >= 0 && 
					posPanel.getTable().getSelectedRow() < posPanel.getModel().size()) {

				//Get the selected quote.
				POS selectedPos = posPanel.getModel().getDataAt(posPanel.getSorter().convertRowIndexToModel(posPanel.getTable().getSelectedRow()));
				//Show quick info.
				posDetailsPanel.setPOS(selectedPos);
			}
		}
	}
}
