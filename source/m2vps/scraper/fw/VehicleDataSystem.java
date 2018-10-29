
package m2vps.scraper.fw;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Defines a job posting
 * 
 */
@Entity
@Table(name = "vehicle_data_system",indexes={@Index(name = "ix_band", columnList="band", unique=false)} )
public class VehicleDataSystem
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
	@Column(name = "ranking")
	private double ranking;
	@Column(name = "band")
	private String band;
	@Column(name="vehicle_data_id")
	private long vehicleData;
	
	public VehicleDataSystem()
	{

	}
	
	public VehicleDataSystem(VehicleData vehicleData,long band)
	{
		source = vehicleData.getSource();
		manufacturer = vehicleData.getManufacturer();
		model = vehicleData.getModel();
		mileage = vehicleData.getMileage();
		price = vehicleData.getPrice();
		year = vehicleData.getYear();
		weblink = vehicleData.getWeblink();
		webResultPage = vehicleData.getWebResultPage();
		province = vehicleData.getProvince();
		ranking = vehicleData.getRankingModel();
		this.band = band+"";
		this.vehicleData = vehicleData.getId(); 
	}	
	public long getId()
	{
		return id;
	}
	public void setId(long id)
	{
		this.id = id;
	}
	public String getSource()
	{
		return source;
	}
	public void setSource(String source)
	{
		this.source = source;
	}
	public String getManufacturer()
	{
		return manufacturer;
	}
	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}
	public String getModel()
	{
		return model;
	}
	public void setModel(String model)
	{
		this.model = model;
	}
	public String getMileage()
	{
		return mileage;
	}
	public void setMileage(String mileage)
	{
		this.mileage = mileage;
	}
	public double getPrice()
	{
		return price;
	}
	public void setPrice(double price)
	{
		this.price = price;
	}
	public String getYear()
	{
		return year;
	}
	public void setYear(String year)
	{
		this.year = year;
	}
	public String getWeblink()
	{
		return weblink;
	}
	public void setWeblink(String weblink)
	{
		this.weblink = weblink;
	}
	public String getWebResultPage()
	{
		return webResultPage;
	}
	public void setWebResultPage(String webResultPage)
	{
		this.webResultPage = webResultPage;
	}
	public String getProvince()
	{
		return province;
	}
	public void setProvince(String province)
	{
		this.province = province;
	}
	public double getRanking()
	{
		return ranking;
	}
	public void setRanking(double ranking)
	{
		this.ranking = ranking;
	}
	public long getVehicleData()
	{
		return vehicleData;
	}
	public void setVehicleData(long vehicleData)
	{
		this.vehicleData = vehicleData;
	}

	public String getBand()
	{
		return band;
	}

	public void setBand(String band)
	{
		this.band = band;
	}
	
	

	
}
