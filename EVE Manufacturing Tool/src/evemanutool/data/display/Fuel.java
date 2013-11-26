package evemanutool.data.display;

import evemanutool.constants.DBConstants;
import evemanutool.data.database.Item;

public class Fuel implements DBConstants {

	private final Item item;
	private final FuelPurpose purpose;
	private final int amount;
	private final int reqAmount;
	
	public Fuel(Item item, FuelPurpose purpose, int amount, int reqAmount) {
		this.item = item;
		this.purpose = purpose;
		this.amount = amount;
		this.reqAmount = reqAmount;
	}

	public Item getItem() {
		return item;
	}
	
	public FuelPurpose getPurpose() {
		return purpose;
	}

	public int getAmount() {
		return amount;
	}

	public int getReqAmount() {
		return reqAmount;
	}
}
