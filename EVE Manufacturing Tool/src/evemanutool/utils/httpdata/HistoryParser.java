package evemanutool.utils.httpdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import evemanutool.data.cache.TradeEntry;
import evemanutool.data.cache.TradeHistoryEntry;

public class HistoryParser extends DefaultHandler{
	
	//Result list.
	private final HashMap<Integer, TradeHistoryEntry> result = new HashMap<>();
	
	//InputStream of XML file.
	private final InputStream is;
	
	public HistoryParser(InputStream inputStream, Collection<Integer> typeIds, long locationId) {
		is = inputStream;
		for (Integer typeId : typeIds) {
			result.put(typeId, new TradeHistoryEntry(typeId, locationId));
		}
	}

	public void parse() {
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(is, this);
		            
		} catch (ParserConfigurationException e) {
		    System.out.println("ParserConfig error");
		} catch (SAXException e) {
		    System.out.println("SAXException : xml not well formed");
		} catch (IOException e) {
			System.out.println("IO error");
		}	
	}
	
	@Override
	public void startElement(String s, String s1, String element,
			Attributes attributes) throws SAXException {
		
		//A new row, add info to corresponding object.
		if (element.equals("row")) {
			TradeHistoryEntry tmpH = result.get(Integer.parseInt(attributes.getValue("typeID")));
			
			String[] tmpDate = attributes.getValue("date").split("-", -1);
			Calendar tmpC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			//Clear timeFields to create the clean date at 00:00:00.
			tmpC.clear();
			tmpC.set(Integer.parseInt(tmpDate[0]),
					Integer.parseInt(tmpDate[1]) - 1, // Months start with 0.
					Integer.parseInt(tmpDate[2]));
			
			//Don't include history entries for the current day
			//since most of them will be incomplete.
			if (System.currentTimeMillis() - tmpC.getTimeInMillis() > (24 * 3600 * 1000)) {
				
				//Add the PriceHistoryEntry.
				tmpH.getHistory().add(new TradeEntry(
						tmpC.getTime(),
						Double.parseDouble(attributes.getValue("lowPrice")),
						Double.parseDouble(attributes.getValue("avgPrice")),
						Double.parseDouble(attributes.getValue("highPrice")),
						Long.parseLong(attributes.getValue("volume")),
						Integer.parseInt(attributes.getValue("orders"))));
			}
		}
	}	
	
	public List<TradeHistoryEntry> getResult() {
		return new ArrayList<>(result.values());
	}
}
