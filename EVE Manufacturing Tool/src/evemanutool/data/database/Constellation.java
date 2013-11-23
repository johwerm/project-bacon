package evemanutool.data.database;

public class Constellation extends AbstractLocation{

	private final long regionId;
	
	public Constellation(long constellationId, long regionId,
			String constellationName) {
		super(constellationId, constellationName);
		this.regionId = regionId;
	}
	
	public long getRegionId() {
		return regionId;
	}
}
