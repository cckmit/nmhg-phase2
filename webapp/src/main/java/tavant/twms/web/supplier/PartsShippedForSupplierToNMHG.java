package tavant.twms.web.supplier;

import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.InboxItem;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;

/**
 * Created by Ajitkumar.singh on 31/1/14.
 */
public class PartsShippedForSupplierToNMHG extends AbstractSupplierActionSupport {
	
	  private TaskViewService taskViewService;
 

	@Override
    protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
        return workListService.getSupplierRecoveryClaimBasedView(criteria);
    }

    @Override
    protected PageResult<?> getPageResult(List inboxItems, PageSpecification pageSpecification, int noOfPages) {
        return new PageResult<InboxItem>(inboxItems, pageSpecification, noOfPages);
    }

    @Override
    protected String getAlias() {
        return "recoveryClaim";
    }
    
    //TODO temp method only needs to change
    public String submit(){
        List<TaskInstance> taskInstances = getTaskInstancesForShipper(getId());
        taskViewService.submitAllTaskInstances(taskInstances, "Parts Shipped fork");
        return SUCCESS;
    }
    

	   public TaskViewService getTaskViewService() {
	        return taskViewService;
	    }

	    public void setTaskViewService(TaskViewService taskViewService) {
	        this.taskViewService = taskViewService;
	    }

}
