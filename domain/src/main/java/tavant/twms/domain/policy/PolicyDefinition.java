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

import java.sql.Types;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
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

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.I18NPolicyTermsAndConditions;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.rules.RuleExecutionCallback;
import tavant.twms.domain.rules.RuleExecutionTemplate;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.ServiceProviderCertificationStatus;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.authz.infra.SecurityHelper;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.Duration;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class PolicyDefinition implements Policy, BusinessUnitAware,
		AuditableColumns,Comparable<PolicyDefinition> {

	private static class RuleExecutionCallbackImpl extends
			RuleExecutionCallback {
		private final PolicyDefinition policy;

		private Map<DomainRule, Map<Boolean, String>> results = new HashMap<DomainRule, Map<Boolean, String>>();

		private final Claim claim;

		private RuleExecutionCallbackImpl(PolicyDefinition policy,
				Map<DomainRule, Map<Boolean, String>> results, Claim claim) {
			this.policy = policy;
			this.results = results;
			this.claim = claim;
		}

		public Map<String, Object> getActionResultHolder() {
			return new HashMap<String, Object>();
		}

		public List<DomainRule> getRulesForExecution() {
			return new ArrayList<DomainRule>(this.policy
					.getApplicabilityTerms());
		}

		public Map<String, Object> getTransactionContext() {
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("claim", this.claim);
			context.put("policy", this.policy);
			return context;
		}

		public void setRuleEvaluationResult(
				Map<DomainRule, Map<Boolean, String>> results) {
			this.results.putAll(results);
		}
	}

	@Id
	@GeneratedValue(generator = "PolicyDefinition")
	@GenericGenerator(name = "PolicyDefinition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "POLICY_DEF_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	@Column(nullable = false)
	private String code;

	private String description;

	private boolean currentlyInactive;
	
	@Column(nullable = true, length = 4000)
	private String comments;

	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	// TODO: Need to introduce a not-null constraint here.
	private WarrantyType warrantyType;

	@Type(type = "org.hibernate.type.EnumType", parameters = {
			@Parameter(name = "enumClass", value = "tavant.twms.domain.orgmodel.ServiceProviderCertificationStatus"),
			@Parameter(name = "type", value = "" + Types.VARCHAR) })
	private ServiceProviderCertificationStatus certificationStatus;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "policy_for_serviceproviders", joinColumns = { @JoinColumn(name = "policy_defn") }, inverseJoinColumns = { @JoinColumn(name = "for_service_provider") })
	private Set<ServiceProvider> applicableServiceProviders = new HashSet<ServiceProvider>();

	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "policy_for_dealer_groups", joinColumns = { @JoinColumn(name = "policy_defn") }, inverseJoinColumns = { @JoinColumn(name = "for_dealer_groups") })
	private Set<DealerGroup> applicableDealerGroups = new HashSet<DealerGroup>();
	
	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinTable(name = "policy_def_applicability_terms")
	private Set<DomainRule> applicabilityTerms = new HashSet<DomainRule>();

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "duration.fromDate", column = @Column(name = "active_from", nullable = false)),
			@AttributeOverride(name = "duration.tillDate", column = @Column(name = "active_till", nullable = false)),
			@AttributeOverride(name = "availableByDefault", column = @Column(name = "avlbl_by_default")) })
	private Availability availability = new Availability();

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "serviceHoursCovered", column = @Column(name = "service_hrs_covered", nullable = true)),
			@AttributeOverride(name = "monthsCoveredFromShipment", column = @Column(name = "months_frm_shipment", nullable = true)),
			@AttributeOverride(name = "monthsCoveredFromDelivery", column = @Column(name = "months_frm_delivery", nullable = true)),
            @AttributeOverride(name = "monthsFromShipmentForEWP", column = @Column(name = "months_frm_shipment_ewp", nullable = true)),
            @AttributeOverride(name = "monthsFromDeliveryForEWP", column = @Column(name = "months_frm_delivery_ewp", nullable = true)),
            @AttributeOverride(name = "minMonthsFromDeliveryForEWP", column = @Column(name = "min_months_frm_delivery_ewp", nullable = true)),
	        @AttributeOverride(name = "monthsCoveredFromBuildDate", column = @Column(name = "months_from_build_date", nullable = true)),
	        @AttributeOverride(name = "monthsCoveredFromOriginalDeliveryDate", column = @Column(name = "months_frm_orgnl_delivery", nullable = true))})
	private CoverageTerms coverageTerms = new CoverageTerms();

	@Embedded
	@AttributeOverrides( { @AttributeOverride(name = "transferable", column = @Column(name = "transferable")) })
	private TransferDetails transferDetails = new TransferDetails();

	@ManyToMany
	private Set<Label> labels = new HashSet<Label>();

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger(PolicyDefinition.class);

	@Column(nullable = false)
	private Long priority;
	
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "for_policy_definition", nullable = false, updatable = false,insertable = true)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@IndexColumn(name = "list_index", nullable = false)
	private List<PolicyDefinitionAudit> policyDefinitionAudits = new ArrayList<PolicyDefinitionAudit>();

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	private Boolean buildDateApplicable;

	private Boolean invisibleFilingDr;
	
	@Transient
	private String termsAndConditions;

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "POLICY_DEFINITION", nullable = false)
	private List<I18NPolicyTermsAndConditions> i18NPolicyTermsAndConditions = new ArrayList<I18NPolicyTermsAndConditions>();
	
	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "POLICY", nullable = false)
	private List<PolicyFees> policyFees = new ArrayList<PolicyFees>();

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "POLICY_DEFINITION", nullable = false)
	private Set<ApplicableCustomerTypes> customerTypes = new HashSet<ApplicableCustomerTypes>();

	private Boolean isThirdPartyPolicy;

	private Boolean isPolicyApplicableForWr;
	
	private Boolean isPolicyForcedOnWr;

    @ManyToMany(fetch= FetchType.LAZY)
    @JoinTable(name = "policy_not_for_providers",
               joinColumns = { @JoinColumn(name = "policy_defn") },
                               inverseJoinColumns = { @JoinColumn(name = "for_service_provider") })
    private List<ServiceProvider> blackListedServiceProviders = new ArrayList<ServiceProvider>();

    private Boolean forInternalUsersOnly;

    private Boolean attachmentMandatory;

    @Column(name="noms_policy_option_code")
    private String nomsPolicyOptionCode;
    
    @Column(name="noms_tier_description")
    private String nomsTierDescription;
    
    public String getNomsPolicyOptionCode() {
		return nomsPolicyOptionCode;
	}

	public void setNomsPolicyOptionCode(String nomsPolicyOptionCode) {
		this.nomsPolicyOptionCode = nomsPolicyOptionCode;
	}

	public String getNomsTierDescription() {
		return nomsTierDescription;
	}

	public void setNomsTierDescription(String nomsTierDescription) {
		this.nomsTierDescription = nomsTierDescription;
	}

	public Boolean getAttachmentMandatory() {
    	if(attachmentMandatory == null)
    		return false;
		return attachmentMandatory;
	}

	public void setAttachmentMandatory(Boolean attachmentMandatory) {
		this.attachmentMandatory = attachmentMandatory;
	}

	public Boolean getForInternalUsersOnly() {
		if(forInternalUsersOnly == null)
			return false;
		return forInternalUsersOnly;
	}

	public void setForInternalUsersOnly(Boolean forInternalUsersOnly) {
		this.forInternalUsersOnly = forInternalUsersOnly;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

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

	public Availability getAvailability() {
		return this.availability;
	}

	public void setAvailability(Availability availableFor) {
		this.availability = availableFor;
	}

	public CoverageTerms getCoverageTerms() {
		return this.coverageTerms;
	}

	public void setCoverageTerms(CoverageTerms coverageTerms) {
		this.coverageTerms = coverageTerms;
	}

	public TransferDetails getTransferDetails() {
		return this.transferDetails;
	}

	public void setTransferDetails(TransferDetails transferDetails) {
		this.transferDetails = transferDetails;
	}

	public WarrantyType getWarrantyType() {
		return this.warrantyType;
	}

	public void setWarrantyType(WarrantyType warrantyType) {
		this.warrantyType = warrantyType;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCurrentlyInactive() {
		return this.currentlyInactive;
	}

	public void setCurrentlyInactive(boolean currentlyInactive) {
		this.currentlyInactive = currentlyInactive;
	}
	
	public boolean isAvailableByDefault() {
		return this.availability.isAvailableByDefault();
	}

	public boolean isTransferableByDefault() {
		return this.transferDetails.getTransferFee().isZero();
	}

	public boolean isApplicable(final Claim claim,
			RuleExecutionTemplate ruleExecutionTemplate) {
		final Map<DomainRule, Map<Boolean, String>> evaluationResults = new HashMap<DomainRule, Map<Boolean, String>>();
		final PolicyDefinition thisPolicy = this;
		RuleExecutionCallbackImpl ruleExecutionCallback = new RuleExecutionCallbackImpl(
				thisPolicy, evaluationResults, claim);
		ruleExecutionTemplate.executeRules(ruleExecutionCallback);
		if (!evaluationResults.isEmpty()) {
            boolean toReturn = true;
            for (Map.Entry<DomainRule, Map<Boolean, String>> entry : evaluationResults
					.entrySet()) {
				Map<Boolean, String> clmFailedMsgMap = entry.getValue();
				Boolean clmFailedCheck = clmFailedMsgMap.keySet().iterator()
						.next();
				if (!clmFailedCheck) {
                    toReturn=false;
				}
			}
            return toReturn;
        }else if(ruleExecutionCallback.getRulesForExecution().isEmpty()){
            return true;
        }
		return false;
	}

	public boolean isStillAvailable(InventoryItem inventoryItem)
			throws PolicyException {
		return isAvailable(inventoryItem, Clock.today());
	}

	public boolean isAvailable(InventoryItem inventoryItem,
			CalendarDate asOfDate) throws PolicyException {
		try {
			boolean hoursOnMachineWithinCoverage = isHoursOnMachineWithinCoverage(inventoryItem);
/*			boolean deliveryOnOrBeforeWarrantyStart = !inventoryItem
					.getDeliveryDate().isAfter(
							computeWarrantyEndDate(inventoryItem));*/
			return hoursOnMachineWithinCoverage
					&& warrantyPeriodFor(inventoryItem) != null;
		} catch (PolicyNotApplicableException e) {
			return false;
		}
	}

	public boolean isGWPolicyAvailable(InventoryItem inventoryItem,
			CalendarDate asOfDate) throws PolicyException {
		try {
			boolean deliveryOnOrBeforeWarrantyStart = !inventoryItem
					.getDeliveryDate().isAfter(
							computeWarrantyEndDate(inventoryItem));
			return deliveryOnOrBeforeWarrantyStart
					&& warrantyPeriodFor(inventoryItem).includes(asOfDate);
		} catch (PolicyNotApplicableException e) {
			return false;
		}
	}

	public boolean isHoursOnMachineWithinCoverage(InventoryItem inventoryItem) {
		return this.coverageTerms
				.isHoursOnMachineWithinCoverageLimit(inventoryItem);
	}

	public void addApplicabilityTerm(DomainRule newDomainRule) {
		this.applicabilityTerms.add(newDomainRule);
	}

	public void removeApplicabilityTerm(DomainRule aDomainRule) {
		this.applicabilityTerms.remove(aDomainRule);
	}

	public void activate() {
		this.currentlyInactive = false;
	}

	public void deactivate() {
		this.currentlyInactive = true;
	}

	public boolean isTransferable() {
		return this.transferDetails.isTransferable();
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Set<DomainRule> getApplicabilityTerms() {
		return this.applicabilityTerms;
	}

	public void setApplicabilityTerms(Set<DomainRule> applicabilityTerms) {
		this.applicabilityTerms = applicabilityTerms;
	}

	public boolean isCurrentlyAvailable() {
		if (isCurrentlyInactive()) {
			return false;
		} else {
			return getAvailability().getDuration().includes(Clock.today());
		}
	}

	public boolean covers(ClaimedItem claimedItem, Integer serviceHoursCovered)
			throws PolicyException {
		return this.coverageTerms.covers(claimedItem,
				computeWarrantyPeriod(claimedItem), serviceHoursCovered);
	}

	public boolean covers(Claim claimedItem, Integer serviceHoursCovered)
		throws PolicyException {
		return this.coverageTerms.covers(claimedItem.getClaimedItems().get(0),
		computeWarrantyPeriod(claimedItem), serviceHoursCovered);
	}
	
	public boolean covers(ClaimedItem claimedItem,
			CalendarDuration warrantyPeriod, Integer serviceHoursCovered) {
		return this.coverageTerms.covers(claimedItem, warrantyPeriod,
				serviceHoursCovered);
	}
	
	public boolean covers(Claim claimedItem,
			CalendarDuration warrantyPeriod, Integer serviceHoursCovered) {
		return this.coverageTerms.covers(claimedItem.getClaimedItems().get(0), warrantyPeriod,
				serviceHoursCovered);
	}

	public CalendarDuration warrantyPeriodFor(InventoryItem inventoryItem)
			throws PolicyException {
		if (!inventoryItem.isDelivered()) {
			throw new PolicyException(
					"Warranty end date cannot be computed without knowing delivery date.");
		}		
		if (!inventoryItem.isShipped()) {
			throw new PolicyException(
					"Warranty end date cannot be computed without knowing shipment date.");
		} 
		CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
		CalendarDate shipmentDate = inventoryItem.getShipmentDate();
		CalendarDate warrantyStartDate = deliveryDate;
		if(inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			if(deliveryDate.isAfter(shipmentDate.plusMonths(6))){
				warrantyStartDate = shipmentDate;
			}
		} else {
			CalendarDate cutOffDate = getCutOffDate(shipmentDate);
			if(deliveryDate.isAfter(cutOffDate)) {
				warrantyStartDate = cutOffDate;
			}
		}
		CalendarDate warrantyEndDate = computeWarrantyEndDate(inventoryItem);
        if (warrantyStartDate.isAfter(warrantyEndDate)) {
            return new CalendarDuration(warrantyStartDate,warrantyStartDate); // creating a dummy coverage of 1 day with INACTIVE status
		} else {
			return new CalendarDuration(warrantyStartDate, warrantyEndDate);
		}

	}
	
	private CalendarDate getCutOffDate(CalendarDate shipmentDate) {
		Integer monthsCoveredFromShipment = getCoverageTerms().getMonthsCoveredFromShipment();
		Integer monthsCoveredFromDelivery = getCoverageTerms().getMonthsCoveredFromDelivery();
		Integer stockWindowPeriod = monthsCoveredFromShipment - monthsCoveredFromDelivery;
		CalendarDate cutOffDate = shipmentDate.plusMonths(stockWindowPeriod);
		return cutOffDate;
	}
	
	public CalendarDuration warrantyPeriodFor(InventoryItem inventoryItem, CalendarDate installationDate)
			throws PolicyException {
		if (!inventoryItem.isDelivered()) {
			throw new PolicyException(
					"Warranty end date cannot be computed without knowing delivery date.");
		}
		if (!inventoryItem.isShipped()) {
			throw new PolicyException(
					"Warranty end date cannot be computed without knowing shipment date.");
		} 
		CalendarDate deliveryDate = null;
		if(installationDate != null){
			deliveryDate = installationDate;
		}else{
			deliveryDate = inventoryItem.getDeliveryDate();
		}
		CalendarDate shipmentDate = inventoryItem.getShipmentDate();
		CalendarDate warrantyStartDate = deliveryDate;
		if(inventoryItem.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGEMEA)) {
			if(deliveryDate.isAfter(shipmentDate.plusMonths(6))){
				warrantyStartDate = shipmentDate;
			}
		} else {
			CalendarDate cutOffDate = getCutOffDate(shipmentDate);
			if(deliveryDate.isAfter(cutOffDate)) {
				warrantyStartDate = cutOffDate;
			}
		}
		CalendarDate warrantyEndDate = computeWarrantyEndDate(inventoryItem, installationDate);
        if (warrantyStartDate.isAfter(warrantyEndDate)) {
            return new CalendarDuration(warrantyStartDate,warrantyStartDate);
		} else {
			return new CalendarDuration(warrantyStartDate, warrantyEndDate);
		}

	}


	public CalendarDate computeWarrantyEndDate(
			InventoryItem aDeliveredInventoryItem) throws PolicyException {
		return this.coverageTerms.warrantyEndDate(aDeliveredInventoryItem);
	}
	
	public CalendarDate computeWarrantyEndDate(
			InventoryItem aDeliveredInventoryItem, CalendarDate installationDate) throws PolicyException {
		return this.coverageTerms.warrantyEndDate(aDeliveredInventoryItem,installationDate);
	}

	public CalendarDuration getWarrantyPeriod() {
		throw new UnsupportedOperationException(
				"Warranty period can be determined ONLY if the policy is registered");
	}

	public boolean isStillAvailableFor(InventoryItem inventoryItem)
			throws PolicyException {
		return isAvailable(inventoryItem, Clock.today());
	}

	/**
	 * @param claimedItem
	 * @return
	 */
	protected CalendarDuration computeWarrantyPeriod(ClaimedItem claimedItem)
			throws PolicyException {
		if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class,
				claimedItem.getClaim()) && claimedItem.getClaim().getInstallationDate()!=null) {
			return new CalendarDuration(claimedItem.getClaim().getInstallationDate(),
					claimedItem.getClaim().getInstallationDate().plusMonths(getCoverageTerms()
							.getMonthsCoveredFromDelivery()));
			}
		ItemReference itemReference = claimedItem.getItemReference();

		if (itemReference.isSerialized()) {
			InventoryItem inventoryItem = itemReference
					.getReferredInventoryItem();
			CalendarDate dateOfShipment = inventoryItem.getShipmentDate();
			if (inventoryItem.isDelivered()) {
				return warrantyPeriodFor(inventoryItem);
			} else {
				return new CalendarDuration(dateOfShipment, dateOfShipment
						.plusMonths(getCoverageTerms()
								.getMonthsCoveredFromShipment()));
			}
		}		
		return warrantyPeriodBasedOnInstallationDate(claimedItem);
	}
	
	protected CalendarDuration computeWarrantyPeriod(Claim claim)
			throws PolicyException {
		if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class,
				claim) && claim.getInstallationDate()!=null) {
			return new CalendarDuration(claim.getInstallationDate(),
					claim.getInstallationDate().plusMonths(getCoverageTerms()
							.getMonthsCoveredFromDelivery()));
			}
		ItemReference itemReference = claim.getPartItemReference();

		if (itemReference.isSerialized()) {
			InventoryItem inventoryItem = itemReference
					.getReferredInventoryItem();
			CalendarDate dateOfShipment = inventoryItem.getShipmentDate();
			if (inventoryItem.isDelivered()) {
				return warrantyPeriodFor(inventoryItem);
			} else {
				return new CalendarDuration(dateOfShipment, dateOfShipment
						.plusMonths(getCoverageTerms()
								.getMonthsCoveredFromShipment()));
			}
		}
		return warrantyPeriodBasedOnDate(claim);
	}


	/**
	 * @param claimedItem
	 * @return
	 */
	protected CalendarDuration warrantyPeriodBasedOnInstallationDate(
			ClaimedItem claimedItem) {
		CalendarDate warrantyStartDate;
		CalendarDate warrantyEndDate;
		warrantyStartDate = claimedItem.getClaim().getInstallationDate();
		// This is for Non Serializable Machine Claim
		if (warrantyStartDate == null && !claimedItem.getClaim().getItemReference().isSerialized()) {
			warrantyStartDate = claimedItem.getClaim().getPurchaseDate();
		}
		warrantyEndDate = warrantyStartDate.plus(
				Duration.months(this.coverageTerms
						.getMonthsCoveredFromDelivery())).previousDay();
		return new CalendarDuration(warrantyStartDate, warrantyEndDate);
	}

	protected CalendarDuration warrantyPeriodBasedOnDate(
			Claim claim) {
		CalendarDate warrantyStartDate;
		CalendarDate warrantyEndDate;
		warrantyStartDate = claim.getInstallationDate()!=null? claim.getInstallationDate():claim.getPurchaseDate();
		warrantyEndDate = warrantyStartDate.plus(
				Duration.months(this.coverageTerms
						.getMonthsCoveredFromDelivery())).previousDay();
		return new CalendarDuration(warrantyStartDate, warrantyEndDate);
	}
	public PolicyDefinition getPolicyDefinition() {
		return this;
	}

	public Set<Label> getLabels() {
		return this.labels;
	}

	public void setLabels(Set<Label> labels) {
		this.labels = labels;
	}

	public boolean isInActive() {
		CalendarDuration duration = getAvailability().getDuration();
		CalendarDate date = Clock.today();
		if (this.currentlyInactive || duration.getFromDate().isAfter(date)
				|| duration.getTillDate().isBefore(date)) {
			return true;
		}
		return false;
	}

	public Long getPriority() {
		return this.priority;
	}

	public void setPriority(Long priority) {
		this.priority = priority;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public Boolean getBuildDateApplicable() {
		return buildDateApplicable;
	}

	public void setBuildDateApplicable(Boolean buildDateApplicable) {
		this.buildDateApplicable = buildDateApplicable;
	}

	public Boolean getInvisibleFilingDr() {
		return invisibleFilingDr;
	}

	public void setInvisibleFilingDr(Boolean invisibleFilingDr) {
		this.invisibleFilingDr = invisibleFilingDr;
	}

	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public List<I18NPolicyTermsAndConditions> getI18NPolicyTermsAndConditions() {
		return i18NPolicyTermsAndConditions;
	}

	public void setI18NPolicyTermsAndConditions(
			List<I18NPolicyTermsAndConditions> policyTermsAndConditions) {
		i18NPolicyTermsAndConditions = policyTermsAndConditions;
	}

	public String getTermsAndConditions() {
		String termsAndCondition_locale = "";
		for (I18NPolicyTermsAndConditions i18NPolicyTermsAndCondition : this.i18NPolicyTermsAndConditions) {
			if (i18NPolicyTermsAndCondition != null
					&& i18NPolicyTermsAndCondition.getLocale() != null
					&& i18NPolicyTermsAndCondition.getLocale()
							.equalsIgnoreCase(
									new SecurityHelper().getLoggedInUser()
											.getLocale().toString())
					&& i18NPolicyTermsAndCondition.getTermsAndConditions() != null) {
				termsAndCondition_locale = i18NPolicyTermsAndCondition
						.getTermsAndConditions();
				break;
			} else if (i18NPolicyTermsAndCondition != null
					&& i18NPolicyTermsAndCondition.getLocale() != null
					&& i18NPolicyTermsAndCondition.getLocale()
							.equalsIgnoreCase("en_US")) {
				termsAndCondition_locale = i18NPolicyTermsAndCondition
						.getTermsAndConditions();
			}
		}

		return termsAndCondition_locale;
	}

	public Set<ApplicableCustomerTypes> getCustomertypes() {
		return customerTypes;
	}

	public void setCustomertypes(Set<ApplicableCustomerTypes> customertypes) {
		this.customerTypes = customertypes;
	}

	public Boolean getIsThirdPartyPolicy() {
		return isThirdPartyPolicy;
	}

	public void setIsThirdPartyPolicy(Boolean isThirdPartyPolicy) {
		this.isThirdPartyPolicy = isThirdPartyPolicy;
	}

	public Boolean getIsPolicyApplicableForWr() {
		if(isPolicyApplicableForWr==null){
			return Boolean.FALSE;
		}
		return isPolicyApplicableForWr;
	}

	public void setIsPolicyApplicableForWr(Boolean isPolicyApplicableForWr) {
		this.isPolicyApplicableForWr = isPolicyApplicableForWr;
	}

	public List<PolicyFees> getPolicyFees() {
		return policyFees;
	}

	public void setPolicyFees(List<PolicyFees> policyFees) {
		this.policyFees = policyFees;
	}
	
	public List<PolicyFees> getRegistrationFees() {
		List<PolicyFees> registrationFees = new ArrayList<PolicyFees>();
		for(PolicyFees policyFees:this.policyFees)
        {
        	if(!policyFees.getIsTransferable())
        	{
        		registrationFees.add(policyFees);
        	}
        	
        }
		return registrationFees;
	}
	
	public List<PolicyFees> getTransferFees() {
		List<PolicyFees> transferFees = new ArrayList<PolicyFees>();
		for(PolicyFees policyFees:this.policyFees)
        {
        	if(policyFees.getIsTransferable())
        	{
        		transferFees.add(policyFees);
        	}
        	
        }
		return transferFees;
	}

	public Money getTransferFee(String currency){
		for(PolicyFees fee: getTransferFees()){
			if(fee.getPolicyFee()!=null && fee.getPolicyFee().breachEncapsulationOfCurrency().getCurrencyCode().equalsIgnoreCase(currency)){
				return fee.getPolicyFee();
			}
		}
		return Money.valueOf(0.0,Currency.getInstance(currency));
	}

	public List<PolicyDefinitionAudit> getPolicyDefinitionAudits() {
		return policyDefinitionAudits;
	}

	public void setPolicyDefinitionAudits(
			List<PolicyDefinitionAudit> policyDefinitionAudits) {
		this.policyDefinitionAudits = policyDefinitionAudits;		
	}

    public List<ServiceProvider> getBlackListedServiceProviders() {
        return blackListedServiceProviders;
    }

    public void setBlackListedServiceProviders(List<ServiceProvider> blackListedServiceProviders) {
        this.blackListedServiceProviders = blackListedServiceProviders;
    }

    public boolean isServiceProviderBlackListed(ServiceProvider serviceProvider){
        boolean isExisting=false;
        for (ServiceProvider blackListedServiceProvider : blackListedServiceProviders) {
            if(blackListedServiceProvider.getId().longValue()==serviceProvider.getId().longValue()){
                isExisting=true;
                break;
            }
        }
        return isExisting;
    }

	/**
	 * @param certificationStatus the certificationStatus to set
	 */
	public void setCertificationStatus(ServiceProviderCertificationStatus certificationStatus) {
		this.certificationStatus = certificationStatus;
	}

	/**
	 * @return the certificationStatus
	 */
	public ServiceProviderCertificationStatus getCertificationStatus() {
		return certificationStatus;
	}

	/**
	 * @param applicableServiceProviders the applicableServiceProviders to set
	 */
	public void setApplicableServiceProviders(
			Set<ServiceProvider> applicableServiceProviders) {
		this.applicableServiceProviders = applicableServiceProviders;
	}

	/**
	 * @return the applicableServiceProviders
	 */
	public Set<ServiceProvider> getApplicableServiceProviders() {
		return applicableServiceProviders;
	}

	/**
	 * @param applicableDealerGroups the applicableDealerGroups to set
	 */
	public void setApplicableDealerGroups(Set<DealerGroup> applicableDealerGroups) {
		this.applicableDealerGroups = applicableDealerGroups;
	}

	/**
	 * @return the applicableDealerGroups
	 */
	public Set<DealerGroup> getApplicableDealerGroups() {
		return applicableDealerGroups;
	}

	public void setIsPolicyForcedOnWr(Boolean isPolicyForcedOnWr) {
		this.isPolicyForcedOnWr = isPolicyForcedOnWr;
	}

	public Boolean getIsPolicyForcedOnWr() {
		return isPolicyForcedOnWr;
	}
	
    public int compareTo(PolicyDefinition other) {
    	if (other == null) {
            return 1;
        }
    	int codeCompare = this.getCode().compareTo(other.getCode());
    	return codeCompare;
    }
    
	@Override
	public boolean equals(Object policyDefinition) {
		if (policyDefinition!=null&&this.getId().equals(((PolicyDefinition) policyDefinition).getId())) {
			return true;
		} else {
			return false;
		}
	}
	
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}
	
}
