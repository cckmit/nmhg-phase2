package tavant.twms.domain.notification;

import java.util.List;

import tavant.twms.domain.orgmodel.EventState;

/**
 * @author priyank.gupta
 *
 */
public class EventStateServiceImpl implements EventStateService 
{
	
	EventStateRepository eventStateRepository = null;

	public EventState findEventStateByName(String eventStateName)
	{
		return eventStateRepository.fetchEventStateByName(eventStateName);
	}
	
	public EventStateRepository getEventStateRepository() 
	{
		return eventStateRepository;
	}

	public void setEventStateRepository(EventStateRepository eventStateRepository) 
	{
		this.eventStateRepository = eventStateRepository;
	}
}
