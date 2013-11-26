package evemanutool.utils.databases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import com.beimin.eveapi.account.apikeyinfo.ApiKeyInfoParser;
import com.beimin.eveapi.account.apikeyinfo.ApiKeyInfoResponse;
import com.beimin.eveapi.api.calllist.Call;
import com.beimin.eveapi.api.calllist.CallListParser;
import com.beimin.eveapi.api.calllist.CallListResponse;
import com.beimin.eveapi.core.ApiAuthorization;
import com.beimin.eveapi.corporation.accountbalance.AccountBalanceParser;
import com.beimin.eveapi.corporation.assetlist.AssetListParser;
import com.beimin.eveapi.corporation.industryjobs.IndustryJobsParser;
import com.beimin.eveapi.corporation.marketorders.MarketOrdersParser;
import com.beimin.eveapi.corporation.member.security.ApiSecurityMember;
import com.beimin.eveapi.corporation.member.security.ApiSecurityRole;
import com.beimin.eveapi.corporation.member.security.ApiSecurityTitle;
import com.beimin.eveapi.corporation.member.security.MemberSecurityParser;
import com.beimin.eveapi.corporation.member.security.MemberSecurityResponse;
import com.beimin.eveapi.corporation.member.tracking.ApiMember;
import com.beimin.eveapi.corporation.member.tracking.MemberTrackingParser;
import com.beimin.eveapi.corporation.member.tracking.MemberTrackingResponse;
import com.beimin.eveapi.corporation.sheet.CorpSheetParser;
import com.beimin.eveapi.corporation.sheet.CorpSheetResponse;
import com.beimin.eveapi.corporation.starbase.detail.StarbaseDetailParser;
import com.beimin.eveapi.corporation.starbase.detail.StarbaseDetailResponse;
import com.beimin.eveapi.corporation.starbase.list.ApiStarbase;
import com.beimin.eveapi.corporation.starbase.list.StarbaseListParser;
import com.beimin.eveapi.corporation.starbase.list.StarbaseListResponse;
import com.beimin.eveapi.corporation.titles.ApiRole;
import com.beimin.eveapi.corporation.titles.ApiTitle;
import com.beimin.eveapi.corporation.titles.CorporationTitlesParser;
import com.beimin.eveapi.corporation.titles.CorporationTitlesResponse;
import com.beimin.eveapi.corporation.wallet.journal.WalletJournalParser;
import com.beimin.eveapi.corporation.wallet.transactions.WalletTransactionsParser;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.KeyType;
import com.beimin.eveapi.shared.accountbalance.AccountBalanceResponse;
import com.beimin.eveapi.shared.accountbalance.EveAccountBalance;
import com.beimin.eveapi.shared.assetlist.AssetListResponse;
import com.beimin.eveapi.shared.assetlist.EveAsset;
import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;
import com.beimin.eveapi.shared.industryjobs.IndustryJobsResponse;
import com.beimin.eveapi.shared.locations.ApiLocation;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;
import com.beimin.eveapi.shared.marketorders.MarketOrdersResponse;
import com.beimin.eveapi.shared.wallet.journal.ApiJournalEntry;
import com.beimin.eveapi.shared.wallet.journal.WalletJournalResponse;
import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction;
import com.beimin.eveapi.shared.wallet.transactions.WalletTransactionsResponse;
import com.beimin.eveapi.utils.AccessChecker;

import evemanutool.constants.DBConstants;
import evemanutool.constants.ErrorConstants;
import evemanutool.constants.UserPrefConstants;
import evemanutool.data.cache.BlueprintAssetEntry;
import evemanutool.data.cache.CorpProductionEntry;
import evemanutool.data.cache.IndustryStatsEntry;
import evemanutool.data.cache.WalletJournalEntry;
import evemanutool.data.cache.WalletTransactionEntry;
import evemanutool.data.database.AbstractLocation;
import evemanutool.data.database.AbstractStation;
import evemanutool.data.database.Blueprint;
import evemanutool.data.database.ManuQuote;
import evemanutool.data.database.Material;
import evemanutool.data.database.Station;
import evemanutool.data.display.Asset;
import evemanutool.data.display.BlueprintAsset;
import evemanutool.data.display.CorpMember;
import evemanutool.data.display.CorpProductionQuote;
import evemanutool.data.display.Fuel;
import evemanutool.data.display.ManuAcquisition;
import evemanutool.data.display.MarketAcquisition;
import evemanutool.data.display.MarketOrder;
import evemanutool.data.display.POS;
import evemanutool.data.display.Supply;
import evemanutool.data.general.Time;
import evemanutool.gui.main.EMT;
import evemanutool.prefs.Preferences;
import evemanutool.prefs.Preferences.API;
import evemanutool.prefs.Preferences.Account;
import evemanutool.utils.calc.AssetCalculator;
import evemanutool.utils.calc.IndustryJobCalculator;
import evemanutool.utils.calc.ProductionCalculator;
import evemanutool.utils.calc.TaxCalculator;
import evemanutool.utils.calc.ValueCalculator;
import evemanutool.utils.datahandling.Database;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;
import evemanutool.utils.exceptions.ApiServerException;

public class CorpApiDB extends Database implements DBConstants, UserPrefConstants, ErrorConstants {
	
	//DB:s
	private Preferences prefs;
	private BlueprintDB bdb;
	private PriceDB pdb;
	private ItemDB idb;
	private LocationDB ldb;
	private TechDB tdb;
	private GraphicDB gdb;

	//Data.
	//API.
	private volatile ApiAuthorization auth;

	//Characters.
	private ConcurrentHashMap<Long, ApiMember> characterMap = new ConcurrentHashMap<>(); //CharacterId, Info.
	private volatile ArrayList<CorpMember> corpMembers;
	
	//Roles.
	private ConcurrentHashMap<Long, ApiSecurityMember> securityMap = new ConcurrentHashMap<>(); //CharcterId, SecInfo.
	
	//Info.
	private volatile CorpSheetResponse corpInfo;

	//Account balance.
	private ConcurrentHashMap<Integer, EveAccountBalance> accountMap = new ConcurrentHashMap<>(); //AccountKey, Balance.
	
	//Market orders.
	private volatile ArrayList<ApiMarketOrder> rawOrders;
	private volatile ArrayList<MarketOrder> sellOrders;
	private volatile ArrayList<MarketOrder> buyOrders;
	
	//Industry jobs.
	private volatile ArrayList<ApiIndustryJob> industryJobs;
	
	//Assets.
	private volatile ArrayList<Asset> treeAssets;
	private volatile ArrayList<Asset> flatAssets;
	private volatile ArrayList<EveAsset<EveAsset<?>>> rawAssets;
	
	//Cached values.
	//Blueprints.
	private volatile ArrayList<BlueprintAsset> bpos;
	private volatile ArrayList<BlueprintAssetEntry> rawBpos;
	private volatile ArrayList<BlueprintAssetEntry> rawOldBpos;

	//Wallet.
	private ConcurrentHashMap<Integer, ArrayList<WalletJournalEntry>> walletJournal = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Integer, ArrayList<WalletTransactionEntry>> walletTransactions = new ConcurrentHashMap<>();
	
	//Statistics.
	private volatile ArrayList<IndustryStatsEntry> industryStats;
	
	//Production.
	private Collection<CorpProductionQuote> productionQuotes; //Initialized as synchronizedCollection, locks on the instance during iteration.
	private volatile ArrayList<CorpProductionEntry> rawProductionQuotes;
	private volatile ArrayList<Supply> supplies;
	private volatile ArrayList<MarketAcquisition> marketAcquisitions;
	private volatile ArrayList<ManuAcquisition> manuAcquisitions;
	
	//Starbases.
	private volatile ArrayList<ApiStarbase> rawPOSList;
	private volatile ArrayList<Map<Integer, Integer>> rawFuelList;
	private volatile ArrayList<POS> posList;
	
	
	public CorpApiDB() {
		super(false, true, Stage.COMPUTE, null);
	}
	
	public void init(Preferences prefs, BlueprintDB bdb, ItemDB idb, PriceDB pdb, LocationDB ldb, GraphicDB gdb, TechDB tdb) {

		this.prefs = prefs;
		this.bdb = bdb;
		this.idb = idb;
		this.pdb = pdb;
		this.ldb = ldb;
		this.tdb = tdb;
		this.gdb = gdb;
	}
	
	@Override
	public synchronized void loadRawData() throws Exception {

		//Read cache
		readFromFile();
		
		//Create and check the API-key.
		auth = new ApiAuthorization(prefs.getAPIId(API.ID), prefs.getAPIKey(API.KEY)); //Move to prefs.
		if (!checkAPIAuthoraization(auth)) {
			throw new ApiServerException(API_AUTH_ERROR_MESSAGE);
		}
		
		try {
			//Get security information.
			MemberSecurityParser parser = MemberSecurityParser.getInstance();
			MemberSecurityResponse response = parser.getResponse(auth);
			for (ApiSecurityMember a : response.getMembers()) {
				securityMap.put(a.getCharacterID(), a);
			}
			
			//Get corporation members.
			MemberTrackingParser parser1 = MemberTrackingParser.getInstance();
			MemberTrackingResponse response1 = parser1.getResponse(auth);
			for (ApiMember a : response1.getAll()) {
				characterMap.put(a.getCharacterID(), a);
			}
			
			//Get general corporation information
			CorpSheetParser parser2 = CorpSheetParser.getInstance();
			corpInfo = parser2.getResponse(auth);
			
			//Get account balance.
			AccountBalanceParser parser3 = AccountBalanceParser.getInstance();
			AccountBalanceResponse response3 = parser3.getResponse(auth);
			for (EveAccountBalance e : response3.getAll()) {
				accountMap.put(e.getAccountKey(), e);
			}
			
			//Get market orders.
			MarketOrdersParser parser4 = MarketOrdersParser.getInstance();
			MarketOrdersResponse response4 = parser4.getResponse(auth);
			rawOrders = new ArrayList<>(response4.getAll());
			
			//Get industry jobs.
			IndustryJobsParser parser5 = IndustryJobsParser.getInstance();
			IndustryJobsResponse response5 = parser5.getResponse(auth);
			industryJobs = new ArrayList<>(response5.getAll());
			
			//Get assets.
			AssetListParser parser6 = AssetListParser.getInstance();
			AssetListResponse response6 = parser6.getResponse(auth);
			rawAssets = new ArrayList<>(response6.getAll());
			
			//Complete Journal list.
			WalletJournalParser parser7 = WalletJournalParser.getInstance();
			WalletJournalResponse response7;
			ArrayList<WalletJournalEntry> l1;
			WalletJournalEntry e1;
			for (int i = DIVISION_KEYS[0]; i < DIVISION_KEYS[DIVISION_KEYS.length - 1]; i++) {
				
				//Initiate the list.
				l1 = walletJournal.get(i);
				response7 = parser7.getResponse(auth, i);
				if (l1 == null) {
					walletJournal.put(i, new ArrayList<WalletJournalEntry>());
					l1 = walletJournal.get(i);
				}
				//Add entries.
				for (ApiJournalEntry a : response7.getAll()) {
					
					e1 = new WalletJournalEntry(a);
					if (!l1.contains(e1)) {
						l1.add(e1);
					}
				}
				//Sort the list.				
				Collections.sort(l1);
			}
				
			//Complete Transaction list.
			WalletTransactionsParser parser8 = WalletTransactionsParser.getInstance();
			WalletTransactionsResponse response8;
			ArrayList<WalletTransactionEntry> l2;
			WalletTransactionEntry e2;
			for (int i = DIVISION_KEYS[0]; i < DIVISION_KEYS[DIVISION_KEYS.length - 1]; i++) {
				
				//Initiate the list.
				l2 = walletTransactions.get(i);
				response8 = parser8.getResponse(auth, i);
				if (l2 == null) {
					walletTransactions.put(i, new ArrayList<WalletTransactionEntry>());
					l2 = walletTransactions.get(i);
				}
				//Add entries.
				for (ApiWalletTransaction a : response8.getAll()) {
					
					e2 = new WalletTransactionEntry(a);
					
					if (!l2.contains(e2)) {
						l2.add(e2);
					}
				}
				//Sort the list.
				Collections.sort(l2);
			}
			
			CorporationTitlesParser parser9 = CorporationTitlesParser.getInstance();
			CorporationTitlesResponse response9 = parser9.getResponse(auth);
			for (ApiTitle a : response9.getAll()) {
				for (ApiSecurityMember aSM : securityMap.values()) {
					for (ApiSecurityTitle aST : aSM.getTitles()) {
						if (aST.getTitleID() == a.getTitleID()) {
							//Add roles.
							for (ApiRole aR : a.getRoles()) {
								addRole(aR, aSM.getRoles());
							}
							for (ApiRole aR : a.getRolesAtBase()) {
								addRole(aR, aSM.getRolesAtBase());
							}
							for (ApiRole aR : a.getRolesAtHQ()) {
								addRole(aR, aSM.getRolesAtHQ());
							}
							for (ApiRole aR : a.getRolesAtOther()) {
								addRole(aR, aSM.getRolesAtOther());
							}
						}
					}
				}
			}
			
			
			
			StarbaseListParser parser10 = StarbaseListParser.getInstance();
			StarbaseListResponse response10 = parser10.getResponse(auth);
			ArrayList<ApiStarbase> aL = new ArrayList<>();
			ArrayList<Map<Integer, Integer>> fL = new ArrayList<>();
			for (ApiStarbase a : response10.getAll()) {
				StarbaseDetailParser parser11 = StarbaseDetailParser.getInstance();
				StarbaseDetailResponse response11 = parser11.getResponse(auth, a.getItemID());
				aL.add(a);
				fL.add(response11.getFuelMap());
			}	
			rawPOSList = aL;
			rawFuelList = fL;
			
		} catch (ApiException e) {
			//Convert to a ApiServerException.
			throw new ApiServerException(e);
		}
		
		//Show message.
		EMT.M_HANDLER.addMessage("Corp data loaded from chache and API.");
	}

	@Override
	public synchronized void processData() throws Exception {
		
		//Process market orders.
		ArrayList<MarketOrder> tmpSellOrders = new ArrayList<>();
		ArrayList<MarketOrder> tmpBuyOrders = new ArrayList<>();
		Station s;
		for (ApiMarketOrder a : rawOrders) {
			//Get station.
			s = ldb.getStationById(a.getStationID());
			if (a.getBid() == 1 &&
					(a.getIssued().getTime() + (((long) a.getDuration()) * 24 * 3600 * 1000) - new Date().getTime()) > 0) {
				//Buy order.
				//Avoid expired orders.
				tmpBuyOrders.add(new MarketOrder(a, getCorpMemberFromId(a.getCharID()),
						s, ldb.getRegionById(s.getRegionId()), idb.getItem(a.getTypeID()), 
						corpInfo.getWalletDivisions().get(a.getAccountKey())));
				
			}else {
				//Sell order.
				tmpSellOrders.add(new MarketOrder(a, getCorpMemberFromId(a.getCharID()),
						s, ldb.getRegionById(s.getRegionId()), idb.getItem(a.getTypeID()), 
						corpInfo.getWalletDivisions().get(a.getAccountKey())));
			}
		}
		//Set new database to global reference.
		sellOrders = tmpSellOrders;
		buyOrders = tmpBuyOrders;
		
		//A Converted asset list, completed with extra item- and location data.
		treeAssets = AssetCalculator.convertCompleteAssets(rawAssets, ldb, idb);
		
		//A flattened asset list, all items after each other.
		flatAssets = AssetCalculator.getFlatAssets(treeAssets);
		
		//Assets, add player given names/locations and load icons for used items.
		//ItemId list for API call.
		ArrayList<Long> idList = new ArrayList<>();
		for (Asset a : flatAssets) {
			//Add asset to id list.
			if (a.getAssets() != null && !a.getAssets().isEmpty() && a.getItem().getTypeId() != OFFICE_TYPEID) {
				//Add valid item id:s.
				idList.add(a.getItemID());
			}
			//Load icon.
			if (a.getItem().getIcon() == null) {
				a.getItem().setIcon(gdb.get32Icon(a.getItem().getTypeId()));
			}
			//Load icon for location, (Station).
			if (a.getLocation() != null && a.getLocation() instanceof AbstractStation && 
					((AbstractStation) a.getLocation()).getItem().getIcon() == null) {
				((AbstractStation) a.getLocation()).getItem().setIcon(
						gdb.get32Icon(((AbstractStation) a.getLocation()).getItem().getTypeId()));
			}
		}
		//Data map.
		HashMap<Long, ApiLocation> dataMap = new HashMap<>();

		//Get data.
		for (ApiLocation aL : ldb.getContainerNames(auth, idList)) {
			dataMap.put(aL.getItemID(), aL);
		}
		//Set names.
		ApiLocation alocation;
		for (Asset a : flatAssets) {
			alocation = dataMap.get(a.getItemID());
			if (alocation != null) {
				a.setPlayerName(alocation.getItemName());
			}
		}
		
		//Get blueprints in assets.
		//Temporary variables.
		ArrayList<BlueprintAssetEntry> tmpRawBpos = new ArrayList<>();
		Blueprint b = null;
		BlueprintAssetEntry bAE = null;
		int index = 0;
		for (Asset a : flatAssets) {
			b = bdb.getByBlueprintId(a.getItem().getTypeId());
			if (b != null) {
				//0 => untouched BPO,-1 => BPO, -2 => BPC
				if (a.getRawQuantity() == -1) { 
					//A BPO.
					bAE = new BlueprintAssetEntry(a.getItem().getTypeId(), a.getItemID(), true);
					tmpRawBpos.add(bAE);
					//Set ME/PE levels, if found in industry jobs or cache.
					index = rawOldBpos.indexOf(bAE);
					if (index >= 0) {
						bAE.setMe(rawOldBpos.get(index).getMe());
						bAE.setPe(rawOldBpos.get(index).getPe());
					}
				}else if (a.getRawQuantity() == 0) {
					//An unresearched BPO.
					for (int j = 0; j < a.getQuantity(); j++) {
						//Add BPO:s
						bAE = new BlueprintAssetEntry(a.getItem().getTypeId(), a.getItemID(), true);
						tmpRawBpos.add(bAE);
					}
				}
			}
		}
		//Get blueprints in research.
		//Update values from jobs (ME, PE).
		for (ApiIndustryJob iJ : industryJobs) {
			//0 => BPO, 1 => BPC.
			if (iJ.getInstalledItemCopy() == 0) {
				bAE = new BlueprintAssetEntry(iJ.getInstalledItemTypeID(), iJ.getInstalledItemID(), true);
				index = tmpRawBpos.indexOf(bAE);
				if (index < 0) {
					tmpRawBpos.add(bAE);
					IndustryJobCalculator.updateWithIndustryJob(iJ, bAE, bdb.getByBlueprintId(iJ.getInstalledItemTypeID()));
				}else {
					IndustryJobCalculator.updateWithIndustryJob(iJ, tmpRawBpos.get(index), bdb.getByBlueprintId(iJ.getInstalledItemTypeID()));
				}
			}
		}
		
		//Blueprint assets.
		//Temporary variables.
		ArrayList<BlueprintAsset> tmpBpos = new ArrayList<>();
		IndustryActivity iA = null;
		BpoState bS = null;
		AbstractLocation blocation = null;
		//Convert all BlueprintAssetEntries.
		for (BlueprintAssetEntry bAE2 : tmpRawBpos) {
			//Get state from industryJobs if still active.
			for (ApiIndustryJob iJ : industryJobs) {
				if (iJ.getInstalledItemID() == bAE2.getItemId()) {
					if (!iJ.isCompleted()) {
						//Set activity, state and location.
						iA = IndustryActivity.getFromKey(iJ.getActivityID());
						bS = BpoState.getState(iJ.getBeginProductionTime().getTime() < System.currentTimeMillis(),
								iJ.getEndProductionTime().getTime() < System.currentTimeMillis(), false, iJ.getCompletedStatus());
						blocation = ldb.getLocationFromId(iJ.getContainerLocationID());
						break;
					}else {
						//Clear any previous values.
						iA = null;
						bS = null;
						blocation = null;
						break;
					}
				}
			}
			
			//Set location from assets if not set.
			if (blocation == null) {
				blocation = AssetCalculator.getParentAssetLocation(treeAssets, bAE2.getItemId());
			}
			//Add BPO.
			tmpBpos.add(new BlueprintAsset(idb.getItem(bAE2.getTypeId()), blocation, bAE2, iA, bS));
		}
		
		//Starbases.
		ArrayList<POS> tmpPOSList = new ArrayList<>();
		ApiStarbase a;
		ArrayList<Fuel> fL;
		for (int i = 0; i < rawPOSList.size(); i++) {
			a = rawPOSList.get(i);
			fL = new ArrayList<>();
			
			//Create fuelList.
			for (Map<Integer, Integer> fuelMap : rawFuelList) {
				for (Entry<Integer, Integer> fuel : fuelMap.entrySet()) {
					fL.add(new Fuel(idb.getItem(fuel.getKey()), fuel.getValue()));
				}
			}
			
			//Create POS.
			tmpPOSList.add(new POS(a.getItemID(), idb.getItem(a.getTypeID()), (long) a.getMoonID(),
					ldb.getSystemById((long) a.getLocationID()), a.getStarbaseState(), a.getOnlineTimestamp(),
					a.getStateTimestamp(), new Time(0), fL));
		}

		//Set new database to global reference.
		rawBpos = tmpRawBpos;
		bpos = tmpBpos;
		posList = tmpPOSList;
		
		//Show message.
		EMT.M_HANDLER.addMessage("Industry and asset data procesed.");
	}
	
	@Override
	public synchronized void computeData() throws Exception {
		
		ArrayList<CorpMember> tmpCorpMembers = new ArrayList<>();
		for (ApiSecurityMember aSM : securityMap.values()) {
			tmpCorpMembers.add(new CorpMember(
					aSM.getCharacterID(), 
					TaxCalculator.calculateTotalTax(aSM.getCharacterID(), walletJournal.get(DIVISION_KEYS[0])), 
					TaxCalculator.calculateAverageWeekTax(aSM.getCharacterID(), walletJournal.get(DIVISION_KEYS[0])), 
					ValueCalculator.getCorpMemberAccessValue(treeAssets, aSM, characterMap.get(aSM.getCharacterID()), 
							corpInfo, prefs, pdb, accountMap), 
							characterMap.get(aSM.getCharacterID())));
		}
		
		//Add new industry stats.
		IndustryStatsEntry stats = new IndustryStatsEntry(new Date(), 
				ValueCalculator.getBuyOrderEscrow(buyOrders), 
				ValueCalculator.getSellOrderValue(buyOrders),
				accountMap.get(DIVISION_KEYS[prefs.getAccountIndex(Account.INDUSTRY_WALLET)]).getBalance(), 
				ValueCalculator.getAssetValue(prefs, pdb, 
						AssetCalculator.getFlatAssetsInCorpHangar(flatAssets, prefs.getAccountIndex(Account.INDUSTRY_HANGAR))));
		
		//Sort first.
		ArrayList<IndustryStatsEntry> tmpIndustryStats = new ArrayList<>(industryStats);
		Collections.sort(tmpIndustryStats);
		
		//Make sure that unnecessary entries are removed.
		if (tmpIndustryStats.size() >= 2) {
			if (tmpIndustryStats.get(0).getDate().getTime() - tmpIndustryStats.get(1).getDate().getTime() < STATS_UPDATE_INTERVAL * 3600000) {
				tmpIndustryStats.remove(0);
			}
		}
		tmpIndustryStats.add(0, stats);
		
		industryStats = tmpIndustryStats;
		
		//Create/convert production data.
		CorpProductionQuote cQ;
		productionQuotes = Collections.synchronizedCollection(new ArrayList<CorpProductionQuote>());
		for (CorpProductionEntry cMQE : rawProductionQuotes) {
			
			cQ = ProductionCalculator.calculateProductionQuoteFromRaw(cMQE, bdb, pdb, idb, tdb, this, prefs);
			if (!productionQuotes.contains(cQ)) {
				productionQuotes.add(cQ);
			}
		}
		
		//Create the supply data.
		updateSupplyData();
		
		//Set new database to global reference.
		corpMembers = tmpCorpMembers;

		//Show message.
		EMT.M_HANDLER.addMessage("Production results computed.");
		
		//Last initiation step, set complete.
		super.setComplete(true);
	}
	
	@Override
	public void saveData() {
		
		PrintWriter out;
		
		try {
			//Journal.
			out = new PrintWriter(WALLET_JOURNAL_CACHE_PATH);
		
			for (int i = DIVISION_KEYS[0]; i < DIVISION_KEYS[DIVISION_KEYS.length - 1]; i++) {
				//Print delimiter between accounts.
				out.println(LEVEL1_DELIM + i);
				for (WalletJournalEntry p : walletJournal.get(i)) {
					out.println(p.toParseString());
				}
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e1) {
			System.err.println("Could not save corp file cache: " + e1.getMessage());
		}
		
		try {
			//Transactions.
			out = new PrintWriter(WALLET_TRANS_CACHE_PATH);
			
			for (int i = DIVISION_KEYS[0]; i < DIVISION_KEYS[DIVISION_KEYS.length - 1]; i++) {
				//Print delimiter between accounts.
				out.println(LEVEL1_DELIM + i);
				for (WalletTransactionEntry p : walletTransactions.get(i)) {
					out.println(p.toParseString());
				}
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e1) {
			System.err.println("Could not save corp file cache: " + e1.getMessage());
		}	
		
		try {
			//Industry stats.
			out = new PrintWriter(INDUSTRY_STATS_CACHE_PATH);
			
			for (IndustryStatsEntry i : industryStats) {
				out.println(i.toParseString());
			}
			
			out.flush();
			out.close();
		} catch (FileNotFoundException e1) {
			System.err.println("Could not save corp file cache: " + e1.getMessage());
		}
			
		try {
			//Blueprint assets.
			out = new PrintWriter(BPO_ASSETS_CACHE_PATH);
			
			for (BlueprintAssetEntry b : rawBpos) {
				//Only write if bpo is researched.
				if (b.getMe() > 0 || b.getPe() > 0) {
					out.println(b.toParseString());
				}
			}
			
			out.flush();
			out.close();
		} catch (FileNotFoundException e1) {
			System.err.println("Could not save corp file cache: " + e1.getMessage());
		}
		
		try {
			//Production quotes.
			out = new PrintWriter(CORP_QUOTES_CACHE_PATH);
				
			synchronized (productionQuotes) {
				for (CorpProductionQuote cMQ : productionQuotes) {
					out.println(new CorpProductionEntry(cMQ).toParseString());
				}
			}
			
			out.flush();
			out.close();
		} catch (FileNotFoundException e1) {
			System.err.println("Could not save corp file cache: " + e1.getMessage());
		}
		System.out.println("---Finished saving corp data.---");
	}

	private boolean checkAPIAuthoraization(ApiAuthorization auth) throws ApiException {
		
		//Get key info.
		ApiKeyInfoParser parser1 = ApiKeyInfoParser.getInstance();
		ApiKeyInfoResponse response1 = parser1.getResponse(auth);
		
		//Get call info.
		CallListParser parser2 = CallListParser.getInstance();
		CallListResponse response2 = parser2.getResponse();
		ArrayList<Long> accessMasks = new ArrayList<>();
		
		//Extract accessMasks.
		for (Call call : AccessChecker.getCalls(response1, response2.get())) {
			//Check corporation access.
			if (call.getType() == KeyType.Corporation) {
				accessMasks.add(call.getAccessMask());
			}
		}
		
		for (long l : CORP_ACCESS_MASKS) {
			if (!accessMasks.contains(l)) {
				//Insufficient authorization.
				return false;
			}
		}
		//Sufficient authorization.
		return true;
	}

	private void readFromFile() {
		
		Scanner sc;
		int account = 0;
		String line;
		
		try {
			//Journal.
			ArrayList<WalletJournalEntry> l1 = null;
			int i = 0;
			sc = new Scanner(new File(WALLET_JOURNAL_CACHE_PATH));
			while (sc.hasNextLine()) {
				
				i++;
				line = sc.nextLine();
				
				//Set account number.
				if (line.startsWith(LEVEL1_DELIM)) {
					account = Integer.parseInt(line.substring(LEVEL1_DELIM.length()));
					//Initiate list.
					l1 = walletJournal.get(account);
					if (l1 == null) {
						walletJournal.put(account, new ArrayList<WalletJournalEntry>());
						l1 = walletJournal.get(account);
					}
				}else if (!line.equalsIgnoreCase("")) {
					try {
						l1.add(new WalletJournalEntry().fromParseString(line));
					} catch (Exception e) {
						System.err.println("Parse error for line nr: " + i + " ->" + line);
						e.printStackTrace();
					}
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.err.println("Could not read corp wallet journal cache: " + e.getMessage());
		}
		
		try {
			//Transactions.
			ArrayList<WalletTransactionEntry> l2 = null;
			int i = 0;
			sc = new Scanner(new File(WALLET_TRANS_CACHE_PATH));
			while (sc.hasNextLine()) {
				
				i++;
				line = sc.nextLine();
				
				//Set account number.
				if (line.startsWith(LEVEL1_DELIM)) {
					account = Integer.parseInt(line.substring(LEVEL1_DELIM.length()));
					//Initiate list.
					l2 = walletTransactions.get(account);
					if (l2 == null) {
						walletTransactions.put(account, new ArrayList<WalletTransactionEntry>());
						l2 = walletTransactions.get(account);
					}
				}else if (!line.equalsIgnoreCase("")) {
					try {
						l2.add(new WalletTransactionEntry().fromParseString(line));
					} catch (Exception e) {
						System.err.println("Parse error for line nr: " + i + " ->" + line);
						e.printStackTrace();
					}
				}
			}
			sc.close();
		} catch (Exception e) {
			System.err.println("Could not read corp wallet transaction cache: " + e.getMessage());
		}	
		
		try {	
			//Industry stats.
			sc = new Scanner(new File(INDUSTRY_STATS_CACHE_PATH));
			ArrayList<IndustryStatsEntry> tmpIndustryStats = new ArrayList<>();
			while (sc.hasNextLine()) {
				try {
					tmpIndustryStats.add(new IndustryStatsEntry().fromParseString(sc.nextLine()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			industryStats = tmpIndustryStats;
			sc.close();
		} catch (Exception e) {
			industryStats = new ArrayList<>();
			System.err.println("Could not read corp industry stats cache: " + e.getMessage());
		}
	
		try {
			//Blueprint assets.
			sc = new Scanner(new File(BPO_ASSETS_CACHE_PATH));
			ArrayList<BlueprintAssetEntry> tmpRawOldBpos = new ArrayList<>();
			while (sc.hasNextLine()) {
				try {
					tmpRawOldBpos.add(new BlueprintAssetEntry().fromParseString(sc.nextLine()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			rawOldBpos = tmpRawOldBpos;
			sc.close();
		} catch (Exception e) {
			rawOldBpos = new ArrayList<>();
			System.err.println("Could not read corp bpo stats cache: " + e.getMessage());
		}
		
		try {
			//Production quotes.
			sc = new Scanner(new File(CORP_QUOTES_CACHE_PATH));
			ArrayList<CorpProductionEntry> tmpRawProductionQuotes = new ArrayList<>();
			while (sc.hasNextLine()) {
				try {
					tmpRawProductionQuotes.add(new CorpProductionEntry().fromParseString(sc.nextLine()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			rawProductionQuotes = tmpRawProductionQuotes;
			sc.close();
		} catch (Exception e) {
			rawProductionQuotes = new ArrayList<>();
			System.err.println("Could not read corp production quote cache: " + e.getMessage());
		}
	}
	
	private void addRole(ApiRole aR, Collection<ApiSecurityRole> l) {
		//Only add role if list doesn't already contain it.
		for (ApiSecurityRole itrASR : l) {
			if (itrASR.getRoleID() == aR.getRoleID()) {
				return;
			}
		}
		ApiSecurityRole aSR = new ApiSecurityRole();
		aSR.setRoleID(aR.getRoleID());
		aSR.setRoleName(aR.getRoleName());
		l.add(aSR);
	}

	public ArrayList<BlueprintAsset> getBpos() {
		return bpos;
	}
	
	public ArrayList<Asset> getAssets() {
		return treeAssets;
	}

	public ArrayList<IndustryStatsEntry> getIndustryStats() {
		return industryStats;
	}

	public ApiMember getCorpMemberFromId(long charID) {
		return characterMap.get(charID);
	}
	
	public ArrayList<MarketOrder> getBuyOrders() {
		return buyOrders;
	}
	
	public ArrayList<MarketOrder> getSellOrders() {
		return sellOrders;
	}
	
	public ConcurrentHashMap<Integer, ArrayList<WalletTransactionEntry>> getWalletTransactions() {
		return walletTransactions;
	}
	
	public ConcurrentHashMap<Integer, ArrayList<WalletJournalEntry>> getWalletJournal() {
		return walletJournal;
	}
	
	public ArrayList<ApiIndustryJob> getIndustryJobs() {
		return industryJobs;
	}
	
	public ArrayList<Asset> getFlatAssets() {
		return flatAssets;
	}
	
	public Collection<CorpProductionQuote> getProductionQuotes() {
		return productionQuotes;
	}

	public ArrayList<Supply> getSupplies() {
		return supplies;
	}

	public ArrayList<MarketAcquisition> getMarketAcquisitions() {
		return marketAcquisitions;
	}

	public ArrayList<ManuAcquisition> getManuAcquisitions() {
		return manuAcquisitions;
	}
	
	public ArrayList<CorpMember> getCorpMembers() {
		return corpMembers;
	}

	public ArrayList<POS> getPosList() {
		return posList;
	}

	/*
	 * Methods for managing/updating the production quotes.
	 */
	public boolean removeProductionQuote(CorpProductionQuote selectedQuote) {
		//Will try to remove the given quote and returns a boolean based on success.
		return productionQuotes.remove(selectedQuote);
	}

	public boolean addProductionQuote(ManuQuote quote) {
		
		//Will try to remove the given quote and returns a boolean based on success.
		CorpProductionQuote cQ = ProductionCalculator.calculateProductionQuoteFromQuote(quote, bdb, pdb, this, prefs);
			
		if (productionQuotes.contains(cQ)) {
			return false;
		}
		productionQuotes.add(cQ);
		return true;
	}

	public void updateSupplyData() {
		
		//Init main lists.
		ArrayList<Supply> tmpSupplies = new ArrayList<>();
		ArrayList<MarketAcquisition> tmpMarketAcquisitions = new ArrayList<>();
		ArrayList<ManuAcquisition> tmpManuAcquisitions = new ArrayList<>();
		
		//Adds materials that are manufactured here with correct amounts.
		ArrayList<Material> manuMaterials = new ArrayList<>(); 
		
		//Adds materials that are bought here with correct amounts.
		ArrayList<Material> marketMaterials = new ArrayList<>();
		
		//Temporary variables.
		Supply s;
		Material m;
		int index;
		
		synchronized (productionQuotes) {
			for (CorpProductionQuote cMQ : productionQuotes) {
				//Only include if active.
				if (cMQ.isActive()) {
					for (Material m1 : cMQ.getQuote().getMatList()) {			
						//Set the amount of material.
						m = new Material(m1.getItem(), m1.getAmount() * cMQ.getSellTarget(), 
								m1.isRecycled(), m1.getPrice(), m1.canBeManufactured(), m1.isProduced(), m1.getManufactureQuote());
						
						if (m.isProduced()) {
							//Material is manufactured.
							index = manuMaterials.indexOf(m);
							//Add to manufactured.
							if (index < 0) {
								manuMaterials.add(m);
							}else {
								//Add to amount.
								manuMaterials.get(index).setAmount(manuMaterials.get(index).getAmount() + m.getAmount());
							}
							
							for (Material m2 : m1.getManufactureQuote().getMatList()) {
								//Set the amount of material.
								m = new Material(m1.getItem(), m2.getAmount() * m.getAmount(), 
										m2.isRecycled(), m2.getPrice(), m2.canBeManufactured(), m2.isProduced(), m2.getManufactureQuote());
								
								//Sub-Material is bought.
								index = marketMaterials.indexOf(m);
								//Add to bought.
								if (index < 0) {
									marketMaterials.add(m);
								}else {
									//Add to amount.
									marketMaterials.get(index).setAmount(marketMaterials.get(index).getAmount() + m.getAmount());
								}
							}
						}else {
							//Material is bought.
							index = marketMaterials.indexOf(m);
							//Add to bought.
							if (index < 0) {
								marketMaterials.add(m);
							}else {
								//Add to amount.
								marketMaterials.get(index).setAmount(marketMaterials.get(index).getAmount() + m.getAmount());
							}
						}
					}
				}
			}
		}
		
		
		//Create the supply list.
		//Add from market.
		for (Material m1 : marketMaterials) {
			s = new Supply(m1.getItem(), 0, m1.getAmount(), 0, 0);
			index = tmpSupplies.indexOf(s);
			if (index < 0) {
				tmpSupplies.add(s);
			}else {
				tmpSupplies.get(index).setNeeded(tmpSupplies.get(index).getNeeded() + s.getNeeded());
			}
		}
		//Add from manufacturing.
		for (Material m1 : manuMaterials) {
			s = new Supply(m1.getItem(), 0, m1.getAmount(), 0, 0);
			index = tmpSupplies.indexOf(s);
			if (index < 0) {
				tmpSupplies.add(s);
			}else {
				tmpSupplies.get(index).setNeeded(tmpSupplies.get(index).getNeeded() + s.getNeeded());
			}
		}
		
		//Init supply values.
		long needed;
		double tmp;
		ArrayList<Asset> astL = AssetCalculator.getFlatAssetsInCorpHangar(flatAssets, prefs.getAccountIndex(Account.INDUSTRY_HANGAR));
		for (Supply supply : tmpSupplies) {
			//Count stock.
			for (Asset a : astL) {
				if (supply.getItem().getTypeId() == a.getItem().getTypeId()) {
					supply.setStock(supply.getStock() + a.getQuantity());
				}
			}
			//Set needed to the correct value depending on stock.
			//Round up since items will need to be acquired whole but only.
			tmp = supply.getNeeded() - supply.getStock();
			//Only round up if number isn't whole.
			needed = (long) (tmp == (double) ((long) tmp) ? tmp : tmp + 1);
			supply.setNeeded(needed < 0 ? 0 : needed);
			
			//Count items on buy orders.
			for (MarketOrder mO : buyOrders) {
				if (supply.getItem().getTypeId() == mO.getItem().getTypeId()) {
					supply.setOnBuyOrder(supply.getOnBuyOrder() + mO.getMarketOrder().getVolRemaining());
				}
			}
			
			//Count items on industry jobs.
			for (ApiIndustryJob iJ : industryJobs) {
				if (supply.getItem().getTypeId() == iJ.getOutputTypeID() && !iJ.isCompleted()) {
					//Only count from jobs that are not delivered yet.
					supply.setInProduction(supply.getInProduction() + (iJ.getRuns() * supply.getItem().getPortionSize()));
				}
			}

			//Create acquisition data.
			needed = 0;
			//Market acquisitions.
			for (Material m1 : marketMaterials) {
				if (supply.getItem().getTypeId() == m1.getItem().getTypeId()) {
					if (supply.getNeeded() > 0) {
						//Add market acquisition.
						needed = (int) (supply.getNeeded() > m1.getAmount() ? m1.getAmount() + 1 : supply.getNeeded());
						tmpMarketAcquisitions.add(new MarketAcquisition(
								supply.getItem(), needed, m1.getPrice(), supply.getItem().getVolume() * needed, m1.getPrice() * needed));
					}
					break;
				}
			}
			
			//Manufacturing acquisitions.
			for (Material m1 : manuMaterials) {
				if (supply.getItem().getTypeId() == m1.getItem().getTypeId()) {
					if (supply.getNeeded() - needed > 0) {
						needed = (int) (supply.getNeeded() - needed > m1.getAmount() ? m1.getAmount() + 1 : supply.getNeeded() - needed);
						tmpManuAcquisitions.add(new ManuAcquisition(m1.getManufactureQuote(), needed, supply.getInProduction(), 
								ProductionCalculator.calculateMaterialCoverage(m1, needed, tmpSupplies), 
								new Time((int) (m1.getManufactureQuote().getManuTime().toSeconds() * needed + 0.5))));
					}
					break;
				}
			}
		}
		//Set new database to global reference.
		supplies = tmpSupplies;
		manuAcquisitions = tmpManuAcquisitions;
		marketAcquisitions = tmpMarketAcquisitions;
	}
}
