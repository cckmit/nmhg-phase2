package tavant.twms.domain.catalog;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "MISC_ITEM_CONFIG")
public class MiscellaneousItemConfiguration {

	@Id
    @GeneratedValue(generator = "MiscellaneousItemConfiguration")
	@GenericGenerator(name = "MiscellaneousItemConfiguration", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "MISC_ITEM_CONFIG_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private MiscellaneousItem miscellaneousItem;

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "MISC_ITEM_CONFIG", nullable = false)
	private List<MiscItemRate> miscItemRates = new ArrayList<MiscItemRate>();

	private Long tresholdQuantity;

	@NotNull
	@Type(type = "org.hibernate.type.EnumType", parameters = {
			@Parameter(name = "enumClass", value = "tavant.twms.domain.catalog.ItemUOMTypes"),
			@Parameter(name = "type", value = "" + Types.VARCHAR) })
	private ItemUOMTypes uom;

	private boolean active = Boolean.TRUE.booleanValue();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTresholdQuantity() {
		return tresholdQuantity;
	}

	public void setTresholdQuantity(Long tresholdQuantity) {
		this.tresholdQuantity = tresholdQuantity;
	}

	public ItemUOMTypes getUom() {
		return uom;
	}

	public void setUom(ItemUOMTypes uom) {
		this.uom = uom;
	}

	public MiscellaneousItem getMiscellaneousItem() {
		return miscellaneousItem;
	}

	public void setMiscellaneousItem(MiscellaneousItem miscellaneousItem) {
		this.miscellaneousItem = miscellaneousItem;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<MiscItemRate> getMiscItemRates() {
		return miscItemRates;
	}

	public void setMiscItemRates(List<MiscItemRate> miscItemRates) {
		this.miscItemRates = miscItemRates;
	}

	public MiscItemRate getMiscItemRateForCurrency(Currency currency){
		MiscItemRate rate = null;
		if(getMiscItemRates() != null && !getMiscItemRates().isEmpty()){
			for(MiscItemRate miscItemRate : getMiscItemRates()){
				if(miscItemRate.getRate().breachEncapsulationOfCurrency().equals(currency)){
					return miscItemRate;
				}
			}
		}
		return rate;
	}
}
