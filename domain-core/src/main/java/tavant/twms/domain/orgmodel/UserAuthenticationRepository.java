package tavant.twms.domain.orgmodel;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Nov 6, 2008
 * Time: 11:36:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface UserAuthenticationRepository {
    User findByName(String userName);
}
