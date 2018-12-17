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
package tavant.twms.domain.partreturn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.mail.Part;

import org.acegisecurity.concurrent.SessionInformation;
import org.apache.log4j.Logger;
import org.apache.poi.util.SystemOutLogger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignAssignmentService;
import tavant.twms.domain.campaign.OEMPartToReplace;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.inventory.ItemReplacementReason;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.DealerSchemeService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.query.HibernateQuery;
import tavant.twms.domain.query.HibernateQueryGenerator;
import tavant.twms.domain.query.PartReturnClaimSummary;
import tavant.twms.domain.query.PartReturnQueryGenerator;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author vineeth.varghese
 *
 */
public class PartReturnServiceImpl implements PartReturnService , BeanFactoryAware  {

    private static final Logger LOGGER = Logger.getLogger(PartReturnServiceImpl.class);

    private PartReturnDefinitionRepository partReturnDefinitionRepository;

    private Map<String, PaymentConditionEvaluator> paymentConditionEvaluators;

    private LovRepository lovRepository;

    private CampaignAssignmentService campaignAssignmentService;

    private PartReturnRepository partReturnRepository;

    private DealerGroupService dealerGroupService;

    private LocationRepository locationRepository;

    private PredicateAdministrationService predicateAdministrationService;

    private InventoryService inventoryService;

    private boolean isDealerInTerritoryExclusion;
    
    private BeanFactory beanFactory;

    private SecurityHelper securityHelper;

    public void updatePartReturns(OEMPartReplaced partReplaced, Claim claim) {

        if (!partReplaced.getPartReturns().isEmpty() || partReplaced.getPartReturn() != null) {
            updateExistingPartReturns(partReplaced, claim);
            return;
        }
        Item item = partReplaced.getItemReference().getUnserializedItem();
        String warrantyType = getWarrantyType(claim);
        ServiceProvider forDealer = claim.getForDealer();
        Criteria criteria = populateCriteria(claim, item, warrantyType);
        boolean isCausalPart = item.equals(claim.getServiceInformation().getCausalPart());

        CalendarDate eqmtBilledDate = claim.getEquipmentBilledDate();
        CalendarDate partReturnTriggerDate = Clock.today();
        isDealerInTerritoryExclusion = dealerGroupService.isDealerInTerritoryExclusion(claim.getForDealer());
        if(isDealerInTerritoryExclusion){
        	return;
        }
        // The 'date' that goes here should ideally be the
        // InventoryItem.billedDate
        // of the equipment of which this part. But that still needs to be in
        // place.
        // FIXME: Refine the date logic.
        // Refine the logic for picking up the exact logic
        PartReturnDefinition partReturnDefinition = this.partReturnDefinitionRepository
                .findPartReturnDefinition(item, criteria);

        //We don't need this logic anymore since prc will be fetched based on the part number match.
       // List<PartReturnDefinition> partReturnDefinitions = this.partReturnDefinitionRepository.findPartReturnDefinitions(item, criteria);

        /*long topRelevance = partReturnDefinitions != null && partReturnDefinitions.size() > 0 ? partReturnDefinitions.get(0).getCriteria().getRelevanceScore() : 0;
        PartReturnDefinition partReturnDefinition = partReturnDefinitions != null && partReturnDefinitions.size() > 0 ? partReturnDefinitions.get(0) : null;
        //find the item match and return that one
        for(PartReturnDefinition partDef : partReturnDefinitions){
            if(partDef.getCriteria().getRelevanceScore() == topRelevance){
                if(partDef.getItemCriterion() != null && partDef.getItemCriterion().getItem() != null &&
                        partDef.getItemCriterion().getItem().getAlternateNumber().equals(item.getNumber())){
                    partReturnDefinition = partDef;
                    break;
                }
            }
        }*/

        if (partReturnDefinition != null && !isDealerInTerritoryExclusion) {

        	//If the dealer available in exclusion list then no part return required.
        	Set<ServiceProvider> excludedDealers = partReturnDefinition.getExcludedDealers();
        	if(excludedDealers != null && excludedDealers.contains(claim.getForDealer())){ 
        		return;
        	}
        	//If the dealergroup available in exclusion list then no part return required.
        	Set<DealerGroup> excludedDealerGroups = partReturnDefinition.getExcludedDealerGroups();
        	if(excludedDealerGroups != null){ 
        		for(DealerGroup dealerGroup : excludedDealerGroups){
        			if(dealerGroup.isDealerInGroup(forDealer)){
        				return;
        			}
        		}
        	}
        	
            PartReturnConfiguration partReturnConfiguration = partReturnDefinition
                    .findConfigurationFor(eqmtBilledDate, isCausalPart);
            if ((partReturnConfiguration != null) && (eqmtBilledDate != null)
                    && areAllPartsNotReceived(partReturnConfiguration)) {
                List<PartReturn> partReturns = new ArrayList<PartReturn>();
                int prcCounter=partReplaced.getNumberOfUnits();
                if (partReturnConfiguration.getMaxQuantity() != null) {
                    prcCounter = partReturnConfiguration.getMaxQuantity() - partReturnConfiguration.getQuantityReceived();
                    if (prcCounter > partReplaced.getNumberOfUnits()) {
                        prcCounter = partReplaced.getNumberOfUnits();
                    }
                }
                for (int count = 0; count < prcCounter; count++) {
                    partReturns.add(partReturnConfiguration.createPartReturn(partReturnTriggerDate,
                            eqmtBilledDate, forDealer));
                }
                if (partReturns != null && !partReturns.isEmpty()) {
                    partReplaced.setPartToBeReturned(true);
                }
                partReplaced.setPartReturns(partReturns);
                partReplaced.setPartReturnConfiguration(partReturnConfiguration);
            }
        }
        /*
         * If its a campaign claim and the return details are specified in the
         * campaign definition, pick up those details as the return
         * configuration, else if the part is marked for return with no details
         * specified in the campaign defn, then pick up the default
         * configuration.
         */

        if (claim.isOfType(ClaimType.CAMPAIGN)) {
            OEMPartToReplace partToReplace = findCampaignOemPart(item, claim);
            if (partToReplace!=null && partToReplace.getReturnLocation() != null) {
                List<PartReturn> partReturns = new ArrayList<PartReturn>();
                for (int count = 0; count < partReplaced.getNumberOfUnits(); count++) { //Modified since Part return qty should always be determined by the qty on the claim not on the Field Modification setup
                	if(partToReplace.getDueDays() == null){
                		continue;
                	}
                	partReturns.add(createPartReturnForCampaignClaim(claim, partToReplace));
                }
                partReplaced.setPartReturns(partReturns);
                partReplaced.setPartToBeReturned(true);
            }
        }
    }

    

	public String getWarrantyType(Claim claim) {
        Policy applicablePolicy = claim.getApplicablePolicy();
        boolean aPolicyIsApplicable = applicablePolicy != null;
        String warrantyType = null;
        if (aPolicyIsApplicable) {
            warrantyType = applicablePolicy.getWarrantyType().getType();
        }
        return warrantyType;
    }

    public Criteria populateCriteria(Claim claim, Item item, String warrantyType) {
        Criteria criteria = new Criteria();
        criteria.setDealerCriterion(new DealerCriterion(claim.getForDealer()));
        criteria.setWarrantyType(warrantyType);
        criteria.setClaimType(claim.getType());
        PartsClaim partsClaim = null;
        if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)) {
            partsClaim = new HibernateCast<PartsClaim>().cast(claim);
        }
        if (claim.getClaimedItems() != null
                && !claim.getClaimedItems().isEmpty() && claim.getClaimedItems().get(0) != null
                && claim.getClaimedItems().get(0).getItemReference() != null
                && claim.getClaimedItems().get(0).getItemReference().isSerialized()) {
            criteria.setProductType(claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem().getOfType().getProduct());
        } else if ((partsClaim != null && !partsClaim.getPartInstalled())||(partsClaim != null && partsClaim.getPartInstalled() && (partsClaim.getCompetitorModelBrand()!=null && !partsClaim.getCompetitorModelBrand().isEmpty()
				&& !partsClaim.getCompetitorModelDescription().isEmpty() && !partsClaim
				.getCompetitorModelTruckSerialnumber().isEmpty()))) {
            criteria.setProductType(claim.getPartItemReference().getUnserializedItem().getProduct());
        } else if (!claim.getClaimedItems().get(0).getItemReference().isSerialized()) {
            criteria.setProductType(claim.getClaimedItems().get(0).getItemReference().getModel().getIsPartOf());
        }
        return criteria;
    }

    private PartReturn createPartReturnForCampaignClaim(Claim claim, OEMPartToReplace partToReplace) {
        PartReturn partReturn = new PartReturn();
        Integer dueDays = partToReplace.getDueDays();
        partReturn.setDueDate(claim.getFiledOnDate().plusDays(dueDays));
        partReturn.setPaymentCondition(new PaymentCondition(partToReplace.getPaymentCondition()));
        partReturn.setReturnLocation(partToReplace.getReturnLocation());
        partReturn.setReturnedBy(claim.getForDealer());
        return partReturn;
    }

    private OEMPartToReplace findCampaignOemPart(Item item, Claim claim) {
        Campaign campaign = this.campaignAssignmentService.findCampaignAssociatedWithClaim(claim);
        List<OEMPartToReplace> oemParts = campaign.getAllOEMPartsReplace();
        for (OEMPartToReplace replace : oemParts) {
            if (replace.getItem().getNumber().equalsIgnoreCase(item.getNumber())) {
                return replace;
            }
        }
        return null;
    }

    public boolean isEligibleForPayment(Claim claim) {
        Collection<OEMPartReplaced> parts = claim.getServiceInformation().getServiceDetail()
                .getOEMPartsReplaced();
        boolean isClaimEligibleForPayment = true;
        for (OEMPartReplaced part : parts) {
            checkPaymentConditionConsistency(part);
            for (PartReturn partReturn : part.getPartReturns()) {
                if (partReturn != null) {
                    PaymentCondition paymentCondition = partReturn.getPaymentCondition();
                    PaymentConditionEvaluator paymentConditionEvaluator = this.paymentConditionEvaluators
                            .get(paymentCondition.getCode());
                    isClaimEligibleForPayment = isClaimEligibleForPayment
                            && paymentConditionEvaluator.isEligibleForPayment(partReturn);
                }

            }
        }
        return isClaimEligibleForPayment;
    }

    // Adding this for the time being... vineeth
    public void checkPaymentConditionConsistency(OEMPartReplaced partReplaced) {
        List<PartReturn> partReturns = partReplaced.getPartReturns();
        PaymentCondition firstPaymentCondition = null;
        for (PartReturn partReturn : partReturns) {
            firstPaymentCondition = firstPaymentCondition == null ? partReturn
                    .getPaymentCondition() : firstPaymentCondition;
            PaymentCondition paymentCondition = partReturn.getPaymentCondition();
            if (!(firstPaymentCondition.equals(paymentCondition))) {
                throw new IllegalStateException("Part Return Payment Conditions for Part["
                        + partReplaced.getItemReference().getUnserializedItem().getNumber()
                        + "] is inconsistent");
            }
        }
    }

    public void updatePartReturnsForClaim(Claim claim, List<OEMPartReplaced> replacedParts) {
        List<OEMPartReplaced> partsReplaced = claim.getServiceInformation()
				.getServiceDetail().getReplacedParts();
        //List<OEMPartReplaced>removedParts = fetchRemovedParts(claim,replacedParts);
        //partsReplaced.addAll(removedParts);
        for (OEMPartReplaced part : partsReplaced) {
            //If part is already shipped no point of updating the part return
            if (part.getNumberOfUnits() > 0 && !part.isPartShippedOrCannotBeShipped()) {
                Item item = part.getItemReference().getUnserializedItem();
                if(LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Attempting Part return for part [" + part + "] and item [" + item
                            + "]");
                }
                updatePartReturns(part, claim);
                if(LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Obtainted part return [" + part + "]");
                }
            }

        }
    }

    public void updateExistingPartReturns(OEMPartReplaced partReplaced, Claim claim) {
        // if(!ClaimState.DRAFT.equals(claim.getState()) &&
		// !ClaimState.SUBMITTED.equals(claim.getState())){

	    	Collection<PartReturn> partReturns = partReplaced.getPartReturns();
	        PartReturn updatedPartReturn = partReplaced.getPartReturn();
	        if(updatedPartReturn != null && partReplaced.isPartToBeReturned()){
	        updatedPartReturn.setOemPartReplaced(partReplaced);
	        updateChangesByProcessor(updatedPartReturn, partReturns);
	        reopenClosedPartReturns(updatedPartReturn, partReturns);
                if (partReturns != null) {
                    int partReturnsCountDiff = partReplaced.getNumberOfUnits()
                            - getPartReturnsCount(partReturns);
                    if (partReturnsCountDiff > 0) {
                        addNewPartReturns(partReturns, partReturnsCountDiff, updatedPartReturn);
                    } else {
                        removePartReturns(partReturns, partReturnsCountDiff);
                    }
                }
            } else if (!partReturns.isEmpty()) {
                removePartReturns(partReturns, partReturns.size());
            }
       }

	private void reopenClosedPartReturns(PartReturn updatedPartReturn,
			Collection<PartReturn> partReturns) {
		if(!updatedPartReturn.getStatus().equals(PartReturnStatus.CLOSE))
			return;
		CalendarDate partReturnTriggerDate = Clock.today();
		for(PartReturn pr : partReturns) {
			if(!pr.getStatus().equals(PartReturnStatus.CLOSE))
				continue;
			pr.setStatus(PartReturnStatus.PART_TO_BE_SHIPPED);
			pr.setTriggerStatus(PartReturnTaskTriggerStatus.TO_BE_TRIGGERED);
			pr.setDueDate(partReturnTriggerDate.plusDays(pr.getActualDueDays()));
		}
	}
	
    private Integer getPartReturnsCount(Collection<PartReturn> partReturns) {
        int count = 0;
        for (PartReturn partReturn : partReturns) {
            if ((PartReturnTaskTriggerStatus.TO_BE_TRIGGERED.ordinal() <= partReturn.getTriggerStatus()
                    .ordinal())&& !PartReturnStatus.REMOVED_BY_PROCESSOR.equals(partReturn.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private boolean isPartRemovedOrClaimDenied(OEMPartReplaced partReplaced){
        int count = 0;
        for (PartReturn partReturn : partReplaced.getPartReturns()) {
           if(PartReturnStatus.REMOVED_BY_PROCESSOR.equals(partReturn.getStatus())){
               count ++;
           }
        }
        if(partReplaced.getNumberOfUnits().intValue()==count){
            return true;
        }else{
            return false;
        }
    }

    private void removePartReturns(Collection<PartReturn> partReturns, int partReturnsCountDiff) {
        partReturnsCountDiff = Math.abs(partReturnsCountDiff);
        Map<String, Collection<PartReturn>> partReturnMap = getPartReturnsSortedMap(partReturns);
        Collection<PartReturn> partToBeShippedReturns = partReturnMap
                .get(PartReturnStatus.PART_TO_BE_SHIPPED.getStatus());
        for (PartReturn partReturn : partToBeShippedReturns) {
            if (partReturnsCountDiff > 0) {
                partReturn.setTriggerStatus(PartReturnTaskTriggerStatus.TO_BE_ENDED);
                partReturn.setStatus(PartReturnStatus.REMOVED_BY_PROCESSOR);
                partReturnsCountDiff--;
            }
        }
        if (partReturnsCountDiff > 0) {
            Collection<PartReturn> shipmentGeneratedReturns = partReturnMap
                    .get(PartReturnStatus.SHIPMENT_GENERATED.getStatus());
            for (PartReturn partReturn : shipmentGeneratedReturns) {
                if (partReturnsCountDiff > 0) {
                    partReturn.setTriggerStatus(PartReturnTaskTriggerStatus.TO_BE_ENDED);
                    partReturn.setStatus(PartReturnStatus.REMOVED_BY_PROCESSOR);
                    partReturnsCountDiff--;
                }
            }
        }
    }

    private void addNewPartReturns(Collection<PartReturn> partReturns, int partReturnsCountDiff,
            PartReturn updatePartReturn) {
        if (updatePartReturn.getOemPartReplaced().isPartToBeReturned()) {
            for (int count = 0; count < partReturnsCountDiff; count++) {
                partReturns.add(createOrUpdatePartReturn(updatePartReturn, new PartReturn()));
            }
        }
    }

    private void updateChangesByProcessor(PartReturn updatedPartReturn,
            Collection<PartReturn> partReturns) {
        Iterator<PartReturn> partReturnIttr = partReturns.iterator();

        if (!updatedPartReturn.getOemPartReplaced().isPartToBeReturned()
                && PartReturnTaskTriggerStatus.TRIGGERED.equals(updatedPartReturn
                        .getTriggerStatus())) {
            updatedPartReturn.setTriggerStatus(PartReturnTaskTriggerStatus.TO_BE_ENDED);
        }
        if (updatedPartReturn.getReturnLocation() != null
                && StringUtils.hasText(updatedPartReturn.getReturnLocation().getCode())
                && updatedPartReturn.getReturnLocation().getId() == null) {
            updatedPartReturn.setReturnLocation(this.locationRepository
                    .findByLocationCode(updatedPartReturn.getReturnLocation().getCode()));
        }
        if (updatedPartReturn.getPaymentCondition() != null
                && StringUtils.hasText(updatedPartReturn.getPaymentCondition().getCode())) {
            updatedPartReturn.setPaymentCondition(this.findPaymentCondition(updatedPartReturn
                    .getPaymentCondition().getCode()));
        }
        //Commenting the if condition because, Due Date should ALWAYS get updated based on the Current date and due days.
        //if (updatedPartReturn.getDueDate() == null) {
            //updatedPartReturn.setDueDate(Clock.today().plusDays(updatedPartReturn.getDueDays())); 
        //}
        if(!updatedPartReturn.isDueDaysReadOnly()) {
	        if (updatedPartReturn.getDueDate() != null
	        		&& updatedPartReturn.getDueDate().compareTo(Clock.today().plusDays(updatedPartReturn.getActualDueDays()))!=0)
	        {
	        	updatedPartReturn.setDueDateUpdated(true);
	        }
	        updatedPartReturn.setDueDate(Clock.today().plusDays(updatedPartReturn.getActualDueDays()));
        }
        for (; partReturnIttr.hasNext();) {
            createOrUpdatePartReturn(updatedPartReturn, partReturnIttr.next());
        }

    }

    private PartReturn createOrUpdatePartReturn(PartReturn partReturn, PartReturn newPartReturn) {

        newPartReturn.setPaymentCondition(partReturn.getPaymentCondition());
        newPartReturn.setReturnedBy(partReturn.getReturnedBy());
        newPartReturn.setReturnLocation(partReturn.getReturnLocation());
        newPartReturn.setDueDays(partReturn.getActualDueDays());
        newPartReturn.setDueDate(partReturn.getDueDate());
        newPartReturn.setOemPartReplaced(partReturn.getOemPartReplaced());
        newPartReturn.setRmaNumber(partReturn.getRmaNumber());
        newPartReturn.setDealerPickupLocation(partReturn.getDealerPickupLocation());
        if (PartReturnTaskTriggerStatus.TO_BE_ENDED.equals(partReturn.getTriggerStatus())) {
            newPartReturn.setTriggerStatus(partReturn.getTriggerStatus());
        }

        return newPartReturn;

    }

    private Map<String, Collection<PartReturn>> getPartReturnsSortedMap(
            Collection<PartReturn> partReturns) {
        Map<String, Collection<PartReturn>> partReturnsMap = new HashMap<String, Collection<PartReturn>>();
        Collection<PartReturn> shipmentGeneratedReturns = new ArrayList<PartReturn>();
        Collection<PartReturn> partToBeShippedReturns = new ArrayList<PartReturn>();
        for (PartReturn partReturn : partReturns) {
        	if (PartReturnStatus.REMOVED_BY_PROCESSOR.equals(partReturn.getStatus())) {
        		continue;
        	}
        	if (PartReturnStatus.PART_TO_BE_SHIPPED.equals(partReturn.getStatus())) {
        		partToBeShippedReturns.add(partReturn);
        	}else if (PartReturnStatus.SHIPMENT_GENERATED.equals(partReturn.getStatus())) {
                shipmentGeneratedReturns.add(partReturn);
            }
        }
        partReturnsMap.put(PartReturnStatus.SHIPMENT_GENERATED.getStatus(),
                shipmentGeneratedReturns);
        partReturnsMap.put(PartReturnStatus.PART_TO_BE_SHIPPED.getStatus(),
        		partToBeShippedReturns);
        return partReturnsMap;
    }

    public void updateBOMForOEMParts(Claim claim) {
        ServiceInformation serviceInformation = claim.getServiceInformation();
        ServiceDetail serviceDetail = serviceInformation.getServiceDetail();
        Collection<OEMPartReplaced> partsReplaced = serviceDetail.getReplacedParts();
        InventoryItem referredInventoryItem = claim.getPartItemReference()
                .getReferredInventoryItem();

        for (OEMPartReplaced part : partsReplaced) {
            // Update the BOM of the Inventrory Items involved
            if ((part.getSerialNumberOfNewPart() != null)
                    && (part.getSerialNumberOfNewPart().trim().length() > 0)) {
                String serialNumberOfNewPart = part.getSerialNumberOfNewPart().trim();
                InventoryItem newInventoryItem = null;
                try {
                    newInventoryItem = this.inventoryService.findSerializedItem(serialNumberOfNewPart);
                    // Inventory Item already exists
                } catch (ItemNotFoundException e) {
                    // Inventory Item doesn't exists, so create it.
                    InventoryItem replacedInventoryItem = part.getItemReference()
                            .getReferredInventoryItem();
                    newInventoryItem = new InventoryItem();
                    newInventoryItem.setSerialNumber(serialNumberOfNewPart);
                    newInventoryItem.setOfType(replacedInventoryItem.getOfType());
                    this.inventoryService.createInventoryItem(newInventoryItem);
                    ItemReplacementReason itemReplacementReason = new ItemReplacementReason();
                    itemReplacementReason.setClaim(claim);
                    // Update the BOM of Claim's ItemReference
                    referredInventoryItem.replaceSerializedPart(replacedInventoryItem,
                            newInventoryItem, itemReplacementReason);
                }
            }
        }
    }

    public void acceptPartAfterInspection(List<PartReturn> partReturns, String comments,
            String accepatanceCode) {
        if ((partReturns == null) || (partReturns.size() == 0)) {
            throw new IllegalArgumentException("There are no parts selected for inspection.");
        }
        PartAcceptanceReason acceptanceReason = (PartAcceptanceReason) this.lovRepository.findByCode(
                PartAcceptanceReason.class.getSimpleName(), accepatanceCode);
        for (PartReturn partReturn : partReturns) {
            partReturn.acceptPartAfterInspection(comments, acceptanceReason);
        }
    }

    public void rejectPartAfterInspection(List<PartReturn> partReturns, String failureCode,
            String comments) {
        if ((partReturns == null) || (partReturns.size() == 0)) {
            throw new IllegalArgumentException("There are no parts selected for inspection.");
        }

        FailureReason failureReason = (FailureReason) this.lovRepository.findByCode(FailureReason.class
                .getSimpleName(), failureCode);
        for (PartReturn part : partReturns) {
            part.rejectPartAfterInspection(failureReason, comments);
        }
    }

    private Map<PartReturnStatus,Integer> preparePartReturnMap(OEMPartReplaced partReplaced) {
    	HashMap<PartReturnStatus,Integer> partReturnMap = new HashMap<PartReturnStatus,Integer>();
    	for(PartReturn pr : partReplaced.getPartReturns()) {
    		/*The code has been rewritten so that the Audit generated when a part 
    		 * is received and inspected at the same time displays the correct value /TSAUAT-37 (Not a clean way,but couldnt find an alternate workaround)*/    
    		if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.PART_RECEIVED.getStatus())
    				 && (PartReturnStatus.PART_ACCEPTED.equals(pr.getStatus()) || PartReturnStatus.PART_REJECTED.equals(pr.getStatus()))){
    			partReturnMap.put(PartReturnStatus.PART_RECEIVED,partReturnMap.get(PartReturnStatus.PART_RECEIVED)==null ? 
        				1 : (partReturnMap.get(PartReturnStatus.PART_RECEIVED)+1));
    		}
            else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER.getStatus())){
    			partReturnMap.put(PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER,partReturnMap.get(PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER)==null ?
        				1 : (partReturnMap.get(PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER)+1));
    		}
             else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.NMHG_TO_DEALER_SHIPMENT_GENERATED.getStatus())){
    			partReturnMap.put(PartReturnStatus.NMHG_TO_DEALER_SHIPMENT_GENERATED,partReturnMap.get(PartReturnStatus.NMHG_TO_DEALER_SHIPMENT_GENERATED)==null ?
        				1 : (partReturnMap.get(PartReturnStatus.NMHG_TO_DEALER_SHIPMENT_GENERATED)+1));
    		}
             else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.NMHG_TO_DEALER_PART_SHIPPED.getStatus())){
    			partReturnMap.put(PartReturnStatus.NMHG_TO_DEALER_PART_SHIPPED,partReturnMap.get(PartReturnStatus.NMHG_TO_DEALER_PART_SHIPPED)==null ?
        				1 : (partReturnMap.get(PartReturnStatus.NMHG_TO_DEALER_PART_SHIPPED)+1));
    		}
             else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.RETURN_CANCELLED_AS_CLAIM_RESUBMITTED.getStatus())){
    			partReturnMap.put(PartReturnStatus.RETURN_CANCELLED_AS_CLAIM_RESUBMITTED,partReturnMap.get(PartReturnStatus.RETURN_CANCELLED_AS_CLAIM_RESUBMITTED)==null ?
        				1 : (partReturnMap.get(PartReturnStatus.RETURN_CANCELLED_AS_CLAIM_RESUBMITTED)+1));
    		}

            else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.WAITING_FOR_CEVA_TRACKING_INFO.getStatus())){
                partReturnMap.put(PartReturnStatus.WAITING_FOR_CEVA_TRACKING_INFO,partReturnMap.get(PartReturnStatus.WAITING_FOR_CEVA_TRACKING_INFO)==null ?
                        1 : (partReturnMap.get(PartReturnStatus.WAITING_FOR_CEVA_TRACKING_INFO)+1));
            }

            else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.REMOVED_BY_PROCESSOR.getStatus())){
                partReturnMap.put(PartReturnStatus.REMOVED_BY_PROCESSOR,partReturnMap.get(PartReturnStatus.REMOVED_BY_PROCESSOR)==null ?
                        1 : (partReturnMap.get(PartReturnStatus.REMOVED_BY_PROCESSOR)+1));
            }
            else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.getStatus())){
                partReturnMap.put(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER,partReturnMap.get(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER)==null ?
                        1 : (partReturnMap.get(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER)+1));
            }
            else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.PART_NOT_RECEIVED_BY_SUPPLIER.getStatus())){
                partReturnMap.put(PartReturnStatus.PART_NOT_RECEIVED_BY_SUPPLIER,partReturnMap.get(PartReturnStatus.PART_NOT_RECEIVED_BY_SUPPLIER)==null ?
                        1 : (partReturnMap.get(PartReturnStatus.PART_NOT_RECEIVED_BY_SUPPLIER)+1));
            }
            else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.PARTS_COLLECTED_BY_DEALER.getStatus())){
                partReturnMap.put(PartReturnStatus.PARTS_COLLECTED_BY_DEALER,partReturnMap.get(PartReturnStatus.PARTS_COLLECTED_BY_DEALER)==null ?
                        1 : (partReturnMap.get(PartReturnStatus.PARTS_COLLECTED_BY_DEALER)+1));
            }
            else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.PARTS_NOT_COLLECTED_BY_DEALER.getStatus())){
                partReturnMap.put(PartReturnStatus.PARTS_NOT_COLLECTED_BY_DEALER,partReturnMap.get(PartReturnStatus.PARTS_NOT_COLLECTED_BY_DEALER)==null ?
                        1 : (partReturnMap.get(PartReturnStatus.PARTS_NOT_COLLECTED_BY_DEALER)+1));
            }
            else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.PART_MARKED_AS_SCRAPPED.getStatus())){
                partReturnMap.put(PartReturnStatus.PART_MARKED_AS_SCRAPPED,partReturnMap.get(PartReturnStatus.PART_MARKED_AS_SCRAPPED)==null ?
                        1 : (partReturnMap.get(PartReturnStatus.PART_MARKED_AS_SCRAPPED)+1));
            }
            else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.WPRA_GENERATED.getStatus())){
                partReturnMap.put(PartReturnStatus.WPRA_GENERATED,partReturnMap.get(PartReturnStatus.WPRA_GENERATED)==null ?
                        1 : (partReturnMap.get(PartReturnStatus.WPRA_GENERATED)+1));
            }
            else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.PART_MOVED_TO_OVERDUE.getStatus())){
                partReturnMap.put(PartReturnStatus.PART_MOVED_TO_OVERDUE,partReturnMap.get(PartReturnStatus.PART_MOVED_TO_OVERDUE)==null ?
                        1 : (partReturnMap.get(PartReturnStatus.PART_MOVED_TO_OVERDUE)+1));
            }
            else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.PART_NOT_RECIEVED.getStatus())){
                partReturnMap.put(PartReturnStatus.PART_NOT_RECIEVED,partReturnMap.get(PartReturnStatus.PART_NOT_RECIEVED)==null ?
                        1 : (partReturnMap.get(PartReturnStatus.PART_NOT_RECIEVED)+1));
            }
            else if(partReplaced.getPartAction1().getActionTaken().equalsIgnoreCase(PartReturnStatus.PART_AUTO_CONFIRMED.getStatus())){
                partReturnMap.put(PartReturnStatus.PART_AUTO_CONFIRMED,partReturnMap.get(PartReturnStatus.PART_AUTO_CONFIRMED)==null ?
                        1 : (partReturnMap.get(PartReturnStatus.PART_AUTO_CONFIRMED)+1));
            }
            else {
    		   partReturnMap.put(pr.getStatus(), partReturnMap.get(pr.getStatus())==null ?
    				1 : (partReturnMap.get(pr.getStatus())+1) );
    	    }
    	}
    	return partReturnMap;
    }
    
    public PartReturnStatus findPartReturnStatusOfReplacedOEMPart(OEMPartReplaced partReplaced) {
    	int numberOfUnits=0;
    	if(!CollectionUtils.isEmpty(partReplaced.getPartReturns())){
    		for(PartReturn pr : partReplaced.getPartReturns())
    			if(!pr.getStatus().equals(PartReturnStatus.REMOVED_BY_PROCESSOR))
    				numberOfUnits++;
    	}else{
    		numberOfUnits=partReplaced.getNumberOfUnits();
    	}
    	Map<PartReturnStatus,Integer> partReturnMap = preparePartReturnMap(partReplaced);
    	 if(partReturnMap.containsKey(PartReturnStatus.PART_ACCEPTED)) {
    		if(partReturnMap.get(PartReturnStatus.PART_ACCEPTED) == numberOfUnits)
    			return PartReturnStatus.PART_ACCEPTED;
    		else
    			return PartReturnStatus.PARTIALLY_ACCEPTED;
    	}
    	 else if(partReturnMap.containsKey(PartReturnStatus.PART_REJECTED)) {
    		if(partReturnMap.get(PartReturnStatus.PART_REJECTED) == numberOfUnits)
    			return PartReturnStatus.PART_REJECTED;
    		else
    			return PartReturnStatus.PARTIALLY_REJECTED;
    	}
    	else if(partReturnMap.containsKey(PartReturnStatus.PART_RECEIVED)) {
    		if(partReturnMap.get(PartReturnStatus.PART_RECEIVED) == numberOfUnits)
    			return PartReturnStatus.PART_RECEIVED;
    		else
    			return PartReturnStatus.PARTIALLY_RECEIVED;
    	}else if(partReturnMap.containsKey(PartReturnStatus.PART_SHIPPED)) {
    		if(partReturnMap.get(PartReturnStatus.PART_SHIPPED) == numberOfUnits)
    			return PartReturnStatus.PART_SHIPPED;
    		else
    			return PartReturnStatus.PARTIALLY_SHIPPED;
    	}else if(partReturnMap.containsKey(PartReturnStatus.CANNOT_BE_SHIPPED)) {
    		if(partReturnMap.get(PartReturnStatus.CANNOT_BE_SHIPPED) == numberOfUnits)
    			return PartReturnStatus.CANNOT_BE_SHIPPED;
    		int count = partReturnMap.get(PartReturnStatus.CANNOT_BE_SHIPPED);
    		count += (partReturnMap.containsKey(PartReturnStatus.SHIPMENT_GENERATED) ? 
    					partReturnMap.get(PartReturnStatus.SHIPMENT_GENERATED) : 0);
    		if(count == numberOfUnits)
    			return PartReturnStatus.SHIPMENT_GENERATED;
    		else
    			return PartReturnStatus.PARTIALLY_SHIPMENT_GENERATED;
    	}else if(partReturnMap.containsKey(PartReturnStatus.SHIPMENT_GENERATED)) {
    		if(partReturnMap.get(PartReturnStatus.SHIPMENT_GENERATED) == numberOfUnits)
    			return PartReturnStatus.SHIPMENT_GENERATED;
    		else
    			return PartReturnStatus.PARTIALLY_SHIPMENT_GENERATED;
    	}else if(partReturnMap.containsKey(PartReturnStatus.PART_TO_BE_SHIPPED)) {
    		return PartReturnStatus.PART_TO_BE_SHIPPED;
    	}else if(partReturnMap.containsKey(PartReturnStatus.REMOVED_BY_PROCESSOR)) {
    		return PartReturnStatus.REMOVED_BY_PROCESSOR;
    	}else if(partReturnMap.containsKey(PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER)) {
    		if(partReturnMap.get(PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER) == numberOfUnits)
    			return PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER;
    		else
    			return PartReturnStatus.DEALER_PARTIALLY_REQUESTED;
    	}
        else if(partReturnMap.containsKey(PartReturnStatus.NMHG_TO_DEALER_SHIPMENT_GENERATED)) {
    		if(partReturnMap.get(PartReturnStatus.NMHG_TO_DEALER_SHIPMENT_GENERATED) == numberOfUnits)
    			return PartReturnStatus.NMHG_TO_DEALER_SHIPMENT_GENERATED;
    		else
    			return PartReturnStatus.NMHG_TO_DEALER_PARTIALLY_SHIPMENT_GENERATED;
    	}
         else if(partReturnMap.containsKey(PartReturnStatus.NMHG_TO_DEALER_PART_SHIPPED)) {
    		if(partReturnMap.get(PartReturnStatus.NMHG_TO_DEALER_PART_SHIPPED) == numberOfUnits)
    			return PartReturnStatus.NMHG_TO_DEALER_PART_SHIPPED;
    		else
    			return PartReturnStatus.NMHG_TO_DEALER_PARTIALLY_SHIPPED;
    	}
         else if(partReturnMap.containsKey(PartReturnStatus.RETURN_CANCELLED_AS_CLAIM_RESUBMITTED)) {
    			return PartReturnStatus.RETURN_CANCELLED_AS_CLAIM_RESUBMITTED;
    	}
        else if(partReturnMap.containsKey(PartReturnStatus.WAITING_FOR_CEVA_TRACKING_INFO)) {
                return PartReturnStatus.WAITING_FOR_CEVA_TRACKING_INFO;
        }
        else if(partReturnMap.containsKey(PartReturnStatus.WPRA_GENERATED)){
            return PartReturnStatus.WPRA_GENERATED;
        }
        else if(partReturnMap.containsKey(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER)){
            return PartReturnStatus.PART_RECEIVED_BY_SUPPLIER;
        }
        else if(partReturnMap.containsKey(PartReturnStatus.PART_NOT_RECEIVED_BY_SUPPLIER)){
            return PartReturnStatus.PART_NOT_RECEIVED_BY_SUPPLIER;
        }
        else if(partReturnMap.containsKey(PartReturnStatus.PARTS_COLLECTED_BY_DEALER)){
            return PartReturnStatus.PARTS_COLLECTED_BY_DEALER;
        }
        else if(partReturnMap.containsKey(PartReturnStatus.PARTS_NOT_COLLECTED_BY_DEALER)){
            return PartReturnStatus.PARTS_NOT_COLLECTED_BY_DEALER;
        }
        else if(partReturnMap.containsKey(PartReturnStatus.PART_MARKED_AS_SCRAPPED)){
            return PartReturnStatus.PART_MARKED_AS_SCRAPPED;
        }
        else if(partReturnMap.containsKey(PartReturnStatus.PART_MOVED_TO_OVERDUE)){
            return PartReturnStatus.PART_MOVED_TO_OVERDUE;
        }
        else if(partReturnMap.containsKey(PartReturnStatus.PART_NOT_RECIEVED)){
             return PartReturnStatus.PART_REJECTED;
         }
         else if(partReturnMap.containsKey(PartReturnStatus.PART_AUTO_CONFIRMED)){
             return PartReturnStatus.PART_AUTO_CONFIRMED;
         }


        return null;
    }
    
    public void updatePartStatus(OEMPartReplaced partReplaced){
    	PartReturnStatus partStatus = findPartReturnStatusOfReplacedOEMPart(partReplaced);
    	partReplaced.setStatus(partStatus);
   }

    public boolean isPartShipped(OEMPartReplaced partReplaced) {
        boolean isShipped = true;
        if (partReplaced.getPartReturns().isEmpty()) {
            isShipped = false;
        }
        for (PartReturn partReturn : partReplaced.getPartReturns()) {
            if (!partReturn.getStatus().isPartShipped()) {
                isShipped = false;
            }
        }
        return isShipped;
    }

    public PageResult<PartReturnClaimSummary> findAllPartReturnsMatchingQuery(
            Long domainPredicateId, ListCriteria listCriteria) {
        DomainPredicate predicate = this.predicateAdministrationService.findById(domainPredicateId);
        HibernateQueryGenerator generator = new PartReturnQueryGenerator(beanFactory);
        generator.visit(predicate);
        HibernateQuery query = generator.getHibernateQuery();
        String sortString = listCriteria.getSortCriteriaString();
        String queryWithoutSelect = query.getQueryWithoutSelect();
        if (listCriteria.isFilterCriteriaSpecified()) {
            String filter = listCriteria.getParamterizedFilterCriteria();
            queryWithoutSelect = queryWithoutSelect + " and (" + filter + " )";
        }
        return this.partReturnRepository.findPartReturnsUsingDynamicQuery(queryWithoutSelect,
                sortString, query.getSelectClause(), listCriteria.getPageSpecification(), query
                        .getParameters(), listCriteria.getTypedParameterMap());
    }

    public boolean isUnique(PartReturnDefinition partReturnDefinition) {
        return this.partReturnDefinitionRepository.isUnique(partReturnDefinition);
    }

    public void save(PartReturnDefinition partReturnDefinition) {
        this.partReturnDefinitionRepository.save(partReturnDefinition);
    }

    public void delete(PartReturnDefinition partReturnDefinition) {
        this.partReturnDefinitionRepository.delete(partReturnDefinition);
    }

    public void update(PartReturnDefinition partReturnDefinition) {
        this.partReturnDefinitionRepository.update(partReturnDefinition);
    }

    public List<PaymentCondition> findAllPaymentConditions() {
        return this.partReturnDefinitionRepository.findAllPaymentConditions();
    }

    public PaymentCondition findPaymentCondition(String code) {
        return this.partReturnDefinitionRepository.findPaymentConditionForCode(code);
    }

    public PartReturnDefinition findPartReturnDefinitionById(Long id) {
        return this.partReturnDefinitionRepository.findById(id);
    }

    public PartReturn findPartReturn(Long id) {
        return this.partReturnRepository.findById(id);
    }

    public void setCampaignAssignmentService(CampaignAssignmentService campaignAssignmentService) {
        this.campaignAssignmentService = campaignAssignmentService;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Required
    public void setPredicateAdministrationService(
            PredicateAdministrationService predicateAdministrationService) {
        this.predicateAdministrationService = predicateAdministrationService;
    }

    @Required
    public void setPartReturnRepository(PartReturnRepository partReturnRepository) {
        this.partReturnRepository = partReturnRepository;
    }

    public void setPartReturnDefinitionRepository(
            PartReturnDefinitionRepository partReturnDefinitionRepository) {
        this.partReturnDefinitionRepository = partReturnDefinitionRepository;
    }

    public void setLovRepository(LovRepository lovRepository) {
        this.lovRepository = lovRepository;
    }

    public void setPaymentConditionEvaluators(
            Map<String, PaymentConditionEvaluator> paymentConditionEvaluators) {
        this.paymentConditionEvaluators = paymentConditionEvaluators;
    }

    public boolean canMakePaymentDecision(Claim claim) {
        Collection<OEMPartReplaced> parts = claim.getServiceInformation().getServiceDetail()
                .getOEMPartsReplaced();
        boolean canMakePmtDecision = true;
        canMakePmtDecision = canMakePmtDecision(canMakePmtDecision, parts,claim.getBusinessUnitInfo());

        Collection<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled = claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled();
		for (HussmanPartsReplacedInstalled installed : hussmanPartsReplacedInstalled) {
			if(installed != null && installed.getReplacedParts() != null){
				List<OEMPartReplaced> replacedParts = installed.getReplacedParts();
				canMakePmtDecision = canMakePmtDecision(canMakePmtDecision, replacedParts,claim.getBusinessUnitInfo());
			}
		}
		return canMakePmtDecision;

    }

	private boolean canMakePmtDecision(boolean canMakePmtDecision, Collection<OEMPartReplaced> replacedParts,BusinessUnitInfo bu) {
		for (OEMPartReplaced part : replacedParts) {
			if (part.getPartReturns() == null || part.getPartReturns().size() == 0) {
		        continue;
		    }
		    checkPaymentConditionConsistency(part);
		    for (PartReturn partReturn : part.getPartReturns()) {
		        if (canProceedWithoutCheck(partReturn)) {
		            continue;
		        }
		        PaymentCondition paymentCondition = partReturn.getPaymentCondition();
		        if (this.paymentConditionEvaluators == null) {
		            initializePaymentConditionEvaluators();
		        }
		        PaymentConditionEvaluator paymentConditionEvaluator = this.paymentConditionEvaluators
		                .get(paymentCondition.getCode());
		        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(bu.getName());
		        canMakePmtDecision = canMakePmtDecision
		                && paymentConditionEvaluator.canMakePaymentDecision(partReturn);

		    }
		}
		return canMakePmtDecision;
	}

    private boolean canProceedWithoutCheck(PartReturn partReturn) {
        return partReturn == null
                || partReturn.getReturnLocation() == null
                || (partReturn.getTriggerStatus().ordinal() >= PartReturnTaskTriggerStatus.TO_BE_ENDED
                        .ordinal());
    }

    private void initializePaymentConditionEvaluators() {
        this.paymentConditionEvaluators = new HashMap<String, PaymentConditionEvaluator>();
        this.paymentConditionEvaluators.put("PAY", new PayWithNoReturnEvaluator());
        this.paymentConditionEvaluators.put("PAY_ON_RETURN", new PayOnlyWithReturnEvaluator());
        this.paymentConditionEvaluators.put("PAY_ON_INSPECTION", new PayOnlyWithInspectionEvaluator());
    }

    public void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public boolean shouldClaimMoveToRejectedParts(Claim claim) {
		ClaimState state = claim.getState();
		if (state.ordinal() > ClaimState.PROCESSOR_REVIEW.ordinal()
				&& state.ordinal() < ClaimState.PENDING_PAYMENT_RESPONSE
						.ordinal()
				&& !(state.equals(ClaimState.REJECTED_PART_RETURN))) {
			return doesClaimHaveRejectedParts(claim);
		}
		return false;
	}

    public boolean doesClaimHaveRejectedParts(Claim claim) {
		if (claim.getState().equals(ClaimState.REJECTED_PART_RETURN) || claim.getState().equals(ClaimState.REACCEPTED)) {
			return false;
		}
		List<OEMPartReplaced> oemPartsReplaced = claim.getServiceInformation()
				.getServiceDetail().getReplacedParts();

		if (oemPartsReplaced != null && oemPartsReplaced.size() > 0) {
			for (OEMPartReplaced partReplaced : oemPartsReplaced) {
				List<PartReturn> partReturns = partReplaced.getPartReturns();
				if (partReturns != null && partReturns.size() > 0) {
					for (PartReturn partReturn : partReturns) {
						if (PartReturnStatus.PART_REJECTED.getStatus().equalsIgnoreCase(
								partReturn.getStatus().getStatus())) {
                            return true;
                        }
					}
				}
			}
		}
		return false;
	}
    public PageResult<PartReturnClaimSummary> findAllClaimsMatchingCriteria(
    		final PartReturnSearchCriteria partReturnSearchCriteria){

    	return this.partReturnRepository.findAllClaimsMatchingCriteria(partReturnSearchCriteria);
    }

	public List<Claim> getAllDraftClaims()
	{
		return this.partReturnRepository.getAllDraftClaims();
	}

	public List<Claim> forwardedClaimCrossedOverDueDays(int forwardedOverdueDate) {
		// TODO Auto-generated method stub
		return this.partReturnRepository.forwardedClaimCrossedOverDueDays(forwardedOverdueDate);
	}

    private boolean areAllPartsNotReceived(PartReturnConfiguration partReturnConfiguration){
        return partReturnConfiguration.getMaxQuantity()==null ||
                (partReturnConfiguration.getMaxQuantity()!=null
                        && partReturnConfiguration.getQuantityReceived().longValue()<partReturnConfiguration.getMaxQuantity().longValue());
    }

	public List<String> findAllStatusForPartReturn() {
		return this.partReturnRepository.findAllStatusForPartReturn();
	}

	public List<String> findAllLocationsForPartReturn() {
		return this.partReturnRepository.findAllLocationsForPartReturn();
	}

    public List<Claim> fetchShipmentGeneratedClaimsCrossedWindowPeriodDays(int windowPeriodDays) {
		return this.partReturnRepository.fetchShipmentGeneratedClaimsCrossedWindowPeriodDays(windowPeriodDays);
	}

    public void updatePartReturnConfiguration(PartReturnConfiguration partReturnConfiguration){
        partReturnRepository.updatePartReturnConfiguration(partReturnConfiguration);
    }


	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}


    public BeanFactory getBeanFactory() {
		return beanFactory;
	}

   @Required
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public Set<OEMPartReplaced> getAllReplacedParts(Claim claim)
	{
		Set<OEMPartReplaced> replacedParts = new HashSet<OEMPartReplaced>();
		replacedParts.addAll(claim.getServiceInformation().getServiceDetail().getReplacedParts());
		for (HussmanPartsReplacedInstalled replacedInstalledPart : claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled()) {
			replacedParts.addAll(replacedInstalledPart.getReplacedParts());
		}
		return replacedParts;
	}

	public List<OEMPartReplaced> fetchRemovedParts(Claim claim, List<OEMPartReplaced> partsReplaced) {
        List<OEMPartReplaced> removedParts = new ArrayList<OEMPartReplaced>();
        if (partsReplaced != null) {
            for (OEMPartReplaced partReplaced : new HashSet<OEMPartReplaced>(partsReplaced)) {
                boolean partRemoved = true;
                for (OEMPartReplaced replacedPart : getAllReplacedParts(claim)) {
                    if (partReplaced.getId() != null && replacedPart.getId()!=null &&
                            partReplaced.getId().longValue() == replacedPart.getId().longValue()&& partReplaced.isPartToBeReturned()) {
                        partRemoved = false;
                        break;
                    }
                }
                if (partReplaced.getId() != null && partRemoved) {
                    partReplaced.setPartToBeReturned(false);
                    removedParts.add(partReplaced);
                }
            }
        }
        return removedParts;
    }
    
	public Boolean canClaimBeAccepted(Claim claim) {
		boolean canClaimBeAccepted = true;
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		
		
		
		return canClaimBeAccepted;
	}
	
	public void update(PartReturn partReturn){
		this.partReturnRepository.update(partReturn);
	}


	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public void createPartReturnDefinitionAudit(
			PartReturnDefinition partReturnDefinition) {
    	PartReturnDefinitionAudit audit = new PartReturnDefinitionAudit();
    	audit.setComments(partReturnDefinition.getComments());
    	audit.setForDefinition(partReturnDefinition);
    	audit.setStatus(partReturnDefinition.getStatus());
    	partReturnDefinition.getPartReturnDefinitionAudits().add(audit);
	}

    /*public void acceptSupplierPartAfterInspection(List<SupplierPartReturn> supplierPartReturns,
                                          String accepatanceCode) {
        if ((supplierPartReturns == null) || (supplierPartReturns.size() == 0)) {
            throw new IllegalArgumentException("There are no parts selected for inspection.");
        }
        SupplierPartAcceptanceReason acceptanceReason = (SupplierPartAcceptanceReason) this.lovRepository.findByCode(
                SupplierPartAcceptanceReason.class.getSimpleName(), accepatanceCode);
        for (SupplierPartReturn supplierPartReturn : supplierPartReturns) {
            supplierPartReturn.setSupplierPartAcceptanceReason(acceptanceReason);
        }
    }

    public void rejectSupplierPartAfterInspection(List<SupplierPartReturn> supplierPartReturns, String failureCode
                                          ) {
        if ((supplierPartReturns == null) || (supplierPartReturns.size() == 0)) {
            throw new IllegalArgumentException("There are no parts selected for inspection.");
        }

        SupplierPartRejectionReason failureReason = (SupplierPartRejectionReason) this.lovRepository.findByCode(SupplierPartRejectionReason.class
                .getSimpleName(), failureCode);
        for (SupplierPartReturn supplierPartReturn : supplierPartReturns) {
            supplierPartReturn.setSupplierPartRejectionReason(failureReason);
        }
    }*/
	
}

