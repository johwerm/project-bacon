package evemanutool.utils.databases;

import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import au.com.bytecode.opencsv.CSVReader;

import com.beimin.eveapi.core.ApiAuthorization;
import com.beimin.eveapi.corporation.locations.LocationsParser;
import com.beimin.eveapi.eve.conquerablestationlist.ApiStation;
import com.beimin.eveapi.eve.conquerablestationlist.ConquerableStationListParser;
import com.beimin.eveapi.eve.conquerablestationlist.StationListResponse;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.shared.locations.ApiLocation;
import com.beimin.eveapi.shared.locations.LocationsResponse;

import evemanutool.constants.DBConstants;
import evemanutool.data.database.AbstractLocation;
import evemanutool.data.database.ConquerableStation;
import evemanutool.data.database.Constellation;
import evemanutool.data.database.Region;
import evemanutool.data.database.SolarSystem;
import evemanutool.data.database.Station;
import evemanutool.gui.main.EMT;
import evemanutool.utils.datahandling.Database;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;

public class LocationDB extends Database implements DBConstants{

	//Constants.
	private static final long LOCATION_API_DELAY = 10; //ms.

	//DB:s.
	private ItemDB idb;

	//Data.
	private ConcurrentHashMap<Long, Station> stationDb = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Long, SolarSystem> systemDb = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Long, Constellation> constellationDb = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Long, Region> regionDb = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Long, ConquerableStation> conqStationDb = new ConcurrentHashMap<>();
	
	public LocationDB() {
		super(true, false, Stage.NESTED, Stage.PROCESS);
	}
	
	public void init(ItemDB idb) {
		this.idb = idb;
	}

	@Override
	public synchronized void loadRawData() throws Exception {
		
		//Temporary variables.
		String[] nextLine;
		long id;
		
		//Parse Regions.
		CSVReader csv = new CSVReader(new FileReader(REGIONS_PATH), ';');
		
		//Skip header.
		csv.readNext();
		while ((nextLine = csv.readNext()) != null) {
			id = Long.parseLong(nextLine[0]);
			regionDb.put(id, new Region(id, nextLine[1]));
		}
		csv.close();
		
		//Parse Constellations.
		csv = new CSVReader(new FileReader(CONSTELLATIONS_PATH), ';');
		
		//Skip header.
		csv.readNext();
		while ((nextLine = csv.readNext()) != null) {
			id = Long.parseLong(nextLine[1]);
			constellationDb.put(id, new Constellation(id, Long.parseLong(nextLine[0]), nextLine[2]));
		}
		csv.close();
		
		//Parse Systems.
		csv = new CSVReader(new FileReader(SYSTEMS_PATH), ';');
		
		//Skip header.
		csv.readNext();
		while ((nextLine = csv.readNext()) != null) {
			id = Long.parseLong(nextLine[2]);
			systemDb.put(id, new SolarSystem(id, Long.parseLong(nextLine[1]), Long.parseLong(nextLine[0]), nextLine[3], 
					Double.parseDouble(nextLine[21].replace(',', '.')), nextLine[25]));
		}
		csv.close();

		//Show message.
		EMT.M_HANDLER.addMessage("Location data loaded: " + 
		(regionDb.size() + constellationDb.size() + systemDb.size()) + " entries added.");
	}
	
	@Override
	public synchronized void loadNestedData() throws Exception {

		//Temporary variables.
		String[] nextLine;
		long id;
		
		//Parse Stations.
		CSVReader csv = new CSVReader(new FileReader(STATIONS_PATH), ';');
		
		//Skip header.
		csv.readNext();

		while ((nextLine = csv.readNext()) != null) {
			id = Long.parseLong(nextLine[0]);
			stationDb.put(id, new Station(id, Long.parseLong(nextLine[8]), Long.parseLong(nextLine[9]),
					Long.parseLong(nextLine[10]), idb.getItem(Integer.parseInt(nextLine[6])), Long.parseLong(nextLine[7]),
					nextLine[11], Double.parseDouble(nextLine[15].replace(',', '.'))));
	    }
		csv.close();
		
		//Get Conquerable Station from API.
		ConquerableStationListParser parser = ConquerableStationListParser.getInstance();
		try {
			StationListResponse response = parser.getResponse();
			for (ApiStation s : response.getStations().values()) {
				conqStationDb.put((long) s.getStationID(), new ConquerableStation(s, idb.getItem(s.getStationTypeID())));
			}
		} catch (ApiException e) {
			System.err.println(e.getMessage());
		}
		
		//Show message.
		EMT.M_HANDLER.addMessage("Station data loaded: " + (stationDb.size() + conqStationDb.size()) + " stations added.");
		
		//Last initiation step, set complete.
		super.setComplete(true);
	}

	public Set<ApiLocation> getContainerNames(ApiAuthorization auth, List<Long> l) {
		//Get location/player-name data from API.
		LocationsParser parser = LocationsParser.getInstance();
		LocationsResponse response = null;
		try {
			response = parser.getResponse(auth, l);
			return response.getAll();
		} catch (ApiException e) {}
		
		//Try and retrieve all names individually.
		HashSet<ApiLocation> ans = new HashSet<>();
		ApiLocation aL = null;
		for (Long n : l) {
			aL = getContainerName(auth, n);
			try {
				Thread.sleep(LOCATION_API_DELAY);
			} catch (InterruptedException e) {}
			
			if (aL != null) {
				ans.add(aL);
			}
		}
		return ans;
	}
	
	private ApiLocation getContainerName(ApiAuthorization auth, long l) {
		//Get location/player-name data from API.
		LocationsParser parser = LocationsParser.getInstance();
		LocationsResponse response = null;
		Long[] ll = {l};
		try {
			response = parser.getResponse(auth, Arrays.asList(ll));
			for (ApiLocation aL : response.getAll()) {
				return aL;
			}
		} catch (ApiException e) {
			System.err.println("ID: " + l);
			System.err.println("Error while loading location-name data: " + e.getMessage());
			//e.printStackTrace();
		}
		return null;
	}
	
	public Station getStationById(Long id) {
		return stationDb.get(id);
	}
	
	public ConquerableStation getConquerableStation(Long id) {
		return conqStationDb.get(id);
	}

	public SolarSystem getSystemById(Long id) {
		return systemDb.get(id);
	}
	
	public Constellation getConstellationById(Long id) {
		return constellationDb.get(id);
	}
	
	public Region getRegionById(Long id) {
		return regionDb.get(id);
	}
	
	public AbstractLocation getLocationFromId(long apiLocationId) {
		
		if (apiLocationId >= 60014861 && apiLocationId < 60014928) {
			//ConquerableStation (Pre-dates player outposts), get from conquerableStations.
			return conqStationDb.get(apiLocationId);

		}else if (apiLocationId >= 60000000 && apiLocationId < 61000000) {
			//Unknown, possibly in staStations (stationDB).
			return stationDb.get(apiLocationId);
			
		}else if (apiLocationId >= 61000000 && apiLocationId < 66000000) {
			//ConquerableOutpost, get from conquerableStations.
			return conqStationDb.get(apiLocationId);
			
		}else if (apiLocationId >= 66000000 && apiLocationId < 67000000) {
			//Office conversion, subtract 6000001 to get stationId.
			return stationDb.get(apiLocationId - 6000001);
			
		}else if (apiLocationId >= 67000000 && apiLocationId < 68000000) {
			//Office conversion, subtract 6000000 to get stationId in conquerableStations.
			return conqStationDb.get(apiLocationId - 6000000);
			
		}else {
			//Location points to solar system.
			return systemDb.get(apiLocationId);
		}
	}
}
