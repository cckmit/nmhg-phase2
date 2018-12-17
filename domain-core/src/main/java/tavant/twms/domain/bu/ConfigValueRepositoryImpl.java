package tavant.twms.domain.bu;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.infra.GenericRepositoryImpl;

public class ConfigValueRepositoryImpl extends GenericRepositoryImpl<ConfigValue, Long> implements ConfigValueRepository {
    
    public Date getNextCreditSubmissionDate(final String buName){
        return (Date) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                //String query = "select min(next_fire_time) from qrtz_triggers where job_name = '" + buName + "' and job_group = 'Credit Submission'";
                String query = "select min(next_fire_time) from qrtz_triggers where job_name='Credit Submission' and job_group = 'Credit Submission'";
                BigDecimal l = (BigDecimal) session.createSQLQuery(query).uniqueResult();
                if(l!=null){
                return new Date(l.longValue());
                }else{
                	 return new Date();
                }
            }
        });
    }

}
