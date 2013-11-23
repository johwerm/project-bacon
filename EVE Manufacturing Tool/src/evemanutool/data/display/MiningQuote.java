package evemanutool.data.display;

import evemanutool.data.database.Item;

public class MiningQuote {
	
	private final Item ore;
	
	//Income
	private final double incomePerH;
	private final double incomePerM3;
	
	public MiningQuote(Item ore, double incomePerH, double incomePerM3) {
		this.ore = ore;
		this.incomePerH = incomePerH;
		this.incomePerM3 = incomePerM3;
	}

	public Item getOre() {
		return ore;
	}

	public double getIncomePerH() {
		return incomePerH;
	}

	public double getIncomePerM3() {
		return incomePerM3;
	}
}
