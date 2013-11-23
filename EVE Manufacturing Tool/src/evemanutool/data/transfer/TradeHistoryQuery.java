package evemanutool.data.transfer;

public class TradeHistoryQuery implements Comparable<TradeHistoryQuery>{

	private final int days;
	private final int typeId;
	
	public TradeHistoryQuery(int days, int typeId) {
		this.days = days;
		this.typeId = typeId;
	}

	public int getDays() {
		return days;
	}

	public int getTypeId() {
		return typeId;
	}

	@Override
	public int compareTo(TradeHistoryQuery mHQ) {
		return Integer.compare(getDays(), mHQ.getDays());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TradeHistoryQuery) {
			return ((TradeHistoryQuery) obj).getTypeId() == getTypeId();
		}
		return false;
	}
}
