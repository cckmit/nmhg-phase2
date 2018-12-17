/*
s *   Copyright (c)2006 Tavant Technologies
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.alarmcode.AlarmCode;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.AcceptanceReason;
import tavant.twms.domain.common.AcceptanceReasonForCP;
import tavant.twms.domain.common.AccountabilityCode;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.ClaimCompetitorModel;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.common.ManufacturingSiteInventory;
import tavant.twms.domain.common.PropertiesWithNestedCurrencyFields;
import tavant.twms.domain.common.PutOnHoldReason;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.common.RequestInfoFromUser;
import tavant.twms.domain.common.SellingEntity;
import tavant.twms.domain.common.SmrReason;
import tavant.twms.domain.common.SourceWarehouse;
import tavant.twms.domain.common.Suppliers;
import tavant.twms.domain.inventory.InventoryClass;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.loa.LimitOfAuthorityScheme;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.ThirdParty;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.domain.stateMandates.StateMandates;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.Auditable;
import tavant.twms.security.SecurityHelper;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;


/**
 * Represents the a warranty Claim Entity.
 *
 * @author kamal.govindraj
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("none")
@PropertiesWithNestedCurrencyFields({"serviceInformation", "payment"})
@FilterDef(name = "bu_name", parameters = {@ParamDef(name = "name", type = "string")})
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public abstract class Claim implements Auditable, BusinessUnitAware {

    private static final Logger logger = Logger.getLogger(Claim.class);

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private int version;

    @Column(insertable = false, updatable = false)
    String type;

    private String clmTypeName;

    private String claimNumber;

    private String histClmNo;

    private Boolean serviceManagerRequest=Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "reason_for_service_mgr_req")
    private SmrReason reasonForServiceManagerRequest;

    private Boolean processedAutomatically;
    //if Part has been modified by receiver claim payment needs to be recalculated.
    private Boolean paymentRecalculationRequired = new Boolean(false);

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate filedOnDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private User filedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedOnDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private User lastUpdatedBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private ServiceProvider forDealer;

    private Integer noOfResubmits = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    private LimitOfAuthorityScheme loaScheme;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private OrganizationAddress servicingLocation;

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @AttributeOverrides({
            @AttributeOverride(name = "serialized", column = @Column(name = "equip_item_ref_szed")),
            @AttributeOverride(name = "unszdSlNo", column = @Column(name = "equip_item_invalidSlNo"))})
    @AssociationOverrides({
            @AssociationOverride(name = "referredItem", joinColumns = @JoinColumn(name = "equip_item_ref_item")),
            @AssociationOverride(name = "referredInventoryItem", joinColumns = @JoinColumn(name = "equip_item_ref_inv_item")),
            @AssociationOverride(name = "unserializedItem", joinColumns = @JoinColumn(name = "equip_item_ref_unszed_item")),
            @AssociationOverride(name = "model", joinColumns = @JoinColumn(name = "equip_model_ref_for_unszed"))})
    protected ItemReference partItemReference = new ItemReference();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "for_claim", nullable = false, updatable = false, insertable = true)
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @IndexColumn(name = "list_index", nullable = false)
    @Filter(name = "excludeMultiClaimAudit")
    private List<ClaimAudit> claimAudits = new ArrayList<ClaimAudit>();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cascade({CascadeType.ALL})
    private ClaimCompetitorModel claimCompetitorModel;

    @Transient
    private ClaimXMLConverter claimXMLConverter;

    @Transient
    private SecurityHelper securityHelper;

    private Boolean appealed;

    private Boolean reopened;

    private Boolean reopenRecoveryClaim = Boolean.FALSE;

    @Transient
    private boolean canDecideForPayment;
    
    @Transient
    private boolean isLateFeeEnabledFrom61to90days = Boolean.FALSE;
    
    @Transient
    private boolean isLateFeeEnabledFrom91to120days = Boolean.FALSE;

	@OneToMany(fetch = FetchType.LAZY)
    @Sort(type = SortType.NATURAL)
    @org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<ClaimedItem> claimedItems = new ArrayList<ClaimedItem>(10);

    private Boolean forMultipleItems = Boolean.FALSE;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    @Sort(type = SortType.NATURAL)
   /* @Filter(
            name = "excludeInactive",
            condition="d_active = 1"
    )*/
    private List<RecoveryClaim> recoveryClaims = new ArrayList<RecoveryClaim>();

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    private MatchReadInfo matchReadInfo;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "claim_additional_attributes")
    private List<ClaimAttributes> claimAdditionalAttributes = new ArrayList<ClaimAttributes>();

    @Column(nullable = true)
    private Boolean updated;

    @Transient
    private Boolean multiClaimMaintenance = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cascade({CascadeType.ALL})
    private Campaign campaign;

    private Boolean claimDenied = Boolean.FALSE;

    private Boolean prtShpNtrcvd = Boolean.FALSE;

    private Boolean commercialPolicy = Boolean.FALSE;

    private String source;

    @Transient
    private EventService eventService;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cascade({CascadeType.ALL})
    private SourceWarehouse sourceWarehouse;

    private String dateCode;

    private String decision;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    private ClaimAudit activeClaimAudit = new ClaimAudit();

    @Transient
    private Set<String> allowedActionsList;

    @Transient
    private boolean canUpdatePayment = false;
    
    @Transient
    private StateMandates stateMandate;

    /**
     * This is to identify if the particular claim is a foc  claim
     */
    private boolean foc;
    
    @Transient
    private boolean priceUpdated=false;

    private String focOrderNo;

    @Column
    private BigDecimal hoursOnPart;
    
    @Column
    private BigDecimal hoursOnTruck;
    
    @Transient
    private OrgService orgService;
    
    @Column(name="Authorization_Received")
    private Boolean cmsAuthCheck= Boolean.FALSE;
    
    public Boolean isCmsAuthCheck() {
		return cmsAuthCheck;
	}

	public void setCmsAuthCheck(Boolean cmsAuthCheck) {
		this.cmsAuthCheck = cmsAuthCheck;
	}


	private Boolean pendingRecovery = Boolean.FALSE;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fromDate", column = @Column(name = "labor_roundup_window_from_date")),
            @AttributeOverride(name = "tillDate", column = @Column(name = "labor_roundup_window_to_date"))})
    private CalendarDuration laborRoundupWindow = new CalendarDuration();

    @OneToOne(mappedBy = "warrantyClaim")
    @Cascade({CascadeType.ALL})
    /*@Filter(
            name = "excludeInactive",
            condition="d_active = 1"
    )*/
    private RecoveryInfo recoveryInfo;

    private Boolean failureReportPending = Boolean.FALSE;

    private Boolean bomUpdationNeeded = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY,optional = true)
    @Cascade( { CascadeType.ALL })
    private Suppliers suppliers;

    private Boolean supplierRecovery = Boolean.FALSE;
    
    @Column(name="CMS_NUMBER")
	private String cmsTicketNumber;
    
    
    private Boolean ncr=Boolean.FALSE;
    protected Boolean warrantyOrder=Boolean.FALSE;

    private Boolean manualReviewConfigured = Boolean.FALSE;
    
    @Column(name="NCR_WITH_30_DAYS")
    private Boolean ncrWith30Days=Boolean.FALSE;
    
    /**
     * Column added as part of SLMSPROD-970. If {@link #ncrWith30Days} is <code>true</code>, then
     * this column must have a value which denotes the class for which 30 Day NCR claim was filed
     */
    @ManyToOne
    @JoinColumn(name="INV_CLASS_30_DAY_NCR")
    private InventoryClass inventoryClassFor30DayNcr;
    
    
    public String getCmsTicketNumber() {
		return cmsTicketNumber;
	}

	public void setCmsTicketNumber(String cmsTicketNumber) {
		this.cmsTicketNumber = cmsTicketNumber;
	}
	private String policyCode;
	
    @Column(name="AUTH_NUMBER")
    private String authNumber;
    
    private String brand;
    
    private String partSerialNumber;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate coverageEndDate;

    private Integer hoursCovered;
    
	@Column(name = "COMPETITOR_MODEL_BRAND")
	private String competitorModelBrand;

	@Column(name = "COMP_MODEL_TRUCKSERIALNUM")
	private String competitorModelTruckSerialnumber;

	@Column(name = "COMP_MODEL_DESCRIPTION")
	private String competitorModelDescription;
	
	@Transient
	private BigDecimal lateFeeValueFrom61to90days;
	
	private Boolean emission;
	
	public BigDecimal getLateFeeValueFrom61to90days() {
		return lateFeeValueFrom61to90days;
	}

	public void setLateFeeValueFrom61to90days(BigDecimal lateFeeValueFrom61to90days) {
		this.lateFeeValueFrom61to90days = lateFeeValueFrom61to90days;
	}


	@Transient
	private BigDecimal lateFeeValueFrom91to120days;
	
    @Transient
    private String replacedPartsBrand;
    
    @Transient
    private String installedPartBrands;
    
    @Transient
    private String replacedPartNumbers;
    
    @Transient
    private String installedPartNumbers;
    
    
	public String getReplacedPartsBrand() {
		return replacedPartsBrand;
	}

	public void setReplacedPartsBrand(String replacedPartsBrand) {
		this.replacedPartsBrand = replacedPartsBrand;
	}

	public String getInstalledPartBrands() {
		return installedPartBrands;
	}

	public void setInstalledPartBrands(String installedPartBrands) {
		this.installedPartBrands = installedPartBrands;
	}

	public String getReplacedPartNumbers() {
		return replacedPartNumbers;
	}

	public void setReplacedPartNumbers(String replacedPartNumbers) {
		this.replacedPartNumbers = replacedPartNumbers;
	}

	public String getInstalledPartNumbers() {
		return installedPartNumbers;
	}

	public void setInstalledPartNumbers(String installedPartNumbers) {
		this.installedPartNumbers = installedPartNumbers;
	}

	public BigDecimal getLateFeeValueFrom91to120days() {
		return lateFeeValueFrom91to120days;
	}

	public void setLateFeeValueFrom91to120days(
			BigDecimal lateFeeValueFrom91to120days) {
		this.lateFeeValueFrom91to120days = lateFeeValueFrom91to120days;
	}

	public String getCompetitorModelBrand() {
		return competitorModelBrand;
	}

	public void setCompetitorModelBrand(String competitorModelBrand) {
		this.competitorModelBrand = competitorModelBrand;
	}

	public String getCompetitorModelTruckSerialnumber() {
		return competitorModelTruckSerialnumber;
	}

	public void setCompetitorModelTruckSerialnumber(
			String competitorModelTruckSerialnumber) {
		this.competitorModelTruckSerialnumber = competitorModelTruckSerialnumber;
	}

	public String getCompetitorModelDescription() {
		return competitorModelDescription;
	}

	public void setCompetitorModelDescription(String competitorModelDescription) {
		this.competitorModelDescription = competitorModelDescription;
	}


    
	public String getAuthNumber() {
		return authNumber;
	}

	public void setAuthNumber(String authNumber) {
		if(authNumber != null && !authNumber.equals("null")){
			this.authNumber = authNumber;
		}
	}

	public RecoveryInfo getRecoveryInfo() {
        return recoveryInfo;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setRecoveryInfo(RecoveryInfo recoveryInfo) {
        this.recoveryInfo = recoveryInfo;
    }

    public MatchReadInfo getMatchReadInfo() {
        return this.matchReadInfo;
    }

    public void setMatchReadInfo(MatchReadInfo matchReadInfo) {
        this.matchReadInfo = matchReadInfo;
    }

    public Boolean getForMultipleItems() {
        return this.forMultipleItems;
    }

    public void setForMultipleItems(Boolean forMultipleItems) {
        this.forMultipleItems = forMultipleItems;
    }

    public List<ClaimedItem> getClaimedItems() {
        return this.claimedItems;
    }

    public void setClaimedItems(List<ClaimedItem> claimedItems) {
        this.claimedItems = claimedItems;
    }

    public boolean addClaimedItem(ClaimedItem claimedItem) {
        claimedItem.setClaim(this);
        return this.claimedItems.add(claimedItem);
    }

    public boolean removeClaimedItem(ClaimedItem claimedItem) {
        claimedItem.setClaim(null);
        return this.claimedItems.remove(claimedItem);
    }

    public void setAttachments(List<Document> attachments) {
        this.getActiveClaimAudit().setAttachments(attachments);
    }

    public List<Document> getAttachments() {
        return this.getActiveClaimAudit().getAttachments();
    }

    /**
     * TODO : set does an add. Because only the last comment is editable
     *
     * @param processComments
     * @author kannan.ekanath
     */
    public void setProcessComments(UserComment processComments) {
        if (processComments != null) {
            this.getActiveClaimAudit().getUserProcessComments().add(processComments);
        }
    }
    
    public boolean isGoodWillPolicy()
    {
    	if(this.policyCode!=null)
    	{
    		if(this.policyCode.equalsIgnoreCase(WarrantyType.POLICY.getType()))
    			return true;
    		else
    			return false;
    	}
    	else
    	{
    		return false;
    	}  

    }

    public boolean isPriceUpdated() {
		return priceUpdated;
	}

	public void setPriceUpdated(boolean priceUpdated) {
		this.priceUpdated = priceUpdated;
	}

	public SortedSet<UserComment> getUserProcessComments() {
        return this.getActiveClaimAudit().getUserProcessComments();
    }

    public void setUserProcessComments(SortedSet<UserComment> userProcessComments) {
        this.getActiveClaimAudit().setUserProcessComments(userProcessComments);
    }

    public abstract ClaimType getType();

    public boolean isOfType(ClaimType type) {
        return getType().equals(type);
    }

    public String getClmTypeNameUsingBu(String businessUnit) {
    	Boolean isBuConfigAMER=false;
    	if(this.securityHelper != null&&this.securityHelper
				.getDefaultBusinessUnit()!=null){
		 isBuConfigAMER = this.securityHelper != null ? this.securityHelper
				.getDefaultBusinessUnit().getName()
				.equals(AdminConstants.NMHGAMER)
				: Boolean.FALSE;
    	}else{
    		isBuConfigAMER=businessUnit.equalsIgnoreCase(AdminConstants.NMHGAMER);
    	}
    	if(isBuConfigAMER && clmTypeName.equalsIgnoreCase("Machine")){
    		return "Unit";
    	} else {
            return clmTypeName;
    	}
    }
    public String getClmTypeName() {
		Boolean isBuConfigAMER = this.securityHelper != null ? this.securityHelper
				.getDefaultBusinessUnit().getName()
				.equals(AdminConstants.NMHGAMER)
				: Boolean.FALSE;
    	if(isBuConfigAMER && clmTypeName.equalsIgnoreCase("Machine")){
    		return "Unit";
    	} 
    	else if(isBuConfigAMER && clmTypeName.equalsIgnoreCase("Field Modification"))
    	{
            return "Field Product Improvement";
    	}
    	else
    	{
    		 return clmTypeName;
    	}
    }

    public void setClmTypeName(String clmTypeName) {
        this.clmTypeName = clmTypeName;
    }

    /**
     * @param forItem
     * @param failureDate
     * @param repairDate
     * @param repairStartDate
     */
    public Claim(InventoryItem forItem, CalendarDate failureDate, CalendarDate repairDate, CalendarDate repairStartDate) {
        this.addClaimedItem(new ClaimedItem(new ItemReference(forItem)));
        this.getActiveClaimAudit().setFailureDate(failureDate);
        this.getActiveClaimAudit().setRepairDate(repairDate);
        this.getActiveClaimAudit().setRepairStartDate(repairStartDate);
    }

    public Claim() {
    }

    public CalendarDate getFiledOnDate() {
        return this.filedOnDate;
    }

    public void setFiledOnDate(CalendarDate claimDate) {
        this.filedOnDate = claimDate;
    }

    public Date getLastUpdatedOnDate() {
        return this.lastUpdatedOnDate;
    }

    public void setLastUpdatedOnDate(Date lastUpdatedOnDate) {
        this.lastUpdatedOnDate = lastUpdatedOnDate;
    }

    public CalendarDate getFailureDate() {
        return this.getActiveClaimAudit().getFailureDate();
    }

    public void setFailureDate(CalendarDate failureDate) {
        this.getActiveClaimAudit().setFailureDate(failureDate);
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

    public CalendarDate getRepairDate() {
        return this.getActiveClaimAudit().getRepairDate();
    }
    
    public CalendarDate getRepairStartDate() {
        return this.getActiveClaimAudit().getRepairStartDate();
    }

    public void setRepairDate(CalendarDate repairDate) {
        this.getActiveClaimAudit().setRepairDate(repairDate);
    }
    public void setRepairStartDate(CalendarDate repairStartDate) {
        this.getActiveClaimAudit().setRepairStartDate(repairStartDate);
    }

    public Boolean isPaymentRecalculationRequired() {
        return paymentRecalculationRequired;
    }

    public void setPaymentRecalculationRequired(Boolean paymentRecalculationRequired) {
        this.paymentRecalculationRequired = paymentRecalculationRequired;
    }

    public boolean isReopenedOrAppealedClaim() {
        return ClaimState.APPEALED.equals(this.getState()) || ClaimState.REOPENED.equals(this.getState()) ||
                Boolean.TRUE.equals(this.getAppealed()) || Boolean.TRUE.equals(this.getReopened());
    }

    public ClaimState unMarshallClaimForClaimState(ClaimAudit claimAudit) {
//        Claim claim = (Claim) this.claimXMLConverter.convertXMLToObject(claimAudit.getPreviousClaimSnapshotAsString());
        return claimAudit.getPreviousState();
    }

    public boolean isAcceptedOrNotReopenedOrAppealedClaim() {
        return !(ClaimState.APPEALED.equals(this.getState()) || ClaimState.REOPENED.equals(this.getState()) ||
                Boolean.TRUE.equals(this.getAppealed()) || Boolean.TRUE.equals(this.getReopened())) ||
                (ClaimState.ACCEPTED.equals(unMarshallClaimForClaimState(this.getLatestAudit())));
    }

    public boolean canPolicyBeComputed() {
        List<ClaimedItem> claimedItems = getClaimedItems();

        boolean inventoryOrItemKnown = claimedItems.size() > 0;
        boolean failureDateKnown = getFailureDate() != null;
        boolean installationDateNotRequired = inventoryOrItemKnown
                && claimedItems.get(0).getItemReference().isSerialized();
        boolean installationDateKnown = getInstallationDate() != null;

        // This is for Non Serializable Machine Claim
        boolean purchaseDateKnown = (!claimedItems.get(0).getItemReference().isSerialized()) && getPurchaseDate() != null;

        if (inventoryOrItemKnown && failureDateKnown
                && (installationDateNotRequired || installationDateKnown || purchaseDateKnown)) {
            // basic checks passed. now we need to check for each individual
            // claimed item.
            for (ClaimedItem claimedItem : claimedItems) {
                if (!canPolicyBeComputedForClaimedItem(claimedItem)) {
                    return false; // check failed for a claimed item. hence
                    // short-circuit.
                }
            }

            return true; // all claimed items passed the check.
        }

        return false; // basic checks failed.
    }

    public abstract boolean canPolicyBeComputedForClaimedItem(ClaimedItem claimedItem);

    public ClaimState getState() {
        return this.getActiveClaimAudit().getState();
    }

    /**
     * @param state the state to set
     */
    public void setState(ClaimState state) {
        if (this.getState() != null && this.getState().equals(state) && ClaimState.PENDING_PAYMENT_SUBMISSION.equals(state)) {
            throw new RuntimeException("Claim State is already in PENDING_PAYMENT_SUBMISSION");
        }

        ClaimAudit activeClaimAudit = this.getActiveClaimAudit();
        if (this.getState() != null && ((!isClaimStateSame(state)) || (this.updated != null && this.updated))
                && !ClaimState.DRAFT_DELETED.getState().equals(state.getState())) {

            Payment payment = activeClaimAudit.getPayment();

            // state has changed create an audit entry
            if (ClaimState.SUBMITTED.equals(state) || ClaimState.SERVICE_MANAGER_REVIEW.equals(state) ||
                    ClaimState.SERVICE_MANAGER_RESPONSE.equals(state)) {
                payment.setClaimedAmount(payment.getTotalAmount());
            }
            if (this.claimXMLConverter != null && this.securityHelper != null) {

                ClaimAudit audit = ClaimAudit.replicateClaimAudit(activeClaimAudit);

                /* audit.setPreviousClaimSnapshotAsString(this.claimXMLConverter
                .convertObjectToXML(this));*/
                audit.setPreviousState(state);
                audit.setInternal(false);
                if (isInternalCommentToBeSet(state)) {
                    audit.setInternalComments(this.getInternalComment());
                }
                if (isExternalCommentToBeSet(state)) {
                    if (this.getExternalComment() != null)
                        audit.setExternalComments(this.getExternalComment());
                    else
                        audit.setExternalComments(this.getInternalComment());
                }
                audit.setPartReturnCommentsToDealer(this.getPartReturnCommentsToDealer());
                audit.setDecision(this.getDecision());
                audit.setUpdatedBy(getLoggedInUser(state));
                Clock.setDefaultTimeZone(TimeZone.getDefault());
                audit.setUpdatedOn(Clock.now());
                audit.setUpdatedTime(new Date());
                audit.setPreviousClaimSnapshot(this);
                audit.setForClaim(this);
                if (this.getMultiClaimMaintenance())
                    audit.setMultiClaimMaintenance(true);
               // audit.setPayment(payment.clone());
                
                //425 changes, related to late fee and deductible, both should not apply on requested amount
                Money zero=Money.valueOf(BigDecimal.ZERO, this.getCurrencyForCalculation());
            	Money lateFee=Money.valueOf(BigDecimal.ZERO, this.getCurrencyForCalculation());
            	Money acceptedLateFee=Money.valueOf(BigDecimal.ZERO, this.getCurrencyForCalculation());
				Money stateMandateLateFee=Money.valueOf(BigDecimal.ZERO, this.getCurrencyForCalculation());
            	Money deductibleAmount=Money.valueOf(BigDecimal.ZERO, this.getCurrencyForCalculation());
            	Money totalAmount=Money.valueOf(BigDecimal.ZERO, this.getCurrencyForCalculation());
            	Money totalBaseAmount=Money.valueOf(BigDecimal.ZERO, this.getCurrencyForCalculation());
            	Money acceptedAmount=Money.valueOf(BigDecimal.ZERO, this.getCurrencyForCalculation());
            	Money stateMandateAmount=Money.valueOf(BigDecimal.ZERO, this.getCurrencyForCalculation());   
            	Money cpWithoutLateFee=Money.valueOf(BigDecimal.ZERO, this.getCurrencyForCalculation());  
				LineItemGroup lateFees=null;			
				LineItemGroup totalClaim=null;    
				GlobalConfiguration.getInstance().setBaseCurrency(this.getCurrencyForCalculation());				
				if (state.equals(ClaimState.SUBMITTED)
						|| state.equals(ClaimState.SERVICE_MANAGER_REVIEW)
						|| state.equals(ClaimState.SERVICE_MANAGER_RESPONSE))						
                {
				
                	Payment latestPayment=activeClaimAudit.getPayment().clone();
                	for(LineItemGroup lineItemGroup:latestPayment.getLineItemGroups())
                	{
                		if(lineItemGroup.getName().equals(Section.LATE_FEE))
                		{                			
                			lateFee=this.getPayment().getLateFee();
    						acceptedLateFee=this.getPayment().getAcceptedLateFee();
    						stateMandateLateFee=this.getPayment().getStateMandateLateFee();	    					
    						lineItemGroup.setBaseAmount(lateFee);
    						lineItemGroup.setGroupTotal(lateFee);
    						lineItemGroup.setAcceptedTotal(acceptedLateFee);
    						lineItemGroup.setStateMandate(this, stateMandateLateFee, Section.LATE_FEE);
    						lateFees=lineItemGroup;
    						
                		}              
                		if(lineItemGroup.getName().equals(Section.DEDUCTIBLE))
                		{
                			deductibleAmount=this.getPayment().getDeductibleAmount();                  			
                			lineItemGroup.setBaseAmount(deductibleAmount);
    						lineItemGroup.setGroupTotal(deductibleAmount);
    						lineItemGroup.setAcceptedTotal(deductibleAmount);
    						lineItemGroup.setStateMandate(this, deductibleAmount, Section.DEDUCTIBLE);
                		}
                		if(lineItemGroup.getName().equals(Section.TOTAL_CLAIM))
                		{                			
                			totalAmount=lineItemGroup.getGroupTotal();
                			totalBaseAmount=lineItemGroup.getBaseAmount();
                			acceptedAmount=lineItemGroup.getAcceptedTotal();
        					stateMandateAmount=lineItemGroup.getGroupTotalStateMandateAmount();
        					totalClaim=lineItemGroup;
                		}                		
                	}
                	if (deductibleAmount == null) {
						deductibleAmount = zero;
					}
                	totalAmount=totalAmount.plus(lateFee).minus(deductibleAmount);
                	totalBaseAmount=totalBaseAmount.plus(lateFee).minus(deductibleAmount);
                	acceptedAmount=acceptedAmount.plus(acceptedLateFee).minus(deductibleAmount);
                	stateMandateAmount=stateMandateAmount.plus(stateMandateLateFee).minus(deductibleAmount);                	
                	totalClaim.setGroupTotal(totalAmount);
                	totalClaim.setBaseAmount(totalBaseAmount);   
                	totalClaim.setAcceptedTotal(acceptedAmount);
                	totalClaim.setGroupTotalStateMandateAmount(stateMandateAmount);                	
                	if(!latestPayment.isFlatAmountApplied())
                	{
                		if(totalClaim.getGroupTotal().breachEncapsulationOfAmount().intValue()!=0)
                			totalClaim.setPercentageAcceptance(totalClaim.getAcceptedTotal().breachEncapsulationOfAmount().multiply(new BigDecimal(100)).divide(totalClaim.getGroupTotal().breachEncapsulationOfAmount(),RoundingMode.HALF_UP));
                	}                	
                	latestPayment.setAmountSelected(totalClaim,this);	
      			  	totalClaim.setAcceptedTotalForCpOfTotalClaimSection(this);
      			   // totalClaim.setAcceptedCpTotal(totalClaim.getAcceptedTotalForCp());
      			  	//Chnages to exclude late fee from claim at cost
                	cpWithoutLateFee=totalClaim.getAcceptedTotalForCp();
					if (latestPayment.getTotalAcceptStateMdtChkbox() != null
							&& latestPayment.getTotalAcceptStateMdtChkbox().equals(
									true)) {
						cpWithoutLateFee = cpWithoutLateFee
								.minus(stateMandateLateFee);
					} else {
						cpWithoutLateFee = cpWithoutLateFee
								.minus(acceptedLateFee);
					}
      			    totalClaim.setAcceptedCpTotal(cpWithoutLateFee);
      			    totalClaim.setAcceptedForCostPriceAfterLateFee(this);
      			    //End
      			    latestPayment.setTotalCreditAmount();
      	            this.setDisbursedAmount();
                	payment.setTotalAmount(totalAmount);
                	payment.setClaimedAmount(totalAmount);
                	activeClaimAudit.setPayment(latestPayment);        	         	
                	
        		}
				
				
				
				if (state.equals(ClaimState.EXTERNAL_REPLIES)) {
					Payment auditPayment = payment.clone();		
					for (LineItemGroup lineItemGroup : auditPayment
							.getLineItemGroups()) {

						if (lineItemGroup.getName().equals(Section.LATE_FEE)) {
							lateFee = this.getPayment().getLateFee();
							acceptedLateFee = this.getPayment()
									.getAcceptedLateFee();
							stateMandateLateFee = this.getPayment()
									.getStateMandateLateFee();
							lineItemGroup.setBaseAmount(zero);
							lineItemGroup.setGroupTotal(zero);
							lineItemGroup.setAcceptedTotal(zero);
							lineItemGroup.setStateMandate(this, zero,
									Section.LATE_FEE);
							lateFees = lineItemGroup;

						}
						if (lineItemGroup.getName().equals(Section.DEDUCTIBLE)) {
							deductibleAmount = this.getPayment()
									.getDeductibleAmount();
							lineItemGroup.setBaseAmount(zero);
							lineItemGroup.setGroupTotal(zero);
							lineItemGroup.setAcceptedTotal(zero);
							lineItemGroup.setStateMandate(this, zero,
									Section.DEDUCTIBLE);
						}
						if (lineItemGroup.getName().equals(Section.TOTAL_CLAIM)) {
							totalAmount = lineItemGroup.getGroupTotal();
							totalBaseAmount = lineItemGroup.getBaseAmount();
							acceptedAmount = lineItemGroup.getAcceptedTotal();
							stateMandateAmount = lineItemGroup
									.getGroupTotalStateMandateAmount();
							totalClaim = lineItemGroup;
						}

					}
					if (deductibleAmount == null) {
						deductibleAmount = zero;
					}
					totalAmount = totalAmount.minus(lateFee).plus(
							deductibleAmount);
					totalBaseAmount = totalBaseAmount.minus(lateFee).plus(
							deductibleAmount);
					acceptedAmount = acceptedAmount.minus(acceptedLateFee)
							.plus(deductibleAmount);
					stateMandateAmount = stateMandateAmount.minus(
							stateMandateLateFee).plus(deductibleAmount);
					totalClaim.setGroupTotal(totalAmount);
					totalClaim.setBaseAmount(totalBaseAmount);
					totalClaim.setAcceptedTotal(acceptedAmount);
					totalClaim.setGroupTotalStateMandateAmount(stateMandateAmount);					
                	if(!auditPayment.isFlatAmountApplied())
                	{
                		if(totalClaim.getGroupTotal().breachEncapsulationOfAmount().intValue()!=0)
                			totalClaim.setPercentageAcceptance(totalClaim.getAcceptedTotal().breachEncapsulationOfAmount().multiply(new BigDecimal(100)).divide(totalClaim.getGroupTotal().breachEncapsulationOfAmount(),RoundingMode.HALF_UP));
                	}                	
                	auditPayment.setAmountSelected(totalClaim,this);	
                	totalClaim.setAcceptedTotalForCpOfTotalClaimSection(this);
                	totalClaim.setAcceptedCpTotal(totalClaim.getAcceptedTotalForCp());
                	auditPayment.setTotalCreditAmount();
      	            this.setDisbursedAmount();
					auditPayment.setTotalAmount(acceptedAmount);

					audit.setPayment(auditPayment);

				} else {
					audit.setPayment(payment.clone());
				}
				GlobalConfiguration.getInstance().setBaseCurrency(Currency.getInstance("USD"));	
                //Added the else condition so that we do not create duplicate credit memo
                //if claim is reopened and accepted without any change.
                if (!isClaimClosed(state)) {
                    audit.getPayment().setActiveCreditMemo(null);
                } else if (!payment.isPaymentToBeMade()) {
                    audit.getPayment().setActiveCreditMemo(null);
                }
                getClaimAudits().add(audit);
                //TSA-903 This is the final fix
                if (state.equals(ClaimState.DENIED)) {
                    this.setClaimDenied(Boolean.TRUE);
                } else if (state.equals(ClaimState.ACCEPTED)) {
                    this.setClaimDenied(Boolean.FALSE);
                }
                //E-mail Notification merge Start
                //Create an event only for claim states different than Draft state and Draft states of FOC claims
                //as in that case we need notification.
                if (!state.getState().equalsIgnoreCase(ClaimState.DRAFT.getState()) && !foc) {
                    //create an event to be taken care of
                    createEvent("claim", state.name(), this.getId());
                }
                //E-mail Notification merge End
            } else {
                logger.error("Unable to create audit entry, dependent service missing");
            }
        }
        if (this.foc) {
            createEvent("claim", state.name(), this.getId());
        }
        activeClaimAudit.setUpdatedTime(new Date());
        activeClaimAudit.setState(state);
    }


    /**
     * This method calls the event service and sets the event appropriately. This method is the only connection
     * between events and claims. for every new event to be generated for a claim state we should add a check
     * here appropriately
     *
     * @param entityName
     * @param eventName
     * @param entityId
     */
    private void createEvent(String entityName, String eventName, Long entityId) {
        final String claimState;

        if (ClaimState.DRAFT.name().toString().equalsIgnoreCase(eventName) && foc) {
            claimState = EventState.WAITING_FOR_LABOR;
        } else if (ClaimState.CP_REVIEW.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.CP_REVIEW;
        } else if (ClaimState.CP_TRANSFER.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.CP_TRANSFER;
        } else if (ClaimState.PROCESSOR_REVIEW.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.CLAIM_PROCESSOR_REVIEW_STATE;
        } else if (ClaimState.SERVICE_MANAGER_REVIEW.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.SERVICE_MANAGER_REVIEW;
        } else if (ClaimState.SERVICE_MANAGER_RESPONSE.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.SERVICE_MANAGER_RESPONSE;
        } else if (ClaimState.FORWARDED.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.CLAIM_FORWARDED;
        } else if (ClaimState.TRANSFERRED.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.CLAIM_TRANSFERED;
        } else if (ClaimState.ADVICE_REQUEST.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.ADVICE_REQUEST;
        } else if (ClaimState.REPLIES.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.CLAIM_REPLIES;
        } else if (ClaimState.WAITING_FOR_PART_RETURNS.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.WAITING_FOR_PART_RETURNS;
        }
        /*else if(ClaimState.REJECTED_PART_RETURN.name().toString().equalsIgnoreCase(eventName))
          {
              claimState = EventState.REJECTED_PART_RETURN;
          }*/
        else if (ClaimState.REACCEPTED.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.CLAIM_REACCEPTED;
        } else if (ClaimState.ACCEPTED_AND_CLOSED.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.CLAIM_ACCEPTED_AND_CLOSED;
        } else if (ClaimState.DENIED_AND_CLOSED.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.CLAIM_DENIED_AND_CLOSED;
        } else if (ClaimState.APPEALED.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.CLAIM_APPEALED;
        } else if (ClaimState.APPROVED.name().toString().equalsIgnoreCase(eventName)) {
            claimState = EventState.PENDING_AUTHORIZATION;
        } else {
            //if a valid state has b
            claimState = null;
        }

        //Obviously if the claim state is not present or does not match our possible criteria then we definitely
        //need not create a new event to be taken care of.
        if (claimState != null) {
            eventService.createEvent("claim", claimState, this.getId());
        }
    }

    private boolean isClaimStateSame(ClaimState state) {
        boolean toReturn = false;
        if (this.getState().equals(state)) {
            if (this.getState().equals(ClaimState.TRANSFERRED) ||
                    this.getState().equals(ClaimState.ON_HOLD) ||
                    this.getState().equals(ClaimState.APPROVED) ||
                    this.getState().equals(ClaimState.ON_HOLD_FOR_PART_RETURN) ||
                    this.getState().equals(ClaimState.SERVICE_MANAGER_REVIEW) ||
                    this.getState().equals(ClaimState.CP_TRANSFER)) {
                return false;
            }
            return true;
        }
        return toReturn;
    }

    public Payment getPayment() {
        return this.getActiveClaimAudit().getPayment();
    }

    public void setPayment(Payment payment) {
        this.getActiveClaimAudit().setPayment(payment);
//        if (payment != null) {
//            payment.setForClaim(this);
//        }
    }

    public String getClaimNumber() {
        return this.claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    /**
     * @return the serviceInformation
     */
    public ServiceInformation getServiceInformation() {
        return this.getActiveClaimAudit().getServiceInformation();
    }

    /**
     * @param serviceInformation the serviceInformation to set
     */
    public void setServiceInformation(ServiceInformation serviceInformation) {
        this.getActiveClaimAudit().setServiceInformation(serviceInformation);
    }

    public ItemReference getPartItemReference() {
        return this.partItemReference;
    }

    public void setPartItemReference(ItemReference partItemReference) {
        this.partItemReference = partItemReference;
    }

    public InventoryItem getForItem() {
        ItemReference itemReference = getItemReference();
        return (itemReference == null) ? null : itemReference.getReferredInventoryItem();
    }

    public Boolean isServiceManagerRequest() {
        return this.serviceManagerRequest;
    }

    public Boolean getServiceManagerRequest() {
        return this.serviceManagerRequest;
    }

    public void setServiceManagerRequest(Boolean serviceManagerRequest) {
        this.serviceManagerRequest = serviceManagerRequest;
    }

    public SmrReason getReasonForServiceManagerRequest() {
        return reasonForServiceManagerRequest;
    }

    public void setReasonForServiceManagerRequest(SmrReason reasonForServiceManagerRequest) {
        this.reasonForServiceManagerRequest = reasonForServiceManagerRequest;
    }

    public String getWorkOrderNumber() {
        return this.getActiveClaimAudit().getWorkOrderNumber();
    }

    public void setWorkOrderNumber(String workOrderNumber) {
        this.getActiveClaimAudit().setWorkOrderNumber(workOrderNumber);
    }

    public String getOtherComments() {
        return this.getActiveClaimAudit().getOtherComments();
    }

    public void setOtherComments(String otherComments) {
        this.getActiveClaimAudit().setOtherComments(otherComments);
    }

    public String getProbableCause() {
        return this.getActiveClaimAudit().getProbableCause();
    }

    public void setProbableCause(String probableCause) {
        this.getActiveClaimAudit().setProbableCause(probableCause);
    }

    public String getWorkPerformed() {
        return this.getActiveClaimAudit().getWorkPerformed();
    }

    public void setWorkPerformed(String workPerformed) {
        this.getActiveClaimAudit().setWorkPerformed(workPerformed);
    }

    public String getConditionFound() {
        return this.getActiveClaimAudit().getConditionFound();
    }

    public void setConditionFound(String conditionFound) {
        this.getActiveClaimAudit().setConditionFound(conditionFound);
    }

    /**
     * @return the filedBy
     */
    public User getFiledBy() {
        return this.filedBy;
    }

    /**
     * @param filedBy the filedBy to set
     */
    public void setFiledBy(User filedBy) {
        this.filedBy = filedBy;
    }

    /**
     * @return the lastUpdatedBy
     */
    public User getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    /**
     * @param lastUpdatedBy the lastUpdatedBy to set
     */
    public void setLastUpdatedBy(User lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    /**
     * @return the forDealer
     */
    public ServiceProvider getForDealerShip() {

        return new HibernateCast<ServiceProvider>().cast(this.forDealer);

    }

    /**
     * @param forDealer the forDealer to set
     */
    public void setForDealerShip(ServiceProvider forDealer) {
        this.forDealer = forDealer; //new HibernateCast<Dealership>().cast(forDealer);
    }

    public ServiceProvider getForDealer() {
        return forDealer;
    }

    public void setForDealer(ServiceProvider forDealer) {
        this.forDealer = forDealer;
    }

    public SortedSet<RuleFailure> getRuleFailures() {
        return this.getActiveClaimAudit().getRuleFailures();
    }

    public void setRuleFailures(SortedSet<RuleFailure> ruleFailures) {
        this.getActiveClaimAudit().setRuleFailures(ruleFailures);
    }

    public boolean addRuleFailure(RuleFailure ruleFailure) {
        return this.getActiveClaimAudit().getRuleFailures().add(ruleFailure);
    }

    public Integer getNoOfResubmits() {
        return this.noOfResubmits;
    }

    public void setNoOfResubmits(Integer noOfResubmits) {
        this.noOfResubmits = noOfResubmits;
    }

    /**
     * @return the serviceManagerAccepted
     */
    public boolean isServiceManagerAccepted() {
        return this.getActiveClaimAudit().isServiceManagerAccepted();
    }

    /**
     * @param serviceManagerAccepted the serviceManagerAccepted to set
     */
    public void setServiceManagerAccepted(boolean serviceManagerAccepted) {
        this.getActiveClaimAudit().setServiceManagerAccepted(serviceManagerAccepted);
    }

    public CalendarDate getInstallationDate() {
        return this.getActiveClaimAudit().getInstallationDate();
    }

    public void setInstallationDate(CalendarDate installationDate) {
        this.getActiveClaimAudit().setInstallationDate(installationDate);
    }

    public CalendarDate getPurchaseDate() {
        return this.getActiveClaimAudit().getPurchaseDate();
    }

    public void setPurchaseDate(CalendarDate purchaseDate) {
        this.getActiveClaimAudit().setPurchaseDate(purchaseDate);
    }

    public CalendarDate getEquipmentBilledDate() {
        if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, this)) {
            PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(this);
            // Changes made based on CR QC-167 wrt Parts Claim
            if (partsClaim.getPartItemReference().isSerialized()) {
                return partsClaim.getPartItemReference().getReferredInventoryItem().getDeliveryDate(); // Serialized Parts Claim will always return the Delivery Date
            } else {
                if (partsClaim.getPartInstalled()) {
                    return getInstallationDate(); // Non Serialized Parts Claim Installed on anything will always return the Installation Date
                } else {
                    return getPurchaseDate();  // Non Serialized Parts Claim Not -Installed  will always return the Purchase Date
                }
            }
        } else {
            if (!getItemReference().isSerialized()) {
                return getPurchaseDate();
            } else {
                InventoryItem inventoryItem = getItemReference().getReferredInventoryItem();
                if (inventoryItem.getBuiltOn() != null) {
                    return inventoryItem.getBuiltOn();
                } else {
                    return inventoryItem.getShipmentDate();
                }
            }
        }
    }

    public boolean hasEnoughInfoToObtainPartReturnConfiguration() {
        return getEquipmentBilledDate() != null;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("payment", getPayment() != null ? getPayment().getId() : "")
                .append("filed on ", this.filedOnDate).append("last updated on",
                        this.getLastUpdatedOnDate()).append("failure date", this.getFailureDate()).append(
                                "repair start date", this.getRepairStartDate()).append(
                        "repair end date", this.getRepairDate()).append("part fitted date",
                        this.getInstallationDate()).append("purchase date", this.getPurchaseDate()).append(
                        "service manager request", this.serviceManagerRequest).append(
                        "probable cause", this.getProbableCause()).append("work performed",
                        this.getWorkPerformed()).append("other comments", this.getOtherComments()).append(
                        "condition found", this.getConditionFound()).append("type", getType()).append(
                        "resubmits", this.noOfResubmits).append("service manager accepted",
                        this.isServiceManagerAccepted()).toString();
    }

    public ClaimAudit getActiveClaimAudit() {
        return activeClaimAudit;
    }

    public void setActiveClaimAudit(ClaimAudit activeClaimAudit) {
        this.activeClaimAudit = activeClaimAudit;
    }

    public Currency getCurrencyForCalculation() {
        return getForDealer().getPreferredCurrency();
    }

    public Criteria priceCriteriaForReplacedItem(BrandItem replacedItem, Policy applicablePolicy) {
        return priceCriteriaForLaborExpense(applicablePolicy);
    }

    public Criteria priceCriteriaForTravelExpense(Policy applicablePolicy) {
        return priceCriteriaForLaborExpense(applicablePolicy);
    }

    public void setCreatedBy(User user) {
        this.filedBy = user;
    }

    public void setCreatedOn(CalendarDate calendarDate) {
        this.filedOnDate = calendarDate;
    }

    public void setLastModifiedBy(User user) {
        this.lastUpdatedBy = user;
    }

    public void setLastModifiedOn(Date calendarDate) {
        this.lastUpdatedOnDate = calendarDate;
    }

    public Criteria priceCriteriaForLaborExpense(Policy applicablePolicy) {
        Criteria criteria = getCriteria();

        if (applicablePolicy != null) {
            WarrantyType warrantyType = applicablePolicy.getWarrantyType();
            if (warrantyType != null && warrantyType.getType() != null) {
                criteria.setWarrantyType(warrantyType.getType());
            }
        }

        return criteria;
    }

    public Criteria getCriteria() {
        return (claimedItems.size() > 0) ? getCriteriaForPayment() : null;
    }

    public Criteria getCriteriaForPayment() {
        Criteria criteria = new Criteria();
        criteria.setClaimType(this.getType());
        criteria.setDealerCriterion(new DealerCriterion(this.getForDealer()));
        // Added for non-serialized claim
        if (this.getClaimedItems() != null
                && !this.getClaimedItems().isEmpty()
                && this.getClaimedItems().get(0) != null
                && this.getClaimedItems().get(0).getItemReference() != null) {

            if (this.getClaimedItems().get(0).getItemReference()
                    .isSerialized()) {
                criteria.setProductType(this.getClaimedItems().get(0)
                        .getItemReference().getReferredInventoryItem()
                        .getOfType().getProduct());
            } else if (this.getClaimedItems().get(0).getItemReference()
                    .getModel() != null) {
                ItemGroup group = this.getClaimedItems().get(0)
                        .getItemReference().getModel();
                criteria.setProductType(group.getProduct(group));
            }
        }
        Policy applicablePolicy = getApplicablePolicy();
        WarrantyType warrantyType = applicablePolicy != null ? applicablePolicy.getWarrantyType()
                : null;
        if (warrantyType != null) {
            criteria.setWarrantyType(warrantyType.getType());
        }
        return criteria;
    }

    public String getCustomerType() {
        String customerType = "";
        if (claimedItems != null && claimedItems.size() > 0) {
            ClaimedItem item = claimedItems.get(0);
            if (item != null && item.getItemReference() != null && item.getItemReference().isSerialized()) {
                customerType = item.getItemReference().getReferredInventoryItem().getCustomerType();
            }
        }
        return customerType;
    }

    public List<ClaimAudit> getClaimAudits() {
        return this.claimAudits;
    }

    public ClaimAudit getLatestAudit() {
        int auditSize = this.claimAudits.size();
        if(auditSize > 0)
            return this.claimAudits.get(auditSize - 1);
        return null;
    }

    public void setClaimAudits(List<ClaimAudit> claimAudits) {
        this.claimAudits = claimAudits;
    }

    public void addClaimAudit(ClaimAudit claimAudit) {
        this.claimAudits.add(claimAudit);
    }

    public Boolean isProcessedAutomatically() {
        return this.processedAutomatically;
    }

    // Can set the value only to true. false by default.
    public void setProcessedAutomatically() {
        this.processedAutomatically = Boolean.TRUE;
    }

    public String getExternalComment() {
        return this.getActiveClaimAudit().getExternalComment();
    }

    public void setExternalComment(String externalComment) {
        this.getActiveClaimAudit().setExternalComment(externalComment);
    }

    public String getInternalComment() {
        return this.getActiveClaimAudit().getInternalComment();
    }

    public void setInternalComment(String internalComment) {
        this.getActiveClaimAudit().setInternalComment(internalComment);
    }

    public AcceptanceReason getAcceptanceReason() {
        return this.getActiveClaimAudit().getAcceptanceReason();
    }

    public void setAcceptanceReason(AcceptanceReason acceptanceReason) {
        this.getActiveClaimAudit().setAcceptanceReason(acceptanceReason);
    }

    public AcceptanceReasonForCP getAcceptanceReasonForCp() {
        return this.getActiveClaimAudit().getAcceptanceReasonForCp();
    }

    public void setAcceptanceReasonForCp(AcceptanceReasonForCP acceptanceReasonForCp) {
        this.getActiveClaimAudit().setAcceptanceReasonForCp(acceptanceReasonForCp);
    }

    public List<RejectionReason> getRejectionReasons() {
        return this.getActiveClaimAudit().getRejectionReasons();
    }

    public void setRejectionReasons(List<RejectionReason> rejectionReasons) {
        this.getActiveClaimAudit().setRejectionReasons(rejectionReasons);
    }
    
    public List<PutOnHoldReason> getPutOnHoldReasons() {    	
    	return this.getActiveClaimAudit().getPutOnHoldReasons();
	}

	public void setPutOnHoldReasons(List<PutOnHoldReason> putOnHoldReasons) {
		 this.getActiveClaimAudit().setPutOnHoldReasons(putOnHoldReasons);
	}
	
	 public List<RequestInfoFromUser> getRequestInfoFromUser() {
	    	return this.getActiveClaimAudit().getRequestInfoFromUser();
	}

	public void setRequestInfoFromUser(List<RequestInfoFromUser> requestInfoFromUser) {
		 this.getActiveClaimAudit().setRequestInfoFromUser(requestInfoFromUser);
	}
		
    public void setClaimXMLConverter(ClaimXMLConverter claimXMLConverter) {
        this.claimXMLConverter = claimXMLConverter;
    }

    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    public Boolean getAppealed() {
        return this.appealed == null ? Boolean.FALSE : this.appealed;
    }

    public void setAppealed(Boolean appealed) {
        this.appealed = appealed;
    }

    public Boolean getReopened() {
        return this.reopened == null ? Boolean.FALSE : this.reopened;
    }

    public void setReopened(Boolean reopened) {
        this.reopened = reopened;
    }

    public Boolean getCpReviewed() {
        return this.getActiveClaimAudit().getCpReviewed();
    }

    public void setCpReviewed(Boolean cpReviewed) {
        this.getActiveClaimAudit().setCpReviewed(cpReviewed);
    }

    public Boolean getEligibleForAppeal() {
        ClaimState claimState = this.getActiveClaimAudit().getState();
        //Refer SLMSPROD-73    -- removed the  && this.getType() != ClaimType.CAMPAIGN condition
        return isRecoveryClaimClosed() && ((claimState == ClaimState.DENIED_AND_CLOSED)
                || (claimState == ClaimState.ACCEPTED_AND_CLOSED && !this.isClaimSubmittedByThirdParty()));
    }

    private Boolean isClaimClosed(ClaimState state) {
        return (state == ClaimState.DENIED_AND_CLOSED || state == ClaimState.ACCEPTED_AND_CLOSED);
    }

    public Boolean getEligibleForTransfer() {
        ClaimState claimState = this.getActiveClaimAudit().getState();
        return (claimState == ClaimState.PROCESSOR_REVIEW || claimState == ClaimState.REJECTED_PART_RETURN ||
                claimState == ClaimState.REOPENED || claimState == ClaimState.ON_HOLD || claimState == ClaimState.ON_HOLD_FOR_PART_RETURN
                || claimState == ClaimState.REPLIES || claimState == ClaimState.TRANSFERRED || claimState == ClaimState.APPROVED);
    }

    public Boolean getEligibleForEdit() {
        ClaimState claimState = this.getActiveClaimAudit().getState();
        return (claimState == ClaimState.PROCESSOR_REVIEW || claimState == ClaimState.REJECTED_PART_RETURN ||
                claimState == ClaimState.REOPENED || claimState == ClaimState.ON_HOLD ||
                claimState == ClaimState.ON_HOLD_FOR_PART_RETURN || claimState == ClaimState.REPLIES ||
                claimState == ClaimState.TRANSFERRED || claimState == ClaimState.SERVICE_MANAGER_REVIEW ||
                claimState == ClaimState.ADVICE_REQUEST || claimState == ClaimState.DRAFT || claimState == ClaimState.APPROVED);
    }

    public Boolean getDealerEligibleForEdit() {
        return (this.getActiveClaimAudit().getState() == ClaimState.FORWARDED);
    }

    public Boolean getStatusListInProgressForDealer() {
        ClaimState claimState = this.getActiveClaimAudit().getState();
        return (claimState == ClaimState.MANUAL_REVIEW || claimState == ClaimState.ON_HOLD || claimState == ClaimState.ON_HOLD_FOR_PART_RETURN ||
                claimState == ClaimState.FORWARDED || claimState == ClaimState.TRANSFERRED || claimState == ClaimState.ADVICE_REQUEST ||
                claimState == ClaimState.REPLIES || claimState == ClaimState.PROCESSOR_REVIEW || claimState == ClaimState.REJECTED_PART_RETURN ||
                claimState == ClaimState.PENDING_PAYMENT_SUBMISSION || claimState == ClaimState.PENDING_PAYMENT_RESPONSE || claimState == ClaimState.REOPENED || claimState == ClaimState.APPROVED);
    }

    public Boolean getEligibleForProcess() {
        ClaimState claimState = this.getActiveClaimAudit().getState();
        return (claimState == ClaimState.PROCESSOR_REVIEW || claimState == ClaimState.REJECTED_PART_RETURN || claimState == ClaimState.ON_HOLD_FOR_PART_RETURN ||
                claimState == ClaimState.ON_HOLD || claimState == ClaimState.REPLIES || claimState == ClaimState.APPEALED
                || claimState == ClaimState.TRANSFERRED || claimState == ClaimState.REOPENED || claimState == ClaimState.EXTERNAL_REPLIES || claimState == ClaimState.APPROVED ||claimState==ClaimState.PENDING_PAYMENT_SUBMISSION
                ||claimState==ClaimState.ACCEPTED);
    }

    public boolean isOpen() {
        ClaimState claimState = this.getActiveClaimAudit().getState();
        return
                claimState != ClaimState.ACCEPTED_AND_CLOSED &&
                        claimState != ClaimState.DENIED_AND_CLOSED &&
                        claimState != ClaimState.DELETED &&
                        claimState != ClaimState.DRAFT_DELETED &&
                        claimState != ClaimState.DEACTIVATED &&
                        claimState != ClaimState.DRAFT;
    }

    public boolean isCanDecideForPayment() {
        return this.canDecideForPayment;
    }

    public void setCanDecideForPayment(boolean isEligibleForPayment) {
        this.canDecideForPayment = isEligibleForPayment;
    }

    // Back ported APIs. These have been added only to ease multicar migration,
    // and will be remvoed shortly!!!

    @Deprecated
    public ItemReference getItemReference() {
        return (this.claimedItems.isEmpty()) ? null : this.claimedItems.get(0).getItemReference();
    }

    @Deprecated
    public void setItemReference(ItemReference itemReference) {
        if (this.claimedItems.isEmpty()) {
            addClaimedItem(new ClaimedItem(itemReference));
        } else {
            this.claimedItems.get(0).setItemReference(itemReference);
        }
    }

    @Deprecated
    public BigDecimal getHoursInService() {
        return (this.claimedItems.isEmpty()) ? null : this.claimedItems.get(0).getHoursInService();
    }

    @Deprecated
    public void setHoursInService(BigDecimal hoursInService) {
        if (this.claimedItems.isEmpty()) {
            addClaimedItem(new ClaimedItem());
        }

        this.claimedItems.get(0).setHoursInService(hoursInService);
    }

    @Deprecated
    public Policy getApplicablePolicy() {
        return (this.claimedItems.isEmpty()) ? null : this.claimedItems.get(0)
                .getApplicablePolicy();
    }

    @Deprecated
    public void setApplicablePolicy(Policy policy) {
        if (this.claimedItems.isEmpty()) {
            addClaimedItem(new ClaimedItem());
        }

        this.claimedItems.get(0).setApplicablePolicy(policy);
    }

    /* We have to display reopen button for Warranty claims if there is no recovery claim and if recovery claim states are in closed,new and WNTY_CLAIM_REOPENED (TKTSA-1174) */
    public boolean isRecoveryClaimClosed() {
        if (this.getRecoveryInfo() != null) {
            for (RecoveryClaimInfo recoveryClaimInfo : this.getRecoveryInfo().getReplacedPartsRecovery()) {
                if (recoveryClaimInfo.getContract() != null && recoveryClaimInfo.getRecoveryClaim() != null
                        && recoveryClaimInfo.getRecoveryClaim().getRecoveryClaimState().getState() != null) {
                    if (!(recoveryClaimInfo.getRecoveryClaim().getRecoveryClaimState().getState().contains("Closed"))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /* we have to display reopen recovery claim check box when all recovery claims on that warranty claim are in closed state only (TKTSA-1174) */
    public Boolean getEligibleForReopenRecoveryClaim() {
        boolean isEligible = false;
        if (this.getRecoveryInfo() != null) {
            for (RecoveryClaimInfo recoveryClaimInfo : this.getRecoveryInfo().getReplacedPartsRecovery()) {
                if (recoveryClaimInfo.getContract() != null && recoveryClaimInfo.getRecoveryClaim() != null
                        && recoveryClaimInfo.getRecoveryClaim().getRecoveryClaimState().getState() != null) {
                    if (recoveryClaimInfo.getRecoveryClaim().getRecoveryClaimState().getState().contains("Closed")) {
                        isEligible = true;
                    } else {
                        isEligible = false;
                        break;
                    }
                }
            }
        }
        return isEligible;
    }

    public boolean isPartReturnRejected() {
        List<OEMPartReplaced> parts = getServiceInformation()
                .getServiceDetail().getReplacedParts();
        for (OEMPartReplaced partReplaced : parts) {
            List<PartReturn> partReturns = partReplaced.getPartReturns();
            for (PartReturn partReturn : partReturns) {
                if (partReturn.getStatus().equals(PartReturnStatus.PART_REJECTED)) {
                    return true;
                }
            }
        }
        return false;
    }

    public AccountabilityCode getAccountabilityCode() {
        return this.getActiveClaimAudit().getAccountabilityCode();
    }

    public void setAccountabilityCode(AccountabilityCode accountabilityCode) {
        this.getActiveClaimAudit().setAccountabilityCode(accountabilityCode);
    }

    public Boolean getReopenRecoveryClaim() {
        return this.reopenRecoveryClaim;
    }

    public void setReopenRecoveryClaim(Boolean reopenRecoveryClaim) {
        this.reopenRecoveryClaim = reopenRecoveryClaim;
    }

    public List<RecoveryClaim> getRecoveryClaims() {
        List<RecoveryClaim> recClaims= new ArrayList<RecoveryClaim>();
        for(RecoveryClaim recoveryClaim : this.recoveryClaims)
        {
            if(recoveryClaim.getD().isActive()){
                recClaims.add(recoveryClaim);
            }
        }

        return recClaims;
    }

    public void setRecoveryClaims(List<RecoveryClaim> recoveryClaims) {
        this.recoveryClaims = recoveryClaims;
    }

    public RecoveryClaim getLatestRecoveryClaim() {
    	
    		int size = this.recoveryClaims.size();
    		if(this.recoveryClaims.size()>0  ){
            return this.recoveryClaims.get(size - 1);
    	}
    	return null;
        
    }

    public void addRecoveryClaim(RecoveryClaim recoveryClaim) {
        this.recoveryClaims.add(recoveryClaim);
    }

    public Boolean getUpdated() {
        return this.updated;
    }

    public void setUpdated(Boolean updated) {
        this.updated = updated;
    }

	public OrganizationAddress getServicingLocation() {
        return this.getActiveClaimAudit().getServicingLocation();
    }

    public void setServicingLocation(OrganizationAddress servicingLocation) {
        this.getActiveClaimAudit().setServicingLocation(servicingLocation);
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    private boolean isInternalCommentToBeSet(ClaimState claimState) {
        if (ClaimState.ACCEPTED_AND_CLOSED.getState().equals(claimState.getState())
                || ClaimState.CLOSED.getState().equals(claimState.getState())
                || ClaimState.DENIED_AND_CLOSED.getState().equals(claimState.getState())
                || ClaimState.REOPENED.getState().equals(claimState.getState())) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isExternalCommentToBeSet(ClaimState claimState) {
        if (ClaimState.PROCESSOR_REVIEW.getState().equals(claimState.getState())
                || ClaimState.PENDING_PAYMENT_SUBMISSION.getState().equals(claimState.getState())
                || ClaimState.PENDING_PAYMENT_RESPONSE.getState().equals(claimState.getState())
                || ClaimState.CLOSED.getState().equals(claimState.getState())
                || ClaimState.REOPENED.getState().equals(claimState.getState())) {
            return false;
        } else {
            return true;
        }
    }


    private User getLoggedInUser(ClaimState state) {
        if (ClaimState.REOPENED.getState().equals(state.getState()) ||
                ClaimState.APPEALED.getState().equals(state.getState())) {
            return this.securityHelper.getLoggedInUser();
        }

        //If claim has no payment to be made(no credit submission),
        //set the updated by user as system user.
        if (ClaimState.ACCEPTED_AND_CLOSED.equals(state)
                || ClaimState.DENIED_AND_CLOSED.equals(state)) {
            return orgService.findSystemUserByName();
        }

        String internalComment = this.getActiveClaimAudit().getInternalComment();
        if ("Auto Denied".equalsIgnoreCase(internalComment) ||
                "Processing Engine".equalsIgnoreCase(internalComment) ||
                "Credit Submission".equalsIgnoreCase(internalComment) ||
                "Auto Reply".equalsIgnoreCase(internalComment)) {
            return orgService.findSystemUserByName();
        } else {
            return this.securityHelper.getLoggedInUser();
        }
    }

    public void reopen(Boolean reopenRecoveryClaim) {
        this.setReopened(Boolean.TRUE);
        this.setRejectionReasons(null);
        this.setPutOnHoldReasons(null);
        this.setRequestInfoFromUser(null);
        this.setAcceptanceReason(null);
        this.setCreditDate(null);
        this.setReopenRecoveryClaim(reopenRecoveryClaim);
    }

    public Boolean getTravelTripConfig() {
        return this.getActiveClaimAudit().getTravelTripConfig();
    }

    public void setTravelTripConfig(Boolean travelTripConfig) {
        this.getActiveClaimAudit().setTravelTripConfig(travelTripConfig);
    }

    public Boolean getTravelHrsConfig() {
        return getActiveClaimAudit().getTravelHrsConfig();
    }

    public void setTravelHrsConfig(Boolean travelHrsConfig) {
        this.getActiveClaimAudit().setTravelHrsConfig(travelHrsConfig);
    }

    public Boolean getTravelDisConfig() {
        return getActiveClaimAudit().getTravelDisConfig();
    }

    public void setTravelDisConfig(Boolean travelDisConfig) {
        this.getActiveClaimAudit().setTravelDisConfig(travelDisConfig);
    }

    public Boolean getOemConfig() {
        return this.getActiveClaimAudit().getOemConfig();
    }

    public void setOemConfig(Boolean oemConfig) {
        this.getActiveClaimAudit().setOemConfig(oemConfig);
    }

    public Boolean getNonOemConfig() {
        return getActiveClaimAudit().getNonOemConfig();
    }

    public void setNonOemConfig(Boolean nonOemConfig) {
        this.getActiveClaimAudit().setNonOemConfig(nonOemConfig);
    }

    public Boolean getLaborConfig() {
        return getActiveClaimAudit().getLaborConfig();
    }

    public void setLaborConfig(Boolean laborConfig) {
        this.getActiveClaimAudit().setLaborConfig(laborConfig);
    }

    public Boolean getMealsConfig() {
        return this.getActiveClaimAudit().getMealsConfig();
    }

    public void setMealsConfig(Boolean mealsConfig) {
        this.getActiveClaimAudit().setMealsConfig(mealsConfig);
    }

    public Boolean getParkingConfig() {
        return this.getActiveClaimAudit().getParkingConfig();
    }

    public void setParkingConfig(Boolean parkingConfig) {
        this.getActiveClaimAudit().setParkingConfig(parkingConfig);
    }

    public Boolean getItemDutyConfig() {
        return getActiveClaimAudit().getItemDutyConfig();
    }
    
    public Boolean getHandlingFeeConfig() {
        return this.getActiveClaimAudit().getHandlingFeeConfig();
    }

    public void setItemDutyConfig(Boolean itemDutyConfig) {
        this.getActiveClaimAudit().setItemDutyConfig(itemDutyConfig);
    }
    
    public void setHandlingFeeConfig(Boolean handlingFeeConfig) {
        this.getActiveClaimAudit().setHandlingFeeConfig(handlingFeeConfig);
    }
    
    public Boolean getTravelConfig()
    {
    	 return this.getActiveClaimAudit().getTravelConfig();
    }
    
    public void setTravelConfig(Boolean travelConfig) {
        this.getActiveClaimAudit().setTravelConfig(travelConfig);
    }

    public String getHistClmNo() {
        return histClmNo;
    }

    public void setHistClmNo(String histClmNo) {
        this.histClmNo = histClmNo;
    }

    public Boolean getClaimDenied() {
        return claimDenied;
    }

    public void setClaimDenied(Boolean claimDenied) {
        this.claimDenied = claimDenied;
    }

    @Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    public BusinessUnitInfo getBusinessUnitInfo() {
        return businessUnitInfo;
    }

    public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
        this.businessUnitInfo = buAudit;
    }

    public Boolean getPrtShpNtrcvd() {
        return prtShpNtrcvd;
    }

    public void setPrtShpNtrcvd(Boolean prtShpNtrcvd) {
        this.prtShpNtrcvd = prtShpNtrcvd;
    }

    /**
     * This method is called by JBPM to decide how the part returns should be handled when a claim is
     * submitted; as if a claim being submitted is on behalf of a third party; then parts should be
     * moved to third party part return inbox else they are moved to normal dealer part return inbox.
     *
     * @return
     */
    public boolean isThirdPartyClaim() {
        if (InstanceOfUtil.isInstanceOfClass(ThirdParty.class, this
                .getForDealer())) {
            if (!isServiceProviderSameAsFiledByOrg() ||
                    getFiledBy().isInternalUser()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method is to check if a claim being submitted is on behalf of a third party or by third part;
     *
     * @return
     */
    public boolean isClaimSubmittedByThirdParty() {
        if (InstanceOfUtil.isInstanceOfClass(ThirdParty.class, this.getForDealer())) {
            return true;
        }
        return false;
    }

    @Transient
    public int getApprovedClaimedItems() {
        int approvedClaimedItems = 0;
        List<ClaimedItem> claimedItems = this.getClaimedItems();
        for (ClaimedItem claimedItem : claimedItems) {
            if (claimedItem.isProcessorApproved()) {
                approvedClaimedItems++;
            }
        }
        return approvedClaimedItems;
    }

    public String getInvoiceNumber() {
        return getActiveClaimAudit().getInvoiceNumber();
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.getActiveClaimAudit().setInvoiceNumber(invoiceNumber);
    }

    public Boolean getMiscPartsConfig() {
        return getActiveClaimAudit().getMiscPartsConfig();
    }

    public void setMiscPartsConfig(Boolean miscPartsConfig) {
        this.getActiveClaimAudit().setMiscPartsConfig(miscPartsConfig);
    }

    public void addAllowedActionsList(Set<String> allowedActionsList) {
        if (this.allowedActionsList == null)
            this.allowedActionsList = new HashSet<String>();
        this.allowedActionsList.addAll(allowedActionsList);
    }

    public Set<String> getAllowedActionsList() {
        return allowedActionsList;
    }

    //E-mail Notification merge Start
    public EventService getEventService() {
        return eventService;
    }


    @Required
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
    //E-mail Notification merge End

    public SellingEntity getSellingEntity() {
        return getActiveClaimAudit().getSellingEntity();
    }

    public void setSellingEntity(SellingEntity sellingEntity) {
        this.getActiveClaimAudit().setSellingEntity(sellingEntity);
    }

    public SourceWarehouse getSourceWarehouse() {
        return sourceWarehouse;
    }

    public void setSourceWarehouse(SourceWarehouse sourceWarehouse) {
        this.sourceWarehouse = sourceWarehouse;
    }

    public Boolean getPerDiemConfig() {
        return getActiveClaimAudit().getPerDiemConfig();
    }

    public void setPerDiemConfig(Boolean perDiemConfig) {
        this.getActiveClaimAudit().setPerDiemConfig(perDiemConfig);
    }

    public Boolean getRentalChargesConfig() {
        return getActiveClaimAudit().getRentalChargesConfig();
    }

    public void setRentalChargesConfig(Boolean rentalChargesConfig) {
        this.getActiveClaimAudit().setRentalChargesConfig(rentalChargesConfig);
    }

    public Boolean getAdditionalTravelHoursConfig() {
        return getActiveClaimAudit().getAdditionalTravelHoursConfig();
    }

    public void setAdditionalTravelHoursConfig(Boolean additionalTravelHoursConfig) {
        this.getActiveClaimAudit().setAdditionalTravelHoursConfig(additionalTravelHoursConfig);
    }

    public String getDateCode() {
        return dateCode;
    }

    public void setDateCode(String dateCode) {
        this.dateCode = dateCode;
    }

    public boolean isFoc() {
        return foc;
    }

    public void setFoc(boolean foc) {
        this.foc = foc;
    }

    public String getFocOrderNo() {
        return focOrderNo;
    }

    public void setFocOrderNo(String focOrderNo) {
        this.focOrderNo = focOrderNo;
    }


    public Map<String, Boolean> getIncludedCostCategories() {
        Map<String, Boolean> includedCostCategories = new HashMap<String, Boolean>();
        includedCostCategories.put(CostCategory.OEM_PARTS_COST_CATEGORY_CODE, getOemConfig());
        includedCostCategories.put(CostCategory.NON_OEM_PARTS_COST_CATEGORY_CODE, getNonOemConfig());
        includedCostCategories.put(CostCategory.LABOR_COST_CATEGORY_CODE, getLaborConfig());
        includedCostCategories.put(CostCategory.TRAVEL_DISTANCE_COST_CATEGORY_CODE, getTravelDisConfig());
        includedCostCategories.put(CostCategory.TRAVEL_HOURS_COST_CATEGORY_CODE, getTravelHrsConfig());
        includedCostCategories.put(CostCategory.TRAVEL_TRIP_COST_CATEGORY_CODE, getTravelTripConfig());
        includedCostCategories.put(CostCategory.MEALS_HOURS_COST_CATEGORY_CODE, getMealsConfig());
        includedCostCategories.put(CostCategory.PARKING_COST_CATEGORY_CODE, getParkingConfig());
        includedCostCategories.put(CostCategory.FREIGHT_DUTY_CATEGORY_CODE, getItemDutyConfig());
        includedCostCategories.put(CostCategory.HANDLING_FEE_CODE, getHandlingFeeConfig());
        includedCostCategories.put(CostCategory.PER_DIEM_COST_CATEGORY_CODE, getPerDiemConfig());
        includedCostCategories.put(CostCategory.RENTAL_CHARGES_COST_CATEGORY_CODE, getRentalChargesConfig());
        includedCostCategories.put(CostCategory.ADDITIONAL_TRAVEL_HOURS_COST_CATEGORY_CODE, getAdditionalTravelHoursConfig());
        includedCostCategories.put(CostCategory.MISC_PARTS_COST_CATEGORY_CODE, getMiscPartsConfig());
        includedCostCategories.put(CostCategory.LOCAL_PURCHASE_COST_CATEGORY_CODE, getLocalPurchaseConfig());
        includedCostCategories.put(CostCategory.TOLLS_COST_CATEGORY_CODE, getTollsConfig());
        includedCostCategories.put(CostCategory.OTHER_FREIGHT_DUTY_COST_CATEGORY_CODE, getOtherFreightDutyConfig());
        includedCostCategories.put(CostCategory.OTHERS_CATEGORY_CODE, getOthersConfig());
        includedCostCategories.put(CostCategory.TRANSPORTATION_COST_CATEGORY_CODE, getTransportation());
        includedCostCategories.put(CostCategory.TRAVEL_COST_CATEGORY_CODE, getTravelConfig());
        return includedCostCategories;
    }


    public boolean isServiceProviderSameAsFiledByOrg() {
        for (Organization org : getFiledBy().getBelongsToOrganizations()) {
            if (org.getId().longValue() == getForDealer().getId().longValue()) {
                return true;
            }
        }
        return false;
    }

    public String getPartReturnStatus() {
        List<OEMPartReplaced> partsReplaced = this.getServiceInformation()
                .getServiceDetail().getReplacedParts();
        return getSortedPartReturnStatus(partsReplaced);
    }

    private String getSortedPartReturnStatus(List<OEMPartReplaced> partsReplaced) {
        if (partsReplaced != null && CollectionUtils.isNotEmpty(partsReplaced)) {
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
        	List<PartReturn> totalPartReturns = new ArrayList<PartReturn>();
            for(OEMPartReplaced oemPartReplaced:partsReplaced){
	         List<PartReturn> partReturns = oemPartReplaced.getPartReturns();
	         totalPartReturns.addAll(partReturns);
            }
            String status=getStatusOfPartReturns(totalPartReturns);
            if(StringUtils.isNotEmpty(status)){
            	return status;
            }
            if (partsReplaced.get(0).getStatus() != null) {
                if (partsReplaced.get(0).getStatus() != null)
                    return partsReplaced.get(0).getStatus().getStatus();
                else
                    return null;
            }
        }
        return null;
    }
    
    private String getStatusOfPartReturns(List<PartReturn> totalPartReturns){
    	 int countPartShipmentGeneratedStatus=0;
         int counterPartShipped=0;
         int countPartReceivedStatus=0;
         int countPartAcceptedStatus=0;
         int counterPartCannotBeShipped=0;
         int countPartRejectedStatus=0;
         if(CollectionUtils.isNotEmpty(totalPartReturns)){
    	 for(PartReturn partReturn : totalPartReturns){
    	    PartReturnStatus status = partReturn.getStatus();
	    	if(status.equals(PartReturnStatus.PARTIALLY_SHIPMENT_GENERATED)){
	           	 return PartReturnStatus.PARTIALLY_SHIPMENT_GENERATED.getStatus();	
	        }
	     	if(status.equals(PartReturnStatus.PARTIALLY_SHIPPED)){
	          	 return PartReturnStatus.PARTIALLY_SHIPPED.getStatus();	
	        }
	  	    if(status.equals(PartReturnStatus.PARTIALLY_RECEIVED)){
	         	 return PartReturnStatus.PARTIALLY_RECEIVED.getStatus();	
	          }
			if(status.equals(PartReturnStatus.PARTIALLY_ACCEPTED)){
	      	 return PartReturnStatus.PARTIALLY_ACCEPTED.getStatus();	
	      	}
	      	if(status.equals(PartReturnStatus.PARTIALLY_REJECTED)){
	         	 return PartReturnStatus.PARTIALLY_REJECTED.getStatus();	
	         }
	      	if(status.equals(PartReturnStatus.SHIPMENT_GENERATED)){
	      		countPartShipmentGeneratedStatus++;
	      	}
	      	if(status.equals(PartReturnStatus.PART_SHIPPED)){
	      		counterPartShipped++;
	      	}
	      	if(status.equals(PartReturnStatus.PART_RECEIVED)){
	      		countPartReceivedStatus++;
	      	}
	      	if(status.equals(PartReturnStatus.PART_REJECTED)){
	      		countPartRejectedStatus++;
	      	}
	      	if(status.equals(PartReturnStatus.PART_ACCEPTED)){
	      		countPartAcceptedStatus++;
	      	}
	      	if(status.equals(PartReturnStatus.CANNOT_BE_SHIPPED)){
	      		counterPartCannotBeShipped++;
	      	}
    	}
    	 if(countPartAcceptedStatus > 0){
          	return countPartAcceptedStatus==totalPartReturns.size()?PartReturnStatus.PART_ACCEPTED.getStatus():
          		PartReturnStatus.PARTIALLY_ACCEPTED.getStatus(); 
          }
    	 if(countPartRejectedStatus > 0){
          	return countPartRejectedStatus==totalPartReturns.size()?PartReturnStatus.PART_REJECTED.getStatus():
          		PartReturnStatus.PARTIALLY_REJECTED.getStatus(); 
          }
    	
    	 if(countPartReceivedStatus > 0){
          	return countPartReceivedStatus==totalPartReturns.size()?PartReturnStatus.PART_RECEIVED.getStatus():
          		PartReturnStatus.PARTIALLY_RECEIVED.getStatus(); 
          }
    	 if(counterPartShipped > 0){
           	return counterPartShipped==totalPartReturns.size()?PartReturnStatus.PART_SHIPPED.getStatus():
           		PartReturnStatus.PARTIALLY_SHIPPED.getStatus(); 
           }
    	 if(countPartShipmentGeneratedStatus > 0){
          	return countPartShipmentGeneratedStatus==totalPartReturns.size()?PartReturnStatus.SHIPMENT_GENERATED.getStatus():
          		PartReturnStatus.PARTIALLY_SHIPMENT_GENERATED.getStatus(); 
          }
    	 if(counterPartCannotBeShipped==totalPartReturns.size()){
         	return PartReturnStatus.CANNOT_BE_SHIPPED.getStatus(); 
           }
         }
    	return null;
    }

    public void setLocalPurchaseConfig(Boolean localPurchaseConfig) {
        this.getActiveClaimAudit().setLocalPurchaseConfig(localPurchaseConfig);
    }

    public Boolean getLocalPurchaseConfig() {
        return this.getActiveClaimAudit().getLocalPurchaseConfig();
    }

    public void setTollsConfig(Boolean tollsConfig) {
        this.getActiveClaimAudit().setTollsConfig(tollsConfig);
    }

    public Boolean getTollsConfig() {
        return this.getActiveClaimAudit().getTollsConfig();
    }

    public void setOtherFreightDutyConfig(Boolean otherFreightDutyConfig) {
        this.getActiveClaimAudit().setOtherFreightDutyConfig(otherFreightDutyConfig);
    }

    public Boolean getOtherFreightDutyConfig() {
        return this.getActiveClaimAudit().getOtherFreightDutyConfig();
    }

    public void setOthersConfig(Boolean othersConfig) {
        this.getActiveClaimAudit().setOthersConfig(othersConfig);
    }

    public Boolean getOthersConfig() {
        return this.getActiveClaimAudit().getOthersConfig();
    }
    
	public void setTransportation(Boolean transportation){
		this.getActiveClaimAudit().setTransportation(transportation);
	}
	
	public Boolean getTransportation(){
		return this.getActiveClaimAudit().getTransportation();
	}

    public boolean isCanUpdatePayment() {
        return canUpdatePayment;
    }

    public void setCanUpdatePayment(boolean canUpdatePayment) {
        this.canUpdatePayment = canUpdatePayment;
    }

    public BigDecimal getHoursOnPart() {
        return hoursOnPart;
    }

    public void setHoursOnPart(BigDecimal hoursOnPart) {
        this.hoursOnPart = hoursOnPart;
    }

    public BigDecimal getHoursOnTruck() {
		return hoursOnTruck;
	}

	public void setHoursOnTruck(BigDecimal hoursOnTruck) {
		this.hoursOnTruck = hoursOnTruck;
	}

	public Address getOwnerInformation() {
        return this.getActiveClaimAudit().getOwnerInformation();
    }

    public void setOwnerInformation(Address ownerInformation) {
        this.getActiveClaimAudit().setOwnerInformation(ownerInformation);
    }

    public String getModel() {
        if ((!this.claimedItems.isEmpty())
                && this.claimedItems.get(0) != null
                && this.claimedItems.get(0).getItemReference() != null) {
            if ((!this.claimedItems.get(0).getItemReference().isSerialized())
                    && this.claimedItems.get(0).getItemReference().getModel() != null) {
                return this.claimedItems.get(0).getItemReference().getModel().getName();
            } else {
                if (this.claimedItems.get(0).getItemReference().getReferredInventoryItem() != null)
                    return this.claimedItems.get(0).getItemReference().getReferredInventoryItem().getOfType().getName();
            }
        }
        return null;
    }

    public String getSerialNumber() {
        InventoryItem item = getForItem();
        if (item != null) {
            return item.getSerialNumber();
        }
        return null;
    }


    public Payment getPaymentForDealerAudit() {
        Payment payment = null;
        for (ClaimAudit claimAudit : this.getClaimAudits()) {
            if (ClaimState.SUBMITTED.getState().equals(claimAudit.getPreviousState().getState())) {
                payment = claimAudit.getPayment();
            } else if (ClaimState.SERVICE_MANAGER_REVIEW.getState().equals(claimAudit.getPreviousState().getState())) {
                payment = claimAudit.getPayment();
                break;
            } else if (ClaimState.SERVICE_MANAGER_RESPONSE.getState().equals(claimAudit.getPreviousState().getState())) {
                payment = claimAudit.getPayment();
            } else if (ClaimState.EXTERNAL_REPLIES.getState().equals(claimAudit.getPreviousState().getState())) {
                payment = claimAudit.getPayment();
            }
        }
        return payment;
    }

    public Payment getPaymentForClaimState(String claimState) {
        Payment payment = null;
        for (ClaimAudit claimAudit : this.getClaimAudits()) {
            if (claimAudit.getPreviousState().getState().equalsIgnoreCase(claimState)) {
                payment = claimAudit.getPayment();
                break;
            }
        }
        return payment;
    }

    /**
     * This API gives the last Claim Audit against which amount
     * was paid by manufacturer through claim submission interface
     *
     * @return
     */
    public Payment getLatestPaidPayment() {
        Payment latestPayment = null;
        for (ClaimAudit audit : this.claimAudits) {
            if (audit.getPayment() != null && audit.getPayment().getActiveCreditMemo() != null) {
                latestPayment = audit.getPayment();
            }
        }
        return latestPayment;
    }

    /**
     * This API gives the last Claim Audit against which amount
     * was paid by manufacturer through claim submission interface
     *
     * @return
     */
    public List<Payment> getAllPaidPayments() {
        List<Payment> paidPayments = new ArrayList<Payment>();
        for (ClaimAudit audit : this.claimAudits) {
            if (audit.getPayment() != null && audit.getPayment().getActiveCreditMemo() != null) {
                paidPayments.add(audit.getPayment());
            }
        }
        return paidPayments;
    }

    public BigDecimal getNetAcceptancePercentageForPayment() {
        LineItemGroup summationGroup = this.getPayment().getLineItemGroup(Section.TOTAL_CLAIM);
        LineItemGroup dealerGroup = this.getPaymentForDealerAudit().getLineItemGroup(Section.TOTAL_CLAIM);
        Money dealerAskedAmount = null;
        dealerAskedAmount = summationGroup.getAcceptedTotal();
        if (dealerGroup == null) {
            dealerAskedAmount = summationGroup.getAcceptedTotal();
        } else {
            dealerAskedAmount = dealerGroup.getAcceptedTotal();
        }
        Money acceptedAmount = summationGroup.getAcceptedTotal();
        return acceptedAmount.breachEncapsulationOfAmount()
                .multiply(new BigDecimal(100))
                .divide(dealerAskedAmount.breachEncapsulationOfAmount(), 2, 5);

    }

    /**
     * Adjust "Total Credit Amount" for Reopened, Denied and Appealed claims
     */
    public void setDisbursedAmount() {
        List<Payment> latestPaidPayments = getAllPaidPayments();
        for (LineItemGroup lineItemGroup : this.getActiveClaimAudit().getPayment().getLineItemGroups()) {
            Money previousAcceptedAmount = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
            Money previousSCAcceptedAmount = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
            Money previousModifiersAcceptedAmount = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
            Money previousNetPriceAcceptedAmount = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
            for (Payment latestPaidPayment : latestPaidPayments) {
                if (latestPaidPayment != null) {
                    LineItemGroup prevLineItemGroup = latestPaidPayment.getLineItemGroup(lineItemGroup.getName());
                    if (prevLineItemGroup != null) {
                        previousAcceptedAmount = previousAcceptedAmount.plus(prevLineItemGroup.getTotalCreditAmount());
                       if( prevLineItemGroup.getName().equals(Section.OEM_PARTS)||prevLineItemGroup.getName().equals(Section.TOTAL_CLAIM))
                       {
                    	   if(prevLineItemGroup.getScTotalCreditAmount() != null) {
                    		   previousSCAcceptedAmount=previousSCAcceptedAmount.plus(prevLineItemGroup.getScTotalCreditAmount());
                    	   }
                    	   if( prevLineItemGroup.getName().equals(Section.OEM_PARTS)
                    			   && prevLineItemGroup.getNetPriceTotalCreditAmount() != null)
                    		   previousNetPriceAcceptedAmount=previousNetPriceAcceptedAmount.plus(prevLineItemGroup.getNetPriceTotalCreditAmount());
                       }                       
                       if(null != prevLineItemGroup.getModifierTotalCreditAmount()) {
                    	   previousModifiersAcceptedAmount=previousModifiersAcceptedAmount.plus(prevLineItemGroup.getModifierTotalCreditAmount());
                       }
                        
                    }
                }
            }
            if (previousAcceptedAmount != null) {
            	// previousAcceptedAmount will be the amount credited so far
            	Money amount = (lineItemGroup.getTotalCreditAmount().minus(previousAcceptedAmount));
            	lineItemGroup.setTotalCreditAmount(amount);
            }
            if( lineItemGroup.getName().equals(Section.OEM_PARTS)||lineItemGroup.getName().equals(Section.TOTAL_CLAIM))
            {
            	if (previousSCAcceptedAmount != null) {
            		// previousSCAcceptedAmount will be the amount credited so far
            		Money amount = (lineItemGroup.getScTotalCreditAmount().minus(previousSCAcceptedAmount));
            		lineItemGroup.setScTotalCreditAmount(amount);
            	}
            	if( lineItemGroup.getName().equals(Section.OEM_PARTS))
            			{
            		if (previousNetPriceAcceptedAmount != null) {
            			// previousAcceptedAmount will be the amount credited so far
            			Money amount = (lineItemGroup.getNetPriceTotalCreditAmount().minus(previousNetPriceAcceptedAmount));
            			lineItemGroup.setNetPriceTotalCreditAmount(amount);
            		}
            			}
            }
            if (previousModifiersAcceptedAmount != null && lineItemGroup.getModifierTotalCreditAmount() != null) {
            	// previousAcceptedAmount will be the amount credited so far
            	Money amount = (lineItemGroup.getModifierTotalCreditAmount().minus(previousModifiersAcceptedAmount));
            	lineItemGroup.setModifierTotalCreditAmount(amount);
            }
         
        }
    }
    
    public void setDisbursedAmountAfterLateFee() {
        List<Payment> latestPaidPayments = getAllPaidPayments();
        for (LineItemGroup lineItemGroup : this.getActiveClaimAudit().getPayment().getLineItemGroups()) {
        	if(lineItemGroup.getName().equals((Section.TOTAL_CLAIM))||lineItemGroup.getName().equals(Section.LATE_FEE))
        	{
        		Money previousAcceptedAmount = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
        		for (Payment latestPaidPayment : latestPaidPayments) {
        			if (latestPaidPayment != null) {
        				LineItemGroup prevLineItemGroup = latestPaidPayment.getLineItemGroup(lineItemGroup.getName());
        				if (prevLineItemGroup != null) {
        					previousAcceptedAmount = previousAcceptedAmount.plus(prevLineItemGroup.getTotalCreditAmount());
        				}
        			}
        		}
        		if (previousAcceptedAmount != null) {
        			// previousAcceptedAmount will be the amount credited so far
        			Money amount = (lineItemGroup.getTotalCreditAmount().minus(previousAcceptedAmount));
        			lineItemGroup.setTotalCreditAmount(amount);
        		}
        	}
        }
        
    }

    public String getClaimProcessedAs() {
        return this.getActiveClaimAudit().getClaimProcessedAs();
    }

    public void setClaimProcessedAs(String claimProcessedAs) {
        this.getActiveClaimAudit().setClaimProcessedAs(claimProcessedAs);
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

    public boolean wasClaimAutoAccepted() {
        boolean isAutoAccepted = false;
        boolean submitted = false;
        for (ClaimAudit claimAudit : this.getClaimAudits()) {
            if (submitted) {
                isAutoAccepted = claimAudit.getPreviousState().getState().equals(ClaimState.ACCEPTED.getState());
                break;
            }
            if (claimAudit.getPreviousState().getState().equals(ClaimState.SUBMITTED.getState())) {
                submitted = true;
            } else {
                submitted = false;
            }
        }
        return isAutoAccepted;
    }

    public CalendarDate getBuildDate() {
        InventoryItem item = getForItem();
        if (item != null) {
            return item.getBuiltOn();
        }
        return null;
    }

    public ManufacturingSiteInventory getManufacturingSite() {
        InventoryItem item = getForItem();
        if (item != null) {
            return item.getManufacturingSiteInventory();
        }
        return null;
    }

    public CalendarDate getLastModifiedClaimStatusDate() {
        ClaimAudit latestAudit = getLatestAudit();
        if (latestAudit != null) {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            return CalendarDate.from(df.format((Date) latestAudit.getUpdatedTime()), "MM/dd/yyyy");
        }
        return null;
    }

    public Boolean getMultiClaimMaintenance() {
        return multiClaimMaintenance;
    }

    public void setMultiClaimMaintenance(Boolean multiClaimMaintenance) {
        this.multiClaimMaintenance = multiClaimMaintenance;
    }

    /**
     * @param claimCompetitorModel the claimCompetitorModel to set
     */
    public void setClaimCompetitorModel(ClaimCompetitorModel claimCompetitorModel) {
        this.claimCompetitorModel = claimCompetitorModel;
    }

    /**
     * @return the compititorModel
     */
    public ClaimCompetitorModel getClaimCompetitorModel() {
        return claimCompetitorModel;
    }

    public void setLaborRoundupWindow(CalendarDuration laborRoundupWindow) {
        this.laborRoundupWindow = laborRoundupWindow;
    }

    public CalendarDuration getLaborRoundupWindow() {
        return laborRoundupWindow;
    }

    @SuppressWarnings("unused")
    public boolean isClaimInDeniedState() {
        boolean isClaimInDeniedState = false;
        if (this.getState().equals(ClaimState.DENIED) || (this.getState().equals(ClaimState.DENIED_AND_CLOSED))) {
            isClaimInDeniedState = true;
        } else if ((this.getState().equals(ClaimState.PENDING_PAYMENT_SUBMISSION) || this.getState().equals(ClaimState.PENDING_PAYMENT_RESPONSE)) && this.getClaimAudits() != null
                && this.getClaimAudits().size() > 2) {
            ClaimState previousClaimState = this.getClaimAudits().get(this.getClaimAudits().size() - 2)
                    .getPreviousState();
            if (previousClaimState.equals(ClaimState.DENIED)
                    || previousClaimState.equals(ClaimState.DENIED_AND_CLOSED)) {
                isClaimInDeniedState = true;
    } 
    }
        return isClaimInDeniedState;
    }


    public Boolean getWarrantyOrder() {
        return warrantyOrder;
    }

    public void setWarrantyOrder(Boolean warrantyOrder) {
        this.warrantyOrder = warrantyOrder;
    }

    public List<AlarmCode> getAlarmCodes() {
        return getActiveClaimAudit().getAlarmCodes();
    }

    public void setAlarmCodes(List<AlarmCode> alarmCodes) {
        this.getActiveClaimAudit().setAlarmCodes(alarmCodes);
    }

    public void setCommercialPolicy(Boolean commercialPolicy) {
        this.commercialPolicy = commercialPolicy;
    }

    public Boolean getCommercialPolicy() {
        return this.commercialPolicy;
    }

    public Boolean getFailureReportPending() {
        return failureReportPending;
    }

    public void setFailureReportPending(Boolean failureReportPending) {
        this.failureReportPending = failureReportPending;
    }

    public LimitOfAuthorityScheme getLoaScheme() {
        return loaScheme;
    }


    public void setLoaScheme(LimitOfAuthorityScheme loaScheme) {
        this.loaScheme = loaScheme;
    }

    public Boolean getBomUpdationNeeded() {
        return bomUpdationNeeded;
    }

    public void setBomUpdationNeeded(Boolean bomUpdationNeeded) {
        this.bomUpdationNeeded = bomUpdationNeeded;
    }

    public User getAssignToUser() {
        return getActiveClaimAudit().getAssignToUser();
    }

    public void setAssignToUser(User assignToUser) {
        this.getActiveClaimAudit().setAssignToUser(assignToUser);
    }

    public Boolean isPendingRecovery() {
        return pendingRecovery;
    }

    public void setPendingRecovery(Boolean pendingRecovery) {
        this.pendingRecovery = pendingRecovery;
    }

    public List<ClaimAttributes> getClaimAdditionalAttributes() {
        return claimAdditionalAttributes;
    }

    public void setClaimAdditionalAttributes(List<ClaimAttributes> claimAdditionalAttributes) {
        this.claimAdditionalAttributes = claimAdditionalAttributes;
    }

    public void addClaimAttributes(ClaimAttributes claimAttributes) {
        this.claimAdditionalAttributes.add(claimAttributes);
    }

    public Boolean isSupplierRecovery() {
        return this.supplierRecovery;
    }

    public Boolean getSupplierRecovery() {
        return supplierRecovery;
    }

    public void setSupplierRecovery(Boolean supplierRecovery) {
        this.supplierRecovery = supplierRecovery;
    }

    public Suppliers getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(Suppliers suppliers) {
        this.suppliers = suppliers;
    }

	public void setNcr(Boolean ncr) {
		this.ncr = ncr;
	}

	public Boolean isNcr() {
		if(ncr == null){
			return Boolean.FALSE;
		}
		return ncr;
	}

	public void setNcrWith30Days(Boolean ncrWith30Days) {
		this.ncrWith30Days = ncrWith30Days;
	}

	public Boolean isNcrWith30Days() {
		if(ncrWith30Days == null){
			return Boolean.FALSE;
		}
		return ncrWith30Days;
	}
	
	public void setInventoryClassFor30DayNcr(InventoryClass inventoryClassFor30DayNcr) {
		this.inventoryClassFor30DayNcr = inventoryClassFor30DayNcr;
	}
	
	public InventoryClass getInventoryClassFor30DayNcr() {
		return inventoryClassFor30DayNcr;
	}
	
    public Boolean getNcrClaimCheck()
    {
    	if((this.ncr !=null && this.ncr)|| (this.ncrWith30Days !=null && this.ncrWith30Days))
    	{
    		return true;
    	}
    	return false;
    }

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

    @ManyToOne(fetch = FetchType.LAZY)
    private BrandItem brandPartItem;

    public BrandItem getBrandPartItem() {
        return brandPartItem;
    }

    public void setBrandPartItem(BrandItem brandPartItem) {
        this.brandPartItem = brandPartItem;
    }

    public String getCausalPartBrandItemNumber() {
        return activeClaimAudit.getServiceInformation().getCausalBrandPart().getItemNumber();//getCausalPart().getBrandItemNumber(brand);
    }

	public Boolean getManualReviewConfigured() {
		return manualReviewConfigured;
	}

	public void setManualReviewConfigured(Boolean manualReviewConfigured) {
		this.manualReviewConfigured = manualReviewConfigured;
	}

	public String getPartSerialNumber() {
		return partSerialNumber;
	}

	public void setPartSerialNumber(String partSerialNumber) {
		this.partSerialNumber = partSerialNumber;
	}

	public String getPolicyCode() {
		return policyCode;
	}

	public void setPolicyCode(String policyCode) {
		this.policyCode = policyCode;
	}

    public CalendarDate getCoverageEndDate() {
        return coverageEndDate;
    }

    public void setCoverageEndDate(CalendarDate coverageEndDate) {
        this.coverageEndDate = coverageEndDate;
    }

    public Integer getHoursCovered() {
        return hoursCovered;
    }

    public void setHoursCovered(Integer hoursCovered) {
        this.hoursCovered = hoursCovered;
    }

    public CalendarDate getCreditDate() {
        return this.getActiveClaimAudit().getCreditDate();
    }

    public void setCreditDate(CalendarDate creditDate) {
        this.getActiveClaimAudit().setCreditDate(creditDate);
    }

	public StateMandates getStateMandate() {
		return stateMandate;
	}

	public void setStateMandate(StateMandates stateMandate) {
		this.stateMandate = stateMandate;
	}
	
	public boolean isLateFeeEnabledFrom61to90days() {
		return isLateFeeEnabledFrom61to90days;
	}

	public void setLateFeeEnabledFrom61to90days(
			boolean isLateFeeEnabledFrom61to90days) {
		this.isLateFeeEnabledFrom61to90days = isLateFeeEnabledFrom61to90days;
	}

	public boolean isLateFeeEnabledFrom91to120days() {
		return isLateFeeEnabledFrom91to120days;
	}

	public void setLateFeeEnabledFrom91to120days(
			boolean isLateFeeEnabledFrom91to120days) {
		this.isLateFeeEnabledFrom91to120days = isLateFeeEnabledFrom91to120days;
	}

	public String getPartReturnCommentsToDealer() {
		return this.getActiveClaimAudit().getPartReturnCommentsToDealer();
	}

	public void setPartReturnCommentsToDealer(String partReturnCommentsToDealer) {
		this.getActiveClaimAudit().setPartReturnCommentsToDealer(partReturnCommentsToDealer);
	}
	
	    
    public String getCustomerName() {
        String customerName = "";
        if (claimedItems != null && claimedItems.size() > 0) {
            ClaimedItem item = claimedItems.get(0);
            if (item != null && item.getItemReference() != null && item.getItemReference().isSerialized()) {
            	if(item.getItemReference().getReferredInventoryItem().getLatestBuyer() != null){
            		customerName = item.getItemReference().getReferredInventoryItem().getLatestBuyer().getName();
            	}
            }
        }
        return customerName;
    }
	public String getNumberOfDaysFromDatePaid() {
		if (getActiveClaimAudit().getPayment() != null
				&& getActiveClaimAudit().getPayment().getActiveCreditMemo() != null) {
			return String.valueOf(getActiveClaimAudit().getPayment()
					.getActiveCreditMemo().getCreditMemoDate()
					.through(Clock.today()).lengthInDaysInt());
		}
		return "";
	}

	public String getRemovedPartNumber() {
		try {
			String brand;
			brand = new HibernateCast<Dealership>().cast(getForDealer()).getBrand();
			return getActiveClaimAudit().getServiceInformation()
					.getServiceDetail().getHussmanPartsReplacedInstalled()
					.get(0).getReplacedParts().get(0)
					.getItemReference().getReferredItem().getBrandItemNumber(brand);
		} catch (NullPointerException e) {
			logger.error("NullPointerException Occured while fetching Removed Part Number during Search for Claims Paid Over 60 Days Old Without Part Return");
			return "";
		} catch(ArrayIndexOutOfBoundsException ae){
			logger.error("ArrayIndexOutOfBoundsException Occured while fetching Removed Part Number during Search for Claims Paid Over 60 Days Old Without Part Return");
			return "";
		}
	}
	
	public String getRemovedPartDescription() {
		try {
			return getActiveClaimAudit().getServiceInformation()
					.getServiceDetail().getHussmanPartsReplacedInstalled()
					.get(0).getReplacedParts().get(0)
					.getItemReference().getReferredItem().getDescription();
		} catch (NullPointerException e) {
			logger.error("NullPointerException Occured while fetching Removed Part Description during Search for Claims Paid Over 60 Days Old Without Part Return");
			return "";
		} catch (ArrayIndexOutOfBoundsException ae) {
			logger.error("ArrayIndexOutOfBoundsException Occured while fetching Removed Part Description during Search for Claims Paid Over 60 Days Old Without Part Return");
			return "";
		}
	}
	
	 public Set<String> getNotificationsToProcessor() {
	        return this.getActiveClaimAudit().getNotifications();
	    }
	  public boolean addNotifications(String notification) {
	        return this.getActiveClaimAudit().getNotifications().add(notification);
	    }

	public Boolean getEmission() {
		return emission;
	}

	public void setEmission(Boolean emission) {
		this.emission = emission;
	}
}
