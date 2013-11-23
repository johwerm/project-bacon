package evemanutool;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JTable;

import evemanutool.gui.general.components.CollapsablePanel;

@SuppressWarnings("serial")
public class TestEMT extends JFrame {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestEMT();
	}
	
	public TestEMT() {
		setLayout(new BorderLayout());
		
		add(new CollapsablePanel("Toggle ME!", new JTable(3, 3)), BorderLayout.NORTH);
		add(new JScrollBar(), BorderLayout.CENTER);
		pack();
		setVisible(true);
	}
}
