/**
 * 
 */
package tavant.twms.domain.common;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;


/**
 * @author aniruddha.chaturvedi
 * 
 */
public class LovRepositoryImpl extends GenericRepositoryImpl<ListOfValues, Long> implements LovRepository {
	private Map<String,GenericRepository<ListOfValues, Long>> repoMap;
	
	private Map<String,Class> classMap;

	public void setRepoMap(Map<String, GenericRepository<ListOfValues, Long>> map) {
		this.repoMap = map;
	}
	
	public void save(ListOfValues listOfValues) {
		getRepository(listOfValues).save(listOfValues);
	}
	
	public void update(ListOfValues listOfValues){
		getRepository(listOfValues).update(listOfValues);
	}

    public void delete(ListOfValues listOfValues) {
    	getRepository(listOfValues).delete(listOfValues);
    }

    public ListOfValues findById(String className, Long id){
    	return repoMap.get(className).findById(id);
    }
    
    public ListOfValues findByCode(final String className, final String code)
    {
    		return (ListOfValues) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from "
												+ className
												+ " className where className.code = :code")
								.setParameter("code", code).uniqueResult();
					}
				});
    }
    
	public ListOfValues findByDescription(final String className, final String descr , final String locale ,final String bu) {
		final String query = "select distinct className from " + className
				+ " className inner join className.i18nLovTexts i18nLovTexts where i18nLovTexts.description = :descr and  "
				+ " i18nLovTexts.locale = :locale and className.businessUnitInfo = :bu";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("descr", descr);
		params.put("locale", locale);
		params.put("bu", bu);
		return (ListOfValues)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery(query).setProperties(params).uniqueResult();
			}

		});
	}

    public ListOfValues findByCode(final String className, final String code, final String bu)
    {
    	return (ListOfValues) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from "
												+ className
												+ " className where className.code = :code and className.businessUnitInfo=:bu")
								.setParameter("code", code).setParameter("bu", bu).uniqueResult();
					}
				});
    }
    
    @SuppressWarnings({ "unchecked"})
	public ListOfValues findByCodeWithoutBU(final String className, final String code) {
		@SuppressWarnings({"rawtypes" })
		ListOfValues listOfValues = (ListOfValues) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Map<String, Object> params = new HashMap<String, Object>();
				session.disableFilter("bu_name");
				Query hbmQuery = session.createQuery("from " + className + " className where className.code = :code");
				params.put("code", code);
				hbmQuery.setProperties(params);
				hbmQuery.setMaxResults(1);
				Object object = hbmQuery.uniqueResult();
				session.enableFilter("bu_name");
				return object;
			}
		});
		return listOfValues;
	}
    
    @SuppressWarnings("unchecked")
	public List<ListOfValues> findByCodes(final String className, final Collection<String> collectionOfCodes)
    {
    	return (List<ListOfValues>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from "
												+ className
												+ " className where className.code in (:collectionOfCodes)")
								.setParameterList("collectionOfCodes",
										collectionOfCodes).list();
					}
				});
    }

    public List<ListOfValues> findByIds(String className, Collection<Long> ids){
		return repoMap.get(className).findByIds("code", ids);
    }

    public List<ListOfValues> findAll(String className){
    	return repoMap.get(className).findAll();
    }

    public PageResult<ListOfValues> findAll(String className, ListCriteria criteria){
    	String baseQuery = "from " + className;
    	return findPage(baseQuery, criteria);
    }

    /**
	 * @param listOfValues
	 * @return
	 */
	private GenericRepository<ListOfValues, Long> getRepository(ListOfValues listOfValues) {
		return repoMap.get(listOfValues.getClass().getSimpleName());
	}

	public void setClassMap(Map<String, Class> classMap) {
		this.classMap = classMap;
	}
	
	public Class getClassFromClassName(String className) {
		return classMap.get(className);
	}
	
	public List<ListOfValues> findAllActive(String className) {
		List<ListOfValues> lovs = new ArrayList<ListOfValues>();
        try {
            ListOfValues lov = (ListOfValues) getClassFromClassName(className).newInstance();            
            if (className.equals("DocumentType")) {
				lovs = findLOVsThatMatchPropertyType("state",lov);				
				
			} else {
				lovs = findLOVsThatMatchPropertyValues("state", lov);
			}
            Collections.sort(lovs);
        } catch (InstantiationException e) {
			logger.error("Error when fetching active values for LOV " + className, e);
		} catch (IllegalAccessException e) {
			logger.error("Error when fetching active values for LOV " + className, e);
		}
        return lovs;
	}
	
	
	@SuppressWarnings("unchecked")
    private List<ListOfValues> findLOVsThatMatchPropertyValues(final String property, final ListOfValues entity) {
        return (List<ListOfValues>)getHibernateTemplate().execute( new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteriaQuery = session.createCriteria(entity.getClass());
                criteriaQuery.add(Restrictions.eq(property,ListOfValues.ACTIVE_STATE));
                criteriaQuery.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                criteriaQuery.setCacheable(true);
                return criteriaQuery.list();
            }
            
        });
    }

	public ListOfValues findActiveValuesByCode(final String className, final String code) {
		return (ListOfValues) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from "
												+ className
												+ " className where className.code = :code and className.state=:state")
								.setParameter("code", code).setParameter("state", ListOfValues.ACTIVE_STATE).uniqueResult();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<ListOfValues> findLOVsThatMatchPropertyType(final String property, final ListOfValues entity) {		
		return (List<ListOfValues>)getHibernateTemplate().execute( new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Criteria criteriaQuery = session.createCriteria(entity.getClass());                
                criteriaQuery.add(Restrictions.eq("class", "DOCUMENTTYPE")); 
                criteriaQuery.add(Restrictions.eq(property,ListOfValues.ACTIVE_STATE));
                criteriaQuery.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                criteriaQuery.setCacheable(true);
                return criteriaQuery.list();
            }
		});
	}
}
	




