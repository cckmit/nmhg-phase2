package tavant.twms.domain.notification;

import java.util.HashMap;
import java.util.List;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class EventServiceImpl extends GenericServiceImpl<NotificationEvent, Long, Exception> implements EventService {

	private EventRepository eventRepository;

    public GenericRepository<NotificationEvent, Long> getRepository() {
        return eventRepository;
    }

	public void updateAll(List<NotificationEvent> eventList){
		eventRepository.updateAllEvents(eventList);
	} 

	public List<NotificationEvent> fetchPendingEvent(){
        return eventRepository.fetchPendingEmailEvent();
	}	
	
	
	public void createEvent(String entityName, String eventName, HashMap<String, Object> paramMap){
		NotificationEvent event = new NotificationEvent();
		event.setEntityName(entityName);
		event.setEventName(eventName);		
		event.setParameterMap(paramMap);
		event.setPending(true);
		event.setNumberOfTrials(0);
		eventRepository.save(event);
	}
	
	public void createEvent(String entityName, String eventName, Long entityId){
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("id", entityId.toString());
		createEvent(entityName, eventName, paramMap);
	}	
	
	public void setEventRepository(EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}
	
    public List<NotificationEvent> fetchAllPendingEmailEvents() {
        return eventRepository.fetchAllPendingEmailEvents();
    }
}
