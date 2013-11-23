package evemanutool.gui.manu.filter;


import javax.swing.JCheckBox;
import javax.swing.JPanel;

import evemanutool.data.database.ManuQuote;
import evemanutool.gui.manu.frameworks.FilterComponent;

@SuppressWarnings("serial")
public class QuoteBlueprintFilter extends FilterComponent {
	
	public QuoteBlueprintFilter(JPanel parent, String label, boolean canEnable) {
		super(parent, label, new JCheckBox(), canEnable);
	}

	@Override
	protected boolean quoteOk(ManuQuote q) {
		return ((JCheckBox) fComp).isSelected() ? (q.isBaseBPOSeededOnMarket() && q.getBpo().getBlueprintItem().isOnMarket()) : true;
	}

	@Override
	public void reset() {
		((JCheckBox) fComp).setSelected(false);
	}

	@Override
	protected boolean isCovered(ManuQuote q) {
		return true;
	}
}
