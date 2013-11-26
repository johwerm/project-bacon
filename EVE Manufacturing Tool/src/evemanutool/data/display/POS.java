package evemanutool.data.display;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.beimin.eveapi.corporation.starbase.list.ApiStarbaseState;
import com.beimin.eveapi.shared.locations.ApiLocation;

import evemanutool.data.database.Item;
import evemanutool.data.database.SolarSystem;
import evemanutool.data.general.Time;

public class POS {
	
	private final long itemId;
	private final ApiLocation apiLocation;
	private final Item controlTower;
	private final long moonId;
	private final SolarSystem system;
	private final ApiStarbaseState state;
	private final Date onlineTimestamp;
	private final Date stateTimestamp;
	private final Time fuelLeft;
	private final ArrayList<Fuel> fuelList;
	
	public POS(long itemId, ApiLocation apiLocation, Item controlTower, long moonId, SolarSystem system,
			ApiStarbaseState state, Date onlineTimestamp, Date stateTimestamp, 
			Time fuelLeft, Collection<Fuel> fuelList) {
		this.itemId = itemId;
		this.apiLocation = apiLocation;
		this.controlTower = controlTower;
		this.moonId = moonId;
		this.system = system;
		this.state = state;
		this.onlineTimestamp = onlineTimestamp;
		this.stateTimestamp = stateTimestamp;
		this.fuelLeft = fuelLeft;
		this.fuelList = new ArrayList<>(fuelList);
	}

	public long getMoonId() {
		return moonId;
	}

	public long getItemId() {
		return itemId;
	}
	
	public ApiLocation getApiLocation() {
		return apiLocation;
	}

	public Item getControlTower() {
		return controlTower;
	}

	public SolarSystem getSystem() {
		return system;
	}

	public ApiStarbaseState getState() {
		return state;
	}

	public Date getOnlineTimestamp() {
		return onlineTimestamp;
	}

	public Date getStateTimestamp() {
		return stateTimestamp;
	}

	public Time getFuelLeft() {
		return fuelLeft;
	}

	public ArrayList<Fuel> getFuelList() {
		return fuelList;
	}
}
