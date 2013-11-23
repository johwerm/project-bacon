package evemanutool.utils.httpdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import evemanutool.data.cache.TradeHistoryEntry;
import evemanutool.data.transfer.SimpleTradeHistoryQuery;
import evemanutool.data.transfer.TradeHistoryQuery;
import evemanutool.gui.main.EMT;
import evemanutool.utils.databases.PriceDB;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;

public class EVEMarketDataWorker extends ProgressWorker {
	
	//Constants.
	private static final int QUERY_DELAY = 2000;
	private static final int MAX_HTTP_LENGTH = 1000;
	private static final int MAX_RETURN_ROWS = 1000;

	//DB:s.
	private PriceDB pdb;
	
	//Query info.
	private List<TradeHistoryQuery> sellQueries;
	private List<TradeHistoryQuery> buyQueries;
	private List<TradeHistoryQuery> commonQueries = new ArrayList<>();
	private final String sellRegion;
	private final String buyRegion;
	private final boolean forceReplace;
	
	//GUI
	private final JLabel status;
	
	public EVEMarketDataWorker(List<TradeHistoryQuery> sellQueries, List<TradeHistoryQuery> buyQueries, boolean forceReplace, String sellRegion, String buyRegion, PriceDB pdb, JProgressBar historyPB, JLabel historyStatus) {
		super(historyPB, Math.max(sellQueries.size(), buyQueries.size()));
		this.sellQueries = sellQueries;
		this.buyQueries = buyQueries;
		this.sellRegion = sellRegion;
		this.buyRegion = buyRegion;
		this.forceReplace = forceReplace;
		this.pdb = pdb;
		this.status = historyStatus;
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
		for (TradeHistoryQuery mHQ : sellQueries) {
			if (buyQueries.contains(mHQ)) {
				commonQueries.add(new TradeHistoryQuery(Math.max(mHQ.getDays(), 
						buyQueries.get(buyQueries.indexOf(mHQ)).getDays()), mHQ.getTypeId()));
			}
		}

		//Remove the common queries from the single lists.
		for (TradeHistoryQuery mHQ : commonQueries) {
			sellQueries.remove(mHQ);
			buyQueries.remove(mHQ);
		}
		
		doCommonQueries();
		
		doUniqueQueries();
		
		return null;
	}

	private void doUniqueQueries() throws InterruptedException {
		
		SimpleTradeHistoryQuery sTHQ;
		List<TradeHistoryEntry> tL;
		
		//Sort queries after number of days (default).
		Collections.sort(sellQueries);
		Collections.sort(buyQueries);
		
		//Do the unique queries.
		while (!Thread.interrupted() && !sellQueries.isEmpty()) {
			
			//Create the query.
			sTHQ = createQuery(sellQueries);
			
			//Do query.
			tL = EveMarketDataQuery.getMarketInfo(sTHQ.getTypeIds(), sellRegion, sTHQ.getDays());
			pdb.addAllSTH(tL, forceReplace);
			
			//Publish progress
			publish(commonQueries.size() + Math.max(sellQueries.size(), buyQueries.size()));	
			//Wait until next query.
			Thread.sleep(QUERY_DELAY);
		}
		
		while (!Thread.interrupted() && !buyQueries.isEmpty()) {
			
			//Create the query.
			sTHQ = createQuery(buyQueries);
			
			//Do query.
			tL = EveMarketDataQuery.getMarketInfo(sTHQ.getTypeIds(), buyRegion, sTHQ.getDays());
			pdb.addAllSTH(tL, forceReplace);
			
			//Publish progress
			publish(commonQueries.size() + Math.max(sellQueries.size(), buyQueries.size()));	
			//Wait until next query.
			Thread.sleep(QUERY_DELAY);
		}
	}


	private void doCommonQueries() throws InterruptedException {
		
		SimpleTradeHistoryQuery sTHQ;
		List<TradeHistoryEntry> tL;
		
		//Sort queries after number of days (default).
		Collections.sort(commonQueries);
		
		while (!Thread.interrupted() && !commonQueries.isEmpty()) {
			
			//Create the query.
			sTHQ = createQuery(commonQueries);
			
			//Do query.
			tL = EveMarketDataQuery.getMarketInfo(sTHQ.getTypeIds(), sellRegion, sTHQ.getDays());
			pdb.addAllSTH(tL, forceReplace);
			
			if (sellRegion.equals(buyRegion)) {
				pdb.addAllBTH(tL, forceReplace);
			}else {
				//Wait before extra query.
				Thread.sleep(QUERY_DELAY);
				pdb.addAllBTH(EveMarketDataQuery.getMarketInfo(sTHQ.getTypeIds(), buyRegion, sTHQ.getDays()), forceReplace);
			}
			
			//Publish progress
			publish(commonQueries.size() + Math.max(sellQueries.size(), buyQueries.size()));
			
			//Wait until next query.
			Thread.sleep(QUERY_DELAY);
		}
	}
	
	/*
	 * Will remove used queries from the parameter list. 
	 */
	private SimpleTradeHistoryQuery createQuery(List<TradeHistoryQuery> sortedQueries) {
		
		//Temp lists.
		ArrayList<Integer> iL = new ArrayList<>();
		TradeHistoryQuery query;
		int digits = 0;
		int days = 0;
		
		//Pull some ids to make a query.
		while(!sortedQueries.isEmpty()){
			query = sortedQueries.get(0);
			if ((digits += (int) (Math.log10(query.getTypeId()) + 1)) > MAX_HTTP_LENGTH &&
					(iL.size() * query.getDays() > MAX_RETURN_ROWS ||
					((query.getDays() > (days * 2)) && days != 0))) {
				break;
			}
			
			//Query is ok, add and set days.
			iL.add(query.getTypeId());
			days = query.getDays();
			sortedQueries.remove(0);
		}
		
		return new SimpleTradeHistoryQuery(days, iL);
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
