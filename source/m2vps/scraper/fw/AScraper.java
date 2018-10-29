
package m2vps.scraper.fw;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import m2vps.scraper.fw.dao.VehicleDAO;

import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;

public abstract class AScraper
{
	private static long docSizeBytes = 0;
	protected static Logger Log = Logger.getLogger(AScraper.class.getName());
	
	protected static final int MAX_PAGE = 500;
	
	public abstract boolean checkVehicleExists(VehicleData vehicleData);
	public abstract String getURL(String province, String make, String model, int pageCount);
	
	public abstract LinkedList<VehicleData> updateLoad(
		List<VehicleData> existingVehicles,
		String make,
		String model);
	
	public abstract String getName();
	
	protected VehicleDAO vehicleDAO = new VehicleDAO();
	
	/**
	 * 
	 * Calculates and removes outliers
	 *
	 * @param vehicles
	 */
	public void filterOutliers(List<VehicleData> vehicles)
	{
		Log.info("[Outlier] Start");
		
		double meanMilage = 0;
		double meanPrice = 0;
		
		for (VehicleData vehicleData : vehicles)
		{
			meanMilage += Double.parseDouble(vehicleData.getMileage());
			meanPrice +=vehicleData.getPrice();
		}
		
		meanMilage = meanMilage / vehicles.size();
		meanPrice = meanPrice / vehicles.size();
		
		Log.debug("[Outlier] Mean Milage=" + meanMilage + " Price=" + meanPrice);
		
		double divMilage = 0;
		double divPrice = 0;
		
		for (VehicleData vehicleData : vehicles)
		{
			divMilage += Math.pow(Double.parseDouble(vehicleData.getMileage()) - meanMilage, 2);
			divPrice += Math.pow(vehicleData.getPrice() - meanPrice, 2);
		}
		
		Log.debug("[Outlier] Deviation Milage=" + divMilage + " Price=" + divPrice);
		
		double stdDevMilage = Math.sqrt(divMilage / vehicles.size());
		double stdDevPrice = Math.sqrt(divPrice / vehicles.size());
		
		Log.debug("[Outlier] StandardDeviation Milage=" + stdDevMilage + " Price=" + stdDevPrice);
		
		stdDevMilage *= 4;
		stdDevPrice *= 4;
		
		Iterator<VehicleData> iterator = vehicles.iterator();
		
		while (iterator.hasNext())
		{
			VehicleData vehicleData = (VehicleData)iterator.next();
			
			// to low and to high prices should be filters
			if (Math.abs(meanPrice - vehicleData.getPrice()) > stdDevPrice)
			{
				Log.debug("[Outlier] Price Outlier " + vehicleData.toString());
				vehicleData.setOutlier(true);
			}// we should only look at excessively large mileage
			else if ((Double.parseDouble(vehicleData.getMileage()) - meanMilage) > stdDevMilage)
			{
				Log.debug("[Outlier] Mileage Outlier " + vehicleData.toString());
				vehicleData.setOutlier(true);
			}
		}
		Log.info("[Outlier] Complete");
	}
	
	/**
	 * 
	 * Filters out vehicles for which we could not read valid numeric mileage and price
	 *
	 * @param vehicles
	 */
	public void filterVehicleParameters(List<VehicleData> vehicles)
	{
		Log.info("[Filter Vehicle Parameters] Start");
		
		Iterator<VehicleData> iterator = vehicles.iterator();
		
		while (iterator.hasNext())
		{
			boolean fillter = false;
			VehicleData vehicle = (VehicleData)iterator.next();
			
			try
			{
				// validate the the vehicle contains a valid Mileage and price
				long mileage = Long.parseLong(vehicle.getMileage());
				int year = Integer.parseInt(vehicle.getYear());
				int age = Calendar.getInstance().get(Calendar.YEAR) - year - 1;
				
				// Assume at least a 1000km a year
				int minMileage = 1000 * age;
				
				if (mileage < minMileage)
				{
					Log.debug("[Filter Vehicle Parameters] mileage less than min mileage Vehicle=" + vehicle.toString());
					fillter = true;
				}
			}
			catch (Exception e)
			{
				Log.debug("[Filter Vehicle Parameters] Filtering due to exception Vehicle=" + vehicle.toString()+" exception=" + e);
				fillter = true;
			}
			
			// this is special, if this is an old add which has been scraped previously we don't want
			// to delete it. Simply removing it from the list would lead to it not being updated. If
			// its a new add, removing it from the list would mean that is not saved on the db.
			if (fillter)
			{
				Log.debug("[Filter Vehicle Parameters] Vehicle=" + vehicle.toString());
				iterator.remove();
			}
		}
		Log.info("[Filter Vehicle Parameters] Complete");
	}
	
	/**
	 * 
	 * Detects duplicates and marks them as disabled.
	 *
	 * @param vehicles
	 */
	public void filterDuplicates(List<VehicleData> vehicles)
	{
		Hashtable<String, VehicleData> vehicleTable = new Hashtable<String, VehicleData>(vehicles.size());
		
		Log.info("[Duplicate Detection] Start");
		
		Iterator<VehicleData> iterator = vehicles.iterator();
		
		while (iterator.hasNext())
		{
			VehicleData vehicle = (VehicleData)iterator.next();
			
			String key = vehicle.getPrice() + vehicle.getMileage() + vehicle.getYear();
			
			if (vehicleTable.containsKey(key))
			{
				vehicle.setDuplicate(true);
				Log.debug("[Duplicate Detection] Remove Duplicate New " + vehicle.toString() + "\nExisting" +
					vehicleTable.get(key).toString());
			}
			else
			{
				vehicleTable.put(key, vehicle);
			}
		}
		Log.info("[Duplicate Detection] Complete");
	}
	
	
	/**
	 * 
	 * Main entry point of scraping
	 *
	 */
	public void runUpdate(String manufacturer, String model)
	{
		Log.info("[Scrape Update] Start Full Update " + getName());
		
		int totalNew = 0;
		int totalDelete = 0;
		
		Log.info("Processing update load for Manufacturer: " + manufacturer + " Model: " + model);
		
		List<VehicleData> existingVehicles = vehicleDAO.getEnabled(manufacturer, model);
		
		List<VehicleData> vehicles = updateLoad(existingVehicles, manufacturer, model);
		Log.info("[Scraper] Upload New Vehicles " + (vehicles.size() - existingVehicles.size()));
		
		totalNew += vehicles.size() - existingVehicles.size();
		
		filterVehicleParameters(vehicles);
		Log.info("[Filter] Vehicles=" + vehicles.size());
		
		filterDuplicates(vehicles);
		Log.info("[Duplicate] Vehicles=" + vehicles.size());
		
		filterOutliers(vehicles);
		Log.info("[Outlier] Vehicles=" + vehicles.size());
		
		Log.info("[Saving Vehicles] Start=" + vehicles.size());
		vehicleDAO.saveOrUpdateVehicles(vehicles);
		
		Log.info("[Saving Vehicles] End " + manufacturer + " Model: " + model + " Mega Bytes:" +
			((double)docSizeBytes / 1024.0 / 1024.0));
		 Log.info("[Scrape Update] End Full Update New=" + totalNew + " Delete=" + totalDelete +
		 " Mega Bytes:" +
		 ((double)docSizeBytes / 1024.0 / 1024.0));
	}
	
	public String getSaveTextContent(String c)
	{
		String content = cleanString(c);
		content = content.trim();
		content = content.replaceAll("[^\\d]", "");
		return content;
	}
	
	/**
	 * Gets a Document object for the given URL. This document object can be used to run xpath
	 * queries
	 * 
	 * @param urlStr
	 *           The URL that will be parsed into a Document object.
	 */
	protected Document getDocumentFromUrl(String urlStr)
	{
		URL url;
		CountInputStream is = null;
		try
		{
			Thread.sleep(3000);
		}
		catch (InterruptedException e)
		{
			System.out.println("Interrupted while trying to catch a nap");
			Log.info("Interrupted while trying to catch a nap");
		}
		
		try
		{
			Log.debug("[Document] Start " + urlStr);
			url = new URL(urlStr);
			URLConnection cnx = url.openConnection();
			cnx.setAllowUserInteraction(false);
			cnx.setDoOutput(true);
			cnx.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
			Log.debug("[Document] InputStream");
			is = new CountInputStream(cnx.getInputStream());
			Log.debug("[Document] Clean HTML");
			TagNode tagNode = new HtmlCleaner().clean(is);
			Document doc = null;
			Log.debug("[Document] Create DOM");
			doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
			Log.debug("[Document] Finished ");
			return doc;
		}
		catch (MalformedURLException mue)
		{
			Log.error(mue);
		}
		catch (IOException ioe)
		{
			Log.error(ioe);
		}
		catch (ParserConfigurationException e)
		{
			Log.error(e);
		}
		catch (Exception e)
		{
			Log.error(e);
		}
		finally
		{
			try
			{
				if (is != null)
				{
					is.close();
				}
			}
			catch (IOException ioe)
			{
				Log.error(ioe);
			}
		}
		
		Log.info("[Document] Return Null");
		return null;
	}
	
	/**
	 * 
	 * Encodes a URI string for HTTP
	 * 
	 * @param s
	 *           The input string
	 * @return the encoded string
	 */
	protected static String encodeURIComponent(String s)
	{
		String result;
		
		try
		{
			result =
				URLEncoder
					.encode(s, "UTF-8")
					.replaceAll("\\+", "%20")
					.replaceAll("\\%21", "!")
					.replaceAll("\\%27", "'")
					.replaceAll("\\%28", "(")
					.replaceAll("\\%29", ")")
					.replaceAll("\\%7E", "~");
		}
		catch (UnsupportedEncodingException e)
		{
			result = s;
		}
		return result;
	}
	
	/**
	 * 
	 * Removes all non-ascii characters
	 * 
	 * @param inputString
	 * @return
	 */
	protected String cleanString(String inputString)
	{
		/* Clean the dirty characters */
		return inputString.replaceAll("[^\\x00-\\x7E]", "");
	}
	
	public static void printDocument(Document doc)
	{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try
		{
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			transformer.transform(new DOMSource(doc), new StreamResult(new OutputStreamWriter(System.out, "UTF-8")));
		}
		catch (UnsupportedEncodingException | TransformerException e)
		{
			e.printStackTrace();
			Log.error(e);
		}
	}
	
	/* This defines the internal province strings */
	protected static String[] PROVINCES = {"gauteng","western-cape", "free-state", "kwazulu-natal","mpumalanga"};

	
	/* Internal definition for Manufacturers */
	public class InternalManufacturers
	{
		public static final String AUDI 					= "audi";
		public static final String BMW 					= "bmw";
		public static final String CHEVROLET 			= "chevrolet";
		public static final String FORD 					= "ford";
		public static final String SUZUKI 				= "suzuki";
		public static final String HONDA 				= "honda";
		public static final String HYUNDAI 				= "hyundai";
		public static final String JEEP 					= "jeep";
		public static final String KIA 					= "kia";
		public static final String MAZDA					= "mazda";
		public static final String MERCEDES_BENZ		= "mercedes-benz";
		public static final String VOLKSWAGEN			= "volkswagen";
		public static final String VOLVO					= "volvo";
		public static final String TOYOTA				= "toyota";
		public static final String RENAULT				= "renault";
		public static final String PORSCHE				= "porsche";
		public static final String PEUGEOT				= "peugeot";
		public static final String OPEL					= "opel";
		public static final String NISSAN				= "nissan";
	}
	
	/* Internal definition for Models */
	public static class InternalModels
	{
		public static class AUDI
		{
			/* Manufacturer - Audi */
			public static final String A1 = "a1";
			public static final String A3 = "a3";
			public static final String A4 = "a4";
			public static final String A5 = "a5";
			public static final String A6 = "a6";
			public static final String A7 = "a7";
			public static final String A8 = "a8";
//			public static final String ALLROAD = "allroad";
//			public static final String CABRIOLET = "cabriolet";
//			public static final String COUPE = "coupe";
			public static final String Q3 = "q3";
			public static final String Q5 = "q5";
			public static final String Q7 = "q7";
//			public static final String QUATTRO = "quattro";
			public static final String R8 = "r8";
			public static final String RS_Q3 = "rs-q3";
			public static final String RS3 = "rs3";
			public static final String RS4 = "rs4";
			public static final String RS5 = "rs5";
			public static final String RS6 = "rs6";
			public static final String RS7 = "rs7";
			public static final String S3 = "s3";
			public static final String S4 = "s4";
			public static final String S5 = "s5";
			public static final String S6 = "s6";
			public static final String S7 = "s7";
			public static final String S8 = "s8";
			public static final String TT = "tt";
		}
		
		public static class BMW
		{
			/* Manufacturer - BMW */
			public static final String _1_SERIES = "1-series";
			public static final String _2_SERIES = "2-series";
			public static final String _3_SERIES = "3-series";
			public static final String _4_SERIES = "4-series";
			public static final String _5_SERIES = "5-series";
			public static final String _6_SERIES = "6-series";
			public static final String _7_SERIES = "7-series";
			public static final String _8_SERIES = "8-series";
			public static final String M_COUPE = "m-coupe";
			public static final String M1 = "m1";
			public static final String M3 = "m3";
			public static final String M4 = "m4";
			public static final String M5 = "m5";
			public static final String M6 = "m6";
			public static final String X1 = "x1";
			public static final String X3 = "x3";
			public static final String X4 = "x4";
			public static final String X5 = "x5";
			public static final String X6 = "x6";
			public static final String Z1 = "z1";
			public static final String Z3 = "z3";
			public static final String Z4 = "z4";
		}
		
		public static class CHEVROLET
		{
			/* Manufacturer - Chevrolet */
			public static final String AVEO = "aveo";
			public static final String BLAZER = "blazer";
			public static final String CAMARO = "camaro";
			public static final String CAPTIVA = "captiva";
			public static final String CHEVY = "chevy";
			public static final String CORVETTE = "corvette";
			public static final String CRUZE = "cruze";
			public static final String LUMINA = "lumina";
			public static final String OPTRA = "optra";
			public static final String ORLANDO = "orlando";
			public static final String SILVERADO = "silverado";
			public static final String SONIC = "sonic";
			public static final String SPARK = "spark";
			public static final String SSR = "ssr";
			public static final String TRAILBLAZER = "trailblazer";
			public static final String CORSA = "corsa";
			public static final String CORSA_UTILITY = "corsa-utility";
			public static final String UTILITY = "utility";
		}
		
		public static class FORD
		{
			/* Manufacturer - Ford */
			public static final String BANTAM = "bantam";
			public static final String CORTINA = "cortina";
			public static final String COURIER = "courier";
			public static final String ECOSPORT = "ecosport";
			public static final String ESCORT = "escort";
			public static final String EVEREST = "everest";
			public static final String F_SERIES = "f-series";
			public static final String F150 = "f150";
			public static final String F250 = "f250";
			public static final String FAIRLANE = "fairlane";
			public static final String FALCON = "falcon";
			public static final String FIESTA = "fiesta";
			public static final String FIGO = "figo";
			public static final String FOCUS = "focus";
			public static final String GRANADA = "granada";
			public static final String IKON = "ikon";
			public static final String KA = "ka";
			public static final String KUGA = "kuga";
			public static final String LASER = "laser";
			public static final String MONDEO = "mondeo";
			public static final String MUSTANG = "mustang";
			public static final String RANCHERO = "ranchero";
			public static final String RANGER = "ranger";
			public static final String TERRITORY = "territory";
			public static final String TOURNEO_CONNECT = "tourneo-connect";
			public static final String TRACER = "tracer";
			public static final String TRANSIT_MINIBUS = "transit-minibus";
			public static final String TRANSIT_TORNEO = "transit-torneo";
			public static final String TRANSIT_TOURNEO = "transit-tourneo";
		}
		
		public static class HONDA
		{
			/* Manufacturer - Honda */
			public static final String ACCORD = "accord";
			public static final String BALLADE = "ballade";
			public static final String BRIO = "brio";
			public static final String CIVIC = "civic";
			public static final String CR_V = "cr-v";
			public static final String CR_Z = "cr-z";
			public static final String FR_V = "fr-v";
			public static final String JAZZ = "jazz";
			public static final String ODYSSEY = "odyssey";
		}
		
		public static class HYUNDAI
		{
			/* Manufacturer - Hyundai */
			public static final String ACCENT = "accent";
			public static final String ATOS = "atos";
			public static final String ELANTRA = "elantra";
			public static final String GETZ = "getz";
			public static final String GRANDEUR = "grandeur";
			public static final String H1 = "h1";
			public static final String H_1_VAN = "h-1-van";
			public static final String H1_PASSENGER = "h1-passenger";
			public static final String I10 = "i10";
			public static final String I20 = "i20";
			public static final String I30 = "i30";
			public static final String IX35 = "ix35";
			public static final String SANTA_FE = "santa-fe";
			public static final String SONATA = "sonata";
			public static final String TIBURON = "tiburon";
			public static final String TUCSON = "tucson";
			public static final String VELOSTER = "veloster";
		}
		
		public static class JEEP
		{
			/* Manufacturer - Jeep */
			public static final String CHEROKEE = "cherokee";
			public static final String COMMANDER = "commander";
			public static final String COMPASS = "compass";
			public static final String GRAND_CHEROKEE = "grand-cherokee";
			public static final String PATRIOT = "patriot";
			public static final String RENEGADE = "renegade";
			public static final String WRANGLER = "wrangler";
		}
		
		public static class KIA
		{
			/* Manufacturer - Kia */
			public static final String CERATO = "cerato";
			public static final String KOUP = "koup";
			public static final String OPTIMA = "optima";
			public static final String PICANTO = "picanto";
			public static final String RIO = "rio";
			public static final String SORENTO = "sorento";
			public static final String SOUL = "soul";
			public static final String SPORTAGE = "sportage";
		}
		
		public static class MAZDA
		{
			/* Manufacturer - Mazda */
			public static final String BT_50 = "bt-50";
			public static final String CX_5 = "cx-5";
			public static final String CX_7 = "cx-7";
			public static final String DRIFTER = "drifter";
			public static final String MAZDA2 = "mazda2";
			public static final String MAZDA3 = "mazda3";
			public static final String MAZDA5 = "mazda5";
			public static final String MAZDA6 = "mazda6";
			public static final String MX_5 = "mx-5";
		}
		
		public static class MERCEDES_BENZ
		{
			/* Manufacturer - Mercedes-Benz */
			public static final String A_CLASS = "a-class";
			public static final String B_CLASS = "b-class";
			public static final String C_CLASS = "c-class";
			public static final String CL_CLASS = "cl-class";
			public static final String CLA_CLASS = "cla-class";
			public static final String CLC_CLASS = "clc-class";
			public static final String CLK_CLASS = "clk-class";
			public static final String CLS_CLASS = "cls-class";
			public static final String E_CLASS = "e-class";
			public static final String G_CLASS = "g-class";
			public static final String GL_CLASS = "gl-class";
			public static final String GLA_CLASS = "gla-class";
			public static final String M_CLASS = "m-class";
			public static final String R_CLASS = "r-class";
			public static final String S_CLASS = "s-class";
			public static final String SL_CLASS = "sl-class";
			public static final String SLK_CLASS = "slk-class";
			public static final String SLS = "sls";
			public static final String SPRINTER_TRAVELINER = "sprinter-traveliner";
			public static final String V_CLASS = "v-class";
			public static final String VIANO = "viano";
			public static final String VITO = "vito";
			public static final String VITO_COMBI = "vito-combi";
			public static final String VITO_TRAVELINER = "vito-traveliner";
		}
		
		public static class SUZUKI
		{
			/* Manufacturer - Suzuki */
			public static final String ALTO = "alto";
			public static final String GRAND_VITARA = "grand-vitara";
			public static final String JIMNY = "jimny";
			public static final String SPLASH = "splash";
			public static final String SWIFT = "swift";
			public static final String SX4 = "sx4";
			public static final String VITARA = "vitara";
		}
		
		public static class NISSAN
		{
			public static final String _1400_CHAMP = "1400-champ";
			public static final String _350Z = "350z";
			public static final String _370Z = "370z";
			public static final String ALMERA = "almera";
			public static final String GT_R = "gt-r";
			public static final String HARDBODY = "hardbody";
			public static final String JUKE = "juke";
			
			public static final String LIVINA = "livina";
			public static final String MAXIMA = "maxima";
			public static final String MICRA = "micra";
			public static final String MURANO = "murano";
			public static final String NAVARA = "navara";
			public static final String NP200 = "np200";
			public static final String NP300 = "np300";
			public static final String NV200_COMBI = "nv200-combi";
			public static final String PATHFINDER = "pathfinder";
			public static final String PATROL = "patrol";
			public static final String PRIMASTAR_MINIBUS = "primastar-minibus";
			public static final String QASHQAI = "qashqai";
			public static final String QASHQAI_2 = "qashqai-2";
			public static final String SENTRA = "sentra";
			public static final String SKYLINE = "skyline";
			public static final String TIIDA = "tiida";
			public static final String X_TRAIL = "x-trail";
		}
		
		public static class OPEL
		{
			public static final String  CORSA_UTILITY = "corsa-utility";
			public static final String  CORSA_HATCHBACK= "corsa-hatchback";
			public static final String  ASTRA = "astra";
			public static final String  MERIVA = "meriva";
			public static final String  ZAFIRA = "zafira";
		}
		
		public static class PEUGEOT
		{
			public static final String _107 = "107";
			public static final String _2008 = "2008";
			public static final String _206 = "206";
			public static final String _207 = "207";
			public static final String _208 = "208";
			public static final String _3008 = "3008";
			public static final String _307 = "307";
			public static final String _308 = "308";
			public static final String _407 = "407";
			public static final String _508 = "508";
			public static final String PARTNER = "partner";
			public static final String RCZ = "rcz";
		}
		
		public static class PORSCHE
		{
			public static final String BOXSTER = "boxster";
			public static final String CAYENNE = "cayenne";
			public static final String CAYMAN = "cayman";
			public static final String MACAN = "macan";
			public static final String PANAMERA = "panamera";
			public static final String _911 = "911";
		}
		
		public static class RENAULT
		{
			public static final String CLIO = "clio";
			public static final String DUSTER = "duster";
			public static final String FLUENCE = "fluence";
			public static final String GRAND_SCENIC = "grand-scenic";
			public static final String KOLEOS = "koleos";
			public static final String LOGAN = "logan";
			public static final String MEGANE = "megane";
			public static final String SANDERO = "sandero";
			public static final String SCENIC = "scenic";
			public static final String TWINGO = "twingo";
		}
		
		public static class TOYOTA
		{
			public static final String _86 = "86";
			public static final String AURIS = "auris";
			public static final String AVANZA = "avanza";
			public static final String AVENSIS = "avensis	";
			public static final String AYGO = "aygo";
			public static final String CAMRY = "camry";
			public static final String CONDOR = "condor";
			public static final String CONQUEST = "conquest";
			public static final String COROLLA = "corolla";
			public static final String COROLLA_QUEST = "corolla-quest";
			public static final String COROLLA_VERSO = "corolla-verso";
			public static final String ETIOS = "etios";
			public static final String FJ_CRUISER = "fj-cruiser";
			public static final String FORTUNER = "fortuner";
			public static final String HILUX = "hilux";
			public static final String INNOVA = "innova";
			public static final String LANDCRUISER = "landcruiser";
			public static final String MR2 = "mr2";
			public static final String PRADO = "prado";
			public static final String PRIUS = "prius";
			public static final String QUANTUM = "quantum";
			public static final String RAV4 = "rav4";
			public static final String RUNX = "runx";
			public static final String TAZZ = "tazz";
			public static final String VERSO = "verso";
			public static final String YARIS = "yaris";
		}
		
		public static class VOLVO
		{
			public static final String C30 = "c30";
			public static final String C70 = "c70";
			public static final String S40 = "s40";
			public static final String S60 = "s60";
			public static final String S80 = "s80";
			public static final String V40 = "v40";
			public static final String V50 = "v50";
			public static final String V60 = "v60";
			public static final String V70 = "v70";
			public static final String XC60 = "xc60";
			public static final String XC70 = "xc70";
			public static final String XC90 = "xc90";
		}
		
		public static class VOLKSWAGEN
		{
			public static final String _21ST_CENTURY_BEETLE = "21st-century-beetle";
			public static final String AMAROK = "amarok";
			public static final String BEETLE = "beetle";
			public static final String CADDY = "caddy";
			public static final String CADDY_LIFE = "caddy-life";
			public static final String CADDY_MAXI_LIFE = "caddy-maxi-life";
			public static final String CARAVELLE = "caravelle";
			public static final String CC = "cc";
			public static final String CHICO = "chico";
			public static final String CITI = "citi";
			public static final String COMBI = "combi";
			public static final String GOLF = "golf";
			public static final String JETTA = "jetta";
			public static final String KOMBI = "kombi";
			public static final String MICROBUS = "microbus";
			public static final String PASSAT = "passat";
			public static final String POLO = "polo";
			public static final String POLO_VIVO = "polo-vivo";
			public static final String SCIROCCO= "scirocco";
			public static final String SHARAN = "sharan";
			public static final String TIGUAN = "tiguan";
			public static final String TOUAREG = "touareg";
			public static final String TOURAN = "touran";
		}
	}
	
	public class CountInputStream extends FilterInputStream
	{
		public CountInputStream(InputStream in)
		{
			super(in);
		}
		
		@Override
		public int read() throws IOException
		{
			final int c = super.read();
			if (c >= 0)
			{
				docSizeBytes++;
			}
			return c;
		}
		
		@Override
		public int read(byte[] b, int off, int len) throws IOException
		{
			final int bytesRead = super.read(b, off, len);
			if (bytesRead > 0)
			{
				docSizeBytes += bytesRead;
			}
			return bytesRead;
		}
		
		@Override
		public int read(byte[] b) throws IOException
		{
			final int bytesRead = super.read(b);
			if (bytesRead > 0)
			{
				docSizeBytes += bytesRead;
			}
			return bytesRead;
		}
		
		public long getCount()
		{
			return docSizeBytes;
		}
	}
}
