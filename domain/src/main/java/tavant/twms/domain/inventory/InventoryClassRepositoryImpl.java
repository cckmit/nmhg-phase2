package tavant.twms.domain.inventory;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

/**
 * Repository interface implementation for {@link InventoryClass}
 * 
 * @author ravi.sinha
 */
public class InventoryClassRepositoryImpl extends GenericRepositoryImpl<InventoryClass, Long> 
	implements InventoryClassRepository {

	@Override
	public InventoryClass findInventoryClassByName(final String name) {
		
		HibernateCallback<InventoryClass> callback = new HibernateCallback<InventoryClass>() {
			public InventoryClass doInHibernate(Session session)
					throws HibernateException, SQLException {
				
				Query query = session.createQuery(
						"from InventoryClass invClass where invClass.name = :name")
						.setParameter("name", name);
				
				Object returnedObject = query.uniqueResult();

				return (InventoryClass) returnedObject;
			}
		};
		
		InventoryClass returnedInvClass = getHibernateTemplate().execute(callback);
		
		return returnedInvClass;
	}

}
