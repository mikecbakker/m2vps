
package com.m2se.vp.scraper.automart;

import org.jsoup.nodes.Element;

import com.m2se.vp.scraper.AEntity;

public class AutoMartEntity extends AEntity
{
	
	private String html;
	private String webLink;
	
	public AutoMartEntity(Element element)
	{
		this.html = element.toString();
		this.webLink = element.select("td.makemodel").select("a").attr("href");
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
