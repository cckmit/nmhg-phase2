package tavant.twms.process;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = false)
@Deprecated
public interface SupplierFinalResponseEmailScheduler {
	 public void executeTasks();
}
