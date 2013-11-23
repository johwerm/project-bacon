package evemanutool.data.cache;

import java.util.ArrayList;
import java.util.Arrays;

import evemanutool.constants.DBConstants;
import evemanutool.constants.UserPrefConstants;
import evemanutool.data.database.Material;
import evemanutool.data.display.CorpProductionQuote;
import evemanutool.utils.file.Parsable;
import evemanutool.utils.file.ParseTools;

public class CorpProductionEntry implements Parsable<CorpProductionEntry>,
		DBConstants, UserPrefConstants {

	private int bpoTypeId;
	private int sellTarget;
	private boolean active;
	private ArrayList<Integer> manuMaterials = new ArrayList<>();

	// Activity code.
	private IndustryActivity activity;
	// Index for manufacturing material priority.
	private int matIndex;
	// Index for invention/reverse engineering priority. Only relevant if
	// activity != 1.
	private int specIndex;

	//Trend data.
	private ArrayList<NumberTrendEntry> marketTrend;
	private ArrayList<NumberTrendEntry> volumeTrend;
	
	public CorpProductionEntry() {}
	
	public CorpProductionEntry(CorpProductionQuote q) {
		
		bpoTypeId = q.getQuote().getBpo().getBlueprintItem().getTypeId();
		sellTarget = q.getSellTarget();
		active = q.isActive();
		manuMaterials = new ArrayList<>();

		//Condense material choices.
		for (Material m : q.getQuote().getMatList()) {
			if (m.isProduced()) {
				manuMaterials.add(m.getItem().getTypeId());
			}
		}
		//Check activity.
		if (q.getQuote().getInv() != null) {
			activity = IndustryActivity.INVENTION;
		}else if (q.getQuote().getRev() != null) {
			activity = IndustryActivity.REVERSE_ENGINERING;
		}else {
			activity = IndustryActivity.MANUFACTURE;
		}
		//Find indexes.
		matIndex = Arrays.asList(MAT_ACQUIRE_PRIO_ENUM).indexOf(q.getQuote().getPrio());
		if (activity == IndustryActivity.INVENTION) {
			specIndex = Arrays.asList(INV_PRIO_ENUM).indexOf(q.getQuote().getInv().getPrio());
		}else if (activity == IndustryActivity.REVERSE_ENGINERING) {
			specIndex = Arrays.asList(REV_PRIO_ENUM).indexOf(q.getQuote().getRev().getPrio());
		}
		
		marketTrend = q.getMarketTrend();
		volumeTrend = q.getVolumeTrend();
	}

	public int getBpoTypeId() {
		return bpoTypeId;
	}

	public void setBpoTypeId(int bpoTypeId) {
		this.bpoTypeId = bpoTypeId;
	}

	public int getSellTarget() {
		return sellTarget;
	}

	public void setSellTarget(int weekAmount) {
		this.sellTarget = weekAmount;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public ArrayList<Integer> getManuMaterials() {
		return manuMaterials;
	}

	public void setManuMaterials(ArrayList<Integer> manuMaterials) {
		this.manuMaterials = manuMaterials;
	}

	public IndustryActivity getActivity() {
		return activity;
	}

	public void setActivity(IndustryActivity activity) {
		this.activity = activity;
	}

	public int getMatIndex() {
		return matIndex;
	}

	public void setMatIndex(int matIndex) {
		this.matIndex = matIndex;
	}

	public int getSpecIndex() {
		return specIndex;
	}

	public void setSpecIndex(int specIndex) {
		this.specIndex = specIndex;
	}
	
	public ArrayList<NumberTrendEntry> getVolumeTrend() {
		return volumeTrend;
	}
	
	public void setVolumeTrend(ArrayList<NumberTrendEntry> volumeTrend) {
		this.volumeTrend = volumeTrend;
	}
	
	public ArrayList<NumberTrendEntry> getMarketTrend() {
		return marketTrend;
	}
	
	public void setMarketTrend(ArrayList<NumberTrendEntry> marketTrend) {
		this.marketTrend = marketTrend;
	}

	@Override
	public String toParseString() {
		ArrayList<Object> ss = new ArrayList<>();
		ss.add(getBpoTypeId()); ss.add(getSellTarget()); ss.add(isActive());
		ss.add(ParseTools.join(getManuMaterials(), LEVEL3_DELIM)); ss.add(getActivity());
		ss.add(getMatIndex()); ss.add(getSpecIndex());
		ss.add(ParseTools.joinParsables(getMarketTrend(), LEVEL3_DELIM));
		ss.add(ParseTools.joinParsables(getVolumeTrend(), LEVEL3_DELIM));
		return ParseTools.join(ss, LEVEL2_DELIM);
	}

	@Override
	public CorpProductionEntry fromParseString(String s) {
		String[] ss = s.split(LEVEL2_DELIM, -1);
		setBpoTypeId(Integer.parseInt(ss[0]));
		setSellTarget(Integer.parseInt(ss[1]));
		setActive(Boolean.parseBoolean(ss[2]));
		setManuMaterials(ParseTools.breakInts(ss[3], LEVEL3_DELIM));
		setActivity(IndustryActivity.valueOf(ss[4]));
		setMatIndex(Integer.parseInt(ss[5]));
		setSpecIndex(Integer.parseInt(ss[6]));
		setMarketTrend(ParseTools.breakParsables(ss[7], LEVEL3_DELIM, new NumberTrendEntry()));
		setVolumeTrend(ParseTools.breakParsables(ss[8], LEVEL3_DELIM, new NumberTrendEntry()));
		return this;
	}
}
