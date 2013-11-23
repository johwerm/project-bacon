package evemanutool.data.cache;

import java.util.ArrayList;
import java.util.Date;

import com.beimin.eveapi.shared.wallet.RefType;
import com.beimin.eveapi.shared.wallet.journal.ApiJournalEntry;

import evemanutool.constants.DBConstants;
import evemanutool.utils.file.Parsable;
import evemanutool.utils.file.ParseTools;


public class WalletJournalEntry implements Parsable<WalletJournalEntry>, Comparable<WalletJournalEntry>, DBConstants {
	
	private long refId;
	private Date date;
	private double amount;
	private double balance;
	private CharacterEntry sender;
	private CharacterEntry receiver;
	private RefType type;
	private String comment;
	
	public WalletJournalEntry() {}
	
	public WalletJournalEntry(long refId, Date date, double amount,
			double balance, CharacterEntry sender, CharacterEntry receiver, RefType type,
			String comment) {
		this.refId = refId;
		this.date = date;
		this.amount = amount;
		this.balance = balance;
		this.sender = sender;
		this.receiver = receiver;
		this.type = type;
		this.comment = comment;
	}

	public WalletJournalEntry(ApiJournalEntry e) {
		
		refId = e.getRefID();
		date = e.getDate();
		amount = e.getAmount();
		balance = e.getBalance();
		sender = new CharacterEntry(e.getOwnerID1(), e.getOwnerName1());
		receiver = new CharacterEntry(e.getOwnerID2(), e.getOwnerName2());
		type = e.getRefType();
		comment = e.getReason();
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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public CharacterEntry getSender() {
		return sender;
	}

	public void setSender(CharacterEntry sender) {
		this.sender = sender;
	}

	public CharacterEntry getReceiver() {
		return receiver;
	}

	public void setReceiver(CharacterEntry receiver) {
		this.receiver = receiver;
	}

	public RefType getType() {
		return type;
	}

	public void setType(RefType type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WalletJournalEntry) {
			return getRefId() == ((WalletJournalEntry) obj).getRefId();
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) getRefId();
	}
	
	@Override
	public int compareTo(WalletJournalEntry o) {
		//Negate standard sort order => Latest first.
		return -Long.compare(getDate().getTime(), o.getDate().getTime());
	}

	@Override
	public String toParseString() {
		ArrayList<Object> ss = new ArrayList<>();
		ss.add(getRefId()); ss.add(getDate().getTime()); ss.add(getAmount());
		ss.add(getBalance()); ss.add(getSender().toParseString());
		ss.add(getReceiver().toParseString()); ss.add(getType().getId());
		ss.add(getComment());
		return ParseTools.join(ss, LEVEL2_DELIM);
	}

	@Override
	public WalletJournalEntry fromParseString(String s) {
		String[] ss = s.split(LEVEL2_DELIM, -1);
		setRefId(Long.parseLong(ss[0]));
		setDate(new Date(Long.parseLong(ss[1])));
		setAmount(Double.parseDouble(ss[2]));
		setBalance(Double.parseDouble(ss[3]));
		setSender(new CharacterEntry().fromParseString(ss[4]));
		setReceiver(new CharacterEntry().fromParseString(ss[5]));
		setType(RefType.forID(Integer.parseInt(ss[6])));
		setComment(ss[7]);
		return this;
	}
}
