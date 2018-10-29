
package m2vps.scraper.plugin;

import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import m2vps.scraper.fw.AScraper;
import m2vps.scraper.fw.VehicleData;

public class AutoTraderImpl extends AScraper
{
	private static Logger Log = Logger.getRootLogger();
	
	public LinkedList<VehicleData> updateLoad(List<VehicleData> existingVehicles, String make, String model)
	{
		
		if(InternalManufacturers.CHEVROLET.equals(make) && InternalModels.CHEVROLET.CORSA_UTILITY.equals(model))
		{
			LinkedList<VehicleData> vehicles = scrape(existingVehicles, make, InternalModels.CHEVROLET.UTILITY);
			vehicles.addAll(scrape(existingVehicles, make,  InternalModels.CHEVROLET.CORSA));
			vehicles.addAll(scrape(existingVehicles, make,  InternalModels.CHEVROLET.CORSA_UTILITY));
			
			for (VehicleData vehicleData : vehicles)
			{
				vehicleData.setModel( InternalModels.CHEVROLET.CORSA_UTILITY);
			}
			return vehicles;
		}
		
		if(InternalManufacturers.FORD.equals(make) && InternalModels.FORD.TRANSIT_TOURNEO.equals(model))
		{
			LinkedList<VehicleData> vehicles = scrape(existingVehicles, make, InternalModels.FORD.TRANSIT_TOURNEO);
			vehicles.addAll(scrape(existingVehicles, make,  InternalModels.FORD.TRANSIT_TORNEO));
			vehicles.addAll(scrape(existingVehicles, make,  InternalModels.FORD.TRANSIT_MINIBUS));
			
			for (VehicleData vehicleData : vehicles)
			{
				vehicleData.setModel( InternalModels.FORD.TRANSIT_TOURNEO);
			}
			return vehicles;
		}
		
		if(InternalManufacturers.HYUNDAI.equals(make) && InternalModels.HYUNDAI.H1.equals(model))
		{
			LinkedList<VehicleData> vehicles = scrape(existingVehicles, make, InternalModels.HYUNDAI.H1_PASSENGER);
			vehicles.addAll(scrape(existingVehicles, make,  InternalModels.HYUNDAI.H_1_VAN));
			
			for (VehicleData vehicleData : vehicles)
			{
				vehicleData.setModel( InternalModels.HYUNDAI.H1);
			}
			return vehicles;
		}
		
		if(InternalManufacturers.MERCEDES_BENZ.equals(make) && InternalModels.MERCEDES_BENZ.VITO.equals(model))
		{
			LinkedList<VehicleData> vehicles = scrape(existingVehicles, make, InternalModels.MERCEDES_BENZ.VITO_COMBI);
			vehicles.addAll(scrape(existingVehicles, make,  InternalModels.MERCEDES_BENZ.VITO_TRAVELINER));
			
			for (VehicleData vehicleData : vehicles)
			{
				vehicleData.setModel( InternalModels.MERCEDES_BENZ.VITO);
			}
			return vehicles;
		}

		if(InternalManufacturers.TOYOTA.equals(make) && InternalModels.TOYOTA.COROLLA.equals(model))
		{
			LinkedList<VehicleData> vehicles = scrape(existingVehicles, make, InternalModels.TOYOTA.COROLLA);
			vehicles.addAll(scrape(existingVehicles, make,  InternalModels.TOYOTA.COROLLA_QUEST));
			vehicles.addAll(scrape(existingVehicles, make,  InternalModels.TOYOTA.COROLLA_VERSO));
			
			for (VehicleData vehicleData : vehicles)
			{
				vehicleData.setModel( InternalModels.TOYOTA.COROLLA);
			}
			return vehicles;
		}
		
		
		if(InternalManufacturers.VOLKSWAGEN.equals(make) && InternalModels.VOLKSWAGEN.CADDY.equals(model))
		{
			LinkedList<VehicleData> vehicles = scrape(existingVehicles, make, InternalModels.VOLKSWAGEN.CADDY);
			vehicles.addAll(scrape(existingVehicles, make,  InternalModels.VOLKSWAGEN.CADDY_MAXI_LIFE));
			vehicles.addAll(scrape(existingVehicles, make,  InternalModels.VOLKSWAGEN.CADDY_LIFE));
			
			for (VehicleData vehicleData : vehicles)
			{
				vehicleData.setModel( InternalModels.VOLKSWAGEN.CADDY);
			}
			return vehicles;
		}
		
		if(InternalManufacturers.VOLKSWAGEN.equals(make) && InternalModels.VOLKSWAGEN.KOMBI.equals(model))
		{
			LinkedList<VehicleData> vehicles = scrape(existingVehicles, make, InternalModels.VOLKSWAGEN.KOMBI);
			vehicles.addAll(scrape(existingVehicles, make,  InternalModels.VOLKSWAGEN.COMBI));
			vehicles.addAll(scrape(existingVehicles, make,  InternalModels.VOLKSWAGEN.MICROBUS));
			
			for (VehicleData vehicleData : vehicles)
			{
				vehicleData.setModel( InternalModels.VOLKSWAGEN.KOMBI);
			}
			return vehicles;
		}
		
		return scrape(existingVehicles, make, model);
	}
	
	public LinkedList<VehicleData> scrape(List<VehicleData> existingVehicles, String make, String model)
	{
		Hashtable<String, VehicleData> vehicles = new Hashtable<String, VehicleData>();
		
		for (VehicleData vehicleData : existingVehicles)
		{
			vehicles.put(vehicleData.getWeblink(), vehicleData);
		}
		
		for (String province : AScraper.PROVINCES)
		{
			int pageCount = 1;
			boolean lastPageFound = false;
			
			while (pageCount <= MAX_PAGE && !lastPageFound)
			{
				String url = getURL(province, make, model, pageCount);
				
				Log.info("Scraping " + province + " [" + url + "]");
				System.out.println("Scraping " + province + " [" + url + "]");
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
				lastPageFound = true;
				XPath xpath = XPathFactory.newInstance().newXPath();
				try
				{
//					Log.info("nodesZeroResults");
//					/* Extract year */
//					NodeList nodesZeroResults =
//						(NodeList)xpath.evaluate("//div[@class=\"zeroResults\"]", document, XPathConstants.NODESET);
//					
//					if (nodesZeroResults != null && nodesZeroResults.getLength() > 0)
//					{
//						Log.info("[Scraper] Zero Results " + url);
//						break;
//					}
//					
//					Log.info("nodesPage");
//					NodeList nodesPage =
//						(NodeList)xpath.evaluate("//a[@class=\"next\"]/@href", document, XPathConstants.NODESET);
//					if (nodesPage != null && nodesPage.getLength() > 0)
//					{
//						System.out.println("Found next item.  Not last page");
//						Log.info("Found next item.  Not last page");
//						lastPageFound = false;
//					}
					
					//TODO Log.info("nodesPageError");
					/* Extract title and ensure we not scraping for model that doesnt exist. */
					//NodeList nodesPageError =
					//	(NodeList)xpath.evaluate("//div[@class=\"searchView\"]//a/@href", document, XPathConstants.NODESET);
					/*
					if (nodesPageError == null || nodesPageError.item(0) == null ||
						nodesPageError.item(0).getTextContent() == null)
					{
						Log.error(new Exception("Scraping for model that doesnt exist." + url));
						
						// Rather than end process lets rather just break from this model. 
						lastPageFound = true;
						break;
					}*/
					
					/*String detailLink = nodesPageError.item(0).getTextContent();
					if (detailLink == null || !detailLink.contains(url.substring(0, url.indexOf("search?"))))
					{
						Log.error(new Exception("Scraping for model that doesnt exist." + url));
						
						// Rather than end process lets rather just break from this model.
						lastPageFound = true;
						break;
					}*/
					
					// the div[@class=\"searchResult\"] will remove the featured add on the top
					XPathExpression expr = xpath.compile("//div[contains(concat(' ', normalize-space(@class), ' '), ' listing-item-spec ')]");
					Log.info("nodesAge");
					/* Extract year */
					NodeList nodesAge =
						(NodeList)expr.evaluate(
							document,
							XPathConstants.NODESET);
					System.out.println("node age --" + nodesAge.getLength());
					System.out.println(nodesAge.item(0).getNodeName());
					
					Log.info("nodesLink");
					
					//TODO
					String searchResults ="TODO";
					/* Extract link */
					NodeList nodesLink =
						(NodeList)xpath.evaluate(
							searchResults + "//div[@class=\"serpHeader\"]//a/@href",
							document,
							XPathConstants.NODESET);
					
					Log.info("nodesPrice");
					/* Extract price */
					NodeList nodesPrice =
						(NodeList)xpath.evaluate(
							searchResults + "//div[@class=\"serpPrice\"]",
							document,
							XPathConstants.NODESET);
					
					Log.info("nodesMileage");
					// mileage
					NodeList nodesMileage =
						(NodeList)xpath.evaluate(
							searchResults + "//ul[contains(@class, \"advertSpecs\")]/li[1]",
							document,
							XPathConstants.NODESET);
					
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
							break;
						}
						
						vehicle.setPrice(price);
						vehicle.setWeblink((cleanString(nodesLink.item(i).getTextContent()).trim()));
						
						String mileage = "";
						if(nodesMileage.item(i) != null && nodesMileage.item(i).getTextContent() != null)
						{
							mileage = cleanString(nodesMileage.item(i).getTextContent()).trim();	
						}
						
						
						if (mileage.contains("km"))
						{
							mileage = mileage.replaceAll("[^\\d]", "");
						}
						
						vehicle.setMileage(mileage);
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
					System.exit(1);
				}
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
		return "autotrader.co.za";
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
		// http://www.autotrader.co.za/seoregion/gauteng/makemodel/make/opel/model/corsa/bodytype/bakkie/search?pageNumber=2
		
		
		String modelSearch = model;
		
		if(InternalManufacturers.OPEL.equals(make) && (InternalModels.OPEL.CORSA_HATCHBACK.equals(model) || InternalModels.OPEL.CORSA_UTILITY.equals(model)))
		{
			modelSearch = "corsa";
		}
		
		String url = "http://www.autotrader.co.za/seoregion/" + province + "/makemodel/make/" + make + "/model/" + modelSearch;
		
		if (InternalModels.OPEL.CORSA_UTILITY.equals(model))
		{
			url += "/bodytype/bakkie";
		}
		url += "/search?sort=MostRecent&pageNumber=" + pageCount;
		
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
}
