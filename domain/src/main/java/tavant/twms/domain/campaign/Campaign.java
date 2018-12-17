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
package tavant.twms.domain.campaign;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import javax.validation.constraints.NotNull;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.campaign.relateCampaigns.RelateCampaign;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.I18NCampaignText;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.common.Document;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.authz.infra.SecurityHelper;
import tavant.twms.domain.orgmodel.NationalAccount;



import com.domainlanguage.time.CalendarDate;

/**
 * @author Kiran.Kollipara
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class Campaign implements AuditableColumns,BusinessUnitAware{
	@Id
	@GeneratedValue(generator = "Campaign")
	@GenericGenerator(name = "Campaign", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CAMPAIGN_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	// Basic Campaign Info
	@NotNull(message = "{error.campaign.serialNumberRequired}")
	private String code;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL }, optional = true)
	@JoinColumn(name = "campaign_class")
	private CampaignClass campaignClass;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	@NotNull
	private CalendarDate fromDate;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	@NotNull
	private CalendarDate tillDate;
	
	@Transient
	private int totalSerialNumberFixed=0;
	
	@ManyToMany
	private Set<Label> labels = new HashSet<Label>();

	@SuppressWarnings("unused")
	@Transient
	private String description;	
    
 
	@Column(name = "BUDGETED_AMOUNT")
	private Double budgetedAmount;

	public BigDecimal getBudgetedAmount() {
		BigDecimal bdTest = new BigDecimal(budgetedAmount);
		bdTest = bdTest.setScale(2, BigDecimal.ROUND_HALF_UP);		 
		return  bdTest;
	}

	public void setBudgetedAmount(Double budgetedAmount) {	 
		this.budgetedAmount =budgetedAmount;
	}
	
	@OneToOne
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private CampaignCoverage campaignCoverage;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")	
	private CalendarDate buildFromDate;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")	
	private CalendarDate buildTillDate;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "CAMPAIGN_OEM_PARTS")
	private List<OEMPartToReplace> oemPartsToReplace = new ArrayList<OEMPartToReplace>();

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "CAMPAIGN_NONOEM_PARTS")
	private List<NonOEMPartToReplace> nonOEMpartsToReplace= new ArrayList<NonOEMPartToReplace>();

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "CAMPAIGN_MISC_PARTS")
	private List<NonOEMPartToReplace> miscPartsToReplace = new ArrayList<NonOEMPartToReplace>();

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinColumn(name = "FOR_CAMPAIGN",nullable=true )
    private List<HussPartsToReplace> hussPartsToReplace = new ArrayList<HussPartsToReplace>();

    @OneToMany(fetch = FetchType.LAZY)
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	@JoinTable(name = "CAMPAIGNS_IN_RELATED_CAMPAIGN", joinColumns = @JoinColumn(name = "CAMPAIGN"),
	inverseJoinColumns = @JoinColumn(name = "RELATED_CAMPAIGNS"))
    private List<RelateCampaign> relatedCampaign = new ArrayList<RelateCampaign>();
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "for_campaign", nullable = false, updatable = false,insertable = true)
    @Cascade( { org.hibernate.annotations.CascadeType.ALL,org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@IndexColumn(name = "list_index", nullable = false)
	private List<CampaignAudit> campaignAudits = new ArrayList<CampaignAudit>();

	// Notification Details
	private boolean notifyDealer;

	private boolean notifyCustomer;

	private boolean notifiyDealerByEmail;

	private boolean notificationsGenerated;

	private String status;
	
	@Column(nullable = true, length = 4000)
	private String comments;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private CampaignServiceDetail campaignServiceDetail = new CampaignServiceDetail();

	@OneToMany(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private List<Document> attachments = new ArrayList<Document>();

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "CAMPAIGN_DESCRIPTION", nullable = false)
	private List<I18NCampaignText> i18nCampaignTexts = new ArrayList<I18NCampaignText>();

	@ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "campaign_for_nationalaccounts", joinColumns = { @JoinColumn(name = "campaign") }, inverseJoinColumns = { @JoinColumn(name = "for_national_accounts") })
	private Set<NationalAccount> applicableNationalAccounts = new HashSet<NationalAccount>();
	
	
	@OneToMany(mappedBy = "campaign",fetch = FetchType.LAZY)
    private List<CampaignNotification> campaignNotifications = new ArrayList<CampaignNotification>();

	public boolean isValidDuration(CalendarDate startDate, CalendarDate endDate) {
		if (startDate == null && endDate == null) {
			return true;
		} else if (startDate != null && endDate != null) {
			return startDate.compareTo(endDate) != 1;
		} else {
			return false;
		}
	}

	public List<OEMPartToReplace> getAllOEMPartsReplace(){

		if(this.hussPartsToReplace != null && this.hussPartsToReplace.size() > 0 ){
			List<OEMPartToReplace> partsList = new ArrayList<OEMPartToReplace>();
			for (HussPartsToReplace element : hussPartsToReplace) {
				for (OEMPartToReplace removedPart : element.getRemovedParts()) {
						partsList.add(removedPart);
				}

			}
			return partsList;

		} else{
			return this.oemPartsToReplace;
		}


	}

	public CampaignClass getCampaignClass() {
		return campaignClass;
	}

	public void setCampaignClass(CampaignClass campaignClass) {
		this.campaignClass = campaignClass;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		String description_locale="";
		for (I18NCampaignText i18nCampaignText : this.i18nCampaignTexts) {
			if (i18nCampaignText!=null && i18nCampaignText.getLocale()!=null &&
					i18nCampaignText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && i18nCampaignText.getDescription() != null) {
				description_locale = i18nCampaignText.getDescription();
				break;
			}
			else if(i18nCampaignText !=null && i18nCampaignText.getLocale()!=null &&
					i18nCampaignText.getLocale().equalsIgnoreCase("en_US")) {
				description_locale = i18nCampaignText.getDescription();
			}
		}

		return description_locale;
	}

	public String getDescription(String locale) {
		String description_locale="";
		for (I18NCampaignText i18nCampaignText : this.i18nCampaignTexts) {
			if (i18nCampaignText!=null && i18nCampaignText.getLocale()!=null
					&& i18nCampaignText.getLocale().equalsIgnoreCase(locale)
					&& i18nCampaignText.getDescription() != null) {
				description_locale = i18nCampaignText.getDescription();
				break;
			}
		}

		return description_locale;
	}

	public Long getId() {
		return id;
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

	public boolean isNotifiyDealerByEmail() {
		return notifiyDealerByEmail;
	}

	public void setNotifiyDealerByEmail(boolean notifiyDealerByEmail) {
		this.notifiyDealerByEmail = notifiyDealerByEmail;
	}

	public boolean isNotifyCustomer() {
		return notifyCustomer;
	}

	public void setNotifyCustomer(boolean notifyCustomer) {
		this.notifyCustomer = notifyCustomer;
	}

	public boolean isNotifyDealer() {
		return notifyDealer;
	}

	public void setNotifyDealer(boolean notifyDealer) {
		this.notifyDealer = notifyDealer;
	}

	public CalendarDate getBuildFromDate() {
		return buildFromDate;
	}

	public void setBuildFromDate(CalendarDate buildFromDate) {
		this.buildFromDate = buildFromDate;
	}

	public CalendarDate getBuildTillDate() {
		return buildTillDate;
	}

	public void setBuildTillDate(CalendarDate buildTillDate) {
		this.buildTillDate = buildTillDate;
	}

	public CalendarDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(CalendarDate fromDate) {
		this.fromDate = fromDate;
	}

	public CalendarDate getTillDate() {
		return tillDate;
	}

	public void setTillDate(CalendarDate tillDate) {
		this.tillDate = tillDate;
	}

	public CampaignCoverage getCampaignCoverage() {
		return campaignCoverage;
	}

	public void setCampaignCoverage(CampaignCoverage campaignCoverage) {
		this.campaignCoverage = campaignCoverage;
	}

	public List<OEMPartToReplace> getOemPartsToReplace() {
		return oemPartsToReplace;
	}

	public void setOemPartsToReplace(List<OEMPartToReplace> oemPartsToReplace) {
		this.oemPartsToReplace = oemPartsToReplace;
	}

	public List<NonOEMPartToReplace> getNonOEMpartsToReplace() {
		return nonOEMpartsToReplace;
	}

	public void setNonOEMpartsToReplace(
			List<NonOEMPartToReplace> nonOEMpartsToReplace) {
		this.nonOEMpartsToReplace = nonOEMpartsToReplace;
	}

	public boolean isNotificationsGenerated() {
		return notificationsGenerated;
	}

	public void setNotificationsGenerated(boolean notificationsGenerated) {
		this.notificationsGenerated = notificationsGenerated;
	}

	public CampaignServiceDetail getCampaignServiceDetail() {
		return campaignServiceDetail;
	}

	public void setCampaignServiceDetail(CampaignServiceDetail serviceDetail) {
		campaignServiceDetail = serviceDetail;
	}
	public List<Document> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
	}


	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public List<I18NCampaignText> getI18nCampaignTexts() {
		return i18nCampaignTexts;
	}

	public void setI18nCampaignTexts(List<I18NCampaignText> campaignTexts) {
		i18nCampaignTexts = campaignTexts;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<HussPartsToReplace> getHussPartsToReplace() {
		return hussPartsToReplace;
	}

	public void setHussPartsToReplace(List<HussPartsToReplace> hussPartsToReplace) {
		this.hussPartsToReplace = hussPartsToReplace;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public List<NonOEMPartToReplace> getMiscPartsToReplace() {
		return miscPartsToReplace;
	}

	public void setMiscPartsToReplace(List<NonOEMPartToReplace> miscPartsToReplace) {
		this.miscPartsToReplace = miscPartsToReplace;
	}

	public List<RelateCampaign> getRelatedCampaign() {
		return relatedCampaign;
	}

	public void setRelatedCampaign(List<RelateCampaign> relatedCampaign) {
		this.relatedCampaign = relatedCampaign;
	}
	
	public List<CampaignAudit> getCampaignAudits() {
		return campaignAudits;
	}

	public void setCampaignAudits(List<CampaignAudit> campaignAudits) {
		this.campaignAudits = campaignAudits;
	}
	
	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public Set<NationalAccount> getApplicableNationalAccounts() {
		return applicableNationalAccounts;
	}

	public void setApplicableNationalAccounts(
			Set<NationalAccount> selectedNationalAccounts) {
		this.applicableNationalAccounts = selectedNationalAccounts;
	}
	
	public Set<Label> getLabels() {
		return labels;
	}

	public void setLabels(Set<Label> labels) {
		this.labels = labels;
	}

	public String getFieldModAge() {

		int days = fromDate.breachEncapsulationOf_day();
		int month = fromDate.breachEncapsulationOf_month();
		int year = fromDate.breachEncapsulationOf_year();
		Calendar initiatingDate = new GregorianCalendar();
		Calendar currentDate = new GregorianCalendar();
		initiatingDate.set(year, month-1, days);
		long totalDays=((currentDate.getTime().getTime() - initiatingDate
				.getTime().getTime()) / (1000 * 60 * 60 * 24));
		
		return ++totalDays+" days"; 

	}

	public int getTotalSerialNumberFixed() {
		if (campaignNotifications != null && !campaignNotifications.isEmpty()) {
			for (CampaignNotification campaignNotification : campaignNotifications) {
				if (campaignNotification.getNotificationStatus()
						.equalsIgnoreCase("COMPLETE")) {
					this.totalSerialNumberFixed++;
				}
			}
		}
		return totalSerialNumberFixed;
	}

	public void setCampaignNotifications(List<CampaignNotification> campaignNotifications) {
		this.campaignNotifications = campaignNotifications;
	}

	public List<CampaignNotification> getCampaignNotifications() {
		return campaignNotifications;
	}
	public String getNotificationStatusTODisplay(String item) {
		String TempNotificationForDisplay = "PENDING";
		for (CampaignNotification notification : campaignNotifications) {
			if (notification.getItem().getSerialNumber().equals(item)) {
				TempNotificationForDisplay = notification
						.getNotificationStatus();
			}
		}
		return TempNotificationForDisplay;
	}
	
	public String getCampaignStatusToDisplay(String item) {
		String TempCampStatusForDisplay = "Draft";
		for (CampaignNotification notification : campaignNotifications) {
			if (notification.getItem().getSerialNumber().equals(item)) {
				if (getStatus().equals("Active")) {
					TempCampStatusForDisplay = notification
							.getStatuswithReason();
				} else {
					TempCampStatusForDisplay=getStatus();
				}
			}
		}
		return TempCampStatusForDisplay;

	}	
	
	public List<InventoryItem> getItems()
	{
		Set<InventoryItem> distinctInventoryItems = new HashSet<InventoryItem>(this.campaignCoverage.getItems());
		for(CampaignNotification campaignNotification:campaignNotifications)
		{
			distinctInventoryItems.add(campaignNotification.getItem());
		}
		return new ArrayList<InventoryItem>(distinctInventoryItems);
	}
}

