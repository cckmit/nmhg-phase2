/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;
import tavant.twms.security.authz.infra.SecurityHelper;
import tavant.twms.common.Views;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.I18NDomainRuleText;
import tavant.twms.domain.catalog.I18NDomainRuleDescription;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.rules.group.DomainRuleGroup;
import tavant.twms.security.AuditableColumns;


import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@Filters({
	  @Filter(name="excludeInactive")
	})
@Inheritance(strategy=InheritanceType.JOINED)
	@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
	@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@XStreamAlias("domainRule")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DomainRule implements BusinessUnitAware,AuditableColumns{

	@Id
	@GeneratedValue(generator = "DomainRule")
	@GenericGenerator(name = "DomainRule", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "DOMAIN_RULE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	@OneToOne(fetch = FetchType.EAGER)
	@Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE })
	private DomainPredicate predicate;

    @Lob
    private String ognlExpression;
	
	private String description;
	
	@SuppressWarnings("unused")
	@Transient
	private String failureMessage;

	private String context;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	private RejectionReason rejectionReason;
	
	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@IndexColumn(name = "list_index", nullable = false)
	@JoinColumn(name = "domain_rule", nullable = false, updatable = false, insertable = true)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<DomainRuleAudit> ruleAudits = new ArrayList<DomainRuleAudit>();

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "domainRule", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<I18NDomainRuleText> domainRuleTexts = new ArrayList<I18NDomainRuleText>();
	
	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "domainRule", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<I18NDomainRuleDescription> domainRuleDesc = new ArrayList<I18NDomainRuleDescription>();
	
	
	@Column(nullable = false, updatable = false)
	private Integer ruleNumber;
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade( { org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private DomainRuleGroup ruleGroup;

    private String ruleApplicable;
    
    private Long priority;
    
    private String status;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    private ServiceProvider forDealer;
    
    public DomainRuleGroup getRuleGroup() {
        return ruleGroup;
    }

    public void setRuleGroup(DomainRuleGroup ruleGroup) {
        this.ruleGroup = ruleGroup;
        ruleGroup.getRules().add(this);
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
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

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContext() {
		return context;
	}
	
	public void setContext(String context) {
		this.context = context;
	}

	public Long getId() {
		return id;
	}

	public DomainPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(DomainPredicate predicate) {
		this.predicate = predicate;
	}

	public String getFailureMessage() {
		/*String failureDescription = "";
		for (I18NDomainRuleText i18RuleText : this.domainRuleTexts) {
			if (i18RuleText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && i18RuleText.getFailureDescription() != null) {
				failureDescription = i18RuleText.getFailureDescription();
				break;
			}
			else if(i18RuleText.getLocale().equalsIgnoreCase("en_US")) {
				failureDescription = i18RuleText.getFailureDescription();
			}
		
		}*/
		return ruleAudits.get(ruleAudits.size()-1).getFailureMessage();
	}
	public String getFailureMessageInUS(){
		String failureDescription = "";
		for (I18NDomainRuleText i18RuleText : this.domainRuleTexts) {
			if(i18RuleText.getLocale().equalsIgnoreCase("en_US")){
				failureDescription = i18RuleText.getFailureDescription();	
			}
		}
		
		return failureDescription;
	}

	public String getFailureMessageForLocale(String locale) {
		String failureDescription = "";
		for (I18NDomainRuleText failureText : this.getDomainRuleTexts()) {
			if (failureText.getLocale().equalsIgnoreCase(locale) 
					&& failureText.getFailureDescription() != null) {
				failureDescription = failureText.getFailureDescription();
				break;
			}
			else if(failureText.getLocale().equalsIgnoreCase("en_US")) {
				failureDescription = failureText.getFailureDescription();
			}
		}
		return failureDescription;
	}
	
	public String getRuleDescInUS(){
		String ruleDescription = "";
		for (I18NDomainRuleDescription i18Ruledesc : this.domainRuleDesc) {
			if(i18Ruledesc.getLocale().equalsIgnoreCase("en_US")){
				ruleDescription = i18Ruledesc.getRuleDescription();	
			}
		}
		
		return ruleDescription;
	}
	
	public String getRuleDescriptioneForLocale(String locale) {
		String ruleDescription = "";
		for (I18NDomainRuleDescription ruleDesc : this.getDomainRuleDesc()) {
			if (ruleDesc.getLocale().equalsIgnoreCase(locale) 
					&& ruleDesc.getRuleDescription() != null) {
				ruleDescription = ruleDesc.getRuleDescription();
				break;
			}
			else if(ruleDesc.getLocale().equalsIgnoreCase("en_US")) {
				ruleDescription = ruleDesc.getRuleDescription();
			}
		}
		return ruleDescription;
	}
	
	public Integer getRuleNumber() {
		return ruleNumber;
	}

	public void setRuleNumber(Integer ruleNumber) {
		this.ruleNumber = ruleNumber;
	}	

    public String getOgnlExpression() {
        return ognlExpression;
    }

    public void setOgnlExpression(String ognlExpression) {
        this.ognlExpression = ognlExpression;
    }

    public void updateOgnlExpression() {
        if (getPredicate().getPredicateAsXML() != null) {
            OGNLExpressionGenerator expressionGenerator = new OGNLExpressionGenerator();
           getPredicate().accept(expressionGenerator);
            setOgnlExpression(expressionGenerator.getExpressionString());
        }
    }		

	public List<DomainRuleAudit> getRuleAudits() {
		return ruleAudits;
	}

	public void setRuleAudits(List<DomainRuleAudit> ruleAudits) {
		this.ruleAudits = ruleAudits;
	}

	@Transient
	public DomainRuleAction getAction() {
	  return ruleAudits.get(ruleAudits.size()-1).getAction();
	}

	@Transient
	public String getName() {
		   String ruleDescription = "";
			for (I18NDomainRuleDescription i18RuleDesc : this.domainRuleDesc) {
				if (i18RuleDesc.getLocale().equalsIgnoreCase(
						new SecurityHelper().getLoggedInUser().getLocale()
								.toString()) && i18RuleDesc.getRuleDescription() != null) {
					ruleDescription = i18RuleDesc.getRuleDescription();
					break;
				}
				else if(i18RuleDesc.getLocale().equalsIgnoreCase("en_US")) {
					ruleDescription = i18RuleDesc.getRuleDescription();
				}
			
			}
			return ruleDescription;
		
	}	

	
	@Override
	public String toString() {
		StringBuffer text = new StringBuffer(50);
		text.append(getName());
		text.append("{");
		text.append(predicate);
		text.append("}");
		return text.toString();
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
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

	public RejectionReason getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(RejectionReason rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	

	public List<I18NDomainRuleText> getDomainRuleTexts() {
		return domainRuleTexts;
	}

	public void setDomainRuleTexts(List<I18NDomainRuleText> domainRuleTexts) {
		this.domainRuleTexts = domainRuleTexts;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * This should not be used.. Retained only for display in summary table
	 * @return
	 */		
	public String getStatus() {
		return status;
	}

	/**
	 * This should not be used.. Retained only for display in summary table
	 * DomainRule Audit should be used . This field is automatically updated nb
	 * @param status
	 */
	
	@Deprecated
	public void setStatus(String status) {
		this.status = status;
	}
	
	public List<I18NDomainRuleDescription> getDomainRuleDesc() {
		return domainRuleDesc;
	}

	public void setDomainRuleDesc(List<I18NDomainRuleDescription> domainRuleDesc) {
		this.domainRuleDesc = domainRuleDesc;
	}

    public void setRuleApplicable(String ruleApplicable) {
        this.ruleApplicable = ruleApplicable;
    }

    public String getRuleApplicable() {
        return ruleApplicable;
    }

    public ServiceProvider getForDealer() {
        return forDealer;
    }

    public void setForDealer(ServiceProvider forDealer) {
        this.forDealer = forDealer;
    }

	
}


