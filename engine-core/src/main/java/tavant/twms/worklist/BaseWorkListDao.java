package tavant.twms.worklist;

import java.util.Map;

import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.transaction.annotation.Transactional;

public interface BaseWorkListDao {

    /**
     * Returns the tasks and the count for that particular task for a given actor.
     */
    public Map<Task, Long> getAllTasks(WorkListCriteria criteria);

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = false)
    public void updateTaskInstance(TaskInstance taskInstance);
}
