/**
 * 
 */
package tavant.twms.worklist.partreturn;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.worklist.WorkListCriteria;

/**
 * @author kannan.ekanath
 *
 */
public class PartReturnWorkListDaoTest extends EngineRepositoryTestCase {

    private PartReturnWorkListDao partReturnWorkListDao;
    
    public void testQuerySyntax() {
        User user = new User();
        user.setName("user");
        WorkListCriteria criteria = new WorkListCriteria(user);
        criteria.setTaskName("task");
        criteria.setIdentifier("1");
        partReturnWorkListDao.getPartReturnWorkListByLocation(criteria);
        partReturnWorkListDao.findAllTasksForLocation(criteria);
        partReturnWorkListDao.getPartReturnWorkListByShipment(criteria);
        partReturnWorkListDao.findAllTasksForShipment(criteria);
        partReturnWorkListDao.findAllDueAndOverduePartTasksForLocation(criteria);
    }

    public void setPartReturnWorkListDao(PartReturnWorkListDao partReturnWorkListDao) {
        this.partReturnWorkListDao = partReturnWorkListDao;
    }
}
