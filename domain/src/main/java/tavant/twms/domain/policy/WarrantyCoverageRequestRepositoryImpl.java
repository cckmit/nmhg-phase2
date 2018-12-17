package tavant.twms.domain.policy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class WarrantyCoverageRequestRepositoryImpl extends
		GenericRepositoryImpl<WarrantyCoverageRequest, Long> implements
		WarrantyCoverageRequestRepository {

	public WarrantyCoverageRequest findByInventoryItemId(Long id) { 
		String queryString = "select wcr from WarrantyCoverageRequest wcr,InventoryItem ii where ii.id = :inventoryItemId  and wcr.inventoryItem=ii";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("inventoryItemId", id);
		return findUniqueUsingQuery(queryString, map);
	}

	public PageResult<WarrantyCoverageRequest> findPageForAdminPendingRequests(
			ListCriteria listCriteria) {
		final String fromClause = "from WarrantyCoverageRequest wcr";
		StringBuilder fromAndWhereClause = new StringBuilder();
		fromAndWhereClause.append(fromClause).append(
				" where wcr.status in ('SUBMITTED','REPLIED') ");
		if (listCriteria.isFilterCriteriaSpecified()) {
			fromAndWhereClause.append(" AND  ").append(
					listCriteria.getParamterizedFilterCriteria());
		}
		final String orderByClause = listCriteria.getSortCriteriaString();
		Map<String, Object> parameterMap = listCriteria.getParameterMap();
		return findPageUsingQuery(fromAndWhereClause.toString(), orderByClause,
				listCriteria.getPageSpecification(), parameterMap);

	}

	public PageResult<WarrantyCoverageRequest> findPageForDealerRequests(
			ListCriteria listCriteria, User user, ServiceProvider dealership) {

		PageResult<WarrantyCoverageRequest> result = new PageResult<WarrantyCoverageRequest>(
				new ArrayList<WarrantyCoverageRequest>(),
				new PageSpecification(), 0);
		final String fromClause = "from WarrantyCoverageRequest wcr join wcr.audits as wcra ";
		StringBuilder fromAndWhereClause = new StringBuilder();
		fromAndWhereClause
				.append(fromClause)
				.append("where ").append(dealership!=null?"wcr.requestedBy= :dealer":"wcra.assignedTo=:dealer")
				.append(" and ")
				.append(" ((wcr.status  in ('WAITING_FOR_YOUR_RESPONSE','DENIED','FORWARDED','SUBMITTED','REPLIED' ) AND " +
						" wcra.status  in ('WAITING_FOR_YOUR_RESPONSE','DENIED','FORWARDED','SUBMITTED','REPLIED' ) "+
						" and wcra.id in ( select max(id) from WarrantyCoverageRequestAudit where id in wcra ) " +
						" and wcra.d.createdTime + 60 >sysdate)" +
						" or  (wcr.status  in ('APPROVED' ) AND " +
						" wcra.status  in ('APPROVED' ) ))  ");

		if (listCriteria.isFilterCriteriaSpecified()) {
			fromAndWhereClause.append(" AND  ").append(
					listCriteria.getParamterizedFilterCriteria());
		}
		final String orderByClause = listCriteria.getSortCriteriaString();
		Map<String, Object> parameterMap = listCriteria.getParameterMap();
		parameterMap.put("dealer", dealership!=null?dealership:user);

		final Map<String, Object> paramsMap = parameterMap;
		final StringBuffer countQuery = new StringBuffer().append(getExtensionCountForDealerQuery(user , dealership));
		if (listCriteria.isFilterCriteriaSpecified()) {
			countQuery.append(" AND  ").append(
					listCriteria.getParamterizedFilterCriteria());
		}
		
/*		countQuery.append(fromAndWhereClause);*/

		Long numberOfRows = (Long) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery(countQuery.toString());
						// query.setProperties(parameterMap);
						query.setProperties(paramsMap);
						return query.uniqueResult();

					}
				});

		if (numberOfRows.longValue() > 0) {
			String selectClause = "select wcr  ";
			StringBuilder finalClause = new StringBuilder();
			finalClause = finalClause.append(selectClause).append(
					fromAndWhereClause);

			if (orderByClause != null && orderByClause.trim().length() > 0) {
				finalClause.append(" order by ").append(orderByClause);
			}
			final String finalQuery = finalClause.toString();
			PageSpecification pg = listCriteria.getPageSpecification();
			final int offSet = pg.getPageNumber() * pg.getPageSize();
			final int pageSize = pg.getPageSize();

			List<WarrantyCoverageRequest> dbResult = (List<WarrantyCoverageRequest>) getHibernateTemplate()
					.execute(new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							Query query = session.createQuery(finalQuery);
							query.setFirstResult(offSet);
							query.setMaxResults(pageSize);
							query.setProperties(paramsMap);
							return query.list();

						}
					});
			int totalNoOfPages = pg.convertRowsToPages(numberOfRows);
			
			 Set<WarrantyCoverageRequest> setItems = new LinkedHashSet<WarrantyCoverageRequest>(dbResult);
			 dbResult.clear();
			 dbResult.addAll(setItems); 
			
			result = new PageResult<WarrantyCoverageRequest>(dbResult, pg,
					(int) totalNoOfPages);

		}

		return result;
	}

	public void save(WarrantyCoverageRequest entity) {
		getHibernateTemplate().save(entity);
	}

	public int findExtensionCountForDealer(User user, ServiceProvider dealership) {
		final String baseQuery = getExtensionCountForDealerQuery(user, dealership);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dealer", dealership!=null?dealership:user);
		final Map<String,Object> finalMap = params;
        Long count = (Long)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery(baseQuery);
                query.setProperties(finalMap);
                return query.uniqueResult();
            }
        });

		return count != null ? (int) count.longValue() : 0;
		
	}

	private String getExtensionCountForDealerQuery(User user, ServiceProvider dealership) {
		String baseQuery = "select count(*) from WarrantyCoverageRequest wcr where wcr.id in (select wcr.id from WarrantyCoverageRequest wcr join wcr.audits as wcra  "
				+ " where " ;
		baseQuery += dealership!=null?"wcr.requestedBy= :dealer":"wcra.assignedTo=:dealer";
				baseQuery += " and ((wcr.status in ('WAITING_FOR_YOUR_RESPONSE','DENIED','FORWARDED','SUBMITTED','REPLIED' )  "
				+ "and wcra.status in ('WAITING_FOR_YOUR_RESPONSE','DENIED','FORWARDED','SUBMITTED','REPLIED' )" +
					" and wcra.id in ( select max(id) from WarrantyCoverageRequestAudit where id in wcra ) " +
					" and wcra.d.createdTime + 60 > sysdate) " +
					" or wcr.status in ('APPROVED' )  "
				+ " and wcra.status in ('APPROVED' ))) ";
		return baseQuery;
	}

	public Long findExtensionForCoverageRequestsCount() {
         final String countQuery = "select count(*) from WarrantyCoverageRequest wcr where status in ('SUBMITTED','REPLIED')";
         final Map<String,Object>  params = new HashMap<String,Object>();
         Long count = (Long)getHibernateTemplate().execute(new HibernateCallback() {
             public Object doInHibernate(Session session) throws HibernateException, SQLException {
                 Query query = session.createQuery(countQuery);
                 query.setProperties(params);
                 return query.uniqueResult();
             }
         });

		return count;
	}

}
