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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.json.JSONArray;
import org.springframework.util.StringUtils;
import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.common.AcceptanceReason;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.partreturn.FailureReason;
import tavant.twms.domain.partreturn.PartAcceptanceReason;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnAction;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.common.Document;
import tavant.twms.process.PartTaskBean;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.partreturn.PartReturnWorkList;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class DuePartsInspectionAction extends PartReturnInboxAction implements Preparable {

    private static final Logger logger = Logger.getLogger(DuePartsInspectionAction.class);

    private TaskViewService taskViewService;
    
    public static final String ACCEPT = "Accept";

    public static final String TRANSITION = "transition";

    public static final String MARK_FOR_SUPPLY_RECOVERY = "Mark for supplier recovery";

    public LovRepository lovRepository;

    public String failureReasonArray ;

    public String acceptanceReasonArray;

    private String comments;
    
    private String claimID;
    
    private List<Document> attachments = new ArrayList<Document>();
    
	@Override
    protected PartReturnWorkList getWorkList() {
        WorkListCriteria criteria = createCriteria();
        if (SHIPMENT.equals(getInboxViewType())) {
			if (showWPRA()) {
				if (isLoggedInUserADealer()) {
					return getPartReturnWorkListService()
							.getPartReturnWorkListForWpraByDealership(criteria);
				} else {
					return getPartReturnWorkListService()
							.getPartReturnWorkListForWpraByActorId(criteria);
				}			
			} else {
				return getPartReturnWorkListService()
						.getPartReturnWorkListByShipment(criteria);
			}
		} else {
			if(criteria.getSortCriteria().keySet()!=null
	                && criteria.getSortCriteria().keySet().isEmpty()){
	            criteria.addSortCriteria("claim.claimNumber",false);
	        }
			return getPartReturnWorkListService().getPartReturnWorkListByClaim(
					criteria);			
		}
    }

    @Override
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
	        		.findPartReturnInspectionTasksForClaim(criteria);
		}
    }
    
    public String submitTasks() {
        List<PartTaskBean> partTaskBeans = getSelectedPartTaskBeans();
        taskViewService.submitAllTaskInstances(getTasksFromPartTaskBeansForAccetorReject(partTaskBeans));
        if (attachments != null) {
            for (PartTaskBean partTaskBean : partTaskBeans) {
                Claim claim = claimService.findClaim(partTaskBean.getClaim().getId());
                claim.getAttachments().clear();
                claim.getAttachments().addAll(attachments);
                claimService.updateClaim(claim);
            }
        }
        for(PartTaskBean partTaskBean :partTaskBeans){
        	if(REJECT.equals(partTaskBean.getActionTaken()) && partTaskBean.getPartReturn().getPaymentCondition().getCode().equals("PAY_ON_INSPECTION")){
        		processForRejectedPartInboxFlow(partTaskBean);	
        	}
        	processForOnHoldForPartReturnInboxFlow(partTaskBean);
        }
        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
        	if(partReplacedBean.isSelected()){
        		OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
        		part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_ACCEPTED.getStatus()
        				,partReplacedBean.getCountOfAccepted()));
        		part.setPartAction2(new PartReturnAction(PartReturnStatus.PART_REJECTED.getStatus()
        				,partReplacedBean.getCountOfRejected()));
                
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
                getPartReplacedService().updateOEMPartReplaced(part);
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
        }
        createEventsForRejectedPartsOnClaim(partTaskBeans);
        generateView();
        addActionMessage("message.itemStatus.updated");
        return SUCCESS;
    }

    
    public void prepare() throws Exception {

   }

    private JSONArray populateJSonArray(List<ListOfValues> reasons) {
        JSONArray returnArray = new JSONArray();
        JSONArray reasonValuePair = new JSONArray();
        reasonValuePair = new JSONArray();
        reasonValuePair.put("Select");
        returnArray.put(reasonValuePair);
        for (ListOfValues failureReason : reasons) {
            reasonValuePair = new JSONArray();
            reasonValuePair.put(failureReason.getDescription());
            reasonValuePair.put(failureReason.getCode());
            returnArray.put(reasonValuePair);
        }
        return returnArray;
    }

    // TODO : Getting it working. Need to move all the service calls into a
    // single transaction
    // this method is fired from validate() as well...
    protected void processPartTaskBean(PartTaskBean partTaskBean) {
        PartReturn partReturn = partTaskBean.getPartReturn();
        if (partTaskBean.getActionTaken() == ACCEPT){
            partReturn.setActionTaken(PartReturnStatus.PART_ACCEPTED);
        }else if(partTaskBean.getActionTaken() == REJECT){
            partReturn.setActionTaken(PartReturnStatus.PART_REJECTED);
        }
        List<PartReturn> partsAccepted = new ArrayList<PartReturn>();
        List<PartReturn> partsRejected = new ArrayList<PartReturn>();
        if (partTaskBean.getActionTaken() == ACCEPT)
            partsAccepted.add(partTaskBean.getPartReturn());
        else if (partTaskBean.getActionTaken() == REJECT) {
            partsRejected.add(partTaskBean.getPartReturn());
        } 

        // TODO : Move it to a single transaction.
        if (StringUtils.startsWithIgnoreCase(partTaskBean.getActionTaken(), ACCEPT)) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(partTaskBean.getClaim().getBusinessUnitInfo().getName());
            getPartReturnService().acceptPartAfterInspection(partsAccepted, null, partTaskBean.getAcceptanceCause());
            partTaskBean.getTask().setVariable(TRANSITION, "Mark for Reuse or Scrap");
        } else if (StringUtils.startsWithIgnoreCase(partTaskBean.getActionTaken(), REJECT)) {
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(partTaskBean.getClaim().getBusinessUnitInfo().getName());
            getPartReturnService().rejectPartAfterInspection(partsRejected, partTaskBean.getFailureCause(), null);
            partTaskBean.getTask().setVariable(TRANSITION, "Mark for Reuse or Scrap");
        }
    }

    
    // TODO: Need to refactor as duplication of code in parent class as well.
	@Override
	public void validate() {
		super.validate();
		List<PartTaskBean> partTasks = getSelectedPartTaskBeans(false);
		if (!StringUtils.hasText(getComments())) {
			addActionError("error.manageFleetCoverage.commentsMandatory");
		}


		for (OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()) {
			if (partReplacedBean.isSelected()) {
				if (partReplacedBean.getRejected() == 0 && partReplacedBean.getAccepted() == 0) {
					addActionError("error.partReturnConfiguration.noAcceptOrRejectentered");
				} else if (partReplacedBean.getRejected() + partReplacedBean.getAccepted() > (partReplacedBean
						.getReceived() - partReplacedBean.getInspected())) {
					addActionError("error.partReturnConfiguration.excessPartsInspect");
				}
			}
		}

		for (PartTaskBean bean : partTasks) {
			String itemNumber = bean.getPart().getItemReference().getUnserializedItem().getNumber();
			if ((!StringUtils.hasText(bean.getFailureCause()) || "null".equals(bean.getFailureCause()))
					&& REJECT.equals(bean.getActionTaken())) {
				addActionError("error.partReturnConfiguration.failureCauseRequired", new String[] { itemNumber });
				break;
			}
			if ((!StringUtils.hasText(bean.getAcceptanceCause()) || "null".equals(bean.getAcceptanceCause()))
					&& ACCEPT.equals(bean.getActionTaken())) {
				addActionError("error.partReturnConfiguration.AcceptanceCauseRequired", new String[] { itemNumber });
				break;
			}

		}
		if (hasErrors()) {
			generateView();
			this.setUserSpecifiedQuantity();
		}
	}
    
	protected void setUserSpecifiedQuantity() {
		for (ClaimWithPartBeans claimWithPartBeans : getClaimWithPartBeans()) {
			for (OEMPartReplacedBean partReplacedBean : claimWithPartBeans.getPartReplacedBeans()) {
				for (OEMPartReplacedBean uiPartReplacedBean : this.getPartReplacedBeans()) {
					if (partReplacedBean.getPartReplacedId() == uiPartReplacedBean.getPartReplacedId()) {
						partReplacedBean.setAccepted(uiPartReplacedBean.getAccepted());
						partReplacedBean.setRejected(uiPartReplacedBean.getRejected());
						partReplacedBean.setWarehouseLocation(uiPartReplacedBean.getWarehouseLocation());
						partReplacedBean.setSelected(uiPartReplacedBean.isSelected());
						partReplacedBean.setToBeScrapped(uiPartReplacedBean.isToBeScrapped());
						for (PartTaskBean uipartTaskBean : uiPartReplacedBean.getPartReturnTasks()) {
							for (PartTaskBean partTaskBean : partReplacedBean.getPartReturnTasks()) {
								if (partTaskBean.getTask() != null
										&& partTaskBean.getTask().equals(uipartTaskBean.getTask())) {
									partTaskBean.setAcceptanceCause(uipartTaskBean.getAcceptanceCause());
									partTaskBean.setFailureCause(uipartTaskBean.getFailureCause());
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
								}
							}

						}

					}

				}
			}
		}
	}

    public void setTaskViewService(TaskViewService taskViewService) {
        this.taskViewService = taskViewService;
    }

    public void setLovRepository(LovRepository lovRepository) {
        this.lovRepository = lovRepository;
    }

	public String getFailureReasonArray() {
		return failureReasonArray;
	}

	public void setFailureReasonArray(String failureReasonArray) {
		this.failureReasonArray = failureReasonArray;
	}

	public String getAcceptanceReasonArray() {
		return acceptanceReasonArray;
	}

	public void setAcceptanceReasonArray(String acceptanceReasonArray) {
		this.acceptanceReasonArray = acceptanceReasonArray;
	}

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    protected List<TaskInstance> getTasksFromPartTaskBeansForAccetorReject(List<PartTaskBean> partTaskBeans){
        List<TaskInstance> toReturn = new ArrayList<TaskInstance>();
        if(partTaskBeans!=null){
            for (PartTaskBean partTaskBean : partTaskBeans) {
               if(ACCEPT.equalsIgnoreCase(partTaskBean.getActionTaken())
                       || REJECT.equalsIgnoreCase(partTaskBean.getActionTaken())) {
                    toReturn.add(partTaskBean.getTask());
               }
            }
        }
        return toReturn;
    }
}
