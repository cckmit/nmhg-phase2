package tavant.twms.domain.catalog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.common.Views;

import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class BrandItem {

	   @Id
	    @GeneratedValue(generator = "BrandItem")
	    @GenericGenerator(name = "BrandItem", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
	            @Parameter(name = "sequence_name", value = "BRAND_ITEM_SEQ"),
	            @Parameter(name = "initial_value", value = "1000"),
	            @Parameter(name = "increment_size", value = "20")})
	    private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonView(Views.Public.class)
	private Item item;
	private String brand;
	private String itemNumber;

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}



}
