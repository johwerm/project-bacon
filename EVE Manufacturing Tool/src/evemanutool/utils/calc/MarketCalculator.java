package evemanutool.utils.calc;

import java.util.ArrayList;
import java.util.Date;

import evemanutool.constants.DBConstants;
import evemanutool.constants.UserPrefConstants;
import evemanutool.data.cache.MarketInfoEntry;
import evemanutool.data.cache.TradeEntry;
import evemanutool.data.cache.TradeHistoryEntry;
import evemanutool.data.database.Item;
import evemanutool.prefs.Preferences;
import evemanutool.prefs.Preferences.MarketAction;
import evemanutool.prefs.Preferences.MarketPriceType;
import evemanutool.prefs.Preferences.MarketTax;
import evemanutool.utils.databases.PriceDB;

public class MarketCalculator implements UserPrefConstants, DBConstants {
	
	public static double calculatePrice(Action a, int typeId, PriceDB pdb, Preferences prefs) {
		//Returns the cost/income of acquiring/selling an item including taxes.
		double val = 0;
		MarketInfoEntry mI = null;

		if (a == Action.BUY) {
			//Buy Action.
			//Set MarketInfo and return 0 if not valid.
			mI = pdb.getBuyMI(typeId);
			if (mI == null) {
				return val;
			}
			val = mI.getPrice(MARKET_AIM_ENUM[prefs.getMarketOrderAimIndex(MarketAction.BUY_ACTION)])
					.getValue(MARKET_PRICE_ENUM[prefs.getMarketPriceTypeIndex(MarketPriceType.BUY_TYPE)]);
			
			//Add broker.
			val += val * prefs.getMarketTax(MarketTax.BROKER_FEE) / 100;
			
		}else if (a == Action.SELL){
			//Sell Action.
			//Set MarketInfo and return 0 if not valid.
			mI = pdb.getSellMI(typeId);
			if (mI == null) {
				return val;
			}
			val = mI.getPrice(MARKET_AIM_ENUM[prefs.getMarketOrderAimIndex(MarketAction.SELL_ACTION)])
					.getValue(MARKET_PRICE_ENUM[prefs.getMarketPriceTypeIndex(MarketPriceType.SELL_TYPE)]);
			
			//Add sales and broker.
			val -= val * (prefs.getMarketTax(MarketTax.SALES_TAX) + prefs.getMarketTax(MarketTax.BROKER_FEE)) / 100;
		}
		return val;
	}
	
	public static boolean isItemAvailable(Item i, PriceDB pdb, Preferences prefs) {
		//Determine if BPO is seeded.
		if(i.isOnMarket()) {
			ArrayList<TradeEntry> tEL = pdb.getBuyTH(i.getTypeId()).getHistory();
			TradeEntry tE;

			return (!tEL.isEmpty() && (tE = tEL.get(0)) != null && 
					(tE.getVolume() > 0 || tE.getOrders() > 0) && 
					MarketCalculator.calculatePrice(Action.BUY, i.getTypeId(), pdb, prefs) > 0);
		}
		return false;
	}

	public static Trend calculateMarketTrend(Action a, Item i, PriceDB pdb, Preferences prefs) {
		//Determine the general market trend.
		TradeHistoryEntry tHE ;
		if (a == Action.SELL) {
			tHE = pdb.getSellTH(i.getTypeId());
		} else {
			tHE = pdb.getBuyTH(i.getTypeId());
		}

		if(i.isOnMarket() && tHE.getHistory().size() > MINIMUM_HISTORY_SIZE) {
			
			//5 day average.
			double avg5 = calculateTimedMarketAverage(tHE, 5);
			//10 day average.
			double avg10 = calculateTimedMarketAverage(tHE, 10);
			//15 day average.
			double avg15 = calculateTimedMarketAverage(tHE, 15);
			//Full average, (PriceDB.HISTORY_MAX_DAYS).
			double avgFull = calculateTimedMarketAverage(tHE, -1);
			
			//Check for holes in the data.
			if (avg5 == 0 || avg10 == 0 || avg15 == 0 || avgFull == 0) {
				return Trend.INCONCLUSIVE;
			}
			
			if ((avg15 / avgFull) - 1 > SIGNIFICANT_DIFF) {
				//Rising average.
				if ((avg5 / avg15) - 1 > SIGNIFICANT_DIFF) {
					//Rising average.
					return Trend.RISING;
				} else if ((avg5 / avg15) - 1 < -SIGNIFICANT_DIFF) {
					//Falling average.
					return Trend.UNSTABLE;
				}else {
					//Stable average.
					return Trend.RISING;
				}
			} else if ((avg15 / avgFull) - 1 < -SIGNIFICANT_DIFF) {
				//Falling average.
				if ((avg5 / avg15) - 1 > SIGNIFICANT_DIFF) {
					//Rising average.
					return Trend.UNSTABLE;
				} else if ((avg5 / avg15) - 1 < -SIGNIFICANT_DIFF) {
					//Falling average.
					return Trend.FALLING;
				}else {
					//Stable average.
					return Trend.FALLING;
				}
			}else {
				//Stable average.
				if ((avg5 / avg15) - 1 > SIGNIFICANT_DIFF) {
					//Rising average.
					return Trend.UNSTABLE;
				} else if ((avg5 / avg15) - 1 < -SIGNIFICANT_DIFF) {
					//Falling average.
					return Trend.UNSTABLE;
				}else {
					//Stable average.
					return Trend.STABLE;
				}
			}
		}
		return Trend.INCONCLUSIVE;
	}

	private static double calculateTimedMarketAverage(TradeHistoryEntry tHE, int days) {
		//Calculate the price average of the latest x days or entire history if days == -1.
		double ans = 0;
		int counter = 0;
		Date earliestAccepted = new Date(System.currentTimeMillis() - (days * 24 * 3600 * 1000));
		
		for (TradeEntry pHE : tHE.getHistory()) {
			if (pHE.getDate().after(earliestAccepted) || days == -1) {
				ans += pHE.getAvgPrice();
				counter++;
			} else {
				break;
			}
		}
		ans /= (double) counter;
		return ans;
	}
}
