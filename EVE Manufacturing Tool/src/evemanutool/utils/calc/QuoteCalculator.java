package evemanutool.utils.calc;

import java.util.ArrayList;
import java.util.List;

import evemanutool.constants.DBConstants;
import evemanutool.constants.UserPrefConstants;
import evemanutool.data.cache.TradeEntry;
import evemanutool.data.cache.TradeHistoryEntry;
import evemanutool.data.database.Blueprint;
import evemanutool.data.database.Decryptor;
import evemanutool.data.database.Invention;
import evemanutool.data.database.Item;
import evemanutool.data.database.ManuQuote;
import evemanutool.data.database.Material;
import evemanutool.data.database.Relic;
import evemanutool.data.database.ReverseEngineering;
import evemanutool.data.general.Time;
import evemanutool.prefs.Preferences;
import evemanutool.prefs.Preferences.BlueprintStat;
import evemanutool.prefs.Preferences.ImplantMod;
import evemanutool.prefs.Preferences.InstallationMod;
import evemanutool.prefs.Preferences.ManufacturingCost;
import evemanutool.prefs.Preferences.Skill;
import evemanutool.utils.databases.BlueprintDB;
import evemanutool.utils.databases.ItemDB;
import evemanutool.utils.databases.PriceDB;
import evemanutool.utils.databases.TechDB;

public class QuoteCalculator implements UserPrefConstants, DBConstants{
	
	public static ArrayList<ManuQuote> calculateGenericQuotes(List<Blueprint> l, int runs, PriceDB pdb, ItemDB idb, BlueprintDB bdb, TechDB tdb, Preferences prefs,
			MatAcquirePriority mPrio, InvPriority iPrio, RevPriority rPrio) {
	
		//Calculate quotes but choose calculation method. 
		ArrayList<ManuQuote> ans = new ArrayList<>();
		for (Blueprint b : l) {
			//Set choice to null since it's N/A for more than one.
			ans.add(QuoteCalculator.calculateGenericQuote(b, runs, null, pdb, idb, bdb, tdb, prefs, mPrio, iPrio, rPrio));
		}
		return ans;
	}

	public static ManuQuote calculateGenericQuote(Blueprint b, int runs,
			ManuQuote oldQuote, PriceDB pdb, ItemDB idb, BlueprintDB bdb, TechDB tdb,
			Preferences prefs, MatAcquirePriority mPrio, InvPriority iPrio, RevPriority rPrio) {
		
		if (tdb.canBeInvented(b, bdb)) {
			return calculateInventionQuote(b, runs, oldQuote, pdb, tdb, bdb, prefs, iPrio, mPrio);
		}else if (tdb.canBeReverseEngineered(b)) {
			return calculateReverseEngineeringQuote(b, runs, oldQuote, pdb, bdb, idb, prefs, rPrio, mPrio);
		}
		return calculateQuote(b, runs, oldQuote, pdb, bdb, prefs, mPrio);
	}

	public static ArrayList<ManuQuote> calculateQuotes(List<Blueprint> l, int runs, PriceDB pdb, BlueprintDB bdb, Preferences prefs, MatAcquirePriority mPrio) {

		ArrayList<ManuQuote> ans = new ArrayList<>();
		for (Blueprint b : l) {
			//Set choice to null since it's N/A for more than one.
			ans.add(QuoteCalculator.calculateQuote(b, runs, null, pdb, bdb, prefs, mPrio));
		}
		return ans;
	}
	
	public static ManuQuote calculateQuote(Blueprint b, int runs, ManuQuote oldQuote, PriceDB pdb, BlueprintDB bdb, Preferences prefs, MatAcquirePriority mPrio) {
		
		//---Skills and modifiers---
		//Apply skills, implant and slotModifier.
		double time = b.getManuTime() * (1 - (0.04 * SKILL_LEVEL_VALUE[prefs.getSkillLvlIndex(Skill.INDUSTRY)])) 
					* MOD_IMPLANT_VALUE[prefs.getImplantModIndex(ImplantMod.MOD_PE)]
					* MOD_SLOT_VALUE[prefs.getInstallationModIndex(InstallationMod.SLOT_MOD_PE)];
		
		//Finalize time calculation.
		if ((b.getProductivityMod() / ((double) b.getManuTime())) <= 1) {
			if (b.getPe() >= 0) {
				
				time *= (1 - ((b.getProductivityMod() / ((double) b.getManuTime())) * (b.getPe() / ((double) (1 + b.getPe()))))); 
			}else {
				
				time *= (1 - ((b.getProductivityMod() / ((double) b.getManuTime())) * (b.getPe() - 1))); 
			}
		}
			
		double cost = 0, income = 0;
		//Base material list.
		ArrayList<Material> bML = new ArrayList<>();
		//Extra material list.
		ArrayList<Material> eML = new ArrayList<>();
		//Extra material recycle list.
		ArrayList<Material> eMRL = new ArrayList<>();
		double amount;
		double afterRecycleAmount;
		
		//Divide the extra materials.
		for (Material m : b.getExtraMaterials()) {

			//Check if base materials already has a entry for the material.
			if (m.isRecycled()) {
				//Add to subtraction-list if item is recycled.
				for (Material m1 : m.getItem().getBaseMaterials()) {
					eMRL.add(new Material(m1.getItem(), m1.getAmount() * runs));
				}
			}
			//Add skill waste.
			eML.add(new Material(m.getItem(), (m.getAmount() + 
					(int) ((((25 - (5 * SKILL_LEVEL_VALUE[prefs.getSkillLvlIndex(Skill.PRODUCTION_EFFICIENCY)])) * m.getAmount()) / 100) + 0.5)) * runs));
		}
		
		//Calculate the correct amount of base materials including, skill and Me-based waste.
		for (Material m : b.getProduct().getBaseMaterials()) {
			
			afterRecycleAmount = m.getAmount();
			
			//Remove recycled amount.
			if (eMRL.contains(m)) {
				afterRecycleAmount -= eMRL.get(eMRL.indexOf(m)).getAmount();
				//Material amount can't be lower than 0.
				if (afterRecycleAmount < 0) {
					//Don't add the material, recycle has covered the amount.
					continue;
				}
			}
			
			//Set base.
			amount = afterRecycleAmount;

			//Add skill waste.
			amount += (int) ((((25 - (5 * SKILL_LEVEL_VALUE[prefs.getSkillLvlIndex(Skill.PRODUCTION_EFFICIENCY)])) * afterRecycleAmount) / 100) + 0.5); 
			
			//Add ME waste.
			if (b.getMe() >= 0) {
				amount += (int) ((afterRecycleAmount * ((b.getWasteFactor()) / 100.0) * (1 / (double) (b.getMe() + 1))) + 0.5);
				
			}else {
				amount += (int) ((afterRecycleAmount * ((b.getWasteFactor()) / 100.0) * (1 - b.getMe())) + 0.5);
			}
			
			//Apply slot modifier.
			amount *= MOD_SLOT_VALUE[prefs.getInstallationModIndex(InstallationMod.SLOT_MOD_ME)];
			bML.add(new Material(m.getItem(), (amount * runs)));
		}
		
		//Add extra materials to base-list.
		for (Material m : eML) {
			if (bML.contains(m)) {
				bML.get(bML.indexOf(m)).setAmount(bML.get(bML.indexOf(m)).getAmount() + (m.getAmount() * runs));
			}else {		
				bML.add(new Material(m.getItem(), m.getAmount() * runs));
			}
		}
		
		//---PriceChecks---
		//Calculate the cost for the Materials.
		for (Material m : bML) {
			m.setPrice(MarketCalculator.calculatePrice(Action.BUY, m.getItem().getTypeId(), pdb, prefs));
		}
		
		//Do pre calculations of cost to decide how to acquire materials.
		for (Material m : bML) {
			cost += m.getPrice() * m.getAmount(); 
		}
		//Installation cost.
		cost += prefs.getManufacturingCost(ManufacturingCost.INSTALLATION_COST) 
				+ time * prefs.getManufacturingCost(ManufacturingCost.INSTALLATION_COST_H) / 3600;
		
		//Income.
		income = b.getItemsPerRun() * runs * MarketCalculator.calculatePrice(Action.SELL, b.getProduct().getTypeId(), pdb, prefs);
		
		Blueprint tempB;
		//Add special manufacturing prices.
		for (Material m : bML) {
			//Add price to manufacture if applicable, otherwise add null.
			tempB = bdb.getByProductId(m.getItem().getTypeId());
			if (tempB != null) {
				//Set default me/pe levels.
				tempB.setMe(prefs.getBlueprintStat(BlueprintStat.MOD_ME));
				tempB.setPe(prefs.getBlueprintStat(BlueprintStat.MOD_PE));
				m.setManufactureQuote(calculateQuote(tempB, 1, null, pdb, bdb, prefs, mPrio));
				m.setCanBeManufactured(true);
			}else {
				m.setCanBeManufactured(false);
				m.setProduced(false);
				//Leave ManufactureQuote to null.
			}
		}
		
		//Set which prices to use, checks the values from the "oldQuote".
		if (oldQuote != null) {
			for (Material m1 : bML) {
				for (Material m : oldQuote.getMatList()) {
					if (m1.getItem().equals(m.getItem())) {
						m1.setProduced(m.isProduced());
						break;
					}
				}
			}
		}
		
		//Set the values that were not set.
		for (Material m : bML) {
			if (m.isProduced() == null) {
				//Overrides priorities, if material can't be bought (price = 0), always produce.
				if (m.getPrice() == 0) {
					m.setProduced(true);
				} else if (mPrio == MatAcquirePriority.PROFIT_MARGIN && 
						m.getManufactureQuote().getManuCost() < m.getPrice()) {
					//Set to true if the profit increases by manufacturing the material. 
					m.setProduced(true);
				}else if (mPrio == MatAcquirePriority.PROFIT_PER_H_CHAIN &&
						((income - cost) * 3600) / (time * runs) <  ((m.getPrice() - m.getManufactureQuote().getManuCost()) 
																/ m.getManufactureQuote().getManuTime().toHours())) {
					//Set to true if the profit/h increases by manufacturing the material in regard to the time it takes. 
					m.setProduced(true);
				}else {
					m.setProduced(false);
				}
			}
		}
		
		//Reset and re-do calculations.
		cost = 0;
		for (Material m : bML) {
			if (m.isProduced()) {
				cost += m.getManufactureQuote().getManuCost() * m.getAmount();
			}else {
				cost += m.getPrice() * m.getAmount(); 
			}
		}
		//Installation cost.
		cost += prefs.getManufacturingCost(ManufacturingCost.INSTALLATION_COST) 
				+ time * prefs.getManufacturingCost(ManufacturingCost.INSTALLATION_COST_H) / 3600;
		
		//Set the T1 BPO market availability.
		boolean seededOnMarket = MarketCalculator.isItemAvailable(b.getBlueprintItem(), pdb, prefs);
		
		//Create quote.
		ManuQuote ans = new ManuQuote(	b, 
				new Time((int) (time + 0.5)), 
				cost, 
				income, 
				income - cost,
				((income - cost) * 3600) / (time * runs), 
				calculateSalesVolumeRatio(b, pdb, time),
				runs, bML, mPrio,
				MarketCalculator.calculateMarketTrend(Action.SELL, b.getProduct(), pdb, prefs), 
				seededOnMarket);
		
		//Set sustainableProfitValue.
		ans.setSustainableProfitValue(calculateSustainableProfitValue(ans));
		
		return ans;
	}

	public static ArrayList<ManuQuote> calculateInventionQuotes(List<Blueprint> l, int runs, PriceDB pdb, TechDB tdb, BlueprintDB bdb, 
															Preferences prefs, InvPriority iPrio, MatAcquirePriority mPrio) {
		ManuQuote tmp;
		ArrayList<ManuQuote> ans = new ArrayList<>();
		for (Blueprint b : l) {
			//Set choice to null since it's N/A for more than one.
			tmp = QuoteCalculator.calculateInventionQuote(b, runs, null, pdb, tdb, bdb, prefs, iPrio, mPrio);
			//Only include if valid.
			if (tmp != null) {
				ans.add(tmp);
			}
		}
		return ans;
	}

	public static ManuQuote calculateInventionQuote(Blueprint t2Bpo, int runs, ManuQuote oldQuote, PriceDB pdb, TechDB tdb, BlueprintDB bdb, 
												Preferences prefs, InvPriority iPrio, MatAcquirePriority mPrio) {

		//Corresponding T1 blueprint.
		Blueprint t1Bpo = bdb.getByProductId(tdb.getParentId(t2Bpo.getProduct()));
		Blueprint cleanT2Bpo = bdb.getByBlueprintId(t2Bpo.getBlueprintItem().getTypeId());
		
		//Variations.
		ArrayList<Decryptor> dL = t1Bpo.getInvDecryptors();
		dL.add(null); //No decryptor.
		ArrayList<Item> iL = tdb.getT1Items(t1Bpo.getProduct().getTypeId());
		iL.add(null); //No item.
		int[] rL = {1, t1Bpo.getMaxRuns()};
		
		//Combination list.
		ArrayList<ManuQuote> qL = new ArrayList<>();
		ManuQuote tmp = null;
		
		for (int t1BpcRuns : rL) {
			for (Item metaItem : iL) {
				for (Decryptor dec : dL) {
					
					//Only add quote if decryptor is available.
					if (dec == null || MarketCalculator.isItemAvailable(dec.getDecryptor(), pdb, prefs)) {
						
						//Get quote.
						tmp = calculateQuote(calculateInventionBPC(t1Bpo, cleanT2Bpo, metaItem, t1BpcRuns, dec, prefs), runs, oldQuote, pdb, bdb, prefs, mPrio);
						tmp.setInv(new Invention(calculateInventionChance(t1Bpo, metaItem, dec, prefs), 
								dec, metaItem, t1BpcRuns,
								new Time((int) (((t1Bpo.getCopyTime() * t1BpcRuns / (double) t1Bpo.getMaxRuns()) + 0.5) * 
										MOD_COPY_VALUE[prefs.getInstallationModIndex(InstallationMod.SLOT_MOD_COPY)])), //Divide copyTime with number of max-runs.
								calculateInventionBPCRuns(t1Bpo, cleanT2Bpo, t1BpcRuns, dec), 
								new Time(0, 0, 0, calculateInventionTime(t1Bpo, prefs)), iPrio));
						
						qL.add(tmp);
					}
				}
			}
		}
		
		//Pick the best Quote according to priority.
		for (ManuQuote q : qL) {
			if (iPrio == InvPriority.PROFIT_MARGIN && q.getProfit() > tmp.getProfit()) {
				tmp = q;
			} else if (iPrio == InvPriority.PROFIT_PER_H && q.getProfitPerHour() > tmp.getProfitPerHour()) {
				tmp = q;
			} else if (iPrio == InvPriority.PROFIT_PER_COPY_H && 
						q.getProfit() / (q.getInv().getCopyTime().toHours() * q.getInv().getT2BpcRuns() * q.getInv().getSuccessRate()) > 
						tmp.getProfit() / (tmp.getInv().getCopyTime().toHours() * tmp.getInv().getT2BpcRuns() * tmp.getInv().getSuccessRate())) {
				tmp = q;
			} else if (iPrio == InvPriority.PROFIT_SUSTAINED && 
					q.getSustainableProfitValue() > tmp.getSustainableProfitValue()) {
				tmp = q;
			}
		}
		//Set the T1 BPO market availability.
		tmp.setBaseBPOSeededOnMarket(MarketCalculator.isItemAvailable(t1Bpo.getBlueprintItem(), pdb, prefs));
		
		return tmp;
	}
	
	public static ArrayList<ManuQuote> calculateReverseEngineeringQuotes(ArrayList<Blueprint> l, int runs, PriceDB pdb,
																		BlueprintDB bdb, ItemDB idb, Preferences prefs, 
																		RevPriority rPrio, MatAcquirePriority mPrio) {
		ArrayList<ManuQuote> ans = new ArrayList<>();
		ManuQuote tmp;
		for (Blueprint b : l) {
			//Set choice to null since it's N/A for more than one.
			tmp = QuoteCalculator.calculateReverseEngineeringQuote(b, runs, null, pdb, bdb, idb, prefs, rPrio, mPrio);
			//Only include if valid.
			if (tmp != null) {
				ans.add(tmp);
			}
		}
		return ans;
	}
	
	public static ManuQuote calculateReverseEngineeringQuote(Blueprint t3Bpo, int runs, ManuQuote oldQuote, PriceDB pdb,
																		BlueprintDB bdb, ItemDB idb, Preferences prefs, RevPriority rPrio,
																		MatAcquirePriority mPrio) {
		//Combination list.
		ArrayList<ManuQuote> qL = new ArrayList<>();		
		ManuQuote tmp = null;
		Blueprint cleanT3Bpo = bdb.getByBlueprintId(t3Bpo.getBlueprintItem().getTypeId());
		
		for (Relic rel : cleanT3Bpo.getRevRelics()) {
			
			//Only add quote if relic is available.
			if (MarketCalculator.isItemAvailable(rel.getRelic(), pdb, prefs)) {
				tmp = calculateQuote(calculateReverseEngineeringBPC(cleanT3Bpo, rel, idb, prefs), runs, oldQuote, pdb, bdb, prefs, mPrio);
				tmp.setRev(new ReverseEngineering(calculateReverseEngineeringChance(cleanT3Bpo, rel, idb, prefs),
						cleanT3Bpo.getRevDecryptor(),
						rel.getRevMaxRunModifier(), new Time(DEFAULT_REV_TIME), rPrio));
				qL.add(tmp);
			}
		}
				
		//Pick the best Quote according to priority.
		for (ManuQuote q : qL) {
			if (rPrio == RevPriority.PROFIT_MARGIN && q.getProfit() > tmp.getProfit()) {
				tmp = q;
			} else if (rPrio == RevPriority.PROFIT_PER_REV_H && 
						q.getProfit() / (q.getRev().getRevTime().toHours() * q.getRev().getBpcRuns() * q.getRev().getSuccessRate()) >
						tmp.getProfit() / (tmp.getRev().getRevTime().toHours() * tmp.getRev().getBpcRuns() * tmp.getRev().getSuccessRate())) {
				tmp = q;
			}
		}
		return tmp;
	}
	
	private static double calculateSalesVolumeRatio(Blueprint b, PriceDB pdb, double manuTime) {
		
		//Calculate sales volume ratio. (Sales volume average / produced products; one slot in 24h).
		TradeHistoryEntry tHE = pdb.getSellTH(b.getProduct().getTypeId());
		double svr = 0;
		if (!tHE.getHistory().isEmpty()) {
			for (TradeEntry pHE : tHE.getHistory()) {
				svr += pHE.getVolume();
			}
			svr /= tHE.getHistory().size();
			svr /= ((24 * 3600 * b.getProduct().getPortionSize()) / (double) manuTime);
		}
		return svr;
	}

	private static double calculateSustainableProfitValue(ManuQuote q) {
		
		double ans = 1;
		//Profit/h factor (Limit lowest Profit/h at 0).
		ans *= (q.getProfitPerHour() < 0 ? 0 : q.getProfitPerHour()) / PROFIT_PER_H_SUSTAIN;
		//Manufacturing time factor (longer is better -> less player actions).
		ans *= q.getManuTime().toSeconds() / TIME_SUSTAIN;
		//SVR factor.
		ans *= q.getSalesVolumeRatio() / SVR_SUSTAIN;

		//Invention and Copy time.
		if (q.getInv() != null) {
			//Remove manufacturing time factor.
			ans /= q.getManuTime().toSeconds() / TIME_SUSTAIN;
			//Add new manufacturing time factor.
			ans *= q.getManuTime().toSeconds() * q.getInv().getT2BpcRuns() / TIME_SUSTAIN;
			
			//Invention and manufacturing time relation factor.
			ans *= q.getManuTime().toSeconds() / (q.getManuTime().toSeconds() / (q.getInv().getSuccessRate() * q.getInv().getT2BpcRuns()));
			//Copy and Invention time relation factor.
			ans *= (q.getManuTime().toSeconds() / (q.getInv().getSuccessRate() * q.getInv().getT2BpcRuns())) / q.getInv().getCopyTime().toSeconds();
		
		//Reverse engineering.
		}else if (q.getRev() != null) {
			//Remove manufacturing time factor.
			ans /= q.getManuTime().toSeconds() / TIME_SUSTAIN;
			//Add new manufacturing time factor.
			ans *= q.getManuTime().toSeconds() * q.getRev().getBpcRuns() / TIME_SUSTAIN;
			
			//Reverse engineering chance and runs factor.
			ans *= q.getRev().getSuccessRate() * q.getRev().getBpcRuns();
		}
		
		return ans;
	}

	private static int calculateInventionTime(Blueprint t1Bpo, Preferences prefs) {
		
		return (int) (t1Bpo.getInventionTime() * MOD_INV_VALUE[prefs.getInstallationModIndex(InstallationMod.SLOT_MOD_INV)] * 
				MOD_IMPLANT_VALUE[prefs.getImplantModIndex(ImplantMod.MOD_INV)] + 0.5);
	}
	
	private static int calculateInventionBPCRuns(Blueprint t1Bpo, Blueprint t2Bpo, int t1BpcRuns, Decryptor dec) {
		
		double decMod = 0;
		
		if (dec != null) {
			decMod = dec.getInvMaxRunModifier();
		}
		return Math.min(Math.max((int) (((t1BpcRuns / t1Bpo.getMaxRuns()) * (t2Bpo.getMaxRuns() / ((double) 10))) + decMod), 1), t2Bpo.getMaxRuns());
	}
	
	private static double calculateInventionChance(Blueprint t1Bpo, Item metaItem, Decryptor dec, Preferences prefs) {
		
		int metaLevel = 0;
		double decMod = 1;
		if (metaItem != null) {
			metaLevel = metaItem.getMetaLevel();			
		}
		if (dec != null) {
			decMod = dec.getInvProbMultiplier();
		}
		return t1Bpo.getBaseInventionChance() * 
				(1 + 0.01 * SKILL_LEVEL_VALUE[prefs.getSkillLvlIndex(Skill.RACIAL_ENCRYPTION)]) * 
				(1 + (SKILL_LEVEL_VALUE[prefs.getSkillLvlIndex(Skill.DATACORE_SKILLS)] * (0.2 / (5 - metaLevel)))) * decMod;
	}
	
	private static Blueprint calculateInventionBPC(Blueprint t1Bpo, Blueprint t2Bpo, Item metaItem, int copyRuns, Decryptor dec, Preferences prefs) {
		
		double amountMod = 1 / (calculateInventionBPCRuns(t1Bpo, t2Bpo, copyRuns, dec) * 
								calculateInventionChance(t1Bpo, metaItem, dec, prefs)); 
		
		//Create a blueprint with additional materials
		Blueprint b = new Blueprint(t2Bpo);
		if (dec != null) {
			//Add decryptor to materials.
			b.getExtraMaterials().add(new Material(dec.getDecryptor(), amountMod));
			b.setMe(DEFAULT_INV_MEPE_LEVEL + dec.getInvMEModifier());
			b.setPe(DEFAULT_INV_MEPE_LEVEL + dec.getInvPEModifier());
		} else {
			b.setMe(DEFAULT_INV_MEPE_LEVEL);
			b.setPe(DEFAULT_INV_MEPE_LEVEL);
		}
		
		if (metaItem != null) {
			//Add metaItem to materials.
			b.getExtraMaterials().add(new Material(metaItem, amountMod));
		}
		
		//Add datacores and interface.
		for (Material m : t1Bpo.getInvMaterials()) {
			b.getExtraMaterials().add(new Material(m.getItem(), m.getAmount() * amountMod));
		}
		
		return b;
	}

	private static Blueprint calculateReverseEngineeringBPC(Blueprint t3Bpo, Relic rel, ItemDB idb, Preferences prefs) {
		
		double amountMod = 1 / (rel.getRevMaxRunModifier() * calculateReverseEngineeringChance(t3Bpo, rel, idb, prefs));
		
		Blueprint b = new Blueprint(t3Bpo);
		//Set Me/Pe.
		b.setMe(DEFAULT_REV_MEPE_LEVEL);
		b.setPe(DEFAULT_REV_MEPE_LEVEL);
		
		//Add materials.
		b.getExtraMaterials().add(new Material(rel.getRelic(), amountMod));
		b.getExtraMaterials().add(new Material(t3Bpo.getRevDecryptor(), amountMod));
		
		for (Material m : rel.getRevMaterials()) {
			b.getExtraMaterials().add(new Material(m.getItem(), m.getAmount() * amountMod));
		}
		return b;
	}

	private static double calculateReverseEngineeringChance(Blueprint t3Bpo, Relic rel, ItemDB idb, Preferences prefs) {
		return (1 / ((double) idb.getItemsInMarketGroup(t3Bpo.getProduct().getMarketGroup()).size())) //Divides by the number of outcomes.
				* rel.getBaseChance()
				* (1 + (0.01 * SKILL_LEVEL_VALUE[prefs.getSkillLvlIndex(Skill.REVERSE_ENGINEERING)]))
				* (1 + (0.2 * SKILL_LEVEL_VALUE[prefs.getSkillLvlIndex(Skill.DATACORE_SKILLS)]));
	}
}
