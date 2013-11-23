package evemanutool.data.display;

import java.util.ArrayList;

import evemanutool.data.cache.NumberTrendEntry;
import evemanutool.data.database.ManuQuote;

public class CorpProductionQuote {

	//DB-dependent.
	private final ManuQuote quote;
	private final int avgWeekTradedAmount;
	private final int avgWeekSoldAmount;
	private ArrayList<NumberTrendEntry> marketTrend;
	private ArrayList<NumberTrendEntry> volumeTrend;
	private boolean active;
	
	//Stats.
	private final int stock;
	private final int onSale;
	private final int inProduction;
	
	//User affected.
	private int needToProduce;
	private int sellTarget;

	public CorpProductionQuote(ManuQuote quote, int avgWeekTradedAmount,
			int avgWeekSoldAmount, ArrayList<NumberTrendEntry> marketTrend,
			ArrayList<NumberTrendEntry> volumeTrend, boolean active, int stock,
			int onSale, int inProduction, int needToProduce, int sellTarget) {
		this.quote = quote;
		this.avgWeekTradedAmount = avgWeekTradedAmount;
		this.avgWeekSoldAmount = avgWeekSoldAmount;
		this.marketTrend = marketTrend;
		this.volumeTrend = volumeTrend;
		this.active = active;
		this.stock = stock;
		this.onSale = onSale;
		this.inProduction = inProduction;
		this.needToProduce = needToProduce;
		this.sellTarget = sellTarget;
	}

	public int getSellTarget() {
		return sellTarget;
	}

	public void setSellTarget(int sellTarget) {
		this.sellTarget = sellTarget;
	}

	public ManuQuote getQuote() {
		return quote;
	}

	public int getAvgWeekTradedAmount() {
		return avgWeekTradedAmount;
	}
	
	public int getAvgWeekSoldAmount() {
		return avgWeekSoldAmount;
	}

	public int getNeedToProduce() {
		return needToProduce;
	}
	
	public void setNeedToProduce(int needToProduce) {
		this.needToProduce = needToProduce;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public int getStock() {
		return stock;
	}

	public int getOnSale() {
		return onSale;
	}

	public int getInProduction() {
		return inProduction;
	}

	public ArrayList<NumberTrendEntry> getMarketTrend() {
		return marketTrend;
	}

	public void setMarketTrend(ArrayList<NumberTrendEntry> marketTrend) {
		this.marketTrend = marketTrend;
	}

	public ArrayList<NumberTrendEntry> getVolumeTrend() {
		return volumeTrend;
	}

	public void setVolumeTrend(ArrayList<NumberTrendEntry> volumeTrend) {
		this.volumeTrend = volumeTrend;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CorpProductionQuote) {
			return getQuote().getBpo().getBlueprintItem().getTypeId() == 
					((CorpProductionQuote) obj).getQuote().getBpo().getBlueprintItem().getTypeId();
		}
		return false;
	}
}
