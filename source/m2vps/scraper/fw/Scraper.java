
package m2vps.scraper.fw;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import m2vps.scraper.fw.AScraper.InternalManufacturers;
import m2vps.scraper.fw.AScraper.InternalModels;
import m2vps.scraper.fw.util.HibernateLocalUtil;
import m2vps.scraper.plugin.AutoTraderImpl;
import m2vps.scraper.plugin.CarsImpl;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Scraper
{
	
	/**
	 * This hashmap contains the internal definitions of the make and models. We decide on our
	 * internal name. The impl's can perform mappings for the specific url needs
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public static final List<Pair> vehicles = new ArrayList<Pair>();
	
	public static final Hashtable<String, AScraper> scrapers = new Hashtable<String, AScraper>();
	
	private static Logger Log = Logger.getRootLogger();

	/**
	 * 
	 * Entry point
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		/* Configure Logger */
		HTMLLayout layout = new HTMLLayout();
		layout.setLocationInfo(true);
		layout.setTitle("M2VPS Scraper - Trace file");
		
		// creates daily rolling file appender
		DailyRollingFileAppender rollingAppender = new DailyRollingFileAppender();
		rollingAppender.setFile("./Log/update/m2vps_trace.html");
		rollingAppender.setDatePattern("'.'yyyyMMdd");
		rollingAppender.setLayout(layout);
		rollingAppender.activateOptions();
		
		Log.setLevel(Level.INFO);
		Log.addAppender(rollingAppender);
		
		Log.info("[Scraper initiated]");
		
		/* Perform the update load */
		/* 
		 * For each model, update iterate through scraper to load models.
		 * Distributed load, less intensive on any one system. 
		 */
		for (int i = 0; i < vehicles.size(); i ++)
		{
			Pair<String,String> vehicle = vehicles.get(i);
			String manufacturer = vehicle.getLeft();
			String model = vehicle.getRight();
			for(AScraper siteScraper : scrapers.values())
			{
				siteScraper.runUpdate(manufacturer, model);
			}
		}
		
		/* Last thing we do is cleanup */
		HibernateLocalUtil.stopConnectionProvider();
		
		org.apache.log4j.LogManager.shutdown();
		System.out.println("[Scraper completed]");
	}
	
	static
	{
		/* Initialise scrapers */
		scrapers.put("autotrader.co.za", new AutoTraderImpl());
		//scrapers.put("cars.co.za", new CarsImpl());
		
		/* Initialise vehicles */
		/* Audi Models */
		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.A1));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.A3));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.A4));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.A5));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.A6));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.A7));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.A8));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.Q3));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.Q5));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.Q7));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.R8));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.RS_Q3));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.RS3));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.RS4));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.RS5));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.RS6));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.RS7));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.S3));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.S4));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.S5));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.S6));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.S7));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.S8));
//		vehicles.add(Pair.of(InternalManufacturers.AUDI, InternalModels.AUDI.TT));
//		
//		/* BMW models */
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW._1_SERIES));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW._2_SERIES));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW._3_SERIES));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW._4_SERIES));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW._5_SERIES));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW._6_SERIES));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW._7_SERIES));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW._8_SERIES));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.M1));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.M3));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.M4));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.M5));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.M6));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.X1));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.X3));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.X4));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.X5));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.X6));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.Z1));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.Z3));
//		vehicles.add(Pair.of(InternalManufacturers.BMW, InternalModels.BMW.Z4));
//		
//		/* Chevrolet models */
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.AVEO));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.BLAZER));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.CAMARO));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.CAPTIVA));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.CHEVY));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.CORVETTE));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.CRUZE));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.LUMINA));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.OPTRA));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.ORLANDO));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.SILVERADO));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.SONIC));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.SPARK));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.TRAILBLAZER));
//		vehicles.add(Pair.of(InternalManufacturers.CHEVROLET,InternalModels.CHEVROLET.CORSA_UTILITY));
//		
//		/* Ford models */
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.BANTAM));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.CORTINA));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.COURIER));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.ECOSPORT));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.ESCORT));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.EVEREST));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.F150));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.F250));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.FAIRLANE));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.FALCON));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.FIESTA));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.FIGO));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.FOCUS));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.GRANADA));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.IKON));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.KA));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.KUGA));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.LASER));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.MONDEO));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.MUSTANG));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.RANCHERO));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.RANGER));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.TERRITORY));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.TOURNEO_CONNECT));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.TRACER));
//		vehicles.add(Pair.of(InternalManufacturers.FORD,InternalModels.FORD.TRANSIT_TOURNEO));
//		
//		/* Honda models */
//		vehicles.add(Pair.of(InternalManufacturers.HONDA,InternalModels.HONDA.ACCORD));
//		vehicles.add(Pair.of(InternalManufacturers.HONDA,InternalModels.HONDA.BALLADE));
//		vehicles.add(Pair.of(InternalManufacturers.HONDA,InternalModels.HONDA.BRIO));
//		vehicles.add(Pair.of(InternalManufacturers.HONDA,InternalModels.HONDA.CIVIC));
//		vehicles.add(Pair.of(InternalManufacturers.HONDA,InternalModels.HONDA.CR_V));
//		vehicles.add(Pair.of(InternalManufacturers.HONDA,InternalModels.HONDA.CR_Z));
//		vehicles.add(Pair.of(InternalManufacturers.HONDA,InternalModels.HONDA.FR_V));
//		vehicles.add(Pair.of(InternalManufacturers.HONDA,InternalModels.HONDA.JAZZ));
//		vehicles.add(Pair.of(InternalManufacturers.HONDA,InternalModels.HONDA.ODYSSEY));
//		
//		/* Hyundai models */
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.ACCENT));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.ATOS));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.ELANTRA));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.GETZ));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.GRANDEUR));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.H1));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.I10));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.I20));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.I30));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.IX35));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.SANTA_FE));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.SONATA));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.TIBURON));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.TUCSON));
//		vehicles.add(Pair.of(InternalManufacturers.HYUNDAI, InternalModels.HYUNDAI.VELOSTER));
//		
//		/* Jeep models */
//		vehicles.add(Pair.of(InternalManufacturers.JEEP, InternalModels.JEEP.CHEROKEE));
//		vehicles.add(Pair.of(InternalManufacturers.JEEP, InternalModels.JEEP.COMMANDER));
//		vehicles.add(Pair.of(InternalManufacturers.JEEP, InternalModels.JEEP.COMPASS));
//		vehicles.add(Pair.of(InternalManufacturers.JEEP, InternalModels.JEEP.GRAND_CHEROKEE));
//		vehicles.add(Pair.of(InternalManufacturers.JEEP, InternalModels.JEEP.PATRIOT));
//		vehicles.add(Pair.of(InternalManufacturers.JEEP, InternalModels.JEEP.RENEGADE));
//		vehicles.add(Pair.of(InternalManufacturers.JEEP, InternalModels.JEEP.WRANGLER));
//		
//		/* Kia models */
//		vehicles.add(Pair.of(InternalManufacturers.KIA, InternalModels.KIA.CERATO));
//		vehicles.add(Pair.of(InternalManufacturers.KIA, InternalModels.KIA.KOUP));
//		vehicles.add(Pair.of(InternalManufacturers.KIA, InternalModels.KIA.OPTIMA));
//		vehicles.add(Pair.of(InternalManufacturers.KIA, InternalModels.KIA.PICANTO));
//		vehicles.add(Pair.of(InternalManufacturers.KIA, InternalModels.KIA.RIO));
//		vehicles.add(Pair.of(InternalManufacturers.KIA, InternalModels.KIA.SORENTO));
//		vehicles.add(Pair.of(InternalManufacturers.KIA, InternalModels.KIA.SOUL));
//		vehicles.add(Pair.of(InternalManufacturers.KIA, InternalModels.KIA.SPORTAGE));
//		
//		/* Mazda models */
//		vehicles.add(Pair.of(InternalManufacturers.MAZDA, InternalModels.MAZDA.BT_50));
//		vehicles.add(Pair.of(InternalManufacturers.MAZDA, InternalModels.MAZDA.CX_5));
//		vehicles.add(Pair.of(InternalManufacturers.MAZDA, InternalModels.MAZDA.CX_7));
//		vehicles.add(Pair.of(InternalManufacturers.MAZDA, InternalModels.MAZDA.DRIFTER));
//		vehicles.add(Pair.of(InternalManufacturers.MAZDA, InternalModels.MAZDA.MAZDA2));
//		vehicles.add(Pair.of(InternalManufacturers.MAZDA, InternalModels.MAZDA.MAZDA3));
//		vehicles.add(Pair.of(InternalManufacturers.MAZDA, InternalModels.MAZDA.MAZDA5));
//		vehicles.add(Pair.of(InternalManufacturers.MAZDA, InternalModels.MAZDA.MAZDA6));
//		vehicles.add(Pair.of(InternalManufacturers.MAZDA, InternalModels.MAZDA.MX_5));
//		
//		/* Mercedes-Benz models */
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.A_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.B_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.C_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.CL_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.CLA_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.CLC_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.CLK_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.CLS_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.E_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.G_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.GL_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.GLA_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.M_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.R_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.S_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.SL_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.SLK_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.SLS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.SPRINTER_TRAVELINER));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.V_CLASS));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.VIANO));
//		vehicles.add(Pair.of(InternalManufacturers.MERCEDES_BENZ, InternalModels.MERCEDES_BENZ.VITO));
//		
//		/* Suzuki models */
//		vehicles.add(Pair.of(InternalManufacturers.SUZUKI, InternalModels.SUZUKI.ALTO));
//		vehicles.add(Pair.of(InternalManufacturers.SUZUKI, InternalModels.SUZUKI.GRAND_VITARA));
//		vehicles.add(Pair.of(InternalManufacturers.SUZUKI, InternalModels.SUZUKI.JIMNY));
//		vehicles.add(Pair.of(InternalManufacturers.SUZUKI, InternalModels.SUZUKI.SPLASH));
//		vehicles.add(Pair.of(InternalManufacturers.SUZUKI, InternalModels.SUZUKI.SWIFT));
//		vehicles.add(Pair.of(InternalManufacturers.SUZUKI, InternalModels.SUZUKI.SX4));
//		vehicles.add(Pair.of(InternalManufacturers.SUZUKI, InternalModels.SUZUKI.VITARA));
//		
//		/* Nissan models */
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN._1400_CHAMP));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN._350Z));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN._370Z));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.ALMERA));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.GT_R));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.HARDBODY));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.JUKE));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.LIVINA));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.MAXIMA));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.MICRA));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.MURANO));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.NAVARA));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.NP200));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.NP300));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.NV200_COMBI));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.PATHFINDER));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.PATROL));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.PRIMASTAR_MINIBUS));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.QASHQAI));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.SENTRA));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.SKYLINE));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.TIIDA));
//		vehicles.add(Pair.of(InternalManufacturers.NISSAN, InternalModels.NISSAN.X_TRAIL));
//		
//
//		vehicles.add(Pair.of(InternalManufacturers.OPEL, InternalModels.OPEL.ASTRA));
//		vehicles.add(Pair.of(InternalManufacturers.OPEL, InternalModels.OPEL.CORSA_HATCHBACK));
//		vehicles.add(Pair.of(InternalManufacturers.OPEL, InternalModels.OPEL.CORSA_UTILITY));
//		vehicles.add(Pair.of(InternalManufacturers.OPEL, InternalModels.OPEL.MERIVA));
//		vehicles.add(Pair.of(InternalManufacturers.OPEL, InternalModels.OPEL.ZAFIRA));
//		
//		vehicles.add(Pair.of(InternalManufacturers.PEUGEOT, InternalModels.PEUGEOT._107));
//		vehicles.add(Pair.of(InternalManufacturers.PEUGEOT, InternalModels.PEUGEOT._2008));
//		vehicles.add(Pair.of(InternalManufacturers.PEUGEOT, InternalModels.PEUGEOT._206));
//		vehicles.add(Pair.of(InternalManufacturers.PEUGEOT, InternalModels.PEUGEOT._207));
//		vehicles.add(Pair.of(InternalManufacturers.PEUGEOT, InternalModels.PEUGEOT._208));
//		vehicles.add(Pair.of(InternalManufacturers.PEUGEOT, InternalModels.PEUGEOT._3008));
//		vehicles.add(Pair.of(InternalManufacturers.PEUGEOT, InternalModels.PEUGEOT._307));
//		vehicles.add(Pair.of(InternalManufacturers.PEUGEOT, InternalModels.PEUGEOT._308));
//		vehicles.add(Pair.of(InternalManufacturers.PEUGEOT, InternalModels.PEUGEOT._407));
//		vehicles.add(Pair.of(InternalManufacturers.PEUGEOT, InternalModels.PEUGEOT._508));
//		vehicles.add(Pair.of(InternalManufacturers.PEUGEOT, InternalModels.PEUGEOT.PARTNER));
//		vehicles.add(Pair.of(InternalManufacturers.PEUGEOT, InternalModels.PEUGEOT.RCZ));
//		vehicles.add(Pair.of(InternalManufacturers.PORSCHE, InternalModels.PORSCHE._911));
//		vehicles.add(Pair.of(InternalManufacturers.PORSCHE, InternalModels.PORSCHE.BOXSTER));
//		vehicles.add(Pair.of(InternalManufacturers.PORSCHE, InternalModels.PORSCHE.CAYENNE));
//		vehicles.add(Pair.of(InternalManufacturers.PORSCHE, InternalModels.PORSCHE.CAYMAN));
//		vehicles.add(Pair.of(InternalManufacturers.PORSCHE, InternalModels.PORSCHE.MACAN));
//		vehicles.add(Pair.of(InternalManufacturers.PORSCHE, InternalModels.PORSCHE.PANAMERA));
//		
//		
//		vehicles.add(Pair.of(InternalManufacturers.RENAULT, InternalModels.RENAULT.CLIO));
//		vehicles.add(Pair.of(InternalManufacturers.RENAULT, InternalModels.RENAULT.DUSTER));
//		vehicles.add(Pair.of(InternalManufacturers.RENAULT, InternalModels.RENAULT.FLUENCE));
//		vehicles.add(Pair.of(InternalManufacturers.RENAULT, InternalModels.RENAULT.GRAND_SCENIC));
//		vehicles.add(Pair.of(InternalManufacturers.RENAULT, InternalModels.RENAULT.KOLEOS));
//		vehicles.add(Pair.of(InternalManufacturers.RENAULT, InternalModels.RENAULT.LOGAN));
//		vehicles.add(Pair.of(InternalManufacturers.RENAULT, InternalModels.RENAULT.MEGANE));
//		vehicles.add(Pair.of(InternalManufacturers.RENAULT, InternalModels.RENAULT.SANDERO));
//		vehicles.add(Pair.of(InternalManufacturers.RENAULT, InternalModels.RENAULT.SCENIC));
//		vehicles.add(Pair.of(InternalManufacturers.RENAULT, InternalModels.RENAULT.TWINGO));
//		
//		
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA._86));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.AURIS));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.AVANZA));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.AVENSIS));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.AYGO));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.CAMRY));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.CONDOR));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.CONQUEST));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.COROLLA));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.COROLLA_QUEST));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.COROLLA_VERSO));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.ETIOS));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.FJ_CRUISER));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.FORTUNER));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.HILUX));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.INNOVA));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.LANDCRUISER));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.MR2));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.PRADO));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.PRIUS));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.QUANTUM));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.RAV4));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.RUNX));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.TAZZ));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.VERSO));
//		vehicles.add(Pair.of(InternalManufacturers.TOYOTA, InternalModels.TOYOTA.YARIS));
//		
//		vehicles.add(Pair.of(InternalManufacturers.VOLVO, InternalModels.VOLVO.C30));
//		vehicles.add(Pair.of(InternalManufacturers.VOLVO, InternalModels.VOLVO.C70));
//		vehicles.add(Pair.of(InternalManufacturers.VOLVO, InternalModels.VOLVO.S40));
//		vehicles.add(Pair.of(InternalManufacturers.VOLVO, InternalModels.VOLVO.S60));
//		vehicles.add(Pair.of(InternalManufacturers.VOLVO, InternalModels.VOLVO.S80));
//		vehicles.add(Pair.of(InternalManufacturers.VOLVO, InternalModels.VOLVO.V40));
//		vehicles.add(Pair.of(InternalManufacturers.VOLVO, InternalModels.VOLVO.V50));
//		vehicles.add(Pair.of(InternalManufacturers.VOLVO, InternalModels.VOLVO.V60));
//		vehicles.add(Pair.of(InternalManufacturers.VOLVO, InternalModels.VOLVO.V70));
//		vehicles.add(Pair.of(InternalManufacturers.VOLVO, InternalModels.VOLVO.XC60));
//		vehicles.add(Pair.of(InternalManufacturers.VOLVO, InternalModels.VOLVO.XC70));
//		vehicles.add(Pair.of(InternalManufacturers.VOLVO, InternalModels.VOLVO.XC90));
//		
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN._21ST_CENTURY_BEETLE));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.AMAROK));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.BEETLE));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.CADDY));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.CC));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.CHICO));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.CITI));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.GOLF));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.JETTA));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.KOMBI));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.PASSAT));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.POLO));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.POLO_VIVO));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.SCIROCCO));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.SHARAN));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.TIGUAN));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.TOUAREG));
//		vehicles.add(Pair.of(InternalManufacturers.VOLKSWAGEN, InternalModels.VOLKSWAGEN.TOURAN));
	}
}
