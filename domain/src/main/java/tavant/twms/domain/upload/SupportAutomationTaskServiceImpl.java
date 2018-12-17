package tavant.twms.domain.upload;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SupportAutomationTaskServiceImpl extends HibernateDaoSupport implements SupportAutomationTaskService{

	public void execute() {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
				throws HibernateException, SQLException 
				{
				return (session.createSQLQuery("BEGIN SUPPORT_AUTOMATION_TASK(); END;")).executeUpdate();
			    }
		});
		

		
	}

}
