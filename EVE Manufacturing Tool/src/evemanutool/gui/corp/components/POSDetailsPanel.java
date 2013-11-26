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

	
	public POSDetailsPanel() {
		
		//Setup Layout.
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		//Header.
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 15, 10));
		posName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		topPanel.add(posName);
		
		//Material panel.
		fuelPanel = new ScrollableTablePanel<>(new POSFuelModel());
		fuelPanel.setBorder(BorderFactory.createTitledBorder("Fuel"));
		
		//Add main panels.
		add(topPanel);
		add(fuelPanel);
	}
	
	public void setPOS(POS p) {
		
		posName.setText(p.getApiLocation().getItemName());
		fuelPanel.setData(p.getFuelList());
	}
}
