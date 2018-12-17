package tavant.twms.domain.notification;


import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.GenericRepository;


public interface EventRepository extends GenericRepository<NotificationEvent, Long>
{
	@Transactional(readOnly = true)
	public List<NotificationEvent> fetchPendingEmailEvent();
	
	@Transactional(readOnly = false)
	public void updateAllEvents(List<NotificationEvent> eventList);
	
    @Transactional(readOnly = true)
    public List<NotificationEvent> fetchAllPendingEmailEvents();
}
	