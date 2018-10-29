
package m2vps.scraper.fw.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import m2vps.scraper.fw.VehicleYearModelData;
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
public class VehicleYearModelDataDAO
{
	protected static Logger Log = Logger.getLogger(VehicleYearModelDataDAO.class.getName());
	
	
	/**
	 * 
	 * TODO
	 * 
	 * @return
	 */
	public int cleanYearModelData()
	{
		int result = -1;
		Session session = null;
		session = HibernateLocalUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = null;
		try
		{
			transaction = session.beginTransaction();
			String hql = "DELETE FROM VehicleYearModelData";
			Query query = session.createQuery(hql);
			
			result = query.executeUpdate();
			transaction.commit();
		}
		catch (HibernateException e)
		{
			if (transaction != null)
			{
				transaction.rollback();
			}
			System.out.println(e);
			Log.error(e);
		}
		catch (Exception e)
		{
			Log.error(e);
			System.out.println(e);
		}
		
		return result;
	}
	
	/**
	 * 
	 */
	public void addYearModelData(List<VehicleYearModelData> vehicleYearModelDatas)
	{
		Connection connection = null;
		PreparedStatement statement = null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			/* Get a local connection */
			connection =
				DriverManager.getConnection("jdbc:mysql://localhost:3306/autobv?"
					+ "user=vp_user&password=67utEdh5g667utrg6EWY&rewriteBatchedStatements=true");
			
			statement =
				connection.prepareStatement("INSERT INTO vehicle_year_model_data "
					+ "(avg_mileage, avg_price, created, manufacturer, model, number_vehicles, year) "
					+ "VALUES (?,?,?,?,?,?,?)");
			
			int i = 0;
			for (VehicleYearModelData vehicleYearModelData : vehicleYearModelDatas)
			{
				i++;
				statement.setDouble(1, vehicleYearModelData.getAvgMileage());
				statement.setDouble(2, vehicleYearModelData.getAvgPrice());
				statement.setDate(3, new java.sql.Date(vehicleYearModelData.getCreated().getTime()));
				statement.setString(4, vehicleYearModelData.getManufacturer());
				statement.setString(5, vehicleYearModelData.getModel());
				statement.setLong(6, vehicleYearModelData.getNumberVehicle());
				statement.setString(7, vehicleYearModelData.getYear());
				statement.addBatch();
				if ((i+1) % 500 == 0)
				{
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
	
	public List<VehicleYearModelData> getAllVehicleYearModelData()
	{
		List<VehicleYearModelData> vehicleYearModelData = new LinkedList<VehicleYearModelData>();
		Session session = HibernateLocalUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = null;
		try
		{
			transaction = session.beginTransaction();
			String hql = "FROM VehicleYearModelData";
			Query query = session.createQuery(hql);
			vehicleYearModelData = query.list();
			transaction.commit();
		}
		catch (HibernateException e)
		{
			if (transaction != null)
			{
				transaction.rollback();
			}
			System.out.println(e);
			Log.error(e);
		}
		catch (Exception e)
		{
			Log.error(e);
			System.out.println(e);
		}
		return vehicleYearModelData;
	}
}
