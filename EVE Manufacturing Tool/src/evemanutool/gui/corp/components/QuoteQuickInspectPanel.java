package evemanutool.gui.corp.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import evemanutool.data.display.CorpProductionQuote;
import evemanutool.data.general.Time;
import evemanutool.gui.general.components.NumberLabel;

@SuppressWarnings("serial")
public class QuoteQuickInspectPanel extends JPanel {
	
	//Graphical components.
	//Labels.
	private JLabel bpcRunsLabel = new JLabel("BPC Runs");
	private JLabel copyTimeLabel = new JLabel("Copy Time");
	
	//Values.
	private JLabel manuTime = new JLabel(new Time().toString());
	private NumberLabel profitPerH = new NumberLabel(true, " ISK");
	private NumberLabel bpcRuns = new NumberLabel(false, "");
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
		labelBox1.add(bpcRunsLabel);
		labelBox1.add(copyTimeLabel);

		JPanel valueBox1 = new JPanel();
		valueBox1.setLayout(new BoxLayout(valueBox1, BoxLayout.Y_AXIS));
		valueBox1.add(manuTime);
		valueBox1.add(profitPerH);
		valueBox1.add(bpcRuns);
		valueBox1.add(copyTime);
		
		manuTime.setAlignmentX(RIGHT_ALIGNMENT);
		profitPerH.setAlignmentX(RIGHT_ALIGNMENT);
		bpcRuns.setAlignmentX(RIGHT_ALIGNMENT);
		copyTime.setAlignmentX(RIGHT_ALIGNMENT);
		
		//Add to containers.
		subPanel1.add(labelBox1);
		subPanel1.add(valueBox1);
		
		//Hide the optional components to start with.
		bpcRunsLabel.setVisible(false);
		bpcRuns.setVisible(false);
		copyTimeLabel.setVisible(false);
		copyTime.setVisible(false);
		
		add(subPanel1);
	}
	
	public void selectQuote(CorpProductionQuote q) {
		
		//Show the information for the quote.
		manuTime.setText(q.getQuote().getManuTime().toString());
		profitPerH.setValue(q.getQuote().getProfitPerHour());
		
		//Check for invention and reverse engineering and hide components accordingly.
		if (q.getQuote().getInv() != null) {
			bpcRunsLabel.setVisible(true);
			bpcRuns.setVisible(true);
			copyTimeLabel.setVisible(true);
			copyTime.setVisible(true);
			
			bpcRuns.setValue(q.getQuote().getInv().getT2BpcRuns());
			copyTime.setText(q.getQuote().getInv().getCopyTime().toString());
			
		} else if (q.getQuote().getRev() != null) {
			bpcRunsLabel.setVisible(true);
			bpcRuns.setVisible(true);
			copyTimeLabel.setVisible(false);
			copyTime.setVisible(false);
			
			bpcRuns.setValue(q.getQuote().getInv().getT2BpcRuns());
			copyTime.setText(q.getQuote().getInv().getCopyTime().toString());
			
		} else {
			bpcRunsLabel.setVisible(false);
			bpcRuns.setVisible(false);
			copyTimeLabel.setVisible(false);
			copyTime.setVisible(false);
		}
	}
}
