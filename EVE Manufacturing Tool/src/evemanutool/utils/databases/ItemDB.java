package evemanutool.utils.databases;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import au.com.bytecode.opencsv.CSVReader;
import evemanutool.constants.DBConstants;
import evemanutool.data.database.Item;
import evemanutool.data.database.Material;
import evemanutool.utils.datahandling.Database;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;

public class ItemDB extends Database implements DBConstants {

	//Data.
	private ConcurrentHashMap<Integer, Item> db = new ConcurrentHashMap<>();
	
	public ItemDB() {
		super(true, false, Stage.RAW, Stage.NESTED);
	}

	@Override
	public synchronized void loadRawData() throws Exception {
		
		//Temporary variables.
		String[] nextLine;
		int id;
		
		//Parse Meta levels.
		HashMap<Integer, Integer> metaMap = new HashMap<>();
		
		CSVReader csv = new CSVReader(new FileReader(TYPE_ATTRIBUTES_PATH), ';');
		
		//Skip header.
		csv.readNext();
		
		//Add Meta level (attribute Id 633).
		while ((nextLine = csv.readNext()) != null) {
			
			id = Integer.parseInt(nextLine[1]);
			
			if (id == META_LEVEL_ATTR) {
				metaMap.put(Integer.parseInt(nextLine[0]),(int) getAttributValue(nextLine[2], nextLine[3]));
			}
	    }
		csv.close();
		
		//Parse the metaGroups.
		HashMap<Integer, Integer> metaGroupMap = new HashMap<>(); //Item typeId, MetaGroupId.
		csv = new CSVReader(new FileReader(META_TYPES_PATH), ';');
		
		//Skip header.
		csv.readNext();
		
		while ((nextLine = csv.readNext()) != null) {
			
			//Add tech-lvl item.
			metaGroupMap.put(Integer.parseInt(nextLine[0]), Integer.parseInt(nextLine[2]));
			
			//Add tech-I item (parent).
			metaGroupMap.put(Integer.parseInt(nextLine[1]), 1);
			
		}
		csv.close();
		
		//Parse ItemTypes.
		csv = new CSVReader(new FileReader(TYPES_PATH), ';');
		
		//Skip header.
		csv.readNext();
		
		while ((nextLine = csv.readNext()) != null) {
			
			id = Integer.parseInt(nextLine[0]);
			db.put(id, new Item(	nextLine[2], id, Integer.parseInt(nextLine[7]), nextLine[3], 
									Double.parseDouble(nextLine[5].replace(',', '.')), 
									!nextLine[11].equals("") ? Integer.parseInt(nextLine[11]) : 0,
									!nextLine[11].equals(""),
									metaMap.get(id), Integer.parseInt(nextLine[1]), metaGroupMap.get(id),
									!nextLine[8].equals("") ? Integer.parseInt(nextLine[8]) : null,
									!nextLine[9].equals("") ? Double.parseDouble(nextLine[9].replace(',', '.')) : 0));
	    }
		csv.close();
		
		//Read the mineral requirement list.
		csv = new CSVReader(new FileReader(TYPE_MATERIALS_PATH), ';');
								
		//Skip header.
		csv.readNext();
							
		while ((nextLine = csv.readNext()) != null) {
										
			getItem(Integer.parseInt(nextLine[0])).getBaseMaterials().add(
					new Material(getItem(Integer.parseInt(nextLine[1])), Integer.parseInt(nextLine[2])));					
		}					
		csv.close();	
		
		//Last initiation step, set complete.
		super.setComplete(true);
	}
	
	private double getAttributValue(String s1, String s2) {
		
		try {
			return Integer.parseInt(s1);
		} catch (Exception e) {}
		
		try {
			return Double.parseDouble(s2);
		} catch (Exception e) {}
		
		return 0;
	}

	public Item getItem(Integer typeId) {
		//Returns the corresponding item.
		return db.get(typeId);
	}
	
	public ArrayList<Item> getItems(Collection<Integer> typeIds) {
		//Returns a list of corresponding items to the id:s.
		ArrayList<Item> ans = new ArrayList<>();
		for (Integer id : typeIds) {
			ans.add(db.get(id));
		}
		return ans;
	}
	
	public ArrayList<Item> getItemsInGroup(int groupId) {
		//Returns all items in the given marketGroup.
		ArrayList<Item> ans = new ArrayList<>();
		for (Item item : db.values()) {
			if (item.getItemGroup() == groupId) {
				ans.add(item);
			}
		}
		return ans;
	}
	
	public ArrayList<Item> getItemsInMarketGroup(int groupId) {
		
		//Returns a list of all items in the given group. 
		ArrayList<Item> ans = new ArrayList<>();
		for (Item item : db.values()) {
			if (item.getMarketGroup() == groupId) {
				ans.add(item);
			}
		}
		return ans;
	}
}
