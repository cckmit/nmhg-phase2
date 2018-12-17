package tavant.twms.domain.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericRepositoryImpl;

public class EventSubscriptionServiceImpl extends GenericRepositoryImpl<EventState, Long> implements EventSubscriptionService
{

	/* (non-Javadoc)
	 * @see tavant.twms.domain.notification.EventSubscriptionService#getSubscribedEventsForRoles(java.util.Set)
	 */
	public List<EventState> getSubscribedEventsForRoles(Set<Role> userRolesList) 
	{
		Collection<String> roleNames = new ArrayList<String>(10);
		//need to make a collection of all role names
		for(Role role:userRolesList)
		{
			roleNames.add(role.getName());
		}
		String query = "select distinct es from EventState es join es.roles r where r.name in (:roles)";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("roles",roleNames);
		return findUsingQuery(query, params);
	}

	/* (non-Javadoc)
	 * @see tavant.twms.domain.notification.EventSubscriptionService#getSubscribedEventsForUser(tavant.twms.domain.orgmodel.User)
	 */
	public List<EventState> getSubscribedEventsForUser(User user) 
	{
		String query = "select distinct es from User u join u.eventState es where lower(u.name) in (:logins)";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("logins",user.getName().toLowerCase());
		return findUsingQuery(query, params);
	}

	/* (non-Javadoc)
	 * @see tavant.twms.domain.notification.EventSubscriptionService#updateUserEventStateMapping(tavant.twms.domain.orgmodel.User)
	 */
	public void updateUserEventStateMapping(User userToUpdate) 
	{
		//just update the user object all child objects will be updated automatically
		getHibernateTemplate().update(userToUpdate);		
	}
	
	
	

}
