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
package tavant.twms.security.context;

import org.acegisecurity.intercept.web.FilterInvocation;
import org.aopalliance.intercept.MethodInvocation;

/**
 * An interface to convert the runtime context into auth context.This interface
 * needs to be implemented by application developers.It needs to be implemented
 * only for resources that need instance based security.For simple class based
 * security this interface doesn't need to be implemented.
 * 
 */
public interface ResourceContextBuilder {
	/**
	 * Given a runtime context and resource it is responsible for transforming
	 * the runtimeContext into secured resource Context.secured resource context
	 * represents the attributes of the runtime context that are required to
	 * make the authorization decision
	 * <p>
	 * Secured resource context needs to be populated with all such attributes.
	 * For e.g. to make a decision related to generating a letter we require the
	 * department to which it is associated. So we need to extract this
	 * information from the runtime context. That may be the request
	 * {@linkplain FilterInvocation} or method invocation
	 * {@linkplain MethodInvocation} object. The builder would be knowing which
	 * parameter/attribute in request, or argument in method contains that
	 * information.
	 * 
	 * This is a resource specific transformation and this builder needs to be
	 * written for all different resources in the application.Different
	 * resources in the system can share same type of auth context objects.
	 * 
	 * @param resourceName -
	 *            Name of the resource that user is trying to operate upon.For
	 *            programmatic a resource is identified just by a name.Resource
	 *            name should be unique across application.
	 * 
	 * @param runtimeContext -
	 *            Represents the run time context.For HTTPRequests runtime
	 *            context contains HTTPrequest object as it is. For programatic
	 *            resource,any object can be passed as a runtime context.Since
	 *            this interface needs to be implemented for each resource, it
	 *            should have knowledge the kind of runtime context to expect
	 *            for that resource.
	 * 
	 * @return Object authContext - Represents a canonical representation of
	 *         runtime context.Contains all the necessary attributes that are
	 *         required to take authorization decision.
	 */
	Object createContext(String resourceName, Object runtimeContext);
}
