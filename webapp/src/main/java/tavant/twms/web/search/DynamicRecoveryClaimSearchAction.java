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
package tavant.twms.web.search;


/**
 *
 * @author roopali.agrawal
 *
 */
public class DynamicRecoveryClaimSearchAction extends RecoveryClaimSearchAction {
	private String contextName;


	public String getContextName() {
		return contextName;
	}


	public void setContextName(String contextName) {
		this.contextName = contextName;
	}



	public DynamicRecoveryClaimSearchAction(){
		super();
		contextName="RecoveryClaimSearches";
		//removeButton("viewInbox_jsp.inboxButton."+SHOW_SEARCH_PARAM);
    	//addDescriptionButton("show_search_query", "left", "show_search_query_title", "<table width=\"100%\"><tr><td>Foo</td><td>Bar</td></tr><tr><td colspan=\"2\">Baz</td></tr></table>");

	}

}
