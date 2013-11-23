package evemanutool.data.database;


public class Decryptor {
	
	private final Item decryptor;
	private final double invProbMultiplier;
	private final int invMEModifier;
	private final int invPEModifier;
	private final int invMaxRunModifier;
	
	
	public Decryptor(Item decryptor, double invProbMultiplier, int invMEModifier, int invPEModifier, int invMaxRunModifier) {

		this.decryptor = decryptor;
		this.invProbMultiplier = invProbMultiplier;
		this.invMEModifier = invMEModifier;
		this.invPEModifier = invPEModifier;
		this.invMaxRunModifier = invMaxRunModifier;
	}
	
	public Item getDecryptor() {
		return decryptor;
	}
	public double getInvProbMultiplier() {
		return invProbMultiplier;
	}
	public int getInvMEModifier() {
		return invMEModifier;
	}
	public int getInvPEModifier() {
		return invPEModifier;
	}

	public int getInvMaxRunModifier() {
		return invMaxRunModifier;
	}
}
