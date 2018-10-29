package m2vps.scraper.fw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import m2vps.scraper.fw.dao.VehicleCalculatorDAO;
import m2vps.scraper.fw.dao.VehicleDAO;
import m2vps.scraper.fw.dao.VehicleDataSystemDAO;
import m2vps.scraper.fw.dao.VehicleDataTop10DAO;
import m2vps.scraper.fw.dao.VehicleYearModelDataDAO;
import m2vps.scraper.fw.util.HibernateLocalUtil;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class VPCalculator
{
	
	private VehicleDAO vehicleDAO = new VehicleDAO();
	private VehicleCalculatorDAO vehicleCalculatorDAO = new VehicleCalculatorDAO();
	private VehicleYearModelDataDAO vehicleYearModelDataDAO = new VehicleYearModelDataDAO();
	private VehicleDataTop10DAO vehicleTop10DAO = new VehicleDataTop10DAO();
	private VehicleDataSystemDAO vehicleSystemDAO = new VehicleDataSystemDAO();
	
	
	public void calculate()
	{
		/* Clean the tables for load */
		vehicleTop10DAO.cleanTop10();
		vehicleYearModelDataDAO.cleanYearModelData();
		vehicleSystemDAO.cleanSystem();
		
		/* Rows to save */
		List<VehicleData> vehiclesTop10 = new ArrayList<VehicleData>();
		List<VehicleYearModelData> vehicleYearModelDataToSave = new ArrayList<VehicleYearModelData>();
		
		for (int i = 0; i < Scraper.vehicles.size(); i ++)
		{
			Pair<String,String> vehicle = Scraper.vehicles.get(i);
			String manufacturer = vehicle.getLeft();
			String model = vehicle.getRight();
			
			Log.info("[Calculator] Start " + manufacturer + "-" + model);
			List<VehicleData> activeCurrModelVehicles = vehicleDAO.getEnabledJDBC(manufacturer, model);
			
			Log.info("[Calculator] Rank " + manufacturer + "-" + model);
			rank(activeCurrModelVehicles);
			
			Log.info("[Calculator] Year Model Data " + manufacturer + "-" + model);
			calculateVehicleYearModelData(activeCurrModelVehicles, vehicleYearModelDataToSave);
			
			Log.info("[Calculator] Top 10 " + manufacturer + "-" + model);
			calculateTop10Vehicles(manufacturer, model,vehiclesTop10);
		}
		
		/* Write top 10 data*/
		vehicleTop10DAO.saveOrUpdateVehicleDataTop10(vehiclesTop10);
		/* Write year model data */
		vehicleYearModelDataDAO.addYearModelData(vehicleYearModelDataToSave);
		
		Log.info("[Calculator] System wide ranking");
		/* Calculate system wide ranking */
		calculateSystem();
	}
	
	/**
	 * 
	 * TODO
	 *
	 */
	public void publishToRemote()
	{
		/* Get all year model data (local) */
		Log.info("Load local vehicle year model data");
		List<VehicleYearModelData> vehicleYearModelData = vehicleYearModelDataDAO.getAllVehicleYearModelData();
		Log.info("Loaded vehicle year model data");
		
		/* Get all top 10 data */
		Log.info("Load top10 data");
		List<VehicleDataTop10> vehicleTop10Data = vehicleTop10DAO.getAllVehicleDataTop10();
		Log.info("Loaded top10 data");
		
		/* Get all system data */
		Log.info("Load system data");
		List<VehicleDataSystem> vehicleDataSystem = vehicleSystemDAO.getAllVehicleDataSystem();
		Log.info("Loaded system data");
		
		/* Clean and load year model data */
		Log.info("Performing vehicle year model data load to remote");
		vehicleCalculatorDAO.updateVehicleYearModelDataRemote(vehicleYearModelData);
		Log.info("Load to remote completed - vehicle year model data");

		/* Clean and load top 10 data */
		Log.info("Performing top10 data load to remote");
		vehicleCalculatorDAO.updateVehicleDataTop10Remote(vehicleTop10Data);
		Log.info("Load to remote completed - top10 data");
		
		/* Clean and load system data */
		Log.info("Performing vehicle system data load to remote");
		vehicleCalculatorDAO.updateVehicleDataSystemRemote(vehicleDataSystem);
		Log.info("Load to remote completed - vehicle system data");
	}
	
	/**
	 * 
	 * Ranks vehicles based on mileage and price
	 *
	 * @param vehicles
	 */
	public void rank(List<VehicleData> vehicles)
	{
		Log.info("[Ranking] Milage");
		rankMilage(vehicles);
		Log.info("[Ranking] Price");
		rankPrice(vehicles);
		
		Log.info("[Ranking] Saving...");
		vehicleDAO.saveOrUpdateVehicles(vehicles);
	}
	
	/**
	 * 
	 * Ranks vehicles based on mileage.
	 *
	 * @param vehicles
	 */
	private void rankMilage(List<VehicleData> vehicles)
	{
		Collections.sort(vehicles, new Comparator<VehicleData>()
			{
				@Override
				public int compare(VehicleData v1, VehicleData v2)
				{
					long v1Milage = Long.parseLong(v1.getMileage());
					long v2Milage = Long.parseLong(v2.getMileage());
					
					if (v1Milage == v2Milage)
					{
						return 0;
					}
					return v1Milage < v2Milage ? 1 : -1;
				}
			});
		
		for (int i = 0; i < vehicles.size(); i++)
		{
			vehicles.get(i).setRankingMilage((double)i / (double)vehicles.size());
		}
	}
	
	/**
	 * 
	 * Calculates the vehicle price ranks
	 *
	 * @param vehicles
	 */
	private void rankPrice(List<VehicleData> vehicles)
	{
		Collections.sort(vehicles, new Comparator<VehicleData>()
			{
				@Override
				public int compare(VehicleData v1, VehicleData v2)
				{
					double v1Price = v1.getPrice();
					double v2Price = v2.getPrice();
					
					if (v1Price == v2Price)
					{
						return 0;
					}
					return v1Price < v2Price ? 1 : -1;
				}
			});
		
		for (int i = 0; i < vehicles.size(); i++)
		{
			VehicleData v = vehicles.get(i);
			v.setRankingPrice((double)i / (double)vehicles.size());
			v.setRankingModel((v.getRankingMilage() + v.getRankingPrice()) / 2.0);
			
			if (vehicles.size() > 40)
			{
				v.setRankingSystem(v.getRankingModel());
			}
			else
			{
				v.setRankingSystem(0);
			}
		}
	}
	
	/**
	 * 
	 * Updates the vehicle year model data table with calculated averages for mileage and price
	 *
	 * @param vehicles
	 */
	private void calculateVehicleYearModelData(List<VehicleData> vehicles, List<VehicleYearModelData> vehiclesToSave)
	{
		Hashtable<String, VehicleYearModelData> vehicleYearModelDataHash = new Hashtable<String, VehicleYearModelData>();
		
		for (VehicleData vehicleData : vehicles)
		{
			VehicleYearModelData vehicleYearModelData = vehicleYearModelDataHash.get(vehicleData.getYear());
			
			if (vehicleYearModelData == null)
			{
				vehicleYearModelData = new VehicleYearModelData();
				vehicleYearModelData.setAvgMileage(0);
				vehicleYearModelData.setAvgPrice(0);
				vehicleYearModelData.setNumberVehicle(0);
				vehicleYearModelData.setCreated(new Date());
				vehicleYearModelData.setManufacturer(vehicleData.getManufacturer());
				vehicleYearModelData.setModel(vehicleData.getModel());
				vehicleYearModelData.setYear(vehicleData.getYear());
				vehicleYearModelDataHash.put(vehicleData.getYear(), vehicleYearModelData);
			}
			
			vehicleYearModelData.setAvgMileage(vehicleYearModelData.getAvgMileage() +
				Long.parseLong(vehicleData.getMileage()));
			vehicleYearModelData.setAvgPrice(vehicleYearModelData.getAvgPrice() + vehicleData.getPrice());
			vehicleYearModelData.setNumberVehicle(vehicleYearModelData.getNumberVehicle() + 1);
		}
		
		for (String year : vehicleYearModelDataHash.keySet())
		{
			VehicleYearModelData vehicleYearModelData = vehicleYearModelDataHash.get(year);
			
			vehicleYearModelData.setAvgMileage(vehicleYearModelData.getAvgMileage() /
				vehicleYearModelData.getNumberVehicle());
			vehicleYearModelData.setAvgPrice(vehicleYearModelData.getAvgPrice() / vehicleYearModelData.getNumberVehicle());
			vehiclesToSave.add(vehicleYearModelData);
		}
	}
	
	/**
	 * 	
	 * Calculates top 10 cars for each model
	 *
	 * @param manufacturer
	 * @param model
	 */
	protected void calculateTop10Vehicles(String manufacturer,String model, List<VehicleData> vehicles)
	{
		vehicleDAO.getTop10Vehicles(manufacturer, model, vehicles);
	}
	
	/**
	 * 
	 * Calculates best cars system wide based on band
	 *
	 */
	protected void calculateSystem()
	{
		for(int band = 800;band < 15000;band+=100)
		{
			double minPrice = band - (band/10);
			double maxPrice = band + (band/10);
			
			List<VehicleData> vehicles = vehicleDAO.getTop10SystemVehicles(getPrice(minPrice),getPrice(maxPrice));
			
			for (VehicleData vehicleData : vehicles)
			{
				VehicleDataSystem vehicleDataSystem = new VehicleDataSystem(vehicleData, band);
				vehicleSystemDAO.saveOrUpdateVehicleDataSystem(vehicleDataSystem);
			}
		}
	}
	
	/**
	 * 
	 * Calculates the principal value based on 72 month repayment period and an interest rate of 9%
	 *
	 * @param monthlyPrice
	 * @return the principal value
	 */
	public double getPrice(double monthlyPrice)
	{
		int num_periods = -72; // 72 months
		double interest_rate = 9.0/(12.0*100.0); // 9%
		
		double principle_value = monthlyPrice/interest_rate * (1 - Math.pow((1 + interest_rate), num_periods));
		
		return principle_value;
	}
	

	protected static Logger Log = Logger.getRootLogger();
	/**
	 * 
	 * Entry point
	 *
	 * @param args
	 */
	public static void main(String args[])
	{
		/* Configure Logger */
		HTMLLayout layout = new HTMLLayout();
		layout.setLocationInfo(true);
		layout.setTitle("M2VPS Calculator - Trace file");
		
		// creates daily rolling file appender
		DailyRollingFileAppender rollingAppender = new DailyRollingFileAppender();
		rollingAppender.setFile("./Log/calculator/m2vps_calculator_trace.html");
		rollingAppender.setDatePattern("'.'yyyyMMdd");
		rollingAppender.setLayout(layout);
		rollingAppender.activateOptions();
		
		Log.setLevel(Level.INFO);
		Log.addAppender(rollingAppender);
		
		Log.info("[Calculator initiated]");
	
		VPCalculator vpCalculator = new VPCalculator();
		Log.error("Calc start");
		vpCalculator.calculate();
		Log.error("Calc end");
		
		/* Publish to remote */
		Log.error("publish to remote start");
		//vpCalculator.publishToRemote();
		Log.error("publish to remote end");
		
		/* Last thing we do is cleanup */
		HibernateLocalUtil.stopConnectionProvider();
		org.apache.log4j.LogManager.shutdown();
		System.out.println("[Calculator completed]");
	}
}
