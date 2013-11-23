package evemanutool.data.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import evemanutool.constants.DBConstants;
import evemanutool.utils.file.Parsable;
import evemanutool.utils.file.ParseTools;

public class TradeEntry implements Parsable<TradeEntry>, Comparable<TradeEntry>, DBConstants {
	
	//Date.
	private Date date;
	
	//Values.
	public enum HistoryType {LOW,AVG,HIGH,VOLUME,ORDERS}
	
	private final HashMap<HistoryType, Double> hL = new HashMap<>();

	public TradeEntry() {}

	public TradeEntry(Date date, double lowPrice, double avgPrice,
			double highPrice, double volume, double orders) {
		setDate(date);
		setLowPrice(lowPrice);
		setAvgPrice(avgPrice);
		setHighPrice(highPrice);
		setVolume(volume);
		setOrders(orders);
	}

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public double getValue(HistoryType key) {
		return hL.get(key);
	}
	
	public double getLowPrice() {
		return hL.get(HistoryType.LOW);
	}

	public void setLowPrice(double lowPrice) {
		hL.put(HistoryType.LOW, lowPrice);
	}

	public double getAvgPrice() {
		return hL.get(HistoryType.AVG);
	}

	public void setAvgPrice(double avgPrice) {
		hL.put(HistoryType.AVG, avgPrice);
	}

	public double getHighPrice() {
		return hL.get(HistoryType.HIGH);
	}

	public void setHighPrice(double highPrice) {
		hL.put(HistoryType.HIGH, highPrice);
	}

	public double getVolume() {
		return hL.get(HistoryType.VOLUME);
	}

	public void setVolume(double volume) {
		hL.put(HistoryType.VOLUME, volume);
	}

	public double getOrders() {
		return hL.get(HistoryType.ORDERS);
	}

	public void setOrders(double orders) {
		hL.put(HistoryType.ORDERS, orders);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TradeEntry) {
			return getDate().getTime() == ((TradeEntry) obj).getDate().getTime();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) getDate().getTime();
	}

	@Override
	public int compareTo(TradeEntry o) {
		//Negate standard sort order => Latest first.
		return -Long.compare(getDate().getTime(), o.getDate().getTime());
	}

	@Override
	public String toParseString() {
		ArrayList<Object> ss = new ArrayList<>();
		ss.add(getDate().getTime()); ss.add(getLowPrice());
		ss.add(getAvgPrice()); ss.add(getHighPrice());
		ss.add(getVolume()); ss.add(getOrders());
		return ParseTools.join(ss, LEVEL4_DELIM);
	}

	@Override
	public TradeEntry fromParseString(String s) {
		String[] ss = s.split(LEVEL4_DELIM, -1);
		setDate(new Date(Long.parseLong(ss[0])));
		setLowPrice(Double.parseDouble(ss[1]));
		setAvgPrice(Double.parseDouble(ss[2]));
		setHighPrice(Double.parseDouble(ss[3]));
		setVolume(Double.parseDouble(ss[4]));
		setOrders(Double.parseDouble(ss[5]));
		return this;
	}
}
