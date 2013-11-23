package evemanutool.data.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import evemanutool.constants.DBConstants;
import evemanutool.utils.file.Parsable;
import evemanutool.utils.file.ParseTools;



public class MarketInfoEntry implements Parsable<MarketInfoEntry>, DBConstants{
	
	//Price types.
	public enum OrderAim {SELL, BUY, ALL}
	
	//Id.
	private int typeId;
	
	//Location of the price.
	private long locationId;

	//Storage map.
	private HashMap<OrderAim, PriceEntry> pL = new HashMap<>();
	
	//Date of query.
	private Date date;
	
	public MarketInfoEntry() {}

	public MarketInfoEntry(int typeId, long locationId) {
		
		this.typeId = typeId;
		this.locationId = locationId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setPrice(PriceEntry p, OrderAim t) {	
		pL.put(t, p);
	}
	
	public PriceEntry getPrice(OrderAim t) {		
		return pL.get(t);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toParseString() {
		ArrayList<Object> ss = new ArrayList<>();
		ss.add(typeId); ss.add(locationId); ss.add(getPrice(OrderAim.ALL).toParseString()); ss.add(getPrice(OrderAim.BUY).toParseString());
		ss.add(getPrice(OrderAim.SELL).toParseString()); ss.add(date.getTime());
		return ParseTools.join(ss, LEVEL2_DELIM);
	}

	@Override
	public MarketInfoEntry fromParseString(String s) {
		
		String[] ss = s.split(LEVEL2_DELIM);
		setTypeId(Integer.parseInt(ss[0]));
		setLocationId(Long.parseLong(ss[1]));
		setPrice(new PriceEntry().fromParseString(ss[2]), OrderAim.ALL);
		setPrice(new PriceEntry().fromParseString(ss[3]), OrderAim.BUY);
		setPrice(new PriceEntry().fromParseString(ss[4]), OrderAim.SELL);
		setDate(new Date(Long.parseLong(ss[5])));
		
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof MarketInfoEntry) {
			return ((MarketInfoEntry) obj).typeId == typeId;
		}
		return false;
	}
}
