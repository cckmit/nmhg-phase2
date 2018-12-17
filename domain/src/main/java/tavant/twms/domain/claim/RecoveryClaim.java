/*
 *   Copyright (c) 2007 Tavant Technologies
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
package tavant.twms.domain.claim;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.OrderBy;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.claim.payment.RecoveryPayment;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.*;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.supplier.CostLineItem;
import tavant.twms.domain.supplier.contract.CompensationTerm;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.domain.common.Document;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.SecurityHelper;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author pradipta.a
 */
@Entity
@Filters( { @Filter(name = "excludeInactive") })
@PropertiesWithNestedCurrencyFields( { "costLineItems", "recoveryPayment" })
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class RecoveryClaim implements AuditableColumns,BusinessUnitAware,Comparable<RecoveryClaim> {

	private static final Logger logger = Logger.getLogger(RecoveryClaim.class);

	@Id
	@GeneratedValue
	private Long id;

	@Type(type = "org.hibernate.type.EnumType", parameters = {
			@Parameter(name = "enumClass", value = "tavant.twms.domain.claim.RecoveryClaimState"),
			@Parameter(name = "type", value = "" + Types.VARCHAR) })
	private RecoveryClaimState recoveryClaimState;
	
	@Column(nullable = true, length = 4000)
	private String comments;
    
	private String externalComments;
	
	private Boolean physicalShipmentNeeded;

	@ManyToOne(fetch = FetchType.LAZY)
	private Contract contract;

	@ManyToOne(fetch = FetchType.LAZY)
	private Supplier supplier;

	@Transient
	private User loggedInUser;
	
	@Transient
	@Type(type = "tavant.twms.infra.MoneyUserType")
	private Money acceptedAmount;
	
	@OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private RecoveryClaimAudit activeRecoveryClaimAudit = new RecoveryClaimAudit();

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinTable(name = "rec_clm_cost_line_items")
	@OrderBy("section")
	private List<CostLineItem> costLineItems = new ArrayList<CostLineItem>();

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "for_recovery_claim", nullable = false, updatable = false, insertable = true)
	@IndexColumn(name = "list_index", nullable = false)
	@Sort(type=SortType.NATURAL)
	private List<RecoveryClaimAudit> recoveryClaimAudits = new ArrayList<RecoveryClaimAudit>();

	@ManyToOne(fetch = FetchType.LAZY)
	private Claim claim;

	private int version;

	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private RecoveryPayment recoveryPayment;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, optional = true)
	@JoinColumn(name = "rec_clm_accpt_reason")
	private RecoveryClaimAcceptanceReason recoveryClaimAcceptanceReason;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, optional = true)
	@JoinColumn(name = "rec_clm_reject_reason")
	private RecoveryClaimRejectionReason recoveryClaimRejectionReason;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, optional=true)
	@JoinColumn(name="rec_clm_cannot_recover_reason")
	private RecoveryClaimCannotRecoverReason recoveryClaimCannotRecoverReason;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate updatedDate;
	
	@Transient
    private SecurityHelper securityHelper;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
	
	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	@Transient
	private String currentAssignee;

    private String recoveryClaimNumber;

    @OneToOne(mappedBy = "recoveryClaim", fetch = FetchType.LAZY)
    private RecoveryClaimInfo recoveryClaimInfo;

    @Transient
    private boolean selected;
    
    @Transient
    private OrgService orgService;


    private boolean flagForAnotherRecovery = false;

    public boolean isFlagForAnotherRecovery() {
        return flagForAnotherRecovery;
    }

    public void setFlagForAnotherRecovery(boolean flagForAnotherRecovery) {
        this.flagForAnotherRecovery = flagForAnotherRecovery;
    }

    private String documentNumber;
    
    public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getRecoveryClaimNumber() {
        return recoveryClaimNumber;
    }

    public Money getAcceptedAmount() {
		return acceptedAmount;
	}

	public void setAcceptedAmount(Money acceptedAmount) {
		this.acceptedAmount = acceptedAmount;
	}

	public void setRecoveryClaimNumber(String recoveryClaimNumber) {
        this.recoveryClaimNumber = recoveryClaimNumber;
    }

    public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public void addCostLineItem(CostLineItem item) {
		this.costLineItems.add(item);
	}

	public CostLineItem getCostLineItem(String sectionName) {
		for (CostLineItem item : this.costLineItems) {
			if (sectionName.equals(item.getSection().getName())) {
				return item;
			}
		}
		return null;
	}

	public void clearDetails() {
		this.contract = null;
		this.supplier = null;
		this.costLineItems.clear();
	}

	public void updateCostLineItemsFromContract() {
		for(CostLineItem costLineItem : this.costLineItems)
			updateCostLineItemsFromContract(costLineItem);
	}
	
	// @RequiresCurrencyConversion
	public void updateCostLineItemsFromContract(CostLineItem costLineItem) {
		if (this.contract != null) {
			updateWithContract(costLineItem);
		} else {
			updateWithoutContract(costLineItem);
		}
	}

	// @RequiresCurrencyConversion
	private void updateWithContract(CostLineItem cli) {
		if (getContract().doesCoverSection(cli.getSection())) {
			if (logger.isDebugEnabled()) {
				logger.debug("updating line item [" + cli + "]");
			}
			CompensationTerm term = getContract()
					.getCompensationTermForSection(
							cli.getSection().getName());
			if (logger.isDebugEnabled()) {
				logger.debug("Compensation term is [" + term + "]");
			}
            term.getRecoveryFormula().applyFormulaToItem(cli);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Contract doesn't cover CostLineItem [" + cli
						+ "]");
			}
			Currency currency = cli.getSupplierCost()
					.breachEncapsulationOfCurrency();
			cli.setCostAfterApplyingContract(Money.valueOf(0, currency));
			cli.setRecoveredCost(Money.valueOf(0, currency));
		}
        if (logger.isDebugEnabled()) {
            logger.debug("After updating item [" + cli + "]");
        }
    }

	// @RequiresCurrencyConversion
	private void updateWithoutContract(CostLineItem costLineItem) {
		costLineItem.setRecoveredCost(costLineItem.getSupplierCost());
	}

	@Transient
	public Money getTotalRecoveredCost() {
		List<Money> amounts = new ArrayList<Money>();
		for (CostLineItem costLineItem : this.costLineItems) {
			Money recoveredCost = costLineItem.getRecoveredCost();
			if (recoveredCost != null) {
				amounts.add(recoveredCost);
			}
		}
		return Money.sum(amounts);
	}
	
	@Transient
	public Money getTotalRecoveredAmount() {
		 if (RecoveryClaimState.DEBITTED_AND_CLOSED.equals(this.recoveryClaimState)) {
			 return this.activeRecoveryClaimAudit.getRecoveredAmount();			
		 }
		 return getAcceptedCostForSection();
	}
	
	 public Money getAcceptedCostForSection() {		
	     List<RecoveryClaimAudit> recClaimAudit=this.getRecoveryClaimAudits();
	     Money acceptedAmount=null;
		if (this.getRecoveryClaimState().equals(RecoveryClaimState.REOPENED)) {
			acceptedAmount = recClaimAudit.get(recClaimAudit.size() - 2)
					.getAcceptedAmount();
		} else {
			acceptedAmount = recClaimAudit.get(recClaimAudit.size() - 1)
					.getAcceptedAmount();
		}
	   	if(acceptedAmount==null)
	   	{
	   		acceptedAmount=getTotalRecoveredCost();
	   	}
	   return acceptedAmount;
	}
	 
	@Transient
	public Money getIncidentalCost() {
		List<Money> amounts = new ArrayList<Money>();
		for (CostLineItem costLineItem : this.costLineItems) {
			if(		costLineItem.getSection().equals(Section.MEALS)|| 
					costLineItem.getSection().equals(Section.PARKING)|| 
					costLineItem.getSection().equals(Section.ITEM_FREIGHT_DUTY)|| 
					costLineItem.getSection().equals(Section.PER_DIEM)|| 
					costLineItem.getSection().equals(Section.RENTAL_CHARGES)|| 
					costLineItem.getSection().equals(Section.LOCAL_PURCHASE)|| 
					costLineItem.getSection().equals(Section.TOLLS)|| 
					costLineItem.getSection().equals(Section.OTHER_FREIGHT_DUTY)||
					costLineItem.getSection().equals(Section.OTHERS) ||
					costLineItem.getSection().equals(Section.HANDLING_FEE) ||
					costLineItem.getSection().equals(Section.TRANSPORTATION_COST) ){
				Money recoveredCost = costLineItem.getRecoveredCost();
				if (recoveredCost != null) {
					amounts.add(recoveredCost);
				}
			}
		}
		return Money.sum(amounts);
	}
	
	@Transient
	public Money getTravelCost() {
		List<Money> amounts = new ArrayList<Money>();
		for (CostLineItem costLineItem : this.costLineItems) {
			if(		costLineItem.getSection().equals(Section.TRAVEL_BY_DISTANCE)|| 
					costLineItem.getSection().equals(Section.TRAVEL_BY_HOURS)|| 
					costLineItem.getSection().equals(Section.TRAVEL_BY_TRIP)|| 
					costLineItem.getSection().equals(Section.PER_DIEM)|| 
					costLineItem.getSection().equals(Section.ADDITIONAL_TRAVEL_HOURS)
					){
				Money recoveredCost = costLineItem.getRecoveredCost();
				if (recoveredCost != null) {
					amounts.add(recoveredCost);
				}
			}
		}
		return Money.sum(amounts);
	}
	
	@Transient
	public Money getTotalActualCost() {
		List<Money> amounts = new ArrayList<Money>();
		for (CostLineItem costLineItem : this.costLineItems) {
			Money actualCost = costLineItem.getActualCost();
			if (actualCost != null) {
				amounts.add(actualCost);
			}
		}
		return Money.sum(amounts);
	}
	
	@Transient
	public Money getTotalSupplierCost() {
		List<Money> amounts = new ArrayList<Money>();
		for (CostLineItem costLineItem : this.costLineItems) {
			Money supplierCost = costLineItem.getSupplierCost();
			if (supplierCost != null) {
				amounts.add(supplierCost);
			}
		}
		return Money.sum(amounts);
	}

	@Transient
	public Money getTotalCostAfterApplyingContract() {
		List<Money> amounts = new ArrayList<Money>();
		for (CostLineItem costLineItem : this.costLineItems) {
			Money costAfterApplyingContract = costLineItem
					.getCostAfterApplyingContract();
			if (costAfterApplyingContract != null) {
				amounts.add(costAfterApplyingContract);
			}
		}
		return Money.sum(amounts);
	}

	@Transient
	public Money getRecoveredCostForSection(String sectionName) {
		Money recoveredCost = null;
		if (getCostLineItem(sectionName) != null) {
			recoveredCost = getCostLineItem(sectionName).getRecoveredCost();
		}
		return recoveredCost;
	}
	
	@Transient
	public Money getSupplierCostForSection(String sectionName) {
		Money supplierCost = null;
		if (getCostLineItem(sectionName) != null) {
			supplierCost = getCostLineItem(sectionName).getSupplierCost();
		}
		return supplierCost;
	}

	@Transient
	public Money getAcutalCostForSection(String sectionName) {
		Money acutalCost = null;
		if (getCostLineItem(sectionName) != null) {
			acutalCost = getCostLineItem(sectionName).getActualCost();
		}
		return acutalCost;
	}

	@Transient
	public Money getCostAfterApplyingContractForSection(String sectionName) {
		Money costAfterApplyingContract = null;
		if (getCostLineItem(sectionName) != null) {
			costAfterApplyingContract = getCostLineItem(sectionName)
					.getCostAfterApplyingContract();
		}
		return costAfterApplyingContract;
	}

    public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RecoveryClaimState getRecoveryClaimState() {
		return this.recoveryClaimState;
	}

	public void setRecoveryClaimState(RecoveryClaimState recoveryClaimState) {
		this.recoveryClaimState = recoveryClaimState;
		setUpdatedDate(Clock.today());
		RecoveryClaimAudit audit = RecoveryClaimAudit.replicateRecoveryClaimAudit(getActiveRecoveryClaimAudit());
		audit.setCreatedBy(getLoggedInUser(recoveryClaimState));
		audit.setCreatedOn(Clock.today());		
		if(isExternalCommentsToSet(recoveryClaimState)){
			audit.setExternalComments(this.externalComments);
			if(!recoveryClaimState.getState().equalsIgnoreCase("Rejected") && !recoveryClaimState.getState().equalsIgnoreCase("Ready for Debit")){
			audit.setComments(getInternalComments(recoveryClaimState));
			}
		}
		audit.setPartReturnCommentsToDealer(this.getPartReturnCommentsToDealer());
		if (RecoveryClaimState.READY_FOR_DEBIT.equals(this.recoveryClaimState)
				|| RecoveryClaimState.NO_RESPONSE_AND_AUTO_DEBITTED
						.equals(this.recoveryClaimState)
				|| RecoveryClaimState.DISPUTED_AND_AUTO_DEBITTED
						.equals(this.recoveryClaimState)
						|| RecoveryClaimState.DEBITTED_AND_CLOSED
						.equals(this.recoveryClaimState)) {
			if(!RecoveryClaimState.DEBITTED_AND_CLOSED.equals(this.recoveryClaimState))
				audit.setRecoveredAmount(this.getAcceptedAmount());
			audit.setAcceptedAmount(this.getAcceptedAmount());
		}
		audit.setForRecoveryClaim(this);
        audit.setRecoveryClaimState(recoveryClaimState);
		getRecoveryClaimAudits().add(audit);
		activeRecoveryClaimAudit.setRecoveryClaimState(recoveryClaimState);
	}

	private String getInternalComments(RecoveryClaimState recoveryClaimState) {		
	    if (recoveryClaimState
					.equals(RecoveryClaimState.AUTO_DISPUTED_INITIAL_RESPONSE)
					|| recoveryClaimState
							.equals(RecoveryClaimState.AUTO_DISPUTED_FINAL_RESPONSE)) {
	    	return "Auto Disputed";
	    }
		return this.comments;
	}

	private User getLoggedInUser(RecoveryClaimState recoveryClaimState) {
        if (recoveryClaimState
				.equals(RecoveryClaimState.AUTO_DISPUTED_INITIAL_RESPONSE)
				|| recoveryClaimState
						.equals(RecoveryClaimState.AUTO_DISPUTED_FINAL_RESPONSE)) {
            return orgService.findSystemUserByName();
        }
        else if (this.loggedInUser != null) {
			return this.loggedInUser;
		}else if(this.securityHelper != null){
			return this.securityHelper.getLoggedInUser();
		}
        return null;
    }
	
	private boolean isExternalCommentsToSet(
			RecoveryClaimState recoveryClaimState) {
		if (recoveryClaimState
				.equals(RecoveryClaimState.AUTO_DISPUTED_INITIAL_RESPONSE)
				|| recoveryClaimState
						.equals(RecoveryClaimState.AUTO_DISPUTED_FINAL_RESPONSE) || recoveryClaimState
						.equals(RecoveryClaimState.REOPENED) || recoveryClaimState
						.equals(RecoveryClaimState.DEBITTED_AND_CLOSED)) {
			return false;
		}
		return true;
	}

	public RecoveryClaimAudit getLatestRecoveryAudit() {
		if (this.recoveryClaimAudits != null
				&& this.recoveryClaimAudits.size() > 0) {
			return this.recoveryClaimAudits
					.get(this.recoveryClaimAudits.size() - 1);
		}
		return null;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public Boolean getPhysicalShipmentNeeded() {
		return this.physicalShipmentNeeded;
	}

	public void setPhysicalShipmentNeeded(Boolean physicalShipmentNeeded) {
		this.physicalShipmentNeeded = physicalShipmentNeeded;
	}

	public List<CostLineItem> getCostLineItems() {
		return this.costLineItems;
	}

	public void setCostLineItems(List<CostLineItem> costLineItems) {
		this.costLineItems = costLineItems;
	}

	public Contract getContract() {
		return this.contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Supplier getSupplier() {
		return this.supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Claim getClaim() {
		return this.claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public RecoveryPayment getRecoveryPayment() {
		return this.recoveryPayment;
	}

	public void setRecoveryPayment(RecoveryPayment recoveryPayment) {
		this.recoveryPayment = recoveryPayment;
	}

	public List<RecoveryClaimAudit> getRecoveryClaimAudits() {
		return this.recoveryClaimAudits;
	}

	public void setRecoveryClaimAudits(
			List<RecoveryClaimAudit> recoveryClaimAudits) {
		this.recoveryClaimAudits = recoveryClaimAudits;
	}

	public void addClaimAudit(RecoveryClaimAudit recClaimAudit) {
		this.recoveryClaimAudits.add(recClaimAudit);
	}

	public RecoveryClaimAudit getActiveRecoveryClaimAudit() {
	    return this.activeRecoveryClaimAudit;
	}

	public void setActiveRecoveryClaimAudit(
			RecoveryClaimAudit activeRecoveryClaimAudit) {
		this.activeRecoveryClaimAudit = activeRecoveryClaimAudit;
	}

	public User getLoggedInUser() {
		return this.loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public RecoveryClaimAcceptanceReason getRecoveryClaimAcceptanceReason() {
		return recoveryClaimAcceptanceReason;
	}

	public void setRecoveryClaimAcceptanceReason(
			RecoveryClaimAcceptanceReason recoveryClaimAcceptanceReason) {
		this.recoveryClaimAcceptanceReason = recoveryClaimAcceptanceReason;
	}

	public RecoveryClaimRejectionReason getRecoveryClaimRejectionReason() {
		return recoveryClaimRejectionReason;
	}

	public void setRecoveryClaimRejectionReason(
			RecoveryClaimRejectionReason recoveryClaimRejectionReason) {
		this.recoveryClaimRejectionReason = recoveryClaimRejectionReason;
	}

	public CalendarDate getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(CalendarDate updatedDate) {
		this.updatedDate = updatedDate;
	}

	public boolean isRecoveryAmountZero() {
		//NMHGSLMS-1360 Fix
		if(this.getBusinessUnitInfo().getName().equalsIgnoreCase(AdminConstants.NMHGAMER))
			return false;
        Money amountForRecovery = getAcceptedAmount();
        if(!recoveryPayment.getPreviousPaidAmount().isZero()) {
		    amountForRecovery = getAcceptedAmount()
				.minus(recoveryPayment.getPreviousPaidAmount());
        }
		return amountForRecovery.isZero();
	}
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
	
	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo businessUnitInfo) {
		this.businessUnitInfo = businessUnitInfo;
	}
	
	public int compareTo(RecoveryClaim recoveryClaim) {
		if (recoveryClaim == null) {
			return 0;
		}
		if (this.d != null && this.d.getCreatedOn() != null && recoveryClaim.getD() != null && 
				recoveryClaim.getD().getCreatedOn() != null) {
			int dateCompare = this.d.getCreatedOn().compareTo(recoveryClaim.getD().getCreatedOn());
			return dateCompare;
		}
		return 0;
	}
	
	public String getCurrentAssignee(){
		return currentAssignee;
	}

	public void setCurrentAssignee(String currentAssignee){
		this.currentAssignee = currentAssignee;
	}

	public RecoveryClaimInfo getRecoveryClaimInfo() {
		return recoveryClaimInfo;
	}

	public void setRecoveryClaimInfo(RecoveryClaimInfo recoveryClaimInfo) {
		this.recoveryClaimInfo = recoveryClaimInfo;
	}
	
	public User getSupplierUserForRecoveryClaim() {
		String claimBU = getBusinessUnitInfo().getName();
		Set<User> users = new TreeSet<User>();
		if(getContract() != null && getContract().getSupplier() != null 
				&& getContract().getSupplier().getUsers() != null)
			users = getContract().getSupplier().getUsers();
		User masterSupplier = null;
		User supplierUser = null;
		for(User user : users) {
			String userBU = user.getPreferredBu() != null ? user.getPreferredBu() : user.getBusinessUnits() != null && !user.getBusinessUnits().isEmpty() ? user.getBusinessUnits().first().getName() : null;
			if(userBU != null && claimBU.equalsIgnoreCase(userBU)) {
				if(user.hasRole("masterSupplier")) {
					masterSupplier = user;
				} else {
					supplierUser = user;
				}
			}
		}
		if(supplierUser != null) {
			return supplierUser;
		} else {
			return masterSupplier;
		}
	}
	
	public void setAttachments(List<Document> attachments) {
        this.getActiveRecoveryClaimAudit().setAttachments(attachments);
    }	
	
	public List<Document> getAttachments() {
        return this.getActiveRecoveryClaimAudit().getAttachments();
    }

	public RecoveryClaimCannotRecoverReason getRecoveryClaimCannotRecoverReason() {
		return recoveryClaimCannotRecoverReason;
	}

	public void setRecoveryClaimCannotRecoverReason(
			RecoveryClaimCannotRecoverReason recoveryClaimCannotRecoverReason) {
		this.recoveryClaimCannotRecoverReason = recoveryClaimCannotRecoverReason;
	}

	public String getPartReturnStatus() {
         List<RecoverablePart> recoverableParts= this.getRecoveryClaimInfo().getRecoverableParts();
         List<OEMPartReplaced> partsReplaced= new ArrayList<OEMPartReplaced>();
         for(RecoverablePart recoverablePart : recoverableParts){
        	 partsReplaced.add(recoverablePart.getOemPart());
         }
        return getSortedPartReturnStatus(partsReplaced);
    }
	
	private String getSortedPartReturnStatus(List<OEMPartReplaced> partsReplaced) {
        if (partsReplaced != null && !partsReplaced.isEmpty()) {
            Collections.sort(partsReplaced,
                    new Comparator<OEMPartReplaced>() {
                        public int compare(OEMPartReplaced obj1,
                                           OEMPartReplaced obj2) {
                            if (obj1.getStatus() == null && obj2.getStatus() != null)
                                return 1;
                            if (obj1.getStatus() != null && obj2.getStatus() == null)
                                return -1;
                            if (obj1.getStatus() == null && obj2.getStatus() == null)
                                return 0;
                            if (obj1.getStatus().ordinal() > obj2
                                    .getStatus().ordinal()) {
                                return 1;
                            } else if (obj1.getStatus().ordinal() < obj2
                                    .getStatus().ordinal()) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
            if (partsReplaced.get(0).getStatus() != null) {
                if (partsReplaced.get(0).getStatus() != null)
                    return partsReplaced.get(0).getStatus().getStatus();
                else
                    return null;
            }
        }
        return null;
    }

    public String getPartRecoveryStatus() {
        List<RecoverablePart> recoverableParts= this.getRecoveryClaimInfo().getRecoverableParts();
        return getSortedPartRecoveryStatus(recoverableParts);
    }

    private String getSortedPartRecoveryStatus(List<RecoverablePart> partsReplaced) {
        if (partsReplaced != null && !partsReplaced.isEmpty()) {
            Collections.sort(partsReplaced,
                    new Comparator<RecoverablePart>() {
                        public int compare(RecoverablePart obj1,
                                           RecoverablePart obj2) {
                            if (obj1.getStatus() == null && obj2.getStatus() != null)
                                return 1;
                            if (obj1.getStatus() != null && obj2.getStatus() == null)
                                return -1;
                            if (obj1.getStatus() == null && obj2.getStatus() == null)
                                return 0;
                            if (obj1.getStatus().ordinal() > obj2
                                    .getStatus().ordinal()) {
                                return 1;
                            } else if (obj1.getStatus().ordinal() < obj2
                                    .getStatus().ordinal()) {
                                return -1;
                            } else {
                                return 0;
                            }
                        }
                    });
            if (partsReplaced.get(0).getStatus() != null) {
                if (partsReplaced.get(0).getStatus() != null)
                    //TODO For phase 2 check what US wants to be displayed for status
                //TODO If it is VPRA generated no action required
                    if(partsReplaced.get(0).getStatus().getStatus().equalsIgnoreCase(Constants.SHIPMENT_GENERATED)) {
                        return Constants.VPRA_GENERATED;
                    }else
                    return partsReplaced.get(0).getStatus().getStatus();
                else
                    return null;
            }
        }
        return null;
    }

    public String getReturnLocation(){
        if(this.getRecoveryClaimInfo().getRecoverableParts().size() >0 && this.getRecoveryClaimInfo().getRecoverableParts().get(0).getSupplierPartReturns().size()>0){
            return this.getRecoveryClaimInfo().getRecoverableParts().get(0).getSupplierPartReturns().get(0).getReturnLocation().getCode();
        }
        return null;
    }

	public String getExternalComments() {
		return externalComments;
	}

	public void setExternalComments(String externalComments) {
		this.externalComments = externalComments;
	}

	public String getPartReturnCommentsToDealer() {
		return this.getActiveRecoveryClaimAudit().getPartReturnCommentsToDealer();
	}

	public void setPartReturnCommentsToDealer(String partReturnCommentsToDealer) {
		this.getActiveRecoveryClaimAudit().setPartReturnCommentsToDealer(partReturnCommentsToDealer);
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
	
	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

    public boolean isPartShippedByDealer() {
        for(RecoverablePart recoverablePart : this.getRecoveryClaimInfo().getRecoverableParts()){
            if(recoverablePart.getOemPart().isPartToBeReturned() && recoverablePart.getOemPart().isReturnDirectlyToSupplier() && recoverablePart.getOemPart().getStatus() != null && recoverablePart.getOemPart().getStatus().ordinal() >= PartReturnStatus.PART_SHIPPED.ordinal()){
                return true;
            }else if(recoverablePart.isSupplierReturnNeeded() && !recoverablePart.getOemPart().isReturnDirectlyToSupplier() && recoverablePart.getStatus() != null && recoverablePart.getStatus().ordinal() >= PartReturnStatus.PART_SHIPPED.ordinal()){
                return true;
            }
        }
        return false;
    }
}
