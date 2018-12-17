package tavant.twms.domain.notification;

import java.util.List;

import tavant.twms.domain.orgmodel.EventState;

/**
 * @author priyank.gupta
 *
 */
public interface EventStateService 
{
	
	public EventState findEventStateByName(String eventStateName);

}
