package tavant.twms.domain.catalog;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.bu.SourceSystemBuMapping;
import tavant.twms.infra.GenericRepositoryImpl;


public class ItemTypeMappingRepositoryImpl extends
GenericRepositoryImpl<SourceSystemBuMapping, String> implements ItemTypeMappingRepository{

	public ItemTypeMapping findByExternalItemType(final String name) {
		return (ItemTypeMapping) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery("select mapping from ItemTypeMapping mapping where lower(mapping.externalItemType) = :name")
								.setString("name",name.toLowerCase()).uniqueResult();
					}

				});
	}
	}


