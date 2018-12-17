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
package tavant.twms.domain.complaints;

import java.util.HashMap;
import java.util.Map;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class ComplaintsRepositoryImpl extends GenericRepositoryImpl<Complaint, Long> implements ComplaintsRepository{

    /*
     * (non-Javadoc)
     * 
     * @see tavant.twms.domain.complaints.ComplaintsRepository#save(tavant.twms.domain.complaints.Complaint)
     */	
	public void save(Complaint newComplaint) {
		getHibernateTemplate().save(newComplaint);		
	}

	
	// TODO : Need to refactor this method. Need to customise the GenericRepositoryImpl for the same.
	public PageResult<Complaint> fetchFieldReportsOrConsumerComplaintsByType(String type, final ListCriteria criteria) {		
        // criteria.addFilterCriteria(columnName, value)		
		String baseQuery = "from Complaint complaint where complaint.complaintType = :type "
            + getFilterCriteriaString(criteria);
    	//Map<String,Object> params = criteria.getParameterMap();
    	Map<String,Object> params = new HashMap<String, Object>();
    	params.put("type", type);
    	return findPageUsingQuery(baseQuery, criteria.getSortCriteriaString(), criteria.getPageSpecification(), params);
	}


	
	// TODO : need to remove all this crap.
	private String getFilterCriteriaString(ListCriteria criteria) {
        if (criteria.getFilterCriteria().size() > 0) {
            StringBuffer dynamicQuery = new StringBuffer();
            for (String expression : criteria.getFilterCriteria().keySet()) {
            	dynamicQuery.append("and ");
            	dynamicQuery.append(expression);
                dynamicQuery.append(" like ");
                dynamicQuery.append("'" + criteria.getFilterCriteria().get(expression) + "%' ");                
            }
            return dynamicQuery.toString();
        }
        return "";
    }	
}
