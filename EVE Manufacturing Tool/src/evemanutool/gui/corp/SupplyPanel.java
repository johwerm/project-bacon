package evemanutool.gui.corp;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import evemanutool.constants.DBConstants;
import evemanutool.data.display.ManuAcquisition;
import evemanutool.data.display.MarketAcquisition;
import evemanutool.data.display.Supply;
import evemanutool.gui.corp.components.ManuAcquisitionsModel;
import evemanutool.gui.corp.components.MarketAcquisitionsModel;
import evemanutool.gui.corp.components.SupplyModel;
import evemanutool.gui.general.tabel.ScrollableTablePanel;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.datahandling.GUIUpdater;

@SuppressWarnings("serial")
public class SupplyPanel extends JPanel implements GUIUpdater, SwingConstants, DBConstants{
	
	//DB:s.
	private CorpApiDB cdb;
	
	//GUI Components.
	private ScrollableTablePanel<Supply> supplyPanel;
	private ScrollableTablePanel<MarketAcquisition>  marketPanel;
	private ScrollableTablePanel<ManuAcquisition> manuPanel;

	public SupplyPanel(CorpApiDB cdb) {
		
		this.cdb = cdb;

		//Set layout
		setLayout(new GridLayout(1, 2));
		
		//Setup table.
		supplyPanel = new ScrollableTablePanel<>(new SupplyModel());
		supplyPanel.setBorder(BorderFactory.createTitledBorder("Supply List"));
		
		//Setup internal panels.
		JPanel aquirePanel = new JPanel(new GridLayout(2, 1));

		marketPanel = new ScrollableTablePanel<>(new MarketAcquisitionsModel());
		marketPanel.setBorder(BorderFactory.createTitledBorder("To Buy"));
		manuPanel = new ScrollableTablePanel<>(new ManuAcquisitionsModel());
		manuPanel.setBorder(BorderFactory.createTitledBorder("To Manufacture"));
		
		aquirePanel.add(marketPanel);
		aquirePanel.add(manuPanel);
		
		add(supplyPanel);
		add(aquirePanel);
	}
	
	@Override
	public void updateGUI() {
		//CorpAPIDB has new data, update and reload.
		supplyPanel.setData(cdb.getSupplies());
		marketPanel.setData(cdb.getMarketAcquisitions());
		manuPanel.setData(cdb.getManuAcquisitions());
	}
}
