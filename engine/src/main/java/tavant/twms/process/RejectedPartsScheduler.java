package tavant.twms.process;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = false)
public interface RejectedPartsScheduler {
	  public void executeTasks();
}
