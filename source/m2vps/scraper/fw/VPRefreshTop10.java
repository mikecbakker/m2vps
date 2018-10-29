package m2vps.scraper.fw;

import java.util.Iterator;
import java.util.List;

import m2vps.scraper.fw.dao.VehicleDAO;
import m2vps.scraper.fw.util.HibernateLocalUtil;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class VPRefreshTop10
{
	
	private VehicleDAO vehicleDAO = new VehicleDAO();

	protected VPCalculator vpCalculator = new VPCalculator();
	

	public void refresh()
	{
		for (int i = 0; i < Scraper.vehicles.size(); i ++)
		{
			Pair<String,String> vehicle = Scraper.vehicles.get(i);
			String manufacturer = vehicle.getLeft();
			String model = vehicle.getRight();
			
			Log.info("[Refresh] " + manufacturer + "-" + model);
			
			List<VehicleData> existingVehicles = vehicleDAO.getEnabled(manufacturer, model);
			
			Iterator<VehicleData> iterator = existingVehicles.iterator();
			int topModelsCounter = 0;
			int totalDelete = 0;
			
			while (iterator.hasNext() && topModelsCounter < 5)
			{
				VehicleData vehicleData = (VehicleData)iterator.next();
				
				AScraper scraper = Scraper.scrapers.get(vehicleData.getSource());
				
				/* Remove vehicles that don't exist anymore */
				if (!scraper.checkVehicleExists(vehicleData))
				{
					Log.info("[Refresh] Remove Expired Vehicle " + vehicleData.toString());
					vehicleData.setExpired(true);
					vehicleDAO.saveOrUpdateVehicle(vehicleData);
					iterator.remove();
					totalDelete++;
				}
				else
				{
					topModelsCounter++;
				}
			}
			Log.info("[Refresh] Total Vehicle Removed = " + totalDelete );
		}
		
		Log.info("[Refresh] Recalculate System Data");
		VPCalculator vpCalculator = new VPCalculator();
		vpCalculator.calculate();
		Log.info("[Refresh] Publish to remote");
		vpCalculator.publishToRemote();
	}
	
	public static Logger Log = Logger.getRootLogger();

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
		layout.setTitle("M2VPS Refresh Top 10 - Trace file");
		
		// creates daily rolling file appender
		DailyRollingFileAppender rollingAppender = new DailyRollingFileAppender();
		rollingAppender.setFile("./Log/refresh/m2vps_refresh_top10_trace.html");
		rollingAppender.setDatePattern("'.'yyyyMMdd");
		rollingAppender.setLayout(layout);
		rollingAppender.activateOptions();
		
		Log.setLevel(Level.INFO);
		Log.addAppender(rollingAppender);
		
		Log.info("[Refresh] initiated");
		
		VPRefreshTop10 vpRefresher = new VPRefreshTop10();
		
		vpRefresher.refresh();
		
		/* Last thing we do is cleanup */
		HibernateLocalUtil.stopConnectionProvider();
		
		org.apache.log4j.LogManager.shutdown();
	}
}
