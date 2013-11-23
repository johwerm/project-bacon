package evemanutool.utils.calc;

import java.util.ArrayList;

import evemanutool.constants.DBConstants;
import evemanutool.data.database.Item;
import evemanutool.data.database.Material;
import evemanutool.data.display.MiningQuote;
import evemanutool.user.Preferences;
import evemanutool.utils.databases.PriceDB;

public class MiningCalculator implements DBConstants {

	public static ArrayList<MiningQuote> calculateMiningQuotes( Preferences prefs, PriceDB pdb, ArrayList<Item> items, 
			double yieldPerCycle, int cycleTime, int laserCount) {
		
		//Returns a list of calculated miningQuotes.
		ArrayList<MiningQuote> ans = new ArrayList<>();
		for (Item ore : items) {
			ans.add(calculateMiningQuote(prefs, pdb, ore, yieldPerCycle, cycleTime, laserCount));
		}
		return ans;
	}

	public static MiningQuote calculateMiningQuote(Preferences prefs, PriceDB pdb, Item ore,
			double yieldPerCycle, int cycleTime, int laserCount) { 
		
		double incomePerUnit = 0, incomePerM3 = 0;
		
		//Set the individual prices for the minerals and add to income for one batch refine (portionSize).
		for (Material mineral : ore.getBaseMaterials()) {
			mineral.setPrice(MarketCalculator.calculatePrice(Action.SELL, mineral.getItem().getTypeId(), pdb, prefs));
			incomePerUnit += mineral.getPrice() * mineral.getAmount();
		}
		
		//Divide by refine size.
		incomePerUnit /= ore.getPortionSize();
		
		//Calculate the income per volume (M3).
		incomePerM3 = incomePerUnit / ore.getVolume();
		
		//The number of units of ore from one cycle of all lasers), since #units are truncated each cycle.
		int unitsPerCycle = ((int) (yieldPerCycle / ore.getVolume())) * laserCount;
		
		return new MiningQuote(ore, unitsPerCycle * incomePerUnit * (3600 / cycleTime), incomePerM3);
	}
}
