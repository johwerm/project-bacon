package evemanutool.data.cache;

import java.util.ArrayList;
import java.util.HashMap;

import evemanutool.constants.DBConstants;
import evemanutool.utils.file.Parsable;
import evemanutool.utils.file.ParseTools;


public class PriceEntry implements Parsable<PriceEntry>, DBConstants{

	HashMap<PriceType, Double> pL = new HashMap<>(); 
	
	//Price types.
	public enum PriceType {VOLUME, AVG, MAX, MIN, STDDEV, MEDIAN, PERCENTILE}
	
	public PriceEntry(	double volume, double avg, double max, double min,
					double stddev, double median, double percentile) {
		
		setValue(volume, PriceType.VOLUME);
		setValue(avg, PriceType.AVG);
		setValue(max, PriceType.MAX);
		setValue(min, PriceType.MIN);
		setValue(stddev, PriceType.STDDEV);
		setValue(median, PriceType.MEDIAN);
		setValue(percentile, PriceType.PERCENTILE);
	}

	public PriceEntry() {
	}

	public void setValue(double d, PriceType t) {
		pL.put(t, d);
	}
	
	public double getValue(PriceType t) {
		return pL.get(t);
	}

	@Override
	public String toParseString() {
		
		ArrayList<Double> l = new ArrayList<>();
		
		for (PriceType t : PriceType.values()) {
			l.add(getValue(t));
		}
		
		return ParseTools.join(l, LEVEL3_DELIM);
	}

	@Override
	public PriceEntry fromParseString(String s) {

		String[] ss = s.split(LEVEL3_DELIM);
		
		for (int i = 0; i < ss.length; i++) {
			
			setValue(Double.parseDouble(ss[i]), PriceType.values()[i]);
		}	
		return this;
	}
}
