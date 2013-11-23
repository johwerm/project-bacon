package evemanutool.data.cache;

import java.util.ArrayList;

import evemanutool.constants.DBConstants;
import evemanutool.utils.file.Parsable;
import evemanutool.utils.file.ParseTools;

public class BlueprintAssetEntry implements Parsable<BlueprintAssetEntry>, DBConstants {
	
	private int typeId;
	private long itemId;
	private boolean isBpo;
	private int me;
	private int pe;
	
	public BlueprintAssetEntry() {}

	public BlueprintAssetEntry(int typeId, long itemId, boolean isBpo) {
		this.typeId = typeId;
		this.itemId = itemId;
		this.isBpo = isBpo;
	}

	public BlueprintAssetEntry(int typeId, long itemId, boolean isBpo, int me,
			int pe) {
		this.typeId = typeId;
		this.itemId = itemId;
		this.isBpo = isBpo;
		this.me = me;
		this.pe = pe;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public boolean isBpo() {
		return isBpo;
	}

	public void setBpo(boolean isBpo) {
		this.isBpo = isBpo;
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

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BlueprintAssetEntry) {
			return getItemId() == ((BlueprintAssetEntry) obj).getItemId();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) getItemId();
	}

	@Override
	public String toParseString() {
		ArrayList<Object> ss = new ArrayList<>();
		ss.add(getTypeId()); ss.add(getItemId()); ss.add(isBpo());
		ss.add(getMe()); ss.add(getPe());
		return ParseTools.join(ss, LEVEL2_DELIM);
	}

	@Override
	public BlueprintAssetEntry fromParseString(String s) {
		String[] ss = s.split(LEVEL2_DELIM);
		setTypeId(Integer.parseInt(ss[0]));
		setItemId(Long.parseLong(ss[1]));
		setBpo(Boolean.parseBoolean(ss[2]));
		setMe(Integer.parseInt(ss[3]));
		setPe(Integer.parseInt(ss[4]));
		return this;
	}
}
