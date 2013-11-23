package evemanutool.gui.manu.filter;


import javax.swing.JPanel;
import javax.swing.JTextField;

import evemanutool.data.database.ManuQuote;
import evemanutool.gui.manu.frameworks.FilterComponent;

@SuppressWarnings("serial")
public class QuoteTextFilter extends FilterComponent {
	
	//Constants.
	private static final String TEXT_DELIM = ",";
	
	//Values.
	private boolean inclusive;

	public QuoteTextFilter(JPanel parent, String label, JTextField fComp, boolean canEnable, boolean inclusive) {
		super(parent,label, fComp, canEnable);
		this.inclusive = inclusive;
		setToolTipText("Filters all entries based on any of the typed sequences. Ex: \"wolf,avatar,bhaal\"");
	}

	@Override
	protected boolean quoteOk(ManuQuote q) {

		JTextField txtField = (JTextField) fComp;
		String[] txt = txtField.getText().split(TEXT_DELIM);
		
		//If field is empty return true;
		if (txtField.getText().equals("")) {
			return true;
		}
		
		for (String string : txt) {
			//Skip if empty.
			if (string.equals("")) {
				continue;
			}
			if (q.getBpo().getBlueprintItem().getName().toLowerCase().indexOf(string.toLowerCase()) >= 0) {
				return inclusive;
			}
		}
		return !inclusive;
	}

	@Override
	public void reset() {
		((JTextField) fComp).setText("");
	}
	
	@Override
	protected boolean isCovered(ManuQuote q) {
		
		return true;
	}
}
