/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.orgmodel;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.Constants;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class DealerGroupRepositoryImpl extends
		GenericRepositoryImpl<DealerGroup, Long> implements
		DealerGroupRepository {

	public DealerGroup findDealerGroupByName(String name,
			DealerScheme dealerScheme){
		String query = "select ic from DealerGroup ic where ic.scheme =:scheme and ic.name=:name";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("scheme", dealerScheme);
		params.put("name", name);
		return findUniqueUsingQuery(query, params);
	}
	
	public DealerGroup findDealerGroupByCode(String code,
			DealerScheme dealerScheme) {
		String query = "select ic from DealerGroup ic where ic.scheme =:scheme and ic.code=:code";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("scheme", dealerScheme);
		params.put("code", code);
		return findUniqueUsingQuery(query, params);
	}

	public List<DealerGroup> findDealerGroupsFromScheme(
			DealerScheme dealerScheme){
		String query = "select ic from DealerGroup ic where ic.scheme=:scheme";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("scheme", dealerScheme);
		return findUsingQuery(query, params);
	}

	public DealerGroup findGroupContainingDealership(ServiceProvider dealership,
			DealerScheme scheme){
		String query = "select ic from DealerGroup ic join ic.includedDealers as dealership where dealership=:aDealership and ic.scheme=:scheme ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("scheme", scheme);
		params.put("aDealership", dealership);
		DealerGroup dealerGroup = findUniqueUsingQuery(query, params);
		/*if(dealerGroup == null){
			throw new DealerGroupNotFoundException("Dealer '" + dealership.getName() +
					"' does not belong to any group with scheme " + scheme.getName());
		}*/
		return dealerGroup;
	}
	
	public DealerGroup findGroupContainingServiceProviders(ServiceProvider serviceProvider,
			DealerScheme scheme){
		String query = "select ic from DealerGroup ic join ic.includedDealers as serviceProvider where serviceProvider=:aServideProvider and ic.scheme=:scheme ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("scheme", scheme);
		params.put("aServideProvider", serviceProvider);
		DealerGroup dealerGroup = findUniqueUsingQuery(query, params); 
		/*if(dealerGroup == null){
			throw new DealerGroupNotFoundException("Dealer '" + dealership.getName() + 
					"' does not belong to any group with scheme " + scheme.getName());
		}*/
		return dealerGroup;
	}

	public DealerGroup findGroupContainingDealership(ServiceProvider dealership, String purpose,
			BusinessUnitInfo businessUnitInfo) {
		String query = "select ic from DealerGroup ic join ic.includedDealers as dealership where dealership=:aDealership and ic.scheme = (select dealerScheme from DealerScheme dealerScheme join dealerScheme.purposes as purpose "
				+ " where purpose.name=:name and dealerScheme.businessUnitInfo = :businessUnitInfo ) ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", purpose);
		params.put("aDealership", dealership);
		params.put("businessUnitInfo", businessUnitInfo.getName());

		DealerGroup dealerGroup = findUniqueUsingQuery(query, params);
		return dealerGroup;
	}	

	public List<DealerGroup> findGroupsByNameAndDescription(
			DealerScheme scheme, String name, String description){
		String query = "select ic from DealerGroup ic where ic.scheme=:scheme and upper(ic.name) like :nameParam and upper(ic.description) like :descriptionParam order by ic.name";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("scheme", scheme);
		params.put("nameParam", name.toUpperCase() + "%");
		params.put("descriptionParam", description.toUpperCase() + "%");
		return findUsingQuery(query, params);
	}

	@SuppressWarnings("unchecked")
	public PageResult<DealerGroup> findPage(final ListCriteria listCriteria,
			final DealerScheme dealerScheme) {
		PageSpecification pageSpecification = listCriteria
				.getPageSpecification();
		final StringBuffer countQuery = new StringBuffer(" select count(*) ");

		final StringBuffer fromAndWhereClause = new StringBuffer();
		final String fromClause = "from DealerGroup dealerGroup where dealerGroup.scheme=:scheme";
		fromAndWhereClause.append(fromClause);

		if (listCriteria.isFilterCriteriaSpecified()) {
			fromAndWhereClause.append(" and ");
			fromAndWhereClause.append(listCriteria
					.getParamterizedFilterCriteria());
		}

		countQuery.append(fromAndWhereClause);

		if (logger.isDebugEnabled()) {
			logger.debug("findPage(" + fromClause
					+ ",listCriteria) count query is [" + countQuery + "]");
		}

		Long numberOfRows = (Long) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery(countQuery.toString());
						query.setParameter("scheme", dealerScheme);
						for (Map.Entry<String, Object> parameterSpecification : listCriteria
								.getParameterMap().entrySet()) {
							String name = parameterSpecification.getKey();
							Object value = parameterSpecification.getValue();
							query.setParameter(name, value);
						}
						return query.uniqueResult();
					}
				});
		Integer numberOfPages = pageSpecification
				.convertRowsToPages(numberOfRows);

		List<DealerGroup> rowsInPage = new ArrayList<DealerGroup>();
		PageResult<DealerGroup> page = new PageResult<DealerGroup>(rowsInPage,
				pageSpecification, numberOfPages);

		if (logger.isDebugEnabled()) {
			logger.debug(" fetchPage(" + pageSpecification
					+ ",...,...) found (rows=" + numberOfRows + ",pages="
					+ numberOfPages + ")");
		}

		final Integer pageOffset = pageSpecification.offSet();
		if (numberOfRows > 0 && numberOfRows > pageOffset) {

			final Integer pageSize = pageSpecification.getPageSize();
			rowsInPage = (List<DealerGroup>) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							StringBuffer filterAndSort = new StringBuffer(
									fromAndWhereClause);

							if (listCriteria.isSortCriteriaSpecified()) {
								filterAndSort.append(" order by ");
								filterAndSort.append(listCriteria
										.getSortCriteriaString());
							}

							if (logger.isDebugEnabled()) {
								logger.debug(" Unpaginated query for findPage("
										+ fromClause + ",listCriteria) [ "
										+ filterAndSort + " ]");
							}

							Query query = session.createQuery(filterAndSort
									.toString());
							for (Map.Entry<String, Object> parameterSpecification : listCriteria
									.getParameterMap().entrySet()) {
								String name = parameterSpecification.getKey();
								Object value = parameterSpecification
										.getValue();
								query.setParameter(name, value);
							}

							query.setParameter("scheme", dealerScheme);
							return query.setFirstResult(pageOffset)
									.setMaxResults(pageSize).list();
						}
					});
			page = new PageResult<DealerGroup>(rowsInPage, pageSpecification,
					numberOfPages);
		}
		return page;
	}

	public DealerGroup findByNameAndPurpose(String name, String purpose){
		String query = "select dg from DealerGroup dg where upper(dg.name) =:name and dg.scheme in  (select dealerScheme from DealerScheme dealerScheme join dealerScheme.purposes as purpose "
				+ " where purpose.name=:purpose)";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name.toUpperCase());
		params.put("purpose", purpose);
		return findUniqueUsingQuery(query, params);
	}
	
	public List<DealerGroup> findByNamesAndPurpose(List<String> names, String purpose){
		String query = "select dg from DealerGroup dg where upper(dg.name)in (:names) and dg.scheme in  (select dealerScheme from DealerScheme dealerScheme join dealerScheme.purposes as purpose "
				+ " where purpose.name='"+purpose+"')";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("names", names);
		//params.put("purpose", purpose);
		return findUsingQuery(query, params);

	}


        public List<DealerGroup> findAllGroupsForOrganisationHierarchy() {
            return findGroupsForOrganisationHierarchy("");
        }

	public List<DealerGroup> findGroupsForOrganisationHierarchy(String name){

		String query = "select dg from DealerGroup dg where upper(dg.name) " +
                        "like :name and dg.scheme in (" +
                        "select dealerScheme from DealerScheme dealerScheme " +
                        "join dealerScheme.purposes as purpose where " +
                        "purpose.name=:purpose) order by dg.name";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name.toUpperCase() + "%");
		params.put("purpose", AdminConstants.ORGANISTAION_HIERARCHY_PURPOSE);
		return findUsingQuery(query, params);
	}

	@SuppressWarnings("unchecked")
	public List<String> findGroupsWithNameStartingWith(final String name,
			final PageSpecification pageSpecification, final String purpose) {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select dealerGroup.name from DealerGroup dealerGroup where upper(dealerGroup.name) like :name and dealerGroup.scheme in (select dealerScheme from DealerScheme dealerScheme join dealerScheme.purposes as purpose "
												+ " where purpose.name=:purpose)")
								.setParameter("name", name.toUpperCase() + "%").setParameter(
										"purpose", purpose).setFirstResult(
										pageSpecification.getPageSize()
												* pageSpecification
														.getPageNumber())
								.setMaxResults(pageSpecification.getPageSize())
								.list();
					};
				});
	}

	@SuppressWarnings("unchecked")
	public List<DealerGroup> findDealerGroupsWithNameLike(final String name,
			final PageSpecification pageSpecification, final String purpose) {
		return (List<DealerGroup>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery( "select dg from DealerGroup dg " +
										" where upper(dg.name) like :name " +
										" and dg.scheme in (select " +
										" dealerScheme from DealerScheme dealerScheme " +
										" join dealerScheme.purposes as purpose " +
										" where purpose.name=:purpose)")
										.setParameter("name", name.toUpperCase() + "%")
										.setParameter("purpose", purpose)
										.setFirstResult(
										pageSpecification.getPageSize()
												* pageSpecification
														.getPageNumber())
								.setMaxResults(pageSpecification.getPageSize())
								.list();
					};
				});
	}

	public DealerGroup findDealerGroupsForWatchedDealership(ServiceProvider dealer){
		String query = "select dg from DealerGroup dg join dg.scheme scheme join scheme.purposes purpose join  "
				+ " dg.includedDealers as dealer where "
				+ " purpose.name = :purpose and dealer = :dealer";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("purpose", AdminConstants.DEALER_WATCHLIST);
		params.put("dealer", dealer);
		return findUniqueUsingQuery(query, params);
	}
	
	public DealerGroup isDealerInTerritoryExclusion(ServiceProvider dealer){
		String query = "select dg from DealerGroup dg join dg.scheme scheme join scheme.purposes purpose join  "
				+ " dg.includedDealers as dealer where "
				+ " purpose.name = :purpose and dealer = :dealer";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("purpose", AdminConstants.TERRITORY_EXCLUSION);
		params.put("dealer", dealer);
		return findUniqueUsingQuery(query, params);
	}

	public List<DealerGroup> findDealerGroupsByPurposes(List<String> purposes){
		String query = "select distinct dg from DealerGroup dg join dg.scheme scheme join scheme.purposes purpose "
				+ " where "
				+ " purpose.name in (:purposes)"
				+ " order by dg.name asc ";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("purposes", purposes);
		return findUsingQuery(query, params);
	}

	public boolean isDealerGroupExistByNameAndDealer(String dealerGroupName, ServiceProvider dealer) {
		
		String queryStr = "select count(distinct dgParent) from DealerGroup dgParent, DealerGroup dgChild join dgChild.includedDealers as dealer  "
			+ " where dgParent.name = :dealerGrpNameParam and dgParent.nodeInfo.treeId=dgChild.nodeInfo.treeId and " +
              " dgParent.nodeInfo.lft <= dgChild.nodeInfo.lft and " +
              " dgChild.nodeInfo.rgt <= dgParent.nodeInfo.rgt and "
			+ " dealer = :dealerParam";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("dealerParam", dealer);
		params.put("dealerGrpNameParam",dealerGroupName);
		Query query = getSession().createQuery(queryStr);
        query.setProperties(params);
        Long countOfRows = (Long)query.uniqueResult();
		if(countOfRows == 0)
			return false;
		else
			return true;
	}

    @SuppressWarnings({"unchecked"})
    public List<ServiceProvider> findProvidersAtAllLevelForGroup(final String groupName) {
        return (List<ServiceProvider>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                return session.createQuery("select dgChild.includedDealers from " +
                        "DealerGroup dgParent, DealerGroup dgChild where " +
                        "dgParent.name = :groupName and " +
                        "dgParent.nodeInfo.treeId=dgChild.nodeInfo.treeId and " +
                        "dgParent.nodeInfo.lft <= dgChild.nodeInfo.lft and " +
                        "dgChild.nodeInfo.rgt <= dgParent.nodeInfo.rgt")
                        .setParameter("groupName", groupName)
                        .list();
            }
        });
    }
    
    
    @SuppressWarnings({"unchecked"})
    public List<ServiceProvider> findProvidersAtAllLevelForGroupByGroupId(final Long id) {
        return (List<ServiceProvider>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                return session.createQuery("select dgChild.includedDealers from " +
                        "DealerGroup dgParent, DealerGroup dgChild where " +
                        "dgParent.id = :id and " +
                        "dgParent.nodeInfo.treeId=dgChild.nodeInfo.treeId and " +
                        "dgParent.nodeInfo.lft <= dgChild.nodeInfo.lft and " +
                        "dgChild.nodeInfo.rgt <= dgParent.nodeInfo.rgt")
                        .setLong("id", id)
                        .list();
            }
        });
    }

	
}
