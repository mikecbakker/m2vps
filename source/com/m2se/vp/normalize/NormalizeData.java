
package com.m2se.vp.normalize;

import java.util.Date;

/**
 * Defines a Normalized Vehicle data row
 * 
 */
public class NormalizeData
{
	private long id;

	private String source;
	private String manufacturer;
	private String model;
	private String sub_model;
	private String mileage;
	private double price;
	private String year;
	private String weblink;
	private String webResultPage;
	private String province;
	private Date created;
	private Date lastUpdated;
	private boolean expired;
	private boolean duplicate;
	private boolean outlier;
	
	public long getId()
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	/**
	 * Returns the source.
	 *
	 * @return the source.
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * Sets the source.
	 *
	 * @param source the source to set
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * Returns the manufacturer.
	 *
	 * @return the manufacturer.
	 */
	public String getManufacturer()
	{
		return manufacturer;
	}
	
	/**
	 * Sets the manufacturer.
	 *
	 * @param manufacturer
	 *           the manufacturer to set
	 */
	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}
	
	/**
	 * Returns the model.
	 *
	 * @return the model.
	 */
	public String getModel()
	{
		return model;
	}
	
	/**
	 * Sets the model.
	 *
	 * @param model
	 *           the model to set
	 */
	public void setModel(String model)
	{
		this.model = model;
	}
	
	/**
	 * Returns the sub_model.
	 *
	 * @return the sub_model.
	 */
	public String getSub_model()
	{
		return sub_model;
	}

	/**
	 * Sets the sub_model.
	 *
	 * @param sub_model the sub_model to set
	 */
	public void setSub_model(String sub_model)
	{
		this.sub_model = sub_model;
	}

	/**
	 * Returns the mileage.
	 *
	 * @return the mileage.
	 */
	public String getMileage()
	{
		return mileage;
	}
	
	/**
	 * Sets the mileage.
	 *
	 * @param mileage
	 *           the mileage to set
	 */
	public void setMileage(String mileage)
	{
		this.mileage = mileage;
	}
	
	/**
	 * Returns the price.
	 *
	 * @return the price.
	 */
	public double getPrice()
	{
		return price;
	}
	
	/**
	 * Sets the price.
	 *
	 * @param price
	 *           the price to set
	 */
	public void setPrice(double price)
	{
		this.price = price;
	}
	
	/**
	 * Returns the year.
	 *
	 * @return the year.
	 */
	public String getYear()
	{
		return year;
	}
	
	/**
	 * Sets the year.
	 *
	 * @param year
	 *           the year to set
	 */
	public void setYear(String year)
	{
		this.year = year;
	}
	
	/**
	 * Returns the weblink.
	 *
	 * @return the weblink.
	 */
	public String getWeblink()
	{
		return weblink;
	}
	
	/**
	 * Sets the weblink.
	 *
	 * @param weblink
	 *           the weblink to set
	 */
	public void setWeblink(String weblink)
	{
		this.weblink = weblink;
	}
	
	/**
	 * Returns the webResultPage.
	 *
	 * @return the webResultPage.
	 */
	public String getWebResultPage()
	{
		return webResultPage;
	}
	
	/**
	 * Sets the webResultPage.
	 *
	 * @param webResultPage
	 *           the webResultPage to set
	 */
	public void setWebResultPage(String webResultPage)
	{
		this.webResultPage = webResultPage;
	}
	
	/**
	 * Returns the created.
	 *
	 * @return the created.
	 */
	public Date getCreated()
	{
		return created;
	}
	
	/**
	 * Sets the created.
	 *
	 * @param created
	 *           the created to set
	 */
	public void setCreated(Date created)
	{
		this.created = created;
	}
	
	/**
	 * Returns the lastUpdated.
	 *
	 * @return the lastUpdated.
	 */
	public Date getLastUpdated()
	{
		return lastUpdated;
	}
	
	/**
	 * Sets the lastUpdated.
	 *
	 * @param lastUpdated
	 *           the lastUpdated to set
	 */
	public void setLastUpdated(Date lastUpdated)
	{
		this.lastUpdated = lastUpdated;
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
	public String toString()
	{
		return manufacturer + "=" + model + "=Price=" + price + "=Mileage=" + mileage + "=Year=" + year +  "=" + weblink;
	}

	public boolean isExpired()
	{
		return expired;
	}

	public void setExpired(boolean expired)
	{
		this.expired = expired;
	}

	public boolean isDuplicate()
	{
		return duplicate;
	}

	public void setDuplicate(boolean duplicate)
	{
		this.duplicate = duplicate;
	}

	public boolean isOutlier()
	{
		return outlier;
	}

	public void setOutlier(boolean outlier)
	{
		this.outlier = outlier;
	}
}
