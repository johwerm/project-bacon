package evemanutool.data.display;

import evemanutool.data.database.ManuQuote;
import evemanutool.data.general.Time;


public class ManuAcquisition {
	
	private final ManuQuote quote;
	private final long amount;
	private final int inProduction;
	private final double materialCoverage; //Percentage of required materials in stock.
	private final Time totalSlotTime;
	
	public ManuAcquisition(ManuQuote quote, long needed, int inProduction,
			double materialCoverage, Time totalSlotTime) {
		this.quote = quote;
		this.amount = needed;
		this.inProduction = inProduction;
		this.materialCoverage = materialCoverage;
		this.totalSlotTime = totalSlotTime;
	}

	public ManuQuote getQuote() {
		return quote;
	}

	public long getAmount() {
		return amount;
	}
	
	public int getInProduction() {
		return inProduction;
	}

	public double getMaterialCoverage() {
		return materialCoverage;
	}

	public Time getTotalSlotTime() {
		return totalSlotTime;
	}
}
