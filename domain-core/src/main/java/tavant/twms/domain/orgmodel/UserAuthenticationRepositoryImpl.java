package tavant.twms.domain.orgmodel;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.hibernate.Session;
import org.hibernate.HibernateException;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Nov 6, 2008
 * Time: 11:37:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserAuthenticationRepositoryImpl extends HibernateDaoSupport implements UserAuthenticationRepository {
    public User findByName(final String userName) {
        return (User) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from User u where upper(u.name) =:nameParam").setString(
                        "nameParam", userName.toUpperCase()).uniqueResult();
            }
        });
    }
}
