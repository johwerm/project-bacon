package evemanutool.utils.calc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;

import evemanutool.constants.DBConstants;
import evemanutool.constants.UserPrefConstants;
import evemanutool.data.cache.CorpProductionEntry;
import evemanutool.data.cache.MarketInfoEntry.OrderAim;
import evemanutool.data.cache.NumberTrendEntry;
import evemanutool.data.cache.PriceEntry.PriceType;
import evemanutool.data.cache.WalletTransactionEntry;
import evemanutool.data.database.Blueprint;
import evemanutool.data.database.Item;
import evemanutool.data.database.ManuQuote;
import evemanutool.data.database.Material;
import evemanutool.data.display.Asset;
import evemanutool.data.display.CorpProductionQuote;
import evemanutool.data.display.MarketOrder;
import evemanutool.data.display.Supply;
import evemanutool.user.Preferences;
import evemanutool.user.Preferences.BlueprintStat;
import evemanutool.utils.databases.BlueprintDB;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.databases.ItemDB;
import evemanutool.utils.databases.PriceDB;
import evemanutool.utils.databases.TechDB;

public class ProductionCalculator implements UserPrefConstants, DBConstants {
	
	//Constants.
	private static final long MILLIS_IN_MONTH = 2419200000L; //4 weeks

	public static void updateProductionQuote(CorpProductionQuote q) {
		
		//Updates user affected values.
		int needToProduce = q.getSellTarget() - q.getOnSale() - q.getInProduction() - q.getStock(); //Set to 0 if over-stocked (negative). 
		
		q.setNeedToProduce(needToProduce > 0 ? needToProduce : 0);
	}

	public static CorpProductionQuote calculateProductionQuoteFromQuote(ManuQuote quote,  BlueprintDB bdb, PriceDB pdb, ItemDB idb, TechDB tdb,
			Preferences prefs, ArrayList<Asset> hangarAssets, ArrayList<ApiIndustryJob> industryJobs, ArrayList<MarketOrder> sellOrders,
			ArrayList<WalletTransactionEntry> walletTransactions) {
		
		//Temporary variables.
		ArrayList<ManuQuote> matQuotes = new ArrayList<>();
		
		//Create produced-material quotes.
		for (Material m : quote.getMatList()) {
			if (m.isProduced()) {
				matQuotes.add(QuoteCalculator.calculateQuote(bdb.getByProductId(m.getItem().getTypeId()), (int) m.getAmount(), null, pdb, bdb, prefs,
						quote.getPrio()));
			}
		}
		
		//Add new values to trends.		
		//Price trend
		ArrayList<NumberTrendEntry> priceList = new ArrayList<>();
		priceList.add(new NumberTrendEntry(new Date(),
				MarketCalculator.calculatePrice(Action.SELL, quote.getBpo().getProduct().getTypeId(),  pdb, prefs)));
		
		//Volume trend.
		ArrayList<NumberTrendEntry> volumeList = new ArrayList<>();
		volumeList.add(new NumberTrendEntry(new Date(), 
				pdb.getSellMI(quote.getBpo().getProduct().getTypeId()).getPrice(OrderAim.SELL).getValue(PriceType.VOLUME)));
		
		//Calculate production numbers.
		int stock = calculateItemStock(hangarAssets, quote.getBpo().getProduct().getTypeId());
		int onSale = calculateItemOnSale(sellOrders, quote.getBpo().getProduct().getTypeId());
		int inProduction = calculateItemInProduction(industryJobs, quote.getBpo());	
		
		return new CorpProductionQuote(quote, calculateAvgWeekTradeVolume(volumeList),
				calculateAvgWeekSoldAmount(quote.getBpo().getProduct(), walletTransactions), 
				priceList, volumeList, true, stock, onSale, inProduction, 0, 0);
	}

	public static CorpProductionQuote calculateProductionQuoteFromRaw(CorpProductionEntry cMQE, BlueprintDB bdb, PriceDB pdb, ItemDB idb, TechDB tdb,
			Preferences prefs, ArrayList<Asset> hangarAssets, ArrayList<ApiIndustryJob> industryJobs, ArrayList<MarketOrder> sellOrders,
			ArrayList<WalletTransactionEntry> walletTransactions) {

		//Temporary variables.
		ManuQuote quote;
		ManuQuote oldQuote;
		Blueprint b = bdb.getByBlueprintId(cMQE.getBpoTypeId());
		ArrayList<Material> matList = new ArrayList<>();
		ArrayList<ManuQuote> matQuotes = new ArrayList<>();
		
		//Setup an oldQuote to get the right material acquires.
		//Initiate sub parts.
		for (Integer typeId : cMQE.getManuMaterials()) {
			matList.add(new Material(idb.getItem(typeId), 1));
		}
		oldQuote = new ManuQuote(null, null, 0, 0, 0, 0, 0, 0, matList, null, null);
		
		//Create main quote.
		b.setMe(prefs.getBlueprintStat(BlueprintStat.MOD_ME));
		b.setPe(prefs.getBlueprintStat(BlueprintStat.MOD_PE));
		
		quote = QuoteCalculator.calculateGenericQuote(b , 1, oldQuote, pdb, idb, bdb, tdb, prefs,
				MAT_ACQUIRE_PRIO_ENUM[cMQE.getMatIndex()], INV_PRIO_ENUM[cMQE.getSpecIndex()], REV_PRIO_ENUM[cMQE.getSpecIndex()]);
		
		//Create produced-material quotes.
		for (Material m : quote.getMatList()) {
			if (m.isProduced()) {
				matQuotes.add(QuoteCalculator.calculateQuote(bdb.getByProductId(m.getItem().getTypeId()), (int) m.getAmount(), null, pdb, bdb, prefs,
						MAT_ACQUIRE_PRIO_ENUM[cMQE.getMatIndex()]));
			}
		}
		
		//Add new values to trends.
		ArrayList<NumberTrendEntry> nList;
		
		//Price trend
		nList = cMQE.getMarketTrend();
		Collections.sort(nList);
		
		//Make sure that unnecessary entries are removed.
		if (nList.size() >= 2) {
			if (nList.get(0).getDate().getTime() - nList.get(1).getDate().getTime() < CorpApiDB.TRADE_UPDATE_INTERVAL * 3600000) {
				nList.remove(0);
			}
		}
		nList.add(new NumberTrendEntry(new Date(),
				MarketCalculator.calculatePrice(Action.SELL, quote.getBpo().getProduct().getTypeId(),  pdb, prefs)));
		
		//Volume trend.
		nList = cMQE.getVolumeTrend();
		Collections.sort(nList);
		
		//Make sure that unnecessary entries are removed.
		if (nList.size() >= 2) {
			if (nList.get(0).getDate().getTime() - nList.get(1).getDate().getTime() < CorpApiDB.TRADE_UPDATE_INTERVAL * 3600000) {
				nList.remove(0);
			}
		}
		nList.add(new NumberTrendEntry(new Date(), 
				pdb.getSellMI(quote.getBpo().getProduct().getTypeId()).getPrice(OrderAim.SELL).getValue(PriceType.VOLUME)));
		
		//Calculate production numbers.
		int stock = calculateItemStock(hangarAssets, quote.getBpo().getProduct().getTypeId());
		int onSale = calculateItemOnSale(sellOrders, quote.getBpo().getProduct().getTypeId());
		int inProduction = calculateItemInProduction(industryJobs, quote.getBpo());	
		int needToProduce = cMQE.getSellTarget() - onSale - inProduction - stock; //Set to 0 if over-stocked (negative). 
		
		return new CorpProductionQuote(quote, calculateAvgWeekTradeVolume(cMQE.getVolumeTrend()),
				calculateAvgWeekSoldAmount(quote.getBpo().getProduct(), walletTransactions), 
				cMQE.getMarketTrend(), cMQE.getVolumeTrend(), true, stock, onSale, inProduction,
				needToProduce > 0 ? needToProduce : 0, cMQE.getSellTarget());
	}

	public static double calculateMaterialCoverage(Material m, long needed,
			ArrayList<Supply> supplies) {
		//Calculates the percentage as a double where 0 <= ans <= 1.
		double stock = 0, req = 0;
		for (Material m1 : m.getManufactureQuote().getMatList()) {
			//Add required stock.
			req += m1.getAmount() * needed;
			for (Supply supply : supplies) {
				if (supply.getItem().getTypeId() == m.getItem().getTypeId()) {
					//Add owned stock but only as much as is required, not more.
					stock += supply.getStock() > m1.getAmount() * needed ? m1.getAmount() * needed : supply.getStock();
				}
			}
		}
		return stock / req;
	}

	private static int calculateAvgWeekSoldAmount(Item item,
			ArrayList<WalletTransactionEntry> walletTransactions) {
		int amount = 0;
		//Earliest record included in calculations.
		Date earliestRecord = new Date();
		
		for (WalletTransactionEntry wT : walletTransactions) {
			if (wT.getTypeId() == item.getTypeId()) {
				if (wT.getDate().getTime() - new Date().getTime() < MILLIS_IN_MONTH) {
					if (wT.getDate().before(earliestRecord)) {
						//Sets the date if earlier.
						earliestRecord = wT.getDate();
					}
					amount += wT.getQuantity();
				}
			}
		}
		//Calculate the number of weeks data span over.
		double weeks = (earliestRecord.getTime() - new Date().getTime()) * 4 / MILLIS_IN_MONTH;
		
		return (int) (weeks != 0 ? (amount / weeks) + 0.5 : 0);
	}

	private static int calculateAvgWeekTradeVolume(Collection<NumberTrendEntry> l) {
		//Calculates the average.
		//Will calculate the average of all collected data points i.e (the daily average when the user starts the program).
		long volume = 0;
		for (NumberTrendEntry nTE : l) {
			volume += nTE.getNumber();
		}
		return (int) (volume / l.size() + 0.5);
	}

	private static int calculateItemInProduction(ArrayList<ApiIndustryJob> industryJobs, Blueprint b) {
		int count = 0;
		//Count the number of the given type in the assets.
		for (ApiIndustryJob aIJ : industryJobs) {
			if (aIJ.getInstalledItemTypeID() == b.getBlueprintItem().getTypeId()) {
				count += aIJ.getRuns() * b.getProduct().getPortionSize();
			}
		}
		return count;
	}

	private static int calculateItemOnSale(ArrayList<MarketOrder> sellOrders, int typeId) {
		int count = 0;
		//Count the number of the given type in the assets.
		for (MarketOrder mO : sellOrders) {
			if (mO.getItem().getTypeId() == typeId) {
				count += mO.getMarketOrder().getVolRemaining();
			}
		}
		return count;
	}

	private static int calculateItemStock(ArrayList<Asset> hangarAssets, int typeId) {
		int count = 0;
		//Count the number of the given type in the assets.
		for (Asset a : hangarAssets) {
			if (a.getItem().getTypeId() == typeId) {
				count += a.getQuantity();
			}
		}
		return count;
	}
}
