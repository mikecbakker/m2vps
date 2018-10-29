
package com.m2se.vp.scraper.cars;

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

public class CarsScraper extends AScraper
{
	int page = 0;
	
	@Override
	protected String getElementSelector()
	{
		return "div.item";
	}
	
	@Override
	protected String getNextURL()
	{
		page++;
		
		String url =
			"http://www.cars.co.za/searchVehicle.php?new_or_used=&make_model=Abarth%5BAll%5D%7CAlfa+Romeo%5BAll%5D%7CAston+Martin%5BAll%5D%7CAudi%5BAll%5D%7CBMW%5BAll%5D%7CBentley%5BAll%5D%7CC.A.M%5BAll%5D%7CCadillac%5BAll%5D%7CChana%5BAll%5D%7CChery%5BAll%5D%7CChevrolet%5BAll%5D%7CChrysler%5BAll%5D%7CCitroen%5BAll%5D%7CDaewoo%5BAll%5D%7CDaihatsu%5BAll%5D%7CDatsun%5BAll%5D%7CDfsk%5BAll%5D%7CDodge%5BAll%5D%7CFAW%5BAll%5D%7CFerrari%5BAll%5D%7CFiat%5BAll%5D%7CFord%5BAll%5D%7CFoton%5BAll%5D%7CGWM%5BAll%5D%7CGeely%5BAll%5D%7CGonow%5BAll%5D%7CHino%5BAll%5D%7CHonda%5BAll%5D%7CHummer%5BAll%5D%7CHyundai%5BAll%5D%7CInfiniti%5BAll%5D%7CIsuzu%5BAll%5D%7CIveco%5BAll%5D%7CJMC%5BAll%5D%7CJaguar%5BAll%5D%7CJeep%5BAll%5D%7CJinbei%5BAll%5D%7CKia%5BAll%5D%7CLada%5BAll%5D%7CLamborghini%5BAll%5D%7CLand+Rover%5BAll%5D%7CLexus%5BAll%5D%7CMG%5BAll%5D%7CMINI%5BAll%5D%7CMahindra%5BAll%5D%7CMaserati%5BAll%5D%7CMazda%5BAll%5D%7CMclaren%5BAll%5D%7CMercedes-Benz%5BAll%5D%7CMitsubishi%5BAll%5D%7CNissan%5BAll%5D%7COpel%5BAll%5D%7CPeugeot%5BAll%5D%7CPorsche%5BAll%5D%7CProton%5BAll%5D%7CRenault%5BAll%5D%7CRolls-royce%5BAll%5D%7CRover%5BAll%5D%7CSEAT%5BAll%5D%7CSaab%5BAll%5D%7CSmart%5BAll%5D%7CSoyat%5BAll%5D%7CSsangYong%5BAll%5D%7CSubaru%5BAll%5D%7CSuzuki%5BAll%5D%7CTATA%5BAll%5D%7CToyota%5BAll%5D%7CVolkswagen%5BAll%5D%7CVolvo%5BAll%5D%7CZotye%5BAll%5D&vfs_area=&agent_locality=&price_range=&os=&body_type_exact=&transmission=&fuel_type=&login_type=&mapped_colour=&vfs_year=&vfs_mileage=&vehicle_axle_config=&keyword=&sort=vfs_time_d&P=" +
				page + "#.VQMJj52Ueao";
		
		return url;
	}
	
	@Override
	protected AEntity getEntity(Element element)
	{
		return new CarsEntity(element);
	}
	
	@Override
	protected String getName()
	{
		return "cars.co.za";
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
			statement = connection.prepareStatement("SELECT web_link FROM scraper_cars_data");
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
			
			statement = connection.prepareStatement("INSERT INTO scraper_cars_data (html, web_link) VALUES (?,?)");
			
			for (AEntity entity : entities)
			{
				CarsEntity carsEntity = (CarsEntity)entity;
				statement.setString(1, carsEntity.getHtml());
				statement.setString(2, carsEntity.getWebLink());
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
		CarsScraper carsScrapper = new CarsScraper();
		carsScrapper.scrape();
	}
}
