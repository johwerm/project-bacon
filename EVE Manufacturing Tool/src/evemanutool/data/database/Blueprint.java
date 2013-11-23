package evemanutool.data.database;

import java.util.ArrayList;

public class Blueprint {
	
	// BPO.
	private final Item blueprint;
	
	// Stats.
	private final Item product;
	private final int techLevel;
	private final int maxRuns;
	private final int itemsPerRun;
	private final int manuTime; //Sec.
	private final int meTime; //Sec.
	private final int peTime; //Sec.
	private final int copyTime; //Sec.
	private final int inventionTime; //Sec.
	private final double baseInventionChance;
	private final int wasteFactor;
	private final int productivityMod;
	private int me;
	private int pe;
	private ArrayList<Material> extraMaterials = new ArrayList<>();
	private ArrayList<Material> invMaterials = new ArrayList<>();
	private ArrayList<Decryptor> invDecryptors = new ArrayList<>();
	private ArrayList<Relic> revRelics = new ArrayList<>();
	private final Item revDecryptor;

	//A constructor more suited for the database.
	public Blueprint(	Item blueprint, Item product, int techLevel, int maxRuns, int itemsPerRun,
						int manuTime, int wasteFactor, int productivityMod,
						int copyTime, int inventionTime, int meTime, int peTime,
						double baseInventionChance, Item revDecryptor) {

		this.blueprint = blueprint;
		this.product = product;
		this.techLevel = techLevel;
		this.maxRuns = maxRuns;
		this.itemsPerRun = itemsPerRun;
		this.wasteFactor = wasteFactor;
		this.productivityMod = productivityMod;
		this.manuTime = manuTime;
		this.meTime = meTime;
		this.peTime = peTime;
		this.copyTime = copyTime;
		this.inventionTime = inventionTime;
		this.baseInventionChance = baseInventionChance;
		this.revDecryptor = revDecryptor;
	}
	
	public Blueprint(Blueprint b) {
		
		this.blueprint = b.getBlueprintItem();
		this.product = b.getProduct();
		this.techLevel = b.getTechLevel();
		this.maxRuns = b.getMaxRuns();
		this.itemsPerRun = b.getItemsPerRun();
		this.wasteFactor = b.getWasteFactor();
		this.productivityMod = b.getProductivityMod();
		this.manuTime = b.getManuTime();
		this.meTime = b.getMeTime();
		this.peTime = b.getPeTime();
		this.copyTime = b.getCopyTime();
		this.inventionTime = b.getInventionTime();
		this.baseInventionChance = b.getBaseInventionChance();
		this.me = b.getMe();
		this.pe = b.getPe();
		this.extraMaterials = new ArrayList<>(b.getExtraMaterials());
		this.invMaterials = new ArrayList<>(b.getInvMaterials());
		this.invDecryptors = new ArrayList<>(b.getInvDecryptors());
		this.revRelics = new ArrayList<>(b.getRevRelics());
		this.revDecryptor = b.getRevDecryptor();
	}
	
	public Item getProduct() {
		return product;
	}
	
	public int getMaxRuns() {
		return maxRuns;
	}
	
	public int getItemsPerRun() {
		return itemsPerRun;
	}
	
	public int getManuTime() {
		return manuTime;
	}
	
	public int getWasteFactor() {
		return wasteFactor;
	}
	
	public int getMe() {
		return me;
	}
	
	public void setMe(int me) {
		this.me = me;
	}
	
	public int getPe() {
		return pe;
	}
	
	public void setPe(int pe) {
		this.pe = pe;
	}
	
	public int getTechLevel() {
		return techLevel;
	}

	public int getProductivityMod() {
		return productivityMod;
	}

	public Item getBlueprintItem() {
		return blueprint;
	}

	public ArrayList<Material> getExtraMaterials() {
		return extraMaterials;
	}

	public ArrayList<Material> getInvMaterials() {
		return invMaterials;
	}

	public int getCopyTime() {
		return copyTime;
	}

	public int getInventionTime() {
		return inventionTime;
	}

	public double getBaseInventionChance() {
		return baseInventionChance;
	}

	public ArrayList<Decryptor> getInvDecryptors() {
		return invDecryptors;
	}

	public ArrayList<Relic> getRevRelics() {
		return revRelics;
	}

	public Item getRevDecryptor() {
		return revDecryptor;
	}

	public int getMeTime() {
		return meTime;
	}

	public int getPeTime() {
		return peTime;
	}
}
