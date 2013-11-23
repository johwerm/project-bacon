package evemanutool.data.display;

import java.util.ArrayList;

import evemanutool.data.database.AbstractLocation;
import evemanutool.data.database.Item;

public class Asset {

	private final ArrayList<Asset> assets;
	private final Item item;
	private final long itemID;
	private final AbstractLocation location;
	private final int quantity;
	private final Integer rawQuantity;
	private final int flag;
	private final boolean singleton;
	private String playerName;
	
	public Asset(ArrayList<Asset> assets, Item item, long itemId, AbstractLocation location,
			int quantity, Integer rawQuantity, int flag, boolean singleton) {
		this.assets = assets;
		this.item = item;
		this.itemID = itemId;;
		this.location = location;
		this.quantity = quantity;
		this.rawQuantity = rawQuantity;
		this.flag = flag;
		this.singleton = singleton;
	}

	public ArrayList<Asset> getAssets() {
		return assets;
	}

	public Item getItem() {
		return item;
	}

	public long getItemID() {
		return itemID;
	}

	public AbstractLocation getLocation() {
		return location;
	}

	public int getQuantity() {
		return quantity;
	}

	public Integer getRawQuantity() {
		return rawQuantity;
	}

	public int getFlag() {
		return flag;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Asset) {
			return getItemID() == ((Asset) obj).getItemID();
		}
		return false;
	}
}
