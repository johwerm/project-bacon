package evemanutool.gui.manu.filter;


import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import evemanutool.data.database.ManuQuote;
import evemanutool.data.database.MarketGroup;
import evemanutool.gui.manu.frameworks.FilterComponent;
import evemanutool.utils.databases.MarketGroupDB;
import evemanutool.utils.datahandling.GUIUpdater;

@SuppressWarnings("serial")
public class QuoteMarketGroupsFilter extends FilterComponent implements GUIUpdater {
	
	//DB:s.
	private MarketGroupDB gdb;

	//Components.
	private ArrayList<QuoteMarketGroupFilter> fL = new ArrayList<>();
	private QuoteMarketGroupFilter other;

	public QuoteMarketGroupsFilter(JPanel parent, String label, MarketGroupDB gdb, boolean canEnable) {
		super(parent, label, new JPanel(new FlowLayout(FlowLayout.LEADING, 2, 0)), canEnable);
		this.gdb = gdb;
	}
	
	@Override
	public void updateGUI() {
		//Clean layout.
		fComp.removeAll();
		
		//Add new filterboxes.
		QuoteMarketGroupFilter cB;
		JCheckBox b;
		for (MarketGroup mG : gdb.getProductGroups()) {
			b = new JCheckBox();
			b.setSelected(true);
			cB = new QuoteMarketGroupFilter(parent, b, mG.getName(), mG.getChildGroups());
			fL.add(cB);
			fComp.add(cB);
		}
		//Add extra box for rest of the groups.
		b = new JCheckBox();
		b.setSelected(true);
		other = new QuoteMarketGroupFilter(parent, b, "Other", null);
		fComp.add(other);
		
		//Update layout.
		revalidate();
	}
	
	@Override
	protected boolean quoteOk(ManuQuote q) {

		for (QuoteMarketGroupFilter qF : fL) {
			if (qF.quoteOk(q)) {
				return true;
			}
		}
		return other.isSelected() && !isCovered(q);
	}
	
	@Override
	protected boolean isCovered(ManuQuote q) {
		
		for (QuoteMarketGroupFilter qF : fL) {
			if (qF.isCovered(q)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void reset() {

		for (QuoteMarketGroupFilter qF : fL) {
			qF.reset();
		}
	}

	private class QuoteMarketGroupFilter extends FilterComponent {
		
		//Values.
		private Collection<Integer> marketGroups;
		private JCheckBox cB;

		public QuoteMarketGroupFilter(JPanel parent, JCheckBox cB, String label, Collection<Integer> marketGroups) {
			super(parent, label, cB, false);
			this.cB = cB;
			this.marketGroups = marketGroups;
			setToolTipText("Select to include blueprints with products in " + label + " group.");
		}
		
		private boolean isSelected() {
			return cB.isSelected();
		}

		@Override
		protected boolean quoteOk(ManuQuote q) {
			
			if (isSelected()) {
				for (int i : marketGroups) {
					if (q.getBpo().getProduct().getMarketGroup() == i) {
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public void reset() {
			cB.setSelected(true);
		}

		@Override
		protected boolean isCovered(ManuQuote q) {

			for (int i : marketGroups) {
				if (q.getBpo().getProduct().getMarketGroup() == i) {
					return true;
				}
			}
			return false;
		}

	}
}
