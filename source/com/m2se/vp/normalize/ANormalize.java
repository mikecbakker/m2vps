package com.m2se.vp.normalize;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.m2se.vp.database.Database;

/**
 * TODO
 *
 */
public abstract class ANormalize
{
	protected static Logger LOG = Logger.getRootLogger();
	protected abstract String getSql(long startEntityId);
	protected abstract String getEntityName();
	protected abstract long normalizeRowData(ResultSet rs);

	
	public void normalize()
	{
		/* For each entity, look at what our last normalized id is, and then normalize all new data */
		long last_entity_id = getLastEntityId(getEntityName());
		
		/* Clear db of any id's greater than id for this entity. Keep things clean */
		
		/* Save new entries for that entity */
		processNewRows(last_entity_id);
		
		/* Update session info */
	}
	
	protected void processNewRows(long startEntityId)
	{
		Connection connection = null;
		PreparedStatement statement = null;
		try
		{
			connection = Database.getConnection();
			statement = connection.prepareStatement(getSql(startEntityId));
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				long lastIdNormalized = normalizeRowData(rs);
				saveLastEntityIdProcessed(lastIdNormalized);
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
	}
	
	protected static long getLastEntityId(String entity)
	{
		Connection connection = null;
		PreparedStatement statement = null;
		long lastEntityId = 0;
		try
		{
			connection = Database.getConnection();
			statement = connection.prepareStatement(
				"SELECT last_entity_id " +
				"FROM normalize_session_info " +
				"WHERE scraper_entity = " + entity);
			
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				lastEntityId = rs.getLong(1);
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
		return lastEntityId;
	}
	
	protected static void saveLastEntityIdProcessed(long id)
	{
		//TODO update the session tables
	}
}

