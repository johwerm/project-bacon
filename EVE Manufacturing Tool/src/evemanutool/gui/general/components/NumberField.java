package evemanutool.gui.general.components;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@SuppressWarnings("serial")
public class NumberField extends JTextField {

	//Constants.
	private static final Color ERR_BACKGROUND = Color.YELLOW;
	private Color background;
	
	//Limits
	double min;
	double max;
	boolean allowDecimal;
	int maxChars;
	
	DecimalFormat df2;
	
	public NumberField(double value, boolean allowDecimal, double min, double max, int maxChars) {
		background = getBackground();
		this.min = min;
		this.max = max;
		this.allowDecimal = allowDecimal;
		this.maxChars = maxChars - 1;
		
		//Format as decimal or int.
		if (allowDecimal) {
			setText(value + "");
		}else {
			setText((int) value + "");
		}
		
		//Limit the number of characters.
		addKeyListener(new CharListener());
		
		//Set tooltip.
		if (allowDecimal) {	
			setToolTipText("Allowed range is: " + min + " - " + max);
		}else {
			//Adjust to be inclusive.
			setToolTipText("Allowed range is: " + (min + 1) + " - " + (max - 1));
		}
		
		//Set width.
		setColumns(maxChars);
		
		//Set listener.
		getDocument().addDocumentListener(new TextListener());
	}
	
	public void setValue(double value) {
		
		if (allowDecimal) {
			setText(value + "");
		}else {
			setText((int) value + "");
		}
	}
	
	public boolean isValidInput() {
		
		String s = getText();
		double d;
		try {
			if (allowDecimal) {
				d = Double.parseDouble(s);
			}else {
				d = Integer.parseInt(s);
			}
			if (min < d && max > d) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	public double getValue() {

		try {
			return Double.parseDouble(getText());
		} catch (Exception e) {
		}
		return 0;
	}
	
	private void updateBackground() {

		if (isValidInput()) {
			setBackground(background);
		}else {
			setBackground(ERR_BACKGROUND);
		}
	}
	
	private class TextListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent e) {}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateBackground();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateBackground();
		}
	}

	private class CharListener implements KeyListener {
	
		@Override
		public void keyPressed(KeyEvent e) {
			if(getText().length() > maxChars) {
				setText(getText().substring(0, maxChars));
			}
		}
	
		@Override
		public void keyReleased(KeyEvent e) {}
	
		@Override
		public void keyTyped(KeyEvent e) {}
	
	}
}
