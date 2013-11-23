package evemanutool.data.display;

import com.beimin.eveapi.corporation.member.tracking.ApiMember;
import com.beimin.eveapi.shared.marketorders.ApiMarketOrder;

import evemanutool.constants.DBConstants;
import evemanutool.data.database.Item;
import evemanutool.data.database.Region;
import evemanutool.data.database.Station;


public class MarketOrder implements Comparable<MarketOrder>, DBConstants {
	
	private final ApiMarketOrder marketOrder;
	private final ApiMember issuer;
	private final Station station;
	private final Region region;
	private final Item item;
	private final String walletDivision;
	
	public MarketOrder(ApiMarketOrder marketOrder, ApiMember issuer, Station station, 
			Region region, Item item, String walletDivision) {
		this.marketOrder = marketOrder;
		this.issuer = issuer;
		this.station = station;
		this.region = region;
		this.item = item;
		this.walletDivision = walletDivision;
	}

	public ApiMarketOrder getMarketOrder() {
		return marketOrder;
	}

	public ApiMember getIssuer() {
		return issuer;
	}

	public Station getStation() {
		return station;
	}

	public Region getRegion() {
		return region;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MarketOrder) {
			return marketOrder.getOrderID() == ((MarketOrder) obj).marketOrder.getOrderID();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) marketOrder.getOrderID();
	}
	
	@Override
	public int compareTo(MarketOrder o) {
		if (marketOrder.getIssued() == null || o.marketOrder.getIssued() == null) {
			System.out.println("null");
		}
		return Long.compare(marketOrder.getIssued().getTime(), o.marketOrder.getIssued().getTime());
	}

	public Item getItem() {
		return item;
	}

	public String getWalletDivision() {
		return walletDivision;
	}
}
