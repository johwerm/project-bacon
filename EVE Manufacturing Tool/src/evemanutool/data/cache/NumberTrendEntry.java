package evemanutool.data.cache;

import java.util.ArrayList;
import java.util.Date;

import evemanutool.constants.DBConstants;
import evemanutool.utils.file.Parsable;
import evemanutool.utils.file.ParseTools;

public class NumberTrendEntry implements Parsable<NumberTrendEntry>, Comparable<NumberTrendEntry>, DBConstants {
	
	private Date date;
	private double number;
	
	public NumberTrendEntry(Date date, double name) {
		this.date = date;
		this.number = name;
	}

	public NumberTrendEntry() {}

	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}

	public double getNumber() {
		return number;
	}
	
	public void setNumber(double number) {
		this.number = number;
	}
	
	@Override
	public int compareTo(NumberTrendEntry o) {
		//Negate standard sort order => Latest first.
		return -Long.compare(getDate().getTime(), o.getDate().getTime());
	}

	@Override
	public String toParseString() {
		ArrayList<Object> ss = new ArrayList<>();
		ss.add(getDate().getTime()); ss.add(getNumber());
		return ParseTools.join(ss, LEVEL4_DELIM);
	}

	@Override
	public NumberTrendEntry fromParseString(String s) {
		String[] ss = s.split(LEVEL4_DELIM, -1);
		setDate(new Date(Long.parseLong(ss[0])));
		setNumber(Double.parseDouble(ss[1]));
		return this;
	}
}
