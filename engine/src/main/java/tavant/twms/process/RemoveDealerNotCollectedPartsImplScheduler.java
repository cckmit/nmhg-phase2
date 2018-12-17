package tavant.twms.process;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.exe.TaskInstance;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnAction;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.security.SecurityHelper;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.partreturn.PartReturnWorkListDao;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 9/5/13
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class RemoveDealerNotCollectedPartsImplScheduler implements RemoveDealerNotCollectedPartsScheduler {
    private ConfigParamService configParamService;
    private PartReturnWorkListDao partReturnWorkListDao;
    private WorkListItemService workListItemService;
    private PartReturnService partReturnService;
    private PartReplacedService partReplacedService;

    private static final Logger logger = Logger.getLogger(RemoveDealerNotCollectedPartsImplScheduler.class);

    public void executeTasks(){
        logger.info("Enter : Start Removing Not Collected Parts By Dealer : executeTasks() for RemoveDealerNotCollectedPartsImplScheduler");
        populateDummyAuthentication();
        processLogic();
    }

    private void populateDummyAuthentication() {
        SecurityHelper securityHelper = new SecurityHelper();
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            securityHelper.populateSystemUser();
        }
    }

    private List<TaskInstance> getTheTaskList(){
        //get the value from config param
        Long daysAllowedForPartCollection = configParamService.getLongValue(ConfigName.DAYS_FOR_WAITING_FOR_DEALER_TO_COLLECT_PARTS.getName());
        //You basically need to find the open task for claimed part receipt and dealer requested part shipped inbox
        //task should have start date + 45 days

        return partReturnWorkListDao.findAllNotCollectedParts(daysAllowedForPartCollection.intValue());

        //Okay now remove from the inbox and update the oemparts.

    }

    private boolean checkIfTransitionIsAvailable(List transitions, String transition) {
        for (Object t : transitions) {
            if (transition.equals(((Transition) t).getName())) {
                return true;
            }
        }
        return false;
    }

    private void processLogic(){

        List<TaskInstance> tasks = getTheTaskList();
        List<TaskInstance> selectedTasksIfTransitionAvailable = new ArrayList<TaskInstance>();
        //Okay now proceed with updating the task instances
        List<TaskInstance> oneInboxList = new ArrayList<TaskInstance>();
        for(TaskInstance task : tasks){
            //Since we have added this one later, lets check for the transition.
            List transitions = task.getAvailableTransitions();
            if(checkIfTransitionIsAvailable(transitions, "toEnd")){
                selectedTasksIfTransitionAvailable.add(task);
                if(task.getName().equalsIgnoreCase(WorkflowConstants.CLAIMED_PARTS_RECEIPT)) {
                    oneInboxList.add(task);
                }
            }

        }
        List<OEMPartReplaced> parts = getPartsFromTasks(oneInboxList);
        //get unique oem part
        Map<Long, Map<OEMPartReplaced, Integer>> dataMap = new HashMap<Long,  Map<OEMPartReplaced, Integer>>();
        for(OEMPartReplaced part : parts){
            if(dataMap.get(part.getId()) == null){
                Map<OEMPartReplaced, Integer> map = new HashMap<OEMPartReplaced, Integer>();
                map.put(part,1);
                dataMap.put(part.getId(),map);
            }else{
                Map<OEMPartReplaced, Integer> map = dataMap.get(part.getId());
                int quantity = map.get(part) + 1;
                map.put(part,quantity);
            }
        }

        //End the task instances
        workListItemService.endAllTasksWithTransition(selectedTasksIfTransitionAvailable, "toEnd");

        //Now we have the data map for parts
        //Update the history
        //time to update the part return audit

        for(Map<OEMPartReplaced, Integer> oemMap : dataMap.values()){

                for(OEMPartReplaced part :oemMap.keySet()){
                     part.setPartAction1(new PartReturnAction(PartReturnStatus.PARTS_NOT_COLLECTED_BY_DEALER.getStatus()
                        ,oemMap.get(part)));
                     part.setComments("Auto processed by system. Parts not collected by dealer within "+configParamService.getLongValue(ConfigName.DAYS_FOR_WAITING_FOR_DEALER_TO_COLLECT_PARTS.getName()) +" days.");
                     partReturnService.updatePartStatus(part);
                     partReplacedService.updateOEMPartReplaced(part);
                }

            }
    }

    private List<OEMPartReplaced> getPartsFromTasks(List<TaskInstance> tasks) {
        List<OEMPartReplaced> parts = new ArrayList<OEMPartReplaced>();
        for (TaskInstance task : tasks) {
            PartReturn partReturn = (PartReturn) task.getVariable("partReturn");
            if(partReturn != null){
                parts.add(partReturn.getOemPartReplaced());
            }
        }
        return parts;
    }

    public void setPartReturnWorkListDao(PartReturnWorkListDao partReturnWorkListDao) {
        this.partReturnWorkListDao = partReturnWorkListDao;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }

    public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

    public void setPartReplacedService(PartReplacedService partReplacedService) {
        this.partReplacedService = partReplacedService;
    }
}
