package evemanutool.utils.databases;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import au.com.bytecode.opencsv.CSVReader;
import evemanutool.constants.DBConstants;
import evemanutool.data.database.Blueprint;
import evemanutool.data.database.Decryptor;
import evemanutool.data.database.Item;
import evemanutool.data.database.MarketGroup;
import evemanutool.data.database.Material;
import evemanutool.data.database.Relic;
import evemanutool.gui.main.EMT;
import evemanutool.utils.datahandling.Database;
import evemanutool.utils.datahandling.DatabaseHandler.Stage;

public class BlueprintDB extends Database implements DBConstants {
	
	//DB:s
	private ItemDB idb;
	private MarketGroupDB mdb;
	private TechDB tdb;
	
	//Data.
	private volatile HashMap<Integer, Blueprint> dbB;
	private volatile HashMap<Integer, Blueprint> dbP;
	
	//List of market types.
	private volatile HashSet<Integer> idL;
	private volatile HashSet<Integer> idSL; //Shorter list.
	
	public BlueprintDB() {
		super(true, false, Stage.DERIVED, Stage.PROCESS);
	}
	
	public void init(ItemDB idb, MarketGroupDB mdb, TechDB tdb) {
		this.idb = idb;
		this.mdb = mdb;
		this.tdb = tdb;
	}

	@Override
	public synchronized void loadDerivedData() throws Exception {
		
		
		//Temporary variables.
		HashMap<Integer, Blueprint> tmpDbB = new HashMap<Integer, Blueprint>();
		HashMap<Integer, Blueprint> tmpDbP = new HashMap<Integer, Blueprint>();
		String[] nextLine;
		Blueprint b;
		Item i;
		MarketGroup mg;
		int id;
		double tmpValue;
		
		//Read the BPO list.
		CSVReader csv = new CSVReader(new FileReader(BLUEPRINTS_PATH), ';');
		
		//Skip header.
		csv.readNext();

		while ((nextLine = csv.readNext()) != null) {
						
		id = Integer.parseInt(nextLine[0]);
		i = idb.getItem(Integer.parseInt(nextLine[2]));
		
			//Only add if the BPO-product and BPO is on market.
			if (i.isOnMarket()) {

				//Add to DB.
				add(	new Blueprint(	idb.getItem(id), i, 
						Integer.parseInt(nextLine[4]), Integer.parseInt(nextLine[12]), i.getPortionSize(), 
						Integer.parseInt(nextLine[3]), Integer.parseInt(nextLine[11]), Integer.parseInt(nextLine[9]),
						Integer.parseInt(nextLine[7]) * 2, //Base is only half of max runs.
						Integer.parseInt(nextLine[8]),
						Integer.parseInt(nextLine[6]), Integer.parseInt(nextLine[5]),
						getInventionChance(i, tdb), getRevDecryptor(i, idb)),
						tmpDbB, tmpDbP); 
			}
		}
		csv.close();
		
		//Read the material requirement list.
		csv = new CSVReader(new FileReader(TYPE_REQUIREMENTS_PATH), ';');
				
		//Skip header.
		csv.readNext();
			
		while ((nextLine = csv.readNext()) != null) {
			
			b = tmpDbB.get(Integer.parseInt(nextLine[0]));
			
			//1 == manufacturing.
			if (b != null) {
				
				i = idb.getItem(Integer.parseInt(nextLine[2]));
				
				mg = mdb.getParentpByGroup(i.getMarketGroup());
				//Skip if item is a skill.
				if (mg != null && mg.getName().equalsIgnoreCase("skills")) {
					continue;
				}
				
				if (Integer.parseInt(nextLine[1]) == IndustryActivity.MANUFACTURE.key) {
					
					//Add extra material, (Amount = Quantity * Damage_Per_Job).
					b.getExtraMaterials().add(new Material(i, 
							Integer.parseInt(nextLine[3]) * Double.parseDouble(nextLine[4].replace(',', '.')), Integer.parseInt(nextLine[5]) == 1));		
				}else if (Integer.parseInt(nextLine[1]) == IndustryActivity.INVENTION.key) {
					
					tmpValue = Integer.parseInt(nextLine[3]) * Double.parseDouble(nextLine[4].replace(',', '.'));
					
					//If interface, add decryptors and set amount to 0.
					if (i.getItemGroup() == DATA_INTERFACES_GROUP) {
						b.getInvDecryptors().addAll(tdb.getDecryptorsFromInterface(i.getTypeId()));
						tmpValue = 0;
					}
					
					//Add to invention list. (Set amounts for Interfaces to 0, (base amount: 1))
					b.getInvMaterials().add(new Material(i, tmpValue));
				}
			}
		}		
		csv.close();
		
		//Add revRelics to T3 blueprints.
		for (Blueprint bP : tmpDbB.values()) {
			if (bP.getTechLevel() == 3) {
				bP.getRevRelics().addAll(tdb.getRevRelicsFromProduct(bP.getProduct()));
			}
		}
		
		//Check for BPO:s with non accessible items and add all approved to query list.
		ArrayList<Blueprint> removeList = new ArrayList<>();
		HashSet<Integer> tmpIdL = new HashSet<>();
		HashSet<Integer> tmpIdSL = new HashSet<>();
		
		for (Blueprint bl : tmpDbB.values()) {
			if (hasNonMarketMaterials(bl.getExtraMaterials()) || hasNonMarketMaterials(bl.getProduct().getBaseMaterials())) {
				
				removeList.add(bl);
			} else {

				//Add to market query id:s.
				tmpIdL.add(bl.getProduct().getTypeId());
				tmpIdSL.add(bl.getProduct().getTypeId());
				//Add BPO if on market.
				if (bl.getBlueprintItem().isOnMarket()) {
					tmpIdL.add(bl.getBlueprintItem().getTypeId());
				}
				for (Material m : bl.getExtraMaterials()) {
					tmpIdL.add(m.getItem().getTypeId());
					tmpIdSL.add(m.getItem().getTypeId());
				}	
				for (Material m : bl.getProduct().getBaseMaterials()) {
					tmpIdL.add(m.getItem().getTypeId());
					tmpIdSL.add(m.getItem().getTypeId());
				}	
				//If blueprint can be invented add materials and decryptors.
				if (bl.getInvMaterials().size() > 0) {
					
					for (Material m : bl.getInvMaterials()) {
						tmpIdL.add(m.getItem().getTypeId());
						tmpIdSL.add(m.getItem().getTypeId());
					}	
					for (Decryptor d : bl.getInvDecryptors()) {
						tmpIdL.add(d.getDecryptor().getTypeId());
						tmpIdSL.add(d.getDecryptor().getTypeId());
					}	
					for (Item item : tdb.getT1Items(bl.getProduct().getTypeId())) {
						tmpIdL.add(item.getTypeId());
						tmpIdSL.add(item.getTypeId());
					}
				}
				//If blueprint can be reverse engineered add materials.
				if (bl.getRevDecryptor() != null && bl.getRevRelics().size() > 0) {
					tmpIdL.add(bl.getRevDecryptor().getTypeId());
					tmpIdSL.add(bl.getRevDecryptor().getTypeId());
					for (Relic rel : bl.getRevRelics()) {
						tmpIdL.add(rel.getRelic().getTypeId());
						tmpIdSL.add(rel.getRelic().getTypeId());
						for (Material m : rel.getRevMaterials()) {
							tmpIdL.add(m.getItem().getTypeId());
							tmpIdSL.add(m.getItem().getTypeId());
						}
					}
				}
			}
		}
		//Remove invalid blueprints.
		for (Blueprint blueprint : removeList) {
			remove(blueprint, tmpDbB, tmpDbP);
		}
		
		//Set new database to global reference.
		dbB = tmpDbB;
		dbP = tmpDbP;
		idL = tmpIdL;
		idSL = tmpIdSL;
		
		//Show message.
		EMT.M_HANDLER.addMessage("Blueprint data collected and merged.");

		//Last initiation step, set complete.
		super.setComplete(true);
	}

	private Item getRevDecryptor(Item i, ItemDB idb) {
		
		for (Item dec : idb.getItemsInGroup(REV_DECRYPTOR_GROUP_ID)) {
			if (dec.getRaceId() == i.getRaceId()) {
				return dec;
			}
		}
		return null;
	}

	private double getInventionChance(Item product, TechDB tdb) {
		
		Integer id = product.getItemGroup();
		
		if (Arrays.asList(INV_20_CHANCE_MODGROUPS).contains(id) || product.getTypeId() == INV_20_CHANCE_MODID) {
			return 0.20;
		} else if (Arrays.asList(INV_25_CHANCE_MODGROUPS).contains(id) || product.getTypeId() == INV_25_CHANCE_MODID) {
			return 0.25;
		} else if (Arrays.asList(INV_30_CHANCE_MODGROUPS).contains(id) || product.getTypeId() == INV_30_CHANCE_MODID){
			return 0.30;
		} else if (tdb.getT2Item(product.getTypeId()) != null){
			return 0.40;
		}
		return 0.0;
	}

	private boolean hasNonMarketMaterials(Collection<Material> mL) {
		
		for (Material m : mL) {
			if (!m.getItem().isOnMarket()) {
				return true;
			}
		}
		return false;
	}
	
	private void remove(Blueprint b, HashMap<Integer, Blueprint> dbB, HashMap<Integer, Blueprint> dbP) {
		dbB.remove(b.getBlueprintItem().getTypeId()); 
		dbP.remove(b.getProduct().getTypeId());
	}

	private void add(Blueprint b, HashMap<Integer, Blueprint> dbB, HashMap<Integer, Blueprint> dbP) {
		
		dbB.put(b.getBlueprintItem().getTypeId(), b);
		dbP.put(b.getProduct().getTypeId(), b);
	}

	private Blueprint getByBpId(Integer typeId) {
		return dbB.get(typeId);
	}
	
	private Blueprint getByPrId(Integer typeId) {
		return dbP.get(typeId);
	}

	public Blueprint getByBlueprintId(Integer typeId) {
		if (dbB.containsKey(typeId)) {
			return new Blueprint(getByBpId(typeId));
		}
		return null;
	}

	public Blueprint getByProductId(Integer typeId) {
		if (dbP.containsKey(typeId)) {
			return new Blueprint(getByPrId(typeId));
		}
		return null;
	}
	
	public ArrayList<Integer> getMinMarketQueryIds() {
		return new ArrayList<>(idSL);
	}

	public ArrayList<Integer> getMarketQueryIds() {
		return new ArrayList<>(idL);
	}
	
	public ArrayList<Blueprint> getCompleteList() {
		
		//Copies the HashMap to a separate array.
		ArrayList<Blueprint> ba = new ArrayList<>(dbB.size());
		
		for (Blueprint b : dbB.values()) {
			
			ba.add(new Blueprint(b));
		}	
		return ba;
	}
}
