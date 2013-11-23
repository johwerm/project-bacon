package evemanutool.utils.httpdata;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import evemanutool.data.cache.MarketInfoEntry;

public class EveCentralQuery {
	
	//URI parts.
	private static final String EVECENTRAL_ADDR = "http://api.eve-central.com/api/marketstat?";
	private static final String EVECENTRAL_SYSTEM = "usesystem=";
	private static final String EVECENTRAL_PARAM_ID = "&typeid=";
	private static final List<MarketInfoEntry> EMPTY = Collections.emptyList();
	
	
	public static List<MarketInfoEntry> getMarketInfo(Collection<Integer> typeIds, String sysCode) {
		
		if (typeIds == null || typeIds.isEmpty()) {
            return EMPTY;
		}
		String evecentralQuery = createQuery(typeIds, sysCode);
		
		long start = System.currentTimeMillis();
        try {
                HttpURLConnection conn = (HttpURLConnection) new URL(evecentralQuery).openConnection();
                
                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new IOException(conn.getResponseCode() + " " + conn.getResponseMessage());
                }
                
                PriceParser parser = new PriceParser(conn.getInputStream(), Long.parseLong(sysCode));
                parser.parse(); 
                conn.disconnect();
                
                System.out.println("Getting " + typeIds.size() + " prices from eve-central.com... " +
                		(System.currentTimeMillis() - start) + " ms");
                
                return parser.getResult();
                
        } catch (MalformedURLException e) {
                System.err.println(e.toString());
                return EMPTY;
        } catch (IOException e) {	
                System.err.println(e.toString() + "\t" + evecentralQuery);
                return EMPTY;
        }
	}
	
	private static String createQuery(Collection<Integer> typeIds, String sysCode) {
		
		StringBuilder buf = new StringBuilder(EVECENTRAL_ADDR);
		buf.append(EVECENTRAL_SYSTEM).append(sysCode);
		
        for (Integer i: typeIds) {
        	buf.append(EVECENTRAL_PARAM_ID).append(i);
        }
        return buf.toString();
	}

}
