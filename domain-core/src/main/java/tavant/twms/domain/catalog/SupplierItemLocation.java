package tavant.twms.domain.catalog;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.supplier.ItemMapping;

import com.domainlanguage.time.CalendarDate;

@Entity
public class SupplierItemLocation {
	@Id
	@GeneratedValue(generator = "SupplierItemLocation")
	@GenericGenerator(name = "SupplierItemLocation", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SUPPLIER_ITEM_LOCATION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	private String locationCode;
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate fromDate;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate toDate;
    
    private boolean status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_mapping")
    @Cascade( { org.hibernate.annotations.CascadeType.ALL,org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private ItemMapping itemMapping;

	public ItemMapping getItemMapping() {
		return itemMapping;
	}

	public void setItemMapping(ItemMapping itemMapping) {
		this.itemMapping = itemMapping;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

    public CalendarDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(CalendarDate fromDate) {
        this.fromDate = fromDate;
    }

    public CalendarDate getToDate() {
        return toDate;
    }

    public void setToDate(CalendarDate toDate) {
        this.toDate = toDate;
    }

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean st) {
		status = st;
	}

}
