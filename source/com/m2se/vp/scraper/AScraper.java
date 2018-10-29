
package com.m2se.vp.scraper;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class AScraper implements Runnable
{
	protected static Logger LOG = Logger.getRootLogger();
	
	protected long documentSize = 0;
	protected long entitiesCount = 0;
	
	protected abstract String getElementSelector();
	
	protected abstract HashSet<String> getDuplicateCheckKeys();
	
	protected abstract AEntity getEntity(Element element);
	
	protected abstract void save(List<AEntity> entities);
	
	protected abstract String getNextURL();
	
	protected abstract String getName();
	
	@Override
	public void run()
	{
		LOG.info("Run Scraper - " + getName());
		scrape();
	}
	
	public boolean filterEntity(Element element)
	{
		return false;
	}
	
	protected Document getDocument(String url)
	{
		try
		{
			Document document =
				Jsoup
					.connect(url)
					.header("Accept-Encoding", "gzip, deflate")
					.userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
					.maxBodySize(0)
					.timeout(600000)
					.get();
			
			documentSize += document.toString().length();
			
			return document;
		}
		catch (IOException e)
		{
			LOG.error("Error loading document " + url, e);
			return null;
		}
	}
	
	public void scrape()
	{
		
		LinkedList<AEntity> entries = new LinkedList<AEntity>();
		
		HashSet<String> duplicateList = getDuplicateCheckKeys();
		
		String url = getNextURL();
		
		boolean eof = false;
		
		while (!eof && url != null)
		{
			LOG.info(getName() + "-Scrap Count-" + entitiesCount + "-URL " + url);
			Document document = getDocument(url);
			
			if (document != null)
			{
				Elements elements = document.select(getElementSelector());
				
				for (int i = 0; i < elements.size(); i++)
				{
					Element element = elements.get(i);
					
					if (!filterEntity(element))
					{
						AEntity entity = getEntity(element);
						
						if (!duplicateList.contains(entity.getKey()))
						{
							entitiesCount++;
							entries.addFirst(entity);
						}
						else
						{
							// duplicate we can stop searching
							eof = true;
							LOG.info(getName() + "-Duplicate " + entity.getKey());
							break;
						}
					}
				}
			}
			url = getNextURL();
			
			if (entries.size() > 200)
			{
				save(entries);
				entries = new LinkedList<AEntity>();
				
				eof = true;
			}
			
		}
		save(entries);
		
		LOG.info(getName() + "-Total Vehicles - " + entitiesCount);
		LOG.info(getName() + "-Total Download Size - " + (documentSize / 1000000.0));
	}
}
