package tavant.twms.external;

import org.jbpm.taskmgmt.exe.TaskInstance;
import tavant.twms.web.actions.TwmsActionSupport;
import tavant.twms.worklist.WorkListItemService;

public class TaskTransitionAction extends TwmsActionSupport {
    
    
    private Long id;
    
    private String currentTaskName;
    
    private String transitionTaken;
    
    private WorkListItemService workListItemService;
    
    
    
    @Override
    public String execute() throws Exception {
        
        
        return SUCCESS;
    }

    public String submit(){
        
        TaskInstance taskInstance = workListItemService.findTask(id);    
        workListItemService.endTaskWithTransition(taskInstance, transitionTaken);
        return SUCCESS;
        
    }

    public String getCurrentTaskName() {
        return currentTaskName;
    }

    public void setCurrentTaskName(String currentTaskName) {
        this.currentTaskName = currentTaskName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransitionTaken() {
        return transitionTaken;
    }

    public void setTransitionTaken(String transitionTaken) {
        this.transitionTaken = transitionTaken;
    }

    public WorkListItemService getWorkListItemService() {
        return workListItemService;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }
    
    
    
    
}       