package tavant.twms.web.partsreturn;

import com.domainlanguage.time.CalendarDate;
import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.StringUtils;
import tavant.twms.common.NoValuesDefinedException;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.partreturn.*;
import tavant.twms.infra.BeanProvider;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.partreturn.PartReturnWorkList;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: deepak.patel
 * Date: 16/10/12
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class DealerRequestedPartsReturnAction extends PartReturnInboxAction {

    private static final Logger logger = Logger.getLogger(DealerRequestedPartsReturnAction.class);

    private static final long DEALER_REQUEST_WINDOW_PERIOD_DEFAULT_VALUE = 60;

    public List<Long> getSelectedTaskInstanceIds() {
        return selectedTaskInstanceIds;
    }

    public void setSelectedTaskInstanceIds(List<Long> selectedTaskInstanceIds) {
        this.selectedTaskInstanceIds = selectedTaskInstanceIds;
    }

    private String claimId;

    private List<Long> selectedTaskInstanceIds = new ArrayList<Long>();

    private PartReturnProcessingService partReturnProcessingService;

    private String comments;

    public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
        this.partReturnProcessingService = partReturnProcessingService;
    }

    public DealerRequestedPartsReturnAction(){
        super();
        setActionUrl("rejectedParts");
    }

    @Override
	public void validate() {
		super.validate();
		validateFormData();

		if (hasActionErrors()) {
			generateView();
			setUserSpecifiedQuantity();
		}
	}

     public void validateFormData() {
		if (!hasActionErrors()) {
			for (OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()) {
				if (partReplacedBean.isSelected()) {
					if (partReplacedBean.getCannotShip() == 0 && partReplacedBean.getShip() == 0) {
						addActionError("error.partReturnConfiguration.noPartSelected");
					} else if (partReplacedBean.getCannotShip() + partReplacedBean.getShip() > partReplacedBean
							.getToBeShipped()) {
						addActionError("error.partReturnConfiguration.excessRejectedParts");
					}
				}
			}
			for (OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()) {
				if (partReplacedBean.getCannotShip() > 0 && !StringUtils.hasText(getComments())) {
					addActionError("error.manageFleetCoverage.commentsMandatory");
					break;
				}
			}
		}
	}

     public String initiateReturnRequest() throws Exception {
        List<PartTaskBean> selectedPartTasks = getSelectedPartTaskBeans();
        this.partReturnProcessingService.initiateDealerRequestedPart(selectedPartTasks,transitionTaken);
        endTasksForCannotShip(selectedPartTasks);
        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
        	if(partReplacedBean.isSelected()){
        		OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER.getStatus()
            				,partReplacedBean.getCountOfShip()));
        		part.setPartAction2(new PartReturnAction(PartReturnStatus.RETURN_TO_DEALER_NOT_REQUIRED.getStatus()
             				,partReplacedBean.getCountOfCannotShip()));
        		part.setComments(getComments());
                if(selectedPartTasks.size() > 0 && selectedPartTasks.get(0).getPartReturn()!=null && selectedPartTasks.get(0).getPartReturn().getReturnedBy() == null){
                    selectedPartTasks.get(0).getPartReturn().setReturnedBy(selectedPartTasks.get(0).getClaim().getForDealer());
                    if(null == part.getPartReturn()){
                        part.setPartReturn(selectedPartTasks.get(0).getPartReturn());
                    }
                }
        		 updatePartStatus(part);
        		 getPartReplacedService().updateOEMPartReplaced(part);
        		}

        }

         return resultingView();

    }

     protected String resultingView() {
        generateView();
        if (getClaimWithPartBeans().size() == 0) {
            addActionMessage("message.itemStatus.updated");
            return SUCCESS;
        }
        if (!hasActionErrors()) {
            addActionMessage("message.itemStatus.updated");
        }
        return INPUT;
    }

     private void endTasksForCannotShip(List<PartTaskBean> selectedPartTasks) {

        List<TaskInstance> partTasksToBeEnded = new ArrayList<TaskInstance>();
        for (PartTaskBean instance : selectedPartTasks) {
            if (PartReturnTaskTriggerStatus.TO_BE_ENDED.equals(instance.getPartReturn().getTriggerStatus())) {
                partTasksToBeEnded.add(instance.getTask());
                instance.getPartReturn().setTriggerStatus(PartReturnTaskTriggerStatus.ENDED);
            }
        }
        getWorkListItemService().endAllTasksWithTransition(partTasksToBeEnded, "toEnd");
    }

     public String getDealerRequestedWindowPeriodForBU(String buName)
    {
    	String windowPeriod = null;
        Map<String, List<Object>> daysBUMapConsideredForDenying = getConfigParamService()
                .getValuesForAllBUs(ConfigName.DEALER_REQUEST_FOR_PARTS.getName());
        if (daysBUMapConsideredForDenying != null && daysBUMapConsideredForDenying.get(buName) != null)
        {
            windowPeriod = (String)daysBUMapConsideredForDenying.get(buName).get(0);
        }

    	return windowPeriod;
    }

     public boolean isReturnRequestAllowed(String buName)
    {
        long windowPeriod = getDealerRequestedWindowPeriodForBU(buName) != null ? Integer.parseInt(getDealerRequestedWindowPeriodForBU(buName)) : DEALER_REQUEST_WINDOW_PERIOD_DEFAULT_VALUE;
        Claim claim = getClaimService().findClaim(Long.valueOf(getId()));
        if(null != claim && ClaimState.DENIED_AND_CLOSED.equals(claim.getState())){

            //Fix slmsprod-641, window period will be calculated from claim credit date
            CalendarDate lastUpdateDate=claim.getCreditDate();
            /*Calendar cal = Calendar.getInstance();
            cal.setTime(lastUpdatedDate);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH)+1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
			CalendarDate lastUpdateDate = CalendarDate.date(year,month,day);*/
            if(null != lastUpdateDate) {
                Date lastUpdateDate_canval = new Date(lastUpdateDate.breachEncapsulationOf_year()-1900,lastUpdateDate.breachEncapsulationOf_month()-1,lastUpdateDate.breachEncapsulationOf_day());
                if(((new Date().getTime() - lastUpdateDate_canval.getTime())/(1000 * 60 * 60 * 24)) > windowPeriod)
                    return false;
            }
            //if credit date is null take the last updated date
            else{
                Date lastUpdatedOnDateDate=claim.getLastUpdatedOnDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(lastUpdatedOnDateDate);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH)+1;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                lastUpdateDate = CalendarDate.date(year,month,day);
                Date lastUpdateDate_canval = new Date(lastUpdateDate.breachEncapsulationOf_year()-1900,lastUpdateDate.breachEncapsulationOf_month()-1,lastUpdateDate.breachEncapsulationOf_day());
                if(((new Date().getTime() - lastUpdateDate_canval.getTime())/(1000 * 60 * 60 * 24)) > windowPeriod)
                    return false;
            }
        }
    	return true;
    }

    @Override
       protected List<SummaryTableColumn> getHeader() {
           List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
           tableHeadData.add(new SummaryTableColumn("columnTitle.partReturnConfiguration.claimNo",
                   "claim.claimNumber", 16, "string"));
           tableHeadData.add(new SummaryTableColumn("columnTitle.partReturnConfiguration.claimNo", "claim.id", 17,
                   "number", "id", true, true, true, false));
           return tableHeadData;
       }

       public BeanProvider getBeanProvider() {
           return new SkipInitialOgnlBeanProvider() {
               @Override
               public Object getProperty(String propertyPath, Object root) {
                   if ("populateModelNumber".equals(propertyPath)) {
                       boolean isSerialized = (Boolean) super.getProperty("claim.itemReference.serialized", root);
                       String modelNumber = "";
                       if(isSerialized)
                       {
                           modelNumber = (String)super.getProperty("claim.itemReference.unserializedItem.model.name", root);
                       }
                       else
                       {
                           modelNumber = (String)super.getProperty("claim.itemReference.model.name", root);
                       }
                       return modelNumber;
                   } else {
                       return super.getProperty(propertyPath, root);
                   }
               }
           };
       }

       @Override
       protected PartReturnWorkList getWorkList() {
           WorkListCriteria criteria = createCriteria();
           if(criteria.getSortCriteria().keySet()!=null
                   && criteria.getSortCriteria().keySet().isEmpty()){
               criteria.addSortCriteria("claim.claimNumber",false);
           }
           return getPartReturnWorkListService().getPartReturnWorkListByClaim(criteria);
       }

       @Override
       protected List<TaskInstance> findAllPartTasksForId(String id) {
           WorkListCriteria criteria = createCriteria();
           criteria.setIdentifier(id);
           return getPartReturnWorkListItemService().findAllTasksForClaim(criteria);
       }

       @Override
       public void setId(String claimId) {
           this.claimId = claimId;
       }

       @Override
       public String getId() {
           return this.claimId;
       }
       public boolean isPageReadOnly() {
           return false;
       }

       public boolean isPageReadOnlyAdditional() {
           boolean isReadOnlyDealer = false;
           Set<Role> roles = getLoggedInUser().getRoles();
           for (Role role : roles) {
               if (role.getName().equalsIgnoreCase(Role.READ_ONLY_DEALER)) {
                   isReadOnlyDealer = true;
                   break;
               }
           }
           return isReadOnlyDealer;
       }

    protected void setUserSpecifiedQuantity() {
		for (ClaimWithPartBeans claimWithPartBeans : getClaimWithPartBeans()) {
			for (OEMPartReplacedBean partReplacedBean : claimWithPartBeans.getPartReplacedBeans()) {
				for (OEMPartReplacedBean uiPartReplacedBean : this.getPartReplacedBeans()) {
					if (partReplacedBean.getPartReplacedId() == uiPartReplacedBean.getPartReplacedId()) {
						partReplacedBean.setSelected(uiPartReplacedBean.isSelected());
						partReplacedBean.setShip(uiPartReplacedBean.getShip());
						partReplacedBean.setCannotShip(uiPartReplacedBean.getCannotShip());


					}
				}
			}
		}
	}

    public String removeFromInbox() throws Exception{
        List<TaskInstance> openTasksForClaim = getWorkListItemService().findAllOpenTasksForClaim(Long.valueOf(getId()));
        List<TaskInstance> openTasksForDealer = new ArrayList<TaskInstance>();
        for(TaskInstance instance : openTasksForClaim){
            if(instance.getTask().getName().equals(WorkflowConstants.REJETCTED_PARTS_INBOX)){
               openTasksForDealer.add(instance);
            }
        }
        getWorkListItemService().endAllTasksWithTransition(openTasksForDealer,"Request Rejected Part");
        addActionMessage("message.itemStatus.updated");
        return SUCCESS;
    }

    public String getComments() {
       return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}
