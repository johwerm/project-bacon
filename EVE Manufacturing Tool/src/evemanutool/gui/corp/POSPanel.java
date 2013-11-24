package evemanutool.gui.corp;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import evemanutool.data.display.POS;
import evemanutool.gui.corp.components.POSModel;
import evemanutool.gui.general.tabel.ScrollableTablePanel;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.datahandling.GUIUpdater;

@SuppressWarnings("serial")
public class POSPanel extends JPanel implements GUIUpdater {
	
	//DB:s
	private CorpApiDB cdb;

	//Internal panels.
	private ScrollableTablePanel<POS> posList;

	public POSPanel(CorpApiDB cdb) {
		
		//Set DB refs.
		this.cdb = cdb;

		//Main layout.
		setLayout(new GridLayout(1, 2));
		
		posList = new ScrollableTablePanel<>(new POSModel());
		posList.setBorder(BorderFactory.createTitledBorder("Starbases"));
		
		//Create details panel.
		JPanel p2 = new JPanel(new GridLayout(2, 1));
		
		//Add top level components.
		add(posList);
		add(p2);
	}
	
	@Override
	public void updateGUI(){
		//POS list.
		posList.setData(cdb.getPosList());
	}
}
