package evemanutool.utils.httpdata;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import evemanutool.data.cache.MarketInfoEntry;
import evemanutool.gui.main.EMT;
import evemanutool.utils.databases.PriceDB;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;

public class EVECentralWorker extends ProgressWorker {

	//Constants.
	private static final int QUERY_DELAY = 2000;
	private static final int MAX_QUERIES = 25;

	//DB:s.
	private PriceDB pdb;
	
	//GUI
	private JLabel status;
	
	//Query info.
	private List<Integer> sellTypeIds;
	private List<Integer> buyTypeIds;
	private List<Integer> commonTypeIds = new ArrayList<>();
	private String sellSystem;
	private String buySystem;
	
	public EVECentralWorker(List<Integer> sellTypeIds, List<Integer> buyTypeIds, String sellSystem, String buySystem, PriceDB pdb, JProgressBar marketPB, JLabel marketStatus) {
		super(marketPB, Math.max(sellTypeIds.size(), buyTypeIds.size()));
		this.sellTypeIds = sellTypeIds;
		this.buyTypeIds = buyTypeIds;
		this.sellSystem = sellSystem;
		this.buySystem = buySystem;
		this.pdb = pdb;
		this.status = marketStatus;
	}

	@Override
	protected Void doInBackground() throws Exception {
		super.doInBackground();
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				status.setText("Incomplete: Updating");
				
			}
		});
		
		//Find common queries. Use the max number of days of the two.
		for (Integer id : sellTypeIds) {
			if (buyTypeIds.contains(id)) {
				commonTypeIds.add(id);
			}
		}
		//Remove the common queries from the single lists.
		for (Integer id : commonTypeIds) {
			sellTypeIds.remove(id);
			buyTypeIds.remove(id);
		}
		
		doCommonQueries();
		doUniqueQueries();
		
		return null;
	}

	private void doCommonQueries() throws InterruptedException {
		
		//Temp lists.
		ArrayList<Integer> iL = new ArrayList<>();
		List<MarketInfoEntry> mL = new ArrayList<>();
		
		while (!Thread.interrupted() && !commonTypeIds.isEmpty()) {
			
			iL = createQuery(commonTypeIds);
			
			//Do query.
			mL = EveCentralQuery.getMarketInfo(iL, sellSystem);
			pdb.addAllSMI(mL);
			
			if (sellSystem.equals(buySystem)) {
				pdb.addAllBMI(mL);
			}else {
				//Wait before extra query.
				Thread.sleep(QUERY_DELAY);
				pdb.addAllBMI(EveCentralQuery.getMarketInfo(iL, buySystem));
			}
			
			//Publish progress.
			publish(commonTypeIds.size() + Math.max(sellTypeIds.size(), buyTypeIds.size()));
			
			//Wait until next query.
			Thread.sleep(QUERY_DELAY);
		}
	}

	private void doUniqueQueries() throws InterruptedException {
		
		//Temp lists.
		ArrayList<Integer> iL = new ArrayList<>();
		List<MarketInfoEntry> mL = new ArrayList<>();
		
		while (!Thread.interrupted() && !sellTypeIds.isEmpty()) {
			
			iL = createQuery(sellTypeIds);
			
			//Do query.
			mL = EveCentralQuery.getMarketInfo(iL, sellSystem);
			pdb.addAllSMI(mL);
			
			//Publish progress.
			publish(commonTypeIds.size() + Math.max(sellTypeIds.size(), buyTypeIds.size()));
			
			//Wait until next query.
			Thread.sleep(QUERY_DELAY);
		}

		while (!Thread.interrupted() && !buyTypeIds.isEmpty()) {
			
			iL = createQuery(buyTypeIds);
			
			//Do query.
			mL = EveCentralQuery.getMarketInfo(iL, buySystem);
			pdb.addAllBMI(mL);
			
			//Publish progress.
			publish(commonTypeIds.size() + Math.max(sellTypeIds.size(), buyTypeIds.size()));
			
			//Wait until next query.
			Thread.sleep(QUERY_DELAY);
		}
	}
	
	/*
	 * Will remove used id's from the parameter list.
	 */
	private ArrayList<Integer> createQuery(List<Integer> typeIds) {
		ArrayList<Integer> iL = new ArrayList<>();
		
		//Pull some ids to make a query.
		while(!commonTypeIds.isEmpty() && iL.size() < MAX_QUERIES){
			iL.add(commonTypeIds.get(0));
			commonTypeIds.remove(0);
		}
		return iL;
	}
	
	@Override
	protected void process(List<Integer> chunks) {
		super.process(chunks);
		if (pdb.isComplete()) {
			status.setText("Expired: Updating");
		}
	}
	
	@Override
	protected void done() {
		super.done();
		status.setText("Complete: Idle");
		//Only update if database already complete to avoid double updating.
		if (pdb.isComplete()) {
			EMT.D_HANDLER.reportDBUpdateAtStage(pdb, Stage.PROCESS);
		}
	}
}
