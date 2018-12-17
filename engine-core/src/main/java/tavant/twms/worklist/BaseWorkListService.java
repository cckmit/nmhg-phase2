package tavant.twms.worklist;

import java.util.Map;

import org.jbpm.taskmgmt.def.Task;

public interface BaseWorkListService {
    public Map<Task, Long> getAllTasks(WorkListCriteria criteria);
}
