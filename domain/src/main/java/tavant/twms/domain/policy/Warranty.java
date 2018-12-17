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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.DiscountType;
import tavant.twms.domain.common.Oem;
import tavant.twms.domain.inventory.DieselTierWaiver;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionType;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.common.Document;
import tavant.twms.security.AuditableColumns;
import tavant.twms.domain.policy.Customer;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class Warranty implements AuditableColumns{
    @Id
    @GeneratedValue(generator = "Warranty")
	@GenericGenerator(name = "Warranty", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "WARRANTY_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "for_item", insertable = false, updatable = false)
    private InventoryItem forItem;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    private Party customer;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    private AddressForTransfer addressForTransfer;
    
    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    private AddressForTransfer operatorAddressForTransfer;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "warranty")
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    @Filter(name="excludeInactive")
    private Set<RegisteredPolicy> policies = new HashSet<RegisteredPolicy>();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @Cascade({CascadeType.ALL,CascadeType.DELETE_ORPHAN})
    private MarketingInformation marketingInformation;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(value = { CascadeType.ALL })
    private InventoryTransaction forTransaction;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    @Column(nullable = true)
    private CalendarDate deliveryDate;  

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.policy.WarrantyStatus"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    private WarrantyStatus status;

    private boolean draft;

	@OneToMany(fetch = FetchType.LAZY)
    @org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private List<Document> attachments = new ArrayList<Document>();

	@ManyToOne(fetch = FetchType.LAZY,optional=true)
	private User filedBy;

	@ManyToOne(fetch = FetchType.LAZY,optional=true)
	private ServiceProvider forDealer;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @Transient
    private String registrationComments;

    @OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "for_warranty", nullable = false)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@IndexColumn(name = "list_index", nullable = false)
    @Filter(name="excludeInactive")
    private List<WarrantyAudit> warrantyAudits = new ArrayList<WarrantyAudit>();

    @ManyToOne
    private InventoryTransactionType transactionType;

    private String multiDRETRNumber;

    private String customerType;
    
    private String operatorType;
    
      
   
	/*
     * This should be maintained as a warranty attribute.
     * But as we do not have any domain for this, this is maintained here.
     * */
    private String modifyDeleteComments;
    
    @ManyToOne( fetch = FetchType.LAZY)
    private Customer operator;

    @ManyToOne( fetch = FetchType.LAZY)
    private Party installingDealer;
    
    @ManyToOne( fetch = FetchType.LAZY)
    private Oem oem;
    
    private String fleetNumber;
    
    @Column(name="EQUIPMENT_VIN")
    private String equipmentVIN;
    
    @Column(name="MANUAL_FLAG_DR")
    private Boolean manualFlagDr;
    
    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate installationDate;
  
    @OneToMany(mappedBy = "forWarranty")
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    private List<SelectedAdditionalMarketingInfo> selectedAddtlMktInfo = new ArrayList<SelectedAdditionalMarketingInfo>();
    
    @ManyToOne(fetch = FetchType.LAZY,optional=true)
    private ServiceProvider certifiedInstaller;
    
    @ManyToOne(fetch = FetchType.LAZY,optional=true)
    private Customer nonCertifiedInstaller;
    
    @OneToOne(fetch = FetchType.LAZY)
    @Cascade(value = { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
    private Address customerAddress;

    @OneToOne(fetch = FetchType.LAZY)
	@Cascade(CascadeType.ALL)
	private DieselTierWaiver dieselTierWaiver;
    
    private Boolean invalidItdrAttachment;
    
    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate filedDate;
    
    @Column(name="pdi_generated")
    private Boolean pdiGenerated;
    
    @OneToOne(fetch = FetchType.LAZY)
	@Cascade(CascadeType.ALL)
    private DiscountType discountType;
    
    private String discountNumber;
    
    private String discountPercentage;
    
	public CalendarDate getFiledDate() {
		return filedDate;
	}

	public void setFiledDate(CalendarDate filedDate) {
		this.filedDate = filedDate;
	}

	public Boolean getInvalidItdrAttachment() {
		return invalidItdrAttachment;
	}

	public void setInvalidItdrAttachment(Boolean invalidItdrAttachment) {
		this.invalidItdrAttachment = invalidItdrAttachment;
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

    public InventoryItem getForItem() {
        return this.forItem;
    }

    public void setForItem(InventoryItem forItem) {
        this.forItem = forItem;
    }

    public InventoryTransaction getForTransaction() {
        return this.forTransaction;
    }

    public void setForTransaction(InventoryTransaction forTransaction) {
        this.forTransaction = forTransaction;
    }

    public Set<RegisteredPolicy> getPolicies() {
        return this.policies;
    }

    public void setPolicies(Set<RegisteredPolicy> policies) {
        this.policies = policies;
    }

    public Party getCustomer() {
        return this.customer;
    }

    public void setCustomer(Party customer) {
        this.customer = customer;
    }

    public MarketingInformation getMarketingInformation() {
        return this.marketingInformation;
    }

    public void setMarketingInformation(MarketingInformation marketingInformation) {
        this.marketingInformation = marketingInformation;
    }

	public AddressForTransfer getAddressForTransfer() {
		return addressForTransfer;
	}

	public void setAddressForTransfer(AddressForTransfer addressForTransfer) {
		this.addressForTransfer = addressForTransfer;
	}

	public boolean isDraft() {
        return this.draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public CalendarDate getDeliveryDate() {
        return this.deliveryDate;
    }

    public void setDeliveryDate(CalendarDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public CalendarDate getEndDate() {
        CalendarDate lastDay = null;
        for (Policy policy : this.policies) {
            CalendarDuration warrantyPeriod = policy.getWarrantyPeriod();
            CalendarDate tillDate = warrantyPeriod.getTillDate();
            lastDay = lastDay == null ? tillDate : lastDay.compareTo(tillDate) == -1 ? tillDate
                    : lastDay;
        }
        return lastDay;
    }

    public CalendarDate getStartDate() {
        CalendarDate startDate = null;
        for (Policy policy : this.policies) {
            CalendarDuration warrantyPeriod = policy.getWarrantyPeriod();
            CalendarDate fromDate = warrantyPeriod.getFromDate();
            startDate = startDate == null ? fromDate
                    : startDate.compareTo(fromDate) == 1 ? fromDate : startDate;
        }
        return startDate;
    }

    public void setInventoryItem(InventoryItem forItem) {
        this.forItem = forItem;
        forItem.setWarranty(this);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("forItem", this.forItem)
                .toString();
    }

    public WarrantyStatus getStatus() {
        return this.status;
    }

    public void setStatus(WarrantyStatus status) {
        this.status = status;
    }

    /**
     * This API ensures that warranty is not in some intermediate status like
     * DRAFT/SUBMITTED
     * @return
     */
    public boolean isValidStatus() {
        if (this.status == null || WarrantyStatus.DRAFT.getStatus().equals(this.status.getStatus())
                || WarrantyStatus.DELETED.getStatus().equals(this.status.getStatus())) {
            return false;
        }
        return true;
    }

	public List<Document> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
	}

	public User getFiledBy() {
		return filedBy;
	}

	public void setFiledBy(User filedBy) {
		this.filedBy = filedBy;
	}

	public ServiceProvider getForDealer() {
		return forDealer;
	}

	public void setForDealer(ServiceProvider forDealer) {
		this.forDealer = forDealer;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public String getRegistrationComments() {
        return registrationComments;
    }

    public void setRegistrationComments(String registrationComments) {
        this.registrationComments = registrationComments;
    }

    public List<WarrantyAudit> getWarrantyAudits() {
        return warrantyAudits;
    }

    public void setWarrantyAudits(List<WarrantyAudit> warrantyAudits) {
        this.warrantyAudits = warrantyAudits;
    }

    public WarrantyAudit getLatestAudit(){
        if (warrantyAudits.isEmpty()) {
			return null;
		}else{
            return getWarrantyAudits().get(getWarrantyAudits().size()-1);
        }
    }

    public InventoryTransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(InventoryTransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getMultiDRETRNumber() {
        return multiDRETRNumber;
    }

    public void setMultiDRETRNumber(String multiDRETRNumber) {
        this.multiDRETRNumber = multiDRETRNumber;
    }

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getModifyDeleteComments() {
		return modifyDeleteComments;
	}

	public void setModifyDeleteComments(String modifyDeleteComments) {
		this.modifyDeleteComments = modifyDeleteComments;
	}

	public Customer getOperator() {
		return operator;
	}

	public void setOperator(Customer operator) {
		this.operator = operator;
	}

	public Party getInstallingDealer() {
		return installingDealer;
	}

	public void setInstallingDealer(Party installingDealer) {
		this.installingDealer = installingDealer;
	}

	public Oem getOem() {
		return oem;
	}

	public void setOem(Oem oem) {
		this.oem = oem;
	}

	public String getFleetNumber() {
		return fleetNumber;
	}

	public void setFleetNumber(String fleetNumber) {
		this.fleetNumber = fleetNumber;
	}

	public String getEquipmentVIN() {
		return equipmentVIN;
	}

	public void setEquipmentVIN(String equipmentVIN) {
		this.equipmentVIN = equipmentVIN;
	}

	public CalendarDate getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(CalendarDate installationDate) {
		this.installationDate = installationDate;
	}

	public List<SelectedAdditionalMarketingInfo> getSelectedAddtlMktInfo() {
		return selectedAddtlMktInfo;
	}

	public void setSelectedAddtlMktInfo(
			List<SelectedAdditionalMarketingInfo> selectedAddtlMktInfo) {
		this.selectedAddtlMktInfo = selectedAddtlMktInfo;
	}

	public ServiceProvider getCertifiedInstaller() {
		return certifiedInstaller;
	}

	public void setCertifiedInstaller(ServiceProvider certifiedInstaller) {
		this.certifiedInstaller = certifiedInstaller;
	}

	public Customer getNonCertifiedInstaller() {
		return nonCertifiedInstaller;
	}

	public void setNonCertifiedInstaller(Customer nonCertifiedInstaller) {
		this.nonCertifiedInstaller = nonCertifiedInstaller;
	}

	public AddressForTransfer getOperatorAddressForTransfer() {
		return operatorAddressForTransfer;
	}

	public void setOperatorAddressForTransfer(AddressForTransfer operatorAddressForTransfer) {
		this.operatorAddressForTransfer = operatorAddressForTransfer;
	}

	public String getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}

	public Address getCustomerAddress() {
		return customerAddress;
	}

	public void setCustomerAddress(Address customerAddress) {
		this.customerAddress = customerAddress;
	}

	public DieselTierWaiver getDieselTierWaiver() {
		return dieselTierWaiver;
	}

	public void setDieselTierWaiver(DieselTierWaiver dieselTierWaiver) {
		this.dieselTierWaiver = dieselTierWaiver;
	}

	public Boolean getManualFlagDr() {
		return manualFlagDr;
	}

	public void setManualFlagDr(Boolean manualFlagDr) {
		this.manualFlagDr = manualFlagDr;
	}

	public Boolean getPdiGenerated() {
		return pdiGenerated;
	}

	public void setPdiGenerated(Boolean pdiGenerated) {
		this.pdiGenerated = pdiGenerated;
	}

	public String getDiscountNumber() {
		return discountNumber;
	}

	public void setDiscountNumber(String discountNumber) {
		this.discountNumber = discountNumber;
	}

	public String getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(String discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public DiscountType getDiscountType() {
		return discountType;
	}

	public void setDiscountType(DiscountType discountType) {
		this.discountType = discountType;
	}
}
