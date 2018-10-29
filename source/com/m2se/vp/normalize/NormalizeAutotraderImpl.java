
package com.m2se.vp.normalize;

import java.sql.ResultSet;

import org.apache.log4j.Logger;

public class NormalizeAutotraderImpl extends ANormalize
{
	protected static Logger LOG = Logger.getRootLogger();
	
	@Override
	public String getEntityName()
	{
		return "autotrader";
	}
	
	@Override
	public String getSql(long startEntityId)
	{
		return 
			"SELECT id, created, html, province, weblink " +
			"FROM scraper_autotrader_data " +
			"WHERE id > " + startEntityId;
	}
	

	@Override
	protected long normalizeRowData(ResultSet rs)
	{
		NormalizeData normalizeData = new NormalizeData();
		
		/* Perform mapping */
		
		/* Save to db */
		
		/* Select its id */
		return 0;
	}
}

