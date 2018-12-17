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

package tavant.twms.web.partsreturn;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.catalog.ItemUOMTypes;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAttributes;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.common.AcceptanceReason;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.TaskCriteria;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigOptionConstants;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.uom.UomMappings;
import tavant.twms.domain.uom.UomMappingsService;
import tavant.twms.domain.common.Document;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.partreturn.PartReturnWorkList;

import com.opensymphony.xwork2.Preparable;

public class DuePartsReceiptAction extends PartReturnInboxAction implements
		Preparable, ConfigOptionConstants {
	
	  private static final Logger logger = Logger
      .getLogger(DuePartsReceiptAction.class);

	public static final String MARK_NOT_TO_BE_RETURNED = "Mark not to be returned";

	public static final String TRANSITION = "transition";

	private String comments;

	private TaskViewService taskViewService;

	private WarehouseService warehouseService;

	public LovRepository lovRepository;

	public String failureReasonArray;

	public String acceptanceReasonArray;

	public Shipment shipment;

	private String claimID;
	


	private List<Document> attachments = new ArrayList<Document>();

	private PartReturnDefinitionRepository partReturnDefinitionRepository;

	private String newComments;

	private UomMappingsService uomMappingsService;
	
	private PartReturnProcessingService partReturnProcessingService;
	
	private Item item;

    List<Shipment> shipments = new ArrayList<Shipment>();

    private void endTasksFromWpraGeneratedInboxForReceivedParts() {
        List<Long> partReturnsIds = new ArrayList<Long>();
        List<PartTaskBean> selectedBeans = getPartTaskBeanForReceived(getSelectedPartTaskBeans());
        //add parts not received also
        selectedBeans.addAll(getPartsTasksForNotReceived(getSelectedPartTaskBeans()));
        for(PartTaskBean taskBean : selectedBeans)
        {
             if(taskBean.getPartReturn() != null)
                 partReturnsIds.add(taskBean.getPartReturn().getId());

        }
        if(partReturnsIds.size() > 0)
            partReturnProcessingService.endWPRATasksForParts(partReturnsIds, WorkflowConstants.GENERATED_WPRA, "endWpra");
    }
    
    private void setReceiptDateOfShipment(){
    	for (OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()) {
    		if(partReplacedBean.getReceive() > 0){
    			shipment.setReceiptDate(new Timestamp(new Date().getTime()));
    			break;
    		}
    	}
    }
    
	public String submitTasks() {
		List<PartTaskBean> partTaskBeans = getSelectedPartTaskBeans();		
		List<PartReturn> parts = getPartReturnsFromPartTaskBeans(partTaskBeans);		
		Map<Claim, List<PartTaskBean>> distinctClmMap = new HashMap<Claim, List<PartTaskBean>>();
		List<PartTaskBean> arrayForClaim = null;
		for (PartTaskBean partTaskBean : partTaskBeans) {
			if (partTaskBean.isIncorrectPartReturned()) {
				if (distinctClmMap.get(partTaskBean.getClaim()) == null) {
					arrayForClaim = new ArrayList<PartTaskBean>();
					arrayForClaim.add(partTaskBean);
					distinctClmMap.put(partTaskBean.getClaim(), arrayForClaim);
				} else {
					List<PartTaskBean> arrayForClaims = distinctClmMap.get(partTaskBean.getClaim());
					arrayForClaims.add(partTaskBean);
					distinctClmMap.put(partTaskBean.getClaim(), arrayForClaims);
				}
			}
			if (attachments != null && isNotShipmentView()) {
				Claim claim = claimService.findClaim(partTaskBean.getClaim().getId());
				claim.getAttachments().clear();
				claim.getAttachments().addAll(attachments);
				claimService.updateClaim(claim);
			}
		}
		
		shipment = parts.get(0).getShipment();
		setReceiptDateOfShipment();
		//		shipment.setComments(comments);
		taskViewService.submitAllTaskInstances(getTasksForReceived(partTaskBeans), transitionTaken);
		taskViewService.submitAllTaskInstances(getTasksForNotReceived(partTaskBeans), transitionTaken);

        //NMHGSLSM-240 Need to end the wpra generated for parts inbox
        try{
            endTasksFromWpraGeneratedInboxForReceivedParts();
        }catch(Exception e){
            //we should stop the flow to execute if the wpra generated task is not ended. Since that will be taken care by the scheduler
            logger.error("Could not end wpra generated task");
        }


		Map<OEMPartReplaced, List<PartTaskBean>> groupedByParts = groupByParts(getSelectedPartTaskBeans());
		//need to modify this.Modify to add the new beans into partReplacedBeans.
		for (OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()) { 
			if (partReplacedBean.isSelected()) {
				OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                /*if(getConfigParamService().getBooleanValue(ConfigName.REJECT_PART_ON_NOT_RECEIVED.getName()) && partReplacedBean.getReceive()==0){
				    part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_NOT_RECIEVED.getStatus(),
						partReplacedBean.getDidNotReceive()));
                }else{*/
                if(getConfigParamService().getBooleanValue(ConfigName.REJECT_PART_ON_NOT_RECEIVED.getName()) && partReplacedBean.getReceive() > 0)  {
				part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_RECEIVED.getStatus(),
						partReplacedBean.getReceive()));
                }else{
                    part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_RECEIVED.getStatus(),
                            partReplacedBean.getReceive()));
                }
                if(getConfigParamService().getBooleanValue(ConfigName.REJECT_PART_ON_NOT_RECEIVED.getName())){
                    if(partReplacedBean.getReceive()==0)
                    { part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_NOT_RECIEVED.getStatus(),
						partReplacedBean.getDidNotReceive()));
                    }else{
                        part.setPartAction2(new PartReturnAction(PartReturnStatus.PART_NOT_RECIEVED.getStatus(),
                                partReplacedBean.getDidNotReceive()));
                    }

                }else{
				part.setPartAction2(new PartReturnAction(PartReturnStatus.PART_TO_BE_SHIPPED.getStatus(),
						partReplacedBean.getDidNotReceive()));
                }
				part.setComments(comments);
				updatePartStatus(part);
				if (partReplacedBean.isToBeInspected()) {
					part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_ACCEPTED.getStatus(),
							partReplacedBean.getAccepted()));
					part.setPartAction2(new PartReturnAction(PartReturnStatus.PART_REJECTED.getStatus(),
							partReplacedBean.getRejected()));
                    part.setPartAction3(new PartReturnAction(PartReturnStatus.PART_NOT_RECIEVED.getStatus(),
                            partReplacedBean.getDidNotReceive()));
					part.setComments(comments);
					if(partReplacedBean.getAcceptanceCauses().get(0)!=null && !partReplacedBean.getAcceptanceCauses().get(0).isEmpty() && !partReplacedBean.getAcceptanceCauses().get(0).equals("null"))
					{
						ListOfValues acceptReason=lovRepository.findByCode(PartAcceptanceReason.class.getSimpleName(), partReplacedBean.getAcceptanceCauses().get(0));
						part.setAcceptanceCause(acceptReason.getDescription());
					}
					if(partReplacedBean.getFailureCauses().get(0)!=null && !partReplacedBean.getFailureCauses().get(0).isEmpty()&& !partReplacedBean.getFailureCauses().get(0).equals("null") )
					{
						ListOfValues rejectReason=lovRepository.findByCode(FailureReason.class.getSimpleName(), partReplacedBean.getFailureCauses().get(0));
						part.setFailureCause(rejectReason.getDescription());
					}
					updatePartStatus(part);
					
					if(partReplacedBean.isToBeScrapped()){
						part.setPartScrapped(true);
						part.setScrapDate(new Date());
						part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_MARKED_AS_SCRAPPED.getStatus()
				                ,part.getPartsReceived()));
						part.setPartAction2(null);
						part.setPartAction3(null);
				        updatePartStatus(part);
					}
				}
				getPartReplacedService().updateOEMPartReplaced(part);
			}
		}

		// API to set the event upon receipt of parts
		// createEvent(parts);
		// end of the creation of even API

		// API to set the event upon part rejection incase receiver has
		// inspector permissions.
		createEventsForRejectedPartsOnClaim(partTaskBeans);
		// end of the creation of events API

		// This API updates the count of the part received if the part is in
		// PRC
		updatePartReceivedCount(getPartReplacedBeans());
		generateView();
		addActionMessage("message.itemStatus.updated");
		return SUCCESS;
	}
	
	private boolean hasDuplicateSerializedParts(List<PartTaskBean> partTaskBeans) {
		HashSet<InventoryItem> set = new HashSet<InventoryItem>();
		for (PartTaskBean partTaskBean : partTaskBeans) {
			if (partTaskBean.getPartOffSerialNumber() != null) {
				if (!set.add(partTaskBean.getPartOffSerialNumber()))
					return true;			}
		}
		return false;
	}

	
	private boolean isToBeInspected(List<PartTaskBean> partTaskBeans){
		boolean toBeInspected = false;
		for(PartTaskBean partTaskBean : partTaskBeans){
			toBeInspected = toBeInspected ||  partTaskBean.isToBeInspected();	
		}
		 
		return toBeInspected;
	}
	

	
	//In case part off is implemented we cannot extract parts to be updated from getPartReplacedBeans() as parts may be modified by receiver
	private Map<OEMPartReplaced, List<PartTaskBean>> groupByParts(List<PartTaskBean> selectedPartTaskBeans) {
		Map<OEMPartReplaced, List<PartTaskBean>> distinctOEMPartMap = new HashMap<OEMPartReplaced, List<PartTaskBean>>();
		for (PartTaskBean partTaskBean : selectedPartTaskBeans) {
			if (partTaskBean.isSelected()) {
				/*This flag is set if the receiver replaces the part with a part that is already present as a replaced part in a claim.
				In this case the new part needs to be updated with OEM Part(partTaskBean.getPart() will give the old part)*/
				if(partTaskBean.getNewOEMPart()==null){
					groupTaskBeansByPart(distinctOEMPartMap,partTaskBean.getPart(),partTaskBean);
				}else{
					groupTaskBeansByPart(distinctOEMPartMap,partTaskBean.getNewOEMPart(),partTaskBean);
				
			}
				}
		}
		return distinctOEMPartMap;
	}
	
	private Map<OEMPartReplaced, List<PartTaskBean>> groupTaskBeansByPart(Map<OEMPartReplaced, List<PartTaskBean>> distinctOEMPartMap,OEMPartReplaced part,PartTaskBean partTaskBean){
		if (distinctOEMPartMap.get(part) == null) {
			List<PartTaskBean>	parTaskBeansWithSamePart = new ArrayList<PartTaskBean>();
			parTaskBeansWithSamePart.add(partTaskBean);
			distinctOEMPartMap.put(part, parTaskBeansWithSamePart);
		} else {
			List<PartTaskBean>	parTaskBeansWithSamePart = distinctOEMPartMap.get(part);
			parTaskBeansWithSamePart.add(partTaskBean);
			distinctOEMPartMap.put(part, parTaskBeansWithSamePart);
		}
		return distinctOEMPartMap;
	}

	


	
	
	
	
	@SuppressWarnings("unchecked")
	private void splitTKPartReplaced(Claim claim) {
		ListIterator it = claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled()
				.listIterator();
		
		List<HussmanPartsReplacedInstalled> newToAdd= new ArrayList<HussmanPartsReplacedInstalled>();
		while (it.hasNext()) {
			boolean serializedExists = false;
			HussmanPartsReplacedInstalled hpri = (HussmanPartsReplacedInstalled) it.next();
			List<OEMPartReplaced> serializedReplacedParts = new ArrayList<OEMPartReplaced>();
			if (hpri.getReplacedParts() != null && hpri.getReplacedParts().size() > 0) {
				for(OEMPartReplaced oemPart : hpri.getReplacedParts()){
						if (oemPart.getItemReference() != null
								&& oemPart.getItemReference().getReferredInventoryItem() != null) {
							serializedExists = serializedExists || true;
							serializedReplacedParts.add(oemPart);
						}

					}
					
				}
			
				if (serializedExists && hpri.getReplacedParts().size() > 1) {
					for(OEMPartReplaced oemPart : serializedReplacedParts){
						newToAdd.add(createNewHussReplacedPart(oemPart,hpri));//create list of hpart
				}
				hpri.getReplacedParts().removeAll(serializedReplacedParts);
				if(CollectionUtils.isEmpty(hpri.getReplacedParts())){
					it.remove();
				}
				}
		}
		
		claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled().addAll(newToAdd);
		claimService.updateClaim(claim);
	}
	
	
	private HussmanPartsReplacedInstalled createNewHussReplacedPart(OEMPartReplaced replaced,HussmanPartsReplacedInstalled hpr){
		HussmanPartsReplacedInstalled hpri = new HussmanPartsReplacedInstalled();
		List<OEMPartReplaced> replacedParts = new ArrayList<OEMPartReplaced>();
		replacedParts.add(replaced);
		List<InstalledParts> newInstalled =new ArrayList<InstalledParts> ();
		List<InstalledParts> newNonBUInstalled =new ArrayList<InstalledParts> ();
		for(InstalledParts installed:hpr.getHussmanInstalledParts()){
			newInstalled.add(installed.clone());
		}
		for(InstalledParts installed:hpr.getNonHussmanInstalledParts()){
			newNonBUInstalled.add(installed.clone());
		}
		hpri.setHussmanInstalledParts(newInstalled);
		hpri.setNonHussmanInstalledParts(newNonBUInstalled);
		hpri.setReplacedParts(replacedParts);
		return hpri;
	}
	
	 private List<OEMPartReplaced> correctOEMParts(Claim claim, List<PartTaskBean> partTaskBeans,List<OEMPartReplaced> currentListOfOEMParts){
		//to avoid concurrent modification exception.
		List<OEMPartReplaced> modifiedListOfOEMParts = new ArrayList<OEMPartReplaced>(currentListOfOEMParts);
		for (OEMPartReplaced partReplaced : currentListOfOEMParts) {
			Map<Item, List<PartTaskBean>> tempMap = new HashMap<Item, List<PartTaskBean>>();
			for (PartTaskBean partTaskBean : partTaskBeans) {
				if (partReplaced.equals(partTaskBean.getPart())) {
					//If serial number is entered quantity should always be one.
					if (partTaskBean.getPartOffSerialNumber() != null) {
						if(partTaskBean.getPartOffPartNumber().equals
								(partTaskBean.getPart().getItemReference().getUnserializedItem())){
							partReplaced.getItemReference().
							setReferredInventoryItem(partTaskBean.getPartOffSerialNumber());
						}else {
							OEMPartReplaced newOEmPartReplaced = createNewSerializedOEMPartReplaced(partTaskBean);
							modifiedListOfOEMParts.add(newOEmPartReplaced);
							partReplaced.setNumberOfUnits(partReplaced.getNumberOfUnits() - 1);
							partReplaced.getPartReturns().remove(partTaskBean.getPartReturn());
						}
						
					} else {
						//Group all the parts and add it to the map.
						if (tempMap.containsKey(partTaskBean.getPartOffPartNumber())) {
							List<PartTaskBean> partReturnTasks = tempMap.get(partTaskBean.getPartOffPartNumber());
							partReturnTasks.add(partTaskBean);
							tempMap.put(partTaskBean.getPartOffPartNumber(), partReturnTasks);
						} else {
							List<PartTaskBean> partReturnTasks = new ArrayList<PartTaskBean>();
							partReturnTasks.add(partTaskBean);
							tempMap.put(partTaskBean.getPartOffPartNumber(), partReturnTasks);
						}
						partReplaced.setNumberOfUnits(partReplaced.getNumberOfUnits() - 1);
						partReplaced.getPartReturns().remove(partTaskBean.getPartReturn());
					}
				}
			}//In case the part already exists in the Claim we do not need to create a new oempartreplaced
			for (Item item : tempMap.keySet()) {
				boolean alreadyExist = false;
				for (OEMPartReplaced part : modifiedListOfOEMParts) {
					if (part.getItemReference().getUnserializedItem().equals(item)) {
						alreadyExist = true;
						for (PartTaskBean partBean : tempMap.get(item)) {
							part.setNumberOfUnits(part.getNumberOfUnits() + 1);
							PartReturn partReturn = partBean.getPartReturn();
							partReturn.setOemPartReplaced(part);
							part.getPartReturns().add(partReturn);
							partBean.setNewOEMPart(part);
						}
					}

				}
				if (!alreadyExist) {
					//Create new Parts from the list stored in the map
					OEMPartReplaced newOEmPartReplaced = createNewOEMParts(item, tempMap.get(item), partReplaced);
					modifiedListOfOEMParts.add(newOEmPartReplaced);
				}
			}
		}
		for (OEMPartReplaced oempartReplaced : currentListOfOEMParts) {//as the reference to modifiedListOfOEMParts is same this works fine
			if (oempartReplaced.getNumberOfUnits() == 0) {
				oempartReplaced.getD().setActive(false);
				modifiedListOfOEMParts.remove(oempartReplaced);
			}
		}
		return modifiedListOfOEMParts;
	}
	
	private OEMPartReplaced createNewOEMParts(Item item,List<PartTaskBean> partTaskBeans,OEMPartReplaced oldPart){
		List<PartReturn> listOfPartReturns = new ArrayList<PartReturn>();
		for(PartTaskBean partTaskBean : partTaskBeans){
			listOfPartReturns.add(partTaskBean.getPartReturn());
		}
		OEMPartReplaced newPart = new OEMPartReplaced();
		newPart.setItemReference(new ItemReference(item));
		newPart.setPartReturns(listOfPartReturns);
        newPart.getClaimAttributes().clear();
		newPart.setClaimAttributes(new ArrayList<ClaimAttributes>(oldPart.getClaimAttributes()));//to avoid exception due to shared reference to collection
		newPart.setNumberOfUnits(listOfPartReturns.size());
		newPart.setPartToBeReturned(true);
		if (super.getConfigParamService()
				.getBooleanValue(ConfigName.IS_UOM_ENABLED.getName())) {
			setUomMappingForPart(newPart,partTaskBeans.get(0).getClaim().getBusinessUnitInfo().getName());
	}
		return newPart;
	}	
	
	private OEMPartReplaced createNewSerializedOEMPartReplaced(PartTaskBean partReturnTask)	{
		
		OEMPartReplaced oldPart = partReturnTask.getPart();
		
		List<PartReturn> listOfPartReturns = new ArrayList<PartReturn>();
		listOfPartReturns.add(partReturnTask.getPartReturn());
		OEMPartReplaced newPart = new OEMPartReplaced();
		newPart.setItemReference(new ItemReference());
		newPart.getItemReference().setReferredInventoryItem(partReturnTask.getPartOffSerialNumber());
        newPart.getClaimAttributes().clear();
		newPart.setClaimAttributes(new ArrayList<ClaimAttributes>(oldPart.getClaimAttributes()));
		newPart.setNumberOfUnits(1);
		newPart.setPartReturns(listOfPartReturns);
		newPart.setPartToBeReturned(true);
		newPart.setStatus(oldPart.getStatus());//this is not set yet
		if (super.getConfigParamService()
				.getBooleanValue(ConfigName.IS_UOM_ENABLED.getName())) {
			setUomMappingForPart(newPart,partReturnTask.getClaim().getBusinessUnitInfo().getName());
	}
		return newPart;
	}
	
	private void setUomMappingForPart(OEMPartReplaced oEMPartReplaced, String buName) {
		String baseUomString = null;
		try {
			baseUomString = StringUtils.trimAllWhitespace(oEMPartReplaced.getItemReference().getUnserializedItem()
					.getUom().getType());
		} catch (Exception e) {
			logger.debug("Unexpected error while setting baseUOMString occurred for : "
					+ oEMPartReplaced.getItemReference().getUnserializedItem().getNumber());
		}
		if (StringUtils.hasText(baseUomString) && !ItemUOMTypes.EACH.getType().equalsIgnoreCase(baseUomString)) {
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(buName);
			UomMappings uom = uomMappingsService.findUomMappingForBaseUom(oEMPartReplaced.getItemReference()
					.getUnserializedItem().getUom().getName());
			oEMPartReplaced.setUomMapping(uom);

		}
	}


	// TODO: Check if the validations can be moved to xml.
	@Override
	public void validate() {
		super.validate();
		List<PartTaskBean> partTasks = getSelectedPartTaskBeans(false);
		if (hasDuplicateSerializedParts(partTasks)) {		
			addActionError("foc.widget.returnedPartRepeated");		
		}
		if (!StringUtils.hasText(getComments())) {
			addActionError("error.manageFleetCoverage.commentsMandatory");
		}
		if (!partTasks.isEmpty() && partTasks.get(0).getPartReturn().getOemPartReplaced().isPartReturnInitiatedBySupplier()) {
			for (PartTaskBean partTaskBean : partTasks) {
				if (partTaskBean.getPart().getAppliedContract() == null) {
					addErrorMessageForPartReturn("error.partReturnTaskBean.applicableContractNotSelected", 
							partTaskBean);
				}
			}
		}
		for (OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()) {
			if (partReplacedBean.isSelected()) {
				if (partReplacedBean.getReceive() <= 0
						&& partReplacedBean.getDidNotReceive() <= 0) {
					addActionError("error.partReturnConfiguration.noPartsReceipt");
					break;
				} else if (partReplacedBean.getReceive()
						+ partReplacedBean.getDidNotReceive() > partReplacedBean
						.getQtyForShipment()) {
					addActionError("error.partReturnConfiguration.excessPartsReceipt");
					break;
				} else if (partReplacedBean.getDidNotReceive() > 0
						&& !StringUtils.hasText(getNewComments())) {
					addActionError("error.manageFleetCoverage.commentsMandatory");
					break;
				}
			}
		}
		validateForPartRecieving(partTasks);
		validateForPartInspection(partTasks);
		if (hasErrors()) {
			generateView();
			setUserSpecifiedQuantity();
		}

	}

    
	protected void setUserSpecifiedQuantity() {
		for (ClaimWithPartBeans claimWithPartBeans : getClaimWithPartBeans()) {
			for (OEMPartReplacedBean partReplacedBean : claimWithPartBeans.getPartReplacedBeans()) {
				for (OEMPartReplacedBean uiPartReplacedBean : this.getPartReplacedBeans()) {
					if (partReplacedBean.getPartReplacedId() == uiPartReplacedBean.getPartReplacedId()) {
						partReplacedBean.setSelected(uiPartReplacedBean.isSelected());
						partReplacedBean.setReceive(uiPartReplacedBean.getReceive());
						partReplacedBean.setDidNotReceive(uiPartReplacedBean.getDidNotReceive());
						partReplacedBean.setToBeInspected(uiPartReplacedBean.isToBeInspected());
						partReplacedBean.setToBeScrapped(uiPartReplacedBean.isToBeScrapped());
						partReplacedBean.setAccepted(uiPartReplacedBean.getAccepted());
						partReplacedBean.setRejected(uiPartReplacedBean.getRejected());
						partReplacedBean.setWarehouseLocation(uiPartReplacedBean.getWarehouseLocation());
						for (PartTaskBean uipartTaskBean : uiPartReplacedBean.getPartReturnTasks()) {
							for (PartTaskBean partTaskBean : partReplacedBean.getPartReturnTasks()) {
								if (partTaskBean.getTask() != null
										&& partTaskBean.getTask().equals(uipartTaskBean.getTask())) {
									partTaskBean.setWarehouseLocation(uipartTaskBean.getWarehouseLocation());
									partTaskBean.setToBeInspected(uipartTaskBean.isToBeInspected());
									/*
									 * The value is set after negation as in
									 * the jsp the value displayed is
									 * !selected while the page is loaded.
									 * The selected value is set to false(in
									 * PartTaskBean) and could not be
									 * changed in the base class owing to
									 * multiple dependencies for other
									 * BU's.The page when loaded needed all
									 * the part return tasks to be set as
									 * true by default . Thus the value is
									 * set as !selected below while
									 * reloading of page after validation.
									 */
									partTaskBean.setSelected(!uipartTaskBean.isSelected());
									partTaskBean.setReceiptStatus(uipartTaskBean.getReceiptStatus());
									if (partTaskBean.isToBeInspected()) {
										partTaskBean.setAcceptanceCause(uipartTaskBean.getAcceptanceCause());
										partTaskBean.setFailureCause(uipartTaskBean.getFailureCause());
										partTaskBean.setInspectionStatus(uipartTaskBean.getInspectionStatus());
									}
								}

							}
						}
					}
				}
			}
		}
	}

	protected void preparePreview(List<TaskInstance> partTasks) {
		super.preparePreview(partTasks);
		if (partTasks.size() > 0) {
			comments = new PartTaskBean(partTasks.get(0)).getPartReturn()
					.getShipment().getComments();
			shipment = new PartTaskBean(partTasks.get(0)).getPartReturn()
					.getShipment();
            for(TaskInstance instance : partTasks){
                if(!shipments.contains(new PartTaskBean(instance).getPartReturn().getShipment()))
                    shipments.add(new PartTaskBean(instance).getPartReturn().getShipment());
            }
		}
		if( getClaimWithPartBeans().size() > 0 && getClaimWithPartBeans().get(0).getClaim() != null)
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(
					getClaimWithPartBeans().get(0).getClaim().getBusinessUnitInfo().getName());

	}
	
	// TODO : Transitions are hardcoded for now.
	protected void processPartTaskBean(PartTaskBean partTaskBean) {

		partTaskBean.getPartReturn().setWarehouseLocation(
				partTaskBean.getWarehouseLocation());
        if (partTaskBean.getActionTaken() == ACCEPT){
            partTaskBean.getPartReturn().setActionTaken(PartReturnStatus.PART_ACCEPTED);
        }else if(partTaskBean.getActionTaken() == REJECT){
            partTaskBean.getPartReturn().setActionTaken(PartReturnStatus.PART_REJECTED);
        }else{
            partTaskBean.getPartReturn().setActionTaken(PartReturnStatus.getPartReturnStatus(
				partTaskBean.getActionTaken()));
        }
        if (MARK_FOR_INSPECTION.equals(partTaskBean.getActionTaken()))
			partTaskBean.getTask().setVariable(TRANSITION,
					"Send for Inspection");
		else if (MARK_NOT_RECEIVED.equals(partTaskBean.getActionTaken()))
        {
            //SLMSPROD-603 mark part rejected on not received
            if(getConfigParamService().getBooleanValue(ConfigName.REJECT_PART_ON_NOT_RECEIVED.getName())){
                partTaskBean.getTask().setVariable(TRANSITION, "Inspected");
                updatePartsAfterInspection(partTaskBean);
            }else{
			partTaskBean.getTask().setVariable(TRANSITION, "Part Not Received");
            }
        }

		else if (ACCEPT.equals(partTaskBean.getActionTaken())
				|| REJECT.equals(partTaskBean.getActionTaken())) {
			partTaskBean.getTask().setVariable(TRANSITION, "Inspected");
			updatePartsAfterInspection(partTaskBean);
		}
	}

	private void updatePartsAfterInspection(PartTaskBean partTaskBean) {
		List<PartReturn> partsAccepted = new ArrayList<PartReturn>();
		List<PartReturn> partsRejected = new ArrayList<PartReturn>();
		if (partTaskBean.getActionTaken() == ACCEPT)
			partsAccepted.add(partTaskBean.getPartReturn());
		else if (partTaskBean.getActionTaken() == REJECT || partTaskBean.getActionTaken() == MARK_NOT_RECEIVED ) {
			partsRejected.add(partTaskBean.getPartReturn());
		}
		if (StringUtils.startsWithIgnoreCase(partTaskBean.getActionTaken(),
				ACCEPT)) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(partTaskBean.getClaim().getBusinessUnitInfo().getName());
            getPartReturnService().acceptPartAfterInspection(partsAccepted,
					null, partTaskBean.getAcceptanceCause());
			processForOnHoldForPartReturnInboxFlow(partTaskBean);

		} else if (StringUtils.startsWithIgnoreCase(partTaskBean
				.getActionTaken(), REJECT) || StringUtils.startsWithIgnoreCase(partTaskBean.getActionTaken(), MARK_NOT_RECEIVED) ) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(partTaskBean.getClaim().getBusinessUnitInfo().getName());
            getPartReturnService().rejectPartAfterInspection(partsRejected,
					partTaskBean.getFailureCause(), null);
            //if payment condition is something else, don't move to rejected part return inbox
            if(null != partTaskBean.getPartReturn() && null != partTaskBean.getPartReturn().getPaymentCondition() && partTaskBean.getPartReturn().getPaymentCondition().getCode().equals("PAY_ON_INSPECTION")){
			    processForRejectedPartInboxFlow(partTaskBean);
            }
			processForOnHoldForPartReturnInboxFlow(partTaskBean);
		}
	}

	private boolean isPartInspected(PartTaskBean partTaskBean) {
		Claim claim = partTaskBean.getClaim();
		List<OEMPartReplaced> oEMPartReplaced = claim.getServiceInformation()
				.getServiceDetail().getReplacedParts();
		for (OEMPartReplaced partReplaced : oEMPartReplaced) {
			for (PartReturn partReturn : partReplaced.getPartReturns()) {
				if (partReturn.getStatus().ordinal() < PartReturnStatus.PART_ACCEPTED
						.ordinal()
						|| partReturn.getStatus().ordinal() < PartReturnStatus.PART_RECEIVED
								.ordinal()) {
					return false;
				}
			}
		}
		return true;

	}

    public List<PartTaskBean> getPartTaskBeanForReceived(
            List<PartTaskBean> partTaskBeans) {
        List<PartTaskBean> tasks = new ArrayList<PartTaskBean>();
        for (PartTaskBean partTaskBean : partTaskBeans) {
            if (MARK_FOR_INSPECTION.equals(partTaskBean.getActionTaken())
                    || ACCEPT.equals(partTaskBean.getActionTaken())
                    || REJECT.equals(partTaskBean.getActionTaken()))
                tasks.add(partTaskBean);
        }
        return tasks;
    }


    public List<TaskInstance> getTasksForReceived(
			List<PartTaskBean> partTaskBeans) {
		List<TaskInstance> tasks = new ArrayList<TaskInstance>();
		for (PartTaskBean partTaskBean : partTaskBeans) {
			if (MARK_FOR_INSPECTION.equals(partTaskBean.getActionTaken())
					|| ACCEPT.equals(partTaskBean.getActionTaken())
					|| REJECT.equals(partTaskBean.getActionTaken()))
				tasks.add(partTaskBean.getTask());
		}
		return tasks;
	}

	public List<TaskInstance> getTasksForNotReceived(
			List<PartTaskBean> partTaskBeans) {
		List<TaskInstance> tasks = new ArrayList<TaskInstance>();
		for (PartTaskBean partTaskBean : partTaskBeans) {
			if (MARK_NOT_RECEIVED.equals(partTaskBean.getActionTaken()))
				tasks.add(partTaskBean.getTask());
		}
		return tasks;
	}

    public List<PartTaskBean> getPartsTasksForNotReceived(
            List<PartTaskBean> partTaskBeans) {
        List<PartTaskBean> tasks = new ArrayList<PartTaskBean>();
        for (PartTaskBean partTaskBean : partTaskBeans) {
            if (getConfigParamService().getBooleanValue(ConfigName.REJECT_PART_ON_NOT_RECEIVED.getName()) && MARK_NOT_RECEIVED.equals(partTaskBean.getActionTaken()))
                tasks.add(partTaskBean);
        }
        return tasks;
    }

	public void prepare() throws Exception {
		super.prepare();
		List<ListOfValues> failureReasons = this.lovRepository
				.findAllActive("FailureReason");
		List<ListOfValues> acceptanceReasons = this.lovRepository
				.findAllActive("PartAcceptanceReason");
		failureReasonArray = generateComboboxJson(failureReasons, "code",
				"description");
		acceptanceReasonArray = generateComboboxJson(acceptanceReasons, "code",
				"description");

	}

    private void updatePartReceivedCount(List<OEMPartReplacedBean> oemPartReplacedBeans) {
        if (ON_PART_RECIEVED.equalsIgnoreCase(getConfigParamService().getStringValue(
                ConfigName.PART_RETURN_STATUS_TO_BE_CONSIDERED_FOR_PRC_MAX_QTY.getName()))) {
            for (OEMPartReplacedBean oemPartReplacedBean : oemPartReplacedBeans) {
                if (oemPartReplacedBean.isSelected()
                        && oemPartReplacedBean.getOemPartReplaced() != null
                        && oemPartReplacedBean.getOemPartReplaced().getItemReference() != null) {
                    PartReturnConfiguration partReturnConfiguration = oemPartReplacedBean
                            .getOemPartReplaced().getPartReturnConfiguration();
                    if (partReturnConfiguration != null
                            && oemPartReplacedBean.getOemPartReplaced().getPartReturns() != null
                            && !oemPartReplacedBean.getOemPartReplaced().getPartReturns().isEmpty()
                            && partReturnConfiguration.getMaxQuantity() != null) {
                    	
                        	partReturnConfiguration
							.setQuantityReceived(partReturnConfiguration
									.getQuantityReceived()
									+ oemPartReplacedBean.getReceive());
                        
                        getPartReturnService().updatePartReturnConfiguration(
                                partReturnConfiguration);
                    }
                }
            }
        }
    }

    /**
     * This is the list of part return from which we are going to create an event.  
     * 
     * @param partReturnList
     */
    private void createEvent(List<PartReturn> partReturnList)
    {
    	HashMap<String, Integer> partsShipped = new HashMap<String, Integer>();
    	PartReturn currentPartReturn = null;
    	String currentItemNumber;
    	Set<String> keySet=null;
    	StringBuffer finalPartNumberString; 
    	String finalPartNumberValue;
    	HashMap<String,Object> eventHashMap = new HashMap<String, Object>();
    	Long partReturnId = null;
    	if(partReturnList != null && partReturnList.size() > 0)
    	{
    		for(Iterator<PartReturn> partReturnIte = partReturnList.iterator(); partReturnIte.hasNext();)
    		{
    			currentPartReturn = partReturnIte.next();
    			if(currentPartReturn != null && currentPartReturn.getOemPartReplaced() != null && currentPartReturn.getOemPartReplaced().getItemReference() != null && currentPartReturn.getOemPartReplaced().getItemReference().getReferredItem() != null)
    			{
    				if(partReturnId == null)
    				{
    					partReturnId = currentPartReturn.getId();
    				}
    				currentItemNumber = currentPartReturn.getOemPartReplaced().getItemReference().getReferredItem().getNumber();
    				if(partsShipped.containsKey(currentItemNumber))
    				{
    					//since this part already exist we will merely update quantity
    					partsShipped.put(currentItemNumber, new Integer(partsShipped.get(currentItemNumber) + 1));
    				}
    				else
    				{
    					//since part number doesn't exist we will make an entry with quantity as one.
    					partsShipped.put(currentItemNumber, new Integer(1));
    				}
    			}
    		}
    		
    		//now that we are done with updating part numbers and quantities lets just create a string out of it
        	keySet = partsShipped.keySet();
        	finalPartNumberString = new StringBuffer();
        	for(Iterator<String> ite=keySet.iterator(); ite.hasNext();)
        	{
        		currentItemNumber = ite.next();
        		finalPartNumberString.append(currentItemNumber);
        		finalPartNumberString.append(" :: ");
        		finalPartNumberString.append(partsShipped.get(currentItemNumber));
        		finalPartNumberString.append(", ");
        	}
        	
        	//remove the last comma from the string buffer
        	finalPartNumberValue = finalPartNumberString.substring(0, (finalPartNumberString.length()-2));
        	
        	//set the values in hashMap
        	eventHashMap.put("claimId",claimID);
        	eventHashMap.put("partNumberString",finalPartNumberValue);
        	eventHashMap.put("taskInstanceId", partReturnId.toString());
        	Claim claim = claimService.findClaim(new Long(claimID));
        	eventHashMap.put("subject", "Claim - " + claim.getClaimNumber() + " needs attention");
        	//create the event
        	getEventService().createEvent("partReturn", EventState.DUE_PART_RETURN_RECEIPT, eventHashMap);    	
    	}   	
    }

    private void validateForPartRecieving(List<PartTaskBean> selectedPartTaskBeans){
        for (PartTaskBean bean : selectedPartTaskBeans) {
            if (MARK_FOR_INSPECTION.equals(bean.getActionTaken())
                    || MARK_NOT_RECEIVED.equals(bean.getActionTaken())) {
                String action = bean.getActionTaken();
                String itemNumber = bean.getPart().getItemReference()
                        .getUnserializedItem().getNumber();
                if (!StringUtils.hasText(bean.getActionTaken())) {
                    addActionError(
                            "error.partReturnConfiguration.actionRequired",
                            new String[]{itemNumber});
                    break;
                }
                if (!StringUtils.hasText(bean.getWarehouseLocation())
                        && !(MARK_NOT_TO_BE_RETURNED.equals(action) || MARK_NOT_RECEIVED
                        .equals(action))) {
                    addActionError(
                            "error.partReturnConfiguration.warehouseLocationRequired",
                            new String[]{itemNumber});
                    break;
                }
            }
        }
    }

    private void validateForPartInspection(List<PartTaskBean> selectedPartTaskBeans) {

    	for (OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()) {
    		if (partReplacedBean.isSelected() && Boolean.TRUE == partReplacedBean.isToBeInspected()) {
    			if (partReplacedBean.getRejected() <= 0 && partReplacedBean.getAccepted() <= 0) {
    				addActionError("error.partReturnConfiguration.noAcceptOrRejectentered");
    			} else
    				if (partReplacedBean.getRejected() + partReplacedBean.getAccepted() > partReplacedBean.getReceive()) {
    					addActionError("error.partReturnConfiguration.excessPartsInspect");
    				}
    		}
    	}
    	for (PartTaskBean bean : selectedPartTaskBeans) {
    		if (ACCEPT.equals(bean.getActionTaken())
    				|| REJECT.equals(bean.getActionTaken())) {
    			String itemNumber = bean.getPart().getItemReference().getUnserializedItem().getNumber();
    			if (!(bean.getTask().isOpen())) {
    				addActionError("error.partReturnConfiguration.partNotAvailable", new String[]{itemNumber});
    			}
    			if ((!StringUtils.hasText(bean.getFailureCause()) || "null".equals(bean.getFailureCause())) && REJECT.equals(bean.getActionTaken())) {
    				addActionError("error.partReturnConfiguration.failureCauseRequired", new String[]{itemNumber});
    				break;
    			}
    			if ((!StringUtils.hasText(bean.getAcceptanceCause()) || "null".equals(bean.getAcceptanceCause())) && ACCEPT.equals(bean.getActionTaken())) {
    				addActionError("error.partReturnConfiguration.AcceptanceCauseRequired", new String[]{itemNumber});
    				break;
    			}                
    		}
    	}
    }
    
    

	protected PartReturnWorkList getWorkList() {
		if (SHIPMENT.equals(getInboxViewType())) {
			if (showWPRA()) {
				if(isLoggedInUserADealer()){
					return getPartReturnWorkListService()
							.getPartReturnWorkListForWpraByDealership(createCriteria());
				}
				else{
				return getPartReturnWorkListService()
						.getPartReturnWorkListForWpraByActorId(createCriteria());
				}
			} else {
				return getPartReturnWorkListService()
						.getPartReturnWorkListByShipment(createCriteria());
			}
		} else {
			return getPartReturnWorkListService().getPartReturnWorkListByClaim(
					createCriteria());
		}
	}
    
    protected List<TaskInstance> findAllPartTasksForId(String id) {
        WorkListCriteria criteria = createCriteria();
        criteria.setIdentifier(id);       
        if (SHIPMENT.equals(getInboxViewType())) {
			if (showWPRA()) {
				return getPartReturnWorkListItemService()
						.findAllTasksForWPRA(criteria);
			} else {
				return getPartReturnWorkListItemService()
		        		.findAllTasksForShipment(criteria);
			}
		} else {
			return getPartReturnWorkListItemService()
	        		.findPartReturnReceiptTasksForClaim(criteria);
		}
    }
    

	public Set<String> getWareHouses(String code) {
		return warehouseService.findByWarehouseCode(code).getWarehouseBins();
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setTaskViewService(TaskViewService taskViewService) {
		this.taskViewService = taskViewService;
	}

	public String getComments() {
		return comments;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public void setFailureReasonArray(String failureReasonArray) {
		this.failureReasonArray = failureReasonArray;
	}

	public void setAcceptanceReasonArray(String acceptanceReasonArray) {
		this.acceptanceReasonArray = acceptanceReasonArray;
	}

	public Shipment getShipment() {
		return shipment;
	}

	public void setShipment(Shipment shipment) {
		this.shipment = shipment;
	}

	public String getClaimID() {
		return claimID;
	}

	public void setClaimID(String claimID) {
		this.claimID = claimID;
	}

	public List<Document> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
	}
	
	public UomMappingsService getUomMappingsService() {
		return uomMappingsService;
	}

	public void setUomMappingsService(UomMappingsService uomMappingsService) {
		this.uomMappingsService = uomMappingsService;
	}

	public WarehouseService getWarehouseService() {
		return warehouseService;
	}

	public void setWarehouseService(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}
	
	public void setPartReturnDefinitionRepository(
			PartReturnDefinitionRepository partReturnDefinitionRepository) {
		this.partReturnDefinitionRepository = partReturnDefinitionRepository;
	}

	public String getNewComments() {
		return newComments;
	}

	public void setNewComments(String newComments) {
		this.newComments = newComments;
	}

	public PartReturnProcessingService getPartReturnProcessingService() {
		return partReturnProcessingService;
	}

	public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
		this.partReturnProcessingService = partReturnProcessingService;
	}

    public String getFailureReasonArrayForBusinessUnit(String buName)  {
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(buName);
        List<ListOfValues> failureReasons = this.lovRepository.findAllActive("FailureReason");
        failureReasonArray = generateComboboxJson(failureReasons, "code", "description");
        return failureReasonArray;
	}

    public String getAcceptanceReasonArrayForBusinessUnit(String buName)  {
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(buName);
        List<ListOfValues> failureReasons = this.lovRepository.findAllActive("PartAcceptanceReason");
        acceptanceReasonArray = generateComboboxJson(failureReasons, "code", "description");
        return acceptanceReasonArray;
	}

    public List<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }
}
