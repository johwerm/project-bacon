package evemanutool.utils.calc;

import java.util.ArrayList;
import java.util.Date;

import com.beimin.eveapi.shared.industryjobs.ApiIndustryJob;

import evemanutool.constants.DBConstants;
import evemanutool.constants.UserPrefConstants;
import evemanutool.data.cache.CorpProductionEntry;
import evemanutool.data.cache.TradeEntry;
import evemanutool.data.cache.WalletTransactionEntry;
import evemanutool.data.database.Blueprint;
import evemanutool.data.database.Item;
import evemanutool.data.database.ManuQuote;
import evemanutool.data.database.Material;
import evemanutool.data.display.Asset;
import evemanutool.data.display.CorpProductionQuote;
import evemanutool.data.display.MarketOrder;
import evemanutool.data.display.Supply;
import evemanutool.prefs.Preferences;
import evemanutool.prefs.Preferences.Account;
import evemanutool.prefs.Preferences.BlueprintStat;
import evemanutool.utils.databases.BlueprintDB;
import evemanutool.utils.databases.CorpApiDB;
import evemanutool.utils.databases.ItemDB;
import evemanutool.utils.databases.PriceDB;
import evemanutool.utils.databases.TechDB;

public class ProductionCalculator implements UserPrefConstants, DBConstants {
	
	public static void updateProductionQuote(CorpProductionQuote q) {
		
		//Updates user affected values.
		int needToProduce = q.getSellTarget() - q.getOnSale() - q.getInProduction() - q.getStock(); //Set to 0 if over-stocked (negative). 
		
		q.setNeedToProduce(needToProduce > 0 ? needToProduce : 0);
	}

	public static CorpProductionQuote calculateProductionQuoteFromQuote(ManuQuote quote,  BlueprintDB bdb, 
			PriceDB pdb, CorpApiDB cdb, Preferences prefs) {
		//Temporary variables.
		ArrayList<ManuQuote> matQuotes = new ArrayList<>();
		
		//Create produced-material quotes.
		for (Material m : quote.getMatList()) {
			if (m.isProduced()) {
				matQuotes.add(QuoteCalculator.calculateQuote(bdb.getByProductId(m.getItem().getTypeId()), (int) m.getAmount(), null, pdb, bdb, prefs,
						quote.getPrio()));
			}
		}
		
		//Calculate production numbers.
		int stock = calculateItemStock(AssetCalculator.getFlatAssetsInCorpHangar(
				cdb.getFlatAssets(), prefs.getAccountIndex(Account.INDUSTRY_HANGAR)),
				quote.getBpo().getProduct().getTypeId());
		int onSale = calculateItemOnSale(cdb.getSellOrders(), quote.getBpo().getProduct().getTypeId());
		int inProduction = calculateItemInProduction(cdb.getIndustryJobs(), quote.getBpo());	
		
		return new CorpProductionQuote(quote, calculateAvgWeekTradeVolume(quote.getBpo().getProduct(), pdb),
				calculateAvgWeekCorpSoldAmount(quote.getBpo().getProduct(), cdb, prefs),
				true, stock, onSale, inProduction, 0, 0);
	}

	public static CorpProductionQuote calculateProductionQuoteFromRaw(CorpProductionEntry cMQE, BlueprintDB bdb,
			PriceDB pdb, ItemDB idb, TechDB tdb, CorpApiDB cdb, Preferences prefs) {

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
		
		//Calculate production numbers.
		int stock = calculateItemStock(AssetCalculator.getFlatAssetsInCorpHangar(
				cdb.getFlatAssets(), prefs.getAccountIndex(Account.INDUSTRY_HANGAR)),
				quote.getBpo().getProduct().getTypeId());
		int onSale = calculateItemOnSale(cdb.getSellOrders(), quote.getBpo().getProduct().getTypeId());
		int inProduction = calculateItemInProduction(cdb.getIndustryJobs(), quote.getBpo());	
		int needToProduce = cMQE.getSellTarget() - onSale - inProduction - stock; //Set to 0 if over-stocked (negative). 
		
		return new CorpProductionQuote(quote, calculateAvgWeekTradeVolume(quote.getBpo().getProduct(), pdb),
				calculateAvgWeekCorpSoldAmount(quote.getBpo().getProduct(), cdb, prefs), 
				true, stock, onSale, inProduction,
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

	private static int calculateAvgWeekCorpSoldAmount(Item item, CorpApiDB cdb, Preferences prefs) {
		
		int amount = 0;
		//Earliest record included in calculations.
		Date earliestRecord = new Date();
		Date now = new Date();
		
		for (WalletTransactionEntry wT : 
			cdb.getWalletTransactions().get(DIVISION_KEYS[prefs.getAccountIndex(Account.INDUSTRY_WALLET)])) {
			
			if (wT.getTypeId() == item.getTypeId()) {
				if (now.getTime() - wT.getDate().getTime() < MILLIS_IN_MONTH) {
					if (wT.getDate().before(earliestRecord)) {
						//Sets the date if earlier.
						earliestRecord = wT.getDate();
					}
					amount += wT.getQuantity();
				}
			}
		}
		//Calculate the number of weeks data span over.
		double weeks = (now.getTime() - earliestRecord.getTime()) / ((double) MILLIS_IN_MONTH / WEEKS_IN_MONTH);
		
		return (int) (weeks != 0 ? (amount / weeks) + 0.5 : 0);
	}

	private static int calculateAvgWeekTradeVolume(Item i, PriceDB pdb) {
		//Calculates the average sold over a week.
		long volume = 0;
		for (TradeEntry tE : pdb.getSellTH(i.getTypeId()).getHistory()) {
			volume += tE.getVolume();
		}
		return (int) (volume / ((double) pdb.getSellTH(i.getTypeId()).getHistory().size() / DAYS_IN_WEEK) + 0.5);
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
