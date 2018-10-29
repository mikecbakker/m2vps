
package m2vps.scraper.fw;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Defines a job posting
 * 
 */
@Entity
@Table(name = "vehicle_data")
public class VehicleData
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@Column(name = "source")
	private String source;
	@Column(name = "manufacturer")
	private String manufacturer;
	@Column(name = "model")
	private String model;
	@Column(name = "mileage")
	private String mileage;
	@Column(name = "price")
	private double price;
	@Column(name = "year")
	private String year;
	@Column(name = "weblink", columnDefinition="TEXT")
	private String weblink;
	@Column(name = "web_result_page", columnDefinition="TEXT")
	private String webResultPage;
	@Column(name = "province")
	private String province;
	@Column(name = "ranking_system")
	private double rankingSystem;
	@Column(name = "ranking_model")
	private double rankingModel;
	@Column(name = "ranking_milage")
	private double rankingMilage;
	@Column(name = "ranking_price")
	private double rankingPrice;
	@Column(name = "deviation")
	private double deviation;
	/* System information */
	@Column(name = "created")
	private Date created;
	@Column(name = "last_updated", nullable = true)
	private Date lastUpdated;
	
	@Column(name = "expired")
	private boolean expired;
	@Column(name = "duplicate")
	private boolean duplicate;
	@Column(name = "outlier")
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
	
	public double getRankingSystem()
	{
		return rankingSystem;
	}
	
	public void setRankingSystem(double rankingSystem)
	{
		this.rankingSystem = rankingSystem;
	}
	
	public double getRankingModel()
	{
		return rankingModel;
	}
	
	public void setRankingModel(double rankingModel)
	{
		this.rankingModel = rankingModel;
	}
	
	public double getRankingMilage()
	{
		return rankingMilage;
	}
	
	public void setRankingMilage(double rankingMilage)
	{
		this.rankingMilage = rankingMilage;
	}
	
	public double getRankingPrice()
	{
		return rankingPrice;
	}
	
	public void setRankingPrice(double rankingPrice)
	{
		this.rankingPrice = rankingPrice;
	}
	
	public double getDeviation()
	{
		return deviation;
	}
	
	public void setDeviation(double deviation)
	{
		this.deviation = deviation;
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
