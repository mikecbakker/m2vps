
package m2vps.scraper.fw.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import m2vps.scraper.fw.VehicleData;
import m2vps.scraper.fw.util.HibernateLocalUtil;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Represents the DAO object for a job posting
 * 
 */
public class VehicleDAO
{
	protected static Logger log = Logger.getLogger(VehicleDAO.class.getName());
	
	public void saveOrUpdateVehicles(Collection<VehicleData> vehicles)
	{
		log.info("Saving Vehicles [" + vehicles.size() + "]");
		Session session = HibernateLocalUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = null;
		transaction = session.beginTransaction();
		
		try
		{
			int i = 0;
			for (VehicleData vehicleData : vehicles)
			{
				i++;
				session.saveOrUpdate(vehicleData);
				if (i % 20 == 0)
				{
					// 20, same as the JDBC batch size
					// flush a batch of inserts and release memory:
					session.flush();
					session.clear();
				}
			}
			session.flush();
			session.clear();
			transaction.commit();
		}
		catch (HibernateException e)
		{
			if (transaction != null)
			{
				transaction.rollback();
			}
			log.error(e);
			throw e;
		}
		catch (Exception e)
		{
			log.error(e);
			throw e;
		}
	}
	
	/**
	 * 
	 * Saves a new job posting or updates if present.
	 * 
	 * @param vehicle
	 */
	public void saveOrUpdateVehicle(VehicleData vehicle)
	{
		vehicle.setLastUpdated(new Date());
		Session session = HibernateLocalUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = null;
		try
		{
			transaction = session.beginTransaction();
			session.saveOrUpdate(vehicle);
			transaction.commit();
		}
		catch (HibernateException e)
		{
			if (transaction != null)
			{
				transaction.rollback();
			}
			log.error(e);
		}
		catch (Exception e)
		{
			log.error(e);
		}
	}
	
	/**
	 * 
	 * TODO
	 *
	 * @param make
	 * @param model
	 * @return
	 */
	public List<VehicleData> getEnabled(String make, String model)
	{
		List<VehicleData> vehicles = new LinkedList<VehicleData>();
		Session session = HibernateLocalUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = null;
		try
		{
			transaction = session.beginTransaction();
			String hql =
				"FROM VehicleData "
					+ "WHERE duplicate = false and outlier = false and  manufacturer = :manufacturer AND model = :model  order by ranking_model DESC";
			Query query = session.createQuery(hql);
			query.setParameter("manufacturer", make);
			query.setParameter("model", model);
			vehicles = query.list();
			transaction.commit();
		}
		catch (HibernateException e)
		{
			if (transaction != null)
			{
				transaction.rollback();
			}
			System.out.println(e);
			log.error(e);
		}
		catch (Exception e)
		{
			log.error(e);
			System.out.println(e);
		}
		return vehicles;
	}
	
	public List<VehicleData> getEnabledJDBC(String make, String model)
	{
		List<VehicleData> vehicles = new LinkedList<VehicleData>();
		Connection connection = null;
		PreparedStatement statement = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connection =
				DriverManager.getConnection("jdbc:mysql://localhost:3306/autobv?"
					+ "user=vp_user&password=67utEdh5g667utrg6EWY&rewriteBatchedStatements=true");
			
			String query =
				"SELECT id, created, deviation, duplicate, expired, last_updated, manufacturer, model, outlier, price, "
					+ "province, ranking_milage, ranking_price, ranking_model, ranking_system, source, weblink, web_result_page, year, mileage "
					+ "FROM vehicle_data "
					+ "WHERE duplicate = false AND outlier = false AND manufacturer = ? AND model = ?";
			statement = connection.prepareStatement(query);
			statement.setString(1, make);
			statement.setString(2, model);
			
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				VehicleData vehicleData = new VehicleData();
				vehicleData.setId(rs.getLong(1));
				vehicleData.setCreated(rs.getDate(2));
				vehicleData.setDeviation(rs.getLong(3));
				vehicleData.setDuplicate(rs.getBoolean(4));
				vehicleData.setExpired(rs.getBoolean(5));
				vehicleData.setLastUpdated(rs.getDate(6));
				vehicleData.setManufacturer(rs.getString(7));
				vehicleData.setModel(rs.getString(8));
				vehicleData.setOutlier(rs.getBoolean(9));
				vehicleData.setPrice(rs.getLong(10));
				vehicleData.setProvince(rs.getString(11));
				vehicleData.setRankingMilage(rs.getLong(12));
				vehicleData.setRankingPrice(rs.getLong(13));
				vehicleData.setRankingModel(rs.getLong(14));
				vehicleData.setRankingSystem(rs.getLong(15));
				vehicleData.setSource(rs.getString(16));
				vehicleData.setWeblink(rs.getString(17));
				vehicleData.setWebResultPage(rs.getString(18));
				vehicleData.setYear(rs.getString(19));
				vehicleData.setMileage(rs.getString(20));
				
				vehicles.add(vehicleData);
			}
			rs.close();
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
		return vehicles;
	}
	
	/**
	 * Get the available vehicles, it should not be an outlier or duplicate
	 * 
	 * @param make
	 * @param model
	 * @return
	 */
	public List<VehicleData> getTop10Vehicles(String make, String model, List<VehicleData> vehicles)
	{
			Connection connection = null;
			PreparedStatement statement = null;
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				connection =
					DriverManager.getConnection("jdbc:mysql://localhost:3306/autobv?"
						+ "user=vp_user&password=67utEdh5g667utrg6EWY&rewriteBatchedStatements=true");
				
				String query =
					"SELECT id, created, deviation, duplicate, expired, last_updated, manufacturer, model, outlier, price, "
						+ "province, ranking_milage, ranking_price, ranking_model, ranking_system, source, weblink, web_result_page, year, mileage "
						+ "FROM vehicle_data "
						+ "WHERE expired = false AND duplicate = false AND outlier = false AND manufacturer = ? AND model = ?  ORDER BY ranking_model DESC";
				statement = connection.prepareStatement(query);
				statement.setString(1, make);
				statement.setString(2, model);
				
				ResultSet rs = statement.executeQuery();
				while (rs.next())
				{
					VehicleData vehicleData = new VehicleData();
					vehicleData.setId(rs.getLong(1));
					vehicleData.setCreated(rs.getDate(2));
					vehicleData.setDeviation(rs.getLong(3));
					vehicleData.setDuplicate(rs.getBoolean(4));
					vehicleData.setExpired(rs.getBoolean(5));
					vehicleData.setLastUpdated(rs.getDate(6));
					vehicleData.setManufacturer(rs.getString(7));
					vehicleData.setModel(rs.getString(8));
					vehicleData.setOutlier(rs.getBoolean(9));
					vehicleData.setPrice(rs.getLong(10));
					vehicleData.setProvince(rs.getString(11));
					vehicleData.setRankingMilage(rs.getLong(12));
					vehicleData.setRankingPrice(rs.getLong(13));
					vehicleData.setRankingModel(rs.getLong(14));
					vehicleData.setRankingSystem(rs.getLong(15));
					vehicleData.setSource(rs.getString(16));
					vehicleData.setWeblink(rs.getString(17));
					vehicleData.setWebResultPage(rs.getString(18));
					vehicleData.setYear(rs.getString(19));
					vehicleData.setMileage(rs.getString(20));
					
					vehicles.add(vehicleData);
				}
				rs.close();
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
			return vehicles;
	}
	
	/**
	 * Get the available vehicles, it should not be an outlier or duplicate
	 * 
	 * @param make
	 * @param model
	 * @return
	 */
	public List<VehicleData> getTop10SystemVehicles(double minPrice, double maxPrice)
	{
		List<VehicleData> vehicles = new LinkedList<VehicleData>();
		Connection connection = null;
		PreparedStatement statement = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			connection =
				DriverManager.getConnection("jdbc:mysql://localhost:3306/autobv?"
					+ "user=vp_user&password=67utEdh5g667utrg6EWY&rewriteBatchedStatements=true");
			
			String query =
				"SELECT id, created, deviation, duplicate, expired, last_updated, manufacturer, model, outlier, price, "
					+ "province, ranking_milage, ranking_price, ranking_model, ranking_system, source, weblink, web_result_page, year, mileage "
					+ "FROM vehicle_data "
					+ "WHERE expired = false AND duplicate = false AND outlier = false AND price > ? AND price < ?  "
					+ "ORDER BY ranking_system DESC LIMIT 10";
			//System.out.println(query);
			statement = connection.prepareStatement(query);
			statement.setDouble(1, minPrice);
			statement.setDouble(2, maxPrice);
			
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				VehicleData vehicleData = new VehicleData();
				vehicleData.setId(rs.getLong(1));
				vehicleData.setCreated(rs.getDate(2));
				vehicleData.setDeviation(rs.getLong(3));
				vehicleData.setDuplicate(rs.getBoolean(4));
				vehicleData.setExpired(rs.getBoolean(5));
				vehicleData.setLastUpdated(rs.getDate(6));
				vehicleData.setManufacturer(rs.getString(7));
				vehicleData.setModel(rs.getString(8));
				vehicleData.setOutlier(rs.getBoolean(9));
				vehicleData.setPrice(rs.getLong(10));
				vehicleData.setProvince(rs.getString(11));
				vehicleData.setRankingMilage(rs.getLong(12));
				vehicleData.setRankingPrice(rs.getLong(13));
				vehicleData.setRankingModel(rs.getLong(14));
				vehicleData.setRankingSystem(rs.getLong(15));
				vehicleData.setSource(rs.getString(16));
				vehicleData.setWeblink(rs.getString(17));
				vehicleData.setWebResultPage(rs.getString(18));
				vehicleData.setYear(rs.getString(19));
				vehicleData.setMileage(rs.getString(20));
				
				vehicles.add(vehicleData);
			}
			rs.close();
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
		return vehicles;
	}
	
	public int delete(VehicleData vehicle)
	{
		int result = -1;
		Session session = HibernateLocalUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = null;
		try
		{
			transaction = session.beginTransaction();
			session.delete(vehicle);
			transaction.commit();
		}
		catch (HibernateException e)
		{
			if (transaction != null)
			{
				transaction.rollback();
			}
			System.out.println(e);
			log.error(e);
		}
		catch (Exception e)
		{
			log.error(e);
			System.out.println(e);
		}
		
		return result;
	}
}
