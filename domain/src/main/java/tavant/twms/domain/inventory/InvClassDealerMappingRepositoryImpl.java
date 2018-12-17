package tavant.twms.domain.inventory;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericRepositoryImpl;

/**
 * Repository interface implementation for {@link InvClassDealerMapping}
 * 
 * @author ravi.sinha
 */
public class InvClassDealerMappingRepositoryImpl extends GenericRepositoryImpl<InvClassDealerMapping, Long> 
	implements InvClassDealerMappingRepository {

	@Override
	public List<InvClassDealerMapping> findInvClassDealerMappings(final ServiceProvider serviceProvider) {
		HibernateCallback<List<InvClassDealerMapping>> callback = new HibernateCallback<List<InvClassDealerMapping>>() {
			public List<InvClassDealerMapping> doInHibernate(Session session)
					throws HibernateException, SQLException {
				
				Criteria criteria = session.createCriteria(InvClassDealerMapping.class);
				
				criteria.createAlias("serviceProvider", "serviceProvider");
				criteria.add(Restrictions.eq("serviceProvider.id", serviceProvider.getId()));
				
				List<InvClassDealerMapping> returnedMappings = (List<InvClassDealerMapping>) criteria.list();

				return (List<InvClassDealerMapping>) returnedMappings;
			}
		};
		
		List<InvClassDealerMapping> returnedMappings = getHibernateTemplate().execute(callback);
		
		return returnedMappings;
	}
}
