
package com.m2se.vp.scraper.cars;

import org.jsoup.nodes.Element;

import com.m2se.vp.scraper.AEntity;

public class CarsEntity extends AEntity
{
	
	private String html;
	private String webLink;
	
	public CarsEntity(Element element)
	{
		this.html = element.toString();
		this.webLink = "http://www.cars.co.za" + element.select("div.left_block").select("a").attr("href");
	}
	
	public String getHtml()
	{
		return html;
	}
	
	public void setHtml(String html)
	{
		this.html = html;
	}
	
	public String getWebLink()
	{
		return webLink;
	}
	
	public void setWebLink(String webLink)
	{
		this.webLink = webLink;
	}

	@Override
	public String getKey()
	{
		return webLink;
	}
	
}
