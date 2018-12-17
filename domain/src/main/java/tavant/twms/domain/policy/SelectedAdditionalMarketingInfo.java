package tavant.twms.domain.policy;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Table(name = "SEL_ADDTL_MKTING_INFO")
@Filters({
  @Filter(name="excludeInactive")
})
public class SelectedAdditionalMarketingInfo implements AuditableColumns{
    @Id
    @GeneratedValue(generator = "SelectedAdditionalMarketingInfo")
	@GenericGenerator(name = "SelectedAdditionalMarketingInfo", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SEL_ADDTL_MARKETING_INFO_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private AdditionalMarketingInfo addtlMarketingInfo; 

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Warranty forWarranty;

    private String value;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

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

	public AdditionalMarketingInfo getAddtlMarketingInfo() {
		return addtlMarketingInfo;
	}

	public void setAddtlMarketingInfo(AdditionalMarketingInfo addtlMarketingInfo) {
		this.addtlMarketingInfo = addtlMarketingInfo;
	}

	public Warranty getForWarranty() {
		return forWarranty;
	}

	public void setForWarranty(Warranty forWarranty) {
		this.forWarranty = forWarranty;
	}

    public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

}
