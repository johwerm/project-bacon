package evemanutool.gui.corp;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import evemanutool.data.display.MarketOrder;
import evemanutool.gui.corp.components.MarketOrderModel;
import evemanutool.gui.general.tabel.ScrollableTablePanel;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.datahandling.GUIUpdater;

@SuppressWarnings("serial")
public class TradePanel extends JPanel implements GUIUpdater {
	
	//DB:s
	private CorpApiDB cdb;

	//Internal panels.
	private ScrollableTablePanel<MarketOrder> sellOrders;
	private ScrollableTablePanel<MarketOrder> buyOrders;

	public TradePanel(CorpApiDB cdb) {
		
		//Set DB refs.
		this.cdb = cdb;

		//Main layout.
		setLayout(new GridLayout(1, 2));
		
		//Create contract panels.
		JPanel p1 = new JPanel(new GridLayout(2, 1));
		
		
		//Create market panels.
		JPanel p2 = new JPanel(new GridLayout(2, 1));
		
		sellOrders = new ScrollableTablePanel<>(new MarketOrderModel());
		sellOrders.setBorder(BorderFactory.createTitledBorder("Sell Orders"));
		p2.add(sellOrders);
		
		buyOrders = new ScrollableTablePanel<>(new MarketOrderModel());
		buyOrders.setBorder(BorderFactory.createTitledBorder("Buy Orders"));
		p2.add(buyOrders);
		
		//Add top level components.
		add(p1);
		add(p2);
	}
	
	@Override
	public void updateGUI(){
		//Orders.
		sellOrders.setData(cdb.getSellOrders());
		buyOrders.setData(cdb.getBuyOrders());
		
		//Contracts.
		
	}
}
