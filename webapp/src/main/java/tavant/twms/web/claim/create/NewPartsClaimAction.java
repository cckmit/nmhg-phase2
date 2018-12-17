package tavant.twms.web.claim.create;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.claimsubmission.PartsClaimSubmissionUtil;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemAttributeValue;
import tavant.twms.domain.inventory.InventoryScrapTransaction;
import tavant.twms.domain.inventory.InventoryScrapTransactionXMLConverter;
import tavant.twms.domain.inventory.InventoryStolenTransaction;
import tavant.twms.domain.inventory.InventoryStolenTransactionXMLConverter;
import tavant.twms.domain.orgmodel.AttributeConstants;

import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class NewPartsClaimAction extends AbstractNewClaimAction implements Preparable{
	
    private PartsClaim claim;
    
    private PartsClaimSubmissionUtil partsClaimSubmissionUtil;
    
    public InventoryItem getPartInvItem() {
		return partInvItem;
	}

	public void setPartInvItem(InventoryItem partInvItem) {
		this.partInvItem = partInvItem;
	}

	private InventoryItem partInvItem;
	
	private List<ListOfValues> competitorModels = new ArrayList<ListOfValues>();
	
	private InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter;
	
	private InventoryStolenTransactionXMLConverter inventoryStolenTransactionXMLConverter;
    
    private Boolean dealerNumberSelected;
    
    private Boolean serialNumberSelected;
    
    private Boolean productModelSelected;
    
    private String partInstalledOn;
    
    public String saveDraft() {
    	if(validatePartsClaimforStandardPolicy(claim)){
    		if (getConfigParamService().getBooleanValue(
					ConfigName.VALIDATE_STD_WRNTY_ON_PARTS_CLAIMS.getName())) 
    		addActionError("error.partsClaim.standardWarrantyPeriodExists");
    		else{
    			addActionWarning("warning.partsClaim.standardWarrantyPeriodExists");
    			if (getConfigParamService().getBooleanValue(
    					ConfigName.VALIDATE_MARKETING_GROUP_CODES.getName())) 
    						validateMktgGrpCodesForNonSerializedClaims(this.claim);
    			}
    		}
		if (!validatePartsClaimforStandardPolicy(claim)){
			if(getConfigParamService().getBooleanValue(ConfigName.VALIDATE_MARKETING_GROUP_CODES.getName()))
				validateMktgGrpCodesForNonSerializedClaims(this.claim);
		}
			
		if (hasActionErrors())
			return INPUT;
		
        /* The parts claim without host has a default claimed item since
           this is required for the code stabilty. Later this should be fixed.*/

        if((claim.getBrand() == null ||claim.getBrand().isEmpty()) && !displayBrandDropDown()){
            if(!this.partInstalledOn.equalsIgnoreCase("PART_INSTALLED_ON_HOST")){
                claim.setBrand(claim.getBrandPartItem().getBrand());
            }
        }
        return saveDraft(this.claim);
    }

    public PartsClaim getClaim() {
        return claim;
    }

    public void setClaim(PartsClaim claim) {
        this.claim = claim;
    }
    
    public void setPartsClaimSubmissionUtil(PartsClaimSubmissionUtil partsClaimSubmissionUtil) {
        this.partsClaimSubmissionUtil = partsClaimSubmissionUtil;
    }
    
	public boolean toBeChecked(String partInstalledOn) {
		if (partInstalledOn.equals(this.partInstalledOn)) {
			return true;
		} else {
			return false;
		}
    }
    
    @SuppressWarnings("deprecation")
	public void prepare() throws Exception {
    	setCompetitorModels(partsClaimSubmissionUtil.getCompetitorModels());
		if(this.claim != null && this.claim.getItemReference()  != null)
		{				
			if(this.claim.getItemReference().getReferredInventoryItem() != null)
			{				
				setSelectedBusinessUnit(this.claim.getItemReference().getReferredInventoryItem().getBusinessUnitInfo().getName());
			}
			else if (this.claim.getItemReference().getReferredItem() != null && 
					this.claim.getItemReference().getReferredItem().getModel() != null)
			{
				setSelectedBusinessUnit(this.claim.getItemReference().getReferredItem().getModel().getBusinessUnitInfo().getName());
			}
		}
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public void validate() {
        super.validate();
        checkAuthNumber(getClaim());
        if(getClaim().getForDealer().getServiceProviderNumber() == null){
       	 addActionError("error.newClaim.selectDealer");
       }
//        isInvoiceNumberRequired(getClaim());
      
        if(partInvItem != null) {
        	claim.getPartItemReference().setReferredInventoryItem(partInvItem);
        	setActionErrors(partsClaimSubmissionUtil.validate(claim));
        }
        setActionErrors(partsClaimSubmissionUtil.validate(claim, getForSerialized(), productModelSelected,serialNumberSelected,partInstalledOn));
        setActionErrors(partsClaimSubmissionUtil.validateRegisteredPart(claim));
    }
    
	public List<ClaimType> getClaimTypes() {
		List<ClaimType> tempClaimTypes = new ArrayList<ClaimType>();
		List<ClaimType> claimTypes=new ArrayList<ClaimType>();
		tempClaimTypes = getClaimService().fetchAllClaimTypesForBusinessUnit();
		claimTypes.add(ClaimType.PARTS);
		for (ClaimType claimType : tempClaimTypes) {
			if(!ClaimType.PARTS.getType().equals(claimType.getType())){
				claimTypes.add(ClaimType.getUIDisplayName(claimType.getType()));
			}
		}
		return claimTypes;
	}
	
	//Validating the invoice number.
	public void isInvoiceNumberRequired(Claim claim){
		 if(this.getConfigParamService().getBooleanValue(ConfigName.IS_INVOICE_NUMBER_REQUIRED.getName())){
	    	if( claim!= null && claim.getInvoiceNumber()!=null && !StringUtils.hasText(claim.getInvoiceNumber())){
			    addActionError("error.newClaim.invoiceNumberRequired");
			}
	    }
	 }
    
    public boolean isItemNumberDisplayRequired() {
		 return getConfigParamService().getBooleanValue(ConfigName.IS_ITEM_NUMBER_DISPLAY_REQUIRED
					.getName());
		}
    
    private void validateRepairDate(PartsClaim claim) {
    	for (ClaimedItem claimedItem : claim.getClaimedItems()) {
			InventoryItem inventoryItem = claimedItem.getItemReference()
					.getReferredInventoryItem();
			CalendarDate scrapDate = null;
			CalendarDate unScrapDate = null;
			CalendarDate stolenDate = null;
			CalendarDate unStolenDate = null;
			if(inventoryItem != null && inventoryItem.getInventoryItemAttrVals() != null){
			if (inventoryItem.getInventoryItemAttrVals().size() > 0) {
				for (InventoryItemAttributeValue inventoryItemAttrVal : inventoryItem
						.getInventoryItemAttrVals()) {
					if (AttributeConstants.SCRAP_COMMENTS
							.equals(inventoryItemAttrVal.getAttribute()
									.getName())) {
						InventoryScrapTransaction scrap = (InventoryScrapTransaction) this.inventoryScrapTransactionXMLConverter
								.convertXMLToObject(inventoryItemAttrVal.getValue());
						scrapDate = scrap.getDateOfScrapOrUnscrap();
					}
					if (AttributeConstants.UN_SCRAP_COMMENTS
							.equals(inventoryItemAttrVal.getAttribute()
									.getName())) {
						InventoryScrapTransaction unScrap = (InventoryScrapTransaction) this.inventoryScrapTransactionXMLConverter
								.convertXMLToObject(inventoryItemAttrVal.getValue());
						unScrapDate = unScrap.getDateOfScrapOrUnscrap();
					}
					if (scrapDate != null && unScrapDate != null) {
						if((claimedItem.getClaim().getRepairDate().isAfter(scrapDate) || claimedItem.getClaim().getRepairDate().equals(scrapDate)) 
                				&& (claimedItem.getClaim().getRepairDate().isBefore(unScrapDate) || claimedItem.getClaim().getRepairDate().equals(unScrapDate))
                				&& !scrapDate.equals(unScrapDate)){
							addActionError("message.scrap.machineScrapped",inventoryItem.getSerialNumber());														
						} else {
							scrapDate = null;
							unScrapDate = null;
						}
					}

					if (AttributeConstants.STOLEN_COMMENTS
							.equals(inventoryItemAttrVal.getAttribute()
									.getName())) {
						InventoryStolenTransaction stolen = (InventoryStolenTransaction) this.inventoryStolenTransactionXMLConverter
								.convertXMLToObject(inventoryItemAttrVal.getValue());
						stolenDate = stolen.getDateOfStolenOrUnstolen();
					}
					if (AttributeConstants.UN_STOLEN_COMMENTS
							.equals(inventoryItemAttrVal.getAttribute()
									.getName())) {
						InventoryStolenTransaction unStolen = (InventoryStolenTransaction) this.inventoryStolenTransactionXMLConverter
								.convertXMLToObject(inventoryItemAttrVal.getValue());
						unStolenDate = unStolen.getDateOfStolenOrUnstolen();
					}
					if (stolenDate != null && unStolenDate != null) {
						if((claimedItem.getClaim().getRepairDate().isAfter(stolenDate) || claimedItem.getClaim().getRepairDate().equals(stolenDate)) 
                				&& (claimedItem.getClaim().getRepairDate().isBefore(unStolenDate) || claimedItem.getClaim().getRepairDate().equals(unStolenDate))
                				&& !stolenDate.equals(unStolenDate)){
							addActionError("message.stole.machineStolen",inventoryItem.getSerialNumber());														
						} else {
							stolenDate = null;
							unStolenDate = null;
						}
					}
				
				}
				 if(scrapDate != null && unScrapDate == null
	                		&& (claimedItem.getClaim().getRepairDate().isAfter(scrapDate) || claimedItem.getClaim().getRepairDate().equals(scrapDate))){
					addActionError("message.scrap.machineScrapped",inventoryItem.getSerialNumber());
					addActionError("message.scrap.claim", scrapDate.toString("MM/dd/yyyy"));					
				}
				 if(stolenDate != null && unStolenDate == null
	                		&& (claimedItem.getClaim().getRepairDate().isAfter(stolenDate) || claimedItem.getClaim().getRepairDate().equals(stolenDate))){
					addActionError("message.stole.machineStolen",inventoryItem.getSerialNumber());
					addActionError("message.stole.claim", stolenDate.toString("MM/dd/yyyy"));					
				}
			}
			}
		}			
	}


	public InventoryScrapTransactionXMLConverter getInventoryScrapTransactionXMLConverter() {
		return inventoryScrapTransactionXMLConverter;
	}

	public void setInventoryScrapTransactionXMLConverter(
			InventoryScrapTransactionXMLConverter inventoryScrapTransactionXMLConverter) {
		this.inventoryScrapTransactionXMLConverter = inventoryScrapTransactionXMLConverter;
	}

	public InventoryStolenTransactionXMLConverter getInventoryStolenTransactionXMLConverter() {
		return inventoryStolenTransactionXMLConverter;
	}

	public void setInventoryStolenTransactionXMLConverter(
			InventoryStolenTransactionXMLConverter inventoryStolenTransactionXMLConverter) {
		this.inventoryStolenTransactionXMLConverter = inventoryStolenTransactionXMLConverter;
	}
	
	public Boolean getDealerNumberSelected() {
		return dealerNumberSelected;
	}

	public void setDealerNumberSelected(Boolean dealerNumberSelected) {
		this.dealerNumberSelected = dealerNumberSelected;
	}    
	
	public Boolean getSerialNumberSelected() {
		return serialNumberSelected;
	}

	public void setSerialNumberSelected(Boolean serialNumberSelected) {
		this.serialNumberSelected = serialNumberSelected;
	}

	/**
	 * @param competitorModels the competitorModels to set
	 */
	public void setCompetitorModels(List<ListOfValues> competitorModels) {
		this.competitorModels = competitorModels;
	}

	/**
	 * @return the competitorModels
	 */
	public List<ListOfValues> getCompetitorModels() {
		return competitorModels;
	}

	/**
	 * @param productModelSelected the productModelSelected to set
	 */
	public void setProductModelSelected(Boolean productModelSelected) {
		this.productModelSelected = productModelSelected;
	}

	/**
	 * @return the productModelSelected
	 */
	public Boolean getProductModelSelected() {
		return productModelSelected;
	}

	public String getPartInstalledOn() {
		return partInstalledOn;
	}

	public void setPartInstalledOn(String partInstalledOn) {
		this.partInstalledOn = partInstalledOn;		
	}
    
}