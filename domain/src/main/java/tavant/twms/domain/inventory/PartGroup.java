package tavant.twms.domain.inventory;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class PartGroup {

	@Id
	@GeneratedValue(generator = "PartGroup")
	@GenericGenerator(name = "PartGroup", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PART_GROUP_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20")})
	private Long id;

	private String partGroupDescription;
	private BigDecimal qty;
	private BigDecimal standardCost;
	private String partGroupCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getPartGroupDescription() {
		return partGroupDescription;
	}

	public void setPartGroupDescription(String partGroupDescription) {
		this.partGroupDescription = partGroupDescription;
	}

	public BigDecimal getQty() {
		return qty;
	}

	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}

	public BigDecimal getStandardCost() {
		return standardCost;
	}

	public void setStandardCost(BigDecimal standardCost) {
		this.standardCost = standardCost;
	}
	public String getPartGroupCode() {
		return partGroupCode;
	}

	public void setPartGroupCode(String partGroupCode) {
		this.partGroupCode = partGroupCode;
	}

}
