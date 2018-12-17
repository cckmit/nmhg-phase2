package tavant.twms.domain.notification;

import java.util.List;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;

@Transactional(readOnly = true)
public interface EventSubscriptionService 
{
	public List<EventState> getSubscribedEventsForRoles(Set<Role> userRolesList);
	
	public List<EventState> getSubscribedEventsForUser(User user);
	
	@Transactional(readOnly = false)
	public void updateUserEventStateMapping(User userToUpdate);
}
