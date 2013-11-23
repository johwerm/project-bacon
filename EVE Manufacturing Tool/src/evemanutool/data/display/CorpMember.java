package evemanutool.data.display;

import com.beimin.eveapi.corporation.member.tracking.ApiMember;


public class CorpMember {

	private final long id;
	private final double totalTax;
	private final double avgWeekTax;
	private final double iskAccess;
	private final ApiMember tracking;
	
	public CorpMember(long id, double totalTax, double avgWeekTax,
			double iskAccess, ApiMember tracking) {
		this.id = id;
		this.totalTax = totalTax;
		this.avgWeekTax = avgWeekTax;
		this.iskAccess = iskAccess;
		this.tracking = tracking;
	}

	public long getId() {
		return id;
	}

	public double getTotalTax() {
		return totalTax;
	}

	public double getAvgWeekTax() {
		return avgWeekTax;
	}
	
	public double getIskAccess() {
		return iskAccess;
	}

	public ApiMember getTracking() {
		return tracking;
	}
}
