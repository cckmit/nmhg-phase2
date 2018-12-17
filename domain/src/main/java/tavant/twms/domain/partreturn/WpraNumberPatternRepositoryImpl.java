package tavant.twms.domain.partreturn;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

public class WpraNumberPatternRepositoryImpl extends GenericRepositoryImpl<WpraNumberPattern,Long>
		implements WpraNumberPatternRepository {

	public List<WpraNumberPattern> findAllWpraNumberPatterns() {
		List<WpraNumberPattern> patterns = findAll();
		return patterns;
	}
	
	@SuppressWarnings("unchecked")
	public WpraNumberPattern findActivePattern(){
        List<WpraNumberPattern> pattern = (List<WpraNumberPattern>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from WpraNumberPattern w " +
                                           "where w.isActive = true " +
                                           "order by w.id").list();
            }

        });
		 return pattern.get(0);
	}
	
	
	@Override
	public void update(WpraNumberPattern wpraPatternToBeUpdated) {
		getHibernateTemplate().update(wpraPatternToBeUpdated);
	}
	
	@SuppressWarnings("unchecked")
	public String getNextGeneratedWpraNumber(final String sequenceName){
		Iterator it =  (Iterator) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createSQLQuery("select to_char(" + sequenceName + ".nextval,'FM000009') from dual d").list().iterator();
            }

        });
		return (it.next()).toString();
	}

	public void resetSequenceName(final String sequenceName) {
		dropSequenceName(sequenceName);
		recreateSequenceName(sequenceName);
		
	}

	@SuppressWarnings("unchecked")
	private void dropSequenceName(final String sequenceName) {
		getHibernateTemplate().execute(new HibernateCallback(){
			 public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                return session.createSQLQuery("DROP SEQUENCE "+sequenceName);
	            }

	        });
	}

	@SuppressWarnings("unchecked")
	private void recreateSequenceName(final String sequenceName) {
		getHibernateTemplate().execute(new HibernateCallback(){
			 public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                return session.createSQLQuery("CREATE SEQUENCE " +sequenceName+ " START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE");
	            }

	        });
	}
}
