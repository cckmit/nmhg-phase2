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

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Constraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;


import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.common.PropertiesWithNestedCurrencyFields;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.contract.CompensationTerm;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.uom.UomMappings;
import tavant.twms.domain.customReports.CustomReportAnswer;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

@Entity
@Table(name = "OEM_PART_REPLACED")
public class OEMPartReplaced extends PartReplaced {

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "serialized", column = @Column(name = "item_ref_szed"))})
    @AssociationOverrides({
            @AssociationOverride(name = "referredItem", joinColumns = @JoinColumn(name = "item_ref_item")),
            @AssociationOverride(name = "referredInventoryItem", joinColumns = @JoinColumn(name = "item_ref_inv_item")),
            @AssociationOverride(name = "unserializedItem", joinColumns = @JoinColumn(name = "item_ref_unszed_item"))})
    private ItemReference itemReference = new ItemReference();

    /**
     * This flag is true when either Part Return or Supplier Recovery is
     * configured
     */
    private boolean partToBeReturned;

    /**
     * This flag when set would set the price of the OEM Part replace to 0 as
     * the part would have been shipped already by the OEM and hence no cost
     * against the part could be claimed. Applicable to Campaign Claims. This
     * flag would be set at the time of campaign setup.
     */
    private boolean shippedByOem;
    
    //Adding as part of SLMS-776
  	private String dateCode;

    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<PartReturn> partReturns = new ArrayList<PartReturn>();

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private Shipment shipment;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    private Wpra wpra;

    @Transient
    private Item supplierItem;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item oemDealerPartReplaced;

    private String serialNumberOfNewPart;


    @Transient
    private PartReturn partReturn;

    @Column(name = "PR_INITIATED_BY_SUPPLIER", nullable = true)
    private boolean partReturnInitiatedBySupplier;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "part_rep_clm_attr")
    private List<ClaimAttributes> claimAttributes = new ArrayList<ClaimAttributes>();

    @OneToOne(fetch = FetchType.LAZY)
    private PartReturnConfiguration partReturnConfiguration;

    @OneToMany(mappedBy = "oemPart")
    @Cascade({org.hibernate.annotations.CascadeType.DELETE})
    private List<RecoverablePart> recoverableParts = new ArrayList<RecoverablePart>();

    @Transient
    private BasePartReturn activePartReturn;

    /* Part Return Action taken
    */
    @Transient
    private PartReturnAction partAction1;

    /*
     * Part Return Action taken
    */
    @Transient
    private PartReturnAction partAction2;

    @Transient
    private PartReturnAction partAction3;

    @Transient
    String comments;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "for_part_replaced", nullable = false, updatable = false, insertable = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @IndexColumn(name = "list_index", nullable = false)
    private List<PartReturnAudit> partReturnAudits = new ArrayList<PartReturnAudit>();

    /*
    * Overall part return status
    */
    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.partreturn.PartReturnStatus"),
            @Parameter(name = "type", value = "" + Types.VARCHAR)})
    private PartReturnStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private CustomReportAnswer customReportAnswer;


    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    protected UomMappings uomMapping;
    
    private String serialNumber;
    @Transient
	private String acceptanceCause;
    @Transient
	private String failureCause;
    
    @Transient
    private boolean  partReturnToDealer;
    

	public boolean isPartReturnToDealer() {
		return partReturnToDealer;
	}

	public void setPartReturnToDealer(boolean partReturnToDealer) {
		this.partReturnToDealer = partReturnToDealer;
	}

	public List<PartReturnAudit> getPartReturnAudits() {
        return partReturnAudits;
    }

    public void setPartReturnAudits(List<PartReturnAudit> partReturnAudits) {
        this.partReturnAudits = partReturnAudits;
    }

    public OEMPartReplaced(ItemReference itemReference, int numberOfUnits) {
        this.itemReference = itemReference;
        this.numberOfUnits = numberOfUnits;
    }

    public OEMPartReplaced(ItemReference itemReference, BrandItem brandItem, int numberOfUnits) {
        this.brandItem = brandItem;
        this.itemReference = itemReference;
        this.numberOfUnits = numberOfUnits;
    }

    /**
     * Constructor - mainly for the hibernate
     */
    public OEMPartReplaced() {

    }

    /**
     * @return the itemReplaced
     */
    public ItemReference getItemReference() {
        return this.itemReference;
    }

    /**
     * @param itemReplaced the itemReplaced to set
     */
    public void setItemReference(ItemReference itemReference) {
        this.itemReference = itemReference;
    }

    /**
     * @return the partReturn
     */
    public List<PartReturn> getPartReturns() {
        return this.partReturns;
    }

    /**
     * @param partReturn the partReturn to set
     */
    public void setPartReturns(List<PartReturn> partReturns) {
        // Setting the inverse relationship here. Kannan
        if (partReturns != null && !partReturns.isEmpty()) {
            for (PartReturn part : partReturns) {
                part.setOemPartReplaced(this);
            }
        }
        this.partReturns = partReturns;
    }

    /**
     * @return the partToBeReturned
     */
    public boolean isPartToBeReturned() {
        return this.partToBeReturned;
    }

    public boolean isPartInspected() {
        // If Part Return status is "inspected"
        boolean isInspected = true;
        for (PartReturn partReturn : this.partReturns) {
            if (partReturn.getStatus().ordinal() < PartReturnStatus.PART_ACCEPTED.ordinal() && !partReturn.getStatus().equals(PartReturnStatus.CANNOT_BE_SHIPPED)
                    && !partReturn.getStatus().equals(PartReturnStatus.REMOVED_BY_PROCESSOR)) {
                return false;
            }
        }
        return isInspected;
    }

    public String getLatestAuditComments() {
        String latestComments = "";

        if (getPartReturnAudits() != null && getPartReturnAudits().size() > 0) {
            latestComments = getPartReturnAudits().get(getPartReturnAudits().size() - 1).getComments();
        }
        return latestComments;
    }

    /**
     * @param partToBeReturned the partToBeReturned to set
     */
    public void setPartToBeReturned(boolean isPartToBeReturned) {
        this.partToBeReturned = isPartToBeReturned;
    }

    public Shipment getShipment() {
        return this.shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public boolean isPartReturnRejected() {
        // If Part Return status is "reject"
        return getPartReturns() != null
                && (PartReturnStatus.PART_REJECTED.equals((getPartReturns().get(0)).getStatus()));
    }

    @Transient
    public boolean isPartRecoveredFromSupplier(Contract contract, Item causalItem, ClaimType type) {
        if (type != null && ClaimType.CAMPAIGN.getType().equalsIgnoreCase(type.getType())) {
            return true;
        }
        return (causalItem != null && causalItem.getId() == this.itemReference
                .getUnserializedItem().getId())
                || (contract != null && contract.getCollateralDamageToBePaid() != null && contract
                .getCollateralDamageToBePaid());
    }

    @Transient
    /**
     * A part is marked for direct supplier recovery, only if there is a
     * supplier part return without a OEM due part return
     */
    public boolean isPartMarkedForDirectSupplierRecovery() {
        return (this.partReturns == null) && isPartRecoveredFromSupplier(null, null, null);
    }

    /**
     * TODO: These are only convenience APIs to get due dates, shipments etc, It
     * will flip, between due part return and supplier part return.
     * <p/>
     * I am working, on the idea of having a PartReturn list, and designating
     * one item as a active part return
     *
     * @return
     * @author kannan.ekanath
     */

    public CalendarDate getDueDateForPartReturn() {
        return getActivePartReturn().getDueDate();
    }

    public int getDueDaysForPartReturn() {
        return getActivePartReturn().getDueDays();
    }

    public void setActivePartReturn(BasePartReturn basePartReturn) {
        this.activePartReturn = basePartReturn;
    }

    public BasePartReturn getActivePartReturn() {
        for (BasePartReturn partReturn : this.partReturns) {
            if (!PartReturnStatus.REMOVED_BY_PROCESSOR.equals(partReturn.getStatus())) {
                return partReturn;
            }
        }
        return null;
    }

    public BasePartReturn getRuleContextActivePartReturn() {
        for (BasePartReturn partReturn : this.partReturns) {
            if (!PartReturnStatus.REMOVED_BY_PROCESSOR.equals(partReturn.getStatus())) {
                return partReturn;
            }
        }
        BasePartReturn toReturn = new BasePartReturn();
        PaymentCondition pmtCondn = new PaymentCondition();
        pmtCondn.setDescription("");
        toReturn.setPaymentCondition(pmtCondn);
        return toReturn;
    }

    public boolean isShippedByOem() {
        return this.shippedByOem;
    }

    public void setShippedByOem(boolean shippedByOem) {
        this.shippedByOem = shippedByOem;
    }

    public String getSerialNumberOfNewPart() {
        return this.serialNumberOfNewPart;
    }

    public void setSerialNumberOfNewPart(String serialNumberOfNewPart) {
        this.serialNumberOfNewPart = serialNumberOfNewPart;
    }


    public PartReturn getPartReturn() {
        return this.partReturn;
    }

    public void setPartReturn(PartReturn partReturn) {
        this.partReturn = partReturn;
    }

    public boolean isPartReturnInitiatedBySupplier() {
        return this.partReturnInitiatedBySupplier;
    }

    public void setPartReturnInitiatedBySupplier(boolean partReturnInitiatedBySupplier) {
        this.partReturnInitiatedBySupplier = partReturnInitiatedBySupplier;
    }

    public boolean isPartShipped() {
        for (PartReturn partReturn : this.partReturns) {
            if (partReturn.getStatus().isPartShipped())
                return true;
        }
        return false;
    }

    /*@Transient
    
     * Calculate number of claimed items applicable
     
    public int getNumberOfUnitsApplicable(Contract contract, Claim claim) {
        int unitsPerClaim = this.numberOfUnits / claim.getClaimedItems().size();
        int numberOfItemsCovered = 0;
        for (ClaimedItem claimedItem : claim.getClaimedItems()) {
            if (contract.isClaimedItemCovered(claimedItem)) {
                numberOfItemsCovered++;
            }
        }
        return numberOfItemsCovered * unitsPerClaim;
    }
*/
    public int getPartsReceived() {
        int partsReceived = 0;
        for (PartReturn partReturn : this.partReturns) {
            if (partReturn.getStatus().ordinal() > PartReturnStatus.PART_RECEIVED.ordinal())
                partsReceived++;
        }
        return partsReceived;
    }

    public List<ClaimAttributes> getClaimAttributes() {
        return claimAttributes;
    }

    public void setClaimAttributes(List<ClaimAttributes> claimAttributes) {
        this.claimAttributes = claimAttributes;
    }

    //Already commented, not for Part Replaced CR
    /*public PartReturnStatus getPartReturnStatus() {
            return partReturnStatus;
        }
    */

    public PartReturnAction getPartAction1() {
        return partAction1;
    }

    public void setPartAction1(PartReturnAction partAction1) {
        this.partAction1 = partAction1;
    }

    public PartReturnAction getPartAction2() {
        return partAction2;
    }

    public void setPartAction2(PartReturnAction partAction2) {
        this.partAction2 = partAction2;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public PartReturnStatus getStatus() {
        return status;
    }

    public UomMappings getUomMapping() {
        return uomMapping;
    }

    public void setUomMapping(UomMappings uomMapping) {
        this.uomMapping = uomMapping;
    }

	public void setStatus(PartReturnStatus status) {
        this.status = status;
        if (this.status != null) {
            PartReturnAudit partReturnAudit = new PartReturnAudit(this.partAction1, this.partAction2, this.partAction3);
            partReturnAudit.setComments(this.comments);
            partReturnAudit.setPrStatus(this.status.getStatus());
            partReturnAudit.setAcceptanceCause(acceptanceCause);
            partReturnAudit.setFailureCause(failureCause);
            partReturnAudit.setForPartReplaced(this);
            this.partReturnAudits.add(partReturnAudit);
        }
    }

    public PartReturnConfiguration getPartReturnConfiguration() {
        return partReturnConfiguration;
    }

    public void setPartReturnConfiguration(PartReturnConfiguration partReturnConfiguration) {
        this.partReturnConfiguration = partReturnConfiguration;
    }


    public Money costAtCP(String costPriceType) {
        GlobalConfiguration globalConfiguration = GlobalConfiguration
                .getInstance();
        if (this.numberOfUnits == null || (this.costPricePerUnit == null && this.materialCost == null)) {
            return globalConfiguration.zeroInBaseCurrency();
        }
        Money costPriceWithUomCorrection = null;
        if (CompensationTerm.STARDARD_COST.equalsIgnoreCase(costPriceType)) {
            costPriceWithUomCorrection = numberOfUnits != null ? this.costPricePerUnit.times(this.numberOfUnits)
                    : globalConfiguration.zeroInBaseCurrency();
            if (this.uomMapping != null && !costPriceWithUomCorrection.isZero()) {
                costPriceWithUomCorrection = costPriceWithUomCorrection.dividedBy(
                        this.uomMapping.getMappingFraction().doubleValue());
            }
        } else if (CompensationTerm.MATERIAL_COST.equalsIgnoreCase(costPriceType)) {
            costPriceWithUomCorrection = numberOfUnits != null ? this.materialCost.times(this.numberOfUnits)
                    : globalConfiguration.zeroInBaseCurrency();
            if (this.uomMapping != null && !costPriceWithUomCorrection.isZero()) {
                costPriceWithUomCorrection = costPriceWithUomCorrection.dividedBy(
                        this.uomMapping.getMappingFraction().doubleValue());
            }
        } else {
            costPriceWithUomCorrection = numberOfUnits != null ? this.costPricePerUnit.times(this.numberOfUnits)
                    : globalConfiguration.zeroInBaseCurrency();
            if (this.uomMapping != null && !costPriceWithUomCorrection.isZero()) {
                costPriceWithUomCorrection = costPriceWithUomCorrection.dividedBy(
                        this.uomMapping.getMappingFraction().doubleValue());
            }
        }
        return costPriceWithUomCorrection;

    }

    public Money cost() {
        Money priceWithUomCorrection = super.cost();
        if (this.uomMapping != null && !priceWithUomCorrection.isZero()) {
            priceWithUomCorrection = priceWithUomCorrection.dividedBy(this.uomMapping.getMappingFraction().doubleValue());
        }
        return priceWithUomCorrection;
    }


    /**
     * .
     * This method is an utility method used for display purposes only
     *
     * @return
     */
    @Transient
    public Money getUomAdjustedPricePerUnit() {
        if (this.uomMapping != null && this.uomMapping.getMappingFraction() != null && this.pricePerUnit != null) {
            return this.pricePerUnit.dividedBy(this.uomMapping.getMappingFraction());
        } else {
            return this.pricePerUnit;
        }

    }

    @Transient
    public Money getUomAdjustedCostPrice(String costPriceType) {
        if (CompensationTerm.MATERIAL_COST.equalsIgnoreCase(costPriceType)) {
            if (this.uomMapping != null && this.uomMapping.getMappingFraction() != null && this.materialCost != null) {
                return this.materialCost.dividedBy(this.uomMapping.getMappingFraction());
            } else {
                return this.materialCost;
            }
        } else if (CompensationTerm.STARDARD_COST.equalsIgnoreCase(costPriceType)) {
            if (this.uomMapping != null && this.uomMapping.getMappingFraction() != null && this.costPricePerUnit != null) {
                return this.costPricePerUnit.dividedBy(this.uomMapping.getMappingFraction());
            } else {
                return this.costPricePerUnit;
            }
        } else {
            if (this.uomMapping != null && this.uomMapping.getMappingFraction() != null && this.costPricePerUnit != null) {
                return this.costPricePerUnit.dividedBy(this.uomMapping.getMappingFraction());
            } else {
                return this.costPricePerUnit;
            }
        }

    }

    public Item getOemDealerPartReplaced() {
        return oemDealerPartReplaced;
    }

    public void setOemDealerPartReplaced(Item oemDealerPartReplaced) {
        this.oemDealerPartReplaced = oemDealerPartReplaced;
    }

    public boolean isShipmentGenerated() {
        for (PartReturn partReturn : partReturns) {
            if (partReturn.getStatus().isShipmentGenerated()) {
                return true;
            }
        }
        return false;
    }

    public boolean isPartShippedOrCannotBeShipped() {
        for (PartReturn partReturn : partReturns) {
            if (partReturn.getStatus().isPartShipped()
                    || partReturn.getStatus().equals(PartReturnStatus.CANNOT_BE_SHIPPED) || isPartScrapped()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean getPartShippedOrCannotBeShipped(){
    	return this.isPartShippedOrCannotBeShipped();
    }

    public boolean isDueDaysReadOnly() {
        for (PartReturn partReturn : partReturns) {
            if ((partReturn.getDueDate() != null && !Clock.today().isBefore(partReturn.getDueDate()))
                    || partReturn.getStatus().ordinal() > PartReturnStatus.PART_TO_BE_SHIPPED.ordinal()) {
                return true;
            }
        }
        return false;
    }

    public List<PartReturn> getActivePartReturns() {
        List<PartReturn> activePartReturns = new ArrayList<PartReturn>();
        for (PartReturn pr : partReturns) {
            if (!pr.getStatus().equals(PartReturnStatus.REMOVED_BY_PROCESSOR))
                activePartReturns.add(pr);
        }
        return activePartReturns;
    }

    public boolean isPartInWarehouse() {
        if (this.getPartReturns().isEmpty() || this.isPartScrapped())
            return false;
        boolean isPresent = true;
        for (PartReturn partReturn : this.getPartReturns()) {
            if (!partReturn.isPartReceived()) {
                isPresent = false;
            }
        }
        return isPresent;
    }


    public boolean isPartReturnsPresent() {
        if (this.getPartReturns().isEmpty())
            return false;
        for (PartReturn partReturn : this.getPartReturns()) {
            if (PartReturnStatus.REMOVED_BY_PROCESSOR.equals(partReturn.getStatus()))
                return false;
        }
        return true;
    }

    public List<RecoverablePart> getRecoverableParts() {
        return recoverableParts;
    }

    public void setRecoverableParts(List<RecoverablePart> recoverableParts) {
        this.recoverableParts = recoverableParts;
    }

    public CustomReportAnswer getCustomReportAnswer() {
        return customReportAnswer;
    }

    public void setCustomReportAnswer(CustomReportAnswer customReportAnswer) {
        this.customReportAnswer = customReportAnswer;
    }

    public Wpra getWpra() {
        return wpra;
    }

    public void setWpra(Wpra wpra) {
        this.wpra = wpra;
    }

   /* private String brand;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }*/

    public OEMPartReplaced clone() {
        OEMPartReplaced oemPartReplaced = new OEMPartReplaced();
        oemPartReplaced.setItemReference(itemReference.clone());
        oemPartReplaced.setPartToBeReturned(partToBeReturned);
        oemPartReplaced.setReturnDirectlyToSupplier(returnDirectlyToSupplier);
        oemPartReplaced.setShippedByOem(shippedByOem);
        oemPartReplaced.setShipment(shipment);
        oemPartReplaced.setWpra(wpra);
        oemPartReplaced.setOemDealerPartReplaced(oemDealerPartReplaced);
        oemPartReplaced.setSerialNumberOfNewPart(serialNumberOfNewPart);
        oemPartReplaced.setPartReturnInitiatedBySupplier(partReturnInitiatedBySupplier);
        oemPartReplaced.setStatus(status);
        oemPartReplaced.setCustomReportAnswer(customReportAnswer);
       // oemPartReplaced.setReturnPartToDealer(returnPartToDealer);
        oemPartReplaced.setBrandItem(brandItem);

        for (PartReturn partReturn : this.partReturns) {
            oemPartReplaced.getPartReturns().add(partReturn.clone());
        }

        for (ClaimAttributes claimAttributes : this.claimAttributes) {
            oemPartReplaced.getClaimAttributes().add(claimAttributes.clone());
        }

        if (this.partReturnConfiguration != null) {
            oemPartReplaced.setPartReturnConfiguration(this.partReturnConfiguration);
        }

        for (RecoverablePart recoverablePart : this.recoverableParts) {
            oemPartReplaced.getRecoverableParts().add(recoverablePart.clone());
        }

        for (PartReturnAudit partReturnAudit : this.partReturnAudits) {
            oemPartReplaced.getPartReturnAudits().add(partReturnAudit);
        }

        return oemPartReplaced;
    }

    /*@Transient
    private boolean returnPartToDealer = false;

    public boolean isReturnPartToDealer() {
        return returnPartToDealer;
    }

    public void setReturnPartToDealer(boolean returnPartToDealer) {
        this.returnPartToDealer = returnPartToDealer;
    }*/

    private boolean returnDirectlyToSupplier = false;

    public boolean isReturnDirectlyToSupplier() {
        return returnDirectlyToSupplier;
    }

    public void setReturnDirectlyToSupplier(boolean returnDirectlyToSupplier) {
        this.returnDirectlyToSupplier = returnDirectlyToSupplier;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Contract appliedContract;

    public Contract getAppliedContract() {
        return appliedContract;
    }

    public void setAppliedContract(Contract appliedContract) {
        this.appliedContract = appliedContract;
    }

    public User getUserIfRecoveryClaimIsCreated(){
    	User masterSupplier = null;
		User supplierUser = null;
		Set<User> users = new TreeSet<User>();
        if(null != getAppliedContract() && null != getAppliedContract().getSupplier() &&  null != getAppliedContract().getSupplier().getUsers() && getAppliedContract().getSupplier().getUsers().size() >0){
            users = getAppliedContract().getSupplier().getUsers();
            String contractBU = getAppliedContract().getBusinessUnitInfo().getName();
            for(User user : users) {
            	String userBU = user.getPreferredBu() != null ? user.getPreferredBu() : user.getBusinessUnits() != null && !user.getBusinessUnits().isEmpty() ? user.getBusinessUnits().first().getName() : null;
    			if(contractBU.equalsIgnoreCase(userBU)) {
    				if(user.hasRole("masterSupplier")) {
    					masterSupplier = user;
    				} else {
    					supplierUser = user;
    				}
    			}
    		}
        }
        if(supplierUser != null) {
			return supplierUser;
		} else {
			return masterSupplier;
		}
    }

    public boolean  isSupplierReturnAlreadyInitiated(){
        return (this.recoverableParts != null && this.recoverableParts.size()> 0 && this.recoverableParts.get(0).isSupplierReturnNeeded());
    }

	public String getDateCode() {
		return dateCode;
	}

	public void setDateCode(String dateCode) {
		this.dateCode = dateCode;
	}

    @Column(name="PART_SCRAPPED", nullable=false)
    private boolean partScrapped = false;

    public boolean isPartScrapped() {
        return partScrapped;
    }

    public void setPartScrapped(boolean partScrapped) {
        this.partScrapped = partScrapped;
    }

    private Date scrapDate;

    public Date getScrapDate() {
        return scrapDate;
    }

    public void setScrapDate(Date scrapDate) {
        this.scrapDate = scrapDate;
    }

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getAcceptanceCause() {
		return acceptanceCause;
	}

	public void setAcceptanceCause(String acceptanceCause) {
		this.acceptanceCause = acceptanceCause;
	}

	public String getFailureCause() {
		return failureCause;
	}

	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}

    public PartReturnAction getPartAction3() {
        return partAction3;
    }

    public void setPartAction3(PartReturnAction partAction3) {
        this.partAction3 = partAction3;
    }
    
    public boolean isPartReceivedAndVerified(){
    	return this.getStatus()!= null ? (PartReturnStatus.getPartReturnStatus(this.getStatus().getStatus()).ordinal() > PartReturnStatus.PART_RECEIVED.ordinal()): false;
    } 
    
    public boolean isPartShippedToDealer(){
    	return this.getStatus()!= null ? (PartReturnStatus.getPartReturnStatus(this.getStatus().getStatus()).ordinal() >= PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER.ordinal()) : false;
    }

    public int getShippedPartsQuantity(){
       int quantity = 0;
       for(PartReturn partReturn : this.partReturns) {
           if (partReturn.getStatus().isPartShipped()){
               quantity++;
           }
       }

       return quantity;
    }

    public PartReturn getDealerPartReturn(){
        for(PartReturn p : this.getPartReturns()){
            if((p.getStatus().ordinal() <= PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.ordinal()) || (p.getStatus().getStatus().equalsIgnoreCase(PartReturnStatus.PART_MARKED_AS_SCRAPPED.getStatus()))){
                if(p.getShipment() != null){
                    return p;
                }
            }
        }
        return null;
    }

    @ManyToOne(cascade = { CascadeType.ALL },fetch=FetchType.LAZY)
    @JoinColumn(name="brand_item")
    private BrandItem brandItem ;

    public BrandItem getBrandItem() {
        return brandItem;
    }

    public void setBrandItem(BrandItem brandItem) {
        this.brandItem = brandItem;
    }

    @Transient
    private boolean askBackFromSupplier = false;

    @Transient
    private Location tempLocationSetupForSupplier;

    public Location getTempLocationSetupForSupplier() {
        return tempLocationSetupForSupplier;
    }

    public void setTempLocationSetupForSupplier(Location tempLocationSetupForSupplier) {
        this.tempLocationSetupForSupplier = tempLocationSetupForSupplier;
    }

    public boolean isAskBackFromSupplier() {
        return askBackFromSupplier;
    }

    public void setAskBackFromSupplier(boolean askBackFromSupplier) {
        this.askBackFromSupplier = askBackFromSupplier;
    }

    @Transient
    private String actedBy;

    public String getActedBy() {
        return actedBy;
    }

    public void setActedBy(String actedBy) {
        this.actedBy = actedBy;
    }
}
