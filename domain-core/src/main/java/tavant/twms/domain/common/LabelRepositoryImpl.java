/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.domain.common;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.naming.ldap.PagedResultsControl;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.StringUtils;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class LabelRepositoryImpl extends GenericRepositoryImpl<Label, String> implements LabelRepository {

	@SuppressWarnings("unchecked")
	public List<String> findLabelsWithNameLike(final String label, final int pageNumber, final int pageSize) {
		final List<String> labelNames = new ArrayList<String>();
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria labelCriteria = session.createCriteria(Label.class);
						if(StringUtils.hasText(label)){
							labelCriteria.add(Restrictions.ilike("name", label, MatchMode.START));
						}
						labelCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);						
						for (Object label : labelCriteria.list()) {
							labelNames.add(((Label)label).getName());
						}
						return labelNames;
					};
				});		
	}
	
	@SuppressWarnings("unchecked")
	public List<String> findLabelsWithNameAndTypeLike(final String label, final String labelType, final int pageNumber, final int pageSize) {
		final List<String> labelNames = new ArrayList<String>();
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria labelCriteria = session.createCriteria(Label.class);
						if(StringUtils.hasText(label)){
							labelCriteria.add(Restrictions.ilike("name", label, MatchMode.START));
							labelCriteria.add(Restrictions.ilike("type", labelType, MatchMode.START));
						}
						labelCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);						
						for (Object label : labelCriteria.list()) {
							labelNames.add(((Label)label).getName());
						}
						return labelNames;
					};
				});		
	}
	
	@SuppressWarnings("unchecked")
	public Label findLabelWithName(final String label) {		
		return (Label) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria labelCriteria = session.createCriteria(Label.class);
						if(StringUtils.hasText(label)){
							labelCriteria.add(Restrictions.ilike("name", label, MatchMode.EXACT));
						}
						labelCriteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
						return labelCriteria.uniqueResult();
					};
				});		
	}

    public PageResult<?> findAllSupplierLabels(final ListCriteria listCriteria) {
        return findPageUsingQuery(Label.SUPPLIER, listCriteria);
    }

    public PageResult<?> findAllPolicyDefintionLabels(final ListCriteria listCriteria) {
        return findPageUsingQuery(Label.POLICY, listCriteria);
    }
    
    public PageResult<?> findAllCampaignLabels(final ListCriteria listCriteria) {
        return findPageUsingQuery(Label.CAMPAIGN, listCriteria);
    }
    public PageResult<?> findAllFaultCodeDefinitionLabels(final ListCriteria listCriteria) {
        return findPageUsingQuery(Label.FAULT_CODE_DEFINITION, listCriteria);
    }

    public PageResult<?> findAllJobCodeDefinitionLabels(final ListCriteria listCriteria) {
        return findPageUsingQuery(Label.SERVICE_PROCEDURE_DEFINITION, listCriteria);
    }

    public PageResult<?> findAllInventoryLabels(final ListCriteria listCriteria) {
        return findPageUsingQuery(Label.INVENTORY, listCriteria);
    }

    public PageResult<?> findAllModelLabels(final ListCriteria listCriteria) {
        return findPageUsingQuery(Label.MODEL, listCriteria);
    }
    
    public PageResult<?> findAllWarehouseLabels(final ListCriteria listCriteria) {
        return findPageUsingQuery(Label.WAREHOUSE, listCriteria);
    }

    public PageResult<?> findPageUsingQuery(final String fromClause, final String orderByClause,
			final String selectClause,PageSpecification pageSpecification,
			final String distinctClause,final ListCriteria listCriteria){
		 final StringBuffer fromAndWhereClause = new StringBuffer(fromClause);
		 if( listCriteria.isFilterCriteriaSpecified() ) {
            fromAndWhereClause.append(" where ");
            String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
			fromAndWhereClause.append(paramterizedFilterCriteria);
        }
        final String queryWithoutSelect = fromAndWhereClause.toString();        
        final QueryParameters parameters = new QueryParameters();
        final Map<String, Object> parameterMap = listCriteria.getParameterMap();
        parameters.setNamedParameters(parameterMap);
		return findPageUsingQueryForDistinctItems(queryWithoutSelect, orderByClause, selectClause, pageSpecification, parameters, distinctClause);
		
	}

    public List<Label> findLabelsForType(final String type) {
        return (List<Label>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session
                                .createQuery(
                                        "from Label where type = :type order by name asc")
                                .setParameter("type", type).list();
                    }
                });
    }

    public PageResult<?> findPageUsingQuery(String type,ListCriteria listCriteria) {
        PageSpecification pageSpecification = listCriteria.getPageSpecification();
        StringBuffer queryWithoutSelect = new StringBuffer("  from Label label where label.type = :type");
        if (listCriteria.isFilterCriteriaSpecified()) {
            queryWithoutSelect.append(" and ");
            String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
            queryWithoutSelect.append(paramterizedFilterCriteria);
        }
        final String queryWithWhereClause = queryWithoutSelect.toString();
        final QueryParameters parameters = new QueryParameters();
        final String sortClause = listCriteria.getSortCriteriaString();
        final Map<String, Object> parameterMap = listCriteria.getParameterMap();
        parameterMap.put("type", type);
        parameters.setNamedParameters(parameterMap);
        return findPageUsingQueryForDistinctItems(queryWithWhereClause, sortClause,
                "select distinct(label)", pageSpecification, parameters, "distinct label");

    }

    public PageResult<?> findAllFleetInventoryLabels(final ListCriteria listCriteria) {
        return findPageUsingQuery(Label.FLEET_INVENTORY, listCriteria);
    }

	public PageResult<?> findAllContractLabels(ListCriteria listCriteria) {
		 return findPageUsingQuery(Label.CONTRACT, listCriteria);
	}
	
    public PageResult<?> findAllFleetCustomerLabels(ListCriteria listCriteria) {
        return findPageUsingQuery(Label.FLEET_CUSTOMER, listCriteria);
    }
	
}


