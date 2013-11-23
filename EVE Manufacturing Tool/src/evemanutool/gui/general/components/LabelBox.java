package evemanutool.gui.general.components;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class LabelBox extends JPanel {
	
	public LabelBox(String label, JComponent c, int align) {
		setLayout(new BoxLayout(this, align));
		JLabel l = new JLabel(label);
		l.setAlignmentX(CENTER_ALIGNMENT);
		c.setAlignmentX(CENTER_ALIGNMENT);
		add(l);
		
		//Add some space if horizontal.
		if (align == BoxLayout.X_AXIS) {
			add(Box.createHorizontalStrut(5));
		}
		add(c);
	}	
}