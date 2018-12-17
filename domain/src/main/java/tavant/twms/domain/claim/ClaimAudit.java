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
package tavant.twms.domain.claim;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;
import tavant.twms.domain.alarmcode.AlarmCode;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.common.*;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.common.Document;
import javax.persistence.*;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import static tavant.twms.domain.claim.ClaimState.*;

/**
 * Represents audit information for claim object.
 *
 * @author roopali.agrawal
 */
@Entity
@PropertiesWithNestedCurrencyFields({"serviceInformation", "payment"})
public class ClaimAudit {
    @Id
    @GeneratedValue(generator = "ClaimAudit")
    @GenericGenerator(name = "ClaimAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "CLAIM_AUDIT_SEQ"),
            @Parameter(name = "initial_value", value = "1000"),
            @Parameter(name = "increment_size", value = "20")})
    private Long id;

    @Version
    private int version;

    @Type(type = "tavant.twms.infra.CalendarTimeUserType")
    private TimePoint updatedOn;

    private Date updatedTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private User updatedBy;

    private String internalComments;

    private String externalComments;

    private String decision;

    private Boolean multiClaimMaintenance = false;
    
    private Boolean isLateFeeApprovalRequired = false;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.claim.ClaimState"),
            @Parameter(name = "type", value = "" + Types.VARCHAR)})
    private ClaimState previousState;

    private boolean internal;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    private ClaimSnapshot claimSnapshot;

    private transient Claim previousClaimSnapshot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "for_claim", insertable = false, updatable = false)
    private Claim forClaim;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    private Payment payment;

    private String workOrderNumber;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate failureDate;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate repairDate;
 
    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate repairStartDate;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate installationDate;

    // FIXME Remove this field. Field not used as it serves the same purpose as
    // installationDate.
    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate purchaseDate;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.claim.ClaimState"),
            @Parameter(name = "type", value = "" + Types.VARCHAR)})
    private ClaimState state;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    private ServiceInformation serviceInformation = new ServiceInformation();

    @Column(length = 4000)
    private String probableCause;

    @Column(length = 4000)
    private String workPerformed;

    @Column(length = 4000)
    private String otherComments;

    @Column(length = 4000)
    private String conditionFound;

    private String internalComment;

    private String externalComment;

    @OneToMany(fetch = FetchType.LAZY)
    @Sort(type = SortType.NATURAL)
    @org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private SortedSet<RuleFailure> ruleFailures = new TreeSet<RuleFailure>();

    @OneToMany(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})
    private List<Document> attachments = new ArrayList<Document>();

    private boolean serviceManagerAccepted = false;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    @Sort(type = SortType.NATURAL)
    @JoinTable(name = "CLAIM_AUDIT_USER_PRO_COMMENTS")
    private SortedSet<UserComment> userProcessComments = new TreeSet<UserComment>();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cascade({CascadeType.ALL})
    private AcceptanceReason acceptanceReason;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cascade({CascadeType.ALL})
    private AcceptanceReasonForCP acceptanceReasonForCp;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "claim_audit_to_reject_reason", joinColumns = { @JoinColumn(name = "claim_audit_id") }, inverseJoinColumns = { @JoinColumn(name = "reject_reason_id") })
    private List<RejectionReason> rejectionReasons;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "claim_audit_to_put_on_hold", joinColumns = { @JoinColumn(name = "claim_audit_id") }, inverseJoinColumns = { @JoinColumn(name = "put_on_hold_id") })
	List<PutOnHoldReason> putOnHoldReasons =  new ArrayList<PutOnHoldReason>();
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "claim_audit_to_req_from_user", joinColumns = { @JoinColumn(name = "claim_audit_id") }, inverseJoinColumns = { @JoinColumn(name = "req_from_user_id") })
	List<RequestInfoFromUser> requestInfoFromUser =  new ArrayList<RequestInfoFromUser>();
    
    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    @Sort(type = SortType.NATURAL)
    private List<AlarmCode> alarmCodes = new ArrayList<AlarmCode>();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cascade({CascadeType.ALL})
    private AccountabilityCode accountabilityCode;

    private Boolean travelTripConfig = new Boolean(false);

    private Boolean travelHrsConfig = new Boolean(false);

    private Boolean travelDisConfig = new Boolean(false);

    private Boolean oemConfig = new Boolean(false);

    private Boolean nonOemConfig = new Boolean(false);

    private Boolean miscPartsConfig = new Boolean(false);

    private Boolean mealsConfig = new Boolean(false);

    private Boolean parkingConfig = new Boolean(false);

    private Boolean itemDutyConfig = new Boolean(false);

    private Boolean laborConfig = new Boolean(false);

    private Boolean cpReviewed = Boolean.FALSE;

    private Boolean perDiemConfig = new Boolean(false);

    private Boolean rentalChargesConfig = new Boolean(false);

    private Boolean additionalTravelHoursConfig = new Boolean(false);

    private Boolean localPurchaseConfig = new Boolean(false);

    private Boolean tollsConfig = new Boolean(false);

    private Boolean otherFreightDutyConfig = new Boolean(false);
    
    private Boolean handlingFeeConfig = new Boolean(false);
    
    private Boolean travelConfig = new Boolean(false);
    
    private Boolean transportation = new Boolean(false);

	private Boolean othersConfig = new Boolean(false);
    
    private Boolean isPriceFetchDown = new Boolean(false);
    
	private Boolean isPriceFetchReturnZero = new Boolean(false);
    
    private String priceFetchErrorMessage ;
    
     
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cascade({CascadeType.ALL})
    private SellingEntity sellingEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Address ownerInformation;

    private String claimProcessedAs;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private User assignToUser;
    
    @Column(columnDefinition = "numeric(19,6)")
    private BigDecimal exchangeRate;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate creditDate;
    

    @Column(length = 4000)
    private String partReturnCommentsToDealer;
    
    @ElementCollection(targetClass=String.class)
    private Set<String> notifications = new HashSet<String>();
    
    public String getPartReturnCommentsToDealer() {
		return partReturnCommentsToDealer;
	}

	public void setPartReturnCommentsToDealer(String partReturnCommentsToDealer) {
		this.partReturnCommentsToDealer = partReturnCommentsToDealer;
	}


    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private OrganizationAddress servicingLocation;
    
    public OrganizationAddress getServicingLocation() {
		return servicingLocation;
	}

	public void setServicingLocation(OrganizationAddress servicingLocation) {
		this.servicingLocation = servicingLocation;
	}


	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getExternalComments() {
        return this.externalComments;
    }

    public void setExternalComments(String externalComments) {
        this.externalComments = externalComments;
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

    public boolean isInternal() {
        return this.internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public String getInternalComments() {
        return this.internalComments;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
    
    public String getTruncatedExternalComments() {
        if (StringUtils.hasText(this.externalComments) && this.externalComments.length() > 60) {
            return this.externalComments.substring(0, 60).concat("...");
        } else {
            return this.externalComments;
        }
    }

    public String getTruncatedInternalComments() {
        if (StringUtils.hasText(this.internalComments) && this.internalComments.length() > 60) {
            return this.internalComments.substring(0, 60).concat("...");
        } else {
            return this.internalComments;
        }
    }

    public void setInternalComments(String internalComments) {
        this.internalComments = internalComments;
    }

    public String getPreviousClaimSnapshotAsString() {
        return (this.claimSnapshot == null) ? null :
                this.claimSnapshot.getPreviousClaimSnapshotAsString();
    }

    public void setPreviousClaimSnapshotAsString(String previousSnapshot) {
        if (this.claimSnapshot == null)
            this.claimSnapshot = new ClaimSnapshot();
        this.claimSnapshot.setPreviousClaimSnapshotAsString(previousSnapshot);
    }

    public ClaimState getPreviousState() {
        return this.previousState;
    }

    public void setPreviousState(ClaimState previousState) {
        this.previousState = previousState;
    }

    public User getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public TimePoint getUpdatedOn() {
        return this.updatedOn;
    }

    public String getUpdatedDateOn() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d yyyy");
        return sdf.format(this.updatedOn.asJavaUtilDate());
    }

    public void setUpdatedOn(TimePoint updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("updated on", this.updatedOn)
                .append("updated by", this.updatedBy).append("internal comments",
                        this.internalComments)
                .append("internal", this.internal).append("external comments",
                        this.externalComments)
                .append("previous state", this.previousState).toString();
    }


    public Claim getPreviousClaimSnapshot() {
        return this.previousClaimSnapshot;
    }

    public void setPreviousClaimSnapshot(Claim claimSnapshot) {
        this.previousClaimSnapshot = claimSnapshot;
    }

    public Claim getForClaim() {
        return this.forClaim;
    }

    public void setForClaim(Claim forClaim) {
        this.forClaim = forClaim;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public boolean isCommentViewableByDealer(ClaimState claimState) {
        List<ClaimState> notViewableClaimStatesForDealer = new ArrayList<ClaimState>();
        notViewableClaimStatesForDealer.add(TRANSFERRED);
        notViewableClaimStatesForDealer.add(ADVICE_REQUEST);
        notViewableClaimStatesForDealer.add(ON_HOLD);
        notViewableClaimStatesForDealer.add(ON_HOLD_FOR_PART_RETURN);
        notViewableClaimStatesForDealer.add(PROCESSOR_REVIEW);
        notViewableClaimStatesForDealer.add(APPROVED);
        notViewableClaimStatesForDealer.add(REJECTED);
        notViewableClaimStatesForDealer.add(WAITING_FOR_PART_RETURNS);
        notViewableClaimStatesForDealer.add(REJECTED_PART_RETURN);
        notViewableClaimStatesForDealer.add(REACCEPTED);
        notViewableClaimStatesForDealer.add(PENDING_PAYMENT_SUBMISSION);
        notViewableClaimStatesForDealer.add(PENDING_PAYMENT_RESPONSE);
        notViewableClaimStatesForDealer.add(REOPENED);
        notViewableClaimStatesForDealer.add(CP_REVIEW);
        notViewableClaimStatesForDealer.add(CP_TRANSFER);
        notViewableClaimStatesForDealer.add(MANUAL_REVIEW);
        notViewableClaimStatesForDealer.add(ACCEPTED);
        notViewableClaimStatesForDealer.add(REJECTED);
        notViewableClaimStatesForDealer.add(REPLIES);
        notViewableClaimStatesForDealer.add(ACCEPTED);
        notViewableClaimStatesForDealer.add(DENIED);
        notViewableClaimStatesForDealer.add(DELETED);

        if (notViewableClaimStatesForDealer.contains(claimState)) {
            return false;
        }
        return true;
    }

    public static ClaimAudit replicateClaimAudit(ClaimAudit activeClaimAudit) {
        ClaimAudit audit = new ClaimAudit();

        audit.setWorkOrderNumber(activeClaimAudit.getWorkOrderNumber());
        audit.setFailureDate(activeClaimAudit.getFailureDate());
        audit.setRepairDate(activeClaimAudit.getRepairDate());
        audit.setRepairDate(activeClaimAudit.getRepairStartDate());
        audit.setInstallationDate(activeClaimAudit.getInstallationDate());
        audit.setPurchaseDate(activeClaimAudit.getPurchaseDate());
        audit.setState(activeClaimAudit.getState());
        audit.setUpdatedBy(activeClaimAudit.getUpdatedBy());
        audit.setProbableCause(activeClaimAudit.getProbableCause());
        audit.setWorkPerformed(activeClaimAudit.getWorkPerformed());
        audit.setOtherComments(activeClaimAudit.getOtherComments());
        audit.setConditionFound(activeClaimAudit.getConditionFound());
        audit.setServiceManagerAccepted(activeClaimAudit.isServiceManagerAccepted());
        audit.setAcceptanceReason(activeClaimAudit.getAcceptanceReason());
        audit.setAcceptanceReasonForCp(activeClaimAudit.getAcceptanceReasonForCp());
        if(null != audit.getRejectionReasons() && audit.getRejectionReasons().size()>0){
        	audit.setRejectionReasons(activeClaimAudit.getRejectionReasons());	
        }
		if(null != audit.getPutOnHoldReasons() && audit.getPutOnHoldReasons().size()>0){
			 audit.setPutOnHoldReasons(activeClaimAudit.getPutOnHoldReasons());
		}
		if(null != audit.getRequestInfoFromUser() && audit.getRequestInfoFromUser().size()>0){
			audit.setRequestInfoFromUser(activeClaimAudit.getRequestInfoFromUser());
		}
        audit.setAccountabilityCode(activeClaimAudit.getAccountabilityCode());
        audit.setTravelTripConfig(activeClaimAudit.getTravelTripConfig());
        audit.setTravelHrsConfig(activeClaimAudit.getTravelHrsConfig());
        audit.setTravelDisConfig(activeClaimAudit.getTravelDisConfig());
        audit.setOemConfig(activeClaimAudit.getOemConfig());
        audit.setNonOemConfig(activeClaimAudit.getNonOemConfig());
        audit.setMiscPartsConfig(activeClaimAudit.getMiscPartsConfig());
        audit.setMealsConfig(activeClaimAudit.getMealsConfig());
        audit.setParkingConfig(activeClaimAudit.getParkingConfig());
        audit.setItemDutyConfig(activeClaimAudit.getItemDutyConfig());
        audit.setLaborConfig(activeClaimAudit.getLaborConfig());
        audit.setCpReviewed(activeClaimAudit.getCpReviewed());
        audit.setPerDiemConfig(activeClaimAudit.getPerDiemConfig());
        audit.setRentalChargesConfig(activeClaimAudit.getRentalChargesConfig());
        audit.setAdditionalTravelHoursConfig(activeClaimAudit.getAdditionalTravelHoursConfig());
        audit.setLocalPurchaseConfig(activeClaimAudit.getLocalPurchaseConfig());
        audit.setTollsConfig(activeClaimAudit.getTollsConfig());
        audit.setOtherFreightDutyConfig(activeClaimAudit.getOtherFreightDutyConfig());
        audit.setOthersConfig(activeClaimAudit.getOthersConfig());
        audit.setInvoiceNumber(activeClaimAudit.getInvoiceNumber());
        audit.setSellingEntity(activeClaimAudit.getSellingEntity());
        audit.setClaimProcessedAs(activeClaimAudit.getClaimProcessedAs());
        audit.setCreditDate(activeClaimAudit.getCreditDate());
        audit.setIsPriceFetchDown(activeClaimAudit.getIsPriceFetchDown());
        audit.setIsPriceFetchReturnZero(activeClaimAudit.getIsPriceFetchReturnZero());
        audit.setPriceFetchErrorMessage(activeClaimAudit.getPriceFetchErrorMessage());

        audit.setTransportation(activeClaimAudit.getTransportation());
        audit.setIsLateFeeApprovalRequired(activeClaimAudit.getIsLateFeeApprovalRequired());

        audit.setServicingLocation(activeClaimAudit.getServicingLocation());

        

        if (activeClaimAudit.getOwnerInformation() != null) {
            audit.setOwnerInformation(activeClaimAudit.getOwnerInformation());
        }

        audit.setServiceInformation(activeClaimAudit.getServiceInformation().clone());

        for (AlarmCode alarmCode : activeClaimAudit.getAlarmCodes()) {
            audit.getAlarmCodes().add(alarmCode.clone());
        }

        for (RuleFailure ruleFailure : activeClaimAudit.getRuleFailures()) {
            audit.getRuleFailures().add(ruleFailure.clone());
        }

        for (Document document : activeClaimAudit.getAttachments()) {
            audit.getAttachments().add(document);
        }

        for (UserComment userComment : activeClaimAudit.getUserProcessComments()) {
            audit.getUserProcessComments().add(userComment.clone());
        }

        return audit;
    }

    public Boolean getMultiClaimMaintenance() {
        return multiClaimMaintenance;
    }

    public void setMultiClaimMaintenance(Boolean multiClaimMaintenance) {
        this.multiClaimMaintenance = multiClaimMaintenance;
    }
    
    public Payment getPayment() {
        return payment;
    }
    
    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(String workOrderNumber) {
        this.workOrderNumber = workOrderNumber;
    }

    public CalendarDate getFailureDate() {
        return failureDate;
    }

    public void setFailureDate(CalendarDate failureDate) {
        this.failureDate = failureDate;
    }

    public CalendarDate getRepairDate() {
        return repairDate;
    }

    public void setRepairDate(CalendarDate repairDate) {
        this.repairDate = repairDate;
    }

    public CalendarDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(CalendarDate installationDate) {
        this.installationDate = installationDate;
    }

    public CalendarDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(CalendarDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public ClaimState getState() {
        return state;
    }

    public void setState(ClaimState state) {
        this.state = state;
    }

    public ServiceInformation getServiceInformation() {
        return serviceInformation;
    }

    public void setServiceInformation(ServiceInformation serviceInformation) {
        this.serviceInformation = serviceInformation;
    }

    public String getProbableCause() {
        return probableCause;
    }

    public void setProbableCause(String probableCause) {
        this.probableCause = probableCause;
    }

    public String getWorkPerformed() {
        return workPerformed;
    }

    public void setWorkPerformed(String workPerformed) {
        this.workPerformed = workPerformed;
    }

    public String getOtherComments() {
        return otherComments;
    }

    public void setOtherComments(String otherComments) {
        this.otherComments = otherComments;
    }

    public String getConditionFound() {
        return conditionFound;
    }

	/*
	 * Updated the method for SLMS-774 
	 * updatedCondFound will replace the special characters
	 * in condition_found column 
	 * This column will be sent to SAP
	 */
    public void setConditionFound(String conditionFound) {
		if (conditionFound != null) {
			String updatedCondFound = conditionFound
					.replaceAll(
						"\\u0022|\u0025|\u0026|\u0027|\u002C|\u003A|\u003B|\u003C|\u003E|"
					   + "\u00A6|\u00A7|\u00A9|\u00AE|\u00B0|\u00B1|\u00B4|\u00B6|\u00B7|"
					   + "\u00B8|\u00F7|\u02DC|\u2013|\u2014|\u2018|\u2019|\u201C|\u201D|"
					   + "\u201E|\u2022|\u2044|\u2122|\u2190|\u2191|\u2192|\u2193|\u2194",
						"");
			this.conditionFound = updatedCondFound;
		} else {
        this.conditionFound = conditionFound;
    }
	}

    public String getInternalComment() {
        return internalComment;
    }

    public void setInternalComment(String internalComment) {
        this.internalComment = internalComment;
    }

    public String getExternalComment() {
        return externalComment;
    }

    public void setExternalComment(String externalComment) {
        this.externalComment = externalComment;
    }

    public SortedSet<RuleFailure> getRuleFailures() {
        return ruleFailures;
    }

    public void setRuleFailures(SortedSet<RuleFailure> ruleFailures) {
        this.ruleFailures = ruleFailures;
    }

    public List<Document> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Document> attachments) {
        this.attachments = attachments;
    }

    public boolean isServiceManagerAccepted() {
        return serviceManagerAccepted;
    }

    public void setServiceManagerAccepted(boolean serviceManagerAccepted) {
        this.serviceManagerAccepted = serviceManagerAccepted;
    }

    public SortedSet<UserComment> getUserProcessComments() {
        return userProcessComments;
    }

    public void setUserProcessComments(SortedSet<UserComment> userProcessComments) {
        this.userProcessComments = userProcessComments;
    }

    public AcceptanceReason getAcceptanceReason() {
        return acceptanceReason;
    }

    public void setAcceptanceReason(AcceptanceReason acceptanceReason) {
        this.acceptanceReason = acceptanceReason;
    }

    public AcceptanceReasonForCP getAcceptanceReasonForCp() {
        return acceptanceReasonForCp;
    }

    public void setAcceptanceReasonForCp(AcceptanceReasonForCP acceptanceReasonForCp) {
        this.acceptanceReasonForCp = acceptanceReasonForCp;
    }   

    public List<RejectionReason> getRejectionReasons() {
		return rejectionReasons;
	}

	public void setRejectionReasons(List<RejectionReason> rejectionReasons) {
		this.rejectionReasons = rejectionReasons;
	}

	public List<AlarmCode> getAlarmCodes() {
        return alarmCodes;
    }

    public void setAlarmCodes(List<AlarmCode> alarmCodes) {
        this.alarmCodes = alarmCodes;
    }

    public AccountabilityCode getAccountabilityCode() {
        return accountabilityCode;
    }

    public void setAccountabilityCode(AccountabilityCode accountabilityCode) {
        this.accountabilityCode = accountabilityCode;
    }

    public Boolean getTravelTripConfig() {
        return travelTripConfig;
    }

    public void setTravelTripConfig(Boolean travelTripConfig) {
        this.travelTripConfig = travelTripConfig;
    }

    public Boolean getTravelHrsConfig() {
        return travelHrsConfig;
    }

    public void setTravelHrsConfig(Boolean travelHrsConfig) {
        this.travelHrsConfig = travelHrsConfig;
    }

    public Boolean getTravelDisConfig() {
        return travelDisConfig;
    }

    public void setTravelDisConfig(Boolean travelDisConfig) {
        this.travelDisConfig = travelDisConfig;
    }

    public Boolean getOemConfig() {
        return oemConfig;
    }

    public void setOemConfig(Boolean oemConfig) {
        this.oemConfig = oemConfig;
    }

    public Boolean getNonOemConfig() {
        return nonOemConfig;
    }

    public void setNonOemConfig(Boolean nonOemConfig) {
        this.nonOemConfig = nonOemConfig;
    }

    public Boolean getMiscPartsConfig() {
        return miscPartsConfig;
    }

    public void setMiscPartsConfig(Boolean miscPartsConfig) {
        this.miscPartsConfig = miscPartsConfig;
    }

    public Boolean getMealsConfig() {
        return mealsConfig;
    }

    public void setMealsConfig(Boolean mealsConfig) {
        this.mealsConfig = mealsConfig;
    }

    public Boolean getParkingConfig() {
        return parkingConfig;
    }

    public void setParkingConfig(Boolean parkingConfig) {
        this.parkingConfig = parkingConfig;
    }

    public Boolean getItemDutyConfig() {
        return itemDutyConfig;
    }

    public void setItemDutyConfig(Boolean itemDutyConfig) {
        this.itemDutyConfig = itemDutyConfig;
    }

    public Boolean getLaborConfig() {
        return laborConfig;
    }

    public void setLaborConfig(Boolean laborConfig) {
        this.laborConfig = laborConfig;
    }

    public Boolean getCpReviewed() {
        return cpReviewed;
    }

    public void setCpReviewed(Boolean cpReviewed) {
        this.cpReviewed = cpReviewed;
    }

    public Boolean getPerDiemConfig() {
        return perDiemConfig;
    }

    public void setPerDiemConfig(Boolean perDiemConfig) {
        this.perDiemConfig = perDiemConfig;
    }

    public Boolean getRentalChargesConfig() {
        return rentalChargesConfig;
    }

    public void setRentalChargesConfig(Boolean rentalChargesConfig) {
        this.rentalChargesConfig = rentalChargesConfig;
    }

    public Boolean getAdditionalTravelHoursConfig() {
        return additionalTravelHoursConfig;
    }

    public void setAdditionalTravelHoursConfig(Boolean additionalTravelHoursConfig) {
        this.additionalTravelHoursConfig = additionalTravelHoursConfig;
    }

    public Boolean getLocalPurchaseConfig() {
        return localPurchaseConfig;
    }

    public void setLocalPurchaseConfig(Boolean localPurchaseConfig) {
        this.localPurchaseConfig = localPurchaseConfig;
    }

    public Boolean getTollsConfig() {
        return tollsConfig;
    }

    public void setTollsConfig(Boolean tollsConfig) {
        this.tollsConfig = tollsConfig;
    }

    public Boolean getOtherFreightDutyConfig() {
        return otherFreightDutyConfig;
    }

    public void setOtherFreightDutyConfig(Boolean otherFreightDutyConfig) {
        this.otherFreightDutyConfig = otherFreightDutyConfig;
    }

    public Boolean getOthersConfig() {
        return othersConfig;
    }

    public void setOthersConfig(Boolean othersConfig) {
        this.othersConfig = othersConfig;
    }
    
    public Boolean getHandlingFeeConfig() {
		return handlingFeeConfig;
	}

	public void setHandlingFeeConfig(Boolean handlingFeeConfig) {
		this.handlingFeeConfig = handlingFeeConfig;
	}
	
    public Boolean getTravelConfig() {
		return travelConfig;
	}

	public void setTravelConfig(Boolean travelConfig) {
		this.travelConfig = travelConfig;
	}

	public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public SellingEntity getSellingEntity() {
        return sellingEntity;
    }

    public void setSellingEntity(SellingEntity sellingEntity) {
        this.sellingEntity = sellingEntity;
    }

    public Address getOwnerInformation() {
        return ownerInformation;
    }

    public void setOwnerInformation(Address ownerInformation) {
        this.ownerInformation = ownerInformation;
    }

    public String getClaimProcessedAs() {
        return claimProcessedAs;
    }

    public void setClaimProcessedAs(String claimProcessedAs) {
        this.claimProcessedAs = claimProcessedAs;
    }

    public User getAssignToUser() {
        return assignToUser;
    }

    public void setAssignToUser(User assignToUser) {
        this.assignToUser = assignToUser;
    }

	public CalendarDate getRepairStartDate() {
		return repairStartDate;
	}

	public void setRepairStartDate(CalendarDate repairStartDate) {
		this.repairStartDate = repairStartDate;
	}

    public CalendarDate getCreditDate() {
        return creditDate;
    }

    public void setCreditDate(CalendarDate creditDate) {
        this.creditDate = creditDate;
    }
    public Boolean getIsPriceFetchDown() {
    	if(this.isPriceFetchDown==null){
    		return Boolean.FALSE;
    	}
		return isPriceFetchDown;
	}

	public void setIsPriceFetchDown(Boolean isPriceFetchDown) {
		this.isPriceFetchDown = isPriceFetchDown;
	}

	public Boolean getIsPriceFetchReturnZero() {
		if(this.isPriceFetchReturnZero==null){
    		return Boolean.FALSE;
    	}
		return isPriceFetchReturnZero;
	}

	public void setIsPriceFetchReturnZero(Boolean isPriceFetchReturnZero) {
		this.isPriceFetchReturnZero = isPriceFetchReturnZero;
	}
	
	public Boolean isPriceZero() {
		for(HussmanPartsReplacedInstalled parts : this.serviceInformation.serviceDetail.getHussmanPartsReplacedInstalled()){
			for(InstalledParts installedPart : parts.getHussmanInstalledParts()){
				if(installedPart.getPricePerUnit()!=null && installedPart.getPricePerUnit().isZero()){
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	public String getPriceFetchErrorMessage() {
		return priceFetchErrorMessage;
	}

	public void setPriceFetchErrorMessage(String priceFetchErrorMessage) {
		this.priceFetchErrorMessage = priceFetchErrorMessage;
	}

	public Boolean getTransportation() {
		return transportation;
	}

	public void setTransportation(Boolean transportation) {
		this.transportation = transportation;
	}

    public Boolean getIsLateFeeApprovalRequired() {
		return isLateFeeApprovalRequired;
	}

	public void setIsLateFeeApprovalRequired(Boolean isLateFeeApprovalRequired) {
		this.isLateFeeApprovalRequired = isLateFeeApprovalRequired;
	}	

	public List<PutOnHoldReason> getPutOnHoldReasons() {
		return putOnHoldReasons;
	}

	public void setPutOnHoldReasons(List<PutOnHoldReason> putOnHoldReasons) {
		this.putOnHoldReasons = putOnHoldReasons;
	}

	public List<RequestInfoFromUser> getRequestInfoFromUser() {
		return requestInfoFromUser;
	}

	public void setRequestInfoFromUser(List<RequestInfoFromUser> requestInfoFromUser) {
		this.requestInfoFromUser = requestInfoFromUser;
	}

	public Set<String> getNotifications() {
		return notifications;
	}

	public void setNotifications(Set<String> notifications) {
		this.notifications = notifications;
	}

	public String getRejectionReasonForInboxView(){
		if(null != getRejectionReasons() && getRejectionReasons().size()>0){
        	return getRejectionReasons().get(0).getDescription();	
        }
		return "";
		
	}
}
