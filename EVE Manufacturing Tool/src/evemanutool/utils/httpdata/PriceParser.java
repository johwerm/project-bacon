package evemanutool.utils.httpdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import evemanutool.data.cache.MarketInfoEntry;
import evemanutool.data.cache.MarketInfoEntry.OrderAim;
import evemanutool.data.cache.PriceEntry;
import evemanutool.data.cache.PriceEntry.PriceType;

public class PriceParser extends DefaultHandler{
	
	//Result list.
	private final List<MarketInfoEntry> result = new LinkedList<MarketInfoEntry>();
	
	//InputStream of XML file.
	private final InputStream is;
	private final long locationId;
	
	//Temporary variables.
	MarketInfoEntry tmpInfo;
	String tmpValue;
	PriceEntry tmpPrice;

	
	public PriceParser(InputStream inputStream, long locationId) {
		is = inputStream;
		this.locationId = locationId;
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
		
		//A new type, create a new info object.
		if (element.equals("type")) {
			tmpInfo = new MarketInfoEntry(Integer.parseInt(attributes.getValue("id")), locationId);
			//Set date to current.
			tmpInfo.setDate(new Date());

		} else if (element.equals("all") || element.equals("buy") || element.equals("sell")) {	
			tmpPrice = new PriceEntry();
		}
	}	
	
	@Override
	public void endElement(String s, String s1, String element)
			throws SAXException {
		
		if (element.equals("type")) {
			result.add(tmpInfo);
			
		}else if (element.equals("all")) {
			tmpInfo.setPrice(tmpPrice, OrderAim.ALL);
			
		}else if (element.equals("buy")) {
			tmpInfo.setPrice(tmpPrice, OrderAim.BUY);
			
		}else if (element.equals("sell")) {
			tmpInfo.setPrice(tmpPrice, OrderAim.SELL);
			
		}else if (element.equals("volume")) {
			tmpPrice.setValue(Double.parseDouble(tmpValue), PriceType.VOLUME);
			
		}else if (element.equals("avg")) {
			
			tmpPrice.setValue(Double.parseDouble(tmpValue), PriceType.AVG);
		}else if (element.equals("max")) {
			tmpPrice.setValue(Double.parseDouble(tmpValue), PriceType.MAX);
			
		}else if (element.equals("min")) {
			tmpPrice.setValue(Double.parseDouble(tmpValue), PriceType.MIN);
		
		}else if (element.equals("stddev")) {
			tmpPrice.setValue(Double.parseDouble(tmpValue), PriceType.STDDEV);
			
		}else if (element.equals("median")) {
			tmpPrice.setValue(Double.parseDouble(tmpValue), PriceType.MEDIAN);
			
		}else if (element.equals("percentile")) {
			tmpPrice.setValue(Double.parseDouble(tmpValue), PriceType.PERCENTILE);		
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		tmpValue = new String(ch, start, length);
	}
	
	public List<MarketInfoEntry> getResult() {
		return result;
	}
}
