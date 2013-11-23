package evemanutool.utils.databases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import evemanutool.constants.DBConstants;
import evemanutool.constants.UserPrefConstants;
import evemanutool.data.database.Blueprint;
import evemanutool.data.database.ManuQuote;
import evemanutool.data.display.MiningQuote;
import evemanutool.gui.main.EMT;
import evemanutool.prefs.Preferences;
import evemanutool.prefs.Preferences.BlueprintStat;
import evemanutool.prefs.Preferences.DefaultPriority;
import evemanutool.prefs.Preferences.MiningCycle;
import evemanutool.prefs.Preferences.MiningLasers;
import evemanutool.prefs.Preferences.MiningYield;
import evemanutool.utils.calc.MiningCalculator;
import evemanutool.utils.calc.QuoteCalculator;
import evemanutool.utils.datahandling.Database;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;

public class QuoteDB extends Database implements DBConstants, UserPrefConstants {
	
	//DB:s
	private PriceDB pdb;
	private BlueprintDB bdb;
	private Preferences prefs;
	private TechDB tdb;
	private ItemDB idb;
	
	//Data.
	//BPO lists.
	private volatile ArrayList<Blueprint> t1;
	private volatile ArrayList<Blueprint> inv;
	private volatile ArrayList<Blueprint> rev;
	
	//Manufacturing.
	private volatile ArrayList<ManuQuote> manuL;
	private volatile ArrayList<ManuQuote> invL;
	private volatile ArrayList<ManuQuote> revL;

	//Mining.
	private volatile ArrayList<MiningQuote> oreL;
	private volatile ArrayList<MiningQuote> iceL;

	public QuoteDB() {
		super(false, false, Stage.COMPUTE, null);
	}
	
	public void init(PriceDB pdb, BlueprintDB bdb, TechDB tdb, ItemDB idb, Preferences prefs) {
		this.pdb = pdb;
		this.bdb = bdb;
		this.tdb = tdb;
		this.idb = idb;
		this.prefs = prefs;
	}
	
	@Override
	public synchronized void processData() throws Exception {
		
		//Temporary.
		ArrayList<Blueprint> tmpT1 = new ArrayList<>();
		ArrayList<Blueprint> tmpInv = new ArrayList<>();
		ArrayList<Blueprint> tmpRev = new ArrayList<>();
		
		//Divide into groups.
		for (Blueprint b : bdb.getCompleteList()) {
			//Set default me and pe levels.
			b.setMe(prefs.getBlueprintStat(BlueprintStat.MOD_ME));
			b.setPe(prefs.getBlueprintStat(BlueprintStat.MOD_PE));
			
			if (tdb.canBeInvented(b, bdb)) {
				//Create list with invention blueprints.
				tmpInv.add(b);
			}else if (tdb.canBeReverseEngineered(b)) {
				//Create list with reverse engineering blueprints.
				tmpRev.add(b);
			}else {
				tmpT1.add(b);
			}
		}
		
		//Set new database to global reference.
		t1 = tmpT1;
		inv = tmpInv;
		rev = tmpRev;
	}
	
	@Override
	public synchronized void computeData() throws Exception {
		
		//Manufacturing.
		manuL = QuoteCalculator.calculateQuotes(t1, 1, pdb, bdb, prefs, 
				MAT_ACQUIRE_PRIO_ENUM[prefs.getDefaultPriorityIndex(DefaultPriority.MAT_CALC)]);
		
		invL = QuoteCalculator.calculateInventionQuotes(inv, 1, pdb, tdb, bdb, prefs, 
				INV_PRIO_ENUM[prefs.getDefaultPriorityIndex(DefaultPriority.INV_CALC)], 
				MAT_ACQUIRE_PRIO_ENUM[prefs.getDefaultPriorityIndex(DefaultPriority.MAT_CALC)]);
		
		revL = QuoteCalculator.calculateReverseEngineeringQuotes(rev, 1, pdb, bdb, idb, prefs, 
				REV_PRIO_ENUM[prefs.getDefaultPriorityIndex(DefaultPriority.REV_CALC)],
				MAT_ACQUIRE_PRIO_ENUM[prefs.getDefaultPriorityIndex(DefaultPriority.MAT_CALC)]);
		
		//Mining.
		oreL = MiningCalculator.calculateMiningQuotes(prefs, pdb, 
				idb.getItems(Arrays.asList(ORE_TYPEID)), 
				prefs.getMiningYield(MiningYield.ORE), prefs.getMiningCycle(MiningCycle.ORE), prefs.getMiningLasers(MiningLasers.ORE));
		
		iceL = MiningCalculator.calculateMiningQuotes(prefs, pdb, 
				idb.getItems(Arrays.asList(ICE_TYPEID)), 
				ICE_LASER_YIELD_PER_CYCLE, prefs.getMiningCycle(MiningCycle.ICE), prefs.getMiningLasers(MiningLasers.ICE));
		
		//Show message.
		EMT.M_HANDLER.addMessage("Manufacturing results for " + (t1.size() + inv.size() + rev.size()) + " blueprints computed.");
		
		//Last initiation step, set complete.
		super.setComplete(true);
	}

	public Collection<ManuQuote> getManuQuotes(QuoteType qT) {
		
		//Return the right quotelist.
		//Will return null if type is wrong or DB uninitiated.
		
		if (qT == QuoteType.T1) {
			return manuL;
		} else if (qT == QuoteType.INV) {
			return invL;
		} else if (qT == QuoteType.REV) {
			return revL;
		}
		return null;
	}

	public Collection<MiningQuote> getMiningQuotes(QuoteType qT) {
		
		//Return the right quotelist.
		//Will return null if type is wrong or DB uninitiated.

		if (qT == QuoteType.ORE) {
			return oreL;
		} else if (qT == QuoteType.ICE) {
			return iceL;
		}
		return null;
	}
}
