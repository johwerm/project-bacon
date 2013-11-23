package evemanutool.data.database;

public abstract class AbstractLocation {

	private final long locationId;
	private final String name;
	
	public AbstractLocation(long locationId, String name) {
		this.locationId = locationId;
		this.name = name;
	}
	
	public long getLocationId() {
		return locationId;
	}
	public String getName() {
		return name;
	}
}
