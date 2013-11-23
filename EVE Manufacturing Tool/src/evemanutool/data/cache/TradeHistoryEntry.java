package evemanutool.data.cache;

import java.util.ArrayList;
import java.util.Collection;

import evemanutool.constants.DBConstants;
import evemanutool.utils.file.Parsable;
import evemanutool.utils.file.ParseTools;

public class TradeHistoryEntry implements Parsable<TradeHistoryEntry>, DBConstants {
	
	/*
	 * Use:
	 * History list should always be sorted,
	 * this is the programmers responsibility to maintain when using this class.
	 */
	
	//Id.
	private int typeId;
	
	//Location.
	private long locationId;
	
	//Data
	private ArrayList<TradeEntry> history;
	
	public TradeHistoryEntry() {}
	
	public TradeHistoryEntry(int typeId, long locationId) {
		this.typeId = typeId;
		this.locationId = locationId;
		this.history = new ArrayList<>();
	}

	public void setHistory(Collection<TradeEntry> history) {
		this.history = new ArrayList<>(history);
	}

	public ArrayList<TradeEntry> getHistory() {
		return history;
	}
	
	public int getTypeId() {
		return typeId;
	}
	
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	
	public long getLocationId() {
		return locationId;
	}
	
	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TradeHistoryEntry) {
			return ((TradeHistoryEntry) obj).getTypeId() == getTypeId();
		}
		return false;
	}

	@Override
	public String toParseString() {
		ArrayList<Object> ss = new ArrayList<>();
		ss.add(typeId); ss.add(locationId); ss.add(ParseTools.joinParsables(history, LEVEL3_DELIM));
		return ParseTools.join(ss, LEVEL2_DELIM);
	}

	@Override
	public TradeHistoryEntry fromParseString(String s) {
		String[] ss = s.split(LEVEL2_DELIM, -1);
		setTypeId(Integer.parseInt(ss[0]));
		setLocationId(Long.parseLong(ss[1]));
		setHistory(ParseTools.breakParsables(ss[2], LEVEL3_DELIM, new TradeEntry()));
		return this;
	}
}
