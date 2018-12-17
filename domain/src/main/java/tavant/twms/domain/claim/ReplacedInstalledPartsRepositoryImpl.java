package tavant.twms.domain.claim;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.inventory.Pagination;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.PageSpecification;

public class ReplacedInstalledPartsRepositoryImpl extends
		GenericRepositoryImpl<InstalledParts, Long> implements
		ReplacedInstalledPartsRepository {

	@SuppressWarnings("unchecked")
	public Long generateSequence() {
		Iterator it = getSession().createSQLQuery(
				"select GROUP_ID_SEQ.nextVal from dual d").list()
				.iterator();
		return new Long((it.next()).toString());
	}

	@SuppressWarnings("unchecked")
	public List<String> findItemNumbers(List<String> listOfPartNumbers) {
		String query = "select number from Item item where number in (:listOfItems)";
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("listOfItems", listOfPartNumbers);		
		return findItemNumbersUsingQuery(query, map);	
	}

	public List<String> findItemNumbers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("unchecked")
    protected List<String> findItemNumbersUsingQuery(final String queryString,final Map<String,Object> params) {
        return (List<String>)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                if( logger.isDebugEnabled() ) {
                    logger.debug("findUsingQuery("+queryString+","+params+")");
                }
                
                Query query = session.createQuery(queryString);
                query.setProperties(params);
                return query.list();
            }
        });
    }
}
