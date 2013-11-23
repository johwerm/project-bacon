package evemanutool.gui.manu.filter;


import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import evemanutool.constants.DBConstants;
import evemanutool.data.database.ManuQuote;
import evemanutool.gui.manu.frameworks.FilterComponent;

@SuppressWarnings("serial")
public class QuoteMetaLevelsFilter extends FilterComponent implements DBConstants {
	
	//Components.
	private ArrayList<QuoteMetaLevelFilter> fL = new ArrayList<>();
	private QuoteMetaLevelFilter techI;
	private QuoteMetaLevelFilter other;

	public QuoteMetaLevelsFilter(JPanel parent, String label, boolean canEnable) {
		super(parent, label, new JPanel(new FlowLayout(FlowLayout.LEADING, 2, 0)), canEnable);
		
		QuoteMetaLevelFilter cB;
		JCheckBox b;
		
		//Tech I needs to be handled alone.
		b = new JCheckBox();
		b.setSelected(true);
		techI = new QuoteMetaLevelFilter(parent, b, META_GROUP_TECH_I_LABEL, META_GROUP_TECH_I_VALUE, null);
		fL.add(techI);
		fComp.add(techI);
		
		//Add the rest of the Meta-groups.
		for (int i = 0; i < META_GROUP_VALUE.length; i++) {
			b = new JCheckBox();
			b.setSelected(true);
			
			//Special case for faction, extra metaLevels added.
			if (META_GROUP_LABEL[i].equals("Faction")) {
				cB = new QuoteMetaLevelFilter(parent, b, META_GROUP_LABEL[i], META_GROUP_VALUE[i], META_FACTION_LEVELS);
			}else {
				cB = new QuoteMetaLevelFilter(parent, b, META_GROUP_LABEL[i], META_GROUP_VALUE[i], null);
			}
			
			fL.add(cB);
			fComp.add(cB);
		}
		//Add extra box for non included groups/items.
		b = new JCheckBox();
		b.setSelected(true);
		other = new QuoteMetaLevelFilter(parent, b, "Other", null, null);
		fComp.add(other);
	}

	@Override
	protected boolean quoteOk(ManuQuote q) {
		
		boolean isCovered = false;
		
		//Check metaGroup and if false check metaLevel.
		if (q.getBpo().getProduct().getMetaLevel() != null && 
				q.getBpo().getProduct().getMetaLevel() >= META_GROUP_TECH_I_MINMAX.getFst() &&
				q.getBpo().getProduct().getMetaLevel() <= META_GROUP_TECH_I_MINMAX.getSnd()) {
			if (techI.isSelected()) {
				return true;
			}
			isCovered = true;
		}
		
		for (QuoteMetaLevelFilter qF : fL) {
			if (qF.quoteOk(q)) {
				return true;
			}
			if (qF.isCovered(q)) {
				isCovered = true;
			}
		}
		
		return other.isSelected() && !isCovered;
	}
	
	@Override
	protected boolean isCovered(ManuQuote q) {
		
		for (QuoteMetaLevelFilter qF : fL) {
			if (qF.isCovered(q)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void reset() {

		
	}

	private class QuoteMetaLevelFilter extends FilterComponent {
		
		//Values.
		private Integer metaGroup;
		private Integer[] metaLevel;
		private JCheckBox cB;

		public QuoteMetaLevelFilter(JPanel parent, JCheckBox cB, String label, Integer metaGroup, Integer[] metaLevel) {
			super(parent, label, cB, false);
			this.cB = cB;
			this.metaGroup = metaGroup;
			this.metaLevel = metaLevel;
			setToolTipText("Select to include blueprints with products in " + label + " group.");
		}
		
		private boolean isSelected() {
			
			return cB.isSelected();
		}
		
		@Override
		protected boolean isCovered(ManuQuote q) {

			return metaLevel != null && Arrays.asList(metaLevel).contains(q.getBpo().getProduct().getMetaLevel()) ||
					q.getBpo().getProduct().getMetaGroup() == metaGroup;
		}

		@Override
		protected boolean quoteOk(ManuQuote q) {
			
			if (isSelected() && metaLevel != null && 
					Arrays.asList(metaLevel).contains(q.getBpo().getProduct().getMetaLevel())) {
				return true;
			}
			return isSelected() ? q.getBpo().getProduct().getMetaGroup() == metaGroup : false;
		}

		@Override
		public void reset() {
			cB.setSelected(true);
		}
	}
}
