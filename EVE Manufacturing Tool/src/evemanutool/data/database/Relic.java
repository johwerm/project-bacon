package evemanutool.data.database;

import java.util.ArrayList;

public class Relic {
	
	private final Item relic;
	private final double baseChance;
	private final int revMaxRunModifier;
	private final ArrayList<Material> revMaterials = new ArrayList<>();
	
	
	public Relic(Item relic, double baseChance, int revMaxRunModifier) {
		this.relic = relic;
		this.baseChance = baseChance;
		this.revMaxRunModifier = revMaxRunModifier;
	}

	public int getRevMaxRunModifier() {
		return revMaxRunModifier;
	}

	public double getBaseChance() {
		return baseChance;
	}

	public Item getRelic() {
		return relic;
	}

	public ArrayList<Material> getRevMaterials() {
		return revMaterials;
	}
}
