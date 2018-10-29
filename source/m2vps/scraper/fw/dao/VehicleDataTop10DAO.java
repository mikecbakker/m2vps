
package m2vps.scraper.fw.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import m2vps.scraper.fw.VehicleData;
import m2vps.scraper.fw.VehicleDataTop10;
import m2vps.scraper.fw.util.HibernateLocalUtil;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class VehicleDataTop10DAO
{
	protected static Logger Log = Logger.getLogger(VehicleDataTop10DAO.class.getName());
	
	/**
	 * 
	 * TODO
	 * 
	 * @return
	 */
	public int cleanTop10()
	{
		int result = -1;
		Session session = null;
		session = HibernateLocalUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = null;
		try
		{
			transaction = session.beginTransaction();
			String hql = "DELETE FROM VehicleDataTop10";
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
	 * TODO
	 * 
	 * @return
	 */
	public List<VehicleDataTop10> getAllVehicleDataTop10()
	{
		List<VehicleDataTop10> vehicleDataTop10 = new LinkedList<VehicleDataTop10>();
		Session session = HibernateLocalUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = null;
		try
		{
			transaction = session.beginTransaction();
			String hql = "FROM VehicleDataTop10";
			Query query = session.createQuery(hql);
			vehicleDataTop10 = query.list();
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
		
		return vehicleDataTop10;
	}
	
	public void saveOrUpdateVehicleDataTop10(List<VehicleData> vehicles)
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
				connection.prepareStatement("INSERT INTO vehicle_data_top_10 "
					+ "(source, manufacturer, model, mileage, price, year, weblink, web_result_page, "
					+ "province, ranking, vehicle_data_id) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?)");
			
			int i = 0;
			for (VehicleData vehicleData : vehicles)
			{
				i++;
				statement.setString(1, vehicleData.getSource());
				statement.setString(2, vehicleData.getManufacturer());
				statement.setString(3, vehicleData.getModel());
				statement.setString(4, vehicleData.getMileage());
				statement.setDouble(5, vehicleData.getPrice());
				statement.setString(6, vehicleData.getYear());
				statement.setString(7, vehicleData.getWeblink());
				statement.setString(8, vehicleData.getWebResultPage());
				statement.setString(9, vehicleData.getProvince());
				statement.setDouble(10, vehicleData.getRankingModel());
				statement.setLong(11, vehicleData.getId());
				statement.addBatch();
				if ((i + 1) % 1000 == 0)
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
	
}
