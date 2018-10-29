
package m2vps.scraper.fw.dao;

import java.util.LinkedList;
import java.util.List;

import m2vps.scraper.fw.VehicleDataSystem;
import m2vps.scraper.fw.util.HibernateLocalUtil;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class VehicleDataSystemDAO
{
	protected static Logger Log = Logger.getLogger(VehicleDataSystemDAO.class.getName());
	
	/**
	 * 
	 * TODO
	 *
	 * @return
	 */
	public int cleanSystem()
	{
		int result = -1;
		Session session = null;
		session = HibernateLocalUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = null;
		try
		{
			transaction = session.beginTransaction();
			String hql = "DELETE FROM VehicleDataSystem";
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
	public List<VehicleDataSystem> getAllVehicleDataSystem()
	{
		List<VehicleDataSystem> vehicleDataSystem = new LinkedList<VehicleDataSystem>();
		Session session = HibernateLocalUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = null;
		try
		{
			transaction = session.beginTransaction();
			String hql = "FROM VehicleDataSystem";
			Query query = session.createQuery(hql);
			vehicleDataSystem = query.list();
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
		
		return vehicleDataSystem;
	}
	
	public void saveOrUpdateVehicleDataSystem(VehicleDataSystem vehicleDataSystem)
	{
		Session session = null;
		session = HibernateLocalUtil.getSessionFactory().getCurrentSession();
		Transaction transaction = null;
		transaction = session.beginTransaction();
		
		try
		{
			session.saveOrUpdate(vehicleDataSystem);
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
			Log.error(e);
			throw e;
		}
		catch (Exception e)
		{
			Log.error(e);
			throw e;
		}
	}
}
