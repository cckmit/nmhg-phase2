package tavant.twms.domain.notification;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.infra.GenericRepository;

public interface EventStateRepository extends GenericRepository<EventState, Long>
{
	@Transactional(readOnly = true)
	public EventState fetchEventStateByName(String eventName);
}
