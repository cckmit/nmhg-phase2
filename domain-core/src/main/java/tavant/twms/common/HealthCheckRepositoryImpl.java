package tavant.twms.common;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class HealthCheckRepositoryImpl extends HibernateDaoSupport implements
		HealthCheckRepository {
	public void checkConnectivity() {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				String sqlQuery = "select 1 from dual";
                return session.createSQLQuery(sqlQuery).list();
			}
		});
	}
}
