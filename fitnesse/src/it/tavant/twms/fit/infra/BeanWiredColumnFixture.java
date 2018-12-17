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
package tavant.twms.fit.infra;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.model.OrgAwareUserDetails;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

import fit.ColumnFixture;

/**
 * @author vineeth.varghese
 * @date Oct 27, 2006
 */
public class BeanWiredColumnFixture extends ColumnFixture {

	static {
		Clock.setDefaultTimeZone(TimeZone.getDefault());
		Clock.timeSource();
	}

	public static final String DATE_PATTERN = "dd/mm/yyyy";

	public BeanWiredColumnFixture() {
		ApplicationContextHolder.getApplicationContextHolder()
				.autowireBeanProperties(this);
		populateTestUserCredentials();
	}

	protected CalendarDate getDate(String dateRepresentation) {
		return CalendarDate.from(dateRepresentation, DATE_PATTERN);
	}

	protected boolean hasText(String textValue) {
		if (textValue != null && !("".equals(textValue.trim()))) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected void populateTestUserCredentials() {
		GrantedAuthority[] authorities = null;
		String[] roles = new String[] { "dealer" };
		if ((roles == null) || (roles.length == 0)) {
			authorities = new GrantedAuthority[0];
		} else {
			authorities = new GrantedAuthority[roles.length];
			for (int i = 0; i < roles.length; i++) {
				authorities[i] = new GrantedAuthorityImpl(roles[i]);
			}
		}
		// Simulate a succesful user authentication.
		User orgUser = new User();
		orgUser.setName("bishop");
		orgUser.setPassword("tavant");
		orgUser.setId(new Long(1));
		Set<Role> userRoles = null;
		if (roles == null) {
			userRoles = Collections.EMPTY_SET;
		} else {
			userRoles = new HashSet<Role>(roles.length);
			for (String roleName : roles) {
				Role role = new Role();
				role.setName(roleName);
				userRoles.add(role);
			}
		}
		orgUser.setRoles(userRoles);
		OrgAwareUserDetails orgUserDetails = new OrgAwareUserDetails(orgUser);

		/*
		 * Note : We *have* to use this three-argument version of the
		 * constructor to set the authentication token as "trusted", there by
		 * preventing Acegi from attempting to actually authenticate it.
		 */
		final UsernamePasswordAuthenticationToken authenticationToken = 
			new UsernamePasswordAuthenticationToken(orgUserDetails, "tavant", authorities);

		SecurityContext securityContext = new SecurityContextImpl();
		securityContext.setAuthentication(authenticationToken);
		SecurityContextHolder.setContext(securityContext);
	}
}
