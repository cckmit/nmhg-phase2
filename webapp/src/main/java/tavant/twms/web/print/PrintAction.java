/**
 * 
 */
package tavant.twms.web.print;

import java.util.Set;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 * @author mritunjay.kumar
 * 
 */
public class PrintAction extends I18nActionSupport {
	protected boolean isInternalUser(User loggedInUser) {
		Organization organization = loggedInUser.getBelongsToOrganization();
		if (InstanceOfUtil.isInstanceOfClass( ServiceProvider.class, organization)) {
			return false;
		} else {
			return true;
		}
	}

	protected boolean getCurrentUserJustADealer(User loggedInUser) {
		Set<Role> roles = loggedInUser.getRoles();
		if (roles.size() > 1) {
			return false;
		}
		for (Role role : roles) {
			if ("dealer".equals(role.getName())) {
				return true;
			}
		}
		return false;
	}

	protected boolean isShowPaymentInfo(User loggedInUser, Long userId) {
		if (getCurrentUserJustADealer(loggedInUser)
				|| loggedInUser.getId().equals(userId)
				|| isInternalUser(loggedInUser)) {
			return true;
		}
		return false;
	}
	
	protected boolean isShowPercentageAccepted(Claim claim) {
		if (claim.getState().ordinal() > ClaimState.SUBMITTED.ordinal()) {
			return true;
		}
		return false;
	}

}
