package tavant.twms.domain.configuration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.GenericRepositoryImpl;

public class ConfigParamRepositoryImpl extends
		GenericRepositoryImpl<ConfigParam, Long> implements
		ConfigParamRepository {

	private CriteriaHelper criteriaHelper;

	public CriteriaHelper getCriteriaHelper() {
		return this.criteriaHelper;
	}

	public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
		this.criteriaHelper = criteriaHelper;
	}

	@Override
	public void save(ConfigParam newwntyConfigParam) {
		getHibernateTemplate().save(newwntyConfigParam);
	}

	@Override
	public void update(ConfigParam wntyConfigParamUpdated) {
		getHibernateTemplate().update(wntyConfigParamUpdated);
	}

	@Override
	public void delete(ConfigParam wntyConfigParam) {
		getHibernateTemplate().delete(wntyConfigParam);
	}

	/**
	 * This API will return only the config param looked up by name not the config values.
	 * To get the config values for a given config param name, use getValuesForConfigParam
	 * API in this class
	 */
	public ConfigParam findConfig(String name) {
		final String param = name;
		return (ConfigParam) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {						
						Criteria configCriteria = session
								.createCriteria(ConfigParam.class);
						configCriteria.add(Restrictions.eq("name", param));												
						configCriteria
								.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
						configCriteria.setCacheable(true);
						return configCriteria.uniqueResult();
					};
				});
	}

	public ConfigParam reloadConfig(String name) {
		/**
		 * This method is being wiped off as the session.refresh conflicts with cache implementation
		 * Also its not clear why refresh is being done here as in a given flow we should never be 
		 * looking for the same config param once for a given BU and once again for all BUs.
		 * If somebody understands this, we can see how to make this work with cache.
		 */
		return findConfig(name);
	}

	/**
	 * Can this be clubbed with findConfig query itself?
	 */
	@SuppressWarnings("unchecked")
	public List<ConfigValue> getValuesForConfigParam(final String configParamName) {	  	
		return (List<ConfigValue>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						String queryStr = " select configValue from ConfigValue configValue where configValue.configParam.name = :configParamName  ";
						Query query = session.createQuery(queryStr.toString());
						HashMap<String, Object> parameterMap = new HashMap<String, Object>();
						parameterMap.put("configParamName", configParamName);
						query.setProperties(parameterMap);
						query.setCacheable(true);						
						return query.list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<ConfigValue> getValuesForConfigParamByBU(final String configParamName,
			final String BUName) {
		return (List<ConfigValue>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException {
						String queryStr = " select configValue from ConfigValue configValue where configValue.configParam.name = :configParamName and configValue.businessUnitInfo = :businessUnitInfo  ";
						Query query = session.createQuery(queryStr.toString());
						HashMap<String, Object> parameterMap = new HashMap<String, Object>();
						parameterMap.put("configParamName", configParamName);
						parameterMap.put("businessUnitInfo", BUName);
						query.setProperties(parameterMap);
						query.setCacheable(true);						
						return query.list();
					}
				});
	}

}
