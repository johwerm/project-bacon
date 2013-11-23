package evemanutool.gui.manu.frameworks;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.text.Document;

import evemanutool.constants.UserPrefConstants;
import evemanutool.data.database.Blueprint;
import evemanutool.data.database.ManuQuote;
import evemanutool.data.database.Material;
import evemanutool.gui.corp.ProductionPanel;
import evemanutool.gui.general.components.LabelBox;
import evemanutool.gui.general.components.NumberLabel;
import evemanutool.gui.general.tabel.BooleanCellRenderer;
import evemanutool.gui.general.tabel.ScrollableTablePanel;
import evemanutool.gui.manu.components.MaterialModel;
import evemanutool.prefs.Preferences;
import evemanutool.utils.databases.BlueprintDB;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.databases.PriceDB;

@SuppressWarnings("serial")
public abstract class InspectPanel extends JPanel implements UserPrefConstants{
	
	//DB:s and references.
	protected Preferences prefs;
	protected PriceDB pdb;
	protected BlueprintDB bdb;
	private CorpApiDB cdb;
	private ProductionPanel prodPanel;
	
	//Quote data.
	protected Blueprint modBpo;
	private ManuQuote currentQuote;
	
	//Main panels.
	private JPanel headerPanel = new JPanel();
	private JPanel pricePanel = new JPanel();
	private JPanel profitPanel = new JPanel();
	private JPanel actionPanel = new JPanel();
	private ScrollableTablePanel<Material> matPanel;
	
	//Graphical components.
	private JLabel bpoName = new JLabel("BPO name");
	private NumberLabel bpoId = new NumberLabel(false, "");
	private NumberLabel bpoMaxRuns = new NumberLabel(false, "");
	private NumberLabel bpoNPCCost = new NumberLabel(false, " ISK");
	private JButton addQuoteBtn = new JButton("Add To Corp");
	
	private NumberLabel manCost = new NumberLabel(true, " ISK");
	private NumberLabel manCostAll = new NumberLabel(true, " ISK");
	private NumberLabel sellPrice = new NumberLabel(true, " ISK");
	private NumberLabel sellPriceAll = new NumberLabel(true, " ISK");
	private NumberLabel profit = new NumberLabel(true, " ISK");
	private NumberLabel profitAll = new NumberLabel(true, " ISK");
	
	private NumberLabel profitPerH = new NumberLabel(true, " ISK");
	private NumberLabel profitPercent = new NumberLabel(true, "%");
	
	//Listener instance.
	private InputListener listener = new InputListener();
	
	public InspectPanel(Preferences prefs, PriceDB pdb, BlueprintDB bdb, CorpApiDB cdb, ProductionPanel prodPanel) {
		//Set fields.
		this.prefs = prefs;
		this.pdb = pdb;
		this.bdb = bdb;
		this.cdb = cdb;
		this.prodPanel = prodPanel;
		
		//Setup Layout.
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//Header.
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEADING, 15, 10));
		bpoName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		p1.add(bpoName);
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEADING, 15, 10));
		bpoId.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
		bpoMaxRuns.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
		bpoNPCCost.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
		p2.add(new LabelBox("Bpo Type Id", bpoId, BoxLayout.X_AXIS));
		p2.add(new LabelBox("Max Runs", bpoMaxRuns, BoxLayout.X_AXIS));
		p2.add(new LabelBox("NPC Cost", bpoNPCCost, BoxLayout.X_AXIS));
		headerPanel.add(p1);
		headerPanel.add(p2);
		
		//Price.
		pricePanel.setLayout(new FlowLayout(FlowLayout.LEADING, 15, 10));
		
		//First box.
		JPanel labelBox1 = new JPanel();
		labelBox1.setLayout(new BoxLayout(labelBox1, BoxLayout.Y_AXIS));
				
		labelBox1.add(new JLabel("Manufacture Cost"));
		labelBox1.add(new JLabel("Sell Income"));
		labelBox1.add(new JLabel("Profit"));

		JPanel valueBox1 = new JPanel();
		valueBox1.setLayout(new BoxLayout(valueBox1, BoxLayout.Y_AXIS));
		valueBox1.setPreferredSize(new Dimension(120, 50));
				
		//Adjust components.
		manCost.setAlignmentX(RIGHT_ALIGNMENT);
		sellPrice.setAlignmentX(RIGHT_ALIGNMENT);
		profit.setAlignmentX(RIGHT_ALIGNMENT);
		
		valueBox1.add(manCost);
		valueBox1.add(sellPrice);
		valueBox1.add(profit);
				
		//Second box
		JPanel labelBox2 = new JPanel();
		labelBox2.setLayout(new BoxLayout(labelBox2, BoxLayout.Y_AXIS));
		
		labelBox2.add(new JLabel("Manufacture Cost (All runs)"));
		labelBox2.add(new JLabel("Sell Income (All runs)"));
		labelBox2.add(new JLabel("Profit (All runs)"));
	
		JPanel valueBox2 = new JPanel();
		valueBox2.setLayout(new BoxLayout(valueBox2, BoxLayout.Y_AXIS));
		valueBox2.setPreferredSize(new Dimension(120, 50));
		
		//Adjust components.
		manCostAll.setAlignmentX(RIGHT_ALIGNMENT);
		sellPriceAll.setAlignmentX(RIGHT_ALIGNMENT);
		profitAll.setAlignmentX(RIGHT_ALIGNMENT);
		
		valueBox2.add(manCostAll);
		valueBox2.add(sellPriceAll);
		valueBox2.add(profitAll);
		pricePanel.add(labelBox1);
		pricePanel.add(valueBox1);
		pricePanel.add(Box.createHorizontalStrut(10));
		pricePanel.add(labelBox2);
		pricePanel.add(valueBox2);
		
		//Profit.
		profitPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 15, 10));
		
		JPanel labelBox3 = new JPanel();
		labelBox3.setLayout(new BoxLayout(labelBox3, BoxLayout.Y_AXIS));
		
		labelBox3.add(new JLabel("Profit/hour"));
		labelBox3.add(new JLabel("Profit %"));
		
		JPanel valueBox3 = new JPanel();
		valueBox3.setLayout(new BoxLayout(valueBox3, BoxLayout.Y_AXIS));
		valueBox3.setPreferredSize(new Dimension(120, 30));
		
		//Adjust components.
		profitPerH.setAlignmentX(RIGHT_ALIGNMENT);
		profitPercent.setAlignmentX(RIGHT_ALIGNMENT);
		
		valueBox3.add(profitPerH);
		valueBox3.add(profitPercent);
		
		profitPanel.add(labelBox3);
		profitPanel.add(valueBox3);
		
		//Action panel.
		actionPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 15, 0));
		actionPanel.add(addQuoteBtn);
		addQuoteBtn.addActionListener(listener);
		
		//Material panel.
		matPanel = new ScrollableTablePanel<>(new MaterialModel());
		matPanel.setBorder(BorderFactory.createTitledBorder("Materials"));
		//To make non-applicable checkboxes invisible.
		matPanel.getTable().getColumnModel().getColumn(2).setCellRenderer(new BooleanCellRenderer());
		
		//Set update listener.
		matPanel.getModel().addTableModelListener(listener);		
	}
	
	public void setBpo(Blueprint b) {
		
		//Set bpo.
		modBpo = new Blueprint(b);
		currentQuote = null;
		
		updateMainComponents(initQuote(modBpo));
	}

	protected abstract ManuQuote initQuote(Blueprint b);
	
	protected abstract ManuQuote makeQuote(ManuQuote currentQuote);

	protected abstract void updateFields(ManuQuote q);
	
	protected void updateMainComponents(ManuQuote q) {
	
		//Set the current quote.
		currentQuote = q;
		
		matPanel.getModel().setData(q.getMatList());
		bpoName.setText(q.getBpo().getBlueprintItem().getName());
		bpoId.setValue(q.getBpo().getBlueprintItem().getTypeId());
		bpoMaxRuns.setValue(q.getBpo().getMaxRuns());
		
		// Display a message if it's not available.
		if (q.isBaseBPOSeededOnMarket()) {
			bpoNPCCost.setValue(q.getBpo().getBlueprintItem().getBasePrice());
		} else {
			bpoNPCCost.setText("Not available");
		}
		
		manCost.setValue(q.getManuCost() / q.getRuns());
		manCostAll.setValue(q.getManuCost());
		sellPrice.setValue(q.getSellIncome() / q.getRuns());
		sellPriceAll.setValue(q.getSellIncome());
		profit.setValue(q.getProfit() / q.getRuns());
		profitAll.setValue(q.getProfit());
		
		profitPerH.setValue(q.getProfitPerHour());
		profitPercent.setValue((q.getProfit() / q.getManuCost()) * 100);
		
		//Call subclass method.
		updateFields(q);
	}

	protected void updateQuote() {

		if (currentQuote != null) {
			ManuQuote q = makeQuote(currentQuote);
			
			if (q == null) {
				return;
			} else {
				updateMainComponents(q);
			}
		}
	}

	protected void assignListener(Document d) {
		d.addDocumentListener(listener);
	}

	protected void assignListener(JComboBox<?> c) {
		c.addActionListener(listener);
	}

	protected JPanel getHeaderRow() {
		return headerPanel;
	}

	protected JPanel getPriceRow() {
		return pricePanel;
	}
	
	protected JPanel getProfitRow() {
		return profitPanel;
	}

	protected JPanel getActionRow() {
		return actionPanel;
	}
	
	protected JPanel getMaterialPanel() {
		return matPanel;
	}
	
	private class InputListener implements DocumentListener, ActionListener, TableModelListener {

		@Override
		public void changedUpdate(DocumentEvent e) {}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateQuote();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateQuote();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//Subclass component i.e ComboBox or AddButton has called.
			//Create a new quote for the InspectPanel to avoid using the as same in production calculations.
			ManuQuote q = currentQuote;
			updateQuote();
			
			if (e.getSource() == addQuoteBtn) {
				//Add quote to db.
				if (q != null && cdb.isComplete()) {
					if (!cdb.addProductionQuote(q)) {
						//If not successful, show dialog.
						JOptionPane.showMessageDialog(null, "The selected quote could not be added and may already exist", "Info", JOptionPane.INFORMATION_MESSAGE);
					}else {
						cdb.updateSupplyData();
						prodPanel.updateGUI();
					}
				}
			}
		}

		@Override
		public void tableChanged(TableModelEvent e) {
			if (e.getColumn() != TableModelEvent.ALL_COLUMNS) {
				updateQuote();
			}
		}
	}
}
