
package com.m2se.vp.scraper.autotrader;

import org.jsoup.nodes.Element;

import com.m2se.vp.scraper.AEntity;

public class AutoTraderEntity extends AEntity
{
	
	private String html;
	private String webLink;
	private String province;
	
	public AutoTraderEntity(Element element,String province)
	{
		this.province = province;
		this.html = element.toString();
		this.webLink = element.select("div.serpHeader").select("a").attr("href");
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
	
	public String getProvince()
	{
		return province;
	}

	public void setProvince(String province)
	{
		this.province = province;
	}

	@Override
	public String getKey()
	{
		return webLink;
	}
	
}
