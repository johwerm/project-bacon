package evemanutool.user;

import java.util.HashSet;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;

import evemanutool.constants.UserPrefConstants;

public class Preferences implements ConfigurationListener, UserPrefConstants {
	
	private static final String SAVE_PATH = "user.properties";
	private static final int DEFAULT_SKILL_INDEX = 5;
	private static final int DEFAULT_MODIFIER_INDEX = 0;
	private static final int DEFAULT_REGION_INDEX = 0;
	private static final int DEFAULT_UPDATE_FREQ_VALUE = 100;
	private static final double DEFAULT_TAX_VALUE = 1.0;
	private static final int DEFAULT_ORDER_INDEX = 0;
	private static final int DEFAULT_PRICE_INDEX = 0;
	private static final int DEFAULT_MANU_COST_VALUE = 333;
	private static final int DEFAULT_MOD_LEVEL = 10;
	private static final int DEFAULT_PRIORITY_INDEX = 0;
	private static final int DEFAULT_MINING_LASERS = 2;
	private static final double DEFAULT_MINING_YIELD = 1500;
	private static final int DEFAULT_MINING_CYCLE = 180;
	private static final int DEFAULT_ACCOUNT_INDEX = 0;
	
	//Keys for properties to determine skill levels.
	public enum Skill {INDUSTRY("industry_skill"), PRODUCTION_EFFICIENCY("productionefficiency_skill"),
						SCIENCE("science_skill"), RACIAL_ENCRYPTION("racialencryption_skill"),
						DATACORE_SKILLS("datacoreskills_skill"), REVERSE_ENGINEERING("reverseengineering_skill"); 
		 public final String key;
		 private Skill(String s) {key = s;}
	}

	//Keys for properties to determine the update frequency in hours for queries.
	public enum MarketSetting {UPDATE_FREQ("updatefreq_market"); 
		 public final String key;
		 private MarketSetting(String s) {key = s;}
	}
	
	//Keys for properties to determine what system should be used for market queries.
	public enum MarketSystem {BUY_SYSTEM("buysystem_market"), SELL_SYSTEM("sellsystem_market");
	 	public final String key;
	 	private MarketSystem(String s) {key = s;}
	}

	//Keys for properties to determine what type of price should be used (buy or sell).
	public enum MarketAction {SELL_ACTION("sellorder_market"), BUY_ACTION("buyorder_market");
		 public final String key;
		 private MarketAction(String s) {key = s;}
	}

	//Keys for properties to determine what value should be used (avg, min, max etc).
	public enum MarketPriceType {BUY_TYPE("buytype_market"), SELL_TYPE("selltype_market");
		public final String key;
		private MarketPriceType(String s) {key = s;}
	}

	//Keys for properties to determine what tax percentages should be applied.
	public enum MarketTax {SALES_TAX("salestax_market"), BROKER_FEE("borkerfee_market");
		 public final String key;
		 private MarketTax(String s) {key = s;}
	}
	
	//Keys for properties to determine installation costs will be added.
	public enum ManufacturingCost {
		INSTALLATION_COST("installationcost_manu"), INSTALLATION_COST_H("installationcosth_manu");
		public final String key; 
		private ManufacturingCost(String s) {key = s;}
	}
	
	//Keys for properties to determine what modifiers are in use.
	public enum InstallationMod {SLOT_MOD_PE("slotmodpe_manu"), SLOT_MOD_ME("slotmodme_manu"), 
									SLOT_MOD_INV("slotmodinv_manu"), SLOT_MOD_COPY("slotmodcopy_manu");	 
		public final String key;	 
		private InstallationMod(String s) {key = s;}
	}
	
	//Keys for properties to determine what modifiers are in use.
	public enum ImplantMod {
		MOD_PE("modpe_manu"), MOD_INV("modinv_manu");	 
		public final String key;	 
		private ImplantMod(String s) {key = s;}
	}
	
	public enum BlueprintStat {
		MOD_PE("modpe_blue"),MOD_ME("modme_blue");	 
		public final String key;	 
		private BlueprintStat(String s) {key = s;}
	}

	public enum DefaultPriority {
		INV_CALC("invcalc_prio"),MAT_CALC("matcalc_prio"),REV_CALC("revcalc_prio");	 
		public final String key;	 
		private DefaultPriority(String s) {key = s;}
	}
	
	public enum MiningLasers {
		ICE("ice_lasers"),ORE("ore_lasers");	 
		public final String key;	 
		private MiningLasers(String s) {key = s;}
	}
	
	public enum MiningYield {
		ORE("ore_yield");	 
		public final String key;	 
		private MiningYield(String s) {key = s;}
	}
	
	public enum MiningCycle {
		ICE("ice_cycle"),ORE("ore_cycle");	 
		public final String key;	 
		private MiningCycle(String s) {key = s;}
	}
	
	public enum Account {
		INDUSTRY_WALLET("industrywallet_account"), INDUSTRY_HANGAR("industryhangar_account");	 
		public final String key;	 
		private Account(String s) {key = s;}
	}

	public enum API {
		KEY("api_key"), ID("api_id");	 
		public final String key;	 
		private API(String s) {key = s;}
	}
	
	//Do QuoteDB compute.
	public static final String[] INDUSTRY_DEPENDANT_KEYS = {Skill.INDUSTRY.key, Skill.PRODUCTION_EFFICIENCY.key,
		Skill.SCIENCE.key, Skill.DATACORE_SKILLS.key, Skill.RACIAL_ENCRYPTION.key, Skill.REVERSE_ENGINEERING.key,
		ManufacturingCost.INSTALLATION_COST.key, ManufacturingCost.INSTALLATION_COST_H.key, InstallationMod.SLOT_MOD_PE.key, 
		InstallationMod.SLOT_MOD_ME.key, InstallationMod.SLOT_MOD_INV.key, InstallationMod.SLOT_MOD_COPY.key,
		ImplantMod.MOD_PE.key, ImplantMod.MOD_INV.key, DefaultPriority.INV_CALC.key, DefaultPriority.MAT_CALC.key, 
		DefaultPriority.REV_CALC.key, MiningLasers.ICE.key, MiningLasers.ORE.key, MiningYield.ORE.key, 
		MiningCycle.ICE.key, MiningCycle.ORE.key};
	
	//Do QuoteDB process.
	public static final String[] BPO_DEPENDANT_KEYS = {BlueprintStat.MOD_PE.key, BlueprintStat.MOD_ME.key};

	//Do CorpAPIDB raw.
	public static final String[] CORP_DEPENDANT_KEYS = {Account.INDUSTRY_HANGAR.key, Account.INDUSTRY_WALLET.key, 
		API.ID.key, API.KEY.key};

	//Update at process PriceDB
	public static final String[] PRICE_DEPENDANT_KEYS = {MarketAction.SELL_ACTION.key, MarketAction.BUY_ACTION.key, 
		MarketPriceType.SELL_TYPE.key, MarketPriceType.BUY_TYPE.key, MarketTax.SALES_TAX.key, MarketTax.BROKER_FEE.key};

	//Do PriceDB process.
	public static final String[] MARKET_UPDATE_DEPENDANT_KEYS = {MarketSetting.UPDATE_FREQ.key, MarketSystem.SELL_SYSTEM.key, 
		MarketSystem.BUY_SYSTEM.key};
	
	//Main object to store all settings.
	private PropertiesConfiguration conf;
	private HashSet<String> changedSettings = new HashSet<>();
	
	//Read from file or create new.
	public Preferences() {
		
		try {
			System.out.print("Loading preferences from file... ");
			conf = new PropertiesConfiguration(SAVE_PATH);	
			System.out.println("Done");
		} catch (ConfigurationException e) {
			System.err.println("Preference file could not be loaded");
			conf = new PropertiesConfiguration();
		}
		conf.setFileName(SAVE_PATH);
		conf.setAutoSave(true);
		conf.addConfigurationListener(this);
	}
	
	public void save() throws ConfigurationException {
		conf.save();
	}
	
	@Override
	public void configurationChanged(ConfigurationEvent event) {
		
		Object prop = conf.getProperty(event.getPropertyName());
		
		//Add to the list of changed settings.
		if (event.isBeforeUpdate() && conf.containsKey(event.getPropertyName())) {
			
			if (event.getPropertyValue() instanceof Integer) {
				if (prop instanceof String) {
					if (!(((String) prop).equals(((Integer) event.getPropertyValue()) + ""))) {
						changedSettings.add(event.getPropertyName());
					}
				} else {
					if (!(((Integer) prop).equals(((Integer) event.getPropertyValue())))) {
						changedSettings.add(event.getPropertyName());
					}
				}
			} else if (event.getPropertyValue() instanceof Double) {
				if (prop instanceof String) {
					if (!(((String) prop).equals(((Double) event.getPropertyValue()) + ""))) {
						changedSettings.add(event.getPropertyName());
					}
				} else {
					if (!(((Double) prop).equals(((Double) event.getPropertyValue())))) {
						changedSettings.add(event.getPropertyName());
					}
				}
			} else if (event.getPropertyValue() instanceof String) {
				if (!(((String) prop).equals(((String) event.getPropertyValue())))) {
					changedSettings.add(event.getPropertyName());
				}
			}
		}
	}
	
	public HashSet<String> getChangedSettings() {
		return changedSettings;
	}
	
	public Object getPrefByKey(String key) {
		return conf.getProperty(key);
	}
	
	public void setPrefByKey(String key, Object o) {
		conf.setProperty(key, o);
	}
	
	public int getSkillLvlIndex(Skill s) {
		return conf.getInt(s.key, DEFAULT_SKILL_INDEX);
	}
	
	public void setSkillLvlIndex(Skill s, int lvl) {
		conf.setProperty(s.key, lvl);
	}
	
	public int getMarketSetting(MarketSetting m) {
		return conf.getInt(m.key, DEFAULT_UPDATE_FREQ_VALUE);
	}
	
	public void setMarketSetting(MarketSetting m, int value) {
		conf.setProperty(m.key, value);
	}
	
	public double getMarketTax(MarketTax m) {
		return conf.getDouble(m.key, DEFAULT_TAX_VALUE);
	}
	
	public void setMarketTax(MarketTax m, double tax) {
		conf.setProperty(m.key, tax);
	}
	
	public int getMarketSystemIndex(MarketSystem m) {
		return conf.getInt(m.key, DEFAULT_REGION_INDEX);
	}
	
	public void setMarketSystem(MarketSystem m, int index) {
		conf.setProperty(m.key, index);
	}
	
	public int getMarketOrderAimIndex(MarketAction m) {
		return conf.getInt(m.key, DEFAULT_ORDER_INDEX);
	}
	
	public void setMarketActionAimIndex(MarketAction m, int index) {
		conf.setProperty(m.key, index);
	}
	
	public int getMarketPriceTypeIndex(MarketPriceType m) {
		return conf.getInt(m.key, DEFAULT_PRICE_INDEX);
	}
	
	public void setMarketPriceTypeIndex(MarketPriceType m, int index) {
		conf.setProperty(m.key, index);
	}
	
	public int getManufacturingCost(ManufacturingCost m) {
		return conf.getInt(m.key, DEFAULT_MANU_COST_VALUE);
	}
	
	public void setManufacturingCost(ManufacturingCost m, int value) {
		conf.setProperty(m.key, value);
	}
	
	public int getInstallationModIndex(InstallationMod m) {
		return conf.getInt(m.key, DEFAULT_MODIFIER_INDEX);
	}
	
	public void setManufacturingModIndex(InstallationMod m, int value) {
		conf.setProperty(m.key, value);
	}
	
	public int getImplantModIndex(ImplantMod m) {
		return conf.getInt(m.key, DEFAULT_MODIFIER_INDEX);
	}
	
	public void setImplantModIndex(ImplantMod m, int value) {
		conf.setProperty(m.key, value);
	}
	
	public int getBlueprintStat(BlueprintStat m) {
		return conf.getInt(m.key, DEFAULT_MOD_LEVEL);
	}
	
	public void setBlueprintStat(BlueprintStat m, int value) {
		conf.setProperty(m.key, value);
	}
	
	public int getDefaultPriorityIndex(DefaultPriority m) {
		return conf.getInt(m.key, DEFAULT_PRIORITY_INDEX);
	}
	
	public void setDefaultPriorityIndex(DefaultPriority m, int value) {
		conf.setProperty(m.key, value);
	}
	
	public int getMiningLasers(MiningLasers m) {
		return conf.getInt(m.key, DEFAULT_MINING_LASERS);
	}
	
	public void setMiningLasers(MiningLasers m, int value) {
		conf.setProperty(m.key, value);
	}

	public double getMiningYield(MiningYield m) {
		return conf.getDouble(m.key, DEFAULT_MINING_YIELD);
	}
	
	public void setMiningYield(MiningYield m, double value) {
		conf.setProperty(m.key, value);
	}
	
	public int getMiningCycle(MiningCycle m) {
		return conf.getInt(m.key, DEFAULT_MINING_CYCLE);
	}
	
	public void setMiningCycle(MiningCycle m, int value) {
		conf.setProperty(m.key, value);
	}
	
	public String getAPIKey(API m) {
		return conf.getString(m.key, "");
	}
	
	public void setAPIKey(API m, String value) {
		conf.setProperty(m.key, value);
	}
	
	public int getAPIId(API m) {
		return conf.getInt(m.key, 0);
	}
	
	public void setAPIId(API m, int value) {
		conf.setProperty(m.key, value);
	}
	
	public int getAccountIndex(Account s) {
		return conf.getInt(s.key, DEFAULT_ACCOUNT_INDEX);
	}
	
	public void setAccountIndex(Account s, int value) {
		conf.setProperty(s.key, value);
	}
}
