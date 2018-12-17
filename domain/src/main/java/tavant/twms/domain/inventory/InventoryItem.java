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

package tavant.twms.domain.inventory;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterDefs;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.common.ManufacturingSiteInventory;
import tavant.twms.domain.common.Oem;
import tavant.twms.domain.common.SourceWarehouse;
import tavant.twms.domain.customReports.CustomReportAnswer;
import tavant.twms.domain.orgmodel.AddressBookType;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.DirectCustomer;
import tavant.twms.domain.orgmodel.InterCompany;
import tavant.twms.domain.orgmodel.NationalAccount;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.OwnershipState;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.common.Document;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * Represents an inventory item that can be identified using a serial number
 * (VIN for an automobilie, chasis number)
 * 
 * @author kamal.govindraj
 * 
 */
@Entity
@FilterDefs({
		@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") }),
		@FilterDef(name = "currentOwner", parameters = { @ParamDef(name = "invOwner", type = "java.lang.Long") })
})
@Filters({
    @Filter(name = "excludeInactive"),
	@Filter(name = "bu_name", condition = "business_unit_info in (:name)"),
	@Filter(name = "currentOwner", condition ="(current_owner in (:invOwner) or ship_to in (:invOwner))")
})
public class InventoryItem implements BusinessUnitAware, AuditableColumns {
	@Id
	@GeneratedValue(generator = "InventoryItem")
	@GenericGenerator(name = "InventoryItem", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "INVENTORY_ITEM_SEQ"),
			@Parameter(name = "initial_value", value = "1500"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	private String serialNumber;

	private String factoryOrderNumber;
	
	private String vinNumber;
		
	@ManyToOne(fetch = FetchType.LAZY)
	private Item ofType;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	@Column(nullable = true)
	private CalendarDate builtOn;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	@Column(nullable = true)
	private CalendarDate registrationDate;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	@Column(nullable = true)
	private CalendarDate deliveryDate;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	@Column(nullable = true)
	private CalendarDate shipmentDate;

	@ManyToOne(fetch = FetchType.LAZY)
	private InventoryType type;
	
	private Long hoursOnMachine = new Long(0);
	
	 @OneToMany(mappedBy = "item",fetch = FetchType.LAZY)
     private List<CampaignNotification> campaignNotifications;

	@OneToMany(mappedBy = "transactedItem", fetch = FetchType.LAZY)
	@Cascade( { CascadeType.SAVE_UPDATE })
	@Filter(name = "excludeInactive")
	private List<InventoryTransaction> transactionHistory = new ArrayList<InventoryTransaction>();

	@ManyToOne(fetch = FetchType.LAZY)
	private InventoryItemCondition conditionType;

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "for_item", nullable = false)
	@IndexColumn(name = "list_index", nullable = false)
        @Filter(name="excludeInactive")
	private List<Warranty> warrantyHistory = new ArrayList<Warranty>();
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "inventory_item", nullable = false)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@IndexColumn(name = "list_index", nullable = false)
	private List<ComponentAuditHistory> componentAuditHistory = new ArrayList<ComponentAuditHistory>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "partOf")
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	private List<InventoryItemComposition> composedOf = new ArrayList<InventoryItemComposition>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
	@Cascade({ CascadeType.ALL,CascadeType.DELETE_ORPHAN })
	@OrderBy
	private List<InventoryItemAdditionalComponents> additionalComponents = new ArrayList<InventoryItemAdditionalComponents>();

	@ManyToMany
	private Set<Label> labels = new HashSet<Label>();

	@ManyToOne(fetch = FetchType.LAZY)
	private OwnershipState ownershipState;

	@ManyToMany
	@JoinTable(name = "inv_item_attr_vals", joinColumns = { @JoinColumn(name = "inv_item_id") }, inverseJoinColumns = { @JoinColumn(name = "inv_item_attr_val_id") })
	private List<InventoryItemAttributeValue> inventoryItemAttrVals;

	@ManyToOne(fetch = FetchType.LAZY)
	private Organization currentOwner;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Organization shipTo;

	@OneToOne(fetch = FetchType.LAZY)
	@Cascade(CascadeType.ALL)
	private DieselTierWaiver waiverDuringDr;
	
	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "inventory_item", nullable = false)
	@OrderBy("sequenceNumber")
	private List<InventoryComment> inventoryComments = new ArrayList<InventoryComment>();
	
	@OneToMany(fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({org.hibernate.annotations.CascadeType.ALL})
    @JoinTable(name="INVENTORY_ITEM_ATTACHMENTS", joinColumns = { @JoinColumn(name = "INVENTORY_ITEM") })
    private List<Document> attachments = new ArrayList<Document>();
	
	//Stdw_Reserve_Amount_Year1
	private BigDecimal stdwReserveAmountYear1;
	private BigDecimal stdwReserveAmountYear2;
	private BigDecimal aopRate1;
	private BigDecimal aopRate2;
	private BigDecimal aopTargetRate1;
	private BigDecimal aopTargetRate2;
	private BigDecimal orderGrossValue;
	private BigDecimal orderNetValue;
	private String currency;
	private String nomenclature;
	
	public List<Document> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
		
	}

	public List<InventoryComment> getInventoryComments() {
		return inventoryComments;
	}

	public void setInventoryComments(List<InventoryComment> inventoryComments) {
		this.inventoryComments = inventoryComments;
	}

    public Boolean getInventoryCommentExists() {
        return this.inventoryComments.size() > 0;
    }

	public DieselTierWaiver getWaiverDuringDr() {
		return waiverDuringDr;
	}

	public void setWaiverDuringDr(DieselTierWaiver waiverDuringDr) {
		this.waiverDuringDr = waiverDuringDr;
	}

	public Organization getShipTo() {
		return shipTo;
	}

	public void setShipTo(Organization shipTo) {
		this.shipTo = shipTo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	private Party latestBuyer;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	@OneToMany(mappedBy = "forInventory", fetch = FetchType.LAZY)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<CustomReportAnswer> reportAnswers;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@Cascade( { CascadeType.ALL })
	private SourceWarehouse sourceWarehouse;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate wntyStartDate;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate wntyEndDate;

	private boolean serializedPart = false;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate installationDate;
	
	@Type(type = "org.hibernate.type.EnumType", parameters = {
			@Parameter(name = "enumClass", value = "tavant.twms.domain.inventory.InventoryItemSource"),
			@Parameter(name = "type", value = "" + Types.VARCHAR) })
	private InventoryItemSource source;
	
	@ManyToOne( fetch = FetchType.LAZY)
    private Customer operator;

    @ManyToOne( fetch = FetchType.LAZY)
    private Party installingDealer;
    
    @ManyToOne( fetch = FetchType.LAZY)
    private Oem oem;    
 
    @ManyToOne(fetch = FetchType.LAZY)
    private Warranty latestWarranty;
    
    private String fleetNumber;  
    
    private String dieselTier;
    
    private String disclaimerInfo;
    
    private Boolean isDisclaimer;
	
	public Boolean getIsDisclaimer() {
		if(isDisclaimer == null){
			return false;
		}
		return isDisclaimer;
	}

	public void setIsDisclaimer(Boolean isDisclaimer) {
		this.isDisclaimer = isDisclaimer;
	}

	public String getDisclaimerInfo() {
		return disclaimerInfo;
	}

	public void setDisclaimerInfo(String disclaimerInfo) {
		this.disclaimerInfo = disclaimerInfo;
	}

	private Boolean preOrderBooking = Boolean.FALSE;
    
    private String marketingGroupCode;
    
    private String billToPurchaseOrder;
    private Date orderReceivedDate;
    private Date actualCtsDate;
	private String discAuthorizationNumber;
    private BigDecimal discountPercent;
    private String orderType;
    private BigDecimal mdeCapacity;
    private String modelPower;
    private String brandType;
    private Date itaBookDate;
    private Date itaBookReportDate;
    private Date itaDeliveryDate;
    private Date itaDeliveryReportDate;
    private String operatingUnit;
    @OneToMany(cascade = { javax.persistence.CascadeType.ALL })
    private List<Option> options = new ArrayList<Option>();
    
    @OneToMany(cascade = { javax.persistence.CascadeType.ALL })
    private List<PartGroup> partGroups = new ArrayList<PartGroup>();

	@Transient
    private InventoryService inventoryService;
	
	@Transient
    private String componentDescription;
    
	public String getComponentDescription() {
		return componentDescription;
	}

	public void setComponentDescription(String componentDescription) {
		this.componentDescription = componentDescription;
	}
    
	public InventoryService getInventoryService() {
		return inventoryService;
	}
	
	@Required
	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public InventoryItemSource getSource() {
		return source;
	}

	public void setSource(InventoryItemSource source) {
		this.source = source;
	}
	
	public boolean getSerializedPart() {
		return serializedPart ;
	}

	public void setSerializedPart(boolean serializedPart ) {
		this.serializedPart  = serializedPart ;
	}

	public CalendarDate getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(CalendarDate installationDate) {
		this.installationDate = installationDate;
	}

	public CalendarDate getWntyStartDate() {
		return wntyStartDate;
	}

	public void setWntyStartDate(CalendarDate wntyStartDate) {
		this.wntyStartDate = wntyStartDate;
	}

	public CalendarDate getWntyEndDate() {
		return wntyEndDate;
	}

	public void setWntyEndDate(CalendarDate wntyEndDate) {
		this.wntyEndDate = wntyEndDate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	private ManufacturingSiteInventory manufacturingSiteInventory;

	private Boolean pendingWarranty = Boolean.FALSE;

	public OwnershipState getOwnershipState() {
		return ownershipState;
	}

	public void setOwnershipState(OwnershipState ownershipState) {
		this.ownershipState = ownershipState;
	}

	public List<InventoryItemAttributeValue> getInventoryItemAttrVals() {
		return inventoryItemAttrVals;
	}

	public void setInventoryItemAttrVals(
			List<InventoryItemAttributeValue> inventoryItemAttrVals) {
		this.inventoryItemAttrVals = inventoryItemAttrVals;
	}

	public Set<Label> getLabels() {
		return labels;
	}

	public void setLabels(Set<Label> labels) {
		this.labels = labels;
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

	public void setMyVersion(int version) {
		this.version = version;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Item getOfType() {
		return ofType;
	}

	public void setOfType(Item ofType) {
		this.ofType = ofType;
	}

	public Party getOwnedBy() {
		return this.latestBuyer;
	}

	public Organization getDealer() {
		Organization owner = getCurrentOwner();
		if (owner == null && getSerializedPart()) {
			owner = inventoryService.findInventoryItemForMajorComponent(getId()).getOwner();
		}
		return owner;
	}

	public CalendarDate getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(CalendarDate registrationDate) {
		this.registrationDate = registrationDate;
	}

	public CalendarDate getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(CalendarDate deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public InventoryType getType() {
		return type;
	}

	public void setType(InventoryType type) {
		this.type = type;
	}

	public Long getHoursOnMachine() {
		return hoursOnMachine;
	}

	public void setHoursOnMachine(Long hoursOnMachine) {
		this.hoursOnMachine = hoursOnMachine;
	}

	public List<InventoryTransaction> getTransactionHistory() {
		return transactionHistory;
	}

	public void setTransactionHistory(
			List<InventoryTransaction> transactionHistory) {
		this.transactionHistory = transactionHistory;
	}

	public InventoryItemCondition getConditionType() {
		return conditionType;
	}

	public void setConditionType(InventoryItemCondition condition) {
		conditionType = condition;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", id).append(
				"serial number", serialNumber).append("registration date",
				registrationDate).append("delivery date", deliveryDate).append(
				"hours on machine", hoursOnMachine).toString();
	}
	
	@Override
	public boolean equals(Object inventoryItem) {
		if (inventoryItem!=null && this.getId().equals(((InventoryItem)inventoryItem).getId())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isRegisteredForWarranty() {
		return registrationDate != null;
	}

	public boolean isDelivered() {
		return deliveryDate != null;
	}

	public boolean isShipped() {
		return shipmentDate != null;
	}
	
	public void setShipmentDate(CalendarDate shipmentDate) {
		this.shipmentDate = shipmentDate;
	}

	public CalendarDate getShipmentDate() {
		return shipmentDate;
	}

	public boolean isInStock() {
		return type.getType().equals("STOCK");
	}

	public boolean isRetailed() {
		return type.getType().equals("RETAIL");
	}

	public Warranty getWarranty() {
        ListIterator li = warrantyHistory.listIterator(warrantyHistory.size());

        while (li.hasPrevious()) {
            Warranty warranty = (Warranty) li.previous();
            if (warranty != null && warranty.isValidStatus()) {
                return warranty;
            }
        }
		return null;
	}

	public Warranty getPreviousWarranty(Long warrantyId) {
        ListIterator li = warrantyHistory.listIterator(warrantyHistory.size());
        while (li.hasPrevious()) {
            Warranty warranty = (Warranty) li.previous();
            if (warranty != null && warranty.getId().longValue() != warrantyId.longValue() && warranty.isValidStatus()) {
                return warranty;
            }
        }
        return null;
	}

	public void setWarranty(Warranty warranty) {
		warrantyHistory.add(warranty);
	}

	public CalendarDate getBuiltOn() {
		return builtOn;
	}

	public void setBuiltOn(CalendarDate builtOn) {
		this.builtOn = builtOn;
	}

	public List<Warranty> getWarrantyHistory() {
		return warrantyHistory;
	}

	public void setWarrantyHistory(List<Warranty> warrantyHistory) {
		this.warrantyHistory = warrantyHistory;
	}

	public List<InventoryItemComposition> getComposedOf() {
		return composedOf;
	}

	public void setComposedOf(List<InventoryItemComposition> composedOf) {
		this.composedOf = composedOf;
	}

	public InventoryItemComposition include(InventoryItem part) {
		for(InventoryItemComposition composition : getComposedOf()){
			if(composition.getPart().equals(part)){
				composition.getD().setActive(part.getD().isActive());
				getComposedOf().remove(composition);
				break;
			}
		}
		InventoryItemComposition inventoryItemComposition = new InventoryItemComposition(part);
		inventoryItemComposition.setPartOf(this);
		inventoryItemComposition.getD().setActive(part.getD().isActive());
		if(part.getD().isActive()){
			getComposedOf().add(inventoryItemComposition);
		}
		return inventoryItemComposition;
	}
	public InventoryItemComposition include(InventoryItem part,
			InventoryItemComposition component) {
		InventoryItemComposition inventoryItemComposition = new InventoryItemComposition(
				part);
		inventoryItemComposition.setPartOf(this);
		inventoryItemComposition.setManufacturer(component.getManufacturer());
		inventoryItemComposition.setSequenceNumber(component
				.getSequenceNumber());
		inventoryItemComposition.setComponentSerialType(component
				.getComponentSerialType());
		inventoryItemComposition.setStatus(component.getStatus());
		inventoryItemComposition.getD().setActive(component.getD().isActive());
		inventoryItemComposition.setSerialTypeDescription(component.getSerialTypeDescription());
			getComposedOf().add(inventoryItemComposition);
		return inventoryItemComposition;
	}
	
	public ComponentAuditHistory includeAuditHystory(InventoryItem part,
			ComponentAuditHistory componentAudit) {
		ComponentAuditHistory componentAuditHistory = new ComponentAuditHistory(
				part);
		componentAuditHistory.setInventoryItem(this);
		componentAuditHistory.getD().setActive(part.getD().isActive());
		componentAuditHistory.setComponentPartNumber(componentAudit
				.getComponentPartNumber());
		componentAuditHistory.setComponentPartSerialNumber(componentAudit
				.getComponentPartSerialNumber());
		componentAuditHistory.setComponentSerialType(componentAudit
				.getComponentSerialType());
		componentAuditHistory.setTransactionType(componentAudit
				.getTransactionType().toString());
		componentAuditHistory.setSequenceNumber(componentAudit
				.getSequenceNumber());
		componentAuditHistory.setManufacturer(componentAudit.getManufacturer());
		componentAuditHistory.setSerialTypeDescription(componentAudit
				.getSerialTypeDescription());
		componentAuditHistory.getD().setActive(componentAudit.getD().isActive());
			getComponentAuditHistory().add(componentAuditHistory);
		return componentAuditHistory;
	}
	
	public boolean includes(InventoryItem aSerializedPart) {
		List<InventoryItemComposition> compositions = getComposedOf();
		for (InventoryItemComposition eachComposition : compositions) {
			InventoryItem part = eachComposition.getPart();
			if (part.equals(aSerializedPart) || part.includes(aSerializedPart)) {
				return true;
			}
		}
		return false;
	}

	public InventoryItemComposition getStructuralRelationship(
			InventoryItem aSerializedPart) {
		if (includes(aSerializedPart)) {
			List<InventoryItemComposition> compositions = getComposedOf();
			for (InventoryItemComposition composition : compositions) {
				InventoryItem part = composition.getPart();
				if (part.equals(aSerializedPart)) {
					return composition;
				} else if (part.includes(aSerializedPart)) {
					return part.getStructuralRelationship(aSerializedPart);
				}
			}
		}
		return null;
	}

	public SerializedItemReplacement replaceSerializedPart(
			InventoryItem oldPart, InventoryItem newPart,
			ItemReplacementReason dueTo) {
		Assert.notNull(oldPart, "Specify the old part in part replacement");
		Assert.notNull(newPart, "Specify the new part in part replacement");
		Assert.isTrue(includes(oldPart),
				"Replaced part is not part of this serialized item");
		InventoryItemComposition structuralRelationship = getStructuralRelationship(oldPart);		
		return structuralRelationship.replacePart(newPart, dueTo);
	}

	/**
	 * TODO replace with a service method.
	 */
	public Warranty getDraftWarranty() {
		if (!warrantyHistory.isEmpty()) {
			Warranty latest = warrantyHistory.get(warrantyHistory.size() - 1);
			if (WarrantyStatus.DRAFT.equals(latest.getStatus())) {
				return latest;
			}
		}
		return null;
	}

	public List<Warranty> getWarranties(List<String> transactionTypes) {
		List<Warranty> warrantyHistory = this.getWarrantyHistory();
		List<Warranty> warranties = new ArrayList<Warranty>();
		if (warrantyHistory != null && warrantyHistory.size() > 0) {
			for (Warranty warranty : warrantyHistory) {
				for (String transType : transactionTypes) {
					if (warranty != null && transType.equals(warranty.getTransactionType()
							.getTrnxTypeValue())) {
						warranties.add(warranty);
					}
				}
			}
		}
		return warranties;
	}

	public Warranty getWarrantyForTransaction(Long transactionId) {
		List<Warranty> warrantyList = this.getWarrantyHistory();
		for (Warranty warranty : warrantyList) {
			if (warranty.getForTransaction().getId().longValue() == transactionId) {
				return warranty;
			}
		}
		return null;

	}

	public CalendarDate getDeliveryReportDate() {
		List<String> drTransactionTypes = new ArrayList<String>();
		drTransactionTypes.add(InvTransationType.DR.getTransactionType());
		drTransactionTypes
				.add(InvTransationType.DR_MODIFY.getTransactionType());
		drTransactionTypes.add(InvTransationType.DR_RENTAL.getTransactionType());
		Warranty deliveryWarranty = this.getWarranties(drTransactionTypes).get(
				0);
		return deliveryWarranty.getDeliveryDate();
	}

	public Long getNumberOfETRs() {
		Long counter = 0L;
		for (InventoryTransaction inventoryTransaction : transactionHistory) {
			if (InvTransationType.ETR.getTransactionType().equals(
					inventoryTransaction.getInvTransactionType()
							.getTrnxTypeValue())) {
				counter++;
			} else if (InvTransationType.ETR_DELETE.getTransactionType()
					.equals(
							inventoryTransaction.getInvTransactionType()
									.getTrnxTypeValue())) {
				counter--;
			}
		}
		return counter;
	}

	public InventoryTransaction getLatestTransactionForATransactionType(
			List<String> transactionTypes) {
		Collections.sort(transactionHistory);
		for (InventoryTransaction invTransaction : transactionHistory) {
			for (String transType : transactionTypes) {
				if (transType.equals(invTransaction.getInvTransactionType()
						.getTrnxTypeValue())) {
					return invTransaction;
				}
			}
		}
		return null;
	}

	public InventoryTransaction getLatestTransaction() {
			return getLatestTransactionForATransactionType((String)null);
	}

	public InventoryTransaction getLatestTransactionForATransactionType(
			String transactionType) {
		Collections.sort(transactionHistory);
		if (transactionHistory.isEmpty()) {
			return null;
		}
		if(transactionType == null)
			return transactionHistory.get(0);

		for (InventoryTransaction invTransaction : transactionHistory) {
			if (transactionType.equals(invTransaction.getInvTransactionType()
					.getTrnxTypeValue())) {
				return invTransaction;
			}
		}
		return null;
	}

	public String getOwnedByName() {
		if (getLatestBuyer().isDealer()) {
			return getLatestBuyer().getName();
		} else if (getLatestBuyer().isCustomer()) {
			Customer c = new HibernateCast<Customer>().cast(transactionHistory.get(0).getBuyer());
            return c.getCompanyName() + "(" + c.getCustomerId() + ")";
		} else {
			return null;
		}
	}

	public Warranty getLatestWarranty() {
		return latestWarranty;
	}
	
	public void setLatestWarranty(Warranty latestWarranty) {
		this.latestWarranty = latestWarranty;
	}

	
	
	public Organization getCurrentOwner() {
		return currentOwner;
	}

	public void setCurrentOwner(Organization currentOwner) {
		this.currentOwner = currentOwner;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public Party getLatestBuyer() {
		return latestBuyer;
	}

	public void setLatestBuyer(Party latestBuyer) {
		this.latestBuyer = latestBuyer;
	}

	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public ManufacturingSiteInventory getManufacturingSiteInventory() {
		return manufacturingSiteInventory;
	}

	public void setManufacturingSiteInventory(
			ManufacturingSiteInventory manufacturingSiteInventory) {
		this.manufacturingSiteInventory = manufacturingSiteInventory;
	}

	@Transient
	public String getCustomerType() {
		String customerType = "";
		if (this.getLatestBuyer() != null) {
			Party party = this.getLatestBuyer();

			if (InstanceOfUtil.isInstanceOfClass(Customer.class, party)) {
				customerType = AddressBookType.ENDCUSTOMER.getType();
			} else if (InstanceOfUtil
					.isInstanceOfClass(Dealership.class, party)) {
				customerType = AddressBookType.DEALER.getType();
			} else if (InstanceOfUtil.isInstanceOfClass(NationalAccount.class,
					party)) {
				customerType = AddressBookType.NATIONALACCOUNT.getType();
			} else if (InstanceOfUtil.isInstanceOfClass(InterCompany.class,
					party)) {
				customerType = AddressBookType.INTERCOMPANY.getType();
			} else if (InstanceOfUtil.isInstanceOfClass(DirectCustomer.class,
					party)) {
				customerType = AddressBookType.DIRECTCUSTOMER.getType();
			}
		}
		return customerType;
	}

	public String getFactoryOrderNumber() {
		return factoryOrderNumber;
	}

	public void setFactoryOrderNumber(String factoryOrderNumber) {
		this.factoryOrderNumber = factoryOrderNumber;
	}
    
	
	public String getVinNumber() {
		return vinNumber;
	}

	public void setVinNumber(String vinNumber) {
		this.vinNumber = vinNumber;
	}

	public Boolean getPendingWarranty() {
		return pendingWarranty;
	}

	public void setPendingWarranty(Boolean pendingWarranty) {
		this.pendingWarranty = pendingWarranty;
	}

	public List<CustomReportAnswer> getReportAnswers() {
		return reportAnswers;
	}

	public void setReportAnswers(List<CustomReportAnswer> reportAnswers) {
		this.reportAnswers = reportAnswers;
	}

	public SourceWarehouse getSourceWarehouse() {
		return sourceWarehouse;
	}

	public void setSourceWarehouse(SourceWarehouse sourceWarehouse) {
		this.sourceWarehouse = sourceWarehouse;
	}

	public Boolean getEtrPending() {
		return getPendingWarranty()
				&& InvTransationType.ETR.getTransactionType().equals(
						getLatestWarranty().getTransactionType()
								.getTrnxTypeValue());
	}

	public String getMachineAge() {
		return String.valueOf(getShipmentDate().through(Clock.today())
				.lengthInMonthsInt());
	}

	public String getSalesOrderNumber() {
		for (InventoryTransaction transaction : transactionHistory) {
			if ((InvTransationType.IB.getTransactionType())
					.equalsIgnoreCase(transaction.getInvTransactionType()
							.getTrnxTypeValue())) {
				return transaction.getSalesOrderNumber();
			}
		}
		return null;
	}

	public String getInvoiceNumber() {
		for (InventoryTransaction transaction : transactionHistory) {
			if ((InvTransationType.IB.getTransactionType())
					.equalsIgnoreCase(transaction.getInvTransactionType()
							.getTrnxTypeValue())) {
				return transaction.getInvoiceNumber();
			}
		}
		return null;
	}
	
	public String getInvoiceDate() {
		for (InventoryTransaction transaction : transactionHistory) {
			if ((InvTransationType.IB.getTransactionType())
					.equalsIgnoreCase(transaction.getInvTransactionType()
							.getTrnxTypeValue())) {
				return transaction.getInvoiceDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser());
			}
		}
		return null;
	}
    
	public String getDealerLocation() {
		String dealerLocation = null;
		for (InventoryTransaction transaction : transactionHistory) {
			if ((InvTransationType.DEALER_TO_DEALER.getTransactionType())
					.equalsIgnoreCase(transaction.getInvTransactionType()
							.getTrnxTypeValue())) {
				dealerLocation = transaction.getShipToSiteNumber();
				break;
			}
		}
		if(dealerLocation == null){
			for (InventoryTransaction transaction : transactionHistory) {
				if ((InvTransationType.IB.getTransactionType())
						.equalsIgnoreCase(transaction.getInvTransactionType()
								.getTrnxTypeValue())) {
					return transaction.getShipToSiteNumber();
				}
			}
		}
		return dealerLocation;
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
		
	public List<CampaignNotification> getCampaignNotifications() {
		return campaignNotifications;
	}

	public void setCampaignNotifications(
			List<CampaignNotification> campaignNotifications) {
		this.campaignNotifications = campaignNotifications;
	}

	public ServiceProvider getOwner() {
        if (InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, getCurrentOwner())) {
            return new HibernateCast<ServiceProvider>().cast(getCurrentOwner());
        }
		return null;
	}

    public String getDieselTier() {
		return dieselTier;
	}

	public void setDieselTier(String dieselTier) {
		this.dieselTier = dieselTier;
	}
	
	public Boolean getPreOrderBooking() {
		if(preOrderBooking == null){
			return false;
		}
		return preOrderBooking;
	}

	public void setPreOrderBooking(Boolean preOrderBooking) {
		this.preOrderBooking = preOrderBooking;
	}
	
	public String getMarketingGroupCode() {
		return marketingGroupCode;
	}

	public void setMarketingGroupCode(String marketingGroupCode) {
		this.marketingGroupCode = marketingGroupCode;
	}
	
	 
	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}
	

	public List<PartGroup> getPartGroups() {
		return partGroups;
	}

	public void setPartGroups(List<PartGroup> partGroups) {
		this.partGroups = partGroups;
	}

    public String getBillToPurchaseOrder() {
		return billToPurchaseOrder;
	}

	public void setBillToPurchaseOrder(String billToPurchaseOrder) {
		this.billToPurchaseOrder = billToPurchaseOrder;
	}

	public Date getOrderReceivedDate() {
		return orderReceivedDate;
	}

	public void setOrderReceivedDate(Date orderReceivedDate) {
		this.orderReceivedDate = orderReceivedDate;
	}


	public String getDiscAuthorizationNumber() {
		return discAuthorizationNumber;
	}

	public void setDiscAuthorizationNumber(String discAuthorizationNumber) {
		this.discAuthorizationNumber = discAuthorizationNumber;
	}

	public BigDecimal getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(BigDecimal discountPercent) {
		this.discountPercent = discountPercent;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public BigDecimal getMdeCapacity() {
		return mdeCapacity;
	}

	public void setMdeCapacity(BigDecimal mdeCapacity) {
		this.mdeCapacity = mdeCapacity;
	}

	public String getModelPower() {
		return modelPower;
	}

	public void setModelPower(String modelPower) {
		this.modelPower = modelPower;
	}



	public String getBrandType() {
		return brandType;
	}

	public void setBrandType(String brandType) {
		this.brandType = brandType;
	}

	public Date getItaBookDate() {
		return itaBookDate;
	}

	public void setItaBookDate(Date itaBookDate) {
		this.itaBookDate = itaBookDate;
	}

	public Date getItaBookReportDate() {
		return itaBookReportDate;
	}

	public void setItaBookReportDate(Date itaBookReportDate) {
		this.itaBookReportDate = itaBookReportDate;
	}

	public Date getItaDeliveryDate() {
		return itaDeliveryDate;
	}

	public void setItaDeliveryDate(Date itaDeliveryDate) {
		this.itaDeliveryDate = itaDeliveryDate;
	}

	public Date getItaDeliveryReportDate() {
		return itaDeliveryReportDate;
	}

	public void setItaDeliveryReportDate(Date itaDeliveryReportDate) {
		this.itaDeliveryReportDate = itaDeliveryReportDate;
	}
	
	public Date getActualCtsDate() {
		return actualCtsDate;
	}

	public void setActualCtsDate(Date actualCtsDate) {
		this.actualCtsDate = actualCtsDate;
	}

    public String getOperatingUnit() {
        return operatingUnit;
    }

    public void setOperatingUnit(String operatingUnit) {
        this.operatingUnit = operatingUnit;
    }
    
    
    public Organization getRetailedDealer() {
    	Organization retailedDealer = null;
		
		List<String> applicableTrxnTypes = new ArrayList<String>(5);
		applicableTrxnTypes.add(InvTransationType.DR.getTransactionType());
		applicableTrxnTypes.add(InvTransationType.DEMO.getTransactionType());
		applicableTrxnTypes.add(InvTransationType.DR_RENTAL.getTransactionType());
		
		if (this.isRetailed()) {
			
			for (InventoryTransaction iTrxn : this.getTransactionHistory()) {
				
				String iTrxnTypeVal = iTrxn.getInvTransactionType().getTrnxTypeValue();

				if (applicableTrxnTypes.contains(iTrxnTypeVal)) {
					retailedDealer = new HibernateCast<ServiceProvider>().cast(iTrxn.getSeller());
					break;
				}
			}
		}
		else {
			this.setInventoryService(inventoryService);
			retailedDealer = this.getDealer();
		}
		
		return retailedDealer;
    }
    
    public List<ComponentAuditHistory> getComponentAuditHistory() {
		return componentAuditHistory;
	}

	public void setComponentAuditHistory(
			List<ComponentAuditHistory> componentAuditHistory) {
		this.componentAuditHistory = componentAuditHistory;
	}
	
	public List<InventoryItemAdditionalComponents> getAdditionalComponents() {
		return additionalComponents;
	}

	public void setAdditionalComponents(
			List<InventoryItemAdditionalComponents> additionalComponents) {
		this.additionalComponents =additionalComponents;
	}

	public BigDecimal getAopRate1() {
		return aopRate1;
	}

	public void setAopRate1(BigDecimal aopRate1) {
		this.aopRate1 = aopRate1;
	}

	public BigDecimal getAopRate2() {
		return aopRate2;
	}

	public void setAopRate2(BigDecimal aopRate2) {
		this.aopRate2 = aopRate2;
	}

	public BigDecimal getAopTargetRate1() {
		return aopTargetRate1;
	}

	public void setAopTargetRate1(BigDecimal aopTargetRate1) {
		this.aopTargetRate1 = aopTargetRate1;
	}

	public BigDecimal getAopTargetRate2() {
		return aopTargetRate2;
	}

	public void setAopTargetRate2(BigDecimal aopTargetRate2) {
		this.aopTargetRate2 = aopTargetRate2;
	}

	public BigDecimal getOrderGrossValue() {
		return orderGrossValue;
	}

	public void setOrderGrossValue(BigDecimal orderGrossValue) {
		this.orderGrossValue = orderGrossValue;
	}

	public BigDecimal getOrderNetValue() {
		return orderNetValue;
	}

	public void setOrderNetValue(BigDecimal orderNetValue) {
		this.orderNetValue = orderNetValue;
	}

	public BigDecimal getStdwReserveAmountYear1() {
		return stdwReserveAmountYear1;
	}

	public void setStdwReserveAmountYear1(BigDecimal stdwReserveAmountYear1) {
		this.stdwReserveAmountYear1 = stdwReserveAmountYear1;
	}

	public BigDecimal getStdwReserveAmountYear2() {
		return stdwReserveAmountYear2;
	}

	public void setStdwReserveAmountYear2(BigDecimal stdwReserveAmountYear2) {
		this.stdwReserveAmountYear2 = stdwReserveAmountYear2;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getNomenClature() {
		return nomenclature;
	}

	public void setNomenClature(String nomenClature) {
		this.nomenclature = nomenClature;
	}
}