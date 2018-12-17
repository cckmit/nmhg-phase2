package tavant.twms.domain.claim.claimsubmission;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;


public class MachineClaimSubmissionUtil {
    private ClaimSubmissionUtil claimSubmissionUtil;


    public void setClaimSubmissionUtil(ClaimSubmissionUtil claimSubmissionUtil) {
        this.claimSubmissionUtil = claimSubmissionUtil;
    }

    public Map<String, String[]> validateMachineClaim(Claim claim, Map<String, List<java.lang.String>> fieldErrors,
            List<ClaimedItem> tempList, String isForSerialized) {
        Map<String, String[]> errorCodes = new HashMap<String, String[]>();
        boolean forSerializedSet = true;
        if(!StringUtils.hasText(isForSerialized)) {
	    	if(claim.getItemReference().getReferredInventoryItem() == null && claim.getItemReference().getModel() != null) {
	    		isForSerialized = "false";
	    	} else if(claim.getItemReference().getReferredInventoryItem() != null) {
	    		isForSerialized = "true";
	    	}
	    }
        if (claim.getForDealer() == null) {
            errorCodes.put("error.newClaim.selectDealer", null);
        }
        if (claim != null) {
            List<ClaimedItem> claimedItems = claim.getClaimedItems();
            int totalClaimedItems = claimedItems.size();
            if (totalClaimedItems == 1) {
                ItemReference itemReference = claimedItems.get(0).getItemReference();

                if(itemReference!=null && itemReference.getReferredItem()!=null && itemReference.getReferredItem().getItemType()!=null && claim.getType()!=null)
                {
                    if(itemReference.getReferredItem().getItemType().equalsIgnoreCase(ClaimType.MACHINE.toString()) && !claim.getType().equals(ClaimType.MACHINE))
                    {
                        errorCodes.put("error.newClaim.invalidIMachineItemType", null);
                    }
                     if(itemReference.getReferredItem().getItemType().equalsIgnoreCase(ClaimType.ATTACHMENT.toString()) && !claim.getType().equals(ClaimType.ATTACHMENT))
                    {
                        errorCodes.put("error.newClaim.invalidIAttachmentItemType", null);
                    }
                }

                if ((!StringUtils.hasText(isForSerialized) || ClaimSubmissionUtil.TRUE.equals(isForSerialized)) && itemReference != null
                        && itemReference.getReferredInventoryItem() == null) {
                    errorCodes.put("error.newClaim.invalidInventoryItem", null);
                } else if (ClaimSubmissionUtil.FALSE.equals(isForSerialized)
                        && (itemReference != null && (itemReference.getModel() == null || itemReference
                                .getModel().getId() == null))) {
                    errorCodes.put("error.newClaim.invalidBaseModelEmpty", null);
                }
                if (ClaimSubmissionUtil.FALSE.equals(isForSerialized)) {
                	
                    if (claim.getForDealer() == null) {
                        errorCodes.put("error.newClaim.selectDealer", null);
                    }
                    if (claim.getPurchaseDate() == null) {
                        errorCodes.put("error.newClaim.purchasedateRequired", null);
                    }
                    if (claim.getFailureDate() != null && claim.getPurchaseDate() != null
                            && claim.getFailureDate().isBefore(claim.getPurchaseDate())) {
                        errorCodes.put("error.newClaim.invalidPurchaseDuration", null);
                    }
                    /**
                     * If invoice number is configured,it is mandatory
                     */
                    if (claimSubmissionUtil.isInvoiceNumberApplicable()
                            && !StringUtils.hasText(claim.getInvoiceNumber())) {
                        errorCodes.put("message.error.invoiceNumberMandatory", null);
                    }

                    // Date code is mandatory only for non-serialized claims and
                    // also if it is enabled for BU
                    // We are validating only at Draft level since we don't
                    // provide an option modify the
                    // Date code at the later life cycle of claim.
                    if (claimSubmissionUtil.isDateCodeEnabled() && !StringUtils.hasText(claim.getDateCode()))
                        errorCodes.put("error.newClaim.dateCodeRequired", null);

                }
                boolean isItemNumberDisplayed = false;
                if(forSerializedSet) {
                	isItemNumberDisplayed = claimSubmissionUtil.isItemNumberDisplayRequired();
                }
                if (isItemNumberDisplayed && ClaimSubmissionUtil.FALSE.equals(isForSerialized)
                        && (itemReference != null && itemReference.getReferredItem() == null)) {
                    errorCodes.put("error.newClaim.invalidItemNumberEmpty", null);
                }
                if (fieldErrors != null && !fieldErrors.containsKey("claim.hoursInService")) {
                    if (claimedItems.get(0).getHoursInService() == null) {
                        errorCodes.put("error.newClaim.serviceHrsRequired", null);
                    } else if (claimedItems.get(0).getHoursInService() != null
                            && claimedItems.get(0).getHoursInService().longValue() < 0) {
                        errorCodes.put("error.newClaim.serviceHrsInvalidValue", null);
                    }
                }

                boolean modelSpecified = itemReference.getModel() != null;
                if (ClaimSubmissionUtil.FALSE.equals(isForSerialized) && modelSpecified) {
                    InventoryItem inventoryItem = null;
                    if (StringUtils.hasText(itemReference.getUnszdSlNo())
                            && itemReference.getModel().getName() != null) {
                        inventoryItem = claimSubmissionUtil.getInventoryItemForNonSerializedSerialNumber(itemReference
                                .getUnszdSlNo(), itemReference.getModel().getName());
                    }
                    if (inventoryItem != null) {
                        itemReference.setReferredInventoryItem(inventoryItem);
                    }
                    itemReference.setSerialized(inventoryItem != null);
                }
            } else if (totalClaimedItems > 1) {
                for (Iterator<ClaimedItem> iterator = claimedItems.iterator(); iterator.hasNext();) {
                    ClaimedItem claimedItem = (ClaimedItem) iterator.next();
                    if (claimedItem==null || claimedItem.getItemReference().getReferredInventoryItem() == null) {
                        iterator.remove();
                    }
                }
                if (claimedItems.size() == 0) {
                    errorCodes.put("error.newClaim.itemRequired", null);
                    claim.setClaimedItems(tempList);
                } else {
                    int counter = 0;
                    boolean isTextEntered = false;
                    for (ClaimedItem claimedItem : claimedItems) {
                        if (fieldErrors.containsKey("claim.claimedItems[" + counter
                                + "].hoursInService")) {
                            isTextEntered = true;
                            break;
                        }
                        counter++;
                    }
                    for (ClaimedItem claimedItem : claimedItems) {
                        if (!isTextEntered) {
                            if (claimedItem.getHoursInService() == null) {
                                errorCodes.put("error.newClaim.serviceHrsRequired", null);
                                break;
                            } else if (claimedItem.getHoursInService() != null
                                    && claimedItem.getHoursInService().longValue() < 0) {
                                errorCodes.put("error.newClaim.serviceHrsInvalidValue", null);
                                break;
                            }
                        }
                    }
                }
            }
            if(claim.getForItem()!=null){
            	if(!claim.isNcr()){
            		if(isForSerialized.equals("true") && claim.getForItem().isRetailed() && claim.getForItem().getDeliveryDate()!=null && claim.getFailureDate()!=null && claim.getFailureDate().isBefore(claim.getForItem().getDeliveryDate())){
            			errorCodes.put("error.newClaim.failureDateBeforeDeliveryDate",null);           	
            		}
            	}else{
            		if(isForSerialized.equals("true") && claim.getForItem().getShipmentDate()!=null && claim.getFailureDate()!=null && claim.getFailureDate().isBefore(claim.getForItem().getShipmentDate())){
            			errorCodes.put("error.newNCRClaim.failureDateBeforeShipmentDate",null);
            		}
            		if(isForSerialized.equals("true") && claim.getForItem().isRetailed() && claim.getForItem().getDeliveryDate()!=null && claim.getFailureDate()!=null && claim.getFailureDate().isBefore(claim.getForItem().getDeliveryDate())){
            			errorCodes.put("error.newNCRClaim.failureDateAfterDeliveryDate",null);
            		}
            	}
         }
      }
        return errorCodes;
    }

}
