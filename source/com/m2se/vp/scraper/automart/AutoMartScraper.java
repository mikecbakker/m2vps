
package com.m2se.vp.scraper.automart;

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

public class AutoMartScraper extends AScraper
{
	int page = 0;
	
	@Override
	protected String getElementSelector()
	{
		return "tr.Item.Dealer";
	}
	
	@Override
	protected String getNextURL()
	{
		String url ="http://www.automart.co.za/cars/search-results/offset/" + (page*20);
		page++;
		return url;
	}
	
	@Override
	protected AEntity getEntity(Element element)
	{
		return new AutoMartEntity(element);
	}
	
	@Override
	protected String getName()
	{
		return "automart.co.za";
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
			statement = connection.prepareStatement("SELECT web_link FROM scraper_automart_data");
			ResultSet rs = statement.executeQuery();
			
			while (rs.next())
			{
				webLinks.add(rs.getString(1));
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
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
			
			statement = connection.prepareStatement("INSERT INTO scraper_automart_data (html, web_link) VALUES (?,?)");
			
			for (AEntity entity : entities)
			{
				AutoMartEntity autoMartEntity = (AutoMartEntity)entity;
				statement.setString(1, autoMartEntity.getHtml());
				statement.setString(2, autoMartEntity.getWebLink());
				statement.addBatch();
			}
			
			statement.executeBatch();
			statement.close();
		}
		catch (Exception e)
		{
			System.out.println(e);
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
	
	public static void main(String[] args)
	{
		AutoMartScraper carsScrapper = new AutoMartScraper();
		carsScrapper.scrape();
	}
}
