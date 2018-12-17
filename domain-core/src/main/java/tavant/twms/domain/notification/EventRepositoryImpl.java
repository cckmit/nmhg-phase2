package tavant.twms.domain.notification;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import tavant.twms.infra.GenericRepositoryImpl;

public class EventRepositoryImpl extends GenericRepositoryImpl<NotificationEvent, Long> implements  EventRepository {
		
	@SuppressWarnings("unchecked")
	public List<NotificationEvent> fetchPendingEmailEvent()
	{		
		String query = "from NotificationEvent ne " +
                "where ne.numberOfTrials < 3 and " +
                "rownum < 41 and " +
                "ne.d.createdOn = to_date(sysdate,'DD-MM-YY') and ne.pending=true";
		HashMap params = new HashMap();
		return findUsingQuery(query, params);
	}	
	
	public void updateAllEvents(List<NotificationEvent> eventList)
	{
		updateAll(eventList);		
	}
	
    @SuppressWarnings("unchecked")
    public List<NotificationEvent> fetchAllPendingEmailEvents() {
        String query = "from NotificationEvent ne " + "where ne.numberOfTrials < 3 and " + "rownum < 41 and " + "ne.pending=true";
        HashMap params = new HashMap();
        return findUsingQuery(query, params);
    }
}
