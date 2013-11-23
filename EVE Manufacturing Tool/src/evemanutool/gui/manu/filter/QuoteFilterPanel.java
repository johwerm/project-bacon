package evemanutool.gui.manu.filter;


import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;

import evemanutool.data.database.ManuQuote;
import evemanutool.gui.general.components.CollapsablePanel;
import evemanutool.gui.manu.frameworks.FilterComponent;
import evemanutool.utils.databases.MarketGroupDB;
import evemanutool.utils.datahandling.GUIUpdater;

@SuppressWarnings("serial")
public class QuoteFilterPanel extends JPanel implements GUIUpdater{
	
	//Constants.
	public static final String FILTER_UPDATE = "filterUpdate";

	//DB:s.
	
	//Component list.
	private ArrayList<FilterComponent> fL = new ArrayList<>();


	public QuoteFilterPanel(MarketGroupDB gdb) {
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		//Main panels.
		JPanel textFilterPanel= new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 10));
		textFilterPanel.setBorder(BorderFactory.createTitledBorder("Blueprint"));
		JPanel marketFilterPanel = new JPanel();
		marketFilterPanel.setLayout(new BoxLayout(marketFilterPanel, BoxLayout.Y_AXIS));
		marketFilterPanel.setBorder(BorderFactory.createTitledBorder("Product"));
		
		//Create components.
		//Text filters.
		addComp(textFilterPanel, new QuoteTextFilter(this, "Inclusive", new JTextField(16), false, true));
		addComp(textFilterPanel, new QuoteTextFilter(this, "Exclusive", new JTextField(16), false, false));
		
		//Blueprint on market filter.
		addComp(marketFilterPanel, new QuoteBlueprintFilter(this, "Blueprint On Market", false));
		
		//Market group filter.
		addComp(marketFilterPanel, new QuoteMarketGroupsFilter(this, "Market Groups", gdb, false));
		
		//Meta group filter.
		addComp(marketFilterPanel, new QuoteMetaLevelsFilter(this, "Meta Levels", false));
		
		//Add to main panel.
		p.add(textFilterPanel);
		p.add(marketFilterPanel);
		
		JPanel cP = new CollapsablePanel("Show/Hide Filter", p);
		add(cP);
	}
	
	private void addComp(JPanel parent, FilterComponent fC) {
		parent.add(fC);
		fL.add(fC);
	}
	
	public ArrayList<ManuQuote> filter(Collection<ManuQuote> list) {
		
		ArrayList<ManuQuote> ans = new ArrayList<>();
		boolean isOkay = false;

		for (ManuQuote q : list) {
			//Remove quotes according to the valid filter values.
			isOkay = true;
			for (FilterComponent fC : fL) {
				if (!fC.isQuoteOk(q)) {
					isOkay = false;
					break;
				}
			}
			if (isOkay) {
				ans.add(q);
			}
		}
		return ans;
	}
	
	public void reset() {
		
		for (FilterComponent fC : fL) {
			fC.reset();
		}
	}

	@Override
	public void updateGUI() {
		//Update GUI for the affected components.
		for (FilterComponent fC : fL) {
			if (fC instanceof GUIUpdater) {
				((GUIUpdater) fC).updateGUI();
			}
		}
	}
}
