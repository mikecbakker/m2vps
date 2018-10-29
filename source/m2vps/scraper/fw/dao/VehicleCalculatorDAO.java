
package m2vps.scraper.fw.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import m2vps.scraper.fw.VehicleDataSystem;
import m2vps.scraper.fw.VehicleDataTop10;
import m2vps.scraper.fw.VehicleYearModelData;

import org.apache.log4j.Logger;

/**
 * Represents the DAO object for a job posting
 * 
 */
public class VehicleCalculatorDAO
{
	protected static Logger Log = Logger.getLogger(VehicleCalculatorDAO.class.getName());
	
	/**
	 * 
	 * TODO
	 * 
	 * @param remoteVehicleYearModelData
	 */
	public void updateVehicleYearModelDataRemote(List<VehicleYearModelData> vehicles)
	{
		Connection connection = null;
		PreparedStatement statement = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connection =
				DriverManager.getConnection("jdbc:mysql://192.186.255.166:3306/autobv?"
					+ "user=vp_user&password=67utEdh5g667utrg6EWY&rewriteBatchedStatements=true");
			
			/* Clean table first */
			System.out.println("Cleaning vehicle_year_model_data table");
			statement = connection.prepareStatement("TRUNCATE TABLE vehicle_year_model_data");
			statement.addBatch();
			statement.executeBatch();
			statement.close();
			System.out.println("Loading vehicle_year_model_data");
			
			statement =
				connection.prepareStatement("INSERT INTO vehicle_year_model_data "
					+ "(avg_mileage, avg_price, created, manufacturer, model, number_vehicles, year) "
					+ "VALUES (?,?,?,?,?,?,?)");
			
			int i = 0;
			for (VehicleYearModelData vehicleData : vehicles)
			{
				i++;
				statement.setLong(1, vehicleData.getAvgMileage());
				statement.setDouble(2, vehicleData.getAvgPrice());
				statement.setDate(3, new java.sql.Date(vehicleData.getCreated().getTime()));
				statement.setString(4, vehicleData.getManufacturer());
				statement.setString(5, vehicleData.getModel());
				statement.setLong(6, vehicleData.getNumberVehicle());
				statement.setString(7, vehicleData.getYear());
				statement.addBatch();
				if ((i + 1) % 500 == 0)
				{
					System.out.println("Flushing batch to remote");
					statement.executeBatch();
				}
			}
			statement.executeBatch();
			statement.close();
		}
		catch (Exception e)
		{
			System.out.println("Exception" + e);
		}
		finally
		{
			if (statement != null)
				try
				{
					statement.close();
				}
				catch (SQLException logOrIgnore)
				{
					//
				}
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (SQLException logOrIgnore)
				{
					//
				}
			}
		}
	}
	
	/**
	 * 
	 * TODO
	 * 
	 * @param remoteVehicleYearModelData
	 */
	public void updateVehicleDataTop10Remote(List<VehicleDataTop10> vehicleDataTop10Rows)
	{
		Connection connection = null;
		PreparedStatement statement = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connection =
				DriverManager.getConnection("jdbc:mysql://192.186.255.166:3306/autobv?"
					+ "user=vp_user&password=67utEdh5g667utrg6EWY&rewriteBatchedStatements=true");
			
			/* Clean table first */
			System.out.println("Cleaning vehicle_data_top_10 table");
			statement = connection.prepareStatement("TRUNCATE TABLE vehicle_data_top_10");
			statement.addBatch();
			statement.executeBatch();
			statement.close();
			System.out.println("Loading vehicle_data_top_10");
			
			statement =
				connection
					.prepareStatement("INSERT INTO vehicle_data_top_10 "
						+ "(manufacturer, mileage, model, price, province, ranking, source, vehicle_data_id, web_result_page, weblink, year) "
						+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)");
			
			int i = 0;
			for (VehicleDataTop10 vehicleDataTop10Row : vehicleDataTop10Rows)
			{
				i++;
				statement.setString(1, vehicleDataTop10Row.getManufacturer());
				statement.setString(2, vehicleDataTop10Row.getMileage());
				statement.setString(3, vehicleDataTop10Row.getModel());
				statement.setDouble(4, vehicleDataTop10Row.getPrice());
				statement.setString(5, vehicleDataTop10Row.getProvince());
				statement.setDouble(6, vehicleDataTop10Row.getRanking());
				statement.setString(7, vehicleDataTop10Row.getSource());
				statement.setLong(8, vehicleDataTop10Row.getVehicleData());
				statement.setString(9, vehicleDataTop10Row.getWebResultPage());
				statement.setString(10, vehicleDataTop10Row.getWeblink());
				statement.setString(11, vehicleDataTop10Row.getYear());
				
				statement.addBatch();
				if ((i + 1) % 500 == 0)
				{
					System.out.println("Flushing batch to remote");
					statement.executeBatch();
				}
			}
			statement.executeBatch();
			statement.close();
		}
		catch (Exception e)
		{
			System.out.println("Exception" + e);
		}
		finally
		{
			if (statement != null)
				try
				{
					statement.close();
				}
				catch (SQLException logOrIgnore)
				{
				}
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (SQLException logOrIgnore)
				{
				}
			}
		}
	}
	
	/**
	 * 
	 * TODO
	 * 
	 * @param remoteVehicleYearModelData
	 */
	public void updateVehicleDataSystemRemote(List<VehicleDataSystem> vehicleDataSystemRows)
	{
		Connection connection = null;
		PreparedStatement statement = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connection =
				DriverManager.getConnection("jdbc:mysql://192.186.255.166:3306/autobv?"
					+ "user=vp_user&password=67utEdh5g667utrg6EWY&rewriteBatchedStatements=true");
			
			/* Clean table first */
			System.out.println("Cleaning vehicle_data_system table");
			statement = connection.prepareStatement("TRUNCATE TABLE vehicle_data_system");
			statement.addBatch();
			statement.executeBatch();
			statement.close();
			System.out.println("Loading vehicle_data_system");
			
			statement =
				connection
					.prepareStatement("INSERT INTO vehicle_data_system "
						+ "(manufacturer, mileage, model, price, province, ranking, source, vehicle_data_id, web_result_page, weblink, year,band) "
						+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			
			int i = 0;
			for (VehicleDataSystem vehicleDataSystemRow : vehicleDataSystemRows)
			{
				i++;
				statement.setString(1, vehicleDataSystemRow.getManufacturer());
				statement.setString(2, vehicleDataSystemRow.getMileage());
				statement.setString(3, vehicleDataSystemRow.getModel());
				statement.setDouble(4, vehicleDataSystemRow.getPrice());
				statement.setString(5, vehicleDataSystemRow.getProvince());
				statement.setDouble(6, vehicleDataSystemRow.getRanking());
				statement.setString(7, vehicleDataSystemRow.getSource());
				statement.setLong(8, vehicleDataSystemRow.getVehicleData());
				statement.setString(9, vehicleDataSystemRow.getWebResultPage());
				statement.setString(10, vehicleDataSystemRow.getWeblink());
				statement.setString(11, vehicleDataSystemRow.getYear());
				statement.setString(12, vehicleDataSystemRow.getBand());
				
				statement.addBatch();
				if ((i + 1) % 500 == 0)
				{
					System.out.println("Flushing batch to remote");
					statement.executeBatch();
				}
			}
			statement.executeBatch();
			statement.close();
		}
		catch (Exception e)
		{
			System.out.println("Exception");
			e.printStackTrace(System.out);
		}
		finally
		{
			if (statement != null)
				try
				{
					statement.close();
				}
				catch (SQLException logOrIgnore)
				{
				}
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (SQLException logOrIgnore)
				{
				}
			}
		}
	}
}
