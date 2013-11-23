package evemanutool.data.transfer;

import java.util.ArrayList;

public class SimpleTradeHistoryQuery {

	private final int days;
	private final ArrayList<Integer> typeIds;
	
	public SimpleTradeHistoryQuery(int days, ArrayList<Integer> typeIds) {
		this.days = days;
		this.typeIds = typeIds;
	}

	public int getDays() {
		return days;
	}

	public ArrayList<Integer> getTypeIds() {
		return typeIds;
	}
}
