package tavant.twms.domain.claim;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

public class ClaimNumberPatternRepositoryImpl extends GenericRepositoryImpl<ClaimNumberPattern,Long>
		implements ClaimNumberPatternRepository {

	public List<ClaimNumberPattern> findAllClaimNumberPatterns() {
		List<ClaimNumberPattern> patterns = findAll();
		return patterns;
	}
	
	@SuppressWarnings("unchecked")
	public ClaimNumberPattern findActivePattern(){
        List<ClaimNumberPattern> pattern = (List<ClaimNumberPattern>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from ClaimNumberPattern c " +
                                           "where c.isActive = true " +
                                           "order by c.id").list();
            }

        });
		 return pattern.get(0);
	}
	
	
	@Override
	public void update(ClaimNumberPattern claimPatternToBeUpdated) {
		getHibernateTemplate().update(claimPatternToBeUpdated);
	}
	
	@SuppressWarnings("unchecked")
	public String getNextGeneratedClaimNumber(final String sequenceName, final String prefix){
		Iterator it =  (Iterator) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createSQLQuery("select to_char(" + sequenceName + ".nextval,'" + prefix + "') from dual d").list().iterator();
            }

        });
		return (it.next()).toString();
	}

	public void resetSequenceName(final String sequenceName) {
		dropSequenceName(sequenceName);
		recreateSequenceName(sequenceName);
		
	}

	private void dropSequenceName(final String sequenceName) {
		getHibernateTemplate().execute(new HibernateCallback(){
			 public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                return session.createSQLQuery("DROP SEQUENCE "+sequenceName);
	            }

	        });
	}

	private void recreateSequenceName(final String sequenceName) {
		getHibernateTemplate().execute(new HibernateCallback(){
			 public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                return session.createSQLQuery("CREATE SEQUENCE" +sequenceName+ "START WITH 0 INCREMENT BY 1 NOCACHE NOCYCLE");
	            }

	        });
	}
}
