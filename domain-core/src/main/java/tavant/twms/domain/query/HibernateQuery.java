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

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.style.ToStringCreator;

import tavant.twms.infra.TypedQueryParameter;

public class HibernateQuery {
	private String selectClause;
	private String queryWithoutSelect;
	private String orderByClause;
	private List<TypedQueryParameter> parameters=new ArrayList<TypedQueryParameter>();
	public String getOrderByClause() {
		return orderByClause;
	}
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}
	public List<TypedQueryParameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<TypedQueryParameter> parameters) {
		this.parameters = parameters;
	}
	public String getQueryWithoutSelect() {
		return queryWithoutSelect;
	}
	public void setQueryWithoutSelect(String queryWithoutSelect) {
		this.queryWithoutSelect = queryWithoutSelect;
	}
	public String getSelectClause() {
		return selectClause;
	}
	public void setSelectClause(String selectClause) {
		this.selectClause = selectClause;
	}
	
	public String toString() {
        StringBuffer buffer=new StringBuffer("HQL[");
        buffer.append(selectClause);
        buffer.append(queryWithoutSelect);
        buffer.append("] Parameters[");
        for(Object param:parameters)
        {
        	buffer.append(param);
        	buffer.append(",");
        }
        buffer.append("]");
        return buffer.toString();
    }
	
	
	

}
