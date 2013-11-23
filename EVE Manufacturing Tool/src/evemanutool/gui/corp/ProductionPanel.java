package evemanutool.gui.corp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import evemanutool.constants.DBConstants;
import evemanutool.data.cache.TradeEntry.HistoryType;
import evemanutool.data.display.CorpProductionQuote;
import evemanutool.gui.corp.components.CorpProductionQuoteModel;
import evemanutool.gui.corp.components.TradeHistoryPanel;
import evemanutool.gui.general.tabel.ScrollableTablePanel;
import evemanutool.gui.main.EMT;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.databases.PriceDB;
import evemanutool.utils.datahandling.GUIUpdater;

@SuppressWarnings("serial")
public class ProductionPanel extends JPanel implements GUIUpdater, SwingConstants, DBConstants{
	
	//DB:s.
	private CorpApiDB cdb;
	private PriceDB pdb;
	
	//Data.
	private CorpProductionQuote selectedQuote;
	
	//GUI Components.
	private ScrollableTablePanel<CorpProductionQuote> prodPanel;
	private SupplyPanel supplyPanel;
	private TradeHistoryPanel marketTrend;
	private TradeHistoryPanel volumeTrend;
	private JButton removeBtn;
	private JButton lookUpBtn;

	public ProductionPanel(CorpApiDB cdb, PriceDB pdb, SupplyPanel supplyPanel) {
		
		this.cdb = cdb;
		this.pdb = pdb;
		this.supplyPanel = supplyPanel;

		//Set layout
		setLayout(new GridLayout(1, 2));
		JPanel tablePanel = new JPanel(new BorderLayout());
		
		//Setup table.
		prodPanel = new ScrollableTablePanel<>(new CorpProductionQuoteModel(cdb, supplyPanel));
		prodPanel.getTable().getSelectionModel().addListSelectionListener(new SelectionListener());
		prodPanel.setBorder(BorderFactory.createTitledBorder("Production List"));
		
		//Setup actionPanel.
		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 20, 5));
		actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
		ButtonListener bL = new ButtonListener();
		removeBtn = new JButton("Remove");
		removeBtn.addActionListener(bL);
		lookUpBtn = new JButton("Look Up");
		lookUpBtn.addActionListener(bL);
		actionPanel.add(removeBtn);
		actionPanel.add(lookUpBtn);
		
		tablePanel.add(actionPanel, BorderLayout.NORTH);
		tablePanel.add(prodPanel, BorderLayout.CENTER);
		
		//Setup internal panels.
		JPanel trendPanel = new JPanel(new GridLayout(2, 1));
		marketTrend = new TradeHistoryPanel("Market Trend (Average Price)", "ISK");
		volumeTrend = new TradeHistoryPanel("Volume Trend (Sold Units)", "Units");
		trendPanel.add(marketTrend);
		trendPanel.add(volumeTrend);
		
		add(tablePanel);
		add(trendPanel);
	}
	
	@Override
	public void updateGUI() {
		//CorpAPIDB has new data, update and reload.
		prodPanel.setData(cdb.getProductionQuotes());
	}


	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			JButton b = (JButton) e.getSource();
			
			//Confirms that the selection and quote are valid.
			if (selectedQuote != null) {
				if (b == removeBtn && cdb.isComplete()) {
					//Try to remove the quote.
					if (!cdb.removeProductionQuote(selectedQuote)) {
						//If not successful, show dialog.
						JOptionPane.showMessageDialog(null, "The selected quote could not be removed", "Info", JOptionPane.INFORMATION_MESSAGE);
					}else {
						updateGUI();
						cdb.updateSupplyData();
						supplyPanel.updateGUI();
					}
				}else if (b == lookUpBtn) {
					//Implement tab switching.
					EMT.MAIN.showQuote(selectedQuote.getQuote());
				}
			}
		}
	}
	
	private class SelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			
			//Sets the selected quote from the showed list (Not the complete).
			if (!e.getValueIsAdjusting() && 
					prodPanel.getTable().getSelectedRow() >= 0 && 
					prodPanel.getTable().getSelectedRow() < prodPanel.getModel().size()) {
				
				//Set the trend data.
				selectedQuote = prodPanel.getModel().getDataAt(prodPanel.getSorter().convertRowIndexToModel(prodPanel.getTable().getSelectedRow()));
				marketTrend.setTradeHistory(pdb.getSellTH(selectedQuote.getQuote().getBpo().getProduct().getTypeId()), HistoryType.AVG);
				volumeTrend.setTradeHistory(pdb.getSellTH(selectedQuote.getQuote().getBpo().getProduct().getTypeId()), HistoryType.VOLUME);
			}
		}
	}
}
