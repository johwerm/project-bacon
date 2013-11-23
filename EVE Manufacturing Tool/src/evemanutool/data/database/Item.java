package evemanutool.data.database;

import java.util.ArrayList;

import javax.swing.ImageIcon;

public final class Item {

	private final String name;
	private final int typeId;
	private final int portionSize;
	private final String description;
	private final double volume;
	private final int marketGroup;
	private final int itemGroup;
	private final boolean onMarket;
	private final Integer metaLevel;
	private final Integer metaGroup;
	private final Integer raceId;
	private final double basePrice;
	private final ArrayList<Material> baseMaterials = new ArrayList<>();
	private ImageIcon icon;


	public Item(String name, int typeId, int portionSize, String description,
				double volume, int marketGroup, boolean onMarket, Integer metaLevel,
				int itemGroup, Integer metaGroup, Integer raceId, double basePrice) {
		
		this.name = name;
		this.typeId = typeId;
		this.portionSize = portionSize;
		this.description = description;
		this.volume = volume;
		this.marketGroup = marketGroup;
		this.onMarket = onMarket;
		this.metaLevel = metaLevel;
		this.metaGroup = metaGroup;
		this.itemGroup = itemGroup;
		this.raceId = raceId;
		this.basePrice = basePrice;
	}

	public String getName() {
		return name;
	}
	
	public int getTypeId() {
		return typeId;
	}

	public int getPortionSize() {
		return portionSize;
	}

	public String getDescription() {
		return description;
	}

	public double getVolume() {
		return volume;
	}
	
	public int getMarketGroup() {
		return marketGroup;
	}
	
	public boolean isOnMarket() {
		return onMarket;
	}

	public ArrayList<Material> getBaseMaterials() {
		return baseMaterials;
	}

	public Integer getMetaLevel() {
		return metaLevel;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Item) {
			return typeId == ((Item) obj).typeId;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getTypeId();
	}

	public int getItemGroup() {
		return itemGroup;
	}

	public Integer getMetaGroup() {
		return metaGroup;
	}

	public Integer getRaceId() {
		return raceId;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public double getBasePrice() {
		return basePrice;
	}
}
