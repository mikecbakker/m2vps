
package com.m2se.vp.scraper.autotrader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

import org.jsoup.nodes.Element;

import com.m2se.vp.database.Database;
import com.m2se.vp.scraper.AEntity;
import com.m2se.vp.scraper.AScraper;

public class AutoTraderScraper extends AScraper
{
	protected static final String[] PROVINCES = {"gauteng", "western-cape", "free-state", "kwazulu-natal", "mpumalanga"};
	
	int page = 0;
	String province = null;
	
	@Override
	public boolean filterEntity(Element element)
	{
		String html = element.html();
		
		return html.contains("listingStandoutBorder") || html.contains("featureRes");
	}
	
	@Override
	protected String getElementSelector()
	{
		return "div.searchResult";
	}
	
	@Override
	public void scrape()
	{
		for (String province : PROVINCES)
		{
			LOG.info(getName() + "-" + province);
			page = 0;
			this.province = province;
			super.scrape();
		}
	}
	
	@Override
	protected String getNextURL()
	{
		page++;
		String url = "http://www.autotrader.co.za/seoregion/" + province + "/search?sort=MostRecent&pageNumber=" + page;
		return url;
	}
	
	@Override
	protected AEntity getEntity(Element element)
	{
		return new AutoTraderEntity(element,province);
	}
	
	@Override
	protected String getName()
	{
		return "autotrader.co.za";
	}
	
	@Override
	protected HashSet<String> getDuplicateCheckKeys()
	{
		HashSet<String> webLinks = new HashSet<String>();
		
		Connection connection = null;
		PreparedStatement statement = null;
		try
		{
			connection = Database.getConnection();
			statement = connection.prepareStatement("SELECT web_link FROM scraper_autotrader_data");
			ResultSet rs = statement.executeQuery();
			
			while (rs.next())
			{
				webLinks.add(rs.getString(1));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LOG.error(e);
		}
		finally
		{
			if (statement != null)
				try
				{
					statement.close();
				}
				catch (SQLException e)
				{
					LOG.error(e);
				}
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (SQLException e)
				{
					LOG.error(e);
				}
			}
		}
		
		return webLinks;
	}
	
	@Override
	protected void save(List<AEntity> entities)
	{
		Connection connection = null;
		PreparedStatement statement = null;
		try
		{
			connection = Database.getConnection();
			
			statement = connection.prepareStatement("INSERT INTO scraper_autotrader_data (html, web_link,province) VALUES (?,?,?)");
			
			for (AEntity entity : entities)
			{
				AutoTraderEntity autoTrader = (AutoTraderEntity)entity;
				statement.setString(1, autoTrader.getHtml());
				statement.setString(2, autoTrader.getWebLink());
				statement.setString(3, autoTrader.getProvince());
				statement.addBatch();
			}
			
			statement.executeBatch();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			LOG.error(e);
		}
		finally
		{
			if (statement != null)
				try
				{
					statement.close();
				}
				catch (SQLException e)
				{
					LOG.error(e);
				}
			if (connection != null)
			{
				try
				{
					connection.close();
				}
				catch (SQLException e)
				{
					LOG.error(e);
				}
			}
		}
		
	}
}
