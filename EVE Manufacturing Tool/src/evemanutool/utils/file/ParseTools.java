package evemanutool.utils.file;

import java.util.ArrayList;
import java.util.Collection;


public class ParseTools {
	
	public static String join(Collection<?> ss, String delimiter) {
		
		String joined = "";
	    int n = 0;
	    for (Object item : ss) {
	        joined += item;
	        if (++n < ss.size())
	            joined += delimiter;
	    }
	    return joined;
	}

	public static <T extends Parsable<T>> String joinParsables(Collection<T> ss, String delimiter) {
		 
		String joined = "";
	    int n = 0;
	    for (Parsable<?> p : ss) {
	        joined += p.toParseString();
	        if (++n < ss.size())
	            joined += delimiter;
	    }
	    return joined;
	}
	
	public static ArrayList<Integer> breakInts(String s, String delimiter) {
		ArrayList<Integer> ans = new ArrayList<>();
		for (String ss : s.split(delimiter, -1)) {
			if (!ss.equals("")) {
				ans.add(Integer.parseInt(ss));
			}
		}
		return ans;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Parsable<T>> ArrayList<T> breakParsables(String s, String delimiter, T p) {
		
		ArrayList<T> ans = new ArrayList<>();
		T obj;
		for (String ss : s.split(delimiter, -1)) {
			if (!ss.equals("")) {
				try {
					obj = (T) p.getClass().newInstance();
					ans.add(obj.fromParseString(ss));
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return ans;
	}
}
