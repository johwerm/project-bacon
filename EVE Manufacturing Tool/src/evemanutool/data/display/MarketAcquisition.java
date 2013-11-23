package evemanutool.data.display;

import evemanutool.data.database.Item;

public class MarketAcquisition {
	
	private final Item item;
	private final long amount;
	private final double price;
	private final double totalVolume;
	private final double totalCost;
	
	public MarketAcquisition(Item item, long needed, double price, 
			double totalVolume, double totalCost) {
		this.item = item;
		this.amount = needed;
		this.price = price;
		this.totalVolume = totalVolume;
		this.totalCost = totalCost;
	}

	public Item getItem() {
		return item;
	}

	public long getAmount() {
		return amount;
	}

	public double getPrice() {
		return price;
	}
	
	public double getTotalVolume() {
		return totalVolume;
	}

	public double getTotalCost() {
		return totalCost;
	}
}
