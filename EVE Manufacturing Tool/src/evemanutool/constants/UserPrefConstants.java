package evemanutool.constants;

import evemanutool.constants.DBConstants.InvPriority;
import evemanutool.constants.DBConstants.MatAcquirePriority;
import evemanutool.constants.DBConstants.RevPriority;
import evemanutool.data.cache.MarketInfoEntry.OrderAim;
import evemanutool.data.cache.PriceEntry.PriceType;

public interface UserPrefConstants {

	//System labels and codes for EVE-Central / EVE-market-data  queries.
	public static final String[] MARKET_SYSTEM_LABEL = {"Jita", "Amarr", "Rens", "Dodixie", "Hek", "Tash-Murkon Prime", "Oursulaert", "Motsu"};
	public static final String[] MARKET_SYSTEM_CODE = {"30000142", "30002187", "30002510", "30002659", "30002053", "30001671", "30004969", "30002819"};
	public static final String[] MARKET_REGION_CODE = {"10000002", "10000043", "10000030", "10000032", "10000042", "10000020", "10000064", "10000033"};
	
	//Order labels and enums.
	public static final String[] MARKET_AIM_LABEL = {"Sell", "Buy"};
	public static final OrderAim[] MARKET_AIM_ENUM = {OrderAim.SELL, OrderAim.BUY};
	
	//Price type labels and enums.
	public static final String[] MARKET_PRICE_LABEL = {"Min", "Max", "Median", "Percentile"};
	public static final PriceType[] MARKET_PRICE_ENUM = {PriceType.MIN, PriceType.MAX, PriceType.MEDIAN, PriceType.PERCENTILE};

	//Material acquire priorities labels and enums.
	public static final String[] MAT_ACQUIRE_PRIO_LABEL = {"Profit/Manufacturing slot", "Profit Margin"};
	public static final MatAcquirePriority[] MAT_ACQUIRE_PRIO_ENUM = {MatAcquirePriority.PROFIT_PER_H_CHAIN, MatAcquirePriority.PROFIT_MARGIN};

	//Invention priorities labels and enums.
	public static final String[] INV_PRIO_LABEL = {"Profit Margin", "Profit/hour", "Profit/copy time", "Sustainable Profit"};
	public static final InvPriority[] INV_PRIO_ENUM = {InvPriority.PROFIT_MARGIN, InvPriority.PROFIT_PER_H, InvPriority.PROFIT_PER_COPY_H, InvPriority.PROFIT_SUSTAINED};

	//Invention priorities labels and enums.
	public static final String[] REV_PRIO_LABEL = {"Profit Margin", "Profit/rev hour"};
	public static final RevPriority[] REV_PRIO_ENUM = {RevPriority.PROFIT_MARGIN, RevPriority.PROFIT_PER_REV_H};
	
	//Manufacturing slot modifiers.
	public static final String[] MOD_SLOT_LABEL = {"1", "0.75"};
	public static final double[] MOD_SLOT_VALUE = {1.0,0.75};

	//Invention slot modifiers.
	public static final String[] MOD_INV_LABEL = {"1", "0.50"};
	public static final double[] MOD_INV_VALUE = {1.0,0.50};
	
	//Copying slot modifiers.
	public static final String[] MOD_COPY_LABEL = {"1", "0.75", "0.65"};
	public static final double[] MOD_COPY_VALUE = {1.0,0.75,0.65};
	
	//Manufacturing implant modifiers.
	public static final String[] MOD_IMPLANT_LABEL = {"0%", "1%", "2%", "4%"};
	public static final double[] MOD_IMPLANT_VALUE = {1, 0.99, 0.98, 0.96};
	
	//Skill levels.
	public static final String[] SKILL_LEVEL_LABEL = {"0", "I", "II", "III", "IV", "V"};
	public static final int[] SKILL_LEVEL_VALUE = {0, 1, 2, 3, 4, 5};
	
	//Accounts.
	public static final String[] ACCOUNT_LABEL = {"1", "2", "3", "4", "5", "6", "7"};
	public static final int[] ACCOUNT_VALUE = {0, 1, 2, 3, 4, 5, 6};
}
