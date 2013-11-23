package evemanutool.data.database;

import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;

public class ConquerableStation extends AbstractStation{
	
	private final String corporationName;
	
	public ConquerableStation(long stationId, String name, long systemId,
			Item item, long corporationId, String corporationName) {
		super(stationId, systemId, item, corporationId, name);
		this.corporationName = corporationName;
	}
	
	public ConquerableStation(ApiStation s, Item item) {
		this(s.getStationID(), s.getStationName(), s.getSolarSystemID(), item, s.getCorporationID(), s.getCorporationName());
	}

	public String getCorporationName() {
		return corporationName;
	}
}
