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
 *
 */
package tavant.twms.integration.layer.authentication;

import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.log4j.Logger;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.fault.XFireFault;
import org.codehaus.xfire.handler.AbstractHandler;
import org.jdom.Element;
import org.jdom.Namespace;

public class AuthenticationHandler extends AbstractHandler {

	private static final Logger logger = Logger
			.getLogger(AuthenticationHandler.class);

	private final String TOKEN_NS = "http://parts.layer.integration.twms.tavant";

	private AuthenticationManager authenticationManager;

	public void setAuthenticationManager(
			AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public void invoke(MessageContext context) throws Exception {
		try {
			Element header = context.getInMessage().getHeader();
			if (header != null) {
				Namespace ns = Namespace.getNamespace(TOKEN_NS);
				Element token = header.getChild("AuthenticationToken", ns);
				if (token != null) {
					String authString = token.getText();
					String username = authString.substring(0, authString
							.indexOf("$"));
					String password = authString.substring(authString
							.indexOf("$") + 1);
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
							username, password);
					authenticationManager.authenticate(auth);
				}
			}

		} catch (Exception e) {
			logger.warn(e);
			throw new XFireFault("Authentication Failed.", XFireFault.SENDER);
		}

	}

}
