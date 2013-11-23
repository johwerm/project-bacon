package evemanutool.utils.calc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.beimin.eveapi.corporation.member.security.ApiSecurityMember;
import com.beimin.eveapi.corporation.member.security.ApiSecurityRole;
import com.beimin.eveapi.corporation.member.tracking.ApiMember;
import com.beimin.eveapi.corporation.sheet.CorpSheetResponse;
import com.beimin.eveapi.shared.assetlist.EveAsset;

import evemanutool.constants.DBConstants;
import evemanutool.data.database.AbstractLocation;
import evemanutool.data.display.Asset;
import evemanutool.utils.databases.ItemDB;
import evemanutool.utils.databases.LocationDB;

public class AssetCalculator implements DBConstants{
	
	public static ArrayList<Asset> getAccessibleAssets(Collection<Asset> treeAssets, ApiSecurityMember aSM, ApiMember aM, CorpSheetResponse cSR) {
		
		ArrayList<Asset> ans = new ArrayList<>();
		ArrayList<Asset> tmp;
		ArrayList<Asset> tmp2;
		int index;
		
		//Check base.
		tmp = getFlatAssetsInLocation(treeAssets, aM.getBaseID());
		if (!tmp.isEmpty()) {
			//Check for each hangar access mask.
			for (index = 0; index < ROLE_HANGARTAKE_ACCESS_MASKS.length; index++) {
				for (ApiSecurityRole aSR : aSM.getRolesAtBase()) {
					//If the character has a matching role or is a director.
					if (aSR.getRoleID() == ROLE_HANGARTAKE_ACCESS_MASKS[index] || 
							aSR.getRoleID() == ROLE_DIRECTOR_MASK) {
						ans.addAll(getFlatAssetsInCorpHangar(tmp, index));
					}
				}
			}
		}
		
		//Check HQ.
		tmp = getFlatAssetsInLocation(treeAssets, cSR.getStationID());
		if (!tmp.isEmpty()) {
			//Check for each hangar access mask.
			for (index = 0; index < ROLE_HANGARTAKE_ACCESS_MASKS.length; index++) {
				for (ApiSecurityRole aSR : aSM.getRolesAtHQ()) {
					//If the character has a matching role or is a director.
					if (aSR.getRoleID() == ROLE_HANGARTAKE_ACCESS_MASKS[index] || 
							aSR.getRoleID() == ROLE_DIRECTOR_MASK) {
						ans.addAll(getFlatAssetsInCorpHangar(tmp, index));
					}
				}
			}
		}
		
		//Check other.
		//Get all assets in other locations.
		tmp.addAll(getFlatAssetsInLocation(treeAssets, aM.getBaseID()));
		tmp2 = getFlatAssets(treeAssets);
		tmp2.removeAll(tmp);
		if (!tmp2.isEmpty()) {
			//Check for each hangar access mask.
			for (index = 0; index < ROLE_HANGARTAKE_ACCESS_MASKS.length; index++) {
				for (ApiSecurityRole aSR : aSM.getRolesAtOther()) {
					//If the character has a matching role or is a director.
					if (aSR.getRoleID() == ROLE_HANGARTAKE_ACCESS_MASKS[index] || 
							aSR.getRoleID() == ROLE_DIRECTOR_MASK) {
						ans.addAll(getFlatAssetsInCorpHangar(tmp2, index));
					}
				}
			}
		}
		
		return ans;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Asset> convertCompleteAssets(ArrayList<EveAsset<EveAsset<?>>> rawAssets, 
			LocationDB ldb, ItemDB idb) {
		//All assets.
		ArrayList<Asset> ans = new ArrayList<>();
		//Temporary assets (market deliveries, space containers?).
		ArrayList<Asset> tmp = new ArrayList<>();
		
		for (EveAsset<?> eA : rawAssets) {
			//Make sure that no items are from market deliveries.
			if (eA.getFlag() == MARKET_DELIVERIES_FLAG && eA.getAssets() != null) {
				//Market deliveries.	
				tmp.add(new Asset(convertAssets((Collection<EveAsset<?>>) eA.getAssets(), idb), idb.getItem(eA.getTypeID()),
						eA.getItemID(), ldb.getLocationFromId(eA.getLocationID()), eA.getQuantity(), eA.getRawQuantity(),
						eA.getFlag(), eA.getSingleton()));
				
			}else if (eA.getAssets() != null) {
				//Office or space.
				ans.add(new Asset(convertAssets((Collection<EveAsset<?>>) eA.getAssets(), idb), idb.getItem(eA.getTypeID()),
						eA.getItemID(), ldb.getLocationFromId(eA.getLocationID()), eA.getQuantity(), eA.getRawQuantity(),
						eA.getFlag(), eA.getSingleton()));
			}
			
		}
		
		//Sort and add all temp assets.
		//List of new "Assets" to include on the top-level, containing deliveries and items in space.
		ArrayList<Asset> parents = new ArrayList<>();
		Asset parent;
		
		for (Asset a : tmp) {
			//Assets equals() implemented to compare loacationIds. 
			//Note: Copy Asset always, set Flag to 0 and Location to null to make them show up as normal items.
			parent = null;
			for (Asset p : parents) {
				if (a.getLocation().getLocationId() == p.getLocation().getLocationId()) {
					parent = p;
				}
			}
			if (parent == null) {
				//Add new parent. Treat it as an office.
				parents.add(parent = new Asset(
						new ArrayList<Asset>(), idb.getItem(OFFICE_TYPEID), 0, a.getLocation(), 1, 1, a.getFlag(), false));
				parent.getAssets().add(new Asset(a.getAssets(), a.getItem(), a.getItemID(), null,
						a.getQuantity(), a.getRawQuantity(), 0, a.isSingleton()));
			}else {
				//Parent already exists, add asset.
				parent.getAssets().add(new Asset(a.getAssets(), a.getItem(), a.getItemID(), null,
						a.getQuantity(), a.getRawQuantity(), 0, a.isSingleton()));
			}
		}
		 
		ans.addAll(parents);
		return ans;
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<Asset> convertAssets(Collection<EveAsset<?>> l, ItemDB idb) {
		ArrayList<Asset> ans = new ArrayList<>();
		for (EveAsset<?> eA : l) {
			if (eA.getAssets() != null && !eA.getAssets().isEmpty()) {
				ans.add(new Asset(convertAssets((Collection<EveAsset<?>>) eA.getAssets(), idb), idb.getItem(eA.getTypeID()),
						eA.getItemID(), null, eA.getQuantity(), eA.getRawQuantity(),
						eA.getFlag(), eA.getSingleton()));
			}else {
				ans.add(new Asset(null, idb.getItem(eA.getTypeID()),
						eA.getItemID(), null, eA.getQuantity(), eA.getRawQuantity(),
						eA.getFlag(), eA.getSingleton()));
			}
		}
		return ans;
	}

	private static ArrayList<Asset> getFlatAssetsInLocation(Collection<Asset> treeAssets, long l) {
		
		for (Asset asset : treeAssets) {
			if (asset.getLocation().getLocationId() == l) {
				return getFlatAssets(asset.getAssets());
			}
		}
		//Don't return null.
		return new ArrayList<>();
	}

	public static ArrayList<Asset> getFlatAssets(Collection<Asset> treeAssets) {
		ArrayList<Asset> ans = new ArrayList<>();
		for (Asset asset : treeAssets) {
			ans.add(asset);
			if (asset.getAssets() != null && !asset.getAssets().isEmpty()) {
				ans.addAll(getFlatAssets(asset.getAssets()));
			}
		}
		return ans;
	}
	

	public static ArrayList<Asset> getFlatAssetsInCorpHangar(Collection<Asset> flatAssets, int hangarIndex) {
		ArrayList<Asset> ans = new ArrayList<>();
		for (Asset a : flatAssets) {
			if (Arrays.binarySearch(DIVISION_FLAGS, a.getFlag()) == hangarIndex) {
				ans.add(a);
			}
		}
		return ans;
	}
	
	public static AbstractLocation getParentAssetLocation(Collection<Asset> treeAssets, long itemId) {
		
		//Search each top-parent for the given asset.
		for (Asset parent : treeAssets) {
			if (contains(parent, itemId)) {
				return parent.getLocation();
			}
		}
		//If no parent could be found, return null.
		return null;
	}

	private static boolean contains(Asset parent, long itemId) {
		
		//Search for the asset.
		for (Asset a : parent.getAssets()) {
			if (a.getItemID() == itemId) {
				return true;
			} else {
				//Search underlying assets.
				if (a.getAssets() != null && contains(a, itemId)) {
					return true;
				}
			}
		}
		return false;
	}
}
