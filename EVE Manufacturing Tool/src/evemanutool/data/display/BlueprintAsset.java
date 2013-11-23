package evemanutool.data.display;

import evemanutool.constants.DBConstants.IndustryActivity;
import evemanutool.constants.DBConstants.BpoState;
import evemanutool.data.cache.BlueprintAssetEntry;
import evemanutool.data.database.AbstractLocation;
import evemanutool.data.database.Item;

public class BlueprintAsset {
	
	private final Item item;
	private final AbstractLocation location;
	private final BlueprintAssetEntry assetEntry;
	private final IndustryActivity activity;
	private final BpoState state;
	
	public BlueprintAsset(Item item, AbstractLocation location,
			BlueprintAssetEntry assetEntry, IndustryActivity activity, BpoState state) {
		this.item = item;
		this.location = location;
		this.assetEntry = assetEntry;
		this.activity = activity;
		this.state = state;
	}

	public Item getItem() {
		return item;
	}

	public AbstractLocation getLocation() {
		return location;
	}

	public BlueprintAssetEntry getAssetEntry() {
		return assetEntry;
	}

	public IndustryActivity getActivity() {
		return activity;
	}

	public BpoState getState() {
		return state;
	}
}
