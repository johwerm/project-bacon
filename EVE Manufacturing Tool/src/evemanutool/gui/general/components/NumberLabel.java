package evemanutool.gui.general.components;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class NumberLabel extends JLabel {
	
	private boolean allowDecimal;
	private String suffix;
	private DecimalFormat formatter;
	
	public NumberLabel(boolean allowDecimal, String suffix) {
		super("0");
		this.suffix = suffix;
	}
	
	public void setValue(double d) {
		
		if (allowDecimal) {
			formatter = new DecimalFormat("#,###,###,##0.00");
		}else {
			formatter = new DecimalFormat("#,###,###,###");
		}
		
		setText(formatter.format(d) + suffix);
		
		//Set font color.
		if (d < 0) {
			setForeground(Color.RED);
		}else {
			setForeground(Color.BLACK);
		}
	}
}