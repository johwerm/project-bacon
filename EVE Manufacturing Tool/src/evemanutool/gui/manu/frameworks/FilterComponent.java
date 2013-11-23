package evemanutool.gui.manu.frameworks;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import evemanutool.data.database.ManuQuote;
import evemanutool.gui.general.components.LabelBox;

@SuppressWarnings("serial")
public abstract class FilterComponent extends JPanel {
	
	//Constants.
	public static final String FILTER_UPDATE = "filterUpdate";
	
	//Graphical components.
	protected JComponent fComp;
	protected JPanel parent;
	private JCheckBox enableBox;

	public FilterComponent(JPanel listenerParent, String label, JComponent fComp, boolean canEnable) {
		
		this.parent = listenerParent;
		this.fComp = fComp;
		
		//Set layout.
		setLayout(new FlowLayout(FlowLayout.LEADING, 10, 0));
		
		//Create listener.
		InputListener listener = new InputListener();
	
		if (canEnable) {
			//Add a checkbox if item can be enabled/disabled and set to unchecked.
			enableBox = new JCheckBox();
			enableBox.setSelected(false);
			enableBox.addActionListener(listener);
			add(new LabelBox("Enable", enableBox, BoxLayout.Y_AXIS));
		}
		
		add(new LabelBox(label, fComp, BoxLayout.Y_AXIS));
		
		//Add listeners, takes text components, checkboxes and buttons.
		if (fComp instanceof JTextComponent) {
			((JTextComponent) fComp).getDocument().addDocumentListener(listener);
		}else if (fComp instanceof AbstractButton) {
			((AbstractButton) fComp).addActionListener(listener);
		}
	}
	
	public boolean isQuoteOk(ManuQuote q) {
		
		if (enableBox != null) {
			return enableBox.isSelected() ? quoteOk(q) : true;
		}else {
			return quoteOk(q);
		}
	}
	
	//Resets the filterComponent allowing all entries.
	public abstract void reset();

	//Checks if the given entry is accepted by the filterComponent.
	protected abstract boolean quoteOk(ManuQuote q);

	//Checks if the entry is affected in this filterComponent.
	protected abstract boolean isCovered(ManuQuote q);
	
	private class InputListener implements DocumentListener, ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			parent.firePropertyChange(FILTER_UPDATE, false, true);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {}

		@Override
		public void insertUpdate(DocumentEvent e) {
			parent.firePropertyChange(FILTER_UPDATE, false, true);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			parent.firePropertyChange(FILTER_UPDATE, false, true);
		}
	}
}
