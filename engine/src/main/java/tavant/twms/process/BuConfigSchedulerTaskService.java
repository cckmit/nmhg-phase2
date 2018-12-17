package tavant.twms.process;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = false)
public interface BuConfigSchedulerTaskService {
	public void executeTasks();

    public void executeCleanupForWpraAndSupPrtShippedTasks();
}
