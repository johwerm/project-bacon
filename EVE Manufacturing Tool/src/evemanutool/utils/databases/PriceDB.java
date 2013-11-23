package evemanutool.utils.databases;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import evemanutool.constants.DBConstants;
import evemanutool.constants.UserPrefConstants;
import evemanutool.data.cache.MarketInfoEntry;
import evemanutool.data.cache.TradeEntry;
import evemanutool.data.cache.TradeHistoryEntry;
import evemanutool.data.transfer.TradeHistoryQuery;
import evemanutool.gui.main.EMT;
import evemanutool.prefs.Preferences;
import evemanutool.prefs.Preferences.MarketSetting;
import evemanutool.prefs.Preferences.MarketSystem;
import evemanutool.utils.datahandling.Database;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;
import evemanutool.utils.httpdata.EVECentralWorker;
import evemanutool.utils.httpdata.EVEMarketDataWorker;

public class PriceDB extends Database implements DBConstants, UserPrefConstants {
	
	//Constants.
	private static final long MARKET_CHECK_DELAY = 50; // Ms.

	//DB:s
	private BlueprintDB bdb;
	private Preferences prefs;
	
	//GUI
	private JProgressBar marketPB;
	private JLabel marketStatus;
	private JProgressBar historyPB;
	private JLabel historyStatus;

	//Data
	//Market, EVE-Central.
	private HashMap<Integer, MarketInfoEntry> dbms = new HashMap<Integer, MarketInfoEntry>(); //Sell.
	private HashMap<Integer, MarketInfoEntry> dbmb = new HashMap<Integer, MarketInfoEntry>(); //Buy.
	//History, EVE-marketdata.
	private HashMap<Integer, TradeHistoryEntry> dbhs = new HashMap<Integer, TradeHistoryEntry>(); //Sell.
	private HashMap<Integer, TradeHistoryEntry> dbhb = new HashMap<Integer, TradeHistoryEntry>(); //Buy.

	//List of all possible query ids.
	private ArrayList<Integer> historyQueryIds;
	private ArrayList<Integer> marketQueryIds;
	
	//DB status.
	//Query state.
	private Date lastQuery;
	
	//Query workers.
	private EVECentralWorker marketWorker;
	private EVEMarketDataWorker historyWorker;

	//Lock objects.
	private Object marketLock = new Object();
	private Object historyLock = new Object();
	private Object queryTimeLock = new Object();
	
	public PriceDB() {
		super(true, true, Stage.PROCESS, Stage.COMPUTE);
	}
	
	public void init(BlueprintDB bdb, Preferences prefs,
			JProgressBar marketPB, JLabel marketStatus,
			JProgressBar historyPB, JLabel historyStatus) {
		this.bdb = bdb;
		this.prefs = prefs;
		this.marketPB = marketPB;
		this.marketStatus = marketStatus;
		this.historyPB = historyPB;
		this.historyStatus = historyStatus;
	}

	@Override
	public synchronized void loadRawData() throws Exception {
		
		//Catch errors here since they are not critical to operation.
		Scanner sc;
		try {
			sc = new Scanner(new File(MARKET_CACHE_PATH));
			while (sc.hasNextLine()) {
				
				String[] infoStr = sc.nextLine().split(LEVEL1_DELIM);
				
				addSMI(new MarketInfoEntry().fromParseString(infoStr[0]));
				addBMI(new MarketInfoEntry().fromParseString(infoStr[1]));
				
			}
			sc.close();
		} catch (Exception e) {
			System.err.println("Could not read market file cache: " + e.getMessage());
		}
		String[] infoStr = null;
		try {
			sc = new Scanner(new File(HISTORY_CACHE_PATH));
			while (sc.hasNextLine()) {
				
				infoStr = sc.nextLine().split(LEVEL1_DELIM);
				
				addSTH(new TradeHistoryEntry().fromParseString(infoStr[0]), true);
				addBTH(new TradeHistoryEntry().fromParseString(infoStr[1]), true);
				
			}
			sc.close();
		} catch (Exception e) {
			System.err.println("Could not read history file cache: " + e.getMessage());
		}

		//Show message.
		EMT.M_HANDLER.addMessage("Market data loaded from cache. "
								+ (dbmb.size() + dbms.size()) + " price entries added and " + (dbhb.size() + dbhs.size()) + " history entries added.");
	}
	
	@Override
	public synchronized void processData() throws Exception {
		
		//Make sure all queries are added.
		historyQueryIds = bdb.getMarketQueryIds();
		marketQueryIds = bdb.getMinMarketQueryIds();
		
		//Interrupt the query threads.
		if (marketWorker != null) {
			marketWorker.cancel(true);
		}
		if (historyWorker != null) {
			historyWorker.cancel(true);
		}

		//Temporary variables.
		ArrayList<Integer> mSL = new ArrayList<>();
		ArrayList<Integer> mBL = new ArrayList<>();
		Date earliestAccepted;
		
		//Market
		//Set earliest date from preferences.
		earliestAccepted = new Date(System.currentTimeMillis() -
				(((long) prefs.getMarketSetting(MarketSetting.UPDATE_FREQ)) * 3600 * 1000));
		
		//Get market locations.
		long sellLocation = Long.parseLong(MARKET_SYSTEM_CODE[prefs.getMarketSystemIndex(MarketSystem.SELL_SYSTEM)]);
		long buyLocation = Long.parseLong(MARKET_SYSTEM_CODE[prefs.getMarketSystemIndex(MarketSystem.BUY_SYSTEM)]);
		
		synchronized (marketLock) {
			//Add non existing types.
			for (Integer typeId : marketQueryIds) {
				if (!dbms.containsKey(typeId)) {
					mSL.add(typeId);
				}	
				if (!dbmb.containsKey(typeId)) {
					mBL.add(typeId);
				}	
			}
			//Add too old types or wrong location.
			for (MarketInfoEntry mI : dbms.values()) {
				if (mI.getDate().before(earliestAccepted) || mI.getLocationId() != sellLocation) {
					mSL.add(mI.getTypeId());
				}		
			}
			for (MarketInfoEntry mI : dbmb.values()) {
				if (mI.getDate().before(earliestAccepted) || mI.getLocationId() != buyLocation) {
					mBL.add(mI.getTypeId());
				}				
			}
		}
		//Start QueryWorker.
		startMarketUpdate(mSL, mBL);
		
		//History
		//Set earliest date from preferences.
		earliestAccepted = new Date(System.currentTimeMillis() -
				(((long) HISTORY_MAX_DAYS) * 24 * 3600 * 1000));
		
		//Get market locations.
		sellLocation = Long.parseLong(MARKET_REGION_CODE[prefs.getMarketSystemIndex(MarketSystem.SELL_SYSTEM)]);
		buyLocation = Long.parseLong(MARKET_REGION_CODE[prefs.getMarketSystemIndex(MarketSystem.BUY_SYSTEM)]);
		
		//Check if database is incomplete.
		ArrayList<TradeHistoryQuery> sHL = new ArrayList<>();
		ArrayList<TradeHistoryQuery> bHL = new ArrayList<>();
		
		synchronized (historyLock) {
			int days;
			for (Integer typeId : historyQueryIds) {
				if (!dbhs.containsKey(typeId)) {
					//No history exists, add max amount.
					sHL.add(new TradeHistoryQuery(HISTORY_MAX_DAYS, typeId));
				}	
				if (!dbhb.containsKey(typeId)) {
					//No history exists, add max amount.
					bHL.add(new TradeHistoryQuery(HISTORY_MAX_DAYS, typeId));
				}	
			}
			//Check for outdated entries or wrong location.
			for (TradeHistoryEntry mH : dbhs.values()) {
				//Find the last entry to keep.
				for (int i = 0; i < mH.getHistory().size(); i++) {
					if (mH.getHistory().get(i).getDate().before(earliestAccepted)) {
						//Entry should not be included set history to sublist.
						mH.setHistory(mH.getHistory().subList(0, i));
						break;
					}
				}
				//Add a query for the rest if difference is larger than one day.
				if (mH.getHistory().isEmpty() || mH.getLocationId() != sellLocation) {
					sHL.add(new TradeHistoryQuery(HISTORY_MAX_DAYS, mH.getTypeId()));
				
				} else if ((days = (int) ((System.currentTimeMillis() - mH.getHistory().get(0).getDate().getTime()) / (24 * 3600 * 1000)))
						> MINIMUM_HISTORY_UPDATE_NEED) { 
					//Round down to avoid unnecessary data.
					//The number of days  will reach back to the latest entry. 
					sHL.add(new TradeHistoryQuery(days, mH.getTypeId()));
				}				
			}
			for (TradeHistoryEntry mH : dbhb.values()) {
				//Find the last entry to keep.
				for (int i = 0; i < mH.getHistory().size(); i++) {
					if (mH.getHistory().get(i).getDate().before(earliestAccepted)) {
						//Entry should not be included set history to sublist.
						mH.setHistory(mH.getHistory().subList(0, i));
						break;
					}
				}
				//Add a query for the rest if difference is larger than one day.
				if (mH.getHistory().isEmpty() || mH.getLocationId() != buyLocation) {
					bHL.add(new TradeHistoryQuery(HISTORY_MAX_DAYS, mH.getTypeId()));
					
				} else if ((days = (int) ((System.currentTimeMillis() - mH.getHistory().get(0).getDate().getTime()) / (24 * 3600 * 1000)))
						> MINIMUM_HISTORY_UPDATE_NEED) { 
					//Round down to avoid unnecessary data.
					//The number of days  will reach back to the latest entry. 
					bHL.add(new TradeHistoryQuery(days, mH.getTypeId()));
				}				
			}
		}
		
		//Start queries.
		startHistoryUpdate(sHL, bHL, false);
		
		//Wait for complete coverage, not necessarily entirely up to date.
		while (!isMIComplete() || !isTHComplete()) {
			//Wait until next check.
			Thread.sleep(MARKET_CHECK_DELAY);
		}
		
		//Last initiation step, set complete.
		super.setComplete(true);
	}
	
	@Override
	public void saveData() {
		
		try {
			//Interrupt the query threads.
			if (marketWorker != null) {
				marketWorker.cancel(true);
			}
			if (historyWorker != null) {
				historyWorker.cancel(true);
			}
			
			PrintWriter out = new PrintWriter(MARKET_CACHE_PATH);
			
			for (Integer id : marketQueryIds) {

				//Only save valid types.
				out.println(dbms.get(id).toParseString() + LEVEL1_DELIM + dbmb.get(id).toParseString());
			}
			out.flush();
			out.close();

			out = new PrintWriter(HISTORY_CACHE_PATH);
			
			for (Integer id : historyQueryIds) {
				//Only save valid types.
				out.println(dbhs.get(id).toParseString() + LEVEL1_DELIM + dbhb.get(id).toParseString());
			}
			out.flush();
			out.close();
			
			System.out.println("---Finished saving market data.---");
			
		} catch (FileNotFoundException e1) {
			System.err.println(e1.getMessage());
		}
	}
	
	@Override
	public void kill() {
		//Interrupt the query threads.
		if (marketWorker != null) {
			marketWorker.cancel(true);
		}
		if (historyWorker != null) {
			historyWorker.cancel(true);
		}
	}

	private void startMarketUpdate(List<Integer> sL, List<Integer> bL){
		
		marketWorker = new EVECentralWorker(sL, bL,
				MARKET_SYSTEM_CODE[prefs.getMarketSystemIndex(MarketSystem.SELL_SYSTEM)], 
				MARKET_SYSTEM_CODE[prefs.getMarketSystemIndex(MarketSystem.BUY_SYSTEM)], 
				this,
				marketPB,
				marketStatus);
		marketWorker.execute();
	}

	//Add MarketInfo.
	private void addSMI(MarketInfoEntry sellInfo) {
		synchronized (marketLock) {
			dbms.put(sellInfo.getTypeId(), sellInfo);
			
			//Update lastQuery date.
			setLastQuery(new Date());
		}
	}
	
	//Add MarketInfo.
	private void addBMI(MarketInfoEntry buyInfo) {
		synchronized (marketLock) {
			dbmb.put(buyInfo.getTypeId(), buyInfo);
			
			//Update lastQuery date.
			setLastQuery(new Date());
		}
	}
	
	public void addAllSMI(List<MarketInfoEntry> sellPrice) {
		for (int i = 0; i < sellPrice.size(); i++) {
			addSMI(sellPrice.get(i));
		}
	}

	public void addAllBMI(List<MarketInfoEntry> buyPrice) {
		for (int i = 0; i < buyPrice.size(); i++) {
			addBMI(buyPrice.get(i));
		}
	}

	//Returns the price of an item from typeId.
	public MarketInfoEntry getSellMI(int typeId) {	
		synchronized (marketLock) {
			return dbms.get(typeId);
		}
	}

	//Returns the price of an item from typeId.
	public MarketInfoEntry getBuyMI(int typeId) {	
		synchronized (marketLock) {
			return dbmb.get(typeId);
		}
	}
	
	private boolean isMIComplete() {
		synchronized (marketLock) {
			//Check for coverage.
			for (Integer typeId : marketQueryIds) {
				if (!dbms.containsKey(typeId) || !dbmb.containsKey(typeId)) {
					return false;
				}
			}
			return true;
		}
	}
	
	private void startHistoryUpdate(List<TradeHistoryQuery> sL, List<TradeHistoryQuery> bL, boolean forceReplace){
		
		historyWorker = new EVEMarketDataWorker(sL, bL, forceReplace,
				MARKET_REGION_CODE[prefs.getMarketSystemIndex(MarketSystem.SELL_SYSTEM)],
				MARKET_REGION_CODE[prefs.getMarketSystemIndex(MarketSystem.BUY_SYSTEM)], 
				this,
				historyPB,
				historyStatus);
		historyWorker.execute();
	}

	//Add MarketHistory, should have the same typeId.
	private void addTH(HashMap<Integer, TradeHistoryEntry> db, TradeHistoryEntry history, boolean forceReplace) {
		synchronized (historyLock) {
			//Add a new entry i none exist.
			if (db.get(history.getTypeId()) == null) {
				db.put(history.getTypeId(), new TradeHistoryEntry(history.getTypeId(), history.getLocationId()));
			}
			
			if (forceReplace) {
				
				//Replace the entry.
				db.put(history.getTypeId(), history);
			} else {
				
				//Add entries to history.
				ArrayList<TradeEntry> tmp = db.get(history.getTypeId()).getHistory();
				for (TradeEntry pHE : history.getHistory()) {
					if (!tmp.contains(pHE)) {
						tmp.add(pHE);
					}
				}
			}
			
			//Sort.
			Collections.sort(db.get(history.getTypeId()).getHistory());
		}
	}

	//Add MarketHistory, should have the same typeId.
	private void addSTH(TradeHistoryEntry sellHistory, boolean forceReplace) {
		addTH(dbhs, sellHistory, forceReplace);
	}

	//Add MarketHistory, should have the same typeId.
	private void addBTH(TradeHistoryEntry buyHistory, boolean forceReplace) {
		addTH(dbhb, buyHistory, forceReplace);
	}

	public void addAllSTH(List<TradeHistoryEntry> sHL, boolean forceReplace) {
		for (int i = 0; i < sHL.size(); i++) {
			addSTH(sHL.get(i), forceReplace);
		}
	}

	public void addAllBTH(List<TradeHistoryEntry> bHL, boolean forceReplace) {
		for (int i = 0; i < bHL.size(); i++) {
			addBTH(bHL.get(i), forceReplace);
		}
	}
	
	//Returns the history of an item from typeId.
	public TradeHistoryEntry getSellTH(int typeId) {	
		synchronized (historyLock) {
			return dbhs.get(typeId);
		}
	}

	//Returns the history of an item from typeId.
	public TradeHistoryEntry getBuyTH(int typeId) {	
		synchronized (historyLock) {
			return dbhb.get(typeId);
		}
	}
	
	private boolean isTHComplete() {
		synchronized (historyLock) {
			//Check for coverage.
			for (Integer typeId : historyQueryIds) {
				if (!dbhs.containsKey(typeId) || !dbhb.containsKey(typeId)) {
					return false;
				}
			}
			return true;
		}
	}
	
	
	private void setLastQuery(Date lastQuery) {
		synchronized (queryTimeLock) {
			this.lastQuery = lastQuery;
		}
	}
	
	private Date getLastQuery() {
		synchronized (queryTimeLock) {
			return lastQuery;
		}
	}

	public boolean forceUpdateMarketData() {
		//Returns true or false depending on if the update was started.
		if (isComplete() && (getLastQuery() == null ||
				System.currentTimeMillis() - getLastQuery().getTime() > MARKET_UPDATE_DELAY)) {
			//Market.
			//Interrupt the query thread if running.
			if (marketWorker != null) {
				marketWorker.cancel(true);
			}
			
			//Copy 
			ArrayList<Integer> mQueries = new ArrayList<>();
			for (Integer id : historyQueryIds) {
				mQueries.add(id);
			}
			startMarketUpdate(mQueries, mQueries);

			//History.
			//Interrupt the query thread if running.
			if (historyWorker != null) {
				historyWorker.cancel(true);
			}
			
			ArrayList<TradeHistoryQuery> tQueries = new ArrayList<>();
			for (Integer id : historyQueryIds) {
				tQueries.add(new TradeHistoryQuery(HISTORY_MAX_DAYS, id));
			}
			
			startHistoryUpdate(tQueries, tQueries, true);
			
			//Show message.
			EMT.M_HANDLER.addMessage("Market update forced. "
									+ marketQueryIds.size()	+ " price entries queued and  " + historyQueryIds.size() + " history entries queued.");
			
			return true;
		}
		return false;
	}
	
	public int getMinsToNextMarketUpdate() {
		if (getLastQuery() == null) {
			return 0;
		}
		return (int) (((MARKET_UPDATE_DELAY + getLastQuery().getTime() - System.currentTimeMillis()) / (60 * 1000)) + 0.5);
	}
}
