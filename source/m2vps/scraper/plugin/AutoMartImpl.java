
package m2vps.scraper.plugin;

import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import m2vps.scraper.fw.AScraper;
import m2vps.scraper.fw.VehicleData;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class AutoMartImpl extends AScraper
{
	private static Logger Log = Logger.getRootLogger();
	
	public LinkedList<VehicleData> updateLoad(List<VehicleData> existingVehicles, String make, String model)
	{
		return scrap(existingVehicles, make, model);
	}
	
	public LinkedList<VehicleData> scrap(List<VehicleData> existingVehicles, String make, String model)
	{
		Hashtable<String, VehicleData> vehicles = new Hashtable<String, VehicleData>();
		
		for (VehicleData vehicleData : existingVehicles)
		{
			vehicles.put(vehicleData.getWeblink(), vehicleData);
		}
		
		int pageCount = 0;
		boolean lastPageFound = false;
		
		while (pageCount <= MAX_PAGE && !lastPageFound)
		{
			String url = getURL(null, make, model, pageCount);
			
			System.out.println("Scraping " + " [" + url + "]");
			if(1==1)
			break;
			Document document = getDocumentFromUrl(url);
			
			// Couldn't construct document so lets just skip over onto the next one.
			if (document == null)
			{
				Log.info("Skip Document");
				break;
			}
			
			/*
			 * Detect if we have found the last page Assume we are on last page. If we find 'next',
			 * then we set it back
			 */
			lastPageFound = false;
			XPath xpath = XPathFactory.newInstance().newXPath();
			try
			{
				Log.info("nodesZeroResults");
				
				NodeList nodesZeroResults =
					(NodeList)xpath.evaluate("//div[@class=\"Warning\"]", document, XPathConstants.NODESET);
				
				if (nodesZeroResults != null && nodesZeroResults.getLength() > 0)
				{
					Log.info("[Scraper] Zero Results " + url);
					break;
				}
				
				NodeList nodeModelCheck =
					(NodeList)xpath.evaluate("//div[@id=\"dsd_ddlModel\"]//span//a", document, XPathConstants.NODESET);
				
				if (nodeModelCheck == null || nodeModelCheck.item(0) == null ||
					"ANY".equals(nodeModelCheck.item(0).getTextContent().trim()))
				{
					Log.warn("[Scraper] Invalid Model " + url);
					break;
				}
				
				// the div[@class=\"searchResult\"] will remove the featured add on the top
				String searchResults = "//tr[@class=\"Item Dealer\"]";
				
				Log.info("nodesAge");
				/* Extract year */
				NodeList nodesAge =
					(NodeList)xpath.evaluate(searchResults + "//td[@class=\"year\"]", document, XPathConstants.NODESET);
				
				Log.info("nodesLink");
				/* Extract link */
				NodeList nodesLink =
					(NodeList)xpath.evaluate(
						searchResults + "//td[@class=\"makemodel\"]//strong//a/@href",
						document,
						XPathConstants.NODESET);
				
				Log.info("nodesPrice");
				/* Extract price */
				NodeList nodesPrice =
					(NodeList)xpath.evaluate(searchResults + "//td[@class=\"price\"]", document, XPathConstants.NODESET);
				
				Log.info("nodesMileage");
				// mileage
				NodeList nodesMileage =
					(NodeList)xpath.evaluate(searchResults + "//td[@class=\"km\"]", document, XPathConstants.NODESET);
				
				Log.info("nodeProvince");
				// province
				NodeList nodesProvince =
					(NodeList)xpath.evaluate(searchResults + "//td[@class=\"region\"]", document, XPathConstants.NODESET);
				
				// check if there is any results
				if (nodesLink.getLength() == 0)
				{
					lastPageFound = true;
					break;
				}
				/* Instantiate the vehicle objects here */
				for (int i = 0; i < nodesLink.getLength(); i++)
				{
					VehicleData vehicle = new VehicleData();
					vehicle.setSource(getName());
					vehicle.setCreated(new Date());
					vehicle.setManufacturer(make);
					vehicle.setModel(model);
					vehicle.setWebResultPage(url);
					vehicle.setYear(getSaveTextContent(nodesAge.item(i).getTextContent()));
					
					String priceStr = (getSaveTextContent(nodesPrice.item(i).getTextContent()));
					double price = 0;
					try
					{
						price = Double.parseDouble(priceStr);
					}
					catch (Exception e)
					{
						// invalid price, don't add this vehicle
						continue;
					}
					
					vehicle.setPrice(price);
					vehicle.setWeblink((cleanString(nodesLink.item(i).getTextContent()).trim()));
					
					Log.info(i + " WebLink=" + vehicle.getWeblink());
					
					String mileage = "";
					if (nodesMileage.item(i) != null && nodesMileage.item(i).getTextContent() != null)
					{
						mileage = cleanString(nodesMileage.item(i).getTextContent()).trim();
					}
					
					if (mileage.contains("km"))
					{
						mileage = mileage.replaceAll("[^\\d]", "");
					}
					
					vehicle.setMileage(mileage);
					
					String province = nodesProvince.item(i).getTextContent();
					
					if (province != null)
					{
						province = province.toLowerCase().replace(" ", "-");
					}
					
					vehicle.setProvince(province);
					vehicle.setRankingSystem(0);
					vehicle.setRankingModel(0);
					vehicle.setRankingMilage(0);
					vehicle.setRankingPrice(0);
					
					
					if (vehicles.containsKey(vehicle.getWeblink()))
					{
						Log.info("Duplicate Web Link Found, stopping update load" + vehicle.getWeblink());
						lastPageFound = true;
						break;
					}
					vehicles.put(vehicle.getWeblink(), vehicle);
				}
				pageCount++;
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				System.out.println(e);
				Log.error(e);
				pageCount++;
			}
		}
		return new LinkedList<VehicleData>(vehicles.values());
	}
	
	/**
	 * 
	 * Returns this implementation's name
	 *
	 * @return
	 * @see m2vps.scraper.fw.AScraper#getName()
	 */
	@Override
	public String getName()
	{
		return "automart.co.za";
	}
	
	/**
	 * Get the URL
	 * 
	 * @param make
	 * @param model
	 * @param bodyType
	 * @param pageCount
	 * @return
	 */
	@Override
	public String getURL(String province, String make, String model, int pageCount)
	{
		// http://www.automart.co.za/cars/search-results/audi/a1/offset/40
		
		String modelSearch = model;
		
		String url =
			"http://www.automart.co.za/cars/search-results/" + make + "/" + modelSearch + "/offset/" + pageCount * 20;
		
		return url;
	}
	
	/**
	 * 
	 * Checks whether a vehicle exists
	 *
	 * @param vehicleData
	 * @return
	 * @see m2vps.scraper.fw.AScraper#checkVehicleExists(m2vps.scraper.fw.VehicleData)
	 */
	@Override
	public boolean checkVehicleExists(VehicleData vehicleData)
	{
		/*
		 * Only check vehicles for our plug-in. If this vehicle was scraped by a different plug-in,
		 * then simply return true as the other plug-in will take care of it. Plug-ins manage their
		 * own vehicles.
		 */
		if (!getName().equals(vehicleData.getSource()))
		{
			return true;
		}
		
		Document document = getDocumentFromUrl(vehicleData.getWeblink());
		
		if (document == null)
		{
			// assume the vehicle still exist
			Log.warn("No Document Found - " + vehicleData.getWeblink());
			return true;
		}
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		/* Extract year */
		NodeList nodesZeroResults;
		try
		{
			nodesZeroResults =
				(NodeList)xpath.evaluate("//div[@class=\"redirectMessage\"]", document, XPathConstants.NODESET);
			return !(nodesZeroResults != null && nodesZeroResults.getLength() > 0);
		}
		catch (XPathExpressionException e)
		{
			e.printStackTrace();
			System.out.println(e);
			Log.error(e);
		}
		return true;
	}
	
	public static void main(String[] args) throws XPathExpressionException
	{
		AutoMartImpl impl = new AutoMartImpl();
		
		String url = "http://www.automart.co.za/cars/search-results/audi/a8888/offset/0";
		
		System.out.println("http://www.automart.co.za/cars/search-results/audi/a8/offset/0");
		
		Document document = impl.getDocumentFromUrl(url);
		
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		
//		Log.info("nodesAge");
//		/* Extract year */
//		NodeList nodeModelCheck =
//			(NodeList)xpath.evaluate("//div[@id=\"dsd_ddlModel\"]//span//a", document, XPathConstants.NODESET);
//		
//
//		
//		System.out.println("*" + nodesAge.item(0).getTextContent() +"*");
		
	}
}
