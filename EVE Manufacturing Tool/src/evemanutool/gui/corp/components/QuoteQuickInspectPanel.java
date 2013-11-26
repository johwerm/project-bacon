package evemanutool.gui.corp.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import evemanutool.data.database.ManuQuote;
import evemanutool.data.general.Time;
import evemanutool.gui.general.components.NumberLabel;

@SuppressWarnings("serial")
public class QuoteQuickInspectPanel extends JPanel {
	
	//Graphical components.
	//Labels.
	private JLabel techItemLabel = new JLabel("BPC Runs");
	private JLabel copyTimeLabel = new JLabel("Copy Time");
	
	//Values.
	private JLabel manuTime = new JLabel(new Time().toString());
	private NumberLabel profitPerH = new NumberLabel(true, " ISK");
	
	private JLabel techItem = new JLabel();
	private JLabel copyTime = new JLabel(new Time().toString());
	
	public QuoteQuickInspectPanel() {
		
		setLayout(new BorderLayout());
		
		//Setup internal panels.
		JPanel subPanel1 = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 5));
		
		//Create components.		
		JPanel labelBox1 = new JPanel();
		labelBox1.setLayout(new BoxLayout(labelBox1, BoxLayout.Y_AXIS));				
		labelBox1.add(new JLabel("Manufacturing Time"));
		labelBox1.add(new JLabel("Profit/h"));
		labelBox1.add(techItemLabel);
		labelBox1.add(copyTimeLabel);

		JPanel valueBox1 = new JPanel();
		valueBox1.setLayout(new BoxLayout(valueBox1, BoxLayout.Y_AXIS));
		valueBox1.add(manuTime);
		valueBox1.add(profitPerH);
		valueBox1.add(techItem);
		valueBox1.add(copyTime);
		
		manuTime.setAlignmentX(RIGHT_ALIGNMENT);
		profitPerH.setAlignmentX(RIGHT_ALIGNMENT);
		techItem.setAlignmentX(RIGHT_ALIGNMENT);
		copyTime.setAlignmentX(RIGHT_ALIGNMENT);
		
		//Add to containers.
		subPanel1.add(labelBox1);
		subPanel1.add(valueBox1);
		
		//Hide the optional components to start with.
		hideInvRevValues();
		
		add(subPanel1);
	}
	
	public void selectQuote(ManuQuote q) {
		
		//Show the information for the quote.
		manuTime.setText(q.getManuTime().toString());
		profitPerH.setValue(q.getProfitPerHour());
		
		//Check for invention and reverse engineering and hide components accordingly.
		if (q.getInv() != null || q.getRev() != null) {
			showInvRevValues(q);
			
		} else  {
			hideInvRevValues();
			
			
			
		}
	}
	
	private void showInvRevValues(ManuQuote q) {
		
		//Show the appropriate components.
		techItemLabel.setVisible(true);
		techItem.setVisible(true);
		
		if (q.getInv() != null) {
			//Set the invention label.
			techItemLabel.setText("Decryptor");
			//Set value depending on if a decryptor is used.
			if (q.getInv().getDec() != null) {
				techItem.setText(q.getInv().getDec().getDecryptor().getName());
			} else {
				//No decryptor is used.
				techItem.setText("None");
			}
			
			copyTimeLabel.setVisible(true);
			copyTime.setVisible(true);
			copyTime.setText(q.getInv().getCopyTime().toString());
			
		} else if (q.getRev() != null) {
			//Set the reverse engineering label.
			techItemLabel.setText("Hybrid decryptor");
			//Hybrid decryptor is non-optional.
			techItem.setText(q.getRev().getHybridDecryptor().getName());
			
			copyTimeLabel.setVisible(false);
			copyTime.setVisible(false);
		}
	}
	
	private void hideInvRevValues() {
		
		//Hide the components.
		techItemLabel.setVisible(false);
		techItem.setVisible(false);
		copyTimeLabel.setVisible(false);
		copyTime.setVisible(false);
	}
}
