package tavant.twms.domain.catalog;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@SuppressWarnings("unchecked")
public class MiscellaneousItemConfigRepositoryImpl extends
		GenericRepositoryImpl implements MiscellaneousItemConfigRepository {

	private CriteriaHelper criteriaHelper;
	
	public void createMiscItem(MiscellaneousItem miscellaneousItem){
		getHibernateTemplate().save(miscellaneousItem);
	}
	
	public boolean findIfMiscellaneousPartExists(String name){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", name);		
		final String sql = "select count(*) from MiscellaneousItem misc where upper(misc.partNumber) = :name"; 

		final Map<String,Object> params = map;
		 Long count = (Long)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
                      Query query = session.createQuery(sql);
                      query.setProperties(params);
                      return query.uniqueResult();				
			}
		 });
		 
		 return count != null && count.longValue() > 0 ? true : false;
	}

	public boolean isDataForServiceProviderConfigured(ServiceProvider serviceProvider, List<MiscellaneousItem> miscItems) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("serviceProvider", serviceProvider);
		params.put("miscItems", miscItems);
		final String sql = "select count(*) from MiscellaneousItemCriteria mic join mic.itemConfigs miscItemConfigs " +
				"where miscItemConfigs.miscellaneousItem in (:miscItems) and " +
				"mic.serviceProvider = :serviceProvider and mic.active = true";
		final Map<String,Object> parameters = params;
		Long count = (Long)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(sql);
				query.setProperties(parameters);
				return query.uniqueResult();
			}			
		});
		return count != null && count.longValue() > 0 ? true : false; 
	}

	public boolean isDataForDealerGroupConfigured(DealerGroup dealerGroup, List<MiscellaneousItem> miscItems) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("dealerGroup", dealerGroup);
		params.put("miscItems", miscItems);
		final String sql = "select count(*) from MiscellaneousItemCriteria mic join mic.itemConfigs miscItemConfigs " +
						   "where miscItemConfigs.miscellaneousItem in (:miscItems) and " +
						   "mic.dealerGroup = :dealerGroup and mic.active = true";
		final Map<String,Object> parameters = params;
		Long count = (Long)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(sql);
				query.setProperties(parameters);
				return query.uniqueResult();
			}
			
		});
		return count != null && count.longValue() > 0 ? true : false;
	}
	
	public boolean isMiscItemConfiguredForAll(List<MiscellaneousItem> miscItems) {
		Map<String,Object> params = new HashMap<String,Object>();		
		params.put("miscItems", miscItems);
		final String sql = "select count(*) from MiscellaneousItemCriteria mic join mic.itemConfigs miscItemConfigs " +
				"where miscItemConfigs.miscellaneousItem in (:miscItems) and mic.active = true";
		final Map<String,Object> parameters = params;
		Long count = (Long)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(sql);
				query.setProperties(parameters);
				return query.uniqueResult();
			}			
		});
		return count != null && count.longValue() > 0 ? true : false; 
	}

	public boolean findIfConfigurationWithSameNameExists(String name) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", name);		
		final String sql = "select count(*) from MiscellaneousItemCriteria misc where upper(misc.configName) = :name"; 

		final Map<String,Object> params = map;
		 Long count = (Long)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
                      Query query = session.createQuery(sql);
                      query.setProperties(params);
                      return query.uniqueResult();				
			}
		 });
		 
		 return count != null && count.longValue() > 0 ? true : false;
	}

	public MiscellaneousItem findMiscellaneousItemById(Long id) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", id);		
		final String sql = "select miscItem from MiscellaneousItem miscItem where id = :id"; 
         
		final Map<String,Object> params = map;
		 return (MiscellaneousItem)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
                      Query query = session.createQuery(sql);
                      query.setProperties(params);
                      return query.uniqueResult();				
			}
		 });
   }

	public List<String> findAllPartNumbersStartingWith(String prefix) {
		final String queryString = "select mItem.partNumber from MiscellaneousItem mItem "
				+ "where upper(mItem.partNumber) like :prefix order by mItem.partNumber ";		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("prefix", prefix.toUpperCase() + "%");
		final Map<String, Object> parameters = params; 
		 return (List<String>)getHibernateTemplate().execute(new HibernateCallback(){
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
	                      Query query = session.createQuery(queryString);
	                      query.setProperties(parameters);
	                      return query.list();				
				}
			 });
	}

	public MiscellaneousItem findMiscellaneousItemByPartNumber(String partNumber) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partNumber", partNumber);		
		final String sql = "select miscItem from MiscellaneousItem miscItem where partNumber = :partNumber";          
		final Map<String,Object> params = map;
		 return (MiscellaneousItem)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
                      Query query = session.createQuery(sql);
                      query.setProperties(params);
                      return query.uniqueResult();				
			}
		 });
	}

	public List<MiscellaneousItemConfiguration> findAllMiscellaneousItemConfigs() {

		final String sql = " from MiscellaneousItemConfiguration miscItem ";          		
		 return (List<MiscellaneousItemConfiguration>)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
                      Query query = session.createQuery(sql);                      
                      return query.list();				
			}
		 });
		
	
	}

	public MiscellaneousItemCriteria findForDealerGroup(Long dealerGroupId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("dealerGroupId", dealerGroupId);
		final String sql = "select mic from MiscellaneousItemCriteria mic " +
				"where dealerGroup.id = :dealerGroupId and active = true";
		
		final Map<String,Object> parameters = params;
		return (MiscellaneousItemCriteria)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(sql);
				query.setProperties(parameters);
				return query.uniqueResult();
			}
			
		});
	}
	
	public List<MiscellaneousItem> findMiscellanousPartForDealer(Long dealerId, String partNumberSearchPrefix) {
		Map<String,Object> params = new HashMap<String,Object>();			
		final String sql = "select distinct miscItemConfigs.miscellaneousItem from MiscellaneousItemCriteria mic " +
				" join mic.itemConfigs miscItemConfigs " +
				" left outer join mic.dealerGroup specifiedDealerGroup," +
				" DealerGroup dealerGroup join dealerGroup.includedDealers dealerInGroup " +
				" where (mic.serviceProvider is null or mic.serviceProvider.id = :serviceProviderId ) and " +
				" ( specifiedDealerGroup is null  or " +
				" (  specifiedDealerGroup.nodeInfo.treeId=dealerGroup.nodeInfo.treeId and " + 
				" specifiedDealerGroup.nodeInfo.lft <= dealerGroup.nodeInfo.lft and " +
				" dealerGroup.nodeInfo.rgt <= specifiedDealerGroup.nodeInfo.rgt and " +
				" :serviceProviderId = dealerInGroup "+ 
				" ) ) " + 
				"  and upper(miscItemConfigs.miscellaneousItem.partNumber) like :miscPartNumber and mic.active = true";	
		final Map<String,Object> parameters = params;
		params.put("serviceProviderId", dealerId);
		params.put("miscPartNumber", partNumberSearchPrefix.toUpperCase() + "%");
		return (List<MiscellaneousItem>)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(sql);
				query.setProperties(parameters);
				return query.list();
			}			
		});
	}
	
	public List<MiscellaneousItem> findMiscellanousParts(String partNumberSearchPrefix){
		final Map<String,Object> params = new HashMap<String,Object>();
		final String sql="select distinct miscItem from MiscellaneousItemCriteria mic join mic.itemConfigs miscItemConfigs " +
			"join miscItemConfigs.miscellaneousItem miscItem where lower(miscItem.partNumber) like :miscPartNumber and mic.active = true";	
		params.put("miscPartNumber", partNumberSearchPrefix.toLowerCase() + "%");
		return (List<MiscellaneousItem>)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(sql);
				query.setProperties(params);
				return query.list();
			}			
		});
	}
	
	public PageResult<MiscellaneousItemCriteria> findAllConfigurations(String miscPartsConfigSelectQuery, ListCriteria listCriteria) {
		return findPage(miscPartsConfigSelectQuery, listCriteria);
	}

	public PageResult<MiscellaneousItem> fetchAllMiscellaneousPart(ListCriteria listCriteria) {
		return findPage("from MiscellaneousItem miscItem", listCriteria);
	}

	public MiscellaneousItemCriteria findMiscPartConfigById(final Long miscPartConfigId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", miscPartConfigId);		
		final String sql = "select miscItemCriteria from MiscellaneousItemCriteria miscItemCriteria where id = :id";          
		final Map<String,Object> params = map;
		 return (MiscellaneousItemCriteria) getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
                      Query query = session.createQuery(sql);
                      query.setProperties(params);
                      return query.uniqueResult();				
			}
		 });
	}

	public CriteriaHelper getCriteriaHelper() {
		return criteriaHelper;
	}

	public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
		this.criteriaHelper = criteriaHelper;
	}
	
	public MiscellaneousItemConfiguration findMiscellanousPartConfigurationForDealerAndMiscPart(final Long dealerId,final String miscPartNumber){
		MiscellaneousItemConfiguration miscItemConfig = (MiscellaneousItemConfiguration) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException,
                            SQLException {
                        Query hbmQuery = session.getNamedQuery("miscPartConfigForDealerAndPart");
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("serviceProviderId",dealerId);
                        params.put("miscPartNumber",miscPartNumber);
                        hbmQuery.setProperties(params);
                        hbmQuery.setMaxResults(1);
                        return hbmQuery.uniqueResult();
                    }
                });
        return miscItemConfig;
	}
	
	public MiscellaneousItemConfiguration findMiscellanousPartConfigurationForMiscPart(final String miscPartNumber){
		MiscellaneousItemConfiguration miscItemConfig = (MiscellaneousItemConfiguration) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException,
                            SQLException {
                        Query hbmQuery = session.getNamedQuery("miscPartConfigForPart");
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("miscPartNumber",miscPartNumber);
                        hbmQuery.setProperties(params);
                        hbmQuery.setMaxResults(1);
                        return hbmQuery.uniqueResult();
                    }
                });
        return miscItemConfig;
		
	}
}
