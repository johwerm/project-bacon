package evemanutool.constants;

import com.beimin.eveapi.shared.wallet.RefType;

import evemanutool.data.general.Pair;

public interface DBConstants {
	
	//Local cache and data storage.
	//Caches.
	public static final String MARKET_CACHE_PATH = "cache/market.data";
	public static final String HISTORY_CACHE_PATH = "cache/markethistory.data";
	public static final String TYPE_IMGAGES_PATH = "types";
	public static final String INDUSTRY_STATS_CACHE_PATH = "cache/industrystats.data";
	public static final String BPO_ASSETS_CACHE_PATH = "cache/bpoassets.data";
	public static final String WALLET_JOURNAL_CACHE_PATH = "cache/wjournal.data";
	public static final String WALLET_TRANS_CACHE_PATH = "cache/wtransaction.data";
	public static final String CORP_QUOTES_CACHE_PATH = "cache/corpquotes.data";
	
	//CSV files.
	public static final String MARKET_GROUP_PATH = "data/invMarketGroups.csv";
	public static final String TYPES_PATH = "data/invTypes.csv";
	public static final String TYPE_MATERIALS_PATH = "data/invTypeMaterials.csv";
	public static final String BLUEPRINTS_PATH = "data/invBlueprintTypes.csv";
	public static final String TYPE_REQUIREMENTS_PATH = "data/ramTypeRequirements.csv";
	public static final String TYPE_ATTRIBUTES_PATH = "data/dgmTypeAttributes.csv";
	public static final String META_TYPES_PATH = "data/invMetaTypes.csv";
	public static final String STATIONS_PATH = "data/staStations.csv";
	public static final String SYSTEMS_PATH = "data/mapSolarSystems.csv";
	public static final String CONSTELLATIONS_PATH = "data/mapConstellations.csv";
	public static final String REGIONS_PATH = "data/mapRegions.csv";

	//Parse delimiters.
	public static final String LEVEL1_DELIM = "%";
	public static final String LEVEL2_DELIM = "¤";
	public static final String LEVEL3_DELIM = "&";
	public static final String LEVEL4_DELIM = "@";
	
	//API data.
	//General.
	public static final long[] CORP_ACCESS_MASKS = {1,2,8,128,256,512,2048,4096,131072,262144,524288,1048576,2097152,4194304,8388608,16777216,33554432};
	
	//Roles.
	public static final long ROLE_DIRECTOR_MASK = 1;
	public static final long[] ROLE_ACCOUNTTAKE_ACCESS_MASKS = {134217728, 268435456, 536870912, 1073741824, 2147483648L, 4294967296L, 8589934592L};
	public static final long[] ROLE_HANGARTAKE_ACCESS_MASKS = {8192, 16384, 32768, 65536, 131072, 262144, 524288};
	public static final long[] ROLE_CONTAINERTAKE_ACCESS_MASKS = {4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L,
																	70368744177664L, 140737488355328L, 281474976710656L};
	
	//Wallet.
	public static final RefType[] TAX_TYPES = {RefType.MISSION_REWARD, RefType.BOUNTY_PRIZE};
	
	//Conversion keys.
	public static final int[] DIVISION_KEYS = {1000, 1001, 1002, 1003, 1004, 1005, 1006};
	public static final int OFFICE_TYPEID = 27;

	//Updates.
	public static final long STATS_UPDATE_INTERVAL = 5; //H
	public static final int TRADE_UPDATE_INTERVAL = 24; //H
	
	//Flags.
	public static final int[] DIVISION_FLAGS = {4, 116, 117, 118, 119, 120, 121};
	public static final int MARKET_DELIVERIES_FLAG = 62;
	public static final Pair<Integer, Integer> SHIP_SLOT_FLAGS_MINMAX = new Pair<>(11, 34);
	public static final Pair<Integer, Integer> RIG_SLOT_FLAGS_MINMAX = new Pair<>(92, 99);
	public static final Pair<Integer, Integer> SUBSYSTEM_SLOT_FLAGS_MINMAX = new Pair<>(125, 132);
	
	//Mining
	//General.
	public static final double ICE_LASER_YIELD_PER_CYCLE = 1000;
	
	//Ores (0.0 -> High).
	public static final Integer[] ORE_TYPEID = {22,17425,17426,
											1223,17428,17429,
											1225,17432,17433,
											1232,17436,17437,
											1229,17865,17866,
											11396,17869,17870,
											19,17466,17467,
											21,17440,17441,
											1231,17444,17445,
											1226,17448,17449,
											20,17452,17453,
											1227,17867,17868,
											18,17455,17456,
											1224,17459,17460,
											1228,17463,17464,
											1230,17470,17471};
	
	//Ice.
	public static final Integer[] ICE_TYPEID = {16262,16263,16264,
											16265,16266,16267,
											16268,16269,17975,
											17976,17977,17978};
	
	//Invention & Tech
	//General.
	public static final int DEFAULT_INV_MEPE_LEVEL = -4;
	public static final int T2_META_LEVEL = 5;
	
	//Meta Groups.
	public static final Pair<Integer, Integer> META_GROUP_TECH_I_MINMAX = new Pair<>(0, 4);
	public static final String META_GROUP_TECH_I_LABEL = "Tech I";
	public static final Integer META_GROUP_TECH_I_VALUE = 1;
	public static final Integer[] META_FACTION_LEVELS = {7,8};
	public static final Integer[] META_GROUP_VALUE = {2,3,4,6,5,14};
	public static final String[] META_GROUP_LABEL = {"Tech II", "Storyline", "Faction", "Deadspace", "Officer", "Tech III"};
	
	//Item Groups.
	public static final int DATA_INTERFACES_GROUP = 716;
	
	//Basechance item groups.
	public static final Integer[] INV_20_CHANCE_MODGROUPS = {419, 27};
	public static final int INV_20_CHANCE_MODID = 17476;
	public static final Integer[] INV_25_CHANCE_MODGROUPS = {26, 28};
	public static final int INV_25_CHANCE_MODID = 17478;
	public static final Integer[] INV_30_CHANCE_MODGROUPS = {25, 420, 513};
	public static final int INV_30_CHANCE_MODID = 17480;
	
	//Attributes.
	public static final int META_LEVEL_ATTR = 633;
	public static final int DECRYPTORS_ATTR = 1115;
	public static final int INV_PROB_MULTIPLIER_ATTR = 1112;
	public static final int INV_ME_MODIFIER_ATTR = 1113;
	public static final int INV_PE_MODIFIER_ATTR = 1114;
	public static final int INV_MAX_RUNS_MODIFIER_ATTR = 1124;

	//Reverse Engineering.
	//General.
	public static final int DEFAULT_REV_MEPE_LEVEL = 0;
	public static final int DEFAULT_REV_TIME = 3600; //Secs.
	
	//Item groups.
	public static final int REV_DECRYPTOR_GROUP_ID = 979;
	public static final int REV_RELIC_MARKET_GROUP_ID = 1149;
	public static final Integer[] REV_TYPE_GROUP_ID = {956, 954, 958, 957, 955, 963}; //Off, Def, Engi, Prop, Elec, Hull.
	public static final Integer[] REV_RELIC_TYPE_GROUP_ID = {991, 993, 992, 971, 990, 997}; //Off, Def, Engi, Prop, Elec, Hull.
	
	//Relic values.
	public static final String[] REV_RELIC_TYPE_LABEL = {"Intact", "Malfunctioning", "Wrecked"};
	public static final double[] REV_RELIC_TYPE_CHANCE = {0.4, 0.3, 0.2};
	public static final int[] REV_RELIC_TYPE_RUNS = {20, 10, 3};
	
	//Market
	//General.
	public static final String[] PRODUCT_MARKET_GROUPS = {"Ships", "Ship Equipment", "Ammunition & Charges", "Drones", "Manufacture & Research"};
	public enum Action {BUY,SELL}
	public static final int HISTORY_MAX_DAYS = 30; // Days.
	
	//Trend.
	public enum Trend {RISING, FALLING, STABLE,UNSTABLE, INCONCLUSIVE}
	
	public static final int MINIMUM_HISTORY_SIZE = 20;
	public static final double SIGNIFICANT_DIFF = 0.05; // 5 %
	
	//SPV - Weighting values for Sustained Profit values.
	public static final double PROFIT_PER_H_SUSTAIN = 200000;
	public static final double TIME_SUSTAIN = 24 * 60 * 60; // sec.
	public static final double SVR_SUSTAIN = 10;

	//Updates.
	public static final int MARKET_UPDATE_DELAY = 5 * 60 * 1000; //(millisec) --> 5 min. 
	public static final int MINIMUM_HISTORY_UPDATE_NEED = 3; //Days.

	//Industry
	//Manufacturing priorities.
	public enum InvPriority {PROFIT_MARGIN, PROFIT_PER_H, PROFIT_PER_COPY_H, PROFIT_SUSTAINED}
	public enum RevPriority {PROFIT_MARGIN, PROFIT_PER_REV_H}
	public enum MatAcquirePriority {PROFIT_MARGIN, PROFIT_PER_H_CHAIN}
	
	//Industry activities.
	public enum IndustryActivity {
		MANUFACTURE("Manufacturing", 1), TIME("Time Research", 3),
		MATERIAL("Material Research", 4), COPYING("Copying", 5),
		REVERSE_ENGINERING("Reverse Engineering", 7), INVENTION("Invention", 8);
		
		public final String name;
		public final int key;
	
		private IndustryActivity(String name, int key) {
			this.name = name;
			this.key = key;
		}
		
		public static IndustryActivity getFromKey(int key) {
			for (IndustryActivity a : values()) {
				if (a.key == key) {
					return a;
				}
			}
			return null;
		}
	}

	//Industry job states.
	public enum BpoState {
		FAILED("Failed", 0), DELIVERED("Delivered", 1),
		ABORTED("Aborted", 4), PENDING("Pending", 5), IN_PROGRESS("In progress", 6),
		READY("Ready", 7);
		
		public final String name;
		public final int key;
	
		private BpoState(String name, int key) {
			this.name = name;
			this.key = key;
		}
		
		public static BpoState getState(boolean started, boolean ended, boolean completed, int status) {
			//Decoding logic to get the correct job state. (started/ended are given by date)
			if (completed) {
				for (BpoState a : values()) {
					if (a.key == status) {
						return a;
					}
				}
			}else {
				if (started) {
					if (ended) {
						return READY;
					}else {
						return IN_PROGRESS;
					}
				}else {
					return PENDING;
				}
			}
			return null;
		}
	}

	//Quote types.
	public enum QuoteType {
		T1,INV,REV,ORE,ICE;
	}
	
}
