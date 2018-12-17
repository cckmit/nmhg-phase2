package tavant.twms.domain.email;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import tavant.twms.infra.GenericRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by deepak.patel on 31/3/14.
 */
public class TestEmailRepositoryImpl extends GenericRepositoryImpl<TestEmails, Long> implements TestEmailRepository {
    public List<TestEmails> findAllTestEmails() {
        return (List<TestEmails>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createCriteria(TestEmails.class).setCacheable(true).list();
            }
        });
    }
}
