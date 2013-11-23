package evemanutool.utils.httpdata;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import evemanutool.data.cache.TradeHistoryEntry;
import evemanutool.utils.file.ParseTools;

public class EveMarketDataQuery {
	
	//URI parts.
	private static final String EVEMARKETDATA_ADDR = "http://api.eve-marketdata.com/api/item_history2.xml?char_name=demo";
	private static final String EVEMARKETDATA_REGION = "&region_ids=";
	private static final String EVEMARKETDATA_DAYS = "&days=";
	private static final String EVEMARKETDATA_PARAM_ID = "&type_ids=";
	private static final List<TradeHistoryEntry> EMPTY = Collections.emptyList();
	
	
	public static List<TradeHistoryEntry> getMarketInfo(Collection<Integer> typeIds, String regCode, int days) {
		
		if (typeIds == null || typeIds.isEmpty()) {
            return EMPTY;
		}
		String eveMarketDataQuery = createQuery(typeIds, regCode, days);
		
		long start = System.currentTimeMillis();
        try {
                HttpURLConnection conn = (HttpURLConnection) new URL(eveMarketDataQuery).openConnection();
                
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new IOException(conn.getResponseCode() + " " + conn.getResponseMessage());
                }
                
                HistoryParser parser = new HistoryParser(conn.getInputStream(), typeIds, Long.parseLong(regCode));
                parser.parse(); 
                conn.disconnect();
                
                System.out.println("Getting history for " + typeIds.size() + 
                		" types (" + days + " days) from eve-marketdata.com... " + 
                		(System.currentTimeMillis() - start) + " ms");
                
                return parser.getResult();
                
        } catch (MalformedURLException e) {
                System.err.println(e.toString());
                return EMPTY;
        } catch (IOException e) {	
                System.err.println(e.toString() + "\t" + eveMarketDataQuery);
                return EMPTY;
        }
	}
	
	private static String createQuery(Collection<Integer> typeIds, String regCode, int days) {
		
		StringBuilder buf = new StringBuilder(EVEMARKETDATA_ADDR);
		buf.append(EVEMARKETDATA_REGION).append(regCode)
		.append(EVEMARKETDATA_DAYS).append(days)
		.append(EVEMARKETDATA_PARAM_ID).append(ParseTools.join(typeIds, ","));
		
        return buf.toString();
	}

}
