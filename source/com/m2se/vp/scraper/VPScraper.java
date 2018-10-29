package com.m2se.vp.scraper;

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

public class VPScraper
{
	private static Logger LOG = Logger.getRootLogger();
	
	public static void main(String[] args) throws InterruptedException
	{
		/* Configure Logger */
		HTMLLayout layout = new HTMLLayout();
		layout.setLocationInfo(true);
		layout.setTitle("M2SE VPScraper");
		
		// creates daily rolling file appender
		DailyRollingFileAppender rollingAppender = new DailyRollingFileAppender();
		rollingAppender.setFile("./Log/scraper/m2seVPScraper.html");
		rollingAppender.setDatePattern("'.'yyyyMMdd");
		rollingAppender.setLayout(layout);
		rollingAppender.activateOptions();
		
		LOG.setLevel(Level.INFO);
		LOG.addAppender(rollingAppender);
		
		LOG.info("[VPScraper initiated]");

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
		
		List<Thread> scrapers = new LinkedList<Thread>();
		scrapers.add(new Thread(new AutoTraderScraper()));
		scrapers.add(new Thread(new CarsScraper()));
		scrapers.add(new Thread(new AutoMartScraper()));
		
		for (Thread scraper : scrapers)
		{
			System.out.println("Start Scraper");
			scraper.start();
		}
		
		boolean finished = false;
		
		while(!finished)
		{
			finished = true;
			for (Thread scraper : scrapers)
			{
				finished = finished && !scraper.isAlive();
			}
			Thread.sleep(2000);
			
			System.out.println("Finished " + finished);
		}
		
		LOG.info("[VPScraper Finished]");
	}
}
