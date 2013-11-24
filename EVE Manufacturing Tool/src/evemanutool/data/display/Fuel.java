package evemanutool.data.display;

import evemanutool.data.database.Item;

public class Fuel {

	private final Item item;
	private final int amount;
	
	public Fuel(Item item, int amount) {
		this.item = item;
		this.amount = amount;
	}

	public Item getItem() {
		return item;
	}

	public int getAmount() {
		return amount;
	}
}
