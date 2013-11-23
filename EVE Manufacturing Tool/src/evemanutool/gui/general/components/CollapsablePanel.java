package evemanutool.gui.general.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class CollapsablePanel extends JPanel {

	private JComponent contentComp;

	public CollapsablePanel(String text, JComponent c) {
		super(new GridBagLayout());
		
		//Set content component.
		contentComp = c;
		contentComp.setVisible(false);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		
		//Setup header.
		JPanel headerPanel = new JPanel();
		
		JButton toggleBtn = new JButton(text);
		toggleBtn.addActionListener(new ToggleListener());
		toggleBtn.setHorizontalAlignment(SwingConstants.CENTER);
		
		headerPanel.add(toggleBtn);
		
		//Add header and content component.
		add(headerPanel, gbc);
		add(contentComp, gbc);
	}
	
	private class ToggleListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//Toggle visibility.
			if (contentComp.isVisible()) {
				contentComp.setVisible(false);
			} else {
				contentComp.setVisible(true);
			}
		}
	}
}
