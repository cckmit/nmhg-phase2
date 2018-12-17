package tavant.twms.domain.rules.group;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import javax.persistence.*;

import java.util.List;
import java.util.ArrayList;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.security.AuditableColumns;

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com">Vikas Sasidharan</a>
 * Date: 22 Sep, 2008
 * Time: 3:19:23 PM
 */
@Entity
@FilterDefs({
		@FilterDef(name = "bu_name", 
				   parameters = { @ParamDef(name = "name", type = "string") })		
		})
@Filters({
	@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
})
@XStreamAlias("DomainRuleGroup")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DomainRuleGroup implements BusinessUnitAware,AuditableColumns{

    @Id
    @GeneratedValue(generator = "DomainRuleGroup")
    @GenericGenerator(name = "DomainRuleGroup", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
            @Parameter(name = "sequence_name", value = "DOMAIN_RULE_GROUP_SEQ"),
            @Parameter(name = "initial_value", value = "1000"),
            @Parameter(name = "increment_size", value = "20") })
    private Long id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "ruleGroup", fetch = FetchType.LAZY)
    @OrderBy("priority")
    @org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})    
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<DomainRule> rules = new ArrayList<DomainRule>();

    private Long priority;

    /**
     * If set to true, then once we are done executing all the rules in this group we check if at least one of the rules
     * in the group fired and if yes, we terminate the entire rule processing flow at that point.
     */
    @Column(name = "STOP_RULE_PROC_ON_SUCCESS")
    private Boolean stopRuleProcessingOnSuccess = Boolean.FALSE;

    /**
     * If set to true, then at the first instance of a rule firing, we terminate the entire rule processing flow at that
     * point. Note that when a rule fires and the flow is terminated, the remaining rules in the same rule group are
     * also skipped.
     */
    @Column(name = "STOP_RULE_PROC_ON_FIRST_SUCC")
    private Boolean stopRuleProcessingOnFirstSuccess = Boolean.FALSE;

    
    /*
     * Applicable for routing rule groups only.
     * If set to true, when there is no result after executing all the rules from the rule group,
     * rule processing will be stopped without executing the remaining rule groups.
     * Otherwise the processing will continue with the next rule group. 
     */
    @Column(name = "STOP_RULE_PROC_ON_NO_RESULT")
    private Boolean stopRuleProcessingOnNoResult = Boolean.FALSE;
    
    /*
     * Applicable for routing rule groups only.
     * If set to true, when there is more than one result after executing all the rules from the rule group,
     * rule processing will be stopped without executing the remaining rule groups and load balancing is done.
     * Otherwise the processing will continue with the next rule group to narrow down the result. 
     */
    @Column(name = "STOP_RULE_PROC_ON_MULTI_RESULT")
    private Boolean stopRuleProcessingOnMultipleResult = Boolean.FALSE;
        
    private String context;
    
    /**
     * Flag used to soft delete the rule group.  
     */
    private String status;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<DomainRule> getRules() {
        return rules;
    }

    public void setRules(List<DomainRule> rules) {
        this.rules = rules;
    }

    public void addRule(DomainRule rule) {
        rule.setRuleGroup(this);
        rules.add(rule);
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Boolean getStopRuleProcessingOnSuccess() {
        return stopRuleProcessingOnSuccess;
    }

    public void setStopRuleProcessingOnSuccess(Boolean stopRuleProcessingOnSuccess) {
        this.stopRuleProcessingOnSuccess = stopRuleProcessingOnSuccess;
    }

    public Boolean getStopRuleProcessingOnFirstSuccess() {
        return stopRuleProcessingOnFirstSuccess;
    }

    public void setStopRuleProcessingOnFirstSuccess(Boolean stopRuleProcessingOnFirstSuccess) {
        this.stopRuleProcessingOnFirstSuccess = stopRuleProcessingOnFirstSuccess;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();
    
	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
		
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

	public void setD(AuditableColEntity auditableColEntity) {
		this.d = d;
	}
	
	public Boolean getStopRuleProcessingOnNoResult() {
		return stopRuleProcessingOnNoResult;
	}

	public void setStopRuleProcessingOnNoResult(Boolean stopRuleProcessingOnNoResult) {
		this.stopRuleProcessingOnNoResult = stopRuleProcessingOnNoResult;
	}

	public Boolean getStopRuleProcessingOnMultipleResult() {
		return stopRuleProcessingOnMultipleResult;
	}

	public void setStopRuleProcessingOnMultipleResult(
			Boolean stopRuleProcessingOnMultipleResult) {
		this.stopRuleProcessingOnMultipleResult = stopRuleProcessingOnMultipleResult;
	}

}
