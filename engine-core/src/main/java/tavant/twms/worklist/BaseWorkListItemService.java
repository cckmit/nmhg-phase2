package tavant.twms.worklist;

import java.util.List;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface BaseWorkListItemService {

    public TaskInstance findTask(Long id);

    public List<TaskInstance> findTasks(List ids);

    @Transactional(readOnly = false)
    public void endTaskWithTransition(TaskInstance taskInstance, String transition);

    @Transactional(readOnly = false)
    public void endAllTasksWithTransition(List<TaskInstance> taskInstances);

    @Transactional(readOnly = false)
    public void endAllTasksWithTransition(List<TaskInstance> taskInstances, String transition);

    @Transactional(readOnly = false)
    public void endTaskWithReassignment(final TaskInstance taskInstance,
                                        final String transition, final String userName);

    public List<TaskInstance> getTaskInstancesAtTaskName(String taskName);
}
