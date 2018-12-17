package tavant.twms.domain.stateMandates;

import java.sql.SQLException;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class StateMandatesRepositoryImpl extends GenericRepositoryImpl<StateMandates, Long>implements StateMandatesRepository{
	
	
		public StateMandates findByName(final String state) {
	        return (StateMandates) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	            	return session.createQuery("from StateMandates sm where sm.state =:state").setString(
	            	        "state", state).uniqueResult();
	            }
	        });
	    
	}
		
		public StateMandates findActiveByName(final String state) {
	        return (StateMandates) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                return session.createQuery("from StateMandates sm where sm.state =:state and status = 'ACTIVE'").setString(
	                        "state", state).uniqueResult();
	            }
	        });	
		}

	public PageResult<StateMandates> findByActive(ListCriteria criteria) {
		StringBuffer baseQuery = new StringBuffer("from StateMandates where status='ACTIVE'");
		if (criteria.isFilterCriteriaSpecified()) {
			baseQuery.append(" and ");
			String paramterizedFilterCriteria = criteria.getParamterizedFilterCriteria();
			baseQuery.append(paramterizedFilterCriteria);
		}
		final Map<String, Object> parameterMap = criteria.getParameterMap();
		 return findPageUsingQuery(baseQuery.toString(), getSortCriteriaString(criteria), criteria.getPageSpecification(), parameterMap);
	}
	
	public PageResult<StateMandates> findAll(ListCriteria criteria) {
		StringBuffer baseQuery = new StringBuffer("from StateMandates where status in ('ACTIVE','INACTIVE')");
		if (criteria.isFilterCriteriaSpecified()) {
			baseQuery.append(" and ");
			String paramterizedFilterCriteria = criteria.getParamterizedFilterCriteria();
			baseQuery.append(paramterizedFilterCriteria);
		}
		final Map<String, Object> parameterMap = criteria.getParameterMap();
		 return findPageUsingQuery(baseQuery.toString(),getSortCriteriaString(criteria), criteria.getPageSpecification(), parameterMap);
	}
	
	private String getSortCriteriaString(ListCriteria criteria) {
		if (criteria.getSortCriteria().size() > 0) {
			StringBuffer dynamicQuery = new StringBuffer();
			for (String columnName : criteria.getSortCriteria().keySet()) {
				dynamicQuery.append(columnName);
				dynamicQuery.append(" ");
				dynamicQuery.append(criteria.getSortCriteria().get(columnName));
				dynamicQuery.append(",");
			}
			dynamicQuery.deleteCharAt(dynamicQuery.length() - 1);
			return dynamicQuery.toString();
		}
		return "";
	}

}


