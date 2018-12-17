package tavant.twms.domain.policy;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;

@Entity
@Table(name = "REQUEST_WNTY_CVG")
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class WarrantyCoverageRequest implements BusinessUnitAware{

	private static final MessageFormat COMMENTS_LOG_FORMAT = new java.text.MessageFormat("{0} Comments :  {1}"); 
	
	@Id
    @GeneratedValue(generator = "Warranty")
	@GenericGenerator(name = "Warranty", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "REQUEST_WNTY_CVG_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private InventoryItem inventoryItem;
	
	
	private String status;
	
    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@Sort(type = SortType.COMPARATOR, comparator = WarrantyCoverageRequestAuditDescendingComparator.class)
	@JoinColumn(name = "REQUEST_WNTY_CVG", nullable = false)
    private SortedSet<WarrantyCoverageRequestAudit> audits = new java.util.TreeSet<WarrantyCoverageRequestAudit>();
    
	@Transient	
	private List<PolicyDefinition> policiesWithReducedCoverage;
	
	@Transient
	private Integer noOfMonthsInReduction;
	
	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private ServiceProvider requestedBy;
    
    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate updatedOnDate;
	
    @Transient
    public boolean isRequestWithDealer(){
		if(WarrantyCoverageRequestAudit.FORWARDED.equals(status)||
				WarrantyCoverageRequestAudit.WAITING_FOR_YOUR_RESPONSE.equals(status)	){
		  return true;	
		}
		return false;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}


	public Integer getNoOfMonthsInReduction() {
		return noOfMonthsInReduction;
	}

	public void setNoOfMonthsInReduction(Integer noOfMonthsInReduction) {
		this.noOfMonthsInReduction = noOfMonthsInReduction;
	}

	public SortedSet<WarrantyCoverageRequestAudit> getAudits() {
		return audits;
	}

	public void setAudits(SortedSet<WarrantyCoverageRequestAudit> audits) {
		this.audits = audits;
	}

	public List<PolicyDefinition> getPoliciesWithReducedCoverage() {
		return policiesWithReducedCoverage;
	}

	public void setPoliciesWithReducedCoverage(
			List<PolicyDefinition> policiesWithReducedCoverage) {
		this.policiesWithReducedCoverage = policiesWithReducedCoverage;
	}
	
	public void addPoliciesWithReducedCoverage(PolicyDefinition policy){
		if (this.policiesWithReducedCoverage == null){
			this.policiesWithReducedCoverage = new ArrayList<PolicyDefinition>();			
		}
		this.policiesWithReducedCoverage.add(policy);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}
	public void setBusinessUnitInfo(BusinessUnitInfo businessUnitInfo) {
		this.businessUnitInfo = businessUnitInfo;
	}
	
	public boolean isRequestPending(){
		if(WarrantyCoverageRequestAudit.WAITING_FOR_YOUR_RESPONSE.equals(this.status)
				|| WarrantyCoverageRequestAudit.EXTENSION_NOT_REQUESTED.equals(this.status)
				|| WarrantyCoverageRequestAudit.APPROVED.equals(this.status)
				|| WarrantyCoverageRequestAudit.DENIED.equals(this.status)){
			return false;
		}
		return true;
	}

    public ServiceProvider getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(ServiceProvider requestedBy) {
        this.requestedBy = requestedBy;
    }
	public CalendarDate getUpdatedOnDate() {
		return updatedOnDate;
	}
	public void setUpdatedOnDate(CalendarDate updatedOnDate) {
		this.updatedOnDate = updatedOnDate;
	}

}
