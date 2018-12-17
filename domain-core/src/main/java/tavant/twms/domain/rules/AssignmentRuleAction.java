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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import tavant.twms.domain.loa.LimitOfAuthorityScheme;
import tavant.twms.domain.orgmodel.UserCluster;

@Entity
public class AssignmentRuleAction extends DomainRuleAction {

	@ManyToOne(fetch = FetchType.LAZY) // assign to & not assign to userCluster1
	private UserCluster userCluster;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private LimitOfAuthorityScheme loaScheme;

	public AssignmentRuleAction() {
		super();
	}
	
	public AssignmentRuleAction(UserCluster userCluster, String context) {
		this.userCluster = userCluster;		
		this.context = context;
	}
	
	public AssignmentRuleAction(String context,LimitOfAuthorityScheme loaScheme) {
        this.context = context;
        this.loaScheme = loaScheme;
    }

	 //TODO: setting the user cluster name for the name field.
	 // Is this required. ?
	public void setUserCluster(UserCluster userCluster) {
		this.userCluster = userCluster;
		this.name = userCluster.getName(); 
										  
	}

	public UserCluster getUserCluster() {
		return userCluster;
	}
	
	public LimitOfAuthorityScheme getLoaScheme() {
		return loaScheme;
	}

	public void setLoaScheme(LimitOfAuthorityScheme loaScheme) {
		this.loaScheme = loaScheme;
	}
}
