package tavant.twms.domain.notification;

import java.util.HashMap;
import java.util.List;

import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.infra.GenericRepositoryImpl;

public class EventStateRepositoryImpl extends GenericRepositoryImpl<EventState, Long> implements  EventStateRepository
{
	
	public EventState fetchEventStateByName(String eventName)
	{
		String query = "from EventState es where es.name = :eventName";
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("eventName", eventName);
		return findUniqueUsingQuery(query, params);
	}
}
