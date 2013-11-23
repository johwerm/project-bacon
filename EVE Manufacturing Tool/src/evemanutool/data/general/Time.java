package evemanutool.data.general;

/*
 * Used to represent an positive amount of time.
 * If initiated with a negative value all is set to 0.
 */
public class Time implements Comparable<Time>{
	
	private int day;
	private int hour;
	private int min;
	private int sec;
	
	public Time() {
		
		day = 0;
		hour = 0;
		min = 0;
		sec = 0;
	}
	
	public Time(long ms) {
		
		if (ms < 0){
			ms = 0;
		}

		sec = (int) (ms / 1000);
		
		format();
	}
	
	public Time(int s) {
		
		if (s < 0){
			s = 0;
		}

		sec = s;
		
		format();
	}
	
	public Time(int d, int h, int m, int s) {
		
		if (d < 0 || h < 0 || m < 0 || s < 0){
			d = 0;
			h = 0;
			m = 0;
			s = 0;
		}

		day = d;
		hour = h;
		min = m;
		sec = s;
		
		format();
	}

	@Override
	public String toString() {
		
		return day + "d:" + hour + "h:" + min + "m:" + sec + "s";
	}
	
	// Formats the time variables to appropriate values.
	private void format() {
		
		if (sec >= 60) {
			
			min += sec / 60;
			sec = sec % 60;
		}
		
		if (min >= 60) {
			
			hour += min / 60;
			min = min % 60;
		}
		
		if (hour >= 24) {
			
			day += hour / 24;
			hour = hour % 24;
		}
	}
	
	public double toHours() {
		return (day * 24) + hour + (min / ((double) 60)) + (sec / ((double) 3600));
	}
	
	public double toSeconds() {
		return sec + min * 60 + hour * 3600 + day * 3600 * 24;
	}

	@Override
	public int compareTo(Time t) {
		
		int d1 = sec + min * 60 + hour * 3600 + day * 86400;
		int d2 = t.sec + t.min * 60 + t.hour * 3600 + t.day * 86400;
		
		if (d1 > d2) {
			return 1;
		}
		if (d1 < d2) {
			return -1;
		}
		return 0;
	}
}
