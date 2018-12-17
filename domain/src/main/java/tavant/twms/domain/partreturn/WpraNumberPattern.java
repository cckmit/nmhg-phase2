package tavant.twms.domain.partreturn;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;

import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class WpraNumberPattern implements AuditableColumns,BusinessUnitAware{

	@Id
	@GeneratedValue(generator = "WpraNumberPattern")
	@GenericGenerator(name = "WpraNumberPattern", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
         @Parameter(name = "sequence_name", value = "SEQ_WpraNumberPattern"),
         @Parameter(name = "initial_value", value = "1"),
         @Parameter(name = "increment_size", value = "20") })
 	private Long id;

	@Column
	private Long numberingPattern ;

	@Column
	private boolean isActive;

	@Column
	private String template;

	@Column
	private String patternType;
	
	private String sequenceName;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getNumberingPattern() {
		return this.numberingPattern;
	}
	public void setNumberingPattern(Long numberingPattern) {
		this.numberingPattern = numberingPattern;
	}
	public boolean isActive() {
		return this.isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public String getTemplate() {
		return this.template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public AuditableColEntity getD() {
		return d;
	}
	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public String getPatternType() {
		return patternType;
	}
	public void setPatternType(String patternType) {
		this.patternType = patternType;
	}

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}
	public String getSequenceName() {
		return sequenceName;
	}
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	
}
