package evemanutool.gui.corp.components;


import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import evemanutool.data.display.Fuel;
import evemanutool.data.display.POS;
import evemanutool.gui.general.tabel.ScrollableTablePanel;

@SuppressWarnings("serial")
public class POSDetailsPanel extends JPanel {
	
	//Subpanels.
	private ScrollableTablePanel<Fuel> fuelPanel;
	
	//Graphical components.
	private JLabel posName = new JLabel("POS Name");
	private JLabel posState = new JLabel();

	
	public POSDetailsPanel() {
		
		//Setup Layout.
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//Header.
		JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEADING, 15, 10));
		posName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		p1.add(posName);

		//Info.
		JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEADING, 15, 10));
		p2.add(new JLabel("State"));
		p2.add(posState);
		
		//Material panel.
		fuelPanel = new ScrollableTablePanel<>(new POSFuelModel());
		fuelPanel.setBorder(BorderFactory.createTitledBorder("Fuel"));
		
		//Add main panels.
		add(p1);
		add(p2);
		add(fuelPanel);
	}
	
	public void setPOS(POS p) {
		
		posName.setText(p.getApiLocation().getItemName());
		posState.setText(p.getState().name());
		fuelPanel.setData(p.getFuelList());
	}
}
