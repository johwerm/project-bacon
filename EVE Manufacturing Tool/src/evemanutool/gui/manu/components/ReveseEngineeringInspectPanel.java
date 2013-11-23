package evemanutool.gui.manu.components;


import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
import evemanutool.utils.databases.ItemDB;
import evemanutool.utils.databases.PriceDB;

@SuppressWarnings("serial")
public class ReveseEngineeringInspectPanel extends InspectPanel {
	
	//DB:s and prefs.
	private ItemDB idb;
	
	//Graphical components.
	private NumberField runs;
	private NumberLabel meLevel = new NumberLabel(false, "");
	private NumberLabel peLevel = new NumberLabel(false, "");
	private NumberLabel unitsPerRun = new NumberLabel(false, "");
	private JLabel manuTime = new JLabel(new Time().toString());
	
	private NumberLabel success = new NumberLabel(false, "%");
	private NumberLabel t3BpcRuns = new NumberLabel(false, "");
	private JLabel revTime = new JLabel(new Time().toString());
	
	public ReveseEngineeringInspectPanel(Preferences prefs, PriceDB pdb, BlueprintDB bdb, ItemDB idb, CorpApiDB cdb, ProductionPanel prodPanel) {
		super(prefs, pdb, bdb, cdb, prodPanel);
		this.idb = idb;
		
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
		JPanel revPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 15, 10));
		revPanel.setBorder(BorderFactory.createTitledBorder("Reverse Engineering"));
		
		//Create components.
		JPanel labelBox1 = new JPanel();
		labelBox1.setLayout(new BoxLayout(labelBox1, BoxLayout.Y_AXIS));				
		labelBox1.add(new JLabel("Success rate"));
		labelBox1.add(new JLabel("T3 BPC Runs"));
		labelBox1.add(new JLabel("Reverse Engineering Time"));

		JPanel valueBox1 = new JPanel();
		valueBox1.setLayout(new BoxLayout(valueBox1, BoxLayout.Y_AXIS));
		valueBox1.add(success);
		valueBox1.add(t3BpcRuns);
		valueBox1.add(revTime);
				
		//Adjust components.
		success.setAlignmentX(RIGHT_ALIGNMENT);
		t3BpcRuns.setAlignmentX(RIGHT_ALIGNMENT);
		revTime.setAlignmentX(RIGHT_ALIGNMENT);
		
		revPanel.add(labelBox1);
		revPanel.add(valueBox1);
		
		bpoSubPanel2 = super.getProfitRow();
		bpoSubPanel2.add(revPanel);
		bpoPanel.add(bpoSubPanel2);
		
		//Fifth row.
		bpoPanel.add(super.getActionRow());

		//Add main panels.
		add(bpoPanel);
		add(super.getMaterialPanel());
		
		//Set listeners.
		assignListener(runs.getDocument());
	}
	
	@Override
	protected ManuQuote initQuote(Blueprint b) {
		
		//Init fields.
		runs.setValue(1);
		meLevel.setValue(b.getMe());
		peLevel.setValue(b.getPe());
		
		return QuoteCalculator.calculateReverseEngineeringQuote(modBpo, 1, null, pdb, bdb, idb, prefs,
				REV_PRIO_ENUM[prefs.getDefaultPriorityIndex(DefaultPriority.REV_CALC)], 
				MAT_ACQUIRE_PRIO_ENUM[prefs.getDefaultPriorityIndex(DefaultPriority.MAT_CALC)]);
	}
	
	@Override
	protected ManuQuote makeQuote(ManuQuote currentQuote) {
		
		//Create Quote from fields.
		if (modBpo != null && runs.isValidInput()) {
			return QuoteCalculator.calculateReverseEngineeringQuote(modBpo, (int) runs.getValue(), currentQuote, pdb, bdb, idb, prefs,
					REV_PRIO_ENUM[prefs.getDefaultPriorityIndex(DefaultPriority.REV_CALC)], 
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

		//Reverse Engineering fields.
		success.setValue(q.getRev().getSuccessRate() * 100);
		t3BpcRuns.setValue(q.getRev().getBpcRuns());
		revTime.setText(q.getRev().getRevTime().toString());
	}
}
