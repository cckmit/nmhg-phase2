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
package tavant.twms.security.policy;


import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.vo.Resource;



/**
 * An executor is associated with a function. Whenever any authorization related
 * decision is to be taken then the framework would call the registered
 * executor against that function for taking the final access related decision.
 * <p>
 * Implementing classes have to be registered with the framework through a
 * mapping file
 * 
 * @version 1.0, 07/17/2007
 */
public interface PolicyExecutor {
	/**
	 * Called by the Security framework to decide whether the user is allowed
	 * to access the secured resource.
	 * 
	 * @param resource
	 *            to be protected.
	 * 
	 * @param userProfile
	 *            user logged in
	 * @param instanceData
	 *            The Secured Object Context which is built by the
	 *            {@linkplain ResourceContextBuilder}
	 * @return boolean permission
	 */
	public boolean execute(Resource resource, User user, Object instanceData);
}
