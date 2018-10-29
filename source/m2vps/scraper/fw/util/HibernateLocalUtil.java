package m2vps.scraper.fw.util;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.Stoppable;

public class HibernateLocalUtil
{
	protected static Logger log = Logger.getLogger(HibernateLocalUtil.class.getName());
	private static SessionFactory sessionFactory;
	
	static
	{
		try
		{
			// Create the SessionFactory from standard (hibernate.cfg.xml)
			// config file.
			sessionFactory = new Configuration().configure().buildSessionFactory();
		}
		catch (Throwable ex)
		{
			// Log the exception.
			log.error("Initial SessionFactory creation failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static void recreateFactory() throws Throwable
	{
		try
		{
			// Create the SessionFactory from standard (hibernate.cfg.xml)
			// config file.
			sessionFactory = new Configuration().configure().buildSessionFactory();
		}
		catch (Throwable ex)
		{
			// Log the exception.
			log.error("Initial SessionFactory creation failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}
	
	public static void closeSessionFactory()
	{
		sessionFactory.close();
	}
	
	public static void stopConnectionProvider()
	{
		closeSessionFactory();
		final SessionFactoryImplementor sessionFactoryImplementor =
			(SessionFactoryImplementor)sessionFactory;
		ConnectionProvider connectionProvider = sessionFactoryImplementor.getConnectionProvider();
		if (Stoppable.class.isInstance(connectionProvider))
		{
			((Stoppable)connectionProvider).stop();
		}
	}
}