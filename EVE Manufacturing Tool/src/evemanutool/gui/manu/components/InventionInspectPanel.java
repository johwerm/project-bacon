package evemanutool.gui.manu.components;


import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import evemanutool.data.database.Blueprint;
import evemanutool.data.database.ManuQuote;
import evemanutool.data.general.Time;
import evemanutool.gui.corp.ProductionPanel;
import evemanutool.gui.general.components.LabelBox;
import evemanutool.gui.general.components.NumberField;
import evemanutool.gui.general.components.NumberLabel;
import evemanutool.gui.manu.frameworks.InspectPanel;
import evemanutool.prefs.Preferences;
import evemanutool.prefs.Preferences.DefaultPriority;
import evemanutool.utils.calc.QuoteCalculator;
import evemanutool.utils.databases.BlueprintDB;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.databases.PriceDB;
import evemanutool.utils.databases.TechDB;

@SuppressWarnings("serial")
public class InventionInspectPanel extends InspectPanel {
	
	//DB:s and prefs.
	private TechDB mdb;
	
	//Graphical components.
	private NumberField runs;
	private NumberLabel meLevel = new NumberLabel(false, "");
	private NumberLabel peLevel = new NumberLabel(false, "");
	private NumberLabel unitsPerRun = new NumberLabel(false, "");
	private JLabel manuTime = new JLabel(new Time().toString());
	
	private NumberLabel success = new NumberLabel(false, "%");
	private NumberLabel t2BpcRuns = new NumberLabel(false, "");
	private NumberLabel t1BpcRuns = new NumberLabel(false, "");
	private JLabel copyTime = new JLabel(new Time().toString());
	private JLabel invTime = new JLabel(new Time().toString());
	
	private JComboBox<String> invPriority;
	
	public InventionInspectPanel(Preferences prefs, PriceDB pdb, BlueprintDB bdb, TechDB mdb, CorpApiDB cdb, ProductionPanel prodPanel) {
		super(prefs, pdb, bdb, cdb, prodPanel);
		this.mdb = mdb;
		
		//Top panels.
		JPanel bpoPanel = new JPanel();
		bpoPanel.setLayout(new BoxLayout(bpoPanel, BoxLayout.Y_AXIS));
		bpoPanel.setBorder(BorderFactory.createTitledBorder("Blueprint"));
		
		//Sub panels.
		JPanel bpoSubPanel1 = new JPanel(new FlowLayout(FlowLayout.LEADING, 40, 10));
		JPanel bpoSubPanel2;

		//Create components.		
		runs = new NumberField(1, false, 0, 100000, 5);
		
		//Bpo panel.
		//First row.
		bpoPanel.add(super.getHeaderRow());

		//Second row.
		runs.setPreferredSize(new Dimension(50, 25));
		bpoSubPanel1.add(new LabelBox("Runs", runs, BoxLayout.Y_AXIS));
		bpoSubPanel1.add(new LabelBox("ME", meLevel, BoxLayout.Y_AXIS));
		bpoSubPanel1.add(new LabelBox("PE", peLevel, BoxLayout.Y_AXIS));
		bpoSubPanel1.add(new LabelBox("Units/run", unitsPerRun, BoxLayout.Y_AXIS));
		bpoSubPanel1.add(new LabelBox("Manufacture Time", manuTime, BoxLayout.Y_AXIS));
		
		bpoPanel.add(bpoSubPanel1);

		//Third row.
		bpoPanel.add(super.getPriceRow());

		//Fourth row.
		JPanel inventionPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 15, 10));
		inventionPanel.setBorder(BorderFactory.createTitledBorder("Invention"));
		
		//Create components.
		invPriority = new JComboBox<>(INV_PRIO_LABEL);
		invPriority.setSelectedIndex(prefs.getDefaultPriorityIndex(DefaultPriority.INV_CALC));
		
		JPanel labelBox1 = new JPanel();
		labelBox1.setLayout(new BoxLayout(labelBox1, BoxLayout.Y_AXIS));				
		labelBox1.add(new JLabel("Success rate"));
		labelBox1.add(new JLabel("T2 BPC Runs"));
		labelBox1.add(new JLabel("Invention Time"));
		labelBox1.add(new JLabel("T1 BPC Runs"));
		labelBox1.add(new JLabel("Copy Time"));

		JPanel valueBox1 = new JPanel();
		valueBox1.setLayout(new BoxLayout(valueBox1, BoxLayout.Y_AXIS));
		valueBox1.add(success);
		valueBox1.add(t2BpcRuns);
		valueBox1.add(invTime);
		valueBox1.add(t1BpcRuns);
		valueBox1.add(copyTime);
				
		//Adjust components.
		success.setAlignmentX(RIGHT_ALIGNMENT);
		t2BpcRuns.setAlignmentX(RIGHT_ALIGNMENT);
		invTime.setAlignmentX(RIGHT_ALIGNMENT);
		t1BpcRuns.setAlignmentX(RIGHT_ALIGNMENT);
		copyTime.setAlignmentX(RIGHT_ALIGNMENT);
		
		inventionPanel.add(labelBox1);
		inventionPanel.add(valueBox1);
		inventionPanel.add(new LabelBox("Invention Priority", invPriority, BoxLayout.Y_AXIS));
		
		bpoSubPanel2 = super.getProfitRow();
		bpoSubPanel2.add(inventionPanel);
		bpoPanel.add(bpoSubPanel2);
		
		//Fifth row.
		bpoPanel.add(super.getActionRow());

		//Add main panels.
		add(bpoPanel);
		add(super.getMaterialPanel());
		
		//Set listeners.
		assignListener(runs.getDocument());
		assignListener(invPriority);
	}
	
	@Override
	protected ManuQuote initQuote(Blueprint b) {
		
		//Init fields.
		runs.setValue(1);
		meLevel.setValue(b.getMe());
		peLevel.setValue(b.getPe());
		invPriority.setSelectedIndex(prefs.getDefaultPriorityIndex(DefaultPriority.INV_CALC));
		
		return QuoteCalculator.calculateInventionQuote(modBpo, 1, null, pdb, mdb, bdb, prefs,
				INV_PRIO_ENUM[invPriority.getSelectedIndex()], 
				MAT_ACQUIRE_PRIO_ENUM[prefs.getDefaultPriorityIndex(DefaultPriority.MAT_CALC)]);
	}
	
	@Override
	protected ManuQuote makeQuote(ManuQuote currentQuote) {
		
		//Create Quote from fields.
		if (modBpo != null && runs.isValidInput()) {
			return QuoteCalculator.calculateInventionQuote(modBpo, (int) runs.getValue(), currentQuote, pdb, mdb, bdb, prefs,
					INV_PRIO_ENUM[invPriority.getSelectedIndex()], 
					MAT_ACQUIRE_PRIO_ENUM[prefs.getDefaultPriorityIndex(DefaultPriority.MAT_CALC)]);
		}
		return null;
	}
	
	@Override
	protected void updateFields(ManuQuote q) {

		//Update fields and labels.
		unitsPerRun.setValue(q.getBpo().getItemsPerRun());
		manuTime.setText(q.getManuTime().toString());
		meLevel.setValue(q.getBpo().getMe());
		peLevel.setValue(q.getBpo().getPe());

		//Invention fields.
		success.setValue(q.getInv().getSuccessRate() * 100);
		t1BpcRuns.setValue(q.getInv().getT1BpcRuns());
		t2BpcRuns.setValue(q.getInv().getT2BpcRuns());
		copyTime.setText(q.getInv().getCopyTime().toString());
		invTime.setText(q.getInv().getInvTime().toString());
	}
}
