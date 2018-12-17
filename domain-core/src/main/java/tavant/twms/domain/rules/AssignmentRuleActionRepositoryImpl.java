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
package tavant.twms.domain.rules;

import java.util.HashMap;
import java.util.Map;

import tavant.twms.infra.GenericRepositoryImpl;

public class AssignmentRuleActionRepositoryImpl 
		extends GenericRepositoryImpl<AssignmentRuleAction, Long>
		implements AssignmentRuleActionRepository {

    public AssignmentRuleAction findAssignmentRuleActionByUserGroupNameAndState(String userGroupName, String result) {
		String query = "select ara from AssignmentRuleAction ara where ara.userCluster.name =:userGroupName " +
        			"and ara.state =:result";
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("userGroupName",userGroupName);
        params.put("result",result);
        return findUniqueUsingQuery(query, params);    	
    }
    
    public AssignmentRuleAction findAssignmentRuleActionByUserGroupNameAndState(String userGroupName, String result, String context){
		String query = "select ara from AssignmentRuleAction ara where ara.userCluster.name =:userGroupName " +
				"and ara.state =:result and ara.context=:context";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userGroupName",userGroupName);
		params.put("result",result);
		params.put("context",context);
		return findUniqueUsingQuery(query, params);    	
    }
    
    public AssignmentRuleAction findAssignmentRuleActionByLOASchemeStateAndContext(String loaScheme, String result,String context){
		    	
	   	String query = "select ara from AssignmentRuleAction ara where ara.loaScheme.name =:loaSchemeName " +
				"and ara.state =:result and ara.context=:context";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("loaSchemeName",loaScheme);
		params.put("result",result);
		params.put("context",context);
		return findUniqueUsingQuery(query, params);    	
	}
    
}
