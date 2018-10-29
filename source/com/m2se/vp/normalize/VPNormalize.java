
package com.m2se.vp.normalize;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.m2se.vp.database.Database;
import com.m2se.vp.scraper.automart.AutoMartScraper;
import com.m2se.vp.scraper.autotrader.AutoTraderScraper;
import com.m2se.vp.scraper.cars.CarsScraper;

/**
 * This class normalizes data from the scraper tables into the normalized_vehicle_data table
 *
 */
public class VPNormalize
{
	private static Logger LOG = Logger.getRootLogger();
	
	public static void main(String args[])
	{
		/* Configure Logger */
		HTMLLayout layout = new HTMLLayout();
		layout.setLocationInfo(true);
		layout.setTitle("M2SE VPNormalize");
		
		// creates daily rolling file appender
		DailyRollingFileAppender rollingAppender = new DailyRollingFileAppender();
		rollingAppender.setFile("./Log/normalize/m2seVPNormalize.html");
		rollingAppender.setDatePattern("'.'yyyyMMdd");
		rollingAppender.setLayout(layout);
		rollingAppender.activateOptions();
		
		LOG.setLevel(Level.INFO);
		LOG.addAppender(rollingAppender);
		
		LOG.info("[VPNormalize initiated]");
		
		
		/* Always do db connection test - exit if exception */
		try
		{
			Database.getConnection();
		}
		catch(SQLException se)
		{
			LOG.error("Could not obtain db connection. Check the mysql instance is started." + se);
			System.exit(1);
		}
		
		/* Run normalization process */
		ANormalize autoTraderNormalizer = new NormalizeAutotraderImpl();
		autoTraderNormalizer.normalize();
		
		LOG.info("[VPNormalize Finished]");
	}
}

