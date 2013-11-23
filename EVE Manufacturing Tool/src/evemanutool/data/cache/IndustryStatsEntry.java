package evemanutool.data.cache;

import java.util.ArrayList;
import java.util.Date;

import evemanutool.constants.DBConstants;
import evemanutool.utils.file.Parsable;
import evemanutool.utils.file.ParseTools;

public class IndustryStatsEntry implements Parsable<IndustryStatsEntry>, Comparable<IndustryStatsEntry>, DBConstants {
	
	private Date date;
	private double buyOrderEscrow;
	private double sellOrderValue;
	private double industryWallet;
	private double materialValue;
	
	public IndustryStatsEntry() {}
	
	public IndustryStatsEntry(Date date, double buyOrderEscrow, double sellOrderValue,
			double industryWallet, double materialValue) {
		
		this.date = date;
		this.buyOrderEscrow = buyOrderEscrow;
		this.sellOrderValue = sellOrderValue;
		this.industryWallet = industryWallet;
		this.materialValue = materialValue;
	}

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public double getBuyOrderEscrow() {
		return buyOrderEscrow;
	}
	
	public void setBuyOrderEscrow(double buyOrderEscrow) {
		this.buyOrderEscrow = buyOrderEscrow;
	}

	public double getSellOrderValue() {
		return sellOrderValue;
	}
	
	public void setSellOrderValue(double sellOrderValue) {
		this.sellOrderValue = sellOrderValue;
	}

	public double getIndustryWallet() {
		return industryWallet;
	}
	
	public void setIndustryWallet(double industryWallet) {
		this.industryWallet = industryWallet;
	}

	public double getMaterialValue() {
		return materialValue;
	}
	
	public void setMaterialValue(double materialValue) {
		this.materialValue = materialValue;
	}

	public double getTotalCapital() {
		return buyOrderEscrow + sellOrderValue + industryWallet + materialValue;
	}
	
	public double getMarketOrdersValue() {
		return buyOrderEscrow + sellOrderValue;
	}
	
	@Override
	public int compareTo(IndustryStatsEntry o) {
		//Negate standard sort order => Latest first.
		return -Long.compare(getDate().getTime(), o.getDate().getTime());
	}

	@Override
	public String toParseString() {
		ArrayList<Object> ss = new ArrayList<>();
		ss.add(getDate().getTime()); ss.add(getBuyOrderEscrow()); ss.add(getSellOrderValue());
		ss.add(getIndustryWallet()); ss.add(getMaterialValue());
		return ParseTools.join(ss, LEVEL2_DELIM);
	}

	@Override
	public IndustryStatsEntry fromParseString(String s) {
		String[] ss = s.split(LEVEL2_DELIM, -1);
		setDate(new Date(Long.parseLong(ss[0])));
		setBuyOrderEscrow(Double.parseDouble(ss[1]));
		setSellOrderValue(Double.parseDouble(ss[2]));
		setIndustryWallet(Double.parseDouble(ss[3]));
		setMaterialValue(Double.parseDouble(ss[4]));
		return this;
	}
}
