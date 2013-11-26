package evemanutool.data.database;

public class Region extends AbstractLocation{

	private final long factionId;
	
	public Region(long regionId, String regionName, long factionId) {
		super(regionId, regionName);
		
		this.factionId = factionId;
	}
	
	public long getFactionId() {
		return factionId;
	}
}
