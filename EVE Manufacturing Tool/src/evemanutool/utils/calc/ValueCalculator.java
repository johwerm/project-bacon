package evemanutool.utils.calc;

import java.util.Collection;
import java.util.Map;

import com.beimin.eveapi.corporation.member.security.ApiSecurityMember;
import com.beimin.eveapi.corporation.member.security.ApiSecurityRole;
import com.beimin.eveapi.corporation.member.tracking.ApiMember;
import com.beimin.eveapi.corporation.sheet.CorpSheetResponse;
import com.beimin.eveapi.shared.accountbalance.EveAccountBalance;

import evemanutool.constants.DBConstants;
import evemanutool.data.display.Asset;
import evemanutool.data.display.MarketOrder;
import evemanutool.user.Preferences;
import evemanutool.utils.databases.PriceDB;

public class ValueCalculator implements DBConstants {
	
	public static double getCorpMemberAccessValue(Collection<Asset> treeAssets, ApiSecurityMember aSM, 
			ApiMember aM, CorpSheetResponse cSR, Preferences prefs, PriceDB pdb, Map<Integer, EveAccountBalance> accountMap) {
		
		double val = getAssetValue(prefs, pdb, AssetCalculator.getAccessibleAssets(treeAssets, aSM, aM, cSR));
		
		//Check for each division access mask.
		int index;
		for (index = 0; index < ROLE_ACCOUNTTAKE_ACCESS_MASKS.length; index++) {
			for (ApiSecurityRole aSR : aSM.getRoles()) {
				//If the character has a matching role or is a director.
				if (aSR.getRoleID() == ROLE_ACCOUNTTAKE_ACCESS_MASKS[index] || 
						aSR.getRoleID() == ROLE_DIRECTOR_MASK) {
					val += accountMap.get(DIVISION_KEYS[index]).getBalance();
				}
			}
		}
		
		return val;
	}

	public static double getAssetValue(Preferences prefs, PriceDB pdb, Collection<Asset> flatAssets) {
		double val = 0;
		for (Asset a : flatAssets) {
			if (a.getQuantity() > 0) {
				val += MarketCalculator.calculatePrice(Action.SELL, a.getItem().getTypeId(), pdb, prefs) * a.getQuantity();
			}
		}
		return val;
	}

	public static double getSellOrderValue(Collection<MarketOrder> sellOrders) {
		double val = 0;
		for (MarketOrder a : sellOrders) {
			val += a.getMarketOrder().getPrice() * a.getMarketOrder().getVolRemaining();
		}
		return val;
	}

	public static double getBuyOrderEscrow(Collection<MarketOrder> buyOrders) {
		double val = 0;
		for (MarketOrder a : buyOrders) {
			val += a.getMarketOrder().getEscrow();
		}
		return val;
	}

}
