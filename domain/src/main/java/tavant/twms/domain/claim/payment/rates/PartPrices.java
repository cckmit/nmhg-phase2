package tavant.twms.domain.claim.payment.rates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import javax.persistence.UniqueConstraint;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;
import org.hibernate.annotations.ForeignKey;

@Entity
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@Filters( { @Filter(name = "excludeInactive") })
public class PartPrices  implements BusinessUnitAware, AuditableColumns {

	@Id
	@GeneratedValue(generator = "PartPrices")
	@GenericGenerator(name = "PartPrices", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PART_PRICES_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	private String status;
	
	 @OneToMany(mappedBy = "partPrices", fetch = FetchType.LAZY)
		@Cascade( { org.hibernate.annotations.CascadeType.ALL,
				org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
		@Column(nullable = false)	
		@Filter(name="excludeInactive")
		private List<PartPrice> rates = new ArrayList<PartPrice>();

	public List<PartPrice> getRates() {
		return rates;
	}

	public void setRates(List<PartPrice> rates) {
		this.rates = rates;
	}

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
     
	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	@Column(nullable = true, length = 4000)
	private String comments;

	 @ManyToOne(optional = false, fetch = FetchType.LAZY)
	 @ForeignKey(name="PARTNUMBER_PARTNUMBER_FK")
	 private Item nmhg_part_number;
	
	@OneToMany(fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@IndexColumn(name = "list_index", nullable = false)
	@JoinColumn(name = "PART_PRICES")
	private List<PartPriceAudit> partPriceAudits = new ArrayList<PartPriceAudit>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

		public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public List<PartPriceAudit> getPartPriceAudits() {
		if (this.partPriceAudits != null && this.partPriceAudits.size() > 0) {
			Collections.sort(this.partPriceAudits, Collections.reverseOrder());
		}
		return partPriceAudits;
	}

	public void setPartPriceAudits(List<PartPriceAudit> partPriceAudits) {
		this.partPriceAudits = partPriceAudits;
	}

	public Item getNmhg_part_number() {
		return nmhg_part_number;
	}

	public void setNmhg_part_number(Item nmhg_part_number) {
		this.nmhg_part_number = nmhg_part_number;
	}


}
