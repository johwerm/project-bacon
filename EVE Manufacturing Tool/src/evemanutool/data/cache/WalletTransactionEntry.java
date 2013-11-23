package evemanutool.data.cache;

import java.util.ArrayList;
import java.util.Date;

import com.beimin.eveapi.shared.wallet.transactions.ApiWalletTransaction;

import evemanutool.constants.DBConstants;
import evemanutool.utils.file.Parsable;
import evemanutool.utils.file.ParseTools;

public class WalletTransactionEntry implements Parsable<WalletTransactionEntry>, Comparable<WalletTransactionEntry>, DBConstants {

	private long refId;
	private Date date;
	private double price;
	private int quantity;
	private int typeId;
	private CharacterEntry character;
	private CharacterEntry client;
	private long stationId;
	private String stationName;
	
	public WalletTransactionEntry() {}
	
	public WalletTransactionEntry(long refId, Date date, double price,
			int quantity, int typeId, CharacterEntry character, CharacterEntry client,
			long stationId, String stationName) {
		this.refId = refId;
		this.date = date;
		this.price = price;
		this.quantity = quantity;
		this.typeId = typeId;
		this.character = character;
		this.client = client;
		this.stationId = stationId;
		this.stationName = stationName;
	}

	public WalletTransactionEntry(ApiWalletTransaction w) {
		refId = w.getTransactionID();
		date = w.getTransactionDateTime();
		price = w.getPrice();
		quantity = w.getQuantity();
		typeId = w.getTypeID();
		character = new CharacterEntry(w.getCharacterID(), w.getCharacterName());
		client = new CharacterEntry(w.getClientID(), w.getClientName());
		stationId = w.getStationID();
		stationName = w.getStationName();
	}

	public long getRefId() {
		return refId;
	}

	public void setRefId(long refID) {
		this.refId = refID;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public CharacterEntry getCharacter() {
		return character;
	}

	public void setCharacter(CharacterEntry character) {
		this.character = character;
	}

	public CharacterEntry getClient() {
		return client;
	}

	public void setClient(CharacterEntry client) {
		this.client = client;
	}

	public long getStationId() {
		return stationId;
	}

	public void setStationId(long stationId) {
		this.stationId = stationId;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WalletTransactionEntry) {
			return getRefId() == ((WalletTransactionEntry) obj).getRefId();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) getRefId();
	}
	
	@Override
	public int compareTo(WalletTransactionEntry o) {
		//Negate standard sort order => Latest first.
		return -Long.compare(getDate().getTime(), o.getDate().getTime());
	}

	@Override
	public String toParseString() {
		ArrayList<Object> ss = new ArrayList<>();
		ss.add(getRefId()); ss.add(getDate().getTime()); ss.add(getPrice());
		ss.add(getQuantity()); ss.add(getTypeId());
		ss.add(getCharacter().toParseString()); ss.add(getClient().toParseString());
		ss.add(getStationId());	ss.add(getStationName());
		return ParseTools.join(ss, LEVEL2_DELIM);
	}

	@Override
	public WalletTransactionEntry fromParseString(String s) {
		String[] ss = s.split(LEVEL2_DELIM, -1);
		setRefId(Long.parseLong(ss[0]));
		setDate(new Date(Long.parseLong(ss[1])));
		setPrice(Double.parseDouble(ss[2]));
		setQuantity(Integer.parseInt(ss[3]));
		setTypeId(Integer.parseInt(ss[4]));
		setCharacter(new CharacterEntry().fromParseString(ss[5]));
		setClient(new CharacterEntry().fromParseString(ss[6]));
		setStationId(Integer.parseInt(ss[7]));
		setStationName(ss[8]);
		return this;
	}
}
