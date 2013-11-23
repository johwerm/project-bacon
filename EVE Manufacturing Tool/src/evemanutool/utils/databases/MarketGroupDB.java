package evemanutool.utils.databases;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;

import au.com.bytecode.opencsv.CSVReader;
import evemanutool.constants.DBConstants;
import evemanutool.data.database.MarketGroup;
import evemanutool.gui.main.EMT;
import evemanutool.utils.datahandling.Database;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;

public class MarketGroupDB extends Database implements DBConstants{

	//Data.
	private volatile ArrayList<MarketGroup> db = new ArrayList<>();
	
	public MarketGroupDB() {
		super(true, false, Stage.RAW, Stage.DERIVED);
	}
	
	@Override
	public synchronized void loadRawData() throws Exception {

		//Temporary variables.
		ArrayList<MarketGroup> tmpDb = new ArrayList<>();
		ArrayList<String[]> allGroups = new ArrayList<>();
		ArrayList<Integer> tmpGroups = new ArrayList<>();
		String[] nextLine;
		
		//Parse Top level MarketGroups.
		CSVReader csv = new CSVReader(new FileReader(MARKET_GROUP_PATH), ';');
		
		//Skip header.
		csv.readNext();
		//Add all topLevel parents to the list.
		while ((nextLine = csv.readNext()) != null) {
			
			//If group has no parent.
			if (nextLine[1].equals("")) {
				
				tmpDb.add(new MarketGroup(nextLine[2], Integer.parseInt(nextLine[0]), nextLine[3]));
			}
	    }
		csv.close();
		
		//Read all.
		csv = new CSVReader(new FileReader(MARKET_GROUP_PATH), ';');
		
		//Skip header.
		csv.readNext();
		allGroups.addAll(csv.readAll());
		csv.close();
		
		for (MarketGroup mg : tmpDb) {
			
			tmpGroups.clear();
			tmpGroups.add(mg.getGroupId());
			
			while(!hasOnlyItemGroups(tmpGroups, allGroups)) {
				
				tmpGroups = getChildGroups(tmpGroups, allGroups);
			}
			mg.getChildGroups().addAll(tmpGroups);
		}
		//Set new database to global reference.
		db = tmpDb;
		
		//Show message.
		EMT.M_HANDLER.addMessage("Marketgroups and metadata loaded.");
		
		//Last initiation step, set complete.
		super.setComplete(true);
	}

	private ArrayList<Integer> getChildGroups(Collection<Integer> l, Collection<String[]> dataSource) {
		
		ArrayList<Integer> ans = new ArrayList<>();
			
		for (String[] line : dataSource) {
				
			//Child to parentGroup has been found.
			if (!line[1].equals("") && l.contains(Integer.parseInt(line[1]))) {
					
				ans.add(Integer.parseInt(line[0]));					
			}
		}
		
		//Remove all groups with only subGroups.
		for (String[] line : dataSource) {
			
			if (l.contains(Integer.parseInt(line[0])) && line[5].equals("0")) {
				l.remove(Integer.parseInt(line[0]));
			}
		}
		
		
		return ans;
	}
	
	private boolean hasOnlyItemGroups(Collection<Integer> l, Collection<String[]> dataSource) {
		
		for (String[] line : dataSource) {
			
			if (l.contains(Integer.parseInt(line[0])) && line[5].equals("0")) {
				
				return false;
			}
		}
		return true;		
	}
	
	public ArrayList<MarketGroup> getGroupList() {
		return db;
	}

	public ArrayList<MarketGroup> getProductGroups() {
	
		ArrayList<MarketGroup> mGL = new ArrayList<>();
		for (String name : PRODUCT_MARKET_GROUPS) {
			mGL.add(getTopGroupByName(name));
		}
		return mGL;
	}

	public MarketGroup getParentpByGroup(Integer groupId) {
		
		for (MarketGroup mg : db) {
			
			if (mg.getChildGroups().contains(groupId)) {
				return mg;
			}
		}
		return null;
	}

	public MarketGroup getTopGroupByName(String groupName) {
		
		for (MarketGroup mg : db) {
			if (mg.getName().equalsIgnoreCase(groupName)) {
				return mg;
			}
		}
		return null;
	}	
}
