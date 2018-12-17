package tavant.twms.process;

import org.springframework.transaction.annotation.Transactional;

import com.domainlanguage.time.CalendarDate;

/**
 * Created by IntelliJ IDEA.
 * User: deepak.patel
 * Date: 3/12/12
 * Time: 6:25 PM
 * To change this template use File | Settings | File Templates.
 */
@Transactional(readOnly = false)
public interface GenerateWpraScheduler {
    public void executeTasks();
    public void executeTasksBetweenDate(CalendarDate startDate,CalendarDate endDate);
}
