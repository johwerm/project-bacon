package evemanutool.data.database;


public final class Material {
	
	//Final values.
	private final Item item;
	private final boolean recycle;
	
	//Changeable values.
	private double amount;
	private double price;
	private boolean canBeManufactured = false;
	private Boolean produced = null;
	private ManuQuote manufactureQuote = null;
	
	
	
	public Material(Item item, double amount, boolean recycle, double price,
			boolean canBeManufactured, Boolean produced,
			ManuQuote manufactureQuote) {
		this.item = item;
		this.amount = amount;
		this.recycle = recycle;
		this.price = price;
		this.canBeManufactured = canBeManufactured;
		this.produced = produced;
		this.manufactureQuote = manufactureQuote;
	}

	public Material(Item item, double d) {

		this.item = item;
		this.amount = d;
		this.recycle = false;
	}
	
	public Material(Item item, double d, boolean recycle) {

		this.item = item;
		this.amount = d;
		this.recycle = recycle;
	}

	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Item getItem() {
		return item;
	}

	public boolean isRecycled() {
		return recycle;
	}
	
	//Changeable values.
	public Boolean isProduced() {
		return produced;
	}

	public void setProduced(Boolean produced) {
		this.produced = produced;
	}

	public ManuQuote getManufactureQuote() {
		return manufactureQuote;
	}

	public void setManufactureQuote(ManuQuote manufactureQuote) {
		this.manufactureQuote = manufactureQuote;
	}

	public boolean canBeManufactured() {
		return canBeManufactured;
	}

	public void setCanBeManufactured(boolean canBeManufactured) {
		this.canBeManufactured = canBeManufactured;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public boolean equals(Object obj) {
		//Compare the items.
		return ((Material) obj).getItem().getTypeId() == getItem().getTypeId();
	}

	@Override
	public int hashCode() {
		return getItem().getTypeId();
	}
}
