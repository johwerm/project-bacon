package evemanutool.utils.databases;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import au.com.bytecode.opencsv.CSVReader;
import evemanutool.constants.DBConstants;
import evemanutool.data.database.Blueprint;
import evemanutool.data.database.Decryptor;
import evemanutool.data.database.Item;
import evemanutool.data.database.Material;
import evemanutool.data.database.Relic;
import evemanutool.gui.main.EMT;
import evemanutool.utils.datahandling.Database;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;

public class TechDB extends Database implements DBConstants{

	//DB:s.
	private ItemDB idb;
	
	//Data.
	private volatile HashMap<Integer, ArrayList<Item>> mdb; //Parent typeId, Meta Item variations.
	private volatile HashMap<Integer, ArrayList<Decryptor>> ddb; //Interface Id, Decryptor list.
	private volatile HashMap<Integer, ArrayList<Material>> rdb; //Relic id, Material list.
	private ConcurrentHashMap<Integer, HashMap<Integer, Double>> didb = new ConcurrentHashMap<>(); //Decryptor Id, Attributes.

	public TechDB() {
		super(true, false, Stage.NESTED, Stage.DERIVED);
	}
	
	public void init(ItemDB idb) {
		this.idb = idb;
	}

	@Override
	public synchronized void loadRawData() throws Exception {
		
		//Temporary variables.
		String[] nextLine;
		int id;
		
		//Parse attributes for decryptors.
		CSVReader csv = new CSVReader(new FileReader(TYPE_ATTRIBUTES_PATH), ';');
		
		//Skip header.
		csv.readNext();
		
		//Check for decryptor attributes.
		while ((nextLine = csv.readNext()) != null) {
			
			id = Integer.parseInt(nextLine[1]);
			
			//Look for decryptor attributes.
			if (id == INV_PROB_MULTIPLIER_ATTR || id == INV_ME_MODIFIER_ATTR
					|| id == INV_PE_MODIFIER_ATTR || id == INV_MAX_RUNS_MODIFIER_ATTR) {
				
				if (!didb.containsKey(Integer.parseInt(nextLine[0]))) {
					didb.put(Integer.parseInt(nextLine[0]), new HashMap<Integer, Double>());
				}
				didb.get(Integer.parseInt(nextLine[0])).put(id, getAttributValue(nextLine[2], nextLine[3]));
			}
	    }
		csv.close();
	}
	
	@Override
	public synchronized void loadNestedData() throws Exception {
		
		//Temporary variables.
		HashMap<Integer, ArrayList<Item>> tmpMdb = new HashMap<>();
		HashMap<Integer, ArrayList<Decryptor>> tmpDdb = new HashMap<>();
		HashMap<Integer, ArrayList<Material>> tmpRdb = new HashMap<>();
		String[] nextLine;
		int id;
		ArrayList<Item> tmpList;
		
		//Parse Meta levels.
		CSVReader csv = new CSVReader(new FileReader(META_TYPES_PATH), ';');
		
		//Skip header.
		csv.readNext();
		
		//Add all sub items.
		while ((nextLine = csv.readNext()) != null) {
			
			id = Integer.parseInt(nextLine[1]);
			tmpList = tmpMdb.get(id);
			
			//Create new list if null.
			if (tmpList == null) {
				tmpList = new ArrayList<>();
				tmpMdb.put(id, tmpList);
			}
			tmpList.add(idb.getItem(Integer.parseInt(nextLine[0])));
	    }
		csv.close();
		
		//Parse attributes for interfaces and decryptor groups.
		ArrayList<Decryptor> decList;
		csv = new CSVReader(new FileReader(TYPE_ATTRIBUTES_PATH), ';');
		
		//Skip header.
		csv.readNext();
		
		//Add decryptor lists..
		while ((nextLine = csv.readNext()) != null) {
			
			id = Integer.parseInt(nextLine[1]);
			
			if (id == DECRYPTORS_ATTR) {
				decList = new ArrayList<>();
				for (Item i : idb.getItemsInGroup((int) getAttributValue(nextLine[2], nextLine[3]))) {
					decList.add(new Decryptor(i, didb.get(i.getTypeId()).get(INV_PROB_MULTIPLIER_ATTR),
											didb.get(i.getTypeId()).get(INV_ME_MODIFIER_ATTR).intValue(), 
											didb.get(i.getTypeId()).get(INV_PE_MODIFIER_ATTR).intValue(), 
											didb.get(i.getTypeId()).get(INV_MAX_RUNS_MODIFIER_ATTR).intValue()));
				}
				tmpDdb.put(Integer.parseInt(nextLine[0]), decList);
			}
	    }
		csv.close();
		
		//Add the relics.
		for (Item rel : idb.getItemsInMarketGroup(REV_RELIC_MARKET_GROUP_ID)) {
			tmpRdb.put(rel.getTypeId(), new ArrayList<Material>());
		}
		
		//Parse ramTypeRequirements for reversed engineering materials.
		csv = new CSVReader(new FileReader(TYPE_REQUIREMENTS_PATH), ';');
		
		//Skip header.
		csv.readNext();
		
		while ((nextLine = csv.readNext()) != null) {
			
			id = Integer.parseInt(nextLine[1]);
			
			if (id == IndustryActivity.REVERSE_ENGINERING.key && tmpRdb.keySet().contains(Integer.parseInt(nextLine[0]))) {
				tmpRdb.get(Integer.parseInt(nextLine[0])).add(new Material(idb.getItem(Integer.parseInt(nextLine[2])),
						Integer.parseInt(nextLine[3]) * Double.parseDouble(nextLine[4].replace(',', '.'))));
			}
	    }
		csv.close();
		
		//Set new database to global reference.
		mdb = tmpMdb;
		ddb = tmpDdb;
		rdb = tmpRdb;
		
		//Show message.
		EMT.M_HANDLER.addMessage("Decryptors, interfaces and relics loaded.");
		
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
	
	private int getRevRunModifier(Item i) {
		
		for (int j = 0; j < REV_RELIC_TYPE_LABEL.length; j++) {
			if (i.getName().startsWith(REV_RELIC_TYPE_LABEL[j])) {
				return REV_RELIC_TYPE_RUNS[j];
			}
		}
		return 0;
	}

	private double getRevBaseChance(Item i) {
		
		for (int j = 0; j < REV_RELIC_TYPE_LABEL.length; j++) {
			if (i.getName().startsWith(REV_RELIC_TYPE_LABEL[j])) {
				return REV_RELIC_TYPE_CHANCE[j];
			}
		}
		return 0;
	}

	public ArrayList<Decryptor> getDecryptorsFromInterface(int typeId) {
		return ddb.get(typeId);
	}
	
	public Item getItemWithMetaLevel(Integer parentId, int metaLevel) {

		for (Item i : mdb.get(parentId)) {
			if (i.getMetaLevel() == metaLevel) {
				return i;
			}
		}
		return null;
	}

	public Item getT2Item(Integer parentId) {

		//Return null if nonexistent.
		if (mdb.get(parentId) == null) {
			return null;
		}
		for (Item i : mdb.get(parentId)) {
		
			if (i.getMetaLevel() != null && i.getMetaLevel() == T2_META_LEVEL) {
				return i;
			}
		}
		return null;
	}
	
	public ArrayList<Item> getT1Items(Integer parentId) {

		ArrayList<Item> ans = new ArrayList<>();
		
		for (Item i : mdb.get(parentId)) {
			if (i.getMetaLevel() != null && i.getMetaLevel() < T2_META_LEVEL) {
				ans.add(i);
			}
		}
		return ans;
	}
	
	public Integer getParentId(Item item) {
		
		for (Entry<Integer, ArrayList<Item>> e : mdb.entrySet()) {
			if (e.getValue().contains(item)) {
				return e.getKey();
			}
		}
		return null;
	}

	public boolean canBeInvented(Blueprint b, BlueprintDB bdb) {
		Blueprint bl = bdb.getByProductId(getParentId(b.getProduct()));
		if (bl == null) {
			return false;
		}
		return bl.getBaseInventionChance() > 0 && b.getTechLevel() == 2;
	}

	public boolean canBeReverseEngineered(Blueprint b) {
		
		if (b.getRevRelics().size() == 0) {
			return false;
		}
		return b.getRevRelics().get(0).getBaseChance() > 0;
	}

	public ArrayList<Relic> getRevRelicsFromProduct(Item product) {

		ArrayList<Relic> ans = new ArrayList<>();
		int index = 0;
		index = Arrays.asList(REV_TYPE_GROUP_ID).indexOf(product.getItemGroup());
		
		if (index >= 0) {
			Relic r;
			for (Item i : idb.getItemsInGroup(REV_RELIC_TYPE_GROUP_ID[index])) {
				r = new Relic(i, getRevBaseChance(i), getRevRunModifier(i));
				r.getRevMaterials().addAll(rdb.get(i.getTypeId()));
				ans.add(r);
			}
		}
		return ans;
	}
}