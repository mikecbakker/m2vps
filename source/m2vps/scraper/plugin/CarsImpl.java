
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

import org.apache.commons.lang3.text.WordUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class CarsImpl extends AScraper
{
	@Override
	public LinkedList<VehicleData> updateLoad(List<VehicleData> existingVehicles, String make, String model)
	{
		
		Hashtable<String, VehicleData> vehicles = new Hashtable<String, VehicleData>();
		
		// don't scrap the beetle, cars.co.za does not distingues between the old and new beetle
		if (InternalModels.VOLKSWAGEN._21ST_CENTURY_BEETLE.equals(model) ||
			InternalModels.VOLKSWAGEN.BEETLE.equals(model))
		{
			return new LinkedList<VehicleData>(vehicles.values());
		}
		
		for (VehicleData vehicleData : existingVehicles)
		{
			vehicles.put(vehicleData.getWeblink(), vehicleData);
		}
		
		int pageCount = 1;
		boolean lastPageFound = false;
		
		while (pageCount <= MAX_PAGE && !lastPageFound)
		{
			String url = getURL(null, make, model, pageCount);
			
			Log.info("Scraping " + " [" + url + "]");
			System.out.println("Scraping "  + " [" + url + "]");
			
			Document document = getDocumentFromUrl(url);
			
			/*
			 * Detect if we have found the last page Assume we are on last page. If we find 'next',
			 * then we set it back
			 */
			lastPageFound = true;
			XPath xpath = XPathFactory.newInstance().newXPath();
			try
			{
				// TODO need to implement this for cars.co.za
				NodeList nodesZeroResults =
					(NodeList)xpath.evaluate("//div[@class=\"zeroResults\"]", document, XPathConstants.NODESET);
				
				if (nodesZeroResults != null && nodesZeroResults.getLength() > 0)
				{
					Log.info("[Scraper] Zero Results " + url);
					break;
				}
				
				NodeList nodesPage = (NodeList)xpath.evaluate("//li[@class=\"next\"]", document, XPathConstants.NODESET);
				
				if (nodesPage != null && nodesPage.getLength() > 0)
				{
					System.out.println("Found next item.  Not last page");
					Log.info("Found next item.  Not last page");
					lastPageFound = false;
				}
				else
				{
					// on last page
					System.out.println("On the last page");
					Log.info("On the last page");
				}
				
				/* Extract title and ensure we not scraping for model that doesn't exist. */
				/*
				 * cars.co.za doesn't show car results if you select a bad model. So can ignore this
				 * check for now.
				 */
				
				/* Extract vehicle data */
				String searchResults = "//div[@class=\"item clearfix\"]";
				/* Extract year */
				NodeList nodesAge =
					(NodeList)xpath.evaluate(
						searchResults + "//div[@class=\"left_block\"]/h3/a",
						document,
						XPathConstants.NODESET);
				/* Extract link */
				NodeList nodesLink =
					(NodeList)xpath.evaluate(
						searchResults + "//div[@class=\"left_block\"]/h3/a/@href",
						document,
						XPathConstants.NODESET);
				/* Extract price */
				NodeList nodesPrice =
					(NodeList)xpath.evaluate(
						searchResults + "//div[@class=\"right_block\"]/span[@class=\"price\"]",
						document,
						XPathConstants.NODESET);
				// mileage
				NodeList nodesMileage =
					(NodeList)xpath.evaluate(
						searchResults + "//ul[contains(@class, \"vehicle-details-options\")]/li[1]",
						document,
						XPathConstants.NODESET);
				
				NodeList nodesProvince =
					(NodeList)xpath.evaluate(
						searchResults + "//div[@class=\"left_block\"]//div[@class=\"vehicle-type-locality\"]",
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
					vehicle.setYear(getSaveTextContent(nodesAge.item(i).getTextContent().trim().substring(0, 4)));
					
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
					vehicle.setWeblink("http://www.cars.co.za" + (cleanString(nodesLink.item(i).getTextContent()).trim()));
					
					String mileage = "";
					
					if (nodesMileage.item(i) != null && nodesMileage.item(i).getTextContent() != null)
					{
						mileage = cleanString(nodesMileage.item(i).getTextContent()).trim();
					}
					
					if (mileage.contains("Km"))
					{
						mileage = mileage.replaceAll("[^\\d]", "");
					}
					
					vehicle.setMileage(mileage);
					vehicle.setProvince(getProvince(nodesProvince.item(i).getTextContent()));
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
		return new LinkedList<VehicleData>(vehicles.values());
	}
	
	@Override
	public String getName()
	{
		return "cars.co.za";
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
		//http://www.cars.co.za/searchVehicle.php?new_or_used=&make_model=BMW%5B1+Series%5D&vfs_area=&agent_locality=&price_range=&os=#.VPcdMp2Ueao
			
		/* Perform manufacturer mapping */
		String mappedManufacturer = getManufacturer(make);
		
		/* Perform model mapping */
		String mappedModel = getModel(make, model);
		
		/*
		 * http://www.cars.co.za/searchVehicle.php?new_or_used=&make_model=BMW%5B3+Series%5D
		 * &vfs_area=Gauteng&agent_locality=&price_range=&os=&body_type_exact=&transmission=
		 * &fuel_type=&login_type=&mapped_colour=&vfs_year=&vfs_mileage=&vehicle_axle_config=
		 * &keyword=&sort=vfs_time_d &P=51#
		 */
		
		/* Sort most recent */
		String url =
			" http://www.cars.co.za/searchVehicle.php?new_or_used=&make_model=" + mappedManufacturer + "%5B" +
				mappedModel + "%5D" + "&vfs_area=&agent_locality=&price_range=&os=&body_type_exact=&transmission=" +
				"&fuel_type=&login_type=&mapped_colour=&vfs_year=&vfs_mileage=&vehicle_axle_config=" +
				"&keyword=&sort=vfs_time_d" + "&P=" + pageCount + "#";
		return url;
	}
	
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
				(NodeList)xpath.evaluate("//div[@class=\"col_main error-page\"]", document, XPathConstants.NODESET);
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
	
	/**
	 * 
	 * Method used to construct the web province based on internal province
	 * 
	 * @param location
	 * @return
	 */
	protected String getProvince(String location)
	{
		if(location == null)
		{
			return "unknown";
		}
		
		if(location.toLowerCase().contains("gauteng"))
		{
			return "gauteng";
		}
		
		if(location.toLowerCase().contains("western cape"))
		{
			return "western-cape";
		}
		
		if(location.toLowerCase().contains("free-state"))
		{
			return "free-state";
		}
		
		if(location.toLowerCase().contains("kwazulu natal"))
		{
			return "kwazulu-natal";
		}
		
		if(location.toLowerCase().contains("mpumalanga"))
		{
			return "mpumalanga";
		}
		
		if(location.toLowerCase().contains("limpopo"))
		{
			return "limpopo";
		}
		
		if(location.toLowerCase().contains("eastern cape"))
		{
			return "eatern-cape";
		}
		
		if(location.toLowerCase().contains("northern cape"))
		{
			return "northen-cape";
		}
		
		if(location.toLowerCase().contains("north west"))
		{
			return "north-west";
		}

		return "unknown";
	}
	
	/**
	 * 
	 * Method used to construct the web manufacturer based on internal manufacturer
	 * 
	 * @param internalManufacturer
	 * @return
	 */
	protected String getManufacturer(String internalManufacturer)
	{
		String mappedManufacturer = WordUtils.capitalize(internalManufacturer);
		
		// mercedes-benz => Mercedes-Benz is special case containing hyphen
		if (InternalManufacturers.MERCEDES_BENZ.equals(internalManufacturer))
		{
			return mappedManufacturer;
		}
		
		// General rule is replace (space/hyphen) with +
		mappedManufacturer = WordUtils.capitalize(mappedManufacturer);
		mappedManufacturer = mappedManufacturer.replace('-', '+');
		return mappedManufacturer;
	}
	
	/**
	 * 
	 * Method used to construct the web manufacturer based on internal manufacturer
	 * 
	 * @param internalModel
	 * @return
	 */
	protected String getModel(String manufacture, String internalModel)
	{
		String mappedModel = internalModel;
		
		if (InternalModels.FORD.TOURNEO_CONNECT.equals(internalModel))
		{
			mappedModel = "Tourneo";
		}
		
		if (InternalModels.MAZDA.MAZDA2.equals(internalModel))
		{
			mappedModel = "2";
		}
		
		if (InternalModels.MAZDA.MAZDA3.equals(internalModel))
		{
			mappedModel = "3";
		}
		
		if (InternalModels.MAZDA.MAZDA5.equals(internalModel))
		{
			mappedModel = "5";
		}
		
		if (InternalModels.MAZDA.MAZDA6.equals(internalModel))
		{
			mappedModel = "6";
		}
		
		if (InternalModels.MERCEDES_BENZ.SPRINTER_TRAVELINER.equals(internalModel))
		{
			mappedModel = "sprinter";
		}
		
		if (InternalModels.NISSAN._1400_CHAMP.equals(internalModel))
		{
			mappedModel = "1400-bakkie";
		}
		
		if (InternalModels.NISSAN.NV200_COMBI.equals(internalModel))
		{
			mappedModel = "NV200";
		}
		
		if (InternalModels.NISSAN.PRIMASTAR_MINIBUS.equals(internalModel))
		{
			mappedModel = "primastar";
		}
		
		if (InternalModels.NISSAN.X_TRAIL.equals(internalModel))
		{
			mappedModel = "X-Trail";
			return mappedModel;
		}
		
		if (InternalModels.NISSAN.GT_R.equals(internalModel))
		{
			mappedModel = "GT-R";
			return mappedModel;
		}
		
		if (InternalModels.OPEL.CORSA_HATCHBACK.equals(internalModel))
		{
			mappedModel = "Corsa";
			return mappedModel;
		}
		
		if (InternalModels.TOYOTA.LANDCRUISER.equals(internalModel))
		{
			mappedModel = "Land+Cruiser";
			return mappedModel;
		}
		
		if (InternalModels.TOYOTA.RAV4.equals(internalModel))
		{
			mappedModel = "Rav+4";
			return mappedModel;
		}
		// 1-series => 1+Series
		mappedModel = WordUtils.capitalize(mappedModel);
		
		if (!(InternalManufacturers.MAZDA.equals(manufacture) ||
			InternalManufacturers.MERCEDES_BENZ.equals(manufacture) || InternalModels.HONDA.CR_V.equals(internalModel) ||
			InternalModels.HONDA.CR_Z.equals(internalModel) || InternalModels.HONDA.FR_V.equals(internalModel)))
		{
			mappedModel = mappedModel.replace('-', '+');
		}
		return mappedModel;
	}
	
	public static void main(String[] args) throws XPathExpressionException
	{
		CarsImpl carsImpl = new CarsImpl();
		
		String url = carsImpl.getURL(null, InternalManufacturers.AUDI, InternalModels.AUDI.A1, 1);
		
		Document d = carsImpl.getDocumentFromUrl(url);
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		String searchResults = "//div[@class=\"item clearfix\"]";
		/* Extract year */
		NodeList nodesProvince =
			(NodeList)xpath.evaluate(
				searchResults + "//div[@class=\"left_block\"]//div[@class=\"vehicle-type-locality\"]",
				d,
				XPathConstants.NODESET);
		
		System.out.println(nodesProvince.item(0).getTextContent());
	}
}
