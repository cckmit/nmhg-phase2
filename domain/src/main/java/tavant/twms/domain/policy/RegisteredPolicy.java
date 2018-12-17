/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.domain.policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.rules.RuleExecutionTemplate;
import tavant.twms.domain.common.Document;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.CalendarInterval;

/**
 *
 * @author radhakrishnan.j
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Table(name = "policy")
public class RegisteredPolicy implements Policy, Comparable<RegisteredPolicy>, AuditableColumns {
	@Id
	@GeneratedValue(generator = "Policy")
	@GenericGenerator(name = "Policy", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "POLICY_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PolicyDefinition policyDefinition;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "amount", nullable = true),
            @Column(name = "currency", nullable = true) })
    private Money amount;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "price", nullable = false),
            @Column(name = "price_currency", nullable = false) })
    private Money price;
    
    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "tax_amount", nullable = true),
            @Column(name = "tax_currency", nullable = true) })
    private Money taxAmount;
    
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Warranty warranty;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade( { CascadeType.ALL })
    @JoinColumn(name = "forPolicy", nullable = false)
    @Filter(name="excludeInactive")
    // make this a sorted set
    private final List<RegisteredPolicyAudit> policyAudits = new ArrayList<RegisteredPolicyAudit>();

    private String purchaseOrderNumber;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate purchaseDate;
    
    @OneToMany(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JoinTable(name = "policy_attachments", joinColumns = { @JoinColumn(name = "policy") },
    		inverseJoinColumns = { @JoinColumn(name = "attachments") })
    private List<Document> attachments = new ArrayList<Document>();
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
    
    private String status;
    
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PolicyDefinition getPolicyDefinition() {
        return this.policyDefinition;
    }

    public void setPolicyDefinition(PolicyDefinition policyDefinition) {
        this.policyDefinition = policyDefinition;
    }

    public WarrantyType getWarrantyType() {
        return this.policyDefinition.getWarrantyType();
    }

    public String getCode() {
        return this.policyDefinition.getCode();
    }

    /*
     * (non-Javadoc)
     * @see tavant.twms.domain.policy.Policy#getDescription()
     */
    public String getDescription() {
        return this.policyDefinition.getDescription();
    }

    /*
     * (non-Javadoc)
     * @see tavant.twms.domain.policy.Policy#getWarrantyPeriod()
     */
    public CalendarDuration getWarrantyPeriod() {
        if (this.getPolicyAudits() == null || this.getPolicyAudits().size() == 0) {
            return null;
        } else {
            return this.getPolicyAudits().get(this.getPolicyAudits().size() - 1)
                    .getWarrantyPeriod();
        }
    }

    public void setWarrantyPeriod(CalendarDuration warrantyPeriod) {

        RegisteredPolicyAudit audit = new RegisteredPolicyAudit();
        audit.setWarrantyPeriod(warrantyPeriod);
        this.policyAudits.add(audit);

    }

    /*
     * (non-Javadoc)
     * @see tavant.twms.domain.policy.Policy#covers(tavant.twms.domain.claim.Claim)
     */
    public boolean covers(ClaimedItem claimedItem, Integer serviceHoursCovered)
            throws PolicyException {
        if (this.getWarrantyPeriod() == null) {
            if (this.getPolicyAudits() != null) {
                this.getPolicyAudits().get(this.getPolicyAudits().size() - 1).setWarrantyPeriod(
                        this.policyDefinition.computeWarrantyPeriod(claimedItem));
            }
        }
        boolean isCoverageActive = (RegisteredPolicyStatusType.TERMINATED.getStatus().equals(
                this.getPolicyAudits().get(this.getPolicyAudits().size() - 1).getStatus()) || RegisteredPolicyStatusType.INPROGRESS.getStatus().equals(
                        this.getPolicyAudits().get(this.getPolicyAudits().size() - 1).getStatus()) || 
                        RegisteredPolicyStatusType.INACTIVE.getStatus().equals(
                                this.getPolicyAudits().get(this.getPolicyAudits().size() - 1).getStatus())) ? false
                : true;
        if (!isCoverageActive) {
            return false;
        } else {
            return this.policyDefinition.covers(claimedItem, getLatestPolicyAudit()
                    .getWarrantyPeriod(), getLatestPolicyAudit().getServiceHoursCovered());
        }
    }
    
	public boolean covers(Claim claimedItem, Integer serviceHoursCovered)
			throws PolicyException {
		if (this.getWarrantyPeriod() == null) {
			if (this.getPolicyAudits() != null) {
				this.getPolicyAudits().get(this.getPolicyAudits().size() - 1)
						.setWarrantyPeriod(
								this.policyDefinition
										.computeWarrantyPeriod(claimedItem));
			}
		}
		boolean isCoverageActive = (RegisteredPolicyStatusType.TERMINATED
				.getStatus().equals(
						this.getPolicyAudits().get(
								this.getPolicyAudits().size() - 1).getStatus())
				|| RegisteredPolicyStatusType.INPROGRESS.getStatus().equals(
						this.getPolicyAudits().get(
								this.getPolicyAudits().size() - 1).getStatus()) || RegisteredPolicyStatusType.INACTIVE
				.getStatus().equals(
						this.getPolicyAudits().get(
								this.getPolicyAudits().size() - 1).getStatus())) ? false
				: true;
		if (!isCoverageActive) {
			return false;
		} else {
			return this.policyDefinition.covers(claimedItem,
					getLatestPolicyAudit().getWarrantyPeriod(),
					getLatestPolicyAudit().getServiceHoursCovered());
		}
	}

    /*
     * (non-Javadoc)
     * @see tavant.twms.domain.policy.Policy#isStillAvailableFor(tavant.twms.domain.inventory.InventoryItem)
     */
    public boolean isStillAvailableFor(InventoryItem inventoryItem) throws PolicyException {
        return isAvailable(inventoryItem, inventoryItem.getDeliveryDate());
    }

    public boolean isAvailable(InventoryItem inventoryItem, CalendarDate asOfDate)
            throws PolicyException {
        if (inventoryItem.isInStock()) {
            return false;
        } else {
            if (this.getWarrantyPeriod() != null) {
                return this.policyDefinition.isHoursOnMachineWithinCoverage(inventoryItem)
                        && this.getWarrantyPeriod().includes(asOfDate);
            } else {
                return false;
            }
        }
    }
    
    public int compareTo(RegisteredPolicy other) {
    	if (other == null) {
            return 1;
        }
    	int codeCompare = this.policyDefinition.getCode().compareTo(other.policyDefinition.getCode());
    	return codeCompare;
    }
    
    public CalendarInterval reductionInCoverage(){  
    	if(!WarrantyType.POLICY.getType()
				.equals(getPolicyDefinition().getWarrantyType().getType())){
    		Integer monthsCoveredFromDelivery = getPolicyDefinition().getCoverageTerms()
    		.getMonthsCoveredFromDelivery();
    		CalendarDate maxDateForWarrantyCoverage = getWarrantyPeriod().getFromDate().plusMonths(monthsCoveredFromDelivery).previousDay();
    		CalendarDate actualCoverageDate = getWarrantyPeriod().getTillDate();

    		if(actualCoverageDate.isBefore(maxDateForWarrantyCoverage)){			
    			CalendarInterval ci =	CalendarInterval.inclusive(actualCoverageDate, maxDateForWarrantyCoverage.previousDay());
    			return ci;			
    		} else{    	
    			return null;
    		}
    	}
    	return null;
    }
    
    /*
     * (non-Javadoc)
     * @see tavant.twms.domain.policy.Policy#isApplicable(tavant.twms.domain.claim.Claim,
     * tavant.twms.domain.rules.RuleExecutionTemplate)
     */
    public boolean isApplicable(Claim claim, RuleExecutionTemplate ruleExecutionTemplate) {
        return this.policyDefinition.isApplicable(claim, ruleExecutionTemplate);
    }

    public Warranty getWarranty() {
        return this.warranty;
    }

    public void setWarranty(Warranty warranty) {
        this.warranty = warranty;
    }

    public Money getPrice() {
        return this.price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("policyDefinition",
                this.policyDefinition.getId()).append("warrantyPeriod", getWarrantyPeriod())
                .toString();
    }

    public List<RegisteredPolicyAudit> getPolicyAudits() {
        if (this.policyAudits != null && this.policyAudits.size() > 0) {
            Collections.sort(this.policyAudits);
        }
        return this.policyAudits;
    }

    public RegisteredPolicyAudit getLatestPolicyAudit() {
        if (this.getPolicyAudits() != null && this.getPolicyAudits().size() > 0) {
            return this.getPolicyAudits().get(this.getPolicyAudits().size() - 1);
        }
        return null;
    }
    
    public RegisteredPolicyAudit getLatestButOnePolicyAudit() {
        if (this.getPolicyAudits() != null && this.getPolicyAudits().size() > 0) {
			int i = this.getPolicyAudits().size() - 2;
			if (i >= 0) {
				return this.getPolicyAudits().get(i);
			}
		}
		return null;
    }

    public RegisteredPolicyAudit getFirstPolicyAudit() {
        if (this.getPolicyAudits() != null && this.getPolicyAudits().size() > 0) {
            return this.getPolicyAudits().get(0);
        }
        return null;
    }

    public Long getPolicyAuditSize() {
        if (this.policyAudits != null && this.policyAudits.size() > 0) {
            return new Long(this.policyAudits.size());
        }
        return new Long(0);
    }

    public String getPurchaseOrderNumber() {
        return this.purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

	public Money getAmount() {
		return amount;
	}

	public void setAmount(Money amount) {
		this.amount = amount;
	}

	public Money getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(Money taxAmount) {
		this.taxAmount = taxAmount;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public CalendarDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(CalendarDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
	@Override
	public boolean equals(Object registeredPolicy) {
		if (registeredPolicy!=null&&this.policyDefinition.getId().equals(((RegisteredPolicy) registeredPolicy).getPolicyDefinition().getId())) {
			return true;
		} else {
			return false;
		}
	}
    
    public List<Document> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
	}
	
	public RegisteredPolicy clone() {
		RegisteredPolicy registeredPolicy = new RegisteredPolicy();
		registeredPolicy.amount = this.amount;
		registeredPolicy.getAttachments().addAll(this.attachments);		
		registeredPolicy.setPolicyDefinition(this.policyDefinition);
		registeredPolicy.purchaseOrderNumber = this.purchaseOrderNumber;
		registeredPolicy.purchaseDate = this.purchaseDate;
		registeredPolicy.price = this.price;
		registeredPolicy.taxAmount = this.taxAmount;		
		return registeredPolicy;
	}

	public boolean covers(ClaimedItem claimedItem,
			CalendarDuration warrantyPeriod, Integer serviceHoursCovered) {
		return this.policyDefinition.covers(claimedItem, warrantyPeriod, serviceHoursCovered);
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
