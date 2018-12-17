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
package tavant.twms.domain.supplier.contract;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.PropertiesWithNestedCurrencyFields;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Carrier;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.rules.RuleExecutionTemplate;
import tavant.twms.domain.supplier.contract.CoverageCondition.ComparisonWith;
import tavant.twms.security.AuditableColumns;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.HibernateCast;

@Entity
@Filters( { @Filter(name = "excludeInactive") })
@PropertiesWithNestedCurrencyFields( { "compensationTerms" })
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
/**
 * @author kannan.ekanath
 */
public class Contract implements AuditableColumns, BusinessUnitAware {

	@Id
	@GeneratedValue(generator = "Contract")
	@GenericGenerator(name = "Contract", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CONTRACT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	private String name;

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "fromDate", column = @Column(name = "from_date")),
			@AttributeOverride(name = "tillDate", column = @Column(name = "till_date")) })
	private CalendarDuration validityPeriod;

	@ManyToMany(fetch = FetchType.LAZY)
	private List<Item> itemsCovered = new ArrayList<Item>();

	private String description;

	@OneToMany(cascade = { CascadeType.ALL })
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<CoverageCondition> coverageConditions = new ArrayList<CoverageCondition>();

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	private List<CompensationTerm> compensationTerms = new ArrayList<CompensationTerm>();

	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<DomainRule> applicabilityTerms = new ArrayList<DomainRule>();

	@ManyToOne(fetch = FetchType.LAZY)
	private Supplier supplier;

	private Boolean physicalShipmentRequired = true;

	private Boolean sraReviewRequired = true;

	private Boolean autoDebitEnabled = true;

	private Boolean offlineDebitEnabled = false;

	private Boolean collateralDamageToBePaid = true;

	private Boolean recoveryBasedOnCausalPart = true;

	@ManyToOne(fetch = FetchType.LAZY)
	private Location location;

	private Integer supplierResponsePeriod;

	private Integer supplierDisputePeriod;

	private Integer dueDays;
	
	private String shippingInstruction;

	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	private Carrier carrier;

	private String carrierAccount;

	@Column(name="INITIATE_RECOVERY_CLAIM")
	private String whenToInitiateRecoveryClaim;
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    private String rmaNumber;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CalendarDuration getValidityPeriod() {
		return this.validityPeriod;
	}

	public void setValidityPeriod(CalendarDuration validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	public List<Item> getItemsCovered() {
		return this.itemsCovered;
	}

	public void setItemsCovered(List<Item> itemsCovered) {
		this.itemsCovered = itemsCovered;
	}

	public boolean addItemCovered(Item item) {
		return this.itemsCovered.add(item);
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Supplier getSupplier() {
		return this.supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public List<CompensationTerm> getCompensationTerms() {
		return this.compensationTerms;
	}

	public void setCompensationTerms(List<CompensationTerm> compensationTerms) {
		this.compensationTerms = compensationTerms;
	}

	public void addCompensationTerm(CompensationTerm compensationTerm) {
		this.compensationTerms.add(compensationTerm);
	}

	public CompensationTerm getCompensationTermForSection(String sectionName) {
		for (CompensationTerm term : this.compensationTerms) {
			if (sectionName.equals(term.getSection().getName())) {
				return term;
			}
		}
		return null;
	}

	public boolean doesCoverSection(Section section) {
		return getCompensationTermForSection(section.getName()) != null;
	}

	public boolean doesCoverSection(String sectionName) {
		return getCompensationTermForSection(sectionName) != null;

	}

	@Transient
	public boolean isApplicable(Claim claim,
			RuleExecutionTemplate ruleExecutionTemplate,
			boolean doesSerializedPartsHaveShipmentDate) {
		boolean isApplicable = false;
		for (ClaimedItem claimedItem : claim.getClaimedItems()) {
			isApplicable = isClaimedItemCovered(claimedItem, doesSerializedPartsHaveShipmentDate);
			if(!isApplicable)
				break;
		}
		if(isApplicable)
			isApplicable = isRulesEvaluationSuccess(ruleExecutionTemplate,claim);
		return isApplicable;
	}
	
	public boolean isClaimedItemCovered(ClaimedItem claimedItem,boolean doesSerializedPartsHaveShipmentDate) {
		boolean isApplicable = false;
		for (CoverageCondition coverageCondition : this.coverageConditions) {
			if (coverageCondition == null)
				continue;
			if (coverageCondition.getComparedWith() != ComparisonWith.ENERGY_UNITS) {
				isApplicable = coverageCondition.isApplicable(
						claimedItem,doesSerializedPartsHaveShipmentDate);
			} else {
				if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claimedItem
						.getClaim())) {
					isApplicable = coverageCondition
								.isCovered(claimedItem);
				} else {
					isApplicable = coverageCondition
							.isClaimedItemCovered(claimedItem);
				}
			}
			if (!isApplicable)
				break;
		}
		return isApplicable;
	}

	protected boolean isRulesEvaluationSuccess(
			RuleExecutionTemplate ruleExecutionTemplate, Claim claim) {
		ContractRuleExecutionCallBack callBack = new ContractRuleExecutionCallBack(
				claim, this);
		ruleExecutionTemplate.executeRules(callBack);
		return callBack.haveAllRulesPassed();
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", this.id).append(
				"supplier", this.supplier).append("description",
				this.description).toString();
	}

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getPreparedRecoveryFormula(String sectionName) {
		String preparedRecoveryFormula = null;

		for (CompensationTerm compensationTerm : this.compensationTerms) {
			if (sectionName.equals(compensationTerm.getSection().getName())) {
				preparedRecoveryFormula = compensationTerm
						.getPreparedRecoveryFormula();
			}
		}
		return preparedRecoveryFormula;
	}

	public List<DomainRule> getApplicabilityTerms() {
		return this.applicabilityTerms;
	}

	public void setApplicabilityTerms(List<DomainRule> applicabilityTerms) {
		this.applicabilityTerms = applicabilityTerms;
	}

	public boolean addApplicabilityTerm(DomainRule domainRule) {
		return this.applicabilityTerms.add(domainRule);
	}

	public Boolean getPhysicalShipmentRequired() {
		return this.physicalShipmentRequired;
	}

	public void setPhysicalShipmentRequired(Boolean physicalShipmentRequired) {
		this.physicalShipmentRequired = physicalShipmentRequired;
	}

	public List<CoverageCondition> getCoverageConditions() {
		return coverageConditions;
	}

	public void setCoverageConditions(List<CoverageCondition> coverageConditions) {
		this.coverageConditions = coverageConditions;
	}

	public Carrier getCarrier() {
		return carrier;
	}

	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
	}

	public String getCarrierAccount() {
		return carrierAccount;
	}

	public void setCarrierAccount(String carrierAccount) {
		this.carrierAccount = carrierAccount;
	}

	public Boolean getSraReviewRequired() {
		return sraReviewRequired;
	}

	public void setSraReviewRequired(Boolean sraReviewRequired) {
		this.sraReviewRequired = sraReviewRequired;
	}

	public Boolean getAutoDebitEnabled() {
		return autoDebitEnabled;
	}

	public void setAutoDebitEnabled(Boolean autoDebitEnabled) {
		this.autoDebitEnabled = autoDebitEnabled;
	}

	public Boolean getCollateralDamageToBePaid() {
		return collateralDamageToBePaid;
	}

	public void setCollateralDamageToBePaid(Boolean collateralDamageToBePaid) {
		this.collateralDamageToBePaid = collateralDamageToBePaid;
	}

	public Integer getDueDays() {
		return dueDays;
	}

	public void setDueDays(Integer dueDays) {
		this.dueDays = dueDays;
	}

	public Integer getSupplierResponsePeriod() {
		return supplierResponsePeriod;
	}

	public void setSupplierResponsePeriod(Integer supplierResponsePeriod) {
		this.supplierResponsePeriod = supplierResponsePeriod;
	}

	public Integer getSupplierDisputePeriod() {
		return supplierDisputePeriod;
	}

	public void setSupplierDisputePeriod(Integer supplierDisputePeriod) {
		this.supplierDisputePeriod = supplierDisputePeriod;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public Boolean getOfflineDebitEnabled() {
		return offlineDebitEnabled;
	}

	public void setOfflineDebitEnabled(Boolean offlineDebitEnabled) {
		this.offlineDebitEnabled = offlineDebitEnabled;
	}

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public Boolean getRecoveryBasedOnCausalPart() {
		return recoveryBasedOnCausalPart;
	}

	public void setRecoveryBasedOnCausalPart(Boolean recoveryBasedOnCausalPart) {
		this.recoveryBasedOnCausalPart = recoveryBasedOnCausalPart;
	}

	public String getWhenToInitiateRecoveryClaim() {
		if(whenToInitiateRecoveryClaim==null){
			return AdminConstants.DO_NOT_AUTO_INITIATE;
		}
		return whenToInitiateRecoveryClaim;
	}

	public void setWhenToInitiateRecoveryClaim(String whenToInitiateRecoveryClaim) {
		this.whenToInitiateRecoveryClaim = whenToInitiateRecoveryClaim;
	}

    public String getRmaNumber() {
        return rmaNumber;
    }

    public void setRmaNumber(String rmaNumber) {
        this.rmaNumber = rmaNumber;
    }

	/**
	 * @return the shippingInstruction
	 */
	public String getShippingInstruction() {
		return shippingInstruction;
	}

	/**
	 * @param shippingInstruction the shippingInstruction to set
	 */
	public void setShippingInstruction(String shippingInstruction) {
		this.shippingInstruction = shippingInstruction;
	}
}
