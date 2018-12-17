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
package tavant.twms.domain.query;

import java.util.Map;

import org.apache.log4j.Logger;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.businessobject.IBusinessObjectModel;
/**
 *
 * @author roopali.agrawal
 *
 */
public class HibernateGroupByQueryGenerator extends HibernateQueryGenerator{
	private static final Logger logger = Logger.getLogger(HibernateGroupByQueryGenerator.class);
	public HibernateGroupByQueryGenerator(String context){
		super(context);
	}

	@Override
    protected HibernateQuery getHibernateQuery(boolean isSubQuery) {
		HibernateQuery query=new HibernateQuery();
		//todo-temp
		IBusinessObjectModel model = BusinessObjectModelFactory.getInstance().getBusinessObjectModel(this.businessObjectContext);
		String alias = model.getTopLevelAlias();
		StringBuffer selectClause=new StringBuffer("select ");
		selectClause.append(alias+".id ");

		query.setSelectClause(selectClause.toString());
		query.setQueryWithoutSelect(getParameterizedQueryString());
		//todo
		query.setOrderByClause(null);
		if(logger.isInfoEnabled())
		{
		    logger.info("HQL query is "+query);
		}
		return query;
	}


	private String getParameterizedQueryString() {
		StringBuffer finalQuery = new StringBuffer();
		//todo-temp
		IBusinessObjectModel model = BusinessObjectModelFactory.getInstance().getBusinessObjectModel(this.businessObjectContext);
		String alias = model.getTopLevelAlias();

		finalQuery.append(" from ");
		finalQuery.append(model.getTopLevelTypeName());
		finalQuery.append(" ");
		finalQuery.append(alias);

		for (Map.Entry<String, String> join : this.entityJoins.entrySet()) {
			finalQuery.append(" join ");
			finalQuery.append(join.getKey());
			finalQuery.append(" ");
			finalQuery.append(join.getValue());
		}
		finalQuery.append(" GROUP BY "+alias+".id ");
		if (!("".equals(this.queryString.toString()))) {
			finalQuery.append(" HAVING ( ");
			finalQuery.append(this.queryString.toString());
			finalQuery.append(" ) ");
		}

		return finalQuery.toString();
	}



}
