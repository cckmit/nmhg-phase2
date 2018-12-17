package tavant.twms.infra;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.event.EventSource;
import org.hibernate.event.def.DefaultDeleteEventListener;
import org.hibernate.persister.entity.EntityPersister;

/**
 * Custom Tavant class to handle Delete Orphan case where the child is not yet persisted and Hibernate tries to delete it.
 * http://opensource.atlassian.com/projects/hibernate/browse/HHH-2146
 * @author ramalakshmi.p
 *
 */
@SuppressWarnings( { "serial", "unused" })
public class TWMSCustomerHibernateDeleteListener extends DefaultDeleteEventListener 
{
	protected void deleteTransientEntity(EventSource session, Object entity,
			boolean cascadeDeleteEnabled, EntityPersister persister,
			Set transientEntities) {
		super.deleteTransientEntity(session, entity, cascadeDeleteEnabled,
				persister, transientEntities == null ? new HashSet()
						: transientEntities);
	}
}