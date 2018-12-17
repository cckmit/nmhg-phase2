package tavant.twms.domain.claim.claimsubmission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.I18nDomainTextReader;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;

public class PartsClaimSubmissionUtil {

	private ClaimSubmissionUtil claimSubmissionUtil;

	private static final String COMPETITOR_MODEL = "ClaimCompetitorModel";

	private LovRepository lovRepository;
	
	private ConfigParamService configParamService;	   
	
	private InventoryService inventoryService;
	
	private I18nDomainTextReader i18nDomainTextReader;
	
	private final static Logger logger = Logger
			.getLogger(PartsClaimSubmissionUtil.class.getName());

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public void setClaimSubmissionUtil(ClaimSubmissionUtil claimSubmissionUtil) {
		this.claimSubmissionUtil = claimSubmissionUtil;
	}
	
	public Map<String, String[]> validate(PartsClaim claim, String isForSerialized, Boolean productModelSelected,Boolean serialNumberSelected) {

		return validate(claim, isForSerialized,productModelSelected,serialNumberSelected,null);
	}
		
	public Map<String, String[]> validate(PartsClaim claim){
		Map<String, String[]> errorMap = new HashMap<String, String[]>();
		InventoryItem partInvItem = claim.getPartItemReference().getReferredInventoryItem();
		
		if (partInvItem.getInstallationDate() != null && claim.getFailureDate() != null) {
			
			if (partInvItem.getInstallationDate().isAfter(claim.getFailureDate()))
				errorMap.put("error.partsClaim.failureDateBeforeInvInstallationDate",
						null);
		}
		if (partInvItem.getInstallationDate() != null && claim.getRepairDate() != null) {
			
			if (partInvItem.getInstallationDate().isAfter(claim.getRepairDate()))
				errorMap.put("error.partsClaim.repairDateBeforeInvInstallationDate",
						null);
		}
		return errorMap;
	}
	
	public Map<String, String[]> validate(PartsClaim claim,
			String isForSerialized, Boolean productModelSelected,Boolean serialNumberSelected,String partInstalledOn) {
		Map<String, String[]> errorMap = new HashMap<String, String[]>();
	    if(!StringUtils.hasText(isForSerialized)) {
	    	if(claim.getItemReference().getReferredInventoryItem() == null && claim.getItemReference().getModel() != null) {
	    		isForSerialized = "false";
	    	} else if(claim.getItemReference().getReferredInventoryItem() != null) {
	    		isForSerialized = "true";
	    	}
	    }
	    if (claim.getInstallationDate() == null && partInstalledOn !=null && !partInstalledOn.equals("PART_NOT_INSTALLED")) {
			errorMap.put("error.newClaim.warrantyStartDateRequired", null);
		}
		if (claim.getForDealer() == null) {
			errorMap.put("error.newClaim.selectDealer", null);
		}
		if(serialNumberSelected)
		{
		if (claim.getPartItemReference().getReferredInventoryItem() == null) {
			errorMap.put("error.newClaim.partSerialNumberRequired", null);
		}
		}
		else
		{
			if (claim.getPartItemReference().getReferredItem() == null)
			{
				errorMap.put("error.newClaim.partNumberRequired",
						null);
			}
			 
		}
		
		if (!claim.getPartItemReference().isSerialized()) {
			 if (!claim.getPartInstalled()
					&& claim.getPurchaseDate() == null) {
				errorMap.put("error.newClaim.purchasedateRequired",
						null);
			}
		}
		
		
		if (claim.getInstallationDate() != null) {
			if (claim.getInstallationDate().isAfter(claim.getFailureDate()))
				errorMap.put("error.newClaim.invalidInstallationDuration",
						null);
		}
		 
		if (claim.getPurchaseDate() != null) {
			claim.getInstallationDate();
			if (claim.getPurchaseDate().isAfter(claim.getFailureDate()))
				errorMap.put("error.newClaim.invalidPurchaseDuration",
						null);
		}
		List<ClaimedItem> claimedItems = claim.getClaimedItems();
		ItemReference itemReference = claimedItems.get(0).getItemReference();
		if (ClaimSubmissionUtil.FALSE.equals(isForSerialized) && claim.getPartInstalled()) {
			if (itemReference != null) {
				if (claimSubmissionUtil.isItemNumberDisplayRequired()
						&& itemReference.getReferredItem() == null) {
					errorMap.put("error.newClaim.invalidItemNumberEmpty",
							null);
				}
				if (itemReference.getModel() == null
						|| (itemReference.getModel() != null && itemReference.getModel().getId() == null)) {
					errorMap.put("error.newClaim.invalidBaseModelEmpty",
							null);
				}
			}
	
			if (claim.getItemReference().getModel() != null
					&& itemReference.getModel().getId() != null) {
				claimSubmissionUtil
						.checkIfInvExistsForNonSerializedSerialNumber(claim);
			}

			if (claimSubmissionUtil.isDateCodeEnabled()
					&& !StringUtils.hasText(claim.getDateCode())) {
				errorMap.put("error.newClaim.dateCodeRequired",
						null);
			}
	
		}
		if(productModelSelected != null && claim.getPartInstalled() && !ClaimSubmissionUtil.FALSE.equals(isForSerialized)) {			
			if(productModelSelected) {
				if(!StringUtils.hasText(claim.getCompetitorModelBrand())){
					errorMap.put("error.marketInfo.competitiorModelBrand",
							null);	
				}
				if(!StringUtils.hasText(claim.getCompetitorModelDescription())){
					
					errorMap.put("error.marketInfo.competitiorModelDescription",
							null);	
				}
				if(!StringUtils.hasText(claim.getCompetitorModelTruckSerialnumber())){
					errorMap.put("error.marketInfo.competitiorModelTruckSerialNumber",
							null);	
				}
			} else {
				//claim.setCompetitorModelBrand(null);
				//claim.setCompetitorModelDescription(null);
				//claim.setCompetitorModelTruckSerialnumber(null);
				if(claim.getItemReference().getReferredInventoryItem() == null) {
					errorMap.put("error.newClaim.serialNoRequired",
							null);
				}
			}
		} else {
			claim.setClaimCompetitorModel(null);
			claim.setCompetitorModelDescription(null);
			claim.setCompetitorModelTruckSerialnumber(null);
		}
		if ((!StringUtils.hasText(isForSerialized) || ClaimSubmissionUtil.TRUE
				.equals(isForSerialized))
				&& claim.getPartInstalled() && (productModelSelected != null && !productModelSelected)) {
			claimSubmissionUtil.validateRepairDate(claim, errorMap);
		}
		
		  
		if (claim.getHoursInService() == null && partInstalledOn !=null){
			if(!partInstalledOn.equals("PART_INSTALLED_ON_NON_SERIALIZED_HOST")){
				if(!partInstalledOn.equals("PART_NOT_INSTALLED")){
					 errorMap.put("error.newClaim.serviceHrsRequired", null);
				}				
			}
		}
		  else if(claim.getHoursInService()!=null && claim.getHoursInService().longValue()<0){
		  
			  errorMap.put("error.newClaim.serviceHrsInvalid",null) ;      
		}
		  if (claim.getHoursOnTruck() == null && partInstalledOn !=null && !partInstalledOn.equals("PART_NOT_INSTALLED")) {
				errorMap.put("error.newClaim.hoursOnTruckInvalid", null);
			}
		  else if(claim.getHoursOnTruck()!=null && claim.getHoursOnTruck().longValue()<0){
		  
			  errorMap.put("error.newClaim.hoursOnTruckWhenInvalid",null) ;      
		}
		 
		  
		  if(claim.getForItem()!=null){
			  if(claim.getForItem()!=null && claim.getForItem().isRetailed() && claim.getForItem().getDeliveryDate()!=null && claim.getFailureDate()!=null && claim.getFailureDate().isBefore(claim.getForItem().getDeliveryDate()) && partInstalledOn !=null && !partInstalledOn.equals("PART_INSTALLED_ON_COMPETITOR_MODEL")){
				  errorMap.put("error.newClaim.failureDateBeforeDeliveryDate",null);           	
			  }
		 }	  
		return errorMap;
			
	}
	public Map<String, String[]> validateRegisteredPart(Claim claim) {
		Map<String, String[]> errorMap = new HashMap<String, String[]>();
		if (claim.getPartItemReference().getReferredInventoryItem() != null) {  // if part is serialized part
			
			/**
			 * unit => null (if part is stand alone part)
			 * unit => not-null (if part is installed on serailized machine)
			 */
			
			InventoryItem unit = this.inventoryService.findInventoryItemForMajorComponent(claim.getPartItemReference()
					.getReferredInventoryItem().getId());
			ItemReference unitItemReference = claim.getClaimedItems().get(0).getItemReference();
			if (unit != null) {
				
				if (unitItemReference.getReferredInventoryItem() == null) {
					errorMap.put("error.newClaim.invalidMajorComponent", new String[] { claim.getPartItemReference()
							.getReferredInventoryItem().getSerialNumber() });
					
				} else if (!unitItemReference.getReferredInventoryItem().equals(unit)) {
							errorMap.put("error.newClaim.invalidSerializedMachine", new String[] { claim.getPartItemReference()
									.getReferredInventoryItem().getSerialNumber(), unit.getSerialNumber() });
				}
				
			} else if (unit == null && claim.getItemReference().getReferredInventoryItem() != null) {
				StringBuilder errorInvalidUnit = new StringBuilder();					
				errorInvalidUnit.append(" " + i18nDomainTextReader.getProperty("label.newClaim.isInstalledOnCompetitorModel") + "\" ");
				if (isPartsClaimWithoutHostAllowed()) {
					errorInvalidUnit.append("or");
					errorInvalidUnit.append(" \"" + i18nDomainTextReader.getProperty("label.newClaim.notInstalled") + "\" ");
				}
				if (isNonSerializedClaimAllowed()) {
					errorInvalidUnit.append("or");
					errorInvalidUnit.append(" \"" + i18nDomainTextReader.getProperty("label.newClaim.isInstalledOnNonSerializedHost"));
				}	
				if (claim.getItemReference().getUnszdSlNo() != null) {
					errorMap.put("error.newClaim.existingInventoryItem", new String[] { errorInvalidUnit.toString() });
				} else {
					errorMap.put("error.newClaim.invalidUnit", new String[] {
							claim.getPartItemReference().getReferredInventoryItem().getSerialNumber(),
							errorInvalidUnit.toString() });
				}
			}			
		}
		return errorMap;
	}
	
	public List<ListOfValues> getCompetitorModels() {
        return lovRepository.findAllActive(COMPETITOR_MODEL);
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	 public boolean isNonSerializedClaimAllowed(){
	        return configParamService.getBooleanValue(ConfigName.NON_SERIALIZED_CLAIM_ALLOWED.getName());
	}
	 
	 public boolean isPartsClaimWithoutHostAllowed() {
	        return configParamService.getBooleanValue(ConfigName.PARTS_CLAIM_WITHOUT_HOST_ALLOWED.getName());
	 }
	 
	 public void setConfigParamService(ConfigParamService configParamService) {
			this.configParamService = configParamService;
	}

		public ConfigParamService getConfigParamService() {
			return configParamService;
	}

	public I18nDomainTextReader getI18nDomainTextReader() {
		return i18nDomainTextReader;
	}

	public void setI18nDomainTextReader(I18nDomainTextReader i18nDomainTextReader) {
		this.i18nDomainTextReader = i18nDomainTextReader;
	}
		
		
}
