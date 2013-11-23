package evemanutool.data.database;

public class SolarSystem extends AbstractLocation{

	private final long constellationId;
	private final long regionId;
	private final double security;
	private final String securityClass;
	
	public SolarSystem(long systemId, long constellationId, long regionId,
			String systemName, double security, String securityClass) {
		super(systemId, systemName);
		this.constellationId = constellationId;
		this.regionId = regionId;
		this.security = security;
		this.securityClass = securityClass;
	}

	public long getConstellationId() {
		return constellationId;
	}

	public long getRegionId() {
		return regionId;
	}

	public double getSecurity() {
		return security;
	}

	public String getSecurityClass() {
		return securityClass;
	}
}
