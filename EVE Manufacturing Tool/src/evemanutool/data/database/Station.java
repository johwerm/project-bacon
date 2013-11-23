package evemanutool.data.database;

public class Station extends AbstractStation{
	
	private final long constellationId;
	private final long regionId;
	private final double reprocessingEff;
	
	public Station(long stationId, long systemId, long constellationId,
			long regionId, Item item, long corporationId, String stationName,
			double reprocessingEff) {
		super(stationId, systemId, item, corporationId, stationName);
		this.constellationId = constellationId;
		this.regionId = regionId;
		this.reprocessingEff = reprocessingEff;
	}

	public long getConstellationId() {
		return constellationId;
	}

	public long getRegionId() {
		return regionId;
	}
	
	public double getReprocessingEff() {
		return reprocessingEff;
	}
}
