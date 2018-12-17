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
package tavant.twms.security.authz;

import java.util.List;

import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.vo.Resource;


public interface AuthorizationService {

    public static final String READ_ONLY_SUBJECT_AREA="readOnlyAccess";
    public static final String READ_ONLY_FUNCTIONAL_AREA="readOnlyAccess";

	/**
	 * Returns true if the User has permission
	 * on the specified Resource else false
	 *
	 * @param user client
	 * @param resource to be authorized
	 * @param instance data
	 * @return boolean
	 */
	public abstract boolean isAuthorized(User user, Resource resource, Object instanceData);
	
	/**
	 * 
	 * @param user
	 * @param functionalArea
	 * @param subjectArea
	 * @return boolean
	 */
	public boolean isAuthorized(User user, String functionalArea, String subjectArea);
	
	 public Resource getResource(String functionalArea, String subjectArea);
	
	public boolean isHavingReadOnlyAccessToTwms(User user);

}