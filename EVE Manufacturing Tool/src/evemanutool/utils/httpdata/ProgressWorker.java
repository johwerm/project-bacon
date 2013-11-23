package evemanutool.utils.httpdata;

import java.text.NumberFormat;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public abstract class ProgressWorker extends SwingWorker<Void, Integer> {

	//Constants.
	public static final int PROGRESS_MIN = 0;
	public static final int PROGRESS_MAX = 1000;
	
	//GUI
	private JProgressBar pB;
	
	//Progress data.
	private final int numOfWork;
	private NumberFormat percentFormat;
	
	/*
	 * To update progress
	 * implementation should use publish(numWorkLeft) to send
	 * the number of work parts left.
	 */
	public ProgressWorker(JProgressBar pB, int numOfWork) {
		this.pB = pB;
		this.numOfWork = numOfWork;
		percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1);
	}

	@Override
	protected Void doInBackground() throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				//Start progress.
				pB.setStringPainted(true);
			}
		});
		return null;
	}
	
	@Override
	protected void process(List<Integer> chunks) {
		double progress = (numOfWork - chunks.get(chunks.size() - 1)) / (double) numOfWork;
		pB.setString(percentFormat.format(progress));
		pB.setValue((int) (progress * PROGRESS_MAX + 0.5));
	}
	
	@Override
	protected void done() {
		//Set progress to 100%.
		pB.setValue(PROGRESS_MAX);
		pB.setStringPainted(false);
	}
}
