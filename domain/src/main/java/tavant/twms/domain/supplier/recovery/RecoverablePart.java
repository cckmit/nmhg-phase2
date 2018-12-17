/*
 *   Copyright (c)2008 Tavant Technologies
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
package tavant.twms.domain.supplier.recovery;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

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

import org.hibernate.annotations.*;

import com.domainlanguage.money.Money;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.ExcludeConversion;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.PartReturnAction;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.supplier.PartRecoveryAudit;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.contract.CompensationTerm;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.security.AuditableColumns;

/**
 * @author kaustubhshobhan.b
 */
@Entity
@Filters({
@Filter(name = "excludeInactive")
        })
public class RecoverablePart implements AuditableColumns {

    @Id
    @GeneratedValue(generator = "RecoverablePart")
    @GenericGenerator(name = "RecoverablePart", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
    @Parameter(name = "sequence_name", value = "RECOVERABLE_PART_SEQ"),
    @Parameter(name = "initial_value", value = "200"),
    @Parameter(name = "increment_size", value = "20")})
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oem_part", insertable = true, updatable = true,nullable = true)
    @Cascade({CascadeType.SAVE_UPDATE})
    private OEMPartReplaced oemPart;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Item supplierItem;

    private int quantity;


    private boolean supplierReturnNeeded;

    @Transient
    private boolean initiatedBySupplier = false;

    public boolean isInitiatedBySupplier() {
        return initiatedBySupplier;
    }

    public void setInitiatedBySupplier(boolean initiatedBySupplier) {
        this.initiatedBySupplier = initiatedBySupplier;
    }

    @OneToMany(mappedBy="recoverablePart" ,fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL })
	private List<SupplierPartReturn> supplierPartReturns = new ArrayList<SupplierPartReturn>();

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "cost_price_per_unit_amt"),
			@Column(name = "cost_price_per_unit_curr") })
	@ExcludeConversion		
	private Money costPricePerUnit;
	

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "material_cost_amt"),
			@Column(name = "material_cost_curr") })
	@ExcludeConversion
	private Money materialCost;    

	@Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "for_part_replaced", nullable = false, updatable = false, insertable = true)
    @Cascade({org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @IndexColumn(name = "list_index", nullable = false)
    private List<PartRecoveryAudit> partRecoveryAudits = new ArrayList<PartRecoveryAudit>();

    /*
    * Overall part return status
    */
    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.partreturn.PartReturnStatus"),
            @Parameter(name = "type", value = "" + Types.VARCHAR)})
    private PartReturnStatus status;

    public RecoverablePart() {
    }

    public RecoverablePart(OEMPartReplaced oemPartReplaced, int quantity) {
        this.setOemPart(oemPartReplaced);
        this.setQuantity(quantity);
        
    }

    

    public int getNumberOfUnitsApplicable(Contract contract, Claim claim,boolean doesSerializedPartsHaveShipmentDate) {
        int unitsPerClaim = this.quantity / claim.getClaimedItems().size();
        int numberOfItemsCovered = 0;
        for (ClaimedItem claimedItem : claim.getClaimedItems()) {
            if (contract.isClaimedItemCovered(claimedItem,doesSerializedPartsHaveShipmentDate)) {
                numberOfItemsCovered++;
            }
        }
        return numberOfItemsCovered * unitsPerClaim;
    }

    public Money getSupplierPartCost(Contract contract, Claim claim, CompensationTerm compensationTerm, String costPriceType,boolean doesSerializedPartsHaveShipmentDate) {
        Money uomAdjustedPrice = null;
        if (CompensationTerm.DEALER_NET_PRICE.equals(compensationTerm.getPriceType())) {
            uomAdjustedPrice = this.getOemPart().getUomAdjustedPricePerUnit();
        } else if (costPriceType != null) {
            uomAdjustedPrice = getUomAdjustedCostPrice(costPriceType, claim);
        }
        if (uomAdjustedPrice != null)
            return uomAdjustedPrice.times(getNumberOfUnitsApplicable(contract, claim,doesSerializedPartsHaveShipmentDate));
        else
            return Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
    }
    
    
    /**
     * @param costPriceType
     * @param claim
     * @return
     * This material costs (Part Plant Price) will be used for calculation on VR claims if the warranty claim was a unit claim or FPI claim.. 
     * And if the warranty claim is Parts claim then Standard Cost will be used for the VR claims. 
     * If in case the Standard Cost is there for the part but not the Material Cost then Standard Cost will be used for VR claims when the warranty claim is Unit claim Or FPI Claim. 
     * costPriceType is config param, Value can be set according to bussiness unit.
     */
    public Money getUomAdjustedCostPrice(String costPriceType, Claim claim){
    	if(ClaimType.MACHINE.equals(claim.getType()) || ClaimType.FIELD_MODIFICATION.equals(claim.getType())){
			if(CompensationTerm.MATERIAL_COST.equalsIgnoreCase(costPriceType)){
				if(!this.materialCost.isZero()){
					if(this.getOemPart().getUomMapping() !=null && this.getOemPart().getUomMapping().getMappingFraction() !=null && this.materialCost!= null){
						return this.materialCost.dividedBy(this.getOemPart().getUomMapping().getMappingFraction());
					} else {
						return this.materialCost;
					}
				}else{
					if(this.getOemPart().getUomMapping()!=null && this.getOemPart().getUomMapping().getMappingFraction() !=null && this.costPricePerUnit != null){
						return this.costPricePerUnit.dividedBy(this.getOemPart().getUomMapping().getMappingFraction());
					} else {
						return this.costPricePerUnit;
					}				
				}
			}else if(CompensationTerm.STARDARD_COST.equalsIgnoreCase(costPriceType)){
				if(!this.costPricePerUnit.isZero()){
					if(this.getOemPart().getUomMapping()!=null && this.getOemPart().getUomMapping().getMappingFraction() !=null && this.costPricePerUnit != null){
						return this.costPricePerUnit.dividedBy(this.getOemPart().getUomMapping().getMappingFraction());
					} else {
						return this.costPricePerUnit;
					}
				}else{
					if(this.getOemPart().getUomMapping() !=null && this.getOemPart().getUomMapping().getMappingFraction() !=null && this.materialCost!= null){
						return this.materialCost.dividedBy(this.getOemPart().getUomMapping().getMappingFraction());
					} else {
						return this.materialCost;
					}				
				}
			}else {
				if(this.getOemPart().getUomMapping()!=null && this.getOemPart().getUomMapping().getMappingFraction() !=null && this.costPricePerUnit != null){
					return this.costPricePerUnit.dividedBy(this.getOemPart().getUomMapping().getMappingFraction());
				} else {
					return this.costPricePerUnit;
				}
			}
    	}else{
			if(this.getOemPart().getUomMapping()!=null && this.getOemPart().getUomMapping().getMappingFraction() !=null && this.costPricePerUnit != null){
				return this.costPricePerUnit.dividedBy(this.getOemPart().getUomMapping().getMappingFraction());
			} else {
				return this.costPricePerUnit;
			}
    	}
		
	}


    public Money getDNetSupplierCost() {
        Money dNetPrice = this.getOemPart().getUomAdjustedPricePerUnit().times(this.getQuantity());
        return dNetPrice != null ? dNetPrice : Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
    }
    
	public boolean isSupplierPartReturnModificationAllowed() {

		if(!this.getSupplierPartReturns().isEmpty()){
            return false;
        }

        if((this.getOemPart().isReturnDirectlyToSupplier() && this.getOemPart().isPartReturnsPresent()) || this.getOemPart().isPartScrapped()){
            return false;
        }
		return true;
	}
   
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

  

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public OEMPartReplaced getOemPart() {
        return oemPart;
    }

    public void setOemPart(OEMPartReplaced oemPart) {
        this.oemPart = oemPart;
    }

	public Money getCostPricePerUnit() {
		return costPricePerUnit;
	}

	public void setCostPricePerUnit(Money costPricePerUnit) {
		this.costPricePerUnit = costPricePerUnit;
	}

    public Money getMaterialCost() {
		return materialCost;
	}

	public void setMaterialCost(Money materialCost) {
		this.materialCost = materialCost;
	}
	
	public boolean isSupplierReturnNeeded() {
		return supplierReturnNeeded;
	}

	public void setSupplierReturnNeeded(boolean supplierReturnNeeded) {
		this.supplierReturnNeeded = supplierReturnNeeded;
	}

	public List<SupplierPartReturn> getSupplierPartReturns() {
		return supplierPartReturns;
	}

	public void setSupplierPartReturns(List<SupplierPartReturn> supplierPartReturns) {
		this.supplierPartReturns = supplierPartReturns;
	}

	public Item getSupplierItem() {
		return supplierItem;
	}

	public void setSupplierItem(Item supplierItem) {
		this.supplierItem = supplierItem;
	}

    public List<PartRecoveryAudit> getPartRecoveryAudits() {
        return partRecoveryAudits;
    }

    public void setPartRecoveryAudits(List<PartRecoveryAudit> partRecoveryAudits) {
        this.partRecoveryAudits = partRecoveryAudits;
    }

    public PartReturnStatus getStatus() {
        return status;
    }

    public void setStatus(PartReturnStatus status) {
        this.status = status;
        setStatus(status, null,null,null);
    }

   /* public void setStatus(PartReturnStatus status, String comment) {
        this.status = status;
        if(this.status != null){
            PartRecoveryAudit audit = new PartRecoveryAudit();
            audit.setComments(comment);
            audit.setPrStatus(this.status.getStatus());
            audit.setForPartReplaced(this);
            this.partRecoveryAudits.add(audit);
        }
    }*/

    public void setStatus(PartReturnStatus status, String comment, String shipmentNo,String trackinNo) {
        this.status = status;
        if(this.status != null){
            PartRecoveryAudit audit = new PartRecoveryAudit();
            audit.setComments(comment);
            audit.setPrStatus(this.status.getStatus());
            audit.setForPartReplaced(this);
            audit.setShipmentNumber(shipmentNo);
            audit.setTrackingNo(trackinNo);
            this.partRecoveryAudits.add(audit);
        }
    }

    public void setStatus(PartReturnStatus status, String comment, String quantity) {
        this.status = status;
        if(this.status != null){
            PartRecoveryAudit audit = new PartRecoveryAudit();
            audit.setComments(new StringBuilder(comment).append("(Quantity : " + quantity).append(")").toString());
            audit.setPrStatus(this.status.getStatus());
            audit.setForPartReplaced(this);
            this.partRecoveryAudits.add(audit);
        }
    }

    public void setStatus(PartReturnStatus status, String comment, PartReturnAction action1, PartReturnAction action2, PartReturnAction action3) {
        this.status = status;
        if(this.status != null){
            PartRecoveryAudit audit = new PartRecoveryAudit();
            audit.setComments(comment);
            audit.setPrStatus(this.status.getStatus());
            audit.setForPartReplaced(this);
            if(action1 != null){
                audit.setPartReturnAction1(action1);
            }
            if(action2 != null){
                audit.setPartReturnAction2(action2);
            }
            if(action3 != null){
                audit.setPartReturnAction3(action3);
            }
            this.partRecoveryAudits.add(audit);
        }
    }

    public void setStatus(PartReturnStatus status, String comment, PartReturnAction action1, PartReturnAction action2, PartReturnAction action3, Shipment shipment) {
        this.status = status;
        if(this.status != null){
            PartRecoveryAudit audit = new PartRecoveryAudit();
            audit.setComments(comment);
            audit.setPrStatus(this.status.getStatus());
            audit.setForPartReplaced(this);
            if(action1 != null){
                audit.setPartReturnAction1(action1);
            }
            if(action2 != null){
                audit.setPartReturnAction2(action2);
            }
            if(action3 != null){
                audit.setPartReturnAction3(action3);
            }
            if(null != shipment ){
                audit.setShipmentNumber(shipment.getId().toString());
                if(shipment.getTrackingId() != null){
                    audit.setTrackingNo(shipment.getTrackingId());
                }

            }
            this.partRecoveryAudits.add(audit);
        }
    }

    public void setStatus(PartReturnStatus status, String comment, PartReturnAction action1, PartReturnAction action2, PartReturnAction action3, String acceptanceCause, String failureCause) {
        this.status = status;
        if(this.status != null){
            PartRecoveryAudit audit = new PartRecoveryAudit();
            audit.setComments(comment);
            audit.setPrStatus(this.status.getStatus());
            audit.setForPartReplaced(this);
            if(action1 != null){
                audit.setPartReturnAction1(action1);
            }
            if(action2 != null){
                audit.setPartReturnAction2(action2);
            }
            if(action3 != null){
                audit.setPartReturnAction3(action3);
            }
            if(acceptanceCause != null){
                audit.setAcceptanceCause(acceptanceCause);
            }
            if(failureCause != null){
                audit.setFailureCause(failureCause);
            }
            this.partRecoveryAudits.add(audit);
        }
    }

    public RecoverablePart clone() {
        RecoverablePart recoverablePart = new RecoverablePart();
        recoverablePart.setCostPricePerUnit(costPricePerUnit);
        recoverablePart.setMaterialCost(materialCost);
        recoverablePart.setOemPart(oemPart);
        recoverablePart.setQuantity(quantity);
        recoverablePart.setSupplierItem(supplierItem);
        recoverablePart.setSupplierPartReturns(supplierPartReturns);
        recoverablePart.setSupplierReturnNeeded(supplierReturnNeeded);
        recoverablePart.setStatus(status);
        for (PartRecoveryAudit partReturnAudit : this.partRecoveryAudits) {
            recoverablePart.getPartRecoveryAudits().add(partReturnAudit);
        }
        return recoverablePart;

    }

    public String getRetrunLocationForSupplier(){
        for(SupplierPartReturn suplierPartReturn: this.getSupplierPartReturns()){
            if(suplierPartReturn.getStatus().ordinal()>= PartReturnStatus.PARTS_TO_BE_SHIPPED_BY_SUPPLIER_TO_NMHG.ordinal()){
                return suplierPartReturn.getReturnLocation().getCode();
            }

        }
        return null;
    }

    public Location getRetrunLocationObjectForSupplier(){
        for(SupplierPartReturn suplierPartReturn: this.getSupplierPartReturns()){
            if(suplierPartReturn.getStatus().ordinal()>= PartReturnStatus.PARTS_TO_BE_SHIPPED_BY_SUPPLIER_TO_NMHG.ordinal()){
                return suplierPartReturn.getReturnLocation();
            }

        }
        return null;
    }

    public Location getOriginalRetrunLocationObjectForSupplier(){
        for(SupplierPartReturn suplierPartReturn: this.getSupplierPartReturns()){
            if(suplierPartReturn.getStatus().ordinal()< PartReturnStatus.PARTS_TO_BE_SHIPPED_BY_SUPPLIER_TO_NMHG.ordinal()){
                return suplierPartReturn.getReturnLocation();
            }

        }
        return null;
    }

    private int receivedFromSupplier = 0;

    public int getReceivedFromSupplier() {
        if(receivedFromSupplier > 0)
            return receivedFromSupplier;
        else
            return this.getOemPart().getNumberOfUnits();
    }

    public void setReceivedFromSupplier(int receivedFromSupplier) {
        this.receivedFromSupplier = receivedFromSupplier;
    }
}
