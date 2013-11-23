package evemanutool.data.database;

import java.util.ArrayList;

import evemanutool.constants.DBConstants;
import evemanutool.data.general.Time;

public class ManuQuote implements DBConstants {
	
	//BPO Info
	private final Blueprint bpo;
	private boolean baseBPOSeededOnMarket;
	
	//Time
	private final Time manuTime;
	
	//Cost & Income
	private final double manuCost;
	private final double sellIncome;
	private final double profit;
	private final double profitPerHour;
	private final double salesVolumeRatio;
	private final Trend productTrend;
	private double sustainableProfitValue;
	
	//Required materials.
	private final int runs;
	private ArrayList<Material> matList;
	private Invention inv;
	private ReverseEngineering rev;
	private final MatAcquirePriority prio;
	
	public ManuQuote(	Blueprint bpo, Time manuTime, double manuCost, double sellIncome, double profit,
					double profitPerHour, double salesVolumeRatio, int runs, ArrayList<Material> matList,
					MatAcquirePriority prio, Trend trend) {
		
		this.bpo = bpo;
		this.manuTime = manuTime;
		this.manuCost = manuCost;
		this.sellIncome = sellIncome;
		this.profit = profit;
		this.profitPerHour = profitPerHour;
		this.salesVolumeRatio = salesVolumeRatio;
		this.runs = runs;
		this.matList = matList;
		this.prio = prio;
		this.productTrend = trend;
	}
	
	//Initiates all fields. 
	public ManuQuote(	Blueprint bpo, Time manuTime, double manuCost, double sellIncome, double profit,
					double profitPerHour, double salesVolumeRatio, int runs, ArrayList<Material> matList,
					MatAcquirePriority prio, Trend trend, boolean baseBPOSeededOnMarket) {
		
		this.bpo = bpo;
		this.manuTime = manuTime;
		this.manuCost = manuCost;
		this.sellIncome = sellIncome;
		this.profit = profit;
		this.profitPerHour = profitPerHour;
		this.salesVolumeRatio = salesVolumeRatio;
		this.runs = runs;
		this.matList = matList;
		this.prio = prio;
		this.baseBPOSeededOnMarket = baseBPOSeededOnMarket;
		this.productTrend = trend;
	}
	
	public Blueprint getBpo() {
		return bpo;
	}
	public double getManuCost() {
		return manuCost;
	}
	public Time getManuTime() {
		return manuTime;
	}
	public ArrayList<Material> getMatList() {
		return matList;
	}
	public void setMatList(ArrayList<Material> matList) {
		this.matList = matList;
	}
	public double getProfit() {
		return profit;
	}
	public double getProfitPerHour() {
		return profitPerHour;
	}
	public double getSellIncome() {
		return sellIncome;
	}
	public double getSalesVolumeRatio() {
		return salesVolumeRatio;
	}

	public double getSustainableProfitValue() {
		return sustainableProfitValue;
	}
	
	public void setSustainableProfitValue(double sustainableProfitValue) {
		this.sustainableProfitValue = sustainableProfitValue;
	}

	public int getRuns() {
		return runs;
	}

	public Invention getInv() {
		return inv;
	}

	public void setInv(Invention inv) {
		this.inv = inv;
	}

	public ReverseEngineering getRev() {
		return rev;
	}

	public void setRev(ReverseEngineering rev) {
		this.rev = rev;
	}

	public MatAcquirePriority getPrio() {
		return prio;
	}
	
	public Trend getProductTrend() {
		return productTrend;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ManuQuote) {
			return getBpo().getBlueprintItem().getTypeId() == 
					((ManuQuote) obj).getBpo().getBlueprintItem().getTypeId();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getBpo().getBlueprintItem().getTypeId();
	}

	public boolean isBaseBPOSeededOnMarket() {
		return baseBPOSeededOnMarket;
	}

	public void setBaseBPOSeededOnMarket(boolean baseBPOSeededOnMarket) {
		this.baseBPOSeededOnMarket = baseBPOSeededOnMarket;
	}
}
