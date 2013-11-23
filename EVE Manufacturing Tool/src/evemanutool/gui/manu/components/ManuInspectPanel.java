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
import evemanutool.utils.databases.PriceDB;

@SuppressWarnings("serial")
public class ManuInspectPanel extends InspectPanel {
	
	//Graphical components.
	private NumberField runs;
	private NumberField meLevel;
	private NumberField peLevel;
	private NumberLabel unitsPerRun = new NumberLabel(false, "");
	private JLabel manuTime = new JLabel(new Time().toString());
	
	public ManuInspectPanel(Preferences prefs, PriceDB pdb, BlueprintDB bdb, CorpApiDB cdb, ProductionPanel prodPanel) {
		super(prefs, pdb, bdb, cdb, prodPanel);
		
		//Top panels.
		JPanel bpoPanel = new JPanel();
		bpoPanel.setLayout(new BoxLayout(bpoPanel, BoxLayout.Y_AXIS));
		bpoPanel.setBorder(BorderFactory.createTitledBorder("Blueprint"));
		
		//Sub panels.
		JPanel bpoSubPanel1 = new JPanel(new FlowLayout(FlowLayout.LEADING, 40, 10));

		//Create components.		
		runs = new NumberField(1, false, 0, 100000, 5);
		meLevel = new NumberField(0, false, -50, 1000, 3);
		peLevel = new NumberField(0, false, -50, 1000, 3);
		
		//Bpo panel.
		//First row.		
		bpoPanel.add(super.getHeaderRow());

		//Second row.
		runs.setPreferredSize(new Dimension(50, 25));
		bpoSubPanel1.add(new LabelBox("Runs", runs, BoxLayout.Y_AXIS));
		meLevel.setPreferredSize(new Dimension(50, 25));
		bpoSubPanel1.add(new LabelBox("ME", meLevel, BoxLayout.Y_AXIS));
		peLevel.setPreferredSize(new Dimension(50, 25));
		bpoSubPanel1.add(new LabelBox("PE", peLevel, BoxLayout.Y_AXIS));
		bpoSubPanel1.add(new LabelBox("Units/run", unitsPerRun, BoxLayout.Y_AXIS));
		bpoSubPanel1.add(new LabelBox("Manufacture Time", manuTime, BoxLayout.Y_AXIS));
		
		bpoPanel.add(bpoSubPanel1);

		//Third row.
		bpoPanel.add(super.getPriceRow());

		//Fourth row.	
		bpoPanel.add(super.getProfitRow());
		
		//Fifth row.
		bpoPanel.add(super.getActionRow());
		
		//Add main panels.
		add(bpoPanel);
		add(super.getMaterialPanel());
		
		//Set listeners.
		assignListener(runs.getDocument());
		assignListener(meLevel.getDocument());
		assignListener(peLevel.getDocument());
	}
	
	@Override
	protected ManuQuote initQuote(Blueprint b) {
		
		//Init fields.
		runs.setValue(1);
		meLevel.setValue(b.getMe());
		peLevel.setValue(b.getPe());
		return QuoteCalculator.calculateQuote(modBpo, 1, null, pdb, bdb, prefs, 
				MAT_ACQUIRE_PRIO_ENUM[prefs.getDefaultPriorityIndex(DefaultPriority.MAT_CALC)]);
	}
	
	@Override
	protected ManuQuote makeQuote(ManuQuote currentQuote) {
		
		//Create Quote from fields.
		if (modBpo != null && runs.isValidInput() && meLevel.isValidInput() && peLevel.isValidInput()) {
			modBpo.setMe((int) meLevel.getValue());
			modBpo.setPe((int) peLevel.getValue());
			return QuoteCalculator.calculateQuote(modBpo, (int) runs.getValue(), currentQuote, pdb, bdb, prefs, 
					MAT_ACQUIRE_PRIO_ENUM[prefs.getDefaultPriorityIndex(DefaultPriority.MAT_CALC)]);
		}
		return null;
	}
	
	@Override
	protected void updateFields(ManuQuote q) {
		
		//Update subclass-specific fields and labels.
		unitsPerRun.setValue(q.getBpo().getItemsPerRun());
		manuTime.setText(q.getManuTime().toString());
	}
}
