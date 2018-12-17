package tavant.twms.domain.notification;

import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.GenericService;

/**
 * This provides all services related to EmailEvent entity. 
 * @author vaibhav.fouzdar
 */
@Transactional(readOnly=true)
public interface EventService extends GenericService<NotificationEvent, Long, Exception> {
	
	/**
	 * Creates a new event for action taken.
	 * @ Records following:  
	 * 	@ * Entity Name			  
	 * 	@ * Event Name			 
	 *  @ * Other Parameters in Map:  		
	 *  		@ id:			identifier for the entity  		 	
	 *  
	 *  @author vaibhav.fouzdar
	 */	
	@Transactional(readOnly=false)
	public void createEvent(String entityName, String eventName, HashMap<String, Object> paramMap);
	
	@Transactional(readOnly=false)
	public void createEvent(String entityName, String eventName, Long entityId);
	
	public List<NotificationEvent> fetchPendingEvent();
	
	@Transactional(readOnly=false)
	public void updateAll(List<NotificationEvent> eventList);
	
	public List<NotificationEvent> fetchAllPendingEmailEvents();
}
