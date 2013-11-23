package evemanutool.data.database;

public class AbstractStation extends AbstractLocation{
	
	private final long systemId;
	private final Item item;
	private final long corporationId;
	
	public AbstractStation(long stationId, long systemId, Item item, long corporationId, String stationName) {
		super(stationId, stationName);
		this.systemId = systemId;
		this.item = item;
		this.corporationId = corporationId;
	}

	public long getSystemId() {
		return systemId;
	}

	public Item getItem() {
		return item;
	}

	public long getCorporationId() {
		return corporationId;
	}
}
