package tavant.twms.domain.stateMandates;

import java.math.BigDecimal;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import com.domainlanguage.time.CalendarDate;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.LaborRateType;
import tavant.twms.security.AuditableColumns;

@Entity
@Table(name = "State_Mandates")
@Filters({ @Filter(name = "excludeInactive") })
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class StateMandates implements BusinessUnitAware, AuditableColumns {
	
	public StateMandates(){
		
	}

	@Id
	@Column(name= "ID")
	@GeneratedValue(generator = "StateMandates")
	@GenericGenerator(name = "StateMandates", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "STATE_MANDATES_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	@Column(nullable = true)
	private CalendarDate effectiveDate;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@Cascade({ CascadeType.ALL })
	private LaborRateType laborRateType;
	
	@OneToMany(mappedBy="stateMandates",fetch = FetchType.LAZY)
	@Cascade({ CascadeType.ALL })
	private List<StateMndteCostCtgyMapping> stateMandateCostCatgs = new ArrayList<StateMndteCostCtgyMapping>();
	
	@OneToMany(fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({
			org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "STATE_MANDATE")
	@IndexColumn(name = "list_index", nullable = false)
	private List<StateMandateAudit> stateMandateAudit = new ArrayList<StateMandateAudit>();

	@Version
	private int version;

	private String status;
	
	@Column(name="STATE")
	private String state;
	
	@Column(name="OEM_PARTS_PERCENT")
	private BigDecimal oemPartsPercent;
	
	public BigDecimal getOemPartsPercent() {
		return oemPartsPercent;
	}

	public void setOemPartsPercent(BigDecimal oemPartsPercent) {
		this.oemPartsPercent = oemPartsPercent;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CalendarDate getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(CalendarDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public LaborRateType getLaborRateType() {
		return laborRateType;
	}

	public void setLaborRateType(LaborRateType laborRateType) {
		this.laborRateType = laborRateType;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public List<StateMndteCostCtgyMapping> getStateMandateCostCatgs() {
		return stateMandateCostCatgs;
	}

	public void setStateMandateCostCatgs(
			List<StateMndteCostCtgyMapping> stateMandateCostCatgs) {
		this.stateMandateCostCatgs = stateMandateCostCatgs;
	}

	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;

	}
	
	public List<StateMandateAudit> getStateMandateAudit() {
		if (this.stateMandateAudit != null && this.stateMandateAudit.size() > 0) {
            Collections.sort(this.stateMandateAudit,Collections.reverseOrder());
        }
		return stateMandateAudit;
	}

	public void setStateMandateAudit(List<StateMandateAudit> stateMandateAudit) {
		this.stateMandateAudit = stateMandateAudit;
	}

	public boolean isStateMandateApplyForCostCategory(String costCategoryName)
	{
		List<StateMndteCostCtgyMapping> stateMndteCostCtgyMappings=this.stateMandateCostCatgs;
		for(StateMndteCostCtgyMapping stateMndteCostCtgyMapping:stateMndteCostCtgyMappings)
		{
			if(stateMndteCostCtgyMapping.getCostCategory()!=null)
			{
			if(stateMndteCostCtgyMapping.getCostCategory().getName().equals(costCategoryName)&&stateMndteCostCtgyMapping.getMandatory().equals(true))
				return true;
			}
			else if(stateMndteCostCtgyMapping.getOthers().equals(costCategoryName)&&stateMndteCostCtgyMapping.getMandatory().equals(true))
			{
				return true;
			}
		}
		return false;
	}

}
