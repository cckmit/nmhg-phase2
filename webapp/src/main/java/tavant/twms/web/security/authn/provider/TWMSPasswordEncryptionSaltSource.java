package tavant.twms.web.security.authn.provider;

import org.acegisecurity.providers.dao.SaltSource;
import org.acegisecurity.userdetails.UserDetails;

import tavant.twms.security.model.OrgAwareUserDetails;

public class TWMSPasswordEncryptionSaltSource implements SaltSource {

	public Object getSalt(UserDetails user) {
		if (user instanceof OrgAwareUserDetails) {
			return ((OrgAwareUserDetails)user).getOrgUser().getSalt();
		} else {
			return null;
		}
	}
}
