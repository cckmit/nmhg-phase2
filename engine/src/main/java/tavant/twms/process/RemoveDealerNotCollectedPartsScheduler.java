package tavant.twms.process;

import org.springframework.transaction.annotation.Transactional;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 9/5/13
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
@Transactional(readOnly = false)
public interface RemoveDealerNotCollectedPartsScheduler {

    public void executeTasks();

}
