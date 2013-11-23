package evemanutool.data.display;

import evemanutool.data.database.Item;

public class Supply{
	
	private final Item item;
	private double stock;
	private double needed;
	private int onBuyOrder;
	private int inProduction;
	
	public Supply(Item item, int stock, double needed, int onBuyOrder,
			int inProduction) {
		this.item = item;
		this.stock = stock;
		this.needed = needed;
		this.onBuyOrder = onBuyOrder;
		this.inProduction = inProduction;
	}

	public double getStock() {
		return stock;
	}


	public void setStock(double d) {
		this.stock = d;
	}


	public double getNeeded() {
		return needed;
	}


	public void setNeeded(double needed) {
		this.needed = needed;
	}


	public int getOnBuyOrder() {
		return onBuyOrder;
	}


	public void setOnBuyOrder(int onBuyOrder) {
		this.onBuyOrder = onBuyOrder;
	}


	public int getInProduction() {
		return inProduction;
	}


	public void setInProduction(int inProduction) {
		this.inProduction = inProduction;
	}


	public Item getItem() {
		return item;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Supply) {
			return getItem().getTypeId() == ((Supply) obj).getItem().getTypeId();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getItem().getTypeId();
	}
}
