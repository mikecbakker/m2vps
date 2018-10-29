
package m2vps.scraper.fw;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Defines a data related to a vehicle year model
 * 
 */
@Entity
@Table(name = "vehicle_year_model_data")
public class VehicleYearModelData
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;
	
	@Column(name = "manufacturer")
	private String manufacturer;
	@Column(name = "model")
	private String model;
	@Column(name = "year")
	private String year;
	
	@Column(name = "avg_mileage")
	private long avgMileage;
	@Column(name = "avg_price")
	private double avgPrice;
	@Column(name = "number_vehicles")
	private long numberVehicle;
	
	/* System information */
	@Column(name = "created")
	private Date created;
	
	public long getId()
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
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
	
	public String getYear()
	{
		return year;
	}
	
	public void setYear(String year)
	{
		this.year = year;
	}
	
	public long getAvgMileage()
	{
		return avgMileage;
	}
	
	public void setAvgMileage(long avgMileage)
	{
		this.avgMileage = avgMileage;
	}
	
	public double getAvgPrice()
	{
		return avgPrice;
	}
	
	public void setAvgPrice(double avgPrice)
	{
		this.avgPrice = avgPrice;
	}
	
	public long getNumberVehicle()
	{
		return numberVehicle;
	}
	
	public void setNumberVehicle(long numberVehicle)
	{
		this.numberVehicle = numberVehicle;
	}
	
	public Date getCreated()
	{
		return created;
	}
	
	public void setCreated(Date created)
	{
		this.created = created;
	}
}
