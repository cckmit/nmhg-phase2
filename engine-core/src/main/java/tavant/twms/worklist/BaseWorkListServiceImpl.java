package tavant.twms.worklist;

import java.util.Map;

import org.jbpm.taskmgmt.def.Task;

public abstract class BaseWorkListServiceImpl implements BaseWorkListService {

    public Map<Task, Long> getAllTasks(WorkListCriteria criteria) {
        return getWorkListDao().getAllTasks(criteria);
    }

    public abstract BaseWorkListDao getWorkListDao();
}
